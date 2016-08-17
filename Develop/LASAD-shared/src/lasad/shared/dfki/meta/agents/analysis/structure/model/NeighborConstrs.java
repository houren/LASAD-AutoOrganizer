package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Specifies that some {@link NodeVariable} must have an out-link to some other
 * {@link NodeVariable} ({@link #outNeighbors}), an in-link from some other
 * {@link NodeVariable} ({@link #inNeighbors}), or be connected in any way with
 * some other {@link NodeVariable} ({@link #neighbors}).
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class NeighborConstrs implements Serializable {

	private static final long serialVersionUID = 4662034690405252421L;

	private List<LinkVariable> directedLinkConstraintsToAdd = new Vector<LinkVariable>();
	private List<LinkVariable> undirectedLinkConstraintsToAdd = new Vector<LinkVariable>();

	public List<LinkVariable> getDirectedLinkConstraintsToAdd() {
		return directedLinkConstraintsToAdd;
	}

	public void setDirectedLinkConstraintsToAdd(
			List<LinkVariable> directedLinkConstraintsToAdd) {
		this.directedLinkConstraintsToAdd = directedLinkConstraintsToAdd;
	}

	public List<LinkVariable> getUndirectedLinkConstraintsToAdd() {
		return undirectedLinkConstraintsToAdd;
	}

	public void setUndirectedLinkConstraintsToAdd(
			List<LinkVariable> undirectedLinkConstraintsToAdd) {
		this.undirectedLinkConstraintsToAdd = undirectedLinkConstraintsToAdd;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("dir-link-constrs={");
		Iterator<LinkVariable> linkIter = directedLinkConstraintsToAdd
				.iterator();
		while (linkIter.hasNext()) {
			LinkVariable linkVar = linkIter.next();
			buf.append("[" + linkVar.getVarID() + ", "
					+ linkVar.getSource().getVarID() + "->"
					+ linkVar.getTarget().getVarID() + "]");
			if (linkIter.hasNext()) {
				buf.append(", ");
			}
		}
		buf.append("}, ");
		buf.append("undir-link-constrs={");
		linkIter = undirectedLinkConstraintsToAdd.iterator();
		while (linkIter.hasNext()) {
			LinkVariable linkVar = linkIter.next();
			buf.append("[" + linkVar.getVarID() + ", "
					+ linkVar.getSource().getVarID() + "->"
					+ linkVar.getTarget().getVarID() + "]");
			if (linkIter.hasNext()) {
				buf.append(", ");
			}
		}
		buf.append("}");
		return buf.toString();

	}

}
