package lasad.gwt.client.ui.workspace.minimap;

import java.util.ArrayList;

/**
 * Represents a relation of original values in a LASAD MiniMap.
 * 
 * @author David Drexler
 * 
 * @version 1.0
 */
public class RelationOrig extends Line {

	/**
	 * The element id of the original relation.
	 */
	public String elementID;

	/**
	 * ID of element 1.
	 */
	public int element1ID;
	
	/**
	 * ID of element 2.
	 */
	public int element2ID;

	/**
	 * True if element 1 is a box.
	 */
	public boolean box1;
	
	/**
	 * True if element 2 is a box.
	 */
	public boolean box2;	
	
	/**
	 * Percent position of note on relation.
	 */	
	public float percent;	
	
	/**
	 * ID list of connected relations.
	 */	
	public ArrayList<Integer> relationIDs;	


	/**
	 * Creates a new relation instance.
	 * 
	 * @param x1 			the x start value
	 * @param y1 			the y start value
	 * @param x2 			the x end value
	 * @param y2 			the y end value
	 * @param elementID 	the ID name of the element type 
	 * @param element1ID 	the ID of element 1
	 * @param element2ID	the ID of element 2
	 * @param box1 			True if element 1 is a box
	 * @param box2			True if element 2 is a box 
	 */
	public RelationOrig(int x1, int y1, int x2, int y2, String elementID, int element1ID, int element2ID, boolean box1, boolean box2, float percent) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.elementID = elementID;
		this.element1ID = element1ID;
		this.element2ID = element2ID;
		this.box1 = box1;
		this.box2 = box2;		
		this.percent = percent;
		this.relationIDs = new ArrayList<Integer>();
	}
	
	
	/**
	 * Returns all values as string.
	 * 
	 * @return	all values as string	
	 */
	@Override
	public String toString() {
		return "x1: " + x1 + " y1: " + y1 + " x2: " + x2 + " y2: " + y2 + " Element1ID: " + element1ID + " Element2ID: " + element2ID + " Percent: " + percent + " relationIDs: " + relationIDs.toString();
	}
}

