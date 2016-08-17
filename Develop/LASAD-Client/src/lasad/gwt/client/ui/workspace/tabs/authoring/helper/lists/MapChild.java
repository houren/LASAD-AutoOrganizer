package lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

@SuppressWarnings("serial")
public class MapChild extends BaseTreeModel {

	public MapChild() {

	}

	public MapChild(String name) {
		set("name", name);
	}

	public String getName() {
		return (String) get("name");
	}

	public String toString() {
		return getName();
	}

}
