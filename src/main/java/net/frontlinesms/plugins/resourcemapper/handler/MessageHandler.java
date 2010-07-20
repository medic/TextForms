package net.frontlinesms.plugins.resourcemapper.handler;

import net.frontlinesms.data.domain.FrontlineMessage;

public interface MessageHandler {

	public void handleMessage(FrontlineMessage message);
	
	public boolean isSatisfiedBy(String message);
}
