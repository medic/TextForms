package net.frontlinesms.plugins.textforms.handler.questions;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.questions.ImageQuestion;
import net.frontlinesms.plugins.textforms.data.domain.questions.QuestionType;

/**
 * ImageHandler
 * @author dalezak
 *
 */
public class ImageHandler extends CallbackHandler<ImageQuestion> {
	
	@SuppressWarnings("unused")
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(ImageHandler.class);
	
	/**
	 * ImageHandler
	 */
	public ImageHandler() {}
	
	@Override
	public Class<ImageQuestion> getQuestionClass() {
		return ImageQuestion.class;
	}
	
	/**
	 * Get PlainTextQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(QuestionType.IMAGE);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		return true;
	}

	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage m) {
		return m.getBinaryContent() != null && m.getTextContent().length() > 0;
	}
}
