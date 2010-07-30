package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Date;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperMessages;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseFactory;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.handler.MessageHandler;
import net.frontlinesms.plugins.resourcemapper.upload.UploadDocument;
import net.frontlinesms.plugins.resourcemapper.upload.UploadDocumentFactory;

/**
 * FieldMessageHandler
 * @author dalezak
 *
 * @param <M> Field
 */
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
	
	/**
	 * Is this a valid response?
	 * @param words array of words
	 * @return true if valid
	 */
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
				sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerInvalidKeyword(words[0]), true);
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
						if (this.publishResponse(response) == false) {
							sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerErrorUploadResponse(), true);
						}
					}
					else {
						sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerErrorSaveResponse(), true);
					}
				}
				else {
					sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerRegister(ResourceMapperProperties.getRegisterKeywords()), true);
				}	
			}
			else {
				sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerInvalidKeyword(words[0]), true);
			}
		}
		else {
			sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerErrorResponse(message.getTextContent()), true);
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
	
	/**
	 * Publish Response
	 * @param fieldResponse FieldResponse
	 * @return true if successful
	 */
	protected boolean publishResponse(FieldResponse<M> fieldResponse) {
		if (fieldResponse != null) {
			LOG.debug("publishResponse: %s", fieldResponse);
			UploadDocument uploadDocument = UploadDocumentFactory.createUploadDocument();
			if (uploadDocument != null) {
				uploadDocument.setPhoneNumber(fieldResponse.getSubmitterPhone());
				uploadDocument.setHospitalId(fieldResponse.getSubmitterHospitalId());
				uploadDocument.addFieldResponse(fieldResponse);
				return uploadDocument.upload();
			}
		}
		else {
			LOG.error("publishResponse: NULL");
		}
		return true;
	}
}
