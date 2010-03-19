package net.frontlinesms.plugins.resourcemapper.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;

public interface HospitalContactDao {
	/** @return all countacts in the system */
	public List<HospitalContact> getAllHospitalContacts();
	
	/**
	 * Retrieves the contact with the specified msisdn, or returns NULL if none exists.
	 * @param phoneNumber a phone number
	 * @return contact with the specified msisdn, or returns <code>null</code> if none exists
	 */
	public HospitalContact getHospitalContactByPhoneNumber(String phoneNumber);
	
	/**
	 * Deletes a contact from the system
	 * @param contact the contact to delete
	 */
	public void deleteHospitalContact(HospitalContact contact);
	
	/**
	 * Saves a contact to the system
	 * @param contact the contact to save
	 * @throws DuplicateKeyException if the contact's phone number is already in use by another contact 
	 */
	public void saveHospitalContact(HospitalContact contact) throws DuplicateKeyException;
	
	/**
	 * Updates a contact's details in the data source
	 * @param contact the contact whose details should be updated
	 * @throws DuplicateKeyException if the contact's phone number is already in use by another contact
	 */
	public void updateHospitalContact(HospitalContact contact) throws DuplicateKeyException;
	
	
	/** 
	 * Performs a like query on the name value of all contacts,
	 * so it returns every contact that has 'name' in their name at any point
	 * @param contactNameFilter A contact's name, or any part of it
	 * @return count of all contacts whose names match the filter
	 */
	public List<HospitalContact> getHospitalContactsByName(String name);
	
	public List<HospitalContact> getBlacklistedContacts();
	
	public List<HospitalContact> getWhitelistedContacts();
	
	public List<HospitalContact> getHospitalContactsByHospitalId(String hopsitalId);
}
