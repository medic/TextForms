package net.frontlinesms.plugins.resourcemapper.upload;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONException;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperConstants;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * Upload JSON Document
 * @author dalezak
 *
 */
public class JSONDocument extends UploadDocument {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(JSONDocument.class);
	private Map<String, Object> items = new HashMap<String, Object>();
	
	/**
	 * JSONDocument
	 */
	public JSONDocument() {
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
		for (String key : this.items.keySet()) {
			try {
				json.put(key, this.items.get(key));
			} 
			catch (JSONException ex) {
				LOG.error("JSONException: %s", ex);
			}	
		}
		for (FieldResponse fieldResponse : this.getFieldResponses()) {
			String schema = fieldResponse.getMappingSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = fieldResponse.getResponseValue();
				if (responseValue != null) {
					try {
						json.append(schema, responseValue);
					} 
					catch (JSONException ex) {
						LOG.error("JSONException: %s", ex);
					}
				}
				else {
					LOG.error("ResponseValue is NULL for '%s'", schema);
				}
			}
		}
		return json.toString();
	}
	
	@Override
	public String getTitle() {
		return InternationalisationUtils.getI18NString(ResourceMapperConstants.DOCUMENT_UPLOAD_JSON);
	}
	
}