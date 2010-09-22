package net.frontlinesms.plugins.surveys.data.domain.answers;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysProperties;
import net.frontlinesms.plugins.surveys.data.domain.questions.BooleanQuestion;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;

@Entity
public class BooleanAnswer extends Answer<BooleanQuestion> {

	public BooleanAnswer() {
		super();
	}

	public BooleanAnswer(FrontlineMessage message, Contact contact,
			Date dateSubmitted, String organizationId, BooleanQuestion question) {
		super(message, contact, dateSubmitted, organizationId, question);
	}
	
	@Override
	public boolean isAnswerFor(Question question) {
		return question.getClass() == BooleanQuestion.class;
	}
	
	@Override
	public String getAnswerValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return toBooleanString(words[0]);
		}
		else if (words != null && words.length == 2) {
			return toBooleanString(words[1]);
		}
		return null;
	}
	
	private String toBooleanString(String word) {
		for (String trueValue : SurveysProperties.getBooleanTrueValues()) {
			if (word.equalsIgnoreCase(trueValue)) {
				return "TRUE";
			}
		}
		for (String falseValue : SurveysProperties.getBooleanFalseValues()) {
			if (word.equalsIgnoreCase(falseValue)) {
				return "FALSE";
			}
		}
		return null;
	}
}
