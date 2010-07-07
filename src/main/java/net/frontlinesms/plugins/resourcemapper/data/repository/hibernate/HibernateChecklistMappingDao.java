package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.ChecklistField;
import net.frontlinesms.plugins.resourcemapper.data.repository.ChecklistMappingDao;

public class HibernateChecklistMappingDao extends BaseHibernateDao<ChecklistField> implements ChecklistMappingDao {

	protected HibernateChecklistMappingDao() {
		super(ChecklistField.class);
	}

	public void deleteChecklistMapping(ChecklistField mapping) {
		super.delete(mapping);
	}

	public List<ChecklistField> getMappingForShortCode(String shortcode) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("shortCode", shortcode));
		return super.getList(c);
	}

	public void saveChecklistMapping(ChecklistField mapping) throws DuplicateKeyException {
		super.save(mapping);

	}

	public void saveChecklistMappingWithoutDuplicateHandling(ChecklistField mapping) {
		super.saveWithoutDuplicateHandling(mapping);
	}

	public void updateChecklistMapping(ChecklistField mapping) throws DuplicateKeyException {
		super.update(mapping);
	}

	public void updateChecklistMappingWithoutDuplicateHandling(ChecklistField mapping) {
		super.updateWithoutDuplicateHandling(mapping);
	}

	public List<ChecklistField> getAllChecklistMappings() {
		return super.getAll();
	}
	
	private static final String SHORT_CODES_QUERY = "select c.shortCode from PlainTextMapping c  where DTYPE='ChecklistMapping'";
	
	public List<String> getShortCodes() {
		return super.getSession().createSQLQuery(SHORT_CODES_QUERY).list();
	}


}
