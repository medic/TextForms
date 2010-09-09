package net.frontlinesms.plugins.surveys.data.domain.questions;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value=QuestionType.CHECKLIST)
public class ChecklistQuestion extends CodedQuestion {
	
	public ChecklistQuestion() {
		super(null, null, null);
	}
	
	public ChecklistQuestion(String name, String keyword, List<String> choices) {
		super(name, keyword, choices);
	}

	@Override
	public String getType() {
		return QuestionType.CHECKLIST;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(SurveysConstants.TYPE_CHECKLIST);
	}
	
}
