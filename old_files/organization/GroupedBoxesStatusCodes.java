package lasad.gwt.client.model.organization;

/**
 *	Just the success/error codes used in AutoOrganizer and AbstractCreateLinkDialogListener.  Should not be instantiated.
 *	@author Kevin Loughlin
 *	@since 6 July 2015
 */
public class GroupedBoxesStatusCodes
{
	public static final int SUCCESS = 0;
	public static final int NULL_BOX = 1;
	public static final int SAME_BOX = 2;
	public static final int NOT_SAME_TYPE = 3;
	public static final int TOO_MANY_SIBS = 4;
	public static final int TWO_WAY_LINK = 5;
	public static final int CANT_BE_GROUPED = 6;

	private GroupedBoxesStatusCodes()
	{
	}
}