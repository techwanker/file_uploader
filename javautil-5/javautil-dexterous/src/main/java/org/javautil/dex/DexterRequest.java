package org.javautil.dex;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.javautil.datasources.Datasources;
import org.javautil.dex.dexterous.Dexterous;
import org.javautil.dex.dexterous.StringStores;
import org.javautil.document.MimeType;
import org.javautil.jdbc.BindVariable;
import org.javautil.jdbc.BindVariableValue;

import com.mchange.v2.c3p0.DataSources;


/**
 *
 * @author jim
 * todo this needs to go away
 */
public class DexterRequest implements Request {

    public static final String                 revision        = "$Revision: 1.1 $";

    @SuppressWarnings("unused")
    private static final Logger                logger          = Logger.getLogger(DexterRequest.class.getName());

    private String resultSetId;

 //   private SelectHelper                       selectHelper;

    private ArrayList<String>                  breaks          = null;

    private String                             htmlElementName = null;

    private MimeType                           mimeType        = MimeType.CSV;

    //private String                             dateFormat;

    private long                              allowableQueryMilliseconds;

    private String                             worksheetName;

    private String                             sqlText;

    private final HashMap<String, BindVariable>      bindVariables   = new HashMap<String, BindVariable>();

    private final HashMap<String, BindVariableValue> bindValues      = new HashMap<String, BindVariableValue>();

    private HSSFWorkbook                       workbook;

    private StringStores                       stringStores;

    private StringStores                       lastStringStores;

    private SimpleDateFormat                   dateFormat;

    private String dataSourceName;

    private CrosstabControl crosstabControl;

	private String traceId;

	private String dateFormatString;

	private String	pumpIntoDataSourceName;

	private String	pumpFromDataSourceName;

	private String insertText;

	//private DataSources dataSources = null;

	private Dexterous dexterous;

   // private CursorBreakScripts breakScripts;

    public void addBindVariable(final BindVariable var) {
        bindVariables.put(var.getBindName().toUpperCase(), var);
    }

    public void addBindVariables(final Collection<BindVariable> variables) {
        for (final BindVariable var : variables) {
            bindVariables.put(var.getBindName().toUpperCase(), var);
        }
    }

    public void addBindVariableValues(final Collection<BindVariableValue> values) {
        for (final BindVariableValue val : values) {
            bindValues.put(val.getVariableName().toUpperCase(), val);
        }
    }

    public void addVariableValue(final BindVariableValue bvv) {
    	if (bvv == null) {
    		throw new IllegalArgumentException("bvv is null");
    	}
    	final String name = bvv.getVariableName();
    	if (bindValues.get(name) != null) {
    		logger.warn("variable " + name + " has already been defined");
    	}
    	bindValues.put(bvv.getVariableName(),bvv);
    	System.out.println(getBindVariableValuesAsString());

    }

    public void defineVariable(final String name, final BigDecimal value) {
		final BindVariableValue bvv = new BindVariableValue(name,value);
		addVariableValue(bvv);
	}

    public void defineVariable(final String name, final Date value) {
		final BindVariableValue bvv = new BindVariableValue(name,value);
		addVariableValue(bvv);
	}

    public void defineVariable(final String name, final String  value) {
		final BindVariableValue bvv = new BindVariableValue(name,value);
		addVariableValue(bvv);
	}

    public long getAllowableQueryMilliseconds() {
		return allowableQueryMilliseconds;
	}
    public Date getAsDate(final String text) throws ParseException {
		if (dateFormat == null) {
			logger.info("creating default date format as \"yyyy-mm-dd\"");
			dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		}
		final Date returnValue = dateFormat.parse(text);
		return returnValue;
	}

    public Collection<BindVariableValue> getBindVariableValues() {
		return bindValues.values();
	}

    public String getBindVariableValuesAsString() {
    	final StringBuilder b = new StringBuilder();
    	for (final BindVariableValue bvv : bindValues.values()) {
    		b.append("name: ");
    		b.append(bvv.getVariableName());
    		b.append(" value: ");
    		b.append(bvv.getValue());
    		b.append("\n");
    	}
    	return b.toString();
    }

    public ArrayList<String> getBreaks() {
		return breaks;
	}



    /**
	 * @return Returns the crosstabControl.
	 */
	public CrosstabControl getCrosstabControl() {
		return crosstabControl;
	}

    /**
	 * @return the dataSourceName
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

    /**
     * @return Returns the dateFormat.
     */
    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public String getDateFormatString() {
		return dateFormatString;
	}

    /**
     * @return Returns the htmlElementName.
     */
    public String getHtmlElementName() {
        return htmlElementName;
    }

    public String getInsertText() {
		return insertText;
	}



	/**
     * @return Returns the lastStringStores.
     */
    public StringStores getLastStringStores() {
        return lastStringStores;
    }

