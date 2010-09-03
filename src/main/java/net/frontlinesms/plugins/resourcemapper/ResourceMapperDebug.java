package net.frontlinesms.plugins.resourcemapper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingFactory;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.upload.CSVUploader;
import net.frontlinesms.plugins.resourcemapper.upload.DocumentUploader;
import net.frontlinesms.plugins.resourcemapper.upload.JSONUploader;
import net.frontlinesms.plugins.resourcemapper.upload.ResourceFinderUploader;
import net.frontlinesms.plugins.resourcemapper.upload.XMLUploader;

/**
 * ResourceMapperDebug
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public class ResourceMapperDebug {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ResourceMapperDebug.class);
	
	/**
	 * MessageDao
	 */
	private MessageDao messageDao;
	
	/**
	 * HospitalContactDao
	 */
	private HospitalContactDao hospitalContactDao;
	
	/**
	 * FieldMappingDao
	 */
	private FieldMappingDao fieldMappingDao;
	
	/**
	 * FieldResponseDao
	 */
	private FieldResponseDao fieldResponseDao;
	
	/**
	 * ResourceMapperDebug
	 * @param appContext ApplicationContext
	 */
	public ResourceMapperDebug(ApplicationContext appContext) {
		this.messageDao = (MessageDao)appContext.getBean("messageDao");
		this.hospitalContactDao = (HospitalContactDao)appContext.getBean("hospitalContactDao");
		this.fieldMappingDao = (FieldMappingDao)appContext.getBean("fieldMappingDao");
		this.fieldResponseDao = (FieldResponseDao)appContext.getBean("fieldResponseDao");
	}
	
	public void createDebugContacts() {
		LOG.debug("createDebugContacts");
		createContact("Dale Zak", "+13063413644", "dalezak@gmail.com", true, "paho.org/HealthC_ID/1");
	}
	
	public void createContact(String name, String phoneNumber, String emailAddress, boolean active, String hospitalId) {
		try {
			HospitalContact hospitalContact = new HospitalContact(name, phoneNumber, emailAddress, active, hospitalId);
			this.hospitalContactDao.saveHospitalContact(hospitalContact);
			LOG.debug("Contact Created [%s, %s, %s, %s]", hospitalContact.getName(), hospitalContact.getPhoneNumber(), hospitalContact.getEmailAddress(), hospitalContact.getHospitalId());
		} 
		catch (DuplicateKeyException ex) {
			LOG.error("Contact Exists [%s, %s, %s, %s]", name, phoneNumber, emailAddress, hospitalId);
		}
	}
	
	public void createDebugFields() {
		LOG.debug("createDebugFields");
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
			this.fieldMappingDao.saveFieldMapping(field);
			LOG.debug("Field Created [%s, %s, %s, %s]", field.getName(), field.getKeyword(), field.getType(), field.getSchemaName());
		} 
		catch (DuplicateKeyException e) {
			LOG.error("Field Exists [%s, %s, %s, %s]", name, keyword, type, schema);
		}
	}
	
	public void createDebugResponses() {
		LOG.debug("createDebugResponses");
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
			createResponse(this.getDateReceived(), this.getAuthor(), message);
		}
	}
	
	public void createResponse(long dateReceived, String senderMsisdn, String message) {
		try {
			FrontlineMessage frontlineMessage = FrontlineMessage.createIncomingMessage(dateReceived, senderMsisdn, null, message);
			this.messageDao.saveMessage(frontlineMessage);
			LOG.debug("Response Created [%s, %s, %s]", dateReceived, senderMsisdn, message);
		}
		catch (Exception ex) {
			LOG.error("Exception: %s", ex);
		}
	}
	
	public void createUploadXMLDocument() {
		LOG.debug("createUploadXMLDocument");
		DocumentUploader document = new XMLUploader();
		document.setPhoneNumber(this.getAuthor());
		document.setHospitalId(this.getHospitalId());
		for (FieldResponse fieldResponse : this.fieldResponseDao.getAllFieldResponses()) {
			document.addFieldResponse(fieldResponse);
		}
		
		LOG.debug(document.toString());
	}
	
	public void createUploadJSONDocument() {
		LOG.debug("createUploadJSONDocument");
		DocumentUploader document = new JSONUploader();
		document.setPhoneNumber(this.getAuthor());
		document.setHospitalId(this.getHospitalId());
		for (FieldResponse fieldResponse : this.fieldResponseDao.getAllFieldResponses()) {
			document.addFieldResponse(fieldResponse);
		}
		LOG.debug(document.toString());
	}
	
	public void createUploadCSVDocument() {
		LOG.debug("createUploadCSVDocument");
		DocumentUploader document = new CSVUploader();
		document.setPhoneNumber(this.getAuthor());
		document.setHospitalId(this.getHospitalId());
		for (FieldResponse fieldResponse : this.fieldResponseDao.getAllFieldResponses()) {
			document.addFieldResponse(fieldResponse);
		}
		LOG.debug(document.toString());
	}
	
	public void createUploadGoogleDocument() {
		LOG.debug("createUploadGoogleDocument");
		DocumentUploader document = new ResourceFinderUploader();
		document.setPhoneNumber(this.getAuthor());
		document.setHospitalId(this.getHospitalId());
		for (FieldResponse fieldResponse : this.fieldResponseDao.getAllFieldResponses()) {
			document.addFieldResponse(fieldResponse);
		}
		LOG.debug(document.toString());
	}
	
	public void createResponseOutputs() {
		LOG.debug("createResponseOutputs");
		for (FieldResponse fieldResponse : this.fieldResponseDao.getAllFieldResponses()) {
			LOG.debug("Response: %s", fieldResponse.getMessageText());
			LOG.debug("%s : %s : %s", fieldResponse.getMappingType(), fieldResponse.getMappingName(), fieldResponse.getResponseValue());
		}
	}
	
	public void startDebugTerminal() {
		Thread thread = new DebugTerminal(this.getAuthor());
		thread.start();
    }

	/**
	 * Inner threaded class for listening to System.in
	 * @author dalezak
	 *
	 */
	private class DebugTerminal extends Thread {
		private String phoneNumber;
		
		public DebugTerminal(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}
		
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
	            	createResponse(Calendar.getInstance().getTimeInMillis(), this.phoneNumber, message);
	            }
	        }
	        LOG.debug("...startDebugTerminal");
		 }
	}
	
	private String getAuthor() {
		for (HospitalContact contact : this.hospitalContactDao.getAllHospitalContacts()) {
			return contact.getPhoneNumber();
		}
		return null;
	}
	
	private String getHospitalId() {
		for (HospitalContact contact : this.hospitalContactDao.getAllHospitalContacts()) {
			return contact.getHospitalId();
		}
		return null;
	}
	
	private long getDateReceived() {
		return Calendar.getInstance().getTimeInMillis();
	}
}