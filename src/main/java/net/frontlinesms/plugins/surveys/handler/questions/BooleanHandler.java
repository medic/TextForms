package net.frontlinesms.plugins.surveys.handler.questions;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysProperties;
import net.frontlinesms.plugins.surveys.data.domain.questions.BooleanQuestion;

/**
 * BooleanHandler
 * @author dalezak
 *
 */
public class BooleanHandler extends CodedHandler<BooleanQuestion> {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(BooleanHandler.class);
	
	/**
	 * BooleanQuestion
	 */
	private static final BooleanQuestion booleanQuestion = new BooleanQuestion();
	
	/**
	 * BooleanHandler
	 */
	public BooleanHandler() {}
	
	/**
	 * Get BooleanQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(booleanQuestion);
	}
	
	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		String[] words = this.toWords(message.getTextContent(), 2);
		return words != null && words.length == 1 && isValidBoolean(words[0]);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		return words != null && words.length == 2 && isValidBoolean(words[1]);
	}
	
	private boolean isValidBoolean(String word) {
		if (word != null) {
			for (String trueValue : SurveysProperties.getBooleanTrueValues()) {
				if (trueValue.trim().equalsIgnoreCase(word.trim())) {
					return true;
				}
			}
			for (String falseValue : SurveysProperties.getBooleanFalseValues()) {
				if (falseValue.trim().equalsIgnoreCase(word.trim())) {
					return true;
				}
			}
		}
		LOG.error("Invalid Boolean Value: %s", word);
		return false;
	}

}
