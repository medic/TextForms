package net.frontlinesms.plugins.surveys.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.SurveysProperties;
import net.frontlinesms.plugins.surveys.data.repository.QuestionDao;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;

import org.springframework.context.ApplicationContext;

/**
 * InfoHandler
 * @author dalezak
 *
 */
public class InfoHandler extends MessageHandler {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(InfoHandler.class);
	
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
		this.questionDao = (QuestionDao) appContext.getBean("questionDao");
	}
	
	/**
	 * Get Info keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return Arrays.asList(SurveysProperties.getInfoKeywords());
	}
	
	/**
	 * Handle Info message
	 */
	@Override
	public void handleMessage(FrontlineMessage message) {
		LOG.debug("handleMessage: %s", message.getTextContent());
		String[] words = this.toWords(message.getTextContent(), 2);
		if (words.length == 2) {
			Question question = this.questionDao.getQuestionForKeyword(words[1]);
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
			}
			else {
				sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerInvalidKeyword(getAllKeywords()), true);
			}		
		}
		else {
			sendReply(message.getSenderMsisdn(), SurveysMessages.getHandlerHelp(getAllKeywords()), false);
		}
	}
	
	/**
	 * Get all keywords for all Questions
	 * @return
	 */
	private String [] getAllKeywords() {
		List<String> keywords = this.questionDao.getKeywords();
		return keywords.toArray(new String[keywords.size()]);
	}

}
