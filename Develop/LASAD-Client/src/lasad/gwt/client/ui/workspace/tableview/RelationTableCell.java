package lasad.gwt.client.ui.workspace.tableview;
//package lasad.gwt.client.ui.table;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import lasad.gwt.client.model.MVCViewRecipient;
//import lasad.gwt.client.model.UnspecifiedElementModel;
//
//import com.extjs.gxt.ui.client.widget.Component;
//import com.extjs.gxt.ui.client.widget.LayoutContainer;
//import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
//
///**
// * A TableCell, that contains more than one Argument Relations.
// * 
// * @author erkang
// *
// */
//public class RelationTableCell extends LayoutContainer implements CellContainer{
//
//	//*************************************************************************************
//	//	Fields
//	//*************************************************************************************
//	
//	private TableCellInfo tableCellInfo;
//	
//	private TableCellTypeEnum type;
//	
////	private boolean editMode;
//	
//	//*************************************************************************************
//	//	Constructor
//	//*************************************************************************************
//	
//	public RelationTableCell() {
//		
//		setBorders(true);
//		setLayout(new AnchorLayout());
////		setSize(230, 230);
//		
//		setSize(ArgumentMapTable.preferredSize.width, ArgumentMapTable.preferredSize.height);
//		
//	}
//	
//	//*************************************************************************************
//	//	Methods
//	//*************************************************************************************
//	
//	
//	public List<UnspecifiedElementModel> getModels() {
//		
//		List<UnspecifiedElementModel> models = new ArrayList<UnspecifiedElementModel>();
//		
//		for (Component component : getItems()) {
//			
//			if (component instanceof RelationCell) {
//				
//				RelationCell cell = (RelationCell)component;
//				
//				UnspecifiedElementModel model = cell.getConnectedModel();
//				
//				models.add(model);
//			}
//			
//		}
//		
//		
//		
//		return models;
//	}
//	
//	
//	//*************************************************************************************
//	// CellContainer
//	//*************************************************************************************
//
//	@Override
//	public MVCViewRecipient addComponent(String type, String id) {
//		
//		return null;
//		
//	}
//	
//	
//	@Override
//	public void removeComponent(UnspecifiedElementModel model) {
//		// TODO Auto-generated method stub
//		
//		
////		List<Component> components = getItems();
////		
////		for (Component component : components) {
////			
////			if (component instanceof MVCViewRecipient) {
////				
////				MVCViewRecipient recipient = (MVCViewRecipient)component;
////				UnspecifiedElementModel elementModel = recipient.getConnectedModel();
////				
////				if (model.equals(elementModel)) {
////					
////					remove(component);
////					layout();
////					
////					break;
////				}
////				
////			}
////		}
//	}
//	
//
//	@Override
//	public TableCellInfo getTableCellInfo() {
//		return tableCellInfo;
//	}
//
//
//	@Override
//	public void setTableCellInfo(TableCellInfo tableCellInfo) {
//		this.tableCellInfo = tableCellInfo;
//	}
//
//	
//	public void changeView(TableZoomEnum zoom) {
//		
////		setSize(ArgumentMapTable.preferredSize.width, ArgumentMapTable.preferredSize.height);
//		
//		for (Component component: getItems()) {
//			
//			if (component instanceof RelationCell) {
//				
//				RelationCell relationCell = (RelationCell)component;
//				
//				relationCell.changeView(zoom);
//				
////				setLayoutData(relationCell, new AnchorData("100% 10%"));
//			}
//			
//			
//			
////			if (component instanceof CellContainer) {
////				
////				CellContainer cellContainer = (CellContainer)component;
////				cellContainer.changeView(zoom);
////			}
//		}
//	}
//
//
//	
//	//*************************************************************************************
//	// getter & setter
//	//*************************************************************************************
//	
//	/**
//	 * @return the type
//	 */
//	public TableCellTypeEnum getType() {
//		return type;
//	}
//
//	/**
//	 * @param type the type to set
//	 */
//	public void setType(TableCellTypeEnum type) {
//		this.type = type;
//	}
//}
