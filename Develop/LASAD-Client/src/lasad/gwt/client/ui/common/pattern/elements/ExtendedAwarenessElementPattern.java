package lasad.gwt.client.ui.common.pattern.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.elements.AbstractExtendedAwarenessElement;

public class ExtendedAwarenessElementPattern extends
		AbstractExtendedAwarenessElement {

	public ExtendedAwarenessElementPattern(
			ExtendedElementContainerInterface recipient,
			ElementInfo elementInfo, String value) {
		super(recipient, elementInfo, value);
	}

	public ExtendedAwarenessElementPattern(
			ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);
	}

	@Override
	public void updateElementWithMultipleValues(String mapID, int id,
			Vector<Object[]> values) {
		//Nothing to do
	}

}
