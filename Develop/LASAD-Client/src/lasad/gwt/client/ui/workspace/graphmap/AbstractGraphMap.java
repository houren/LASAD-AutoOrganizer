package lasad.gwt.client.ui.workspace.graphmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.List;

import lasad.gwt.client.helper.connector.Direction;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.model.organization.ArgumentModel;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.common.GenericFocusHandler;
import lasad.gwt.client.ui.common.GenericSelectionHandler;
import lasad.gwt.client.ui.common.fade.GenericFadeableElementHandler;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.elements.AbstractExtendedTextElement;

import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.Timer;

/*
 * refactoring of AbstractArgumentMap
 *
 *
 * Super: ContentPanel
 * Sub: GraphMap
 */
public abstract class AbstractGraphMap extends ContentPanel implements FocusableInterface, HighlightableElementInterface {

//	protected LASADActionSender communicator = LASADActionSender.getInstance();
//	protected ActionFactory actionBuilder = ActionFactory.getInstance();

	protected GraphMapSpace myArgumentMapSpace;

	// for auto organization
	protected int orgBoxWidth = 200;
	protected int orgBoxHeight = 107;

	protected int fontSize = 10;

//	protected MVCViewSession myViewSession;

//	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	protected Map<Direction, Integer> mapDimensions = new HashMap<Direction, Integer>();

	protected String ID;

	// the orientation for auto organization (i.e. should links point up or down); the options are upward (true) or downward (false)
	protected boolean orgOrientation;

	protected boolean deleteElementsWithoutConfirmation;

	protected GenericFocusHandler focusHandler;
	protected GenericHighlightHandler highlightHandler;

	protected GenericFadeableElementHandler fadeHandler = new GenericFadeableElementHandler(this);
	protected GenericSelectionHandler selectionHandler = new GenericSelectionHandler(this);
	public static HashMap<String, Integer> mapIDtoCursorID = new HashMap<String, Integer>();
	protected int oldMouseX = 0;

	protected int oldMouseY = 0;

	protected int myAwarenessCursorID = -1;

	protected boolean refreshMyCursor = true;

	protected boolean refreshMyCursorPersistent = false;

	protected boolean disableNormalCursorUpdates = false;

	protected int numberOfCursorRecords = 0;
	protected Timer t = null;

	protected ArgumentModel argModel;

	public AbstractGraphMap(GraphMapSpace parentElement) {
		this.myArgumentMapSpace = parentElement;
		//this.myViewSession = myArgumentMapSpace.getSession();
		//ID = myViewSession.getController().getMapInfo().getMapID();
		init();
		focusHandler = new GenericFocusHandler(this);
		highlightHandler = new GenericHighlightHandler(this);
		argModel = new ArgumentModel(this.getID());
		orgOrientation = true;
	}

	public ArgumentModel getArgModel()
	{
		return argModel;
	}

	public void setOrgBoxWidth(int width)
	{
		this.orgBoxWidth = width;
	}

	public void setOrgBoxHeight(int minBoxHeight)
	{
		this.orgBoxHeight = minBoxHeight;
	}

	public int getOrgBoxWidth()
	{
		return orgBoxWidth;
	}

	public int getOrgBoxHeight()
	{
		return orgBoxHeight;
	}

	public Vector<AbstractBox> getBoxes()
	{
		Vector<AbstractBox> boxes = new Vector<AbstractBox>();
		List<Component> mapComponents = this.getItems();
		for(Component mapComponent : mapComponents){
			boxes.add((AbstractBox) mapComponent);
		}
		return boxes;
	}

	// By Darlan Santana Farias
	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;

		List<Component> mapComponents = this.getItems();
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
	}

	public int getFontSize(){
		return this.fontSize;
	}

	public boolean getOrgOrientation()
	{
		return orgOrientation;
	}

	public void setOrgOrientation(boolean isUpward)
	{
		orgOrientation = isUpward;
	}

	/*
	 * if needed add the code that was commented out in the constructor.
	 */
	protected abstract void init(); 
	
	public abstract void deleteAllFeedbackClusters();

