package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.ShortCodeProperties;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseDao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateFieldResponseDao extends BaseHibernateDao<FieldResponse> implements FieldResponseDao {

	protected HibernateFieldResponseDao() {
		super(FieldResponse.class);
	}

	public void deleteFieldResponse(FieldResponse response) {
		super.delete(response);
	}

	public List<FieldResponse> getAllFieldResponses() {
		return super.getAll();
	}

	public void saveFieldResponse(FieldResponse response) throws DuplicateKeyException {
		super.save(response);
	}

	public void saveFieldResponseWithoutDuplicateHandling(FieldResponse response) {
		super.saveWithoutDuplicateHandling(response);
	}

	public void updateFieldResponse(FieldResponse response) throws DuplicateKeyException {
		super.update(response);
	}

	public void updateFieldResponseWithoutDuplicateHandling(FieldResponse response) {
		super.updateWithoutDuplicateHandling(response);
	}

	public List<FieldResponse> getFieldResponsesForMapping(Field mapping) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("mapping", mapping));
		return super.getList(c);
	}
	
	public List<FieldResponse> getFieldResponsesForHospitalId(String hospitalId) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("hospitalId", hospitalId));
		return super.getList(c);
	}
	
	public List<FieldResponse> getFieldResponsesForSubmitter(HospitalContact contact) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("submitter", contact));
		return super.getList(c);
	}
	
	public List<FieldResponse> getFieldResponses(Field mapping, HospitalContact contact) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("mapping", mapping));
		c.add(Restrictions.eq("submitter", contact));
		return super.getList(c);
	}
	
}
