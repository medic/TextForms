package net.frontlinesms.plugins.surveys;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.plugins.surveys.data.domain.OrganizationDetails;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.plugins.surveys.data.repository.QuestionFactory;
import net.frontlinesms.plugins.surveys.data.repository.AnswerDao;
import net.frontlinesms.plugins.surveys.data.repository.QuestionDao;
import net.frontlinesms.plugins.surveys.upload.CSVUploader;
import net.frontlinesms.plugins.surveys.upload.DocumentUploader;
import net.frontlinesms.plugins.surveys.upload.JSONUploader;
import net.frontlinesms.plugins.surveys.upload.ResourceFinderUploader;
import net.frontlinesms.plugins.surveys.upload.XMLUploader;

/**
 * SurveysDebug
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public class SurveysDebug {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(SurveysDebug.class);
	
	/**
	 * MessageDao
	 */
	private MessageDao messageDao;
	
	/**
	 * ContactDao
	 */
	private ContactDao contactDao;
	
	/**
	 * QuestionDao
	 */
	private QuestionDao questionDao;
	
	/**
	 * AnswerDao
	 */
	private AnswerDao answerDao;
	
	/**
	 * SurveysDebug
	 * @param appContext ApplicationContext
	 */
	public SurveysDebug(ApplicationContext appContext) {
		this.messageDao = (MessageDao)appContext.getBean("messageDao", MessageDao.class);
		this.contactDao = (ContactDao)appContext.getBean("contactDao", ContactDao.class);
		this.questionDao = (QuestionDao)appContext.getBean("questionDao", QuestionDao.class);
		this.answerDao = (AnswerDao)appContext.getBean("answerDao", AnswerDao.class);
	}
	
	public void createDebugContacts() {
		LOG.debug("createDebugContacts");
		createContact("Dale Zak", "+13063413644", "dalezak@gmail.com", true, "paho.org/HealthC_ID/1");
	}
	
	public void createContact(String name, String phoneNumber, String emailAddress, boolean active, String organizationId) {
		try {
			Contact contact = new Contact(name, phoneNumber, null, emailAddress, null, active);
			contact.addDetails(new OrganizationDetails(organizationId));
			this.contactDao.saveContact(contact);
			LOG.debug("Contact Created [%s, %s, %s, %s]", contact.getName(), contact.getPhoneNumber(), contact.getEmailAddress(), organizationId);
		} 
		catch (DuplicateKeyException ex) {
			LOG.error("Contact Exists [%s, %s, %s, %s]", name, phoneNumber, emailAddress, organizationId);
		}
	}
	
	public void createDebugQuestions() {
		LOG.debug("createDebugQuestions");
		//PLAIN TEXT
		createQuestion("Hospital Title", "title", "What is the hospital's title?", "plaintext", "title", null);
		createQuestion("Hospital Alternative Title", "alt", "What is the hospital's alternative title?", "plaintext", "alt_title", null);
		createQuestion("Hospital Contact Name", "contact", "What is hospital contact name?", "plaintext", "contact_name", null);
		createQuestion("Hospital Phone", "phone", "What is hospital phone number?", "plaintext", "phone", null);
		createQuestion("Hospital Email", "email", "What is hospital email address?", "plaintext", "email", null);
		createQuestion("Hospital Department", "department", "What is the hospital department?", "plaintext", "department", null);
		createQuestion("Hospital District", "district", "What is the hospital district?", "plaintext", "district", null);
		createQuestion("Hospital Commune", "commune", "What is the hospital commune?", "plaintext", "commune", null);
		createQuestion("Hospital Address", "address", "What is hospital address?", "plaintext", "address", null);
		createQuestion("Hospital Location", "location", "What is hospital location (latitude,longitude)?", "plaintext", "location", null);
		createQuestion("Hospital Location Accuracy", "accuracy", "What is hospital location accuracy?", "plaintext", "accuracy", null);
		createQuestion("Hospital Damage", "damage", "What is the hospital damage?", "plaintext", "damage", null);
		createQuestion("Additional Comments", "comments", "Additional comments?", "plaintext", "comments", null);
		//INTEGER
		createQuestion("Available Beds", "available", "How many beds does the hospital have available?", "integer", "available_beds", null);
		createQuestion("Total Beds", "beds", "The total number of hospital beds?", "integer", "total_beds", null);
		//BOOLEAN
		createQuestion("Hospital Reachable By Road", "reachable", "Is the hospital reachable by road?", "boolean", "reachable_by_road", null);
		createQuestion("Hospital Can Pick Up Patients", "pickup", "Can the hospital pick up patients?", "boolean", "can_pick_up_patients", null);
		//MULTICHOICE
		createQuestion("Hospital Type", "type", "What is the hospital type?", "multichoice", "organization_type",
				new String [] {"PUBLIC", "FOR_PROFIT", "UNIVERSITY", "COMMUNITY", "NGO", "FAITH_BASED", "MILITARY", "MIXED"});
		createQuestion("Hospital Category", "category", "What is the hospital category?", "multichoice", "category",
				new String [] {"HOSPITAL", "CLINIC", "MOBILE_CLINIC", "DISPENSARY"});
		createQuestion("Hospital Construction", "construction", "What is the hospital construction?", "multichoice", "construction",
				new String [] {"REINFORCED_CONCRETE", "UNREINFORCED_MASONRY", "WOOD_FRAME", "ADOBE"});
		createQuestion("Hospital Operational Status", "status", "What is the hospital operational status?", "multichoice", "operational_status",
				new String [] {"OPERATIONAL", "NO_SURGICAL_CAPACITY", "FIELD_HOSPITAL", "FIELD_WITH_HOSPITAL", "CLOSED_OR_CLOSING"});
		//CHECKLIST
		createQuestion("Hospital Services", "services", "What services does the hospital offer?", "checklist", "services", 
						new String [] {"GENERAL_SURGERY", "ORTHOPEDICS", "NEUROSURGERY", "VASCULAR_SURGERY", 
									   "INTERNAL_MEDICINE", "CARDIOLOGY", "INFECTIOUS_DISEASE", "PEDIATRICS", 
									   "POSTOPERATIVE_CARE", "REHABILITATION", "OBSTETRICS_GYNECOLOGY", "MENTAL_HEALTH",
									   "DIALYSIS", "LAB", "X_RAY", "CT_SCAN", "BLOOD_BANK", "MORTUARY_SERVICES"});
		
	}
	
	public void createQuestion(String name, String keyword, String infoSnippet, String type, String schema, String [] choices) {
		try {
			List<String> choiceList = choices != null ? Arrays.asList(choices) : null;
			Question question = QuestionFactory.createQuestion(name, keyword, infoSnippet, type, schema, choiceList);
			this.questionDao.saveQuestion(question);
			LOG.debug("Question Created [%s, %s, %s, %s]", question.getName(), question.getKeyword(), question.getType(), question.getSchemaName());
		} 
		catch (DuplicateKeyException e) {
			LOG.error("Question Exists [%s, %s, %s, %s]", name, keyword, type, schema);
		}
	}
	
	public void createDebugAnswers() {
		LOG.debug("createDebugAnswers");
		for (String message : new String[] {"title Title", 
											"alt Alternative Title",
											"available 55",
											"beds 66",
											"services 2,6,15",
											"contact Name",
											"phone +12 345-678-90",
											"email user@example.com",
											"department Ouest",
											"district Leeogane",
											"commune Petit-Goave",
											"address 123 Example Street",
											"location 18.3037,-72.8636",
											"accuracy Description of location accuracy.",
											"organization Example Organization",
											"category hospital",
											"construction 1",
											"damage Text describing building damage.",
											"status 1",
											"comments Arbitrary comment text goes here.",
											"reachable true",
											"pickup no",
											"invalid abc123", //invalid response
											"invalid" //invalid keyword
											}) {
			createAnswer(this.getDateReceived(), this.getAuthor(), message);
		}
	}
	
	public void createAnswer(long dateReceived, String senderMsisdn, String message) {
		try {
			FrontlineMessage frontlineMessage = FrontlineMessage.createIncomingMessage(dateReceived, senderMsisdn, null, message);
			this.messageDao.saveMessage(frontlineMessage);
			LOG.debug("Answer Created [%s, %s, %s]", dateReceived, senderMsisdn, message);
		}
		catch (Exception ex) {
			LOG.error("Exception: %s", ex);
		}
	}
	
	public void createUploadXMLDocument() {
		LOG.debug("createUploadXMLDocument");
		DocumentUploader document = new XMLUploader();
		document.setPhoneNumber(this.getAuthor());
		document.setOrganizationId(this.getOrganizationId());
		for (Answer answer : this.answerDao.getAllAnswers()) {
			document.addAnswer(answer);
		}
		
		LOG.debug(document.toString());
	}
	
	public void createUploadJSONDocument() {
		LOG.debug("createUploadJSONDocument");
		DocumentUploader document = new JSONUploader();
		document.setPhoneNumber(this.getAuthor());
		document.setOrganizationId(this.getOrganizationId());
		for (Answer answer : this.answerDao.getAllAnswers()) {
			document.addAnswer(answer);
		}
		LOG.debug(document.toString());
	}
	
	public void createUploadCSVDocument() {
		LOG.debug("createUploadCSVDocument");
		DocumentUploader document = new CSVUploader();
		document.setPhoneNumber(this.getAuthor());
		document.setOrganizationId(this.getOrganizationId());
		for (Answer answer : this.answerDao.getAllAnswers()) {
			document.addAnswer(answer);
		}
		LOG.debug(document.toString());
	}
	
	public void createUploadGoogleDocument() {
		LOG.debug("createUploadGoogleDocument");
		DocumentUploader document = new ResourceFinderUploader();
		document.setPhoneNumber(this.getAuthor());
		document.setOrganizationId(this.getOrganizationId());
		for (Answer answer : this.answerDao.getAllAnswers()) {
			document.addAnswer(answer);
		}
		LOG.debug(document.toString());
	}
	
	public void createAnswerOutputs() {
		LOG.debug("createAnswerOutputs");
		for (Answer answer : this.answerDao.getAllAnswers()) {
			LOG.debug("Answer: %s", answer.getMessageText());
			LOG.debug("%s : %s : %s", answer.getQuestionType(), answer.getQuestionName(), answer.getAnswerValue());
		}
	}
	
	public void startDebugTerminal() {
		Thread thread = new DebugTerminal();
		thread.start();
    }

	/**
	 * Inner threaded class for listening to System.in
	 * @author dalezak
	 *
	 */
	private class DebugTerminal extends Thread {
		public void run() {
			LOG.debug("startDebugTerminal...");
			List<String> exitKeywords = Arrays.asList("exit", "x", "quit", "q");
			LOG.error("Enter a message for the system");
	        Scanner scanner = new Scanner(System.in);
	        while(true) { 
	            String message = scanner.nextLine();
	            if (exitKeywords.contains(message.trim().toLowerCase())) {
	            	break;
	            }
	            else {
	            	createAnswer(Calendar.getInstance().getTimeInMillis(), getAuthor(), message);
	            }
	        }
	        LOG.debug("...startDebugTerminal");
		 }
	}
	
	private String getAuthor() {
		for (Contact contact : this.contactDao.getAllContacts()) {
			if (contact.getPhoneNumber() != null) {
				return contact.getPhoneNumber();
			}
			else if (contact.getOtherPhoneNumber() != null) {
				return contact.getOtherPhoneNumber();
			}
		}
		return null;
	}
	
	private String getOrganizationId() {
		for (Contact contact : this.contactDao.getAllContacts()) {
			OrganizationDetails organizationDetails = contact.getDetails(OrganizationDetails.class);
			return organizationDetails != null ? organizationDetails.getOrganizationId() : null;
		}
		return null;
	}
	
	private long getDateReceived() {
		return Calendar.getInstance().getTimeInMillis();
	}
}