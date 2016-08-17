package lasad.gwt.client.ui.common.pattern.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.elements.AbstractExtendedTranscriptElement;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;

public class ExtendedTranscriptElementPattern extends
		AbstractExtendedTranscriptElement {

	public ExtendedTranscriptElementPattern(
			ExtendedElementContainerInterface container, ElementInfo config,
			TranscriptLinkData tData) {
		super(container, config, tData);
	}

	@Override
	public void updateElementWithMultipleValues(String mapID, int id,
			Vector<Object[]> values) {
		//Nothing to do
	}

}
