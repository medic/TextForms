package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

import org.hibernate.annotations.CollectionOfElements;

@Entity
@DiscriminatorValue(value = "coded")
public class CodedField extends PlainTextField {

	public CodedField(String fullName, String abbreviation) {
		super(fullName, abbreviation);
	}
	public CodedField(){super();}
	
	public CodedField(String shortCode, String pathToElement,Set<String> possibleResponses) {
		super(shortCode, pathToElement);
		this.possibleResponses = possibleResponses;
	}
	
	@CollectionOfElements
	@JoinTable(name="coded_possible_responses", joinColumns = @JoinColumn(name="response"))
	@Column(name="possible_responses")
	private Set<String> possibleResponses;
	
	
	public Set<String> getPossibleResponses() {
		return possibleResponses;
	}

	public void addPossibleResponse(String response) {
		possibleResponses.add(response);
	}
	
	public boolean removePossibleResponse(String response){
		return possibleResponses.remove(response);
	}
	
	public void setPossibleResponses(Set<String> responses){
		this.possibleResponses = responses;
	}
	
	public String getType() {
		return "coded";
	}
	
	public String getTypeLabel() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.TYPE_CODED);
	}
}
