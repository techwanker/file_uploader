package org.javautil.dex.dexterous.jdbc;



	import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import bsh.NameSpace;
import bsh.UtilEvalError;

import org.javautil.dex.parser.UncheckedParseException;
import org.javautil.jdbc.SelectColumn;
import org.javautil.jdbc.SelectColumns;
import org.javautil.text.StringHelper;

// todo figure out why this still exists

	/**
	 *
	 * //System.out.println("adding bind variable " + var);
	 */
	public class SqlStatement {
	        public final static Pattern tokenFinder = Pattern
	                        .compile("([^\"':]*)(['\":])(.*)");

	        // public final static Pattern bindFinder =
	        // Pattern.compile("([a-zA-Z$#_]*)(.*)");
	        public final static Pattern bindNameFinder = Pattern
	                        .compile("([a-zA-Z$#_]*)(.*)");

	        public final static Pattern quoteEndFinder = Pattern
	                        .compile("([^']*')(.*)");

	        public final static Pattern doubleQuoteEndFinder = Pattern
	                        .compile("([^\"*]\")(.*)");

	        private final String sqlText;

	        // private TreeMap<String,BindVariable> bindVariables = new
	        // TreeMap<String,BindVariable>();
	        // private ArrayList<BindVariable> bindVarsList = new
	        // ArrayList<BindVariable>();
	        private HashMap<String, ArrayList<Integer>> bindVariableNumbers = new HashMap<String, ArrayList<Integer>>();

	        private final SelectColumns columns = new SelectColumns();

	        /**
	         * Log if bind variables are not in the statement.
	         *
	         * May be useful to suppress when the same bind variables are passed for
	         * multiple statements and not all statements have the same bind variables.
	         */
	        // private static boolean logUnusedBindVariables = true;
	        private final Level logUnusedBindVariables = Level.DEBUG;

	        private boolean allowUnbound = false;

	        private final Logger logger = Logger.getLogger(this.getClass().getName());

	        private final boolean logBinds = true;

	        private int bindVariableCount = 0;

	        public SqlStatement(final String s) {
	                sqlText = s;
	                logger.debug("before parse");
	                parseBindVariables();
	                logger.debug("after parse");
	        }

	        /**
	         * Checks the specified values against the statement
	         * <ul>
	         * <li> ensure that every value is a variable specified in the sql.</li>
	         * <li> ensure that no value is duplicated</li>
	         * </ul>
	         * Checks that all variables are bound
	         * <ul>
	         * <li> every bind variable :xxx toke in statement not in quotes must be in
	         * the list of bind values.</li>
	         * </ul>
	         *
	         * @todo add case sensitivity option
	         * @param binds
	         * @throws SQLException
	         * @throws UtilEvalError
	         */
	        public void bind(final PreparedStatement stmt, final NameSpace binds)
	                        throws SQLException {
	                final HashMap<String, Object> upperBinds = new HashMap<String, Object>();
	                final HashMap<String,String>  upperNames = new HashMap<String,String>();

	                for (final String name : binds.getAllNames()) {
	                        Object value;
	                        try {
	                                value = binds.getVariable(name);
	                final String upper = name.toUpperCase();
	                final String prevName = upperNames.put(upper, name);
	                if (prevName != null) {
	                        final String message = "collision of name: '" + prevName + "' with '" + name + "'";
	                        throw new IllegalStateException(message);
	                }

	                        } catch (final UtilEvalError e) {
	                                logger.error("not likely since we were enumerating");
	                                throw new IllegalStateException(e.getMessage() + " while getting '" + name + "'");
	                        }
	                //      logger.error(name + ":" + o.toString() + " " + o.getClass().getName());
	                }


	                // todo should be checking to see what was unbound
	                // report unbound variables
//	              if (!allowUnbound) {
//	                      if (unboundVariables != null) {
//	                              StringBuffer buff = new StringBuffer();
//	                              buff.append("variablesNotBound: ");
//	                              for (String unbound : unboundVariables) {
//	                                      buff.append(unbound);
//	                                      buff.append(" ");
//	                              }
//	                              throw new IllegalArgumentException(buff.toString());
//	                      }
//	              }
//	              if (unboundVariables != null) {
//	                      for (String nm : unboundVariables) {
//	                              logger.debug("binding null " + nm);
//	                              bindNull(stmt, nm);
	//
//	                      }
//	              }
	                // bind values to variables
	                // logger.debug("bind count " + binds.size());
	                for (final String upName : upperBinds.keySet()) {
	                        // logger.info("binding " + bind.getVariableName());
	                        final ArrayList<Integer> bindNumbers = bindVariableNumbers.get(upName);
	                        Object bindValue;
	                        try {
	                                bindValue = binds.getVariable(upName);
	                        } catch (final UtilEvalError e) {
	                                throw new UncheckedParseException(e);
	                        }
	                        if (bindNumbers == null
	                                        && logger.isEnabledFor(logUnusedBindVariables)) {
	                                final String message = "while processing bind variable '" + upName
	                                                + " ' no such bind in statement " + sqlText;
	                                logger.log(logUnusedBindVariables, message);
	                                // logger.warn(message);
	                                // throw new IllegalArgumentException(message);
	                        }
	                        if (bindValue == null) {
	                                bindNull(stmt, upName);
	                        } else {
	                                if (bindNumbers != null) {
	                                        for (final Integer bindNumber : bindNumbers) {
	                                                // logger.debug("getting value object");

	                                                if (logBinds) {

	                                                        logger.debug("binding number " + bindNumber
	                                                                        + " to " + bindValue);

	                                                }

	                                                stmt.setObject(bindNumber, bindValue);
	                                        }
	                                } else {
	                                        if (logBinds) {
	                                                logger.debug("no binds found in statement");
	                                        }
	                                }
	                        }
	                }
	        }

	        public String getJbcStatement() {
	                final StringBuffer buff = new StringBuffer(4096);
	                final String lines[] = StringHelper.split(sqlText, "\n");
	                for (int i = 0; i < lines.length; i++) {
	                        buff.append("\"");
	                        final String line = lines[i].replaceAll("\"", "\\\"");
	                        buff.append(line.trim());
	                        buff.append("\\n\"");
	                        if (i < lines.length - 1) {
	                                buff.append(" + \n");

	                        }
	                }
	                return new String(buff);

	        }

	        // tod is this used anywhere
	        public SelectColumn[] getSelectColumns() {
	                // logger.warn("size " + columns.size());
	                return columns.getSelectColumns();
	        }

	        public void prepareBindExecute(final Connection conn, final Map<String, Object> binds)
	                        throws SQLException {
	                try {
	                        final PreparedStatement stmt = conn.prepareStatement(sqlText);
	                        bind(stmt, binds);
	                        stmt.execute();
	                        stmt.close();
	                } catch (final SQLException e) {
	                        e.printStackTrace();
	                        logger.error(toString(binds) + " " + e.getMessage());
	                        throw e;
	                } catch (final IllegalArgumentException iae) {
	                        iae.printStackTrace();
	                        logger.error(toString(binds) + " " + iae.getMessage());
	                        throw iae;
	                }
	        }

	        // void parse() {
	        // String nextDelimiter = "\"':";
	        // String bindDelimiter = "\"':";
	        // StringTokenizer t = new StringTokenizer(sqlText, nextDelimiter, true);
	        // while (t.hasMoreTokens()) {
	        // String token = t.nextToken(bindDelimiter);
	        // //System.out.println("token '" + token + "'");
	        // if (token.equals("\"")) {
	        // nextDelimiter = "\"";
	        // }
	        // if (token.equals("'")) {
	        // nextDelimiter = "'";
	        // }
	        // if (token.equals(":")) {
	        // nextDelimiter = " "; // needs to accomodate a lot more than this start
	        // with )
	        // // use indexOf
	        // "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_")
	        // token = t.nextToken(" )\n");
	        // //System.out.println("token 2'" + token + "'");
	        // addBindVar(token);
	        // }
	        //
	        // }
	        // }
	        private void addBindVariable(final String bindVariableName) {
	                ArrayList<Integer> bindIndexes = bindVariableNumbers
	                                .get(bindVariableName.toUpperCase());
	                if (bindIndexes == null) {
	                        bindIndexes = new ArrayList<Integer>();
	                }
	                bindVariableCount++;
	                bindIndexes.add(bindVariableCount); // new
	                bindVariableNumbers.put(bindVariableName.toUpperCase(), bindIndexes);
	        }

	        /**
	         * Checks the specified values against the statement
	         * <ul>
	         * <li> ensure that every value is a variable specified in the sql.</li>
	         * <li> ensure that no value is duplicated</li>
	         * </ul>
	         * Checks that all variables are bound
	         * <ul>
	         * <li> every bind variable :xxx toke in statement not in quotes must be in
	         * the list of bind values.</li>
	         * </ul>
	         *
	         * @todo add case sensitivity option
	         * @param binds
	         * @throws SQLException
	         */
	        private void bind(final PreparedStatement stmt, final Map<String, Object> binds)
	                        throws SQLException {
	                final HashMap<String, Object> upperBinds = new HashMap<String, Object>();
	                for (final String bindLower : binds.keySet()) {
	                        upperBinds.put(bindLower.toUpperCase(), binds.get(bindLower));
	                }
	                ArrayList<String> unboundVariables = null;
	                // bindVariableValues.clear();
	                // if (binds != null) {
	                // for (String upName : upperBinds.keySet()) {
	                //
	                // //for (BindVariableValue bind : binds) {
	                //
	                // if (bindVariableNumbers.get(upName) == null && logUnusedBindVariables
	                // ) {
	                // String message = "bind variable: '" + upName + "' is not in the
	                // statement" + "\n"
	                // + toString();
	                // logger.warn(message);
	                // // throw new IllegalArgumentException(message);
	                // }
	                // }
	                // }
	                // check that all variables are bound
	                for (final String variableName : bindVariableNumbers.keySet()) {
	                        final String bindName = variableName.toUpperCase();
	                        if (upperBinds.get(bindName) == null) {
	                                if (unboundVariables == null) {
	                                        unboundVariables = new ArrayList<String>();
	                                }
	                                unboundVariables.add(bindName);
	                        }
	                }
	                // report unbound variables
	                if (!allowUnbound) {
	                        if (unboundVariables != null) {
	                                final StringBuffer buff = new StringBuffer();
	                                buff.append("variablesNotBound: ");
	                                for (final String unbound : unboundVariables) {
	                                        buff.append(unbound);
	                                        buff.append(" ");
	                                }
	                                throw new IllegalArgumentException(buff.toString());
	                        }
	                }
	                if (unboundVariables != null) {
	                        for (final String nm : unboundVariables) {
	                                logger.debug("binding null " + nm);
	                                bindNull(stmt, nm);

	                        }
	                }
	                // bind values to variables
	                // logger.debug("bind count " + binds.size());
	                for (final String upName : upperBinds.keySet()) {
	                        // logger.info("binding " + bind.getVariableName());
	                        final ArrayList<Integer> bindNumbers = bindVariableNumbers.get(upName);
	                        final Object bindValue = binds.get(upName);
	                        if (bindNumbers == null
	                                        && logger.isEnabledFor(logUnusedBindVariables)) {
	                                final String message = "while processing bind variable '" + upName
	                                                + " ' no such bind in statement " + sqlText;
	                                logger.log(logUnusedBindVariables, message);
	                                // logger.warn(message);
	                                // throw new IllegalArgumentException(message);
	                        }
	                        if (bindValue == null) {
	                                bindNull(stmt, upName);
	                        } else {
	                                if (bindNumbers != null) {
	                                        for (final Integer bindNumber : bindNumbers) {
	                                                // logger.debug("getting value object");

	                                                if (logBinds) {

	                                                        logger.debug("binding number " + bindNumber
	                                                                        + " to " + bindValue);

	                                                }

	                                                stmt.setObject(bindNumber, bindValue);
	                                        }
	                                } else {
	                                        if (logBinds) {
	                                                logger.info("no binds found in statement");
	                                        }
	                                }
	                        }
	                }
	        }

	        private void bindNull(final PreparedStatement stmt, final String bindVariableName)
	                        throws SQLException {
	                final ArrayList<Integer> bindNumbers = bindVariableNumbers
	                                .get(bindVariableName.toUpperCase());
	                if (bindNumbers != null) {
	                        for (final Integer bindNumber : bindNumbers) {
	                                stmt.setNull(bindNumber, Types.NULL);
	                        }
	                }
	        }

	        private String bindsToString(final Map<String, Object> binds) {
	                final StringBuilder b = new StringBuilder();
	                b.append("bindValues\n");
	                for (final String key : binds.keySet()) {
	                        b.append(key);
	                        b.append(": '");
	                        b.append(binds.get(key));
	                        b.append("'\n");
	                }
	                return b.toString();
	        }

	        private String bindVariableNumbersToString() {
	                final StringBuilder b = new StringBuilder();
	                b.append("bindVariableNumbers\n");
	                for (final String bindVariable : bindVariableNumbers.keySet()) {
	                        final ArrayList<Integer> numbers = bindVariableNumbers.get(bindVariable);
	                        b.append(bindVariable);
	                        b.append(": ");
	                        for (final Integer number : numbers) {
	                                b.append(number);
	                                b.append(" ");
	                        }
	                        b.append("\n");
	                }
	                return b.toString();
	        }

	        // @todo remove this code
	        // public String getBindStatements() {
	        // StringBuffer buff = new StringBuffer();
	        // Iterator it = bindVarsList.iterator();
	        // int i = 1;
	        // String indent = " ";
	        // buff.append(indent + "PreparedStatementHelper helper = new
	        // PreparedStatementHelper(stmt);\n");
	        // while (it.hasNext()) {
	        // BindVariable b = (BindVariable) it.next();
	        // if (b.getTypeText().equals("date")) {
	        // buff.append(indent + "helper.setDate(" + i + ",DateHelper.toSqlDate(new
	        // java.util.Date(" + b.getBindName() + ")));w\n");
	        // }
	        // if (b.getTypeText().equals("int")) {
	        // buff.append(indent + "helper.setInt(" + i + ",new Integer(" +
	        // b.getBindName() + "));\n");
	        // }
	        // if (b.getTypeText().equals("double")) {
	        // buff.append(indent + "helper.setDouble(" + i + ",new Double(" +
	        // b.getBindName() + "));\n");
	        // }
	        // if (b.getTypeText().equals("string")) {
	        // buff.append(indent + "stmt.setString(" + i+ "," + b.getBindName() +
	        // ");\n");
	        // }
	        // i++;
	        // }
	        // return new String(buff);
	        // }

	        private String toString(final Map<String, Object> binds) {
	                final StringBuilder b = new StringBuilder();
	                b.append("sqlText: '" + sqlText + "\n");
	                b.append(bindsToString(binds));
	                b.append("\n" + bindVariableNumbersToString());
	                return b.toString();
	        }

	        // public final static Pattern finder = Pattern.compile("")
	        void parseBindVariables() {
	                @SuppressWarnings("unused")
	                String leading = null;
	                String delim = null;
	                String trailing = null;
	                bindVariableNumbers = new HashMap<String, ArrayList<Integer>>();
	                // boolean moreTokens = true;
	                String unparsed = sqlText.replaceAll("\n", "");
	                while (unparsed.length() > 0) {
	                        final Matcher tokenMatcher = tokenFinder.matcher(unparsed);
	                        tokenMatcher.useAnchoringBounds(false);
	                        if (tokenMatcher.find()) {
	                                leading = tokenMatcher.group(1);
	                                delim = tokenMatcher.group(2);
	                                trailing = tokenMatcher.group(3);
	                                unparsed = trailing;
	                        } else {
	                                break;
	                        }
	                        final char delimiter = delim.charAt(0);
	                        switch (delimiter) {
	                        case ':':
	                                final Matcher bindNameMatcher = bindNameFinder.matcher(unparsed);
	                                bindNameMatcher.find();
	                                final String bindVariable = bindNameMatcher.group(1);
	                                unparsed = bindNameMatcher.group(2);
	                                addBindVariable(bindVariable.toUpperCase());
	                                break;
	                        case '"':
	                                final Matcher dquoteMatcher = doubleQuoteEndFinder.matcher(trailing);
	                                unparsed = dquoteMatcher.group(2);
	                                break;
	                        case '\'':
	                                final Matcher quoteMatcher = quoteEndFinder.matcher(trailing);
	                                quoteMatcher.find();
	                                unparsed = quoteMatcher.group(2);
	                                break;
	                        }
	                }
	        }

	        // public BindVariable[] getBindVariables() {
	        // BindVariable[] rc = new BindVariable[bindVarsList.size()];
	        // int i = 0;
	        // for (Iterator it = bindVarsList.iterator() ; it.hasNext(); ) {
	        // rc[i++] = (BindVariable) it.next();
	        // }
	        // return rc;
	        // }

	        // private void addBindVar(String var) {
	        // if (var == null) {
	        // throw new java.lang.NullPointerException("bind variable is null");
	        // }
	        // if (logger.isLoggable(Level.DEBUGR)) {
	        // logger.debug("setBindVariable " + var);
	        // }
	        //
	        // BindVariable b = new BindVariable(var);
	        //              bindVars.put(var,b);
	        //              bindVarsList.add(b);
	        //
	        //      }

	}
