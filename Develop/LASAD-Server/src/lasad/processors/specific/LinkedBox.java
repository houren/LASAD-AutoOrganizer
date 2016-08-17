package lasad.processors.specific;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *	LinkedBox is an alternative representation to AbstractBox, more conducive for map organization and modeling.  Each LinkedBox has its boxID
 * 	and rootID, as well as all its types of Links (i.e. an OrganizerLink that points to the next box). This allows for an easy
 * 	organization of all Links on the map, as they can be followed in a chain similar to a linked list.  This class is key to AutoOrganizer.
 * 	All names within this class are self-explanatory.
 * 	@author Kevin Loughlin
 * 	@since 11 June 2015, Updated 29 July 2015
 */
public class LinkedBox
{
	// Garbage value
	private final int ERROR = -1;

	// I.e. premise, conclusion, etc.
	private String type;

	// Be mindful of difference between boxID and rootID.
	private int boxID;
	private int rootID;

	private int width;
	private int height;

	// Note: updating xLeft with METHODS will automatically update xCenter and vice-versa.  Same is true for y equivalents.
	private double xLeft;
	private double yTop;

	private double xCenter;
	private double yCenter;

	// In these hashmaps, integer is linkID
	private Map<Integer, OrganizerLink> childLinks;
	private Map<Integer, OrganizerLink> parentLinks;
	private Map<Integer, OrganizerLink> siblingLinks;

	private Set<LinkedBox> childBoxes;
	private Set<LinkedBox> parentBoxes;
	private Set<LinkedBox> siblingBoxes;

	// Height and width level will be used like a coordinate grid once we come to organizeMap() of AutoOrganizer
	private int heightLevel;
	private int widthLevel;

	// if the box can be grouped
	private boolean canBeGrouped;

	// This is the meat and bones constructor
	public LinkedBox(int boxID, int rootID, String type, double xLeft, double yTop, int width, int height, boolean canBeGrouped)
	{
		this.boxID = boxID;
		this.rootID = rootID;
		this.type = type;
		this.xLeft = xLeft;
		this.yTop = yTop;
		this.width = width;
		this.height = height;
		this.xCenter = xLeft + width / 2.0;
		this.yCenter = yTop + height / 2.0;
		this.childLinks = new HashMap<Integer, OrganizerLink>();
		this.parentLinks = new HashMap<Integer, OrganizerLink>();
		this.siblingLinks = new HashMap<Integer, OrganizerLink>();
		this.childBoxes = new HashSet<LinkedBox>();
		this.parentBoxes = new HashSet<LinkedBox>();
		this.siblingBoxes = new HashSet<LinkedBox>();
		this.heightLevel = Integer.MIN_VALUE + 1;
		this.widthLevel = Integer.MAX_VALUE - 1;
		this.canBeGrouped = canBeGrouped;
	}

	// I don't want people to use the default constructor because this LinkedBox needs definitive IDs.  This is just for cloning.
	private LinkedBox()
	{
		this.boxID = ERROR;
		this.rootID = ERROR;
		this.type = "garbage";
		this.xLeft = ERROR;
		this.yTop = ERROR;
		this.width = ERROR;
		this.height = ERROR;
		this.xCenter = ERROR;
		this.yCenter = ERROR;
		this.childLinks = new HashMap<Integer, OrganizerLink>();
		this.parentLinks = new HashMap<Integer, OrganizerLink>();
		this.siblingLinks = new HashMap<Integer, OrganizerLink>();
		this.childBoxes = new HashSet<LinkedBox>();
		this.parentBoxes = new HashSet<LinkedBox>();
		this.siblingBoxes = new HashSet<LinkedBox>();
		this.heightLevel = Integer.MIN_VALUE;
		this.widthLevel = Integer.MAX_VALUE;
		this.canBeGrouped = false;
	}

	public boolean getCanBeGrouped()
	{
		return canBeGrouped;
	}

	public int getBoxID()
	{
		return boxID;
	}

