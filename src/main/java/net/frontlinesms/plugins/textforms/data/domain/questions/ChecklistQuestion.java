package net.frontlinesms.plugins.textforms.data.domain.questions;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.textforms.TextFormsConstants;
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
		return InternationalisationUtils.getI18NString(TextFormsConstants.TYPE_CHECKLIST);
	}
	
	@Override
	public String toString(boolean includeChoices) {
		if(!includeChoices){
			return String.format("%s (%s)- %s", getName(),getTypeLabel(),getInfoSnippet());
		}else{
			StringBuilder message = new StringBuilder(getInfoSnippet() + "\n");
			for(int i = 0; i < getChoices().size(); i++){
				message.append((i+1) + " "+ getChoices().get(i)+ "\n");
			}
			message.append(InternationalisationUtils.getI18NString("plugin.textforms.handler.checklist.instructions"));
			return message.toString();
		}
	}
	
}
