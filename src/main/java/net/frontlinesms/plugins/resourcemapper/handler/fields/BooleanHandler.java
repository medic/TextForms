package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.BooleanField;

import org.springframework.context.ApplicationContext;

public class BooleanHandler extends CodedHandler<BooleanField> {
	
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(BooleanHandler.class);
	
	private static final BooleanField booleanField = new BooleanField();
	
	public BooleanHandler() {
		super(null, null);
	}
	
	public BooleanHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public Collection<String> getKeywords() {
		return this.mappingDao.getKeywordsForField(booleanField);
	}
	
	@Override
	public boolean shouldHandleCallbackMessage(FrontlineMessage message) {
		String[] words = message.getTextContent().replaceFirst("[\\s]", " ").split(" ", 2);
		return words != null && words.length == 1 && isValidBoolean(words[0]);
	}
	
	@Override
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length == 2 && isValidBoolean(words[1]);
	}
	
	private boolean isValidBoolean(String word) {
		if (word != null) {
			for (String possible : ResourceMapperProperties.getBooleanValues()) {
				if (possible.trim().equalsIgnoreCase(word.trim())) {
					return true;
				}
			}	
		}
		LOG.error("Invalid Boolean Value: %s", word);
		return false;
	}

}
