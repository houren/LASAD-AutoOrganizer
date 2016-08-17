package lasad.gwt.client.model.organization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.elements.AbstractExtendedTextElement;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.shared.communication.objects.ActionPackage;

import com.extjs.gxt.ui.client.widget.Component;
// I'm aware that importing from the same package is unnecessary, but I do it in case I change the package of this class ever.

/**
 *	An AutoOrganizer can clean up the user's workspace into a clearer visual representation of the argument.
 *	It can also update links in ArgumentMap representations where a type of relation can create groups of boxes.
 *	
 *	The overall map organizing method, accordingly called organizeMap(), is only called when the user clicks the corresponding
 *	button on the ArgumentMapMenuBar. Though originally built for maps using Mara Harrell's template, this class can be applied
 *	to any map from any template.  There is a model (see ArgumentModel.java) that updates with every change to the map.
 *	Thus, we don't need to start from scratch and gather components to update "group" links.
 *	For organizeMap, we start from scratch - reconstructing the ArgumentGrid each time.
 *	@author Kevin Loughlin and Darlan Santana Farias
 *	@since 12 June 2015, Updated 28 August 2015
 */
public class AutoOrganizer
{
	// The width that all boxes will take upon organization
	private int boxWidth = 200;

	// The minimum height boxes will take upon organization (if the box needs more height to fit text, it will expand)
	private int minBoxHeight = 100;

	// The default minimum space between rows of boxes when organized
	private final double DEFAULT_MIN_VERT_SPACE = 50.0;

	private final boolean DEBUG = false;

	// The maximum number of siblings (grouped boxes) a box can have
	private final int MAX_SIBLINGS = 2;

	// Perfectly centered box location
	private final double CENTER_X = 2400.0;
	private final double CENTER_Y = CENTER_X;

	// The orientation of the maps links (true for down, false for up)
	private boolean downward;

	// The map that this instance of AutoOrganizer corresponds to
	private AbstractGraphMap map;

	// For sending map updates to the server
	private LASADActionSender communicator = LASADActionSender.getInstance();
	private ActionFactory actionBuilder = ActionFactory.getInstance();
	private MVController controller;

	// The organization model that we will update (the actual model the server updates is contained within the controller)
	private ArgumentModel argModel;

	/**
	 *	The only constructor that should be used
	 *	@param map - The argument map for this instance of AutoOrganizer
	 */
	public AutoOrganizer(AbstractGraphMap map)
	{
		this.map = map;
		controller = LASAD_Client.getMVCController(map.getID());
		argModel = map.getArgModel();
		downward = false;
	}

	public boolean getOrientation()
	{
		return downward;
	}

	public void setOrientation(boolean downward)
	{
		this.downward = downward;
	}

	public void setBoxWidth(int width)
	{
		this.boxWidth = width;
	}

	public void setMinBoxHeight(int minBoxHeight)
	{
		this.minBoxHeight = minBoxHeight;
	}

	public int getBoxWidth()
	{
		return boxWidth;
	}

	public int getMinBoxHeight()
	{
		return minBoxHeight;
	}

