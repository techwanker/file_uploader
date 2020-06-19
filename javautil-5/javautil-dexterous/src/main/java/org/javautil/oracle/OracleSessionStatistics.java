//package org.javautil.oracle;
//
//
//
//	import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.ArrayList;
//
//import org.javautil.oracle.OracleSessionStatSet;
//import org.javautil.oracle.WrappedOracleConnection;
//import org.javautil.oracle.dao.OracleSessionWaits;
//import org.javautil.oracle.dto.OracleSessionWait;
//
//
//	public class OracleSessionStatistics {
//
//	        private final Logger logger = Logger.getLogger(getClass().getName());
//	        private static final String newline = System.getProperty("line.separator");
//
//
//	        public static void showStats(final Connection conn) throws SQLException {
//	                final Logger logger = Logger.getLogger(OracleSessionStatistics.class);
//	                if (conn instanceof WrappedOracleConnection) {
//	                        final WrappedOracleConnection oconn = (WrappedOracleConnection) conn;
//	                        final OracleSessionStatSet stats = oconn.getSessionStats();
//	                        final String waitsFormatted = stats.asString(oconn.getStatNames());
//	                        // Todo this is an exceptionally squirrelly way to do anything and will muck things up if Running\
//	                        // multiple threads against the root logger
//	                        /// todo strip dexterous
//	                        System.out.println("Dexterous showStats " + waitsFormatted);
//	                        final Level before = logger.getLevel();
//	                        final Logger l = Logger.getLogger("");
//	                        l.setLevel(Level.INFO);
//
//	                        l.info(waitsFormatted);
//	                        l.setLevel(before);
//	                } else {
//	                        logger.warn("show waits ignored - connection is not to an oracle database");
//	                }
//	        }
//
//	        public static void showWait(final Connection conn ) throws SQLException {
//	                final Logger logger = Logger.getLogger(OracleSessionStatistics.class);
//	                //logger.error("in showWait");
//	                if (conn instanceof WrappedOracleConnection) {
//	                        final WrappedOracleConnection oconn = (WrappedOracleConnection) conn;
//	                        final ArrayList<OracleSessionWait> waits = oconn.getSessionWaits();
//	                        final String waitsFormatted = OracleSessionWaits.format(waits);
//	                        final Level before = logger.getLevel();
//	                        final Logger l = Logger.getLogger("");
//	                        l.setLevel(Level.INFO);
//	                        System.out.println("Dexterous.showWait " + newline + waitsFormatted);
//	                        l.info(waitsFormatted);
//	                        l.setLevel(before);
//	                } else {
//	                        logger.warn("show waits ignored - connection is not to an oracle database");
//	                }
//	        }
// }
