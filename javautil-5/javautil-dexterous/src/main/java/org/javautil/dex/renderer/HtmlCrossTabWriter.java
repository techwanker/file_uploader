package org.javautil.dex.renderer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;
import org.javautil.jdbc.BindVariableValue;
import org.javautil.text.EncodingEscapeHelper;


/**
 * @todo reuse the objects from the heading to save space
 * @todo create a string pool set crosstab column columName set crosstab row columnName1,columnName2 set crosstab value columnName
 * @author jim
 *
 */
public class HtmlCrossTabWriter extends AbstractCrosstabWriter implements Renderer {
    //AbstractCrossTabWriter implements XmlCrossTabWriter {

    @SuppressWarnings("hiding")
    public static final String  revision = "$Revision: 1.1 $";

    private static final Logger logger = Logger.getLogger(HtmlCrossTabWriter.class.getName());

    private final StringBuilder buff = new StringBuilder();

    private static final RenderingCapability capability = new RenderingCapability(MimeType.HTML,true);
  //  private long rowCount = -1;

    public HtmlCrossTabWriter() {
    	super(capability);
    }

    @Override
	public void addBindVariableValues(final Collection<BindVariableValue> values) {
        // TODO Auto-generated method stub

    }


    public void process() throws RenderingException {
      if (getWriters() == null) {
          throw new java.lang.IllegalArgumentException("writers is null");
      }
     // setWriters(writers);
      buff.append("<table border='1'>");

      checkCrossTab();
      try {
      writeCrossTabHtml();
      buff.append("</table>");

      logger.info("writing " + buff.toString());
      write(buff.toString());
      } catch (final Exception e) {
    	  throw new RenderingException(e);
      }
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
        for (final Map.Entry<String, ArrayList<String>> entry : getRowLeadingColumns().entrySet()) {

            buff.append("<tr>");

            for (final String keyCol : entry.getValue()) {
            	buff.append("<th>");
                buff.append(EncodingEscapeHelper.toDisableTags(keyCol));
                buff.append("</th>");
            }
            final HashMap<Object, Object> dataMap = getRowDataOrder().get(entry.getKey());
            for (final Object columnValue : getSortedColumnLabels().values()) {
                final Object cellValue = dataMap.get(columnValue);
                buff.append("<td>");
                if (cellValue == null) {
                	buff.append("&nbsp;");
                } else {
                	buff.append(EncodingEscapeHelper.toDisableTags(cellValue.toString()));
                }
            }
            buff.append("</tr>");
        }
    }



    private void writeHeadingRowHtml() {

        buff.append("<tr>");

        for (final String columnHeader : getCrossTabRows()) {
            buff.append("<th>");
            buff.append(columnHeader);
            buff.append("</th>");
        }

        for (final Object o : getSortedColumnLabels().values()) {
            buff.append("<th>");
            buff.append(EncodingEscapeHelper.toDisableTags(o.toString()));

            buff.append("</th>");
        }
        buff.append("</tr>");
    }
}
