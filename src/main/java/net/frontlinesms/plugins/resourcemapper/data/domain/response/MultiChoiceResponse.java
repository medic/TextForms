package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.MultiChoiceField;

@Entity
public class MultiChoiceResponse extends CodedResponse<MultiChoiceField> {

	public MultiChoiceResponse() {
		super();
	}

	public MultiChoiceResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, MultiChoiceField mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
	}
	
	@Override
	public boolean isResponseFor(Field field) {
		return field.getClass() == MultiChoiceField.class;
	}
}
