package net.frontlinesms.plugins.surveys;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

/*
 * SurveysMessages
 * @author Dale Zak
 * 
 */
public final class SurveysMessages {
	
	public static String getHandlerHelp(String[] keywords) {
		return String.format("%s : %s", getI18NString("plugins.surveys.handler.help"), arrayToString(keywords));
	}
	
	public static String getHandlerRegister(String[] keywords) {
		return String.format("%s : %s", getI18NString("plugins.surveys.handler.register"), arrayToString(keywords));
	}
	
	public static String getHandlerRegisterSuccessful(String phoneNumber) {
		return String.format("%s : %s", getI18NString("plugins.surveys.handler.register.successful"), phoneNumber);
	}
	
	public static String getHandlerInvalidKeyword(String[] keywords) {
		return String.format("%s : %s", getI18NString("plugins.surveys.handler.invalid.keyword"), arrayToString(keywords));
	}
	
	public static String getHandlerInvalidAnswer(String questionTypeLabel, String response) {
		return String.format("%s (%s) : %s", getI18NString("plugins.surveys.handler.invalid.response"), questionTypeLabel, response);
	}
	
	public static String getHandlerInvalidCallback() {
		return getI18NString("plugins.surveys.handler.invalid.callback");
	}
	
	public static String getHandlerInvalidCallbackKeyword(String keyword) {
		return String.format("%s : %s", getI18NString("plugins.surveys.handler.invalid.keyword"), keyword);
	}
	
	public static String getHandlerErrorSaveContact(String contact) {
		return String.format("%s : %s", getI18NString("plugins.surveys.handler.error.save.contact"), contact);
	}
	
	public static String getHandlerErrorUpdateContact(String contact) {
		return String.format("%s : %s", getI18NString("plugins.surveys.handler.error.update.contact"), contact);
	}
	
	public static String getHandlerErrorAnswer(String response) {
		return String.format("%s : %s", getI18NString("plugins.surveys.handler.error.response"), response);
	}
	
	public static String getHandlerErrorSaveAnswer() {
		return getI18NString("plugins.surveys.handler.error.save.response");
	}
	
	public static String getHandlerErrorUploadAnswer() {
		return getI18NString("plugins.surveys.handler.error.upload.response");
	}
	
	public static String getMessageAllContacts() {
		return getI18NString("plugins.surveys.all.contacts");
	}
	
	public static String getMessageTrue() {
		return getI18NString("plugins.surveys.true");
	}
	
	public static String getMessageFalse() {
		return getI18NString("plugins.surveys.false");
	}
	
	public static String getMessageMissingQuestion() {
		return getI18NString("plugins.surveys.alert.missing.question.name");
	}
	
	public static String getMessageMissingKeyword() {
		return getI18NString("plugins.surveys.alert.missing.question.keyword");
	}
	
	public static String getMessageMissingType() {
		return getI18NString("plugins.surveys.alert.missing.question.type");
	}
	
	public static String getMessageDuplicateKeyword() {
		return getI18NString("plugins.surveys.alert.duplicate.keyword");
	}
	
	public static String getMessageDuplicateChoice() {
		return getI18NString("plugins.surveys.alert.duplicate.choice");
	}
	
	public static String getMessageSearchAnswers() {
		return getI18NString("plugins.surveys.search.responses");
	}
	
	public static String getMessageSearchContacts() {
		return getI18NString("plugins.surveys.search.people");
	}
	
	public static String getMessageSearchQuestions() {
		return getI18NString("plugins.surveys.search.questions");
	}
	
	public static String getMessageSearchSurveys() {
		return getI18NString("plugins.surveys.search.surveys");
	}
	
	public static String getMessageChoiceRequired() {
		return getI18NString("plugins.surveys.alert.choice.required");
	} 
	
	public static String getSurveyNameRequired() {
		return getI18NString("plugins.surveys.survey.name.required");
	} 

	public static String getSurveyKeywordRequired() {
		return getI18NString("plugins.surveys.survey.keyword.required");
	} 
	
	public static String getSurveyQuestionsRequired() {
		return getI18NString("plugins.surveys.survey.questions.required");
	} 

	public static String getSurveyName() {
		return getI18NString("plugins.surveys.survey.name");
	}
	
	public static String getSurveyKeyword() {
		return getI18NString("plugins.surveys.survey.keyword");
	}
	
	public static String getSurveyQuestions() {
		return getI18NString("plugins.surveys.survey.questions");
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