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
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.plugins.surveys.data.domain.HospitalContact;
import net.frontlinesms.plugins.surveys.data.domain.questions.Question;
import net.frontlinesms.plugins.surveys.data.domain.answers.Answer;
import net.frontlinesms.plugins.surveys.data.repository.AnswerDao;
import net.frontlinesms.plugins.surveys.data.repository.HospitalContactDao;
import net.frontlinesms.plugins.surveys.search.AnswerQueryGenerator;
import net.frontlinesms.plugins.surveys.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.surveys.ui.components.PagedAdvancedTableController;

/*
 * BrowseDataPanelHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class BrowseDataPanelHandler implements ThinletUiEventHandler, AdvancedTableActionDelegate {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(BrowseDataPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/surveys/browseDataPanel.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	
	private final Object mainPanel;
	private BrowseDataDialogHandler editDialog;
	private final SurveysCallback callback;
	
	private HospitalContact selectedContact;
	private final HospitalContactDao hospitalContactDao;
	private final AnswerDao answerDao;
	
	private Question selectedQuestion;
	private final Object comboSubmitter;
	private final Object searchQuestion;
	private final Object textDate;
	
	private final Object panelQuestions;
	private final Object tableQuestions;
	
	private String sortColumn;
	private boolean sortAscending;
	
	private AnswerQueryGenerator queryGenerator;
	private PagedAdvancedTableController tableController;
	
	public BrowseDataPanelHandler(UiGeneratorController ui, ApplicationContext appContext, SurveysCallback callback) {
		LOG.debug("BrowseDataPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		this.editDialog = new BrowseDataDialogHandler(this.ui, this.appContext, callback);
		
		this.hospitalContactDao = (HospitalContactDao)appContext.getBean("hospitalContactDao");
		this.answerDao = (AnswerDao)appContext.getBean("answerDao");
		
		this.tableQuestions = this.ui.find(this.mainPanel, "tableQuestions");
		this.panelQuestions = this.ui.find(this.mainPanel, "panelQuestions");
		this.searchQuestion = this.ui.find(this.mainPanel, "searchQuestion");
		this.comboSubmitter = this.ui.find(this.mainPanel, "comboSubmitter");
		this.textDate = this.ui.find(this.mainPanel, "textDate");
		
		this.tableController = new PagedAdvancedTableController(this, this.appContext, this.ui, this.tableQuestions, this.panelQuestions);
		this.tableController.putHeader(Answer.class, 
									   new String[]{getI18NString(SurveysConstants.TABLE_DATE),
													getI18NString(SurveysConstants.TABLE_SUBMITTER),
													getI18NString(SurveysConstants.TABLE_ORGANIZATION),
													getI18NString(SurveysConstants.TABLE_QUESTION),
													getI18NString(SurveysConstants.TABLE_ANSWER)}, 
									   new String[]{"getDateSubmittedText", 
													"getSubmitterName", 
													"getHospitalId", 
													"getQuestionName", 
													"getMessageText"},
									   new String[]{"/icons/date.png", 
													"/icons/user_sender.png", 
													"/icons/organization.png", 
													"/icons/question.png",
													"/icons/sms_receive.png"},
									   new String []{"dateSubmitted", 
													 "submitter.name",
													 "hospitalId",
													 "question.name",
													 "message.textMessageContent"});
		
		this.queryGenerator = new AnswerQueryGenerator(this.appContext, this.tableController);
		this.tableController.setQueryGenerator(this.queryGenerator);
		this.tableController.setResultsPhrases(getI18NString(SurveysConstants.TABLE_RESULTS), 
											   getI18NString(SurveysConstants.TABLE_NO_RESULTS), 
											   getI18NString(SurveysConstants.TABLE_NO_SEARCH_RESULTS));
		this.tableController.setPagingPhrases(getI18NString(SurveysConstants.TABLE_TO), 
											  getI18NString(SurveysConstants.TABLE_OF));
		
		startSearch();
		textfieldFocusLost(this.searchQuestion);
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}

	public void refresh() {
		String searchText = this.ui.getText(this.searchQuestion);
		this.queryGenerator.startSearch(searchText);
	}
	
	public void focus(Object component) {
		if (component != null) {
			this.ui.requestFocus(component);
		}
	}
	
	public void loadHospitalContacts() {
		this.ui.removeAll(this.comboSubmitter);
		Object allContacts = this.ui.createComboboxChoice(SurveysMessages.getMessageAllContacts(), null);
		this.ui.setIcon(allContacts, "/icons/users.png");
		this.ui.add(this.comboSubmitter, allContacts);
		for (HospitalContact contact : this.hospitalContactDao.getAllHospitalContacts()) {
			Object comboboxChoice = this.ui.createComboboxChoice(contact.getDisplayName(), contact);
			this.ui.setIcon(comboboxChoice, "/icons/user.png");
			this.ui.add(this.comboSubmitter, comboboxChoice);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void refreshAnswers(Answer answer) {
		startSearch();
	}
	
	public void showDateSelecter(Object textQuestion) {
		LOG.debug("showDateSelecter");
		this.ui.showDateSelecter(textQuestion);
	}
	
	public void dateChanged(Object textDate) {
		LOG.debug("dateChanged");
		String dateText = this.ui.getText(textDate);
		startSearch();
	}
	
	public void showConfirmationDialog(String methodToBeCalled) {
		this.ui.showConfirmationDialog(methodToBeCalled, this);
	}
	
	public void searchByQuestion(Object searchQuestion) {
		startSearch();
	}
	
	private void startSearch() {
		String searchText = this.ui.getText(this.searchQuestion);
		if (searchText.equalsIgnoreCase(SurveysMessages.getMessageSearchAnswers())) {
			searchText = "";
		}
		String dateReceived = this.ui.getText(this.textDate);
		String phoneNumber = null;
		Object selectedSubmitter = this.ui.getSelectedItem(this.comboSubmitter);
		if (selectedSubmitter != null) {
			HospitalContact submitter = this.ui.getAttachedObject(selectedSubmitter, HospitalContact.class);
			if (submitter != null) {
				LOG.debug("submitterChanged: %s", submitter.getPhoneNumber());
				phoneNumber = submitter.getPhoneNumber();
			}
		}
		this.queryGenerator.startSearch(searchText, this.sortColumn, this.sortAscending, dateReceived, phoneNumber);
	}
	
	public void submitterChanged(Object comboSubmitter) {
		startSearch();
	}
	
	public void setSelectedQuestion(Question question) {
		LOG.debug("setSelectedQuestion: %s", question);
		this.selectedQuestion = question;
		if (question != null) {
			this.ui.setText(this.searchQuestion, question.getName());
		}
		else {
			this.ui.setText(this.searchQuestion, SurveysMessages.getMessageSearchAnswers());
		}
		startSearch();
		textfieldFocusLost(this.searchQuestion);
	}
	
	public void setSelectedContact(HospitalContact contact) {
		//LOG.debug("setSelectedContact: %s", contact);
		this.selectedContact = contact;
		if (contact != null) {
			int index = 0;
			for (Object comboboxChoice : this.ui.getItems(this.comboSubmitter)) {
				HospitalContact contactItem = this.ui.getAttachedObject(comboboxChoice, HospitalContact.class);
				if (contact.equals(contactItem)) {
					this.ui.setSelectedIndex(this.comboSubmitter, index);
					LOG.debug("Selecting Contact: %s", contact.getDisplayName());
					break;
				}
				index++;
			}
		}
		else {
			this.ui.setSelectedIndex(this.comboSubmitter, 0);
		}
		startSearch();
	}
	
	public void doubleClickAction(Object selectedObject) {
		LOG.debug("doubleClickAction");
	}

	public void resultsChanged() {
		//LOG.debug("resultsChanged");
	}

	public void sortChanged(String column, boolean ascending) {
		LOG.debug("sortChanged: column=%s ascending=%s", column, ascending);
		this.sortColumn = column;
		this.sortAscending = ascending;
		startSearch();
	}
	
	public void selectionChanged(Object selectedObject) {
		LOG.debug("selectionChanged");
	}
	
	@SuppressWarnings("unchecked")
	private Answer getSelectedAnswer() {
		final Object selectedRow = this.ui.getSelectedItem(this.tableQuestions);
		if (selectedRow != null) {
			return (Answer)this.ui.getAttachedObject(selectedRow, Answer.class);
		}
		return null;
	}

	public void textfieldFocusGained(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText.equalsIgnoreCase(SurveysMessages.getMessageSearchAnswers())) {
			this.ui.setText(textfield, "");
		}
		this.ui.setForeground(Color.BLACK);
	}
	
	public void textfieldFocusLost(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText == null || searchText.length() == 0) {
			this.ui.setText(textfield, SurveysMessages.getMessageSearchAnswers());
			this.ui.setForeground(Color.LIGHT_GRAY);
		}
		else {
			this.ui.setForeground(Color.BLACK);
		}
	}
}
