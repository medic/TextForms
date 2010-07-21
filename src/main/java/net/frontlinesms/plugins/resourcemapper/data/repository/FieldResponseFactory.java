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
import net.frontlinesms.data.repository.MessageDao;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.BooleanResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.ChecklistResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.MultiChoiceResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.PlainTextResponse;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.BooleanField;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.ChecklistField;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.MultiChoiceField;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.PlainTextField;

/*
 * FieldResponseFactory
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public final class FieldResponseFactory {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(FieldResponseFactory.class);
	
	/*
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
	
	public static FieldResponse createFieldResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, Field field) {
		if (field.getClass() == PlainTextField.class) {
			return createFieldResponse(message, submitter, dateSubmitted, hospitalId, (PlainTextField)field);
		}
		else if (field.getClass() == BooleanField.class) {
			return createFieldResponse(message, submitter, dateSubmitted, hospitalId, (BooleanField)field);
		}
		else if (field.getClass() == ChecklistField.class) {
			return createFieldResponse(message, submitter, dateSubmitted, hospitalId, (ChecklistField)field);
		}
		else if (field.getClass() == MultiChoiceField.class) {
			return createFieldResponse(message, submitter, dateSubmitted, hospitalId, (MultiChoiceField)field);
		}
		return null;
	}
	
	public static PlainTextResponse createFieldResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, PlainTextField field) {
		return new PlainTextResponse(message, submitter, dateSubmitted, hospitalId, field);
	}
	
	public static BooleanResponse createFieldResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, BooleanField field) {
		return new BooleanResponse(message, submitter, dateSubmitted, hospitalId, field);
	}
	
	public static ChecklistResponse createFieldResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, ChecklistField field) {
		return new ChecklistResponse(message, submitter, dateSubmitted, hospitalId, field);
	}
	
	public static MultiChoiceResponse createFieldResponse(FrontlineMessage message, HospitalContact submitter, Date dateSubmitted, String hospitalId, MultiChoiceField field) {
		return new MultiChoiceResponse(message, submitter, dateSubmitted, hospitalId, field);
	}
	
}