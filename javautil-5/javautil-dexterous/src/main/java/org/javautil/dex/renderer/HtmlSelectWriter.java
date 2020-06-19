package org.javautil.dex.renderer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.javautil.dex.dexterous.jdbc.ResultSetHelper;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;



/**
 * @todo reuse the objects from the heading to save space
 * @todo create a string pool set crosstab column columName set crosstab row columnName1,columnName2 set crosstab value columnName
 * @author jim
 *
 */
public class HtmlSelectWriter extends AbstractRenderer {

    @SuppressWarnings("hiding")
    public static final String revision        = "$Revision: 1.1 $";

    // private Document document;
    private Element            root;

    private String             htmlElementName = null;

    private ResultSet          rset;

    private int rowsProcessed = -1;

    private final String dateFormat = "yyyy-MM-dd";
    private static final RenderingCapability capability = new RenderingCapability(MimeType.HTML_SELECT,false);
    //private SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    /*
     * (non-Javadoc)
     *
     * @see org.javautil.jdbc.CrossTabWriter#process()
     */

    public HtmlSelectWriter() {
    	super(capability);

    }

//    public long getRowsProcessed() {
//    	return rowsProcessed;
//    }

    public void  process() throws RenderingException {
    	try {
    		   this.htmlElementName = getState().getHtmlElementName();
        //String elementName = null;
        Element select = null;

        if (rset == null) {
            throw new IllegalStateException("setRset has not been called");
        }
        if (htmlElementName == null) {
            throw new IllegalStateException("htmlElementName is null");
        }
        final ResultSetMetaData meta = rset.getMetaData();
        if (meta == null) {
            throw new IllegalStateException("meta null");
        }

        final int columnCount = meta.getColumnCount();

        if (columnCount > 2) {
            throw new IllegalArgumentException("resultSet must no more than 2 columns");
        }
        final ResultSetHelper rsetHelper = new ResultSetHelper(rset);
        rsetHelper.setDateFormat(dateFormat);
        root = DocumentHelper.createElement("html");
        root.setText(htmlElementName);

            select = root.addElement("select");
            select.addAttribute("name",htmlElementName);

        while (rset.next()) {
            rowsProcessed++;
            int colIndex = 1;
            //if (columnCount )
            final String optionValue = rsetHelper.getString(colIndex++);
            String optionDisplay = null;
            if (columnCount == 1) {
                optionDisplay = optionValue;
            } else {
                optionDisplay = rsetHelper.getString(colIndex++);
            }
            final Element option = select.addElement("option");
            for (int i = 1; i <= columnCount; i++) {
                option.addAttribute("value", optionValue);
                option.setText(optionDisplay);

            }
        }
    	} catch (final SQLException sqe) {
    		throw new RenderingException(sqe);
    	}

    }

//    public void process(Writer writer) throws SQLException, IOException {
//
//        if (writer == null) {
//            throw new IllegalArgumentException("writer is null");
//        }
//
//        process();
//
//        OutputFormat format = OutputFormat.createPrettyPrint();
//        XMLWriter xw = new XMLWriter(writer, format);
//        xw.write(root);
//        xw.flush();
//
//    }
//
//    public void setDateFormat(String format) {
//        dateFormat = format;
//      //  dateFormatter = new SimpleDateFormat(format);
//    }
//
//    public void setResultSet(ResultSet rset) {
//        this.rset = rset;
//    }

}