	/**
	 *	Organizes the map either top to bottom or bottom to top.  A clean-up function for the workspace.
	 */
	public void organizeMap()
	{
		// Begin as default, but update later if more space is needed (ex. maybe a link has a large panel)
		double minVertSpace = DEFAULT_MIN_VERT_SPACE;

		final boolean DOWNWARD = downward;
		Logger.log("[lasad.gwt.client.model.organization.AutoOrganizer] Beginning organizeMap", Logger.DEBUG);

		communicator.sendActionPackage(actionBuilder.startAutoOrganization(map.getID()));

		// Try catch just because organizeMap is complicated and if there's an error we don't want a crash
		try
		{
			Set<LinkedBox> boxesToSendToServer = new HashSet<LinkedBox>();

			double columnXcoord = CENTER_X;

			// Organize the grid by height and width "levels" (think chess board)
			for (ArgumentThread argThread : argModel.getArgThreads())
			{
				// organizes the grid, and we will then base coordinate positions from this grid organization
				argThread.organizeGrid(DOWNWARD);
				ArgumentGrid grid = argThread.getGrid();
				if (grid.getBoxes().size() == 0)
				{
					continue;
				}

				// Take into account the possibility of large elements on the link panel when determining vertical spacing between rows
				List<Component> mapComponents = map.getItems();
				for (Component mapComponent : mapComponents)
				{
					if(mapComponent instanceof AbstractLinkPanel)
					{
						AbstractLinkPanel linkPanel = (AbstractLinkPanel) mapComponent;
						if (linkPanel.getMyLink().getExtendedElements().size() != 0)
						{
							minVertSpace = (int) Math.max(minVertSpace, linkPanel.getSize().height + DEFAULT_MIN_VERT_SPACE);
						}
					}
				}

				// Important to make a copy so that we don't modify the actual mapComponents when we remove nonbox elements for below loop speed
				List<Component> mapComponentsCopy = new ArrayList<Component>(mapComponents);
				ArrayList<Component> toRemove = new ArrayList<Component>();

				/*	We need to call setSize from abstractBox, so we temporarily shift from LinkedBoxes to AbstractBoxes
					I'm aware it's annoying that we have two types of boxes representing the same thing, but it made life a lot easier
					for auto organization to have almost everything I needed in one place (LinkedBox) */
				for (LinkedBox box : grid.getBoxes())
				{
					for (Component mapComponent : mapComponentsCopy)
					{
						if (mapComponent instanceof AbstractBox)
						{
							AbstractBox myBox = (AbstractBox) mapComponent;
							if (myBox.getConnectedModel().getId() == box.getBoxID())
							{
								/*	The setSize method will update the textBox appropriately.  We want the box height to be just enough to fit the text
									By shrinking it first, we allow it to be the minimum height and width possible */
								toRemove.add(mapComponent);
								myBox.setSize(boxWidth, minBoxHeight);
								box.setSize(boxWidth, minBoxHeight);

								for (AbstractExtendedElement childElement : myBox.getExtendedElements())
								{
									if (childElement instanceof AbstractExtendedTextElement)
									{
										// Updates the LinkedBox instance to correspond with the AbstractBox
										AbstractExtendedTextElement textElt = (AbstractExtendedTextElement) childElement;
										myBox.textAreaCallNewHeightgrow(textElt.determineBoxHeightChange());
										box.setHeight(myBox.getHeight());
										break;
									}
								}
								updateBoxSize(box);
								break;
							}
						}
						else
						{
							toRemove.add(mapComponent);
						}
					}
					mapComponentsCopy.removeAll(toRemove);
				}
				
				IntPair minMaxColumn = ArgumentGrid.determineMinMaxWidthLevels(grid.getBoxes());
				final int MIN_WIDTH_LEVEL = minMaxColumn.getMin();
				final int MAX_WIDTH_LEVEL = minMaxColumn.getMax();

				IntPair minMaxRow = ArgumentGrid.determineMinMaxHeightLevels(grid.getBoxes());
				final int MIN_HEIGHT_LEVEL = minMaxRow.getMin();
				final int MAX_HEIGHT_LEVEL = minMaxRow.getMax();

				// Sets the y coord either top to bottom or bottom to top, providing space between each row. Each thread should start at the same y-coord
				if (DOWNWARD)
				{
					double rowYcoord = CENTER_Y;
					for (int rowCount = MAX_HEIGHT_LEVEL; rowCount >= MIN_HEIGHT_LEVEL; rowCount--)
					{
						ArrayList<LinkedBox> row = grid.getBoxesAtHeightLevel(rowCount);

						// Make sure there's room at each row to accomodate the tallest box
						int tallestHeightAtRow = Integer.MIN_VALUE;
						for (LinkedBox box : row)
						{
							box.setYTop(rowYcoord);
							
							final int BOX_HEIGHT = box.getHeight();
							if (BOX_HEIGHT > tallestHeightAtRow)
							{
								tallestHeightAtRow = BOX_HEIGHT;
							}				
						}

						// Add space between rows; a value of MIN_VALUE would indicate there were no boxes at the row, which should be impossible
						if (tallestHeightAtRow != Integer.MIN_VALUE)
						{
							rowYcoord += tallestHeightAtRow + minVertSpace;
						}
						else
						{
							rowYcoord += minVertSpace;
						}
					}
				}
				else
				{
					double nextRowYcoord = CENTER_Y;
					for (int rowCount = MIN_HEIGHT_LEVEL; rowCount <= MAX_HEIGHT_LEVEL; rowCount++)
					{
						ArrayList<LinkedBox> row = grid.getBoxesAtHeightLevel(rowCount);

						for (LinkedBox box : row)
						{
							box.setYTop(nextRowYcoord);
						}

						ArrayList<LinkedBox> nextRow = grid.getBoxesAtHeightLevel(rowCount + 1);
						if (nextRow.size() > 0)
						{
							int tallestHeightAtNextRow = Integer.MIN_VALUE;
							for (LinkedBox box : nextRow)
							{
								final int BOX_HEIGHT = box.getHeight();
								if (BOX_HEIGHT > tallestHeightAtNextRow)
								{
									tallestHeightAtNextRow = BOX_HEIGHT;
								}
							}

							nextRowYcoord = nextRowYcoord - minVertSpace - tallestHeightAtNextRow;
						}
					}
				}

				// Sets the x coord left to right
				for (int columnNumber = MIN_WIDTH_LEVEL; columnNumber <= MAX_WIDTH_LEVEL; columnNumber++)
				{
					Set<LinkedBox> column = grid.getBoxesAtWidthLevel(columnNumber);
					for (LinkedBox box : column)
					{
						box.setXLeft(columnXcoord);
						boxesToSendToServer.add(box);
					}
					// Space between boxes
					columnXcoord += (boxWidth * 3) / 4;
				}

				// Give an extra space between threads
				columnXcoord += boxWidth;

				if (DEBUG)
				{
					Logger.log(grid.toString(), Logger.DEBUG);
					Logger.log(argThread.toString(), Logger.DEBUG);
				}
			}

			// Send the new positions to the server
			updateBoxPositions(boxesToSendToServer);

		}
		// Just in case
		catch (Exception e)
		{
			LASADInfo.display("Error", "An unknown error has occurred - current arrangement of map cannot be auto organized.");
			e.printStackTrace();
			Logger.log(e.toString(), Logger.DEBUG);
			Logger.log(e.getMessage(), Logger.DEBUG);
			Logger.log(e.getStackTrace().toString(), Logger.DEBUG);
			Logger.log(e.getClass().toString(), Logger.DEBUG);
		}
		finally
		{
			// Position the cursor of the map
			final double[] SCROLL_EDGE = determineScrollEdge(DOWNWARD);
			communicator.sendActionPackage(actionBuilder.finishAutoOrganization(map.getID(), DOWNWARD, this.getBoxWidth(), this.getMinBoxHeight(), SCROLL_EDGE[0], SCROLL_EDGE[1]));

			// Free some memory for speed (garbage collector will take the nullified values)
			for (ArgumentThread argThread : argModel.getArgThreads())
			{
				argThread.getGrid().empty();
			}

			Logger.log("[lasad.gwt.client.model.organization.AutoOrganizer] Finishing organizeMap", Logger.DEBUG);		
		}
	}

