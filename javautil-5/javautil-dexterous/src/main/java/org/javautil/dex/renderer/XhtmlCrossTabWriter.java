package org.javautil.dex.renderer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.javautil.dex.Dexter;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.jdbc.BindVariableValue;

/**
 * @todo reuse the objects from the heading to save space
 * @todo create a string pool set crosstab column columName set crosstab row columnName1,columnName2 set crosstab value columnName
 * @author jim
 *
 */
public class XhtmlCrossTabWriter extends AbstractXmlCrosstabWriter {
    //AbstractCrossTabWriter implements XmlCrossTabWriter {

    @SuppressWarnings("hiding")
    public static final String  revision = "$Revision: 1.1 $";

    @SuppressWarnings("unused")
    private static final Logger logger   = Logger.getLogger(Dexter.class.getName());

    private Element             root;
  //  private static final RenderingCapability capability = new RenderingCapability(MimeType.XHTML,false);
    public void addBindVariableValues(final Collection<BindVariableValue> values) {
        // TODO Auto-generated method stub

    }

    public void process() throws RenderingException {
        root = DocumentHelper.createElement("crosstab");
        checkCrossTab();
        try {
			writeCrossTabHtml();
		} catch (final SQLException e) {
			throw new RenderingException(e);

		}
       // return root;
    }


    /**
     * should encapsulate for encoding by creating a document
     *
     * @throws SQLException
     * @throws IOException
     */
    private void writeCrossTabHtml() throws SQLException {
        crossTabResultSet();
        for (final Object columnLabel : getUniqueColumnLabels().values()) {
            getSortedColumnLabels().put(columnLabel, columnLabel);
        }

        populateRowSortOrder();

        writeHeadingRowHtml();
        final Map<String, ArrayList<String>> rowLeading = getRowLeadingColumns();

        logger.info("rowLeading size " + rowLeading.size());

        for (final Map.Entry<String, ArrayList<String>> entry : getRowLeadingColumns().entrySet()) {

            // StringBuilder sb = new StringBuilder();
            final Element tableRow = root.addElement("tr");

            for (final String keyCol : entry.getValue()) {
                final Element tableHeader = tableRow.addElement("th");
                tableHeader.setText(keyCol);

            }
            final Map<Object, String> dataMap = getRowDataOrder().get(entry.getKey());
            for (final Object columnValue : getSortedColumnLabels().values()) {
                final String cellValue = dataMap.get(columnValue);

                final Element cell = tableRow.addElement("td");

                if (cellValue == null) {
                	//cell.s
                	cell.addCDATA("&nbsp;");
                	//cell.add("&nbsp;");
                    //cell.setText("&nbsp;");
                } else {
                    cell.setText(cellValue);
                }

            }
        }
    }



    private void writeHeadingRowHtml() {

        //StringBuilder sb = new StringBuilder();
        final Element tableRow = root.addElement("tr");
        //sb.append("<tr>");
        for (final String columnHeader : getCrossTabRows()) {
            final Element tableHeader = tableRow.addElement("th");
            tableHeader.setText(columnHeader);
        }

        for (final Object o : getSortedColumnLabels().values()) {
            final Element tableHeader = tableRow.addElement("th");
            tableHeader.setText(o.toString());

        }

        //String output = sb.toString();
        //buff.append(output);

    }

	@Override
	public long getRowsProcessed() {
		// TODO Auto-generated method stub
		return 0;
	}


}
