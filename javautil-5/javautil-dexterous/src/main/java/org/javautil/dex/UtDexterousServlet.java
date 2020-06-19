package org.javautil.dex;

//import java.io.File;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.SQLException;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import java.util.regex.Pattern;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.javautil.dao.UtDexterousS;
//import org.javautil.dto.UtDexterous;
//import org.javautil.j2ee.AbstractServlet;
//import org.javautil.j2ee.ServletRequestException;
//import org.javautil.machine.Machine;
//import org.javautil.util.Settings;
//

/**
 *  @param pdid
 *  @param did
 *  @param ds
 *
 *   </servlet>
 <servlet>
     <servlet-name>report</servlet-name>
     <servlet-class>org.javautil.dex.UtDexterousServlet</servlet-class>
 <init-param>
   <description>Report Definition Data Source Name</description>
    <param-name>dataSourceName</param-name>
    <param-value>jdbc/default</param-value>
 </init-param>
 <init-param>
    <description>Style Sheet for a report request</description>
   <param-name>report-xsl</param-name>
    <param-value>/usr/local/javautil/Dexterous/xsl/report_parms_to_html.xsl</param-value>
 </init-param>
 <load-on-startup>1</load-on-startup>
 </servlet>
 */
//public class UtDexterousServlet extends AbstractServlet {
//	public static final String revision = "$Revision: 1.1 $";
//
//	private static final String XSL_FILE = "report-xsl";
//
//	private static final String DATA_SOURCE_NAME = "dataSourceName";
//
//	// private String sqlRootPath = null;
//	private Logger logger = Logger.getLogger(this.getClass().getName());
//
//	private String xslRoot;
//
//	private String styleSheetFileName;
//
//	private File styleSheetFile;
//
//	private Pattern uriPattern = Pattern.compile(".*/(.*)");
//
//	public UtDexterousServlet() {
//	//	Level logLevel = Level.DEBUG;
//	//	logger.info("setting log level to " + logLevel);
//	//	logger.setLevel(logLevel);
//	}
//
//	@Override
//	/**
//	 * @todo configure isLoggable
//	 */
//	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//		String URI = request.getRequestURI();
//		logger.debug("processing uri " + URI);
//		//logger.info("xslRoot: '" + get)
//		Connection conn = null;
//		String dataSourceName = getDataSourceName(request);
//		String operation = "getConnection";
//		try {
//			String dexterousId = getUtDexterousId(request);
//			if (logger.isLoggable(Level.DEBUGR)) {
//				logger.log(Level.DEBUGR, "pdid " + dexterousId);
//			}
//			conn = Machine.getConnection(dataSourceName);
//			operation = "getUtDexterous";
//			UtDexterous utd = UtDexterousS.getForIdRecursively(conn, getUtDexterousId(request));
//			if (utd == null) {
//				throw new IllegalStateException("utd is null ");
//			}
//			String xml = utd.asXML();
//			logger.debug("xml is " + xml + Settings.newLine);
//			File styleSheet = getXslStyleSheet(request);
//
//			if (logger.isLoggable(Level.DEBUGR)) {
//				logger.debug("styleSheet is" + styleSheet.getCanonicalPath());
//			}
//			setOutputStream(xml, styleSheet, request, response);
//			logger.debug("transformation complete");
//		} catch (SQLException e) {
//			//e.getCause().printStackTrace();
//			e.printStackTrace();
//			throw new ServletException("during operation " + operation + " " + e.getMessage()); // + " " + e.getCause());
//		} catch (ServletRequestException e) {
//
//			e.printStackTrace();
//			throw new ServletException(e);
//		} finally {
//			if (conn != null) {
//				try {
//					conn.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//					logger.error(e.getMessage());
//				}
//			}
//		}
//
//	}
//
//	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//		doGet(request, response);
//	}
//
//	public void init() throws ServletException {
//		super.init();
//		validateState();
//	}
////	public void initialize(Element arguments) {
////		super.initialize(arguments);
////		validateState();
////
////	}
//
//	@Override
//	public void validateState() {
//		validateStyleSheet();
//		validateConnection();
//	}
//
//	private String getDataSourceName(HttpServletRequest request) {
//		if (request == null) {
//			throw new IllegalStateException("request is null");
//		}
//		String dataSourceName = request.getParameter("ds");
//		return dataSourceName == null ? "jdbc/default" : dataSourceName;
//	}
//
//	private String getUtDexterousId(HttpServletRequest request) throws ServletRequestException {
//		String id = request.getParameter("did");
//		if (id == null) {
//			throw new ServletRequestException("parameter 'did' not specified");
//		}
//		return id;
//	}
//
//	private File getXslStyleSheet(HttpServletRequest request) throws ServletException {
//		File returnValue = null;
//		String fileName = request.getParameter("xsl");
//		if (fileName == null) {
//			logger.debug("xsl not specified as parm");
//		}
//
//
//		if (fileName != null) {
//			xslRoot = getValue("xslRoot");
//			if (xslRoot == null) {
//				throw new IllegalArgumentException("xslRoot is not defined");
//			}
//			String scriptPath = xslRoot + "/" + fileName;
//			returnValue = new File(scriptPath);
//			if (!returnValue.canRead()) {
//				throw new ServletException("can't read " + scriptPath);
//			} else {
//				if (logger.isLoggable(Level.DEBUGR)) {
//					logger.debug("xsl specified: using stylesheet '" + scriptPath + "'");
//				}
//			}
//		} else {
//			returnValue = styleSheetFile;
//		}
//		if (returnValue == null) {
//			throw new IllegalStateException("can't find stylesheet");
//		}
//		return returnValue;
//	}
//
//	private void validateConnection() {
//       // @todo
//	}
//
//	private void validateStyleSheet() {
//		styleSheetFileName = getValue(XSL_FILE);
//		if (styleSheetFileName == null) {
//			throw new IllegalStateException("param " + XSL_FILE + " was not specified in initialization parameters");
//		}
//		styleSheetFile = new File(styleSheetFileName);
//
//		if (!styleSheetFile.canRead()) {
//			throw new IllegalArgumentException("can't read '" + styleSheetFileName + "'");
//		}
//		if (logger.isDebugEnabled()) {
//			logger.debug("default styleSheet set to " + styleSheetFile.getAbsolutePath());
//		}
//	}
//
//}
