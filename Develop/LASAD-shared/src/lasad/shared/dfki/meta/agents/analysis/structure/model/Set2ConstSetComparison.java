package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.util.List;

/**
 * Comparison of a set variable to another set variable.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class Set2ConstSetComparison extends Comparison implements
		Set2SomethingComparison, Something2SetComparison {

	private static final long serialVersionUID = -1029576813124464374L;

	private Set2SetOperator operator;
	private List<String> rightExpr;

	public Set2SetOperator getOperator() {
		return operator;
	}

	public void setOperator(Set2SetOperator operator) {
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
		Set2ConstSetComparison other = (Set2ConstSetComparison) obj;
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
		return "Set2ConstSetComparison [operator=" + operator + ", rightExpr="
				+ rightExpr + "], " + super.toString();
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		buf.append(prefix + "[comp] " + operator.getName() + " " + rightExpr + "\n");
		return buf.toString();
	}
}
