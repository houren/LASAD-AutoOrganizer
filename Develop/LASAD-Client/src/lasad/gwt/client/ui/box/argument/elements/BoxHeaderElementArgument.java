package lasad.gwt.client.ui.box.argument.elements;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.argument.helper.BoxHeaderButtonListenerArgument;
import lasad.gwt.client.ui.box.elements.AbstractBoxHeaderElement;
import lasad.gwt.client.ui.box.helper.AbstractBoxHeaderButtonListener;

public class BoxHeaderElementArgument extends AbstractBoxHeaderElement {

	public BoxHeaderElementArgument(AbstractBox correspondingBox, String title,
			boolean isReplay) {
		super(correspondingBox, title, isReplay);
	}

	@Override
	protected AbstractBoxHeaderButtonListener createBoxHeaderButtonListener(
			AbstractBox correspondingBox) {
		return new BoxHeaderButtonListenerArgument(this.correspondingBox);
	}

}
