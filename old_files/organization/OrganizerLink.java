package lasad.gwt.client.model.organization;

/**
 * OrganizerLink is, like LinkedBox is to AbstractBox, a way of representing an AbstractLinkPanel (and/or an AbstractLink) that is more
 * friendly to AutoOrganizer.  The important info for updating a link via an Action is contained within an OrganizerLink.
 * @author Kevin Loughlin
 * @since 17 June 2015, Updated 1 April 2016
 */
public class OrganizerLink
{
	// Just for helping assign unique linkIDs, not even sure if this is necessary because LASAD seems to correctly assign link IDs elsewhere
	private static int lastLinkID = -1;

	private final LinkedBox startBox;
	private final LinkedBox endBox;

	// I.e. what kind of relation (perhaps it could be support, refutation, Linked Premises, depending on the ontology and its terminology)
	private final String type;

	// Helps with removal of links, otherwise not needed
	private final int linkID;

	// Whether or not this type of OrganizerLink can group boxes together
	private final boolean connectsGroup;

	// generate from startBox and endBox box ID's, using Cantor's pairing function for a unique number
	private final int hashCode;

	/**
	 * Constructor
	 */
	public OrganizerLink(int linkID, LinkedBox startBox, LinkedBox endBox, String type, boolean connectsGroup)
	{
		this.linkID = linkID;
		lastLinkID = this.linkID;
		this.startBox = startBox;
		this.endBox = endBox;
		this.type = type;
		this.connectsGroup = connectsGroup;
		this.hashCode = cantorPairFunction(this.getStartBox().getBoxID(), this.getEndBox().getBoxID());
	}

	/**
	 * Somehow this linkID turns out right in the end.  No idea how...
	 */
	public OrganizerLink(LinkedBox startBox, LinkedBox endBox, String type, boolean connectsGroup)
	{
		this.linkID = lastLinkID + 1;
		lastLinkID = this.linkID;
		this.startBox = startBox;
		this.endBox = endBox;
		this.type = type;
		this.connectsGroup = connectsGroup;
		this.hashCode = cantorPairFunction(this.getStartBox().getBoxID(), this.getEndBox().getBoxID());
	}

	/* Gets */

	public int getLinkID()
	{
		return linkID;
	}

	public LinkedBox getStartBox()
	{
		return startBox;
	}

	public LinkedBox getEndBox()
	{
		return endBox;
	}

	public int getStartBoxID()
	{
		return startBox.getBoxID();
	}

	public int getEndBoxID()
	{
		return endBox.getBoxID();
	}

	public int getStartBoxRootID()
	{
		return startBox.getRootID();
	}

	public int getEndBoxRootID()
	{
		return endBox.getRootID();
	}

	public String getType()
	{
		return type;
	}

	public boolean getConnectsGroup()
	{
		return connectsGroup;
	}

	// Use Cantor's pairing function to produce unique ID based from box IDs, done in constructor for constant time lookup here
	@Override
	public int hashCode()
	{
		return this.hashCode;
	}
	
	private int cantorPairFunction(final int k1, final int k2)
	{
		return ( ( k1 + k2 ) * ( k1 + k2 + 1 ) ) / 2 + k2;  
	}

	public OrganizerLink clone()
	{
		return new OrganizerLink(this.getLinkID(), this.getStartBox(), this.getEndBox(), this.getType(), this.getConnectsGroup());
	}

	/* BoxIDs should be unique and thus just checking the start and end as well as type should be sufficient for equality.
		If the invariants of an ArgumentMap change, then this method will need to be updated accordingly. */
	@Override
	public boolean equals(Object object)
	{
		if (object instanceof OrganizerLink)
		{
			OrganizerLink link = (OrganizerLink) object;
			if (startBox.equals(link.getStartBox()) && endBox.equals(link.getEndBox()))
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
		return new String("linkID: " + linkID + "; type: " + type + "; startBox: " + startBox.getRootID() + "; endBox: " + endBox.getRootID());
	}
}