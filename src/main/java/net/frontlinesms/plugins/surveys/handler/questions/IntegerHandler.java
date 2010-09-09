package net.frontlinesms.plugins.surveys.handler.questions;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.questions.IntegerQuestion;

/**
 * IntegerHandler
 * @author dalezak
 *
 */
public class IntegerHandler extends CallbackHandler<IntegerQuestion> {
	
	@SuppressWarnings("unused")
	private static final SurveysLogger LOG = SurveysLogger.getLogger(IntegerHandler.class);
	
	/**
	 * IntegerQuestion
	 */
	private static final IntegerQuestion integerQuestion = new IntegerQuestion();
	
	/**
	 * IntegerHandler
	 */
	public IntegerHandler() {}

	/**
	 * Get IntegerQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(integerQuestion);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		return words != null && words.length == 2 && isValidInteger(words[1]);
	}

	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		String[] words = this.toWords(message.getTextContent(), 2);
		return words != null && words.length == 1 && isValidInteger(words[0]);
	}

}
