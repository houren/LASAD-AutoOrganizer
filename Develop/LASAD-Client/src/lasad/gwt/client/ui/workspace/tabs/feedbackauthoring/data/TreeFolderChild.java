package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;

public class TreeFolderChild extends BaseTreeModel implements Serializable, Comparable<TreeFolderChild> {

	/**
	 * This class represents a child element in a {@link TreeGrid}
	 */
	private static final long serialVersionUID = 8093171177003553908L;
	
	public TreeFolderChild(){
	}
	
	public TreeFolderChild(Map<String, String> map) {
		this();
		Set<String> keys = map.keySet();
		for(String key:keys){
			String value = map.get(key);
			set(key, value);
		}
	}
	public String getValue(String key){
		return (String) get(key);
	}
	
	public TreeFolderChild(String name, String status){
		set(GridElementLabel.NAME, name);
		set(GridElementLabel.NAME, status);
	}
	
	public TreeFolderChild(String name, String status, String aux){
		set(GridElementLabel.NAME, name);
		set(GridElementLabel.STATUS, status);
		set(GridElementLabel.AUX, aux);
	}
	
	public String getName() {
		return (String) get(GridElementLabel.NAME);
	}
//	public String getAux() {
//		return (String) get(GridElementLabel.AUX);
//	}

	@Override
	public String toString() {
		return getValue(GridElementLabel.NAME);
	}
	
	@Override
	public int compareTo(TreeFolderChild o) {
		if (this.getValue(GridElementLabel.ID) != null && o.getValue(GridElementLabel.ID) != null){
			return this.getValue(GridElementLabel.ID).compareTo(o.getValue(GridElementLabel.ID));
		}else{
			return this.getValue(GridElementLabel.NAME).compareTo(o.getValue(GridElementLabel.NAME));
		}
//		return this.getValue(GridElementLabel.NAME).compareTo(o.getValue(GridElementLabel.NAME));
		//return this.getName().compareTo(o.getName());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeFolderChild other = (TreeFolderChild) obj;
		if(this.getValue(GridElementLabel.ID) != null && other.getValue(GridElementLabel.ID) != null){
			if (!this.getValue(GridElementLabel.ID).equals(other.getValue(GridElementLabel.ID)))
				return false;
			return true;
		}else{
			if (this.getValue(GridElementLabel.NAME) == null) {
				if (other.getValue(GridElementLabel.NAME) != null)
					return false;
			} else if (!this.getValue(GridElementLabel.NAME).equals(other.getValue(GridElementLabel.NAME)))
				return false;
			return true;
		}
		
	}
}
