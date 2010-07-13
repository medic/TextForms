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

import java.util.List;
import java.util.ArrayList;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

/*
 * RemindersFactory
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public final class FieldMappingFactory {

	private static Logger LOG = FrontlineUtils.getLogger(FieldMappingFactory.class);
	
	/*
	 * Get list of Field classes
	 * (To add a new Reminder classes to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.reminders.data.domain.Reminder
	 * with the full package and class name of the new implementing Reminder class)
	 */
	public static List<Field> getFieldClasses() {
		if (fieldClasses == null) {
			fieldClasses = new ArrayList<Field>();
			for (Field field : ServiceLoader.load(Field.class)) {
				System.out.println("Field Discovered: " + field);
				fieldClasses.add(field);
		    }
		}
		return fieldClasses;
	}private static List<Field> fieldClasses = null;
	
	public static Field createField(String name, String abbrev, String infoSnippet, String type) {
		for (Field fieldClass : getFieldClasses()) {
			if (fieldClass.getType().equalsIgnoreCase(type)) {
				try {
					Field field = fieldClass.getClass().newInstance();
					field.setFullName(name);
					field.setAbbreviation(abbrev);
					field.setInfoSnippet(infoSnippet);
					LOG.debug("Field Created: " + field);
					return field;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		LOG.debug("Unable to find class for type: " + type);
		return null;
	}
	
}