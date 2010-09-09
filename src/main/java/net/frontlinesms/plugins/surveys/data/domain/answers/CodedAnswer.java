package net.frontlinesms.plugins.surveys.data.domain.answers;

import java.util.Date;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.domain.questions.CodedQuestion;

@Entity
public abstract class CodedAnswer<M extends CodedQuestion> extends Answer<M> {

	public CodedAnswer() {
		super();
	}

	public CodedAnswer(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, M question) {
		super(message, submitter, dateSubmitted, hospitalId, question);
	}

}
