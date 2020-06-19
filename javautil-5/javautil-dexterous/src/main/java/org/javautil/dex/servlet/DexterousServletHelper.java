package org.javautil.dex.servlet;

import java.io.File;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.javautil.dex.dexterous.Dexterous;
import org.javautil.dex.dexterous.DexterousState;
import org.javautil.dex.dexterous.State;
import org.javautil.document.MimeType;
import org.javautil.io.WriterSet;

// all exceptions are logged because of some exception message propagation problems in
// current version of Jetty
public class DexterousServletHelper {
	private final HttpServletRequest request;

	private final HttpServletResponse response;

	private File dexterousScriptRoot;

	private File xslRoot;

	// make it static, too many calls for instantiation overhead
	private static Logger logger = Logger
			.getLogger(DexterousServletHelper.class.getName());

	public DexterousServletHelper(final HttpServletRequest request,
			final HttpServletResponse response) {
		if (request == null) {
			final String message = "request was null";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		if (response == null) {
			final String message = "response was null";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		this.request = request;
		this.response = response;

	}

	// public void setDexterous(Dexterous dexterous) {
	// this.dexterous = dexterous;
	// }

	public void process() throws ServletException {
		try {
			logger.info("in configureDexterous");
			// Dexterous dexterous = new Dexterous();
			final WriterSet writers = new WriterSet("ServletHelper");
			final OutputStream os = response.getOutputStream();
			writers.addWriter(os, "response", true, 0L);
			final DexterousState state = new State();
			state.setWriters(writers);
			state.setMimeType(getMimeType());
			state.setRequestFile(getDexterousScriptFile());
			state.setDataSourceName(getDataSourceName());
			if (getXslStyleSheet() != null) {
				state.setStylesheetFileName(getXslStyleSheet()
						.getAbsolutePath());
			}
			state.setBindVariables(request);
			state.setCloseWriters(true);
			state.setEmitMetaData(true);
			final Dexterous dexterous = new Dexterous(state);
			dexterous.process();
		} catch (final ServletException se) {
			logger.error(se.getMessage());
			throw se;
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ServletException(e.getClass().getName() + " "
					+ e.getMessage());
		}

		//
	}

	public String getDataSourceName() throws ServletException {
		// Machine machine = new Machine();
		final String dataSourceId = request.getParameter("ds");
		logger.info("ds = '" + dataSourceId + "'");
		if (dataSourceId == null) {
			throw new ServletException(
					"parm ds (datasource name) was not specified in request");
		}
		return dataSourceId;
	}

	public void setDexterousScriptRoot(final String directoryName) {
		if (directoryName == null) {
			throw new IllegalArgumentException("directoryName is null");
		}
		final File f = new File(directoryName);
		setDexterousScriptRoot(f);
	}

	public void setDexterousScriptRoot(final File file) {
		validateFile(file);
		dexterousScriptRoot = file;
		logger.info("dexterousScriptRoot set to: '" + file.getAbsolutePath()
				+ "'");
	}

	public void setXslRoot(final String directoryName) {
		if (directoryName == null) {
			throw new IllegalArgumentException("directoryName is null");
		}
		final File f = new File(directoryName);
		setXslRoot(f);
	}

	public void setXslRoot(final File file) {
		validateFile(file);
		xslRoot = file;
	}

	public void validateFile(final File file) {
		if (file == null) {
			final String message = "file was null";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		if (!file.exists()) {
			final String message = "file " + file.getAbsolutePath()
					+ " does not exist";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		if (!file.canRead()) {
			final String message = "file " + file.getAbsolutePath()
					+ " cannot be read";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}

	}

	// @todo set file names better than this
	@SuppressWarnings("incomplete-switch")
	private MimeType getMimeType() {
		final String pMimeType = request.getParameter("mime");
		MimeType mimeType = MimeType.XML;
		if (logger.isDebugEnabled()) {
			logger.debug("mime type " + mimeType + " pMimeType " + pMimeType);
		}
		try {
			if (pMimeType != null) {
				mimeType = MimeType.getMimeType(pMimeType);
				logger.debug("setMimeType to " + mimeType);

			}
		} catch (final Exception e) {
			logger.warn(e.getMessage());
			throw new IllegalArgumentException("unsupported mime type "
					+ pMimeType);
		}
		switch (mimeType) {
		case EXCEL:
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ "TOAD.xls" + "\"");
			break;
		case HTML:
			response.setContentType("text/html");
			break;
		case CSV:
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ "squirrel.csv" + "\"");
			break;
		case EXCEL_XML:
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ "TOAD.xls" + "\"");
			break;
		}
		return mimeType;
	}

	private File getDexterousScriptFile() {
		File returnValue = null;
		final String fileName = request.getParameter("fn");

		logger.info("fn: " + fileName);
		if (dexterousScriptRoot == null) {
			final String message = "dexterousScriptRoot is null";
			logger.error(message);
			throw new IllegalStateException(message);
		}
		if (fileName != null) {
			final String scriptPath = dexterousScriptRoot.getAbsolutePath()
					+ "/" + fileName;
			logger.info("script path is " + scriptPath);
			final File file = new File(scriptPath);
			{
				if (!file.canRead() || !file.exists()) {
					final StringBuilder b = new StringBuilder();
					b.append("cannot read file: '");
					b.append(scriptPath);
					b.append("' '");
					b.append(file.getAbsolutePath());
					logger.warn(b.toString());
					throw new IllegalArgumentException(b.toString());
				}
			}
			returnValue = new File(scriptPath);
		}
		return returnValue;
	}

	private File getXslStyleSheet() {
		File returnValue = null;
		final String fileName = request.getParameter("xsl");

		if (fileName != null) {
			final String scriptPath = xslRoot + "/" + fileName;
			returnValue = new File(scriptPath);
			if (!returnValue.canRead()) {
				throw new IllegalArgumentException("can't read " + scriptPath);
			}
			logger.info("xsl specified: using stylesheet '" + scriptPath + "'");
		}
		return returnValue;
	}

}
