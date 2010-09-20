package net.frontlinesms.plugins.surveys.handler.questions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.questions.DateQuestion;
import net.frontlinesms.plugins.surveys.data.domain.questions.QuestionType;

/**
 * DateHandler
 * @author dalezak
 *
 */
public class DateHandler extends CallbackHandler<DateQuestion> {
	
	@SuppressWarnings("unused")
	private static final SurveysLogger LOG = SurveysLogger.getLogger(DateHandler.class);	
	
	/**
	 * DateHandler
	 */
	public DateHandler() {}
	
	@Override
	public Class<DateQuestion> getQuestionClass() {
		return DateQuestion.class;
	}
	
	/**
	 * Get DateQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(QuestionType.DATE);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		return words != null && words.length == 2 && isValidDate(words[1]);
	}
	
	private boolean isValidDate(String word) {
		for (String format : new String [] {"dd/MM/yyyy", "dd-MM-yyyy", "dd/MM/yy", "dd-MM-yy"}) {
			try {
				if (word != null && word.length() > 0) {
					DateFormat dateFormat = new SimpleDateFormat(format);
					return dateFormat.parse(word) != null;	
				}
			} 
			catch (ParseException e) {
				//do nothing
			}
		}
		return false;
	}

	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		String[] words = this.toWords(message.getTextContent(), 2);
		return words != null && words.length == 1 && isValidDate(words[0]);
	}

}
