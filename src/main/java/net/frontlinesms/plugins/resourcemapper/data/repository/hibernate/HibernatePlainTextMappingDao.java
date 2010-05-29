package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextMapping;
import net.frontlinesms.plugins.resourcemapper.data.repository.PlainTextMappingDao;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernatePlainTextMappingDao extends BaseHibernateDao<PlainTextMapping> implements PlainTextMappingDao {

	protected HibernatePlainTextMappingDao() {
		super(PlainTextMapping.class);
	}
	
	private static final String MAPPING_FOR_SHORT_CODE = "select * from PlainTextMapping p where p.DTYPE='PlainTextMapping' and p.shortCode=";
	
	public PlainTextMapping getMappingForShortCode(String shortcode) {
		String code = ShortCodeProperties.getInstance().getKeyForShortCode(shortcode);
		Query q = super.getSession().createQuery(MAPPING_FOR_SHORT_CODE+"'"+code+"'");
		return (PlainTextMapping) q.uniqueResult();
	}

	public void savePlainTextMapping(PlainTextMapping mapping) throws DuplicateKeyException {
		super.save(mapping);
	}

	public void savePlainTextMappingWithoutDuplicateHandling(PlainTextMapping mapping) {
		super.saveWithoutDuplicateHandling(mapping);
	}

	public void updatePlainTextMapping(PlainTextMapping mapping) throws DuplicateKeyException {
		super.update(mapping);
	}

	public void updatePlainTextMappingWithoutDuplicateHandling( PlainTextMapping mapping) {
		super.updateWithoutDuplicateHandling(mapping);

	}

	public void deletePlainTextMapping(PlainTextMapping mapping) {
		super.delete(mapping);
	}

	public List<PlainTextMapping> getAllPlainTextMappings() {
		return super.getAll();
	}
	
	private static final String SHORT_CODES_QUERY = "select p.shortCode from PlainTextMapping p where DTYPE='PlainTextMapping'";
	
	@SuppressWarnings("unchecked")
	public List<String> getShortCodes() {
		List<String> results =super.getSession().createSQLQuery(SHORT_CODES_QUERY).list();
		return results;
	}

	private static final String ALL_SHORT_CODES_QUERY = "select p.shortCode from PlainTextMapping";
	
	@SuppressWarnings("unchecked")
	public List<String> getAllShortCodes() {
		return super.getSession().createQuery(ALL_SHORT_CODES_QUERY).list();
	}

	public PlainTextMapping searchAllMappingsForForCode(String shortcode) {
		DetachedCriteria c = super.getCriterion();
		String code = ShortCodeProperties.getInstance().getKeyForShortCode(shortcode);
		c.add(Restrictions.eq("shortCode", code));
		return super.getUnique(c);
	}
}
