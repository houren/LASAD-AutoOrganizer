package lasad.shared.dfki.meta.ontology.descr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasad.shared.dfki.meta.ontology.base.BaseType;
import lasad.shared.dfki.meta.ontology.base.RatingType;
import lasad.shared.dfki.meta.ontology.base.TextType;


/**
 * 
 * Description of a nonstandard element (node / link) property (i.e., a property
 * defined for one specific element). The number of instances of a nonstandard
 * property can vary ({@link #minCardinality} and {@link #maxCardinality}). If (
 * {@link #maxCardinality} > 1) the property (and its components) are of
 * {@link ComparisonDataType#SET}. Nonstandard property have a {@link BaseType},
 * which corresponds to the specific UI widget the property is based upon (e.g.,
 * there are {@link BaseType}s for text fields [ {@link TextType}] and rating
 * elements [ {@link RatingType}]). The {@link BaseType} comprises all the
 * attributes that are solely based on the corresponding UI widget.
 * 
 * <br/>
 * <br/>
 * (see {@link PropDescr}, {@link BaseType})
 * 
 * @author oliverscheuer
 * 
 */
public class NonStandardPropDescr extends PropDescr {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3206481553305220101L;

	public static final int UNRESTRICTED = -1;

	protected BaseType baseType;

	protected int minCardinality;
	protected int maxCardinality;

	protected boolean codedValues = false;
	Map<String, String> code2value = new HashMap<String, String>();
	String defaultValue = null;

	public NonStandardPropDescr(){
		
	}
	
	public NonStandardPropDescr(BaseType baseT, String propID, int minC,
			int maxC) {
		super(propID, baseT.getJessTemplateName());
		this.baseType = baseT;
		this.minCardinality = minC;
		this.maxCardinality = maxC;
	}

	public void setCodedValues(Map<String, String> code2value) {
		this.code2value = code2value;
		codedValues = true;
	}
	
	public String getValue(String code) {
		return code2value.get(code);
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean hasOnlySingleComponent() {
		return baseType.hasOnlySingleComponent();
	}

	public String getJessTemplate() {
		return baseType.getJessTemplateName();
	}

	public List<String> getComponentIDs() {
		return baseType.getComponentIDs();
	}

	public String getJessSlot(String componentID) {
		return baseType.getJessSlot(componentID);
	}

	public JessDataType getSlotDataType(String componentID) {
		return baseType.getSlotDataType(componentID);
	}

	public ComparisonGroup getComparisonGroup(String componentID) {
		return baseType.getComparisonGroup(componentID);
	}

	public ComparisonDataType getComparisonDataType(String componentID) {
		JessDataType jessDataType = getSlotDataType(componentID);
		if (maxCardinality > 1 || JessDataType.LIST.equals(jessDataType)) {
			return ComparisonDataType.SET;
		} else if (JessDataType.STRING.equals(jessDataType)) {
			return ComparisonDataType.STRING;
		} else if (JessDataType.NUMBER.equals(jessDataType)) {
			return ComparisonDataType.NUMBER;
		}
		System.err.println("Unhandled case in method 'getComparisonDataType'");
		return null;
	}

	public boolean valuesCoded() {
		return codedValues;
	}

	public Map<String, String> getCode2value() {
		return code2value;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public int getMinCardinality() {
		return minCardinality;
	}

	public void setMinCardinality(int minCardinality) {
		this.minCardinality = minCardinality;
	}

	public int getMaxCardinality() {
		return maxCardinality;
	}

	public void setMaxCardinality(int maxCardinality) {
		this.maxCardinality = maxCardinality;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((baseType == null) ? 0 : baseType.hashCode());
		result = prime * result
				+ ((code2value == null) ? 0 : code2value.hashCode());
		result = prime * result + (codedValues ? 1231 : 1237);
		result = prime * result
				+ ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + maxCardinality;
		result = prime * result + minCardinality;
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
		NonStandardPropDescr other = (NonStandardPropDescr) obj;
		if (baseType == null) {
			if (other.baseType != null)
				return false;
		} else if (!baseType.equals(other.baseType))
			return false;
		if (code2value == null) {
			if (other.code2value != null)
				return false;
		} else if (!code2value.equals(other.code2value))
			return false;
		if (codedValues != other.codedValues)
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (maxCardinality != other.maxCardinality)
			return false;
		if (minCardinality != other.minCardinality)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NonStandardPropDescr [baseType=" + baseType
				+ ", minCardinality=" + minCardinality + ", maxCardinality="
				+ maxCardinality + ", codedValues=" + codedValues
				+ ", code2value=" + code2value + ", defaultValue="
				+ defaultValue + ", propID=" + propID + "]";
	}

}
