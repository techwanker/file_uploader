package org.javautil.bshjdbc;

import java.util.HashMap;
import java.util.Map;

import bsh.NameSpace;
import bsh.UtilEvalError;

public class NamespaceHelper {
	public static Map<String,Object> getVariables(NameSpace namespace) {
		HashMap<String,Object> variables = new HashMap<String,Object>();
		String[] names = namespace.getVariableNames();
		for (String name : names) {
			try {
				variables.put(name, namespace.getVariable(name));
			} catch (UtilEvalError e) {
				throw new RuntimeException(e);
			}
		}
		return variables;
	}
}
