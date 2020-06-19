package org.javautil.dex.arguments;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.javautil.commandline.CommandLineHandler;
import org.javautil.commandline.ParamType;
import org.javautil.commandline.annotations.MultiValue;
import org.javautil.commandline.annotations.Optional;
import org.javautil.dex.DbexpertsEnvironment;
import org.javautil.document.MimeType;

public class DexterousArguments {

	public static final String revision = "$Revision: 1.1 $";

	private final Logger logger = Logger.getLogger(getClass());
	//
	// Variables set by command line processing
	//
	@Optional
	private String prompt = "dex> ";
	@Optional
	private String dataSourceName;
	@Optional
	@MultiValue(type = ParamType.FILE)
	private Collection<File> inputFiles = new ArrayList<File>();
	@Optional
	private String userName;
	@Optional
	private File configFile;
	@Optional
	private boolean echo = false;
	@Optional
	private boolean disableCache = true;

	private MimeType defaultMimeType = MimeType.CSV;
	@Optional
	private String defaultBaseDirectory = null;
	@Optional
	private Collection<File> stickyCommandFiles;
	@Optional
	private final String stickyViolationPolicy = "abort";

	@Optional
	private boolean verifyOnly;

	private final Collection<Integer> events = new HashSet<Integer>();

	private boolean dieOnParseError = true;
	/**
	 * TODO move all to log4j.xml
	 */
	private boolean logMethodOnly;
	@Optional
	private String logFileNameMask;
	@Optional
	private String logFileMask;

	private final Level fileLoggerLevel = Level.WARN;

	private final Level stdoutLoggerLevel = Level.WARN;

	private final boolean logMethod = false;

	private final boolean logTime = false;

	private File logDirectory = null;

	// public static final String DEXTEROUS_LOG_DIR = "DEXTEROUS_LOG_DIR";

	// private final StringParam logFileMaskOption = new
	// StringParam("logFileNameMask","mask for file name ");
	//
	// private final FileParam logDirectoryOption = new
	// FileParam("logDirectory",
	// "directory in which log files are to be created",
	// FileParam.IS_WRITEABLE,
	// Parameter.OPTIONAL,
	// Parameter.SINGLE_VALUED);
	// //
	// // Input into command line processing
	// //
	//
	// //
	// // Arguments
	// //
	// private final FileParam inputFilesArg = new FileParam("file",
	// "files to be processed",
	// FileParam.IS_READABLE,
	// Parameter.OPTIONAL,
	// Parameter.MULTI_VALUED);
	//
	// //
	// // options
	// //
	//
	// //private ArrayList<Parameter> argList = new ArrayList<Parameter>();
	//
	// private final Parameter arguments [] = new Parameter[] {inputFilesArg};
	//
	// private final StringParam dataSourceOption = new
	// StringParam("dataSource",
	// "name of the dataSource");
	//
	// private final StringParam promptOption = new
	// StringParam("prompt","the prompt text");
	//
	// private final StringParam userNameOption = new
	// StringParam("username","username or username/password");
	// /**
	// * The option for defining the requirements for the configuration file.
	// */
	// private final FileParam configFileOption = new
	// FileParam("config","configuration file ",
	// FileParam.IS_FILE & FileParam.IS_READABLE,
	// Parameter.OPTIONAL,
	// Parameter.SINGLE_VALUED);
	//
	// private final FileParam stickyCommandFilesOption = new
	// FileParam("stickyCommands","file containing commands that may not be over-ridden in script files ",
	// FileParam.IS_FILE &
	// FileParam.IS_READABLE,Parameter.OPTIONAL,Parameter.MULTI_VALUED);
	//
	// private final FileParam defaultBaseDirectoryOption = new
	// FileParam("baseDirectory", "base directory for spooled output",
	// FileParam.IS_DIR & FileParam.IS_WRITEABLE,
	// Parameter.OPTIONAL, Parameter.SINGLE_VALUED);
	//
	//
	// private final String[] logLevels =
	// {"SEVERE","WARNING","INFO","FINE","FINER","FINEST",
	// "severe","warning","info","fine","finer","finest",
	// };
	// private final StringParam stdoutLogLevelOption = new
	// StringParam("stdoutLoggerLevel","stdout logging level ",logLevels);
	// private final StringParam fileLogLevelOption = new
	// StringParam("fileLoggerLevel", "log level for log file", logLevels);
	// private final StringParam mimeTypeOption = new
	// StringParam("mimeType","default mime type ");
	//
	// private final String[] stickyViolationPolicies = new String[]
	// {"ignore","warn","abort"};
	// private final StringParam stickyViolationPolicyOption = new
	// StringParam("stickyViolationPolicy",
	// "action to take when a sticky rule is violated",
	// stickyViolationPolicies, // restricted values
	// Parameter.OPTIONAL,
	// Parameter.SINGLE_VALUED);
	//
	// private final BooleanParam verifyOnlyOption = new
	// BooleanParam("verifyOnly","verify that the script will work but set select timeout to 1 ms");
	//
	// private final BooleanParam disableCacheOption = new
	// BooleanParam("disableCache","disable Cache default " + disableCache);
	//
	// private final BooleanParam echoOption = new
	// BooleanParam("echo","echo input");
	//
	// private final BooleanParam logTimeOption = new
	// BooleanParam("logTime","timestamp log messages");
	// private final BooleanParam logMethodOption = new
	// BooleanParam("logMethod","log method and class");
	// private final BooleanParam logMethodOnlyOption = new
	// BooleanParam("logMethodOnly",
	// "log the method name but not the class name that generated the message");
	//
	// private final String helpText = "Dexterous command line interface. " +
	// "lots of options";
	//
	//
	// private final StringParam eventOption = new StringParam("event",
	// "eventName example '-event=TRANSFORM,8'",
	// null, // acceptable values
	// Parameter.OPTIONAL, //
	// Parameter.MULTI_VALUED);
	//
	// private final Parameter [] options = new Parameter[] { dataSourceOption,
	// promptOption, userNameOption, configFileOption, echoOption,
	// stdoutLogLevelOption,
	// disableCacheOption, mimeTypeOption, defaultBaseDirectoryOption,
	// stickyViolationPolicyOption, stickyCommandFilesOption, eventOption,
	// verifyOnlyOption,
	// logTimeOption, logMethodOption, logMethodOnlyOption, logDirectoryOption,
	// logFileMaskOption};
	//
	// private final BuildInfo bi = new BuildInfo();
	// private final CmdLineHandler cl = new
	// VersionCmdLineHandler(bi.getVersion(),
	// new HelpCmdLineHandler(helpText, "dexterous",
	// "dexter command line interface",
	// options,
	// arguments));//

