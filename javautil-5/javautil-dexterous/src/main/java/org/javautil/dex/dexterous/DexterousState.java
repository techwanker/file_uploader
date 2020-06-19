package org.javautil.dex.dexterous;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.javautil.dex.DexterousStateException;
import org.javautil.dex.EventHelper;
import org.javautil.dex.bsh.Cursor;
import org.javautil.dex.parser.NodeRelationship;
import org.javautil.dex.parser.Parser;
import org.javautil.dex.renderer.ExcelXmlWriter;
import org.javautil.document.MimeType;
import org.javautil.io.WriterSet;
import org.javautil.jdbc.BindVariable;
import org.javautil.jdbc.BindVariableValue;
import org.javautil.jdbc.JdbcType;
import org.javautil.jdbc.datasources.DataSources;
import org.javautil.jdbc.datasources.InvalidDataSourceException;
import org.javautil.persistence.PersistenceException;

import bsh.UtilEvalError;

public interface DexterousState {

	public static final String revision = "$Revision: 1.1 $";
	/**
	 * xml root element name
	 */
	public static final String ROOT_ELEMENT_NAME = "document";
	public static final long WRITE_ALL = Long.MAX_VALUE;
	public static final long WRITE_PROMPTS = 1;

	public abstract void addBindVariable(String variableName, BindVariable bv);

	public abstract void addBindVariable(String bindName, JdbcType jdbcType,
			String value);

	public abstract void addStringStore(String columnName);

	public abstract void clearBindVariables();

	/**
	 * @throws IOException
	 */
	public abstract void closeWriters() throws IOException;

	// @todo why am I carrying a connection around without it being a javautil
	// Connection
	public abstract Cursor createCursor(String cursorName, String mySqlText,
			String recordName) throws SQLException, UtilEvalError,
			PersistenceException;

	// /
	// / Acccessors
	// /
	/**
	 * @return the allowableQueryMilliseconds
	 */
	public abstract Integer getAllowableQueryMilliseconds();

	/**
	 * @return the baseDirectoryName
	 */
	public abstract String getBaseDirectoryName();

	/**
	 * @return the bindValues
	 */
	public abstract Map<String, BindVariableValue> getBindValues();

	// @todo switch to user the namespace
	public abstract BindVariable getBindVariable(String name);

	/**
	 * @return the breaks
	 */
	public abstract ArrayList<String> getBreaks();

	/**
	 * @return the crossTabColumns
	 */
	public abstract List<String> getCrosstabColumns();

	/**
	 * @return the crossTabRows
	 */
	public abstract List<String> getCrosstabRows();

	/**
	 * @return the crossTabValues
	 */
	public abstract List<String> getCrosstabValues();

	public abstract File getCurrentFile();

	/**
	 * @return the dataSource
	 * 
	 *         todo who uses this and why
	 */
	public abstract DataSource getDataSource();

	/**
	 * @return the dateFormat
	 */
	public abstract String getDateFormat();

	/**
	 * @return the defaultDataSourceName
	 */
	public abstract String getDefaultDataSourceName();

	// public abstract Dexter getDexter();

	/**
	 * @return the dexterLogFileName
	 */
	public abstract String getDexterLogFileName();

	/**
	 * @return the dexterLogWriter
	 */
	public abstract Writer getDexterLogWriter();

	public abstract boolean getEcho();

	public abstract EventHelper getEvents();

	public abstract SimpleDateFormat getFileDateFormatter();

	public abstract LineNumberReader getInputReader();

	public abstract File getLogDirectory();

	/**
	 * @return the mimeType
	 */
	public abstract MimeType getMimeType();

	/**
	 * @return the resultSetId
	 */
	public abstract String getResultSetId();

	public abstract String getSqlText();

	/**
	 * @return the stylesheetFileName
	 */
	public abstract String getStylesheetFileName();

	/**
	 * @return the traceFileName
	 */
	public abstract String getTraceFileName();

	/**
	 * @return the traceId
	 */
	public abstract String getTraceId();

	/**
	 * @return the traceLogger
	 */
	public abstract Logger getTraceLogger();

	/**
	 * @return the workbook
	 */
	public abstract HSSFWorkbook getWorkbook();

	/**
	 * @return the worksheetName
	 */
	public abstract String getWorksheetName();

