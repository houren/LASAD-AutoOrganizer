package lasad.shared.dfki.meta.agents.analysis.structure.model;

/**
 * Comparison of a numeric variable to some constant numeric value.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class Num2ConstNumComparison extends Comparison {

	private static final long serialVersionUID = 2667998421570610789L;

	private Num2NumOperator operator;
	private int rightExpr;

	public Num2NumOperator getOperator() {
		return operator;
	}

	public void setOperator(Num2NumOperator operator) {
		this.operator = operator;
	}

	public int getRightExpr() {
		return rightExpr;
	}

	public void setRightExpr(int rightExpr) {
		this.rightExpr = rightExpr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + rightExpr;
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
		Num2ConstNumComparison other = (Num2ConstNumComparison) obj;
		if (operator != other.operator)
			return false;
		if (rightExpr != other.rightExpr)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Num2ConstNumComparison [operator=" + operator + ", rightExpr="
				+ rightExpr + "], " + super.toString();
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		buf.append(prefix + "[comp] " + operator.getName() + " " + rightExpr + "\n");
		return buf.toString();
	}
}
