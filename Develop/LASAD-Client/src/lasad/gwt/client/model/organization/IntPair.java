package lasad.gwt.client.model.organization;

/**
 *	Support for a pair of integers, which I am using as a max and min together in the same class for easier returns and speed.
 *	@author Kevin Loughlin
 *	@since 10 July 2015
 */
public class IntPair
{
	int min;
	int max;

	public IntPair(int min, int max)
	{
		this.min = min;
		this.max = max;
	}

	public int getMin()
	{
		return min;
	}

	public int getMax()
	{
		return max;
	}

	/**
	 *	The root is the top (max) height level if DOWNWARD is true and the opposite if false
	 */
	public int calcRoot(final boolean DOWNWARD)
	{
		if (DOWNWARD)
		{
			return max;
		}
		else
		{
			return min;
		}
	}
}