	public abstract String getWriterDescriptions();

	public abstract WriterSet getWriters();

	/**
	 * @return the autoTrace
	 */
	public abstract boolean isAutoTrace();

	/**
	 * @return Returns the closeWriters.
	 */
	public abstract boolean isCloseWriters();

	/**
	 * @return the emitMetaData
	 */
	public abstract boolean isEmitMetaData();

	/**
	 * @return the endOfInput
	 */
	public abstract boolean isEndOfInput();

	/**
	 * @return the makePaths
	 */
	public abstract boolean isMakePaths();

	/**
	 * @return the termout
	 */
	public abstract boolean isTermout();

	public abstract boolean isUseCachedIfAvailable();

	/**
	 * @return the useCacheThisRequest
	 */
	public abstract boolean isUseCacheThisRequest();

	/**
	 * @param allowableQueryMilliseconds
	 *            the allowableQueryMilliseconds to set
	 */
	public abstract void setAllowableQueryMilliseconds(
			Integer allowableQueryMilliseconds);

	// public abstract void setAllowableQueryTimeoutMilliseconds(Long l);

	/**
	 * @param autoTrace
	 *            the autoTrace to set
	 */
	public abstract void setAutoTrace(boolean autoTrace);

	/**
	 * @param baseDirectoryName
	 *            the baseDirectoryName to set
	 */
	public abstract void setBaseDirectoryName(String baseDirectoryName);

	/**
	 * @param bindValues
	 *            the bindValues to set
	 */
	public abstract void setBindValues(
			final Map<String, BindVariableValue> bindValues);

	// @todo stop using bindValues and just use the namespace
	public abstract void setBindVariable(String variableName,
			final BindVariableValue value) throws UtilEvalError;

	public abstract void setBindVariables(HttpServletRequest request);

	/**
	 * Sets the sql bind variables from the variables in the the bsh namespace.
	 * 
	 * @todo should use a single namespace in the future.
	 * 
	 */
	// @todo check for lower case variable names and name case collision
	public abstract void setBindVariablesFromNamespace();

	public abstract void setBreaks(ArrayList<String> _breaks);

	/**
	 * @param closeWriters
	 *            The closeWriters to set.
	 */
	public abstract void setCloseWriters(boolean closeWriters);

	public abstract void setConnection(Connection conn);

	public abstract void setConsoleHandlerLogLevel(Level level);

	/**
	 * @param crossTabColumns
	 *            the crossTabColumns to set
	 */
	public abstract void setCrossTabColumns(List<String> crossTabColumns);

	/**
	 * @param name
	 *            the crossTabRows to set
	 */
	public abstract void setCrossTabRows(List<String> name);

	/**
	 * @param crossTabValues
	 *            the crossTabValues to set
	 */
	public abstract void setCrossTabValues(List<String> crossTabValues);

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public abstract void setDataSource(DataSource dataSource);

	public abstract void setDataSourceName(String dsn)
			throws InvalidDataSourceException, SQLException;

	public abstract void setDataSources(DataSources dataSources);

	/**
	 * @param dateFormat
	 *            the dateFormat to set
	 */
	public abstract void setDateFormat(String dateFormat);

	/**
	 * @param defaultDataSourceName
	 *            the defaultDataSourceName to set
	 */
	public abstract void setDefaultDataSourceName(String defaultDataSourceName);

	/**
	 * @param dexterLogFileName
	 *            the dexterLogFileName to set
	 */
	public abstract void setDexterLogFileName(String dexterLogFileName);

	/**
	 * @param dexterLogWriter
	 *            the dexterLogWriter to set
	 */
	public abstract void setDexterLogWriter(Writer dexterLogWriter);

	public abstract void setDocumentTemplate(String fileName)
			throws DocumentException;

	public abstract void setDocumentTemplateXpath(String xpath,
			NodeRelationship nodeRelationship, boolean asElement);

	public abstract void setEcho(boolean val);

	/**
	 * @param emitMetaData
	 *            the emitMetaData to set
	 */
	public abstract void setEmitMetaData(boolean emitMetaData);

	/**
	 * @param endOfInput
	 *            the endOfInput to set
	 */
	public abstract void setEndOfInput(boolean endOfInput);

