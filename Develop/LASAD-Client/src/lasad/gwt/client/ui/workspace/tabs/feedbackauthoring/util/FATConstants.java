package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

/**
 * Constants used in the Feedback Authoring Tool. 
 * @author Anahuac
 *
 */
public class FATConstants {
	public static final String IMAGE_PATH = "resources/images/feedbackauthoring/";
	public static final String FA_ONTOLOGY_NAME = "FeedbackAuthoring";
	
	public static final int SLIDER_MIN_VAL = 0;
	public static final int SLIDER_MAX_VAL = 9;
	public static final int SLIDER_DEFAULT_VAL = 3;
	public static final int SLIDER_INC_VAL = 1;
	
	
	public static final int AGENT_STATUS_COMPILED = 1; //agent configuration has been sent to Feedback Engine
	public static final int AGENT_STATUS_ERROR = 2;  //agent configuration contains error(s)
	public static final int AGENT_STATUS_OK = 3;  //agent configuration doesn't have error and is ready to be compiled
	
	//Column types
	public static final int TEXT_TYPE = 1; 
	
	public static final int ADD_TYPE = 0;
	public static final int EDIT_TYPE = 1;
	
	public static final String STD_MSG = "std-message";
	
}
