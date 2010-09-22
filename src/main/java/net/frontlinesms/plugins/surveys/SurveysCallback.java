package net.frontlinesms.plugins.surveys;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.plugins.surveys.data.domain.Survey;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;

/*
 * ResourcesMapperCallback
 * @author Dale Zak
 */
@SuppressWarnings("unchecked")
public interface SurveysCallback {
	public void viewAnswers(Contact contact);
	public void viewAnswers(Question question);
	public void refreshContact(Contact contact);
	public void refreshQuestion(Question question);
	public void refreshAnswer(Answer answer);
	public void refreshSurvey(Survey survey);
}