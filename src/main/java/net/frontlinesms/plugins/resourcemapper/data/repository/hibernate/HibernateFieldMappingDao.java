package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;

public class HibernateFieldMappingDao extends BaseHibernateDao<Field> implements FieldMappingDao {

	protected HibernateFieldMappingDao() {
		super(Field.class);
	}
	
	public void saveFieldMapping(Field field) throws DuplicateKeyException {
		super.save(field);
	}

	public void saveFieldMappingWithoutDuplicateHandling(Field field) {
		super.saveWithoutDuplicateHandling(field);
	}

	public void updateFieldMapping(Field field) throws DuplicateKeyException {
		super.update(field);
	}

	public void updateFieldMappingWithoutDuplicateHandling(Field field) {
		super.updateWithoutDuplicateHandling(field);
	}

	public void deleteFieldMapping(Field field) {
		super.delete(field);
	}

	public List<Field> getAllFieldMappings() {
		return super.getAll();
	}

	public List<String> getAbbreviations() {
		List<String> abbreviations = new ArrayList<String>();
		for (Field field : super.getAll()) {
			abbreviations.add(field.getAbbreviation());
		}
		return abbreviations;
	}
	
	public Field getFieldForAbbreviation(String abbrev) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq("abbreviation", abbrev));
		return super.getUnique(criteria);
	}
	
//	private static final String ALL_SHORT_CODES_QUERY = "select p.shortCode from PlainTextMapping";
//	private static final String SHORT_CODES_QUERY = "select p.shortCode from PlainTextMapping p where DTYPE='PlainTextMapping'";
//	private static final String MAPPING_FOR_SHORT_CODE = "from PlainTextMapping p where p.class=PlainTextMapping and p.shortCode=";
	
//	@SuppressWarnings("unchecked")
//	public List<String> getShortCodes() {
//		List<String> results = super.getSession().createSQLQuery(SHORT_CODES_QUERY).list();
//		return results;
//	}
//
//	@SuppressWarnings("unchecked")
//	public List<String> getAllShortCodes() {
//		return super.getSession().createQuery(ALL_SHORT_CODES_QUERY).list();
//	}
//
//	public Field searchAllMappingsForForCode(String shortcode) {
//		DetachedCriteria c = super.getCriterion();
//		String code = ShortCodeProperties.getInstance().getKeyForShortCode(shortcode);
//		c.add(Restrictions.eq("shortCode", code));
//		return super.getUnique(c);
//	}
//
//	public Field getMappingForShortCode(String shortcode) {
//		String code = ShortCodeProperties.getInstance().getKeyForShortCode(shortcode);
//		Query q = super.getSession().createQuery(MAPPING_FOR_SHORT_CODE + "'" + code + "'");
//		return (Field) q.uniqueResult();
//	}
}
