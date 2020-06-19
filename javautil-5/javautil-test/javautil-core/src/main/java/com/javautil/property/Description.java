package com.javautil.property;
import java.io.StreamTokenizer;
import java.io.StringReader;
/**																			
	Describe a property; this description has no effect on processing; it is documentation.
*/
class Description {
	/**
	propertyName of the property.  Typically the fully qualified classpropertyName that uses the property. 
	*/
	private String propertyName = null;
	/**
	propertyName of the property.  Typically the fully qualified classpropertyName that uses the property. 
	*/
	private String description = null;
	String text;
	public Description (String text) {
		this.text = text;

		StringReader    reader = new StringReader(text);
		StreamTokenizer tokenizer = new StreamTokenizer(reader); 
		tokenizer.quoteChar('"');

		try {
			//tokenizer.nextToken(); //get the $ in $describe
			tokenizer.nextToken(); //get describe
			tokenizer.nextToken(); //get propertyName 
			//
			propertyName =  tokenizer.sval;
			tokenizer.nextToken();
			description  =  tokenizer.sval;
			System.out.println("!!!!!!propertyName: '" + propertyName);
			System.out.println("!!!!!!description: '"  + description);
		} catch (java.io.IOException i) {
			// not going to happen as the input stream is a string
		}
	}

	public Description(String propertyName, String description) {
		this.propertyName = propertyName;
		this.description  = description;
	}

	public String getDescription() {
		return description;
	}

	public String getPropertyName() {
		return propertyName;
	}

}

