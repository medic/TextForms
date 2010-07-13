package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperPluginController;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.CodedField;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.CodedResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.CodedMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.xml.XMLPublisher;
import net.frontlinesms.plugins.resourcemapper.xml.XMLUtils;

import org.dom4j.Document;
import org.springframework.context.ApplicationContext;

public class CodedHandler implements CallbackHandler<CodedField> {

	private FrontlineSMS frontline;
	private CodedMappingDao mappingDao;
	private HospitalContactDao contactDao;
	private HashMap<String,CodedField> callbacks;
	
	public CodedHandler(FrontlineSMS frontline, ApplicationContext appCon) {
		this.frontline = frontline;
		mappingDao = (CodedMappingDao) appCon.getBean("codedMappingDao");
		contactDao = (HospitalContactDao) appCon.getBean("hospitalContactDao");
		callbacks = new HashMap<String, CodedField>();
	}

	public Collection<String> getKeywords() {
		return ShortCodeProperties.getInstance().getShortCodesForKeys(mappingDao.getShortCodes());
	}

	@SuppressWarnings("static-access")
	public void handleMessage(FrontlineMessage m) {
		String content = m.getTextContent().trim();
		content.replaceAll("[\\s]", " ");
		//String[] commands = content.split(" ");
		if (messageIsValid(content)){
			String message = content + " " +ShortCodeProperties.getInstance().getValueForKey("coded.answer.prefix");
			Set<String> possibleResponses = mappingDao.getMappingForShortCode(content).getPossibleResponses();
			int index = 1;
			for (String possibleResponse: possibleResponses) {
				message += "\n" + index + " - " + possibleResponse;
				index++;		
			}
			output(m.getSenderMsisdn(),message);
			ResourceMapperPluginController.registerCallback(m.getSenderMsisdn(), this);
			this.callbacks.put(m.getSenderMsisdn(), mappingDao.getMappingForShortCode(content));
		}
		else{
			output(m.getSenderMsisdn(),ShortCodeProperties.getInstance().getValueForKey(ShortCodeProperties.CODED_VALIDATION_ERROR));
		}
	}
	
	/**
	 * The message is valid if it contains only 1 name of a coded field
	 * @param content
	 * @return
	 */
	public boolean messageIsValid(String content){
		return content.split(" ").length ==1 && getKeywords().contains(content);
	}

	/**
	 * Gets the string response for the response. This method should only 
	 * be called after a callback is received
	 * @param content
	 * @return
	 */
	public String getResponseForContent(String content, CodedField mapping) {
		//TODO Does this really need to be a set?
		Set<String> possibleResponses = mapping.getPossibleResponses();
		return possibleResponses.toArray(new String[possibleResponses.size()])[Integer.parseInt(content)-1];
	}

	public void generateAndPublishXML(FieldResponse<CodedField> response) {
//		Document doc = XMLUtils.getInitializedDocument(response);
//		String textResponse = getResponseForContent(response.getMessage().getTextContent(), response.getMapping());
//		String path = response.getMapping().getPathToElement() + "=" + textResponse;
//		XMLUtils.handlePath(path, doc);
//		for (String paths : response.getMapping().getAdditionalInstructions()) {
//			XMLUtils.handlePath(paths, doc);
//		}
//		XMLPublisher.publish(doc.asXML());
	}
	
	protected void output(String msisdn, String text){
		if (ResourceMapperProperties.getInstance().isInDebugMode()){
			System.out.println(text);
		}
		else {
			frontline.sendTextMessage(msisdn, text);
		}
	}

	public void handleCallback(FrontlineMessage m) {
		if(callbackMessageIsValid(m.getTextContent(),callbacks.get(m.getSenderMsisdn()))){
			CodedField mapping = callbacks.get(m.getSenderMsisdn());
			HospitalContact contact = contactDao.getHospitalContactByPhoneNumber(m.getSenderMsisdn());
			CodedResponse response = new CodedResponse(m, contact, new Date(), contact.getHospitalId(), mapping);
			generateAndPublishXML(response);
			ResourceMapperPluginController.unregisterCallback(m.getSenderMsisdn());
		}else{
			output(m.getSenderMsisdn(),ShortCodeProperties.getInstance().getValueForKey("coded.bad.answer.response"));
			ResourceMapperPluginController.unregisterCallback(m.getSenderMsisdn());
		}
	}
	
	private boolean callbackMessageIsValid(String content, CodedField mapping){
		if(shouldHandleCallbackMessage(content)){
			int max = mapping.getPossibleResponses().size();
			if(Integer.parseInt(content) <= max && Integer.parseInt(content) > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * this handler wants to handle the callback message if it contains only 1 number
	 * @see net.frontlinesms.plugins.resourcemapper.handler.fields.CallbackHandler#shouldHandleCallbackMessage(net.frontlinesms.data.domain.Message)
	 */
	public boolean shouldHandleCallbackMessage(FrontlineMessage m) {
		return shouldHandleCallbackMessage(m.getTextContent());
	}
	
	/**
	 * Helper method for shouldHandleCallbackMessage(Message m)
	 * @param content
	 * @return
	 */
	private boolean shouldHandleCallbackMessage(String content){
		//TODO:make this work
		return !content.matches("\\D") && content.split(" ").length ==1;
	}

	public void callBackTimedOut(String msisdn) {
		callbacks.remove(msisdn);
	}

}
