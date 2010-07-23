package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.IntegerField;

import org.springframework.context.ApplicationContext;

public class IntegerHandler extends FieldMessageHandler<IntegerField> {
	
	@SuppressWarnings("unused")
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(IntegerHandler.class);
	
	private static final IntegerField integerField = new IntegerField();
	
	public IntegerHandler() {
		super(null, null);
	}
	
	public IntegerHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public Collection<String> getKeywords() {
		return this.mappingDao.getKeywordsForField(integerField);
	}
	
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length == 2 && isValidInteger(words[1]);
	}

}
