package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import com.extjs.gxt.ui.client.widget.Info;

/*
 * This class helps to display a toaster message
 */
public class ToasterMessage{

	public static void log(String logText) {
		Info.display(FeedbackAuthoringStrings.INFORMATION_LABEL, logText);
	}

	public static void logWar(String logText) {
		Info.display(FeedbackAuthoringStrings.WARNING, logText);
	}
	
	public static void logErr(String logText) {
		Info.display(FeedbackAuthoringStrings.ERROR_LABEL, logText);
	}

}
