package lasad.gwt.client.model.organization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lasad.gwt.client.logger.Logger;

/**
 *	An argument thread is a connected chain of boxes on the argument map space.
 *  This class is useful for AutoOrganizer.
 *	@author Kevin Loughlin
 *	@since 19 June 2015, Updated 29 July 2015
 */

public class ArgumentThread
{
	// HashMap allows for constant lookup time by BoxID
	private Map<Integer, LinkedBox> boxMap;

	private static int assigner = Integer.MIN_VALUE;

	private int threadID;

	// The chessboard set up used for auto organization of the map
	private ArgumentGrid grid;

	public ArgumentThread()
	{
		this.boxMap = new HashMap<Integer, LinkedBox>();
		this.grid = new ArgumentGrid();
		threadID = assigner;
		assigner++;
	}

	public ArgumentThread(LinkedBox box)
	{
		this();
		this.addBox(box);
	}

	public ArgumentThread(Set<LinkedBox> boxes)
	{
		this();
		for (LinkedBox box : boxes)
		{
			this.addBox(box);
		}
	}

	public Set<LinkedBox> getBoxes()
	{
		return new HashSet<LinkedBox>(boxMap.values());
	}

	// This is kinda a weird way of doing this but it works and seems neat enough, so I guess don't fix something that isn't broken? TBD
	public void organizeGrid(final boolean DOWNWARD) throws Exception
	{
		Exception myException = null;
		for (LinkedBox startBox : this.getBoxes())
		{
			// Fixes bug where certain startBoxes dont work for grid organization.  Would like to develop the algorithm so that startBox doesn't
			// matter, but this fixes the bug regardless
			try
			{
				this.setGrid(this.grid.organize(DOWNWARD, startBox));
				return;
			}
			catch (Exception e)
			{
				Logger.log("Auto Organization failed with startBox " + startBox.getRootID() + ".  Retrying with next box if available...", Logger.DEBUG);
				myException = e;
				this.grid.empty();
			}
		}

		if (myException != null)
		{
			throw myException;
		}
	}

	private void setGrid(ArgumentGrid grid)
	{
		this.grid = grid;
	}

	public ArgumentGrid getGrid()
	{
		return grid;
	}

	public void addBox(LinkedBox box)
	{
		if (boxMap.values().contains(box))
		{
			return;
		}
		else
		{
			boxMap.put(box.getBoxID(), box);
		}
	}

	public void addBoxes(Set<LinkedBox> boxes)
	{
		for (LinkedBox box : boxes)
		{
			this.addBox(box);
		}
	}

	public LinkedBox removeBoxByBoxID(int boxID)
	{
		return boxMap.remove(boxID);
	}

	public OrganizerLink removeLinkByLinkID(int linkID)
	{
		OrganizerLink finalReturn = null;
		boolean isFirstBox = true;
		for (LinkedBox box : this.getBoxes())
		{
			OrganizerLink returnLink = box.removeLinkByLinkID(linkID);
			if (returnLink != null)
			{
				if (isFirstBox)
				{
					isFirstBox = false;
					finalReturn = returnLink;
				}
				else
				{
					return returnLink;
				}
			}
		}
		return finalReturn;
	}

	public void removeLinksTo(LinkedBox boxBeingRemoved)
	{
		Set<LinkedBox> relatedBoxes = boxBeingRemoved.getRelatedBoxes();
		for (LinkedBox box : this.getBoxes())
		{
			if (relatedBoxes.contains(box))
			{
				box.removeLinkTo(boxBeingRemoved);
			}
		}
	}

	public void removeBoxes(Set<LinkedBox> boxes)
	{
		for (LinkedBox box : boxes)
		{
			this.removeBoxByBoxID(box.getBoxID());
		}
	}

	public boolean contains(LinkedBox box)
	{
		return boxMap.containsValue(box);
	}

	public LinkedBox getBoxByBoxID(int boxID)
	{
		return boxMap.get(boxID);
	}

	public LinkedBox getBoxByRootID(int rootID)
	{
		for (LinkedBox box : boxMap.values())
		{
			if (box.getRootID() == rootID)
			{
				return box;
			}
		}
		
		return null;
	}

	// Since ID would be the same, I can just remove and add the "newBox"
	public void replaceBox(LinkedBox newBox)
	{
		this.removeBoxByBoxID(newBox.getBoxID());
		this.addBox(newBox);
	}

	// Guarantees uniqueness
	@Override
	public int hashCode()
	{
		return this.threadID;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof ArgumentThread)
		{
			ArgumentThread other = (ArgumentThread) o;
			Set<LinkedBox> thisBoxes = this.getBoxes();
			int thisSize = thisBoxes.size();
			Set<LinkedBox> otherBoxes = other.getBoxes();
			if (thisSize != otherBoxes.size())
			{
				return false;
			}
			else if(thisBoxes.containsAll(otherBoxes))
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
		StringBuilder buffer = new StringBuilder("\n\tBEGIN ARGUMENT THREAD\n");
		for (LinkedBox box : boxMap.values())
		{
			buffer = buffer.append(box.toString());
		}

		buffer = buffer.append("\n\tEND ARGUMENT THREAD\n");
		return buffer.toString();
	}
}