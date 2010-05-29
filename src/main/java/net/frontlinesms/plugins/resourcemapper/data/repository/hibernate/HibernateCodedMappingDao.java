package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.CodedMapping;
import net.frontlinesms.plugins.resourcemapper.data.repository.CodedMappingDao;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateCodedMappingDao extends BaseHibernateDao<CodedMapping> implements CodedMappingDao {

	protected HibernateCodedMappingDao() {
		super(CodedMapping.class);
	}

	public void deleteCodedMapping(CodedMapping mapping) {
		super.delete(mapping);
	}
	
	public CodedMapping getMappingForShortCode(String shortcode) {
		String code = ShortCodeProperties.getInstance().getKeyForShortCode(shortcode);
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("shortCode", code));
		return super.getUnique(c);
	}

	public void saveCodedMapping(CodedMapping mapping) throws DuplicateKeyException {
		super.save(mapping);
	}

	public void saveCodedMappingWithoutDuplicateHandling(CodedMapping mapping) {
		super.saveWithoutDuplicateHandling(mapping);
	}

	public void updateCodedMapping(CodedMapping mapping) throws DuplicateKeyException {
		super.update(mapping);
	}

	public void updateCodedMappingWithoutDuplicateHandling(CodedMapping mapping) {
		super.updateWithoutDuplicateHandling(mapping);
	}

	public List<CodedMapping> getAllCodedMappings() {
		return super.getAll();
	}
	
	private static final String SHORT_CODES_QUERY = "select c.shortCode from PlainTextMapping c  where DTYPE='CodedMapping'";
	
	public List<String> getShortCodes() {
		return super.getSession().createSQLQuery(SHORT_CODES_QUERY).list();
	}


}
