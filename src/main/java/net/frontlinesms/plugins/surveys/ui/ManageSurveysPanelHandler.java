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
package net.frontlinesms.plugins.surveys.ui;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

import java.awt.Color;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.surveys.SurveysCallback;
import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.SurveysPluginController;
import net.frontlinesms.plugins.surveys.data.domain.Survey;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.repository.SurveyDao;
import net.frontlinesms.plugins.surveys.search.SurveyQueryGenerator;
import net.frontlinesms.plugins.surveys.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.surveys.ui.components.PagedAdvancedTableController;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageSurveysPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManageSurveysPanelHandler extends ExtendedThinlet implements ThinletUiEventHandler, AdvancedTableActionDelegate, EventObserver {
	
	private static final long serialVersionUID = 1L;
	private static final SurveysLogger LOG = SurveysLogger.getLogger(ManageSurveysPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/surveys/manageSurveysPanel.xml";
	
	private final FrontlineSMS frontline;
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final SurveysPluginController pluginController;
	private final SurveysCallback callback;
	
	private final Object mainPanel;
	private final Object searchSurveys;
	private final Object panelSurveys;
	private final Object tableSurveys;
	private final Object buttonDeleteSurvey;
	private final Object buttonEditSurvey;
	private final Object buttonSendSurvey;
	private final Object tableQuestions;
	
	private final SurveyQueryGenerator queryGenerator;
	
	private final SurveyDao surveyDao;
	
	public ManageSurveysPanelHandler(UiGeneratorController ui, ApplicationContext appContext, SurveysCallback callback, FrontlineSMS frontlineController, SurveysPluginController pluginController) {
		LOG.debug("ManageSurveysPanelHandler");
		this.frontline = frontlineController;
		this.ui = ui;
		this.appContext = appContext;
		this.pluginController = pluginController;
		frontlineController.getEventBus().registerObserver(this);
		
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		
		this.searchSurveys = this.ui.find(this.mainPanel, "searchSurveys");
		this.tableSurveys = this.ui.find(this.mainPanel, "tableSurveys");
		this.panelSurveys = this.ui.find(this.mainPanel, "panelSurveys");
		this.buttonDeleteSurvey = this.ui.find(this.mainPanel, "buttonDeleteSurvey");
		this.buttonEditSurvey = this.ui.find(this.mainPanel, "buttonEditSurvey");
		this.buttonSendSurvey = this.ui.find(this.mainPanel, "buttonSendSurvey");
		this.tableQuestions = this.ui.find(this.mainPanel, "tableQuestions");
		
		this.surveyDao = (SurveyDao) appContext.getBean("surveyDao", SurveyDao.class);
		
		PagedAdvancedTableController surveyTableController = getPagedAdvancedTableController(this.tableSurveys, this.panelSurveys, Survey.class, 
																new String[]{SurveysMessages.getSurveyName(), SurveysMessages.getSurveyKeyword(), SurveysMessages.getSurveyQuestions()}, 
																new String[]{"getName", "getKeyword", "getQuestionNames"},
																new String[]{"/icons/survey.png", "/icons/question_keyword.png", "/icons/question.png"},
																new String[]{"name", "keyword"});
		this.queryGenerator = new SurveyQueryGenerator(this.appContext, surveyTableController);
		surveyTableController.setQueryGenerator(this.queryGenerator);
		this.queryGenerator.startSearch("");
		
		focusLost(this.searchSurveys);
	}
	
	private PagedAdvancedTableController getPagedAdvancedTableController(Object table, Object panel, Class<?> clazz, String[] columnNames, String[] columnMethods, String[] columnIcons, String[] columnSorts) {
		PagedAdvancedTableController tableController = new PagedAdvancedTableController(this, appContext, ui, table, panel);
		tableController.putHeader(clazz, columnNames, columnMethods, columnIcons, columnSorts);
		tableController.setResultsPhrases(getI18NString(SurveysConstants.TABLE_RESULTS), 
										  getI18NString(SurveysConstants.TABLE_NO_RESULTS), 
										  getI18NString(SurveysConstants.TABLE_NO_SEARCH_RESULTS));
		tableController.setPagingPhrases(getI18NString(SurveysConstants.TABLE_TO), 
										 getI18NString(SurveysConstants.TABLE_OF));
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
		this.ui.showConfirmationDialog(methodToBeCalled, this, SurveysConstants.CONFIRM_DELETE_SURVEY);
	}
	
	public void addSurvey() {
		LOG.debug("addSurvey");
		ManageSurveysDialogHandler dialog = new ManageSurveysDialogHandler(ui, appContext, callback);
		dialog.show(null);
	}
	
	public void deleteSurvey() {
		LOG.debug("deleteSurvey");
		Survey survey = this.getSelectedSurvey();
		if (survey != null) {
			this.surveyDao.deleteSurvey(survey);
			this.queryGenerator.refresh();
		}
		this.ui.removeConfirmationDialog();
	}
	
	public void editSurvey() {
		LOG.debug("editSurvey");
		ManageSurveysDialogHandler dialog = new ManageSurveysDialogHandler(ui, appContext, callback);
		dialog.show(getSelectedSurvey());
	}
	
	public void sendSurvey() {
		LOG.debug("sendSurvey");
		SendSurveyDialogHandler sendSurveyDialog = new SendSurveyDialogHandler(frontline, ui, appContext, pluginController);
		Survey survey = this.getSelectedSurvey();
		if (survey != null) {
			sendSurveyDialog.show(survey);
		}
	}
	
	public void searchSurveys(Object searchSurveys, Object tableSurveys) {
		String searchText = this.ui.getText(searchSurveys);
		if (searchText.equalsIgnoreCase(SurveysMessages.getMessageSearchSurveys())) {
			this.queryGenerator.startSearch("");
		}
		else {
			LOG.debug("searchSurveys: %s", searchText);
			this.queryGenerator.startSearch(searchText);	
		}
	}
	
	private Survey getSelectedSurvey() {
		final Object selectedItem = this.ui.getSelectedItem(this.tableSurveys);
		if (selectedItem != null) {
			return (Survey)this.ui.getAttachedObject(selectedItem, Survey.class);
		}
		return null;
	}
	
	public void doubleClickAction(Object selectedObject) {
		LOG.debug("doubleClickAction");
		ManageSurveysDialogHandler dialog = new ManageSurveysDialogHandler(ui, appContext, callback);
		dialog.show(getSelectedSurvey());
	}

	public void resultsChanged() {
		LOG.debug("resultsChanged");
		selectionChanged(null);
	}

	public void sortChanged(String column, boolean ascending) {
		LOG.debug("sortChanged: column=%s ascending=%s", column, ascending);
		String searchText = this.ui.getText(this.searchSurveys);
		this.queryGenerator.startSearch(searchText, column, ascending);
	}
	
	public void focusGained(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText.equalsIgnoreCase(SurveysMessages.getMessageSearchSurveys())) {
			this.ui.setText(textfield, "");
		}
		this.ui.setForeground(Color.BLACK);
	}
	
	public void focusLost(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText == null || searchText.length() == 0) {
			this.ui.setText(textfield, SurveysMessages.getMessageSearchSurveys());
			this.ui.setForeground(Color.LIGHT_GRAY);
		}
		else {
			this.ui.setForeground(Color.BLACK);
		}
	}

	public void selectionChanged(Object selectedObject) {
		boolean hasSelectedItem = selectedObject != null;
		LOG.debug("selectionChanged:%s", hasSelectedItem);
		this.ui.setEnabled(this.buttonEditSurvey, hasSelectedItem);
		this.ui.setEnabled(this.buttonDeleteSurvey, hasSelectedItem);
		this.ui.setEnabled(this.buttonSendSurvey, hasSelectedItem);
		
		this.ui.removeAll(this.tableQuestions);
		Survey survey = (Survey)selectedObject;
		if (survey != null) {
			for (Question question : survey.getQuestions()) {
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
			if (databaseEntityNotification.getDatabaseEntity() instanceof Survey) {
				this.queryGenerator.refresh();
			}
		}
	}
}