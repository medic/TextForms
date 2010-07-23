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

import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;

/*
 * FieldMappingFactory
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public final class FieldMappingFactory {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(FieldMappingFactory.class);
	
	/**
	 * Get list of Field classes
	 * (To add a new Field classes to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field
	 * with the full package and class name of the new implementing Field class)
	 */
	public static List<Field> getFieldClasses() {
		if (fieldClasses == null) {
			fieldClasses = new ArrayList<Field>();
			for (Field field : ServiceLoader.load(Field.class)) {
				LOG.debug("Field Discovered: %s", field.getClass().getSimpleName());
				fieldClasses.add(field);
		    }
		}
		return fieldClasses;
	}private static List<Field> fieldClasses = null;
	
	/**
	 * Create instance of Field for associated type
	 * @param name Field name
	 * @param keyword Field keyword
	 * @param infoSnippet Field info snippet
	 * @param type Field type
	 * @param choices Field choices
	 * @return Field
	 */
	public static Field createField(String name, String keyword, String infoSnippet, String type, List<String> choices) {
		for (Field fieldClass : getFieldClasses()) {
			if (fieldClass.getType().equalsIgnoreCase(type)) {
				try {
					Field field = fieldClass.getClass().newInstance();
					field.setName(name);
					field.setKeyword(keyword);
					field.setInfoSnippet(infoSnippet);
					field.setChoices(choices);
					LOG.debug("Field Created: %s", field.getClass().getSimpleName());
					return field;
				} 
				catch (InstantiationException ex) {
					LOG.error("InstantiationException: %s", ex);
				} 
				catch (IllegalAccessException ex) {
					LOG.error("InstantiationException: %s", ex);
				}
			}
		}
		LOG.error("Unable to find class for field: %s", type);
		return null;
	}
	
}