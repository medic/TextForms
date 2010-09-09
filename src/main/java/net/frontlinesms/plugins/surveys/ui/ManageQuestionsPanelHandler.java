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

import net.frontlinesms.plugins.surveys.SurveysCallback;
import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.repository.QuestionDao;
import net.frontlinesms.plugins.surveys.search.QuestionQueryGenerator;
import net.frontlinesms.plugins.surveys.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.surveys.ui.components.PagedAdvancedTableController;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageQuestionsPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManageQuestionsPanelHandler implements ThinletUiEventHandler, AdvancedTableActionDelegate {
	
	private static SurveysLogger LOG = SurveysLogger.getLogger(ManageQuestionsPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/surveys/manageQuestionsPanel.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	
	private final Object mainPanel;
	private ManageQuestionsDialogHandler editDialog;
	private final SurveysCallback callback;
	
	private final Object searchQuestion;
	private final Object panelQuestions;
	private final Object tableQuestions;
	
	private final Object labelNameValue;
	private final Object labelKeywordValue;
	private final Object labelTypeValue;
	private final Object labelInfoValue;
	private final Object labelChoicesValue;
	private final Object labelSchemaValue;
	private final Object labelChoices;
	
	private final Object editButton;
	private final Object deleteButton;
	private final Object viewAnswersButton;
	
	private QuestionQueryGenerator queryGenerator;
	private PagedAdvancedTableController tableController;
	
	private QuestionDao questionDao;
	
	public ManageQuestionsPanelHandler(UiGeneratorController ui, ApplicationContext appContext, SurveysCallback callback) {
		LOG.debug("ManageQuestionsPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		
		this.searchQuestion = this.ui.find(this.mainPanel, "searchQuestion");
		this.tableQuestions = this.ui.find(this.mainPanel, "tableQuestions");
		this.panelQuestions = this.ui.find(this.mainPanel, "panelQuestions");
		
		this.labelNameValue = this.ui.find(this.mainPanel, "labelNameValue");
		this.labelKeywordValue = this.ui.find(this.mainPanel, "labelKeywordValue");
		this.labelTypeValue = this.ui.find(this.mainPanel, "labelTypeValue");
		this.labelInfoValue = this.ui.find(this.mainPanel, "labelInfoValue");
		this.labelChoicesValue = this.ui.find(this.mainPanel, "labelChoicesValue");
		this.labelChoices = this.ui.find(this.mainPanel, "labelChoices");
		this.labelSchemaValue = this.ui.find(this.mainPanel, "labelSchemaValue");
			
		this.editDialog = new ManageQuestionsDialogHandler(this.ui, this.appContext, callback);
		this.editButton = this.ui.find(this.mainPanel, "buttonEditQuestion");
		this.deleteButton = this.ui.find(this.mainPanel, "buttonDeleteQuestion");
		this.viewAnswersButton = this.ui.find(this.mainPanel, "buttonViewAnswers");
	
		this.questionDao = (QuestionDao) appContext.getBean("questionDao");
		
		this.tableController = new PagedAdvancedTableController(this, this.appContext, this.ui, this.tableQuestions, this.panelQuestions);
		this.tableController.putHeader(Question.class, 
									   new String[]{getI18NString(SurveysConstants.TABLE_QUESTIONNAME), 
													getI18NString(SurveysConstants.TABLE_KEYWORD), 
													getI18NString(SurveysConstants.TABLE_TYPE),
													getI18NString(SurveysConstants.TABLE_SCHEMA)}, 
									   new String[]{"getName", "getKeyword", "getTypeLabel", "getSchemaName"},
									   new String[]{"/icons/question.png", "/icons/question_keyword.png", "/icons/question_type.png", "/icons/question_schema.png"},
									   new String[]{"name", "keyword", "class", "schemaName"});
		
		this.queryGenerator = new QuestionQueryGenerator(this.appContext, this.tableController);
		this.tableController.setQueryGenerator(this.queryGenerator);
		this.tableController.setResultsPhrases(getI18NString(SurveysConstants.TABLE_RESULTS), 
											   getI18NString(SurveysConstants.TABLE_NO_RESULTS), 
											   getI18NString(SurveysConstants.TABLE_NO_SEARCH_RESULTS));
		this.tableController.setPagingPhrases(getI18NString(SurveysConstants.TABLE_TO), 
											  getI18NString(SurveysConstants.TABLE_OF));
		this.queryGenerator.startSearch("");
		
		focusLost(this.searchQuestion);
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
	
	public void addQuestion(Object tableQuestion) {
		LOG.debug("addQuestion");
		this.editDialog.show(null);
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this, SurveysConstants.CONFIRM_DELETE_QUESTION);
	}
	
	public void deleteQuestion() {
		LOG.debug("deleteQuestion");
		Question question = this.getSelectedQuestion();
		if (question != null) {
			this.questionDao.deleteQuestion(question);
		}
		this.ui.removeConfirmationDialog();
		this.refreshQuestions(null);
	}
	
	public void refreshQuestions(Question question) {
		searchByQuestion(this.searchQuestion, this.tableQuestions);
	}
	
	public void editQuestion(Object panelQuestion) {
		this.editDialog.show(this.getSelectedQuestion());
	}
	
	public void searchByQuestion(Object searchQuestion, Object tableQuestion) {
		String searchText = this.ui.getText(searchQuestion);
		if (searchText.equalsIgnoreCase(SurveysMessages.getMessageSearchQuestions())) {
			this.queryGenerator.startSearch("");
		}
		else {
			LOG.debug("searchByContact: %s", searchText);
			this.queryGenerator.startSearch(searchText);	
		}
	}
	
	public void viewAnswers(Object panelQuestion) {
		LOG.debug("viewAnswers");
		if (this.callback != null) {
			this.callback.viewAnswers(this.getSelectedQuestion());
		}	
	}
	
	private Question getSelectedQuestion() {
		final Object selectedRow = this.ui.getSelectedItem(this.tableQuestions);
		if (selectedRow != null) {
			return (Question)this.ui.getAttachedObject(selectedRow, Question.class);
		}
		return null;
	}
	
	public void doubleClickAction(Object selectedObject) {
		LOG.debug("doubleClickAction");
		this.editDialog.show(this.getSelectedQuestion());
	}

	public void resultsChanged() {
		LOG.debug("resultsChanged");
		selectionChanged(null);
	}

	public void sortChanged(String column, boolean ascending) {
		LOG.debug("sortChanged: column=%s ascending=%s", column, ascending);
		String searchText = this.ui.getText(this.searchQuestion);
		this.queryGenerator.startSearch(searchText, column, ascending);
	}
	
	public void selectionChanged(Object selectedObject) {
		LOG.debug("selectionChanged");
		Question question = this.getSelectedQuestion();
		if (question != null) {
			this.ui.setEnabled(this.editButton, true);
			this.ui.setEnabled(this.deleteButton, true);
			this.ui.setEnabled(this.viewAnswersButton, true);
			this.ui.setText(this.labelNameValue, question.getName());
			this.ui.setText(this.labelKeywordValue, question.getKeyword());
			this.ui.setText(this.labelTypeValue, question.getTypeLabel());
			this.ui.setText(this.labelInfoValue, question.getInfoSnippet());
			this.ui.setText(this.labelSchemaValue, question.getSchemaName());
			if ("boolean".equalsIgnoreCase(question.getType())) {
				String choicesLabel = String.format("%s, %s", SurveysMessages.getMessageTrue(), SurveysMessages.getMessageFalse());
				this.ui.setText(this.labelChoicesValue, choicesLabel);
				this.ui.setVisible(this.labelChoices, true);
				this.ui.setVisible(this.labelChoicesValue, true);
			}
			else if (question.getChoices() != null) {
				this.ui.setText(this.labelChoicesValue, question.getChoicesLabel());
				this.ui.setVisible(this.labelChoices, true);
				this.ui.setVisible(this.labelChoicesValue, true);
			}
			else {
				this.ui.setVisible(this.labelChoices, false);
				this.ui.setVisible(this.labelChoicesValue, false);
			}
		}
		else {
			this.ui.setEnabled(this.editButton, false);
			this.ui.setEnabled(this.deleteButton, false);
			this.ui.setEnabled(this.viewAnswersButton, false);
			this.ui.setText(this.labelNameValue, "");
			this.ui.setText(this.labelKeywordValue, "");
			this.ui.setText(this.labelTypeValue, "");
			this.ui.setText(this.labelInfoValue, "");
			this.ui.setText(this.labelSchemaValue, "");
			this.ui.setText(this.labelChoicesValue, "");
			this.ui.setVisible(this.labelChoices, false);
			this.ui.setVisible(this.labelChoicesValue, false);
		}
	}
	
	public void focusGained(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText.equalsIgnoreCase(SurveysMessages.getMessageSearchQuestions())) {
			this.ui.setText(textfield, "");
		}
		this.ui.setForeground(Color.BLACK);
	}
	
	public void focusLost(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText == null || searchText.length() == 0) {
			this.ui.setText(textfield, SurveysMessages.getMessageSearchQuestions());
			this.ui.setForeground(Color.LIGHT_GRAY);
		}
		else {
			this.ui.setForeground(Color.BLACK);
		}
	}
}