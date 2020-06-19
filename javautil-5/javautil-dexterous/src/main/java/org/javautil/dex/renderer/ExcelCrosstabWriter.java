package org.javautil.dex.renderer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.javautil.dex.Crosstabber;
import org.javautil.dex.PoiRenderer;
import org.javautil.dex.excel.ExcelHelper;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;


/**
 * @todo reuse the objects from the heading to save space
 * @todo create a string pool set crosstab column columnName set crosstab row columnName1,columnName2 set crosstab value columnName
 * @author jim
 *
 */
public class ExcelCrosstabWriter extends AbstractCrosstabWriter implements
		Renderer,
		PoiRenderer, Crosstabber {

	@SuppressWarnings("hiding")
	public static final String revision = "$Revision: 1.1 $";

	private static final int CHARACTER_WIDTH_FACTOR = 255;

	private HSSFSheet sheet;

	private short rowNum;

	private short colNum;

	private HSSFRow row;
	private HSSFWorkbook workbook;

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	// Create a cell and put a value in it.
	// private HSSFCell cell;
	private String worksheetName;

	//    private HSSFCellStyle dateStyle;
	//    private HSSFCellStyle numberAlignRight;
	private ExcelHelper helper;

	private static RenderingCapability capability = new RenderingCapability(MimeType.EXCEL,true);
	public ExcelCrosstabWriter() {
		super(capability);
		//        dateStyle = workbook.createCellStyle();
		//        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		//        numberAlignRight = workbook.createCellStyle();
		//        //numberAlignRight.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
		//        numberAlignRight.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		//        numberAlignRight.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		//new HSSFCellStyle(HSSFDataFormat.getBuiltinFormat(""),HSSFCellStyle.ALIGN_RIGHT);
	}




	/* (non-Javadoc)
	 * @see com.javautil.dex.excel.WorkbookCrosstabWriter#process(org.apache.poi.hssf.usermodel.HSSFWorkbook)
	 */
	public void process() throws RenderingException {
		if (workbook == null) {
			throw new IllegalStateException("workBook is null");
		}
		final ExcelHelper helper = new ExcelHelper(workbook);
		if (worksheetName == null) {
			final int sheetCount = workbook.getNumberOfSheets();
			worksheetName = "Sheet" + (sheetCount + 1);
		}
		sheet = workbook.createSheet(worksheetName);

		checkCrossTab();

		try {
			setColumnWidths();
			writeCrossTab();
		} catch (final Exception e) {
			throw new RenderingException(e);
		}

	}

//
//	/* (non-Javadoc)
//	 * @see com.javautil.dex.excel.WorkbookCrosstabWriter#setDateFormat(java.lang.String)
//	 */
//	public void setDateFormat(String format) {
//		// TODO Auto-generated method stub
//
//	}

	/* (non-Javadoc)
	 * @see com.javautil.dex.excel.WorkbookCrosstabWriter#setWorksheetName(java.lang.String)
	 */
	public void setWorksheetName(final String worksheetName) {
		this.worksheetName = worksheetName;

	}

	/**
	 * should encapsulate for encoding by creating a document
	 *
	 * @throws SQLException
	 * @throws IOException
	 * @throws IOException
	 */
	private void writeCrossTab() throws SQLException, IOException {
		crossTabResultSet();
		for (final Object columnLabel : getUniqueColumnLabels().values()) {
			getSortedColumnLabels().put(columnLabel, columnLabel);
		}

		populateRowSortOrder();

		writeHeadingRow();


		for (final Map.Entry<String, ArrayList<String>> entry : getRowLeadingColumns()
				.entrySet()) {
			row = sheet.createRow(rowNum++);
			colNum = 0;
			for (final String keyCol : entry.getValue()) {
				final HSSFCell cell = row.createCell(colNum++);

				cell.setCellValue(keyCol);
				setColumnWidth(colNum++,keyCol);

			}
			final HashMap<Object, Object> dataMap = getRowDataOrder().get(
					entry.getKey());
			for (final Object columnValue : getSortedColumnLabels().values()) {
				final Object cellValue = dataMap.get(columnValue);
				final HSSFCell cell = row.createCell(colNum);
				//   logger.info("row " + rowNum + " colNum " + colNum + " " + cellValue.getClass().getName());
				if (cellValue != null) {

					helper.setCell(cell, cellValue);
				//	setColumnWidth(colNum,cellValue.toString());
				}
				// cell.setCellValue(cellValue);
				colNum++;
			}
		}
	}

	private void writeHeadingRow() throws SQLException, IOException {

		row = sheet.createRow(rowNum++);

		for (final String columnHeader : getCrossTabRows()) {

			final HSSFCell cell = row.createCell(colNum++);
			cell.setCellValue(columnHeader);
		}

		for (final Object o : getSortedColumnLabels().values()) {
			final HSSFCell cell = row.createCell(colNum++);
			helper.setCell(cell, o);
		}
	}

//	 protected void flush() throws IOException {
//	}

	short getColumnWidth(final String label, final int dataWidthCharacters) {
		final int labelWidthCharacters = label.length();
		final int longerChars = labelWidthCharacters > dataWidthCharacters ? labelWidthCharacters : dataWidthCharacters;

		final short width256thsInch = (short) (longerChars * CHARACTER_WIDTH_FACTOR);
		return width256thsInch;
	}

	void setColumnWidth(final short columnNumber, final int stringWidth) {

		final short currentWidth = sheet.getColumnWidth(columnNumber);
		final short textWidth = (short) (stringWidth * CHARACTER_WIDTH_FACTOR);
		if (textWidth > currentWidth) {
			sheet.setColumnWidth(columnNumber,textWidth);
			logger.debug("setting columnNumber " + columnNumber + " width " + textWidth);
		}
	}

	void setColumnWidth(final short columnNumber, final String text) {
		final int stringWidth = text == null ? 0 : text.length();
		final short currentWidth = sheet.getColumnWidth(columnNumber);
		final short textWidth = (short) (stringWidth * CHARACTER_WIDTH_FACTOR);
		if (textWidth > currentWidth) {
			sheet.setColumnWidth(columnNumber,textWidth);
			final String message = "setting columnNumber " + columnNumber + " width " + textWidth;
			System.out.println(message);
		}
	}

	void setColumnWidths() throws SQLException {
		final ResultSet rset = getRset();
		final ResultSetMetaData meta = rset.getMetaData();
		final TreeMap<String,Integer> columnNameMap = new TreeMap<String,Integer>();
		logger.debug("setting column widths");
		for (int i = 1; i < meta.getColumnCount(); i++) {
			final String columnName = meta.getColumnName(i);
			columnNameMap.put(columnName, i);
		}


		short currColNum = 0;
		for (final String columnHeader : getCrossTabRows()) {
			final Integer columnNumber = columnNameMap.get(columnHeader.toUpperCase());
			if (columnNumber == null) {
				final StringBuilder b = new StringBuilder();
				b.append("cannot find '" + columnHeader + "' in map");
				b.append(newline);
				for (final String key : columnNameMap.keySet()) {
					b.append(key);
					b.append(newline);
				}
				throw new IllegalArgumentException(b.toString());
			}
			final int dataChars = meta.getColumnDisplaySize(columnNumber);
			final short width  = getColumnWidth(columnHeader,dataChars);
			System.out.println("setting width of column " + currColNum + " to " + width);
			sheet.setColumnWidth(currColNum++,width);
		}

		System.out.println("sortedColumnLabels size " + getSortedColumnLabels().values().size());
		for (final Object o : getSortedColumnLabels().values()) {
			final String columnHeader = o.toString();
			setColumnWidth(currColNum, o.toString());

		}


	}


	public void setWorkbook(final HSSFWorkbook workbook) {
		if (workbook == null) {
			throw new IllegalArgumentException("workbook is null");
		}
		this.workbook = workbook;

	}


}