	public int getRootID()
	{
		return rootID;
	}

	public String getType()
	{
		return type;
	}

	public int getWidth()
	{
		return width;
	}

	public Coordinate getDimensions()
	{
		return new Coordinate(width, height);
	}

	public OrganizerLink getLinkByLinkID(int linkID)
	{
		OrganizerLink returnLink = childLinks.get(linkID);
		if (returnLink == null)
		{
			returnLink = parentLinks.get(linkID);
			if (returnLink == null)
			{
				returnLink = siblingLinks.get(linkID);
			}
		}

		return returnLink;
	}

	public OrganizerLink removeLinkByLinkID(int linkID)
	{
		OrganizerLink returnLink = this.removeChildLinkByID(linkID);
		if (returnLink == null)
		{
			returnLink = this.removeParentLinkByID(linkID);
			if (returnLink == null)
			{
				returnLink = this.removeSiblingLinkByID(linkID);
			}
		}
		return returnLink;
	}

	public void setWidth(int width)
	{
		this.width = width;
		this.xCenter = this.xLeft + width / 2.0;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
		this.yCenter = this.yTop + height / 2.0;
	}

	public void setSize(int width, int height)
	{
		this.setWidth(width);
		this.setHeight(height);
	}

	public double getXLeft()
	{
		return xLeft;
	}

	public void setXLeft(double xLeft)
	{
		this.xLeft = xLeft;
		this.xCenter = xLeft + this.width / 2.0;
	}

	public double getXCenter()
	{
		return xCenter;
	}

	public void setXCenter(double xCenter)
	{
		this.xCenter = xCenter;
		this.xLeft = xCenter - this.width / 2.0;
	}

	public double getYTop()
	{
		return yTop;
	}

	public void setYTop(double yTop)
	{
		this.yTop = yTop;
		this.yCenter = yTop + this.height / 2.0;
	}

	public double getYCenter()
	{
		return yCenter;
	}

	public void setYCenter(double yCenter)
	{
		this.yCenter = yCenter;
		this.yTop = yCenter - this.height / 2.0;
	}

	public Set<OrganizerLink> getChildLinks()
	{
		return new HashSet<OrganizerLink>(childLinks.values());
	}

	public Set<OrganizerLink> getParentLinks()
	{
		return new HashSet<OrganizerLink>(parentLinks.values());
	}

	public Set<OrganizerLink> getSiblingLinks()
	{
		return new HashSet<OrganizerLink>(siblingLinks.values());
	}

	public Set<LinkedBox> getChildBoxes()
	{
		return childBoxes;
	}

	/**
	 *	Only for use during auto organization, because it requires height levels to be assigned on the grid.
	 *	Next level is defined as this box's height level plus one
	 */
	public Set<LinkedBox> getChildBoxesOnNextLevel()
	{
		Set<LinkedBox> childBoxesOnNextLevel = new HashSet<LinkedBox>(this.getChildBoxes());
		Set<LinkedBox> toRemove = new HashSet<LinkedBox>();
		final int CHILD_HEIGHT = this.getHeightLevel() + 1;
		for (LinkedBox box : childBoxesOnNextLevel)
		{
			if (box.getHeightLevel() != CHILD_HEIGHT)
			{
				toRemove.add(box);
			}
		}
		childBoxesOnNextLevel.removeAll(toRemove);
		return childBoxesOnNextLevel;
	}

	public Set<LinkedBox> getParentBoxes()
	{
		return parentBoxes;
	}

	/**
	 *	Only for use during auto organization, because it requires height levels to be assigned on the grid.
	 *	Previous level is defined as this box's height level minus one.
	 */
	public Set<LinkedBox> getParentBoxesOnPreviousLevel()
	{
		Set<LinkedBox> parentBoxesOnPreviousLevel = new HashSet<LinkedBox>(this.getParentBoxes());
		Set<LinkedBox> toRemove = new HashSet<LinkedBox>();
		final int PARENT_HEIGHT = this.getHeightLevel() - 1;
		for (LinkedBox box : parentBoxesOnPreviousLevel)
		{
			if (box.getHeightLevel() != PARENT_HEIGHT)
			{
				toRemove.add(box);
			}
		}
		parentBoxesOnPreviousLevel.removeAll(toRemove);
		return parentBoxesOnPreviousLevel;
	}

