package org.javautil.dex.excel;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 */
public class ExcelHelper {

	private static final String types = "String, Date, Number, Clob";

	public static final String revision = "$Revision: 1.1 $";
	private final HSSFCellStyle dateStyle;
	private final HSSFCellStyle numberAlignRight;

	public ExcelHelper(final HSSFWorkbook workbook) {

		dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		numberAlignRight = workbook.createCellStyle();

		numberAlignRight.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

	}

	public void setCell(final HSSFCell cell, final Object o) throws SQLException, IOException {

		//logger.info("row " + rowNum + " colNum " + colNum + " " + o.getClass().getName());
		if (o == null) {
			throw new IllegalArgumentException("o is null");
		}
		while (true) {
			if (o instanceof String) {
				cell.setCellValue((String) o);
				break;
			}
			if (o instanceof java.sql.Timestamp) {

				cell.setCellValue((java.util.Date) o);
				cell.setCellStyle(dateStyle);
				break;
			}
			if (o instanceof java.sql.Date) {
				cell.setCellValue((java.util.Date) o);

				cell.setCellStyle(dateStyle);
				break;
			}
			if (o instanceof Number) {
				final Number n = (Number) o;
				final double d = n.doubleValue();
				cell.setCellValue(d);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(numberAlignRight);
				break;
			}
			if (o instanceof Clob) {
				final Clob c = (Clob) o;
				final Reader r = c.getCharacterStream();
				final StringBuilder b = new StringBuilder();
				int ch;
				while ((ch = r.read()) != -1) {
					b.append(ch);
				}
				final String s = b.toString();
				cell.setCellValue(s);
				break;
			}
//			if (o instanceof BigDecimal) {
//				double d = ((BigDecimal) o).doubleValue();
//				cell.setCellValue(d);
//				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//				cell.setCellStyle(numberAlignRight);
//
//				break;
//			}

			throw new IllegalStateException("unmapped data type " + o.getClass().getName() + " must be one of " + types);
		}
	}

	public static int getCharacterWidth(final Object o) {

		int width = 0;

		if (o != null) {

			while (true) {
				if (o instanceof String) {
					width = ((String) o).length();
					break;
				}
				if (o instanceof java.sql.Timestamp) {
					width = 18;
					break;
				}
				if (o instanceof java.sql.Date) {
					width = 10;
					break;
				}
				if (o instanceof Number) {
					width = ((Number) o).toString().length();
					break;
				}
				if (o instanceof Clob) {
					width = 80;
					break;
				}

				throw new IllegalStateException("unmapped data type " + o.getClass().getName());
			}
		}
		return width;

	}

}
