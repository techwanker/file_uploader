package org.javautil.dex.renderer.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.dom4j.Element;

/**
 * @todo reuse the objects from the heading to save space
 * @todo create a string pool set crosstab column columName set crosstab row columnName1,columnName2 set crosstab value columnName
 * @author jim
 *
 */
public interface  DexterXmlWriter  {

    @SuppressWarnings("hiding")
    public static final String revision = "$Revision: 1.1 $";

    public long getRowsProcessed();

    public abstract Element process() throws RenderingException;

    public void setDateFormat (String format);

    public void setResultSet(ResultSet rset) throws SQLException;
}