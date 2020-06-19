package org.javautil.dex;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.javautil.dex.dexterous.Dexterous;
import org.javautil.dex.dexterous.DexterousState;
import org.javautil.dex.dexterous.State;
import org.javautil.document.MimeType;
import org.javautil.jdbc.datasources.DataSources;
import org.javautil.jdbc.datasources.InvalidDataSourceException;

//import org.javautil.xml.TransformerHelperThread;

// TODO I'm confused we have transformations occurring twice here and once in Dexterous
// now dexterous isn't always invoked through the servlet but can we delegate transformation to it so that it
// only occurs once?
@SuppressWarnings("serial")
/**
 * @param pdid
 * @param did
 * @param ds
 */
public class DexterousServlet extends HttpServlet {
	public static final String revision = "$Revision: 1.1 $";

	private static String newline = System.getProperty("line.separator");

	private final static String DEXTEROUS_ROOT = "dexterousRoot";

	private final static String DEXTEROUS_SERVLET_ROOT = "DEXTEROUS_SERVLET_ROOT";
	private final static String XSL_ROOT = "xslRoot";

	/**
	 * TODO wtf? The name of the configuration file for getting data sources
	 */
	private final static String MACHINE_CONFIG = "machineConfig";

	private final static String AUTHORIZER = "authorizer";

	private final static String CONFIGURATION_ENVIRONMENT_VARIABLE = "environmentName";

	private static Logger logger = Logger.getLogger(DexterousServlet.class
			.getName());

	private static String DEFAULT_DATA_SOURCE_NAME = "jdbc/default";

	// private static org.javautil.jdbc.Datasources dataSources = null;

	private static EventHelper events = new EventHelper();

	private static DataSources dataSources;
	// todo all events should be in class
	private static final Integer SHOW_DSN = new Integer(100);

	private static final Integer LOG_MIME = new Integer(200);

	private static final Integer LOG_PARAMETERS = new Integer(300);

	private String dexterousRoot;

	private String xslRoot;

	private File configPath;

	private final Pattern uriPattern = Pattern.compile(".*/(.*)");

	private final boolean allowCaching = false;

	private SessionAuthorization authorizer = new OpenDoorAuthorizer();

	static {
		events.addEvent(SHOW_DSN);
		// events.addEvent(LOG_MIME);
		events.addEvent(LOG_PARAMETERS);
	}

	// TODO convert to JNDI
	public static Connection getConnection(final HttpServletRequest request)
			throws SQLException, InvalidDataSourceException {
		// if (dataSources == null) {
		// dataSources = DatasourcesFactory.getDataSources();
		// }

		String dataSourceId = request.getParameter("ds");
		logger.info("ds = '" + dataSourceId + "'");
		if (dataSourceId == null) {
			logger.info("ds not specified using default "
					+ DEFAULT_DATA_SOURCE_NAME);
			dataSourceId = DEFAULT_DATA_SOURCE_NAME;
		}

		if (events.exists(SHOW_DSN)) {
			logger.debug("ds = '" + dataSourceId + "' getting dataSource "
					+ dataSourceId);
		}
		return dataSources.getDataSource(dataSourceId).getConnection();
	}

