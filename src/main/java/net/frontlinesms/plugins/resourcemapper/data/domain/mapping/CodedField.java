package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;

@Entity
@DiscriminatorValue(value="coded")
public abstract class CodedField extends PlainTextField {

	public CodedField() {
		super();
	}
	
	public CodedField(String name, String keyword) {
		super(name, keyword);
	}
	
	public CodedField(String name, String keyword, List<String> choices) {
		super(name, keyword);
		this.choices = choices;
	}
	
	@CollectionOfElements(targetElement=String.class, fetch=FetchType.EAGER)
	@JoinTable(name="field_choices", joinColumns = @JoinColumn(name="choice"))
	@Column(name="choices")
	@Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<String> choices;
	
	public List<String> getChoices() {
		return this.choices;
	}

	public void setChoices(List<String> choices) {
		this.choices = choices;
	}
	
	public void addChoice(String choice) {
		if (this.choices == null) {
			this.choices = new ArrayList<String>();
		}
		this.choices.add(choice);
	}
	
	public boolean removeChoice(String choice){
		if (this.choices != null) {
			return this.choices.remove(choice);
		}
		return false;
	}
	
	public abstract String getType();
	
	public abstract String getTypeLabel();
}
