package net.frontlinesms.plugins.textforms;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;

/*
 * ResourcesMapperCallback
 * @author Dale Zak
 */
@SuppressWarnings("unchecked")
public interface TextFormsCallback {
	public void viewAnswers(Contact contact);
	public void viewAnswers(Question question);
	public void refreshContact(Contact contact);
	public void refreshQuestion(Question question);
	public void refreshAnswer(Answer answer);
	public void refreshTextForm(TextForm textform);
}