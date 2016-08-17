package lasad.gwt.client.settings;

/**
 * Global configuration file to enable different debugging and logging modes
 * 
 * TODO Implement it in all classes + a DEBUG_LOCAL for each specific class
 * 
 * @author Frank Loll
 * 
 */
public class DebugSettings {

	public static String version = "2.0.0";
	public static boolean debug = true;
	public static boolean debug_details = true;
	public static boolean debug_errors = true;
	public static boolean debug_polling = true;
	
	public static boolean isDebug() {
		return debug;
	}
	public static boolean isDebug_details() {
		return debug_details;
	}
	public static boolean isDebug_errors() {
		return debug_errors;
	}
	public static boolean isDebug_polling() {
		return debug_polling;
	}
	public static void setDebug(boolean debug) {
		DebugSettings.debug = debug;
	}
	public static void setDebug_details(boolean debug_details) {
		DebugSettings.debug_details = debug_details;
	}
	public static void setDebug_errors(boolean debug_errors) {
		DebugSettings.debug_errors = debug_errors;
	}
	public static void setDebug_polling(boolean debug_polling) {
		DebugSettings.debug_polling = debug_polling;
	}
}
