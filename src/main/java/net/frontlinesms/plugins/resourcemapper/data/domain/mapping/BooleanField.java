package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value=FieldType.BOOLEAN)
public class BooleanField extends CodedField {
	
	public BooleanField() {
		this(null, null);
	}
	
	public BooleanField(String fullName, String keyword) {
		super(fullName, keyword, null);
	}

	@Override
	public void addChoice(String choice) {
		//do nothing for BooleanField
	}
	
	@Override
	public boolean removeChoice(String choice){
		//do nothing for  choice
		return false;
	}
	
	@Override
	public String getType() {
		return FieldType.BOOLEAN;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_BOOLEAN);
	}
	
}
