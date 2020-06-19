package org.javautil.dex.renderer.interfaces;

public class RenderingException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = -6640491865399134128L;

	public RenderingException(final Exception e) {
		super(e);
	}

	public RenderingException(final String string) {
		super(string);
	}
}
