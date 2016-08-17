package lasad.gwt.client.ui.box.elements;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCHelper;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractBoxHeaderButtonListener;
import lasad.gwt.client.ui.common.helper.TextSelection;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;



/**
 * The title bar of a box
 * 
 * @author Frank Loll
 * @author anahuacv refactoring
 * 
 */
public abstract class AbstractBoxHeaderElement extends BoxComponent {

	private Element content;
	protected Element contentText;
	private Element delButton;
	private Element addButton;
	private Element closeButton;
	private Element configButton;

	protected String title;
	protected String rootElementID;
	protected AbstractBox correspondingBox;

	private AbstractBoxHeaderButtonListener buttonListener;

	/**
	 * The correspondingBox is needed to remove it from the map
	 * 
	 * @param correspondingBox
	 * @param title
	 */
	public AbstractBoxHeaderElement(AbstractBox correspondingBox, String title, boolean isReplay) {
		this.correspondingBox = correspondingBox;
		this.title = title;

		// Create elements
		content = DOM.createDiv();
		DOM.setStyleAttribute(content, "height", "13px");

		contentText = DOM.createDiv();
		contentText.setInnerHTML(this.title);
		DOM.appendChild(content, contentText);

		closeButton = DOM.createDiv();
		DOM.appendChild(content, closeButton);
		
		configButton = DOM.createDiv();
		configButton.setClassName("config-button");
		DOM.setStyleAttribute(this.configButton, "visibility", "hidden");
		String configButtonFlag = correspondingBox.getElementInfo().getElementOption(ParameterTypes.ConfigButton);
		if(configButtonFlag != null || Boolean.parseBoolean(configButtonFlag)){
			DOM.appendChild(content, configButton);
		}
		

		addButton = DOM.createDiv();
		DOM.appendChild(content, addButton);

		delButton = DOM.createDiv();
		DOM.appendChild(content, delButton);
		
		content.setClassName("box-heading");
		contentText.setClassName("box-heading-text");
		closeButton.setClassName("close-button");		
		addButton.setClassName("add-button");
		delButton.setClassName("del-button");
		
		TextSelection.disableTextSelection(contentText);
		
		DOM.setStyleAttribute(this.addButton, "visibility", "hidden");
		DOM.setStyleAttribute(this.delButton, "visibility", "hidden");
		
		// Create Listener for the buttons in the header
		if(!isReplay)
		{
			//this.buttonListener = new BoxHeaderButtonListener(this.correspondingBox);
			this.buttonListener = createBoxHeaderButtonListener(this.correspondingBox);
			DOM.sinkEvents(closeButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
			DOM.setEventListener(closeButton, buttonListener);
			
			if(configButtonFlag != null || Boolean.parseBoolean(configButtonFlag)){
				DOM.sinkEvents(configButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
				DOM.setEventListener(configButton, buttonListener);
			}

			DOM.sinkEvents(addButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
			DOM.setEventListener(addButton, buttonListener);

			DOM.sinkEvents(delButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
			DOM.setEventListener(delButton, buttonListener);
		}		
	}
	protected abstract AbstractBoxHeaderButtonListener createBoxHeaderButtonListener(AbstractBox correspondingBox);

	public void setRootElementID(String rootElementID) {
		this.rootElementID = rootElementID;

		contentText.setInnerHTML(this.rootElementID + "&nbsp;&middot;&nbsp;<b>" + this.title + "</b>");
	}

	public Element getContentText(){
		return contentText;
	}
	
	public void setEditButtonVisibility(boolean visible) {
		if (visible) {

			for (ElementInfo childElement : correspondingBox.getElementInfo().getChildElements().values()) {
				// Calculate current element Count
				int elementCount = MVCHelper.getChildModelsByElementID(correspondingBox.getConnectedModel(), childElement.getElementID()).size();
				if ((childElement.getElementOption(ParameterTypes.ManualAdd) == null || !childElement.getElementOption(ParameterTypes.ManualAdd).equals("false")) && elementCount < childElement.getMaxQuantity()) {
					DOM.setStyleAttribute(this.addButton, "visibility", "visible");
				}
				if (elementCount > childElement.getMinQuantity()) {
					DOM.setStyleAttribute(this.delButton, "visibility", "visible");
				}
			}
			DOM.setStyleAttribute(this.configButton, "visibility", "visible");

		} else {
			if (addButton.getClassName().equals("add-button") && delButton.getClassName().equals("del-button") && closeButton.getClassName().equals("close-button")) {
				DOM.setStyleAttribute(this.addButton, "visibility", "hidden");
			}
			if (delButton.getClassName().equals("del-button") && addButton.getClassName().equals("add-button") && closeButton.getClassName().equals("close-button")) {
				DOM.setStyleAttribute(this.delButton, "visibility", "hidden");
			}
			if(configButton.getClassName().equals("config-button")){
				DOM.setStyleAttribute(this.configButton, "visibility", "hidden");
			}
		}

	}

	@Override
	protected void onRender(Element target, int index) {
		this.setElement(content, target, index);
	}
}
