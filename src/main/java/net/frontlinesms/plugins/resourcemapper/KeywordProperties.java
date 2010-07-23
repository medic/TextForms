package net.frontlinesms.plugins.resourcemapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.frontlinesms.resources.ClasspathPropertySet;
import net.frontlinesms.resources.FilePropertySet;
import net.frontlinesms.resources.UserHomeFilePropertySet;

/**
 * @author Dieterich
 *
 */
public class KeywordProperties extends FilePropertySet {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(KeywordProperties.class);
	
	private static KeywordProperties instance;
	
	public static final String PLAINTEXT_VALIDATION_ERROR = "plugins.resourcemapper.error.plaintext.validation";
	public static final String BOOLEAN_VALIDATION_ERROR = "plugins.resourcemapper.error.boolean.validation";
	public static final String CHECKLIST_VALIDATION_ERROR = "plugins.resourcemapper.error.checklist.validation";
	public static final String MULTICHOICE_VALIDATION_ERROR = "plugins.resourcemapper.error.multichoice.validation";

	protected KeywordProperties() {
		super(new File("keyword.properties"));
	}

	public static synchronized KeywordProperties getInstance() {
		if (instance == null) {
			instance = new KeywordProperties();
		}
		return instance;
	}

	/**
	 * This returns the keyword for the specified key
	 * 
	 * @param key
	 * @return
	 */
	public String getValueForKey(String key) {
		return getProperty(key);
	}

	/**
	 * Returns the keyword for the specified key. This method traverses the
	 * key-value relationship backwards, so if there is the following entry in
	 * the .properties file: </br></br><code>services.surgery=surg </code>
	 * </br></br>and you provide this method with "surg", it will return
	 * "services.surgery"
	 * 
	 * @param keyword
	 *            the keyword in question
	 * @return the key for the keyword provided
	 */
	public String getKeyForKeyword(String keyword) {
		return getKeyForValue(keyword);
	}

	/**
	 * Info snippets are stored in the properties file by extending the name of
	 * the field's original key with a .info, so services.surgery becomes
	 * services.surgery.info. This method returns the info snippet for a key by
	 * appending .info to the key provided and attempting to pull the value from
	 * the properties map.
	 * 
	 * @param key
	 * @return The info snippet
	 */
	public String getInfoSnippetForKey(String key) {
		return getProperty(key + ".info");
	}

	/**
	 * Info snippets are stored in the properties file by extending the name of
	 * the field's original key with a .info, so services.surgery becomes
	 * services.surgery.info. This method returns the info snippet for a short
	 * code by retrieving the key for the specified keyword and then
	 * appending .info to that key and attempting to pull the value from the
	 * properties map.
	 * 
	 * @param key
	 * @return The info snippet
	 */
	public String getInfoSnippetForKeyword(String keyword) {
		return getInfoSnippetForKey(getKeyForKeyword(keyword));
	}

	/**
	 * Helper method for {@linkg #getKeyForKeyword(String)}
	 * @param value
	 * @return
	 */
	public String getKeyForValue(String value) {
		Set<Entry<String, String>> pairEntries = super.getProperties().entrySet();
		Entry<String, String> nxt = null;
		Iterator<Entry<String, String>> it = pairEntries.iterator();
		while (it.hasNext()) {
			nxt = (Map.Entry<String, String>) it.next();
			if (nxt.getValue().equals(value)) {
				return nxt.getKey();
			}
		}
		return null;
	}

	/**
	 * Processes a batch request for keywords
	 * @param keys
	 * @return
	 */
	public List<String> getShortCodesForKeys(List<String> keys) {
		ArrayList<String> results = new ArrayList<String>();
		for (String key : keys) {
			results.add(getProperty(key));
		}
		return results;
	}
	
	/**
	 * Processes a batch request for keys
	 * @param keywords
	 * @return
	 */
	public List<String> getKeysForKeywords(List<String> keywords) {
		ArrayList<String> results = new ArrayList<String>();
		for (String keyword : keywords) {
			results.add(getKeyForValue(keyword));
		}
		return results;
	}
}
