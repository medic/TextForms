package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value=FieldType.CHECKLIST)
public class ChecklistField extends CodedField {
	
	public ChecklistField() {
		super(null, null, null);
	}
	
	public ChecklistField(String name, String keyword, List<String> choices) {
		super(name, keyword, choices);
	}

	@Override
	public String getType() {
		return FieldType.CHECKLIST;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_CHECKLIST);
	}
	
}
