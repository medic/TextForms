package net.frontlinesms.plugins.resourcemapper.data.domain;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.Contact;

@Entity
public class HospitalContact extends Contact {
	
	private boolean isBlacklisted;
	
	private String hospitalId;
	
	private Date lastResponse;
		
	protected HospitalContact() {
		super(null, null, null, null, null, true);
	}
	
	public HospitalContact(String name, String phoneNumber, String emailAddress, boolean active, String hospitalId) {
		super(name, phoneNumber, null, emailAddress, null, active);
		this.setBlackListed(false);
		this.setHospitalId(hospitalId);
	}

	public void setBlackListed(boolean isBlackListed) {
		this.isBlacklisted = isBlackListed;
	}

	public boolean isBlackListed() {
		return isBlacklisted;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getHospitalId() {
		return hospitalId;
	}
	
	public Date getLastResponse() {
		return lastResponse;
	}
	
	public void setLastResponse(Date lastResponse) {
		this.lastResponse = lastResponse;
	}

}
