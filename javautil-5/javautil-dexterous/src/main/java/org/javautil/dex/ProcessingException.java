package org.javautil.dex;

@SuppressWarnings("serial")
public class ProcessingException extends Exception {
	Exception exception;

	public ProcessingException(final Exception e) {
		this.exception = e;
	}

	@Override
	public String getMessage() {
		return exception.getMessage();
	}

}
