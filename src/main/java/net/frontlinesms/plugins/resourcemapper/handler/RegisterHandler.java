package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.Arrays;
import java.util.Collection;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperMessages;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;

import org.springframework.context.ApplicationContext;

/**
 * RegisterHandler
 * @author dalezak
 *
 */
public class RegisterHandler extends MessageHandler {
	
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(RegisterHandler.class);
	
	/**
	 * HospitalContactDao
	 */
	protected HospitalContactDao hospitalContactDao;
	
	/**
	 * RegisterHandler
	 */
	public RegisterHandler() {}
	
	/**
	 * Set ApplicationContext
	 * @param appContext appContext
	 */
	@Override
	public void setApplicationContext(ApplicationContext appContext) { 
		this.hospitalContactDao = (HospitalContactDao) appContext.getBean("hospitalContactDao");
	}
	
	/**
	 * Get Register keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return Arrays.asList(ResourceMapperProperties.getRegisterKeywords());
	}
	
	@Override
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.toWords(message.getTextContent(), 2);
		if (words.length == 2) {
			HospitalContact contact = this.hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
			if (contact != null) {
				contact.setHospitalId(words[1]);
				try {
					this.hospitalContactDao.updateHospitalContact(contact);
					LOG.debug("HospitalContact '%s' Updated: %s", contact.getName(), contact.getHospitalId());
					sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerRegisterSuccessful(message.getSenderMsisdn()), true);
				} 
				catch (DuplicateKeyException ex) {
					LOG.error("DuplicateKeyException: %s", ex);
					sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerErrorUpdateContact(contact.getDisplayName()), true);
				}
			}
			else {
				HospitalContact newContact = new HospitalContact(null, message.getSenderMsisdn(), null, true, words[1]);
				try {
					this.hospitalContactDao.saveHospitalContact(newContact);
					LOG.debug("HospitalContact '%s' Saved: %s", newContact.getName(), newContact.getHospitalId());
					sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerRegisterSuccessful(message.getSenderMsisdn()), true);
				} 
				catch (DuplicateKeyException ex) {
					LOG.error("DuplicateKeyException: %s", ex);
					sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerErrorSaveContact(message.getSenderMsisdn()), true);
				}
			}
		}
		else {
			sendReply(message.getSenderMsisdn(), ResourceMapperMessages.getHandlerRegister(ResourceMapperProperties.getRegisterKeywords()), true);
		}
	}
	
}
