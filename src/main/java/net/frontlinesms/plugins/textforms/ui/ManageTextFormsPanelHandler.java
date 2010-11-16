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
package net.frontlinesms.plugins.textforms.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.awt.Color;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.textforms.TextFormsCallback;
import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.TextFormsPluginController;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.repository.TextFormDao;
import net.frontlinesms.plugins.textforms.search.TextFormQueryGenerator;
import net.frontlinesms.plugins.textforms.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.textforms.ui.components.PagedAdvancedTableController;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageTextFormsPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManageTextFormsPanelHandler extends ExtendedThinlet implements ThinletUiEventHandler, AdvancedTableActionDelegate, EventObserver {
	
	private static final long serialVersionUID = 1L;
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(ManageTextFormsPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/textforms/manageTextFormsPanel.xml";
	
	private final FrontlineSMS frontline;
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final TextFormsPluginController pluginController;
	private final TextFormsCallback callback;
	
	private final Object mainPanel;
	private final Object searchTextForms;
	private final Object panelTextForms;
	private final Object tableTextForms;
	private final Object buttonDeleteTextForm;
	private final Object buttonEditTextForm;
	private final Object buttonSendTextForm;
	private final Object tableQuestions;
	
	private final TextFormQueryGenerator queryGenerator;
	
	private final TextFormDao textformDao;
	
	public ManageTextFormsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, TextFormsCallback callback, FrontlineSMS frontlineController, TextFormsPluginController pluginController) {
		LOG.debug("ManageTextFormsPanelHandler");
		this.frontline = frontlineController;
		this.ui = ui;
		this.appContext = appContext;
		this.pluginController = pluginController;
		frontlineController.getEventBus().registerObserver(this);
		
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		
		this.searchTextForms = this.ui.find(this.mainPanel, "searchTextForms");
		this.tableTextForms = this.ui.find(this.mainPanel, "tableTextForms");
		this.panelTextForms = this.ui.find(this.mainPanel, "panelTextForms");
		this.buttonDeleteTextForm = this.ui.find(this.mainPanel, "buttonDeleteTextForm");
		this.buttonEditTextForm = this.ui.find(this.mainPanel, "buttonEditTextForm");
		this.buttonSendTextForm = this.ui.find(this.mainPanel, "buttonSendTextForm");
		this.tableQuestions = this.ui.find(this.mainPanel, "tableQuestions");
		
		this.textformDao = (TextFormDao) appContext.getBean("textformDao", TextFormDao.class);
		
		PagedAdvancedTableController textformTableController = getPagedAdvancedTableController(this.tableTextForms, this.panelTextForms, TextForm.class, 
																new String[]{TextFormsMessages.getTextFormName(), TextFormsMessages.getTextFormKeyword(), TextFormsMessages.getTextFormQuestions()}, 
																new String[]{"getName", "getKeyword", "getQuestionNames"},
																new String[]{"/icons/textform.png", "/icons/question_keyword.png", "/icons/question.png"},
																new String[]{"name", "keyword"});
		this.queryGenerator = new TextFormQueryGenerator(this.appContext, textformTableController);
		textformTableController.setQueryGenerator(this.queryGenerator);
		this.queryGenerator.startSearch("");
		
		focusLost(this.searchTextForms);
	}
	
	private PagedAdvancedTableController getPagedAdvancedTableController(Object table, Object panel, Class<?> clazz, String[] columnNames, String[] columnMethods, String[] columnIcons, String[] columnSorts) {
		PagedAdvancedTableController tableController = new PagedAdvancedTableController(this, appContext, ui, table, panel);
		tableController.putHeader(clazz, columnNames, columnMethods, columnIcons, columnSorts);
		tableController.setResultsPhrases(getI18NString(TextFormsConstants.TABLE_RESULTS), 
										  getI18NString(TextFormsConstants.TABLE_NO_RESULTS), 
										  getI18NString(TextFormsConstants.TABLE_NO_SEARCH_RESULTS));
		tableController.setPagingPhrases(getI18NString(TextFormsConstants.TABLE_TO), 
										 getI18NString(TextFormsConstants.TABLE_OF));
		return tableController;
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	public void reloadData() {
		this.queryGenerator.refresh();
	}
	
	public void focus(Object component) {
		if (component != null) {
			this.ui.requestFocus(component);
		}
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this, TextFormsConstants.CONFIRM_DELETE_textform);
	}
	
	public void addTextForm() {
		LOG.debug("addTextForm");
		ManageTextFormsDialogHandler dialog = new ManageTextFormsDialogHandler(ui, appContext, callback);
		dialog.show(null);
	}
	
	public void deleteTextForm() {
		LOG.debug("deleteTextForm");
		TextForm textform = this.getSelectedTextForm();
		if (textform != null) {
			this.textformDao.deleteTextForm(textform);
			this.queryGenerator.refresh();
		}
		this.ui.removeConfirmationDialog();
	}
	
	public void editTextForm() {
		LOG.debug("editTextForm");
		ManageTextFormsDialogHandler dialog = new ManageTextFormsDialogHandler(ui, appContext, callback);
		dialog.show(getSelectedTextForm());
	}
	
	public void sendTextForm() {
		LOG.debug("sendTextForm");
		SendTextFormDialogHandler sendTextFormDialog = new SendTextFormDialogHandler(frontline, ui, appContext, pluginController);
		TextForm textform = this.getSelectedTextForm();
		if (textform != null) {
			sendTextFormDialog.show(textform);
		}
	}
	
	public void searchTextForms(Object searchTextForms, Object tableTextForms) {
		String searchText = this.ui.getText(searchTextForms);
		if (searchText.equalsIgnoreCase(TextFormsMessages.getMessageSearchTextForms())) {
			this.queryGenerator.startSearch("");
		}
		else {
			LOG.debug("searchTextForms: %s", searchText);
			this.queryGenerator.startSearch(searchText);	
		}
	}
	
	private TextForm getSelectedTextForm() {
		final Object selectedItem = this.ui.getSelectedItem(this.tableTextForms);
		if (selectedItem != null) {
			return (TextForm)this.ui.getAttachedObject(selectedItem, TextForm.class);
		}
		return null;
	}
	
	public void doubleClickAction(Object selectedObject) {
		LOG.debug("doubleClickAction");
		ManageTextFormsDialogHandler dialog = new ManageTextFormsDialogHandler(ui, appContext, callback);
		dialog.show(getSelectedTextForm());
	}

	public void resultsChanged() {
		LOG.debug("resultsChanged");
		selectionChanged(null);
	}

	public void sortChanged(String column, boolean ascending) {
		LOG.debug("sortChanged: column=%s ascending=%s", column, ascending);
		String searchText = this.ui.getText(this.searchTextForms);
		this.queryGenerator.startSearch(searchText, column, ascending);
	}
	
	public void focusGained(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText.equalsIgnoreCase(TextFormsMessages.getMessageSearchTextForms())) {
			this.ui.setText(textfield, "");
		}
		this.ui.setForeground(Color.BLACK);
	}
	
	public void focusLost(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText == null || searchText.length() == 0) {
			this.ui.setText(textfield, TextFormsMessages.getMessageSearchTextForms());
			this.ui.setForeground(Color.LIGHT_GRAY);
		}
		else {
			this.ui.setForeground(Color.BLACK);
		}
	}

	public void selectionChanged(Object selectedObject) {
		boolean hasSelectedItem = selectedObject != null;
		LOG.debug("selectionChanged:%s", hasSelectedItem);
		this.ui.setEnabled(this.buttonEditTextForm, hasSelectedItem);
		this.ui.setEnabled(this.buttonDeleteTextForm, hasSelectedItem);
		this.ui.setEnabled(this.buttonSendTextForm, hasSelectedItem);
		
		this.ui.removeAll(this.tableQuestions);
		TextForm textform = (TextForm)selectedObject;
		if (textform != null) {
			for (Question question : textform.getQuestions()) {
				this.ui.add(tableQuestions, getRow(question));
			}	
		}
	}
	
	public Object getRow(Question question){
		Object row = this.ui.createTableRow(question);
		this.createTableCell(row, question.getName());
		this.createTableCell(row, question.getKeyword());
		this.createTableCell(row, question.getTypeLabel());
		this.createTableCell(row, question.getSchemaName());
		this.createTableCell(row, question.getInfoSnippet());
		return row;
	}
	
	public void notify(FrontlineEventNotification notification) {
		if (notification instanceof DatabaseEntityNotification<?>) {
			DatabaseEntityNotification<?> databaseEntityNotification = (DatabaseEntityNotification<?>)notification;
			if (databaseEntityNotification.getDatabaseEntity() instanceof TextForm) {
				this.queryGenerator.refresh();
			}
		}
	}
}