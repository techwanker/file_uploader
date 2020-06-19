package org.javautil.dex.renderer.interfaces;


public interface SpreadSheetXmlWriter {

	public void openRow(int rowNumber);
	public void openWookSheet(String worksheetName);
	public void openWorkBook(String wookbookName);
	public void writeCell(Number number);
	public void writeCell(String text);

}