	public abstract void setFileDateFormat(String formatString);

	public abstract void setInputReader(LineNumberReader r);

	public abstract void setLogDirectory(String token);

	public abstract void setLogLevel(Level level);

	/**
	 * @param makePaths
	 *            the makePaths to set
	 */
	public abstract void setMakePaths(boolean makePaths);

	public abstract void setMimeType(MimeType mimeType);

	// @todo will have to add writes to cache
	public abstract void setOutputStream(OutputStream out);

	public abstract void setParseUntilGo(boolean parseUntilGo);

	public abstract void setRequestFile(File sqlFile);

	/**
	 * @param resultSetId
	 *            the resultSetId to set
	 */
	public abstract void setResultSetId(String resultSetId);

	public abstract void setSqlIsSelect(boolean val);

	/**
	 * @param sqlText
	 *            the sqlText to set
	 */
	public abstract void setSqlText(String sqlText);

	/**
	 * @param stylesheetFileName
	 *            the stylesheetFileName to set
	 */
	public abstract void setStylesheetFileName(String stylesheetFileName);

	/**
	 * @param termout
	 *            the termout to set
	 */
	public abstract void setTermout(boolean termout);

	/**
	 * @param traceFileName
	 *            the traceFileName to set
	 */
	public abstract void setTraceFileName(String traceFileName);

	/**
	 * @param traceId
	 *            the traceId to set
	 */
	public abstract void setTraceId(String traceId);

	/**
	 * @param traceLogger
	 *            the traceLogger to set
	 */
	public abstract void setTraceLogger(Logger traceLogger);

	public abstract void setTranspose(boolean transpose);

	public abstract void setUseCachedIfAvailable(boolean useCachedIfAvailable);

	/**
	 * @param useCacheThisRequest
	 *            the useCacheThisRequest to set
	 */
	public abstract void setUseCacheThisRequest(boolean useCacheThisRequest);

	/**
	 * @param workbook
	 *            the workbook to set
	 */
	public abstract void setWorkbook(HSSFWorkbook workbook);

	/**
	 * @param worksheetName
	 *            the worksheetName to set
	 */
	public abstract void setWorksheetName(String worksheetName);

	public abstract void setWriters(WriterSet writers);

	public abstract void addEvents(Collection<Integer> events);

	public abstract boolean isConnected();

	public abstract Connection getConnection()
			throws InvalidDataSourceException, DexterousStateException;

	/**
	 * One or more request files
	 * 
	 * @return
	 */
	public abstract boolean hasRequestFiles();

	public abstract Collection<File> getRequestFiles();

	public abstract void setRequestFiles(Collection<File> inputFiles);

	public abstract void setVerifyOnly(boolean verifyOnly);

	public abstract boolean sqlIsSelect();

	public abstract SimpleDateFormat getDateFormatter();

	public abstract Element getCurrentElement();

	public abstract String getHtmlElementName();

	public abstract void setLastStringStores(StringStores stringStores);

	public abstract boolean isEmitXmlAsElement();

	// public abstract Document getXmlDocument();
	//
	// public abstract Map<String, BindVariable> getBindVariables();

	public abstract ExcelXmlWriter getXmlWorkbook();

	// private Map<String, BindVariableValue> bindValues = new TreeMap<String,
	// BindVariableValue>();
	//
	// private bindVariables = new TreeMap<String, BindVariable>();

	public abstract boolean isCrossTab();

	public abstract void incrementSqlExceptionCount();

	public abstract int getSqlExceptionCount();

	public abstract int getMaxSqlExceptionCount();

	public abstract ExcelXmlWriter getExcelXmlWriter();

	public abstract void setExcelXmlWriter(ExcelXmlWriter object);

	public abstract String getLastStringStores();

	public abstract Collection<String> getStore(String upperCase);

	public abstract long getRowCount();

	public abstract void setParser(Parser parser);

	/**
	 * returns the last sql statement if it was a select statement
	 * 
	 * @return
	 */
	public abstract String getSelectText();

	public abstract String getStateInfo();

	/**
	 * true if not processing a list of files
	 * 
	 * @return
	 */
	public abstract boolean isInteractive();

	public abstract Document getXmlDocument();

	// public abstract void write(String text) throws IOException;

}