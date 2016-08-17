package lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists;

import lasad.gwt.client.model.ElementInfo;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

@SuppressWarnings("serial")
public class OntologyParent extends BaseTreeModel implements Comparable<OntologyParent> {

	private static int ID = 0;
	private ElementInfo elementInfo;
	
	public ElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(ElementInfo elementInfo) {
		this.elementInfo = elementInfo;
	}
	
	public OntologyParent() {
		set("id", ID++);
	}

	public OntologyParent(String name) {
		set("id", ID++);
		set("name", name);
	}

	public OntologyParent(String name, BaseTreeModel[] children) {
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
	public int compareTo(OntologyParent o) {
		return this.getName().compareTo(o.getName());
	}
}
