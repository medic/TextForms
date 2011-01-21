package net.frontlinesms.plugins.textforms.data.domain.questions;

import java.util.List;

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
 * Inteface for Question classes
 * @author Dale Zak
 *
 */
@Entity
@Table(name="question")
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(value="question")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Question {
	
	public Question() {}
	
	public Question(String name, String keyword) {
		this.name = name;
		this.keyword = keyword;
	}
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "question_id", unique=true, nullable=false, updatable=false)
	protected long id;
	
	/**
	 * The full name of this question. 
	 */
	@Column(name="name", nullable=false)
	protected String name;
	
	public long getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * The 'textable' keyword for this question
	 */
	@Column(name="keyword", unique=true, nullable=false)
	protected String keyword;
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword.toLowerCase();
	}
	
	/**
	 * A short (<160 character) description of this question
	 */
	@Column(name="info", nullable=true)
	protected String infoSnippet;
	
	public String getInfoSnippet() {
		return infoSnippet;
	}
	
	public void setInfoSnippet(String infoSnippet) {
		this.infoSnippet = infoSnippet;
	}
	
	/**
	 * The "name" attribute in the Google XML schema
	 */
	@Column(name="schema", nullable=true)
	private String schemaName;

	public String getSchemaName() {
		return schemaName;
	}
	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	public abstract List<String> getChoices();
	
	public abstract String getChoicesLabel();
	
	public abstract void setChoices(List<String> choices);
	
	public abstract String getType();
	
	public abstract String getTypeLabel();
	
	public String getFormatLabel() {
		return null;
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean includeChoices) {
		StringBuilder questionText = new StringBuilder(getName());
		questionText.append(" (");
		questionText.append(getTypeLabel());
		if (getFormatLabel() != null) {
			questionText.append(" ");
			questionText.append(getFormatLabel());
		}
		questionText.append(")");
		if (getInfoSnippet() != null && getInfoSnippet().length() > 0) {
			questionText.append(" ");
			questionText.append(getInfoSnippet());	
		}
		if (includeChoices && getChoices() != null && getChoices().size() > 0) {
			int index = 1;
			for (String choice : getChoices()) {
				questionText.append("\n");
				questionText.append(index);
				questionText.append(" ");
				questionText.append(choice);
				index++;		
			}
		}
		return questionText.toString();
	}
}