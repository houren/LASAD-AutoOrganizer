package lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

@SuppressWarnings("serial")
public class TemplateParent extends BaseTreeModel implements Comparable<TemplateParent> {

	private static int ID = 0;

	public TemplateParent() {
		set("id", ID++);
	}

	public TemplateParent(String name) {
		set("id", ID++);
		set("name", name);
	}

	public TemplateParent(String name, BaseTreeModel[] children) {
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

	@Override
	public int compareTo(TemplateParent o) {
		return this.getName().compareTo(o.getName());
	}
}
