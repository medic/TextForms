package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.BooleanField;
import net.frontlinesms.plugins.resourcemapper.data.repository.BooleanMappingDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateBooleanMappingDao extends BaseHibernateDao<BooleanField> implements BooleanMappingDao {
	
	protected HibernateBooleanMappingDao() {
		super(BooleanField.class);
	}

	public void deleteBooleanMapping(BooleanField mapping) {
		super.delete(mapping);
	}

	public BooleanField getMappingForShortCode(String shortcode) {
		DetachedCriteria c = super.getCriterion();
		String code = ShortCodeProperties.getInstance().getKeyForShortCode(shortcode);
		c.add(Restrictions.eq("shortCode", code));
		return super.getUnique(c);
	}

	public void saveBooleanMapping(BooleanField mapping) throws DuplicateKeyException {
		super.save(mapping);
	}

	public void saveBooleanMappingWithoutDuplicateHandling(BooleanField mapping) {
		super.saveWithoutDuplicateHandling(mapping);
	}

	public void updateBooleanMapping(BooleanField mapping) throws DuplicateKeyException {
		super.update(mapping);
	}

	public void updateBooleanMappingWithoutDuplicateHandling(BooleanField mapping) {
		super.updateWithoutDuplicateHandling(mapping);
	}

	public List<BooleanField> getAllBooleanMappings() {
		return super.getAll();
	}

	private static final String SHORT_CODES_QUERY = "select b.shortCode from PlainTextMapping b  where DTYPE='BooleanMapping'";
	
	public List<String> getShortCodes() {
		return super.getSession().createSQLQuery(SHORT_CODES_QUERY).list();
	}

}
