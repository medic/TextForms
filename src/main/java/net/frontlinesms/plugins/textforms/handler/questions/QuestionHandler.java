package net.frontlinesms.plugins.textforms.handler.questions;

import java.util.Date;
import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.TextFormsProperties;
import net.frontlinesms.plugins.textforms.data.domain.OrganizationDetails;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.repository.AnswerDao;
import net.frontlinesms.plugins.textforms.data.repository.AnswerFactory;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;
import net.frontlinesms.plugins.textforms.handler.MessageHandler;
import net.frontlinesms.plugins.textforms.upload.ResourceFinderUploader;

import org.springframework.context.ApplicationContext;

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
				LOG.error(String.format("No question found for keyword. Contact: %s | Message:%s",message.getSenderMsisdn(),message.getTextContent()));
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidKeyword(words[0]), true);
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
					else {// we were unable to create the answer
						LOG.error(String.format("Unable to save answer. Contact: %s | Question: %s | Message:%s",message.getSenderMsisdn(),question.getName(), message.getTextContent()));
						sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerErrorSaveAnswer(), true);
					}
				}
				else { // the user is not registered
					LOG.error(String.format("Unregistered contact attempted to submit a message. Contact: %s | Question: %s | Message:%s",message.getSenderMsisdn(),question.getName(), message.getTextContent()));
					sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerPleaseRegister(TextFormsProperties.getRegisterKeywords()[0]), true);
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
			ResourceFinderUploader rfuploader = new ResourceFinderUploader();
			rfuploader.setPhoneNumber(answer.getContactPhone());
			rfuploader.setOrganizationId(answer.getContactOrganizationId());
			rfuploader.addAnswer(answer);
			return rfuploader.upload();
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
