package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class ComparisonSuppl implements Serializable {

	private static final long serialVersionUID = -8415246813381782224L;

	private ComparisonType type;
	private Bin binClassification;
	private int id;

	public ComparisonType getType() {
		return type;
	}

	public void setType(ComparisonType type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Bin getBin() {
		return binClassification;
	}

	public void setBin(Bin binClassification) {
		this.binClassification = binClassification;
	}

	@Override
	public String toString() {
		return "ComparisonSuppl [type=" + type + ", id=" + id + "bin="
				+ binClassification.getNum() + "]";
	}

}
