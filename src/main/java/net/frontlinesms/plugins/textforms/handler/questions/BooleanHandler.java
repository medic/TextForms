package net.frontlinesms.plugins.textforms.handler.questions;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsProperties;
import net.frontlinesms.plugins.textforms.data.domain.questions.BooleanQuestion;
import net.frontlinesms.plugins.textforms.data.domain.questions.QuestionType;

/**
 * BooleanHandler
 * @author dalezak
 *
 */
public class BooleanHandler extends CodedHandler<BooleanQuestion> {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(BooleanHandler.class);
	
	/**
	 * BooleanHandler
	 */
	public BooleanHandler() {}
	
	@Override
	public Class<BooleanQuestion> getQuestionClass() {
		return BooleanQuestion.class;
	}
	
	/**
	 * Get BooleanQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(QuestionType.BOOLEAN);
	}
	
	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		String[] words = this.getWords(message.getTextContent(), 2);
		return words != null && words.length == 1 && isValidBoolean(words[0]);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		return words != null && words.length == 2 && isValidBoolean(words[1]);
	}
	
	private boolean isValidBoolean(String word) {
		if (word != null) {
			for (String trueValue : TextFormsProperties.getBooleanTrueValues()) {
				if (trueValue.trim().equalsIgnoreCase(word.trim())) {
					return true;
				}
			}
			for (String falseValue : TextFormsProperties.getBooleanFalseValues()) {
				if (falseValue.trim().equalsIgnoreCase(word.trim())) {
					return true;
				}
			}
		}
		LOG.error("Invalid Boolean Value: %s", word);
		return false;
	}

}
