package net.frontlinesms.plugins.resourcemapper;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.listener.IncomingMessageListener;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.plugins.resourcemapper.handler.MessageHandler;
import net.frontlinesms.plugins.resourcemapper.handler.MessageHandlerFactory;
import net.frontlinesms.plugins.resourcemapper.handler.fields.CallbackHandler;
import net.frontlinesms.plugins.resourcemapper.ui.ResourceMapperThinletTabController;
import net.frontlinesms.ui.UiGeneratorController;

@PluginControllerProperties(name="Resource Mapper", iconPath="/icons/small_rmapper.png",
		springConfigLocation="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper-spring-hibernate.xml",
		hibernateConfigPath="classpath:net/frontlinesms/plugins/resourcemapper/resourcemapper.hibernate.cfg.xml")
public class ResourceMapperPluginController extends BasePluginController implements IncomingMessageListener {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ResourceMapperPluginController.class);
	
	private FrontlineSMS frontlineController;
	private ApplicationContext appContext;
	
	private Object mainTab;
	
	private List<MessageHandler> listeners;
	private static ArrayList<CallbackInfo> callbacks;
	private ResourceMapperThinletTabController tabController;
	
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
	
	/**
	 * De-initialize ResourceMapperPluginController
	 */
	public void deinit() {
		LOG.debug("deinit");
	}
	
	/**
	 * Initialize ResourceMapperPluginController
	 * @param frontlineController FrontlineSMS
	 * @param appContext ApplicationContext
	 */
	public void init(FrontlineSMS frontlineController, ApplicationContext appContext) throws PluginInitialisationException {
		this.frontlineController = frontlineController;
		frontlineController.addIncomingMessageListener(this);
		this.appContext = appContext;
		this.listeners = MessageHandlerFactory.getHandlerClasses(frontlineController, appContext);
		
		//Un-comment to generate debug information
		ResourceMapperDebug resourceMapperDebug = new ResourceMapperDebug(this, appContext);
		resourceMapperDebug.createDebugContacts();
		resourceMapperDebug.createDebugFields();
		resourceMapperDebug.createDebugResponses();
	}
	
	/** @return {@link #frontlineController} */
	public FrontlineSMS getFrontlineController() {
		return this.frontlineController;
	}
	
	/** @return {@link #appContext} */ 
	public ApplicationContext getApplicationContext() {
		return this.appContext;
	}
	
	/**
	 * Handle incoming messages to FrontlineSMS
	 * @param message FrontlineMessage
	 */
	@SuppressWarnings("unchecked")
	public void incomingMessageEvent(FrontlineMessage message) {
		LOG.debug(" ");
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
		for (MessageHandler listener : this.listeners) {
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
		
		//now, see if there is a callback out on that message
		CallbackHandler callbackHandler = null;
		for (CallbackInfo info : callbacks) {
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
			LOG.error("No Handler Found For '%s'",  message.getTextContent());
		}
	}
	
	/**
	 * Register callback
	 * @param msisdn HospitalContact phone number
	 * @param handler CallbackHandler
	 */
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
