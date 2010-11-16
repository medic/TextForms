package net.frontlinesms.plugins.textforms.data.domain.questions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value=QuestionType.BOOLEAN)
public class BooleanQuestion extends CodedQuestion {
	
	public BooleanQuestion() {
		this(null, null);
	}
	
	public BooleanQuestion(String fullName, String keyword) {
		super(fullName, keyword, null);
	}

	@Override
	public void addChoice(String choice) {
		//do nothing for BooleanQuestion
	}
	
	@Override
	public boolean removeChoice(String choice){
		//do nothing for  choice
		return false;
	}
	
	@Override
	public String getType() {
		return QuestionType.BOOLEAN;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(TextFormsConstants.TYPE_BOOLEAN);
	}
	
}
