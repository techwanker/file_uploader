package org.javautil.dex.renderer.interfaces;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface WorkbookCrosstabWriter {



	public abstract void process(HSSFWorkbook workbook) throws SQLException, IOException;

	/** @todo obviate */
	public abstract void setDateFormat(String format);

	public abstract void setWorksheetName(String worksheetName);

//	public abstract void setBindVariableValues(Collection<BindVariableValue> bindValues);

	public abstract void setResultSet(ResultSet rset);
	public abstract void setCrosstabColumns(Collection<String>crossTabColumns);
	public abstract void setCrosstabRows(Collection<String>crossTabRows);
	public abstract void setCrosstabValues(Collection<String>crossTabValues);
	public abstract int getRowCount();

}