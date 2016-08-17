package lasad.gwt.client.ui.workspace.tabs.authoring.helper.elements.ui;

import lasad.gwt.client.ui.common.helper.TextSelection;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * The title bar of a box
 * 
 * @author Frank Loll
 * 
 */
public class SimpleBoxHeaderElement extends BoxComponent {

	private Element content;
	private Element contentText;
	private Element delButton;
	private Element addButton;
	private Element closeButton;

	private String title;
	private String rootElementID;

	/**
	 * The correspondingBox is needed to remove it from the map
	 * 
	 * @param correspondingBox
	 * @param title
	 */
	public SimpleBoxHeaderElement(String title) {
		this.title = title;

		// Create elements
		content = DOM.createDiv();
		DOM.setStyleAttribute(content, "height", "13px");

		contentText = DOM.createDiv();
		contentText.setInnerHTML(this.title);
		DOM.appendChild(content, contentText);

		closeButton = DOM.createDiv();
		DOM.appendChild(content, closeButton);

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
		
		DOM.setStyleAttribute(this.addButton, "visibility", "visible");
		DOM.setStyleAttribute(this.delButton, "visibility", "visible");
	}

	public void setRootElementID(String rootElementID) {
		this.rootElementID = rootElementID;

		contentText.setInnerHTML(this.rootElementID + "&nbsp;&middot;&nbsp;<b>" + this.title + "</b>");
	}

	@Override
	protected void onRender(Element target, int index) {
		this.setElement(content, target, index);
	}
}