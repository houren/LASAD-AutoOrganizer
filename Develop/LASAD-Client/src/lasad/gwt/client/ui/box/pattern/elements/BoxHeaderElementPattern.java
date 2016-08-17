package lasad.gwt.client.ui.box.pattern.elements;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.elements.AbstractBoxHeaderElement;
import lasad.gwt.client.ui.box.helper.AbstractBoxHeaderButtonListener;
import lasad.gwt.client.ui.box.pattern.helper.BoxHeaderButtonListenerPattern;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ElementVariableUtil;

public class BoxHeaderElementPattern extends AbstractBoxHeaderElement {

	public BoxHeaderElementPattern(AbstractBox correspondingBox, String title,
			boolean isReplay) {
		super(correspondingBox, title, isReplay);
	}

	@Override
	protected AbstractBoxHeaderButtonListener createBoxHeaderButtonListener(
			AbstractBox correspondingBox) {
		return new BoxHeaderButtonListenerPattern(this.correspondingBox);
	}
	
	@Override
	public void setRootElementID(String rootElementID) {
		this.rootElementID = rootElementID;

		String header = ElementVariableUtil.createHeaderForBoxLink(this.title, rootElementID);
		contentText.setInnerHTML("<b>" + header + "</b>");//&nbsp;
	}

}
