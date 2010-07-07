package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;
import java.util.Date;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.PlainTextResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.PlainTextMappingDao;
import net.frontlinesms.plugins.resourcemapper.xml.XMLPublisher;
import net.frontlinesms.plugins.resourcemapper.xml.XMLUtils;

import org.dom4j.Document;
import org.springframework.context.ApplicationContext;

public class PlainTextHandler implements FieldMessageHandler<PlainTextField> {
	
	private FrontlineSMS frontline;
	private PlainTextMappingDao mappingDao;
	private HospitalContactDao contactDao;
	
	public PlainTextHandler(FrontlineSMS frontline, ApplicationContext appCon){
		this.frontline = frontline;
		mappingDao = (PlainTextMappingDao) appCon.getBean("plainTextMappingDao");
		contactDao = (HospitalContactDao) appCon.getBean("hospitalContactDao");
	}
	
	public Collection<String> getKeywords() {
		return ShortCodeProperties.getInstance().getShortCodesForKeys(mappingDao.getShortCodes());
	}

	@SuppressWarnings("static-access")
	public void handleMessage(FrontlineMessage m) {
		String content = m.getTextContent();
		content.replaceFirst("[\\s]", " ");
		String[] commands = content.split(" ",2);
		//the user has texted in only the name of a field, so we treat this
		//as a request for information about the field
		if(commands.length == 1){
			if(ResourceMapperProperties.getInstance().isInDebugMode()){
				System.out.println(ShortCodeProperties.getInstance().getInfoSnippetForShortCode(commands[0]));
			}else{
				frontline.sendTextMessage(m.getSenderMsisdn(), ShortCodeProperties.getInstance().getInfoSnippetForShortCode(commands[0]));
			}
		}else{
			PlainTextField mapping= mappingDao.getMappingForShortCode(commands[0]);
			HospitalContact contact = contactDao.getHospitalContactByPhoneNumber(m.getSenderMsisdn());
			PlainTextResponse response = new PlainTextResponse(m,contact,new Date(),contact.getHospitalId(),mapping);
			generateAndPublishXML(response);
		}
	}
	
	public void generateAndPublishXML(FieldResponse<PlainTextField> response){
		Document doc = XMLUtils.getInitializedDocument(response);
		String text = response.getMessage().getTextContent().split(" ", 2)[1];
		String path = response.getMapping().getPathToElement() +"=" + text;
		XMLUtils.handlePath(path, doc);
		for(String paths: response.getMapping().getAdditionalInstructions()){
			XMLUtils.handlePath(paths, doc);
		}
		XMLPublisher.publish(doc.asXML());
	}
}
