package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.IntegerField;

@Entity
public class IntegerResponse extends FieldResponse<IntegerField> {

	public IntegerResponse() {
		super();
	}

	public IntegerResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, IntegerField mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
	}

	@Override
	public boolean isResponseFor(Field field) {
		return field.getClass() == IntegerField.class;
	}
	
	@Override
	public String getResponseValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return toIntegerString(words[0]);
		}
		else if (words != null && words.length == 2) {
			return toIntegerString(words[1]);
		}
		return null;
	}
	
	private String toIntegerString(String word) {
		return this.isValidInteger(word) ? word : null;
	}
}