//	public void deleteAllFeedbackClusters() {
//		MVController controller = LASAD_Client.getMVCController(this.ID);
//		if (controller != null) {
//			List<Component> mapComponents = this.getItems();
//
//			for (Component c : mapComponents) {
//				if (c instanceof FeedbackCluster) {
//					FeedbackCluster fc = (FeedbackCluster) c;
//
//					communicator.sendActionPackage(actionBuilder.removeElement(this.ID, fc.getID()));
//				}
//			}
//		}
//	}

	public void enableCursorTracking() {}

	public GenericFadeableElementHandler getFadeHandler() {
		return fadeHandler;
	}

	public GenericFocusHandler getFocusHandler() {
		return focusHandler;
	}

	public FocusableInterface getFocusParent() {
		// The Map is the Focus Root, so return null
		return null;
	}

	public GenericHighlightHandler getHighlightHandler() {
		return highlightHandler;
	}

	// **********************************************
	// Constructor
	// **********************************************

	public HighlightableElementInterface getHighlightParent() {
		return null;
	}

	// **********************************************
	// Methods
	// **********************************************

	public String getID() {
		return ID;
	}

	public abstract Size getMapDimensionSize();

	public GraphMapSpace getMyArgumentMapSpace() {
		return myArgumentMapSpace;
	}

	public int getMyAwarenessCursorID() {
		return myAwarenessCursorID;
	}

	// NEW MVC Stuff
	public abstract AbstractMVCViewSession getMyViewSession();
//	public MVCViewSession getMyViewSession() {
//		return myViewSession;
//	}

	public GenericSelectionHandler getSelectionHandler() {
		return selectionHandler;
	}

	// **********************************************
	// getter & setter
	// **********************************************

	public boolean isDeleteElementsWithoutConfirmation() {
		return deleteElementsWithoutConfirmation;
	}

	public void recordCursorTracking() {
		// Timer to allow cursor update

		disableNormalCursorUpdates = true;

		com.google.gwt.user.client.Timer t = new com.google.gwt.user.client.Timer() {
			public void run() {
				if (numberOfCursorRecords < 10) {
					refreshMyCursorPersistent = true;
				} else {
					numberOfCursorRecords = 0;
					refreshMyCursorPersistent = false;
					disableNormalCursorUpdates = false;

					if (AbstractGraphMap.this.t != null) {
						AbstractGraphMap.this.t.cancel();
					}
				}
			}
		};
		t.scheduleRepeating(750);
	}

	public void setDeleteElementsWithoutConfirmation(boolean deleteElementsWithoutConfirmation) {
		this.deleteElementsWithoutConfirmation = deleteElementsWithoutConfirmation;
	}

	public void setElementFocus(boolean focus) {}

	public void setFadeHandler(GenericFadeableElementHandler fadeHandler) {
		this.fadeHandler = fadeHandler;
	}

	public void setFocusHandler(GenericFocusHandler focusHandler) {
		this.focusHandler = focusHandler;
	}

	public void setHighlight(boolean highlight) {}

	public void setHighlightHandler(GenericHighlightHandler highlightHandler) {
		this.highlightHandler = highlightHandler;
	}

	public void setID(String id) {
		ID = id;
	}

	public void setMyArgumentMapSpace(GraphMapSpace myArgumentMapSpace) {
		this.myArgumentMapSpace = myArgumentMapSpace;
	}

	public void setMyAwarenessCursorID(int myAwarenessCursorID) {
		this.myAwarenessCursorID = myAwarenessCursorID;
	}

	public abstract void setMyViewSession(AbstractMVCViewSession myViewSession);
//	public void setMyViewSession(MVCViewSession myViewSession) {
//		this.myViewSession = myViewSession;
//	}

	public void setRefreshMyCursor(boolean refreshMyCursor) {
		this.refreshMyCursor = refreshMyCursor;
	}

	public void setSelectionHandler(GenericSelectionHandler selectionHandler) {
		this.selectionHandler = selectionHandler;
	}
}
