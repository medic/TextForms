package net.frontlinesms.plugins.textforms.upload;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONException;

import net.frontlinesms.plugins.textforms.TextFormsConstants;
import net.frontlinesms.plugins.textforms.TextFormsLogger;
import net.frontlinesms.plugins.textforms.data.domain.answers.Answer;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload JSON Document
 * @author dalezak
 *
 */
public class JSONUploader extends DocumentUploader {

	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(JSONUploader.class);
	
	/**
	 * Collection of additional items
	 */
	private Map<String, Object> items = new HashMap<String, Object>();
	
	/**
	 * JSONUploader
	 */
	public JSONUploader() {
	}

	public void addItem(String key, Object value) {
		this.items.put(key, value);
	}
	
	public Map<String, Object> getItems() {
		return this.items;
	}
	
	/**
	 * Generate JSON string for uploading
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String toString() {
		JSONObject json = new JSONObject();
		//author
		if (this.phoneNumber != null) {
			try {
				json.put("author", this.phoneNumber);
			} 
			catch (JSONException ex) {
				LOG.error("JSONException: %s", ex);
			}
		}
		//hospital id
		if (this.organizationId != null) {
			try {
				json.put("hospital", this.organizationId);
			} 
			catch (JSONException ex) {
				LOG.error("JSONException: %s", ex);
			}
		}
		//items
		for (String key : this.items.keySet()) {
			try {
				json.put(key, this.items.get(key));
			} 
			catch (JSONException ex) {
				LOG.error("JSONException: %s", ex);
			}	
		}
		//responses
		for (Answer answer : this.getAnswers()) {
			String schema = answer.getQuestionSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = answer.getAnswerValue();
				if (responseValue != null) {
					try {
						json.append(schema, responseValue);
					} 
					catch (JSONException ex) {
						LOG.error("JSONException: %s", ex);
					}
				}
				else {
					LOG.error("AnswerValue is NULL for '%s'", schema);
				}
			}
		}
		return json.toString();
	}
	
	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(TextFormsConstants.DOCUMENT_UPLOAD_JSON);
	}
	
	@Override
	protected String getPanelXML() {
		return "/ui/plugins/textforms/upload/JSONUploader.xml";
	}
	
	/**
	 * Get ContentType
	 */
	@Override
	public String getContentType() {
		return "text/json";
	}
}