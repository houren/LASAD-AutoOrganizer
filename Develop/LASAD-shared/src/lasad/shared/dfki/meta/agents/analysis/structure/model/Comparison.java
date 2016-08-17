package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;

/**
 * Represents a comparison of a variable and some RHS expression.
 * {@link Comparison}s are used to constrain the set of possible property values
 * of some {@link ElementVariable}.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public abstract class Comparison implements Serializable {

	private static final long serialVersionUID = -3872599820194614823L;

	private String debugInfo = null;

	private ElementVariableProp leftExpr;

	private ComparisonSuppl supplData = null;

	public ElementVariableProp getLeftExpr() {
		return leftExpr;
	}

	public void setLeftExpr(ElementVariableProp leftExpr) {
		this.leftExpr = leftExpr;
	}

	public ComparisonSuppl getSupplData() {
		return supplData;
	}

	public void setSupplData(ComparisonSuppl supplData) {
		this.supplData = supplData;
	}

	public abstract Operator getOperator();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((leftExpr == null) ? 0 : leftExpr.hashCode());
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
		Comparison other = (Comparison) obj;
		if (leftExpr == null) {
			if (other.leftExpr != null)
				return false;
		} else if (!leftExpr.equals(other.leftExpr))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Comparison [leftExpr=" + leftExpr + ", supplData=" + supplData
				+ "]";
	}

	public String getDebugInfo() {
		return debugInfo;
	}

	public void setDebugInfo(String debugInfo) {
		this.debugInfo = debugInfo;
	}

	public abstract String toPrettyString(String prefix);
}
