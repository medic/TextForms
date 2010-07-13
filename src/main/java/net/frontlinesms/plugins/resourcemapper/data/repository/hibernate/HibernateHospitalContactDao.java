package net.frontlinesms.plugins.resourcemapper.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

public class HibernateHospitalContactDao extends BaseHibernateDao<HospitalContact> implements HospitalContactDao {

	private static final String HOSPITAL_ID_QUERY = "select hosp.hospitalId from HospitalContact hosp";
	
	public List<HospitalContact> getAlHospitalContacts(){
		return super.getAll();
	}
	protected HibernateHospitalContactDao() {
		super(HospitalContact.class);
	}

	public void deleteHospitalContact(HospitalContact contact) {
		super.delete(contact);
	}

	public List<HospitalContact> getAllHospitalContacts() {
		return super.getAll();
	}

	public List<HospitalContact> getAllHospitalContacts(int startIndex, int limit) {
		return super.getAll(startIndex, limit);
	}
	
	public HospitalContact getHospitalContactByPhoneNumber(String phoneNumber) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("phoneNumber", phoneNumber));
		return super.getUnique(c);
	}

	public List<HospitalContact> getHospitalContactsByName(String name) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("name", "%"+name+"%"));
		return super.getList(c);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao#saveHospitalContact(net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact)
	 */
	public void saveHospitalContact(HospitalContact contact) throws DuplicateKeyException {
		super.save(contact);
	}

	/* (non-Javadoc)
	 * @see net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao#updateHospitalContact(net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact)
	 */
	public void updateHospitalContact(HospitalContact contact) throws DuplicateKeyException {
		super.update(contact);
	}

	public List<HospitalContact> getBlacklistedContacts() {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("isBlacklisted", true));
		return super.getList(c);
	}

	public List<HospitalContact> getHospitalContactsByHospitalId( String hospitalId) {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.like("hospitalId", "%"+hospitalId+"%"));
		return super.getList(c);
	}

	public List<HospitalContact> getWhitelistedContacts() {
		DetachedCriteria c = super.getCriterion();
		c.add(Restrictions.eq("isBlacklisted", false));
		return super.getList(c);
	}
	public List<String> getAllHospitalIds() {
		Query q = super.getSession().createQuery(HOSPITAL_ID_QUERY);
		return q.list();
	}
}
