package org.javautil.dex.renderer;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.javautil.dex.dexterous.jdbc.ResultSetHelper;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.document.MimeType;
import org.javautil.io.WriterSet;
import org.javautil.jdbc.BindVariable;



/**
 * @todo reuse the objects from the heading to save space
 * @todo create a string pool set crosstab column columName set crosstab row columnName1,columnName2 set crosstab value columnName
 * @author jim
 *
 * // todo use resultset crosstabber
 *
 */
public abstract class AbstractXmlCrosstabWriter extends AbstractRenderer implements  Renderer {

    @SuppressWarnings("hiding")
    public static final String                                  revision           = "$Revision: 1.1 $";

    @SuppressWarnings("unused")
    private static final Logger                                 logger             = Logger.getLogger(AbstractXmlCrosstabWriter.class
                                                                                           );

    private final String dateFormat = "yyyy-MM-dd";


    private ResultSet                                           rset;

    private ArrayList<String>                                   crossTabColumns    = null;

    private ArrayList<String>                                   crossTabRows       = null;

    private ArrayList<String>                                   crossTabValues     = null;

    private TreeMap<Object, Object>                             uniqueColumnLabels;

    private final TreeMap<Object, Object>                             sortedColumnLabels = new TreeMap<Object, Object>();

    private PrintWriter                                         servletWriter;

    private WriterSet writers;
    /**
     * this ArrayList key identifies the row, the TreeMap object represents the values
     */
    private Map<ArrayList<String>, TreeMap<Object, String>> crossTabMap;

    private Map<String, TreeMap<Object, String>>            rowDataOrder       = new TreeMap<String, TreeMap<Object, String>>();

    private TreeMap<String, ArrayList<String>>                  rowLeadingColumns  = new TreeMap<String, ArrayList<String>>();


    private final TreeMap<String, BindVariable>                       bindVariables      = new TreeMap<String, BindVariable>();

	private int rowsProcessed;

	private static final RenderingCapability capability = new RenderingCapability(MimeType.XML,true);

	protected  AbstractXmlCrosstabWriter () {
		super(capability);
	}

    public void addBindVariables(final Collection<BindVariable> variables) {
        for (final BindVariable var : variables) {
            bindVariables.put(var.getBindName().toUpperCase(), var);
        }
    }

    /**
     * @return Returns the crossTabColumns.
     */
    public ArrayList<String> getCrossTabColumns() {
        return crossTabColumns;
    }

    /**
     * @return Returns the crossTabMap.
     */
    public Map<ArrayList<String>, TreeMap<Object, String>> getCrossTabMap() {
        return crossTabMap;
    }

    /**
     * @return Returns the crossTabRows.
     */
    public ArrayList<String> getCrossTabRows() {
        return crossTabRows;
    }

    /**
     * @return Returns the crossTabValues.
     */
    public ArrayList<String> getCrossTabValues() {
        return crossTabValues;
    }

    /**
     * @return Returns the rowDataOrder.
     */
    public Map<String, TreeMap<Object, String>> getRowDataOrder() {
        return rowDataOrder;
    }

//    public long getRowsProcessed() {
//    	return rowsProcessed;
//    }

    /**
     * @return Returns the servletWriter.
     */
    public PrintWriter getServletWriter() {
        return servletWriter;
    }

    /**
     * @return Returns the sortedColumnLabels.
     */
    public TreeMap<Object, Object> getSortedColumnLabels() {
        return sortedColumnLabels;
    }



    /**
     * @return Returns the uniqueColumnLabels.
     */
    public TreeMap<Object, Object> getUniqueColumnLabels() {
        return uniqueColumnLabels;
    }

