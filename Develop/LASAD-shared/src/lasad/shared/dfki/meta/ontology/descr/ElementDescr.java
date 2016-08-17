package lasad.shared.dfki.meta.ontology.descr;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lasad.shared.dfki.meta.ontology.Ontology;


/**
 * Specification of a given element in terms of its possible properties (see
 * {@link PropDescr}). Properties unfold into standard properties (which all
 * elements have in common, see {@link StandardPropDescr}) and nonstandard
 * properties (vary between elements, see see {@link NonStandardPropDescr})),
 * 
 * @author oliverscheuer
 * 
 */
public abstract class ElementDescr implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7385139683927049221L;

	String elemTypeID;

	// standard props are those that all elements have in common
	List<StandardPropDescr> standardProps = new LinkedList<StandardPropDescr>();

	// non-standard props are those that can differ depending on the ontology
	// definition
	List<NonStandardPropDescr> nonStandardProps = new LinkedList<NonStandardPropDescr>();

	/*
	 * Do not use this constructor, is only for RMI purposes.
	 */
	
	public ElementDescr() {
		
	}
	
	public ElementDescr(String elemTypeID) {

		this.elemTypeID = elemTypeID;

		for (StandardPropDescr standardProp : Ontology
				.getStandardElemProperties()) {
			standardProps.add(standardProp);
		}
	}

	public void addPropDescr(NonStandardPropDescr propDescr) {
		nonStandardProps.add(propDescr);
	}

	public String getTypeID() {
		return elemTypeID;
	}

	public List<StandardPropDescr> getStandardProps() {
		return standardProps;
	}

	public List<NonStandardPropDescr> getNonStandardProps() {
		return nonStandardProps;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elemTypeID == null) ? 0 : elemTypeID.hashCode());
		result = prime
				* result
				+ ((nonStandardProps == null) ? 0 : nonStandardProps.hashCode());
		result = prime * result
				+ ((standardProps == null) ? 0 : standardProps.hashCode());
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
		ElementDescr other = (ElementDescr) obj;
		if (elemTypeID == null) {
			if (other.elemTypeID != null)
				return false;
		} else if (!elemTypeID.equals(other.elemTypeID))
			return false;
		if (nonStandardProps == null) {
			if (other.nonStandardProps != null)
				return false;
		} else if (!nonStandardProps.equals(other.nonStandardProps))
			return false;
		if (standardProps == null) {
			if (other.standardProps != null)
				return false;
		} else if (!standardProps.equals(other.standardProps))
			return false;
		return true;
	}

	@Override
	public String toString() {
//		return getClass().getSimpleName() + "[standardProps=" + standardProps
//				+ ", nonStandardProps=" + nonStandardProps + "]";   //getSimpleName() is not supported by GWT!
		return "ElementDescr[standardProps=" + standardProps
				+ ", nonStandardProps=" + nonStandardProps + "]";
	}

	public String toPrettyString() {
		StringBuffer buf = new StringBuffer();
//		buf.append(getClass().getSimpleName() + ": " + elemTypeID + "\n");   //getSimpleName() is not supported by GWT!
		buf.append("ElementDescr: " + elemTypeID + "\n");		
		for (Iterator<StandardPropDescr> propDescrIter = standardProps
				.iterator(); propDescrIter.hasNext();) {
			buf.append("\t" + propDescrIter.next() + "\n");
		}
		for (Iterator<NonStandardPropDescr> propDescrIter = nonStandardProps
				.iterator(); propDescrIter.hasNext();) {
			buf.append("\t" + propDescrIter.next() + "\n");
		}

		return buf.toString();
	}
	

	public NonStandardPropDescr getNonStandardPropDescrById(String id){
		for (NonStandardPropDescr prop : nonStandardProps){
			if (prop.getPropID().equalsIgnoreCase(id)){
				return prop;
			}
		}
		return null;
	}

}
