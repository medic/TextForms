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
@DiscriminatorValue(value=FieldType.INTEGER)
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
		return FieldType.INTEGER;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_INTEGER);
	}

}
