package net.frontlinesms.plugins.resourcemapper;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class ResourceMapperLogger extends Logger {

	private static final ResourceMapperLoggerFactory LoggerFactory = new ResourceMapperLoggerFactory();
	
	public static ResourceMapperLogger getLogger(String name) {
		return (ResourceMapperLogger) Logger.getLogger(name, LoggerFactory);
	}
	
	public static ResourceMapperLogger getLogger(Class<? extends Object> clazz) {
		return (ResourceMapperLogger) Logger.getLogger(clazz.getName(), LoggerFactory);
	}
	
	protected ResourceMapperLogger(String name) {
		super(name);
	}
	
	public void debug(String format, Object ... args) {
		debug(String.format(format, args));
	}

	@Override
	public void debug(Object text) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getName());
		stringBuilder.append(" - ");
		stringBuilder.append(text);
		System.out.println(stringBuilder);
	}
	
	public void error(String format, Object ... args) {
		error(String.format(format, args));
	}
	
	@Override
	public void error(Object text) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getName());
		stringBuilder.append(" - ");
		stringBuilder.append(text);
		System.err.println(stringBuilder);
	}
	
}

class ResourceMapperLoggerFactory implements LoggerFactory {
    public ResourceMapperLoggerFactory() { 
    }

    public Logger makeNewLoggerInstance(String name) {
        return new ResourceMapperLogger(name);
    }
}