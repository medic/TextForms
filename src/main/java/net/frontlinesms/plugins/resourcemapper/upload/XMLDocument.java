package net.frontlinesms.plugins.resourcemapper.upload;

import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.data.domain.response.FieldResponse;
import net.frontlinesms.plugins.resourcemapper.upload.UploadDocument;

/**
 * Upload XML Document
 * @author dalezak
 *
 */
@SuppressWarnings("unchecked")
public class XMLDocument extends UploadDocument {
	
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(XMLDocument.class);
	
	private final HashMap<String, String> namespaces = new HashMap<String, String>();
	
	private String entryElementName;
	private Document document;
	
	/**
	 * XMLDocument
	 * @param rootElementName root element name
	 */
	public XMLDocument(String rootElementName) {
		this(rootElementName, null);
	}
	
	/**
	 * XMLDocument
	 * @param rootElementName root element name
	 * @param entryElementName entry element name
	 */
	public XMLDocument(String rootElementName, String entryElementName) {
		DocumentFactory factory = DocumentFactory.getInstance();
		this.document = factory.createDocument();
		this.document.addElement(rootElementName);
		this.entryElementName = entryElementName;
	}
	
	/**
	 * Generate XMlL document for uploading
	 */
	@Override
	public String toString() {
		Element rootElement = this.document.getRootElement();
		//add namespaces
		for (String key : this.namespaces.keySet()) {
			rootElement.addNamespace(key, this.namespaces.get(key));
		}
		//add responses
		for (FieldResponse fieldResponse : this.getFieldResponses()) {
			String schema = fieldResponse.getMappingSchema();
			if (schema != null && schema.length() > 0) {
				String responseValue = fieldResponse.getResponseValue();
				if (responseValue != null) {
					if (this.entryElementName != null) {
						Element entry = rootElement.addElement(this.entryElementName);
						entry.addAttribute("name", schema);
						entry.setText(responseValue);	
						entry.addComment(fieldResponse.getMessageText());
					}
					else {
						Element entry = rootElement.addElement(schema);
						entry.setText(responseValue);
						entry.addComment(fieldResponse.getMessageText());
					}
				}
				else {
					LOG.error("ResponseValue is NULL for %s", schema);
				}
			}
		}
		return this.document.asXML();
	}
	
	/**
	 * Add XML namespace
	 * @param key key
	 * @param value namespace
	 */
	public void addNamespace(String key, String value) {
		this.namespaces.put(key, value);
	}
	
	/**
	 * Add XML Element
	 * @param name name
	 * @return Element
	 */
	public Element addElement(String name) {
		return addElement(null, name, null, null);
	}
	
	/**
	 * Add XML Element
	 * @param name name
	 * @param value value
	 * @return Element
	 */
	public Element addElement(String name, String value) {
		return addElement(null, name, value, null);
	}
	
	/**
	 * Add XML Element
	 * @param name name
	 * @param value value
	 * @param namespace XML namespce
	 * @return Element
	 */
	public Element addElement(String name, String value, String namespace) {
		return addElement(null, name, value, namespace);
	}
	
	/**
	 * Add XML Element
	 * @param parent XML parent
	 * @param name name
	 * @param value value
	 * @return Element
	 */
	public Element addElement(Element parent, String name, String value) {
		return addElement(parent, name, value, null);
	}
	
	/**
	 * Add XML Element
	 * @param parent Element parent
	 * @param name element name
	 * @param value element value
	 * @param namespace XML namespace
	 * @return Element
	 */
	public Element addElement(Element parent, String name, String value, String namespace) {
		if (parent == null) {
			parent = this.document.getRootElement();
		}
		Element element = (namespace != null)
			? parent.addElement(name, namespace)
			: parent.addElement(name);
		if (value != null) {
			element.setText(value);
		}
		return element;
	}
	
}