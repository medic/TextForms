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

import org.springframework.context.ApplicationContext;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperCallback;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperMessages;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldMappingDao;
import net.frontlinesms.plugins.resourcemapper.search.FieldMappingQueryGenerator;
import net.frontlinesms.plugins.resourcemapper.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.resourcemapper.ui.components.PagedAdvancedTableController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageFieldsPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManageFieldsPanelHandler implements ThinletUiEventHandler, AdvancedTableActionDelegate {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(ManageFieldsPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/resourcemapper/manageFieldsPanel.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainPanel;
	private ManageFieldsDialogHandler editDialog;
	private ResourceMapperCallback callback;
	
	private Object searchField;
	private Object panelFields;
	private Object tableFields;
	
	private Object labelNameValue;
	private Object labelKeywordValue;
	private Object labelTypeValue;
	private Object labelInfoValue;
	private Object listChoices;
	private Object labelSchemaValue;
	private Object labelChoices;
	
	private Object editButton;
	private Object deleteButton;
	private Object viewResponsesButton;
	
	private FieldMappingQueryGenerator queryGenerator;
	private PagedAdvancedTableController tableController;
	
	private FieldMappingDao fieldMappingDao;
	
	public ManageFieldsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		LOG.debug("ManageFieldsPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		
		this.searchField = this.ui.find(this.mainPanel, "searchField");
		this.tableFields = this.ui.find(this.mainPanel, "tableFields");
		this.panelFields = this.ui.find(this.mainPanel, "panelFields");
		
		this.labelNameValue = this.ui.find(this.mainPanel, "labelNameValue");
		this.labelKeywordValue = this.ui.find(this.mainPanel, "labelKeywordValue");
		this.labelTypeValue = this.ui.find(this.mainPanel, "labelTypeValue");
		this.labelInfoValue = this.ui.find(this.mainPanel, "labelInfoValue");
		this.listChoices = this.ui.find(this.mainPanel, "listChoices");
		this.labelChoices = this.ui.find(this.mainPanel, "labelChoices");
		this.labelSchemaValue = this.ui.find(this.mainPanel, "labelSchemaValue");
			
		this.editDialog = new ManageFieldsDialogHandler(this.ui, this.appContext, callback);
		this.editButton = this.ui.find(this.mainPanel, "buttonEditField");
		this.deleteButton = this.ui.find(this.mainPanel, "buttonDeleteField");
		this.viewResponsesButton = this.ui.find(this.mainPanel, "buttonViewResponses");
	
		this.fieldMappingDao = (FieldMappingDao) appContext.getBean("fieldMappingDao");
		
		this.tableController = new PagedAdvancedTableController(this, this.appContext, this.ui, this.tableFields, this.panelFields);
		this.tableController.putHeader(Field.class, 
									   new String[]{getI18NString(ResourceMapperConstants.TABLE_FIELDNAME), 
													getI18NString(ResourceMapperConstants.TABLE_KEYWORD), 
													getI18NString(ResourceMapperConstants.TABLE_TYPE),
													getI18NString(ResourceMapperConstants.TABLE_SCHEMA)}, 
									   new String[]{"getName", "getKeyword", "getTypeLabel", "getSchemaName"},
									   new String[]{"/icons/field.png", "/icons/description.png", "/icons/type.png", "/icons/httpRequest.png"},
									   new String[]{"name", "keyword", "class", "schemaName"});
		this.queryGenerator = new FieldMappingQueryGenerator(this.appContext, this.tableController);
		this.tableController.setQueryGenerator(this.queryGenerator);
		this.tableController.setResultsPhrases(getI18NString(ResourceMapperConstants.TABLE_RESULTS), 
											   getI18NString(ResourceMapperConstants.TABLE_NO_RESULTS), 
											   getI18NString(ResourceMapperConstants.TABLE_NO_SEARCH_RESULTS));
		this.tableController.setPagingPhrases(getI18NString(ResourceMapperConstants.TABLE_TO), 
											  getI18NString(ResourceMapperConstants.TABLE_OF));
		this.queryGenerator.startSearch("");
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	public void focus(Object component) {
		if (component != null) {
			this.ui.requestFocus(component);
		}
	}
	
	public void addField(Object tableField) {
		LOG.debug("addField");
		this.editDialog.show(null);
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this, ResourceMapperConstants.CONFIRM_DELETE_FIELD);
	}
	
	public void deleteField() {
		LOG.debug("deleteField");
		Field field = this.getSelectedField();
		if (field != null) {
			this.fieldMappingDao.deleteFieldMapping(field);
		}
		this.ui.removeConfirmationDialog();
		this.refreshFields(null);
	}
	
	public void refreshFields(Field field) {
		String searchText = this.ui.getText(this.searchField);
		this.queryGenerator.startSearch(searchText);
	}
	
	public void editField(Object panelField) {
		this.editDialog.show(this.getSelectedField());
	}
	
	public void searchByField(Object searchField, Object tableField, Object buttonClear) {
		String searchText = this.ui.getText(searchField);
		LOG.debug("searchByField: %s", searchText);
		this.queryGenerator.startSearch(searchText);
		this.ui.setEnabled(buttonClear, searchText != null && searchText.length() > 0);
	}
	
	public void searchClear(Object searchField, Object tableField, Object buttonClear) {
		LOG.debug("searchClear");
		this.ui.setText(searchField, "");
		this.searchByField(searchField, tableField, buttonClear);
		this.ui.requestFocus(searchField);
	}
	
	public void viewResponses(Object panelField) {
		LOG.debug("viewResponses");
		if (this.callback != null) {
			this.callback.viewResponses(this.getSelectedField());
		}	
	}
	
	private Field getSelectedField() {
		final Object selectedRow = this.ui.getSelectedItem(this.tableFields);
		if (selectedRow != null) {
			return (Field)this.ui.getAttachedObject(selectedRow, Field.class);
		}
		return null;
	}
	
	public void doubleClickAction(Object selectedObject) {
		LOG.debug("doubleClickAction");
		this.editDialog.show(this.getSelectedField());
	}

	public void resultsChanged() {
		LOG.debug("resultsChanged");
		selectionChanged(null);
	}

	public void sortChanged(String column, boolean ascending) {
		LOG.debug("sortChanged: column=%s ascending=%s", column, ascending);
		String searchText = this.ui.getText(this.searchField);
		this.queryGenerator.startSearch(searchText, column, ascending);
	}
	
	public void selectionChanged(Object selectedObject) {
		LOG.debug("selectionChanged");
		Field field = this.getSelectedField();
		if (field != null) {
			this.ui.setEnabled(this.editButton, true);
			this.ui.setEnabled(this.deleteButton, true);
			this.ui.setEnabled(this.viewResponsesButton, true);
			this.ui.setText(this.labelNameValue, field.getName());
			this.ui.setText(this.labelKeywordValue, field.getKeyword());
			this.ui.setText(this.labelTypeValue, field.getTypeLabel());
			this.ui.setText(this.labelInfoValue, field.getInfoSnippet());
			this.ui.setText(this.labelSchemaValue, field.getSchemaName());
			this.ui.removeAll(this.listChoices);
			if ("boolean".equalsIgnoreCase(field.getType())) {
				this.ui.add(this.listChoices, this.ui.createListItem(ResourceMapperMessages.getMessageTrue(), 1));
				this.ui.add(this.listChoices, this.ui.createListItem(ResourceMapperMessages.getMessageFalse(), 0));
				this.ui.setVisible(this.labelChoices, true);
				this.ui.setVisible(this.listChoices, true);
			}
			else if (field.getChoices() != null) {
				for (String choiceText : field.getChoices()) {
					LOG.debug("choiceText: %s", choiceText);
					this.ui.add(this.listChoices, this.ui.createListItem(choiceText, choiceText));
				}	
				this.ui.setVisible(this.labelChoices, true);
				this.ui.setVisible(this.listChoices, true);
			}
			else {
				this.ui.setVisible(this.labelChoices, false);
				this.ui.setVisible(this.listChoices, false);
			}
		}
		else {
			this.ui.setEnabled(this.editButton, false);
			this.ui.setEnabled(this.deleteButton, false);
			this.ui.setEnabled(this.viewResponsesButton, false);
			this.ui.setText(this.labelNameValue, "");
			this.ui.setText(this.labelKeywordValue, "");
			this.ui.setText(this.labelTypeValue, "");
			this.ui.setText(this.labelInfoValue, "");
			this.ui.setText(this.labelSchemaValue, "");
			this.ui.removeAll(this.listChoices);
			this.ui.setVisible(this.labelChoices, false);
			this.ui.setVisible(this.listChoices, false);
		}
	}
}