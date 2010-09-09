package net.frontlinesms.plugins.surveys.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.plugins.surveys.data.repository.AnswerDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

@SuppressWarnings("unchecked")
public class HibernateAnswerDao extends BaseHibernateDao<Answer> implements AnswerDao {

	protected HibernateAnswerDao() {
		super(Answer.class);
	}

	public void deleteAnswer(Answer response) {
		super.delete(response);
	}

	public List<Answer> getAllAnswers() {
		return super.getAll();
	}

	public void saveAnswer(Answer response) {
		super.saveWithoutDuplicateHandling(response);
	}

	public void saveAnswerWithoutDuplicateHandling(Answer response) {
		super.saveWithoutDuplicateHandling(response);
	}

	public void updateAnswer(Answer response) {
		super.updateWithoutDuplicateHandling(response);
	}

	public List<Answer> getAnswersForQuestion(Question question) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("question", question));
		return super.getList(c);
	}
	
	public List<Answer> getAnswersForHospitalId(String hospitalId) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("hospitalId", hospitalId));
		return super.getList(c);
	}
	
	public List<Answer> getAnswersForSubmitter(HospitalContact contact) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("submitter", contact));
		return super.getList(c);
	}
	
	public List<Answer> getAnswers(Question question, HospitalContact contact) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("question", question));
		c.add(Restrictions.eq("submitter", contact));
		return super.getList(c);
	}
	
}
