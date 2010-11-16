package net.frontlinesms.plugins.textforms.data.domain.questions;

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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@DiscriminatorValue(value="coded")
public abstract class CodedQuestion extends PlainTextQuestion {

	public CodedQuestion() {
		super();
	}
	
	public CodedQuestion(String name, String keyword) {
		super(name, keyword);
	}
	
	public CodedQuestion(String name, String keyword, List<String> choices) {
		super(name, keyword);
		this.choices = choices;
	}
	
	@CollectionOfElements(targetElement=String.class, fetch = FetchType.EAGER)
	@JoinTable(name="question_choices", joinColumns = @JoinColumn(name="choice"))
	@Column(name="choices")
	@Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@Fetch (FetchMode.SELECT)
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
	
	@Override
	public String getChoicesLabel() {
		StringBuilder choicesLabel = new StringBuilder();
		for(String choice : choices) {
			if (choicesLabel.length() > 0) {
				choicesLabel.append(", ");
			}
			choicesLabel.append(choice);
		}
		return choicesLabel.toString();
	}
	
	public abstract String getType();
	
	public abstract String getTypeLabel();
}
