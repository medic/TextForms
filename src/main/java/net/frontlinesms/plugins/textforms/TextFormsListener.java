package net.frontlinesms.plugins.textforms;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.domain.FrontlineMessage.Type;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.textforms.data.domain.TextFormResponse;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.repository.TextFormResponseDao;
import net.frontlinesms.plugins.textforms.handler.MessageHandler;
import net.frontlinesms.plugins.textforms.handler.MessageHandlerFactory;
import net.frontlinesms.plugins.textforms.handler.questions.CallbackHandler;

/**
 * TextFormsListener
 * @author dalezak
 *
 */
public class TextFormsListener implements EventObserver {

	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(TextFormsListener.class);
	
	/**
	 * MessageHandlers
	 */
	private static final List<MessageHandler> listeners = new ArrayList<MessageHandler>();
	
	/**
	 * Collection of Callbacks
	 */
	private static final List<CallbackInfo> callbacks = new ArrayList<CallbackInfo>();
	
	/**
	 * Map of TextForms
	 */
	private static final Map<String, TextFormResponse> textforms = new HashMap<String, TextFormResponse>();
	
	/**
	 * FrontlineSMS
	 */
	private final FrontlineSMS frontline;
	
	/**
	 * TextFormResponseDao
	 */
	private final TextFormResponseDao textformResponseDao;
	
	/**
	 * TextFormsListener
	 * @param frontlineController FrontlineSMS
	 * @param appContext ApplicationContext
	 */
	public TextFormsListener(FrontlineSMS frontlineController, ApplicationContext appContext, TextFormsPluginController pluginController) {
		listeners.addAll(MessageHandlerFactory.getHandlerClasses(frontlineController, appContext));
		frontlineController.getEventBus().registerObserver(this);
		this.frontline = frontlineController;
		this.textformResponseDao = pluginController.getTextFormResponseDao();
	}
	
	/**
	 * Handle FrontlineEventNotification events if notification is EntitySavedNotification or EntityUpdatedNotification
	 * and it contains DatabaseEntity that is a FrontlineMessage
	 */
	public void notify(FrontlineEventNotification notification) {
		FrontlineMessage message = getFrontlineMessage(notification);
		if (message != null) {
			LOG.debug("notify: %s", notification.getClass().getSimpleName());
			//first, remove all callbacks that have timed out
			List<CallbackInfo> expiredCallbacks = getExpiredCallbacks();
			if (expiredCallbacks.size() > 0) {
				LOG.debug("Removing %s Expired Callbacks", expiredCallbacks.size());
				callbacks.removeAll(expiredCallbacks);
			}
			
			//remove all textforms that have timed out
			List<String> expiredTextForms = getExpiredTextForms();
			if (expiredTextForms.size() > 0) {
				LOG.debug("Removing %s Expired TextForms", expiredTextForms.size());
				for (String telephone : expiredTextForms) {
					textforms.remove(telephone);
				}
			}
			
			//see if there is a handler that wants to handle the message
			MessageHandler handler = getMessageHandler(message);
			
			//now, see if there is a callback out on that message
			CallbackHandler<?> callbackHandler = getCallbackHandler(message);
			if (handler != null && callbackHandler != null && callbackHandler.shouldHandleCallbackMessage(message) == false) {
				//if there is a keyword in the message and the callback handler doesn't seem
				//to know what to do with it, give the message to the keyword handler as opposed
				//to the callback handler and remove the callback.
				if(handler.handleMessage(message)) {
					LOG.error("%s [callback]Handler Successful", handler.getClass().getSimpleName());
				}
				unregisterCallback(message.getSenderMsisdn());
			}
			else if (callbackHandler != null) {
				//otherwise, if there is a callback out on the message, pass the message to the callback handler
				Answer<?> answer = callbackHandler.handleCallback(message);
				if(answer != null) {
					LOG.error("%s CallbackHandler Successful", callbackHandler.getClass().getSimpleName());
					TextFormResponse textformResponse = getTextFormResponse(message);
					if (textformResponse != null) {
						LOG.error("Contact:%s TextFormResponse:%s", textformResponse.getContactPhoneNumber(), textformResponse.getTextFormName());
						textformResponse.addAnswer(answer);
						Question question = textformResponse.getNextQuestion();
						if (question != null) {
							LOG.error("Next Question: %s", question.getName());
							registerTextForm(message.getSenderMsisdn(), textformResponse, question);
							sendReply(message.getSenderMsisdn(), question.toString(true), false);	
							LOG.out("%s", question.toString(true));
						}
						else {
							LOG.debug("TextForm '%s' Completed!", textformResponse.getTextFormName());
							sendReply(message.getSenderMsisdn(), TextFormsMessages.getSurveryCompleted(textformResponse.getTextFormName()), false);
							textforms.remove(message.getSenderMsisdn());
							LOG.out("%s", TextFormsMessages.getSurveryCompleted(textformResponse.getTextFormName()));
							try {
								textformResponseDao.updateTextForm(textformResponse);
							} 
							catch (DuplicateKeyException ex) {
								LOG.error("Error saving TextFormResponse: %s", ex);
							}
						}
					}
				}
			}
			else if (handler != null) {
				//if there is no callback out on the message, give it to a keyword handler
				if(handler.handleMessage(message)) {
					LOG.error("%s Handler Successful", handler.getClass().getSimpleName());
				}
			}
			else {
				LOG.error("No Handler Found For '%s'",  message.getTextContent());
			}
		}
	}
	
	private TextFormResponse getTextFormResponse(FrontlineMessage message) {
		return textforms.containsKey(message.getSenderMsisdn()) ? textforms.get(message.getSenderMsisdn()) : null;
	}
	
