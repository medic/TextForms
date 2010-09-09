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

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.surveys.SurveysCallback;
import net.frontlinesms.plugins.surveys.SurveysConstants;
import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysMessages;
import net.frontlinesms.plugins.surveys.data.domain.Survey;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.repository.QuestionDao;
import net.frontlinesms.plugins.surveys.data.repository.QuestionFactory;
import net.frontlinesms.plugins.surveys.data.repository.SurveyDao;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/*
 * ManageQuestionsDialogHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class ManageSurveysDialogHandler extends ExtendedThinlet implements ThinletUiEventHandler {
	
	private static final long serialVersionUID = 1L;
	
	private static SurveysLogger LOG = SurveysLogger.getLogger(ManageSurveysDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/surveys/manageSurveysDialog.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final SurveysCallback callback;
	
	private final Object mainDialog;
	private Survey survey;
	
	private final Object textSurveyName;
	private final Object textSurveyKeyword;
	private final Object comboQuestions;
	private final Object tableQuestions;
	private final Object buttonDeleteQuestion;
	
	private final QuestionDao questionDao;
	private final SurveyDao surveyDao;
	
	public ManageSurveysDialogHandler(UiGeneratorController ui, ApplicationContext appContext, SurveysCallback callback) { 
		LOG.debug("ManageSurveysDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.textSurveyName = this.ui.find(this.mainDialog, "textSurveyName");
		this.textSurveyKeyword = this.ui.find(this.mainDialog, "textSurveyKeyword");
		this.comboQuestions = this.ui.find(this.mainDialog, "comboQuestions");
		this.tableQuestions = this.ui.find(this.mainDialog, "tableQuestions");
		this.buttonDeleteQuestion = this.ui.find(this.mainDialog, "buttonDeleteQuestion"); 
		
		this.questionDao = (QuestionDao) appContext.getBean("questionDao", QuestionDao.class);
		this.surveyDao = (SurveyDao) appContext.getBean("surveyDao", SurveyDao.class);
	}
	
	public void show(Survey survey) {
		this.survey = survey;
		this.ui.removeAll(comboQuestions);
		for (Question question : questionDao.getAllQuestions()) {
			String questionText = String.format("%s (%s)", question.getName(), question.getTypeLabel());
			this.ui.add(comboQuestions, this.ui.createComboboxChoice(questionText, question));
		}
		this.ui.removeAll(tableQuestions);
		if (survey != null) {
			this.ui.setText(this.textSurveyName, survey.getName());
			this.ui.setText(this.textSurveyKeyword, survey.getKeyword());
			for (Question question : survey.getQuestions()) {
				this.ui.add(tableQuestions, getRow(question));
			}
		}
		else {
			this.ui.setText(this.textSurveyName, "");
		}
		this.ui.add(this.mainDialog);
	}
	
	public void removeDialog(Object dialog) {
		LOG.debug("removeDialog");
		this.ui.remove(dialog);
	}
	
	public void addQuestion(Object comboQuestions) {
		Object selectedItem = this.ui.getSelectedItem(comboQuestions);
		Question question = this.ui.getAttachedObject(selectedItem, Question.class);
		this.ui.add(this.tableQuestions, getRow(question));
	}
	
	public void saveSurvey() {
		LOG.debug("saveSurvey");
		String surveyName = this.ui.getText(this.textSurveyName);
		String surveyKeyword = this.ui.getText(this.textSurveyKeyword);
		List<Question> questions = new ArrayList<Question>();
		for(Object item : this.ui.getItems(this.tableQuestions)) {
			questions.add((Question)this.ui.getAttachedObject(item));
		}
		if (surveyName == null || surveyName.length() == 0) {
			this.ui.alert(SurveysMessages.getSurveyNameRequired());
		}
		else if (surveyKeyword == null || surveyKeyword.length() == 0) {
			this.ui.alert(SurveysMessages.getSurveyKeywordRequired());
		}
		else if (questions.size() == 0) {
			this.ui.alert(SurveysMessages.getSurveyQuestionsRequired());
		}
		else if (survey == null) {
			survey = new Survey(surveyName, surveyKeyword, questions);
			try {
				this.surveyDao.saveSurvey(survey);
			} 
			catch (DuplicateKeyException e) {
				e.printStackTrace();
			}
			this.ui.remove(this.mainDialog);
			this.callback.refreshSurvey(survey);
		}
		else {
			survey.setName(surveyName);
			survey.setKeyword(surveyKeyword);
			survey.setQuestions(questions);
			try {
				this.surveyDao.updateSurvey(survey);
			} 
			catch (DuplicateKeyException e) {
				e.printStackTrace();
			}
			this.ui.remove(this.mainDialog);
			this.callback.refreshSurvey(survey);
		}
	}
	
	public void tableChanged(Object table, Object button) {
		this.ui.setEnabled(button, this.ui.getSelectedItem(table) != null);
	}
	
	public void comboChanged(Object comboBox, Object button) {
		this.ui.setEnabled(button, this.ui.getSelectedItem(comboBox) != null);
	}
	
	public void deleteQuestion(Object table, Object button) {
		Object selectedItem = this.getSelectedItem(table);
		this.ui.remove(selectedItem);
		this.ui.setEnabled(button, false);
	}
	
	public Object getRow(Question question){
		Object row = this.ui.createTableRow(question);
		this.createTableCell(row, question.getName());
		this.createTableCell(row, question.getKeyword());
		this.createTableCell(row, question.getTypeLabel());
		return row;
	}
}
