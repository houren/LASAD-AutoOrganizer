package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import lasad.gwt.client.logger.ConsoleLogger;

/**
 * This class provides handling of debug messages within the Feedback Authoring Tool. 
 * @author Anahuac
 *
 */
public class FATDebug {
	
	private final static ConsoleLogger logger = ConsoleLogger.getInstance();
	
	public static final int DEBUG_OFF = 0;
	public static final int DEBUG_ON = 1;
	
	public static final int ERROR = 1;
	public static final int DEBUG = 2;
	public static final int WAR = 3;
	public static final int INFO = 4;
	
	public static final int DEBUG_FLAG = DEBUG_ON;
	public static final int DEBUG_MODE = DEBUG;
	
	public static void print(int mode, String msg){
		if(ERROR == mode || mode >= DEBUG_MODE){
			switch(mode){
				case ERROR: 
					logger.logErr("#ERROR " + msg);
					break;
				case DEBUG: 
					logger.log("#DEBUG " + msg);
					break;
				case WAR: 
					logger.log("#WAR " + msg);
					break;
				case INFO: 
					logger.log("#INFO " + msg);
					break;
				default: 
					logger.log(msg);
			}
		}
	}
	

}
