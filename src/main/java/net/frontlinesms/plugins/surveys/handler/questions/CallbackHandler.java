package net.frontlinesms.plugins.surveys.handler.questions;

import java.util.Date;
import java.util.HashMap;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysListener;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.SurveysProperties;
import net.frontlinesms.plugins.surveys.data.domain.OrganizationDetails;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.plugins.surveys.data.repository.AnswerFactory;

/**
 * CallbackHandler
 * @author dalezak
 *
 * @param <Q> Question
 */
public abstract class CallbackHandler<Q extends Question> extends QuestionHandler<Q> {

	private static final SurveysLogger LOG = SurveysLogger.getLogger(CallbackHandler.class);
	
	/**
	 * Map of callbacks
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
	
	public abstract boolean shouldHandleCallbackMessage(FrontlineMessage m);
	
	/**
	 * Handle Coded message
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean handleMessage(FrontlineMessage message) {
		boolean successful = false;
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.getWords(message.getTextContent(), 2);
		if (words.length == 1) {
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				sendReply(message.getSenderMsisdn(), question.toString(true), false);
				LOG.out("%s", question.toString(true));
				LOG.debug("Register Callback for '%s'", message.getTextContent());
				SurveysListener.registerCallback(message.getSenderMsisdn(), this);
				this.callbacks.put(message.getSenderMsisdn(), this.questionDao.getQuestionForKeyword(message.getTextContent()));
			}
			else {
				sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerInvalidKeyword(this.getAllKeywords()), true);
			}	
		}
		else if (isValidAnswer(words)) {
			Question question = questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				Contact contact = contactDao.getFromMsisdn(message.getSenderMsisdn());
				if (contact != null) {
					OrganizationDetails details = contact.getDetails(OrganizationDetails.class);
					String organizationId = details != null ? details.getOrganizationId() : null;
					if (details != null) {
						details.setLastAnswer(new Date());
					}
					else {
						contact.addDetails(new OrganizationDetails(new Date()));
					}
					Answer<Q> answer = AnswerFactory.createAnswer(message, contact, new Date(), organizationId, question);
					if (answer != null) {
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
					else {
						sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerErrorSaveAnswer(), true);
					}
				}
				else {
					sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerRegister(SurveysProperties.getRegisterKeywords()), true);
				}	
			}
			else {
				sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerInvalidKeyword(this.getAllKeywords()), true);
			}
		}
		else {
			Question question = questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerInvalidAnswer(question.getTypeLabel(), message.getTextContent()), true);
			}
			else {
				sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerErrorAnswer(message.getTextContent()), true);
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
			if (question != null) {
				Contact contact = contactDao.getFromMsisdn(message.getSenderMsisdn());
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
					else {
						sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerErrorSaveAnswer(), true);
					}
				}
				else {
					sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerRegister(SurveysProperties.getRegisterKeywords()), true);
				}		
			}
			else {
				LOG.debug("Callback Question is NULL");
				sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerInvalidCallback(), true);
			}
		}
		else {
			Question question = this.callbacks.get(message.getSenderMsisdn());
			if (question != null) {
				sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerInvalidAnswer(question.getTypeLabel(), message.getTextContent()), true);
			}
			else {
				sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerErrorAnswer(message.getTextContent()), true);
			}
		}
		SurveysListener.unregisterCallback(message.getSenderMsisdn());
		return answer;
	}
}
