package lasad.gwt.client.ui.workspace.tabs.authoring;

import lasad.gwt.client.ui.workspace.tabs.authoring.steps.CreateAndDeleteSessions;
import lasad.gwt.client.ui.workspace.tabs.authoring.steps.CreateAndDeleteTemplate;
import lasad.gwt.client.ui.workspace.tabs.authoring.steps.CreateAndDeleteUsers;
import lasad.gwt.client.ui.workspace.tabs.authoring.steps.CreateModifyAndDeleteOntology;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.google.gwt.user.client.ui.RootPanel;

public class AuthoringTab extends TabItem {	
	
	public static boolean active = false;
	
	public AuthoringTab() {
		
		AuthoringTab.active = true;
		
		this.setLayout(new AccordionLayout());
		this.setSize(RootPanel.get().getElement().getClientWidth(), RootPanel.get().getElement().getClientHeight());
		
		this.setClosable(true);
		this.addStyleName("pad-text");
		this.setText("LASAD Authoring Tool");
		
		initSteps();
		
		this.layout();
	}

	private void initSteps() {
		this.add(new CreateAndDeleteUsers());
		this.add(CreateModifyAndDeleteOntology.getInstance());
		this.add(new CreateAndDeleteTemplate());
		this.add(new CreateAndDeleteSessions());
	}
}
