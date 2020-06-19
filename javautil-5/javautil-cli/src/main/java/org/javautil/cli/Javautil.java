package org.javautil.cli;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class Javautil {
	
	private static Map<String,String> association = new HashMap<String,String>() {{
		put("filelister", "org.javautil.cli.FileLister");
	} };
	private static Logger logger = Logger.getLogger(Javautil.class);
	
	public static void main (String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		if (args.length < 1) {
			
			// TODO externalize names and programs 
			throw new  IllegalArgumentException("program name required");
		}
		String className = association.get(args[0]);
		if (className == null) {
			// TODO list the known programs
		
			throw new IllegalArgumentException("no such program found");
		}
		Class<?> mainClass = Class.forName(className);
		String[] classArgs = shift(args);
		Method main;
		try {
			String [] stringArray;
			main = getMain(mainClass);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} 
//		catch (NoSuchMethodException e) {
//			Method[] methods = mainClass.getMethods();
//			StringBuilder sb = new StringBuilder();
//			for (Method method : methods) {
//				sb.append(method);
//				sb.append("\n");
//			}
//			throw new RuntimeException("no appropriate main known methods are " + sb.toString());
//		}
		String message = "invoking " + main;
		logger.debug(message);
		main.invoke(classArgs);
	}
	
	static String[] shift(String [] args) {
		String [] retval = new String[args.length - 1];
		for (int i = 0; i < (args.length - 1); i++) {
			retval[i] = args[i + 1];
		}
		return retval;
	}
	
	static Method getMain(Class clazz) {
		Method[] methods = clazz.getMethods();
		Method retval = null;
		// TODO this might be the wrong one
		for (Method method : methods ) {
			if (method.getName().equals("main")) {
				retval = method;
				break;
			}
		}
		return retval;
	}
	

}
