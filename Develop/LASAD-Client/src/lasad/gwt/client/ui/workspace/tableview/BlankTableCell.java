package lasad.gwt.client.ui.workspace.tableview;

import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.MVCViewRecipient;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * A blank TableCell without any child components.
 * 
 * It should contain TableCellInfo, and this is needed by flexTable within
 * ArgumentMapTable. So implements CellContainer.
 * 
 * @author erkang
 * 
 */
public class BlankTableCell extends LayoutContainer implements CellContainer {

	private TableCellInfo tableCellInfo;
	private TableCellTypeEnum type;

	public BlankTableCell(TableCellTypeEnum type) {

		this.type = type;

		setBorders(true);
		setStyleAttribute("background", "#ADDAF1");
	}

	@Override
	public MVCViewRecipient addComponent(String type, String id) {
		return null;
	}

	@Override
	public void removeComponent(AbstractUnspecifiedElementModel model) {
	}

	@Override
	public void changeView(TableZoomEnum zoom) {
	}

	public TableCellInfo getTableCellInfo() {
		return tableCellInfo;
	}

	public void setTableCellInfo(TableCellInfo tableCellInfo) {
		this.tableCellInfo = tableCellInfo;
	}

	/**
	 * @return the type
	 */
	public TableCellTypeEnum getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(TableCellTypeEnum type) {
		this.type = type;
	}

}