	public Set<LinkedBox> getSiblingBoxes()
	{
		return siblingBoxes;
	}

	/**
	 *	Gets the link that connects this with
	 *	@param relatedBox
	 *	@return the connecting link or null if it does not exist
	 */
	public OrganizerLink getConnectingLink(LinkedBox relatedBox)
	{
		if (this.getChildBoxes().contains(relatedBox))
		{
			for (OrganizerLink childLink : this.getChildLinks())
			{
				if (childLink.getEndBox().equals(relatedBox))
				{
					return childLink;
				}
			}
		}

		if (this.getParentBoxes().contains(relatedBox))
		{
			for (OrganizerLink parentLink : this.getParentLinks())
			{
				if (parentLink.getStartBox().equals(relatedBox))
				{
					return parentLink;
				}
			}
		}

		if (this.getSiblingBoxes().contains(relatedBox))
		{
			for (OrganizerLink siblingLink : this.getSiblingLinks())
			{
				if (siblingLink.getStartBox().equals(relatedBox) || siblingLink.getEndBox().equals(relatedBox))
				{
					return siblingLink;
				}
			}
		}

		return null;
	}

	/**
	 *	Returns this box and it's extended siblings (its siblings and any siblings of those siblings) from left to right order (if applicable)
	 */
	public Set<LinkedBox> getThisAndExtendedSiblings()
	{
		return findThisAndExtendedSiblings(this, new HashSet<LinkedBox>());
	}

	// intialize currentBox as this and accumulated should be empty; RECURSIVE
	private Set<LinkedBox> findThisAndExtendedSiblings(LinkedBox currentBox, Set<LinkedBox> accumulated)
	{
		if (!accumulated.contains(currentBox))
		{
			accumulated.add(currentBox);
			for (LinkedBox siblingBox : currentBox.getSiblingBoxes())
			{
				accumulated = findThisAndExtendedSiblings(siblingBox, accumulated);
			}
		}
		return accumulated;
	}

	// I naturally avoid duplicates in all add methods by implementing these with HashSet.  Takes care of updating box sets too.
	public void addChildLink(OrganizerLink link)
	{
		this.childLinks.put(link.getLinkID(), link);
		this.childBoxes.add(link.getEndBox());
	}

	public void addParentLink(OrganizerLink link)
	{
		this.parentLinks.put(link.getLinkID(), link);
		this.parentBoxes.add(link.getStartBox());
	}

	public void addSiblingLink(OrganizerLink link)
	{
		this.siblingLinks.put(link.getLinkID(), link);
		if (link.getStartBox().equals(this))
		{
			this.siblingBoxes.add(link.getEndBox());
		}
		else
		{
			this.siblingBoxes.add(link.getStartBox());
		}
	}

	public OrganizerLink removeChildLink(OrganizerLink link)
	{
		return this.removeChildLinkByID(link.getLinkID());
	}

	private OrganizerLink removeChildLinkByID(int linkID)
	{
		OrganizerLink removedLink = this.childLinks.remove(linkID);
		if (removedLink != null)
		{
			this.childBoxes.remove(removedLink.getEndBox());
		}
		return removedLink;
	}

	public OrganizerLink removeParentLink(OrganizerLink link)
	{
		return this.removeParentLinkByID(link.getLinkID());
	}

	private OrganizerLink removeParentLinkByID(int linkID)
	{
		OrganizerLink removedLink = this.parentLinks.remove(linkID);
		if (removedLink != null)
		{
			this.parentBoxes.remove(removedLink.getStartBox());
		}
		return removedLink;
	}

