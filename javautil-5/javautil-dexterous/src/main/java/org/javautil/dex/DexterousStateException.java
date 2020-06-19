package org.javautil.dex;

public class DexterousStateException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DexterousStateException(final Exception e) {
		super(e);
	}

	public DexterousStateException(final String m) {
		super(m);
	}

}
