package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.IntegerField;

@Entity
public class IntegerResponse extends FieldResponse<IntegerField> {

	public IntegerResponse() {
		super();
	}

	public IntegerResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, IntegerField mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
	}

}
