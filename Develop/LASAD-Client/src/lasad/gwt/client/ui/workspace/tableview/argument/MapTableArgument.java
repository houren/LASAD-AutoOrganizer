package lasad.gwt.client.ui.workspace.tableview.argument;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapSpace;
import lasad.gwt.client.ui.workspace.tableview.AbstractMapTable;
import lasad.gwt.client.ui.workspace.tableview.AbstractTableCell;
import lasad.gwt.client.ui.workspace.tableview.TableCellTypeEnum;

public class MapTableArgument extends AbstractMapTable{
	
	protected LASADActionSender communicator = LASADActionSender.getInstance();
	protected ActionFactory actionBuilder = ActionFactory.getInstance();

	public MapTableArgument(ArgumentMapSpace parentElement) {
		super(parentElement);
	}

	@Override
	protected void sendUpdateMyCursorPositionPersistentToServer(String mapID,
			int cursorID, String username, int x, int y) {
		communicator.sendActionPackage(actionBuilder.updateMyCursorPositionPersistent(mapID, cursorID, username, x, y));
	}

	@Override
	protected void sendUpdateMyCursorPositionNonPersistentToServer(
			String mapID, int cursorID, String username, int x, int y) {
		communicator.sendActionPackage(actionBuilder.updateMyCursorPositionNonPersistent(mapID, cursorID, username, x, y));
	}

	@Override
	protected void sendRemoveElementToServer(String mapID, int elementID) {
		communicator.sendActionPackage(actionBuilder.removeElement(mapID, elementID));
	}

	@Override
	protected RelatedRelationArgument getRelatedRelation(ElementInfo info,
			TableCellTypeEnum type) {
		return new RelatedRelationArgument(info, type);
	}

	@Override
	protected AbstractTableCell getTableCell(ElementInfo info,
			TableCellTypeEnum type) {
		return new TableCellArgument(info, type);
	}

}
