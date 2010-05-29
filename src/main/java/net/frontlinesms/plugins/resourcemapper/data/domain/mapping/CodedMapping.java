package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import javax.persistence.Entity;

import org.hibernate.annotations.PolymorphismType;

@Entity
@org.hibernate.annotations.Entity(polymorphism=PolymorphismType.EXPLICIT)
public class CodedMapping extends PlainTextMapping {

	public CodedMapping(String shortCode, String pathToElement) {
		super(shortCode, pathToElement);
	}
	public CodedMapping(){super();}
	
	public CodedMapping(String shortCode, String pathToElement,String[] possibleResponses) {
		super(shortCode, pathToElement);
		setPossibleResponses(possibleResponses);
	}
	
	private String possibleResponses;
	
	
	public String[] getPossibleResponses() {
		return possibleResponses.split("\\|");
	}

	public void addPossibleResponse(String response) {
		if(!possibleResponses.equals("") && possibleResponses != null ){
			possibleResponses +="|";
		}else{
			possibleResponses="";
		}
		this.possibleResponses += response;
	}
	
	public void removePossibleResponse(String response){
		possibleResponses.replace(response, "");
		if(possibleResponses.charAt(0) == '|'){
			possibleResponses = possibleResponses.substring(1);
		}
		possibleResponses.replace("||", "|");
	}
	
	public void setPossibleResponses(String[] responses){
		this.possibleResponses = "";
		for(String r: responses){
			if(!this.possibleResponses.equals("")){
				this.possibleResponses +="|";
			}
			this.possibleResponses += r;
		}
	}
}
