package net.frontlinesms.plugins.textforms.handler.questions;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.domain.questions.MultiChoiceQuestion;
import net.frontlinesms.plugins.textforms.data.domain.questions.QuestionType;

/**
 * MultiChoiceHandler
 * @author dalezak
 *
 */
public class MultiChoiceHandler extends CodedHandler<MultiChoiceQuestion> {
	
	@SuppressWarnings("unused")
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(MultiChoiceHandler.class);

	/**
	 * MultiChoiceHandler
	 */
	public MultiChoiceHandler() {}
	
	@Override
	public Class<MultiChoiceQuestion> getQuestionClass() {
		return MultiChoiceQuestion.class;
	}
	
	/**
	 * Get MultiChoiceQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(QuestionType.MULTICHOICE);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		if (words != null && words.length > 1) {
			Question question = questionDao.getQuestionForKeyword(words[0]);
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
			String[] words = getWords(message.getTextContent(), 2);
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
