package lasad.gwt.client.model;

import java.util.Vector;


public class MVCHelper {

	public static Vector<AbstractUnspecifiedElementModel> getChildModelsByElementID(AbstractUnspecifiedElementModel parent, String type) {
		Vector<AbstractUnspecifiedElementModel> childs = new Vector<AbstractUnspecifiedElementModel>();
		for (AbstractUnspecifiedElementModel child : parent.getChildren()) {
			if (child.getElementid() != null) {
				if (child.getElementid().equalsIgnoreCase(type)) {
					childs.add(child);
				}
			}
		}
		return childs;
	}

	public static Vector<AbstractUnspecifiedElementModel> getChildModelsByType(AbstractUnspecifiedElementModel parent, String type) {
		Vector<AbstractUnspecifiedElementModel> childs = new Vector<AbstractUnspecifiedElementModel>();
		for (AbstractUnspecifiedElementModel child : parent.getChildren()) {
			if (child.getType() != null) {
				if (child.getType().equalsIgnoreCase(type)) {
					childs.add(child);
				}
			}
		}
		return childs;
	}

	public static Vector<AbstractUnspecifiedElementModel> getParentModelsByElementID(AbstractUnspecifiedElementModel child, String type) {
		Vector<AbstractUnspecifiedElementModel> parents = new Vector<AbstractUnspecifiedElementModel>();
		for (AbstractUnspecifiedElementModel parent : child.getParents()) {
			if (parent.getElementid() != null) {
				if (parent.getElementid().equalsIgnoreCase(type)) {
					parents.add(parent);
				}
			}
		}
		return parents;
	}

	public static Vector<AbstractUnspecifiedElementModel> getParentModelsByType(AbstractUnspecifiedElementModel child, String type) {
		Vector<AbstractUnspecifiedElementModel> parents = new Vector<AbstractUnspecifiedElementModel>();
		for (AbstractUnspecifiedElementModel parent : child.getParents()) {
			if (parent.getType() != null) {
				if (parent.getType().equalsIgnoreCase(type)) {
					parents.add(parent);
				}
			}
		}
		return parents;
	}
}
