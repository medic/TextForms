package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.IntegerField;

/**
 * IntegerHandler
 * @author dalezak
 *
 */
public class IntegerHandler extends CallbackHandler<IntegerField> {
	
	@SuppressWarnings("unused")
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(IntegerHandler.class);
	
	/**
	 * IntegerField
	 */
	private static final IntegerField integerField = new IntegerField();
	
	/**
	 * IntegerHandler
	 */
	public IntegerHandler() {}

	/**
	 * Get IntegerField keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.fieldMappingDao.getKeywordsForField(integerField);
	}
	
	@Override
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length == 2 && isValidInteger(words[1]);
	}

	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		String[] words = this.toWords(message.getTextContent(), 2);
		return words != null && words.length == 1 && isValidInteger(words[0]);
	}

}
