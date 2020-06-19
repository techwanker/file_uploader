package org.javautil.dex;

import java.io.File;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.javautil.dex.servlet.DexterousServletHelper;
import org.javautil.jdbc.datasources.DataSources;
import org.javautil.jdbc.datasources.SimpleDatasourcesFactory;

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
public class DexterousServletProxy extends HttpServlet {
	public static final String revision = "$Revision: 1.1 $";

	private static String newline = System.getProperty("line.separator");

	private final static String DEXTEROUS_ROOT = "dexterousRoot";

	private final static String XSL_ROOT = "xslRoot";

	private final static String AUTHORIZER = "authorizer";

	private final static String CONFIGURATION_ENVIRONMENT_VARIABLE = "environmentName";

	private static Logger logger = Logger.getLogger(DexterousServlet.class
			.getName());

	private static DataSources dataSources = null;

	private static EventHelper events = new EventHelper();

	private static final int SHOW_DSN = 100;

	private static final int LOG_PARAMETERS = 300;

	private String dexterousRoot;

	private String xslRoot;

	private File configPath;

	private final Pattern uriPattern = Pattern.compile(".*/(.*)");

	private SessionAuthorization authorizer = new OpenDoorAuthorizer();

	static {
		events.addEvent(SHOW_DSN);
		// events.addEvent(LOG_MIME);
		events.addEvent(LOG_PARAMETERS);
	}

	public DexterousServletProxy() {
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

			final DexterousServletHelper sh = new DexterousServletHelper(
					request, response);

			sh.setDexterousScriptRoot(dexterousRoot);
			sh.setXslRoot(xslRoot);
			sh.process();
			// dexterous.setServletHelper(sh); // this launches
			// dexterous.process();

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
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		} catch (SQLException e) {
			throw new ServletException(e);
		}

	}

	public synchronized void init(final Map<String, String> nameValue)
			throws IllegalAccessException, ClassNotFoundException, SQLException {
		for (final String name : nameValue.keySet()) {
			final String value = nameValue.get(name);
			set(name, value);
		}
		// dexterousRoot = Machine.getMandatoryProperty("dexterousRoot");
		if (dexterousRoot == null) {
			final StringBuilder b = new StringBuilder();
			for (final String name : nameValue.keySet()) {
				final String value = nameValue.get(name);
				b.append(name);
				b.append(":");
				b.append(value);
				b.append("");
			}
			final String message = "dexterousRoot must be specified,  not specified in parms "
					+ b.toString();
			throw new IllegalStateException(message);
		}
		configPath = new File(dexterousRoot);
		if (!configPath.canRead()) {
			throw new IllegalStateException("cannot read " + configPath);
		}
		logger.info("starting DexterousServlet with dexterousRoot "
				+ dexterousRoot);
		if (authorizer == null) {
			final String message = "authorizer class has not been specified";
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
			throws IllegalAccessException, ClassNotFoundException, SQLException {
		if (name == null) {
			logger.error("name is null");
			throw new IllegalArgumentException("name is null");
		}
		if (DEXTEROUS_ROOT.equalsIgnoreCase(name)) {
			setDexterousRoot(value);
		} else if (XSL_ROOT.equalsIgnoreCase(name)) {
			setXslRoot(value);
			// } else if (MACHINE_CONFIG.equalsIgnoreCase(name)) {
			// createDataSources(value);
		} else if (AUTHORIZER.equalsIgnoreCase(name)) {

			instantiateAuthorizer(value);
		} else if (CONFIGURATION_ENVIRONMENT_VARIABLE.equalsIgnoreCase(name)) {
			setConfigEnvironmentVariableName(value);
		} else {
			logger.warn("unknown parameter " + name);
		}
	}

	// @todo use this
	@SuppressWarnings("unchecked")
	private void instantiateAuthorizer(final String value) {

		Class clazz;
		try {
			clazz = Class.forName(value);

			final Object o = clazz.newInstance();
			if (o instanceof SessionAuthorization) {
				authorizer = (SessionAuthorization) o;
			} else {
				final String message = "class '" + value
						+ "' does not implement SessionAuthorization";
				logger.error(message);
				throw new IllegalArgumentException(message);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

	}

	private void setConfigEnvironmentVariableName(final String environmentName)
			throws SQLException {
		final String payload = System.getenv(environmentName);
		if (payload == null) {
			final String message = "environment variable '" + environmentName
					+ "' is not defined cannot be used to define DataSources";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		final File fname = new File(payload);
		logger.info("resolved datasource configuration from environment variable "
				+ environmentName + " with value " + fname);

		dataSources = new SimpleDatasourcesFactory();
		logger.info("dataSources are " + dataSources.toString());

	}

	private void setDexterousRoot(final String root) {
		final File f = new File(root);
		if (!f.canRead()) {
			final String message = "unable to read directory " + root;
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

}
