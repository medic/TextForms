package net.frontlinesms.plugins.surveys;

import java.util.List;
import java.util.ArrayList;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.domain.FrontlineMessage.Type;
import net.frontlinesms.data.events.EntitySavedNotification;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.surveys.data.domain.SurveyResponse;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.handler.MessageHandler;
import net.frontlinesms.plugins.surveys.handler.MessageHandlerFactory;
import net.frontlinesms.plugins.surveys.handler.questions.CallbackHandler;

/**
 * SurveysListener
 * @author dalezak
 *
 */
public class SurveysListener implements EventObserver {

	private static final SurveysLogger LOG = SurveysLogger.getLogger(SurveysListener.class);
	
	/**
	 * MessageHandlers
	 */
	private static final List<MessageHandler> listeners = new ArrayList<MessageHandler>();
	
	/**
	 * Collection of Callbacks
	 */
	private static final List<CallbackInfo> callbacks = new ArrayList<CallbackInfo>();
	
	/**
	 * Collection of Surveys
	 */
	private static final List<SurveyResponse> surveys = new ArrayList<SurveyResponse>();
	
	/**
	 * Are we listening?
	 */
	private boolean listening;
	
	/**
	 * SurveysListener
	 * @param frontlineController FrontlineSMS
	 * @param appContext ApplicationContext
	 */
	public SurveysListener(FrontlineSMS frontlineController, ApplicationContext appContext) {
		this(frontlineController, appContext, true);
	}
	
	/**
	 * SurveysListener
	 * @param frontlineController FrontlineSMS
	 * @param appContext ApplicationContext
	 * @param listening is listening?
	 */
	public SurveysListener(FrontlineSMS frontlineController, ApplicationContext appContext, boolean listening) {
		this.listening = listening;
		listeners.addAll(MessageHandlerFactory.getHandlerClasses(frontlineController, appContext));
		frontlineController.getEventBus().registerObserver(this);
	}
	
	/**
	 * Set is listening
	 * @param listening 
	 */
	public void setListening(boolean listening) {
		this.listening = listening;
	}
	
	/**
	 * Get is listening
	 * @return true if listening
	 */
	public boolean isListening() {
		return this.listening;
	}
	
	/**
	 * Handle FrontlineEventNotification events if notification is EntitySavedNotification or EntityUpdatedNotification
	 * and it contains DatabaseEntity that is a FrontlineMessage
	 */
	public void notify(FrontlineEventNotification notification) {
		if (listening) {
			FrontlineMessage message = getFrontlineMessage(notification);
			if (message != null) {
				LOG.debug("notify: %s", notification.getClass().getSimpleName());
				
				//first, remove all callbacks that have timed out
				List<CallbackInfo> expiredCallbacks = getExpiredCallbacks();
				if (expiredCallbacks.size() > 0) {
					LOG.debug("Removing %s Expired Callbacks", expiredCallbacks.size());
					callbacks.removeAll(expiredCallbacks);
				}
				
				List<SurveyResponse> expiredSurveys = getExpiredSurveys();
				if (expiredSurveys.size() > 0) {
					LOG.debug("Removing %s Expired Surveys", expiredSurveys.size());
					surveys.removeAll(expiredSurveys);
				}
				
				//see if there is a handler that wants to handle the message
				MessageHandler handler = getMessageHandler(message);
				
				//now, see if there is a callback out on that message
				CallbackHandler<?> callbackHandler = getCallbackHandler(message);
				
				if (handler != null && callbackHandler != null && callbackHandler.shouldHandleCallbackMessage(message) == false) {
					//if there is a keyword in the message and the callback handler doesn't seem
					//to know what to do with it, give the message to the keyword handler as opposed
					//to the callback handler and remove the callback.
					handler.handleMessage(message);
					unregisterCallback(message.getSenderMsisdn());
				}
				else if (callbackHandler != null) {
					//otherwise, if there is a callback out on the message, pass the message to the callback handler
					callbackHandler.handleCallback(message);
				}
				else if (handler != null) {
					//if there is no callback out on the message, give it to a keyword handler
					handler.handleMessage(message);
				}
				else {
					LOG.error("No Handler Found For '%s'",  message.getTextContent());
				}
			}
		}
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
				LOG.debug("CallbackHandler: %s", callbackHandler);
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
			if (callback.hasTimedOut()) {
				CallbackHandler<?> callbackHandler = callback.getHandler();
				if (callbackHandler != null) {
					callbackHandler.callBackTimedOut(callback.getPhoneNumber());
				}
				expiredCallbacks.add(callback);
			}
		}
		return expiredCallbacks;
	}
	
	/**
	 * Get expired survey responses
	 * @return collection of expired survey responses
	 */
	private List<SurveyResponse> getExpiredSurveys() {
		List<SurveyResponse> expiredSurveyResponses = new ArrayList<SurveyResponse>();
		for (SurveyResponse surveyResponse : surveys) {
			if (surveyResponse.hasTimedOut(5)) {
				expiredSurveyResponses.add(surveyResponse);
			}
		}
		return expiredSurveyResponses;
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
	 * @param msisdn HospitalContact phone number
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
	 * Register survey response
	 * @param msisdn HospitalContact phone number
	 * @param surveyResponse SurveyResponse
	 */
	public static void registerSurvey(String msisdn, SurveyResponse surveyResponse, Question question) {
		LOG.debug("registerSurvey(%s, %s, %s)", msisdn, surveyResponse.getSurveyName(), question.getClass());
		//if there is already a survey response out on that phone number, do nothing
		for (SurveyResponse survey : surveys) {
			if (survey.getContactPhoneNumber().equalsIgnoreCase(msisdn)) {
				return;
			}
		}
		surveys.add(surveyResponse);
		for (MessageHandler handler : listeners) {
			if(handler instanceof CallbackHandler<?>) {
				CallbackHandler<?> callbackHandler = (CallbackHandler<?>)handler;
				if (callbackHandler.getQuestionClass() == question.getClass()) {
					callbacks.add(new CallbackInfo(msisdn, callbackHandler));
					LOG.debug("Registering Callback: %s", callbackHandler.getClass());
					break;	
				}
			}
		}
	}
	
	/**
	 * Unregister callback
	 * @param msisdn HospitalContact phone number 
	 */
	public static void unregisterCallback(String msisdn) {
		LOG.debug("unregisterCallback(%s)", msisdn);
		if (callbacks != null) {
			ArrayList<CallbackInfo> toRemove = new ArrayList<CallbackInfo>();
			for (CallbackInfo info : callbacks) {
				if (info.getPhoneNumber().equalsIgnoreCase(msisdn)){
					info.getHandler().callBackTimedOut(msisdn);
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
	
}