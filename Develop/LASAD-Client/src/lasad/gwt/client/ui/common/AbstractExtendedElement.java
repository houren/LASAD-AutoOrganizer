package lasad.gwt.client.ui.common;

import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.ui.LASADBoxComponent;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;



/**
 * @author burner
 * @author anahuacv - refactoring
 * 
 *         This Element should be the FrameWork for all further Elements, like
 *         TextElements, TranscriptElements, etc.
 * 
 *         It provides a lot of general functions and holds the whole Model and
 *         general Element Configuration Logic for the Element.
 * 
 */

public abstract class AbstractExtendedElement implements MVCViewRecipient, FocusableInterface, HighlightableElementInterface {

//	protected final LASADActionSender communicator = LASADActionSender.getInstance();
//	protected final ActionFactory actionBuilder = ActionFactory.getInstance();
	
	private ExtendedElementContainerInterface container = null;

	private ElementInfo config = null;
	
	// Vector for possible Element Vars like "TEXT"
	// list is used to ask subelements for the current var value
	protected Vector<ParameterTypes> elementVars = new Vector<ParameterTypes>();

	private int viewModeMinHeight, viewModeMaxHeight;
	private int actualViewModeHeight = 0, actualViewModeWidth = 0;
	
	private boolean blocked = false;
	protected boolean readOnly = false;
	
	private AbstractUnspecifiedElementModel connectedModel = null;
	
	public AbstractExtendedElement(ExtendedElementContainerInterface container, ElementInfo config) {
		this.container = container;

		// Init Content Frame
		contentFrame = DOM.createDiv();
		DOM.setStyleAttribute(contentFrame, "overflow", "hidden");

		Logger.log("EE: Construktor. ContentFrame created", Logger.DEBUG);

		initElementConfig(config);
		
		if (!isModificationAllowed()) {
			this.blocked = true;
		}

		Logger.log("EE: Construktor. done !", Logger.DEBUG);
	}
	
	public AbstractExtendedElement(ExtendedElementContainerInterface container, ElementInfo config, boolean readOnly) {
		this(container, config);
		this.readOnly = readOnly;
	}

	/* SOME GENERAL VIEW AND EDITMODE STUFF */

	private Element contentFrame;
	private boolean activeViewMode = false;

	public Element getContentFrame() {

		setViewMode(true);

		return contentFrame;
	}

	public boolean isActiveViewMode() {
		return activeViewMode;
	}

	public void setViewMode(boolean status) {
		if (activeViewMode == false && status == true) {
			// Workaround for first method call
			switchToViewMode(contentFrame);
			activeViewMode = true;
		}
		else if (activeViewMode == true && status == false) {
			switchToEditMode(contentFrame);
			activeViewMode = false;
		}
	}

	/* STYLING STUFF */

	private void initElementConfig(ElementInfo config) {
		this.config = config;

		String tempValue;

		// VIEWMODE MIN HEIGHT
		tempValue = config.getUiOption(ParameterTypes.MinHeight);
		if (tempValue != null) {
			this.setViewModeMinHeight(Integer.parseInt(tempValue));
		}
		else {
			this.setViewModeMinHeight(0);
		}

		// VIEWMODE MAX HEIGHT
		tempValue = config.getUiOption(ParameterTypes.MaxHeight);
		if (tempValue != null) {
			this.setViewModeMaxHeight(Integer.parseInt(tempValue));
		}
		else {
			this.setViewModeMaxHeight(400);
		}

		this.setActualViewModeHeight(getViewModeMinHeight());
	}

	private boolean ViewModeFixedSize = false;

	public boolean isViewModeFixedSize() {
		return ViewModeFixedSize;
	}

	public void setViewModeFixedSize(boolean viewModeFixedSize) {
		ViewModeFixedSize = viewModeFixedSize;
	}

	private void calculateViewModeFixedSize(){
		// VIEWMODE FIXED HEIGHT
		if (getViewModeMinHeight() > 0 && getViewModeMaxHeight() == getViewModeMinHeight()) {
			this.setViewModeFixedSize(true);
		}
		else {
			this.setViewModeFixedSize(false);
		}
	}
	
	public void setActualViewModeHeight(int size) {
		this.actualViewModeHeight = size;
		DOM.setStyleAttribute(this.contentFrame, "height", String.valueOf(size) + "px");
		// Publish changes to the child Class
		this.setElementSize(new Size(getActualViewModeWidth(), getActualViewModeHeight()));
	}

	public int getActualViewModeHeight() {
		return actualViewModeHeight;
	}

	public void setActualViewModeWidth(int size) {
		this.actualViewModeWidth = size;
		DOM.setStyleAttribute(this.contentFrame, "width", String.valueOf(size) + "px");

		// Publish changes to the child Class
		this.setElementSize(new Size(getActualViewModeWidth(), getActualViewModeHeight()));
	}

	public int getActualViewModeWidth() {
		return actualViewModeWidth;
	}

	public int getViewModeMinHeight() {
		return viewModeMinHeight;
	}

	protected void setViewModeMinHeight(int viewModeMinHeight) {
		this.viewModeMinHeight = viewModeMinHeight;
		calculateViewModeFixedSize();
	}

	public int getViewModeMaxHeight() {
		return viewModeMaxHeight;
	}

	protected void setViewModeMaxHeight(int viewModeMaxHeight) {
		this.viewModeMaxHeight = viewModeMaxHeight;
		calculateViewModeFixedSize();
	}

	/* SOME GETTERS AND SETTERS */

	public ExtendedElementContainerInterface getContainer() {
		return container;
	}

	public ElementInfo getConfig() {
		return config;
	}

