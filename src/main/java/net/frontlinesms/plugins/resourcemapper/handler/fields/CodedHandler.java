package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperPluginController;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.CodedField;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseFactory;

/**
 * CodedHandler
 * @author dalezak
 *
 * @param <M> CodedField
 */
public abstract class CodedHandler<M extends CodedField> extends CallbackHandler<M> {

	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(CodedHandler.class);
	
	/**
	 * Map of callbacks
	 */
	protected final HashMap<String, Field> callbacks = new HashMap<String, Field>();
	
	/**
	 * CodedHandler
	 */
	public CodedHandler() {}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String content = message.getTextContent().replaceFirst("[\\s]", " ");
		String[] words = this.toWords(message.getTextContent(), 2);
		if (words.length == 1) {
			Field field = this.mappingDao.getFieldForKeyword(words[0]);
			if (field != null) {
				if (field.getChoices() != null) {
					StringBuilder reply = new StringBuilder();
					reply.append(field.getInfoSnippet());
					int index = 1;
					for (String choice : field.getChoices()) {
						reply.append("\n");
						reply.append(index);
						reply.append(" ");
						reply.append(choice);
						index++;		
					}
					sendReply(message.getSenderMsisdn(), reply.toString(), false);
					LOG.debug("Register Callback for '%s'", content);
					ResourceMapperPluginController.registerCallback(message.getSenderMsisdn(), this);
					this.callbacks.put(message.getSenderMsisdn(), mappingDao.getFieldForKeyword(content));
				}
				else {
					sendReply(message.getSenderMsisdn(), field.getInfoSnippet(), false);
				}
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
						LOG.debug("Response Created: %s", response);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleCallback(FrontlineMessage message) {
		LOG.debug("handleCallback: %s", message.getTextContent());
		if (shouldHandleCallbackMessage(message)) {
			Field field = this.callbacks.get(message.getSenderMsisdn());
			if (field != null) {
				HospitalContact contact = this.hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					FieldResponse response = FieldResponseFactory.createFieldResponse(message, contact, new Date(), contact.getHospitalId(), field);
					if (response != null) {
						this.responseDao.saveFieldResponse(response);
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
			sendReply(message.getSenderMsisdn(), "Invalid Response", true);
		}
		ResourceMapperPluginController.unregisterCallback(message.getSenderMsisdn());
	}
	
	@Override
	public void callBackTimedOut(String msisdn) {
		this.callbacks.remove(msisdn);
	}

	protected boolean isValidInteger(List<String> choices, String answer) {
		if (answer != null && isValidInteger(answer.trim())) {
			int value = Integer.parseInt(answer.trim());
			if (value > 0 && value <= choices.size()) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isValidString(List<String> choices, String answer) {
		if (choices != null && choices.size() > 0 && answer != null && answer.length() > 0) {
			String answerTrimmed = answer.trim();
			for (String choice : choices) {
				//TODO improve fuzzy string comparison logic
				if (choice.equalsIgnoreCase(answerTrimmed) || 
					choice.toLowerCase().startsWith(answerTrimmed.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
	
}
