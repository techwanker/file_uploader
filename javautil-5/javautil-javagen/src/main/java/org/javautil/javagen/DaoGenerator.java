package org.javautil.javagen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.javautil.dataset.ColumnAttributes;
import org.javautil.jdbc.JdbcJavaNameImpl;
import org.javautil.jdbc.JdbcToJavaImpl;
import org.javautil.jdbc.JdbcToJavaMapper;
import org.javautil.jdbc.metadata.Table;
import org.javautil.text.StringHelper;
import org.javautil.text.StringUtils;

public class DaoGenerator {
	Connection conn;
	private final Logger logger = Logger.getLogger(getClass());

	private final JdbcToJavaMapper mapper = new JdbcToJavaImpl();

	String tab = "      ";
	private final JdbcJavaNameImpl namer = new JdbcJavaNameImpl();

	int stmtType = 0;
	final static String TOK = "~"; // bind variable token separator

	static final int MAXPARAMETERS = 30;
	static BindVar bindVariables[] = new BindVar[MAXPARAMETERS];

	String sqlStatement = null;

	/**
	 * Instantiate an instance of JavaGen using a properties file.
	 * 
	 * The following properties are recognized<br>
	 * see DbConnector for current database properties
	 * <p>
	 * 
	 * @param propertyFileName
	 * @exception java.lang.Exception
	 */

	public DaoGenerator(Connection conn) {
		if (conn == null) {
			throw new IllegalArgumentException("conn is null");
		}

	}

	public DaoGenerator() {
	}

	public String getDao(JavaGeneratorArguments args, List<String> sqlLines,
			Collection<ColumnAttributes> columns) {
		StringBuilder sb = new StringBuilder();
		sb.append(getClassDeclaration(args, columns));
		// sb.append(getImports(columns));
		sb.append(getSelect(sqlLines));
		sb.append("\n\n");
		sb.append(getFetchStub(args));
		sb.append(getGetRowByName(args, columns));
		String retval = sb.toString();
		logger.debug(retval);
		return retval;
	}

	public String getDao(JavaGeneratorArguments args, Table table,
			Collection<ColumnAttributes> columns) {
		StringBuilder sb = new StringBuilder();
		sb.append(getClassDeclaration(args, columns));
		// sb.append(getImports(columns));
		sb.append(getSelectText(table.getTableName()));
		sb.append(getColumnsForSql(columns));
		sb.append(getFetchStub(args));
		sb.append(getGetRowByName(args, columns));
		String retval = sb.toString();
		logger.debug(retval);
		return retval;
	}

