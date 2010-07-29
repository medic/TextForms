package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.MultiChoiceField;

/**
 * MultiChoiceHandler
 * @author dalezak
 *
 */
public class MultiChoiceHandler extends CodedHandler<MultiChoiceField> {
	
	@SuppressWarnings("unused")
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(MultiChoiceHandler.class);
	
	/**
	 * MultiChoiceField
	 */
	private static final MultiChoiceField multiChoiceField = new MultiChoiceField();

	/**
	 * MultiChoiceHandler
	 */
	public MultiChoiceHandler() {}
	
	@Override
	public Collection<String> getKeywords() {
		return this.mappingDao.getKeywordsForField(multiChoiceField);
	}
	
	@Override
	protected boolean isValidResponse(String[] words) {
		if (words != null && words.length > 1) {
			Field field = this.mappingDao.getFieldForKeyword(words[0]);
			if (field != null) {
				List<String> choices = field.getChoices();
				return isValidInteger(choices, words[1]) || isValidString(choices, words[1]);
			}
		}
		return false;
	}
	
	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		Field field = this.callbacks.get(message.getSenderMsisdn());
		if (field != null) {
			List<String> choices = field.getChoices();
			String[] words = this.toWords(message.getTextContent(), 2);
			if (words != null && words.length == 1 && (isValidInteger(choices, words[0]) || isValidString(choices, words[0]))) {
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

}
