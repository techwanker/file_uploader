package org.javautil.dex.renderer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;
import org.javautil.text.EncodingEscapeHelper;

/**
 *
 * @author jim
 *
 */
public class HtmlWriter extends AbstractDexterWriter {



    @SuppressWarnings("hiding")
    public static final String revision = "$Revision: 1.1 $";
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private SimpleDateFormat dateFormatter;
    private long rowCount = 0;
    private static final RenderingCapability capability = new RenderingCapability(MimeType.HTML,false);
    public HtmlWriter() {
    	super(capability);
    	logger.setLevel(Level.WARN);
    }


    /*
     * (non-Javadoc)
     *
     * @see org.javautil.jdbc.CrossTabWriter#process()
     */

    public void process() throws IOException, RenderingException  {

        try {
			writeHtml();
		} catch (final SQLException e) {
			throw new RenderingException(e);
		}
        // writer.write(buff.toString());
    }



	private void writeHeadingRowHtml() throws SQLException, IOException {

        final ResultSetMetaData meta = getRset().getMetaData();
        if (meta == null) {
            throw new IllegalStateException("meta null");
        }

        write("<tr>");
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            final String columnName = meta.getColumnName(i);
            write("<th>");
            write(columnName);
            write("</th>");
        }
        write("</tr>\n");

    }

	/**
     * should encapsulate for encoding by creating a document
     *
     * @throws SQLException
     * @throws IOException
     */
    private void writeHtml() throws SQLException, IOException {
        final ResultSet rset = getRset();
        final ResultSetMetaData metaData = rset.getMetaData();
        final int columnCount = metaData.getColumnCount();
        logger.info("dateFormatter is " + dateFormatter);
        write("<table>\n");
        writeHeadingRowHtml();
        final StringBuilder b = new StringBuilder();
        for (int i = 1; i < columnCount; i++) {
           b.append("column #: " + i + " columnName " + metaData.getColumnName(i) + " " + metaData.getColumnType(i) + "\n");
        }
        logger.info("meta data:\n" + b.toString());
        while (rset.next()) {
        	rowCount++;
            write(" <tr>");

           // write("<th>");
            for (int i = 1; i <= columnCount; i++) {

                final String cellValue = rset.getString(i);

                if (cellValue == null) {
                    write("  <td/>\n");
                } else {
                    write("  <td>");
                	if ((metaData.getColumnType(i) == java.sql.Types.DATE || metaData.getColumnTypeName(i).equals("DATE") ||
                			metaData.getColumnType(i) == java.sql.Types.TIME)
                	    && dateFormatter != null) {

                			final java.sql.Date d = rset.getDate(i);
                			final String outDate = dateFormatter.format(d);
                			write(EncodingEscapeHelper.toDisableTags(outDate));

                	} else {
                           write(EncodingEscapeHelper.toDisableTags(cellValue));
                	}

                    write("</td>\n");
                }

            }
            write("</tr>");

        }

        write("</table>\n");

    }
}
