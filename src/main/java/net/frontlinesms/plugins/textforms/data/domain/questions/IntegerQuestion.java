package net.frontlinesms.plugins.textforms.data.domain.questions;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * A question that stores an integer value
 * @author Dale Zak
 *
 */
@Entity
@DiscriminatorValue(value=QuestionType.INTEGER)
public class IntegerQuestion extends Question {

	public IntegerQuestion() {
		super(null, null);
	}

	public IntegerQuestion(String fullName, String keyword) {
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
		return QuestionType.INTEGER;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(TextFormsConstants.TYPE_INTEGER);
	}

	@Override
	public String getChoicesLabel() {
		return null;
	}
	
	@Override
	public String toString(boolean includeChoices) {
		return String.format("%s (%s)- %s", getName(),getTypeLabel(),getInfoSnippet());
	}

}
