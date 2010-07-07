package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.CodedField;
import net.frontlinesms.plugins.resourcemapper.data.repository.CodedMappingDao;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateCodedMappingDao extends BaseHibernateDao<CodedField> implements CodedMappingDao {

	protected HibernateCodedMappingDao() {
		super(CodedField.class);
	}

	public void deleteCodedMapping(CodedField mapping) {
		super.delete(mapping);
	}
	
	public CodedField getMappingForShortCode(String shortcode) {
		String code = ShortCodeProperties.getInstance().getKeyForShortCode(shortcode);
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("shortCode", code));
		return super.getUnique(c);
	}

	public void saveCodedMapping(CodedField mapping) throws DuplicateKeyException {
		super.save(mapping);
	}

	public void saveCodedMappingWithoutDuplicateHandling(CodedField mapping) {
		super.saveWithoutDuplicateHandling(mapping);
	}

	public void updateCodedMapping(CodedField mapping) throws DuplicateKeyException {
		super.update(mapping);
	}

	public void updateCodedMappingWithoutDuplicateHandling(CodedField mapping) {
		super.updateWithoutDuplicateHandling(mapping);
	}

	public List<CodedField> getAllCodedMappings() {
		return super.getAll();
	}
	
	private static final String SHORT_CODES_QUERY = "select c.shortCode from PlainTextMapping c  where DTYPE='CodedMapping'";
	
	public List<String> getShortCodes() {
		return super.getSession().createSQLQuery(SHORT_CODES_QUERY).list();
	}


}
