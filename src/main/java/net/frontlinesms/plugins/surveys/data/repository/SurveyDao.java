package net.frontlinesms.plugins.surveys.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.surveys.data.domain.Survey;

/**
 * SurveyDao
 * @author dalezak
 *
 */
public interface SurveyDao {
	public List<Survey> getAllSurveys();
	
	public List<Survey> getAllSurveys(int startIndex, int limit);
	
	public void deleteSurvey(Survey survey);
	
	public void saveSurvey(Survey survey) throws DuplicateKeyException;
	
	public void updateSurvey(Survey survey) throws DuplicateKeyException;
	
	public void saveSurveyWithoutDuplicateHandling(Survey survey);
	
	public void updateSurveyWithoutDuplicateHandling(Survey survey);
}