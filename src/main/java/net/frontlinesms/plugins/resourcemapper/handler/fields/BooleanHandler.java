package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.util.Collection;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.BooleanField;

import org.springframework.context.ApplicationContext;

public class BooleanHandler extends FieldMessageHandler<BooleanField> {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(BooleanHandler.class);
	
	private final BooleanField field = new BooleanField();
	
	public BooleanHandler(FrontlineSMS frontline, ApplicationContext appContext) {
		super(frontline, appContext);
	}

	public Collection<String> getKeywords() {
		return this.mappingDao.getAbbreviationsForField(field);
	}
	
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length > 1 && isValidBoolean(words[1]);
	}
	
	private boolean isValidBoolean(String word) {
		//TODO move these values into properties file
		for (String possible : new String [] {"yes", "y", "true", "t", "1", "no", "n", "false", "f", "0"}) {
			if (possible.equalsIgnoreCase(word)) {
				return true;
			}
		}
		return false;
	}

}
