package org.javautil.dex.renderer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.javautil.document.MimeType;
import org.javautil.io.WriterSet;
import org.javautil.io.WriterWrapper;

// @todo manage state transitions better
//write("<?xml version='1.0'?>\n");
//write("<Workbook xmlns='urn:schemas-microsoft-com:office:spreadsheet'\n");
//write(" xmlns:o='urn:schemas-microsoft-com:office:office'\n");
//write(" xmlns:x='urn:schemas-microsoft-com:office:excel'\n");
//write(" xmlns:ss='urn:schemas-microsoft-com:office:spreadsheet'\n");
//write(" xmlns:html='http://www.w3.org/TR/REC-html40'>\n");
//write(" <DocumentProperties xmlns='urn:schemas-microsoft-com:office:office'>\n");
//write("  <Author>Jim Schmidt</Author>\n");
//write("  <LastAuthor>Jim Schmidt</LastAuthor>\n");
//write("  <Created>2006-09-16T03:55:56Z</Created>\n");
//write("  <Company>Trinity Technical Services</Company>\n");
//write("  <Version>10.2625</Version>\n");
//write(" </DocumentProperties>\n");
//write(" <OfficeDocumentSettings xmlns='urn:schemas-microsoft-com:office:office'>\n");
//write("  <DownloadComponents/>\n");
public class ExcelXmlWriter {
	public enum Action {
		CREATE_SHEET, CREATE_ROW, CREATE_CELL;
	}
	private final Logger						logger				= Logger.getLogger(this.getClass().getName());
	private boolean						needsStartWorkbook	= true;
	private final boolean                       needsWorksheet     = true;
	private boolean                       needsCloseWorksheet = false;
	private boolean						needsCloseWorkbook	= true;
	private boolean                       needsRow            = true;
	private int							sheetCount			= 0;
	/* 1 is column A etc */
	private int							nextColumnIndex;
	private boolean						nextCellNeedsIndex	= false;
	private ArrayList<Writer>			writers;
	private ArrayList<WriterWrapper>	wrappers;
	private final String rowIndent = "  ";

	private final String cellIndent = "    ";
	private static final RenderingCapability capability = new RenderingCapability(MimeType.EXCEL_XML,false);
	//
	// Constructors
	//
//	ExcelXmlWriter() {
//	}

	private boolean needsCloseRow = false;

	public ExcelXmlWriter(final ArrayList<Writer> writers) {
		this.writers = writers;
	}

	public ExcelXmlWriter(final File file) throws IOException {
		final Writer writer = new BufferedWriter(new FileWriter(file));
		writers.add(writer);
	}

	public ExcelXmlWriter(final Writer writer)  {
		writers = new ArrayList<Writer>();
		writers.add(writer);
	}

	public ExcelXmlWriter(final WriterSet wrappers) {
		if (wrappers.size() < 1) {
			throw new IllegalArgumentException("no writers");
		}
	//	this.wrappers = wrappers;
		this.wrappers = new ArrayList<WriterWrapper>();
		logger.info("adding writers");
		for (final WriterWrapper wrapper : wrappers.getWriters()) {
			logger.info("adding wrapper " + wrapper.getDescription());
			this.wrappers.add(wrapper);
		}
	}

	public void closeSheet() throws IOException {
		if (needsCloseRow) {
			closeRow();
		}
		write("  </Table>\n");
		write("</Worksheet>\n");
		needsCloseWorksheet = false;
	}

	public void closeWorkbook() throws IOException {
		if (needsCloseWorksheet) {
			closeSheet();
		}
		write("</Workbook>");
		needsCloseWorkbook = false;
		closeWriters();
	}
	public void createDefaultWorkSheet() throws IOException {
		final String sheetName = "Sheet" + ++sheetCount;
		createSheet(sheetName);
		needsRow = true;
	}

	public void createRow() throws IOException {
		if (needsWorksheet) {
			createDefaultWorkSheet();
		}
		if (needsCloseRow) {
			closeRow();
		}
		nextColumnIndex = 1;
		write("<Row>\n");
		needsRow = false;
		needsCloseRow = true;
	}

