package net.frontlinesms.plugins.surveys.handler;

import java.util.Arrays;
import java.util.Collection;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.SurveysProperties;
import net.frontlinesms.plugins.surveys.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;

import org.springframework.context.ApplicationContext;

/**
 * RegisterHandler
 * @author dalezak
 *
 */
public class RegisterHandler extends MessageHandler {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(RegisterHandler.class);
	
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
		return Arrays.asList(SurveysProperties.getRegisterKeywords());
	}
	
	/**
	 * Handle Register message
	 */
	@Override
	public boolean handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.toWords(message.getTextContent(), 2);
		if (words.length == 2) {
			HospitalContact contact = this.hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
			if (contact != null) {
				contact.setHospitalId(words[1]);
				try {
					this.hospitalContactDao.updateHospitalContact(contact);
					LOG.debug("HospitalContact '%s' Updated: %s", contact.getName(), contact.getHospitalId());
					sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerRegisterSuccessful(message.getSenderMsisdn()), true);
					return true;
				} 
				catch (DuplicateKeyException ex) {
					LOG.error("DuplicateKeyException: %s", ex);
					sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerErrorUpdateContact(contact.getDisplayName()), true);
				}
			}
			else {
				HospitalContact newContact = new HospitalContact(null, message.getSenderMsisdn(), null, true, words[1]);
				try {
					this.hospitalContactDao.saveHospitalContact(newContact);
					LOG.debug("HospitalContact '%s' Saved: %s", newContact.getName(), newContact.getHospitalId());
					sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerRegisterSuccessful(message.getSenderMsisdn()), true);
					return true;
				} 
				catch (DuplicateKeyException ex) {
					LOG.error("DuplicateKeyException: %s", ex);
					sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerErrorSaveContact(message.getSenderMsisdn()), true);
				}
			}
		}
		else {
			sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerRegister(SurveysProperties.getRegisterKeywords()), true);
		}
		return false;
	}
	
}
