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
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.CodedResponse;

import org.springframework.context.ApplicationContext;

public class CodedHandler extends CallbackHandler<CodedField> {

	public CodedHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
		callbacks = new HashMap<String, Field>();
	}

	private HashMap<String, Field> callbacks;
	
	
	protected Collection<String> getKeywords() {
		return mappingDao.getAbbreviations();
	}

	@SuppressWarnings("static-access")
	public void handleMessage(FrontlineMessage m) {
		String content = m.getTextContent().trim();
		content.replaceAll("[\\s]", " ");
		//String[] commands = content.split(" ");
		if (isSatisfiedBy(content)) {
			String message = content + " " +ShortCodeProperties.getInstance().getValueForKey("coded.answer.prefix");
			Set<String> possibleResponses = mappingDao.getFieldForAbbreviation(content).getChoices();
			int index = 1;
			for (String possibleResponse: possibleResponses) {
				message += "\n" + index + " - " + possibleResponse;
				index++;		
			}
			output(m.getSenderMsisdn(), message);
			//TODO call via callback interface
			ResourceMapperPluginController.registerCallback(m.getSenderMsisdn(), this);
			this.callbacks.put(m.getSenderMsisdn(), mappingDao.getFieldForAbbreviation(content));
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
	public boolean isSatisfiedBy(String content) {
		return content.split(" ").length == 1 && getKeywords().contains(content);
	}
	
	/**
	 * Gets the string response for the response. This method should only 
	 * be called after a callback is received
	 * @param content
	 * @return
	 */
	public String getResponseForContent(String content, CodedField mapping) {
		//TODO Does this really need to be a set?
		Set<String> possibleResponses = mapping.getChoices();
		return possibleResponses.toArray(new String[possibleResponses.size()])[Integer.parseInt(content)-1];
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
		if (callbackMessageIsValid(m.getTextContent(), callbacks.get(m.getSenderMsisdn()))){
			CodedField mapping = (CodedField)callbacks.get(m.getSenderMsisdn());
			HospitalContact contact = contactDao.getHospitalContactByPhoneNumber(m.getSenderMsisdn());
			CodedResponse response = new CodedResponse(m, contact, new Date(), contact.getHospitalId(), mapping);
			generateAndPublishXML(response);
			ResourceMapperPluginController.unregisterCallback(m.getSenderMsisdn());
		}else{
			output(m.getSenderMsisdn(), ShortCodeProperties.getInstance().getValueForKey("coded.bad.answer.response"));
			ResourceMapperPluginController.unregisterCallback(m.getSenderMsisdn());
		}
	}
	
	private boolean callbackMessageIsValid(String content, Field mapping){
		if (shouldHandleCallbackMessage(content)) {
			int max = mapping.getChoices().size();
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
