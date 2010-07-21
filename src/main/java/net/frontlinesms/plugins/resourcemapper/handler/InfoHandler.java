package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.ArrayList;
import java.util.Collection;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

import org.springframework.context.ApplicationContext;

public class InfoHandler implements MessageHandler {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(InfoHandler.class);
	
	protected FrontlineSMS frontline;
	protected ApplicationContext appContext;
	protected FieldMappingDao mappingDao;
	
	public InfoHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		this.frontline = frontline;
		this.appContext = appContext;
		this.mappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
	}
	
	public Collection<String> getKeywords() {
		ArrayList<String> results = new ArrayList<String>();
		results.add("info");
		results.add("help");
		results.add("?");
		return results;
	}

	public void handleMessage(FrontlineMessage message) {
		LOG.debug("InfoHandler.handleMessage %s", message.getTextContent());
		String[] words = message.getTextContent().replaceFirst("[\\s]", " ").split(" ", 2);
		if (words.length > 1) {
			Field field = this.mappingDao.getFieldForAbbreviation(words[1]);
			if (field != null) {
				sendReply(message.getSenderMsisdn(), field.getInfoSnippet(), false);
			}
			else {
				sendReply(message.getSenderMsisdn(), String.format("No Field Mapping Found For '%s'", words[1]), true);
			}		
		}
		else {
			sendReply(message.getSenderMsisdn(), String.format("Invalid Message '%s'", message.getTextContent()), true);
		}
	}
	
	protected void sendReply(String msisdn, String text, boolean error) {
		if (error) {
			LOG.error("Reply: (%s) %s", msisdn, text);
		}
		else {
			LOG.debug("Reply: (%s) %s", msisdn, text);
		}
		//TODO frontline.sendTextMessage(msisdn, text);
	}

}
