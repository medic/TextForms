package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value="multichoice")
public class MultiChoiceField extends CodedField {
	
	public MultiChoiceField() {
		super();
	}
	
	public MultiChoiceField(String name, String keyword) {
		super(name, keyword);
		this.setChoices(new ArrayList<String>());
	}
		
	public MultiChoiceField(String name, String keyword, List<String> choices) {
		super(name, keyword, choices);
	}
		
	@Override
	public String getType() {
		return "multichoice";
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_MULTICHOICE);
	}
	
	@Override
	public String[] getAdditionalInstructions() {
		return null;
	}

	@Override
	public String getPathToElement() {
		return null;
	}
}
