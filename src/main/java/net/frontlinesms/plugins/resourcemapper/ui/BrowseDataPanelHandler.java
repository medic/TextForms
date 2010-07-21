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
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.plugins.resourcemapper.data.domain.HospitalContact;
import net.frontlinesms.plugins.resourcemapper.data.domain.mapping.Field;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.data.repository.FieldResponseDao;
import net.frontlinesms.plugins.resourcemapper.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.resourcemapper.search.FieldResponseQueryGenerator;
import net.frontlinesms.plugins.resourcemapper.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.resourcemapper.ui.components.PagedAdvancedTableController;

/*
 * BrowseDataPanelHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class BrowseDataPanelHandler implements ThinletUiEventHandler, AdvancedTableActionDelegate {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(BrowseDataPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/resourcemapper/browseDataPanel.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainPanel;
	private BrowseDataDialogHandler editDialog;
	private ResourceMapperCallback callback;
	
	private HospitalContact selectedContact;
	private HospitalContactDao hospitalContactDao;
	private FieldResponseDao fieldResponseDao;
	
	private Field selectedField;
	private Object comboSubmitter;
	private Object searchField;
	private Object textDate;
	
	private Object panelFields;
	private Object tableFields;
	
	private Object editButton;
	private Object deleteButton;
	
	private String sortColumn;
	private boolean sortAscending;
	
	private FieldResponseQueryGenerator queryGenerator;
	private PagedAdvancedTableController tableController;
	
	public BrowseDataPanelHandler(UiGeneratorController ui, ApplicationContext appContext, ResourceMapperCallback callback) {
		LOG.debug("BrowseDataPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		this.editDialog = new BrowseDataDialogHandler(this.ui, this.appContext, callback);
		
		this.hospitalContactDao = (HospitalContactDao)appContext.getBean("hospitalContactDao");
		this.fieldResponseDao = (FieldResponseDao)appContext.getBean("fieldResponseDao");
		
		this.tableFields = this.ui.find(this.mainPanel, "tableFields");
		this.panelFields = this.ui.find(this.mainPanel, "panelFields");
		this.searchField = this.ui.find(this.mainPanel, "searchField");
		this.comboSubmitter = this.ui.find(this.mainPanel, "comboSubmitter");
		this.textDate = this.ui.find(this.mainPanel, "textDate");
		
		this.editButton = this.ui.find(this.mainPanel, "buttonEditResponse");
		this.deleteButton = this.ui.find(this.mainPanel, "buttonDeleteResponse");
		
		this.tableController = new PagedAdvancedTableController(this, this.appContext, this.ui, this.tableFields, this.panelFields);
		this.tableController.putHeader(FieldResponse.class, 
									   new String[]{getI18NString(ResourceMapperConstants.TABLE_DATE),
													getI18NString(ResourceMapperConstants.TABLE_SUBMITTER),
													getI18NString(ResourceMapperConstants.TABLE_HOSPITAL),
													getI18NString(ResourceMapperConstants.TABLE_TYPE),
													getI18NString(ResourceMapperConstants.TABLE_FIELD),
													getI18NString(ResourceMapperConstants.TABLE_ABBREV),
													getI18NString(ResourceMapperConstants.TABLE_RESPONSE)}, 
									   new String[]{"getDateSubmittedText", 
													"getSubmitterName", 
													"getHospitalId", 
													"getMappingTypeLabel", 
													"getMappingName", 
													"getMappingAbbreviation", 
													"getMessageText"},
									   new String[]{"/icons/date.png", 
													"/icons/user_sender.png", 
													"/icons/port_open.png", 
													"/icons/tip.png", 
													"/icons/keyword.png", 
													"/icons/description.png", 
													"/icons/sms_receive.png"},
									   new String []{"dateSubmitted", 
													 "submitter.name",
													 "hospitalId",
													 "mapping.class",
													 "mapping.abbreviation",
													 "mapping.name",
													 "message.textMessageContent"});
		this.queryGenerator = new FieldResponseQueryGenerator(this.appContext, this.tableController);
		this.tableController.setQueryGenerator(this.queryGenerator);
		this.tableController.setResultsPhrases(getI18NString(ResourceMapperConstants.TABLE_RESULTS), 
											   getI18NString(ResourceMapperConstants.TABLE_NO_RESULTS), 
											   getI18NString(ResourceMapperConstants.TABLE_NO_SEARCH_RESULTS));
		this.tableController.setPagingPhrases(getI18NString(ResourceMapperConstants.TABLE_TO), 
											  getI18NString(ResourceMapperConstants.TABLE_OF));
		startSearch();
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}

	public void loadHospitalContacts() {
		this.ui.removeAll(this.comboSubmitter);
		this.ui.add(this.comboSubmitter, this.ui.createComboboxChoice("", null));
		for (HospitalContact contact : this.hospitalContactDao.getAllHospitalContacts()) {
			Object comboboxChoice = this.ui.createComboboxChoice(contact.getName(), contact);
			this.ui.setIcon(comboboxChoice, "/icons/user.png");
			this.ui.add(this.comboSubmitter, comboboxChoice);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void refreshFieldResponses(FieldResponse fieldResponse) {
		startSearch();
	}
	
	public void showDateSelecter(Object textField) {
		LOG.debug("showDateSelecter");
		this.ui.showDateSelecter(textField);
	}
	
	public void dateChanged(Object textDate, Object buttonClear) {
		LOG.debug("dateChanged");
		String dateText = this.ui.getText(textDate);
		this.ui.setEnabled(buttonClear, dateText != null && dateText.length() > 0);
		startSearch();
	}
	
	public void clearDate(Object textDate, Object buttonClear) {
		LOG.debug("clearDate");
		this.ui.setText(textDate, "");
		this.ui.setEnabled(buttonClear, false);
		startSearch();
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this);
	}
	
	public void searchByField(Object searchField, Object buttonClear) {
		String searchText = this.ui.getText(searchField);
		LOG.debug("searchByField: %s", searchText);
		this.ui.setEnabled(buttonClear, searchText != null && searchText.length() > 0);
		startSearch();
	}
	
	public void clearSearch(Object searchField, Object buttonClear) {
		LOG.debug("clearSearch");
		this.ui.setText(searchField, "");
		this.searchByField(searchField, buttonClear);
		this.ui.requestFocus(searchField);
	}
	
	private void startSearch() {
		String text = this.ui.getText(this.searchField);
		String date = this.ui.getText(this.textDate);
		String contact = null;
		Object selectedSubmitter = this.ui.getSelectedItem(this.comboSubmitter);
		if (selectedSubmitter != null) {
			HospitalContact submitter = (HospitalContact)this.ui.getAttachedObject(selectedSubmitter, HospitalContact.class);
			if (submitter != null) {
				LOG.debug("submitterChanged: %s", submitter.getName());
				contact = submitter.getName();
			}
		}
		this.queryGenerator.startSearch(text, this.sortColumn, this.sortAscending,  date, contact);
	}
	
	public void submitterChanged(Object comboSubmitter) {
		startSearch();
	}
	
	public void setSelectedField(Field field) {
		LOG.debug("setSelectedField: %s", field);
		this.selectedField = field;
		Object searchField = this.ui.find(this.mainPanel, "searchField");
		if (field != null) {
			this.ui.setText(searchField, field.getName());
		}
		else {
			this.ui.setText(searchField, "");
		}
		startSearch();
	}
	
	public void setSelectedContact(HospitalContact contact) {
		LOG.debug("setSelectedContact: %s", contact);
		this.selectedContact = contact;
		if (contact != null) {
			int index = 0;
			for (Object comboboxChoice : this.ui.getItems(this.comboSubmitter)) {
				Object attachedObject = this.ui.getAttachedObject(comboboxChoice);
				if (attachedObject != null) {
					HospitalContact contactItem = (HospitalContact)attachedObject;
					if (contact.equals(contactItem)) {
						this.ui.setSelectedIndex(this.comboSubmitter, index);
						LOG.debug("Selecting Contact: %s", contact.getName());
						break;
					}
				}
				index++;
			}
		}
		else {
			this.ui.setSelectedIndex(this.comboSubmitter, 0);
		}
		startSearch();
	}
	
	public void addResponse(Object tableField) {
		LOG.debug("addResponse");
		this.editDialog.loadHospitalContacts();
		this.editDialog.loadFieldMappings();
		this.editDialog.show(null);
	}
	
	public void editResponse(Object tableField) {
		LOG.debug("editResponse");
		this.editDialog.loadHospitalContacts();
		this.editDialog.loadFieldMappings();
		this.editDialog.show(this.getSelectedFieldResponse());
	}
	
	@SuppressWarnings("unchecked")
	public void deleteField() {
		LOG.debug("deleteField");
		FieldResponse fieldResponse = this.getSelectedFieldResponse();
		if (fieldResponse != null) {
			this.fieldResponseDao.deleteFieldResponse(fieldResponse);
		}
	}
	
	public void doubleClickAction(Object selectedObject) {
		LOG.debug("doubleClickAction");
		this.editDialog.loadHospitalContacts();
		this.editDialog.loadFieldMappings();
		this.editDialog.show(this.getSelectedFieldResponse());
	}

	public void resultsChanged() {
		LOG.debug("resultsChanged");
	}

	public void sortChanged(String column, boolean ascending) {
		LOG.debug("sortChanged: column=%s ascending=%s", column, ascending);
		this.sortColumn = column;
		this.sortAscending = ascending;
		startSearch();
	}
	
	@SuppressWarnings("unchecked")
	public void selectionChanged(Object selectedObject) {
		LOG.debug("selectionChanged");
		FieldResponse fieldResponse = this.getSelectedFieldResponse();
		if (fieldResponse != null) {
			this.ui.setEnabled(this.editButton, true);
			this.ui.setEnabled(this.deleteButton, true);
		}
		else {
			this.ui.setEnabled(this.editButton, false);
			this.ui.setEnabled(this.deleteButton, false);
		}
	}
	
	@SuppressWarnings("unchecked")
	private FieldResponse getSelectedFieldResponse() {
		final Object selectedRow = this.ui.getSelectedItem(this.tableFields);
		if (selectedRow != null) {
			return (FieldResponse)this.ui.getAttachedObject(selectedRow, FieldResponse.class);
		}
		return null;
	}

}
