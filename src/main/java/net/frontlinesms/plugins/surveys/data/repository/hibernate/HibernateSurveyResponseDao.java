package net.frontlinesms.plugins.surveys.data.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.data.domain.SurveyResponse;
import net.frontlinesms.plugins.surveys.data.repository.SurveyResponseDao;

public class HibernateSurveyResponseDao extends BaseHibernateDao<SurveyResponse>  implements SurveyResponseDao {

	@SuppressWarnings("unused")
	private static final SurveysLogger LOG = SurveysLogger.getLogger(HibernateSurveyResponseDao.class);
	
	protected HibernateSurveyResponseDao() {
		super(SurveyResponse.class);
	}

	public void deleteSurvey(SurveyResponse surveyResponse) {
		super.delete(surveyResponse);
	}

	public List<SurveyResponse> getAllSurveyResponses() {
		return super.getAll();
	}

	public List<SurveyResponse> getAllSurveyResponses(int startIndex, int limit) {
		return super.getAll(startIndex, limit);
	}

	public SurveyResponse getSurveyResponseByContact(Contact contact) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(SurveyResponse.FIELD_CONTACT, contact));
		return super.getUnique(criteria);
	}

	public void saveSurvey(SurveyResponse surveyResponse) throws DuplicateKeyException {
		super.save(surveyResponse);
	}

	public void updateSurvey(SurveyResponse surveyResponse) throws DuplicateKeyException {
		super.update(surveyResponse);
	}
	
}