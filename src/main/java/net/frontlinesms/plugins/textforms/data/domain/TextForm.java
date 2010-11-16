package net.frontlinesms.plugins.textforms.data.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import net.frontlinesms.plugins.textforms.data.domain.questions.Question;

/**
 * TextForm
 * @author dalezak
 *
 */
@Entity
public class TextForm {
	
	public static final String FIELD_KEYWORD = "keyword";
	public TextForm() {}
	
	public TextForm(String name, String keyword, List<Question> questions) {
		this.name = name;
		this.keyword = keyword;
		this.questions = new ArrayList<Question>();
		this.questions.addAll(questions);
	}
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="textform_id", unique=true, nullable=false, updatable=false)
	protected long id;
	
	public long getId() {
		return this.id;
	}
	
	@Column(name="name", nullable=false)
	protected String name;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name=FIELD_KEYWORD, unique=true, nullable=false)
	protected String keyword;
	
	public String getKeyword() {
		return this.keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getQuestionNames() {
		StringBuilder questionNames = new StringBuilder();
		for(Question question : this.questions) {
			if (questionNames.length() > 0) {
				questionNames.append(", ");
			}
			questionNames.append(question.getName());
		}
		return questionNames.toString();
	}
	
	@CollectionOfElements(targetElement=Question.class, fetch=FetchType.EAGER)
	@JoinTable(name="textform_questions", joinColumns=@JoinColumn(name="textform_id"))
	@Column(name="questions")
	@Fetch (FetchMode.SELECT)
	private List<Question> questions;
	
	public List<Question> getQuestions() {
		return this.questions;
	}

	public boolean setQuestions(List<Question> questions) {
		this.questions = new ArrayList<Question>();
		return this.questions.addAll(questions);
	}
	
	public boolean addQuestion(Question question) {
		if (this.questions == null) {
			this.questions = new ArrayList<Question>();
		}
		return this.questions.add(question);
	}
	
	public boolean removeQuestion(Question question){
		if (this.questions != null) {
			return this.questions.remove(question);
		}
		return false;
	}
}