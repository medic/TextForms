package net.frontlinesms.plugins.resourcemapper.handler.fields;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.BooleanField;

import org.springframework.context.ApplicationContext;

public class BooleanHandler extends FieldMessageHandler<BooleanField> {
	
	public BooleanHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public boolean isSatisfiedBy(String response) {
		if (response.split(" ").length == 1) {
			return true;
		}
		else if (response.split(" ").length > 2) {
			return false;
		}
		String body = response.split(" ")[1];
		for (String str : ShortCodeProperties.getInstance().getValueForKey("yes").split(",")) {
			if (body.equalsIgnoreCase(str)) {
				return true;
			}
		}
		for (String str : ShortCodeProperties.getInstance().getValueForKey("no").split(",")) {
			if (body.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
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
	public String getResponseForContent(String content) {
		String[] responses = content.split(" ", 2);
		String response = responses[1];
		boolean trueResponse = false;
		for (String str:ShortCodeProperties.getInstance().getValueForKey("yes").split(",")) {
			if (response.equalsIgnoreCase(str)) {
				trueResponse = true;
			}
		}
		return trueResponse ? "True" : "False";
	}

}
