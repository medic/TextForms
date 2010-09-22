package net.frontlinesms.plugins.surveys.data.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import net.frontlinesms.data.domain.Details;

/**
 * OrganizationDetails
 * @author dalezak
 *
 */
@Entity
@Table(name="organization_details")
public class OrganizationDetails extends Details {

	public OrganizationDetails(){}
	public OrganizationDetails(String organizationId){
		this.organizationId = organizationId;
	}
	public OrganizationDetails(Date lastAnswer){
		this.lastAnswer = lastAnswer;
	}
	
	private boolean isBlacklisted;
	
	private String organizationId;
	
	private Date lastAnswer;
	
	@Override
	public String toString() {
		return null;
	}
	
	public void setBlackListed(boolean isBlackListed) {
		this.isBlacklisted = isBlackListed;
	}

	public boolean isBlackListed() {
		return isBlacklisted;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationId() {
		return organizationId;
	}
	
	public Date getLastAnswer() {
		return lastAnswer;
	}
	
	public void setLastAnswer(Date lastAnswer) {
		this.lastAnswer = lastAnswer;
	}
	
	public String getLastAnswerText() {
		if (this.lastAnswer != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return dateFormat.format(this.lastAnswer);	
		}
		return "";
	}
	
}