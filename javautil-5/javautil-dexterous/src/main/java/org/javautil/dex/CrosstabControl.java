package org.javautil.dex;

import java.util.ArrayList;


public class CrosstabControl {

	public static final String					revision				= "$Revision: 1.1 $";


	private boolean								isCrossTab				= false;

	private ArrayList<String>					crossTabColumns			= null;

	private ArrayList<String>					crossTabRows			= null;

	private ArrayList<String>					crossTabValues			= null;


	/**
	 * @todo comment
	 * Parses out directives.
	 *
	 * set crosstab columns ...
	 * set crosstab rows ...
	 * set crosstab values ...
	 * @param directive
	 */
    public void addDirective(final String directive) {
    	if (directive == null) {
    		throw new IllegalArgumentException("null directive");
    	}
    	final String[] tokens = directive.split(" ");
    	int tokenNbr = 0;
    	if (tokens[tokenNbr].toUpperCase().equals("REM")) {
    		tokenNbr++;
    	}
    	if (tokens[tokenNbr].toUpperCase().equalsIgnoreCase("SET")) {
    		tokenNbr++;
    	} else {
    		throw new IllegalArgumentException("must start with 'REM SET or SET (case insensitive)");
    	}
    	final String token = tokens[tokenNbr].toUpperCase();
    	while (true) {
			if (token.equals("COLUMN")) {
				final ArrayList<String> columnNames = new ArrayList<String>();
				for (int i = 3; i < tokens.length; i++) {
					columnNames.add(tokens[tokenNbr++]);
				}
				crossTabColumns = columnNames;
				break;
			}
			if (token.equals("ROW")) {
				final ArrayList<String> columnNames = new ArrayList<String>();
				for (int i = 3; i < tokens.length; i++) {
					columnNames.add(tokens[tokenNbr++]);
				}
				crossTabRows = columnNames;
				break;

			}
			if (token.equals("VALUE")) {
				final ArrayList<String> columnNames = new ArrayList<String>();
				for (int i = 3; i < tokens.length; i++) {
					columnNames.add(tokens[tokenNbr++]);
				}
				crossTabValues = columnNames;
				break;
			}
			if (token.equals("OFF")) {
				isCrossTab = false;
				crossTabRows = null;
				crossTabColumns = null;
				crossTabValues = null;
				break;
			}
			throw new IllegalArgumentException("unknown set argument " + token + " must be 'COLUMN', 'ROW', 'VALUE', 'OFF'");
    }


    }

	public ArrayList<String> getCrossTabColumns() {
		if (crossTabColumns == null) {
			throw new IllegalStateException("crossTabColumns has not been set");
		}
		return crossTabColumns;
	}

	public ArrayList<String> getCrossTabRows() {
		if (crossTabRows == null) {
			throw new IllegalStateException("crossTabRows has not been set");
		}
		return crossTabRows;
	}

	public ArrayList<String> getCrossTabValues() {
		if (crossTabValues == null) {
			throw new IllegalStateException("crossTabValues has not been set");
		}
		return crossTabValues;
	}

	public boolean isCrossTab() {
		return isCrossTab;
	}

	public void setCrossTabColumns(final ArrayList<String> crosstabColumns) {
		if (crosstabColumns == null) {
			throw new IllegalArgumentException("crossTabColumns is null");
		}
		if (crosstabColumns.size() < 1) {
			throw new IllegalArgumentException("crosstabColumns is empty");
		}
		this.crossTabColumns = crosstabColumns;
	}

	public void setCrossTabRows(final ArrayList<String> crossTabRows) {
		this.crossTabRows = crossTabRows;
	}

	public void setCrossTabValues(final ArrayList<String> crossTabValues) {
		this.crossTabValues = crossTabValues;
	}

//	public void setCrossTab(boolean isCrossTab) {
//		this.isCrossTab = isCrossTab;
//	}




}
