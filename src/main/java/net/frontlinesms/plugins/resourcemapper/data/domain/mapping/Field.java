package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Inteface for Field classes
 * @author Dale Zak
 *
 */
@Entity
@Table(name="field")
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="field")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Field {
	
	public Field() {}
	
	public Field(String fullName, String abbreviation) {
		this.fullName = fullName;
		this.abbreviation = abbreviation;
	}
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	protected long mid;
	
	/**
	 * The full name of this field. 
	 */
	protected String fullName;
	
	/**
	 * The 'textable' abbreviation for this field
	 */
	protected String abbreviation;

	/**
	 * A short (<160 character) description of this field
	 */
	protected String infoSnippet;
	
	public long getMid() {
		return mid;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getAbbreviation() {
		return abbreviation;
	}
	
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	public String getInfoSnippet() {
		return infoSnippet;
	}
	
	public void setInfoSnippet(String infoSnippet) {
		this.infoSnippet = infoSnippet;
	}
	
	public abstract String getType();
	
	public abstract String getTypeLabel();
	
}