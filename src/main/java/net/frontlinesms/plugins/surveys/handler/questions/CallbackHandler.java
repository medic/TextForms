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
 * @param <M> Question
 */
public abstract class CallbackHandler<M extends Question> extends QuestionHandler<M> {

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
	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.toWords(message.getTextContent(), 2);
		if (words.length == 1) {
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				StringBuilder reply = new StringBuilder(question.getName());
				reply.append(" (");
				reply.append(question.getTypeLabel());
				reply.append(")");
				if (question.getInfoSnippet() != null && question.getInfoSnippet().length() > 0) {
					reply.append(" ");
					reply.append(question.getInfoSnippet());	
				}
				if (question.getChoices() != null && question.getChoices().size() > 0) {
					int index = 1;
					for (String choice : question.getChoices()) {
						reply.append("\n");
						reply.append(index);
						reply.append(" ");
						reply.append(choice);
						index++;		
					}
				}
				sendReply(message.getSenderMsisdn(), reply.toString(), false);
				LOG.debug("Register Callback for '%s'", message.getTextContent());
				SurveysListener.registerCallback(message.getSenderMsisdn(), this);
				this.callbacks.put(message.getSenderMsisdn(), this.questionDao.getQuestionForKeyword(message.getTextContent()));
			}
			else {
				sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerInvalidKeyword(this.getAllKeywords()), true);
			}	
		}
		else if (isValidAnswer(words)) {
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				HospitalContact contact = this.hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					Answer response = AnswerFactory.createAnswer(message, contact, new Date(), contact.getHospitalId(), question);
					if (response != null) {
						this.answerDao.saveAnswer(response);
						LOG.debug("Answer Created: %s", response);
						try {
							contact.setLastAnswer(new Date());
							this.hospitalContactDao.updateHospitalContact(contact);
						} 
						catch (DuplicateKeyException ex) {
							LOG.error("DuplicateKeyException: %s", ex);
						}
						if (this.publishAnswer(response) == false) {
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
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
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
			Question question = this.callbacks.get(message.getSenderMsisdn());
			if (question != null) {
				HospitalContact contact = this.hospitalContactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					Answer response = AnswerFactory.createAnswer(message, contact, new Date(), contact.getHospitalId(), question);
					if (response != null) {
						this.answerDao.saveAnswer(response);
						this.publishAnswer(response);
						LOG.debug("Answer Created: %s", response.getClass());
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
