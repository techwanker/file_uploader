package org.javautil.dex;

import javax.servlet.http.HttpServletRequest;

public interface SessionAuthorization {
	public  boolean isAuthorized(HttpServletRequest request);
}
