package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * A field that stores plain text only
 * @author dieterichlawson
 *
 */
@Entity
@DiscriminatorValue(value = "plaintext")
public class PlainTextField extends Field {

	public PlainTextField() {
		super(null, null);
	}

	public PlainTextField(String fullName, String abbreviation) {
		super(fullName, abbreviation);
	}
	
	/**
	 * The "name" attribute in the Google XML schema
	 */
	private String schemaName;

	public String getSchemaName() {
		return schemaName;
	}
	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	public String getType() {
		return "plaintext";
	}
	
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_PLAIN_TEXT);
	}
}
