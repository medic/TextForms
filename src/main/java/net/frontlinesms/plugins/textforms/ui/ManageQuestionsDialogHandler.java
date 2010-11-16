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

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.plugins.textforms.TextFormsCallback;
import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.TextFormsMessages;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;
import net.frontlinesms.plugins.textforms.data.repository.QuestionFactory;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageQuestionsDialogHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class ManageQuestionsDialogHandler implements ThinletUiEventHandler {
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(ManageQuestionsDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/textforms/manageQuestionsDialog.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final TextFormsCallback callback;
	
	private final Object mainDialog;
	private Question question;
	
	private final Object textName;
	private final Object textKeyword;
	private final Object textInfoSnippet;
	private final Object comboQuestionTypes;
	private final Object listQuestionChoices; 
	private final Object labelQuestionChoices;
	private final Object panelQuestionChoices;
	private final Object textQuestionChoice;
	private final Object textSchema;
	private final Object buttonQuestionAdd;
	private final Object placeholderChoices;
	
	private QuestionDao questionDao;
	
	public ManageQuestionsDialogHandler(UiGeneratorController ui, ApplicationContext appContext, TextFormsCallback callback) { 
		LOG.debug("ManageQuestionsDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.textName = this.ui.find(this.mainDialog, "textName");
		this.textKeyword = this.ui.find(this.mainDialog, "textKeyword");
		this.textInfoSnippet = this.ui.find(this.mainDialog, "textInfoSnippet");
		this.comboQuestionTypes = this.ui.find(this.mainDialog, "comboQuestionTypes"); 
		this.listQuestionChoices = this.ui.find(this.mainDialog, "listQuestionChoices");
		this.labelQuestionChoices = this.ui.find(this.mainDialog, "labelQuestionChoices");
		this.panelQuestionChoices = this.ui.find(this.mainDialog, "panelQuestionChoices");
		this.textQuestionChoice = this.ui.find(this.mainDialog, "textQuestionChoice");	
		this.textSchema = this.ui.find(this.mainDialog, "textSchema");
		this.buttonQuestionAdd = this.ui.find(this.mainDialog, "buttonQuestionAdd");
		this.placeholderChoices = this.ui.find(this.mainDialog, "placeholderChoices");
		this.questionDao = (QuestionDao) appContext.getBean("questionDao", QuestionDao.class);
		
		loadQuestions();
	}
	
	public void show(Question question) {
		this.question = question;
		if (question != null) {
			this.ui.setText(this.textName, question.getName());
			this.ui.setText(this.textKeyword, question.getKeyword());
			this.ui.setText(this.textInfoSnippet, question.getInfoSnippet());
			this.ui.setText(this.textSchema, question.getSchemaName());
			this.ui.setEnabled(this.comboQuestionTypes, false);
			this.ui.setSelectedIndex(this.comboQuestionTypes, -1);
			for (int index = 0; index < this.ui.getCount(this.comboQuestionTypes); index++) {
				Object comboTypeItem = this.ui.getItem(this.comboQuestionTypes, index);
				Object attachedObject = this.ui.getAttachedObject(comboTypeItem);
				if (attachedObject != null) {
					if (question.getType().equalsIgnoreCase(attachedObject.toString())) {
						this.ui.setSelectedIndex(this.comboQuestionTypes, index);
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
			this.ui.setEnabled(this.comboQuestionTypes, true);
			this.ui.setSelectedIndex(this.comboQuestionTypes, 0);
			this.ui.removeAll(this.listQuestionChoices);
		}
		questionTypeChanged(this.comboQuestionTypes, this.panelQuestionChoices, this.listQuestionChoices);
		this.ui.add(this.mainDialog);
	}
	
	private void loadQuestions() {
		for (Question questionClass : QuestionFactory.getQuestionClasses()) {
			LOG.debug("Question: %s", questionClass.getTypeLabel());
			Object comboBoxChoice = this.ui.createComboboxChoice(questionClass.getTypeLabel(), questionClass.getType());
			this.ui.add(this.comboQuestionTypes, comboBoxChoice);
		}
	}
	
	public void saveQuestion(Object dialog) throws DuplicateKeyException {
		LOG.debug("saveQuestion");
		String name = this.ui.getText(this.textName);
		String keyword = this.ui.getText(this.textKeyword);
		String infoSnippet = this.ui.getText(this.textInfoSnippet);
		String schema = this.ui.getText(this.textSchema);
		Object questionType = this.ui.getSelectedItem(this.comboQuestionTypes);
		String type = (String)this.ui.getAttachedObject(questionType);
		List<String> choices = new ArrayList<String>();
		for (Object comboItem : this.ui.getItems(this.listQuestionChoices)) {
			String comboItemText = this.ui.getAttachedObject(comboItem).toString().trim();
			LOG.debug("comboItemText: %s", comboItemText);
			choices.add(comboItemText);	
		}
		try {
			if (name == null || name.length() == 0) {
				this.ui.alert(TextFormsMessages.getMessageMissingQuestion());
			}
			else if (keyword == null || keyword.length() == 0) {
				this.ui.alert(TextFormsMessages.getMessageMissingKeyword());
			}
			else if (type == null || type.length() == 0) {
				this.ui.alert(TextFormsMessages.getMessageMissingType());
			}
			else if (hasDuplicateChoices(choices)) {
				this.ui.alert(TextFormsMessages.getMessageDuplicateChoice());
			}
			else if (this.question != null && this.question.getType().equalsIgnoreCase(type)) {
				this.question.setName(name);
				this.question.setKeyword(keyword);
				this.question.setInfoSnippet(infoSnippet);
				this.question.setSchemaName(schema);
				this.question.setChoices(choices);
				this.questionDao.updateQuestion(this.question);
				this.callback.refreshQuestion(this.question);
				this.ui.remove(dialog);
			}
			else {
				if (this.question != null) {
					LOG.debug("Existing Question Deleted!");
					this.questionDao.deleteQuestion(this.question);
				}
				Question newQuestion = QuestionFactory.createQuestion(name, keyword, infoSnippet, type, schema, choices);
				if (newQuestion != null) {
					LOG.debug("New Question Created!");
					this.questionDao.saveQuestion(newQuestion);
				}
				this.callback.refreshQuestion(newQuestion);
				this.ui.remove(dialog);
			}
		}
		catch (DuplicateKeyException ex) {
			LOG.error("DuplicateKeyException: %s", ex);
			this.ui.alert(TextFormsMessages.getMessageDuplicateKeyword());
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
	
	public void questionTypeChanged(Object comboTypes, Object panelChoices, Object listChoices) {
		LOG.debug("typeChanged");
		Object selectedItem = this.ui.getSelectedItem(comboTypes);
		Object attachedObject = selectedItem != null ? this.ui.getAttachedObject(selectedItem) : null;
		String selectedType = attachedObject != null ? attachedObject.toString() : null;
		this.ui.removeAll(listChoices);
		this.ui.setEnabled(this.buttonQuestionAdd, false);
		this.ui.setEditable(this.textQuestionChoice, false);
		if ("boolean".equalsIgnoreCase(selectedType)) {
			this.ui.add(listChoices, this.ui.createListItem(TextFormsMessages.getMessageTrue(), 1));
			this.ui.add(listChoices, this.ui.createListItem(TextFormsMessages.getMessageFalse(), 0));
			this.ui.setEnabled(listChoices, false);
			setChoicesVisible(true);
		}
		else if ("checklist".equalsIgnoreCase(selectedType) || 
				 "multichoice".equalsIgnoreCase(selectedType)) {
			if (this.question != null) {
				for (String choiceText : this.question.getChoices()) {
					this.ui.setEnabled(listChoices, true);
					this.ui.add(listChoices, this.ui.createListItem(choiceText, choiceText));
				}	
			}
			this.ui.setEnabled(listChoices, true);
			this.ui.setEnabled(this.buttonQuestionAdd, false);
			this.ui.setEditable(this.textQuestionChoice, true);
			this.ui.setEnabled(listChoices, true);
			setChoicesVisible(true);
		}
		else {
			this.ui.setEnabled(listChoices, false);
			setChoicesVisible(false);
		}
	}
	
	private void setChoicesVisible(boolean visible) {
		this.ui.setVisible(this.labelQuestionChoices, visible);
		this.ui.setVisible(this.panelQuestionChoices, visible);
		this.ui.setVisible(this.placeholderChoices, !visible);
	}
	
	public void textQuestionChoiceChanged(Object textQuestionChoice, Object listQuestionChoices, Object buttonQuestionAdd) {
		if (this.ui.getText(textQuestionChoice).length() > 0) {
			this.ui.setEnabled(buttonQuestionAdd, true);
		}
		else {
			this.ui.setEnabled(buttonQuestionAdd, false);
		}
	}
	
	public void addQuestionChoice(Object textQuestionChoice, Object listChoices, Object buttonQuestionAdd) {
		String choiceText = this.ui.getText(textQuestionChoice);
		if (choiceText != null && choiceText.length() > 0) {
			if (isDuplicateChoice(listChoices, choiceText)) {
				this.ui.alert(TextFormsMessages.getMessageDuplicateChoice());
			}
			else {
				this.ui.add(listChoices, this.ui.createListItem(choiceText, choiceText));
				this.ui.setText(textQuestionChoice, "");
				this.ui.setEnabled(buttonQuestionAdd, false);
			}
		}
	}
	
	public void questionChoiceChanged(Object listQuestionChoices, Object buttonQuestionDelete) {
		Object selectedItem = this.ui.getSelectedItem(listQuestionChoices);
		this.ui.setEnabled(buttonQuestionDelete, selectedItem != null);
	}
	
	public void deleteChoice(Object listQuestionChoices, Object buttonQuestionDelete) {
		Object choiceToDelete = this.ui.getSelectedItem(listQuestionChoices);
		String choiceText = this.ui.getText(choiceToDelete);
		LOG.debug("choice to delete: %s", choiceText);
		this.ui.remove(choiceToDelete);
		this.ui.setEnabled(buttonQuestionDelete, false);
	}
}
