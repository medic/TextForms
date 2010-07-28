package net.frontlinesms.plugins.resourcemapper.upload;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.upload.UploadDocument;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload Google Document
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public class GoogleDocument extends UploadDocument {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(XMLDocument.class);
	
	private Namespace atom = new Namespace("atom", "http://www.w3.org/2005/Atom");
	private Namespace status = new Namespace("status", "http://schemas.google.com/status/2010");
	private Namespace gs = new Namespace("gs", "http://schemas.google.com/spreadsheets/2006");
	
	public GoogleDocument() {
		
	}
	
	/**
	 * XML Document
	 */
	private Document document;
	
	/**
	 * Phone Number
	 */
	private String phoneNumber;
	
	/**
	 * Hospital ID
	 */
	private String hospitalId; 
	
	/**
	 * GoogleDocument
	 * @param phoneNumber Phone Number
	 * @param hospitalID Hospital ID
	 */
	public GoogleDocument(String phoneNumber, String hospitalId) {
		DocumentFactory factory = DocumentFactory.getInstance();
		this.document = factory.createDocument();
		Element rootElement = this.document.addElement("entry");
		rootElement.addNamespace("", this.atom.getURI());
		rootElement.addNamespace(this.status.getPrefix(), this.status.getURI());
		rootElement.addNamespace(this.gs.getPrefix(), this.gs.getURI());
		this.phoneNumber = phoneNumber;
		this.hospitalId = hospitalId;
	}
	
	/**
	 * Generate XML document for uploading
	 */
	@Override
	public String toString() {
		Element rootElement = this.document.getRootElement();
		
		//author
		Element authorElement = rootElement.addElement("author");
		Element uriElement = authorElement.addElement("uri");
		if (this.phoneNumber != null) {
			uriElement.setText(String.format("tel:+%s", this.phoneNumber));
		}
		
		//subject
		Element subjectElement = rootElement.addElement(new QName("subject", this.status));
		if (this.hospitalId != null) {
			subjectElement.setText(this.hospitalId);
		}
		
		//observed
		Element observedElement = rootElement.addElement(new QName("observed", this.status));
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT-2"));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		observedElement.setText(dateFormat.format(now.getTime()));
		
		//add responses
		Element reportElement = rootElement.addElement(new QName("report", this.status));
		reportElement.addAttribute("type", "{http://schemas.google.com/status/2010}record");
		Element recordElement = reportElement.addElement(new QName("record", this.status));
		for (FieldResponse fieldResponse : this.getFieldResponses()) {
			String schema = fieldResponse.getMappingSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = fieldResponse.getResponseValue();
				if (responseValue != null) {
					Element entry = recordElement.addElement(new QName("field", this.gs));
					entry.addAttribute("name", schema);
					entry.setText(responseValue);
				}
				else {
					LOG.error("ResponseValue is NULL for %s", schema);
				}
			}
		}
		return this.document.asXML();
	}
	
	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.DOCUMENT_UPLOAD_GOOGLE);
	}
	
}