	/**
	 * Updates the sibling boxes (i.e. grouped boxes) related to the creation of a new link.
	 * For example, if box A is attached to box B via a group link, and box B gets a new child, then box A should also
	 * have a relation pointing to that child.  This method uses the private helper method "updateRecursive" to do its dirty work.
	 * @param link - The new, user-drawn link from which we must search for possibly necessary additional new links
	 */
	public void updateSiblingLinks(OrganizerLink link)
	{
		Set<OrganizerLink> linksToCreate = new HashSet<OrganizerLink>();

		// The original link data
		LinkedBox origStartBox = link.getStartBox();
		LinkedBox origEndBox = link.getEndBox();
		//String linkType = link.getType();

		// If the newly created link connects a group, make sure all the gorup members point to the same children
		if (link.getConnectsGroup())
		{
			Set<OrganizerLink> origStartChildLinks = origStartBox.getChildLinks();
			Set<LinkedBox> origStartChildBoxes = origStartBox.getChildBoxes();

			Set<OrganizerLink> origEndChildLinks = origEndBox.getChildLinks();
			Set<LinkedBox> origEndChildBoxes = origEndBox.getChildBoxes();

			for (OrganizerLink origStartChildLink : origStartChildLinks)
			{
				LinkedBox newChildBox = origStartChildLink.getEndBox();
				if (!origEndChildBoxes.contains(newChildBox))
				{
					OrganizerLink newLink = new OrganizerLink(origEndBox, newChildBox, origStartChildLink.getType(), origStartChildLink.getConnectsGroup());
					linksToCreate.add(newLink);
				}
			}

			for (OrganizerLink origEndChildLink : origEndChildLinks)
			{
				LinkedBox newChildBox = origEndChildLink.getEndBox();
				if (!origStartChildBoxes.contains(newChildBox))
				{
					OrganizerLink newLink = new OrganizerLink(origStartBox, newChildBox, origEndChildLink.getType(), origEndChildLink.getConnectsGroup());
					linksToCreate.add(newLink);
				}
			}
		}
		// else make sure all the grouped parents of the link's child point to the child
		else
		{
			Set<LinkedBox> origStartSiblingBoxes = origStartBox.getSiblingBoxes();

			// We only need the first one, hence why I break, but I use this for loop in case origStartSiblingBoxes is empty so that it will skip
			for (LinkedBox origStartSiblingBox : origStartSiblingBoxes)
			{
				linksToCreate = updateRecursive(origStartSiblingBox, origEndBox, link, new VisitedAndLinksHolder()).getLinks();
				break;
			}
		}

		addLinksToVisual(linksToCreate);
	}

