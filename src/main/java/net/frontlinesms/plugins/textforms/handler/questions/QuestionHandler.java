package net.frontlinesms.plugins.textforms.handler.questions;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.TextFormsProperties;
import net.frontlinesms.plugins.textforms.data.domain.OrganizationDetails;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;
import net.frontlinesms.plugins.textforms.data.repository.AnswerDao;
import net.frontlinesms.plugins.textforms.data.repository.AnswerFactory;
import net.frontlinesms.plugins.textforms.handler.MessageHandler;
import net.frontlinesms.plugins.textforms.upload.DocumentUploader;
import net.frontlinesms.plugins.textforms.upload.DocumentUploaderFactory;

/**
 * QuestionHandler
 * @author dalezak
 *
 * @param <Q> Question
 */
public abstract class QuestionHandler<Q extends Question> extends MessageHandler {

	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(QuestionHandler.class);
	
	/**
	 * QuestionDao
	 */
	protected QuestionDao questionDao;
	
	/**
	 * AnswerDao
	 */
	protected AnswerDao answerDao;
	
	/**
	 * ContactDao
	 */
	protected ContactDao contactDao;
	
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
		this.questionDao = (QuestionDao) appContext.getBean("questionDao", QuestionDao.class);
		this.answerDao = (AnswerDao) appContext.getBean("answerDao", AnswerDao.class);
		this.contactDao = (ContactDao) appContext.getBean("contactDao", ContactDao.class);
	}
	
	/**
	 * Is this a valid response?
	 * @param words array of words
	 * @return true if valid
	 */
	protected abstract boolean isValidAnswer(String[] words);
	
	@SuppressWarnings("unchecked")
	public boolean handleMessage(FrontlineMessage message) {
		boolean successful = false;
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.getWords(message.getTextContent(), 2);
		if (words.length == 1) {
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				sendReply(message.getSenderMsisdn(), question.toString(), false);
			}
			else {
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidKeyword(this.getAllKeywords()), true);
			}	
		}
		else if (isValidAnswer(words)) {
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				Contact contact = this.contactDao.getFromMsisdn(message.getSenderMsisdn());
				if (contact != null) {
					OrganizationDetails details = contact.getDetails(OrganizationDetails.class);
					String organizationId = details != null ? details.getOrganizationId() : null;
					if (details != null) {
						details.setLastAnswer(new Date());
					}
					else {
						contact.addDetails(new OrganizationDetails(new Date()));
					}
					Answer answer = AnswerFactory.createAnswer(message, contact, new Date(), organizationId, question);
					if (answer != null) {
						this.answerDao.saveAnswer(answer);
						LOG.debug("Answer Saved: %s", answer.getClass());
						try {
							this.contactDao.updateContact(contact);
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
						sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerErrorSaveAnswer(), true);
					}
				}
				else {
					sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerRegister(TextFormsProperties.getRegisterKeywords()), true);
				}	
			}
			else {
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidKeyword(this.getAllKeywords()), true);
			}
		}
		else {
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidAnswer(question.getName(), question.getTypeLabel()), true);
			}
			else {
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerErrorAnswer(message.getTextContent()), true);
			}
		}
		return successful;
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
				documentUploader.setPhoneNumber(answer.getContactPhone());
				documentUploader.setOrganizationId(answer.getContactOrganizationId());
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
