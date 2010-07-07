package net.frontlinesms.plugins.resourcemapper.handler;

import java.util.ArrayList;
import java.util.Collection;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;

import org.springframework.context.ApplicationContext;

public class InfoHandler implements MessageHandler {
	
	private FrontlineSMS frontline;
	public InfoHandler(FrontlineSMS frontline, ApplicationContext appCon){
		this.frontline = frontline;
	}
	
	public Collection<String> getKeywords() {
		ArrayList<String> results = new ArrayList<String>();
		results.add(ShortCodeProperties.getInstance().getValueForKey("info"));
		return results;
	}

	public void handleMessage(FrontlineMessage m) {
		//if it's valid, send the requested info snippet
		if(messageIsValid(m.getTextContent())){
			output(m.getSenderMsisdn(), ShortCodeProperties.getInstance().getInfoSnippetForShortCode(m.getTextContent().split(" ")[m.getTextContent().split(" ").length -1]));
		//otherwise, send an error message
		}else{
			output(m.getSenderMsisdn(),ShortCodeProperties.getInstance().getValueForKey("info.error.message"));
		}
	}
	
	private boolean messageIsValid(String message){
		//if there are more than 2 commands in the message then the message is invalid
		if(message.split(" ").length >2){
			return false;
		//if the message contains a valid field name, then it is valid
		}else if(message.split(" ").length ==2){
			if(ShortCodeProperties.getInstance().getKeyForShortCode(message.split(" ")[1]) !=null){
				return true;
			}
		}else if(message.trim().equals(ShortCodeProperties.getInstance().getKeyForShortCode("info"))){
			return true;
		}
		//otherwise, it's false
		return false;
	}
	
	protected void output(String msisdn, String text){
		if(ResourceMapperProperties.getInstance().isInDebugMode()){
			System.out.println(text);
		}else{
			frontline.sendTextMessage(msisdn, text);
		}
	}
	
	public static void main(String[] args) {
		for(String s: "info".split(" ")){
			System.out.println("one");
			System.out.println(s);
			System.out.println("two");
		}
	}

}
