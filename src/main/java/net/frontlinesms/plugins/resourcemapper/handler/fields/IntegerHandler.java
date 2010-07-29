package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.IntegerField;

/**
 * IntegerHandler
 * @author dalezak
 *
 */
public class IntegerHandler extends FieldMessageHandler<IntegerField> {
	
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

	@Override
	public Collection<String> getKeywords() {
		return this.mappingDao.getKeywordsForField(integerField);
	}
	
	@Override
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length == 2 && isValidInteger(words[1]);
	}

}
