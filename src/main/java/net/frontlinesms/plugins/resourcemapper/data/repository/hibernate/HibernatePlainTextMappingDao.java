package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;
import net.frontlinesms.plugins.resourcemapper.data.repository.PlainTextMappingDao;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernatePlainTextMappingDao extends BaseHibernateDao<PlainTextField> implements PlainTextMappingDao {

	protected HibernatePlainTextMappingDao() {
		super(PlainTextField.class);
	}
	
	private static final String MAPPING_FOR_SHORT_CODE = "from PlainTextMapping p where p.class=PlainTextMapping and p.shortCode=";
	
	public PlainTextField getMappingForShortCode(String shortcode) {
		String code = ShortCodeProperties.getInstance().getKeyForShortCode(shortcode);
		Query q = super.getSession().createQuery(MAPPING_FOR_SHORT_CODE+"'"+code+"'");
		return (PlainTextField) q.uniqueResult();
	}

	public void savePlainTextMapping(PlainTextField mapping) throws DuplicateKeyException {
		super.save(mapping);
	}

	public void savePlainTextMappingWithoutDuplicateHandling(PlainTextField mapping) {
		super.saveWithoutDuplicateHandling(mapping);
	}

	public void updatePlainTextMapping(PlainTextField mapping) throws DuplicateKeyException {
		super.update(mapping);
	}

	public void updatePlainTextMappingWithoutDuplicateHandling( PlainTextField mapping) {
		super.updateWithoutDuplicateHandling(mapping);

	}

	public void deletePlainTextMapping(PlainTextField mapping) {
		super.delete(mapping);
	}

	public List<PlainTextField> getAllPlainTextMappings() {
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

	public PlainTextField searchAllMappingsForForCode(String shortcode) {
		DetachedCriteria c = super.getCriterion();
		String code = ShortCodeProperties.getInstance().getKeyForShortCode(shortcode);
		c.add(Restrictions.eq("shortCode", code));
		return super.getUnique(c);
	}
}