	// private String logFileName;
	//
	//
	//
	// public DexterousArguments(){
	//
	// //printHelp();
	// //startHelp();
	//
	// stdoutLogLevelOption.setAcceptableValues(logLevels);
	//
	// mimeTypeOption.setAcceptableValues(MimeType.toStringArray());
	// stickyViolationPolicyOption.setAcceptableValues(stickyViolationPolicies);
	// }
	//
	// public String getArguments() {
	// final MessageBuilder b = new MessageBuilder();
	// b.appendLine("dataSource", dataSourceName);
	// b.appendLine("prompt",prompt);
	// b.appendLine("input files:");
	// for (final File file : inputFiles) {
	// b.appendLine(file.getAbsolutePath());
	// }
	// b.appendLine("userName",userName);
	// if (configFile != null) {
	// b.appendLine("configFile",configFile.getAbsolutePath());
	// }
	// b.appendLine("echo",echo);
	// b.appendLine("disableCache",disableCache);
	// b.appendLine("defaultBaseDirectory",defaultBaseDirectory);
	// b.appendLine("sticky command files");
	// for (final File file : stickyCommandFiles) {
	// b.appendLine(file.getAbsolutePath());
	// }
	// b.appendLine("stickyViolationPolicy",stickyViolationPolicy);
	// b.appendLine("echo",echo);
	// b.appendLine("disableCache",disableCache);
	// b.appendLine("defaultMimeType",defaultMimeType.toString());
	// b.appendLine("verifyOnly",verifyOnly);
	// b.appendLine("logMethod",logMethod);
	// b.appendLine("logTime",logTime);
	// b.appendLine("logDirectory",logDirectory.getAbsolutePath());
	// b.appendLine("dieOnParseError",dieOnParseError);
	// b.appendLine("logMethodOnly",logMethodOnly);
	// b.appendLine("logFileNameMask",logFileNameMask);
	// b.appendLine("logFileMask",logFileMask);
	// b.appendLine("fileLoggerLevel",fileLoggerLevel.toString());
	// b.appendLine("stdoutLoggerLevel",stdoutLoggerLevel.toString());
	// return b.toString();
	// }

	public File getConfigFile() {
		return configFile;
	}

	/**
	 * @return the dataSourceName
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * @return the defaultBaseDirectory
	 */
	public String getDefaultBaseDirectory() {
		return defaultBaseDirectory;
	}

	/**
	 * @return the defaultMimeType
	 */
	public MimeType getDefaultMimeType() {
		return defaultMimeType;
	}

	public Collection<Integer> getEvents() {
		return events;
	}

	public Level getFileLoggerLevel() {
		return fileLoggerLevel;
	}

	/**
	 * @return the inputFiles
	 */
	public Collection<File> getInputFiles() {
		return inputFiles;
	}

