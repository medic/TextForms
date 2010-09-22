package net.frontlinesms.plugins.surveys.data.domain.answers;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.questions.PlainTextQuestion;

@Entity
public class PlainTextAnswer extends Answer<PlainTextQuestion> {

	public PlainTextAnswer() {
		super();
	}

	public PlainTextAnswer(FrontlineMessage message, Contact contact, Date dateSubmitted, String organizationId, PlainTextQuestion question) {
		super(message, contact, dateSubmitted, organizationId, question);
	}
	
	@Override
	public boolean isAnswerFor(Question question) {
		return question.getClass() == PlainTextQuestion.class;
	}
	
	@Override
	public String getAnswerValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return words[0].trim();
		}
		else if (words != null && words.length == 2) {
			return words[1].trim();
		}
		return null;
	}
}
