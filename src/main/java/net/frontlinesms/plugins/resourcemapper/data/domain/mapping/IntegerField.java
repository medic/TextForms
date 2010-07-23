package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * A field that stores an integer value
 * @author Dale Zak
 *
 */
@Entity
@DiscriminatorValue(value="integer")
public class IntegerField extends Field {

	public IntegerField() {
		super(null, null);
	}

	public IntegerField(String fullName, String keyword) {
		super(fullName, keyword);
	}
	
	@Override
	public void setChoices(List<String> choices) {
		//do nothing for IntegerField
	}
	
	@Override
	public List<String> getChoices() {
		//return nothing for IntegerField
		return null;
	}
	
	@Override
	public String getType() {
		return "integer";
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_INTEGER);
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
