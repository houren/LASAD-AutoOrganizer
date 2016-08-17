package lasad.gwt.client.logger;

import java.util.Vector;

import lasad.gwt.client.settings.DebugSettings;

/**
 * Logging instance that is visible in the DEBUG Panel for users with role
 * "Developer"
 **/
public class Logger {

	private static Vector<LoggerInterface> logListener = new Vector<LoggerInterface>();
	
	public static final int DEBUG = 0;
	public static final int DEBUG_DETAILS = 1;
	public static final int DEBUG_ERRORS = 2;
	public static final int DEBUG_POLLING = 3;

	public static void addLogListener(LoggerInterface listener) {
		if (!logListener.contains(listener)) {
			logListener.add(listener);
		}
	}

	public static void removeLogListener(LoggerInterface listener) {
		if (logListener.contains(listener)) {
			logListener.remove(listener);
		}
	}

	/**
	 * Calls the log method on all registered LogListener
	 * 
	 * @param logText The text which which is supposed to be logged
	 * @param logType The logging type. Therefore Logger provides certain static values.
	 */
	public static void log(String logText, int logType) {
		switch (logType) {
		case DEBUG:
			if (DebugSettings.debug) doLog(logText);
			break;

		case DEBUG_DETAILS:
			if (DebugSettings.debug_details) doLog(logText);
			break;
			
		case DEBUG_ERRORS:
			if (DebugSettings.debug_errors) doErrLog(logText);
			break;
			
		case DEBUG_POLLING:
			if (DebugSettings.debug_polling) doLog(logText);
			break;
		}
	}
	
	private static void doLog(String logText) {
		for (LoggerInterface listener : logListener) {
			listener.log(logText);
		}
	}
	
	private static void doErrLog(String logText) {
		for (LoggerInterface listener : logListener) {
			listener.logErr(logText);
		}
	}
}