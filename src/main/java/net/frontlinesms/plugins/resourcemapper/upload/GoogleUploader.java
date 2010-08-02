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
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload Google XML Document
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public class GoogleUploader extends DocumentUploader {
	
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(GoogleUploader.class);
	
	/**
	 * Atom Namespace
	 */
	private static final Namespace NAMESPACE_ATOM = new Namespace("atom", "http://www.w3.org/2005/Atom");
	
	/**
	 * Status Namespace
	 */
	private static final Namespace NAMESPACE_STATUS = new Namespace("status", "http://schemas.google.com/status/2010");
	
	/**
	 * GS Namespace
	 */
	private static final Namespace NAMESPACE_GS = new Namespace("gs", "http://schemas.google.com/spreadsheets/2006");
	
	/**
	 * GoogleUploader
	 */
	public GoogleUploader() { }
	
	/**
	 * Generate Google XML document for uploading
	 */
	@Override
	public String toString() {
		DocumentFactory factory = DocumentFactory.getInstance();
		Document document = factory.createDocument();
		
		//root
		Element rootElement = document.addElement("entry");
		rootElement.addNamespace("", NAMESPACE_ATOM.getURI());
		rootElement.addNamespace(NAMESPACE_STATUS.getPrefix(), NAMESPACE_STATUS.getURI());
		rootElement.addNamespace(NAMESPACE_GS.getPrefix(), NAMESPACE_GS.getURI());
		
		//author
		Element authorElement = rootElement.addElement("author");
		Element uriElement = authorElement.addElement("uri");
		if (this.phoneNumber != null) {
			uriElement.setText(String.format("tel:+%s", this.phoneNumber));
		}
		
		//subject
		Element subjectElement = rootElement.addElement(new QName("subject", NAMESPACE_STATUS));
		if (this.hospitalId != null) {
			subjectElement.setText(this.hospitalId);
		}
		
		//observed
		Element observedElement = rootElement.addElement(new QName("observed", NAMESPACE_STATUS));
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT-2"));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		observedElement.setText(dateFormat.format(now.getTime()));
		
		//add responses
		Element reportElement = rootElement.addElement(new QName("report", NAMESPACE_STATUS));
		reportElement.addAttribute("type", "{http://schemas.google.com/status/2010}record");
		Element recordElement = reportElement.addElement(new QName("record", NAMESPACE_STATUS));
		for (FieldResponse fieldResponse : this.getFieldResponses()) {
			String schema = fieldResponse.getMappingSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = fieldResponse.getResponseValue();
				if (responseValue != null) {
					Element entry = recordElement.addElement(new QName("field", NAMESPACE_GS));
					entry.addAttribute("name", schema);
					entry.setText(responseValue);
				}
				else {
					LOG.error("ResponseValue is NULL for %s", schema);
				}
			}
		}
		return document.asXML();
	}
	
	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.DOCUMENT_UPLOAD_GOOGLE);
	}
	
	@Override
	public String getPanelXML() {
		return "/ui/plugins/resourcemapper/upload/GoogleUploader.xml";
	}
}