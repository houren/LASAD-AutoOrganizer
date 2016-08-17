package lasad.shared.dfki.meta.agents.analysis.structure.model;

/**
 * Comparison of a string variable to some set variable.
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class String2VarSetComparison extends VariableComparison implements
		Something2SetComparison {

	private static final long serialVersionUID = -756278019189491425L;

	private String2SetOperator operator;
	private ElementVariableProp rightExpr;

	public String2SetOperator getOperator() {
		return operator;
	}

	public void setOperator(String2SetOperator operator) {
		this.operator = operator;
	}

	public ElementVariableProp getRightExpr() {
		return rightExpr;
	}

	public void setRightExpr(ElementVariableProp rightExpr) {
		this.rightExpr = rightExpr;
	}

	protected VariableComparison getInverseBasic() {
		Set2VarStringComparison inverted = new Set2VarStringComparison();
		inverted.setOperator(getOperator().invert());
		return inverted;
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
		String2VarSetComparison other = (String2VarSetComparison) obj;
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
		return "String2VarSetComparison [operator=" + operator + ", rightExpr="
				+ rightExpr + "], " + super.toString();
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		buf.append(prefix + "[comp] " + operator.getName() + " ["
				+ rightExpr.toCompactString() + "]\n");
		return buf.toString();
	}
}
