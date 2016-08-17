package lasad.gwt.client.ui.workspace.minimap;

/**
 * Represents a line in a coordinate system.
 * 
 * @author David Drexler
 * 
 * @version 1.0
 */
public class Line {

	/**
	 * The x start coordinate.
	 */
	public int x1;

	/**
	 * The y start coordinate.
	 */
	public int y1;

	/**
	 * The x end coordinate.
	 */
	public int x2;

	/**
	 * The y end coordinate.
	 */
	public int y2;
	
	
	/**
	 * Creates a new line instance.
	 */
	public Line() {

	}

	
	/**
	 * Creates a new line instance.
	 * 
	 * @param x1 the x start value
	 * @param y1 the y start value
	 * @param x2 the x end value
	 * @param y2 the y end value
	 */
	public Line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}


	/**
	 * Returns all values as string.
	 * 
	 * @return	all values as string	
	 */
	public String toString() {
		return "x1: " + x1 + " y1: " + y1 + " x2: " + x2 + " y2: " + y2;
	}
}