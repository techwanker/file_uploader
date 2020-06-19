//package org.javautil.javagen;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.sql.PreparedStatement;
//import java.sql.ResultSetMetaData;
//import java.sql.Types;
//import java.util.logging.Logger;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.javautil.text.StringHelper;
//
//import com.sun.org.apache.xerces.internal.impl.PropertyManager;
//
//public class JavaGen {
//	private static final Logger logger = Logger
//			.getLogger("com.javautil.JavaGen.JavaGen");
//	private final String tab = "      ";
//	// private StringHelper sh = new StringHelper();
//	private static final String classTag = "com.javautil.JavaGen.";
//	// manifest constants for internal representation of oracle data types
//	/** Oracle Datatype for number with scale = 0 and precision < 10. */
//	private static final int INT = 1;
//
//	/** Oracle Datatype varchar2 or char */
//	private static final int STR = 2;
//
//	/** Oracle Datatype number with scale = 0 and precision > 9 and < 18 */
//	private static final int LONG = 3;
//
//	/* Datatype for number with scale != 0 */
//	private static final int DOUBLE = 4; // Oracle
//
//	/** Datatype for Date. */
//	private static final int DATE = 5; // Oracle
//	/** Character Large Object */
//	private static final int CLOB = 6; // Oracle
//	/** Raw Data. */
//	private static final int VARBINARY = 7; // Oracle
//	/** Large numbers */
//	private static final int NUMBER = 8; // Oracle
//
//	// private static final int StmtTypeSelect = 1;
//	// private static final int StmtTypeInsert = 2;
//	// private static final String PlsqlParmPrefix = "p_";
//	/**
//	 * private Domain for codeStyle. private private STYLE_IS_KERNEL (unix
//	 * kernel format) generates code of the form private void
//	 * functionName(argType arg) {<br/>
//	 * private &nbsp;&nbsp;&nbsp;....<br/>
//	 * private }. private
//	 */
//	// tracing control
//	private final boolean Verbose = true;
//	private final boolean ShowArgs = false;
//	// private boolean ShowSqlStmt = false;
//	//
//	private ResultSetMetaData mdata = null;
//	// private boolean objectFormat = false;
//	/*
//	 * column names with _
//	 */
//	private boolean generateInsert = true;
//
//	private String packageName = null;
//	public String packageNameTag = "cPackageName";
//
//	// command line arguments
//	private String mode = null;
//	private String table = "";
//	private final String filenm = null;
//	private String className = null;
//
//	private String ClassNameBase = null;
//	/**
//	 * Name of class for holding one database record.
//	 */
//
//	public String classNameBaseTag = "cClassNameBase";
//	private String ClassNameImplementation = null;
//	public String classNameImplementationTag = "cClassNameImplementation";
//	// private String genImplementationArg = "false"; // name
//	// of
//	// class
//	// for
//	// holding
//	// one
//	// database
//	// record
//	private String PlsqlPackageName = null; // name
//	// of
//	// class
//	// for
//	// holding
//	// one
//	// database
//	// record
//	private String sqlFileName = null;
//	// name
//	// of
//	// file
//	// with
//	// sql
//	// statement
//	// used
//	// in
//	// generating
//	private String sourceCodeDirectory = null;
//	private boolean generatePrimitivesAsObjects = false;
//	// private boolean generatePrimitiveAccessors = false;
//	// derived fields
//	private String PlsqlPackageFileName = null;
//	private String JspMaintName = null;
//	//
//	private BufferedWriter bwBase = null;
//	private BufferedWriter bwPersist = null;
//	// private BufferedWriter bwCon = null;
//	private OracleConnection dbc;
//	private String propertyFileName = null;
//	private PropertyManagement properties = null;
//	// DbConnector arguments
//	private final String host = null;
//	private final String instance = null;
//	private final String userid = null;
//	private final String password = null;
//	private final String port = null;
//	// tags for GetArgs and properties files
//	// static finals
//	// private static final String dataClassNameTag = classTag +
//	// "dataClassName";
//	private static final String classNameTag = classTag + "className";
//	private static final String PlsqlPackageNameTag = classTag
//			+ "PlsqlPackageName";
//	private static final String ClassNameBaseTag = classTag + "ClassNameBase";
//	private static final String ClassNameImplementationTag = classTag
//			+ "ClassNameImplementation";
//	// private static final String genImplementationTag = classTag +
//	// "genImplementation";
//	private static final String packageTag = classTag + "packageName";
//	// private static final String statementTag = classTag + "statement";
//	// private static final String fileTag = classTag + "file";
//	private static final String modeTag = classTag + "mode";
//	private static final String sqlFileNameTag = classTag + "sqlFileName";
//	private static final String tableTag = classTag + "table";
//	private static final String generatePrimitivesAsObjectsTag = classTag
//			+ "generatePrimitivesAsObjects";
//	private static final String generateInsertTag = classTag + "generateInsert";
//	// tags for html and properties files
//	private static final String webTag = "c"; // clear
//	// representation
//	public static final String dataClassNameWeb = webTag + "DataClassName";
//	public static final String classNameWeb = webTag + "ClassName";
//	public static final String PlsqlPackageNameWeb = webTag
//			+ "PlsqlPackageName";
//	public static final String ClassNameBaseWeb = webTag + "ClassNameBase";
//	public static final String ClassNameImplementationWeb = webTag
//			+ "ClassNameImplementation";
//	public static final String genImplementationWeb = webTag
//			+ "GenImplementation";
//	public static final String packageNameWeb = webTag + "PackageName";
//	public static final String statementWeb = webTag + "Statement";
//	public static final String fileWeb = webTag + "File";
//	public static final String modeWeb = webTag + "Mode";
//	public static final String sqlFileNameWeb = webTag + "SqlFileName";
//	public static final String tableWeb = webTag + "Table";
//	public static final String generatePrimitivesAsObjectsWeb = webTag
//			+ "GeneratePrimitivesAsObjects";
//	public static final String generateInsertWeb = webTag + "GenerateInsert";
//	static final String destinationDirectoryWebTag = "cDestinationDirectory";
//	static final String destinationDirectoryTag = classTag
//			+ "destinationDirectory";
//	static final int STYLE_IS_KERNEL = 1;
//	static final int STYLE_IS_PRETTY = 2;
//
//	/**
//	 * Instantiate an instance of JavaGen using a properties file.
//	 * 
//	 * The following properties are recognized<br>
//	 * see DbConnector for current database properties
//	 * <p>
//	 * 
//	 * @param propertyFileName
//	 * @exception java.lang.Exception
//	 */
//	public JavaGen(String propertyFileName) throws java.lang.Exception {
//		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>!!!!!!!!!!!!!!!!!!!!!!");
//		// get all of the properties
//		this.propertyFileName = propertyFileName;
//		properties = new PropertyManager(propertyFileName);
//		// dataClassName = properties.getProperty(dataClassNameTag,"");
//		String destinationDirectory = properties.getProperty(
//				destinationDirectoryTag, ".");
//		className = properties.getProperty(classNameTag, "");
//		PlsqlPackageName = properties.getProperty(PlsqlPackageNameTag, "");
//		table = properties.getProperty(tableTag, "");
//		ClassNameImplementation = properties
//				.getProperty(ClassNameImplementationTag);
//		if (ClassNameImplementation == null) {
//			ClassNameImplementation = StringHelper.attributeName(table);
//			logger.info("!!!ClassNameImplementation derived from table name"
//					+ ClassNameImplementation);
//		}
//		ClassNameBase = properties.getProperty(ClassNameBaseTag);
//		if (ClassNameBase == null) {
//			ClassNameBase = ClassNameImplementation + "Base";
//		}
//		logger.info("ClassNameBase = " + ClassNameBase);
//		JspMaintName = ClassNameImplementation + "Maint.jsp";
//		// genImplementationArg = properties.getProperty(genImplementationTag,
//		// "true");
//		packageName = properties.getProperty(packageTag, "");
//		sourceCodeDirectory = getSourceDirectory(destinationDirectory,
//				packageName);
//		// statement = properties.getProperty(statementTag,"");
//		// file = properties.getProperty(fileTag,"");
//		mode = properties.getProperty(modeTag, "");
//		sqlFileName = properties.getProperty(sqlFileNameTag, "");
//		String genInsertProperty = properties.getProperty(generateInsertTag,
//				"true");
//		if (genInsertProperty.equals("false")) {
//			generateInsert = false;
//		}
//		// String destinationDirectory =
//		// properties.getProperty(destinationDirectoryTag,
//		// "");
//		setGeneratePrimitivesAsObjects();
//	}
//
//	public JavaGen() {
//	}
//
//	/**
//	 * @args className The name of the classfile to be created, not include
//	 *       .java extension
//	 * @args package The name of the package to specify
//	 * @args statement the complete sql statement to be parsed
//	 * @args file fetch select from file named this
//	 * @args table table name for insert for insert;
//	 * 
//	 * @args host host computer
//	 * @args instance false,"instance (KG7, HC7 etc)");
//	 * @args userid false,"user id"
//	 * @args password password;
//	 * @args staticprepare","",false,"Make prepares static? (true or false)
//	 *       (defaults to false)");
//	 * 
//	 * @args "mode" ,"",false,"fetch or insert");
//	 */
//	/**
//	 * Construct an instance of JavaGen, from parsed information.
//	 */
//
//	public JavaGen(ResultSetMetaData meta) {
//		this.mdata = meta;
//	}
//
//	public JavaGen(OracleConnection dbc, String packageName, String className,
//			String sqlStmt, int stmtType) {
//		this.dbc = dbc;
//		this.packageName = packageName;
//		this.className = className;
//		// this.sqlStatement = sqlStmt;
//		// this.stmtType = stmtType;
//	}
//
//	private void setGeneratePrimitivesAsObjects() {
//		String response = properties.getProperty(
//				generatePrimitivesAsObjectsTag, "true");
//		try {
//			generatePrimitivesAsObjects = StringHelper.isTrue(response);
//		} catch (java.lang.IllegalArgumentException e) {
//			generatePrimitivesAsObjects = true;
//			logger.info("Unrecognizable response to parameter generatePrimitivesAsObjects: "
//					+ response + " assuming true\n");
//		}
//		if (generatePrimitivesAsObjects) {
//			logger.info("Generating Primitives as Objects");
//		} else {
//			logger.info("Generating Primitives as Primitives");
//		}
//	}
//
//	/**
//	 * can't do this in the constructor because it throws exceptions
//	 */
//	private void init() throws java.lang.Exception, java.sql.SQLException {
//		if (ShowArgs) {
//			logger.info("In init");
//			displayArgs();
//		}
//		ValidateArguments();
//		dbc = (OracleConnection) ConnectionHelper.getConnection(false);
//		if (Verbose) {
//			logger.info("exiting init");
//		}
//		// set derived values
//		PlsqlPackageName = table + "_pkg";
//		PlsqlPackageFileName = PlsqlPackageName + ".sql";
//	}
//
//	private void displayArgs() {
//		logger.info("mode 		 = " + mode);
//		logger.info("table		 = " + table);
//		logger.info("filenm		 = " + filenm);
//		logger.info("ClassNameBase 	 = " + ClassNameBase);
//		logger.info("ClassNameImplementation 	 = " + ClassNameImplementation);
//		logger.info("PlsqlPackageName = " + PlsqlPackageName);
//		logger.info("sqlFileName 	 = " + sqlFileName);
//		logger.info("bwBase 		 = " + bwBase);
//		logger.info("dbc         	 = " + dbc);
//		logger.info("propertyFileName = " + propertyFileName);
//		logger.info("properties  	 = " + properties);
//		// DbConnector Propert DbConnector Properties
//		logger.info("host      	 = " + host);
//		logger.info("instance  	 = " + instance);
//		logger.info("userid    	 = " + userid);
//		logger.info("password  	 = " + password);
//		logger.info("port      	 = " + port);
//	}
//
//	private void ValidateArguments() throws java.lang.Exception {
//		if ((className == null || className.length() == 0)
//				&& (filenm == null || filenm.length() == 0)
//				&& (table == null || table.length() == 0)) {
//			logger.info("Specify className or filenm or table");
//			throw new Exception("Specify className or filenm or table");
//		}
//		if (PlsqlPackageName == null) {
//			PlsqlPackageName = table + "_pkg";
//		}
//	}
//
////	/**
////	 * Generate the Base class, which contains the data for one row and the data
////	 * accessors.
////	 */
////	private void genBaseRow() throws java.io.IOException, java.sql.SQLException {
////		logger.info("genBaseRow");
////		logger.info("ClassNameBase " + ClassNameBase + "\n");
////		String fileName = null;
////		if (ClassNameBase == null || ClassNameBase.length() == 0) {
////			throw new java.lang.IllegalStateException("ClassNameBase = "
////					+ ClassNameBase);
////		}
////		fileName = ClassNameBase + ".java";
////		String fName = sourceCodeDirectory + "/" + fileName;
////		File f = new File(fName);
////		logger.info("Generating " + fName + "\n");
////		try {
////			bwBase = new BufferedWriter(new FileWriter(f));
////		} catch (Exception e) {
////			logger.info("Unable to open " + fName);
////		}
////		mdata = getMetaDataTable(dbc, table);
////		bwBase.write("package " + packageName + ";\n\n");
////		bwBase.write("import java.util.*;\n");
////		bwBase.write("import com.javautil.oracle.*;\n\n");
////		bwBase.write("import java.text.*;\n");
////		bwBase.write("import com.javautil.util.*;\n");
////		bwBase.write("import java.io.*;\n");
////		bwBase.write("import java.sql.*;\n");
////		bwBase.write("import javax.servlet.*;\n");
////		bwBase.write("import javax.servlet.http.*;\n");
////		// bwBase.write("import com.javautil.util.DateHelper;\n");
////		bwBase.write("\n");
////		bwBase.write("/**\n");
////		bwBase.write("  * Contains a temporal representation of the data persisted in a tuple of "
////				+ table + "\n");
////		bwBase.write("  * Persistence management is supported in class "
////				+ ClassNameBase + ".\n");
////		bwBase.write("  * This code was generated by com.javautil.JavaGen on "
////				+ new java.util.Date() + "\n");
////		bwBase.write("  */\n");
////		bwBase.write("public class " + ClassNameBase + "\n");
////		bwBase.write("{\n");
////		genTagNames(bwBase, mdata);
////		bwBase.write("String obfuscatedPrimaryKey = null;\n");
////		// these attributes come from the database
////		genAttributes(bwBase, mdata);
////		genAllAccessors(bwBase, mdata);
////		genObfuscationAccessors(bwBase);
////		genPopFromHtmlForm(bwBase);
////		bwBase.write(getToXml());
////		bwBase.write("}\n");
////		bwBase.close();
////	}
//
//	// private void genImplementationRow() throws java.io.IOException {
//	// BufferedWriter bw = null;
//	// logger.info("ClassNameImplementation " + ClassNameImplementation + "\n");
//	// String fileName = null;
//	// if (ClassNameImplementation == null || ClassNameImplementation.length()
//	// == 0) {
//	// throw new
//	// java.lang.IllegalStateException("ClassNameImplementation is null");
//	// }
//	// fileName = ClassNameImplementation + ".java";
//	// String fName = sourceCodeDirectory + "/" + fileName;
//	// File f = new File(fName);
//	// if (f.exists()) {
//	// throw new java.lang.IllegalArgumentException("Please delete file '" +
//	// f.getCanonicalFile() + "'");
//	// }
//	// logger.info("Generating Implementation Row " + fName + "\n");
//	// try {
//	// bw = new BufferedWriter(new FileWriter(f));
//	// } catch (java.io.IOException e) {
//	// logger.info("Unable to open " + fName);
//	// throw (e);
//	// }
//	// // mdata= getMetaDataTable(conn,table);
//	// bw.write("package " + packageName + ";\n\n");
//	// bw.write("//import java.util.*;\n");
//	// bw.write("//import com.javautil.oracle.*;\n\n");
//	// bw.write("//import java.text.*;\n");
//	// bw.write("//import com.javautil.util.*;\n");
//	// bw.write("//import java.io.*;\n");
//	// bw.write("//import java.sql.*;\n");
//	// bw.write("//import javax.servlet.*;\n");
//	// bw.write("//import javax.servlet.http.*;\n");
//	// bw.write("\n");
//	// bw.write("public class " + ClassNameImplementation + " extends " +
//	// ClassNameBase + "\n");
//	// bw.write("{\n");
//	// // genTagNames(bwBase,mdata);
//	// // these attributes come from the database
//	// // genAttributes(bwBase,mdata);
//	// // genAllAccessors(bwBase, mdata);
//	// // genObfuscationAccessors(bwBase);
//	// // genPopFromHtmlForm(bwBase);
//	// bw.write("}\n");
//	// bw.close();
//	// }
//
//	// private void genImplementationPersistence() throws java.io.IOException,
//	// java.lang.IllegalStateException {
//	// BufferedWriter bw = null;
//	// logger.info("ClassNameImplementation " + ClassNameImplementation + "\n");
//	// String fileName = null;
//	// if (ClassNameImplementation == null || ClassNameImplementation.length()
//	// == 0) {
//	// throw new
//	// java.lang.IllegalStateException("ClassNameImplementation is null");
//	// }
//	// fileName = ClassNameImplementation + "S.java";
//	// String fName = sourceCodeDirectory + "/" + fileName;
//	// File f = new File(fName);
//	// if (f.exists()) {
//	// throw new java.lang.IllegalArgumentException("Please delete file '" +
//	// f.getAbsoluteFile().getName());
//	// }
//	// logger.info("Generating Implementation Persistence" + fName + "\n");
//	// try {
//	// bw = new BufferedWriter(new FileWriter(f));
//	// } catch (java.io.IOException e) {
//	// logger.info("Unable to open " + fName);
//	// throw (e);
//	// }
//	// mdata = getMetaDataTable(dbc, table);
//	// bw.write("package " + packageName + ";\n\n");
//	// bw.write("import java.util.*;\n");
//	// bw.write("import com.javautil.oracle.*;\n\n");
//	// bw.write("import java.text.*;\n");
//	// bw.write("import com.javautil.util.*;\n");
//	// bw.write("import java.io.*;\n");
//	// bw.write("import java.sql.*;\n");
//	// bw.write("import javax.servlet.*;\n");
//	// bw.write("import javax.servlet.http.*;\n");
//	// bw.write("\n");
//	// bw.write("public class " + ClassNameImplementation + "S extends " +
//	// ClassNameBase + "S\n");
//	// bw.write("{\n");
//	// generateFetchStub(bw);
//	// generateGetAllStub(bw);
//	// bw.write("}\n");
//	// bw.close();
//	// }
//
//	private int getType(ResultSetMetaData mdata, int col)
//			throws java.sql.SQLException {
//		// int getType(String typeName,int type, int precision, int scale) {
//		int type = mdata.getColumnType(col);
//		int precision = mdata.getPrecision(col);
//		int scale = mdata.getScale(col);
//		String typeName = mdata.getColumnTypeName(col);
//		String label = mdata.getColumnLabel(col);
//		int rc = -1;
//		// logger.info("invoked with type " + type);
//		// logger.info("Types.Clob " + Types.CLOB);
//		if (typeName.equals("LONG")) {
//			rc = CLOB;
//		}
//		if (type == Types.NUMERIC) {
//			if (precision == 0 && scale == 0) {
//				rc = NUMBER;
//			} else if (scale != 0) {
//				rc = DOUBLE;
//			} else {
//				rc = precision < 10 ? INT : NUMBER;
//			}
//		}
//		switch (type) {
//		case Types.CHAR:
//			rc = STR;
//			break;
//		case Types.VARCHAR:
//			rc = STR;
//			break;
//		case Types.TIMESTAMP:
//			rc = DATE;
//			break;
//		case Types.DATE:
//			rc = DATE;
//			break;
//		case Types.CLOB:
//			rc = CLOB;
//			break;
//		case Types.VARBINARY:
//			rc = VARBINARY;
//			break;
//		case Types.NUMERIC:
//			break;
//		default:
//			logger.warning("unknown type " + type + " for column " + label);
//		}
//		if (rc == -1) {
//			logger.warning("unknown type: " + type);
//			throw new java.lang.IllegalArgumentException("unknown type type: "
//					+ type);
//		}
//		// logger.finest("column: " + label + " precision: " + precision + "
//		// scale: " + scale + " type " + typeDescription);
//		return rc;
//	}
//
//	private String getInsertStatement() throws java.sql.SQLException {
//		StringBuffer buff = new StringBuffer();
//		buff.append("\"insert into " + table + " \"+\n        \"(\\n\"+\n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			int index = c + 1;
//			String label = mdata.getColumnLabel(index).toLowerCase();
//			buff.append("             \"");
//			buff.append(label);
//			if (index < mdata.getColumnCount()) {
//				buff.append(",");
//			}
//			buff.append(" \\n\"+\n");
//		}
//		buff.append("         \")\\n\"+ \n");
//		buff.append("        \"select ");
//		for (int c = 1; c <= mdata.getColumnCount(); c++) {
//			if (mdata.getColumnLabel(c).toUpperCase().equals("LAST_MOD_DT")) {
//				buff.append(" sysdate ");
//			} else {
//				buff.append("?");
//			}
//			if (c < mdata.getColumnCount()) {
//				buff.append(',');
//			}
//		}
//		buff.append(" from dual\";\n");
//		return new String(buff);
//	}
//
//	private String getInsertRow() throws java.sql.SQLException {
//		StringBuffer buff = new StringBuffer();
//		buff.append("   /**\n");
//		buff.append("   * ConnectionPool safe method for persisting an instance of "
//				+ ClassNameBase + "\n");
//		buff.append("   * if this is called repeatedly within a transaction, it is highly recommended\n");
//		buff.append("   * that the method pairs connectionPersistenceBegin and connectionPersistenceEnd be called \n");
//		buff.append("   * to reduce sql statement parsing.\n");
//		buff.append("   */\n");
//		buff.append("   public void insertRow(" + ClassNameBase
//				+ " row, DbConnector dbc)\n");
//		buff.append("   throws java.sql.SQLException \n");
//		buff.append("   {\n");
//		buff.append("       if (insertStmt == null || !persistConnection)\n");
//		buff.append("       {\n");
//		buff.append("           insertStmt = dbc.prepareStatement(insertText);\n");
//		buff.append("       }\n");
//		buff.append("       PreparedStatementHelper helper = new PreparedStatementHelper(insertStmt);\n");
//		int bindCol = 1;
//		for (int c = 1; c <= mdata.getColumnCount(); c++) {
//			String label = mdata.getColumnLabel(c).toLowerCase();
//			if (label.equals("last_mod_dt")) {
//				logger.info("LAST_MOD_DT found");
//			} else {
//				// System.out.println("processing " + label);
//				buff.append("        "
//						+ getSetHelper(label, c, getType(mdata, c)));
//				bindCol++;
//			}
//		}
//		buff.append("        insertStmt.executeUpdate();\n");
//		buff.append("   }\n");
//		buff.append(" \n");
//		return new String(buff);
//	}
//
//	/**
//	 * generate the attributes based on the genAttributesAsObjects property
//	 */
//	private void genAttributes(BufferedWriter os, ResultSetMetaData mdata)
//			throws java.sql.SQLException, java.io.IOException {
//		genAttributesAsObjects(os, mdata);
//	}
//
//	private void genAttributesAsObjects(BufferedWriter os,
//			ResultSetMetaData mdata) throws java.sql.SQLException,
//			java.io.IOException {
//		os.write(" // class attributes \n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			int index = c + 1;
//			os.write("   /** Container for the data persisted in "
//					+ mdata.getColumnLabel(index) + ". ");
//			String label = mdata.getColumnLabel(index).toLowerCase();
//			int type = mdata.getColumnType(index);
//			if (type < 0) {
//				logger.info("ResultSetMetaData type is " + type);
//				logger.warning("column '" + label + "' is defined as type "
//						+ type);
//			}
//			int precision = mdata.getPrecision(index);
//			int scale = mdata.getScale(index);
//			int sz = mdata.getColumnDisplaySize(index);
//			// int getType = getType(columnTypeName, type, precision, scale);
//			int getType = getType(mdata, index);
//			switch (getType) {
//			case LONG:
//				os.write(" number(" + precision + "," + scale + ") */ \n");
//				break;
//			case INT:
//				os.write(" number(" + precision + "," + scale + ") */ \n");
//				break;
//			case DOUBLE:
//				os.write(" number(" + precision + "," + scale + ") */ \n");
//				break;
//			case STR:
//				os.write(" varchar2(" + sz + ") */ \n");
//				break;
//			case DATE:
//				os.write(" date */ \n");
//				break;
//			case CLOB:
//				os.write(" CLOB  */ \n");
//				break;
//			case VARBINARY:
//				os.write(" VARBINARY */ \n");
//				break;
//			case NUMBER:
//				os.write(" NUMBER */ \n");
//				break;
//			default:
//				logger.info(label + " is an unsupported variable type (type "
//						+ type + " )");
//			}
//			switch (getType) {
//			case LONG:
//				os.write("   Long ");
//				break;
//			case INT:
//				os.write("   Integer ");
//				break;
//			case DOUBLE:
//				os.write("   Double ");
//				break;
//			case STR:
//				os.write("   String ");
//				break;
//			case DATE:
//				os.write("   java.sql.Timestamp ");
//				break;
//			case CLOB:
//				os.write("   String ");
//				break;
//			case VARBINARY:
//				os.write("   byte[] ");
//				break;
//			case NUMBER:
//				os.write("   java.math.BigDecimal ");
//				break;
//			default:
//				logger.info(label + " is an unsupported variable type (type "
//						+ type + " )");
//			}
//			os.write(StringHelper.attributeName(label));
//			os.write(" = null;\n");
//		}
//	}
//
//	private void genTagNames(BufferedWriter os, ResultSetMetaData mdata)
//			throws java.sql.SQLException, java.io.IOException {
//		os.write("\n   // tags for web pages \n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			int index = c + 1;
//			String label = mdata.getColumnLabel(index).toLowerCase();
//			os.write("     /** Field tag for "
//					+ StringHelper.attributeName(label)
//					+ " for validation in servlets and jsp. */\n");
//			os.write("	 public static final String " + tagName(label) + "= \""
//					+ tagName(label) + "\";\n");
//		}
//		os.write("\n");
//	}
//
//	/**
//	 */
//	// private static void createCloseRoutine(BufferedWriter os) {
//	// try {
//	// os.write("   public void close()\n");
//	// os.write("   {\n");
//	// os.write("     try\n");
//	// os.write("     {\n");
//	// os.write("        if (rset!=null)\n");
//	// os.write("        {\n");
//	// os.write("           rset.close();\n");
//	// os.write("           rset=null;\n");
//	// os.write("        }\n");
//	// os.write("        if (stmt!=null)\n");
//	// os.write("        {\n");
//	// os.write("            // stmt.close();\n");
//	// os.write("            // stmt=null;\n");
//	// os.write("        }\n");
//	// os.write("     }catch (Exception e)\n");
//	// os.write("     {\n");
//	// os.write("        System.out.println(e);\n");
//	// os.write("     }\n");
//	// os.write("   }\n");
//	// } catch (Exception e) {
//	// System.out.println("Error in createCloseRoutine");
//	// e.printStackTrace();
//	// System.exit(1);
//	// }
//	// }
//
//	/**
//	 */
//	// private static void createFinalizeRoutine(BufferedWriter os) throws
//	// java.io.IOException {
//	// os.write("   protected void finalize() throws Throwable\n"); // !!
//	// // what
//	// // is
//	// // the
//	// // point
//	// os.write("   {\n");
//	// os.write("       this.close();\n");
//	// os.write("   }\n");
//	// }
//
//	/**
//	 */
//	static void printSelect(String selName, String selString) {
//		logger.info("Begin " + selName);
//		logger.info(selString);
//		logger.info("end " + selName);
//	}
//
//	// @todo convert to user Objects
//	private String getSetHelper(String label, int colNum, int columnType) {
//		String typeSet = "";
//		String attributeName = StringHelper.attributeName(label.toLowerCase());
//		switch (columnType) {
//		case INT:
//			typeSet = "helper.setInt";
//			break;
//		case STR:
//			typeSet = "insertStmt.setString";
//			break;
//		case LONG:
//			typeSet = "helper.setLong";
//			break;
//		case DOUBLE:
//			typeSet = "helper.setDouble";
//			break;
//		case DATE:
//			typeSet = "insertStmt.setTimestamp";
//			break;
//		case NUMBER:
//			typeSet = "insertStmt.setNumber";
//			break;
//		default:
//			logger.warning("unknown datatype for column: " + label);
//		}
//		return (typeSet + "(" + colNum + ",row." + attributeName + ");\n");
//	}
//
//	private void genPlsqlPackage() throws java.sql.SQLException,
//			java.io.IOException {
//		String fileName = PlsqlPackageFileName;
//		String packageName = PlsqlPackageName;
//		String fName = sourceCodeDirectory + "/" + fileName;
//		File f1 = new File(fName);
//		logger.info(">>>Writing to " + fName);
//		BufferedWriter os = new BufferedWriter(new FileWriter(f1));
//		// create package declaration
//		os.write("set echo on\n");
//		os.write("spool " + packageName + "\n");
//		os.write("create or replace package " + packageName + "\n");
//		os.write("is\n");
//		// create add procedure specification
//		genPlSqlPublicProcedure(os, "add");
//		os.write(";\n\n");
//		// create change procedure specification
//		genPlSqlPublicProcedure(os, "change");
//		os.write(";\n\n");
//		// end of package specification
//		os.write("end " + table + "_pkg;\n");
//		os.write("/\n\n");
//		os.write("show errors\n");
//		// create package body
//		os.write("create or replace package body " + packageName + "\n");
//		os.write("is\n");
//		os.write("  my_" + table + " " + table + "%rowtype;\n");
//		genPlSqlSets(os);
//		genPlSqlInsert(os);
//		genPlSqlUpdate(os);
//		genPlSqlPublicProcedure(os, "add");
//		genPlSqlPublicProcedureBody(os);
//		genPlSqlPublicProcedure(os, "change");
//		genPlSqlPublicProcedureBody(os);
//		os.write("end " + packageName + ";\n");
//		os.write("\n");
//		os.write("/\n");
//		os.write("show errors\n");
//		os.write("exit\n");
//		genPlSqlInsertCall(os);
//		os.close();
//	}
//
//	private void genPlSqlInsertCall(BufferedWriter os)
//			throws java.sql.SQLException, java.io.IOException {
//		os.write("/*\n");
//		os.write("   execute " + table + "_pkg." + "add(\n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			String label = mdata.getColumnLabel(c + 1).toLowerCase();
//			os.write("    " + label + "=>   ");
//			if (c < mdata.getColumnCount() - 1) {
//				os.write(",\n");
//			} else {
//				os.write("\n");
//			}
//		}
//		os.write(");\n");
//		os.write("*/\n");
//	}
//
//	private void genPlSqlInsert(BufferedWriter os)
//			throws java.sql.SQLException, java.io.IOException {
//		os.write("procedure add_it is\n");
//		os.write("begin\n");
//		os.write("insert into " + table + "\n");
//		os.write("(\n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			String label = mdata.getColumnLabel(c + 1).toLowerCase();
//			os.write("    " + label);
//			if (c < mdata.getColumnCount() - 1) {
//				os.write(",\n");
//			} else {
//				os.write("\n");
//			}
//		}
//		os.write(")\n");
//		os.write("values\n");
//		os.write("(\n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			String label = mdata.getColumnLabel(c + 1).toLowerCase();
//			os.write("    my_" + table + "." + label);
//			if (c < mdata.getColumnCount() - 1) {
//				os.write(",\n");
//			} else {
//				os.write("\n");
//			}
//		}
//		os.write(");\n");
//		os.write("end add_it;\n");
//	}
//
//	private void genPlSqlUpdate(BufferedWriter os)
//			throws java.sql.SQLException, java.io.IOException {
//		os.write("procedure update_it is\n");
//		os.write("begin\n");
//		os.write("update " + table + "\n");
//		os.write("set\n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			String label = mdata.getColumnLabel(c + 1).toLowerCase();
//			os.write("    " + label + " = " + "my_" + table + "." + label);
//			if (c < mdata.getColumnCount() - 1) {
//				os.write(",\n");
//			} else {
//				os.write("\n");
//			}
//		}
//		os.write(";\n");
//		os.write("end update_it;\n");
//	}
//
//	/**
//	 * generate all of the set methods
//	 */
//	private void genPlSqlSets(BufferedWriter os) throws java.sql.SQLException,
//			java.io.IOException {
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			int index = c + 1;
//			String label = mdata.getColumnLabel(c + 1).toLowerCase();
//			genPlSqlSet(os, label, getType(mdata, index));
//		}
//	}
//
//	private void genPlSqlPublicProcedureBody(BufferedWriter os)
//			throws java.sql.SQLException, java.io.IOException {
//		os.write("is\n");
//		os.write("begin\n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			String label = mdata.getColumnLabel(c + 1).toLowerCase();
//			os.write("       set_" + label + "(p_" + label + ");\n");
//		}
//		os.write("end;\n\n");
//	}
//
//	private void genPlSqlPublicProcedure(BufferedWriter os, String ProcedureName)
//			throws java.io.IOException, java.sql.SQLException {
//		os.write("  procedure " + ProcedureName + "(\n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			int index = c + 1;
//			String label = mdata.getColumnLabel(c + 1).toLowerCase();
//			genPlSqlParm(os, label, getType(mdata, index));
//			if (c < mdata.getColumnCount() - 1) {
//				os.write(",\n");
//			} else {
//				os.write("\n");
//			}
//		}
//		os.write("  )");
//	}
//
//	private void genPlSqlParm(BufferedWriter os, String label, int columnType)
//			throws java.io.IOException {
//		String plsqlType = "";
//		switch (columnType) {
//		case INT:
//			plsqlType = "number";
//			break;
//		case STR:
//			plsqlType = "varchar2";
//			break;
//		case LONG:
//			plsqlType = "number";
//			break;
//		case DOUBLE:
//			plsqlType = "number";
//			break;
//		case DATE:
//			plsqlType = "date";
//			break;
//		}
//		os.write("     p_" + label + " " + plsqlType);
//	}
//
//	private void genPlSqlSet(BufferedWriter os, String label, int columnType)
//			throws java.io.IOException {
//		String plsqlType = "";
//		StringBuffer buff = new StringBuffer();
//		String text;
//		switch (columnType) {
//		case INT:
//			plsqlType = "number";
//			break;
//		case STR:
//			plsqlType = "varchar2";
//			break;
//		case LONG:
//			plsqlType = "number";
//			break;
//		case DOUBLE:
//			plsqlType = "number";
//			break;
//		case DATE:
//			plsqlType = "date";
//			break;
//		}
//		buff.append("       procedure set_" + label + "(val " + plsqlType
//				+ ") is begin\n");
//		// os.write(" if (" + label + "!=" + label + ")\n");
//		// os.write(" then\n");
//		// os.write(" raise_application_error(-20100,pkg_nm || '" + label + "
//		// value '''|| " + "p_" + label + " is invalid);\n");
//		// os.write(" else\n");
//		buff.append("            my_" + table + "." + label + " := val;\n");
//		// os.write(" end if;\n");
//		buff.append("       end set_" + label + ";\n\n");
//		text = new String(buff);
//		os.write(text);
//	}
//
//	private void genObfuscationAccessors(BufferedWriter out)
//			throws java.io.IOException {
//		out.write(tab + "public String getObfuscatedPrimaryKey()\n");
//		out.write(tab + "{\n");
//		out.write(tab + tab + "return obfuscatedPrimaryKey;");
//		out.write(tab + "}\n");
//		out.write("\n");
//		out.write(tab + "public void setObfuscatedPrimaryKey(int val)\n");
//		out.write(tab + "{\n");
//		out.write(tab + tab
//				+ "obfuscatedPrimaryKey =  ObfuscatedPrimaryKey.get(val);\n");
//		out.write(tab + "}\n");
//		out.write(tab + "\n");
//	}
//
//	/**
//	 * generates
//	 */
//	private void genAllAccessors(BufferedWriter os, ResultSetMetaData mdata)
//			throws java.sql.SQLException, java.io.IOException {
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			int index = c + 1;
//			String label = mdata.getColumnLabel(index).toLowerCase();
//			int getType = getType(mdata, index);
//			// System.out.println("Generating accessors " + label + ":" +
//			// getType + "\n");
//			genAccessors(os, label, getType);
//		}
//	}
//
//	private void genAccessors(BufferedWriter os, String _label, int columnType)
//			throws java.io.IOException {
//		String label = null;
//		String getPrefix = "get";
//		String labelUpper = null;
//		String objectType = null;
//		label = StringHelper.attributeName(_label);
//		labelUpper = StringHelper.attributeNameUpper(_label);
//		switch (columnType) {
//		case INT:
//			objectType = "Integer";
//			break;
//		case STR:
//			objectType = "String";
//			break;
//		case LONG:
//			objectType = "Long";
//			break;
//		case DOUBLE:
//			objectType = "Double";
//			break;
//		case DATE:
//			objectType = "java.sql.Timestamp";
//			break;
//		case CLOB:
//			objectType = "String";
//			break;
//		case NUMBER:
//			objectType = "java.math.BigDecimal";
//			break;
//		case VARBINARY:
//			objectType = "byte []";
//			break;
//		}
//		// System.out.println("GenAccessors: " + label + " primitive? " +
//		// primitive + "\n");
//		// generate set
//		os.write("    /** Accessor set method for " + labelUpper
//				+ " no validation provided in base method. */\n");
//		os.write("    public void set" + labelUpper + "(" + objectType
//				+ " val) {\n");
//		os.write("        " + label + "=val;\n");
//		os.write("    }\n");
//		os.write("\n");
//		// generate get
//		os.write("/** Accessor get method for " + labelUpper + ".  */\n");
//		os.write("    public " + objectType + " " + getPrefix + labelUpper
//				+ "() {\n");
//		os.write("        return " + label + ";\n");
//		os.write("    }\n");
//		os.write("\n");
//	}
//
//	// private String tagName(String columnName) {
//	// return tagName(columnName, objectFormat);
//	// }
//	/**
//	 * Create the tag for clearText html refererences by prepending with a 'c'.
//	 */
//	public static String tagName(String columnName) {
//		return "c" + StringHelper.attributeNameUpper(columnName);
//	}
//
//	public static String ClassName(String columnName) {
//		String rc = null;
//		rc = StringHelper.attributeName(columnName);
//		rc = rc.substring(0, 1).toUpperCase() + rc.substring(1);
//		return rc;
//	}
//
//	/**
//	 * generates getRow function which gets one row from the resultset
//	 */
//	private void genGetRowByName(Writer out, String className)
//			throws java.io.IOException, java.sql.SQLException {
//		out.write("    protected static void getRow(ResultSet rset, "
//				+ className + " row)\n");
//		out.write("    throws java.sql.SQLException\n");
//		out.write("    {\n");
//		out.write("        String columnName = null;\n\n");
//		out.write("        try {\n");
//		for (int colNum = 1; colNum <= mdata.getColumnCount(); colNum++) {
//			String label = mdata.getColumnLabel(colNum).toLowerCase();
//			String primitive = null;
//			String object = null;
//			String getMethod = null;
//			int type = getType(mdata, colNum);
//			// logger.info(">>>>>>>>>>>>>>>>type is " + type);
//			switch (type) {
//			case INT:
//				primitive = "int";
//				object = "Integer";
//				getMethod = "getInt";
//				logger.info("getMethod is getInt");
//				break;
//			case LONG:
//				primitive = "long";
//				object = "Long";
//				getMethod = "getLong";
//				break;
//			case DOUBLE:
//				primitive = "double";
//				object = "Double";
//				getMethod = "getDouble";
//				break;
//			case STR:
//				getMethod = "getString";
//				break;
//			case DATE:
//				getMethod = "getTimestamp";
//				break;
//			/*
//			 * case CLOB: String clobName = "clob" +
//			 * sh.attributeNameUpper(label); out.write(" Clob " + clobName + " =
//			 * rset.getClob(columnName = \"" + label + "\");\n");
//			 * out.write(" String " + sh.attributeNameUpper(label) + " = " +
//			 * clobName + ".getSubString((long)0,(int)" + clobName +
//			 * ".length());"); out.write("\n"); break;
//			 */
//			case VARBINARY:
//				getMethod = "getBytes";
//				break;
//			case NUMBER:
//				getMethod = "getBigDecimal";
//				out.write("\n");
//				break;
//			default:
//				logger.info(label + " is an unsupported variable type (type: "
//						+ type + " )");
//			}
//			if (primitive != null) {
//				out.write("        " + primitive + " "
//						+ StringHelper.attributeNameUpper(label) + " = rset."
//						+ getMethod + "(columnName = \"" + label + "\");\n");
//				out.write("        row.set"
//						+ StringHelper.attributeNameUpper(label)
//						+ "(rset.wasNull() ? null : new " + object + "("
//						+ StringHelper.attributeNameUpper(label) + "));\n");
//			} else {
//				out.write("        row.set"
//						+ StringHelper.attributeNameUpper(label) + "(rset."
//						+ getMethod + "(columnName = \"" + label + "\"));\n");
//			}
//		}
//		out.write("        }\n");
//		out.write("        catch (java.sql.SQLException s) {\n");
//		out.write("            throw new java.sql.SQLException(\"error processing column\" + columnName + \"\\n\" + s.getMessage());\n");
//		out.write("        }\n");
//		out.write("    } // end of getRow \n");
//		out.write("\n");
//	} // try
//
//	/**
//	 * Generate getRowByIndexFunction function which gets one row from the
//	 * ResultSet and retrieves the columns using indexes rather than column
//	 * names.
//	 */
//	/**
//	 * JspGenerators
//	 */
//	private void genJspMaint() throws java.sql.SQLException,
//			java.io.IOException {
//		String fileName = JspMaintName;
//		File f1 = new File(fileName);
//		logger.info(">>>Writing to " + fileName);
//		BufferedWriter os = new BufferedWriter(new FileWriter(f1));
//		genJspHead(os);
//		genJspHtmlHead(os);
//		os.write("<body>\n");
//		String DbObjectName = genInstantiateObject(os);
//		genJspOpenForm(os, className + "Insert.jsp");
//		genJspCols(os, DbObjectName);
//		genJspAddButton(os);
//		os.write("\n</form>\n");
//		os.write("</body>\n");
//		os.write("</html>\n");
//		os.close();
//	}
//
//	/**
//	 */
//	private String genInstantiateObject(BufferedWriter os)
//			throws java.io.IOException {
//		String x = ClassNameImplementation.substring(0, 1).toLowerCase();
//		String y = ClassNameImplementation.substring(1);
//		String objectName = x + y; // first character is lower case
//		os.write("<%" + ClassNameImplementation + " " + objectName + " = new "
//				+ ClassNameImplementation + "(); %>\n");
//		return objectName;
//	}
//
//	/**
//	 */
//	private void genJspAddButton(BufferedWriter out) throws java.io.IOException {
//		out.write("<input type=\"submit\" name=\"Add\" value=\"Add\">");
//	}
//
//	/**
//	 * generate jsp declarations
//	 */
//	private void genJspHead(BufferedWriter os) throws java.io.IOException {
//		os.write("<%!-- ensure user is logged in %>\n");
//		os.write("<%@ include file=\"/jspLet/LoginValidate.jsp\"%>\n");
//		os.write("<!doctype HTML PUBLIC \"-//IETF//DTD HTML//EN\">\n");
//		os.write("<%@ page import=\"com.javautil.oracle.*\" %>\n");
//		os.write("<%@ page import=\"java.sql.*\" %>\n");
//		os.write("<%@ page import=\"com.javautil.text.* \"%>\n");
//		os.write("<%@ page import=\"com.javautil.util.*\" %>\n");
//		os.write("<%@ page import=\"" + packageName + ".*\" %>\n");
//	}
//
//	/**
//	 * generate all of the html up to the </head> tag
//	 */
//	private void genJspHtmlHead(BufferedWriter os) throws java.io.IOException {
//		os.write("<html>\n");
//		os.write("<head>\n");
//		os.write("<meta http-equiv=\"Content-Type\"\n");
//		os.write("content=\"text/html; charset=iso-8859-1\">\n");
//		os.write("<title>Default Title</title>\n");
//		os.write("<link rel=\"stylesheet\" href=\"StyleSheets/print.css\" media=\"print\">");
//		os.write("<link rel=\"stylesheet\" href=\"StyleSheets/screen.css\" media=\"screen\">");
//		os.write("</head>\n");
//	}
//
//	/*
//	 * generate a row in the maintenance table and populate with the columns for
//	 * the label of the data and the text box
//	 */
//	private void genJspOpenForm(BufferedWriter os, String DbAddUrl)
//			throws java.io.IOException {
//		os.write("<form action=\"" + DbAddUrl + "\" method=\"post\">\n");
//	}
//
//	/*
//	 * generate a row in the maintenance table and populate with the columns for
//	 * the label of the data and the text box
//	 */
//	private void genJspCols(BufferedWriter os, String DbObjectName)
//			throws java.sql.SQLException, java.io.IOException {
//		os.write("<table>\n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			int index = c + 1;
//			int displaySize = mdata.getColumnDisplaySize(index);
//			String label = mdata.getColumnLabel(c + 1).toLowerCase();
//			os.write("<tr>\n");
//			genJspPairs(os, label, getType(mdata, index), DbObjectName,
//					displaySize);
//			os.write("</tr>\n");
//		}
//		os.write("</table>\n");
//	}
//
//	/**
//	 * generate two columns in the table, the field name and the text box for
//	 * maintaining it
//	 */
//	private void genJspPairs(BufferedWriter os, String label, int columnType,
//			String DbObjectName, int displaySize) throws java.io.IOException {
//		String tag = tagName(label);
//		StringBuffer buff = new StringBuffer();
//		String text;
//		//
//		// stub for later, create different field options based on type
//		switch (columnType) {
//		case INT:
//			break;
//		case STR:
//			break;
//		case LONG:
//			break;
//		case DOUBLE:
//			break;
//		case DATE:
//			break;
//		}
//		String labelUpper = StringHelper.attributeNameUpper(label);
//		buff.append("  <td align='left'>" + label + "</td>\n");
//		buff.append("  <td><input ");
//		buff.append("  type='text' name='" + tag + "' id='" + tag + "'");
//		buff.append("  size='" + displaySize + "' maxlength='" + displaySize
//				+ "'");
//		buff.append(" value='<%=" + DbObjectName + ".get" + labelUpper
//				+ "() %>'" + "></td>\n");
//		text = new String(buff);
//		os.write(text);
//	}
//
//	private void genPopFromHtmlForm(BufferedWriter os)
//			throws java.sql.SQLException, java.io.IOException {
//		// os.write("/*\n");
//		os.write("public void popFromHtmlForm(HttpServletRequest req)\n");
//		os.write("{\n");
//		os.write(tab + "String parm;");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			int index = c + 1;
//			String label = mdata.getColumnLabel(c + 1).toLowerCase();
//			genPopFromHtmlField(os, label, getType(mdata, index));
//		}
//		os.write("}\n");
//		// os.write("*/\n");
//	}
//
//	private void genPopFromHtmlField(BufferedWriter os, String label,
//			int columnType) throws java.io.IOException {
//		os.write(tab + "parm = req.getParameter(c"
//				+ StringHelper.attributeNameUpper(label) + ");\n");
//		os.write(tab + "if (parm != null && !parm.equals(\"\")) { \n");
//		switch (columnType) {
//		case INT:
//			os.write(tab + tab + StringHelper.attributeName(label)
//					+ " = new Integer(parm);\n");
//			break;
//		case STR:
//			os.write(tab + tab + StringHelper.attributeName(label)
//					+ " = req.getParameter(parm);\n");
//			break;
//		case LONG:
//			os.write(tab + tab + StringHelper.attributeName(label)
//					+ " = new Long(parm);\n");
//			break;
//		case DOUBLE:
//			os.write(tab + tab + StringHelper.attributeName(label)
//					+ " = new Double(parm);\n");
//			break;
//		case DATE:
//			os.write(tab
//					+ tab
//					+ StringHelper.attributeName(label)
//					+ " = DateHelper.toSqlTimestamp(new java.util.Date(parm));\n");
//			break;
//		default:
//			break;
//		}
//		os.write("\n" + tab + "}\n");
//	}
//
//	//
//	private ResultSetMetaData getMetaDataTable(OracleConnection conn,
//			String table) {
//		ResultSetMetaData mdata = null;
//		String stmtString = null;
//		try {
//			stmtString = "select * from " + table + " where 1=2";
//			PreparedStatement stmt = conn.prepareStatement(stmtString);
//			mdata = stmt.getMetaData();
//		} catch (Exception e) {
//			logger.info("Error Parsing Statement: " + stmtString);
//			e.printStackTrace();
//			System.exit(1);
//		}
//		return mdata;
//	}
//
//	// should be in com.javautil.text
//	// private String[] split(String inString, String separator) {
//	// StringTokenizer toke = new StringTokenizer(inString, separator);
//	// String strings[] = new String[toke.countTokens()];
//	// for (int i = 0; toke.hasMoreTokens(); i++) {
//	// strings[i] = toke.nextToken();
//	// }
//	// return strings;
//	// }
//
//	/** generate the string used for bind function argument declaration */
//	// private String getBindDeclaration() {
//	// return getBind(true);
//	// }
//
//	/** generate the string used for bind function argument declaration */
//	// private String getBindInvocation() {
//	// return getBind(false);
//	// }
//
//	// generate the bind parameters for bind function declaration and invocation
//	//
//	// private String getBind(boolean emitType) {
//	// String rc = "(";
//	// for (int c = 0; c < bindVariables.length; c++) {
//	// for (int c1 = 0; c1 < c; c1++) {
//	// if (!bindVariables[c1].varName.equals(bindVariables[c].varName)) {
//	// if (c > 0) {
//	// rc += ",";
//	// }
//	// if (emitType) {
//	// rc += bindVariables[c].javaType;
//	// }
//	// rc += bindVariables[c].varName;
//	// }
//	// }
//	// rc += ")";
//	// }
//	// return rc;
//	// }
//
//	/**
//	 * Generate the persistence class, which gets the data into and out of the
//	 * persistent store
//	 */
//	private void genBasePersistence() throws java.io.IOException,
//			java.sql.SQLException {
//		logger.info("genBasePersistence className = " + ClassNameBase + "\n");
//		String fileName = null;
//		if (ClassNameBase == null || ClassNameBase.length() == 0) {
//			throw new java.lang.IllegalStateException("ClassNameBase = "
//					+ ClassNameBase);
//		}
//		fileName = ClassNameBase + "S.java";
//		String fName = sourceCodeDirectory + "/" + fileName;
//		File f = null;
//		logger.info("Generating " + fName + "\n");
//		try {
//			f = new File(fName);
//			bwPersist = new BufferedWriter(new FileWriter(f));
//		} catch (Exception e) {
//			logger.info("Unable to open " + fileName);
//		}
//		logger.info("Writing persistence to " + fileName);
//		mdata = getMetaDataTable(dbc, table);
//		// packages and imports
//		bwPersist.write("package " + packageName + ";\n\n");
//		bwPersist.write("import java.util.*;\n");
//		bwPersist.write("import com.javautil.util.*;\n");
//		bwPersist.write("import com.javautil.oracle.*;\n\n");
//		bwPersist.write("import java.text.*;\n");
//		bwPersist.write("import java.io.*;\n");
//		bwPersist.write("import java.sql.*;\n");
//		bwPersist.write("import javax.servlet.*;\n");
//		bwPersist.write("import javax.servlet.http.*;\n");
//		bwPersist.write("\n");
//		// class declaration
//		bwPersist.write("public class " + ClassNameBase + "S\n");
//		bwPersist.write("{\n");
//		// generate Containers
//		bwPersist.write("    /**\n");
//		bwPersist
//				.write("     *  Container for rows retrieved from fetches in fetched sequence.\n");
//		bwPersist.write("     */\n");
//		bwPersist.write("    ArrayList rows = new ArrayList();\n");
//		bwPersist.write("    /**\n");
//		bwPersist.write("     *   HashMap based on obfuscated primary key.\n");
//		bwPersist.write("     */\n");
//		bwPersist.write("    HashMap   map = new HashMap();\n");
//		bwPersist.write("    /**\n");
//		bwPersist
//				.write("     *   Maintain persistent connection true, connection pool safe, true.");
//		bwPersist.write("     */\n");
//		bwPersist.write("    boolean persistConnection = true;\n");
//		bwPersist.write("\n");
//		bwPersist.write("    ResultSet rset = null;\n");
//		bwPersist.write("    PreparedStatement selectStmt = null;\n");
//		bwPersist.write("    static final String selectText = "
//				+ getSelectStatement() + "\n");
//		if (generateInsert) {
//			bwPersist.write("    PreparedStatement insertStmt = null;\n");
//		}
//		if (generateInsert) {
//			bwPersist.write("    /**\n");
//			bwPersist
//					.write("    * sql text for inserting all rows into the table\n");
//			bwPersist.write("    */\n");
//			bwPersist.write("    static String insertText = "
//					+ getInsertStatement() + "\n");
//		}
//		genPersistConstructor(bwPersist);
//		genGetRowByName(bwPersist, ClassNameImplementation);
//		// genGetRowByIndex(bwPersist, ClassNameImplementation);
//		generateClear(bwPersist);
//		generateAdd(bwPersist);
//		generateConnectionPersistenceMethods(bwPersist);
//		generatePersistenceContainerToXml(bwPersist);
//		generateSize(bwPersist);
//		if (generateInsert) {
//			generateInsertAll(bwPersist);
//			bwPersist.write(getInsertRow());
//		}
//		generateIterator(bwPersist);
//		bwPersist.write("}  // end of class\n");
//		bwPersist.close();
//	}
//
//	/**
//	 * Generate the persistence class, which gets the data into and out of the
//	 * persistent store
//	 */
//	public String getBasePersistence(Connection conn, String sqlText,
//			ResultSetMetaData mdata) throws java.sql.SQLException {
//		StringWriter bwPersist = new StringWriter();
//		// packages and imports
//		bwPersist.write("package " + packageName + ";\n\n");
//		bwPersist.write("import java.util.*;\n");
//		bwPersist.write("import com.javautil.util.*;\n");
//		// bwPersist.write("import com.javautil.oracle.*;\n\n");
//		bwPersist.write("import java.text.*;\n");
//		bwPersist.write("import java.io.*;\n");
//		bwPersist.write("import java.sql.*;\n");
//
//		bwPersist.write("\n");
//		// class declaration
//		bwPersist.write("public class " + ClassNameBase + "S\n");
//		bwPersist.write("{\n");
//		// generate Containers
//		bwPersist.write("    /**\n");
//		bwPersist
//				.write("     *  Container for rows retrieved from fetches in fetched sequence.\n");
//		bwPersist.write("     */\n");
//		bwPersist.write("    private ArrayList rows = new ArrayList();\n");
//
//		bwPersist.write("    private ResultSet rset = null;\n");
//		bwPersist.write("    private PreparedStatement selectStmt = null;\n");
//		bwPersist.write("    static final String selectText = "
//				+ getSelectStatement() + "\n");
//		if (generateInsert) {
//			bwPersist.write("    PreparedStatement insertStmt = null;\n");
//		}
//		if (generateInsert) {
//			bwPersist.write("    /**\n");
//			bwPersist
//					.write("    * sql text for inserting all rows into the table\n");
//			bwPersist.write("    */\n");
//			bwPersist.write("    static String insertText = "
//					+ getInsertStatement() + "\n");
//		}
//		try {
//			genPersistConstructor(bwPersist);
//			genGetRowByName(bwPersist, ClassNameImplementation);
//			// genGetRowByIndex(bwPersist, ClassNameImplementation);
//			generateClear(bwPersist);
//			generateAdd(bwPersist);
//			generateConnectionPersistenceMethods(bwPersist);
//			generatePersistenceContainerToXml(bwPersist);
//			generateSize(bwPersist);
//			if (generateInsert) {
//				generateInsertAll(bwPersist);
//				bwPersist.write(getInsertRow());
//			}
//			generateIterator(bwPersist);
//			bwPersist.write("}  // end of class\n");
//			String code = bwPersist.toString();
//			bwPersist.close();
//		} catch (IOException e) {
//			throw new IllegalStateException(e);
//		}
//
//		return bwPersist.toString();
//	}
//
//	private void genPersistConstructor(Writer out) throws java.io.IOException {
//		out.write("    /** Default constructor for " + ClassNameBase + ". */\n");
//		out.write("    public " + ClassNameBase + "S() {\n");
//		out.write("    }\n\n");
//	}
//
//	private String getSelectStatement() throws java.sql.SQLException {
//		StringBuffer stmtText = new StringBuffer();
//		stmtText.append("\"\" +\n");
//		stmtText.append("         \"SELECT\\n\" +\n");
//		// now the columns
//		for (int colNum = 1; colNum <= mdata.getColumnCount(); colNum++) {
//			String label = mdata.getColumnLabel(colNum).toLowerCase();
//			stmtText.append("        \"    " + label);
//			if (colNum < mdata.getColumnCount()) {
//				stmtText.append(",");
//			}
//			stmtText.append("\\n\" +\n");
//		}
//		stmtText.append("        \"FROM " + table + "\\n\";\n\n");
//		return new String(stmtText);
//	}
//
//	// private void generateFetchStub(BufferedWriter os) throws
//	// java.io.IOException {
//	// os.write("\n");
//	// os.write("   /**\n");
//	// os.write("    * A template for connection pool safe single call.\n");
//	// os.write("    */\n");
//	// os.write("    /*\n");
//	// os.write("    public void getForPk(DbConnector dbc, Integer pk)\n");
//	// os.write("    throws java.sql.SQLException {\n");
//	// os.write("       String stmtText = selectText + \" where primaryKey = ?\";\n");
//	// os.write("       PreparedStatement stmt = null;\n");
//	// os.write("       ResultSet rset=null;\n");
//	// os.write("       clear();\n");
//	// os.write("       try {\n");
//	// os.write("          stmt = dbc.prepareStatement(stmtText);\n");
//	// os.write("          stmt.setInt(1,pk.intValue());\n");
//	// os.write("          rset = stmt.executeQuery();\n");
//	// os.write("          while (rset.next()) {\n");
//	// os.write("           " + ClassNameImplementation + " row = new " +
//	// ClassNameImplementation + "();\n");
//	// os.write("             getRow(rset, row);\n");
//	// os.write("             add(row);\n");
//	// os.write("          }\n ");
//	// os.write("       } catch (java.sql.SQLException s) {\n");
//	// os.write("          throw new java.sql.SQLException(s.getMessage() + \"\\nwhile processing\\n\" + stmtText);\n");
//	// os.write("       } finally { \n");
//	// os.write("         	if (stmt != null) { \n");
//	// os.write("                 stmt.close();\n");
//	// os.write("           }\n");
//	// os.write("       }\n");
//	// os.write("    }\n");
//	// os.write("    */\n");
//	// }
//
//	// private void generateGetAllStub(BufferedWriter os) throws
//	// java.io.IOException {
//	// os.write("\n");
//	// os.write("   /**\n");
//	// os.write("    * A template for connection pool safe call for fetching all records.\n");
//	// os.write("    */\n");
//	// os.write("    public void getAll(DbConnector dbc)\n");
//	// os.write("    throws java.sql.SQLException {\n");
//	// os.write("       String stmtText = selectText ;\n");
//	// os.write("       PreparedStatement stmt = null;\n");
//	// os.write("       ResultSet rset=null;\n");
//	// os.write("       clear();\n");
//	// os.write("       try {\n");
//	// os.write("          stmt = dbc.prepareStatement(stmtText);\n");
//	// os.write("          rset = stmt.executeQuery();\n");
//	// os.write("          while (rset.next()) {\n");
//	// os.write("           " + ClassNameImplementation + " row = new " +
//	// ClassNameImplementation + "();\n");
//	// os.write("             getRow(rset, row);\n");
//	// os.write("             add(row);\n");
//	// os.write("          }\n ");
//	// os.write("       } catch (java.sql.SQLException s) {\n");
//	// os.write("          throw new java.sql.SQLException(s.getMessage() + \"\\nwhile processing\\n\" + stmtText);\n");
//	// os.write("       } finally { \n");
//	// os.write("         	if (stmt != null) { \n");
//	// os.write("                 stmt.close();\n");
//	// os.write("           }\n");
//	// os.write("       }\n");
//	// os.write("    }\n");
//	// }
//
//	private void generateClear(Writer os) throws java.io.IOException {
//		os.write("    public void clear() {\n");
//		os.write("       rows.clear();\n");
//		os.write("    }\n");
//	}
//
//	private void generateAdd(Writer os) throws java.io.IOException {
//		os.write("    public void add(" + ClassNameBase + " row) {\n");
//		os.write("       rows.add(row);\n");
//		os.write("    }\n");
//	}
//
//	private void generateConnectionPersistenceMethods(Writer os)
//			throws java.io.IOException {
//		os.write("    public void connectionPersistenceBegin() {\n");
//		os.write("       persistConnection = true;\n");
//		os.write("    }\n");
//		os.write("\n");
//		os.write("    public void connectionPersistenceEnd()\n");
//		os.write("    throws java.sql.SQLException\n");
//		os.write("    {\n");
//		os.write("       persistConnection = false;\n");
//		if (generateInsert) {
//			os.write("       if (insertStmt != null) {\n");
//			os.write("          insertStmt.close();\n");
//			os.write("          insertStmt = null;\n");
//			os.write("       }\n");
//		}
//		os.write("       if (selectStmt != null) {\n");
//		os.write("            selectStmt.close();\n");
//		os.write("            selectStmt = null;\n");
//		os.write("       }\n");
//		os.write("    }\n");
//	}
//
//	private void generatePersistenceContainerToXml(Writer os)
//			throws java.io.IOException {
//		os.write("\n");
//		os.write("/** Return all of the tuples in regularly formed xml. */\n");
//		os.write("    public String toXml() {\n");
//		os.write("        StringBuffer buff = new StringBuffer(1024);\n");
//		os.write("        Iterator it = rows.iterator();\n");
//		os.write("        buff.append(\"<" + ClassNameImplementation
//				+ "S>\");\n");
//		os.write("        while (it.hasNext()) { \n");
//		os.write("            " + ClassNameImplementation + " tuple = ("
//				+ ClassNameImplementation + ") it.next();\n");
//		os.write("            buff.append(tuple.toXml());\n");
//		os.write("        }\n");
//		os.write("        buff.append(\"</" + ClassNameImplementation
//				+ "S>\");\n");
//		os.write("        return new String(buff);\n");
//		os.write("    }\n");
//	}
//
//	private void generateInsertAll(Writer os) throws java.io.IOException {
//		os.write("\n");
//		os.write("    /** Insert all tuples into persistent store.*/\n");
//		os.write("    public void insertAll(DbConnector dbc)\n");
//		os.write("    throws java.sql.SQLException\n");
//		os.write("    {\n");
//		os.write("        Iterator it = rows.iterator();\n");
//		os.write("        while (it.hasNext()) {\n");
//		os.write("             " + ClassNameImplementation + " row = ("
//				+ ClassNameImplementation + ") it.next();\n");
//		os.write("             insertRow(row,dbc);\n");
//		os.write("        }\n");
//		os.write("    }\n");
//	}
//
//	private void generateSize(Writer os) throws java.io.IOException {
//		os.write("\n");
//		os.write("    /** Return the number of the rows contained. */\n");
//		os.write("    public int size() {\n");
//		os.write("        return rows.size();\n");
//		os.write("    }\n");
//	}
//
//	private void generateIterator(Writer os) throws java.io.IOException {
//		os.write("\n");
//		os.write("    /** Return the rows iterator. */\n");
//		os.write("    public Iterator iterator() { \n");
//		os.write("        return rows.iterator();\n");
//		os.write("    }\n");
//	}
//
//	// private String getShowInsertValues() throws java.sql.SQLException {
//	// StringBuffer buff = new StringBuffer();
//	// buff.append("public void showInsertValues(");
//	// buff.append(ClassNameBase);
//	// buff.append(" row)\n");
//	// buff.append("{\n");
//	// for (int c = 0; c < mdata.getColumnCount(); c++) {
//	// String label = StringHelper.attributeName(mdata.getColumnLabel(c +
//	// 1).toLowerCase());
//	// buff.append("          System.out.println(\"" + label + "=\" + row." +
//	// label + ");\n");
//	// }
//	// buff.append("}\n");
//	// return new String(buff);
//	// }
//
//	private String getToXml() throws java.sql.SQLException {
//		StringBuffer buff = new StringBuffer();
//		buff.append("    public String toXml() {\n");
//		buff.append("       StringBuffer buff = new StringBuffer();\n");
//		for (int c = 0; c < mdata.getColumnCount(); c++) {
//			String label = StringHelper.attributeName(mdata.getColumnLabel(
//					c + 1).toLowerCase());
//			buff.append("       buff.append(\"<" + label + ">\");\n");
//			buff.append("       buff.append(" + label + ");\n");
//			buff.append("       buff.append(\"</" + label + ">\");\n");
//		}
//		buff.append("        return new String(buff);\n");
//		buff.append("    }\n");
//		return new String(buff);
//	}
//
//	public void getHttpRequest(HttpServletRequest request) {
//		packageName = request.getParameter(packageNameTag);
//		ClassNameBase = request.getParameter(classNameBaseTag);
//		ClassNameImplementation = request
//				.getParameter(classNameImplementationTag);
//	}
//
//	private String getSourceDirectory(String destinationDirectory,
//			String packageName) {
//		StringBuffer buff = new StringBuffer();
//		String returnValue = null;
//		File destinationRoot = new File(destinationDirectory);
//		if (!destinationRoot.exists()) {
//			throw new java.lang.IllegalArgumentException("no such directory '"
//					+ destinationDirectory + "'");
//		}
//		buff.append(destinationDirectory);
//		String[] directories = packageName.split("\\.");
//		for (String component : directories) {
//			buff.append("/");
//			buff.append(component);
//		}
//		returnValue = buff.toString();
//		File dir = new File(returnValue);
//		dir.mkdirs();
//		return returnValue;
//	}
//
//	public static void main(String args[]) {
//		JavaGen jg = null;
//		logger.info("JavaGen Version 0.1");
//		try {
//			if (args.length == 1) {
//				jg = new JavaGen(args[0]);
//			}
//		} catch (Exception e) {
//			logger.info(e.getMessage());
//			e.printStackTrace();
//			logger.info("terminating");
//			System.exit(1);
//		}
//		try {
//			jg.init();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//		// jg.objectFormat = true;
//		logger.info("mode = " + jg.mode);
//		try {
//			if (jg.mode.equals("select")) {
//				logger.info("in main sqlFileName = " + jg.sqlFileName);
//				// jg.genSelect();
//			}
//			jg.genBaseRow();
//			jg.genBasePersistence();
//			// if (jg.genImplementationArg.equals("true")) {
//			// jg.genImplementationRow();
//			// jg.genImplementationPersistence();
//			// }
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//		try {
//			jg.genPlsqlPackage();
//			jg.genJspMaint();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//	}
// }
