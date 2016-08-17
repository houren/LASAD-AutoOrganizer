package lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

@SuppressWarnings("serial")
public class UserChild extends BaseTreeModel {

	public UserChild(String name) {
		set("name", name);
		set("type", "USER");
	}

	public String getName() {
		return (String) get("name");
	}

	public String toString() {
		return getName();
	}

	public String getType() {
		return (String) get("type");
	}
}
