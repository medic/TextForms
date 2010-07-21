package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;

public class HibernateFieldMappingDao extends BaseHibernateDao<Field> implements FieldMappingDao {

	@SuppressWarnings("unused")
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(HibernateFieldMappingDao.class);
	
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
	
	public List<String> getAbbreviationsForField(Field field) {
		List<String> abbreviations = new ArrayList<String>();
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq("class", field.getType()));
		for (Field f : super.getList(criteria)) {
			abbreviations.add(f.getAbbreviation());
		}
		return abbreviations;
	}
	
	public Field getFieldForAbbreviation(String abbrev) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq("abbreviation", abbrev));
		return super.getUnique(criteria);
	}
	
}