	public void createSheet(final String sheetName) throws IOException {
		if (needsStartWorkbook) {
			startWorkbook();
		}
		if (needsCloseWorksheet) {
			closeSheet();
		}

		write("<Worksheet ss:Name=\"");
		write(encodedString(sheetName));
		write("\">\n");
		write("  <Table>\n");
		needsCloseWorksheet = true;
	}

	public int getSheetCount() {
		return sheetCount;
	}

	public void nullCell() throws IOException {
		if (needsRow) {
			createRow();
		}
		nextColumnIndex++;
		nextCellNeedsIndex = true;
	}

	public void writeCell(final String data) throws IOException {
		if (needsRow) {
			createRow();
		}
		if (cellIndent != null) {
			write(cellIndent);
		}
		if (nextCellNeedsIndex) {
			write("<Cell ss:index='" + Integer.toString(nextColumnIndex) + "'>");
		} else {
			write("<Cell>");
		}
		nextCellNeedsIndex = false;

		write("<Data ss:Type='String'>");
		write(encodedString(data));
		write("</Data></Cell>\n");
		nextColumnIndex++;
	}

	public void writeRow(final ArrayList<String> cells) throws IOException {
		if (rowIndent != null) {
			write(rowIndent);
		}
		write("<Row>\n");
		for (int i = 0; i < cells.size(); i++) {
			write("  <Cell><Data ss:Type='String'>");
			write(encodedString(cells.get(i)));
			write("</Data></Cell>\n");
		}
		write("</Row>\n");
	}

	private void closeRow() throws IOException {
		write("</Row>\n");
		needsCloseRow = false;
	}
	private void closeWriters() throws IOException {
		if (needsCloseWorkbook) {
			closeWorkbook();
		}
		if (writers != null) {
			for (int i = 0; i < writers.size(); i++) {
				writers.get(i).close();
			}
		}
		if (wrappers != null) {
			for (int i = 0; i < wrappers.size(); i++) {
				wrappers.get(i).close();
			}
		}
	}



	private void startWorkbook() throws IOException {
		if (!needsStartWorkbook) {
			throw new IllegalStateException("startWorkbook has already been called for this workbook");
		}
		write("<?xml version='1.0' encoding='ISO-8859-1'?>\n");
		write("<?mso-application progid='Excel.Sheet'?>\n");
		write("<Workbook xmlns='urn:schemas-microsoft-com:office:spreadsheet' \n");
		write("xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' \n");
		write("xmlns:x='urn:schemas-microsoft-com:office:excel' \n");
		write("xmlns:x2='http://schemas.microsoft.com/office/excel/2003/xml' \n");
		write("xmlns:ss='urn:schemas-microsoft-com:office:spreadsheet' \n");
		write("xmlns:o='urn:schemas-microsoft-com:office:office' \n");
		write("xmlns:html='http://www.w3.org/TR/REC-html40' \n");
		write("xmlns:c='urn:schemas-microsoft-com:office:component:spreadsheet'> \n");
		write(" <OfficeDocumentSettings xmlns='urn:schemas-microsoft-com:office:office'> \n");
		write(" <DownloadComponents/> \n");
		write(" </OfficeDocumentSettings> \n");
		write(" <ExcelWorkbook xmlns='urn:schemas-microsoft-com:office:excel'> \n");
		write("</ExcelWorkbook>\n");
		needsStartWorkbook = false;
	}



	private void write(final String text) throws IOException {
		if (writers != null) {
			for (int i = 0; i < writers.size(); i++) {
				writers.get(i).write(text);
			}
		} else if (wrappers != null) {
			for (int i = 0; i < wrappers.size(); i++) {
				wrappers.get(i).write(text);
			}
		} else {
			throw new IllegalStateException("nothing to write to");
		}
	}

	// public void close() throws IOException {
	// writer.close();
	// }
	String encodedString(final String val) {
		return val;
	}
}
