package org.javautil.jdbc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jjs
 * @since 6/7/2003
 */
public class SelectColumns {
	ArrayList<SelectColumn> cols = new ArrayList<SelectColumn>();

	public SelectColumns() {

	}

	public void add(SelectColumn col) {
		cols.add(col);
	}

	public SelectColumn[] getSelectColumns() {
		SelectColumn[] rc = new SelectColumn[cols.size()];
		int i = 0;
		for (Iterator<SelectColumn> it = cols.iterator(); it.hasNext();) {
			SelectColumn s = it.next();
			rc[i++] = s;
		}
		return rc;
	}

	public int size() {
		return cols.size();
	}

	public void updateFromHtml(HttpServletRequest request) {

		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String n = e.nextElement();

			if (n.startsWith(SelectColumn.PREFIX)) {
				SelectColumn col = getColumn(n);
				String v = request.getParameter(n);
				col.setAttribute(n, v);
			}
		}

	}

	SelectColumn getColumn(String arg) {

		int firstUnderscore = arg.indexOf("_");
		int from = firstUnderscore + 1;
		int secondUnderscore = arg.indexOf("_", from);
		int to = secondUnderscore;

		String n = arg.substring(from, to);

		int index = Integer.parseInt(n);
		return cols.get(index);
	}
}
