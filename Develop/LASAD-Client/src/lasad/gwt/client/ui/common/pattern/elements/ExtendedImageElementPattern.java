package lasad.gwt.client.ui.common.pattern.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.elements.AbstractExtendedImageElement;

public class ExtendedImageElementPattern extends AbstractExtendedImageElement {
	
	public ExtendedImageElementPattern(boolean readonly,
			ExtendedElementContainerInterface container, ElementInfo config) {
		super(readonly, container, config);
	}

	@Override
	public void updateElementWithMultipleValues(String mapID, int id,
			Vector<Object[]> values) {
		//Nothing to do

	}

}
