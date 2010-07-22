package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;
import java.util.List;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.ChecklistField;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

public class ChecklistHandler extends CodedHandler<ChecklistField> {
	
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ChecklistHandler.class);
	
	private static final ChecklistField checklistField = new ChecklistField();
	
	public ChecklistHandler() {
		super(null, null);
	}
	
	public ChecklistHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public Collection<String> getKeywords() {
		return this.mappingDao.getAbbreviationsForField(checklistField);
	}
	
	@Override
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length > 1 && areValidChoices(words[0], words[1]);
	}
	
	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		Field field = this.callbacks.get(message.getSenderMsisdn());
		if (field != null) {
			String[] words = message.getTextContent().replaceFirst("[\\s]", " ").split(" ", 2);
			return words != null && words.length == 1 && areValidChoices(field.getAbbreviation(), words[0]);
		}
		return false;
	}
	
	private boolean areValidChoices(String keyword, String words) {
		Field field = this.mappingDao.getFieldForAbbreviation(keyword);
		if (field != null) {
			List<String> choices = field.getChoices();
			LOG.debug("keyword:%s words:%s choices:%s", keyword, words, choices);
			if (words.indexOf(",") > -1) {
				for (String answer : words.split(",")) {
					if (isValidInteger(choices, answer)) {
						//valid integer
					}
					else if (answer.indexOf("-") > -1) {
						for (String subAnswer : answer.split("-")) {
							if (isValidInteger(choices, subAnswer)) {
								//valid integer
							}
							else if (isValidString(choices, subAnswer)) {
								//valid string choice
							}
							else {
								LOG.error("Invalid: %s", subAnswer);
								return false;
							}
						}
					}
					else if (isValidString(choices, answer)) {
						//valid string choice
					}
					else {
						LOG.error("Invalid: %s", answer);
						return false;
					}
				}	
			}
			else if (isValidInteger(choices, words)) {
				//valid integer
			}
			else if (isValidString(choices, words)) {
				//valid string choice
			}	
			else {
				LOG.error("Invalid: %s", words);
				return false;
			}
			return true;
		}
		return false;
	}
	
}
