package net.frontlinesms.plugins.resourcemapper.handler.fields;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;

import org.springframework.context.ApplicationContext;

public class PlainTextHandler extends FieldMessageHandler<PlainTextField> {
	
	public PlainTextHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public boolean isSatisfiedBy(String response) {
		//TODO improve response validation for PlainTextField
		return response != null && response.length() > 0 && response.split(" ").length > 1;
	}
}
