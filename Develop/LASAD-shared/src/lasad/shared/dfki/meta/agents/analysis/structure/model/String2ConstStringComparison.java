package lasad.shared.dfki.meta.agents.analysis.structure.model;

/**
 * Comparison of a string variable to some constant string value.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class String2ConstStringComparison extends Comparison {

	private static final long serialVersionUID = 3681122809788007968L;

	private String2StringOperator operator;
	private String rightExpr;

	public String2StringOperator getOperator() {
		return operator;
	}

	public void setOperator(String2StringOperator operator) {
		this.operator = operator;
	}

	public String getRightExpr() {
		return rightExpr;
	}

	public void setRightExpr(String rightExpr) {
		this.rightExpr = rightExpr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result
				+ ((rightExpr == null) ? 0 : rightExpr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		String2ConstStringComparison other = (String2ConstStringComparison) obj;
		if (operator != other.operator)
			return false;
		if (rightExpr == null) {
			if (other.rightExpr != null)
				return false;
		} else if (!rightExpr.equals(other.rightExpr))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "String2ConstStringComparison [operator=" + operator
				+ ", rightExpr=" + rightExpr + "], " + super.toString();
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		buf.append(prefix + "[comp] " + operator.getName() + " \"" + rightExpr
				+ "\"\n");
		return buf.toString();
	}
}
