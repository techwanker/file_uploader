package org.javautil.dex.renderer;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.javautil.dex.dexterous.jdbc.ResultSetHelper;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;


/**
 *
 * TODO use DexterRequest
 *
 */
public class XmlChartWriter extends AbstractRenderer implements Renderer {

	@SuppressWarnings("hiding")
	public static final String revision = "$Revision: 1.1 $";

	private static Logger logger = Logger.getLogger(XmlWriter.class.getName());

	private Element root;



	private String resultSetId;

	private String cursorName;

	public String getCursorName() {
		return cursorName;
	}

    private static final RenderingCapability capability = new RenderingCapability(MimeType.XML_CHART,false);
    public XmlChartWriter() {
    	super(capability);
    }
	public void process() throws RenderingException  {

		setRootElement();
		try {
			getResultSet();
			writeXml();
		} catch (final SQLException e) {
			throw new RenderingException(e);

		}

		//return root;
	}

//	public Element process(Document document) throws SQLException {
//		if (document == null) {
//			throw new IllegalArgumentException("document is null");
//		}
//		Element returnValue = process();
//
//		// @SuppressWarnings("unused") String asXml = returnValue.asXML();
//	//	writeElement(returnValue);
//		logger.debug("returning xml of length " + returnValue.asXML().length());
//		getRset().close();
//		return returnValue;
//	}

	public void setCursorName(final String cursorName) {
		this.cursorName = cursorName;
	}


	public void setEmitMetaData(final boolean emitMetaData) {

	}


//	private void createNewDocument()  {
//		document = DocumentHelper.createDocument();
//		document.addElement(Dexterous.ROOT_ELEMENT_NAME);
//
//
//	}

	private void getResultSet() throws SQLException {

		final ResultSetHelper rsh = new ResultSetHelper(getRset());
		setRows(rsh.getAllRows(10000));

	}

	private void setRootElement() {
		root = DocumentHelper.createElement("chart");
		if (resultSetId != null) {
			root.addAttribute("id", resultSetId);
		}
	}

	/**
	 *
	 * TODO check for duplicate column names
	 * TODO conserve cooked names, don't recompute and waste space
	 * @throws SQLException
	 */
	private void writeXml() throws SQLException {
		final ResultSetMetaData meta = getRset().getMetaData();
		final int columnCount = meta.getColumnCount();

		final Element series = root.addElement("series");
		final ArrayList<Object[]> rows = getTheRows();
		for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
			final Object[] row = rows.get(rowNum);
			if (row == null) {
				throw new IllegalStateException("row is null");
			}
			if (row.length == 0 ) {
				throw new IllegalStateException("row length is 0");
			}
			final Object col = row[0];
			if (col != null) {
				final Element val = series.addElement("value");
				val.addAttribute("xid", Integer.toString(rowNum));
				val.setText(col.toString());
			} else {
				throw new IllegalStateException("row " + rowNum + " column 0 is null");
			}
		}

		final Element graphs = root.addElement("graphs");

		for (int index = 1; index < columnCount; index++) {
		    final Element graph = graphs.addElement("graph").addAttribute("gid", Integer.toString(index));
		    for (int rowNum = 0; rowNum < getRows().size();  rowNum++) {
		    	final Object[] row = rows.get(rowNum);
				final Object col = row[index];
					final Element val = graph.addElement("value");
					val.addAttribute("xid", Integer.toString(rowNum));
					val.setText(col.toString());
				}

			}



	}

	/**
	 * Stub for checking that rows are of uniform size and type
	 * @return
	 */
	ArrayList<Object[]> getTheRows() {
		final ArrayList<Object[]> theRows = getRows();
		return theRows;
	}
}
