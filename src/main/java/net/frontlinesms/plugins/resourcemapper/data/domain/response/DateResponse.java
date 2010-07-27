package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.DateField;

@Entity
public class DateResponse extends FieldResponse<DateField> {

	public DateResponse() {
		super();
	}

	public DateResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, DateField mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
	}

	@Override
	public boolean isResponseFor(Field field) {
		return field.getClass() == DateField.class;
	}

	@Override
	public String getResponseValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return toDateString(words[0]);
		}
		else if (words != null && words.length == 2) {
			return toDateString(words[1]);
		}
		return null;
	}
	
	private String toDateString(String word) {
		for (String format : new String [] {"dd/MM/yyyy", "dd-MM-yyyy", "dd/MM/yy", "dd-MM-yy"}) {
			try {
				if (word != null && word.length() > 0) {
					DateFormat inFormat = new SimpleDateFormat(format);
					Date date = inFormat.parse(word);	
					DateFormat outFormat = new SimpleDateFormat("dd/MM/yyyy");
					return outFormat.format(date);
				}
			} 
			catch (ParseException e) {
				//do nothing
			}
		}
		return null;
	}
}
