package net.frontlinesms.plugins.textforms;

import net.frontlinesms.resources.UserHomeFilePropertySet;

public class TextFormsProperties extends UserHomeFilePropertySet {

	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(TextFormsProperties.class);
	
	private static final String DEBUG_MODE = "debug.mode";
	private static final String BOOLEAN_VALUES_TRUE = "boolean.values.true";
	private static final String BOOLEAN_VALUES_FALSE = "boolean.values.false";
	private static final String DOCUMENT_UPLOADER_URL = "document.uploader.url";
	private static final String RESOURCE_FINDER_QUESTIONS_GENERATED = "resourcefinder.fields.generated";
	private static final String INFO_KEYWORDS = "info.keywords";
	private static final String REGISTER_KEYWORDS = "register.keywords";
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String SEPARATOR = ",";
	
	private static TextFormsProperties instance;
	
	protected TextFormsProperties() {
		super("textforms");
	}

	private static synchronized TextFormsProperties getInstance() {
		if (instance == null) {
			instance = new TextFormsProperties();
		}
		return instance;
	}
	
	public static boolean isDebugMode() {
		return TRUE.equalsIgnoreCase(getInstance().getProperty(DEBUG_MODE));
	}
	
	public static void setDebugMode(boolean debug) {
		LOG.debug("setDebugMode: %s", debug);
		if (debug) {
			getInstance().setProperty(DEBUG_MODE, TRUE);
		}
		else {
			getInstance().setProperty(DEBUG_MODE, FALSE);
		}
		getInstance().saveToDisk();
	}
	
	public static boolean areResourceFinderQuestionsGenerated() {
		return TRUE.equalsIgnoreCase(getInstance().getProperty(RESOURCE_FINDER_QUESTIONS_GENERATED));
	}
	
	public static void setResourceFinderQuestionsGenerated(boolean value) {
		if (value) {
			getInstance().setProperty(RESOURCE_FINDER_QUESTIONS_GENERATED, TRUE);
		}
		else {
			getInstance().setProperty(RESOURCE_FINDER_QUESTIONS_GENERATED, FALSE);
		}
		getInstance().saveToDisk();
	}
	
	public static String getPublishURL() {
		return getInstance().getProperty(DOCUMENT_UPLOADER_URL);
	}
	
	public static void setPublishURL(String url) {
		LOG.debug("setPublishURL: %s", url);
		getInstance().setProperty(DOCUMENT_UPLOADER_URL, url);
		getInstance().saveToDisk();
	}
	
	public static String [] getBooleanTrueValues() {
		return getInstance().loadArray(BOOLEAN_VALUES_TRUE, "yes,true,1");
	}
	
	public static void setBooleanTrueValues(String [] values) {
		LOG.debug("setBooleanTrueValues: %s", values);
		getInstance().saveArray(BOOLEAN_VALUES_TRUE, values);
	}
	
	public static String [] getBooleanFalseValues() {
		return getInstance().loadArray(BOOLEAN_VALUES_FALSE, "no,false,0");
	}
	
	public static void setBooleanFalseValues(String [] values) {
		LOG.debug("setBooleanFalseValues: %s", values);
		getInstance().saveArray(BOOLEAN_VALUES_FALSE, values);
	}
	
	public static String [] getInfoKeywords() {
		return getInstance().loadArray(INFO_KEYWORDS, "help,info,?");
	}
	
	public static void setInfoKeywords(String [] keywords) {
		LOG.debug("setInfoKeywords: %s", keywords);
		getInstance().saveArray(INFO_KEYWORDS, keywords);
	}
	
	public static String [] getRegisterKeywords() {
		return getInstance().loadArray(REGISTER_KEYWORDS, "register,login");
	}
	
	public static void setRegisterKeywords(String [] keywords) {
		LOG.debug("setRegisterKeywords: %s", keywords);
		getInstance().saveArray(REGISTER_KEYWORDS, keywords);
	}

	private void saveArray(String keyword, String [] values) {
		StringBuffer sb = new StringBuffer();
		for (String value : values) {
			if (sb.length() > 0) {
				 sb.append(SEPARATOR);
			}
			sb.append(value);
		}
	    LOG.debug("saveArray: %s", sb);
		getInstance().setProperty(keyword, sb.toString());
		getInstance().saveToDisk();
	}
	
	private String [] loadArray(String keyword, String defaultValue) {
		String propertyValue = getInstance().getProperty(keyword);
		if (propertyValue == null || propertyValue.length() == 0) {
			propertyValue = defaultValue;
			getInstance().setProperty(keyword, defaultValue);
			getInstance().saveToDisk();
		}
		return propertyValue.split(SEPARATOR);
	}
}
