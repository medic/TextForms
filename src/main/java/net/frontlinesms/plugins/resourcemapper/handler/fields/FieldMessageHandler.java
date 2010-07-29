package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Date;

import org.springframework.context.ApplicationContext;

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

public abstract class FieldMessageHandler<M extends Field> extends MessageHandler {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(FieldMessageHandler.class);
	
	/**
	 * FieldMappingDao
	 */
	protected FieldMappingDao mappingDao;
	
	/**
	 * FieldResponseDao
	 */
	protected FieldResponseDao responseDao;
	
	/**
	 * HospitalContactDao
	 */
	protected HospitalContactDao hospitalContactDao;
	
	/**
	 * FieldMessageHandler
	 */
	public FieldMessageHandler() {}
	
	/**
	 * Set ApplicationContext
	 * @param appContext appContext
	 */
	public void setApplicationContext(ApplicationContext appContext) { 
		this.mappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
		this.responseDao = (FieldResponseDao) appContext.getBean("fieldResponseDao");
		this.hospitalContactDao = (HospitalContactDao) appContext.getBean("hospitalContactDao");
	}
	
	protected abstract boolean isValidResponse(String[] words);
	
	@SuppressWarnings("unchecked")
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.toWords(message.getTextContent(), 2);
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
				HospitalContact contact = this.hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					FieldResponse response = FieldResponseFactory.createFieldResponse(message, contact, new Date(), contact.getHospitalId(), field);
					if (response != null) {
						this.responseDao.saveFieldResponse(response);
						LOG.debug("FieldResponse Created: %s", response.getClass());
						try {
							contact.setLastResponse(new Date());
							this.hospitalContactDao.updateHospitalContact(contact);
						} 
						catch (DuplicateKeyException ex) {
							LOG.error("DuplicateKeyException: %s", ex);
						}
						this.publishResponse(response);
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
	
	protected void publishResponse(FieldResponse<M> response) {
		if (response != null) {
			//TODO call the selected UploadDocument handler
			LOG.debug("publishResponse: %s", response);
		}
		else {
			LOG.error("Response is NULL");
		}
	}
}