	/**
	 *	Holds the visited boxes and links accumulated in a recursive method, with the benefit of one data structure
	 *	For use with updateSiblingLinks and updateRecursive
	 */
	class VisitedAndLinksHolder
	{
		private Set<LinkedBox> visited;
		private Set<OrganizerLink> links;

		public VisitedAndLinksHolder()
		{
			visited = new HashSet<LinkedBox>();
			links = new HashSet<OrganizerLink>();
		}

		public void addVisited(LinkedBox box)
		{
			visited.add(box);
		}

		public void addLink(OrganizerLink link)
		{
			links.add(link);
		}

		public Set<LinkedBox> getVisited()
		{
			return visited;
		}

		public Set<OrganizerLink> getLinks()
		{
			return links;
		}
	}

	/*	
	 *	Recursively checks the siblings of a given start box to see if they need to be updated with a new relation.
	 *	Keeps track of boxes visited so that the method will eventually end.
	 *	@param startBox - The box from which we might make a link
	 *	@param END_BOX - The constant box to which we will be connecting
	 *	@param LINK_TYPE - The constant type of connection we will make if necessary
	 *  @param holder - The visited boxes and the links that need to be created, should be initialized as empty
	*/
	private VisitedAndLinksHolder updateRecursive(LinkedBox startBox, final LinkedBox END_BOX, final OrganizerLink LINK_DATA, VisitedAndLinksHolder holder)
	{
		if (!holder.getVisited().contains(startBox))
		{
			holder.addVisited(startBox);
			if (!startBox.getChildBoxes().contains(END_BOX))
			{
				holder.addLink(new OrganizerLink(startBox, END_BOX, LINK_DATA.getType(), LINK_DATA.getConnectsGroup()));
			}
			for (LinkedBox siblingBox : startBox.getSiblingBoxes())
			{
				holder = updateRecursive(siblingBox, END_BOX, LINK_DATA, holder);
			}
		}
		return holder;
	}

	/**
	 *	Determines whether or not the passed group OrganizerLink can be created on the map, which depends on invariants such as
	 *	the maximum number of permitted siblings per box, both boxes being the same type, groupable, and no conflicting links between siblings.
	 *	@param link - The link to check for valid creation
	 *	@return Success/error integer code: 0 for success, greater than 0 for error code
	 */
	public int groupedBoxesCanBeCreated(OrganizerLink link)
	{
		LinkedBox startBox = link.getStartBox();
		LinkedBox endBox = link.getEndBox();

		// Boxes shouldn't be null
		if (startBox == null || endBox == null)
		{
			return GroupedBoxesStatusCodes.NULL_BOX;
		}

		// Can't create a link to self
		if (startBox.equals(endBox))
		{
			return GroupedBoxesStatusCodes.SAME_BOX;
		}

		// Checks if they are of groupable type
		if (startBox.getCanBeGrouped())
		{
			// Checks that both boxes are of same type, else return error code
			if (startBox.getType().equalsIgnoreCase(endBox.getType()))
			{
				// Checks that they both have fewer than 2 siblings, else return error code
				if (startBox.getNumSiblings() < MAX_SIBLINGS && endBox.getNumSiblings() < MAX_SIBLINGS)
				{
					// See this.isCompatible for what the method within this if statement checks, else return error code
					if (this.isCompatible(startBox, endBox))
					{
						return GroupedBoxesStatusCodes.SUCCESS;
					}
					else
					{
						return GroupedBoxesStatusCodes.TWO_WAY_LINK;
					}
				}
				else
				{
					return GroupedBoxesStatusCodes.TOO_MANY_SIBS;
				}
			}
			else
			{
				return GroupedBoxesStatusCodes.NOT_SAME_TYPE;
			}
		}
		else
		{
			return GroupedBoxesStatusCodes.CANT_BE_GROUPED;
		}
	}

