package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

@Entity
@DiscriminatorValue(value="boolean")
public class BooleanField extends CodedField {
	
	public BooleanField() {
		this(null, null);
	}
	
	public BooleanField(String fullName, String keyword) {
		super(fullName, keyword, null);
		List<String> yesAndNo = new ArrayList<String>();
		//the only possibilities are 0 and 1, since this is a boolean field
		yesAndNo.add("0");
		yesAndNo.add("1");
		super.setChoices(yesAndNo);
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
		return "boolean";
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_BOOLEAN);
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
