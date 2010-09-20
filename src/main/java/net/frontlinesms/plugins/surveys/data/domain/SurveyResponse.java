package net.frontlinesms.plugins.surveys.data.domain;

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
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;

/**
 * SurveyResponse
 * @author dalezak
 *
 */
@Entity
public class SurveyResponse {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(SurveyResponse.class);
	public static final String FIELD_CONTACT = "contact";
	
	public SurveyResponse(){}
	public SurveyResponse(Survey survey) {
		this.survey = survey;
	}
	public SurveyResponse(Survey survey, List<Answer<?>> answers) {
		this.survey = survey;
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
	protected Survey survey;
	
	public Survey getSurvey() {
		return survey;
	}
	
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
	
	public String getSurveyName() {
		return survey != null ? survey.getName() : null;
	}
	
	public List<Question> getSurveyQuestions() {
		return survey != null ? survey.getQuestions() : null;
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
	
	@CollectionOfElements(targetElement=Answer.class, fetch=FetchType.EAGER)
	@JoinTable(name="survey_answers", joinColumns=@JoinColumn(name="id"))
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
		if (survey.getQuestions().contains(answer.getQuestion())) {
			answers.add(answer);
		}
		else {
			LOG.error("Survey does not contain question: %s", answer.getQuestionName());
		}
	}
	
	protected Date started;
	
	public Date getStarted() {
		return started;
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
			for (Question question : survey.getQuestions()) {
				boolean hasAnswer = false;
				for (Answer<?> answer : answers) {
					if (answer.getQuestionID() == question.getID()) {
						hasAnswer = true;
						break;
					}
				}
				if (hasAnswer == false) {
					//answer not found for question, survey not complete
					return false;	
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean hasTimedOut(int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(started);
		Calendar now = Calendar.getInstance();
		return ((now.getTimeInMillis() - calendar.getTimeInMillis()) * (60 * 1000)) > minutes;
	}
	
}