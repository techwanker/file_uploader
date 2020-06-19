package org.javautil.dex.renderer.interfaces;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author jim
 *
 */
public interface  ExcelWriter  {

    @SuppressWarnings("hiding")
    public static final String revision = "$Revision: 1.1 $";

    public void process(HSSFWorkbook workBook) throws SQLException, IOException;

    public void setResultSet(ResultSet rset) throws SQLException;

    public void setWorksheetName(String worksheetName);

}