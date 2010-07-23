/*
 * FrontlineSMS <http://www.frontlinesms.com>
 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */
package net.frontlinesms.plugins.resourcemapper.data.repository;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.ServiceLoader;

import net.frontlinesms.data.domain.FrontlineMessage;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

/*
 * FieldResponseFactory
 * @author Dale Zak
 */
@SuppressWarnings("unchecked")
public final class FieldResponseFactory {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(FieldResponseFactory.class);
	
	/**
	 * Get list of FieldResponse classes
	 * (To add a new Reminder classes to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse
	 * with the full package and class name of the new implementing Reminder class)
	 */
	public static List<FieldResponse> getFieldResponseClasses() {
		if (fieldResponseClasses == null) {
			fieldResponseClasses = new ArrayList<FieldResponse>();
			for (FieldResponse fieldResponse : ServiceLoader.load(FieldResponse.class)) {
				LOG.debug("FieldResponse Discovered: %s", fieldResponse);
				fieldResponseClasses.add(fieldResponse);
		    }
		}
		return fieldResponseClasses;
	}private static List<FieldResponse> fieldResponseClasses = null;
	
	/**
	 * Create instance of FieldRespone for associated Field mapping
	 * @param message FrontlineMessage
	 * @param submitter HospitalContact
	 * @param dateSubmitted date submitted
	 * @param hospitalId Hospital ID
	 * @param field Field
	 * @return FieldResponse
	 */
	public static FieldResponse createFieldResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, Field field) {
		for (FieldResponse fieldResponseClass : getFieldResponseClasses()) {
			if (fieldResponseClass.getMappingType().equalsIgnoreCase(field.getType())) {
				try {
					FieldResponse fieldResponse = fieldResponseClass.getClass().newInstance();
					fieldResponse.setMessage(message);
					fieldResponse.setSubmitter(submitter);
					fieldResponse.setDateSubmitted(dateSubmitted);
					fieldResponse.setHospitalId(hospitalId);
					fieldResponse.setMapping(field);
					LOG.debug("FieldResponse Created: %s", fieldResponse.getClass().getSimpleName());
					return fieldResponse;
				}
				catch (InstantiationException ex) {
					LOG.error("InstantiationException: %s", ex);
				} 
				catch (IllegalAccessException ex) {
					LOG.error("InstantiationException: %s", ex);
				}
			}
		}
		LOG.error("Unable to find class for field: %s", field.getType());
		return null;
	}
	
}