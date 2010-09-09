package net.frontlinesms.plugins.surveys.handler;

import java.util.List;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.surveys.SurveysLogger;

/*
 * MessageHandlerFactory
 * @author Dale Zak
 */
public final class MessageHandlerFactory {

	private static SurveysLogger LOG = SurveysLogger.getLogger(MessageHandlerFactory.class);
	
	/**
	 * Get list of MessageHandlers
	 * (To add a new MessageHandler to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.surveys.handler.MessageHandler
	 * with the full package and class name of the new implementing MessageHandler class)
	 * @param frontlineController FrontlineSMS
	 * @param appContext ApplicationContext
	 * @return
	 */
	public static List<MessageHandler> getHandlerClasses(FrontlineSMS frontline, ApplicationContext appContext) {
		if (handlerClasses == null) {
			handlerClasses = new ArrayList<MessageHandler>();
			LOG.debug("Loading Handlers...");
			try {
				for (MessageHandler handler : ServiceLoader.load(MessageHandler.class)) {
					LOG.debug("Loading Handler: %s", handler.getClass().getSimpleName());
					handler.setFrontline(frontline);
					handler.setApplicationContext(appContext);
					handlerClasses.add(handler);
			    }
			}
			catch (ServiceConfigurationError ex) {
				LOG.error("ServiceConfigurationError: %s", ex);
			}
			catch (Exception ex) {
				LOG.error("Exception: %s", ex);
			}
		}
		return handlerClasses;
	}private static List<MessageHandler> handlerClasses = null;
	
}