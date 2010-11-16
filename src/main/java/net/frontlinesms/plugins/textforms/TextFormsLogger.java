package net.frontlinesms.plugins.textforms;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class TextFormsLogger extends Logger {

	private static final TextFormsLoggerFactory LoggerFactory = new TextFormsLoggerFactory();
	
	public static TextFormsLogger getLogger(String name) {
		return (TextFormsLogger) Logger.getLogger(name, LoggerFactory);
	}
	
	public static TextFormsLogger getLogger(Class<? extends Object> clazz) {
		return (TextFormsLogger) Logger.getLogger(clazz.getName(), LoggerFactory);
	}
	
	protected TextFormsLogger(String name) {
		super(name);
	}
	
	public void debug(String format, Object ... args) {
		debug(String.format(format, args));
	}
	
	public void debug(String format, String [] args) {
		StringBuffer sb = new StringBuffer();
		for (String arg : args) {
			if (sb.length() > 0) {
				 sb.append(",");
			}
			else {
				 sb.append("[");
			}
			sb.append(arg);
		}
		sb.append("]");
		debug(String.format(format, sb));
	}

	@Override
	public void debug(Object text) {
		if (TextFormsProperties.isDebugMode()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getName());
			stringBuilder.append(" - ");
			stringBuilder.append(text);
			System.out.println(stringBuilder);
		}
		else {
			super.debug(text);
		}
	}
	
	public void error(String format, Object ... args) {
		error(String.format(format, args));
	}
	
	public void error(String format, String [] args) {
		StringBuffer sb = new StringBuffer();
		for (String arg : args) {
			if (sb.length() > 0) {
				 sb.append(",");
			}
			else {
				 sb.append("[");
			}
			sb.append(arg);
		}
		sb.append("]");
	    error(String.format(format, sb));
	}
	
	@Override
	public void error(Object text) {
		if (TextFormsProperties.isDebugMode()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.getName());
			stringBuilder.append(" - ");
			stringBuilder.append(text);
			System.err.println(stringBuilder);
		}
		else {
			super.error(text);
		}
	}
	
	public void out(String message) {
		System.out.println(message);
	}
	
	public void out(String format, Object ... args) {
		System.out.println(String.format(format, args));
	}
	
}

class TextFormsLoggerFactory implements LoggerFactory {
    public TextFormsLoggerFactory() { 
    }

    public Logger makeNewLoggerInstance(String name) {
        return new TextFormsLogger(name);
    }
}