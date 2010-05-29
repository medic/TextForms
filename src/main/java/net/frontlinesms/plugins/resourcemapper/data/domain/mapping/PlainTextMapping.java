package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.PolymorphismType;

@Entity
@org.hibernate.annotations.Entity(polymorphism=PolymorphismType.EXPLICIT)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PlainTextMapping {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long mid;

	public PlainTextMapping(){}

	public long getMid() {
		return mid;
	}
	
	private String shortCode;
	
	private String pathToElement;
	
	private String additionalInstructions;

	public PlainTextMapping(String shortCode, String pathToElement) {
		this.shortCode = shortCode;
		this.pathToElement = pathToElement;
		this.additionalInstructions="";
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getPathToElement() {
		return pathToElement;
	}

	public void setPathToElement(String pathToElement) {
		this.pathToElement = pathToElement;
	}

	public String[] getAdditionalInstructions() {
		return additionalInstructions.split("\\|");
	}

	public void addInstruction(String instruction) {
		if(!additionalInstructions.equals("")){
			additionalInstructions +="|";
		}
		this.additionalInstructions += instruction;
	}
	
	public void removeInstruction(String instruction){
		additionalInstructions.replace(instruction, "");
		if(additionalInstructions.charAt(0) == '|'){
			additionalInstructions = additionalInstructions.substring(1);
		}
		additionalInstructions.replace("||", "|");
	}
	
	public void setAdditionalInstructions(String[] instructions){
		this.additionalInstructions = "";
		for(String r: instructions){
			if(!this.additionalInstructions.equals("")){
				this.additionalInstructions +="|";
			}
			this.additionalInstructions += r;
		}
	}
	
	public static void main(String[] args) {
		String[] strings = "afwefwef".split("|");
		for(String s:strings){
			System.out.println(s);
		}
	}
}
