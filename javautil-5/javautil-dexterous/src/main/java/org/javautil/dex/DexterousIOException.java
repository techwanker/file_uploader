package org.javautil.dex;


/**
 * Should be used when the user did something stupid like request a binary file with no destination
 * @author jjs
 *  tb - Handling exceptions
 */
public class DexterousIOException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DexterousIOException(final Exception e) {
		super(e);
	}

	public DexterousIOException(final String m) {
		super(m);
	}

}
