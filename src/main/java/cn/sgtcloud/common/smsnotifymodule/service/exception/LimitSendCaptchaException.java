package cn.sgtcloud.common.smsnotifymodule.service.exception;

/**
 * 非法的模板內容
 * 
 * @author peisy
 *
 */
public class LimitSendCaptchaException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5950891682517303424L;

	public LimitSendCaptchaException() {
		super();
	}

	public LimitSendCaptchaException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LimitSendCaptchaException(String message, Throwable cause) {
		super(message, cause);
	}

	public LimitSendCaptchaException(String message) {
		super(message);
	}

	public LimitSendCaptchaException(Throwable cause) {
		super(cause);
	}
}
