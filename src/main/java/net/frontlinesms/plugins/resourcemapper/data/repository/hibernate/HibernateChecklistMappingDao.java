package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.ChecklistMapping;
import net.frontlinesms.plugins.resourcemapper.data.repository.ChecklistMappingDao;

public class HibernateChecklistMappingDao extends BaseHibernateDao<ChecklistMapping> implements ChecklistMappingDao {

	protected HibernateChecklistMappingDao() {
		super(ChecklistMapping.class);
	}

	public void deleteChecklistMapping(ChecklistMapping mapping) {
		super.delete(mapping);
	}

	public List<ChecklistMapping> getMappingForShortCode(String shortcode) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("shortCode", shortcode));
		return super.getList(c);
	}

	public void saveChecklistMapping(ChecklistMapping mapping) throws DuplicateKeyException {
		super.save(mapping);

	}

	public void saveChecklistMappingWithoutDuplicateHandling(ChecklistMapping mapping) {
		super.saveWithoutDuplicateHandling(mapping);
	}

	public void updateChecklistMapping(ChecklistMapping mapping) throws DuplicateKeyException {
		super.update(mapping);
	}

	public void updateChecklistMappingWithoutDuplicateHandling(ChecklistMapping mapping) {
		super.updateWithoutDuplicateHandling(mapping);
	}

	public List<ChecklistMapping> getAllChecklistMappings() {
		return super.getAll();
	}
	
	private static final String SHORT_CODES_QUERY = "select c.shortCode from PlainTextMapping c  where DTYPE='ChecklistMapping'";
	
	public List<String> getShortCodes() {
		return super.getSession().createSQLQuery(SHORT_CODES_QUERY).list();
	}


}
