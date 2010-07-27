package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.BooleanField;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

@Entity
public class BooleanResponse extends FieldResponse<BooleanField> {

	public BooleanResponse() {
		super();
	}

	public BooleanResponse(FrontlineMessage message, HospitalContact submitter,
			Date dateSubmitted, String hospitalId, BooleanField mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
	}
	
	@Override
	public boolean isResponseFor(Field field) {
		return field.getClass() == BooleanField.class;
	}
	
	@Override
	public String getResponseValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return toBooleanString(words[0]);
		}
		else if (words != null && words.length == 2) {
			return toBooleanString(words[1]);
		}
		return null;
	}
	
	private String toBooleanString(String word) {
		for (String trueValue : ResourceMapperProperties.getBooleanTrueValues()) {
			if (word.equalsIgnoreCase(trueValue)) {
				return "true";
			}
		}
		for (String falseValue : ResourceMapperProperties.getBooleanFalseValues()) {
			if (word.equalsIgnoreCase(falseValue)) {
				return "false";
			}
		}
		return null;
	}
}
