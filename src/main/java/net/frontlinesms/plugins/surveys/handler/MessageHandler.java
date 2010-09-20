package net.frontlinesms.plugins.surveys.handler;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;

/**
 * MessageHandler
 * @author dalezak
 *
 */
public abstract class MessageHandler {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(MessageHandler.class);
	
	/**
	 * FrontlineSMS
	 */
	private FrontlineSMS frontline;
	
	/**
	 * MessageHandler
	 */
	public MessageHandler() {}
	
	/**
	 * Set FrontlineSMS
	 * @param frontline FrontlineSMS
	 */
	public void setFrontline(FrontlineSMS frontline) {
		this.frontline = frontline;
	}
	
	/**
	 * Set ApplicationContext
	 * @param appContext ApplicationContext
	 */
	public abstract void setApplicationContext(ApplicationContext appContext);
	
	/**
	 * Handle incoming message
	 * @param message FrontlineMessage
	 */
	public abstract void handleMessage(FrontlineMessage message);
	
	/**
	 * Get the list of keywords for this question
	 * @return
	 */
	public abstract Collection<String> getKeywords();
	
	/**
	 * Send reply to user
	 * @param msisdn phone number
	 * @param text reply text
	 * @param error is this an error message?
	 */
	protected void sendReply(String msisdn, String text, boolean error) {
		if (error) {
			LOG.error("(%s) %s", msisdn, text);
		}
		else {
			LOG.debug("(%s) %s", msisdn, text);
		}
		if (this.frontline != null) {
			this.frontline.sendTextMessage(msisdn, text);
		}
	}
	
	/**
	 * Convert message into tokenized string array
	 * @param message string message
	 * @param limit number of times to split the string
	 * @return array of strings
	 */
	protected String[] toWords(String message, int limit) {
		if (message != null) {
			return message.replaceFirst("[\\s]", " ").split(" ", limit);
		}
		return new String[0];
	}
	
	/**
	 * Convert string array into a string, joining with commas
	 * @param args array
	 * @return joined string
	 */
	protected String arrayToString(String [] args) {
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			if (sb.length() > 0) {
				 sb.append(",");
			}
			else {
				 sb.append("[");
			}
			sb.append(arg);
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Get Question Text
	 * @param question Question
	 * @return text
	 */
	protected String getQuestionText(Question question, boolean includeChoices) {
		StringBuilder questionText = new StringBuilder(question.getName());
		questionText.append(" (");
		questionText.append(question.getTypeLabel());
		questionText.append(")");
		if (question.getInfoSnippet() != null && question.getInfoSnippet().length() > 0) {
			questionText.append(" ");
			questionText.append(question.getInfoSnippet());	
		}
		if (includeChoices && question.getChoices() != null && question.getChoices().size() > 0) {
			int index = 1;
			for (String choice : question.getChoices()) {
				questionText.append("\n");
				questionText.append(index);
				questionText.append(" ");
				questionText.append(choice);
				index++;		
			}
		}
		return questionText.toString();
	}
	
}
