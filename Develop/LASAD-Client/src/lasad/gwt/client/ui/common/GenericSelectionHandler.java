package lasad.gwt.client.ui.common;

import java.util.Vector;

import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;

public class GenericSelectionHandler {

private final AbstractGraphMap myMap;
	
	private Vector<SelectableInterface> selections = new Vector<SelectableInterface>();
	
	public GenericSelectionHandler(AbstractGraphMap abstractGraphMap) {
		this.myMap = abstractGraphMap;
	}

	public void selectElement(SelectableInterface el) {
		if(myMap.getMyArgumentMapSpace().isSelectionDetailsActive()) {

			// Unselect all other elements to make sure only one element at a time is selected
			for(SelectableInterface s : selections) {
				s.unselect();
			}
			selections.clear();
			
			// Select new element		
			selections.add(el);
			el.select();
		
		}
	}
	
	public void unselectElement(SelectableInterface el) {
		if(myMap.getMyArgumentMapSpace().isSelectionDetailsActive()) {
			try {
				selections.remove(el);
				el.unselect();
			}
			catch (Exception e){
				unselectElement(el);
			}
		}
	}
	
	public void unselectAll() {
		if(myMap.getMyArgumentMapSpace().isSelectionDetailsActive()) {
			for(int i=0; i<selections.size(); i++) {
				selections.get(i).unselect();
			}
			this.selections.removeAllElements();
		}
	}
	
	public Vector<SelectableInterface> getSelections() {
		return selections;
	}

	public void setSelections(Vector<SelectableInterface> selections) {
		this.selections = selections;
	}

	public AbstractGraphMap getMyMap() {
		return myMap;
	}
}