	public MimeType getMimeType() {
    	return mimeType;
    }
	public String getPumpFromDataSourceName() {
		return pumpFromDataSourceName;
	}

	public String getPumpIntoDataSourceName() {
		return this.pumpIntoDataSourceName;
	}

	public Object getResultSetId() {

		return resultSetId;
	}

	public long getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getSqlText() {
    	return sqlText;
    }

	/**
     * @return Returns the stringStores.
     */
    public StringStores getStringStores() {
        return stringStores;
    }

	/**
	 * @return the traceId
	 */
	public String getTraceId() {
		return traceId;
	}

	/**
	 * @return Returns the workbook.
	 */
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	/**
     * @return Returns the worksheetName.
     */
    public String getWorksheetName() {
        return worksheetName;
    }

	public boolean isCrosstab() {
		return crosstabControl != null;
	}

	public void process(){
//		Dexter d = new Dexter();
//		try {
//			d.setDexterRequest(this);
//			//logger.info("sqlText: " + sqlText);
//			d.setConnection(conn);
//			d.process();
//		} catch (Exception e) {
//
//			//logger.warn(e.getMessage());
//			//e.printStackTrace();
//			throw new ProcessingException(e);
//		}
	}

	/**
     * @todo move somewhere
     * @param request
     */
    public void setBindVariables(final HttpServletRequest request) {
        final StringBuilder sb = new StringBuilder();
        sb.append("bind variables are ");
        for (final BindVariable bf : bindVariables.values()) {
            sb.append("'" + bf.getBindName() + "'");

        }
        final Enumeration <String>e = request.getParameterNames();
        while (e.hasMoreElements()) {
            final String n = e.nextElement();
            final String v = request.getParameter(n);
            final String bindName = n.toUpperCase();
            final BindVariable b = bindVariables.get(bindName);
            if (b != null) {
                final BindVariableValue bv = new BindVariableValue(bindName, b.getJdbcType(), v);
                bindValues.put(bindName, bv);
            } else {
                //logger.info("not a bind variable " + bindName + " value " + v);
            }
        }
    }

	public void setBreaks(final ArrayList<String> _breaks) {
		this.breaks = _breaks;
	}

	/**
	 * For use when acting as a delegate.
	 */
	public void setConnection(final Connection conn) {

	}

	/**
	 * @param crosstabControl The crosstabControl to set.
	 */
	public void setCrosstabControl(final CrosstabControl crosstabControl) {
		this.crosstabControl = crosstabControl;
	}

	/**
	 * @param dataSourceName the dataSourceName to set
	 */
	public void setDataSourceName(final String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

//	public void setDataSources(DataSources ds) {
//		dataSources = ds;
//	}

	/**
     * @param dateFormat
     *            The dateFormat to set.
     */
    public void setDateFormat(final SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

	/**
     * @param htmlElementName
     *            The htmlElementName to set.
     */
    public void setHtmlElementName(final String htmlElementName) {
        this.htmlElementName = htmlElementName;
    }

	public void setIncludeAfter(final String string) {

	}
	public void setIncludeBefore(final String string) {

	}

	public void setInsertText(final String sqlText) {
		insertText = sqlText;
	}

    /**
     * @param lastStringStores The lastStringStores to set.
     */
    public void setLastStringStores(final StringStores lastStringStores) {
        this.lastStringStores = lastStringStores;
    }

    public void setMakePath(final boolean makePath) {

	}

	public void setMimeType(final MimeType mimeType) {
    	if (mimeType == null) {
    		throw new IllegalArgumentException("mimeType may not be null");
    	}
        this.mimeType = mimeType;
    }

	public void setPumpFromDataSourceName(final String name) {
		this.pumpFromDataSourceName = name;
	}

	public void setPumpIntoDataSourceName(final String name) {
		this.pumpIntoDataSourceName= name;
	}

	public void setResultSetId(final String value) {
		this.resultSetId = value;

	}

	public void setSpoolFileName(final String value) {

	}

	public void setSqlText(final String text) {
        this.sqlText = text;
    }

	/**
     * @param stringStores The stringStores to set.
     */
    public void setStringStores(final StringStores stringStores) {
        this.stringStores = stringStores;
    }

	/**
	 * @param traceId the traceId to set
	 */
	public void setTraceId(final String traceId) {
		this.traceId = traceId;
	}

	/**
	 * @param workbook The workbook to set.
	 */
	public void setWorkbook(final HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	/**
     * @param worksheetName The worksheetName to set.
     */
    public void setWorksheetName(final String worksheetName) {
        this.worksheetName = worksheetName;
    }


	void addBindValue(final String variableName,final BindVariableValue bindValue) {
	bindValues.put(variableName, bindValue);
	}

	/**
	 * @return the dexterous
	 */
	Dexterous getDexterous() {
		return dexterous;
	}


	
	public void setDataSources(DataSources ds) {
		// TODO Auto-generated method stub
		
	}


}
