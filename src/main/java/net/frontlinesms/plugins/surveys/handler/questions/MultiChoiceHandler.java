package net.frontlinesms.plugins.surveys.handler.questions;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.questions.MultiChoiceQuestion;

/**
 * MultiChoiceHandler
 * @author dalezak
 *
 */
public class MultiChoiceHandler extends CodedHandler<MultiChoiceQuestion> {
	
	@SuppressWarnings("unused")
	private static SurveysLogger LOG = SurveysLogger.getLogger(MultiChoiceHandler.class);
	
	/**
	 * MultiChoiceQuestion
	 */
	private static final MultiChoiceQuestion multiChoiceQuestion = new MultiChoiceQuestion();

	/**
	 * MultiChoiceHandler
	 */
	public MultiChoiceHandler() {}
	
	/**
	 * Get MultiChoiceQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(multiChoiceQuestion);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		if (words != null && words.length > 1) {
			Question question = this.questionDao.getQuestionForKeyword(words[0]);
			if (question != null) {
				List<String> choices = question.getChoices();
				return isValidInteger(choices, words[1]) || isValidString(choices, words[1]);
			}
		}
		return false;
	}
	
	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		Question question = this.callbacks.get(message.getSenderMsisdn());
		if (question != null) {
			List<String> choices = question.getChoices();
			String[] words = this.toWords(message.getTextContent(), 2);
			if (words != null && words.length == 1 && (isValidInteger(choices, words[0]) || isValidString(choices, words[0]))) {
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

}
