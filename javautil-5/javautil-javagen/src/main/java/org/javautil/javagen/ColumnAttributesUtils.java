package org.javautil.javagen;

import java.util.Collection;
import java.util.TreeSet;

import org.javautil.dataset.ColumnAttributes;
import org.javautil.jdbc.JdbcJavaNameImpl;
import org.javautil.jdbc.JdbcToJavaImpl;
import org.javautil.jdbc.JdbcToJavaMapper;

public class ColumnAttributesUtils {

	private final JdbcToJavaMapper mapper = new JdbcToJavaImpl();
	private final JdbcJavaNameImpl namer = new JdbcJavaNameImpl();

	public String getImports(Collection<ColumnAttributes> columns) {
		StringBuilder sb = new StringBuilder();
		for (String toImport : generateNeededImports(columns)) {
			sb.append("import ");
			sb.append(toImport);
			sb.append(";\n");
		}
		String imports = sb.toString();
		return imports;
	}

	public Collection<String> generateNeededImports(
			Collection<ColumnAttributes> columns) {
		TreeSet<String> neededImports = new TreeSet<String>();
		for (ColumnAttributes column : columns) {
			String className = mapper.getImportClass(column);
			if (className != null) {
				neededImports.add(className);
			}
		}
		return neededImports;
	}

}
