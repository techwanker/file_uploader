package org.javautil.dex.renderer;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;

	/**
	 *
	 * @author jim
	 *
	 */
	public class CsvCrosstabWriter extends AbstractCrosstabWriter implements Renderer {

		private static final RenderingCapability capability = new RenderingCapability(MimeType.CSV,true);


	    @SuppressWarnings("hiding")
	    public static final String  revision = "$Revision: 1.1 $";

	    @SuppressWarnings("unused")
	    private static final Logger logger   = Logger.getLogger(CsvCrosstabWriter.class.getName());

	    private final StringBuilder       buff     = new StringBuilder();

	    public CsvCrosstabWriter() {
	    	super(capability);
	    }

	//  @Override
	  public void process() throws RenderingException {
	      //setWriters(writers);
	      checkCrossTab();
	      try {
	                writeCrossTabCsv();
	            write(buff.toString());
	              flush();
	        } catch (final Exception e) {
	                throw new RenderingException(e);
	        }

	  }
//	    public void setDateFormat(String format) {
//	        if (format == null) {
//	              this.dateFormatter = null;
//	           // throw new IllegalArgumentException("format may not be null");
//	        } else {
//	        this.dateFormatter = new SimpleDateFormat(format);
//	        }
//	    }

	    private void writeCrossTabCsv() throws SQLException {
	        crossTabResultSet();
	        for (final Object columnLabel : getUniqueColumnLabels().values()) {
	            getSortedColumnLabels().put(columnLabel, columnLabel);
	        }

	        populateRowSortOrder();

	        writeHeadingRowCsv();
	        for (final Map.Entry<String, ArrayList<String>> entry : getRowLeadingColumns().entrySet()) {
	            final StringBuilder sb = new StringBuilder();

	            for (final String keyCol : entry.getValue()) {
	                sb.append("\"");
	                sb.append(keyCol);
	                sb.append("\",");
	            }
	            final HashMap<Object, Object> dataMap = getRowDataOrder().get(entry.getKey());
	            int i = 1;
	            for (final Object columnValue : getSortedColumnLabels().values()) {
	                final Object cellValue = dataMap.get(columnValue);
	                if (cellValue != null) {
	                    sb.append(cellValue.toString());
	                }
	                if (i++ < getSortedColumnLabels().size() - 1) {
	                    sb.append(",");
	                }
	            }

	            sb.append("\n");
	            final String line = sb.toString();
	            buff.append(line);

	        }

	    }

	    // todo use string escaper
	    private void writeHeadingRowCsv() {

	        final StringBuilder sb = new StringBuilder();

	        for (final String columnHeader : getCrossTabRows()) {
	            sb.append("\"");
	            sb.append(columnHeader);
	            sb.append("\",");
	        }
	        int i = 1;
	        for (final Object o : getSortedColumnLabels().values()) {
	            sb.append("\"");
	            if (o instanceof java.sql.Date && getDateFormatter() != null) {
	                final java.sql.Date d = (java.sql.Date) o;
	                sb.append(getDateFormatter().format(d));
	            }
	            else {
	            sb.append(o.toString());
	            }
	            sb.append("\"");
	            if (i++ < getSortedColumnLabels().size()) {
	                sb.append(",");
	            }
	        }

	        sb.append("\n");
	        final String output = sb.toString();
	        buff.append(output);
	    }
	}