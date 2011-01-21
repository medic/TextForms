package net.frontlinesms.plugins.textforms.data.repository.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.plugins.textforms.data.repository.TextFormDao;

/**
 * HibernateTextFormDao
 * @author dalezak
 *
 */
public class HibernateTextFormDao extends BaseHibernateDao<TextForm> implements TextFormDao {

	@SuppressWarnings("unused")
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(HibernateTextFormDao.class);
	
	protected HibernateTextFormDao() {
		super(TextForm.class);
	}

	public List<TextForm> getAllTextForms() {
		return super.getAll();
	}
	
	public List<TextForm> getAllTextForms(int startIndex, int limit) {
		return super.getAll(startIndex, limit);
	}

	public void deleteTextForm(TextForm textform) {
		super.delete(textform);
	}
	
	public void saveTextForm(TextForm textform) throws DuplicateKeyException {
		super.save(textform);
	}

	public void saveTextFormWithoutDuplicateHandling(TextForm textform) {
		super.saveWithoutDuplicateHandling(textform);
	}

	public void updateTextForm(TextForm textform) throws DuplicateKeyException {
		super.update(textform);
	}

	public void updateTextFormWithoutDuplicateHandling(TextForm textform) {
		super.updateWithoutDuplicateHandling(textform);
	}
	
	public List<String> getKeywords() {
		List<String> keywords = new ArrayList<String>();
		for (TextForm textform : super.getAll()) {
			keywords.add(textform.getKeyword());
		}
		return keywords;
	}

	public TextForm getTextFormByKeyword(String keyword) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(TextForm.FIELD_KEYWORD, keyword.toLowerCase()));
		return super.getUnique(criteria);		
	}
}