	@SuppressWarnings("incomplete-switch")
	private static MimeType getMimeType(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException {
		final String pMimeType = request.getParameter("mime");
		MimeType mimeType = MimeType.XML;
		if (events.exists(LOG_MIME)) {
			logger.debug("mime type " + mimeType + " pMimeType " + pMimeType);
		}
		try {
			if (pMimeType != null) {
				mimeType = MimeType.getMimeType(pMimeType);
				logger.debug("setMimeType to " + mimeType);

			}
		} catch (final Exception e) {
			logger.warn(e.getMessage());
			throw new ServletException("unsupported mime type " + pMimeType);
		}
		// todo add support for other mime types
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

	public DexterousServlet() {
		final Level logLevel = Level.DEBUG;
		logger.info("setting log level to " + logLevel);
		logger.setLevel(logLevel);
	}

	@Override
	public void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException {
		{

			final String URI = request.getRequestURI();
			logger.debug("processing uri " + URI);

			try {

				try {
					processDexterous(request, response);

				} catch (final ServletException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
					throw e;
				}

				// }
			} catch (final Exception e) {
				logger.error(e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
				throw new ServletException(e.getMessage());
			}
		}

	}

	@Override
	public void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException {
		doGet(request, response);
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized void init() throws ServletException {
		final HashMap<String, String> params = new HashMap<String, String>();
		logger.info("starting init");
		final Enumeration<String> paramNames = getServletConfig()
				.getInitParameterNames();
		while (paramNames.hasMoreElements()) {
			final String paramName = paramNames.nextElement();
			final String value = getServletConfig().getInitParameter(paramName);
			params.put(paramName, value);
		}

		try {
			init(params);
		} catch (final InstantiationException e) {
			logger.error(e.getMessage());
			throw new ServletException(e);
		} catch (final IllegalAccessException e) {
			logger.error(e.getMessage());
			throw new ServletException(e);
		} catch (final ClassNotFoundException e) {
			logger.error(e.getMessage());
			throw new ServletException(e);
		} catch (final Exception e) {
			logger.error(e.getMessage());
			throw new ServletException(e.getClass().getName() + " "
					+ e.getMessage());
		}

	}

	public synchronized void init(final Map<String, String> nameValue)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, ServletException {
		for (final String name : nameValue.keySet()) {
			final String value = nameValue.get(name);
			try {
				set(name, value);
			} catch (final InstantiationException e) {
				logger.error(e.getMessage());
				throw e;
			} catch (final IllegalAccessException e) {
				logger.error(e.getMessage());
				throw e;
			} catch (final ClassNotFoundException e) {
				logger.error(e.getMessage());
				throw e;
			}

		}
		if (dexterousRoot == null) {
			logger.info("dexterousRoot not set in initialization checking environment");
			final String envRoot = System.getenv(DEXTEROUS_SERVLET_ROOT);
			if (envRoot == null) {
				logger.info("environment '" + DEXTEROUS_SERVLET_ROOT
						+ " is not set");
			}
			if (envRoot != null) {
				setDexterousRoot(envRoot);
				logger.info("set dexterousRoot from env: "
						+ DEXTEROUS_SERVLET_ROOT + " " + envRoot);
			} else {
				final ServletContext servletContext = this.getServletContext();
				final String realPath = servletContext
						.getRealPath("./dexterous");
				setDexterousRoot(realPath);
				logger.info("dexterous root set from servlet context to '"
						+ realPath + "'");
			}
			if (dexterousRoot == null) {
				throw new ServletException("dexterousRoot is still null");
			}
			final File f = new File(dexterousRoot);
			if (!f.isDirectory()) {
				throw new ServletException("dexterousRoot '" + dexterousRoot
						+ "' is not a directory");
			}
			if (!f.canRead()) {
				throw new ServletException("cannot read dexterousRoot '"
						+ dexterousRoot + "'");
			}

		}
		// dexterousRoot = Machine.getMandatoryProperty("dexterousRoot");
		if (dexterousRoot == null) {
			final StringBuilder b = new StringBuilder();
			for (final String name : nameValue.keySet()) {
				final String value = nameValue.get(name);
				b.append(name);
				b.append(": '");
				b.append(value);
				b.append("'");
			}
			final String message = "dexterousRoot must be specified,  not specified in parms "
					+ b.toString();
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		configPath = new File(dexterousRoot);
		if (!configPath.canRead()) {
			logger.error("cannot read" + configPath);
			throw new IllegalArgumentException("cannot read " + configPath);
		}
		logger.info("starting DexterousServlet with dexterousRoot "
				+ dexterousRoot);
		if (authorizer == null) {
			final String message = "authorizer class has not been specified";
			logger.error(message);
			throw new IllegalStateException(message);
		}
	}

	/**
	 * 
	 * @param arguments
	 */
	public void initialize(final Element arguments) {
		final Iterator<Element> it = arguments.elementIterator();
		while (it.hasNext()) {
			final Element a = it.next();
			final String name = a.getName();
			final String value = (String) a.getData();
			setNameValue(name, value);
		}

	}

	public void set(final String name, final String value)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		if (name == null) {
			logger.error("name is null");
			throw new IllegalArgumentException("name is null");
		}
		if (DEXTEROUS_ROOT.equalsIgnoreCase(name)) {
			logger.info("initialization parameter for '" + DEXTEROUS_ROOT
					+ " is '" + value + "'");
			setDexterousRoot(value);
		} else if (XSL_ROOT.equalsIgnoreCase(name)) {
			setXslRoot(value);
		} else if (MACHINE_CONFIG.equalsIgnoreCase(name)) {
			// createDataSources(value);
		} else if (AUTHORIZER.equalsIgnoreCase(name)) {

			instantiateAuthorizer(value);
		} else if (CONFIGURATION_ENVIRONMENT_VARIABLE.equalsIgnoreCase(name)) {
			// setConfigEnvironmentVariableName(value);
		} else {
			logger.warn("unknown parameter " + name);
		}
	}

	// private void createDataSources(String configFileName) throws
	// DocumentException, IOException {
	// dataSources = new DataSources(configFileName, false);
	// }

	private File getDexterousScriptFile(final HttpServletRequest request)
			throws ServletException {
		File returnValue = null;
		final String fileName = request.getParameter("fn");
		final String fileId = request.getParameter("fid");
		logger.info("fn: " + fileName);
		logger.info("fid: " + fileId);
		logger.info(DEXTEROUS_ROOT + ": " + dexterousRoot);
		if (fileId != null && fileName != null) {
			logger.error("fid and fn both specified");
			throw new ServletException("fid and fn both specified");
		}
		if (fileId != null) {
			logger.error("not supported at this time");
			throw new ServletException("not supported at this time");
		}
		if (fileName != null) {
			final String scriptPath = dexterousRoot + "/" + fileName;
			logger.info("script path is " + scriptPath);
			final File file = new File(scriptPath);
			{
				if (!file.canRead() || !file.exists()) {
					logger.warn("cannot read file: '" + scriptPath + "'");
					final String absolute = file.getAbsolutePath();
					logger.warn("cannot read absolute + '" + absolute + "'");
				}
			}
			returnValue = new File(scriptPath);
		}
		return returnValue;
	}

	private File getXslStyleSheet(final HttpServletRequest request)
			throws ServletException {
		File returnValue = null;
		final String fileName = request.getParameter("xsl");

		if (fileName != null) {
			final String scriptPath = xslRoot + "/" + fileName;
			returnValue = new File(scriptPath);
			if (!returnValue.canRead()) {
				throw new ServletException("can't read " + scriptPath);
			}
			logger.info("xsl specified: using stylesheet '" + scriptPath + "'");
		}
		return returnValue;
	}

	// @todo use this
	@SuppressWarnings("unchecked")
	private void instantiateAuthorizer(final String value)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		final Class clazz = Class.forName(value);
		final Object o = clazz.newInstance();
		if (o instanceof SessionAuthorization) {
			authorizer = (SessionAuthorization) o;
		} else {
			final String message = "class '" + value
					+ "' does not implement SessionAuthorization";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}

	}

	@SuppressWarnings("unchecked")
	private void logParameters(final HttpServletRequest request) {
		final StringBuilder b = new StringBuilder();
		final Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			final String name = names.nextElement();
			final String value = request.getParameter(name);

			b.append("parm: '");
			b.append(name);
			b.append("' value: '");
			b.append(value);
			b.append("'");
			b.append(newline);
		}
		logger.info(b.toString());
	}

	private void processDexterous(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException, InvalidDataSourceException {

		// Get the database connection
		if (events.exists(LOG_PARAMETERS)) {
			logParameters(request);
		}
		Connection conn;
		try {
			conn = getConnection(request);
		} catch (final SQLException e1) {
			throw new ServletException(e1.getMessage());
		}
		final MimeType mimeType = getMimeType(request, response);
		final DexterousState state = new State();
		state.setMimeType(mimeType);
		//
		// Get Script
		//
		final File dexterousScript = getDexterousScriptFile(request);
		if (dexterousScript == null) {
			logger.error("dexterousScript is null");
			throw new ServletException("script is null");
		}
		try {
			state.setRequestFile(dexterousScript);
		} catch (final Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new ServletException(e.getCause());
		}

		final File styleSheetFile = getXslStyleSheet(request);

		if (styleSheetFile != null) {
			state.setStylesheetFileName(styleSheetFile.getAbsolutePath());
		}
		state.setOutputStream(response.getOutputStream());

		state.setUseCachedIfAvailable(allowCaching);
		state.setConnection(conn);
		state.setBindVariables(request);
		state.setCloseWriters(true);
		state.setEmitMetaData(true);
		try {

			final Dexterous dexterous = new Dexterous(state);
			dexterous.process();

		} catch (final Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new ServletException(e.getCause());
		} finally {
			try {
				conn.close();
			} catch (final SQLException e) {
				logger.error("could not close connection");
			}
		}

	}

	private void setDexterousRoot(final String root) {
		final File f = new File(root);
		if (!f.canRead()) {

			final String message = "unable to read directory '" + root + "'";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		dexterousRoot = root;
	}

	private void setNameValue(final String name, final String value) {
		if (name == null) {
			logger.error("name is null value is " + value);
			throw new IllegalArgumentException("name is null");
		}
		// String upperName = name.toUpperCase();
		if (DEXTEROUS_ROOT.equalsIgnoreCase(name)) {
			setDexterousRoot(value);
		} else if (XSL_ROOT.equalsIgnoreCase(name)) {
			setXslRoot(value);
		}
		logger.warn("property unknown: '" + name + "'");
	}

	private void setXslRoot(final String root) {
		final File f = new File(root);
		if (!f.canRead()) {
			final String message = "unable to read directory " + root;
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		xslRoot = root;
	}

	String getBasePath(final HttpServletRequest request) {
		final String requestURL = request.getRequestURI().toString();
		final String pathInfo = request.getPathInfo();
		final int index = requestURL.indexOf(pathInfo);
		final String basePath = requestURL.substring(0, index);
		logger.info("basePath " + basePath);
		return basePath;
	}
}
