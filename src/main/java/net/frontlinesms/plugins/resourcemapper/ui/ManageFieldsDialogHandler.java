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

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingFactory;
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
	private Object textInfoSnippet;
	private Object comboFieldTypes;
	private Object listFieldChoices; 
	private Object panelFieldChoices;
	private Object textFieldChoice;
	private Object buttonFieldAdd;
	
	private FieldMappingDao fieldMappingDao;
	
	public ManageFieldsDialogHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) { 
		System.out.println("ManagePeopleDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.textName = this.ui.find(this.mainDialog, "textName");
		this.textAbbreviation = this.ui.find(this.mainDialog, "textAbbreviation");
		this.textInfoSnippet = this.ui.find(this.mainDialog, "textInfoSnippet");
		this.comboFieldTypes = this.ui.find(this.mainDialog, "comboFieldTypes");
		this.listFieldChoices = this.ui.find(this.mainDialog, "listFieldChoices");
		this.panelFieldChoices = this.ui.find(this.mainDialog, "panelFieldChoices");
		this.textFieldChoice = this.ui.find(this.mainDialog, "textFieldChoice");	
		this.buttonFieldAdd = this.ui.find(this.mainDialog, "buttonFieldAdd");
		this.fieldMappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
		
		loadFieldMappings();
	}
	
	public void show(Field field) {
		this.field = field;
		if (field != null) {
			this.ui.setText(this.textName, field.getName());
			this.ui.setText(this.textAbbreviation, field.getAbbreviation());
			this.ui.setText(this.textInfoSnippet, field.getInfoSnippet());
			this.ui.setSelectedIndex(this.comboFieldTypes, -1);
			for (int index = 0; index < this.ui.getCount(this.comboFieldTypes); index++) {
				Object comboTypeItem = this.ui.getItem(this.comboFieldTypes, index);
				Object attachedObject = this.ui.getAttachedObject(comboTypeItem);
				if (attachedObject != null) {
					if (field.getType().equalsIgnoreCase(attachedObject.toString())) {
						this.ui.setSelectedIndex(this.comboFieldTypes, index);
						break;
					}			
				}		
			}
		}
		else {
			this.ui.setText(this.textName, "");
			this.ui.setText(this.textAbbreviation, "");
			this.ui.setText(this.textInfoSnippet, "");
			this.ui.setSelectedIndex(this.comboFieldTypes, 0);
			this.ui.removeAll(this.listFieldChoices);
		}
		fieldTypeChanged(this.comboFieldTypes, this.panelFieldChoices, this.listFieldChoices);
		this.ui.add(this.mainDialog);
	}
	
	private void loadFieldMappings() {
		this.ui.add(this.comboFieldTypes, this.ui.createComboboxChoice("", null));
		for (Field fieldClass : FieldMappingFactory.getFieldClasses()) {
			Object comboBoxChoice = this.ui.createComboboxChoice(fieldClass.getTypeLabel(), fieldClass.getType());
			this.ui.setIcon(comboBoxChoice, "/icons/tip.png");
			this.ui.add(this.comboFieldTypes, comboBoxChoice);
		}
	}
	
	public void saveField(Object dialog) throws DuplicateKeyException {
		System.out.println("saveField");
		String name = this.ui.getText(this.textName);
		String abbreviation = this.ui.getText(this.textAbbreviation);
		String infoSnippet = this.ui.getText(this.textInfoSnippet);
		Object fieldType = this.ui.getSelectedItem(this.comboFieldTypes);
		String type = (String)this.ui.getAttachedObject(fieldType);
		Set<String> choices = new TreeSet<String>();
		for (Object comboItem : this.ui.getItems(this.listFieldChoices)) {
			String comboItemText = this.ui.getAttachedObject(comboItem).toString();
			System.out.println("comboItemText: " + comboItemText);
			choices.add(comboItemText);	
		}
		
		try {
			if (name == null || name.length() == 0) {
				this.ui.alert(getI18NString(ResourceMapperConstants.ALERT_MISSING_FIELD_NAME));
			}
			else if (abbreviation == null || abbreviation.length() == 0) {
				this.ui.alert(getI18NString(ResourceMapperConstants.ALERT_MISSING_FIELD_ABBREV));
			}
			else if (type == null || type.length() == 0) {
				this.ui.alert(getI18NString(ResourceMapperConstants.ALERT_MISSING_FIELD_TYPE));
			}
			else if (this.field != null && this.field.getType().equalsIgnoreCase(type)) {
				this.field.setName(name);
				this.field.setAbbreviation(abbreviation);
				this.field.setInfoSnippet(infoSnippet);
				this.field.setChoices(choices);
				this.fieldMappingDao.updateFieldMapping(this.field);
				this.callback.refreshField(this.field);
				this.ui.remove(dialog);
			}
			else {
				if (this.field != null) {
					System.out.println("Existing Field Deleted!");
					this.fieldMappingDao.deleteFieldMapping(this.field);
				}
				Field newField = FieldMappingFactory.createField(name, abbreviation, infoSnippet, type, choices);
				if (newField != null) {
					System.out.println("New Field Created!");
					this.fieldMappingDao.saveFieldMapping(newField);
				}
				this.callback.refreshField(newField);
				this.ui.remove(dialog);
			}
		}
		catch (DuplicateKeyException ex) {
			System.out.println(ex);
			this.ui.alert(getI18NString(ResourceMapperConstants.ALERT_DUPLICATE_ABBREV));
		}
	}
	
	public void removeDialog(Object dialog) {
		System.out.println("removeDialog");
		this.ui.remove(dialog);
	}
	
	public void fieldTypeChanged(Object comboTypes, Object panelChoices, Object listChoices) {
		System.out.println("typeChanged");
		Object selectedItem = this.ui.getSelectedItem(comboTypes);
		Object attachedObject = selectedItem != null ? this.ui.getAttachedObject(selectedItem) : null;
		String selectedType = attachedObject != null ? attachedObject.toString() : null;
		this.ui.removeAll(listChoices);
		this.ui.setEnabled(this.buttonFieldAdd, false);
		this.ui.setEditable(this.textFieldChoice, false);
		this.ui.setEnabled(listChoices, false);
		if ("boolean".equalsIgnoreCase(selectedType)) {
			this.ui.add(listChoices, this.ui.createListItem(getI18NString(ResourceMapperConstants.BOOLEAN_TRUE), 1));
			this.ui.add(listChoices, this.ui.createListItem(getI18NString(ResourceMapperConstants.BOOLEAN_FALSE), 0));
		}
		else if ("checklist".equalsIgnoreCase(selectedType) || 
				 "multichoice".equalsIgnoreCase(selectedType)) {
			this.ui.setEnabled(listChoices, true);
			if (this.field != null) {
				for (String choiceText : this.field.getChoices()) {
					this.ui.setEnabled(listChoices, true);
					Object listItem = this.ui.createListItem(choiceText, choiceText);
					this.ui.setIcon(listItem, "/icons/task_delete.png");
					this.ui.add(listChoices, listItem);
				}	
			}
			this.ui.setEnabled(this.buttonFieldAdd, false);
			this.ui.setEditable(this.textFieldChoice, true);
			this.ui.setEnabled(listChoices, true);
		}
	}
	
	public void textFieldChoiceChanged(Object textFieldChoice, Object listFieldChoices, Object buttonFieldAdd) {
		if (this.ui.getText(textFieldChoice).length() > 0) {
			this.ui.setEnabled(buttonFieldAdd, true);
		}
		else {
			this.ui.setEnabled(buttonFieldAdd, false);
		}
	}
	
	public void addFieldChoice(Object textFieldChoice, Object listChoices, Object buttonFieldAdd) {
		String choiceText = this.ui.getText(textFieldChoice);
		if (choiceText != null && choiceText.length() > 0) {
			Object listItem = this.ui.createListItem(choiceText, choiceText);
			this.ui.setIcon(listItem, "/icons/task_delete.png");
			this.ui.add(listChoices, listItem);
			this.ui.setText(textFieldChoice, "");
			this.ui.setEnabled(buttonFieldAdd, false);
		}
	}
	
	public void deleteFieldChoice(Object listFieldChoices) {
		Object choiceToDelete = this.ui.getSelectedItem(listFieldChoices);
		String choiceText = this.ui.getText(choiceToDelete);
		System.out.println("choice to delete: " + choiceText);
		this.ui.remove(choiceToDelete);
	}
}
