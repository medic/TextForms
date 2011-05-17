package net.frontlinesms.plugins.textforms.handler.questions;

import java.util.Date;
import java.util.HashMap;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.textforms.TextFormsListener;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.TextFormsProperties;
import net.frontlinesms.plugins.textforms.data.domain.OrganizationDetails;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;
import net.frontlinesms.plugins.textforms.data.repository.AnswerFactory;

/**
 * CallbackHandler
 * @author dalezak
 *
 * @param <Q> Question
 */
public abstract class CallbackHandler<Q extends Question> extends QuestionHandler<Q> {

	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(CallbackHandler.class);
	
	/**
	 * Map of callbacks <Phone Number, Question>
	 */
	protected final HashMap<String, Question> callbacks = new HashMap<String, Question>();
	
	/**
	 * CallbackHandler
	 */
	public CallbackHandler() {} 
	
	/**
	 * Remove callback upon timeout
	 */
	public void removeCallback(String msisdn) {
		this.callbacks.remove(msisdn);
	}
	
	/**
	 * Add callback
	 * @param msisdn phone number
	 * @param question question
	 */
	public void addCallback(String msisdn, Question question) {
		callbacks.put(msisdn, question);
	}
	
	/**
	 * This method should return true if the message is formatted properly
	 * @param m
	 * @return
	 */
	public abstract boolean shouldHandleCallbackMessage(FrontlineMessage m);
	
