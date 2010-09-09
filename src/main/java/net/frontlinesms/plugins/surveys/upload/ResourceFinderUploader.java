package net.frontlinesms.plugins.surveys.upload;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.plugins.surveys.data.repository.QuestionDao;
import net.frontlinesms.plugins.surveys.data.repository.QuestionFactory;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload to ResourceFinder
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public class ResourceFinderUploader extends DocumentUploader {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(ResourceFinderUploader.class);
	
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
			this.questionDao = (QuestionDao) appContext.getBean("questionDao");
		}
		return this.questionDao;
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
		if (this.hospitalId != null) {
			subjectElement.setText(this.hospitalId);
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
	
	public void createResourceFinderQuestions() {
		this.ui.removeAll(this.getTable());
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
			this.getQuestionDao().saveQuestion(question);
			LOG.debug("Question Created [%s, %s, %s, %s]", question.getName(), question.getKeyword(), question.getType(), question.getSchemaName());
			this.ui.add(getTable(), getRow(question));
		} 
		catch (DuplicateKeyException e) {
			LOG.error("Question Exists [%s, %s, %s, %s]", name, keyword, type, schema);
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
		return InternationalisationUtils.getI18NString(SurveysConstants.DOCUMENT_UPLOAD_GOOGLE);
	}
	
	@Override
	protected String getPanelXML() {
		return "/ui/plugins/surveys/upload/ResourceFinderUploader.xml";
	}

	/**
	 * Get ContentType
	 */
	@Override
	public String getContentType() {
		return "text/xml";
	}
}