package cn.sgtcloud.common.smsnotifymodule.service.exception;

/**
 * 非法的模板內容
 * 
 * @author peisy
 *
 */
public class IllegalTemplateContentException extends RuntimeException {

	private static final long serialVersionUID = -652695019358330170L;
	
	public IllegalTemplateContentException() {
		super();
	}

	public IllegalTemplateContentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IllegalTemplateContentException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalTemplateContentException(String message) {
		super(message);
	}

	public IllegalTemplateContentException(Throwable cause) {
		super(cause);
	}
}
