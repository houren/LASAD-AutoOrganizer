package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.widget.CustomDualListField;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Class to listen for events in {@link CustomDualListField}
 * @author Anahuac
 *
 */
public class CustomDualListFieldListener implements Listener<CustomDualListFieldEvent>{
	
	public void handleEvent(CustomDualListFieldEvent e) {
	    EventType type = e.getType();
	    if (type == CustomDualListFieldEventType.MoveSelectedDown) {
	    	moveSelectedDown(e);
	    } else if (type == CustomDualListFieldEventType.MoveSelectedUp) {
	    	moveSelectedUp(e);
	    } else if (type == CustomDualListFieldEventType.AddSelected) {
	    	addSelected(e);
	    } else if (type == CustomDualListFieldEventType.RemoveSelected) {
	    	removeSelected(e);
	    }
	  }
	
	  /**
	   * Fires when the "move selected down" button is clicked.
	   * 
	   * @param e the CustomDualListField Event
	   */
	  public void moveSelectedDown(CustomDualListFieldEvent e) {

	  }

	  /**
	   * Fires when the "move selected up" button is clicked.
	   * 
	   * @param e the CustomDualListField Event
	   */
	  public void moveSelectedUp(CustomDualListFieldEvent e) {

	  }
	  
	  /**
	   * Fires when the "Add selected" button is clicked.
	   * 
	   * @param e the CustomDualListField Event
	   */
	  public void addSelected(CustomDualListFieldEvent e) {

	  }
	  
	  /**
	   * Fires when the "Remove selected" buttons is clicked.
	   * 
	   * @param e the CustomDualListField Event
	   */
	  public void removeSelected(CustomDualListFieldEvent e) {

	  }

}
