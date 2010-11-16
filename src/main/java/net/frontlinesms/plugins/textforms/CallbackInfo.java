package net.frontlinesms.plugins.textforms;

import java.util.Date;

import net.frontlinesms.plugins.textforms.handler.questions.CallbackHandler;

import com.ibm.icu.util.Calendar;

@SuppressWarnings("unchecked")
public class CallbackInfo {
	
	@SuppressWarnings("unused")
	private static final TextFormsLogger LOG = TextFormsLogger.getLogger(CallbackInfo.class);
	
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

	public boolean hasTimedOut(double minutes) {
		return (((double)Calendar.getInstance().getTimeInMillis() - (double)startTime) / (60 * 1000)) > minutes;
	}
	
}
