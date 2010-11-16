package net.frontlinesms.plugins.textforms.data.repository.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;

public class HibernateQuestionDao extends BaseHibernateDao<Question> implements QuestionDao {

	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(HibernateQuestionDao.class);
	
	protected HibernateQuestionDao() {
		super(Question.class);
	}
	
	public void saveQuestion(Question question) throws DuplicateKeyException {
		try {
			super.save(question);
		}
		catch (Exception ex) {
			LOG.error("Exception: %s", ex);
		}
	}

	public void saveQuestionWithoutDuplicateHandling(Question question) {
		super.saveWithoutDuplicateHandling(question);
	}

	public void updateQuestion(Question question) throws DuplicateKeyException {
		super.update(question);
	}

	public void updateQuestionWithoutDuplicateHandling(Question question) {
		super.updateWithoutDuplicateHandling(question);
	}

	public void deleteQuestion(Question question) {
		super.delete(question);
	}

	public List<Question> getAllQuestions() {
		return super.getAll();
	}

	public List<String> getKeywords() {
		List<String> keywords = new ArrayList<String>();
		for (Question question : super.getAll()) {
			keywords.add(question.getKeyword());
		}
		return keywords;
	}
	
	public List<String> getKeywordsForQuestion(String questionType) {
		List<String> keywords = new ArrayList<String>();
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq("class", questionType));
		for (Question f : super.getList(criteria)) {
			keywords.add(f.getKeyword());
		}
		return keywords;
	}
	
	public Question getQuestionForKeyword(String keyword) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq("keyword", keyword.trim()));
		return super.getUnique(criteria);
	}
	
}
