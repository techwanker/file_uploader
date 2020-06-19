package org.javautil.javagen;

import java.sql.Types;

import org.javautil.jdbc.metadata.Column;
import org.javautil.jdbc.metadata.TableImpl;

public class ProductTableMeta extends TableImpl {

	public ProductTableMeta() {
		super.setTableName("PRODUCT");
		super.addColumn(getIdColumn());
		super.addColumn(getMfrIdColumn());
		super.addColumn(getDescrColumn());
		super.addColumn(getIntroDtColumn());
	}

	public Column getIdColumn() {
		Column col = new Column();
		col.setColumnName("PRODUCT_ID");
		col.setDataType(Types.NUMERIC);
		col.setColumnTypeName("NUMBER");

		col.setColumnSize(new Integer(9));
		return col;
	}

	public Column getMfrIdColumn() {
		Column col = new Column();
		col.setColumnName("MFR_ID");
		col.setDataType(Types.VARCHAR);
		col.setColumnSize(12);
		col.setColumnTypeName("VARCHAR(12)");
		return col;
	}

	public Column getDescrColumn() {
		Column col = new Column();
		col.setColumnName("DESCR");

		col.setDataType(Types.VARCHAR);
		col.setColumnSize(32);
		col.setColumnTypeName("VARCHAR(32)");
		return col;
	}

	public Column getIntroDtColumn() {
		Column col = new Column();
		col.setDataType(Types.DATE);
		col.setColumnName("INTRO_DT");
		col.setColumnTypeName("DATE");
		return col;
	}

	// TODO clean up
	// @Override
	// public IndexColumns getIndexInfos(Connection meta) throws SQLException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public String[] getSelectStatementLines() {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
