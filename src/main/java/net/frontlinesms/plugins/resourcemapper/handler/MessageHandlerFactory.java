package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.List;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;

/*
 * MessageHandlerFactory
 * @author Dale Zak
 */
public final class MessageHandlerFactory {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(MessageHandlerFactory.class);
	
	/**
	 * Get list of MessageHandlers
	 * (To add a new MessageHandler to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.resourcemapper.handler.MessageHandler
	 * with the full package and class name of the new implementing MessageHandler class)
	 * @param frontlineController FrontlineSMS
	 * @param appContext ApplicationContext
	 * @return
	 */
	public static List<MessageHandler> getHandlers(FrontlineSMS frontline, ApplicationContext appContext) {
		if (handlers == null) {
			handlers = new ArrayList<MessageHandler>();
			LOG.debug("Loading Handlers...");
			try {
				for (MessageHandler handler : ServiceLoader.load(MessageHandler.class)) {
					LOG.debug("Loaded Handler: %s", handler.getClass().getSimpleName());
					handler.setFrontline(frontline);
					handler.setApplicationContext(appContext);
					handlers.add(handler);
			    }
			}
			catch (ServiceConfigurationError ex) {
				LOG.error("ServiceConfigurationError: %s", ex);
			}
		}
		return handlers;
	}private static List<MessageHandler> handlers = null;
	
}