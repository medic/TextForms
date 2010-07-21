package net.frontlinesms.plugins.resourcemapper;

import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;

/*
 * ResourcesMapperCallback
 * @author Dale Zak
 */
@SuppressWarnings("unchecked")
public interface ResourceMapperCallback {
	public void viewResponses(HospitalContact contact);
	public void viewResponses(Field field);
	public void refreshContact(HospitalContact contact);
	public void refreshField(Field field);
	public void refreshFieldResponse(FieldResponse fieldResponse);
}