package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
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

	public void updateFieldMappingWithoutDuplicateHandling( Field field) {
		super.updateWithoutDuplicateHandling(field);

	}

	public void deleteFieldMapping(Field field) {
		super.delete(field);
	}

	public List<Field> getAllFieldMappings() {
		return super.getAll();
	}

}
