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
import net.frontlinesms.plugins.resourcemapper.upload.GoogleUploader;
import net.frontlinesms.plugins.resourcemapper.upload.JSONUploader;
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
		createContact("Dale Zak", "+13063413644", "dalezak@gmail.com", true, "Saskatoon RUH");
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
		createField("Hospital Address", "addr", "Reply with 'addr' keyword followed by hospital address.", "plaintext", "hospital_address", null);
		createField("Hospital Power", "power", "Reply with 'power' keyword followed by your boolean answer.", "boolean", "hospital_power", null);
		createField("Hospital Beds", "beds", "Reply with 'beds' keyword followed by number of beds.", "integer", "hospital_beds", null);
		createField("Hospital Available", "date", "Reply with 'date' keyword followed by date hospital opened.", "date", "hospital_date", null);
		createField("Hospital Type", "type", "Reply with 'type' keyword followed by your checklist answers.", "checklist", "hospital_type", 
						new String [] {"Military", "University", "Clinic", "Private", "Public"});
		createField("Hospital Services", "serv", "Reply with 'serv' keyword followed by your multiple choice answer.", "multichoice", "hospital_services",
						new String [] {"Emergency", "Ambulance", "Dental", "MRI", "CT Scan"});
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
		for (String message : new String[] {"info addr", "help power", "? type", 
											"register", 
											"register Saskatoon RUH 2",
											"addr 123 University Dr, Saskatoon", //ignore previous callback, handled by plaintext handler
											"power", "yes", //callback
											"power yes", "power true", "power 1",
											"power no", "power false", "power 0",
											"power yyy", //invalid response
											"type", "military", "type university, Public", "type 1, 3", "type 1-3", "type 1,3-5",
											"type 3-6", //invalid type range
											"type 6", //invalid type
											"type invalid", //invalid type
											"serv", "1", "serv dental", "serv 2",
											"serv 0", //invalid service index
											"serv invalid", //invalid service
											"beds 143", 
											"beds abc123", //invalid integer
											"date 11/04/2010", "date 4-7-2009",
											"date abd123", //invalid date
											"invalid", "1", "" //invalid responses
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
		DocumentUploader document = new GoogleUploader();
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