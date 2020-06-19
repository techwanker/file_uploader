package org.javautil.dex.parser;


public class SQL {
     private final String text;

     private final SqlCommand command;

     private final String dataSourceName =null;

     public SQL(final String text, final String dataSourceName) {
    	 if (text == null) {
    		 throw new IllegalArgumentException("text is null");
    	 }
    	 command = SqlCommand.parse(text);
    	 this.text = text;
     }

 	public String getDataSourceName() {
    	 return dataSourceName;
     }
     public SqlCommand getSqlCommand() {
 		return command;
 	}

     public String getText() {
    	 return text;
     }
}
