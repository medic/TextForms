package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.MultiChoiceField;

public class MultiChoiceHandler extends FieldMessageHandler<MultiChoiceField> {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(MultiChoiceHandler.class);
	
	private final MultiChoiceField multiChoiceField = new MultiChoiceField();
	
	public MultiChoiceHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public Collection<String> getKeywords() {
		return this.mappingDao.getAbbreviationsForField(multiChoiceField);
	}
	
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length > 1 && (isValidInteger(words[1]) || isValidOption(words[0], words[1]));
	}
	
	private boolean isValidInteger(String word) {
		try {
			Integer.parseInt(word);
			return true;
		} 
		catch (NumberFormatException nfe) {
		
		}
		return false;
	}
	
	private boolean isValidOption(String keyword, String word) {
		Field field = this.mappingDao.getFieldForAbbreviation(keyword);
		if (field != null) {
			for (String choice : field.getChoices()) {
				//TODO improve fuzzy string comparison logic
				if (choice.equalsIgnoreCase(word) || 
					choice.toLowerCase().startsWith(word.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
}
