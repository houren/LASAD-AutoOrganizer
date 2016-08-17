package lasad.gwt.client.ui.workspace.tableview;

import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.MVCViewRecipient;

public interface CellContainer {

	// ChildElement id und type
	MVCViewRecipient addComponent(String type, String id);
	
	void removeComponent(AbstractUnspecifiedElementModel model);
	
	TableCellInfo getTableCellInfo();
	
	void setTableCellInfo(TableCellInfo tableCellInfo);
	
	void changeView(TableZoomEnum zoom);
	
	TableCellTypeEnum getType();
}
