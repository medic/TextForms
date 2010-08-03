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

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperMessages;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingFactory;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageFieldsDialogHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class ManageFieldsDialogHandler implements ThinletUiEventHandler {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ManageFieldsDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/resourcemapper/manageFieldsDialog.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	private ResourceMapperCallback callback;
	
	private Object mainDialog;
	private Field field;
	
	private Object textName;
	private Object textKeyword;
	private Object textInfoSnippet;
	private Object comboFieldTypes;
	private Object listFieldChoices; 
	private Object labelFieldChoices;
	private Object panelFieldChoices;
	private Object textFieldChoice;
	private Object textSchema;
	private Object buttonFieldAdd;
	private Object placeholderChoices;
	
	private FieldMappingDao fieldMappingDao;
	
	public ManageFieldsDialogHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) { 
		LOG.debug("ManagePeopleDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.textName = this.ui.find(this.mainDialog, "textName");
		this.textKeyword = this.ui.find(this.mainDialog, "textKeyword");
		this.textInfoSnippet = this.ui.find(this.mainDialog, "textInfoSnippet");
		this.comboFieldTypes = this.ui.find(this.mainDialog, "comboFieldTypes"); 
		this.listFieldChoices = this.ui.find(this.mainDialog, "listFieldChoices");
		this.labelFieldChoices = this.ui.find(this.mainDialog, "labelFieldChoices");
		this.panelFieldChoices = this.ui.find(this.mainDialog, "panelFieldChoices");
		this.textFieldChoice = this.ui.find(this.mainDialog, "textFieldChoice");	
		this.textSchema = this.ui.find(this.mainDialog, "textSchema");
		this.buttonFieldAdd = this.ui.find(this.mainDialog, "buttonFieldAdd");
		this.placeholderChoices = this.ui.find(this.mainDialog, "placeholderChoices");
		this.fieldMappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
		
		loadFieldMappings();
	}
	
	public void show(Field field) {
		this.field = field;
		if (field != null) {
			this.ui.setText(this.textName, field.getName());
			this.ui.setText(this.textKeyword, field.getKeyword());
			this.ui.setText(this.textInfoSnippet, field.getInfoSnippet());
			this.ui.setText(this.textSchema, field.getSchemaName());
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
			this.ui.setText(this.textKeyword, "");
			this.ui.setText(this.textInfoSnippet, "");
			this.ui.setText(this.textSchema, "");
			this.ui.setSelectedIndex(this.comboFieldTypes, 0);
			this.ui.removeAll(this.listFieldChoices);
		}
		fieldTypeChanged(this.comboFieldTypes, this.panelFieldChoices, this.listFieldChoices);
		this.ui.add(this.mainDialog);
	}
	
	private void loadFieldMappings() {
		for (Field fieldClass : FieldMappingFactory.getFieldClasses()) {
			Object comboBoxChoice = this.ui.createComboboxChoice(fieldClass.getTypeLabel(), fieldClass.getType());
			this.ui.add(this.comboFieldTypes, comboBoxChoice);
		}
	}
	
	public void saveField(Object dialog) throws DuplicateKeyException {
		LOG.debug("saveField");
		String name = this.ui.getText(this.textName);
		String keyword = this.ui.getText(this.textKeyword);
		String infoSnippet = this.ui.getText(this.textInfoSnippet);
		String schema = this.ui.getText(this.textSchema);
		Object fieldType = this.ui.getSelectedItem(this.comboFieldTypes);
		String type = (String)this.ui.getAttachedObject(fieldType);
		List<String> choices = new ArrayList<String>();
		for (Object comboItem : this.ui.getItems(this.listFieldChoices)) {
			String comboItemText = this.ui.getAttachedObject(comboItem).toString().trim();
			LOG.debug("comboItemText: %s", comboItemText);
			choices.add(comboItemText);	
		}
		try {
			if (name == null || name.length() == 0) {
				this.ui.alert(ResourceMapperMessages.getMessageMissingField());
			}
			else if (keyword == null || keyword.length() == 0) {
				this.ui.alert(ResourceMapperMessages.getMessageMissingKeyword());
			}
			else if (type == null || type.length() == 0) {
				this.ui.alert(ResourceMapperMessages.getMessageMissingType());
			}
			else if (hasDuplicateChoices(choices)) {
				this.ui.alert(ResourceMapperMessages.getMessageDuplicateChoice());
			}
			else if (this.field != null && this.field.getType().equalsIgnoreCase(type)) {
				this.field.setName(name);
				this.field.setKeyword(keyword);
				this.field.setInfoSnippet(infoSnippet);
				this.field.setSchemaName(schema);
				this.field.setChoices(choices);
				this.fieldMappingDao.updateFieldMapping(this.field);
				this.callback.refreshField(this.field);
				this.ui.remove(dialog);
			}
			else {
				if (this.field != null) {
					LOG.debug("Existing Field Deleted!");
					this.fieldMappingDao.deleteFieldMapping(this.field);
				}
				Field newField = FieldMappingFactory.createField(name, keyword, infoSnippet, type, schema, choices);
				if (newField != null) {
					LOG.debug("New Field Created!");
					this.fieldMappingDao.saveFieldMapping(newField);
				}
				this.callback.refreshField(newField);
				this.ui.remove(dialog);
			}
		}
		catch (DuplicateKeyException ex) {
			LOG.error("DuplicateKeyException: %s", ex);
			this.ui.alert(ResourceMapperMessages.getMessageDuplicateKeyword());
		}
	}
	
	private boolean hasDuplicateChoices(List<String> choices) {
		Set<String> set = new HashSet<String>(choices);
		return set.size() < choices.size();
	}
	
	private boolean isDuplicateChoice(Object list, String value) {
		for (Object item : this.ui.getItems(list)) {
			if (value.equalsIgnoreCase(this.ui.getText(item))) {
				return true;
			}
		}
		return false;
	}
	
	public void removeDialog(Object dialog) {
		LOG.debug("removeDialog");
		this.ui.remove(dialog);
	}
	
	public void fieldTypeChanged(Object comboTypes, Object panelChoices, Object listChoices) {
		LOG.debug("typeChanged");
		Object selectedItem = this.ui.getSelectedItem(comboTypes);
		Object attachedObject = selectedItem != null ? this.ui.getAttachedObject(selectedItem) : null;
		String selectedType = attachedObject != null ? attachedObject.toString() : null;
		this.ui.removeAll(listChoices);
		this.ui.setEnabled(this.buttonFieldAdd, false);
		this.ui.setEditable(this.textFieldChoice, false);
		if ("boolean".equalsIgnoreCase(selectedType)) {
			this.ui.add(listChoices, this.ui.createListItem(ResourceMapperMessages.getMessageTrue(), 1));
			this.ui.add(listChoices, this.ui.createListItem(ResourceMapperMessages.getMessageFalse(), 0));
			this.ui.setEnabled(listChoices, false);
			setChoicesVisible(true);
		}
		else if ("checklist".equalsIgnoreCase(selectedType) || 
				 "multichoice".equalsIgnoreCase(selectedType)) {
			if (this.field != null) {
				for (String choiceText : this.field.getChoices()) {
					this.ui.setEnabled(listChoices, true);
					this.ui.add(listChoices, this.ui.createListItem(choiceText, choiceText));
				}	
			}
			this.ui.setEnabled(listChoices, true);
			this.ui.setEnabled(this.buttonFieldAdd, false);
			this.ui.setEditable(this.textFieldChoice, true);
			this.ui.setEnabled(listChoices, true);
			setChoicesVisible(true);
		}
		else {
			this.ui.setEnabled(listChoices, false);
			setChoicesVisible(false);
		}
	}
	
	private void setChoicesVisible(boolean visible) {
		this.ui.setVisible(this.labelFieldChoices, visible);
		this.ui.setVisible(this.panelFieldChoices, visible);
		this.ui.setVisible(this.placeholderChoices, !visible);
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
			if (isDuplicateChoice(listChoices, choiceText)) {
				this.ui.alert(ResourceMapperMessages.getMessageDuplicateChoice());
			}
			else {
				this.ui.add(listChoices, this.ui.createListItem(choiceText, choiceText));
				this.ui.setText(textFieldChoice, "");
				this.ui.setEnabled(buttonFieldAdd, false);
			}
		}
	}
	
	public void fieldChoiceChanged(Object listFieldChoices, Object buttonFieldDelete) {
		Object selectedItem = this.ui.getSelectedItem(listFieldChoices);
		this.ui.setEnabled(buttonFieldDelete, selectedItem != null);
	}
	
	public void deleteChoice(Object listFieldChoices, Object buttonFieldDelete) {
		Object choiceToDelete = this.ui.getSelectedItem(listFieldChoices);
		String choiceText = this.ui.getText(choiceToDelete);
		LOG.debug("choice to delete: %s", choiceText);
		this.ui.remove(choiceToDelete);
		this.ui.setEnabled(buttonFieldDelete, false);
	}
}
