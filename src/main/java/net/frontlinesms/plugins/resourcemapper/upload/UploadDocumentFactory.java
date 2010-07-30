package net.frontlinesms.plugins.resourcemapper.upload;

import java.util.List;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;
import net.frontlinesms.plugins.resourcemapper.ResourceMapperProperties;

/*
 * UploadDocumentFactory
 * @author Dale Zak
 */
public final class UploadDocumentFactory {

	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(UploadDocumentFactory.class);
	
	/**
	 * Get list of UploadDocument handlers
	 * (To add a new UploadDocument to the project, append a new row to the file
	 * /resources/META-INF/services/net.frontlinesms.plugins.resourcemapper.handler.MessageHandler
	 * with the full package and class name of the new implementing UploadDocument class)
	 * @return
	 */
	public static List<UploadDocument> getHandlerClasses() {
		if (uploadClasses == null) {
			uploadClasses = new ArrayList<UploadDocument>();
			LOG.debug("Loading UploadDocuments...");
			try {
				for (UploadDocument uploadDocument : ServiceLoader.load(UploadDocument.class)) {
					LOG.debug("Loaded: %s", uploadDocument.getTitle());
					uploadClasses.add(uploadDocument);
			    }
			}
			catch (ServiceConfigurationError ex) {
				LOG.error("ServiceConfigurationError: %s", ex);
			}
		}
		return uploadClasses;
	}private static List<UploadDocument> uploadClasses = null;
	
	/**
	 * Create UploadDocument for currently selected type
	 * @return
	 */
	public static UploadDocument createUploadDocument() {
		UploadDocument uploadDocument = ResourceMapperProperties.getUploadDocumentHandler();
		if (uploadDocument != null) {
			try {
				//create instance of currently selected UploadDocument class
				return uploadDocument.getClass().newInstance();
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