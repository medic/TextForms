package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.Arrays;
import java.util.Collection;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

import org.springframework.context.ApplicationContext;

public class InfoHandler implements MessageHandler {
	
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(InfoHandler.class);
	
	protected FrontlineSMS frontline;
	protected ApplicationContext appContext;
	protected FieldMappingDao mappingDao;
	
	public InfoHandler() {
		this(null, null);
	}
	
	public InfoHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		setFrontline(frontline);
		setApplicationContext(appContext);
	}
	
	public void setFrontline(FrontlineSMS frontline) {
		this.frontline = frontline;
	}
	
	public void setApplicationContext(ApplicationContext appContext) { 
		this.appContext = appContext;
		if (appContext != null) {
			this.mappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
		}
	}
	
	public Collection<String> getKeywords() {
		return Arrays.asList(ResourceMapperProperties.getInfoKeywords());
	}
	
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = message.getTextContent().replaceFirst("[\\s]", " ").split(" ");
		if (words.length == 1) {
			sendReply(message.getSenderMsisdn(), "Welcome to ResourceMapper!", false);
		}
		else if (words.length == 2) {
			Field field = this.mappingDao.getFieldForKeyword(words[1]);
			if (field != null) {
				sendReply(message.getSenderMsisdn(), field.getInfoSnippet(), false);
			}
			else {
				sendReply(message.getSenderMsisdn(), String.format("No Field Mapping Found For '%s'", words[1]), true);
			}		
		}
		else {
			sendReply(message.getSenderMsisdn(), "Welcome to ResourceMapper!", false);
		}
	}
	
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

}
