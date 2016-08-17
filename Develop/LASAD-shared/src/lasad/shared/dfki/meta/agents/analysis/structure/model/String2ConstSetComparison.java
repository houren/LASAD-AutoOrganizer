package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.util.List;

/**
 * Comparison of a string variable to some constant value set.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class String2ConstSetComparison extends Comparison implements
		Something2SetComparison {

	private static final long serialVersionUID = -5482065800831615090L;

	private String2SetOperator operator;
	private List<String> rightExpr;

	public String2SetOperator getOperator() {
		return operator;
	}

	public void setOperator(String2SetOperator operator) {
		this.operator = operator;
	}

	public List<String> getRightExpr() {
		return rightExpr;
	}

	public void setRightExpr(List<String> rightExpr) {
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
		String2ConstSetComparison other = (String2ConstSetComparison) obj;
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
		return "String2ConstSetComparison [operator=" + operator
				+ ", rightExpr=" + rightExpr + "], " + super.toString();
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		buf.append(prefix + "[comp] " + operator.getName() + " " + rightExpr
				+ "\n");
		return buf.toString();
	}
}
