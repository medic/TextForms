package net.frontlinesms.plugins.textforms.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;

public interface QuestionDao {

	/**
	 * Get all questions
	 * @return
	 */
	public List<Question> getAllQuestions();
	
	/**
	 * Deletes a question question from the system
	 * @param question the question question to delete
	 */
	public void deleteQuestion(Question question);
	
	/**
	 * Saves a question question to the system
	 * @param question the question question to save
	 * @throws DuplicateKeyException if the question question's phone number is already in use by another plaintext question 
	 */
	public void saveQuestion(Question question) throws DuplicateKeyException;
	
	/**
	 * Updates a question question's details in the data source
	 * @param question the question question whose details should be updated
	 * @throws DuplicateKeyException if the question question's phone number is already in use by another plaintext question
	 */
	public void updateQuestion(Question question) throws DuplicateKeyException;
	
	/**
	 * Saves a question question to the system
	 * @param question the question question to save
	 * @throws DuplicateKeyException if the question question's phone number is already in use by another plaintext question 
	 */
	public void saveQuestionWithoutDuplicateHandling(Question question);
	
	/**
	 * Updates a question question's details in the data source
	 * @param question the question question whose details should be updated
	 * @throws DuplicateKeyException if the question question's phone number is already in use by another plaintext question
	 */
	public void updateQuestionWithoutDuplicateHandling(Question question);
	

	/**
	 * Get all keywords
	 * @return collection of keywords
	 */
	public List<String> getKeywords();
	
	/**
	 * Get keyword for given Question
	 * @param questionType Question type
	 * @return collection of keywords
	 */
	public List<String> getKeywordsForQuestion(String questionType);
	
	/**
	 * Get Question for associated keyword 
	 * @param keyword keyword
	 * @return Question
	 */
	public Question getQuestionForKeyword(String keyword);
	
}
