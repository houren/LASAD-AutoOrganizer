package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event;

import java.util.HashMap;

/**
 * Abstract class to help the processing of user events on actions buttons
 * @author Anahuac
 *
 */
public abstract class UserActionEventImpl  implements UserActionEvent{
	private HashMap<ActionUserEventProperty, String> propertiesMap = new HashMap<ActionUserEventProperty, String>();
	
	public String getProperty(ActionUserEventProperty name){
		return propertiesMap.get(name);
	}
	
	public void addProperty(ActionUserEventProperty name, String value){
		propertiesMap.put(name, value);
	}
	
}
