package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.ChecklistField;

public class ChecklistHandler extends FieldMessageHandler<ChecklistField> {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ChecklistHandler.class);
	
	private final ChecklistField checklistField = new ChecklistField();
	
	public ChecklistHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public Collection<String> getKeywords() {
		return this.mappingDao.getAbbreviationsForField(checklistField);
	}
	
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length > 1 && isValidOptions(words[0], words[1]);
	}
	
	private boolean isValidOptions(String keyword, String word) {
		//TODO implement validation for ChecklistField
		return false;
	}

}