	/*	
	 *	Checks that there aren't existing invalid connections between the startBoxAndExtSibs group and the end Box children.
	 *	For example, a group link can't be created between box A and B if box A has a child box (C) that is also a parent of Box B.
	 *	This is because B would then also have to point to C (creating a 2 way link) to maintain that grouped boxes point to the same children.
	 *	@param startBoxAndExtSibs - The startBox for the new link and its extended siblings
	 *	@param endBox - The end box for the new link
	 *	@return true if there are no conflicts, false if the grouped boxes should not be created
	 */
	private boolean isCompatible(LinkedBox startBox, LinkedBox endBox)
	{
		Set<LinkedBox> startChildBoxes = startBox.getChildBoxes();
		Set<LinkedBox> endChildBoxes = endBox.getChildBoxes();
		
		for (LinkedBox startChildBox : startChildBoxes)
		{
			if (endBox.hasNonChildLinkWith(startChildBox))
			{
				return false;
			}
		}

		for (LinkedBox endChildBox : endChildBoxes)
		{
			if (startBox.hasNonChildLinkWith(endChildBox))
			{
				return false;
			}
		}

		return true;
	}

	/*
	 *	Updates a box's visual position on the map
	 *	@param box - The box whose position we will update with its new coordinates
	 */
	private void updateBoxPositions(Set<LinkedBox> boxes)
	{
		for (LinkedBox box : boxes)
		{
			int intX = (int) Math.round(box.getXLeft());
			int intY = (int) Math.round(box.getYTop());
			if (this.controller.getElement(box.getBoxID()) != null)
			{
				communicator.sendActionPackage(actionBuilder.updateBoxPosition(map.getID(), box.getBoxID(), intX, intY));
			}
			else
			{
				Logger.log("ERROR: Tried to update box position of nonexisting element.", Logger.DEBUG);
				this.argModel.removeBoxByBoxID(box.getBoxID());
			}
		}
	}

	/*
	 *	Shockingly, this updates a box's visual size on the map
	 *	@param box - The box whose size we will send to the server for an update
	 */
	private void updateBoxSize(LinkedBox box)
	{
		if (this.controller.getElement(box.getBoxID()) != null)
		{
			communicator.sendActionPackage(actionBuilder.updateBoxSize(map.getID(), box.getBoxID(), box.getWidth(), box.getHeight()));
		}
		else
		{
			Logger.log("ERROR: Tried to update box size of nonexisting element.", Logger.DEBUG);
			this.argModel.removeBoxByBoxID(box.getBoxID());
		}
	}

	/*
	 *	Positions the map cursor either with the top most box(es) at the top of the map or bottom-most at the bottom
	 *	@param DOWNWARD - if true, put the bottom boxes at the bottom of the screen, false do other option
	 *	The "edge" is the bottom of the bottom row of boxes in the case of true; top of the top row in case of false
	 */
	private double[] determineScrollEdge(final boolean DOWNWARD)
	{
		// Where end level is either top or bottom depending on DOWNWARD
		ArrayList<LinkedBox> boxesAtEndLevel = new ArrayList<LinkedBox>();

		// The sum of x-coord positions at the edge row
		double edgeSum = 0.0;

		int numEdgeBoxes = 0;
		double edgeCoordY;

		if (DOWNWARD)
		{
			// Find min grid height
			int minGridHeight = Integer.MAX_VALUE;
			for (ArgumentThread ARG_THREAD : argModel.getArgThreads())
			{
				final int GRID_MIN = ArgumentGrid.determineMinMaxHeightLevels(ARG_THREAD.getGrid().getBoxes()).getMin();
				if (GRID_MIN < minGridHeight)
				{
					minGridHeight = GRID_MIN;
				}
			}

			// Find lowest bottom and lowest grid height level, accumulate boxes at end level
			double currentBottom = Double.MIN_VALUE;
			for (ArgumentThread ARG_THREAD : argModel.getArgThreads())
			{
				final ArgumentGrid GRID = ARG_THREAD.getGrid();
			
				ArrayList<LinkedBox> boxesAtMinGridHeight = GRID.getBoxesAtHeightLevel(minGridHeight);
				for (LinkedBox box : boxesAtMinGridHeight)
				{
					boxesAtEndLevel.add(box);
					final double BOTTOM_EDGE = box.getYTop() + box.getHeight();
					if (BOTTOM_EDGE > currentBottom)
					{
						currentBottom = BOTTOM_EDGE;
					}
				}
			}
			edgeCoordY = currentBottom;
		}
		else
		{
			int maxGridHeight = Integer.MIN_VALUE;
			for (ArgumentThread ARG_THREAD : argModel.getArgThreads())
			{
				final int GRID_MAX = ArgumentGrid.determineMinMaxHeightLevels(ARG_THREAD.getGrid().getBoxes()).getMax();
				if (GRID_MAX > maxGridHeight)
				{
					maxGridHeight = GRID_MAX;
				}
			}

			double currentTop = Double.MAX_VALUE;
			for (ArgumentThread ARG_THREAD : argModel.getArgThreads())
			{
				final ArgumentGrid GRID = ARG_THREAD.getGrid();
			
				ArrayList<LinkedBox> boxesAtMaxGridHeight = GRID.getBoxesAtHeightLevel(maxGridHeight);
				for (LinkedBox box : boxesAtMaxGridHeight)
				{
					boxesAtEndLevel.add(box);
					final double TOP_EDGE = box.getYTop();
					if (TOP_EDGE < currentTop)
					{
						currentTop = TOP_EDGE;
					}
				}
			}
			edgeCoordY = currentTop;
		}

		for (LinkedBox box : boxesAtEndLevel)
		{
			edgeSum += box.getXCenter();
			numEdgeBoxes++;
		}

		// Take the average x at the edge level and put the scroller there
		if (numEdgeBoxes > 0)
		{
			return new double[]{edgeSum / numEdgeBoxes, edgeCoordY};
		}
		// If empty, put it at the center
		else
		{
			return new double[]{CENTER_X, CENTER_Y};
		}
	}

