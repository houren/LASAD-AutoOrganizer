package lasad.processors.specific;

/**
 *	The top and bottom most y, left and right most x coords, returned as ints
 *	@author	Kevin Loughlin
 *	@since	Who Knows, Updated 28 August 2015
 */
public class EdgeCoords
{
	int top;
	int right;
	int bottom;
	int left;

	public EdgeCoords(double top, double right, double bottom, double left)
	{
		this.top = (int) Math.round(Math.floor(top));
		this.right = (int) Math.round(Math.ceil(right));
		this.bottom = (int) Math.round(Math.ceil(bottom));
		this.left = (int) Math.round(Math.floor(left));
	}

	public int getTop()
	{
		return top;
	}

	public int getRight()
	{
		return right;
	}

	public int getBottom()
	{
		return bottom;
	}

	public int getLeft()
	{
		return left;
	}
}