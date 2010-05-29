package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.Message;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.ChecklistMapping;

@Entity
public class ChecklistResponse extends FieldResponse<ChecklistMapping> {

	public ChecklistResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChecklistResponse(Message message, HospitalContact submitter,
			Date dateSubmitted, String hospitalId, ChecklistMapping mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
		// TODO Auto-generated constructor stub
	}

}
