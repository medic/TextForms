package net.frontlinesms.plugins.resourcemapper.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;

public interface FieldResponseDao {

	
	public List<FieldResponse> getAllFieldResponses();
	/**
	 * Deletes a field response from the system
	 * @param response the field response to delete
	 */
	public void deleteFieldResponse(FieldResponse response);
	
	/**
	 * Saves a field response to the system
	 * @param response the field response to save
	 * @throws DuplicateKeyException if the mapping's short code is already in use 
	 */
	public void saveFieldResponse(FieldResponse response) throws DuplicateKeyException;
	
	/**
	 * Updates a field response's details in the data source
	 * @param response the field response whose details should be updated
	 * @throws DuplicateKeyException if the mapping's short code is already in use
	 */
	public void updateFieldResponse(FieldResponse response) throws DuplicateKeyException;
	
	/**
	 * Saves a field response to the system without duplicate handling
	 * @param response the field response to save
	 */
	public void saveFieldResponseWithoutDuplicateHandling(FieldResponse response);
	
	/**
	 * Updates a field response's details in the data source without duplicate handling
	 * @param response the field response whose details should be updated
	 */
	public void updateFieldResponseWithoutDuplicateHandling(FieldResponse response);
	
	public List<FieldResponse> getFieldResponsesForMapping(PlainTextField mapping);
	
	public List<FieldResponse> getFieldResponsesForHospitalId(String hospitalId);
	
	public List<FieldResponse> getFieldResponsesForSubmitter(HospitalContact contact);
}
