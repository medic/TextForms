package net.frontlinesms.plugins.resourcemapper.data.domain.mapping;

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
	
	public Field(String name, String keyword) {
		this.name = name;
		this.keyword = keyword;
	}
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "field_id", unique=true, nullable=false, updatable=false)
	protected long id;
	
	/**
	 * The full name of this field. 
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
	 * The 'textable' keyword for this field
	 */
	@Column(name="keyword", unique=true, nullable=false)
	protected String keyword;
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	/**
	 * A short (<160 character) description of this field
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
	
	public abstract void setChoices(List<String> choices);
	
	public abstract String getType();
	
	public abstract String getTypeLabel();
	
}