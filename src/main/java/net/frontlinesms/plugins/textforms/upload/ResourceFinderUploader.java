package net.frontlinesms.plugins.textforms.upload;

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
					Element entry = rowElement.addElement(new QName("field", NAMESPACE_GS));
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

	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(TextFormsConstants.DOCUMENT_UPLOAD_GOOGLE);
	}
}