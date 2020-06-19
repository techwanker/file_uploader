package org.javautil.dex.renderer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.javautil.dex.XmlRenderer;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;

/**
 * @todo reuse the objects from the heading to save space
 * @author jim
 *
 */
public class XmlCrossTabWriter extends AbstractCrosstabWriter implements XmlRenderer {
	@SuppressWarnings("hiding")
	public static final String	revision	= "$Revision: 1.1 $";
	@SuppressWarnings("unused")
	private final Logger	logger		= Logger.getLogger(this.getClass().getName());
	private Element				root;
	private static final RenderingCapability capability = new RenderingCapability(MimeType.XML,true);
    public XmlCrossTabWriter() {
    	super(capability);
    }
	public void process() throws RenderingException  {
		root = DocumentHelper.createElement("crosstab");
		checkCrossTab();
		writeCrossTab();

	}

	public Element getRenderedElement() {
		return root;
	}

	public void setEmitMetaData(final boolean emitMetaData) {

	}

	// do something with this
	public void setResultSetId(final String resultSetId) {
	}

	/**
	 * should encapsulate for encoding by creating a document
	 * @throws RenderingException
	 *
	 * @throws SQLException
	 * // todo use ResultSetCrosstabber

	 */
	private void writeCrossTab() throws RenderingException  {
		try {
			crossTabResultSet();
		} catch (final SQLException e) {
			throw new RenderingException(e);
		}
		for (final Object columnLabel : getUniqueColumnLabels().values()) {
			getSortedColumnLabels().put(columnLabel, columnLabel);
		}
		populateRowSortOrder();
		final Map<String, ArrayList<String>> rowLeading = getRowLeadingColumns();
		logger.info("rowLeading size " + rowLeading.size());
		for (final Map.Entry<String, ArrayList<String>> entry : getRowLeadingColumns().entrySet()) {
			final Element tableRow = root.addElement("row");
			final ArrayList<String> rowKey = getCrossTabRows();
			final ArrayList<String> rowValue = entry.getValue();
			for (int i = 0; i < rowKey.size(); i++) {
				tableRow.addAttribute(rowKey.get(i), rowValue.get(i));
			}
			final HashMap<Object, Object> dataMap = getRowDataOrder().get(entry.getKey());
			for (final Object columnValue : getSortedColumnLabels().values()) {
				Element xtab = null;
				final Object cellValue = dataMap.get(columnValue);
				if (cellValue != null) {
				//	if (xtab == null) {
						xtab = tableRow.addElement("xtab");
				//	}
					xtab.addAttribute("key", columnValue.toString());
					xtab.addAttribute("value", cellValue.toString());
				}
			}
		}
	}


}
