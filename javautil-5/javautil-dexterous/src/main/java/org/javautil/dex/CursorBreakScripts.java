package org.javautil.dex;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

public class CursorBreakScripts {

		private final HashMap<String,HashMap<String,String>> cursorColumnScripts = new HashMap<String,HashMap<String,String>>();
		private final HashMap<String,TreeMap<Integer,String>> cursorIndexColumnNames = new HashMap<String,TreeMap<Integer,String>>();
 		private final HashMap<String,HashMap<String,Integer>> cursorColumnNameIndex = new HashMap<String,HashMap<String,Integer>>();

 		public void add(final String cursorName, final String columnName, final String script) {
 			HashMap<String,String> columnsScripts = cursorColumnScripts.get(cursorName);
 			if (columnsScripts == null) {
 				columnsScripts = new HashMap<String,String>();
 				cursorColumnScripts.put(cursorName, columnsScripts);
 			}
 			columnsScripts.put(columnName,script);
 			//
 			TreeMap<Integer,String> indexColumnNames = cursorIndexColumnNames.get(cursorName);
 			if (indexColumnNames == null) {
 				indexColumnNames = new TreeMap<Integer,String>();
 				cursorIndexColumnNames.put(cursorName, indexColumnNames);
 			}
 			indexColumnNames.put(indexColumnNames.size() + 1, columnName);
 			//
 			HashMap<String,Integer> columnNameIndex = cursorColumnNameIndex.get(cursorName);
 			if (columnNameIndex == null) {
 				columnNameIndex = new HashMap<String,Integer>();
 				cursorColumnNameIndex.put(cursorName,columnNameIndex);
 			}

 			// TODO should add check that the column is not already in the break list.
 			columnNameIndex.put(columnName,indexColumnNames.size());
 		}

 		public Collection<String> getBreakColumns(final String cursorName) {
			final TreeMap<Integer,String> indexColumnNames = cursorIndexColumnNames.get(cursorName);
			final Collection<String>  breakColumns = indexColumnNames.values();
			return breakColumns;
		}

		public String getScript(final String cursorName, final String columnName) {
 			if (cursorName == null) {
 				throw new IllegalArgumentException("cursorName is null");
 			}
 			if (columnName == null) {
 				throw new IllegalArgumentException("columnName is null");
 			}
 			final HashMap<String,String> columnsScripts = cursorColumnScripts.get(cursorName);
 			if (columnsScripts == null) {
 				throw new IllegalArgumentException("cursorName: '" + cursorName + "'" + " has no scripts");
 			}
 			final String script = columnsScripts.get(columnName);
 			if (script == null) {
 				throw new IllegalArgumentException("cursor: '" + cursorName + " has no script for '" + columnName + "'");
 			}
 			return script;
 		}
}
