package net.frontlinesms.plugins.surveys;

import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.domain.Survey;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;

/*
 * ResourcesMapperCallback
 * @author Dale Zak
 */
@SuppressWarnings("unchecked")
public interface SurveysCallback {
	public void viewAnswers(HospitalContact contact);
	public void viewAnswers(Question question);
	public void refreshContact(HospitalContact contact);
	public void refreshQuestion(Question question);
	public void refreshAnswer(Answer answer);
	public void refreshSurvey(Survey survey);
}