package lasad.gwt.client.ui.common.pattern.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.elements.AbstractExtendedSpotsElement;

public class ExtendedSpotsElementPattern extends AbstractExtendedSpotsElement {
	

	public ExtendedSpotsElementPattern(
			ExtendedElementContainerInterface container, ElementInfo config,
			int valueSpot1, int valueSpot2) {
		super(container, config, valueSpot1, valueSpot2);
	}

	@Override
	public void updateElementWithMultipleValues(String mapID, int id,
			Vector<Object[]> values) {
		//Nothing to do

	}

}
