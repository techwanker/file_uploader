package org.javautil.javagen;

import java.io.BufferedWriter;
import java.io.IOException;

import org.javautil.file.FileHelper;
import org.javautil.jdbc.metadata.Table;

public class BaseRowGenerator {

	private final String directoryName;
	private final String packageName;
	private final String rowClassName;
	private final Table columns;
	private final String persistenceClassName;
	private final String persistencePackageName;

	public BaseRowGenerator(String directoryName, String packageName,
			String rowClassName, Table columns, String persistenceClassName,
			String persistencePackageName) {
		this.directoryName = directoryName;
		this.packageName = packageName;
		this.rowClassName = rowClassName;
		this.columns = columns;
		this.persistenceClassName = persistenceClassName;
		this.persistencePackageName = persistencePackageName;
	}

	public void generate() throws IOException {
		BufferedWriter out = FileHelper.getJavaSourceWriter(directoryName,
				packageName, rowClassName);

		out.write(getClassSource().toString());
		out.close();
	}

	public StringBuffer getClassSource() {
		StringBuffer buff = new StringBuffer(1024 * 8);
		buff.append(getPackageName());
		buff.append(getImports());
		buff.append("\n");
		// class declaration
		buff.append(getClassJavaDoc());
		buff.append("public class " + rowClassName + "\n");
		buff.append("\timplements com.javautil.jdbc.ResultSet.ResultSetPopulable ");
		buff.append("{\n");
		//
		// TODO restore
		// buff.append(columns.getAsJavaAttributes());
		// buff.append(columns.getAccessors());
		buff.append(getSetValues());
		buff.append("}\n");

		return buff;

	}

	private StringBuffer getSetValues() {
		StringBuffer buff = new StringBuffer();
		buff.append("\tpublic void setValues(ResultSet rset) throws SQLException {\n");
		buff.append("\t\t");
		buff.append(persistenceClassName);
		buff.append(".getRow(rset,this);\n");
		buff.append("\t}\n");
		return buff;
	}

	private StringBuffer getPackageName() {
		StringBuffer buff = new StringBuffer();
		buff.append("package ");
		buff.append(packageName);
		buff.append(";\n\n");
		return buff;
	}

	String getImportStatement(String packageName, String className) {
		return "import " + packageName + "." + className + ";";
	}

	private StringBuffer getImports() {
		StringBuffer buff = new StringBuffer();

		buff.append("import java.util.*;\n");
		buff.append("import com.javautil.oracle.*;\n\n");
		// buff.append("import java.text.*;\n");
		// buff.append("import com.javautil.util.*;\n");
		// buff.append("import java.io.*;\n");
		buff.append("import java.sql.*;\n");
		buff.append("import com.javautil.util.DateHelper;\n");
		buff.append(getImportStatement(persistencePackageName,
				persistenceClassName));
		return buff;
	}

	StringBuffer getClassJavaDoc() {
		StringBuffer buff = new StringBuffer(1024);

		buff.append("/**\n");
		buff.append("  * Contains a representation of the data persisted in a tuple of ");
		buff.append(columns.getTableName());
		buff.append("\n");
		buff.append(".\n");
		buff.append("  * This code was generated by ");
		buff.append(this.getClass().getName());
		buff.append(" on ");
		buff.append(new java.util.Date().toString());
		buff.append("\n");
		buff.append("  */\n");
		return buff;
	}

}
