package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * A field that stores a date value
 * @author Dale Zak
 *
 */
@Entity
@DiscriminatorValue(value=FieldType.DATE)
public class DateField extends Field {

	public DateField() {
		super(null, null);
	}

	public DateField(String fullName, String keyword) {
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
		return FieldType.DATE;
	}
	
	@Override
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_DATE);
	}

}
