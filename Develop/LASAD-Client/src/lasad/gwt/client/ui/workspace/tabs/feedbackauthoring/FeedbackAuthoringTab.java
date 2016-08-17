package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Feedback Authoring Tool Tab
 * 
 * @author anahuacv
 * 
 */

public class FeedbackAuthoringTab  extends TabItem {
	
	public static final String LASAD_FEEDBACK_AUTHORING_TOOL = "LASAD" + " " + FeedbackAuthoringStrings.FEEDBACK_AUTHORING_TOOL;
	
	public static boolean active = false;
	
	public FeedbackAuthoringTab(){
		FeedbackAuthoringTab.active = true;
		
		this.setClosable(true);
		this.addStyleName("pad-text");
		this.setLayout(new FitLayout());
		//this.setLayout(new BorderLayout());
		this.setText(FeedbackAuthoringStrings.FEEDBACK_AUTHORING_TOOL);
		this.setSize(RootPanel.get().getElement().getClientWidth(), RootPanel.get().getElement().getClientHeight());
		
		initSteps();
		this.layout();
	}
	
	private void initSteps() {
		/* Setting up tab content */
		this.add(FeedbackAuthoringTabContent.getInstance(), new BorderLayoutData(LayoutRegion.CENTER));
	}
	
}
