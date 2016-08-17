package lasad.gwt.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 *  The model for a single element (i.e. link or box, not sure if text boxes/awareness are included).
 *  With this, you can find an element's children, parents, etc.
 */

public abstract class AbstractUnspecifiedElementModel {

	private int id;
	private String type;
	private String elementid;
	private String author;

	private Vector<AbstractUnspecifiedElementModel> parents = new Vector<AbstractUnspecifiedElementModel>();
	private Vector<AbstractUnspecifiedElementModel> children = new Vector<AbstractUnspecifiedElementModel>();
	private Vector<Integer> highlights = new Vector<Integer>();
	
	private Map<ParameterTypes, String> elementValues = new HashMap<ParameterTypes, String>();
	//private MVController controller;
	private boolean isReplay = false;

	public AbstractUnspecifiedElementModel(int id, String type) {
		this.id = id;
		this.type = type;
	}

	public AbstractUnspecifiedElementModel(int id, String type, String author) {
		this.id = id;
		this.type = type;
		this.author = author;
	}

	abstract public void register2MVController(AbstractMVController controller);
//	public void register2MVController(MVController controller) {
//		this.controller = controller.addElementModel(this);
//	}

	// Model Stuff
	public boolean setValue(ParameterTypes vName, String value) {
		if (elementValues.containsKey(vName) && elementValues.get(vName).equals(value)) {
			// Nothing to do, Value doesn't change
			return false;
		} else {
			elementValues.put(vName, value);
			return true;
		}
	}

	public String getValue(ParameterTypes vName) {
		return elementValues.get(vName);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getElementid() {
		return elementid;
	}

	public void setElementId(String elementid) {
		this.elementid = elementid;
	}

	public Map<ParameterTypes, String> getElementValues() {
		return elementValues;
	}

	public void setElementValues(Map<ParameterTypes, String> elementValues) {
		this.elementValues = elementValues;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	// Parent Child Stuff

	public Vector<AbstractUnspecifiedElementModel> getParents() {
		return parents;
	}

	public void setParents(Vector<AbstractUnspecifiedElementModel> parents) {
		this.parents = parents;
	}

	public Vector<AbstractUnspecifiedElementModel> getChildren() {
		return children;
	}

	public void setChilds(Vector<AbstractUnspecifiedElementModel> children) {
		this.children = children;
	}

	public void addParent(AbstractUnspecifiedElementModel parent) {
		this.parents.add(parent);
	}

	public void addChild(AbstractUnspecifiedElementModel child) {
		this.children.add(child);
		child.addParent(this);
	}

	public void removeChild(AbstractUnspecifiedElementModel child) {
		this.children.remove(child);
		child.removeParent(this);
	}

	public void removeParent(AbstractUnspecifiedElementModel parent) {
		this.parents.remove(parent);
	}

	public Vector<Integer> getHighlights() {
		return highlights;
	}

	public void setHighlights(Vector<Integer> highlights) {
		this.highlights = highlights;
	}
	
	public void showHighlights(boolean show) {
		
	}
	
	public void setIsReplay(boolean isR)
	{
		this.isReplay = isR;
	}
	
	public boolean getIsReplay()
	{
		return this.isReplay;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result
				+ ((elementid == null) ? 0 : elementid.hashCode());
		result = prime * result + id;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractUnspecifiedElementModel other = (AbstractUnspecifiedElementModel) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (elementid == null) {
			if (other.elementid != null)
				return false;
		} else if (!elementid.equals(other.elementid))
			return false;
		if (id != other.id)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("AbstractUnspecifiedElementModel [id=" + id + ", type=" + type
				+ ", elementid=" + elementid + ", author=" + author + ", parents=");
		for(AbstractUnspecifiedElementModel parent : parents){
			buf.append("(id=" + parent.id + ", type=" + parent.type + ", elementid=" + parent.elementid + "), ");
		}
		buf.append("children=");
		for(AbstractUnspecifiedElementModel child : children){
			buf.append("(id=" + child.id + ", type=" + child.type + ", elementid=" + child.elementid + "), ");
		}
		buf.append("]");
		return buf.toString();
				
//		return "AbstractUnspecifiedElementModel [id=" + id + ", type=" + type
//				+ ", elementid=" + elementid + ", author=" + author
//				+ ", parents=" + parents + ", children=" + children + "]";
	}
	
	
}
