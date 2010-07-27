package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;

@Entity
public class PlainTextResponse extends FieldResponse<PlainTextField> {

	public PlainTextResponse() {
		super();
	}

	public PlainTextResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, PlainTextField mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
	}
	
	@Override
	public boolean isResponseFor(Field field) {
		return field.getClass() == PlainTextField.class;
	}
	
	@Override
	public String getResponseValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return words[0].trim();
		}
		else if (words != null && words.length == 2) {
			return words[1].trim();
		}
		return null;
	}
}
