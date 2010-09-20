package net.frontlinesms.plugins.surveys.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.plugins.surveys.data.domain.SurveyResponse;

/**
 * SurveyResponseDao
 * @author dalezak
 *
 */
public interface SurveyResponseDao {
	public List<SurveyResponse> getAllSurveyResponses();
	
	public List<SurveyResponse> getAllSurveyResponses(int startIndex, int limit);
	
	public void deleteSurvey(SurveyResponse surveyResponse);
	
	public void saveSurvey(SurveyResponse surveyResponse) throws DuplicateKeyException;
	
	public void updateSurvey(SurveyResponse surveyResponse) throws DuplicateKeyException;
	
	public SurveyResponse getSurveyResponseByContact(Contact contact);
}