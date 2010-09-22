package net.frontlinesms.plugins.surveys.data.domain.answers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.OrganizationDetails;
import net.frontlinesms.plugins.surveys.data.domain.SurveyResponse;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Answer<Q extends Question> {

	@SuppressWarnings("unused")
	private static final SurveysLogger LOG = SurveysLogger.getLogger(Answer.class);
	
	public Answer() {}

	public Answer(FrontlineMessage message, Contact contact, Date dateSubmitted, String organizationId, Q question) {
		this.message = message;
		this.contact = contact;
		this.dateSubmitted = dateSubmitted.getTime();
		this.organizationId = organizationId;
		this.question = question;
	}
	
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false, updatable=false)
	private long fid;

	@OneToOne(cascade={})
	protected FrontlineMessage message;

	@OneToOne(cascade={})
	protected Contact contact;

	@OneToOne(cascade={})
	protected SurveyResponse surveyResponse;
	
	protected long dateSubmitted;

	protected String organizationId;
	
	@OneToOne(targetEntity=Question.class)
	Q question;

	public SurveyResponse getSurveyResponse() {
		return surveyResponse;
	}
	
	public void setSurveyResponse(SurveyResponse surveyResponse) {
		this.surveyResponse = surveyResponse;
	}
	
	public FrontlineMessage getMessage() {
		return message;
	}

	public void setMessage(FrontlineMessage message) {
		this.message = message;
	}

	public String getMessageText() {
		if (this.message != null) {
			return this.message.getTextContent();
		}
		return null;
	}
	
	public Contact getContact() {
		return contact;
	}
	
	public String getContactName() {
		if (this.contact != null) {
			return this.contact.getName();
		}
		return null;
	}
	
	public String getContactDisplayName() {
		if (this.contact != null) {
			return this.contact.getDisplayName();
		}
		return null;
	}
	
	public String getContactPhone() {
		if (this.contact != null) {
			return this.contact.getPhoneNumber();
		}
		return null;
	}

	public String getContactOrganizationId() {
		if (this.contact != null) {
			OrganizationDetails organizationsDetails = contact.getDetails(OrganizationDetails.class);
			return organizationsDetails != null ? organizationsDetails.getOrganizationId() : null;
		}
		return null;
	}
	
	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Date getDateSubmitted() {
		return new Date(dateSubmitted);
	}

	public String getDateSubmittedText() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(new Date(dateSubmitted));
	}
	
	public void setDateSubmitted(Date dateSubmitted) {
		this.dateSubmitted = dateSubmitted.getTime();
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public Q getQuestion() {
		return question;
	}

	public void setQuestion(Q question) {
		this.question = question;
	}
	
	public String getQuestionType() {
		if (this.question != null) {
			return this.question.getType();
		}
		return null;
	}
	
	public String getQuestionTypeLabel() {
		if (this.question != null) {
			return this.question.getTypeLabel();
		}
		return null;
	}
	
	public long getQuestionID() {
		if (this.question != null) {
			return this.question.getID();
		}
		return 0;
	}
	
	public String getQuestionName() {
		if (this.question != null) {
			return this.question.getName();
		}
		return null;
	}
	
	public String getQuestionKeyword() {
		if (this.question != null) {
			return this.question.getKeyword();
		}
		return null;
	}
	
	public String getQuestionSchema() {
		if (this.question != null) {
			return this.question.getSchemaName();
		}
		return null;
	}
	
	public String getQuestionDisplayName() {
		if (this.question != null) {
			return String.format("%s : %s (%s)", this.question.getName(), this.question.getKeyword(), this.question.getTypeLabel());
		}
		return null;
	}

	public long getFid() {
		return fid;
	}
	
	public abstract boolean isAnswerFor(Question question);
	
	public abstract String getAnswerValue();
	
	protected String[] toWords(int limit) {
		String textContent = this.getMessage().getTextContent();
		if (textContent != null) {
			return textContent.replaceFirst("[\\s]", " ").split(" ", limit);
		}
		return new String[0];
	}
	
	protected boolean isValidInteger(String word) {
		try {
			if (word != null) {
				Integer.parseInt(word.trim());
				return true;	
			}
		} 
		catch (NumberFormatException nfe) {
			//do nothing
		}
		return false;
	}
	
	protected boolean isValidInteger(List<String> choices, String answer) {
		if (answer != null && isValidInteger(answer.trim())) {
			int value = Integer.parseInt(answer.trim());
			if (value > 0 && value <= choices.size()) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isValidString(List<String> choices, String answer) {
		if (choices != null && choices.size() > 0 && answer != null && answer.length() > 0) {
			String answerTrimmed = answer.trim();
			for (String choice : choices) {
				//TODO improve fuzzy string comparison logic
				if (choice.equalsIgnoreCase(answerTrimmed) || 
					choice.toLowerCase().startsWith(answerTrimmed.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected String toString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		for (String value : list) {
			if (sb.length() > 0) {
				 sb.append(",");
			}
			sb.append(value);
		}
		return sb.toString();
	}
}
