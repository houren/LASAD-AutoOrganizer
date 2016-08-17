package lasad.shared.dfki.meta.agents.analysis.counter;


/**
 * 
 * (see {@link InstanceTypeSpecific})
 * 
 * @author oliverscheuer
 * 
 */
public class InstanceTypeSpecific_Object extends InstanceTypeSpecific{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8539658897824375115L;
	private String typeID;

	public InstanceTypeSpecific_Object(){
		
	}
	public InstanceTypeSpecific_Object(String typeID) {
		this.typeID = typeID;
	}

	public String getTypeID() {
		return typeID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((typeID == null) ? 0 : typeID.hashCode());
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
		InstanceTypeSpecific_Object other = (InstanceTypeSpecific_Object) obj;
		if (typeID == null) {
			if (other.typeID != null)
				return false;
		} else if (!typeID.equals(other.typeID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[typeID=" + typeID + "]";
	}

}
