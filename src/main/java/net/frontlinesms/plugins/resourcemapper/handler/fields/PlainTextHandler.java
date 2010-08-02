package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;

/**
 * PlainTextHandler
 * @author dalezak
 *
 */
public class PlainTextHandler extends FieldMessageHandler<PlainTextField> {
	
	@SuppressWarnings("unused")
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(PlainTextHandler.class);
	
	/**
	 * PlainTextField
	 */
	private static final PlainTextField plainTextField = new PlainTextField();
	
	/**
	 * PlainTextHandler
	 */
	public PlainTextHandler() {}
	
	/**
	 * Get PlainTextField keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.fieldMappingDao.getKeywordsForField(plainTextField);
	}
	
	@Override
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length > 1 && isValidString(words[1]);
	}

	private boolean isValidString(String word) {
		return word != null && word.trim().length() > 0;
	}
}
