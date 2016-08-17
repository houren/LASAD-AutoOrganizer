package lasad.gwt.client.ui.workspace;

import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;


/**
 * 
 * @author Marcel Bergmann
 *
 */
public class LASADInfo {
	
	private static final int DISPLAY_TIME_IN_MILLISEC = 6000;
	
	private LASADInfo() {
		
	}
	
	/**
	 * Uses the GXT class Info to display messages for the user but
	 * with a custom configuration and the default message showing duration
	 * which is set in this class's constant.
	 * @param The text message's title
	 * @param The text message which is supposed to be displayed
	 */
	public static void display(String title, String message) {
		doDisplay(title, message, DISPLAY_TIME_IN_MILLISEC);
	}
	
	/**
	 * Uses the GXT class Info to display messages for the user but
	 * with a custom configuration. Choose this method if you want your
	 * message showing duration differ from the global settings.
	 * @param The text message's title
	 * @param The text message which is supposed to be displayed
	 * @param The custom message showing duration
	 */
	public static void display(String title, String message, int displayTime) {
		doDisplay(title, message, displayTime);
	}
	
	private static void doDisplay(String title, String message, int displayTime) {
		InfoConfig config = new InfoConfig(title, message);
		config.display = displayTime;
		Info.display(config);
	}
}
