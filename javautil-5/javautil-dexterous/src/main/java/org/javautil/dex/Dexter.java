package org.javautil.dex;

import org.apache.log4j.Logger;


/**
 *
 * @author jim
 *
 */
public class Dexter {
	public static final String					revision		= "$Revision: 1.1 $";

	private static final String					newline			= System.getProperty("line.separator");

	private final Logger						logger			= Logger.getLogger(Dexter.class.getName());









//
//	/**
//	 * @todo move somewhere
//	 * @param request
//	 */
//	public void setBindVariables(HttpServletRequest request) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("bind variables are ");
//		for (BindVariable bf : state.getBindVariables().values()) {
//			sb.append("'" + bf.getBindName() + "'");
//		}
//		Enumeration<String> e = request.getParameterNames();
//		while (e.hasMoreElements()) {
//			String n = e.nextElement();
//			String v = request.getParameter(n);
//			String bindName = n.toUpperCase();
//			BindVariable b = state.getBindVariables().get(bindName);
//			if (b != null) {
//				BindVariableValue bv = new BindVariableValue(bindName, b.getJdbcType(), v);
//				state.getBindValues().put(bindName, bv);
//			} else {
//				// logger.info("not a bind variable " + bindName + " value " +
//				// v);
//			}
//		}
//	}




}
