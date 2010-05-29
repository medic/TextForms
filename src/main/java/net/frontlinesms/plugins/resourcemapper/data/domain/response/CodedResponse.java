package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.Message;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.CodedMapping;

@Entity
public class CodedResponse extends FieldResponse<CodedMapping> {

	public CodedResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CodedResponse(Message message, HospitalContact submitter,
			Date dateSubmitted, String hospitalId, CodedMapping mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
		// TODO Auto-generated constructor stub
	}

}
