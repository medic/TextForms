package net.frontlinesms.plugins.resourcemapper.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.BooleanField;

public interface BooleanMappingDao {
	
	/**
	 * @return All the boolean mappings in the system
	 */
	public List<BooleanField> getAllBooleanMappings();
	
	/**
	 * Deletes a boolean mapping from the system
	 * @param mapping the boolean mapping to delete
	 */
	public void deleteBooleanMapping(BooleanField mapping);
	
	/**
	 * Saves a boolean mapping to the system
	 * @param mapping the boolean mapping to save
	 * @throws DuplicateKeyException if the mapping's short code is already in use 
	 */
	public void saveBooleanMapping(BooleanField mapping) throws DuplicateKeyException;
	
	/**
	 * Updates a boolean mapping's details in the data source
	 * @param mapping the boolean mapping whose details should be updated
	 * @throws DuplicateKeyException if the mapping's short code is already in use
	 */
	public void updateBooleanMapping(BooleanField mapping) throws DuplicateKeyException;
	
	/**
	 * Saves a boolean mapping to the system without duplicate handling
	 * @param mapping the boolean mapping to save
	 */
	public void saveBooleanMappingWithoutDuplicateHandling(BooleanField mapping);
	
	/**
	 * Updates a boolean mapping's details in the data source without duplicate handling
	 * @param mapping the boolean mapping whose details should be updated
	 */
	public void updateBooleanMappingWithoutDuplicateHandling(BooleanField mapping);
	
	/**
	 * Returns the mapping with the designated short code
	 * @param shortcode
	 * @return
	 */
	public BooleanField getMappingForShortCode(String shortcode);
	
	public List<String> getShortCodes();
	
}

