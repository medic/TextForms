package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;

/**
 * MessageHandler
 * @author dalezak
 *
 */
public abstract class MessageHandler {
	
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(MessageHandler.class);
	
	private FrontlineSMS frontline;
	
	public MessageHandler() {}
	
	public void setFrontline(FrontlineSMS frontline) {
		this.frontline = frontline;
	}
	
	public abstract void setApplicationContext(ApplicationContext appContext);
	
	public abstract void handleMessage(FrontlineMessage message);
	
	public abstract Collection<String> getKeywords();
	
	protected void sendReply(String msisdn, String text, boolean error) {
		if (error) {
			LOG.error("(%s) %s", msisdn, text);
		}
		else {
			LOG.debug("(%s) %s", msisdn, text);
		}
		if (this.frontline != null) {
			this.frontline.sendTextMessage(msisdn, text);
		}
	}
	
	protected String[] toWords(String message, int limit) {
		if (message != null) {
			return message.replaceFirst("[\\s]", " ").split(" ", limit);
		}
		return new String[0];
	}
	
	protected String arrayToString(String [] args) {
		StringBuffer sb = new StringBuffer();
		for (String arg : args) {
			if (sb.length() > 0) {
				 sb.append(",");
			}
			else {
				 sb.append("[");
			}
			sb.append(arg);
		}
		sb.append("]");
		return sb.toString();
	}
}
