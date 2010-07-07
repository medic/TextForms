package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;
import java.util.Date;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.Message;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.BooleanField;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.BooleanResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.BooleanMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.xml.XMLPublisher;
import net.frontlinesms.plugins.resourcemapper.xml.XMLUtils;

import org.dom4j.Document;
import org.springframework.context.ApplicationContext;

public class BooleanHandler implements FieldMessageHandler<BooleanField> {

	private FrontlineSMS frontline;
	private BooleanMappingDao mappingDao;
	private HospitalContactDao contactDao;

	public BooleanHandler(FrontlineSMS frontline, ApplicationContext appCon) {
		this.frontline = frontline;
		mappingDao = (BooleanMappingDao) appCon.getBean("booleanMappingDao");
		contactDao = (HospitalContactDao) appCon.getBean("hospitalContactDao");
	}

	public Collection<String> getKeywords() {
		return ShortCodeProperties.getInstance().getShortCodesForKeys(
				mappingDao.getShortCodes());
	}

	@SuppressWarnings("static-access")
	public void handleMessage(Message m) {
		String content = m.getTextContent().trim();
		content.replaceAll("[\\s]", " ");
		String[] commands = content.split(" ");
		if(responseIsValid(content)){
			// the user has texted in only the name of a field, so we treat this
			// as a request for information about the field
			if (commands.length == 1) {
				if (ResourceMapperProperties.getInstance().isInDebugMode()) {
					System.out.println(ShortCodeProperties.getInstance().getInfoSnippetForShortCode(commands[0]));
				} else {
					frontline.sendTextMessage(m.getSenderMsisdn(), ShortCodeProperties.getInstance().getInfoSnippetForShortCode(commands[0]));
				}
			} else if(commands.length == 2){
				BooleanField mapping = mappingDao.getMappingForShortCode(commands[0]);
				HospitalContact contact = contactDao.getHospitalContactByPhoneNumber(m.getSenderMsisdn());
				BooleanResponse response = new BooleanResponse(m, contact, new Date(), contact.getHospitalId(), mapping);
				generateAndPublishXML(response);
			}
		}else{
			if (ResourceMapperProperties.getInstance().isInDebugMode()) {
				System.out.println(ShortCodeProperties.getInstance().getValueForKey(ShortCodeProperties.BOOLEAN_VALIDATION_ERROR));
			} else {
				frontline.sendTextMessage(m.getSenderMsisdn(),ShortCodeProperties.getInstance().getValueForKey(ShortCodeProperties.BOOLEAN_VALIDATION_ERROR));
			}
		}
		
	}
	
	public boolean responseIsValid(String text){
		if(text.split(" ").length ==1){
			return true;
		}else if(text.split(" ").length > 2){
			return false;
		}
		String response = text.split(" ")[1];
		boolean isValid = false;
		for(String str:ShortCodeProperties.getInstance().getValueForKey("yes").split(",")){
			if(response.equalsIgnoreCase(str)){
				isValid=true;
			}
		}
		for(String str:ShortCodeProperties.getInstance().getValueForKey("no").split(",")){
			if(response.equalsIgnoreCase(str)){
				isValid=true;
			}
		}
		return isValid;
	}

	/**
	 * Gets the String response (that represents a boolean response) for the
	 * content of a message. You can assume that the message has been validated
	 * and found to contain a valid response at this point (i.e. the response is
	 * one of the valid boolean responses set out in the
	 * resourcemapper_shortcodes.properties file).
	 * 
	 * @param content
	 * @return
	 */
	public String getResponseForContent(String content){
		String[] responses = content.split(" ",2);
		String response = responses[1];
		boolean trueResponse=false;
		for(String str:ShortCodeProperties.getInstance().getValueForKey("yes").split(",")){
			if(response.equalsIgnoreCase(str)){
				trueResponse=true;
			}
		}
		return trueResponse?"True":"False";
	}

	public void generateAndPublishXML(FieldResponse<BooleanField> response) {
		Document doc = XMLUtils.getInitializedDocument(response);
		String textResponse = getResponseForContent(response.getMessage().getTextContent());
		String path = response.getMapping().getPathToElement() + "=" + textResponse;
		XMLUtils.handlePath(path, doc);
		for (String paths : response.getMapping().getAdditionalInstructions()) {
			XMLUtils.handlePath(paths, doc);
		}
		XMLPublisher.publish(doc.asXML());
	}

}
