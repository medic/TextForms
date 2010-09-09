package net.frontlinesms.plugins.surveys.upload;

import java.util.List;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import net.frontlinesms.plugins.surveys.SurveysLogger;
import net.frontlinesms.plugins.surveys.SurveysProperties;

/*
 * DocumentUploaderFactory
 * @author Dale Zak
 */
public final class DocumentUploaderFactory {

	private static final SurveysLogger LOG = SurveysLogger.getLogger(DocumentUploaderFactory.class);
	
	/**
	 * Get list of DocumentUploader handlers
	 * (To add a new DocumentUploader to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.surveys.upload.DocumentUploader
	 * with the full package and class name of the new implementing DocumentUploader class)
	 * @return
	 */
	public static List<DocumentUploader> getDocumentUploaders() {
		if (documentUploaders == null) {
			documentUploaders = new ArrayList<DocumentUploader>();
			LOG.debug("Loading UploadDocuments...");
			try {
				for (DocumentUploader documentUploader : ServiceLoader.load(DocumentUploader.class)) {
					LOG.debug("Loaded: %s", documentUploader.getTitle());
					documentUploaders.add(documentUploader);
			    }
			}
			catch (ServiceConfigurationError ex) {
				LOG.error("ServiceConfigurationError: %s", ex);
			}
		}
		return documentUploaders;
	}private static List<DocumentUploader> documentUploaders = null;
	
	/**
	 * Create DocumentUploader for currently selected type
	 * @return
	 */
	public static DocumentUploader createDocumentUploader() {
		DocumentUploader documentUploader = SurveysProperties.getDocumentUploader();
		if (documentUploader != null) {
			try {
				//create instance of currently selected UploadDocument class
				return documentUploader.getClass().newInstance();
			} 
			catch (InstantiationException ex) {
				LOG.error("InstantiationException: %s", ex);
			} 
			catch (IllegalAccessException ex) {
				LOG.error("IllegalAccessException: %s", ex);
			}
		}
		return null;
	}
}