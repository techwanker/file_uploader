package org.javautil.dex.renderer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;
import org.javautil.jdbc.SelectHelper;

/**
 *
 *
 */
public class ExcelXmlListWriter extends AbstractRenderer {

	public static final String revision = "$Revision: 1.1 $";
	private ResultSet rset;

	private SelectHelper selectHelper;
	private String worksheetName;
	private long rowCount;
	private ExcelXmlWriter workBook;

	private static final RenderingCapability capability = new RenderingCapability(
			MimeType.EXCEL_XML, false);

	public ExcelXmlListWriter() {
		super(capability);
	}

	// /**
	// * @return the selectHelper
	// */
	// public SelectHelper getSelectHelper() {
	// return selectHelper;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.javautil.jdbc.CrossTabWriter#process()
	 */
	@Override
	public void process() throws IOException, RenderingException {

		if (getState().getWorkbook() == null) {
			throw new IllegalArgumentException("workBook is null");
		}

		if (selectHelper == null) {
			throw new IllegalStateException("selectHelper is null");
		}
		try {
			rset = selectHelper.getResultSet();
		} catch (final SQLException e) {
			throw new RenderingException(e);
		}
		if (rset == null) {
			throw new IllegalStateException(
					"selectHelper returned null ResultSet");
		}
		workBook = getState().getExcelXmlWriter();
		if (worksheetName == null) {
			final int sheetCount = workBook.getSheetCount();
			worksheetName = "Sheet" + (sheetCount + 1);
		}

		workBook.createSheet(worksheetName);
		try {
			final ResultSetMetaData meta = rset.getMetaData();
			final int columnCount = rset.getMetaData().getColumnCount();
			workBook.createRow();

			for (int i = 1; i <= columnCount; i++) {
				workBook.writeCell(meta.getColumnName(i));
			}
			while (rset.next()) {
				rowCount++;
				workBook.createRow();
				for (int i = 1; i <= columnCount; i++) {
					final Object cellValue = rset.getObject(i);
					if (cellValue != null) {
						workBook.writeCell(cellValue.toString());
					} else {
						workBook.nullCell();
					}
				}
			}
		} catch (final SQLException sqe) {
			throw new RenderingException(sqe);
		}
		workBook.closeSheet();
	}

	// TODO nuke
	// /**
	// * @param selectHelper the selectHelper to set
	// */
	// public void setSelectHelper(final SelectHelper selectHelper) {
	// this.selectHelper = selectHelper;
	// }

	public void setWorksheetName(final String worksheetName) {
		this.worksheetName = worksheetName;
	}
}
