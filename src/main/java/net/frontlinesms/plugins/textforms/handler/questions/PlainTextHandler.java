package net.frontlinesms.plugins.textforms.handler.questions;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.questions.PlainTextQuestion;
import net.frontlinesms.plugins.textforms.data.domain.questions.QuestionType;

/**
 * PlainTextHandler
 * @author dalezak
 *
 */
public class PlainTextHandler extends CallbackHandler<PlainTextQuestion> {
	
	@SuppressWarnings("unused")
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(PlainTextHandler.class);
	
	/**
	 * PlainTextHandler
	 */
	public PlainTextHandler() {}
	
	@Override
	public Class<PlainTextQuestion> getQuestionClass() {
		return PlainTextQuestion.class;
	}
	
	/**
	 * Get PlainTextQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(QuestionType.PLAINTEXT);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		return words != null && words.length > 0 && isValidString(words[1]);
	}

	private boolean isValidString(String word) {
		return word != null && word.trim().length() > 0;
	}

	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage m) {
		return m.getTextContent() != null && m.getTextContent().trim().length() > 0;
	}
}
