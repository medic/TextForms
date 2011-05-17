package net.frontlinesms.plugins.textforms.data.domain.questions;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value=QuestionType.IMAGE)
public class ImageQuestion extends Question {

	public ImageQuestion() {
		super(null, null);
	}

	public ImageQuestion(String fullName, String keyword) {
		super(fullName, keyword);
	}
	
	@Override
	public void setChoices(List<String> choices) {
		//do nothing for ImageQuestion
	}
	
	@Override
	public List<String> getChoices() {
		//return nothing for ImageQuestion
		return null;
	}
	
	@Override
	public String getType() {
		return QuestionType.IMAGE;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(TextFormsConstants.TYPE_IMAGE);
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