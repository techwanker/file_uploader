package org.javautil.dex.renderer;


import java.io.File;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.jdbc.BindVariable;
import org.javautil.jdbc.BindVariableValue;


/**
 *
 * @author jim
 *
 */
public abstract class AbstractDexterWriter extends AbstractRenderer implements Renderer {

    public static final String                 revision      = "$Revision: 1.1 $";

    public static final String                 mod           = ".1";

    @SuppressWarnings("unused")
    private final Logger                logger        = Logger.getLogger(this.getClass());

    private PrintWriter                        servletWriter;
    // todo remove all of the state variables
    private final HashMap<String, BindVariable>      bindVariables = new HashMap<String, BindVariable>();

    private final HashMap<String, BindVariableValue> bindValues    = new HashMap<String, BindVariableValue>();

    private File                               outputFile;

  //  private WriterSet                 writers;

    protected AbstractDexterWriter(final RenderingCapability capability) {
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

//    /**
//     * @return Returns the dateFormat.
//     */
//    public String getDateFormat() {
//        return dateFormat;
//    }

    public File getOutputFile() {
        return outputFile;
    }

//    /**
//     * @return Returns the rset.
//     */
//    public ResultSet getRset() {
//        return rset;
//    }

    /**
     * @return Returns the servletWriter.
     */
    public PrintWriter getServletWriter() {
        return servletWriter;
    }

//    /**
//     * @return Returns the writers.
//     */
//    public WriterSet getWriters() {
//        return writers;
//    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see com.javautil.jdbc.CrossTabWriter#process()
//     */
//    public abstract void process(WriterSet writers) throws SQLException, IOException;

    /**
     * @param rowDataOrder
     *            The rowDataOrder to set.
     */

    /**
     * @todo should push up a level
     */
    public void setBindVariables(final HttpServletRequest request) {

        final StringBuilder sb = new StringBuilder();
        sb.append("bind variables are ");
        for (final BindVariable bf : bindVariables.values()) {
            sb.append("'" + bf.getBindName() + "'");

        }
        logger.info(sb.toString());
        final Enumeration<String> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            final String n = e.nextElement();
            final String v = request.getParameter(n);
            final String bindName = n.toUpperCase();
            final BindVariable b = bindVariables.get(bindName);
            // logger.info("setting bind variable " + n + " to " + v );
            if (b != null) {
                logger.info("setting bind variable " + n + " to " + v);
                final BindVariableValue bv = new BindVariableValue(bindName, b.getJdbcType(), v);
                bindValues.put(bindName, bv);
            } else {
                logger.info("not a bind variable " + bindName + " value " + v);
            }
        }
    }

//    /**
//     * @param dateFormat
//     *            The dateFormat to set.
//     */
//    public void setDateFormat(String dateFormat) {
//        this.dateFormat = dateFormat;
//    }

    /*
     * (non-Javadoc)
     *
     * @see com.javautil.jdbc.CrossTabWriter#setOutputFile(java.io.File)
     */
    public final void setOutputFile(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("output file may not be null");
        }
        this.outputFile = file;
    }

//    public void setResultSet(ResultSet rset) {
//        this.rset = rset;
//    }
//
//    /**
//     * @param rset
//     *            The rset to set.
//     */
//    public void setRset(ResultSet rset) {
//        this.rset = rset;
//    }

    /**
     * @param servletWriter
     *            The servletWriter to set.
     */
    public void setServletWriter(final PrintWriter servletWriter) {
        this.servletWriter = servletWriter;
    }

    /**
     * @param writers
     *            The writers to set.
     */
//    public void setWriters(WriterSet writers) {
//        this.writers = writers;
//    }
//
//    protected void flush() throws IOException {
//    	writers.flush();
////        for (WriterWrapper writer : writers) {
////            writer.flush();
////        }
//    }

    protected final String getString(final ResultSet rset, final String columnName) throws SQLException {
        String returnValue = null;
        try {
            returnValue = rset.getString(columnName);
        } catch (final SQLException e) {
            logger.fatal("while getting columnName " + columnName + " " + e.getMessage());
            throw e;
        }
        return returnValue;
    }
//    protected void write(String text) throws IOException {
//    	writers.write(text);
////        for (WriterWrapper writer : writers) {
////            writer.write(text);
////        }
//    }
//


}
