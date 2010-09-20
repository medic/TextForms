package net.frontlinesms.plugins.surveys.handler.questions;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.SurveysProperties;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.plugins.surveys.data.repository.QuestionDao;
import net.frontlinesms.plugins.surveys.data.repository.AnswerDao;
import net.frontlinesms.plugins.surveys.data.repository.AnswerFactory;
import net.frontlinesms.plugins.surveys.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.surveys.handler.MessageHandler;
import net.frontlinesms.plugins.surveys.upload.DocumentUploader;
import net.frontlinesms.plugins.surveys.upload.DocumentUploaderFactory;

/**
 * QuestionHandler
 * @author dalezak
 *
 * @param <Q> Question
 */
public abstract class QuestionHandler<Q extends Question> extends MessageHandler {

	private static final SurveysLogger LOG = SurveysLogger.getLogger(QuestionHandler.class);
	
	/**
	 * QuestionDao
	 */
	protected QuestionDao questionDao;
	
	/**
	 * AnswerDao
	 */
	protected AnswerDao answerDao;
	
	/**
	 * HospitalContactDao
	 */
	protected HospitalContactDao hospitalContactDao;
	
	/**
	 * QuestionHandler
	 */
	public QuestionHandler() {}
	
	/**
	 * Get the question class
	 * @return
	 */
	public abstract Class<Q> getQuestionClass();
	
	/**
	 * Set ApplicationContext
	 * @param appContext appContext
	 */
	public void setApplicationContext(ApplicationContext appContext) { 
		this.questionDao = (QuestionDao) appContext.getBean("questionDao");
		this.answerDao = (AnswerDao) appContext.getBean("answerDao");
		this.hospitalContactDao = (HospitalContactDao) appContext.getBean("hospitalContactDao");
	}
	
	/**
	 * Is this a valid response?
	 * @param words array of words
	 * @return true if valid
	 */
	protected abstract boolean isValidAnswer(String[] words);
	
	@SuppressWarnings("unchecked")
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.toWords(message.getTextContent(), 2);
		if (words.length == 1) {
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				sendReply(message.getSenderMsisdn(), getQuestionText(question, false), false);
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
						LOG.debug("Answer Created: %s", response.getClass());
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
	
	/**
	 * Is valid integer?
	 * @param word 
	 * @return true if valid integer
	 */
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
	 * Publish Answer
	 * @param answer Answer
	 * @return true if successful
	 */
	protected boolean publishAnswer(Answer<Q> answer) {
		if (answer != null) {
			LOG.debug("publishAnswer: %s", answer);
			DocumentUploader documentUploader = DocumentUploaderFactory.createDocumentUploader();
			if (documentUploader != null) {
				documentUploader.setPhoneNumber(answer.getSubmitterPhone());
				documentUploader.setHospitalId(answer.getSubmitterHospitalId());
				documentUploader.addAnswer(answer);
				return documentUploader.upload();
			}
		}
		else {
			LOG.error("publishAnswer: NULL");
		}
		return true;
	}
	
	/**
	 * Get all question question keywords
	 * @return
	 */
	protected String [] getAllKeywords() {
		List<String> keywords = this.questionDao.getKeywords();
		return keywords.toArray(new String[keywords.size()]);
	}
}
