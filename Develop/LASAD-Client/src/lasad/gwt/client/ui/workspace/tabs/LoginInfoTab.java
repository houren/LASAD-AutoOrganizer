package lasad.gwt.client.ui.workspace.tabs;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class LoginInfoTab extends TabItem {

	public LoginInfoTab() {
		this.setText("Login Information");
		this.setClosable(true);
		this.addStyleName("pad-text");
		this.setLayout(new FitLayout());
		this.setBorders(false);
	}
}