	/*
	 *	Wraps each new link to be created as an actionPackage, which is sent to server to be added to the model and map.
	 */
	private void addLinksToVisual(Set<OrganizerLink> linksToCreate)
	{
		String elementType = "relation";

		//MVController controller = LASAD_Client.getMVCController(map.getID());

		ElementInfo linkInfo = new ElementInfo();
		linkInfo.setElementType(elementType);

		for (OrganizerLink link : linksToCreate)
		{
			// a better name for element ID here would be subtype, as in, what kind of relation.  Alas, I didn't name it.
			linkInfo.setElementID(link.getType());

			String startBoxStringID = Integer.toString(link.getStartBox().getBoxID());
			String endBoxStringID = Integer.toString(link.getEndBox().getBoxID());

			ActionPackage myPackage = actionBuilder.autoOrganizerCreateLinkWithElements(linkInfo, map.getID(), startBoxStringID, endBoxStringID);
			communicator.sendActionPackage(myPackage);
		}
	}

	/**
	 *	Determines if supplemental links need to be removed after the passed link is removed.  This might be necessary, for example,
	 *	if the removal of solely the passed link would result in a violation of the grouped boxes invariants (i.e. each sibling box must
	 *	link to each other sibling's children).
	 *	@param removedLink - The link already removed from the model, that provides an easy access point for other nearby links to remove
	 */
	public void determineLinksToRemove(OrganizerLink removedLink)
	{
		Set<OrganizerLink> linksToRemove = new HashSet<OrganizerLink>();

		if (!removedLink.getConnectsGroup())
		{
			LinkedBox startBox = removedLink.getStartBox();

			if (startBox != null)
			{
				Set<OrganizerLink> siblingLinks = startBox.getSiblingLinks();
				for (OrganizerLink link : siblingLinks)
				{
					linksToRemove.add(link);
				}

				removeLinksFromVisual(linksToRemove);
			}
			else
			{
				Logger.log("No necesito hacer nada, links boxes have already been removed", Logger.DEBUG);
			}
		}
	}

	/*
	 *	Helper method called by determineLinksToRemove that actually sends the actionPackage to the server, telling the server to
	 *	remove each necessary link from the map.
	 */
	private void removeLinksFromVisual(Set<OrganizerLink> linksToRemove)
	{
		for (OrganizerLink link : linksToRemove)
		{
			ActionPackage myPackage = actionBuilder.autoOrganizerRemoveElement(map.getID(), link.getLinkID());
			if (this.controller.getElement(link.getLinkID()) != null)
			{
				communicator.sendActionPackage(myPackage);
			}
			else
			{
				LinkedBox startBox = link.getStartBox();
				LinkedBox endBox = link.getEndBox();

				if (startBox != null)
				{
					startBox.removeSiblingLink(link);
				}
				if (endBox != null)
				{
					endBox.removeSiblingLink(link);
				}

				Logger.log("ERROR: Tried to remove a null link: " + link.toString(), Logger.DEBUG);
			}
		}
	}
}