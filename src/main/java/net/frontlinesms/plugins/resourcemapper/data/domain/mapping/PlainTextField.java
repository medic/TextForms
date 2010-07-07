package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.PolymorphismType;

/**
 * A field that stores plain text only
 * @author dieterichlawson
 *
 */
@Entity
@org.hibernate.annotations.Entity(polymorphism=PolymorphismType.EXPLICIT)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="field_type")
public class PlainTextField {

	public PlainTextField(String fullName, String abbreviation) {
		super();
		this.fullName = fullName;
		this.abbreviation = abbreviation;
	}

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long mid;

	public PlainTextField(){}

	public long getMid() {
		return mid;
	}
	
	/**
	 * The full name of this field. 
	 */
	private String fullName;
	
	/**
	 * The 'textable' abbreviation for this field
	 */
	private String abbreviation;

	/**
	 * A short (<160 character) description of this field
	 */
	private String infoSnippet;
	
	/**
	 * The "name" attribute in the Google XML schema
	 */
	private String schemaName;

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
	
	public String getSchemaName() {
		return schemaName;
	}
	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	public String getPathToElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public String [] getAdditionalInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addInstruction(String string) {
		// TODO Auto-generated method stub
	}
}
