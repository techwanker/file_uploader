package org.javautil.dex.renderer.interfaces;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.javautil.dex.dexterous.DexterousState;

public interface Renderer {

	public boolean canRender(DexterousState state);

	public void process() throws RenderingException, IOException;

	public void setState(DexterousState state);
	public void setResultSet(ResultSet rset) throws SQLException;
    public long getRowsProcessed();





}
