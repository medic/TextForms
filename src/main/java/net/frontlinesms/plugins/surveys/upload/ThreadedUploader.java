package net.frontlinesms.plugins.surveys.upload;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.frontlinesms.plugins.surveys.SurveysLogger;

/**
 * Threaded 
 * @author dalezak
 *
 */
public class ThreadedUploader extends Thread {
	
	private static final SurveysLogger LOG = SurveysLogger.getLogger(ThreadedUploader.class);
	
	/**
	 * URL
	 */
	private final URL url;
	
	/**
	 * Content-Type
	 */
	private final String contentType;
	
	/**
	 * Post Body Document
	 */
	private final String document;
	
	/**
	 * ThreadedUploader
	 * @param url URL
	 * @param contentType Content-Type
	 * @param document Post Boy Document
	 * @throws MalformedURLException
	 */
	public ThreadedUploader(String url, String contentType, String document) throws MalformedURLException {
		this.url = new URL(url);
		this.contentType = contentType;
		this.document = document;
	}
	
	public void run() {
		try {
			URLConnection connection = url.openConnection();
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    connection.setUseCaches(false);
		    connection.setDefaultUseCaches(false);
		    connection.setRequestProperty ("Content-Type", contentType);
		    Writer writer = new OutputStreamWriter(connection.getOutputStream());
		    try {
		    	 writer.write(document);
		    	 writer.flush(); 	
		    }
		    finally {
		    	writer.close();	
		    }		 
		    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    try {
		    	String inputLine = null;
		    	StringBuilder response = new StringBuilder();
		        while ((inputLine = reader.readLine()) != null) {
		        	LOG.debug(inputLine);
		        	response.append(inputLine);
		        }
			    LOG.debug("Answer: %s", response.toString());
		    }
		    finally {
		    	reader.close();
		    }
		}
		catch (Exception ex) {
			LOG.error("UploadException: %s", ex);
			ex.printStackTrace();
		}
    }
}