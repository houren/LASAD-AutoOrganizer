package lasad.gwt.client.ui.common.pattern.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.elements.AbstractExtendedURLElement;

public class ExtendedURLElementPattern extends AbstractExtendedURLElement {

	public ExtendedURLElementPattern(
			ExtendedElementContainerInterface container, ElementInfo config,
			boolean readOnly, boolean dummy) {
		super(container, config, readOnly, dummy);
	}

	public ExtendedURLElementPattern(
			ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);
	}

	@Override
	public void updateElementWithMultipleValues(String mapID, int id,
			Vector<Object[]> values) {
		//Nothing to do
	}

}
