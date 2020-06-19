package org.javautil.text;

public class TextUtil {
	public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}

	public static String padLeft(String s, int n) {
	    return String.format("%1$" + n + "s", s);  
	}
	
	public static String padLeft(String s, int n, char padChar) {
		StringBuilder sb = new StringBuilder();
		int padLength = s.length() - n;
		for (int i = 0; i < padLength; i++) {
			sb.append(padChar);
		}
		sb.append(s);
		return sb.toString();
	}
	
	public static String padRight(String s, int n, char padChar) {
		StringBuilder sb = new StringBuilder(s);
		int padLength = s.length() - n;
		for (int i = 0; i < padLength; i++) {
			sb.append(padChar);
		}
		return sb.toString();
	}
}
