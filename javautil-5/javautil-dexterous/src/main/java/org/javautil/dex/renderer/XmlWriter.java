package org.javautil.dex.renderer;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;
import org.javautil.jdbc.ResultSetMetaDataHelper;
import org.javautil.xml.XmlTagHelper;

/**
 * 
 * TODO use DexterRequest
 * 
 * todo fix add output to xml element in state
 */
public class XmlWriter extends AbstractRenderer implements Renderer {

	@SuppressWarnings("hiding")
	public static final String revision = "$Revision: 1.1 $";

	private static Logger logger = Logger.getLogger(XmlWriter.class.getName());

	private static final String newline = System.getProperty("line.separator");
	private Element root;

	private String resultSetId;

	private boolean emitMetaData;

	private String cursorName;

	private boolean emitColumnsAsElementText;
	private static final RenderingCapability capability = new RenderingCapability(
			MimeType.XML, false);

	public XmlWriter() {
		super(capability);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.jdbc.CrossTabWriter#process()
	 */

	public String getCursorName() {
		return cursorName;
	}

	@Override
	public void process() throws RenderingException {

		setRootElement();

		try {
			writeXml();
		} catch (final SQLException e) {
			throw new RenderingException(e);
		}

	}

	// public void setCursorName(String cursorName) {
	// this.cursorName = cursorName;
	// }

	public void setEmitColumnsAsElementText(final boolean emitXmlAsElement) {
		this.emitColumnsAsElementText = emitXmlAsElement;

	}

	public void setEmitMetaData(final boolean emitMetaData) {
		this.emitMetaData = emitMetaData;

	}

	public void setResultSetId(final String resultSetId) {
		this.resultSetId = resultSetId;

	}

	private void setRootElement() {
		root = DocumentHelper.createElement("resultSet");
		if (resultSetId != null) {
			root.addAttribute("id", resultSetId);
		}
	}

	/**
	 * 
	 * TODO check for duplicate column names TODO conserve cooked names, don't
	 * recompute and waste space
	 * 
	 * @throws SQLException
	 */
	@SuppressWarnings("null")
	private void writeXml() throws SQLException {
		final long startTime = System.nanoTime();
		final ResultSetMetaData meta = getRset().getMetaData();
		final int columnCount = meta.getColumnCount();

		final ArrayList<String> breaks = getResultSetHelper().getBreaks();
		ArrayList<Element> breaksList = null;
		if (breaks != null) {
			breaksList = new ArrayList<Element>(breaks.size());
		}
		final HashMap<String, Element> nodeReference = new HashMap<String, Element>();
		Element currentNode = root;
		if (emitMetaData) {
			root.add(ResultSetMetaDataHelper.asElement(meta));
		}
		// Object[] rowData;

		// prepare the XML for its first row data from the breaks
		if (breaks != null) {
			for (int a = 0; a < breaks.size(); a++) {
				final String currentBreak = breaks.get(a);
				currentNode = currentNode.addElement(currentBreak);
				breaksList.add(currentNode);
				nodeReference.put(currentBreak, currentNode);
			}
		}

		// populate the XML
		while (getResultSetHelper().getRow() != null) {
			rowsProcessed++;

			int breakLevel;
			if ((breakLevel = getResultSetHelper().getBreakLevel()) != -1) {
				currentNode = breaksList.get(breakLevel - 1).getParent();
				for (int a = breakLevel - 1; a < breaks.size(); a++) {
					final String currentBreak = getResultSetHelper()
							.getBreak(a);
					currentNode = currentNode.addElement(currentBreak);
					breaksList.set(a, currentNode);
					nodeReference.put(currentBreak, currentNode);
				}
				// @todo process when logic here
			}
			final Element row = currentNode.addElement("row");
			// logger.info("adding row");
			for (int i = 1; i <= columnCount; i++) {
				final String cellValue = getResultSetHelper().getString(i);
				if (cellValue != null) {
					final String rawAttributeName = meta.getColumnName(i);
					final String cookedName = XmlTagHelper
							.getTagName(rawAttributeName);
					if (breaks != null && breaks.contains(rawAttributeName)) {
						nodeReference.get(cookedName).addAttribute("VALUE",
								cellValue);
						// todo check what to do if emitXml
					} else {
						if (emitColumnsAsElementText) {
							final Element columnEl = row.addElement(cookedName);
							columnEl.setText(cellValue);
						} else {
							row.addAttribute(cookedName, cellValue);
						}
					}
				}
			}
		}
		final long endTime = System.nanoTime();
		final double elapsedSeconds = (endTime - startTime) / 1e9;
		if (logger.isDebugEnabled()) {
			logger.debug("xml row count " + rowsProcessed + " elapsedSeconds "
					+ elapsedSeconds);
		}
		// logger.info(root.asXML().);

	}

}
