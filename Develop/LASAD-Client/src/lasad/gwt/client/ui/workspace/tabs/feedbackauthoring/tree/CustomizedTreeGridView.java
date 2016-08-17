package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.tree;

import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;

public class CustomizedTreeGridView extends TreeGridView {
	
	public CustomizedTreeGridView(){
		this.setRowHeight(28);
		//these lines are used to keep the row highlighted even when the mouse is over a Widget
		this.setRowSelectorDepth(30);
		this.setCellSelectorDepth(20);
	}
	
}
