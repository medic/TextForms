package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import net.frontlinesms.data.domain.Message;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class FieldResponse<M extends PlainTextField> {

	public FieldResponse() {}

	public FieldResponse(Message message, HospitalContact submitter, Date dateSubmitted, String hospitalId, M mapping) {
		this.message = message;
		this.submitter = submitter;
		this.dateSubmitted = dateSubmitted.getTime();
		this.hospitalId = hospitalId;
		this.mapping = mapping;
	}
	
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long fid;

	@OneToOne(cascade={},targetEntity=Message.class)
	protected Message message;

	@OneToOne(cascade={},targetEntity=HospitalContact.class)
	protected HospitalContact submitter;

	protected long dateSubmitted;

	protected String hospitalId;
	
	@OneToOne(targetEntity=PlainTextField.class)
	M mapping;

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public HospitalContact getSubmitter() {
		return submitter;
	}

	public void setSubmitter(HospitalContact submitter) {
		this.submitter = submitter;
	}

	public Date getDateSubmitted() {
		return new Date(dateSubmitted);
	}

	public void setDateSubmitted(Date dateSubmitted) {
		this.dateSubmitted = dateSubmitted.getTime();
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public M getMapping() {
		return mapping;
	}

	public void setMapping(M mapping) {
		this.mapping = mapping;
	}

	public long getFid() {
		return fid;
	}
}