	public OrganizerLink removeSiblingLink(OrganizerLink link)
	{
		return this.removeSiblingLinkByID(link.getLinkID());
	}

	private OrganizerLink removeSiblingLinkByID(int linkID)
	{
		OrganizerLink removedLink = this.siblingLinks.remove(linkID);
		if (removedLink != null)
		{
			if (removedLink.getStartBox().equals(this))
			{
				this.siblingBoxes.remove(removedLink.getEndBox());
			}
			else
			{
				this.siblingBoxes.remove(removedLink.getStartBox());
			}
		}	
		return removedLink;
	}

	public int getNumChildren()
	{
		return childLinks.size();
	}

	public int getNumParents()
	{
		return parentLinks.size();
	}

	public int getNumSiblings()
	{
		return siblingLinks.size();
	}

	public int getNumRelations()
	{
		return getNumChildren() + getNumParents() + getNumSiblings();
	}

	/**
	 *	Gets ALL directly related boxes (parents, chidlren, and immediate siblings)
	 */
	public Set<LinkedBox> getRelatedBoxes()
	{
		Set<LinkedBox> relatedBoxes = new HashSet<LinkedBox>();
		relatedBoxes.addAll(this.getChildBoxes());
		relatedBoxes.addAll(this.getParentBoxes());
		relatedBoxes.addAll(this.getSiblingBoxes());
		return relatedBoxes;
	}

	/**
	 *	Height level should not be confused with height.  Height level is for ArgumentGrid (the chessBoard setup), where as height is the
	 *	height of this box in pixels.  Same is true for width.
	 */
	public void setHeightLevel(int heightLevel)
	{
		this.heightLevel = heightLevel;
	}

	public void setWidthLevel(int widthLevel)
	{
		this.widthLevel = widthLevel;
	}

	public void incWidthLevel()
	{
		this.widthLevel++;
	}

	public void decWidthLevel()
	{
		this.widthLevel--;
	}

	public int getHeightLevel()
	{
		return heightLevel;
	}

	public int getWidthLevel()
	{
		return widthLevel;
	}

	/*
	 *	Gets the chessboard position, for ex (column 4, row 3)
	 */
	public Coordinate getGridPosition()
	{
		return new Coordinate(this.getWidthLevel(), this.getHeightLevel());
	}

	public void setGridPosition(int x, int y)
	{
		this.widthLevel = x;
		this.heightLevel = y;
	}

	public void setGridPosition(Coordinate coord)
	{
		this.widthLevel = coord.getX();
		this.heightLevel = coord.getY();
	}

	/**
	 *	Removes this box's link to boxBeingRemoved if there is one
	 */
	public void removeLinkTo(LinkedBox boxBeingRemoved)
	{
		if (this.getChildBoxes().contains(boxBeingRemoved))
		{
			for (OrganizerLink link : this.getChildLinks())
			{
				if (link.getEndBox().equals(boxBeingRemoved))
				{
					this.removeChildLink(link);
					return;
				}
			}
		}

		if (this.getParentBoxes().contains(boxBeingRemoved))
		{
			for (OrganizerLink link : this.getParentLinks())
			{
				if (link.getStartBox().equals(boxBeingRemoved))
				{
					this.removeParentLink(link);
					return;
				}
			}
		}

		if (this.getSiblingBoxes().contains(boxBeingRemoved))
		{
			for (OrganizerLink link : this.getSiblingLinks())
			{
				if (link.getStartBox().equals(boxBeingRemoved))
				{
					this.removeSiblingLink(link);
					return;
				}
				else if (link.getEndBox().equals(boxBeingRemoved))
				{
					this.removeSiblingLink(link);
					return;
				}
			}
		}
	}

