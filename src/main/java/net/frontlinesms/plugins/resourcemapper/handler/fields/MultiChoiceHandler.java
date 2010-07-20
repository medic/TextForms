package net.frontlinesms.plugins.resourcemapper.handler.fields;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.MultiChoiceField;

public class MultiChoiceHandler extends FieldMessageHandler<MultiChoiceField> {
	
	public MultiChoiceHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public boolean isSatisfiedBy(String response) {
		//TODO improve response validation for MultiChoiceField
		return response != null && response.length() > 0;
	}
}
