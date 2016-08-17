package lasad.gwt.client.ui.workspace.minimap;

/**
 * Represents a color in a decimal RGB color model.
 * 
 * @author David Drexler
 * 
 * @version 1.0
 */
public class Color {

	/**
	 * The decimal red value.
	 */
	public int red;

	/**
	 * The decimal green value.
	 */
	public int green;

	/**
	 * The decimal blue value.
	 */
	public int blue;
	
	
	/**
	 * Creates a new color instance.
	 */
	public Color() {

	}

	
	/**
	 * Creates a new color instance.
	 * 
	 * @param red 	decimal red value
	 * @param green decimal green value
	 * @param blue 	decimal blue value
	 */
	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}


	/**
	 * Returns all values as string.
	 * 
	 * @return	all values as string	
	 */
	public String toString() {
		return "red: " + red + " green: " + green + " blue: " + blue;
	}
}