package net.frontlinesms.plugins.surveys.handler.questions;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.questions.ChecklistQuestion;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;

/**
 * ChecklistHandler
 * @author dalezak
 *
 */
public class ChecklistHandler extends CodedHandler<ChecklistQuestion> {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(ChecklistHandler.class);	
	
	/**
	 * ChecklistQuestion
	 */
	private static final ChecklistQuestion checklistQuestion = new ChecklistQuestion();
	
	/**
	 * ChecklistHandler
	 */
	public ChecklistHandler() {}
	
	/**
	 * Get ChecklistQuestion keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.questionDao.getKeywordsForQuestion(checklistQuestion);
	}
	
	@Override
	protected boolean isValidAnswer(String[] words) {
		return words != null && words.length > 1 && areValidChoices(words[0], words[1]);
	}
	
	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		Question question = this.callbacks.get(message.getSenderMsisdn());
		if (question != null) {
			String[] words = this.toWords(message.getTextContent(), 2);
			return words != null && words.length == 1 && areValidChoices(question.getKeyword(), words[0]);
		}
		return false;
	}
	
	private boolean areValidChoices(String keyword, String words) {
		Question question = this.questionDao.getQuestionForKeyword(keyword);
		if (question != null) {
			List<String> choices = question.getChoices();
			//TODO improve this if-else logic
			if (words.indexOf(",") > -1) {
				for (String answer : words.split(",")) {
					if (answer.indexOf("-") > -1) {
						for (String range : answer.split("-")) {
							LOG.debug("Range: %s", range);
							if (isValidInteger(choices, range)) {
								//valid integer choice
							}
							else if (isValidString(choices, range)) {
								//valid string choice
							}
							else {
								LOG.error("Invalid Range: %s", range);
								return false;
							}
						}
					}
					else if (isValidInteger(choices, answer)) {
						//valid integer choice
					}
					else if (isValidString(choices, answer)) {
						//valid string choice
					}
					else {
						LOG.error("Invalid Answer: %s", answer);
						return false;
					}
				}	
			}
			else if (words.indexOf("-") > -1) {
				for (String range : words.split("-")) {
					if (isValidInteger(choices, range)) {
						//valid integer choice
					}
					else if (isValidString(choices, range)) {
						//valid string choice
					}
					else {
						LOG.error("Invalid Range: %s", range);
						return false;
					}
				}
			}
			else if (isValidInteger(choices, words)) {
				//valid integer choice
			}
			else if (isValidString(choices, words)) {
				//valid string choice
			}	
			else {
				LOG.error("Invalid Answer: %s", words);
				return false;
			}
			return true;
		}
		return false;
	}
	
}
