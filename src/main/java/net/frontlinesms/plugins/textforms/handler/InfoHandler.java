package net.frontlinesms.plugins.textforms.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.TextFormsProperties;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;

import org.springframework.context.ApplicationContext;

/**
 * InfoHandler
 * @author dalezak
 *
 */
public class InfoHandler extends MessageHandler {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(InfoHandler.class);
	
	/**
	 * QuestionDao
	 */
	protected QuestionDao questionDao;
	
	/**
	 * InfoHandler
	 */
	public InfoHandler() {}
	
	/**
	 * Set ApplicationContext
	 * @param appContext appContext
	 */
	@Override
	public void setApplicationContext(ApplicationContext appContext) { 
		this.questionDao = (QuestionDao) appContext.getBean("questionDao", QuestionDao.class);
	}
	
	/**
	 * Get Info keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return Arrays.asList(TextFormsProperties.getInfoKeywords());
	}
	
	/**
	 * Handle Info message
	 */
	@Override
	public boolean handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = getWords(message.getTextContent(), 2);
		if (words.length == 2) {
			Question question = questionDao.getQuestionForKeyword(words[1]);
			if (question != null) {
				StringBuilder reply = new StringBuilder(question.getName());
				reply.append(" (");
				reply.append(question.getTypeLabel());
				reply.append(")");
				if (question.getInfoSnippet() != null && question.getInfoSnippet().length() > 0) {
					reply.append(" ");
					reply.append(question.getInfoSnippet());	
				}
				sendReply(message.getSenderMsisdn(), reply.toString(), false);
				return true;
			}
			else {
				sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerInvalidKeyword(getAllKeywords()), true);
			}		
		}
		else {
			sendReply(message.getSenderMsisdn(), TextFormsMessages.getHandlerHelp(getAllKeywords()), false);
		}
		return false;
	}
	
	/**
	 * Get all keywords for all Questions
	 * @return
	 */
	private String [] getAllKeywords() {
		List<String> keywords = questionDao.getKeywords();
		return keywords.toArray(new String[keywords.size()]);
	}

}
