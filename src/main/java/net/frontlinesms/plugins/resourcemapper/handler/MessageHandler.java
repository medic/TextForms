package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;

public interface MessageHandler {

	public void handleMessage(FrontlineMessage message);
	
	public Collection<String> getKeywords();
}
