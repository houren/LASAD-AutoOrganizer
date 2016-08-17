package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

import com.extjs.gxt.ui.client.widget.grid.GridView;

/**
 * Customized {@link GridView} for the {@link CustomizedGrid}.
 * @author Anahuac
 *
 */
public class CustomizedGridView extends GridView {
	
	public CustomizedGridView(){
		//these lines are used to keep the row highlighted even when the mouse is over a Widget
		this.setRowSelectorDepth(20);
		this.setCellSelectorDepth(10);
	}
	
	/**
	 * Method to catch the row selection action
	 */
	@Override
	protected void onRowSelect(int rowIndex){
		super.onRowSelect(rowIndex);
	}
	
//	@Override
//	protected void onRowOver(Element row) {
//		super.onRowOver(row);
////        FATDebug.print(FATDebug.WAR, "onRowOver");
//	}
//	
//    @Override
//    protected void onRowOut(Element row) {
//        super.onRowOut(row);
////        FATDebug.print(FATDebug.WAR, "onRowOut");
//    }
	
}