	/**
	 * Handle Coded message
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean handleMessage(FrontlineMessage message) {
		Contact contact = contactDao.getFromMsisdn(message.getSenderMsisdn());
		if (contact == null) {
			LOG.error(String.format("Unregistered contact attempted to submit a message. Contact: %s | Message:%s",message.getSenderMsisdn(), message.getTextContent()));
			sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerPleaseRegister(TextFormsProperties.getRegisterKeywords()[0]), true);
		}
		boolean successful = false;
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.getWords(message.getTextContent(), 2);
		if (words.length == 1) {// if there is only a keyword in the message
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {// if there is a question for the supplied keyword
				sendReply(message.getSenderMsisdn(), question.toString(true), false);
				LOG.debug("Register Callback for '%s'", message.getTextContent());
				TextFormsListener.registerCallback(message.getSenderMsisdn(), this);
				this.callbacks.put(message.getSenderMsisdn(), this.questionDao.getQuestionForKeyword(message.getTextContent()));
			}
			else {// if there was no question associated with the supplied keyword
				LOG.error(String.format("No question found for keyword. Contact: %s | Message:%s",message.getSenderMsisdn(),message.getTextContent()));
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidKeyword(words[0]), true);
			}	
		}
		else if (isValidAnswer(words)) { //if this is a valid one-message response
			Question question = questionDao.getQuestionForKeyword(words[0]);
			if (question != null) { //if there is a question for the supplied keyword
					OrganizationDetails details = contact.getDetails(OrganizationDetails.class);
					String organizationId = details != null ? details.getOrganizationId() : null;
					if (details != null) {
						details.setLastAnswer(new Date());
					}
					else {
						contact.addDetails(new OrganizationDetails(new Date()));
					}
					Answer<Q> answer = AnswerFactory.createAnswer(message, contact, new Date(), organizationId, question);
					if (answer != null) { // if we were able to create the answer
						answerDao.saveAnswer(answer);
						LOG.debug("Answer Created: %s", answer);
						try {
							contactDao.updateContact(contact);
						} 
						catch (DuplicateKeyException ex) {
							LOG.error("DuplicateKeyException: %s", ex);
						}
						if(publishAnswer(answer)) {
							LOG.debug("Answer Published: %s", answer.getClass().getSimpleName());
						}
						else {
							LOG.debug("Answer NOT Published: %s", answer.getClass().getSimpleName());
						}
						successful = true;
					}
					else {// we were unable to create the answer
						LOG.error(String.format("Unable to save answer. Contact: %s | Question: %s | Message:%s",message.getSenderMsisdn(),question.getName(), message.getTextContent()));
						sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerErrorSaveAnswer(), true);
					}
				}
			else { // there was no question associated with the supplied keyword
				LOG.error(String.format("No question found for keyword. Contact: %s | Message:%s",message.getSenderMsisdn(),message.getTextContent()));
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidKeyword(words[0]), true);
			}
		}
		else { // the message was badly formatted
			Question question = questionDao.getQuestionForKeyword(words[0]);
			String response = "";
			for(int i = 1; i < words.length;i++){
				response +=words[i];
				if(i != words.length -1){
					response+=" ";
				}
			}
			LOG.error(String.format("Message was formatted improperly. Contact: %s | Message: %s",message.getSenderMsisdn(), message.getTextContent()));
			if (question != null) { // if there is a question associated with the keyword
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidAnswerSpecific(question.getName(), question.getTypeLabel(), response),true);
			}
			else { // if there is no question associated with the keyword
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidKeyword(words[0]), true);
			}
		}
		return successful;
	}
	
	@SuppressWarnings("unchecked")
	public Answer<Q> handleCallback(FrontlineMessage message) {
		LOG.debug("handleCallback: %s", message.getTextContent());
		Answer<Q> answer = null;
		if (shouldHandleCallbackMessage(message)) {
			Question question = callbacks.get(message.getSenderMsisdn());
			//if there was a callback out for this user
			if (question != null) {
				Contact contact = contactDao.getFromMsisdn(message.getSenderMsisdn());
				//if the user is actually a contact
				if (contact != null) {
					OrganizationDetails details = contact.getDetails(OrganizationDetails.class);
					String organizationId = details != null ? details.getOrganizationId() : null;
					if (details != null) {
						details.setLastAnswer(new Date());
					}
					else {
						contact.addDetails(new OrganizationDetails(new Date()));
					}
					answer = AnswerFactory.createAnswer(message, contact, new Date(), organizationId, question);
					//if we were able to successfully create the proper answer
					if (answer != null) {
						answerDao.saveAnswer(answer);
						LOG.debug("Answer Saved: %s", answer.getClass().getSimpleName());
						if(publishAnswer(answer)) {
							LOG.debug("Answer Published: %s", answer.getClass().getSimpleName());
						}
						else {
							LOG.debug("Answer NOT Published: %s", answer.getClass().getSimpleName());
						}
						try {
							this.contactDao.updateContact(contact);
						} 
						catch (DuplicateKeyException ex) {
							LOG.error("DuplicateKeyException: %s", ex);
						}
					}
					else {// unable to create answer
						LOG.error(String.format("Unable to save answer. Contact: %s | Question: %s | Message:%s",message.getSenderMsisdn(),question.getName(), message.getTextContent()));
						sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerErrorSaveAnswer(), true);
					}
				}// unregistered contact
				else {
					LOG.error(String.format("Unregistered contact attempted to submit a message. Contact: %s | Question: %s | Message:%s",message.getSenderMsisdn(),question.getName(), message.getTextContent()));
					sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerPleaseRegister(TextFormsProperties.getRegisterKeywords()[0]), true);
				}		
			}// no callback found
			else {
				LOG.error(String.format("System error - callback was null: %s | Message:%s",message.getSenderMsisdn(),message.getTextContent()));
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidCallback(), true);
			}
		}
		else {// if there was an error with the answer
			LOG.error(String.format("Message was formatted improperly. Contact: %s | Message: %s",message.getSenderMsisdn(), message.getTextContent()));
			Question question = this.callbacks.get(message.getSenderMsisdn());
			if (question != null) {//invalid answer, please try again 
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidAnswerSpecific(question.getName(), question.getTypeLabel(), message.getTextContent()),true);
			}
			else {
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidAnswerGeneral(), true);
			}
		}
		TextFormsListener.unregisterCallback(message.getSenderMsisdn());
		return answer;
	}
}
