package lasad.gwt.client.model.organization;

/**
 *	A representation of a coordinate (column, row) point on the chessBoard grid for ArgumentGrid and autoOrganizer.
 *	Again, NOT the pixel location of a box, but rather the column and row indicator.
 *	@author Kevin Loughlin
 *	@since 15 July 2015
 */
public class Coordinate
{
	private int x;
	private int y;

	public Coordinate()
	{
		x = 0;
		y = 0;
	}

	public Coordinate(int x, int y)
	{
		this.setX(x);
		this.setY(y);
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public int hashCode()
	{
		return 31*(x^3) - y^3;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Coordinate)
		{
			Coordinate oAsCoord = (Coordinate) o;
			if (oAsCoord.getX() == this.getX() && oAsCoord.getY() == this.getY())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return new String("(" + x + ", " + y + ")");
	}
}