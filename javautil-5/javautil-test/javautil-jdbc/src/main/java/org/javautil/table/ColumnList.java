//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.06.09 at 01:38:12 PM EDT 
//

package org.javautil.table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for columnList complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="columnList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="columnNameRef" type="{http://www.javautil.org/Table/}columnNameRefType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "columnList", propOrder = { "columnNameRef" })
public class ColumnList {

	@XmlElement(required = true)
	protected ColumnNameRefType columnNameRef;

	/**
	 * Gets the value of the columnNameRef property.
	 * 
	 * @return possible object is {@link ColumnNameRefType }
	 * 
	 */
	public ColumnNameRefType getColumnNameRef() {
		return columnNameRef;
	}

	/**
	 * Sets the value of the columnNameRef property.
	 * 
	 * @param value
	 *            allowed object is {@link ColumnNameRefType }
	 * 
	 */
	public void setColumnNameRef(final ColumnNameRefType value) {
		this.columnNameRef = value;
	}

}
