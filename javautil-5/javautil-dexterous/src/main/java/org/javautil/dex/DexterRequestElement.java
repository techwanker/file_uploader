package org.javautil.dex;

import java.util.ArrayList;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.javautil.jdbc.BindVariableValue;

public class DexterRequestElement {
	private final DexterRequest	request;
	private Element			root;

	public DexterRequestElement(final DexterRequest request) {
		this.request = request;
	}

	public String asXml() {
		getElement();
		return root.asXML();
	}

	public Element getElement() {
		root = DocumentHelper.createElement("dexter");
		final ArrayList <String> breaks = request.getBreaks();
		Element el = null;
		// breaks
		if (breaks != null) {
			for (final String brk : request.getBreaks()) {
				root.addElement("break").addAttribute("columnName", brk);
			}
		}
		// crosstab
		if (request.isCrosstab()) {
			final CrosstabControl cc = request.getCrosstabControl();
			final Element ct = root.addElement("crosstab");
			for (final String columnName : cc.getCrossTabColumns()) {
				el = ct.addElement("column");
				el.setText(columnName);
			}
			for (final String columnName : cc.getCrossTabRows()) {
				el = ct.addElement("row");
				el.setText(columnName);
			}
			for (final String columnName : cc.getCrossTabValues()) {
				el = ct.addElement("value");
				el.setText(columnName);
				// a ddAttribute(arg0, arg1).addAttribute("name",columnName);
			}
		}
		if (request.getDataSourceName() != null) {
			el = root.addElement("dataSourceName");
			el.setText(request.getDataSourceName());
		}
		if (request.getBreaks() != null) {
			for (final String brk : request.getBreaks()) {
				el = root.addElement("break");
				el.setText(brk);
			}
		}
		//
		el = root.addElement("mimeType");
		el.setText(request.getMimeType().toString());
		//
		for (final BindVariableValue bvv : request.getBindVariableValues()) {
			el = root.addElement("bindValue");
			el.addAttribute("variableName", bvv.getVariableName());
			el.addAttribute("value", bvv.getValue().toString());
			el.addAttribute("type", bvv.getValueObject().getClass().getName());
		}
		//
		el = root.addElement("resultSetId");
		el.setData(request.getResultSetId());
		//
		final long aqm = request.getAllowableQueryMilliseconds();
		if (aqm > 0) {
			el = root.addElement("allowableQueryMilliseconds");
			el.setText(Long.toString(aqm));
		}
		//
		el = root.addElement("rowCount");
		el.setText(Long.toString(request.getRowCount()));

		addElement("pumpFromDataSourceName",request.getPumpFromDataSourceName());
		addElement("pumpIntoDataSourceName",request.getPumpIntoDataSourceName());
		addElement("sqlText",request.getSqlText());
		addElement("insertText",request.getInsertText());
		return root;
	}
	private void addElement(final String elementName, final String value) {
		if (value != null) {
			final Element el = root.addElement(elementName);
			el.setText(value);
		}
	}
}
