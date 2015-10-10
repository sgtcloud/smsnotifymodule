package cn.sgtcloud.common.smsnotifymodule.service.exception;

public class SmsNotifyException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8365331424168523659L;

	public SmsNotifyException() {
		super();
	}

	public SmsNotifyException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SmsNotifyException(String message, Throwable cause) {
		super(message, cause);
	}

	public SmsNotifyException(String message) {
		super(message);
	}

	public SmsNotifyException(Throwable cause) {
		super(cause);
	}
}
