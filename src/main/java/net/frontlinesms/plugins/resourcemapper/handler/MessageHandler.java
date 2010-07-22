package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;

public interface MessageHandler {

	public void handleMessage(FrontlineMessage message);
	
	public Collection<String> getKeywords();
	
	public void setFrontline(FrontlineSMS frontline);
	
	public void setApplicationContext(ApplicationContext appContext);
}
