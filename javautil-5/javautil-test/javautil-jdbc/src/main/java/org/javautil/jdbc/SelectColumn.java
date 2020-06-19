package org.javautil.jdbc;

import java.sql.ResultSetMetaData;

import org.javautil.text.StringHelper;

/**
 * @author jjs
 * @since 6/7/2003
 */
public class SelectColumn {

	public static final String PREFIX = "column_";

	public static final String HREF = "_href";

	public static final String FORMAT = "_format";

	public static final String HIDDEN = "_hidden";

	public static final String LABEL = "_label";

	private final String name;

	private String label = "";

	private String formatMask = "";

	private String href = "";

	private String columnTypeName;

	private int columnPrecision;

	private int columnScale;

	public static String getFormatTag(int i) {
		return PREFIX + i + FORMAT;
	}

	public static String getHiddenTag(int i) {
		return PREFIX + i + HIDDEN;
	}

	public static String getHrefTag(int i) {
		return PREFIX + i + HREF;
	}

	public static String getLabelTag(int i) {
		return PREFIX + i + LABEL;
	}

	public static String getTypeText() {
		return ("String");
	}

	public SelectColumn(ResultSetMetaData meta, int column)
			throws java.sql.SQLException {

		this.name = meta.getColumnLabel(column);
		this.label = StringHelper.labelName(name);
		this.columnTypeName = meta.getColumnTypeName(column);
		this.columnPrecision = meta.getPrecision(column);
		this.columnScale = meta.getScale(column);
	}

	public SelectColumn(String name) {
		this.name = name;
		this.label = StringHelper.labelName(name);
	}

	public int getColumnPrecision() {
		return columnPrecision;
	}

	public int getColumnScale() {
		return columnScale;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public String getFormatMask() {
		return formatMask;
	}

	public String getHref() {
		return href;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public void setAttribute(String attribute, String value) {
		if (attribute.endsWith(HREF)) {
			this.href = value;
		} else if (attribute.endsWith(FORMAT)) {
			this.formatMask = value;
		} else if (attribute.endsWith(LABEL)) {
			this.label = value;
		}
	}

	public void setColumnPrecision(int precision) {
		this.columnPrecision = precision;
	}

	public void setColumnScale(int scale) {
		this.columnScale = scale;
	}

	public void setColumnTypeName(String name) {
		this.columnTypeName = name;
	}

	public void setFormatMask(String mask) {
		this.formatMask = mask;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
