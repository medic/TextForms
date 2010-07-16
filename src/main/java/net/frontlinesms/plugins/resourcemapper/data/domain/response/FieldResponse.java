package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class FieldResponse<M extends PlainTextField> {

	public FieldResponse() {}

	public FieldResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, M mapping) {
		this.message = message;
		this.submitter = submitter;
		this.dateSubmitted = dateSubmitted.getTime();
		this.hospitalId = hospitalId;
		this.mapping = mapping;
	}
	
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long fid;

	@OneToOne(cascade={},targetEntity=FrontlineMessage.class)
	protected FrontlineMessage message;

	@OneToOne(cascade={},targetEntity=HospitalContact.class)
	protected HospitalContact submitter;

	protected long dateSubmitted;

	protected String hospitalId;
	
	@OneToOne(targetEntity=PlainTextField.class)
	M mapping;

	public FrontlineMessage getMessage() {
		return message;
	}

	public void setMessage(FrontlineMessage message) {
		this.message = message;
	}

	public String getMessageText() {
		if (this.message != null) {
			return this.message.getTextContent();
		}
		return null;
	}
	
	public HospitalContact getSubmitter() {
		return submitter;
	}
	
	public String getSubmitterName() {
		if (this.submitter != null) {
			return this.submitter.getName();
		}
		return null;
	}
	
	public String getSubmitterPhone() {
		if (this.submitter != null) {
			return this.submitter.getPhoneNumber();
		}
		return null;
	}

	public void setSubmitter(HospitalContact submitter) {
		this.submitter = submitter;
	}

	public Date getDateSubmitted() {
		return new Date(dateSubmitted);
	}

	public String getDateSubmittedText() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(new Date(dateSubmitted));
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
	
	public String getMappingType() {
		if (this.mapping != null) {
			return this.mapping.getType();
		}
		return null;
	}
	
	public String getMappingTypeLabel() {
		if (this.mapping != null) {
			return this.mapping.getTypeLabel();
		}
		return null;
	}
	
	public String getMappingName() {
		if (this.mapping != null) {
			return this.mapping.getName();
		}
		return null;
	}
	
	public String getMappingAbbreviation() {
		if (this.mapping != null) {
			return this.mapping.getAbbreviation();
		}
		return null;
	}
	
	public String getMappingDisplayName() {
		if (this.mapping != null) {
			return String.format("%s : %s (%s)", this.mapping.getName(), this.mapping.getAbbreviation(), this.mapping.getTypeLabel());
		}
		return null;
	}

	public long getFid() {
		return fid;
	}
}
