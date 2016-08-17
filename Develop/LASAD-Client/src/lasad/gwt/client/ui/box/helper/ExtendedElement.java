package lasad.gwt.client.ui.box.helper;

import lasad.gwt.client.ui.GraphicalElementInterface;

import com.google.gwt.user.client.Element;

public class ExtendedElement implements hasFixedHeightInterface {

	protected Element content;
	protected boolean inViewMode = false;
	
	public Element getContent() {
		return content;
	}

	public boolean hasFixedHeight() {
		return false;
	}

	public int getMinHeight() {
		return 0;
	}
	
	public void calculateSize() { }
	
	public void unselect() {}
	
	public void getViewMode() {}
	
	public void getEditMode() {}
	
	protected GraphicalElementInterface parent = null;

	public GraphicalElementInterface getParent() {
		return parent;
	}

	public void setParent(GraphicalElementInterface parent) {
		this.parent = parent;
	}
	
	
}