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
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.textforms.TextFormsCallback;
import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;
import net.frontlinesms.plugins.textforms.data.repository.AnswerDao;
import net.frontlinesms.plugins.textforms.search.AnswerQueryGenerator;
import net.frontlinesms.plugins.textforms.ui.components.AdvancedTableActionDelegate;
import net.frontlinesms.plugins.textforms.ui.components.PagedAdvancedTableController;

/*
 * BrowseDataPanelHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class BrowseDataPanelHandler implements ThinletUiEventHandler, AdvancedTableActionDelegate, EventObserver {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(BrowseDataPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/textforms/browseDataPanel.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	
	private final Object mainPanel;
	private BrowseDataDialogHandler editDialog;
	private final TextFormsCallback callback;
	
	private Contact selectedContact;
	private final ContactDao contactDao;
	private final AnswerDao answerDao;
	
	private Question selectedQuestion;
	private final Object comboContact;
	private final Object searchQuestion;
	private final Object textDate;
	
	private final Object panelQuestions;
	private final Object tableQuestions;
	
	private String sortColumn;
	private boolean sortAscending;
	
	private AnswerQueryGenerator queryGenerator;
	private PagedAdvancedTableController tableController;
	
	public BrowseDataPanelHandler(UiGeneratorController ui, ApplicationContext appContext, TextFormsCallback callback, FrontlineSMS frontlineController) {
		LOG.debug("BrowseDataPanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		frontlineController.getEventBus().registerObserver(this);
		this.callback = callback;
		
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
		this.editDialog = new BrowseDataDialogHandler(this.ui, this.appContext, callback);
		
		this.contactDao = (ContactDao)appContext.getBean("contactDao", ContactDao.class);
		this.answerDao = (AnswerDao)appContext.getBean("answerDao", AnswerDao.class);
		
		this.tableQuestions = this.ui.find(this.mainPanel, "tableQuestions");
		this.panelQuestions = this.ui.find(this.mainPanel, "panelQuestions");
		this.searchQuestion = this.ui.find(this.mainPanel, "searchQuestion");
		this.comboContact = this.ui.find(this.mainPanel, "comboContact");
		this.textDate = this.ui.find(this.mainPanel, "textDate");
		
		this.tableController = new PagedAdvancedTableController(this, this.appContext, this.ui, this.tableQuestions, this.panelQuestions);
		this.tableController.putHeader(Answer.class, 
									   new String[]{getI18NString(TextFormsConstants.TABLE_DATE),
													getI18NString(TextFormsConstants.TABLE_SUBMITTER),
													getI18NString(TextFormsConstants.TABLE_ORGANIZATION),
													getI18NString(TextFormsConstants.TABLE_QUESTION),
													getI18NString(TextFormsConstants.TABLE_ANSWER)}, 
									   new String[]{"getDateSubmittedText", 
													"getContactName", 
													"getOrganizationId", 
													"getQuestionName", 
													"getMessageText"},
									   new String[]{"/icons/date.png", 
													"/icons/user_sender.png", 
													"/icons/organization.png", 
													"/icons/question.png",
													"/icons/sms_receive.png"},
									   new String []{"dateSubmitted", 
													 "contact.name",
													 "organizationId",
													 "question.name",
													 "message.textMessageContent"});
		
		this.queryGenerator = new AnswerQueryGenerator(this.appContext, this.tableController);
		this.tableController.setQueryGenerator(this.queryGenerator);
		this.tableController.setResultsPhrases(getI18NString(TextFormsConstants.TABLE_RESULTS), 
											   getI18NString(TextFormsConstants.TABLE_NO_RESULTS), 
											   getI18NString(TextFormsConstants.TABLE_NO_SEARCH_RESULTS));
		this.tableController.setPagingPhrases(getI18NString(TextFormsConstants.TABLE_TO), 
											  getI18NString(TextFormsConstants.TABLE_OF));
		
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
	
	public void loadContacts() {
		this.ui.removeAll(this.comboContact);
		Object allContacts = this.ui.createComboboxChoice(TextFormsMessages.getMessageAllContacts(), null);
		this.ui.setIcon(allContacts, "/icons/users.png");
		this.ui.add(this.comboContact, allContacts);
		for (Contact contact : this.contactDao.getAllContacts()) {
			Object comboboxChoice = this.ui.createComboboxChoice(contact.getDisplayName(), contact);
			this.ui.setIcon(comboboxChoice, "/icons/user.png");
			this.ui.add(this.comboContact, comboboxChoice);
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
		if (searchText.equalsIgnoreCase(TextFormsMessages.getMessageSearchAnswers())) {
			searchText = "";
		}
		String dateReceived = this.ui.getText(this.textDate);
		String phoneNumber = null;
		Object selectedContact = this.ui.getSelectedItem(this.comboContact);
		if (selectedContact != null) {
			Contact contact = this.ui.getAttachedObject(selectedContact, Contact.class);
			if (contact != null) {
				LOG.debug("contactChanged: %s", contact.getPhoneNumber());
				phoneNumber = contact.getPhoneNumber();
			}
		}
		this.queryGenerator.startSearch(searchText, this.sortColumn, this.sortAscending, dateReceived, phoneNumber);
	}
	
	public void contactChanged(Object comboContact) {
		startSearch();
	}
	
	public void setSelectedQuestion(Question question) {
		LOG.debug("setSelectedQuestion: %s", question);
		this.selectedQuestion = question;
		if (question != null) {
			this.ui.setText(this.searchQuestion, question.getName());
		}
		else {
			this.ui.setText(this.searchQuestion, TextFormsMessages.getMessageSearchAnswers());
		}
		startSearch();
		textfieldFocusLost(this.searchQuestion);
	}
	
	public void setSelectedContact(Contact contact) {
		//LOG.debug("setSelectedContact: %s", contact);
		this.selectedContact = contact;
		if (contact != null) {
			int index = 0;
			for (Object comboboxChoice : this.ui.getItems(this.comboContact)) {
				Contact contactItem = this.ui.getAttachedObject(comboboxChoice, Contact.class);
				if (contact.equals(contactItem)) {
					this.ui.setSelectedIndex(this.comboContact, index);
					LOG.debug("Selecting Contact: %s", contact.getDisplayName());
					break;
				}
				index++;
			}
		}
		else {
			this.ui.setSelectedIndex(this.comboContact, 0);
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
		if (searchText.equalsIgnoreCase(TextFormsMessages.getMessageSearchAnswers())) {
			this.ui.setText(textfield, "");
		}
		this.ui.setForeground(Color.BLACK);
	}
	
	public void textfieldFocusLost(Object textfield) {
		String searchText = this.ui.getText(textfield);
		if (searchText == null || searchText.length() == 0) {
			this.ui.setText(textfield, TextFormsMessages.getMessageSearchAnswers());
			this.ui.setForeground(Color.LIGHT_GRAY);
		}
		else {
			this.ui.setForeground(Color.BLACK);
		}
	}
	
	public void notify(FrontlineEventNotification notification) {
		if (notification instanceof DatabaseEntityNotification<?>) {
			DatabaseEntityNotification<?> databaseEntityNotification = (DatabaseEntityNotification<?>)notification;
			if (databaseEntityNotification.getDatabaseEntity() instanceof Answer<?>) {
				this.queryGenerator.refresh();
			}
		}
	}
}
