package net.frontlinesms.plugins.textforms.data.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ibm.icu.util.Calendar;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * TextFormResponse
 * @author dalezak
 *
 */
@Entity
public class TextFormResponse {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(TextFormResponse.class);
	public static final String FIELD_CONTACT = "contact";
	
	public TextFormResponse(){}
	public TextFormResponse(TextForm textform) {
		this.textform = textform;
	}
	public TextFormResponse(TextForm textform, List<Answer<?>> answers) {
		this.textform = textform;
		this.answers = answers;
	}
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique=true, nullable=false, updatable=false)
	protected long id;
	
	public long getId() {
		return id;
	}
	
	@ManyToOne
	protected TextForm textform;
	
	public TextForm getTextForm() {
		return textform;
	}
	
	public void setTextForm(TextForm textform) {
		this.textform = textform;
	}
	
	public String getTextFormName() {
		return textform != null ? textform.getName() : null;
	}
	
	public List<Question> getTextFormQuestions() {
		return textform != null ? textform.getQuestions() : null;
	}
	
	public Question getNextQuestion() {
		if (textform.getQuestions().size() > 0 && answers == null) {
			return textform.getQuestions().get(0);
		}
		else if (textform.getQuestions().size() > answers.size()) {
			return textform.getQuestions().get(answers.size());
		}
		return null;
	}
	
	@ManyToOne
	protected Contact contact;
	
	public Contact getContact() {
		return contact;
	}
	
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	public String getContactPhoneNumber() {
		return contact != null ? contact.getPhoneNumber() : null;
	}
	
	public String getContactName() {
		return contact != null ? contact.getName() : null;
	}
	
	@CollectionOfElements(targetElement=Answer.class, fetch=FetchType.EAGER)
	@JoinTable(name="textform_answers", joinColumns=@JoinColumn(name="id"))
	@Column(name="answers")
	@Fetch (FetchMode.SELECT)
	protected List<Answer<?>> answers;
	
	public List<Answer<?>> getAnswers() {
		return answers;
	}
	
	public void setAnswers(List<Answer<?>> answers) {
		this.answers = answers;
	}
	
	public void addAnswer(Answer<?> answer) {
		if (answers == null) {
			answers = new ArrayList<Answer<?>>();
		}
		if (textform.getQuestions().contains(answer.getQuestion())) {
			answers.add(answer);
		}
		else {
			LOG.error("TextForm does not contain question: %s", answer.getQuestionName());
		}
	}
	
	protected Date started;
	
	public Date getStarted() {
		return started;
	}
	
	public String getStartedString() {
		return started != null ? InternationalisationUtils.getDatetimeFormat().format(started) : null;
	}
	
	public void setStarted(Date started) {
		this.started = started;
	}
	
	/**
	 * Has all questions been answered?
	 * @return true if all questions have been answered
	 */
	public boolean isCompleted() {
		if (answers != null) {
			for (Question question : textform.getQuestions()) {
				boolean hasAnswer = false;
				for (Answer<?> answer : answers) {
					if (answer.getQuestionID() == question.getID()) {
						hasAnswer = true;
						break;
					}
				}
				if (hasAnswer == false) {
					//answer not found for question, textform not complete
					return false;	
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean hasTimedOut(double minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(started);
		Calendar now = Calendar.getInstance();
		return (((double)now.getTimeInMillis() - (double)calendar.getTimeInMillis()) / (60 * 1000)) > minutes;
	}
	
}