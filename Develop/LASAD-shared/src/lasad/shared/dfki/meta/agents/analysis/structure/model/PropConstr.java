package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * All constraints (see {@link Comparison}s) that apply to a some
 * {@link ElementVariableProp}.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class PropConstr implements Serializable {

	private static final long serialVersionUID = 4669796154901560746L;

	private ElementVariableProp propVar;
	private List<Comparison> comparisons = new Vector<Comparison>();

	public ElementVariableProp getPropVar() {
		return propVar;
	}

	public void setPropVar(ElementVariableProp propVar) {
		this.propVar = propVar;
	}

	public List<Comparison> getComparisons() {
		return comparisons;
	}

	public void setComparisons(List<Comparison> comparisons) {
		this.comparisons = comparisons;
	}

	public boolean removeComparisonIfExists(Comparison comparison) {
		for (int i = 0; i < comparisons.size(); i++) {
			Comparison c = comparisons.get(i);
			if (c.equals(comparison)) {
				comparisons.remove(i);
				return true;
			}
		}
		return false;
	}

	public boolean addComparisonIfNotExists(Comparison comparison) {
		for (int i = 0; i < comparisons.size(); i++) {
			Comparison c = comparisons.get(i);
			if (c.equals(comparison)) {
				return false;
			}
		}
		comparisons.add(comparison);
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((comparisons == null) ? 0 : comparisons.hashCode());
		result = prime * result + ((propVar == null) ? 0 : propVar.hashCode());
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
		PropConstr other = (PropConstr) obj;
		if (comparisons == null) {
			if (other.comparisons != null)
				return false;
		} else if (!comparisons.equals(other.comparisons))
			return false;
		if (propVar == null) {
			if (other.propVar != null)
				return false;
		} else if (!propVar.equals(other.propVar))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PropConstr [propVar=" + propVar + ", comparisons="
				+ comparisons + "]";
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		String prefixL1 = prefix + "...";

		buf.append(prefix + "[prop] " + propVar.toCompactString() + "\n");
		List<Comparison> comparisons = getComparisons();
		// buf.append(prefixL1 + "comparisons" + "\n");
		for (Comparison comparison : comparisons) {
			buf.append(comparison.toPrettyString(prefixL1));
		}
		return buf.toString();
	}
}
