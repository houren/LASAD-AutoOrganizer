package lasad.gwt.client.ui.common.argument.elements;

import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.elements.AbstractExtendedTextElement;

public class ExtendedTextElementArgument extends AbstractExtendedTextElement {
	
	protected final LASADActionSender communicator = LASADActionSender.getInstance();
	protected final ActionFactory actionBuilder = ActionFactory.getInstance();

	public ExtendedTextElementArgument(boolean readOnly,
			ExtendedElementContainerInterface container, ElementInfo config,
			boolean labelOnTop) {
		super(readOnly, container, config, labelOnTop);
	}

	public ExtendedTextElementArgument(boolean readOnly,
			ExtendedElementContainerInterface container, ElementInfo config) {
		super(readOnly, container, config);
	}

	public ExtendedTextElementArgument(ExtendedElementContainerInterface container,
			ElementInfo config) {
		super(container, config);
	}

	@Override
	public void updateElementWithMultipleValues(String mapID, int id,
			Vector<Object[]> values) {
		communicator.sendActionPackage(actionBuilder.updateElementWithMultipleValues(mapID, id, values));
	}

	@Override
	protected void sendRequestLockElement(String mapID, int elementID) {
		communicator.sendActionPackage(actionBuilder.lockElement(mapID, elementID));
	}

	@Override
	protected void sendRequestUnlockElement(String mapID, int elementID) {
		communicator.sendActionPackage(actionBuilder.unlockElement(mapID, elementID));
	}

}
