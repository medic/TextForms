package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;
import java.util.Date;

import org.dom4j.Document;
import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseFactory;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.handler.MessageHandler;
import net.frontlinesms.plugins.resourcemapper.xml.XMLPublisher;
import net.frontlinesms.plugins.resourcemapper.xml.XMLUtils;

public abstract class FieldMessageHandler<M extends Field> implements MessageHandler {

	protected FrontlineSMS frontline;
	protected ApplicationContext appContext;
	
	protected FieldMappingDao mappingDao;
	protected FieldResponseDao responseDao;
	protected HospitalContactDao contactDao;
	
	public FieldMessageHandler(FrontlineSMS frontline, ApplicationContext appContext){
		this.frontline = frontline;
		this.appContext = appContext;
		this.mappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
		this.responseDao = (FieldResponseDao) appContext.getBean("fieldResponseDao");
		this.contactDao = (HospitalContactDao) appContext.getBean("hospitalContactDao");
	}
	
	protected Collection<String> getKeywords() {
		return this.mappingDao.getAbbreviations();
	}

	@SuppressWarnings("static-access")
	public void handleMessage(FrontlineMessage message) {
		System.out.println("FieldMessageHandler.handleMessage: " + message);
		String content = message.getTextContent();
		content.replaceFirst("[\\s]", " ");
		String[] commands = content.split(" ", 2);
		//the user has texted in only the name of a field, 
		//so we treat this as a request for information about the field
		if (commands.length == 1) {
			String infoSnippetForShortCode = ShortCodeProperties.getInstance().getInfoSnippetForShortCode(commands[0]);
			if (ResourceMapperProperties.getInstance().isInDebugMode()) {
				System.out.println(infoSnippetForShortCode);
			}
			else {
				this.frontline.sendTextMessage(message.getSenderMsisdn(), infoSnippetForShortCode);
			}
		}
		else if (commands.length > 0) {
			Field field = this.mappingDao.getFieldForAbbreviation(commands[0]);
			if (field != null) {
				HospitalContact contact = this.contactDao.getHospitalContactByPhoneNumber(message.getSenderMsisdn());
				if (contact != null) {
					FieldResponse response = FieldResponseFactory.createFieldResponse(message, contact, new Date(), contact.getHospitalId(), field);
					//generateAndPublishXML(response);
				}
				else {
					//TODO return warning that hospital is required
					System.err.println("Warning, hospital contact is required");
				}	
			}
			else {
				//TODO return warning that mapping is required
				System.err.println("Warning, field mapping is required");
			}
		}
		else {
			//TODO handle empty case
			System.err.println("Warning, empty command");
		}
	}
	
	public void generateAndPublishXML(FieldResponse<M> response) {
		System.out.println("generateAndPublishXML: " + response);
		if (response != null) {
			Document document = XMLUtils.getInitializedDocument(response);
			String [] words = response.getMessage().getTextContent().split(" ", 2);
			String keyword = words[0];
			System.out.println("keyword: " + keyword);
			String text = words[1];
			System.out.println("text: " + text);
			String pathToElement = response.getMapping().getPathToElement();
			if (pathToElement != null) {
				String path = pathToElement + "=" + text;
				XMLUtils.handlePath(path, document);
			}
			for (String paths: response.getMapping().getAdditionalInstructions()) {
				XMLUtils.handlePath(paths, document);
			}
			XMLPublisher.publish(document.asXML());
		}
		else {
			System.out.println("FieldMessageHandler.generateAndPublishXML response is NULL");
		}
	}
}
