package lasad.gwt.client.ui.common.highlight;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;

public class GenericHighlightHandler {
	//private AbstractGraphMap map;
	private Vector<HighlightableElementInterface> highlightedElements = new Vector<HighlightableElementInterface>();

	// Mapped Parent -> Child
	private Map<HighlightableElementInterface,HighlightableElementInterface> elementParent2ChildRelations = new HashMap<HighlightableElementInterface,HighlightableElementInterface>();
	
	public GenericHighlightHandler(AbstractGraphMap abstractGraphMap) {
		//this.map = abstractGraphMap;
	}
	
	public void releaseAllHighlights(){
		for(HighlightableElementInterface elm : highlightedElements){
			elm.setHighlight(false);
			elementParent2ChildRelations.remove(elm);
		}
		highlightedElements.clear();
	}

	public void releaseHighlight(HighlightableElementInterface element){
		// Check if focus is set
		if(!highlightedElements.contains(element)){
			// Nothing to do
			return;
		}
	
		// Release Fokus from possible Child Element
		HighlightableElementInterface child = elementParent2ChildRelations.get(element);
		if(child != null){
			// Release Focus from Child
			releaseHighlight(child);
		}
		
		// Release Focus from this Element
		elementParent2ChildRelations.remove(element);
		highlightedElements.remove(element);
		element.setHighlight(false);
	}
	
	public void setHighlight(HighlightableElementInterface element){
		// Check if focus already set
		if(highlightedElements.contains(element)){
			return;
		}
		
		Vector<HighlightableElementInterface> tobehighlighted  = new Vector<HighlightableElementInterface>();
			
		// Work on Parent
		HighlightableElementInterface parent = element.getHighlightParent();
		
		while(parent != null){
			// Check if parent is already focused
			if(highlightedElements.contains(parent)){
				// Parent is already focused, now check if an child is already focused
				HighlightableElementInterface child = elementParent2ChildRelations.get(parent);
				while(child != null){
					// remove focus from old child
					child.setHighlight(false);
					highlightedElements.remove(child);
					
					// Work on child childs :-)
					HighlightableElementInterface nextChild = elementParent2ChildRelations.get(child);
					elementParent2ChildRelations.remove(child);
					
					child = nextChild;
				}
				break;
			}
			else{
				// Parent actually not focused  
				tobehighlighted.add(parent);
			}
			parent = parent.getHighlightParent();
		}
		
		// Add Element Focus to Element 
		tobehighlighted.add(element);
		
		for(HighlightableElementInterface elm : tobehighlighted){
			highlightedElements.add(elm);
			parent = elm.getHighlightParent();
			if(parent != null){
				elementParent2ChildRelations.put(parent,elm);
			}
			elm.setHighlight(true);
		}
	}
}
