package net.frontlinesms.plugins.resourcemapper;

import net.frontlinesms.resources.UserHomeFilePropertySet;

public class ResourceMapperProperties extends UserHomeFilePropertySet{

	private static ResourceMapperProperties instance;
	
	protected ResourceMapperProperties() {
		super("resourcemapper");
	}

	public static synchronized ResourceMapperProperties getInstance() {
		if(instance == null) {
			instance = new ResourceMapperProperties();
		}
		return instance;
	}
	
	public static boolean isInDebugMode(){
		return getInstance().getProperty("debug.mode").equalsIgnoreCase("true");
	}
}
