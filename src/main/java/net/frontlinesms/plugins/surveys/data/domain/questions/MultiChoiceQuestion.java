package net.frontlinesms.plugins.surveys.data.domain.questions;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value=QuestionType.MULTICHOICE)
public class MultiChoiceQuestion extends CodedQuestion {
	
	public MultiChoiceQuestion() {
		super();
	}
	
	public MultiChoiceQuestion(String name, String keyword) {
		super(name, keyword);
		this.setChoices(new ArrayList<String>());
	}
		
	public MultiChoiceQuestion(String name, String keyword, List<String> choices) {
		super(name, keyword, choices);
	}
		
	@Override
	public String getType() {
		return QuestionType.MULTICHOICE;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(SurveysConstants.TYPE_MULTICHOICE);
	}
	
}
