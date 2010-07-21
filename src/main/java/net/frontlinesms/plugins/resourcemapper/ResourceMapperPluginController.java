package net.frontlinesms.plugins.resourcemapper;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.listener.IncomingMessageListener;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.resourcemapper.handler.InfoHandler;
import net.frontlinesms.plugins.resourcemapper.handler.MessageHandler;
import net.frontlinesms.plugins.resourcemapper.handler.fields.BooleanHandler;
import net.frontlinesms.plugins.resourcemapper.handler.fields.CallbackHandler;
import net.frontlinesms.plugins.resourcemapper.handler.fields.ChecklistHandler;
import net.frontlinesms.plugins.resourcemapper.handler.fields.MultiChoiceHandler;
import net.frontlinesms.plugins.resourcemapper.handler.fields.PlainTextHandler;
import net.frontlinesms.plugins.resourcemapper.ui.ResourceMapperThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

@PluginControllerProperties(name="Resource Mapper", iconPath="/icons/small_rmapper.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper.hibernate.cfg.xml")
public class ResourceMapperPluginController extends BasePluginController implements IncomingMessageListener {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ResourceMapperPluginController.class);
	
	private FrontlineSMS frontlineController;
	
	/** The Application Context for fetching daos and other Spring stuff */
	private ApplicationContext appContext;
	
	private Object mainTab;
	
	private List<MessageHandler> listeners;
	private static ArrayList<CallbackInfo> callbacks;
	private ResourceMapperThinletTabController tabController;
	private MessageDao messageDao;
	
	public String getName() {
		return getI18NString("resourcemapper.tab.title");
	}

	@Override
	protected Object initThinletTab(UiGeneratorController uiController) {
		tabController = new ResourceMapperThinletTabController(uiController, appContext);
		return tabController.getTab();
	}

	public Object getTab(){
		return mainTab;
	}
	
	public void deinit() {
		LOG.debug("deinit");
	}
	
	public void init(FrontlineSMS frontlineController, ApplicationContext appContext)	throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		frontlineController.addIncomingMessageListener(this);
		this.appContext = appContext;
		this.messageDao = (MessageDao)appContext.getBean("messageDao");
		initListeners();
		//debugIncomingMessageEvents();
	}
	
	/** @return {@link #frontlineController} */
	public FrontlineSMS getFrontlineController() {
		return this.frontlineController;
	}
	
	/** @return {@link #appContext} */ 
	public ApplicationContext getApplicationContext() {
		return this.appContext;
	}
	
	private void initListeners(){
		LOG.debug("initListeners");
		listeners = new ArrayList<MessageHandler>();
		listeners.add(new InfoHandler(frontlineController, appContext));
		listeners.add(new BooleanHandler(frontlineController, appContext));
		listeners.add(new ChecklistHandler(frontlineController, appContext));
		listeners.add(new MultiChoiceHandler(frontlineController, appContext));
		listeners.add(new PlainTextHandler(frontlineController, appContext));
	}
	
	@SuppressWarnings("unused")
	private void debugIncomingMessageEvents() {
		LOG.debug("debugIncomingMessageEvents");
		long dateReceived = Calendar.getInstance().getTimeInMillis();
		String senderMsisdn = "306.341.3644";	
		for (String message : new String[] {"info hosp", "help hosp", "? hosp", "hosp", 
											"hosp Saskatoon RUH", "city Saskatoon",
											"power", "power yes", "power true", "power y", "power t", "power 1",
											"power no", "power false", "power n", "power f", "power 0",
											"power yyy",
											"info day", "day", "1", "day", "tuesday",
											"day 1", "day 7", "day monday",
											"pet", "pet 1", "pet 3", "pet cat", "pet cat, dog", "pet 1, cat", "pet cat, shark, dog",
											"invalid", ""
											}) {
			FrontlineMessage frontlineMessage = FrontlineMessage.createIncomingMessage(dateReceived, senderMsisdn, null, message);
			this.messageDao.saveMessage(frontlineMessage);
			incomingMessageEvent(frontlineMessage);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void incomingMessageEvent(FrontlineMessage message) {
		LOG.debug("-");
		LOG.debug("incomingMessageEvent: %s", message.getTextContent());
		if (callbacks == null) {
			callbacks = new ArrayList<CallbackInfo>();
		}
		
		//first, remove all callbacks that have timed out
		List<CallbackInfo> expiredCallbacks = new ArrayList<CallbackInfo>();
		for (CallbackInfo callback : callbacks) {
			if (callback.hasTimedOut()) {
				CallbackHandler callbackHandler = callback.getHandler();
				if (callbackHandler != null) {
					callbackHandler.callBackTimedOut(callback.getPhoneNumber());
				}
				expiredCallbacks.add(callback);
			}
		}
		if (expiredCallbacks.size() > 0) {
			LOG.debug("Removing %s Expired Callbacks", expiredCallbacks.size());
			callbacks.removeAll(expiredCallbacks);
		}
		
		//see if there is a handler that wants to handle the message
		MessageHandler handler = null;
		for (MessageHandler listener: this.listeners) {
			String[] words = message.getTextContent().replaceFirst("[\\s]", " ").split(" ", 2);
			if (words.length > 0) {
				for (String keyword : listener.getKeywords()) {
					if (keyword.equalsIgnoreCase(words[0])) {
						handler = listener;
						break;
					}
				}			
			}
		}	
		//LOG.debug("MessageHandler: %s", handler);
		
		//now, see if there is a callback out on that message
		CallbackHandler callbackHandler = null;
		for (CallbackInfo info: callbacks) {
			if (info.getPhoneNumber().equalsIgnoreCase(message.getSenderMsisdn())) {
				callbackHandler = info.getHandler();
				break;
			}
		}
		if (callbackHandler != null) {
			LOG.debug("CallbackHandler: %s", callbackHandler);
		}
		
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
			LOG.error("No Handler Found: %s",  message.getTextContent());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void registerCallback(String msisdn, CallbackHandler handler){
		LOG.debug("registerCallback(%s, %s)", msisdn, handler);
		if (callbacks == null) {
			callbacks = new ArrayList<CallbackInfo>();
		}
		//if there is already a callback out on that phone number, do nothing
		for (CallbackInfo info : callbacks) {
			if (info.getPhoneNumber().equalsIgnoreCase(msisdn)) {
				return;
			}
		}
		callbacks.add(new CallbackInfo(msisdn, handler));
	}
	
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
	
	public static void unregisterCallback(CallbackInfo callback) {
		LOG.debug("unregisterCallback(%s)", callback);
		unregisterCallback(callback.getPhoneNumber());
	}

}
