package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value="checklist")
public class ChecklistField extends CodedField {
	
	public ChecklistField() {
		super();
	}
	
	public ChecklistField(String name, String abbreviation, Set<String> choices) {
		super(name, abbreviation, choices);
	}

	public ChecklistField(String shortCode, String pathToElement) {
		super(shortCode, pathToElement);
		this.setChoices(new TreeSet<String>());
	}

	@Override
	public String getType() {
		return "checklist";
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_CHECKLIST);
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
