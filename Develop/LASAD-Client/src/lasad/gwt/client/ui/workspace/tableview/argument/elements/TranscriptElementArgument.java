package lasad.gwt.client.ui.workspace.tableview.argument.elements;

import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTranscriptElement;

public class TranscriptElementArgument extends AbstractTranscriptElement {

	public TranscriptElementArgument(ElementInfo info) {
		super(info);
	}
	
	protected final ActionFactory actionFactory = ActionFactory.getInstance();
	protected final LASADActionSender actionSender = LASADActionSender.getInstance();
	@Override
	protected void sendUpdateElementWithMultipleValuesToServer(String mapID,
			int elementID, Vector<Object[]> values) {
		actionSender.sendActionPackage(actionFactory.updateElementWithMultipleValues(mapID, elementID, values));
	}

}
