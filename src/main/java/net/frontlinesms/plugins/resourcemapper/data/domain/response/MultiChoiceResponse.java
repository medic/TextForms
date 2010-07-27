package net.frontlinesms.plugins.resourcemapper.data.domain.response;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.MultiChoiceField;

@Entity
public class MultiChoiceResponse extends CodedResponse<MultiChoiceField> {

	public MultiChoiceResponse() {
		super();
	}

	public MultiChoiceResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, MultiChoiceField mapping) {
		super(message, submitter, dateSubmitted, hospitalId, mapping);
	}
	
	@Override
	public boolean isResponseFor(Field field) {
		return field.getClass() == MultiChoiceField.class;
	}
	
	@Override
	public String getResponseValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return toMultiChoiceString(words[0]);
		}
		else if (words != null && words.length == 2) {
			return toMultiChoiceString(words[1]);
		}
		return null;
	}
	
	private String toMultiChoiceString(String word) {
		if (this.isValidInteger(word)) {
			int index = Integer.parseInt(word);
			if (index > 0 && index <= this.mapping.getChoices().size()) {
				return this.mapping.getChoices().get(index - 1);
			}
		}
		else {
			for (String choice : this.mapping.getChoices()) {
				if (choice.equalsIgnoreCase(word)) {
					return choice;
				}
			}
		}
		return null;
	}
}
