package net.frontlinesms.plugins.resourcemapper.handler.fields;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.ChecklistField;

public class ChecklistHandler extends FieldMessageHandler<ChecklistField> {
	
	public ChecklistHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public boolean isSatisfiedBy(String response) {
		//TODO improve response validation for ChecklistField
		return response != null && response.length() > 0;
	}
	
}
