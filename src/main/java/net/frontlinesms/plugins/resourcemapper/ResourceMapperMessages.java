package net.frontlinesms.plugins.resourcemapper;

import static net.frontlinesms.ui.i18n.InternationalisationUtils.getI18NString;

/*
 * ResourceMapperMessages
 * @author Dale Zak
 * 
 */
public final class ResourceMapperMessages {
	
	public static String getHandlerHelp(String[] keywords) {
		return String.format("%s : %s", getI18NString("plugins.resourcemapper.handler.help"), arrayToString(keywords));
	}
	
	public static String getHandlerRegister(String[] keywords) {
		return String.format("%s : %s", getI18NString("plugins.resourcemapper.handler.register"), arrayToString(keywords));
	}
	
	public static String getHandlerRegisterSuccessful(String phoneNumber) {
		return String.format("%s : %s", getI18NString("plugins.resourcemapper.handler.register.successful"), phoneNumber);
	}
	
	public static String getHandlerInvalidKeyword(String[] keywords) {
		return String.format("%s : %s", getI18NString("plugins.resourcemapper.handler.invalid.keyword"), arrayToString(keywords));
	}
	
	public static String getHandlerInvalidCallback() {
		return getI18NString("plugins.resourcemapper.handler.invalid.callback");
	}
	
	public static String getHandlerInvalidCallbackKeyword(String keyword) {
		return String.format("%s : %s", getI18NString("plugins.resourcemapper.handler.invalid.keyword"), keyword);
	}
	
	public static String getHandlerErrorSaveContact(String contact) {
		return String.format("%s : %s", getI18NString("plugins.resourcemapper.handler.error.save.contact"), contact);
	}
	
	public static String getHandlerErrorUpdateContact(String contact) {
		return String.format("%s : %s", getI18NString("plugins.resourcemapper.handler.error.update.contact"), contact);
	}
	
	public static String getHandlerErrorResponse(String response) {
		return String.format("%s : %s", getI18NString("plugins.resourcemapper.handler.error.response"), response);
	}
	
	public static String getHandlerErrorSaveResponse() {
		return getI18NString("plugins.resourcemapper.handler.error.save.response");
	}
	
	public static String getHandlerErrorUploadResponse() {
		return getI18NString("plugins.resourcemapper.handler.error.upload.response");
	}
	
	public static String getMessageAllContacts() {
		return getI18NString("plugins.resourcemapper.all.contacts");
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