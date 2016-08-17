package lasad.shared.dfki.meta.ontology.descr;

import java.io.Serializable;
import java.util.List;

/**
 * Description of an element property. Element property themselves unfold into
 * subcomponents ({@link #getComponentIDs()}). For each component the following
 * specifications are provided: Jess slot name, {@link JessDataType},
 * {@link ComparisonDataType}, and {@link ComparisonGroup}. <br/>
 * <br/>
 * (see also {@link JessDataType}, {@link ComparisonDataType},
 * {@link ComparisonGroup})
 * 
 * @author oliverscheuer
 * 
 */
public abstract class PropDescr implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 567893531268056154L;

	public static final String DEFAULT_COMPONENT_ID = "default";

	protected String propID;
	protected String jessTemplate = null;

	public PropDescr() {
	}
	
	public PropDescr(String propID, String jessTemplate) {
		this.propID = propID;
		this.jessTemplate = jessTemplate;
	}

	public String getPropID() {
		return propID;
	}

	public abstract String getJessSlot(String componentID);

	public abstract JessDataType getSlotDataType(String componentID);

	public abstract ComparisonGroup getComparisonGroup(String componentID);

	public abstract ComparisonDataType getComparisonDataType(String componentID);

	public abstract boolean hasOnlySingleComponent();

	public abstract List<String> getComponentIDs();

	public String getJessTemplate() {
		return this.jessTemplate;
	}

	public boolean isInJessElementFact() {
		return jessTemplate == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((propID == null) ? 0 : propID.hashCode());
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
		PropDescr other = (PropDescr) obj;
		if (propID == null) {
			if (other.propID != null)
				return false;
		} else if (!propID.equals(other.propID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PropDescr [propID=" + propID + "]";
	}

}
