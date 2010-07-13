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
package net.frontlinesms.plugins.resourcemapper.ui;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.repository.BooleanMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.ChecklistMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.CodedMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingFactory;
import net.frontlinesms.plugins.resourcemapper.data.repository.PlainTextMappingDao;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageFieldsDialogHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManageFieldsDialogHandler implements ThinletUiEventHandler {
	
	private static Logger LOG = FrontlineUtils.getLogger(ManageFieldsDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/resourcemapper/manageFieldsDialog.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	private ResourceMapperCallback callback;
	
	private Object mainDialog;
	private Field field;
	
	private Object textName;
	private Object textAbbreviation;
	private Object textInfo;
	private Object comboTypes;
	private Object listChoices; 
	
	private FieldMappingDao fieldMappingDao;
	
	public ManageFieldsDialogHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		System.out.println("ManagePeopleDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.textName = this.ui.find(this.mainDialog, "textName");
		this.textAbbreviation = this.ui.find(this.mainDialog, "textAbbreviation");
		this.textInfo = this.ui.find(this.mainDialog, "textInfo");
		this.comboTypes = this.ui.find(this.mainDialog, "comboTypes");
		this.listChoices = this.ui.find(this.mainDialog, "listChoices");
		
		this.fieldMappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
		
		for (Field field : FieldMappingFactory.getFieldClasses()) {
			Object comboBoxChoice = this.ui.createComboboxChoice(field.getTypeLabel(), field.getType());
			this.ui.add(comboTypes, comboBoxChoice);
		}
	}
	
	public void show(Field field) {
		this.field = field;
		if (field != null) {
			this.ui.setText(this.textName, field.getFullName());
			this.ui.setText(this.textAbbreviation, field.getAbbreviation());
			this.ui.setText(this.textInfo, field.getInfoSnippet());
			for (int index = 0; index < this.ui.getCount(this.comboTypes); index++) {
				Object comboTypeItem = this.ui.getItem(this.comboTypes, index);
				String type = this.ui.getAttachedObject(comboTypeItem).toString();
				if (field.getType().equalsIgnoreCase(type)) {
					this.ui.setSelectedIndex(this.comboTypes, index);
					break;
				}				
			}
			typeChanged(this.comboTypes, this.listChoices);
		}
		else {
			this.ui.setText(this.textName, "");
			this.ui.setText(this.textAbbreviation, "");
			this.ui.setText(this.textInfo, "");
			this.ui.setSelectedIndex(this.comboTypes, -1);
			this.ui.setVisible(this.listChoices, false);
			this.ui.removeAll(this.listChoices);
		}
		this.ui.add(this.mainDialog);
	}
	
	public void saveField(Object dialog) throws DuplicateKeyException {
		System.out.println("saveField");
		String fullName = this.ui.getText(this.textName);
		String abbreviation = this.ui.getText(this.textAbbreviation);
		String infoSnippet = this.ui.getText(this.textInfo);
		Object fieldType = this.ui.getSelectedItem(this.comboTypes);
		String type = this.ui.getAttachedObject(fieldType).toString();
		if (this.field != null && this.field.getType().equalsIgnoreCase(type)) {
			this.field.setFullName(fullName);
			this.field.setAbbreviation(abbreviation);
			this.field.setInfoSnippet(infoSnippet);
			this.fieldMappingDao.updateFieldMapping(this.field);
		}
		else {
			if (this.field != null) {
				this.fieldMappingDao.deleteFieldMapping(this.field);
			}
			this.field = FieldMappingFactory.createField(fullName, abbreviation, infoSnippet, type);
			if (this.field != null) {
				this.fieldMappingDao.saveFieldMapping(this.field);
			}
		}
		this.callback.refreshField(this.field);
		this.ui.remove(dialog);
	}
	
	public void removeDialog(Object dialog) {
		System.out.println("removeDialog");
		this.ui.remove(dialog);
	}
	
	public void typeChanged(Object comboTypes, Object listChoices) {
		System.out.println("typeChanged");
		Object selectedTypeItem = this.ui.getSelectedItem(comboTypes);
		String selectedTypeValue = this.ui.getAttachedObject(selectedTypeItem).toString();
		if ("plaintext".equalsIgnoreCase(selectedTypeValue)) {
			this.ui.setVisible(listChoices, false);
		}
		else if ("boolean".equalsIgnoreCase(selectedTypeValue)) {
			this.ui.setVisible(listChoices, false);
		}
		else if ("checklist".equalsIgnoreCase(selectedTypeValue)) {
			this.ui.setVisible(listChoices, true);
		}
		else if ("multichoice".equalsIgnoreCase(selectedTypeValue)) {
			this.ui.setVisible(listChoices, true);
		}
	}
}
