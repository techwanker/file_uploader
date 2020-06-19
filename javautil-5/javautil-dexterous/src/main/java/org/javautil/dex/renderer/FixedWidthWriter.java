package org.javautil.dex.renderer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;
import org.javautil.text.StringHelper;


/**
 *
 * @author jim
 *
 * @todo create a mime type for fixed width
 *
 */
public class FixedWidthWriter extends AbstractDexterWriter {



    @SuppressWarnings("hiding")
    public static final String revision = "$Revision: 1.1 $";
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private SimpleDateFormat dateFormatter;
    private long rowCount = 0;
    private static final RenderingCapability capability = new RenderingCapability(MimeType.FIXED_WIDTH,false);
    public FixedWidthWriter() {
    	super(capability);
    	logger.setLevel(Level.WARN);
    }
//    // belongs in super
//    public long getRowsProcessed(){
//    	return rowCount;
//    }
//    public long getRowCount() {
//		return rowCount;
//	}


    /*
     * (non-Javadoc)
     *
     * @see org.javautil.jdbc.CrossTabWriter#process()
     */
   
    public void process() throws  RenderingException {
    	try {

        final ResultSet rset = getRset();
        final ResultSetMetaData metaData = rset.getMetaData();
        final int columnCount = metaData.getColumnCount();
        logger.info("dateFormatter is " + dateFormatter);

        final StringBuilder b = new StringBuilder();
        logger.info("meta data:\n" + b.toString());
        while (rset.next()) {
        	rowCount++;
            for (int i = 1; i <= columnCount; i++) {
                final String value = rset.getString(i);
                final int width = metaData.getColumnDisplaySize(i);
                final String padValue = StringHelper.rightPad(value,width);
                write(padValue);
                }
        }
        write("\n");
    	} catch (final Exception e) {
    		throw new RenderingException(e);
    	}
    }
}
