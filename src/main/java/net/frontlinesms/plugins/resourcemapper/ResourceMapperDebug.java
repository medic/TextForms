package net.frontlinesms.plugins.resourcemapper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingFactory;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;

public class ResourceMapperDebug {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ResourceMapperDebug.class);
	
	private ResourceMapperPluginController pluginController;
	private MessageDao messageDao;
	private HospitalContactDao hospitalContactDao;
	private FieldMappingDao fieldMappingDao;
	
	public ResourceMapperDebug(ResourceMapperPluginController pluginController, ApplicationContext appContext) {
		this.pluginController = pluginController;
		this.messageDao = (MessageDao)appContext.getBean("messageDao");
		this.hospitalContactDao = (HospitalContactDao)appContext.getBean("hospitalContactDao");
		this.fieldMappingDao = (FieldMappingDao)appContext.getBean("fieldMappingDao");
	}
	
	public void createDebugContacts() {
		LOG.debug("createDebugContacts");
		createContact("Dale Zak", "306.341.3644", "dalezak@gmail.com", true, "Saskatoon RUH");
		createContact("April Hiebert", "306.341.3645", "april.hiebert@gmail.com", true, "Saskatoon City Hospital");
	}
	
	public void createContact(String name, String phoneNumber, String emailAddress, boolean active, String hospitalId) {
		try {
			HospitalContact hospitalContact = new HospitalContact(name, phoneNumber, emailAddress, active, hospitalId);
			LOG.debug("Contact Created [%s, %s, %s, %s]", hospitalContact.getName(), hospitalContact.getPhoneNumber(), hospitalContact.getEmailAddress(), hospitalContact.getHospitalId());
			this.hospitalContactDao.saveHospitalContact(hospitalContact);
			LOG.debug("Contact Saved [%s, %s, %s, %s]", hospitalContact.getName(), hospitalContact.getPhoneNumber(), hospitalContact.getEmailAddress(), hospitalContact.getHospitalId());
		} 
		catch (DuplicateKeyException ex) {
			LOG.error("Contact Exists [%s, %s, %s, %s]", name, phoneNumber, emailAddress, hospitalId);
		}
	}
	
	public void createDebugFields() {
		LOG.debug("createDebugFields");
		createField("Hospital Name", "name", "Reply with 'name' keyword followed by name of hospital.", "plaintext", null);
		createField("Hospital Power", "power", "Reply with 'power' keyword followed by your boolean answer.", "boolean", null);
		//createField("Hospital Beds", "beds", "Reply with 'beds' keyword followed by number of beds.", "integer", null);
		createField("Hospital Type", "type", "Reply with 'type' keyword followed by your checklist answers.", "checklist", 
						new String [] {"Military", "University", "Clinic", "Private", "Public"});
		createField("Hospital Services", "serv", "Reply with 'serv' keyword followed by your multiple choice answer.", "multichoice", 
						new String [] {"Emergency", "Ambulance", "Dental", "MRI", "CT Scan"});
	}
	
	public void createField(String name, String keyword, String infoSnippet, String type, String [] choices) {
		try {
			List<String> choiceList = choices != null ? Arrays.asList(choices) : null;
			Field field = FieldMappingFactory.createField(name, keyword, infoSnippet, type, choiceList);
			LOG.debug("Field Created [%s, %s, %s]", field.getName(), field.getKeyword(), field.getType());
			this.fieldMappingDao.saveFieldMapping(field);
			LOG.debug("Field Saved [%s, %s, %s]", field.getName(), field.getKeyword(), field.getType());
		} 
		catch (DuplicateKeyException e) {
			LOG.error("Field Exists [%s, %s, %s]", name, keyword, type);
		}
	}
	
	public void createDebugResponses() {
		LOG.debug("createDebugResponses");
		long dateReceived = Calendar.getInstance().getTimeInMillis();
		
		String senderMsisdn = null;	
		for (HospitalContact contact : this.hospitalContactDao.getAllHospitalContacts()) {
			senderMsisdn = contact.getPhoneNumber();
			break;
		}
		
		for (String message : new String[] {"info name", "help power", "? type", "serv", 
											"name Saskatoon RUH", //ignore previous callback, handled by plaintext handler
											"power", "yes", "power yes", "power true", "power y", "power t", "power 1",
											"power no", "power false", "power n", "power f", "power 0",
											"power yyy", //invalid response
											"type", "military", "type university, Public", "type 1, 3",
											"type 6", //invalid type
											"type invalid", //invalid type
											"serv", "1", "serv dental", "serv 2",
											"serv 0", //invalid service index
											"serv invalid", //invalid service
											//"beds 143", 
											//"beds abc123", //invalid in
											"invalid", "1", "" //invalid responses
											}) {
			createResponse(dateReceived, senderMsisdn, message);
		}
	}
	
	public void createResponse(long dateReceived, String senderMsisdn, String message) {
		try {
			FrontlineMessage frontlineMessage = FrontlineMessage.createIncomingMessage(dateReceived, senderMsisdn, null, message);
			LOG.debug("Response Created [%s, %s, %s]", frontlineMessage.getDate(), frontlineMessage.getSenderMsisdn(), frontlineMessage.getTextContent());
			this.messageDao.saveMessage(frontlineMessage);
			LOG.debug("Response Saved [%s, %s, %s]", frontlineMessage.getDate(), frontlineMessage.getSenderMsisdn(), frontlineMessage.getTextContent());
			pluginController.incomingMessageEvent(frontlineMessage);
		}
		catch (Exception ex) {
			LOG.error("Exception: %s", ex);
		}
	}
	
}