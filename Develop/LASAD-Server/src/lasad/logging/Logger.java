package lasad.logging;

import java.util.Vector;

import lasad.logging.commonformat.CommonFormatLogger;
import lasad.logging.debug.DebugLogger;
import lasad.shared.communication.objects.ActionPackage;

public class Logger {

	private static Vector<LoggerInterface> logger = new Vector<LoggerInterface>();

	private static final boolean IsInDebugMode = true;

	public static void addLogger(LoggerInterface loggerImplementation) {
		logger.add(loggerImplementation);
	}

	public static void doCFLogging(ActionPackage p) {
		try {
			logPackage2CF(p);
		} catch (Exception e) {
			debugLog(e.toString());
		}
	}

	public static void log(String s) {
		if (IsInDebugMode) {
			for (LoggerInterface l : logger) {
				l.log(getLine(s));
			}
		}
	}

	private static String getLine(String s) {
		if (!s.endsWith("\n")) {
			return s += "\n";
		} else {
			return s;
		}
	}

	public static void logPackage(ActionPackage p) {
		if (IsInDebugMode) {
			for (LoggerInterface l : logger) {
				if (!(l instanceof CommonFormatLogger)) {
					l.log(p);
				}
			}
		}
	}

	public static void logError(String s) {
		for (LoggerInterface l : logger) {
			l.log(getLine(s));
		}
	}

	public static void debugLog(String s) {
		if (IsInDebugMode) {
			for (LoggerInterface l : logger) {
				if (l instanceof DebugLogger) {
					l.log(getLine(s));
				}
			}
		}
	}

	public static void logPackage2CF(ActionPackage p) {
		for (LoggerInterface l : logger) {
			if (l instanceof CommonFormatLogger) {
				l.log(p);
			}
		}
	}

	public static void endLogging() {
		for (LoggerInterface l : logger) {
			l.endLogging();
		}

	}

}
