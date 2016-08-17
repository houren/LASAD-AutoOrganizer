package lasad.gwt.client.ui.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;

public class GenericFocusHandler {

	private AbstractGraphMap map;

	private Vector<FocusableInterface> focusedElements = new Vector<FocusableInterface>();

	// Mapped Parent -> Child
	private Map<FocusableInterface,FocusableInterface> elementParent2ChildRelations = new HashMap<FocusableInterface,FocusableInterface>();

	public GenericFocusHandler(AbstractGraphMap abstractGraphMap) {
		this.map = abstractGraphMap;
	}

	public void releaseAllFocus() {
		for(FocusableInterface elm : focusedElements){
			elm.setElementFocus(false);
			elementParent2ChildRelations.remove(elm);
		}
		focusedElements.clear();
		map.getFadeHandler().releaseAllFadedOutElements();
	}
	
	public void releaseFocus(FocusableInterface element){
		// Check if focus is set
		if(!focusedElements.contains(element)){
			// Nothing to do
			return;
		}
		else {
			// Release Fokus from possible Child Element
			FocusableInterface child = elementParent2ChildRelations.get(element);
			if(child != null){
				// Release Focus from Child
				releaseFocus(child);
			}
			
			// Release Focus from this Element
			elementParent2ChildRelations.remove(element);
			focusedElements.remove(element);
			element.setElementFocus(false);
		}	
	}
	
	public void setFocus(FocusableInterface element){
		// Check if focus already set
		if(focusedElements.contains(element)){
			// Nothing to do
			return;
		}
		// Else:
		
		Vector<FocusableInterface> tobefocused  = new Vector<FocusableInterface>();
		
		
		// Work on Parent
		FocusableInterface parent = element.getFocusParent();
		
		while(parent != null){
			// Check if parent is already focused
			if(focusedElements.contains(parent)){
				// Parent is already focused, now check if an child is already focused
				FocusableInterface child = elementParent2ChildRelations.get(parent);
				while(child != null){
					// remove focus from old child
					child.setElementFocus(false);
					focusedElements.remove(child);
					
					// Work on child childs :-)
					FocusableInterface nextChild = elementParent2ChildRelations.get(child);
					elementParent2ChildRelations.remove(child);
					
					child = nextChild;
				}
				break;
			}
			else {
				// Parent actually not focused  
				tobefocused.add(parent);
			}
			parent = parent.getFocusParent();
		}
		
		
	
		// Add Element Focus to Element 
		tobefocused.add(element);

		
		for(FocusableInterface elm : tobefocused){
			focusedElements.add(elm);
			parent = elm.getFocusParent();
			if(parent != null){
				elementParent2ChildRelations.put(parent,elm);
			}
			elm.setElementFocus(true);
		}
	}
}