	public String getImports(Collection<ColumnAttributes> columns) {
		TreeSet<String> includes = new TreeSet<String>();
		for (ColumnAttributes column : columns) {
			String clazzName = mapper.getImportClass(column);
			if (clazzName != null) {
				includes.add(clazzName);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (String clazzName : includes) {
			sb.append("import ");
			sb.append(clazzName);
			sb.append(";\n");
		}
		return sb.toString();
	}

	public String getClassDeclaration(JavaGeneratorArguments arguments,
			Collection<ColumnAttributes> columns) {
		StringBuilder buff = new StringBuilder(1024 * 8);
		logger.info("genBasePersistence className = "
				+ arguments.getDaoImplClassName() + "\n");

		// mdata = getMetaDataTable(dbc, table);

		// packages and imports
		buff.append("package ");
		buff.append(arguments.getDaoPackageName());
		buff.append(";\n\n");
		buff.append(getImports(columns));

		buff.append("\n");
		// class declaration
		buff.append("public class " + arguments.getDaoImplClassName() + "S\n");
		buff.append("{\n");
		String retval = buff.toString();
		return retval;
	}

	// TODO allow named binds

	// public String getSelectTextAttribute(JavaGeneratorArguments args,
	// Collection<ColumnAttributes> columns) {
	// if (args == null) {
	// throw new IllegalArgumentException("args is null");
	// }
	// String retval = "    static final String selectText = "
	// + getSelectText(args, columns) + "\n";
	// }

	public String getInsertStatement(Table table) {
		StringBuilder buff = new StringBuilder();
		buff.append("\"insert into " + table.getTableName()
				+ " \"+\n        \"(\\n\"+\n");

		boolean needsComma = false;
		for (ColumnAttributes column : table.getColumns()) {
			if (needsComma) {
				buff.append(",\\n\" +\n");
			}
			buff.append("             \"");
			buff.append(column.getColumnName().toLowerCase());
			// buff.append(" \\n\"+\n");
			needsComma = true;
		}
		buff.append("\\n\" +\n"); // close off last select column

		buff.append("        \")\\n\"+\n");

		buff.append("        \"values (");
		needsComma = false;
		int columnCount = table.getColumns().size();
		for (int c = 0; c < columnCount; c++) {
			if (needsComma) {
				buff.append(",");
			}
			buff.append("?");
			needsComma = true;

		}
		buff.append(")\";\n");
		String retval = buff.toString();
		return retval;
	}

	String getInsertRowPreamble(String ClassNameBase) {
		StringBuffer buff = new StringBuffer();
		buff.append("   /**\n");
		buff.append("   * ConnectionPool safe method for persisting an instance of "
				+ ClassNameBase + "\n");
		buff.append("   * if this is called repeatedly within a transaction, it is highly recommended\n");
		buff.append("   * that the method pairs connectionPersistenceBegin and connectionPersistenceEnd be called \n");
		buff.append("   * to reduce sql statement parsing.\n");
		buff.append("   */\n");

		buff.append("   public void insertRow(" + ClassNameBase
				+ " row, DbConnector dbc)\n");
		buff.append("   throws java.sql.SQLException \n");
		buff.append("   {\n");
		buff.append("       if (insertStmt == null || !persistConnection)\n");
		buff.append("       {\n");
		buff.append("           insertStmt = dbc.prepareStatement(insertText);\n");
		buff.append("       }\n");
		buff.append("       PreparedStatementHelper helper = new PreparedStatementHelper(insertStmt);\n");
		return buff.toString();
	}

	public String getInsertRow(JavaGeneratorArguments arguments, Table table) {
		StringBuffer buff = new StringBuffer();
		buff.append(getInsertRowPreamble(arguments.getDaoImplClassName()));
		// ArrayList<Column> columns = tableColumns.getColumns();
		for (ColumnAttributes column : table.getColumns()) {

			buff.append("        ");
			buff.append("insertStatement.setObject(");
			buff.append("bindNbr++,row.get");
			buff.append(namer.getAttributeName(column.getColumnName()));
			buff.append("());\n");

		}
		buff.append("        insertStmt.executeUpdate();\n");
		buff.append("   }\n");
		buff.append(" \n");
		return new String(buff);
	}

	/**
     */
	static void createCloseRoutine(BufferedWriter os) {
		try {
			os.write("   public void close()\n");
			os.write("   {\n");
			os.write("     try\n");
			os.write("     {\n");
			os.write("        if (rset!=null)\n");
			os.write("        {\n");
			os.write("           rset.close();\n");
			os.write("           rset=null;\n");
			os.write("        }\n");
			os.write("        if (stmt!=null)\n");
			os.write("        {\n");
			os.write("            // stmt.close();\n");
			os.write("            // stmt=null;\n");
			os.write("        }\n");
			os.write("     }catch (Exception e)\n");
			os.write("     {\n");
			os.write("        System.out.println(e);\n");
			os.write("     }\n");
			os.write("   }\n");
		} catch (Exception e) {
			System.out.println("Error in createCloseRoutine");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
     */
	static void createFinalizeRoutine(BufferedWriter os)
			throws java.io.IOException {
		os.write("   protected void finalize() throws Throwable\n"); // !! what
																		// is
																		// the
																		// point
		os.write("   {\n");
		os.write("       this.close();\n");
		os.write("   }\n");
	}

	/**
	 * generates
	 */

	// private String tagName(String columnName) {
	// return tagName(columnName, objectFormat);
	// }

	public static String ClassName(String columnName) {
		String rc = null;
		rc = StringHelper.attributeName(columnName);
		rc = rc.substring(0, 1).toUpperCase() + rc.substring(1);
		return rc;
	}

	/**
	 * @todo move to BasePersistenceGenerator. generates getRow function which
	 *       gets one row from the resultset
	 */
	String getGetRowByName(JavaGeneratorArguments args,
			Collection<ColumnAttributes> columns) {
		StringBuffer buff = new StringBuffer(1024 * 16);
		buff.append("\tpublic static void getRow(ResultSet rset, "
				+ args.getDtoClassName() + " row) ");
		buff.append(" throws java.sql.SQLException\n");
		buff.append("\t{\n");
		buff.append("\t\tString columnName = null;\n\n");
		buff.append("\t\ttry {\n");

		for (ColumnAttributes column : columns) {
			buff.append("\t\t\t");
			buff.append(getSetBean(column));
		}

		buff.append("\t\t}\n");
		buff.append("\t\tcatch (java.sql.SQLException s) {\n");
		buff.append("\t\t\tthrow new java.sql.SQLException(\"error processing column\" + columnName + \"\\n\" + s.getMessage());\n,s");
		buff.append("\t\t}\n");
		buff.append("\t\t} // end of getRow \n");
		buff.append("\n");
		return buff.toString();

	} // try

	String getSetBean(ColumnAttributes column
	// , JavaGeneratorArguments args
	) {
		column.getColumnName();
		mapper.getJavaObjectType(column);
		String getJdbcType = mapper.getJdbcType(column);
		column.getColumnType();

		String attrNameUpper = namer.attributeNameInitCap(column
				.getColumnName());
		StringBuilder setter = new StringBuilder();
		// if (args.isGeneratePrimitives()) {
		// setter.append("        " + primitive + " "
		// + StringHelper.attributeNameUpper(label) + " = rset."
		// + getMethod + "(columnName = \"" + label + "\");\n");
		// setter.append("        row.set" + attrNameUpper
		// + "(rset.wasNull() ? null : new " + object + "("
		// + attrNameUpper + "));\n");
		// } else {

		setter.append("row.set");
		setter.append(attrNameUpper);
		setter.append("(rset.");
		setter.append(getJdbcType);
		setter.append("(columnName = \"");
		setter.append(column.getColumnName());
		setter.append("\"));\n");
		// }
		String retval = setter.toString();
		logger.debug("setter : \n" + retval);
		return retval;
	}

	/**
	 * Generate getRowByIndexFunction function which gets one row from the
	 * ResultSet and retrieves the columns using indexes rather than column
	 * names.
	 */

	/** generate the string used for bind function argument declaration */
	String getBindDeclaration() {
		return getBind(true);
	}

	/** generate the string used for bind function argument declaration */
	String getBindInvocation() {
		return getBind(false);
	}

	// generate the bind parameters for bind function declaration and invocation
	//
	String getBind(boolean emitType) {
		String rc = "(";
		for (int c = 0; c < bindVariables.length; c++) {
			for (int c1 = 0; c1 < c; c1++) {
				if (!bindVariables[c1].varName.equals(bindVariables[c].varName)) {
					if (c > 0) {
						rc += ",";
					}
					if (emitType) {
						rc += bindVariables[c].javaType;
					}
					rc += bindVariables[c].varName;
				}
			}
			rc += ")";
		}
		return rc;
	}

	// /**
	// * Generate the persistence class, which gets the data into and out of the
	// * persistent store
	// */
	// String getGenBasePersistence(Table table) {
	// StringBuffer buff = new StringBuffer(1024 * 8);
	// logger.info("genBasePersistence className = "
	// + arguments.getDaoImplClassName() + "\n");
	//
	// // mdata = getMetaDataTable(dbc, table);
	//
	// // packages and imports
	// buff.append("package ");
	// buff.append(packageName);
	// buff.append(";\n\n");
	// buff.append("import java.util.*;\n");
	//
	// buff.append("\n");
	// // class declaration
	// buff.append("public class " + arguments.getDaoImplClassName() + "S\n");
	// buff.append("{\n");
	//
	// buff.append("\n");
	// buff.append("    ResultSet rset = null;\n");
	// buff.append("    PreparedStatement selectStmt = null;\n");
	// // buff.append("    static final String selectText = "
	// // + getSelectStatement(table) + "\n");
	// if (arguments.isGenerateInsert()) {
	// buff.append("    PreparedStatement insertStmt = null;\n");
	// }
	//
	// if (arguments.isGenerateInsert()) {
	// buff.append("    /**\n");
	// buff.append("    * sql text for inserting all rows into the table\n");
	// buff.append("    */\n");
	// buff.append("    static String insertText = "
	// + getInsertStatement(table) + "\n");
	// }
	// // buff.append(getPersistConstructor());
	// buff.append(getGetRowByName(args, table.getColumns()));
	// // genGetRowByIndex(buff.append, arguments.getDtoClassName());
	// buff.append(getGenerateClear());
	// buff.append(getGenerateAdd());
	// buff.append(getGenerateSize());
	// if (args.isGenerateInsert()) {
	// buff.append(getGenerateInsertAll());
	// buff.append(getInsertRow(arguments, table));
	// }
	// buff.append(getGenerateIterator());
	// buff.append("}  // end of class\n");
	// return buff.toString();
	// }

	String getConstructor(JavaGeneratorArguments arguments) {
		StringBuilder sb = new StringBuilder();
		sb.append("    /** Default constructor for "
				+ arguments.getDaoImplClassName() + ". */\n");
		sb.append("    public " + arguments.getDaoImplClassName() + "() {\n");
		sb.append("    }\n\n");
		String retval = sb.toString();
		return retval;
	}

	public String getSelect(List<String> selectText) {
		StringBuilder sb = new StringBuilder();
		sb.append("\tprivate String selectText = \n");
		sb.append(getSelectTextAsJavaString(selectText));
		sb.append(";\n");
		return sb.toString();
	}

	public String getSelectTextAsJavaString(List<String> selectText) {
		return StringUtils.toJavaString(selectText, "\t", true);
	}

	public String getSelectText(String tableName) {
		StringBuilder stmtText = new StringBuilder();
		stmtText.append("\"\" +\n");
		stmtText.append("         \"SELECT\\n\" +\n");
		stmtText.append("  columnList + ");
		stmtText.append("\\n\" +\n");
		stmtText.append("        \"FROM " + tableName + "\\n\";\n\n");
		stmtText.toString();
		return new String(stmtText);
	}

	public String getColumnsForSql(Collection<ColumnAttributes> columns) {
		StringBuilder stmtText = new StringBuilder();
		boolean needsComma = false;
		stmtText.append("String columnList = ");
		for (ColumnAttributes column : columns) {

			if (needsComma) {
				stmtText.append(",\\n\" +\n");
			}
			needsComma = true;
			String label = column.getColumnName().toLowerCase();
			stmtText.append("        \"    ");
			stmtText.append(label);
		}
		String retval = stmtText.toString();
		return retval;
	}

	/**
	 * @todo write generateFetchByPrimaryKey()
	 * @param os
	 *            BufferedWriter
	 * @throws IOException
	 */

	String getFetchStub(JavaGeneratorArguments arguments) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("   /**\n");
		sb.append("    * A template for connection pool safe single call.\n");
		sb.append("    */\n");
		sb.append("    /*\n");
		sb.append("    public void getForPk(Connection dbc, Integer pk)\n");
		sb.append("    throws java.sql.SQLException {\n");
		sb.append("       String stmtText = selectText + \" where primaryKey = ?\";\n");
		sb.append("       PreparedStatement stmt = null;\n");
		sb.append("       ResultSet rset=null;\n");
		sb.append("       clear();\n");
		sb.append("       try {\n");
		sb.append("          stmt = dbc.prepareStatement(stmtText);\n");
		sb.append("          stmt.setInt(1,pk.intValue());\n");
		sb.append("          rset = stmt.executeQuery();\n");
		sb.append("          while (rset.next()) {\n");
		sb.append("           " + arguments.getDtoClassName() + " row = new "
				+ arguments.getDtoClassName() + "();\n");
		sb.append("             getRow(rset, row);\n");
		sb.append("             add(row);\n");
		sb.append("          }\n ");
		sb.append("       } catch (java.sql.SQLException s) {\n");
		sb.append("          throw new java.sql.SQLException(s.getMessage() + \"\\nwhile processing\\n\" + stmtText);\n");
		sb.append("       } finally { \n");
		sb.append("         	if (stmt != null) { \n");
		sb.append("                 stmt.close();\n");
		sb.append("           }\n");
		sb.append("       }\n");
		sb.append("    }\n");
		sb.append("    */\n");
		String retval = sb.toString();
		return retval;
	}

	// TODO should be parameterized
	public String generateGetAllStub(JavaGeneratorArguments arguments) {
		StringBuilder buff = new StringBuilder();
		buff.append("\n");
		buff.append("   /**\n");
		buff.append("    * A template for connection pool safe call for fetching all records.\n");
		buff.append("    */\n");
		buff.append("    public void getAll(Connection dbc)\n");
		buff.append("    throws java.sql.SQLException {\n");
		buff.append("       String stmtText = selectText ;\n");
		buff.append("       PreparedStatement stmt = null;\n");
		buff.append("       ResultSet rset=null;\n");
		buff.append("       clear();\n");
		buff.append("       try {\n");
		buff.append("          stmt = dbc.prepareStatement(stmtText);\n");
		buff.append("          rset = stmt.executeQuery();\n");
		buff.append("          while (rset.next()) {\n");
		buff.append("           " + arguments.getDtoClassName() + " row = new "
				+ arguments.getDtoClassName() + "();\n");
		buff.append("             getRow(rset, row);\n");
		buff.append("             add(row);\n");
		buff.append("          }\n ");
		buff.append("       } catch (java.sql.SQLException s) {\n");
		buff.append("          throw new java.sql.SQLException(s.getMessage() + \"\\nwhile processing\\n\" + stmtText\n,s);\n");
		buff.append("       } finally { \n");
		buff.append("         	if (stmt != null) { \n");
		buff.append("                 stmt.close();\n");
		buff.append("           }\n");
		buff.append("       }\n");
		buff.append("    }\n");
		String retval = buff.toString();
		return retval;
	}

	String getGenerateClear() {
		return "    public void clear(){\n" + "       rows.clear();\n"
				+ "    }\n";
	}

	// String getGenerateAdd() {
	// return "    public void add(" + arguments.getDaoImplClassName()
	// + " row) {\n" + "       rows.add(row);\n" + "    }\n";
	// }
	//
	// void generateConnectionPersistenceMethods(BufferedWriter os)
	// throws java.io.IOException {
	// os.write("    public void connectionPersistenceBegin() {\n");
	// os.write("       persistConnection = true;\n");
	// os.write("    }\n");
	// os.write("\n");
	//
	// os.write("    public void connectionPersistenceEnd()\n");
	// os.write("    throws java.sql.SQLException\n");
	// os.write("    {\n");
	// os.write("       persistConnection = false;\n");
	// if (arguments.isGenerateInsert()) {
	// os.write("       if (insertStmt != null) {\n");
	// os.write("          insertStmt.close();\n");
	// os.write("          insertStmt = null;\n");
	// os.write("       }\n");
	// }
	// os.write("       if (selectStmt != null) {\n");
	// os.write("            selectStmt.close();\n");
	// os.write("            selectStmt = null;\n");
	// os.write("       }\n");
	// os.write("    }\n");
	// }

	StringBuffer getGenerateInsertAll(JavaGeneratorArguments arguments) {
		StringBuffer buff = new StringBuffer(1024 * 4);
		buff.append("\n");
		buff.append("    /** Insert all tuples into persistent store.*/\n");
		buff.append("    public void insertAll(DbConnector dbc)\n");
		buff.append("    throws java.sql.SQLException\n");
		buff.append("    {\n");
		buff.append("        Iterator it = rows.iterator();\n");
		buff.append("        while (it.hasNext()) {\n");
		buff.append("             ");
		buff.append(arguments.getDtoClassName());
		buff.append(" row = (");
		buff.append(arguments.getDtoClassName());
		buff.append(") it.next();\n");
		buff.append("             insertRow(row,dbc);\n");
		buff.append("        }\n");
		buff.append("    }\n");
		return buff;
	}

	String getGenerateSize() {
		return "\n" + "    /** Return the number of the rows contained. */\n"
				+ "    public int size() {\n" + "        return rows.size();\n"
				+ "    }\n";
	}

	// public static void main(String args[]) {
	//
	// // File outFileDscr;
	// DaoGen jg = null;
	//
	// try {
	// if (args.length == 1) {
	// jg = new DaoGen(args[0]);
	// }
	// } catch (Exception e) {
	// logger.info(e.getMessage());
	// e.printStackTrace();
	// logger.info("terminating");
	// System.exit(1);
	// }
	// /*
	// * try { jg.init(); } catch (Exception e) { e.printStackTrace();
	// * System.exit(1); }
	// */
	// jg.objectFormat = true;
	// logger.info("mode = " + jg.mode);
	// try {
	// if (jg.mode.equals("select")) {
	// logger.info("in main sqlFileName = " + jg.sqlFileName);
	// // jg.genSelect();
	// }
	// jg.genBaseRow();
	// jg.genBasePersistence();
	// if (jg.genImplementationArg.equals("true")) {
	// jg.genImplementationRow();
	// jg.genImplementationPersistence();
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// System.exit(1);
	// }
	//
	// }

}
