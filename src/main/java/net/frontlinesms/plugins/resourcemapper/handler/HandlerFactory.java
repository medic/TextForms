package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.List;
import java.util.ArrayList;
import java.util.ServiceLoader;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;

/*
 * HandlerFactory
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public final class HandlerFactory {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(HandlerFactory.class);
	
	/*
	 * Get list of MessageHandler classes
	 * (To add a new MessageHandler classes to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.resourcemapper.handler.MessageHandler
	 * with the full package and class name of the new implementing MessageHandler class)
	 */
	public static List<MessageHandler> getHandlerClasses() {
		if (handlerClasses == null) {
			handlerClasses = new ArrayList<MessageHandler>();
			for (MessageHandler handler : ServiceLoader.load(MessageHandler.class)) {
				LOG.debug("Handler Discovered: %s", handler);
				handlerClasses.add(handler);
		    }
		}
		return handlerClasses;
	}private static List<MessageHandler> handlerClasses = null;
		
}