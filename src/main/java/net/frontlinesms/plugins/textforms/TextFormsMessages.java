package net.frontlinesms.plugins.textforms;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

/*
 * TextFormsMessages
 * @author Dale Zak
 * 
 */
public final class TextFormsMessages {
	
	public static String getHandlerHelp(String[] keywords) {
		return String.format("%s : %s", getI18NString("plugins.textforms.handler.help"), arrayToString(keywords));
	}
	
	public static String getHandlerRegisterDescription(String keyword) {
		return String.format(getI18NString("plugins.textforms.handler.register.keyword"),keyword);
	}
	
	public static String getHandlerPleaseRegister(String keyword) {
		return String.format(getI18NString("plugins.textforms.handler.register.keywords"),keyword);
	}
	
	
	/**
	 * Sent to the user when they have successfully registered their phone with an organization ID.
	 * @param organization
	 * @param phoneNumber
	 * @return
	 */
	public static String getHandlerRegisterSuccessful(String organization, String phoneNumber) {
		return String.format(getI18NString("plugins.textforms.handler.register.successful"), phoneNumber, organization);
	}
	
	public static String getHandlerInvalidKeyword(String keyword) {
		return String.format(getI18NString("plugins.textforms.handler.invalid.keyword"),keyword);
	}
	
	public static String getHandlerInvalidKeywordExtended(String keyword, String keywords) {
		return String.format(getI18NString("plugins.textforms.handler.invalid.keyword.extended"),keyword, keywords);
	}
	
	public static String getHandlerInvalidAnswer(String questionName, String questionTypeLabel, String invalidResponse) {
		return String.format(getI18NString("plugin.textforms.handler.error.invalid"), questionName, questionTypeLabel, invalidResponse);
	}
	
	/**
	 * Returns an "invalid answer" message that contains the question name, type, and the improper response
	 * @param questionName The name of the question
	 * @param questionTypeLabel The type of the question
	 * @param invalidResponse The user-submitted invalid response
	 * @return
	 */
	public static String getHandlerInvalidAnswerSpecific(String questionName, String questionTypeLabel, String invalidResponse) {
		return String.format(getI18NString("plugin.textforms.handler.error.invalid.answer.specific"), questionName, questionTypeLabel, invalidResponse);
	}
	
	/**
	 * Returns an "invalid answer" message that contains the question name and type.
	 * @param questionName The name of the question
	 * @param questionTypeLabel The type of the question
	 * @return
	 */
	public static String getHandlerInvalidAnswerMedium(String questionName, String questionTypeLabel) {
		return String.format(getI18NString("plugin.textforms.handler.error.invalid.answer.medium"), questionName, questionTypeLabel);
	}
	
	/**
	 * Returns an "invalid answer" message that contains no identifying info.
	 */
	public static String getHandlerInvalidAnswerGeneral() {
		return getI18NString("plugin.textforms.handler.error.invalid.answer.general");
	}
	
	public static String getHandlerInvalidCallback() {
		return getI18NString("plugins.textforms.handler.invalid.callback");
	}
	
	public static String getHandlerInvalidCallbackKeyword(String keyword) {
		return String.format("%s : %s", getI18NString("plugins.textforms.handler.invalid.keyword"), keyword);
	}
	
	public static String getHandlerErrorSaveContact(String contact) {
		return String.format("%s : %s", getI18NString("plugins.textforms.handler.error.save.contact"), contact);
	}
	
	public static String getHandlerErrorUpdateContact(String contact) {
		return String.format("%s : %s", getI18NString("plugins.textforms.handler.error.update.contact"), contact);
	}
	
	public static String getHandlerErrorAnswer(String response) {
		return String.format("%s : %s", getI18NString("plugins.textforms.handler.error.answer"), response);
	}
	
	public static String getHandlerErrorSaveAnswer() {
		return getI18NString("plugins.textforms.handler.error.save.answer");
	}
	
	public static String getHandlerErrorUploadAnswer() {
		return getI18NString("plugins.textforms.handler.error.upload.answer");
	}
	
	public static String getHandlerErrorSaveTextForm() {
		return getI18NString("plugins.textforms.handler.error.save.textform");
	}
	
	public static String getHandlerErrorDeleteTextForm() {
		return getI18NString("plugins.textforms.handler.error.delete.textform");
	}
	
	public static String getMessageAllContacts() {
		return getI18NString("plugins.textforms.all.contacts");
	}
	
	public static String getMessageTrue() {
		return getI18NString("plugins.textforms.true");
	}
	
	public static String getMessageFalse() {
		return getI18NString("plugins.textforms.false");
	}
	
	public static String getMessageMissingQuestion() {
		return getI18NString("plugins.textforms.alert.missing.question.name");
	}
	
	public static String getMessageMissingKeyword() {
		return getI18NString("plugins.textforms.alert.missing.question.keyword");
	}
	
	public static String getMessageMissingType() {
		return getI18NString("plugins.textforms.alert.missing.question.type");
	}
	
	public static String getMessageDuplicateKeyword() {
		return getI18NString("plugins.textforms.alert.duplicate.keyword");
	}
	
	public static String getMessageDuplicateChoice() {
		return getI18NString("plugins.textforms.alert.duplicate.choice");
	}
	
	public static String getMessageSearchAnswers() {
		return getI18NString("plugins.textforms.search.answers");
	}
	
	public static String getMessageSearchContacts() {
		return getI18NString("plugins.textforms.search.contacts");
	}
	
	public static String getMessageSearchQuestions() {
		return getI18NString("plugins.textforms.search.questions");
	}
	
	public static String getMessageSearchTextForms() {
		return getI18NString("plugins.textforms.search.textforms");
	}
	
	public static String getMessageChoiceRequired() {
		return getI18NString("plugins.textforms.alert.choice.required");
	} 
	
	public static String getTextFormNameRequired() {
		return getI18NString("plugins.textforms.textform.name.required");
	} 

	public static String getTextFormKeywordRequired() {
		return getI18NString("plugins.textforms.textform.keyword.required");
	} 
	
	public static String getTextFormKeywordUnique() {
		return getI18NString("plugins.textforms.textform.keyword.unique");
	}
	
	public static String getTextFormQuestionsRequired() {
		return getI18NString("plugins.textforms.textform.questions.required");
	} 

	public static String getTextFormName() {
		return getI18NString("plugins.textforms.textform.name");
	}
	
	public static String getTextFormKeyword() {
		return getI18NString("plugins.textforms.textform.keyword");
	}
	
	public static String getTextFormQuestions() {
		return getI18NString("plugins.textforms.textform.questions");
	}
	
	public static String getSurveryCompleted(String textformName) {
		return String.format("%s %s", textformName, getI18NString("plugins.textforms.textform.completed"));
	}
	
	private static String arrayToString(String [] args) {
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			if (sb.length() > 0) {
				 sb.append(", ");
			}
			sb.append(arg);
		}
		return sb.toString();
	}

}