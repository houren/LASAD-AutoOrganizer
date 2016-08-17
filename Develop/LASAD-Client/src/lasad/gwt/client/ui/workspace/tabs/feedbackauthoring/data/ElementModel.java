package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.CustomizedGrid;

import com.extjs.gxt.ui.client.data.BaseModel;

public class ElementModel extends BaseModel implements Serializable, Comparable<ElementModel> {
	
	/**
	 * This class represents an element displayed in {@link CustomizedGrid}
	 */
	private static final long serialVersionUID = 3846793922195348810L;

	public ElementModel(){
		
	}
	public ElementModel(Map<String, String> map) {
		this();
		Set<String> keys = map.keySet();
		for(String key:keys){
			String value = map.get(key);
			set(key, value);
		}
	}
	
	public String getName() {
		return (String) get(GridElementLabel.NAME);
	}
	public String getValue(String key){
		return (String) get(key);
	}
	
	@Override
	public int compareTo(ElementModel o) {
		if (this.getValue(GridElementLabel.ID) != null && o.getValue(GridElementLabel.ID) != null){
			return this.getValue(GridElementLabel.ID).compareTo(o.getValue(GridElementLabel.ID));
		}else{
			return this.getValue(GridElementLabel.NAME).compareTo(o.getValue(GridElementLabel.NAME));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementModel other = (ElementModel) obj;
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
