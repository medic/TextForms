package net.frontlinesms.plugins.surveys.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
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
	
	public List<Answer> getAnswersForOrganizationId(String organizationId) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("organizationId", organizationId));
		return super.getList(c);
	}
	
	public List<Answer> getAnswersForContact(Contact contact) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("contact", contact));
		return super.getList(c);
	}
	
	public List<Answer> getAnswers(Question question, Contact contact) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("question", question));
		c.add(Restrictions.eq("contact", contact));
		return super.getList(c);
	}
	
}
