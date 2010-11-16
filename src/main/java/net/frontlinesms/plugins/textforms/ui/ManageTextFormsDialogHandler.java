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
import net.frontlinesms.plugins.textforms.data.domain.TextForm;
import net.frontlinesms.plugins.textforms.data.domain.questions.Question;
import net.frontlinesms.plugins.textforms.data.repository.QuestionDao;
import net.frontlinesms.plugins.textforms.data.repository.QuestionFactory;
import net.frontlinesms.plugins.textforms.data.repository.TextFormDao;
import net.frontlinesms.ui.ExtendedThinlet;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/*
 * ManageQuestionsDialogHandler
 * @author Dale Zak
 */
@SuppressWarnings("unused")
public class ManageTextFormsDialogHandler extends ExtendedThinlet implements ThinletUiEventHandler {
	
	private static final long serialVersionUID = 1L;
	
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(ManageTextFormsDialogHandler.class);
	private static final String DIALOG_XML = "/ui/plugins/textforms/manageTextFormsDialog.xml";
	
	private final UiGeneratorController ui;
	private final ApplicationContext appContext;
	private final TextFormsCallback callback;
	
	private final Object mainDialog;
	private TextForm textform;
	
	private final Object textTextFormName;
	private final Object textTextFormKeyword;
	private final Object comboQuestions;
	private final Object tableQuestions;
	private final Object buttonDeleteQuestion;
	
	private final QuestionDao questionDao;
	private final TextFormDao textformDao;
	
	public ManageTextFormsDialogHandler(UiGeneratorController ui, ApplicationContext appContext, TextFormsCallback callback) { 
		LOG.debug("ManageTextFormsDialogHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.callback = callback;
		this.mainDialog = this.ui.loadComponentFromFile(DIALOG_XML, this);
		
		this.textTextFormName = this.ui.find(this.mainDialog, "textTextFormName");
		this.textTextFormKeyword = this.ui.find(this.mainDialog, "textTextFormKeyword");
		this.comboQuestions = this.ui.find(this.mainDialog, "comboQuestions");
		this.tableQuestions = this.ui.find(this.mainDialog, "tableQuestions");
		this.buttonDeleteQuestion = this.ui.find(this.mainDialog, "buttonDeleteQuestion"); 
		
		this.questionDao = (QuestionDao) appContext.getBean("questionDao", QuestionDao.class);
		this.textformDao = (TextFormDao) appContext.getBean("textformDao", TextFormDao.class);
	}
	
	public void show(TextForm textform) {
		this.textform = textform;
		this.ui.removeAll(comboQuestions);
		for (Question question : questionDao.getAllQuestions()) {
			String questionText = String.format("%s (%s)", question.getName(), question.getTypeLabel());
			this.ui.add(comboQuestions, this.ui.createComboboxChoice(questionText, question));
		}
		this.ui.removeAll(tableQuestions);
		if (textform != null) {
			this.ui.setText(this.textTextFormName, textform.getName());
			this.ui.setText(this.textTextFormKeyword, textform.getKeyword());
			for (Question question : textform.getQuestions()) {
				this.ui.add(tableQuestions, getRow(question));
			}
		}
		else {
			this.ui.setText(this.textTextFormName, "");
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
	
	public void saveTextForm() {
		LOG.debug("saveTextForm");
		String textformName = this.ui.getText(this.textTextFormName);
		String textformKeyword = this.ui.getText(this.textTextFormKeyword);
		List<Question> questions = new ArrayList<Question>();
		for(Object item : this.ui.getItems(this.tableQuestions)) {
			questions.add((Question)this.ui.getAttachedObject(item));
		}
		if (textformName == null || textformName.length() == 0) {
			this.ui.alert(TextFormsMessages.getTextFormNameRequired());
		}
		else if (textformKeyword == null || textformKeyword.length() == 0) {
			this.ui.alert(TextFormsMessages.getTextFormKeywordRequired());
		}
		else if (questions.size() == 0) {
			this.ui.alert(TextFormsMessages.getTextFormQuestionsRequired());
		}
		else if (textform == null) {
			textform = new TextForm(textformName, textformKeyword, questions);
			try {
				this.textformDao.saveTextForm(textform);
			} 
			catch (DuplicateKeyException e) {
				e.printStackTrace();
			}
			this.ui.remove(this.mainDialog);
			this.callback.refreshTextForm(textform);
		}
		else {
			textform.setName(textformName);
			textform.setKeyword(textformKeyword);
			textform.setQuestions(questions);
			try {
				this.textformDao.updateTextForm(textform);
			} 
			catch (DuplicateKeyException e) {
				e.printStackTrace();
			}
			this.ui.remove(this.mainDialog);
			this.callback.refreshTextForm(textform);
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
