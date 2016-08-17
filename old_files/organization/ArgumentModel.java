package lasad.gwt.client.model.organization;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.elements.AbstractExtendedTextElement;

import com.extjs.gxt.ui.client.widget.Component;
// Aware that this is unnecessary, I just do it as a reminder in case I change location


/**
 *	An argument model is simply a vector of threads, i.e. a vector separate chains of arguments on the map space.
 *	This format of modeling is more conducive to support for AutoOrganizer.
 *	@author Kevin Loughlin
 *	@since 19 June 2015, Updated 30 June 2015
 */
public class ArgumentModel
{
	private Set<ArgumentThread> argThreads;
	private int fontSize = 10;
	private MVController controller;

	public ArgumentModel(String mapID)
	{
		this.argThreads = new HashSet<ArgumentThread>();
		controller = LASAD_Client.getMVCController(mapID);
	}
	
	// By Darlan Santana Farias
	public void setFontSize(int fontSize, boolean isOriginalCall){
		this.fontSize = fontSize;

		List<Component> mapComponents = LASAD_Client.getMapTab(controller.getMapID()).getMyMapSpace().getMyMap().getItems();
		for(Component mapComponent : mapComponents){
			if(mapComponent instanceof AbstractBox){
				AbstractBox box = (AbstractBox) mapComponent;
				if(box.getHeaderFontSize() != fontSize){
					box.setHeaderFontSize(fontSize);
					box.setSize(box.getWidth(), 100);
					for(AbstractExtendedElement element : box.getExtendedElements()){
						if(element instanceof AbstractExtendedTextElement){
							AbstractExtendedTextElement textEl = (AbstractExtendedTextElement)element;
							textEl.setFontSize(fontSize);
							box.textAreaCallNewHeightgrow(textEl.determineBoxHeightChange());
							break;
						}
					}
				}
			}
		}
		if(isOriginalCall)
			LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().changeFontSize(controller.getMapID(), fontSize));
	}
	
	public int getFontSize(){
		return this.fontSize;
	}

	public void addArgThread(ArgumentThread argThread)
	{
		this.argThreads.add(argThread);
	}

	public OrganizerLink removeLinkByLinkID(int linkID)
	{
		for (ArgumentThread argThread : argThreads)
		{
			OrganizerLink removedLink = argThread.removeLinkByLinkID(linkID);
			if (removedLink != null)
			{
				return removedLink;
			}
		}

		return null;
	}

	/**
	 *	Checks to see if the removal of the passed link warrants the creation of a new Thread, and creates the thread if needed
	 *	@param removedLink - The link removed from the model
	 */
	public void createNewThreadIfNecessary(OrganizerLink removedLink)
	{
		LinkedBox startBox = removedLink.getStartBox();
		LinkedBox endBox = removedLink.getEndBox();

		Set<LinkedBox> origThreadBoxes = new HashSet<LinkedBox>();

		Set<LinkedBox> possibleNewThreadBoxes = new HashSet<LinkedBox>();

		if (startBox != null && endBox != null)
		{
			origThreadBoxes = visitRecursive(startBox, new HashSet<LinkedBox>());
			possibleNewThreadBoxes = visitRecursive(endBox, new HashSet<LinkedBox>());
		}
		else
		{
			Logger.log("Nothing to do, link's boxes have already been removed", Logger.DEBUG);
			return;
		}

		if (origThreadBoxes.size() == possibleNewThreadBoxes.size() && origThreadBoxes.containsAll(possibleNewThreadBoxes))
		{
			// They're still in the same thread
			return;
		}
		else
		{
			ArgumentThread newThread = new ArgumentThread(possibleNewThreadBoxes);
			this.addArgThread(newThread);
			this.getBoxThread(startBox).removeBoxes(possibleNewThreadBoxes);
		}
	}

	/*
	 *	Goes through all the boxes directly/indirectly attached to box to see if we can reach all from the original thread
	 *	@param box - The box currently being visited, should be initialized as the endBox of the deleted link
	 *	@param boxesReached - The boxes so far reached, should be initialized as a new, empty HashSet
	 */
	private Set<LinkedBox> visitRecursive(LinkedBox box, Set<LinkedBox> boxesReached)
	{
		if (!boxesReached.contains(box))
		{
			boxesReached.add(box);
			for (LinkedBox childBox : box.getChildBoxes())
			{
				boxesReached = visitRecursive(childBox, boxesReached);
			}

			for (LinkedBox parentBox : box.getParentBoxes())
			{
				boxesReached = visitRecursive(parentBox, boxesReached);
			}

			for (LinkedBox siblingBox : box.getSiblingBoxes())
			{
				boxesReached = visitRecursive(siblingBox, boxesReached);
			}
		}

		return boxesReached;
	}

	public void removeArgThread(ArgumentThread argThread)
	{
		this.argThreads.remove(argThread);
	}

	// I.e. remove threads that are empty
	public void removeExcessThreads()
	{
		Set<ArgumentThread> threadsToRemove = new HashSet<ArgumentThread>();
		for (ArgumentThread argThread : argThreads)
		{
			Set<LinkedBox> boxes = argThread.getBoxes();
			if (boxes.size() == 0)
			{
				threadsToRemove.add(argThread);
			}
		}
		for (ArgumentThread argThread : threadsToRemove)
		{
			this.removeArgThread(argThread);
		}
	}

	// This is a "just in case" method that gets called to verify that there isn't an error with the argModel.
	public void removeLinksTo(LinkedBox removedBox)
	{
		ArgumentThread thread = this.getBoxThread(removedBox);
		if (thread != null)
		{
			Logger.log("ERROR: thread of removed box was not null. Fixing.", Logger.DEBUG);
			thread.removeBoxByBoxID(removedBox.getBoxID());
		}

		for (ArgumentThread argThread : this.getArgThreads())
		{
			argThread.removeLinksTo(removedBox);
		}
	}

	public Set<LinkedBox> getBoxes()
	{
		Set<LinkedBox> boxes = new HashSet<LinkedBox>();
		for (ArgumentThread argThread : this.getArgThreads())
		{
			boxes.addAll(argThread.getBoxes());
		}
		return boxes;
	}

	public LinkedBox getBoxByBoxID(int boxID)
	{
		LinkedBox returnBox = null;
		for (ArgumentThread argThread : this.argThreads)
		{
			returnBox = argThread.getBoxByBoxID(boxID);
			if (returnBox != null)
			{
				return returnBox;
			}
		}
		
		return null;
		
	}

	public LinkedBox removeBoxByBoxID(int boxID)
	{
		for (ArgumentThread argThread : argThreads)
		{
			LinkedBox removedBox = argThread.removeBoxByBoxID(boxID);
			if (removedBox != null){
				if(argThread.getBoxes().size() == 0){
					this.removeArgThread(argThread);
				}
				
				return removedBox;
			}
		}

		return null;
	}

	public LinkedBox getBoxByRootID(int rootID)
	{
		for (ArgumentThread argThread : argThreads)
		{
			LinkedBox box = argThread.getBoxByRootID(rootID);
			if (box != null)
			{
				return box;
			}
		}
		return null;
	}

	// Returns the argument thread of the provided box, else null
	public ArgumentThread getBoxThread(LinkedBox box)
	{
		for (ArgumentThread thread : argThreads)
		{
			if (thread.contains(box))
			{
				return thread;
			}
		}
		return null;
	}

	public Set<ArgumentThread> getArgThreads()
	{
		return argThreads;
	}

	public int getNumArgThreads()
	{
		return argThreads.size();
	}

	// Remember, top left is (0,0), not bottom left.  Gets the left most, right most, top most, and bottom most coord of boxes on grid
	public EdgeCoords calcEdgeCoords()
	{
		double top = Double.MAX_VALUE;
		double left = Double.MAX_VALUE;

		double bottom = Double.MIN_VALUE;
		double right = Double.MIN_VALUE;

		for (ArgumentThread thread : this.getArgThreads())
		{
			for (LinkedBox box : thread.getBoxes())
			{
				double yTop = box.getYTop();
				double yBottom = yTop + box.getHeight();

				double xLeft = box.getXLeft();
				double xRight = xLeft + box.getWidth();

				if (yTop < top)
				{
					top = yTop;
				}

				if (yBottom > bottom)
				{
					bottom = yBottom;
				}

				if (xLeft < left)
				{
					left = xLeft;
				}

				if (xRight > right)
				{
					right = xRight;
				}
			}
		}

		return new EdgeCoords(top, right, bottom, left);
	} 

	@Override
	public String toString()
	{
		int counter = 1;
		StringBuilder buffer = new StringBuilder("\n***********\nBEGIN ARGUMENT MODEL\n***********");
		for (ArgumentThread argThread : this.argThreads)
		{
			buffer.append("\n\tThread " + counter);
			buffer.append(argThread.toString());
			counter++;
		}
		buffer.append("\n***********\nEND OF MODEL\n***********\n");
		return buffer.toString();
	}
}