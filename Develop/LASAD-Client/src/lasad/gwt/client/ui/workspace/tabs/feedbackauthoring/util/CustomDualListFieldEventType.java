package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.widget.CustomDualListField;

import com.extjs.gxt.ui.client.event.EventType;

/**
 * Class to define events to listen for in {@link CustomDualListField}
 * @author Anahuac
 *
 */
public class CustomDualListFieldEventType{
	
	/**
	* MoveSelectedDown event type.
	*/
	public static final EventType MoveSelectedDown = new EventType();
	/**
	* MoveSelectedUp event type.
	*/
	public static final EventType MoveSelectedUp = new EventType();
	/**
	* AddSelected event type.
	*/
	public static final EventType AddSelected = new EventType();
	/**
	* RemoveSelected event type.
	*/
	public static final EventType RemoveSelected = new EventType();
	  

}
