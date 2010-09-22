package net.frontlinesms.plugins.surveys.data.domain.answers;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.data.domain.questions.CodedQuestion;

@Entity
public abstract class CodedAnswer<M extends CodedQuestion> extends Answer<M> {

	public CodedAnswer() {
		super();
	}

	public CodedAnswer(FrontlineMessage message, Contact contact, Date dateSubmitted, String organizationId, M question) {
		super(message, contact, dateSubmitted, organizationId, question);
	}

}
