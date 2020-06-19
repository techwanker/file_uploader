package org.javautil.jdbc.resultset;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import org.dom4j.Element;

public class ResultSetFormatRule {

	private String label = null;
	private String name;
	private int type;
	private String className;
	private String format;
	private Locale locale;
	private int precision;
	private int scale;
	private String align;

	public static ArrayList<ResultSetFormatRule> getDefaultFormatRules(
			ResultSetMetaData meta) throws SQLException {
		ArrayList<ResultSetFormatRule> returnValue = new ArrayList<ResultSetFormatRule>();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			returnValue.add(new ResultSetFormatRule(meta, i));
		}
		return returnValue;
	}

	// private String getDefaultFormat(int type, int precision, int scale ) {
	//
	// switch (type) {
	// case Types.NUMERIC: return "%,f"; // comma separated
	// case Types.TIME: return "%H:M"; // e.g. 13:23
	// case Types.DATE: return "%1$Y-%1$m-%1$D";
	// case Types.TIMESTAMP: return "%1$Y-%1$m-%1$D %1$H:%1$M";
	// default:
	// throw new IllegalArgumentException("unformattable type");
	// }
	//
	// }
	//
	// private String getDefaultFormat(ResultSetFormatRule rule ) {
	// switch (rule.type) {
	// case Types.NUMERIC: return "%,f"; // comma separated
	// case Types.TIME: return "%H:M"; // e.g. 13:23
	// case Types.DATE: return "%Y-m-D";
	// case Types.TIMESTAMP: return "%H:m-Dd";
	// default:
	// throw new IllegalArgumentException("unformattable type");
	// }
	//
	// }
	/**
	 * Convenience method for instantiating from a dom
	 * 
	 * @param rule
	 *            an Element with the following attributes example <rule
	 *            label="Cust #" name="cust_cd" /> or <rule label="Invoice Date"
	 *            name="inv_dt" format="%m-Y" />
	 * 
	 *            </ul>
	 * @param locale
	 */
	public ResultSetFormatRule(Element rule, Locale locale) {
		label = rule.valueOf("@label");
		name = rule.valueOf("@name");
		format = rule.valueOf("@format");
		this.locale = locale;
		// String columnType = rule.valueOf("@type");
		// type = Integer.parseInt(columnType);
		// this.locale = locale;
	}

	/**
	 * @todo finish off getDefaultFormat
	 * @param meta
	 * @param column
	 * @throws SQLException
	 */
	public ResultSetFormatRule(ResultSetMetaData meta, int column)
			throws SQLException {
		label = meta.getColumnLabel(column);
		name = meta.getColumnName(column);
		type = meta.getColumnType(column);
		className = meta.getColumnClassName(column);
		precision = meta.getPrecision(column);
		scale = meta.getPrecision(scale);
		// format = getDefaultFormat(this);
	}

	public Element addElement(Element node) {
		Element el = node.addElement("formatRule");
		el.addAttribute("name", name);
		el.addAttribute("label", label);
		el.addAttribute("format", format);
		return el;
	}

	/**
	 * @return Returns the align.
	 */
	public String getAlign() {
		return align;
	}

	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return Returns the format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return Returns the locale.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the precision.
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @return Returns the scale.
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param align
	 *            The align to set.
	 */
	public void setAlign(String align) {
		this.align = align;
	}

	/**
	 * @param className
	 *            The className to set.
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @param format
	 *            The format to set.
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @param label
	 *            The label to set.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @param locale
	 *            The locale to set.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param precision
	 *            The precision to set.
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * @param scale
	 *            The scale to set.
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}
}
