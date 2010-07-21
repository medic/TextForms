package net.frontlinesms.plugins.resourcemapper;

import java.util.Date;

import net.frontlinesms.plugins.resourcemapper.handler.fields.CallbackHandler;

import com.ibm.icu.util.Calendar;

@SuppressWarnings("unchecked")
public class CallbackInfo {
	
	@SuppressWarnings("unused")
	private static ResourceMapperLogger LOG = ResourceMapperLogger.getLogger(CallbackInfo.class);
	
	private String phoneNumber;
	private CallbackHandler handler;
	private long startTime;

	public CallbackInfo(String phoneNumber, CallbackHandler handler) {
		super();
		this.phoneNumber = phoneNumber;
		this.handler = handler;
		this.startTime = new Date().getTime();
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public CallbackHandler getHandler() {
		return handler;
	}
	
	public void setHandler(CallbackHandler handler) {
		this.handler = handler;
	}

	public boolean hasTimedOut() {
		//TODO load from properties file
		//String[] timeout = ResourceMapperProperties.getInstance().getProperties().get("callback.timeout").split(":");
		String [] timeout = new String [] {"0", "05"};
		int hour = Integer.parseInt(timeout[0]);
		int minute = Integer.parseInt(timeout[1]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0L);
		if (hour > 0) {
			calendar.set(Calendar.HOUR, hour);
		}
		if (minute > 0) {
			calendar.set(Calendar.MINUTE, minute);			
		}
		long timeoutTime = calendar.getTimeInMillis();
		long passedTime = Calendar.getInstance().getTimeInMillis() - startTime;
		if (timeoutTime <= passedTime) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
