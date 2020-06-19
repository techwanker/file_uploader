package org.javautil.dex.renderer;

import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.javautil.dex.excel.ExcelHelper;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;
import org.javautil.jdbc.SqlType;

/**
 *
 */
public class ExcelListWriter extends AbstractRenderer implements Renderer {

	@SuppressWarnings("hiding")
	public static final String revision = "$Revision: 1.1 $";

	// private ResultSet rset;

	private HSSFWorkbook workbook = new HSSFWorkbook();

	private HSSFSheet sheet;

	private short rowNum;

	private short colNum;

	private HSSFRow row;

	private String worksheetName;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private long rowCount;

	private final HSSFCellStyle dateStyle;

	private final HSSFCellStyle numberAlignRight;

	private ExcelHelper helper;
	private static RenderingCapability capability = new RenderingCapability(
			MimeType.EXCEL, false);

	public ExcelListWriter() {
		super(capability);
		dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		numberAlignRight = workbook.createCellStyle();
		// numberAlignRight.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
		numberAlignRight.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		numberAlignRight.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.jdbc.CrossTabWriter#process()
	 */
	@Override
	public void process() throws RenderingException {

		try {
			workbook = getState().getWorkbook();

			if (worksheetName == null) {
				final int sheetCount = workbook.getNumberOfSheets();
				worksheetName = "Sheet" + (sheetCount + 1);
			}
			helper = new ExcelHelper(workbook);
			this.workbook = getState().getWorkbook();

			final long startTime = System.nanoTime();
			logger.debug("creating sheet '" + worksheetName + "' startTime "
					+ startTime + " with " + this.getClass().getName() + " "
					+ revision);
			sheet = workbook.createSheet(worksheetName);
			final ResultSetMetaData meta = getRset().getMetaData();
			final int columnCount = getRset().getMetaData().getColumnCount();
			row = sheet.createRow(rowNum++);
			final String invalidTypes = getInvalidColumnTypesMessage(meta);
			if (invalidTypes != null) {
				throw new UnsupportedOperationException(invalidTypes);
			}
			for (short i = 1; i <= columnCount; i++) {
				setColumnWidth(meta, i);
				final HSSFCell cell = row.createCell(colNum++);
				cell.setCellValue(meta.getColumnName(i));
			}
			while (getRset().next()) {
				rowCount++;
				row = sheet.createRow(rowNum++);

				for (int i = 1; i <= columnCount; i++) {
					final int colNum = i;
					Object cellValue = null;
					switch (meta.getColumnType(colNum)) {
					case Types.DATE:
						cellValue = getRset().getDate(colNum);
						break;
					case Types.TIME:
						cellValue = getRset().getTime(colNum);
						break;
					case Types.TIMESTAMP:
						cellValue = getRset().getTimestamp(colNum);
						break;
					case Types.BIGINT:
					case Types.DECIMAL:
					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.INTEGER:
					case Types.NUMERIC:
					case Types.SMALLINT:
					case Types.TINYINT:
						final double d = getRset().getDouble(colNum);
						cellValue = getRset().wasNull() ? null : new Double(d);
						break;
					case Types.CLOB:
						final Clob c = getRset().getClob(colNum);
						if (!getRset().wasNull()) {
							final Reader r = c.getCharacterStream();
							final StringBuilder b = new StringBuilder();
							int ch;
							while ((ch = r.read()) != -1) {
								b.append((char) ch);
							}
							r.close();
							cellValue = b.toString();
						}
						break;
					case Types.CHAR:
					case Types.VARCHAR:
						cellValue = getRset().getString(colNum);
						break;
					}

					final HSSFCell cell = row.createCell((short) (colNum - 1));
					if (cellValue != null) {
						helper.setCell(cell, cellValue);
					}
					// colNum++;
				}
			}
			final long endTime = System.nanoTime();
			final double elapsedSeconds = (endTime - startTime) / 1e9;
			logger.debug("sheet " + worksheetName + " created with " + rowCount
					+ " rows in " + elapsedSeconds + " seconds");
		} catch (final Exception e) {
			throw new RenderingException(e);
		}
	}

	private void setColumnWidth(final ResultSetMetaData meta,
			final int columnNumber) throws SQLException {

		int dataWidth = meta.getColumnDisplaySize(columnNumber);
		switch (meta.getColumnType(columnNumber)) {
		case Types.DATE:
			dataWidth = 10;
			break;
		case Types.NUMERIC:
			dataWidth = meta.getPrecision(columnNumber) + 1;
		}
		final int titleWidth = meta.getColumnName(columnNumber).length();

		final int charWidth = dataWidth > titleWidth ? dataWidth : titleWidth;
		// int unitsPerChar = 256;
		final int unitsPerChar = 300;

		// logger.debug(message);
		final int funkyWidth = charWidth * unitsPerChar;

		final short shortFunkyWidth = funkyWidth < Short.MAX_VALUE ? (short) funkyWidth
				: Short.MAX_VALUE;
		// String message = "setting width for " + columnNumber + " to " +
		// charWidth + " " + funkyWidth + " " + shortFunkyWidth;
		// System.out.println(message);
		sheet.setColumnWidth((short) (columnNumber - 1), shortFunkyWidth);
	}

	public void setWorksheetName(final String worksheetName) {
		this.worksheetName = worksheetName;

	}

	private String getInvalidColumnTypesMessage(final ResultSetMetaData meta)
			throws SQLException {
		final ArrayList<String> cols = getInvalidColumnTypes(meta);
		String retval = null;
		if (cols != null) {
			final StringBuilder b = new StringBuilder();
			for (final String col : cols) {
				b.append(col);
				b.append(newline);
			}
			retval = b.toString();
		}
		return retval;
	}

	private ArrayList<String> getInvalidColumnTypes(final ResultSetMetaData meta)
			throws SQLException {
		if (meta == null) {
			throw new IllegalArgumentException("meta is null");
		}
		final ArrayList<Integer> unsupportedColumnNumbers = new ArrayList<Integer>();
		final int columnCount = meta.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			final int type = meta.getColumnType(i);
			switch (type) {

			case Types.BIGINT:
			case Types.CHAR:
			case Types.CLOB:
			case Types.DATE:
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.INTEGER:
			case Types.LONGVARCHAR:
			case Types.NUMERIC:
			case Types.REAL:
			case Types.SMALLINT:
			case Types.TIME:
			case Types.TIMESTAMP:
			case Types.TINYINT:
			case Types.VARCHAR:
				break;
			default:
				unsupportedColumnNumbers.add(i);
			}

		}
		ArrayList<String> unsupportedColumns = null;
		if (unsupportedColumnNumbers.size() > 0) {
			unsupportedColumns = new ArrayList<String>();
			for (final Integer cn : unsupportedColumnNumbers) {
				final String message = "# " + cn + " " + meta.getColumnName(cn)
						+ " type "
						+ SqlType.getInstance(meta.getColumnType(cn));
				unsupportedColumns.add(message);
			}
		}
		return unsupportedColumns;
	}
}
