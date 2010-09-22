package net.frontlinesms.plugins.surveys.handler;

import java.util.Arrays;
import java.util.Collection;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.SurveysProperties;
import net.frontlinesms.plugins.surveys.data.domain.OrganizationDetails;

import org.springframework.context.ApplicationContext;

/**
 * RegisterHandler
 * @author dalezak
 *
 */
public class RegisterHandler extends MessageHandler {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(RegisterHandler.class);
	
	/**
	 * ContactDao
	 */
	protected ContactDao contactDao;
	
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
		this.contactDao = (ContactDao) appContext.getBean("contactDao", ContactDao.class);
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
		String[] words = this.getWords(message.getTextContent(), 2);
		if (words.length == 2) {
			Contact contact = this.contactDao.getFromMsisdn(message.getSenderMsisdn());
			if (contact != null) {
				OrganizationDetails details = contact.getDetails(OrganizationDetails.class);
				if (details != null) {
					details.setOrganizationId(words[1]);
				}
				else {
					contact.addDetails(new OrganizationDetails(words[1]));
				}
				try {
					this.contactDao.updateContact(contact);
					LOG.debug("Contact '%s' Updated: %s", contact.getName(), words[1]);
					sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerRegisterSuccessful(message.getSenderMsisdn()), true);
					return true;
				} 
				catch (DuplicateKeyException ex) {
					LOG.error("DuplicateKeyException: %s", ex);
					sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerErrorUpdateContact(contact.getDisplayName()), true);
				}
			}
			else {
				Contact newContact = new Contact(null, message.getSenderMsisdn(), null, null, null, true);
				newContact.addDetails(new OrganizationDetails(words[1]));
				try {
					this.contactDao.saveContact(newContact);
					LOG.debug("Contact '%s' Saved: %s", newContact.getName(), words[1]);
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
