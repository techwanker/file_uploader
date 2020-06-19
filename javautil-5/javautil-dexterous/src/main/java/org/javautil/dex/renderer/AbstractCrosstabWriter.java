package org.javautil.dex.renderer;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.javautil.dex.Crosstabber;
import org.javautil.dex.dexterous.jdbc.ResultSetHelper;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.io.WriterSet;
import org.javautil.jdbc.BindVariable;
import org.javautil.jdbc.BindVariableValue;


/**

*  // todo get all of the values from the state
*
*/
public abstract class AbstractCrosstabWriter extends AbstractRenderer implements Crosstabber, Renderer
{

    public static final String                                  revision           = "$Revision: 1.1 $";



    @SuppressWarnings("unused")
    private static final Logger                                 logger             = Logger.getLogger(AbstractCrosstabWriter.class.getName());



    private ResultSet                                           rset;

    private ArrayList<String>                                   crossTabColumns    = null;

    /**
     * The names of the columns from the resultset that define the rows.
     */
    private ArrayList<String>                                   rowKeyColumnNames       = null;

    private ArrayList<String>                                   crossTabValues     = null;

    private Map<Object, Object>                             uniqueColumnLabels;


    private final Map<Object, Object>                             sortedColumnLabels = new TreeMap<Object, Object>();

    private WriterSet                                  writers;
    /**
     * this ArrayList key identifies the row, the HashMap object represents the values
     */

    private Map<ArrayList<String>, HashMap<Object, Object>> crossTabMap;
    private Map<String, HashMap<Object, Object>>            rowDataOrder       = new TreeMap<String, HashMap<Object, Object>>();

    /**
     * The leading columns that define the row key for the cross tab.
     */
    private TreeMap<String, ArrayList<String>>                  rowLeadingColumns  = new TreeMap<String, ArrayList<String>>();

    private final HashMap<String, BindVariable>                       bindVariables      = new HashMap<String, BindVariable>();

    //private File                                                outputFile;

    private final HashMap<String, BindVariableValue>                  bindValues         = new HashMap<String, BindVariableValue>();

    private int rowCount = -1;

    protected AbstractCrosstabWriter(final RenderingCapability capability) {
    	super(capability);
	}

	public void addBindVariable(final BindVariable var) {
        bindVariables.put(var.getBindName().toUpperCase(), var);
    }

