package net.frontlinesms.plugins.textforms.upload;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.domain.questions.QuestionType;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;
import net.frontlinesms.plugins.textforms.data.repository.QuestionFactory;
import net.frontlinesms.plugins.textforms.data.repository.TextFormDao;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload to ResourceFinder
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public class ResourceFinderUploader extends DocumentUploader {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(ResourceFinderUploader.class);
	
	/**
	 * Atom Namespace
	 */
	private static final Namespace NAMESPACE_ATOM = new Namespace("", "http://www.w3.org/2005/Atom");
	
	/**
	 * Status Namespace
	 */
	private static final Namespace NAMESPACE_REPORT = new Namespace("report", "http://schemas.google.com/report/2010");
	
	/**
	 * GS Namespace
	 */
	private static final Namespace NAMESPACE_GS = new Namespace("gs", "http://schemas.google.com/spreadsheets/2006");
	
	/**
	 * QuestionDao
	 */
	private QuestionDao questionDao;
	
	private QuestionDao getQuestionDao() {
		if (this.questionDao == null) {
			this.questionDao = (QuestionDao) appContext.getBean("questionDao", QuestionDao.class);
		}
		return this.questionDao;
	}
	
	/**
	 * TextFormDao
	 */
	private TextFormDao textformDao;
	
	private TextFormDao getTextFormDao() {
		if (this.textformDao == null) {
			this.textformDao = (TextFormDao) appContext.getBean("textformDao", TextFormDao.class);
		}
		return this.textformDao;
	}
	
	/**
	 * ResourceFinderUploader
	 */
	public ResourceFinderUploader() { }
	
	/**
	 * Generate Google XML document for uploading
	 */
	@Override
	public String toString() {
		DocumentFactory factory = DocumentFactory.getInstance();
		Document document = factory.createDocument();
		
		//feed
		Element feedElement = document.addElement(new QName("feed", NAMESPACE_ATOM));
		feedElement.addNamespace("", NAMESPACE_ATOM.getURI());
		feedElement.addNamespace(NAMESPACE_REPORT.getPrefix(), NAMESPACE_REPORT.getURI());
		feedElement.addNamespace(NAMESPACE_GS.getPrefix(), NAMESPACE_GS.getURI());
		
		//entry
		Element entryElement = feedElement.addElement(new QName("entry", NAMESPACE_ATOM));
		
		//author
		Element authorElement = entryElement.addElement(new QName("author", NAMESPACE_ATOM));
		Element uriElement = authorElement.addElement(new QName("uri", NAMESPACE_ATOM));
		if (this.phoneNumber != null) {
			if (this.phoneNumber.startsWith("+")) {
				uriElement.setText(String.format("tel:%s", this.phoneNumber));
			}
			else {
				uriElement.setText(String.format("tel:+%s", this.phoneNumber));
			}
		}
		
		//subject
		Element subjectElement = entryElement.addElement(new QName("subject", NAMESPACE_REPORT));
		if (this.organizationId != null) {
			subjectElement.setText(this.organizationId);
		}
		
		//observed
		Element observedElement = entryElement.addElement(new QName("observed", NAMESPACE_REPORT));
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT-2"));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		observedElement.setText(dateFormat.format(now.getTime()));
		
		//add responses
		Element contentElement = entryElement.addElement(new QName("content", NAMESPACE_REPORT));
		contentElement.addAttribute("type", "{http://schemas.google.com/report/2010}row");
		Element rowElement = contentElement.addElement(new QName("row", NAMESPACE_REPORT));
		for (Answer answer : this.getAnswers()) {
			String schema = answer.getQuestionSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = answer.getAnswerValue();
				if (responseValue != null) {
					Element entry = rowElement.addElement(new QName("question", NAMESPACE_GS));
					entry.addAttribute("name", schema);
					entry.setText(responseValue);
				}
				else {
					LOG.error("AnswerValue is NULL for %s", schema);
				}
			}
		}
		return document.asXML();
	}
	
	public boolean createResourceFinderQuestions() {
		try {
			this.ui.removeAll(this.getTable());
			List<Question> questions = new ArrayList<Question>();
			//PLAIN TEXT
			createPlainTextQuestion(questions, "Hospital Title", "title", "What is the hospital's title?", "title");
			createPlainTextQuestion(questions, "Hospital Alternative Title", "alt", "What is the hospital's alternative title?", "alt_title");
			createPlainTextQuestion(questions, "Hospital Contact Name", "contact", "What is hospital contact name?", "contact_name");
			createPlainTextQuestion(questions, "Hospital Phone", "phone", "What is hospital phone number?", "phone");
			createPlainTextQuestion(questions, "Hospital Email", "email", "What is hospital email address?", "email");
			createPlainTextQuestion(questions, "Hospital Department", "department", "What is the hospital department?", "department");
			createPlainTextQuestion(questions, "Hospital District", "district", "What is the hospital district?", "district");
			createPlainTextQuestion(questions, "Hospital Commune", "commune", "What is the hospital commune?", "commune");
			createPlainTextQuestion(questions, "Hospital Address", "address", "What is hospital address?", "address");
			createPlainTextQuestion(questions, "Hospital Location", "location", "What is hospital location (latitude,longitude)?", "location");
			createPlainTextQuestion(questions, "Hospital Location Accuracy", "accuracy", "What is hospital location accuracy?", "accuracy");
			createPlainTextQuestion(questions, "Hospital Damage", "damage", "What is the hospital damage?", "damage");
			createPlainTextQuestion(questions, "Additional Comments", "comments", "Additional comments?", "comments");
			//INTEGER
			createIntegerQuestion(questions, "Available Beds", "available", "How many beds does the hospital have available?", "available_beds");
			createIntegerQuestion(questions, "Total Beds", "beds", "The total number of hospital beds?", "total_beds");
			//BOOLEAN
			createBooleanQuestion(questions, "Hospital Reachable By Road", "reachable", "Is the hospital reachable by road?", "reachable_by_road");
			createBooleanQuestion(questions, "Hospital Can Pick Up Patients", "pickup", "Can the hospital pick up patients?", "can_pick_up_patients");
			//MULTICHOICE
			createMultiChoiceQuestion(questions, "Hospital Type", "type", "What is the hospital type?", "organization_type",
										new String [] {"PUBLIC", "FOR_PROFIT", "UNIVERSITY", "COMMUNITY", "NGO", "FAITH_BASED", "MILITARY", "MIXED"});
			createMultiChoiceQuestion(questions, "Hospital Category", "category", "What is the hospital category?", "category",
										new String [] {"HOSPITAL", "CLINIC", "MOBILE_CLINIC", "DISPENSARY"});
			createMultiChoiceQuestion(questions, "Hospital Construction", "construction", "What is the hospital construction?", "construction",
										new String [] {"REINFORCED_CONCRETE", "UNREINFORCED_MASONRY", "WOOD_FRAME", "ADOBE"});
			createMultiChoiceQuestion(questions, "Hospital Operational Status", "status", "What is the hospital operational status?", "operational_status",
										new String [] {"OPERATIONAL", "NO_SURGICAL_CAPACITY", "FIELD_HOSPITAL", "FIELD_WITH_HOSPITAL", "CLOSED_OR_CLOSING"});
			//CHECKLIST
			createChecklistQuestion(questions, "Hospital Services", "services", "What services does the hospital offer?", "services", 
									new String [] {"GENERAL_SURGERY", "ORTHOPEDICS", "NEUROSURGERY", "VASCULAR_SURGERY", 
												   "INTERNAL_MEDICINE", "CARDIOLOGY", "INFECTIOUS_DISEASE", "PEDIATRICS", 
												   "POSTOPERATIVE_CARE", "REHABILITATION", "OBSTETRICS_GYNECOLOGY", "MENTAL_HEALTH",
												   "DIALYSIS", "LAB", "X_RAY", "CT_SCAN", "BLOOD_BANK", "MORTUARY_SERVICES"});
			//textform
			TextForm textform = new TextForm("ResourceFinder TextForm", "finder", questions);
			this.getTextFormDao().saveTextForm(textform);
			return true;
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	protected Question createPlainTextQuestion(List<Question> questions, String name, String keyword, String infoSnippet, String schema) {
		return createQuestion(questions, name, keyword, infoSnippet, QuestionType.PLAINTEXT, schema, null);
	}
	
	protected Question createDateQuestion(List<Question> questions, String name, String keyword, String infoSnippet, String schema) {
		return createQuestion(questions, name, keyword, infoSnippet, QuestionType.DATE, schema, null);
	}
	
	protected Question createIntegerQuestion(List<Question> questions, String name, String keyword, String infoSnippet, String schema) {
		return createQuestion(questions, name, keyword, infoSnippet, QuestionType.INTEGER, schema, null);
	}
	
	protected Question createBooleanQuestion(List<Question> questions, String name, String keyword, String infoSnippet, String schema) {
		return createQuestion(questions, name, keyword, infoSnippet, QuestionType.BOOLEAN, schema, null);
	}
	
	protected Question createMultiChoiceQuestion(List<Question> questions, String name, String keyword, String infoSnippet, String schema, String [] choices) {
		return createQuestion(questions, name, keyword, infoSnippet, QuestionType.MULTICHOICE, schema, choices);
	}
	
	protected Question createChecklistQuestion(List<Question> questions, String name, String keyword, String infoSnippet, String schema, String [] choices) {
		return createQuestion(questions, name, keyword, infoSnippet, QuestionType.CHECKLIST, schema, choices);
	}
	
	protected Question createQuestion(List<Question> questions, String name, String keyword, String infoSnippet, String type, String schema, String [] choices) {
		try {
			List<String> choiceList = choices != null ? Arrays.asList(choices) : null;
			Question question = QuestionFactory.createQuestion(name, keyword, infoSnippet, type, schema, choiceList);
			this.getQuestionDao().saveQuestion(question);
			LOG.debug("Question Created [%s, %s, %s, %s]", question.getName(), question.getKeyword(), question.getType(), question.getSchemaName());
			this.ui.add(getTable(), getRow(question));
			questions.add(question);
			return question;
		} 
		catch (DuplicateKeyException e) {
			LOG.error("Question Exists [%s, %s, %s, %s]", name, keyword, type, schema);
			Question question = this.getQuestionDao().getQuestionForKeyword(keyword);
			questions.add(question);
			return question;
		}
	}
	
	private Object getTable() {
		if (tableQuestions == null) {
			tableQuestions = this.ui.find(this.getMainPanel(), "tableQuestions");
		}
		return tableQuestions;
	}private Object tableQuestions;
	
	private Object getRow(Question question){
		Object row = this.ui.createTableRow(question);
		createTableCell(row, question.getName());
		createTableCell(row, question.getKeyword());
		createTableCell(row, question.getTypeLabel());
		createTableCell(row, question.getSchemaName());
		createTableCell(row, question.getInfoSnippet());
		return row;
	}
	
	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(TextFormsConstants.DOCUMENT_UPLOAD_GOOGLE);
	}
	
	@Override
	protected String getPanelXML() {
		return "/ui/plugins/textforms/upload/ResourceFinderUploader.xml";
	}

	/**
	 * Get ContentType
	 */
	@Override
	public String getContentType() {
		return "text/xml";
	}
}