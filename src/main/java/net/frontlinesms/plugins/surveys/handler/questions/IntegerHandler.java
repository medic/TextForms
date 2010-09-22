package net.frontlinesms.plugins.surveys.handler.questions;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.questions.IntegerQuestion;
import net.frontlinesms.plugins.surveys.data.domain.questions.QuestionType;

/**
 * IntegerHandler
 * @author dalezak
 *
 */
public class IntegerHandler extends CallbackHandler<IntegerQuestion> {
	
	@SuppressWarnings("unused")
	private static final SurveysLogger LOG = SurveysLogger.getLogger(IntegerHandler.class);
	
	/**
	 * IntegerHandler
	 */
	public IntegerHandler() {}

	@Override
	public Class<IntegerQuestion> getQuestionClass() {
		return IntegerQuestion.class;
	}
	
	/**
	 * Get IntegerQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(QuestionType.INTEGER);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		return words != null && words.length == 2 && isValidInteger(words[1]);
	}

	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		String[] words = this.getWords(message.getTextContent(), 2);
		return words != null && words.length == 1 && isValidInteger(words[0]);
	}

}
