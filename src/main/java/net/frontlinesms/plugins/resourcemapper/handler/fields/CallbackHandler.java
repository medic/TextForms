package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Date;
import java.util.HashMap;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperListener;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperMessages;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseFactory;

/**
 * CallbackHandler
 * @author dalezak
 *
 * @param <M> Field
 */
public abstract class CallbackHandler<M extends Field> extends FieldMessageHandler<M> {

	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(CallbackHandler.class);
	
	/**
	 * Map of callbacks
	 */
	protected final HashMap<String, Field> callbacks = new HashMap<String, Field>();
	
	/**
	 * CallbackHandler
	 */
	public CallbackHandler() {} 
	
	/**
	 * Remove callback upon timeout
	 */
	public void callBackTimedOut(String msisdn) {
		this.callbacks.remove(msisdn);
	}
	
	public abstract boolean shouldHandleCallbackMessage(FrontlineMessage m);
	
	/**
	 * Handle Coded message
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.toWords(message.getTextContent(), 2);
		if (words.length == 1) {
			Field field = this.fieldMappingDao.getFieldForKeyword(words[0]);
			if (field != null) {
				StringBuilder reply = new StringBuilder(field.getName());
				reply.append(" (");
				reply.append(field.getTypeLabel());
				reply.append(")");
				if (field.getInfoSnippet() != null && field.getInfoSnippet().length() > 0) {
					reply.append(" ");
					reply.append(field.getInfoSnippet());	
				}
				if (field.getChoices() != null && field.getChoices().size() > 0) {
					int index = 1;
					for (String choice : field.getChoices()) {
						reply.append("\n");
						reply.append(index);
						reply.append(" ");
						reply.append(choice);
						index++;		
					}
				}
				sendReply(message.getSenderMsisdn(), reply.toString(), false);
				LOG.debug("Register Callback for '%s'", message.getTextContent());
				ResourceMapperListener.registerCallback(message.getSenderMsisdn(), this);
				this.callbacks.put(message.getSenderMsisdn(), this.fieldMappingDao.getFieldForKeyword(message.getTextContent()));
			}
			else {
				sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerInvalidKeyword(this.getAllKeywords()), true);
			}	
		}
		else if (isValidResponse(words)) {
			Field field = this.fieldMappingDao.getFieldForKeyword(words[0]);
			if (field != null) {
				HospitalContact contact = this.hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					FieldResponse response = FieldResponseFactory.createFieldResponse(message, contact, new Date(), contact.getHospitalId(), field);
					if (response != null) {
						this.fieldResponseDao.saveFieldResponse(response);
						LOG.debug("Response Created: %s", response);
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
				sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerInvalidKeyword(this.getAllKeywords()), true);
			}
		}
		else {
			Field field = this.fieldMappingDao.getFieldForKeyword(words[0]);
			if (field != null) {
				sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerInvalidResponse(field.getTypeLabel(), message.getTextContent()), true);
			}
			else {
				sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerErrorResponse(message.getTextContent()), true);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void handleCallback(FrontlineMessage message) {
		LOG.debug("handleCallback: %s", message.getTextContent());
		if (shouldHandleCallbackMessage(message)) {
			Field field = this.callbacks.get(message.getSenderMsisdn());
			if (field != null) {
				HospitalContact contact = this.hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					FieldResponse response = FieldResponseFactory.createFieldResponse(message, contact, new Date(), contact.getHospitalId(), field);
					if (response != null) {
						this.fieldResponseDao.saveFieldResponse(response);
						this.publishResponse(response);
						LOG.debug("FieldResponse Created: %s", response.getClass());
						try {
							contact.setLastResponse(new Date());
							this.hospitalContactDao.updateHospitalContact(contact);
						} 
						catch (DuplicateKeyException ex) {
							LOG.error("DuplicateKeyException: %s", ex);
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
				sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerInvalidCallback(), true);
			}
		}
		else {
			Field field = this.callbacks.get(message.getSenderMsisdn());
			if (field != null) {
				sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerInvalidResponse(field.getTypeLabel(), message.getTextContent()), true);
			}
			else {
				sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerErrorResponse(message.getTextContent()), true);
			}
		}
		ResourceMapperListener.unregisterCallback(message.getSenderMsisdn());
	}
}