	/**
	 * Get MessageHandler
	 * @param message FrontlineMessage
	 * @return MessageHandler, if matching keyword is found
	 */
	private MessageHandler getMessageHandler(FrontlineMessage message) {
		for (MessageHandler handler : listeners) {
			String[] words = message.getTextContent().replaceFirst("[\\s]", " ").split(" ", 2);
			if (words.length > 0) {
				for (String keyword : handler.getKeywords()) {
					if (keyword.equalsIgnoreCase(words[0])) {
						return handler;
					}
				}			
			}
		}	
		return null;
	}
	
	/**
	 * Get CallbackHandler
	 * @param message FrontlineMessage
	 * @return CallbackHandler, if one exists
	 */
	private CallbackHandler<?> getCallbackHandler(FrontlineMessage message) {
		for (CallbackInfo callback : callbacks) {
			if (callback.getPhoneNumber().equalsIgnoreCase(message.getSenderMsisdn())) {
				CallbackHandler<?> callbackHandler = callback.getHandler();
				LOG.debug("CallbackHandler: %s", callbackHandler.getClass().getSimpleName());
				return callbackHandler;
			}
		}
		return null;
	}
	
	/**
	 * Get expired callbacks
	 * @return collection of expired callbacks
	 */
	private List<CallbackInfo> getExpiredCallbacks() {
		List<CallbackInfo> expiredCallbacks = new ArrayList<CallbackInfo>();
		for (CallbackInfo callback : callbacks) {
			if (callback.hasTimedOut(5)) {
				CallbackHandler<?> callbackHandler = callback.getHandler();
				if (callbackHandler != null) {
					callbackHandler.removeCallback(callback.getPhoneNumber());
				}
				expiredCallbacks.add(callback);
			}
		}
		return expiredCallbacks;
	}
	
	/**
	 * Get expired textform responses
	 * @return collection of expired textform responses
	 */
	private List<String> getExpiredTextForms() {
		List<String> expiredTextFormResponses = new ArrayList<String>();
		for (String telephoneNumber : textforms.keySet()) {
			TextFormResponse textformResponse = textforms.get(telephoneNumber);
			if (textformResponse.hasTimedOut(10)) {
				expiredTextFormResponses.add(telephoneNumber);
			}
		}
		return expiredTextFormResponses;
	}
	
	/**
	 * Get FrontlineMessage from FrontlineEventNotification
	 * @param notification
	 * @return FrontlineMessage if notification is DatabaseEntityNotification and contains FrontlineMessage of Type.RECEIVED
	 */
	private FrontlineMessage getFrontlineMessage(FrontlineEventNotification notification) {
		if (notification instanceof EntitySavedNotification<?>) {
			EntitySavedNotification<?> entitySavedNotification = (EntitySavedNotification<?>)notification;
			if (entitySavedNotification.getDatabaseEntity() instanceof FrontlineMessage) {
				FrontlineMessage message = (FrontlineMessage)entitySavedNotification.getDatabaseEntity();
				if (message.getType() == Type.RECEIVED) {
					return message;
				}
			}
		}
		return null;
	}

	/**
	 * Register callback
	 * @param msisdn Contact phone number
	 * @param handler CallbackHandler
	 */
	public static void registerCallback(String msisdn, CallbackHandler<?> handler){
		LOG.debug("registerCallback(%s, %s)", msisdn, handler.getClass().getSimpleName());
		//if there is already a callback out on that phone number, do nothing
		for (CallbackInfo info : callbacks) {
			if (info.getPhoneNumber().equalsIgnoreCase(msisdn)) {
				return;
			}
		}
		callbacks.add(new CallbackInfo(msisdn, handler));
	}
	
	/**
	 * Register textform response
	 * @param msisdn Contact phone number
	 * @param textformResponse TextFormResponse
	 */
	public static void registerTextForm(String msisdn, TextFormResponse textformResponse, Question question) {
		LOG.debug("registerTextForm(%s, %s, %s)", msisdn, textformResponse.getTextFormName(), question.getClass());
		textforms.put(msisdn, textformResponse);
		for (MessageHandler handler : listeners) {
			if(handler instanceof CallbackHandler<?>) {
				CallbackHandler<?> callbackHandler = (CallbackHandler<?>)handler;
				if (callbackHandler.getQuestionClass() == question.getClass()) {
					callbacks.add(new CallbackInfo(msisdn, callbackHandler));
					callbackHandler.addCallback(msisdn, question);
					LOG.debug("Registering Callback: %s", callbackHandler.getClass());
					break;	
				}
			}
		}
	}
	
	/**
	 * Unregister callback
	 * @param msisdn Contact phone number 
	 */
	public static void unregisterCallback(String msisdn) {
		LOG.debug("unregisterCallback(%s)", msisdn);
		if (callbacks != null) {
			ArrayList<CallbackInfo> toRemove = new ArrayList<CallbackInfo>();
			for (CallbackInfo info : callbacks) {
				if (info.getPhoneNumber().equalsIgnoreCase(msisdn)){
					info.getHandler().removeCallback(msisdn);
					toRemove.add(info);
				}
			}
			callbacks.removeAll(toRemove);	
		}
	}
	
	/**
	 * Unregister callback
	 * @param callback CallbackInfo
	 */
	public static void unregisterCallback(CallbackInfo callback) {
		LOG.debug("unregisterCallback(%s)", callback);
		unregisterCallback(callback.getPhoneNumber());
	}
	
	protected void sendReply(String msisdn, String text, boolean error) {
		if (error) {
			LOG.error("(%s) %s", msisdn, text);
		}
		else {
			LOG.debug("(%s) %s", msisdn, text);
		}
		if (this.frontline != null) {
			this.frontline.sendTextMessage(msisdn, text);
		}
	}
	
}