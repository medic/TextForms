package net.frontlinesms.plugins.surveys.data.repository;

import java.util.List;

import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;

public interface AnswerDao {

	/**
	 * Get all Answers
	 * @return collection of Answers
	 */
	@SuppressWarnings("unchecked")
	public List<Answer> getAllAnswers();
	
	/**
	 * Deletes a question response from the system
	 * @param response the question response to delete
	 */
	@SuppressWarnings("unchecked")
	public void deleteAnswer(Answer response);
	
	/**
	 * Saves a question response to the system
	 * @param response the question response to save
	 */
	@SuppressWarnings("unchecked")
	public void saveAnswer(Answer response);
	
	/**
	 * Updates a question response's details in the data source
	 * @param response the question response whose details should be updated
	 */
	@SuppressWarnings("unchecked")
	public void updateAnswer(Answer response);
	
	/**
	 * Get all Answers for a given Question apping
	 * @param question Question
	 * @return collection of Answers
	 */
	@SuppressWarnings("unchecked")
	public List<Answer> getAnswersForQuestion(Question question);
	
	/**
	 * Get all Answers for a given Hospital ID
	 * @param hospitalId Hospital ID
	 * @return collection of Answers
	 */
	@SuppressWarnings("unchecked")
	public List<Answer> getAnswersForHospitalId(String hospitalId);
	
	/**
	 * Get all Answers for given submitter HospitalContact
	 * @param contact HospitalContact
	 * @return collection of Answers
	 */
	@SuppressWarnings("unchecked")
	public List<Answer> getAnswersForSubmitter(HospitalContact contact);
}
