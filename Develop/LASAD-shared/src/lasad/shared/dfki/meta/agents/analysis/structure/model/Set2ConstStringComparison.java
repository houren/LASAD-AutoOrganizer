package lasad.shared.dfki.meta.agents.analysis.structure.model;

/**
 * Comparison of a set variable to some constant string value.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class Set2ConstStringComparison extends Comparison implements
		Set2SomethingComparison {

	private static final long serialVersionUID = 3262459295324420018L;

	private Set2StringOperator operator;
	private String rightExpr;

	public Set2StringOperator getOperator() {
		return operator;
	}

	public void setOperator(Set2StringOperator operator) {
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
		Set2ConstStringComparison other = (Set2ConstStringComparison) obj;
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
		return "Set2ConstStringComparison [operator=" + operator
				+ ", rightExpr=" + rightExpr + "], " + super.toString();
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		buf.append(prefix + "[comp]" + operator.getName() + " \"" + rightExpr + "\"\n");
		return buf.toString();
	}
}
