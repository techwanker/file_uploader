package org.javautil.dex.renderer;

import org.javautil.dex.dexterous.DexterousState;
import org.javautil.document.MimeType;

public class RenderingCapability {
	private final MimeType mimeType;
	private final boolean crosstab;

	public RenderingCapability(final MimeType mimeType, final boolean isCrosstab) {
		if (mimeType == null) {
			throw new IllegalArgumentException("mimeType is null");
		}
		this.mimeType = mimeType;
		this.crosstab = isCrosstab;

	}

	/**
	 * @return the mimeType
	 */
	public MimeType getMimeType() {
		return mimeType;
	}

	/**
	 * @return the crosstab
	 */
	public boolean isCrosstab() {
		return crosstab;
	}

	public boolean canRender(final DexterousState state) {
		boolean retval = true;

		if (!mimeType.equals(state.getMimeType())) {
			retval = false;
		}

		if (crosstab != state.isCrossTab()) {
			retval = false;
		}
		return retval;
	}
}
