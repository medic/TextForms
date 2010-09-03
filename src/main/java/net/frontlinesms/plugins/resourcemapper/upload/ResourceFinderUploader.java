package net.frontlinesms.plugins.resourcemapper.upload;

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
import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingFactory;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload to ResourceFinder
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public class ResourceFinderUploader extends DocumentUploader {
	
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ResourceFinderUploader.class);
	
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
	 * FieldMappingDao
	 */
	private FieldMappingDao fieldMappingDao;
	
	private FieldMappingDao getFieldMappingDao() {
		if (this.fieldMappingDao == null) {
			this.fieldMappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
		}
		return this.fieldMappingDao;
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
		for (FieldResponse fieldResponse : this.getFieldResponses()) {
			String schema = fieldResponse.getMappingSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = fieldResponse.getResponseValue();
				if (responseValue != null) {
					Element entry = rowElement.addElement(new QName("field", NAMESPACE_GS));
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
	
	public void createResourceFinderFields() {
		this.ui.removeAll(this.getTable());
		//PLAIN TEXT
		createField("Hospital Title", "title", "What is the hospital's title?", "plaintext", "title", null);
		createField("Hospital Alternative Title", "alt", "What is the hospital's alternative title?", "plaintext", "alt_title", null);
		createField("Hospital Contact Name", "contact", "What is hospital contact name?", "plaintext", "contact_name", null);
		createField("Hospital Phone", "phone", "What is hospital phone number?", "plaintext", "phone", null);
		createField("Hospital Email", "email", "What is hospital email address?", "plaintext", "email", null);
		createField("Hospital Department", "department", "What is the hospital department?", "plaintext", "department", null);
		createField("Hospital District", "district", "What is the hospital district?", "plaintext", "district", null);
		createField("Hospital Commune", "commune", "What is the hospital commune?", "plaintext", "commune", null);
		createField("Hospital Address", "address", "What is hospital address?", "plaintext", "address", null);
		createField("Hospital Location", "location", "What is hospital location (latitude,longitude)?", "plaintext", "location", null);
		createField("Hospital Location Accuracy", "accuracy", "What is hospital location accuracy?", "plaintext", "accuracy", null);
		createField("Hospital Damage", "damage", "What is the hospital damage?", "plaintext", "damage", null);
		createField("Additional Comments", "comments", "Additional comments?", "plaintext", "comments", null);
		//INTEGER
		createField("Available Beds", "available", "How many beds does the hospital have available?", "integer", "available_beds", null);
		createField("Total Beds", "beds", "The total number of hospital beds?", "integer", "total_beds", null);
		//BOOLEAN
		createField("Hospital Reachable By Road", "reachable", "Is the hospital reachable by road?", "boolean", "reachable_by_road", null);
		createField("Hospital Can Pick Up Patients", "pickup", "Can the hospital pick up patients?", "boolean", "can_pick_up_patients", null);
		//MULTICHOICE
		createField("Hospital Type", "type", "What is the hospital type?", "multichoice", "organization_type",
				new String [] {"PUBLIC", "FOR_PROFIT", "UNIVERSITY", "COMMUNITY", "NGO", "FAITH_BASED", "MILITARY", "MIXED"});
		createField("Hospital Category", "category", "What is the hospital category?", "multichoice", "category",
				new String [] {"HOSPITAL", "CLINIC", "MOBILE_CLINIC", "DISPENSARY"});
		createField("Hospital Construction", "construction", "What is the hospital construction?", "multichoice", "construction",
				new String [] {"REINFORCED_CONCRETE", "UNREINFORCED_MASONRY", "WOOD_FRAME", "ADOBE"});
		createField("Hospital Operational Status", "status", "What is the hospital operational status?", "multichoice", "operational_status",
				new String [] {"OPERATIONAL", "NO_SURGICAL_CAPACITY", "FIELD_HOSPITAL", "FIELD_WITH_HOSPITAL", "CLOSED_OR_CLOSING"});
		//CHECKLIST
		createField("Hospital Services", "services", "What services does the hospital offer?", "checklist", "services", 
						new String [] {"GENERAL_SURGERY", "ORTHOPEDICS", "NEUROSURGERY", "VASCULAR_SURGERY", 
									   "INTERNAL_MEDICINE", "CARDIOLOGY", "INFECTIOUS_DISEASE", "PEDIATRICS", 
									   "POSTOPERATIVE_CARE", "REHABILITATION", "OBSTETRICS_GYNECOLOGY", "MENTAL_HEALTH",
									   "DIALYSIS", "LAB", "X_RAY", "CT_SCAN", "BLOOD_BANK", "MORTUARY_SERVICES"});
	}
	
	public void createField(String name, String keyword, String infoSnippet, String type, String schema, String [] choices) {
		try {
			List<String> choiceList = choices != null ? Arrays.asList(choices) : null;
			Field field = FieldMappingFactory.createField(name, keyword, infoSnippet, type, schema, choiceList);
			this.getFieldMappingDao().saveFieldMapping(field);
			LOG.debug("Field Created [%s, %s, %s, %s]", field.getName(), field.getKeyword(), field.getType(), field.getSchemaName());
			this.ui.add(getTable(), getRow(field));
		} 
		catch (DuplicateKeyException e) {
			LOG.error("Field Exists [%s, %s, %s, %s]", name, keyword, type, schema);
		}
	}
	
	private Object getTable() {
		if (tableFields == null) {
			tableFields = this.ui.find(this.getMainPanel(), "tableFields");
		}
		return tableFields;
	}private Object tableFields;
	
	private Object getRow(Field field){
		Object row = this.ui.createTableRow(field);
		createTableCell(row, field.getName());
		createTableCell(row, field.getKeyword());
		createTableCell(row, field.getTypeLabel());
		createTableCell(row, field.getSchemaName());
		createTableCell(row, field.getInfoSnippet());
		return row;
	}
	
	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.DOCUMENT_UPLOAD_GOOGLE);
	}
	
	@Override
	protected String getPanelXML() {
		return "/ui/plugins/resourcemapper/upload/ResourceFinderUploader.xml";
	}

	/**
	 * Get ContentType
	 */
	@Override
	public String getContentType() {
		return "text/xml";
	}
}