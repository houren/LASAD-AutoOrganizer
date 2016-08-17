package lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

@SuppressWarnings("serial")
public class RoleParent extends BaseTreeModel {

	private static int ID = 0;

	public RoleParent() {
		set("id", ID++);
	}

	public RoleParent(String name) {
		set("id", ID++);
		set("name", name);
	}

	public RoleParent(String name, BaseTreeModel[] children) {
		this(name);
		for (int i = 0; i < children.length; i++) {
			add(children[i]);
		}
	}

	public Integer getId() {
		return (Integer) get("id");
	}

	public String getName() {
		return (String) get("name");
	}

	public String toString() {
		return getName();
	}
}
