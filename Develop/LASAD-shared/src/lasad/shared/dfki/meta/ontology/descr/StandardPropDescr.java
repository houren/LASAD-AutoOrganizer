package lasad.shared.dfki.meta.ontology.descr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Description of a standard property (i.e., a property, which all elements have
 * in common).
 * <br/><br/>
 * (see {@link PropDescr})
 * 
 * @author oliverscheuer
 * 
 */
public abstract class StandardPropDescr extends PropDescr implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5885412275659064121L;
	protected List<String> componentIDs = new Vector<String>();
	protected Map<String, String> componentIDJessSlotName = new HashMap<String, String>();
	protected Map<String, JessDataType> componentIDJessDataType = new HashMap<String, JessDataType>();
	protected Map<String, ComparisonGroup> componentIDCompGroup = new HashMap<String, ComparisonGroup>();

	public StandardPropDescr(){
		
	}
	public StandardPropDescr(String propID, String jessTemplate, String jessSlot,
			JessDataType jessDataType, ComparisonGroup compGroup) {
		super(propID, jessTemplate);
		componentIDs.add(DEFAULT_COMPONENT_ID);
		componentIDJessSlotName.put(DEFAULT_COMPONENT_ID, jessSlot);
		componentIDJessDataType.put(DEFAULT_COMPONENT_ID, jessDataType);
		componentIDCompGroup.put(DEFAULT_COMPONENT_ID, compGroup);
	}

	public List<String> getComponentIDs() {
		return componentIDs;
	}

	public String getJessSlot(String componentID) {
		return componentIDJessSlotName.get(componentID);
	}

	public JessDataType getSlotDataType(String componentID) {
		return componentIDJessDataType.get(componentID);
	}

	public ComparisonGroup getComparisonGroup(String componentID) {
		return componentIDCompGroup.get(componentID);
	}

	public ComparisonDataType getComparisonDataType(String componentID) {
		JessDataType jessDataType = getSlotDataType(componentID);
		if (JessDataType.LIST.equals(jessDataType)) {
			return ComparisonDataType.SET;
		} else if (JessDataType.STRING.equals(jessDataType)) {
			return ComparisonDataType.STRING;
		} else if (JessDataType.NUMBER.equals(jessDataType)) {
			return ComparisonDataType.NUMBER;
		}
		System.err.println("Unhandled case in method 'getComparisonDataType'");
		return null;
	}

	public boolean hasOnlySingleComponent() {
		return true;
	}

	@Override
	public String toString() {
		return "StandardPropDescr [componentIDs=" + componentIDs
				+ ", componentIDJessSlotName=" + componentIDJessSlotName
				+ ", componentIDJessDataType=" + componentIDJessDataType
				+ ", componentIDCompGroup=" + componentIDCompGroup
				+ ", propID=" + propID + "]";
	}

}