	public File getLogDirectory() {
		if (logDirectory == null) {
			final String logDir = System.getenv("DEXTEROUS_LOG_DIR");
			if (logDir != null) {
				logger.info("using environment variable "
						+ DbexpertsEnvironment.DEXTEROUS_LOG_DIR);
				final File logDirFile = new File(logDir);
				if (!logDirFile.canWrite()) {
					throw new IllegalArgumentException("can't write to "
							+ logDirFile.getAbsolutePath());
				}
				logDirectory = logDirFile;
			} else {
				logDirectory = new File(".");
			}
		}
		return logDirectory;
	}

	public String getLogFileNameMask() {

		return logFileNameMask;
	}

	/**
	 * @return the prompt
	 */
	public String getPrompt() {
		return prompt;
	}

	/**
	 * @return the stdoutLogLevel
	 */
	public Level getStdoutLoggerLevel() {
		return stdoutLoggerLevel;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the dieOnParseError
	 */
	public boolean isDieOnParseError() {
		return dieOnParseError;
	}

	/**
	 * @return the disableCache
	 */
	public boolean isDisableCache() {
		return disableCache;
	}

	/**
	 * @return the echo
	 */
	public boolean isEcho() {
		return echo;
	}

	public boolean isLogMethod() {
		return logMethod;
	}

	public boolean isLogMethodOnly() {
		return logMethodOnly;
	}

	public boolean isLogTime() {
		return logTime;
	}

	// public boolean isVerifyOnly() {
	// boolean returnValue = false;
	// if (verifyOnlyOption.isSet()) {
	// returnValue = true;
	// }
	// return returnValue;
	// }

	public void printHelp() throws IOException {
		final String helpResource = "org.javautil/dex/DexterousHelp.txt";
		new StringBuffer();
		BufferedReader in;
		try {
			final InputStream stream = getClass().getClassLoader()
					.getResourceAsStream(helpResource);
			if (stream == null) {
				throw new java.lang.IllegalArgumentException(
						"irresolvable resource " + helpResource);
			}
			in = new BufferedReader(new InputStreamReader(stream, "8859_1"));
			while (true) {
				final String line = in.readLine();
				if (line == null) {
					break;
				}
				System.out.println(line);
				// buff.append(line);
			}
		} catch (final java.io.IOException i) {
			throw new java.io.IOException("Failed to load resource: '"
					+ helpResource + "'\n" + i.getMessage());
		}

	}

	public void processArguments(final String[] args) {
		CommandLineHandler clh = new CommandLineHandler(this);
		clh.evaluateArguments(args);
	}

	// public void processArguments(final String[] args) {
	// cl.setDieOnParseError(dieOnParseError);
	// cl.parse(args);
	//
	// final String message = cl.getParseError();
	// if (message != null) {
	// System.out.println(message);
	// System.out.println(cl.getUsage(true));
	// throw new IllegalArgumentException(message);
	// }
	// //
	// dataSourceName = dataSourceOption.getValue();
	//
	// prompt = promptOption.getValue() == null ? "dex> " :
	// promptOption.getValue();
	// inputFiles = (Collection<File>) inputFilesArg.getFiles();
	// userName = userNameOption.getValue();
	// if (configFileOption.isSet()) {
	// configFile = configFileOption.getFile();
	// }
	// if (echoOption.isSet()) {
	// echo = echoOption.isTrue();
	// }
	// if (stdoutLogLevelOption.isSet()) {
	// final String val = stdoutLogLevelOption.getValue();
	// stdoutLoggerLevel = Level.toLevel(val.toUpperCase());
	// System.out.println("DexterousArguments log level " + stdoutLoggerLevel);
	// }
	// if (fileLogLevelOption.isSet()) {
	// final String val = fileLogLevelOption.getValue();
	//
	// fileLoggerLevel = Level.toLevel(val.toUpperCase());
	// System.out.println("DexterousArguments log level " + fileLoggerLevel);
	// }
	// if (mimeTypeOption.isSet()) {
	// defaultMimeType = MimeType.parse(mimeTypeOption.getValue());
	// }
	//
	// if (defaultBaseDirectoryOption.isSet()) {
	// defaultBaseDirectory = defaultBaseDirectoryOption.getValue();
	// }
	//
	// if (stickyCommandFilesOption.isSet()) {
	// stickyCommandFiles = stickyCommandFilesOption.getValues();
	// }
	//
	// if (stickyViolationPolicyOption.isSet() ) {
	// stickyViolationPolicy = stickyViolationPolicyOption.getValue();
	// }
	//
	// if (eventOption.isSet()) {
	// final Collection<String> eventStrings = eventOption.getValues();
	// for (final String s : eventStrings) {
	// try {
	// final Integer i = Integer.parseInt(s);
	// events.add(i);
	// } catch (final NumberFormatException nfe) {
	// throw new IllegalArgumentException("event number '" + s +
	// "' cannot be parsed as an integer" );
	// }
	// }
	//
	// }
	//
	// if (configFileOption.isSet() ) {
	// configFile = configFileOption.getFile();
	// }
	// // if (eventOption.isSet()) {
	// // events = eventOption.getValues();
	// // }
	// logMethod = logMethodOption.isTrue();
	// logMethodOnly = logMethodOnlyOption.isTrue();
	// logTime = logTimeOption.isTrue();
	// if (logDirectoryOption.isSet()) {
	// logDirectory = logDirectoryOption.getFile();
	// }
	//
	// /**
	// * environment, then command line option, then default
	// */
	// if (logFileMaskOption.isSet()) {
	// logFileMask = logFileMaskOption.getValue();
	// } else {
	// final String env = System.getenv(DEXTEROUS_LOG_MASK);
	// if (env != null) {
	// logFileMask = env;
	// }
	// }
	// }

	/**
	 * @param dataSourceName
	 *            the dataSourceName to set
	 */
	public void setDataSourceName(final String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	/**
	 * @param defaultBaseDirectory
	 *            the defaultBaseDirectory to set
	 */
	public void setDefaultBaseDirectory(final String defaultBaseDirectory) {
		this.defaultBaseDirectory = defaultBaseDirectory;
	}

	/**
	 * @param defaultMimeType
	 *            the defaultMimeType to set
	 */
	public void setDefaultMimeType(final MimeType defaultMimeType) {
		this.defaultMimeType = defaultMimeType;
	}

	/**
	 * @param dieOnParseError
	 *            the dieOnParseError to set
	 */
	public void setDieOnParseError(final boolean dieOnParseError) {
		this.dieOnParseError = dieOnParseError;
	}

	/**
	 * @param disableCache
	 *            the disableCache to set
	 */
	public void setDisableCache(final boolean disableCache) {
		this.disableCache = disableCache;
	}

	/**
	 * @param echo
	 *            the echo to set
	 */
	public void setEcho(final boolean echo) {
		this.echo = echo;
	}

	/**
	 * @param inputFiles
	 *            the inputFiles to set
	 */
	public void setInputFiles(final Collection<File> inputFiles) {
		this.inputFiles = inputFiles;
	}

	/**
	 * @param prompt
	 *            the prompt to set
	 */
	public void setPrompt(final String prompt) {
		this.prompt = prompt;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public boolean isVerifyOnly() {
		// TODO Auto-generated method stub
		return verifyOnly;
	}

	public Collection<File> getStickyCommandFiles() {
		return stickyCommandFiles;
	}

	public void setStickyCommandFiles(Collection<File> stickyCommandFiles) {
		this.stickyCommandFiles = stickyCommandFiles;
	}

	public String getLogFileMask() {
		return logFileMask;
	}

	public void setLogFileMask(String logFileMask) {
		this.logFileMask = logFileMask;
	}

	public String getStickyViolationPolicy() {
		return stickyViolationPolicy;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	public void setVerifyOnly(boolean verifyOnly) {
		this.verifyOnly = verifyOnly;
	}

	public void setLogFileNameMask(String logFileNameMask) {
		this.logFileNameMask = logFileNameMask;
	}

	public void setLogDirectory(File logDirectory) {
		this.logDirectory = logDirectory;
	}

	// public void showArguments() {
	// System.out.println(getArguments());
	// }
	//
	//
	//
	//
	//
	// @Override
	// public String toString() {
	// return getArguments();
	// }

	// private void startHelp() throws IOException {
	// final File temp = new File("/tmp/index.html");
	// final BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
	// final String helpResource = "org.javautil/dex/webhelp/Index.txt";
	// final StringBuffer buff = new StringBuffer();
	// BufferedReader in;
	// try {
	// final InputStream stream =
	// getClass().getClassLoader().getResourceAsStream(helpResource);
	// if (stream == null) {
	// throw new java.lang.IllegalArgumentException("irresolvable resource " +
	// helpResource);
	// }
	// in = new BufferedReader(new InputStreamReader(stream, "8859_1"));
	// while (true) {
	// final String line = in.readLine();
	// if (line == null) {
	// break;
	// }
	// bw.write(line);
	// bw.write("\n");
	// }
	// bw.close();
	// final Runtime r = Runtime.getRuntime();
	// r.exec("firefox " + temp.getCanonicalPath());
	// } catch (final java.io.IOException i) {
	// throw new java.io.IOException("Failed to load resource: '" + helpResource
	// + "'\n" + i.getMessage());
	// }
	// }

}
