package net.frontlinesms.plugins.textforms.handler.questions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.questions.DateQuestion;
import net.frontlinesms.plugins.textforms.data.domain.questions.QuestionType;

/**
 * DateHandler
 * @author dalezak
 *
 */
public class DateHandler extends CallbackHandler<DateQuestion> {
	
	@SuppressWarnings("unused")
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(DateHandler.class);	
	
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
		for (String format : new String [] {"dd/MM/yyyy", "dd-MM-yyyy", "dd/MM/yy", "dd-MM-yy", "ddMMyyyy", "ddMMyy"}) {
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
		String[] words = this.getWords(message.getTextContent(), 2);
		return words != null && words.length == 1 && isValidDate(words[0]);
	}

}
