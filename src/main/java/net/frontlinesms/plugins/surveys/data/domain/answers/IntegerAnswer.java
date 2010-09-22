package net.frontlinesms.plugins.surveys.data.domain.answers;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.questions.IntegerQuestion;

@Entity
public class IntegerAnswer extends Answer<IntegerQuestion> {

	public IntegerAnswer() {
		super();
	}

	public IntegerAnswer(FrontlineMessage message, Contact contact, Date dateSubmitted, String organizationId, IntegerQuestion question) {
		super(message, contact, dateSubmitted, organizationId, question);
	}

	@Override
	public boolean isAnswerFor(Question question) {
		return question.getClass() == IntegerQuestion.class;
	}
	
	@Override
	public String getAnswerValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return toIntegerString(words[0]);
		}
		else if (words != null && words.length == 2) {
			return toIntegerString(words[1]);
		}
		return null;
	}
	
	private String toIntegerString(String word) {
		return this.isValidInteger(word) ? word : null;
	}
}
