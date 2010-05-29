package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.Collection;

import net.frontlinesms.data.domain.Message;

public interface MessageHandler {

	public void handleMessage(Message m);
	
	public Collection<String> getKeywords();
}
