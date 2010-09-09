package net.frontlinesms.plugins.surveys.data.domain.answers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.domain.questions.ChecklistQuestion;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;

@Entity
public class ChecklistAnswer extends CodedAnswer<ChecklistQuestion> {

	public ChecklistAnswer() {
		super();
	}

	public ChecklistAnswer(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, ChecklistQuestion question) {
		super(message, submitter, dateSubmitted, hospitalId, question);
	}
	
	@Override
	public boolean isAnswerFor(Question question) {
		return question.getClass() == ChecklistQuestion.class;
	}
	
	@Override
	public String getAnswerValue() {
		String[] words = this.toWords(2);
		if (words != null && words.length == 1) {
			return toChecklistString(words[0]);
		}
		else if (words != null && words.length == 2) {
			return toChecklistString(words[1]);
		}
		return null;
	}
	
	private String toChecklistString(String words) {
		List<String> results = new ArrayList<String>();
		//TODO improve this if-else logic
		if (words.indexOf(",") > -1) {
			for (String answer : words.split(",")) {
				if (answer.indexOf("-") > -1) {
					List<String> range = toChoiceRange(answer);
					if (range != null && range.size() > 0) {
						results.addAll(range);
					}
				}
				else {
					String value = toMultiChoiceString(answer);
					if (value != null) {
						results.add(value);
					}
				}
			}	
		}
		else if (words.indexOf("-") > -1) {
			List<String> range = toChoiceRange(words);
			if (range != null && range.size() > 0) {
				results.addAll(range);
			}
		}
		else {
			String value = toMultiChoiceString(words);
			if (value != null) {
				results.add(value);
			}
		}
		if (results.size() > 0) {
			return toString(results);
		}
		return null;
	}
	
	private List<String> toChoiceRange(String word) {
		String[] words = word.split("-");
		if (words.length == 2) {
			if (this.isValidInteger(words[0]) && this.isValidInteger(words[1])) {
				List<String> results = new ArrayList<String>();
				List<String> choices = this.getQuestion().getChoices();
				int startRange = Integer.parseInt(words[0]) - 1;
				int endRange = Integer.parseInt(words[1]) - 1;
				for (int index = startRange; index <= endRange; index++) {
					results.add(choices.get(index));
				}
				return results;
			}	
		}
		return null;
	}
	
	private String toMultiChoiceString(String word) {
		if (word != null) {
			List<String> choices = this.getQuestion().getChoices();
			if (this.isValidInteger(word.trim())) {
				int index = Integer.parseInt(word.trim());
				if (index > 0 && index <= choices.size()) {
					return choices.get(index - 1);
				}
			}
			else {
				for (String choice : choices) {
					if (choice.trim().equalsIgnoreCase(word.trim())) {
						return choice;
					}
				}
			}	
		}
		return null;
	}
	
}
