
package com.javautil.property;

public class PropertyManagerThread extends Thread
{
	PropertyManagement properties = null;

	public void setPropertyManager(PropertyManagement properties)
	{
		this.properties = properties;
	}
}                                  
