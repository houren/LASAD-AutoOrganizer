package lasad.gwt.client.ui.workspace.details;

import lasad.gwt.client.ui.common.helper.TextSelection;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * The title bar of a box
 * 
 * @author Frank Loll
 * 
 */
public class SimplifiedBoxHeaderElement {
	
	public Element getContent() {
		return content;
	}

	private Element content;
	private Element contentText;

	private String title;
	private String rootElementID;

	/**
	 * The correspondingBox is needed to remove it from the map
	 * 
	 * @param correspondingBox
	 * @param title
	 */
	public SimplifiedBoxHeaderElement(String title) {
		this.title = title;

		// Create elements
		content = DOM.createDiv();
		DOM.setStyleAttribute(content, "height", "13px");

		contentText = DOM.createDiv();
		contentText.setInnerHTML(this.title);
		DOM.appendChild(content, contentText);


		content.setClassName("box-heading");
		contentText.setClassName("box-heading-text");
		
		TextSelection.disableTextSelection(contentText);
	}
	
	public void setTitle(String t) {
		contentText.setInnerHTML(t);
	}

	public void setRootElementID(String rootElementID) {
		this.rootElementID = rootElementID;

		contentText.setInnerHTML(this.rootElementID + "&nbsp;&middot;&nbsp;<b>" + this.title + "</b>");
	}
}