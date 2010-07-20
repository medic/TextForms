package net.frontlinesms.plugins.resourcemapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.frontlinesms.resources.UserHomeFilePropertySet;

/**
 * @author Dieterich
 *
 */
public class ShortCodeProperties extends UserHomeFilePropertySet {

	private static ShortCodeProperties instance;
	
	public static final String PLAIN_TEXT_VALIDATION_ERROR="plain.text.validation.error";
	public static final String BOOLEAN_VALIDATION_ERROR="boolean.error.response";
	public static final String CODED_VALIDATION_ERROR="coded.error.response";

	protected ShortCodeProperties() {
		super("shortcode");
	}

	public static synchronized ShortCodeProperties getInstance() {
		if (instance == null) {
			instance = new ShortCodeProperties();
		}
		return instance;
	}

	/**
	 * This returns the short code for the specified key
	 * 
	 * @param key
	 * @return
	 */
	public String getValueForKey(String key) {
		return getProperty(key);
	}

	/**
	 * Returns the short code for the specified key. This method traverses the
	 * key-value relationship backwards, so if there is the following entry in
	 * the .properties file: </br></br><code>services.surgery=surg </code>
	 * </br></br>and you provide this method with "surg", it will return
	 * "services.surgery"
	 * 
	 * @param code
	 *            the short code in question
	 * @return the key for the short code provided
	 */
	public String getKeyForShortCode(String code) {
		return getKeyForValue(code);
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
		return (getProperty(key + ".info"));
	}

	/**
	 * Info snippets are stored in the properties file by extending the name of
	 * the field's original key with a .info, so services.surgery becomes
	 * services.surgery.info. This method returns the info snippet for a short
	 * code by retrieving the key for the specified short code and then
	 * appending .info to that key and attempting to pull the value from the
	 * properties map.
	 * 
	 * @param key
	 * @return The info snippet
	 */
	public String getInfoSnippetForShortCode(String code) {
		return getInfoSnippetForKey(getKeyForShortCode(code));
	}

	/**
	 * Helper method for {@linkg #getKeyForShortCode(String)}
	 * @param value
	 * @return
	 */
	public String getKeyForValue(String value) {
		java.util.Set<Entry<String, String>> pairEntries = getProperties().entrySet();
		java.util.Map.Entry<String, String> nxt = null;
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
	 * Processes a batch request for short codes
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
	 * @param shortCodes
	 * @return
	 */
	public List<String> getKeysForShortCodes(List<String> shortCodes) {
		ArrayList<String> results = new ArrayList<String>();
		for (String code : shortCodes) {
			results.add(getKeyForValue(code));
		}
		return results;
	}
}
