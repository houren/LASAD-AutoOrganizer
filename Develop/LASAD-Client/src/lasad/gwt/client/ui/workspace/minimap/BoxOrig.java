package lasad.gwt.client.ui.workspace.minimap;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.util.Rectangle;

/**
 * Represents a rectangle of original values in a LASAD MiniMap.
 * 
 * @author David Drexler
 * 
 * @version 1.0
 */
public class BoxOrig extends Rectangle {

	/**
	 * The elementID of the original box.
	 */	
	public String elementID;
	
	/**
	 * ID list of connected relations.
	 */	
	public ArrayList<Integer> relationIDs;	
	
	
	/**
	 * Creates a new BoxOrig instance.
	 * 
	 * @param x			the x value
	 * @param y 		the y value
	 * @param width 	the rectangle's width
	 * @param height 	the rectangle's height
	 * @param elementID the ID name of the element type 
	 */
	public BoxOrig(int x, int y, int width, int height, String elementID) {
		this.x = x;
	    this.y = y;
	    this.width = width;
	    this.height = height;
	    this.elementID = elementID;
	    this.relationIDs = new ArrayList<Integer>();
	}
	
	
	/**
	 * Returns all values as string.
	 * 
	 * @return	all values as string	
	 */
	@Override	
	public String toString() {
		return "x: " + x + " y: " + y + " width: " + width + " height: " + height + " elementID: " + elementID + " relationIDs: " + relationIDs.toString();
	}
}
