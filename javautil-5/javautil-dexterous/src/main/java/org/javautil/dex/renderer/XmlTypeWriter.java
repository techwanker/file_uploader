package org.javautil.dex.renderer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.javautil.dex.dexterous.jdbc.ResultSetHelper;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;
import org.javautil.jdbc.ResultSetMetaDataHelper;
import org.javautil.xml.XmlTagHelper;

/**
 * @todo reuse the objects from the heading to save space
 * @todo create a string pool set crosstab column columName set crosstab row
 *       columnName1,columnName2 set crosstab value columnName
 * 
 * 
 */
public class XmlTypeWriter extends AbstractRenderer implements Renderer {

	@SuppressWarnings("hiding")
	public static final String revision = "$Revision: 1.1 $";

	private static Logger logger = Logger.getLogger(XmlWriter.class.getName());

	private Element root;

	private ResultSet rset;

	// private Set<WriterWrapper> writers;

	// private StringStores stringStores;
	private ResultSetHelper resultSetHelper;

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd");

	private int rowsProcessed = 0;
	private static final RenderingCapability capability = new RenderingCapability(
			MimeType.XML, false);

	public XmlTypeWriter() {
		super(capability);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.jdbc.CrossTabWriter#process()
	 */

	@Override
	public void process() throws RenderingException {

		root = DocumentHelper.createElement("resultSet");

		try {
			writeXml();
		} catch (final SQLException e) {
			throw new RenderingException(e);
		}
		// return root;
	}

	/**
	 * todo review this code
	 * 
	 * @todo check for duplicate column names
	 * @todo conserve cooked names, don't recompute and waste space
	 * @throws SQLException
	 */
	private void writeXml() throws SQLException {
		final ResultSetMetaData meta = rset.getMetaData();
		final int columnCount = meta.getColumnCount();

		final ArrayList<String> breaks = resultSetHelper.getBreaks();
		ArrayList<Element> breaksList = null;
		if (breaks != null) {
			breaksList = new ArrayList<Element>(breaks.size());
		}
		final HashMap<String, Element> nodeReference = new HashMap<String, Element>();
		Element currentNode = root;
		root.add(ResultSetMetaDataHelper.asElement(meta));
		Object[] rowData;

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
		while ((rowData = resultSetHelper.getRow()) != null) {
			rowsProcessed++;
			int breakLevel;
			if ((breakLevel = resultSetHelper.getBreakLevel()) != -1) {
				currentNode = breaksList.get(breakLevel - 1).getParent();
				for (int a = breakLevel - 1; a < breaks.size(); a++) {
					final String currentBreak = resultSetHelper.getBreak(a);
					currentNode = currentNode.addElement(currentBreak);
					breaksList.set(a, currentNode);
					nodeReference.put(currentBreak, currentNode);
				}
			}
			final Element row = currentNode.addElement("row");
			// logger.info("adding row");
			for (int i = 1; i <= columnCount; i++) {
				final String cellValue = resultSetHelper.getString(i);
				if (cellValue != null) {
					final String rawAttributeName = meta.getColumnName(i);
					final String cookedName = XmlTagHelper
							.getTagName(rawAttributeName);
					if (breaks != null && breaks.contains(rawAttributeName)) {
						nodeReference.get(cookedName).addAttribute("VALUE",
								cellValue);
					} else {
						row.addAttribute(cookedName, cellValue);
					}
				}
			}
		}
		// logger.info(root.asXML().);

	}

}
