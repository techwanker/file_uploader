package org.javautil.dex.dexterous;

import java.util.HashMap;

import org.apache.log4j.Logger;

public enum ErrorEvent {
	BREAK, CLEAR, SET, VARIABLE, INCLUDE, EXEC, PROMPT, SELECT, SPOOL, REM, REM_SET, STORE, GET, DESCRIBE, UPDATE, DELETE, INSERT, SHOW, WITH,
	RECORD, LOG, GET_STORE, INCLUDE_BEFORE, INCLUDE_AFTER, CROSSTAB_ROW, CROSSTAB_COLUMN, CROSSTAB_VALUE, OUTPUT_FORMAT, DATE_FORMAT,
	DATA_SOURCE, WORKSHEET, RESULTSET, AUTOTRACE, BASE, TERMOUT, MAKE_PATH, TRACE_ID, NUMBER, DATE, STRING, PUMP_INTO, PUMP_FROM, CONNECT, EXIT,
	COMMIT, WHENEVER, COMMENT, CREATE, DML, RUN, ALTER, DROP, PURGE, TRUNCATE, BEGIN, DECLARE, BEGIN_STATEMENT, ECHO_UNTIL, GO, PASS_ALL,
	NO_PASS_ALL, RUN_FILE, ESCAPE_ECHO, PARSE_UNTIL_GO, ECHO, RUN_FILE_CONCAT, BSH, CURSOR;

	public static final String revision = "$Revision: 1.1 $";

	private static Logger logger = Logger.getLogger(DexterousCommand.class.getName());

	private static HashMap<String, ErrorEvent> mapping = new HashMap<String, ErrorEvent>();
	static {
		mapping.put("ALTER", ALTER);
		mapping.put("BEGIN", BEGIN);
		mapping.put("BEGIN-STATEMENT", BEGIN_STATEMENT);
		mapping.put("GO", GO);
		mapping.put("BREAK", BREAK);
		mapping.put("CURSOR", CURSOR);
		mapping.put("DECLARE", DECLARE);
		mapping.put("DROP", DROP);
		mapping.put("ECHO", ECHO);
		mapping.put("ECHO_UNTIL", ECHO_UNTIL);
		mapping.put("ECHO-UNTIL", ECHO_UNTIL);
		mapping.put("PURGE", PURGE);
		mapping.put("TRUNCATE", TRUNCATE);
		mapping.put("CLEAR", CLEAR);
		mapping.put("SET", SET);
		mapping.put("VARIABLE", VARIABLE);
		mapping.put("EXEC", EXEC);
		mapping.put("INCLUDE", INCLUDE);
		mapping.put("PROMPT", PROMPT);
		mapping.put("SELECT", SELECT);
		mapping.put("SPOOL", SPOOL);
		mapping.put("REM", REM);
		mapping.put("STORE", STORE);
		mapping.put("GET", GET);
		mapping.put("DESCRIBE", DESCRIBE);
		mapping.put("SHOW", SHOW);
		mapping.put("WITH", WITH);
		mapping.put("RECORD", RECORD);
		mapping.put("LOG", LOG);
		mapping.put("CONNECT", CONNECT);
		mapping.put("PARSE-UNTIL-GO", PARSE_UNTIL_GO);
		mapping.put("EXIT", EXIT);
		mapping.put("INSERT", INSERT);
		mapping.put("COMMIT", COMMIT);
		mapping.put("DELETE", DELETE);
		mapping.put("WHENEVER", WHENEVER);
		mapping.put("CREATE", CREATE);
		mapping.put("R", RUN);
		mapping.put("RUN", RUN);
		mapping.put("<<<", PASS_ALL);
		mapping.put(">>>", NO_PASS_ALL);
		mapping.put("@", RUN_FILE);
		mapping.put("!!!", ESCAPE_ECHO);
		mapping.put("[",BSH);
	}

	public static ErrorEvent getCommandType(final String token) {
		ErrorEvent returnValue = null;
		String command = null;
		if (token != null) {
			command = token.toUpperCase();

			if (command.endsWith(";")) {
				command = command.substring(0, command.indexOf(";"));
				// logger.info("command trimmed to " + command);
			}
			if (token.startsWith("--")) {
				returnValue = COMMENT;
			} else if (token.startsWith("@") && token.length() > 1) {
				returnValue = RUN_FILE_CONCAT;
			} else {
				returnValue = mapping.get(command);
			}
		}
		if (returnValue == null) {
			throw new IllegalArgumentException("unknown command " + token);
		}

		return returnValue;
	}

	public static ErrorEvent getCommandType(final String[] tokens) {
		ErrorEvent returnValue;
		String command = null;
		if (tokens == null) {
			throw new IllegalArgumentException("tokens is null");
		}
		if (tokens.length == 0) {
			return null;
		}

			command = tokens[0].toUpperCase();

		if (command.endsWith(";")) {
			command = command.substring(0, command.indexOf(";"));
			// logger.info("command trimmed to " + command);
		}
		if (tokens[0].startsWith("--")) {
			returnValue = COMMENT;
		} else {
			returnValue = mapping.get(command);
			if (returnValue == REM) {
				if (tokens.length > 1) {
					final ErrorEvent cmd = mapping.get(tokens[1].toUpperCase());

					if (cmd == SET) {
						returnValue = REM_SET;
					}
				}

			}
		}
		if (returnValue == null) {
			throw new IllegalArgumentException("unknown command " + tokens[0]);
		}
		return returnValue;
	}

}
