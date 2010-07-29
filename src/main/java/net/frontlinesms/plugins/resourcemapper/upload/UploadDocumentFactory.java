package net.frontlinesms.plugins.resourcemapper.upload;

import java.util.List;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import net.frontlinesms.plugins.resourcemapper.ResourceMapperLogger;

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
	
}