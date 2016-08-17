package lasad.gwt.client.ui.common.pattern.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.elements.AbstractExtendedDropdownElement;

public class ExtendedDropdownElementPattern extends
		AbstractExtendedDropdownElement {

	public ExtendedDropdownElementPattern(
			ExtendedElementContainerInterface container, ElementInfo config,
			boolean readOnly) {
		super(container, config, readOnly);
	}

	public ExtendedDropdownElementPattern(
			ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);
	}

	@Override
	public void updateElementWithMultipleValues(String mapID, int id,
			Vector<Object[]> values) {
		//Nothing to do

	}

}
