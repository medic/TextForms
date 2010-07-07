package net.frontlinesms.plugins.resourcemapper.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;

public interface PlainTextMappingDao {

	public List<PlainTextField> getAllPlainTextMappings();
	/**
	 * Deletes a plaintext mapping from the system
	 * @param mapping the plaintext mapping to delete
	 */
	public void deletePlainTextMapping(PlainTextField mapping);
	
	/**
	 * Saves a plaintext mapping to the system
	 * @param mapping the plaintext mapping to save
	 * @throws DuplicateKeyException if the plaintext mapping's phone number is already in use by another plaintext mapping 
	 */
	public void savePlainTextMapping(PlainTextField mapping) throws DuplicateKeyException;
	
	/**
	 * Updates a plaintext mapping's details in the data source
	 * @param mapping the plaintext mapping whose details should be updated
	 * @throws DuplicateKeyException if the plaintext mapping's phone number is already in use by another plaintext mapping
	 */
	public void updatePlainTextMapping(PlainTextField mapping) throws DuplicateKeyException;
	
	/**
	 * Saves a plaintext mapping to the system
	 * @param mapping the plaintext mapping to save
	 * @throws DuplicateKeyException if the plaintext mapping's phone number is already in use by another plaintext mapping 
	 */
	public void savePlainTextMappingWithoutDuplicateHandling(PlainTextField mapping);
	
	/**
	 * Updates a plaintext mapping's details in the data source
	 * @param mapping the plaintext mapping whose details should be updated
	 * @throws DuplicateKeyException if the plaintext mapping's phone number is already in use by another plaintext mapping
	 */
	public void updatePlainTextMappingWithoutDuplicateHandling(PlainTextField mapping);
	
	/**
	 * Returns the mapping with the designated short code
	 * @param shortcode
	 * @return
	 */
	public PlainTextField getMappingForShortCode(String shortcode);
	
	public List<String> getShortCodes();
	
	public List<String> getAllShortCodes();
	
	public PlainTextField searchAllMappingsForForCode(String shortcode);
	
}
