package lasad.gwt.client.ui.workspace.tableview.argument.elements;

import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTextElement;

public class TextElementArgument extends AbstractTextElement {
	
	protected final ActionFactory actionFactory = ActionFactory.getInstance();
	protected final LASADActionSender actionSender = LASADActionSender.getInstance();

	public TextElementArgument(ElementInfo info) {
		super(info);
	}

	@Override
	protected void sendUpdateElementWithMultipleValuesToServer(String mapID,
			int elementID, Vector<Object[]> values) {
		actionSender.sendActionPackage(actionFactory.updateElementWithMultipleValues(mapID, elementID, values));
	}

	@Override
	protected void sendLockElementToServer(String mapID, int elementID) {
		actionSender.sendActionPackage(actionFactory.lockElement(mapID, elementID));
	}

	@Override
	protected void sendUnlockElementToServer(String mapID, int elementID) {
		actionSender.sendActionPackage(actionFactory.unlockElement(mapID, elementID));
	}

}
