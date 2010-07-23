package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.CodedField;

@Entity
public abstract class CodedResponse<M extends CodedField> extends FieldResponse<M> {

	public CodedResponse() {
		super();
	}

	public CodedResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, M mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
	}

}
