package org.javautil.dex.renderer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.transform.stream.StreamResult;

import org.javautil.dex.DexterRequest;
import org.javautil.dex.dexterous.DexterousState;
import org.javautil.dex.dexterous.StringStores;
import org.javautil.dex.dexterous.jdbc.ResultSetHelper;
import org.javautil.dex.renderer.interfaces.Renderer;

public abstract class AbstractRenderer implements Renderer {
	private DexterRequest		request;

	//private WriterSet			writers;

	private SimpleDateFormat	dateFormatter	= new SimpleDateFormat("yyyy-MM-dd");

	protected int				rowsProcessed	= 0;

	private ArrayList<Object[]>	rows;

	private ResultSetHelper		resultSetHelper;

	private ResultSet			rset;

	private DexterousState state;

	private final RenderingCapability capability;

	private StreamResult streamResult;
	
	public static final String	newline			= System.getProperty("line.separator");


	public AbstractRenderer(final RenderingCapability capability) {
		if (capability == null) {
			throw new IllegalArgumentException("capability is null");
		}
		this.capability = capability;
	}

	public DexterRequest getDexterRequest() {
		return request;
	}

	public void setDexterRequest(final DexterRequest request) {
		this.request = request;
	}

//	public final void setWriters(WriterSet writers) {
//		this.writers = writers;
//	}

//	// TODO validate that this gets called by every method that extends this
//	// class and with caching on
//	public void spool(File f) throws FileNotFoundException, IOException {
//		FileOutputStream os = new FileOutputStream(f);
//		writers.addOrReplaceFileWriter(os, f, true);
//	}

	public void setRowCount(final int count) {
		this.rowsProcessed = count;
	}

	public int getRowCount() {
		return rowsProcessed;
	}

	public long getRowsProcessed() {
		return rowsProcessed;
	}

	/**
	 * @return Returns the stringStores.
	 */
	public StringStores getStringStores() {
		return getResultSetHelper().getStringStores();
	}

	// todo dupe to setRset delete
	public final void setResultSet(final ResultSet rset) throws SQLException  {
		if (rset == null) {
			throw new IllegalArgumentException("rset is null");
		}
		this.setRset(rset);
		// resultSetHelper = new ResultSetHelper(rset);
	}

	public final void setRset(final ResultSet rset) throws SQLException {
		this.rset = rset;
		resultSetHelper = new ResultSetHelper(rset);
	}

	public final ResultSet getRset() {
		return rset;
	}

	public void setRows(final ArrayList<Object[]> rows) {
		this.rows = rows;
	}

	public ArrayList<Object[]> getRows() {
		return rows;
	}

	public final void setDateFormat(final String format) {
		this.setDateFormatter(new SimpleDateFormat(format));

	}

	public final void setDateFormatter(final SimpleDateFormat dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	public final SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	public final void setResultSetHelper(final ResultSetHelper resultSetHelper) {
		this.resultSetHelper = resultSetHelper;
	}

	public final ResultSetHelper getResultSetHelper() {
		return resultSetHelper;
	}

	protected final void write(final String text) throws IOException {
		state.getWriters().write(text);
	}
	   public final  void setState(final DexterousState state) {
	    	if (state == null) {
	    		throw new IllegalArgumentException("state is null");
	    	}
	    	this.state =state;
	    }

	   public final DexterousState getState() {
		   return state;
	   }

	   public final boolean canRender(final DexterousState state) {
		   if (state == null ) {
			   throw new IllegalArgumentException("state is null");
		   }
		   if (capability == null) {
			   throw new IllegalStateException("capability is null");
		   }
		   return capability.canRender(state);
	   }

//	   public Writer getWriterSetAsWriter() {
//		   return writers;
//	   }
}
