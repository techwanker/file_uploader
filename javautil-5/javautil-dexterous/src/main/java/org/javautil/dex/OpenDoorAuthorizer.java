package org.javautil.dex;

import javax.servlet.http.HttpServletRequest;

public class OpenDoorAuthorizer implements SessionAuthorization {

	public boolean isAuthorized(final HttpServletRequest request) {
		return true;
	}

}
