package net.frontlinesms.plugins.surveys.handler.questions;

import java.util.Date;
import java.util.HashMap;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysListener;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.SurveysProperties;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
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
	public void callBackTimedOut(String msisdn) {
		this.callbacks.remove(msisdn);
	}
	
	public abstract boolean shouldHandleCallbackMessage(FrontlineMessage m);
	
	/**
	 * Handle Coded message
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.toWords(message.getTextContent(), 2);
		if (words.length == 1) {
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				sendReply(message.getSenderMsisdn(), getQuestionText(question, true), false);
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
				HospitalContact contact = hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					Answer<Q> answer = AnswerFactory.createAnswer(message, contact, new Date(), contact.getHospitalId(), question);
					if (answer != null) {
						answerDao.saveAnswer(answer);
						LOG.debug("Answer Created: %s", answer);
						try {
							contact.setLastAnswer(new Date());
							hospitalContactDao.updateHospitalContact(contact);
						} 
						catch (DuplicateKeyException ex) {
							LOG.error("DuplicateKeyException: %s", ex);
						}
						if (publishAnswer(answer) == false) {
							sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerErrorUploadAnswer(), true);
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
	}
	
	@SuppressWarnings("unchecked")
	public void handleCallback(FrontlineMessage message) {
		LOG.debug("handleCallback: %s", message.getTextContent());
		if (shouldHandleCallbackMessage(message)) {
			Question question = callbacks.get(message.getSenderMsisdn());
			if (question != null) {
				HospitalContact contact = hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					Answer<Q> answer = AnswerFactory.createAnswer(message, contact, new Date(), contact.getHospitalId(), question);
					if (answer != null) {
						answerDao.saveAnswer(answer);
						publishAnswer(answer);
						LOG.debug("Answer Created: %s", answer.getClass());
						try {
							contact.setLastAnswer(new Date());
							this.hospitalContactDao.updateHospitalContact(contact);
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
	}
}
