package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;

import org.springframework.context.ApplicationContext;

public class PlainTextHandler extends FieldMessageHandler<PlainTextField> {
	
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(PlainTextHandler.class);
	
	private static final PlainTextField plainTextField = new PlainTextField();
	
	public PlainTextHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public Collection<String> getKeywords() {
		return this.mappingDao.getAbbreviationsForField(plainTextField);
	}
	
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length > 1 && isValidString(words[1]);
	}

	private boolean isValidString(String word) {
		return word != null && word.trim().length() > 0;
	}
}
