package net.frontlinesms.plugins.resourcemapper.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextMapping;

public interface PlainTextMappingDao {

	public List<PlainTextMapping> getAllPlainTextMappings();
	/**
	 * Deletes a plaintext mapping from the system
	 * @param mapping the plaintext mapping to delete
	 */
	public void deletePlainTextMapping(PlainTextMapping mapping);
	
	/**
	 * Saves a plaintext mapping to the system
	 * @param mapping the plaintext mapping to save
	 * @throws DuplicateKeyException if the plaintext mapping's phone number is already in use by another plaintext mapping 
	 */
	public void savePlainTextMapping(PlainTextMapping mapping) throws DuplicateKeyException;
	
	/**
	 * Updates a plaintext mapping's details in the data source
	 * @param mapping the plaintext mapping whose details should be updated
	 * @throws DuplicateKeyException if the plaintext mapping's phone number is already in use by another plaintext mapping
	 */
	public void updatePlainTextMapping(PlainTextMapping mapping) throws DuplicateKeyException;
	
	/**
	 * Saves a plaintext mapping to the system
	 * @param mapping the plaintext mapping to save
	 * @throws DuplicateKeyException if the plaintext mapping's phone number is already in use by another plaintext mapping 
	 */
	public void savePlainTextMappingWithoutDuplicateHandling(PlainTextMapping mapping);
	
	/**
	 * Updates a plaintext mapping's details in the data source
	 * @param mapping the plaintext mapping whose details should be updated
	 * @throws DuplicateKeyException if the plaintext mapping's phone number is already in use by another plaintext mapping
	 */
	public void updatePlainTextMappingWithoutDuplicateHandling(PlainTextMapping mapping);
	
	/**
	 * Returns the mapping with the designated short code
	 * @param shortcode
	 * @return
	 */
	public PlainTextMapping getMappingForShortCode(String shortcode);
	
	public List<String> getShortCodes();
	
	public List<String> getAllShortCodes();
	
	public PlainTextMapping searchAllMappingsForForCode(String shortcode);
	
}