	/* MVCRecipient STUFF */

	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vname) {
		// ValueUpdates form the Model, publish them to the client element
		if (elementVars.contains(vname)) {
			this.setVarValue(vname, model.getValue(vname), model.getValue(ParameterTypes.UserName));
		}
	}

	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {
		onRemoveModelConnection();
		
		this.checkForHighlight(model.getValue(ParameterTypes.UserName));
		
		container.removeExtendedElement(this);
	}
	
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {
		this.connectedModel = model;
		
		this.checkForHighlight(model.getValue(ParameterTypes.UserName));
		
		this.onEstablishModelConnection();
		return true;
	}

	public AbstractUnspecifiedElementModel getConnectedModel() {
		return connectedModel;
	}
	
	/**
	 * Method is called on Element Focus change in order to check for value changes, 
	 * if changes are found a message to the server will be send
	 */
	private void updateModel() {
		// Build ActionSet with all needed Updates
		
		Vector<Object[]> values = new Vector<Object[]>();
		
		for (ParameterTypes vName : elementVars) {
			String value = this.getVarValue(vName);
			if (value != null
					&& ((getConnectedModel().getValue(vName) != null && !getConnectedModel().getValue(vName).equals(
							value)) || getConnectedModel().getValue(vName) == null)) {
				// Value has changed
				Object[] tmp = {vName, value};
				values.add(tmp);
			}
		}

		if (values.size() > 0) {
			// Changes are there, make an update
//			MVCViewSession viewSession = (MVCViewSession) this.getContainer().getMVCViewSession();
			AbstractMVCViewSession viewSession = this.getContainer().getMVCViewSession();
			updateElementWithMultipleValues(viewSession.getController().getMapID(), getConnectedModel().getId(), values);
			
			//communicator.sendActionPackage(actionBuilder.updateElementWithMultipleValues(viewSession.getController().getMapID(), getConnectedModel().getId(), values));
//			communicator.sendActionPackage(actionBuilder.updateElementWithMultipleValues(this.getContainer().getMVCViewSession().getController().getMapID(), getConnectedModel().getId(), values));
		}
	}
	
	public abstract void updateElementWithMultipleValues(String mapID, int id, Vector<Object[]> values);

	/* Focusable Stuff */

	/**
	 * Function manages focus changes, which are used to trigger value changes in the view. 
	 * If an Element looses the focus (goes back to View Mode) it is checked if a value has changed.
	 * 
	 * @param boolean focus Focus = 1 -> Switch to Editmode Focus = 0 -> Switch to Viewmode
	 */
	public void setElementFocus(boolean focus) {
		if (focus) {
			// Get Focus, switch to EditMode
			if (this.activeViewMode) {
				this.setViewMode(false);
			}
			// else nothing to do
		}
		else {
			// Lost Focus, switch to ViewMode
			// And Update Values
			if (!this.activeViewMode) {
				this.setViewMode(true);
				updateModel();
			}
		}
	}
	
	/**
	 * Check whether current user is allowed to modify the element.
	 * @return
	 */
	protected boolean isModificationAllowed() {
		boolean allowModification = true;
		if (container instanceof LASADBoxComponent) {
			LASADBoxComponent boxComponent = (LASADBoxComponent) container;
			if (!boxComponent.isModificationAllowed()) {
				allowModification = false;
			}
		} else if (container instanceof AbstractLink) {
			AbstractLink link = (AbstractLink) container;
			allowModification = link.getLinkPanel().isModificationAllowed();
		}
		
		return allowModification;
	}
	
	/**
	 * Check if committing the text inside boxes and linkPanels is enabled.
	 */
	protected boolean isCommitByEnter() {
		boolean returnValue = false;
		if (container instanceof LASADBoxComponent) {
			LASADBoxComponent boxComponent = (LASADBoxComponent) container;
			returnValue = boxComponent.isCommitTextByEnter();
		} else if (container instanceof AbstractLink) {
			AbstractLink link = (AbstractLink) container;
			returnValue = link.getLinkPanel().isCommitTextByEnter();
		}
		
		return returnValue;
	}

	public FocusableInterface getFocusParent() {
		// The Container is the FocusParent
		return container;
	}

	/* Highlightable Stuff */
	
	/** Check if author and current user are the same
	* if not highlight Box or LinkPanel
	*/
	public void checkForHighlight(String username){
		//LASADInfo.display("Username ExtendedElement ", username);
		//LASADInfo.display("Container",this.getContainer().getClass().getName());
		
		if(username != null && !LASAD_Client.getInstance().getUsername().equalsIgnoreCase(username)){
			if(this.getContainer() instanceof AbstractBox){
				((AbstractBox) this.getContainer()).setHighlightAndBack();
			}else if(this.getContainer() instanceof AbstractLink){
				((AbstractLink) this.getContainer()).getLinkPanel().setHighlightAndBack();
			}
		}
	}
	public void setHighlight(boolean highlight) {

		if (highlight) {
			this.setElementHighlight(true);
			// else nothing to do
		}
		else {
			this.setElementHighlight(false);
		}
	}

	public HighlightableElementInterface getHighlightParent() {
		// The Container is the FocusParent
		return container;
	}
	
	/* SOME ABSTRACT METHODS */

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		// keep element blocked if only author can modify
		this.blocked = (isModificationAllowed()) ? blocked : true;
	}

	protected abstract void switchToViewMode(Element contentFrame);

	protected abstract void switchToEditMode(Element contentFrame);

	protected abstract void setElementHighlight(boolean highlight);

	protected abstract void setElementSize(Size size);
	
	protected abstract String getVarValue(ParameterTypes vName);
	
	// Changes Value of an ExtendElement and checks if there has to be a highlight of the Box
	protected abstract void setVarValue(ParameterTypes vName, String value, String username);

	protected abstract void onEstablishModelConnection();

	protected abstract void onRemoveModelConnection();

}