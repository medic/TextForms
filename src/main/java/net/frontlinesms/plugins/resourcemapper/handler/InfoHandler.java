package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

import org.springframework.context.ApplicationContext;

public class InfoHandler implements MessageHandler {
	
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(InfoHandler.class);
	
	protected FrontlineSMS frontline;
	protected ApplicationContext appContext;
	protected FieldMappingDao mappingDao;
	
	public InfoHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		this.frontline = frontline;
		this.appContext = appContext;
		this.mappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
	}
	
	public Collection<String> getKeywords() {
		List<String> results = new ArrayList<String>();
		results.add("info");
		results.add("help");
		results.add("?");
		return results;
	}

	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = message.getTextContent().replaceFirst("[\\s]", " ").split(" ");
		if (words.length == 1) {
			sendReply(message.getSenderMsisdn(), "Welcome to ResourceMapper!", false);
		}
		else if (words.length == 2) {
			Field field = this.mappingDao.getFieldForAbbreviation(words[1]);
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
		//TODO frontline.sendTextMessage(msisdn, text);
	}

}
