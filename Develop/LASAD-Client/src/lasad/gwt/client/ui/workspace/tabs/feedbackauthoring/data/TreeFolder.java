package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;

public class TreeFolder extends BaseTreeModel implements Serializable, Comparable<TreeFolder>  {

	/**
	 * This class represents a parent element in a {@link TreeGrid}
	 */
	private static final long serialVersionUID = -2494870527406041792L;
	
	public TreeFolder() {
	}

	public TreeFolder(Map<String, String> map) {
		this();
		Set<String> keys = map.keySet();
		for(String key:keys){
			String value = map.get(key);
			set(key, value);
		}
	}
	public TreeFolder(Map<String, String> map, BaseTreeModel[] children) {
		this(map);
		for (int i = 0; i < children.length; i++) {
			add(children[i]);
		}
	}
	public String getValue(String key){
		return (String) get(key);
	}

	public String getName() {
		return (String) get(GridElementLabel.NAME);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int compareTo(TreeFolder o) {
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
		TreeFolder other = (TreeFolder) obj;
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
