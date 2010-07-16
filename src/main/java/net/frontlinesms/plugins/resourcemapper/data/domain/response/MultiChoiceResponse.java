package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.MultiChoiceField;

@Entity
public class MultiChoiceResponse extends FieldResponse<MultiChoiceField> {

	public MultiChoiceResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MultiChoiceResponse(FrontlineMessage message, HospitalContact submitter,
			Date dateSubmitted, String hospitalId, MultiChoiceField mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
		// TODO Auto-generated constructor stub
	}

}
