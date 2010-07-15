package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.Set;
import java.util.TreeSet;

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
	
	public MultiChoiceField(String name, String abbreviation, Set<String> choices) {
		super(name, abbreviation, choices);
	}

	public MultiChoiceField(String shortCode, String pathToElement) {
		super(shortCode, pathToElement);
		this.setChoices(new TreeSet<String>());
	}

	public String getType() {
		return "multichoice";
	}
	
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_MULTICHOICE);
	}
}
