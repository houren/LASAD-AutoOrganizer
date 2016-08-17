package lasad.gwt.client.ui.common.fade;

import java.util.Iterator;
import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * The FadeableElementHandler is used to fade elements (i.e. box and links) in and out (i.e. assign them a grey color). Its main purpose is to keep the overview in large argument maps.
 * 
 * @author Frank Loll
 *
 */
public class FadeableElementHandler {

	private AbstractGraphMap myMap;
	private MVController myController;
	private Vector<Integer> outFadedElements = new Vector<Integer>();
	private static Vector<Integer> notToBeFadedOut = new Vector<Integer>();
	
	public FadeableElementHandler(AbstractGraphMap myMap) {
		this.myMap = myMap;
	}
	
	public void fadeElement(int ID) {
		if(myController == null) {
			myController = LASAD_Client.getMVCController(myMap.getID());
		}
		
		if(notToBeFadedOut.size() > 0) {
			releaseAllFadedOutElements();
		}
		else {
			// Fade out all elements that are not directly connected to the concrete element
			Vector<Parameter> updateValues = new Vector<Parameter>();
			Parameter fadeParam = new Parameter(ParameterTypes.Faded, "TRUE");
			updateValues.add(fadeParam);
			
			// Choose elements that should not be faded out			
			notToBeFadedOut.add(ID); // The concrete element from which the actions was called
			
			// If object is a link protect the end and start item as well
			if (myController.getElement(ID).getParents().size() >= 2) {
				notToBeFadedOut.add(myController.getElement(ID).getParents().get(0).getId());
				notToBeFadedOut.add(myController.getElement(ID).getParents().get(1).getId());
			}
			
			// If object is a box, protect all links that are connected to the box as well as all boxes that are connected to these links
			Iterator<AbstractUnspecifiedElementModel> myProtectionIterator = myController.getAllElements().iterator();
			while(myProtectionIterator.hasNext()) {
				AbstractUnspecifiedElementModel item = myProtectionIterator.next();
				if(item.getParents().size() >= 2) {
					if(item.getParents().get(0).getId() == (int) ID || item.getParents().get(1).getId() == (int) ID) {
						notToBeFadedOut.add(item.getId());
						notToBeFadedOut.add(item.getParents().get(0).getId());
						notToBeFadedOut.add(item.getParents().get(1).getId());
					}	
				}
			}
		
			// Fade out all items that are not protected
			Iterator<AbstractUnspecifiedElementModel> myIterator = myController.getAllElements().iterator();
		
			while(myIterator.hasNext()) {
				AbstractUnspecifiedElementModel item = myIterator.next();
				if(item.getType().equalsIgnoreCase("box") || item.getType().equalsIgnoreCase("relation")) {
					boolean fadeOutThisItem = true;
					for(int a=0; a<notToBeFadedOut.size(); a++) {
						if(item.getId() == notToBeFadedOut.get(a).intValue()) {
							fadeOutThisItem = false;
						}
					}
					if(fadeOutThisItem) {
						fadeOutElement(item.getId(), updateValues);
					}
				}
				else {
					continue;
				}
			}
		}
	}

	private void fadeOutElement(int ID, Vector<Parameter> updateValues) {
		myController.updateElement(ID, updateValues);
		outFadedElements.add(ID);
	}
	
	/**
	 * Fade in all elements that are currently out-faded
	 */
	public void releaseAllFadedOutElements() {
		if(myController == null) {
			myController = LASAD_Client.getMVCController(myMap.getID());
		}
		
		if(outFadedElements.size()>0) {
			Vector<Parameter> updateValues = new Vector<Parameter>();
			Parameter fadeParam = new Parameter(ParameterTypes.Faded, "FALSE");
			updateValues.add(fadeParam);
			
			for(int i=0; i<outFadedElements.size(); i++) {
				myController.updateElement(outFadedElements.get(i), updateValues);
			}
			outFadedElements.clear();
			notToBeFadedOut.clear();
		}
	}
}