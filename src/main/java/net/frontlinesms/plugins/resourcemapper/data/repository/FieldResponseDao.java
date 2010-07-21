package net.frontlinesms.plugins.resourcemapper.data.repository;

import java.util.List;

import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;

public interface FieldResponseDao {

	/**
	 * Get all FieldResponses
	 * @return collection of FieldResponses
	 */
	@SuppressWarnings("unchecked")
	public List<FieldResponse> getAllFieldResponses();
	
	/**
	 * Deletes a field response from the system
	 * @param response the field response to delete
	 */
	@SuppressWarnings("unchecked")
	public void deleteFieldResponse(FieldResponse response);
	
	/**
	 * Saves a field response to the system
	 * @param response the field response to save
	 */
	@SuppressWarnings("unchecked")
	public void saveFieldResponse(FieldResponse response);
	
	/**
	 * Updates a field response's details in the data source
	 * @param response the field response whose details should be updated
	 */
	@SuppressWarnings("unchecked")
	public void updateFieldResponse(FieldResponse response);
	
	/**
	 * Get all FieldResponses for a given Field apping
	 * @param mapping Field
	 * @return collection of FieldResponses
	 */
	@SuppressWarnings("unchecked")
	public List<FieldResponse> getFieldResponsesForMapping(Field mapping);
	
	/**
	 * Get all FieldResponses for a given Hospital ID
	 * @param hospitalId Hospital ID
	 * @return collection of FieldResponses
	 */
	@SuppressWarnings("unchecked")
	public List<FieldResponse> getFieldResponsesForHospitalId(String hospitalId);
	
	/**
	 * Get all FieldResponses for given submitter HospitalContact
	 * @param contact HospitalContact
	 * @return collection of FieldResponses
	 */
	@SuppressWarnings("unchecked")
	public List<FieldResponse> getFieldResponsesForSubmitter(HospitalContact contact);
}
