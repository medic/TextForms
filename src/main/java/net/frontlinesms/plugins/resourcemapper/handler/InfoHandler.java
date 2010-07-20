package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.ArrayList;
import java.util.Collection;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

import org.springframework.context.ApplicationContext;

public class InfoHandler implements MessageHandler {
	
	protected FrontlineSMS frontline;
	protected ApplicationContext appContext;
	protected FieldMappingDao mappingDao;
	
	public InfoHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		this.frontline = frontline;
		this.appContext = appContext;
		this.mappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
	}
	
	private Collection<String> getKeywords() {
		ArrayList<String> results = new ArrayList<String>();
		results.add("info");
		results.add("help");
		results.add("?");
		return results;
	}

	public void handleMessage(FrontlineMessage message) {
		System.out.println("InfoHandler.handleMessage: " + message);
		if (message == null) {
			//TODO show warning message
		}
		else if (isSatisfiedBy(message.getTextContent())) {
			//if it's valid, send the requested info snippet
			String [] words = message.getTextContent().split(" ");
			String abbrev = words.length == 1 ? words[0] : words[1];
			Field field = this.mappingDao.getFieldForAbbreviation(abbrev);
			if (field != null) {
				output(message.getSenderMsisdn(), field.getInfoSnippet());
			}
			else {
				output(message.getSenderMsisdn(), "No Field Mapping");
			}
		}
		else {
			//otherwise, send an error message
			output(message.getSenderMsisdn(), "Invalid Response Received");
		}
	}
	
	public boolean isSatisfiedBy(String response) {
		if (response == null) {
			return false;
		}
		String [] words = response.split(" ");
		if (words.length > 2) {
			//if there are more than 2 commands in the message then the message is invalid
			return false;
		}
		else if (words.length == 2) {
			//if the message contains a valid field name, then it is valid
			for (String keyword : getKeywords()) {
				if (keyword.equalsIgnoreCase(words[0])) {
					return true;
				}
			}
		} 
		else if (words.length == 1) {
			return mappingDao.getFieldForAbbreviation(words[0]) != null;
		}
		//otherwise, it's false
		return false;
	}
	
	protected void output(String msisdn, String text) {
		System.out.println("msisdn=" + msisdn + " text=" + text);
//TODO fix ResourceMapperProperties.getInstance().isInDebugMode() to work with resourcemapper.properties file
//		if (ResourceMapperProperties.getInstance().isInDebugMode()) {
//			System.out.println("msisdn=" + msisdn + " text=" + text);
//		}
//		else if (frontline != null) {
//			frontline.sendTextMessage(msisdn, text);
//		}
	}

}
