package lasad.gwt.client.ui.common;

import com.google.gwt.user.client.Element;

/**
 * Allows a box or link to be selected
 * Required to show their details in the SelectionDetailsPanel
 * 
 * @author Frank Loll
 *
 */
public interface SelectableInterface {
	
	public void select();
	public void unselect();

	public Element getRootElement();
}
