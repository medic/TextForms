package net.frontlinesms.plugins.surveys.data.domain.questions;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * A question that stores a date value
 * @author Dale Zak
 *
 */
@Entity
@DiscriminatorValue(value=QuestionType.DATE)
public class DateQuestion extends Question {

	public DateQuestion() {
		super(null, null);
	}

	public DateQuestion(String fullName, String keyword) {
		super(fullName, keyword);
	}
	
	@Override
	public void setChoices(List<String> choices) {
		//do nothing for IntegerQuestion
	}
	
	@Override
	public List<String> getChoices() {
		//return nothing for IntegerQuestion
		return null;
	}
	
	@Override
	public String getType() {
		return QuestionType.DATE;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(SurveysConstants.TYPE_DATE);
	}

	@Override
	public String getChoicesLabel() {
		return null;
	}
	
	@Override
	public String getFormatLabel() {
		return "dd/MM/yyyy";
	}
}