	public boolean hasChildLinkWith(LinkedBox other)
	{
		if (this.getChildBoxes().contains(other))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean hasParentLinkWith(LinkedBox other)
	{
		if (this.getParentBoxes().contains(other))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean hasSiblingLinkWith(LinkedBox other)
	{
		if (this.getSiblingBoxes().contains(other))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 *	ExtendedSiblingLinks reference checking for connections via siblings, siblings of siblings, and so on
	 *	@param boxToFind - The box with which we are checking this instance for an extended sibling connection
	 *	@return true if an extended sibling connection exists, false if not
	 */
	public boolean hasExtendedSiblingLinkWith(LinkedBox boxToFind)
	{
		boolean foundExtendedSibling = false;

		if (this.hasSiblingLinkWith(boxToFind))
		{
			foundExtendedSibling = true;
		}
		else
		{
			foundExtendedSibling = extendedSiblingRecursive(this, boxToFind, new VisitedAndBoolHolder()).getFound();
		}

		return foundExtendedSibling;
	}

	/**
	 *	Holds the visited boxes and whether or not an extended sibling has been found, data accumulated in a recursive method.
	 */
	class VisitedAndBoolHolder
	{
		private Set<LinkedBox> visited;
		private boolean foundExtendedSibling;

		public VisitedAndBoolHolder()
		{
			visited = new HashSet<LinkedBox>();
			foundExtendedSibling = false;
		}

		public void addVisited(LinkedBox box)
		{
			visited.add(box);
		}

		public void setFound(boolean found)
		{
			foundExtendedSibling = found;
		}

		public Set<LinkedBox> getVisited()
		{
			return visited;
		}

		public boolean getFound()
		{
			return foundExtendedSibling;
		}
	}

	/*
	 *	Recursive helper for hasExtendedSiblingLinkWith
	 *	@param box - the Box we are checking to see if it is the one we are searching for
	 *	@param BOX_TO_FIND - The box we are searching for
	 *	@param visited - The accumuated set of visited boxes, should be intiliazed as empty
	 *	@return true if match, false if not
	 */
	private VisitedAndBoolHolder extendedSiblingRecursive(LinkedBox box, LinkedBox BOX_TO_FIND, VisitedAndBoolHolder holder)
	{
		if (!holder.getVisited().contains(box))
		{
			holder.addVisited(box);
			if (box.equals(BOX_TO_FIND))
			{
				holder.setFound(true);
				return holder;
			}
			else
			{
				Set<LinkedBox> siblingBoxes = box.getSiblingBoxes();
				for (LinkedBox siblingBox : siblingBoxes)
				{
					if (extendedSiblingRecursive(siblingBox, BOX_TO_FIND, holder).getFound())
					{
						return holder;
					}
				}
			}
		}
		
		return holder;
	}

	/**
	 *	A nonChild link is a link that is only either a parent link or a sibling link
	 *	@param other - the box to check for the link
	 *	@return true if the link exists, else false
	 */
	public boolean hasNonChildLinkWith(LinkedBox other)
	{
		if (this.hasParentLinkWith(other) || this.hasSiblingLinkWith(other))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean hasLinkWith(LinkedBox other)
	{
		if (this.hasChildLinkWith(other) || this.hasNonChildLinkWith(other))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 *	If this box is part of a group with a parent link to endBox, then a link cannot be created between this box and endBox
	 *	@param endBox: The potential endBox of the link
	 *	@return whether or not this box is part of a group with a parent link to endBox
	 */
	public boolean isPartOfGroupWithParentLinkTo(LinkedBox endBox)
	{
		Set<LinkedBox> startBoxAndExtSibs = this.getThisAndExtendedSiblings();
		for (LinkedBox startBox : startBoxAndExtSibs)
		{
			if (endBox.hasChildLinkWith(startBox))
			{
				return true;
			}
		}
		return false;
	}

	public void setPosition(int xLeft, int yTop)
	{
		setXLeft(xLeft);
		setYTop(yTop);
	}

	public void setChildLinks(Map<Integer, OrganizerLink> childLinks)
	{
		this.childLinks = new HashMap<Integer, OrganizerLink>(childLinks);
	}

	public void setParentLinks(Map<Integer, OrganizerLink> parentLinks)
	{
		this.parentLinks = new HashMap<Integer, OrganizerLink>(parentLinks);
	}

	public void setSiblingLinks(Map<Integer, OrganizerLink> siblingLinks)
	{
		this.siblingLinks = new HashMap<Integer, OrganizerLink>(siblingLinks);
	}

	public void setChildBoxes(Set<LinkedBox> childBoxes)
	{
		this.childBoxes = new HashSet<LinkedBox>(childBoxes);
	}

	public void setParentBoxes(Set<LinkedBox> parentBoxes)
	{
		this.parentBoxes = new HashSet<LinkedBox>(parentBoxes);
	}

	public void setSiblingBoxes(Set<LinkedBox> siblingBoxes)
	{
		this.siblingBoxes = new HashSet<LinkedBox>(siblingBoxes);
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setBoxID(int boxID)
	{
		this.boxID = boxID;
	}

	public void setRootID(int rootID)
	{
		this.rootID = rootID;
	}

	public void setCanBeGrouped(boolean canBeGrouped)
	{
		this.canBeGrouped = canBeGrouped;
	}

	public LinkedBox clone()
	{
		LinkedBox clone = new LinkedBox();
		clone.setBoxID(this.boxID);
		clone.setRootID(this.rootID);
		clone.setType(this.type);
		clone.setXLeft(this.xLeft);
		clone.setYTop(this.yTop);
		clone.setWidth(this.width);
		clone.setHeight(this.height);
		clone.setXCenter(this.xCenter);
		clone.setYCenter(this.yCenter);
		clone.setChildLinks(this.childLinks);
		clone.setParentLinks(this.parentLinks);
		clone.setSiblingLinks(this.siblingLinks);
		clone.setChildBoxes(this.childBoxes);
		clone.setParentBoxes(this.parentBoxes);
		clone.setSiblingBoxes(this.siblingBoxes);
		clone.setHeightLevel(this.heightLevel);
		clone.setWidthLevel(this.widthLevel);
		clone.setCanBeGrouped(this.canBeGrouped);
		return clone;
	}

	// Box IDs are unique and final, so this is all we need for equality for now.
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof LinkedBox)
		{
			LinkedBox otherBox = (LinkedBox) object;
			if (this.boxID == otherBox.getBoxID())
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

	// Necessary override since we're using HashMaps and HashSets in our code.  BoxIDs are unique, so this guarantees no collisions.
	@Override
	public int hashCode()
	{
		return this.boxID;
	}

	// Just outputs the box's boxID and rootID, not its children and parents and siblings
	public String toStringShort(final boolean shouldIndentMore)
	{
		
		if (shouldIndentMore)
		{
			String afterIndents = new String("BOX: " + rootID + "; widthLevel: " + widthLevel + "; heightLevel: " + heightLevel);
			StringBuilder buffer = new StringBuilder("\n\t\t\t\t" + afterIndents);
			return buffer.toString();
		}
		else
		{
			String afterIndents = new String("BOX: " + rootID + "; widthLevel: " + widthLevel + "; heightLevel: " + heightLevel);
			StringBuilder buffer = new StringBuilder("\n\t\t" + afterIndents);
			return buffer.toString();
		}
			
	}

	/**
	 *	Outputs this box's identification numbers as well as the ID's of all its children/parents/siblings
	 */
	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder(this.toStringShort(false));
		buffer.append("\n\t\t\tCHILD BOXES...");
		for (LinkedBox childBox : this.getChildBoxes())
		{
			buffer.append(childBox.toStringShort(true));
		}
		buffer.append("\n\t\t\tPARENT BOXES...");
		for (LinkedBox parentBox : this.getParentBoxes())
		{
			buffer.append(parentBox.toStringShort(true));
		}
		buffer.append("\n\t\t\tSIBLING BOXES...");
		for (LinkedBox siblingBox : this.getSiblingBoxes())
		{
			buffer.append(siblingBox.toStringShort(true));
		}
		buffer.append("\n\t\tEND BOX\n");
		return buffer.toString();
	}
}