    /**
     * @return Returns the writers.
     */
    public WriterSet getWriters() {
        return writers;
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see org.javautil.jdbc.CrossTabWriter#process()
//     */
//    public abstract void process(WriterSet writers) throws SQLException, IOException;

    /*
     * (non-Javadoc)
     *
     * @see org.javautil.jdbc.CrossTabWriter#setCrosstabColumns(java.util.Collection)
     */
    public void setCrosstabColumns(final Collection<String> columnNames) {
        if (columnNames != null) {
            if (crossTabColumns == null) {
                crossTabColumns = new ArrayList<String>();
            }
            crossTabColumns.addAll(columnNames);
        } else {
            logger.warn("columnNames is null");
            crossTabColumns = null;
        }
    }

    /**
     * @param crossTabColumns
     *            The crossTabColumns to set.
     */
    public void setCrosstabColumns(final ArrayList<String> crossTabColumns) {
        this.crossTabColumns = crossTabColumns;
    }

    /**
     * @param crossTabMap
     *            The crossTabMap to set.
     */
    public void setCrossTabMap(final TreeMap<ArrayList<String>, TreeMap<Object, String>> crossTabMap) {
        this.crossTabMap = crossTabMap;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.javautil.jdbc.CrossTabWriter#setCrosstabRows(java.util.Collection)
     */
    public void setCrosstabRows(final Collection<String> names) {
        if (names != null) {
            if (crossTabRows == null) {
                crossTabRows = new ArrayList<String>();
            }
            crossTabRows.addAll(names);
        } else {
            logger.warn("names is null");
            crossTabRows = null;
        }
    }

    /**
     * @param crossTabRows
     *            The crossTabRows to set.
     */
    public void setCrossTabRows(final ArrayList<String> crossTabRows) {
        this.crossTabRows = crossTabRows;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.javautil.jdbc.CrossTabWriter#setCrosstabValues(java.util.Collection)
     */
    public void setCrosstabValues(final Collection<String> values) {
        if (values != null) {
            if (crossTabValues == null) {
                crossTabValues = new ArrayList<String>();
            }
            crossTabValues.addAll(values);
        } else {
            logger.warn("values is null");
            crossTabValues = null;
        }
    }

    /**
     * @param crossTabValues
     *            The crossTabValues to set.
     */
    public void setCrossTabValues(final ArrayList<String> crossTabValues) {
        this.crossTabValues = crossTabValues;
    }



//    @Override
//	public void setDateFormat(String format) {
//        dateFormat = format;
//
//
//    }
//
//    @Override
//	public void setResultSet(ResultSet rset) {
//       this.rset = rset;
//    }

    /**
     * @param rowDataOrder
     *            The rowDataOrder to set.
     */
    public void setRowDataOrder(final TreeMap<String, TreeMap<Object, String>> rowDataOrder) {
        this.rowDataOrder = rowDataOrder;
    }
//
//    /**
//     * @param writers The writers to set.
//     */
//    @Override
//	public void setWriters(WriterSet writers) {
//        this.writers = writers;
//    }
//
//
    /**
     * @param servletWriter
     *            The servletWriter to set.
     */

    protected final void checkCrossTab() {
        if (crossTabColumns != null || crossTabRows != null || crossTabValues != null) {
            if (crossTabColumns == null || crossTabRows == null || crossTabValues == null) {
                final StringBuilder m = new StringBuilder();
                m.append("crossTabColumns");
                m.append(crossTabColumns == null ? "is null" : "specified");

                m.append("crossTabRows ");
                m.append(crossTabRows == null ? " is null" : " specified");
                m.append("crossTabValues ");
                m.append(crossTabValues == null ? " is null" : " specified");

                throw new IllegalArgumentException("inconsistent crosstab state " + m.toString());
            }

        }
    }

    protected final void crossTabResultSet() throws SQLException {
        logger.info("cross tabbing begins");
        uniqueColumnLabels = new TreeMap<Object, Object>();

        crossTabMap = new TreeMap<ArrayList<String>, TreeMap<Object, String>>();
        final ResultSetHelper rsetHelper = new ResultSetHelper(rset);
        rsetHelper.setDateFormat(dateFormat);
        int rowCount = 0;
        while (rset.next()) {
        	rowsProcessed++;
            if (rowCount++ % 100 == 0) {
                 logger.info("read " + rowCount + " rows ");
            }
            final ArrayList<String> rowKeys = new ArrayList<String>();
            for (final String colName : crossTabRows) {
                rowKeys.add(rsetHelper.getString(colName));
            }
            rowKeys.trimToSize();
            final Object obj = rset.getObject(crossTabColumns.get(0));
            Object cachedObject = uniqueColumnLabels.get(obj);
            if (cachedObject == null) {
                uniqueColumnLabels.put(obj, obj);
                cachedObject = obj;
            }

            TreeMap<Object, String> columnValues = crossTabMap.get(rowKeys);
            if (columnValues == null) {
                columnValues = new TreeMap<Object, String>();
                crossTabMap.put(rowKeys, columnValues);
            }

            final String value = rset.getString(crossTabValues.get(0));
            columnValues.put(cachedObject, value);
        }
        logger.info("cross tabbing ends");
    }
    /**
     * should encapsulate for encoding by creating a document
     */

    protected final TreeSet<Object> getCrossTabColumnValues() {
        final TreeSet<Object> returnValue = new TreeSet<Object>();
        returnValue.addAll(uniqueColumnLabels.values());
        return returnValue;
    }

    /**
     * @return Returns the rowLeadingColumns.
     */
    protected TreeMap<String, ArrayList<String>> getRowLeadingColumns() {
        return rowLeadingColumns;
    }

    protected final void populateRowSortOrder() {
        for (final Map.Entry<ArrayList<String>, TreeMap<Object, String>> entry : crossTabMap.entrySet()) {
            final ArrayList<String> key = entry.getKey();
            final TreeMap<Object, String> values = entry.getValue();
            final StringBuilder b = new StringBuilder();
            for (final String kv : key) {
                b.append(kv);
            }
            final String rowKey = b.toString();
            rowDataOrder.put(rowKey, values);
            rowLeadingColumns.put(rowKey, key);
        }
    }

    /**
     * @param rowLeadingColumns
     *            The rowLeadingColumns to set.
     */
    protected void setRowLeadingColumns(final TreeMap<String, ArrayList<String>> rowLeadingColumns) {
        this.rowLeadingColumns = rowLeadingColumns;
    }

//    protected void writeElement(Element el) throws IOException {
//        StringWriter sw = new StringWriter();
//        OutputFormat outformat = OutputFormat.createPrettyPrint();
//        XMLWriter xw = new XMLWriter(sw, outformat);
//
//        xw.write(el);
//        //System.out.println(sw.toString());
//        for (WriterWrapper writer : writers) {
//            if (writer.isPretty()) {
//            xw = new XMLWriter(writer.getWriter(),outformat);
//            } else {
//                xw = new XMLWriter(writer.getWriter());
//            }
//            xw.write(el);
//        }
//    }
}
