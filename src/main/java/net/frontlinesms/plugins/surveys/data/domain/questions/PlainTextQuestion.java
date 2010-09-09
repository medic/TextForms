package net.frontlinesms.plugins.surveys.data.domain.questions;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * A question that stores plain text only
 * @author dieterichlawson
 *
 */
@Entity
@DiscriminatorValue(value=QuestionType.PLAINTEXT)
public class PlainTextQuestion extends Question {

	public PlainTextQuestion() {
		super(null, null);
	}

	public PlainTextQuestion(String fullName, String keyword) {
		super(fullName, keyword);
	}
	
	@Override
	public void setChoices(List<String> choices) {
		//do nothing for PlainTextQuestion
	}
	
	@Override
	public List<String> getChoices() {
		//return nothing for PlainTextQuestion
		return null;
	}
	
	@Override
	public String getType() {
		return QuestionType.PLAINTEXT;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(SurveysConstants.TYPE_PLAINTEXT);
	}

	@Override
	public String getChoicesLabel() {
		return null;
	}

}
