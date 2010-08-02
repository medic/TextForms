package net.frontlinesms.plugins.resourcemapper.handler.fields;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.DateField;

/**
 * DateHandler
 * @author dalezak
 *
 */
public class DateHandler extends FieldMessageHandler<DateField> {
	
	@SuppressWarnings("unused")
	private static final ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(DateHandler.class);	
	
	/**
	 * DateField
	 */
	private static final DateField dateField = new DateField();
	
	/**
	 * DateHandler
	 */
	public DateHandler() {}
	
	/**
	 * Get DateField keywords
	 */
	@Override
	public Collection<String> getKeywords() {
		return this.fieldMappingDao.getKeywordsForField(dateField);
	}
	
	@Override
	protected boolean isValidResponse(String[] words) {
		return words != null && words.length == 2 && isValidDate(words[1]);
	}
	
	private boolean isValidDate(String word) {
		for (String format : new String [] {"dd/MM/yyyy", "dd-MM-yyyy", "dd/MM/yy", "dd-MM-yy"}) {
			try {
				if (word != null && word.length() > 0) {
					DateFormat dateFormat = new SimpleDateFormat(format);
					return dateFormat.parse(word) != null;	
				}
			} 
			catch (ParseException e) {
				//do nothing
			}
		}
		return false;
	}

}