    public void addBindVariables(final Collection<BindVariable> variables) {
        for (final BindVariable var : variables) {
            bindVariables.put(var.getBindName().toUpperCase(), var);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.javautil.jdbc.CrossTabWriter#addBindVariableValues(java.util.Collection)
     */
    public void addBindVariableValues(final Collection<BindVariableValue> values) {
        for (final BindVariableValue val : values) {
            bindValues.put(val.getVariableName().toUpperCase(), val);
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
    public Map<ArrayList<String>, HashMap<Object, Object>> getCrossTabMap() {
        return crossTabMap;
    }



    /**
     * @return Returns the crossTabRows.
     */
    public ArrayList<String> getCrossTabRows() {
        return rowKeyColumnNames;
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
    public Map<String, HashMap<Object, Object>> getRowDataOrder() {
        return rowDataOrder;
    }


    /**
     * @return Returns the sortedColumnLabels.
     */
    public Map<Object, Object> getSortedColumnLabels() {
        return sortedColumnLabels;
    }

    /**
     * @return Returns the uniqueColumnLabels.
     */
    public Map<Object, Object> getUniqueColumnLabels() {
        return uniqueColumnLabels;
    }

    /**
     * @return Returns the writer.
     */
    public WriterSet getWriters() {
        return writers;
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see com.javautil.jdbc.CrossTabWriter#process()
//     */
//  //  public abstract void process(WriterSet writer) throws SQLException, IOException;

    @SuppressWarnings("unchecked")
	public void setBindVariables(final HttpServletRequest request) {

        final StringBuilder sb = new StringBuilder();
        sb.append("bind variables are ");
        for (final BindVariable bf : bindVariables.values()) {
            sb.append("'" + bf.getBindName() + "'");

        }
        logger.debug(sb.toString());
        final Enumeration<String> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            final String n = e.nextElement();
            final String v = request.getParameter(n);
            final String bindName = n.toUpperCase();
            final BindVariable b = bindVariables.get(bindName);
            if (b != null) {
                final BindVariableValue bv = new BindVariableValue(bindName, b.getJdbcType(), v);
                bindValues.put(bindName, bv);
            } else {
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.javautil.jdbc.CrossTabWriter#setCrosstabColumns(java.util.Collection)
     */
    public void setCrosstabColumnColumnNames(final Collection<String> columnNames) {
        if (columnNames != null) {

                crossTabColumns = new ArrayList<String>();

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
    public void setCrosstabColumnColumnNames(final ArrayList<String> crossTabColumns) {
        this.crossTabColumns = crossTabColumns;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.javautil.jdbc.CrossTabWriter#setCrosstabRows(java.util.Collection)
     */
    public void setCrosstabRowKeyColumnNames(final Collection<String> names) {
        if (names != null) {
            if (rowKeyColumnNames == null) {
                rowKeyColumnNames = new ArrayList<String>();
            }
            rowKeyColumnNames.addAll(names);
        } else {
            logger.warn("names is null");
            rowKeyColumnNames = null;
        }
    }

    /**
     * @param crossTabRows
     *            The crossTabRows to set.
     */
    public void setCrosstabRowKeyColumnNames(final ArrayList<String> crossTabRows) {
        this.rowKeyColumnNames = crossTabRows;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.javautil.jdbc.CrossTabWriter#setCrosstabValues(java.util.Collection)
     */
    public void setCrosstabCellColumnNames(final Collection<String> values) {
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
    public void setCrosstabCellColumnNames(final ArrayList<String> crossTabValues) {
        this.crossTabValues = crossTabValues;
    }




    /**
     * @param rowDataOrder
     *            The rowDataOrder to set.
     */
    public void setRowDataOrder(final HashMap<String, HashMap<Object, Object>> rowDataOrder) {
        this.rowDataOrder = rowDataOrder;
    }



    protected final void checkCrossTab() {
        if (crossTabColumns != null || rowKeyColumnNames != null || crossTabValues != null) {
            if (crossTabColumns == null || rowKeyColumnNames == null || crossTabValues == null) {
                final StringBuilder m = new StringBuilder();
                m.append("crossTabColumns");
                m.append(crossTabColumns == null ? "is null" : "specified");

                m.append("crossTabRows ");
                m.append(rowKeyColumnNames == null ? " is null" : " specified");
                m.append("crossTabValues ");
                m.append(crossTabValues == null ? " is null" : " specified");

                throw new IllegalArgumentException("inconsistent crosstab state " + m.toString());
            }

        }
    }

    protected final void crossTabResultSet() throws SQLException {
        logger.debug("cross tabbing begins");
        uniqueColumnLabels = new HashMap<Object, Object>();

        crossTabMap = new HashMap<ArrayList<String>, HashMap<Object, Object>>();

        final ResultSetHelper rsh = new ResultSetHelper(rset);
        rowCount = 0;
        // process each row of the resultset
        while (rset.next()) {
            rowCount++;
            final ArrayList<String> rowKeys = new ArrayList<String>();

            for (final String colName : rowKeyColumnNames) {

            	try {
            		final String colVal = rsh.getString(colName);
            		rowKeys.add(colVal);
            	} catch (final SQLException sqe) {
            		throw new SQLException("invalid crosstab row column while getting column + '" + colName + "' " + sqe.getMessage());
            	}

            }
            rowKeys.trimToSize();
            final Object obj = rsh.getObject(crossTabColumns.get(0));
            Object cachedObject = uniqueColumnLabels.get(obj);
            if (cachedObject == null) {
                uniqueColumnLabels.put(obj, obj);
                cachedObject = obj;
            }

            HashMap<Object, Object> columnValues = crossTabMap.get(rowKeys);
            if (columnValues == null) {
                columnValues = new HashMap<Object, Object>();
                crossTabMap.put(rowKeys, columnValues);
            }

            final Object value = rsh.getObject(crossTabValues.get(0));
            columnValues.put(cachedObject, value);
        }

        logger.debug("cross tabbing ends Dates fetched " + rsh.getDatesFetchedCount() + " datePoolSize " + rsh.getDatePoolSize());
    }




    protected final TreeSet<Object> getCrossTabColumnValues() {
        final TreeSet<Object> returnValue = new TreeSet<Object>();
        returnValue.addAll(uniqueColumnLabels.values());
        return returnValue;
    }


	public ArrayList<Object> getHeadingRow() {
		final ArrayList<Object> cols = new ArrayList<Object>();
		for (final String columnHeader : getCrossTabRows()) {
			cols.add(columnHeader);
		}

		for (final Object o : getSortedColumnLabels().values()) {
			cols.add(o);
		}
		return cols;
	}

	public int getColumnCount() {
		return getHeadingRow().size();
	}
	// todo create ResultSet crosstabber and remove from this class
	public Object[][] getMatrix() {
		final ArrayList<Object> heading = getHeadingRow();
		final int columnCount = heading.size();
		final int dataRowCount = getRowDataOrder().size();
		final Object[][] matrix = new Object[dataRowCount + 1][];
		matrix[0] = getHeadingRow().toArray();
		int rowIndex = 1;
		for (final Map.Entry<String, ArrayList<String>> entry : getRowLeadingColumns()
				.entrySet()) {
			final Object[] row = matrix[rowIndex] = new Object[columnCount];
			int colIndex = 0;
			for (final String keyCol : entry.getValue()) {
				row[colIndex++] = keyCol;
			}
			final HashMap<Object, Object> dataMap = getRowDataOrder().get(
					entry.getKey());
			for (final Object columnValue : getSortedColumnLabels().values()) {
				row[colIndex++] = columnValue;
			}
			rowIndex++;
		}
		return matrix;
	}

	int getDisplayLength(final Object o) {
		// todo complete
		return 10;
	}

	public int[] getDisplayWidths(final Object[][] rows) {
		int [] retval = new int[0];
		int maxColumns = 0;
		for (int i = 0; i < rows.length; i++) {

			if (rows[i].length > maxColumns) {
				maxColumns = rows[i].length;
			}
		}
		retval = new int[maxColumns];
		for (int i = 0; i < rows.length; i++) {
			final Object [] row = rows[i];
			for (int j = 0; j < row.length; j++) {
			     final Object o = row[j];
			     final int displayLength = getDisplayLength(o);
			     if (displayLength > retval[j]) {
			    	 retval[j] = displayLength;
			     }
			}
		}
		return retval;
	}
    /**
     * @return Returns the rowLeadingColumns.
     */
    protected TreeMap<String, ArrayList<String>> getRowLeadingColumns() {
        return rowLeadingColumns;
    }

    protected final void populateRowSortOrder() {
        final char low = 0;
        for (final Map.Entry<ArrayList<String>, HashMap<Object, Object>> entry : crossTabMap.entrySet()) {
            final ArrayList<String> key = entry.getKey();
            final HashMap<Object, Object> values = entry.getValue();
            final StringBuilder b = new StringBuilder();
            for (final String kv : key) {
                b.append(kv);
                b.append(low);
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

   protected void flush() throws IOException {
    	writers.flush();
    }


}
