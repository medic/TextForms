package net.frontlinesms.plugins.textforms.data.domain.questions;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.textforms.TextFormsConstants;
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
		return InternationalisationUtils.getI18NString(TextFormsConstants.TYPE_MULTICHOICE);
	}
	
	public String toString(boolean includeChoices) {
		if(!includeChoices){
			return String.format("%s (%s)- %s", getName(),getTypeLabel(),getInfoSnippet());
		}else{
			StringBuilder message = new StringBuilder(getInfoSnippet() + "\n");
			for(int i = 0; i < getChoices().size(); i++){
				message.append((i+1) + " "+ getChoices().get(i)+ "\n");
			}
			message.append(InternationalisationUtils.getI18NString("plugin.textforms.handler.multichoice.instructions"));
			return message.toString();
		}
	}
}
