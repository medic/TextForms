package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Date;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseFactory;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.handler.MessageHandler;

public abstract class FieldMessageHandler<M extends Field> implements MessageHandler {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(FieldMessageHandler.class);
	
	protected FrontlineSMS frontline;
	protected ApplicationContext appContext;
	
	protected FieldMappingDao mappingDao;
	protected FieldResponseDao responseDao;
	protected HospitalContactDao contactDao;
	
	public FieldMessageHandler() {
		this(null, null);
	}
	
	public FieldMessageHandler(FrontlineSMS frontline, ApplicationContext appContext){
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
			this.responseDao = (FieldResponseDao) appContext.getBean("fieldResponseDao");
			this.contactDao = (HospitalContactDao) appContext.getBean("hospitalContactDao");
		}
	}
	
	protected abstract boolean isValidResponse(String[] words);
	
	@SuppressWarnings("unchecked")
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = message.getTextContent().replaceFirst("[\\s]", " ").split(" ", 2);
		if (words.length == 1) {
			Field field = this.mappingDao.getFieldForKeyword(words[0]);
			if (field != null) {
				sendReply(message.getSenderMsisdn(), field.getInfoSnippet(), false);
			}
			else {
				sendReply(message.getSenderMsisdn(), String.format("No Field Mapping Found For '%s'", words[0]), true);
			}	
		}
		else if (isValidResponse(words)) {
			Field field = this.mappingDao.getFieldForKeyword(words[0]);
			if (field != null) {
				HospitalContact contact = this.contactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					FieldResponse response = FieldResponseFactory.createFieldResponse(message, contact, new Date(), contact.getHospitalId(), field);
					if (response != null) {
						this.responseDao.saveFieldResponse(response);
						generateAndPublishXML(response);
						LOG.debug("FieldResponse Created: %s", response.getClass());
						try {
							contact.setLastResponse(new Date());
							this.contactDao.updateHospitalContact(contact);
						} 
						catch (DuplicateKeyException ex) {
							LOG.error("DuplicateKeyException: %s", ex);
						}
					}
					else {
						sendReply(message.getSenderMsisdn(), "Warning, unable to create response", true);
					}
				}
				else {
					sendReply(message.getSenderMsisdn(), "Warning, hospital contact is required", true);
				}	
			}
			else {
				sendReply(message.getSenderMsisdn(), "Warning, field mapping is required", true);
			}
		}
		else {
			sendReply(message.getSenderMsisdn(), String.format("Invalid Response Received '%s'", message.getTextContent()), true);
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
	
	protected boolean isValidInteger(String word) {
		try {
			if (word != null) {
				Integer.parseInt(word.trim());
				return true;	
			}
		} 
		catch (NumberFormatException nfe) {
			//do nothing
		}
		return false;
	}
	
	protected void generateAndPublishXML(FieldResponse<M> response) {
		LOG.debug("generateAndPublishXML: %s", response);
		if (response != null) {
//			Document document = XMLUtils.getInitializedDocument(response);
//			String content = response.getMessage().getTextContent().replaceFirst("[\\s]", " ");
//			String [] words = content.split(" ", 2);
//			String keyword = words[0];
//			String text = words[1];
//			LOG.debug("keyword:%s text:%s", keyword, text);
//			String pathToElement = response.getMapping().getPathToElement();
//			if (pathToElement != null) {
//				String path = pathToElement + "=" + text;
//				XMLUtils.handlePath(path, document);
//			}
//			for (String paths: response.getMapping().getAdditionalInstructions()) {
//				XMLUtils.handlePath(paths, document);
//			}
//			XMLPublisher.publish(document.asXML());
		}
		else {
			LOG.debug("FieldMessageHandler.generateAndPublishXML response is NULL");
		}
	}
}
