package net.frontlinesms.plugins.textforms.data.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.TextFormResponse;
import net.frontlinesms.plugins.textforms.data.repository.TextFormResponseDao;

public class HibernateTextFormResponseDao extends BaseHibernateDao<TextFormResponse>  implements TextFormResponseDao {

	@SuppressWarnings("unused")
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(HibernateTextFormResponseDao.class);
	
	protected HibernateTextFormResponseDao() {
		super(TextFormResponse.class);
	}

	public void deleteTextForm(TextFormResponse textformResponse) {
		super.delete(textformResponse);
	}

	public List<TextFormResponse> getAllTextFormResponses() {
		return super.getAll();
	}

	public List<TextFormResponse> getAllTextFormResponses(int startIndex, int limit) {
		return super.getAll(startIndex, limit);
	}

	public TextFormResponse getTextFormResponseByContact(Contact contact) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(TextFormResponse.FIELD_CONTACT, contact));
		return super.getUnique(criteria);
	}

	public void saveTextForm(TextFormResponse textformResponse) throws DuplicateKeyException {
		super.save(textformResponse);
	}

	public void updateTextForm(TextFormResponse textformResponse) throws DuplicateKeyException {
		super.update(textformResponse);
	}
	
}