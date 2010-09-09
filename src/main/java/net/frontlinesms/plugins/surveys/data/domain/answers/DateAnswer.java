package net.frontlinesms.plugins.surveys.data.domain.answers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.questions.DateQuestion;

@Entity
public class DateAnswer extends Answer<DateQuestion> {

	public DateAnswer() {
		super();
	}

	public DateAnswer(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, DateQuestion question) {
		super(message, submitter, dateSubmitted, hospitalId, question);
	}

	@Override
	public boolean isAnswerFor(Question question) {
		return question.getClass() == DateQuestion.class;
	}

	@Override
	public String getAnswerValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return toDateString(words[0]);
		}
		else if (words != null && words.length == 2) {
			return toDateString(words[1]);
		}
		return null;
	}
	
	private String toDateString(String word) {
		for (String format : new String [] {"dd/MM/yyyy", "dd-MM-yyyy", "dd/MM/yy", "dd-MM-yy"}) {
			try {
				if (word != null && word.length() > 0) {
					DateFormat inFormat = new SimpleDateFormat(format);
					Date date = inFormat.parse(word);	
					DateFormat outFormat = new SimpleDateFormat("dd/MM/yyyy");
					return outFormat.format(date);
				}
			} 
			catch (ParseException e) {
				//do nothing
			}
		}
		return null;
	}
}
