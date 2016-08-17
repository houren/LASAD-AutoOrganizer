package lasad.shared.dfki.meta.agents.analysis.structure.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Representation of a graphical pattern described in terms of
 * {@link NodeVariable}s and {@link LinkVariable}s and constraints imposed on
 * them
 * 
 * <br/>
 * <br/>
 * (see also {@link NodeConstr} and {@link LinkConstr}).
 * 
 * @author Almer Bolatov, Oliver Scheuer
 * 
 */
public class StructuralPattern implements Serializable {

	private static final long serialVersionUID = -2769084252506052043L;

	private String ID;
	private String subID = null;

	private List<NodeVariable> nodeVars = new Vector<NodeVariable>();
	private List<LinkVariable> linkVars = new Vector<LinkVariable>();

	private Set<StructuralPattern> notPatterns = new HashSet<StructuralPattern>();

	private StructuralPatternSuppl supplData = null;

	/**
	 * Only for RMI serialization. DO NOT USE THIS CONSTRUCTOR!
	 */
	/*
	private StructuralPattern() {
	}
	*/

	public StructuralPattern(String ID) {
		this.ID = ID;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getSubID() {
		return subID;
	}

	private void setSubID(String subID) {
		this.subID = subID;
	}

	public List<NodeVariable> getNodeVars() {
		return nodeVars;
	}

	public void setNodeVars(List<NodeVariable> nodeVars) {
		this.nodeVars = nodeVars;
	}

	public void addNodeVar(NodeVariable nodeVar) {
		nodeVars.add(nodeVar);
	}

	public void removeNodeVar(NodeVariable nodeVar) {
		nodeVars.remove(nodeVar);
	}

	public List<LinkVariable> getLinkVars() {
		return linkVars;
	}

	public void setLinkVars(List<LinkVariable> linkVars) {
		this.linkVars = linkVars;
	}

	public void addLinkVar(LinkVariable nodeVar) {
		linkVars.add(nodeVar);
	}

	public void removeLinkVar(LinkVariable linkVar) {
		linkVars.remove(linkVar);
	}

	public Collection<StructuralPattern> getNotPatterns() {
		return notPatterns;
	}

	public void setNotPatterns(Set<StructuralPattern> notPatterns) {
		this.notPatterns = notPatterns;
	}

	public void addNotSubPattern(StructuralPattern notPattern) {
		if (notPatterns.contains(notPattern)) {
			return;
		}
		notPatterns.add(notPattern);
	}

	public void removeNotSubPattern(StructuralPattern notPattern) {
		notPatterns.remove(notPattern);
	}

	/**
	 * 
	 * @return List of {@link NodeConstr}s with lower sequence number than
	 *         reference {@link NodeConstr}
	 */
	public List<NodeVariable> getNodePredecessors(NodeVariable queryNode) {
		List<NodeVariable> result = new Vector<NodeVariable>();
		String varID = queryNode.getVarID();
		for (NodeVariable otherNode : nodeVars) {
			String otherVarID = otherNode.getVarID();
			if (varID.equals(otherVarID)) {
				return result;
			}
			result.add(otherNode);
		}
		System.err.println("Error in method 'getNodePredecessors(...)': "
				+ "NodeConstr not found in Pattern.");
		return null;
	}

	/**
	 * 
	 * @return List of {@link LinkConstr}s with lower sequence number than
	 *         reference {@link LinkConstr}
	 */

	public List<LinkVariable> getLinkPredecessors(LinkVariable queryLink) {
		List<LinkVariable> result = new Vector<LinkVariable>();
		String varID = queryLink.getVarID();
		for (LinkVariable otherLink : linkVars) {
			String otherVarID = otherLink.getVarID();
			if (varID.equals(otherVarID)) {
				return result;
			}
			result.add(otherLink);
		}
		System.err.println("Error in method 'getLinkPredecessors(...)': "
				+ "LinkConstr not found in Pattern.");
		return null;
	}

	public StructuralPatternSuppl getSupplData() {
		return supplData;
	}

	public void setSupplData(StructuralPatternSuppl supplData) {
		this.supplData = supplData;
	}

	public boolean isEmpty() {
		return nodeVars.isEmpty() && linkVars.isEmpty()
				&& notPatterns.isEmpty();
	}

	private void println(Object obj) {
		System.out.println(obj.toString());
		// logger.in
	}

	/**
	 * Prints pattern object
	 */
	public void printPattern() {
		StructuralPattern pattern = this;

		println("Pattern.id = " + pattern.getID());

		/**
		 * Print NODE constraints of this pattern
		 */
		println("NODE constrs:");
		String ind;
		for (NodeVariable nodeVar : pattern.getNodeVars()) {
			ind = "\t";

			println(ind + "ElemVarID: " + nodeVar.getVarID());

			println(ind + "PropConstr:");

			for (PropConstr pc : nodeVar.getConstrs().getPropConstrs()) {
				ind = "\t\t";

				ElementVariableProp propVar = pc.getPropVar();
				println(ind + "propID: " + propVar.getPropId());
				println(ind + "compID: " + propVar.getCompID());

				for (Comparison c : pc.getComparisons()) {
					ind = "\t\t\t";
					println(ind + "operator: " + c.getOperator().toString());

					if (c instanceof String2ConstStringComparison) {
						String2ConstStringComparison comp = (String2ConstStringComparison) c;
						println(ind + "value: \"" + comp.getRightExpr() + "\"");
					}
				}
			}
			println("");
		}

		/**
		 * Print LINK constraints of this pattern
		 */
		println("LINK constrs:");
		for (LinkVariable linkVar : pattern.getLinkVars()) {
			ind = "\t";

			String sourceVarID = linkVar.getConstrs().getSource().getVarID();
			String targetVarID = linkVar.getConstrs().getTarget().getVarID();
			boolean directionMatters = linkVar.getConstrs()
					.getDirectionMatters();
			println(ind + "Source: " + sourceVarID);
			println(ind + "Target: " + targetVarID);
			println(ind + "directionMatters: " + directionMatters);

			println(ind + "ElemVarID: " + linkVar.getVarID());

			println(ind + "PropConstr:");

			for (PropConstr pc : linkVar.getConstrs().getPropConstrs()) {
				ind = "\t\t";

				ElementVariableProp propVar = pc.getPropVar();
				println(ind + "propID: " + propVar.getPropId());
				println(ind + "compID: " + propVar.getCompID());

				for (Comparison c : pc.getComparisons()) {
					ind = "\t\t\t";
					println(ind + "operator: " + c.getOperator().toString());

					if (c instanceof String2ConstStringComparison) {
						String2ConstStringComparison comp = (String2ConstStringComparison) c;
						println(ind + "value: \"" + comp.getRightExpr() + "\"");
					} else if (c instanceof String2ConstSetComparison) {
						String2ConstSetComparison comp = (String2ConstSetComparison) c;
						for (String s : comp.getRightExpr()) {
							println(ind + "value: \"" + s + "\"");
						}
					}
				}
			}

			println("");
		}
	}

	public StructuralPattern createFreshSubPattern() {
		StructuralPattern subPattern = new StructuralPattern(ID);
		subPattern.setSubID(getFreshSubID());
		return subPattern;
	}

	private int currentCount = 0;

	private String getFreshSubID() {
		return "" + ++currentCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((subID == null) ? 0 : subID.hashCode());
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
		StructuralPattern other = (StructuralPattern) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (subID == null) {
			if (other.subID != null)
				return false;
		} else if (!subID.equals(other.subID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StructuralPattern [ID=" + ID + ", subID=" + subID
				+ ", nodeVars=" + nodeVars + ", linkVars=" + linkVars
				+ ", notPatterns=" + notPatterns + ", supplData=" + supplData
				+ ", currentCount=" + currentCount + "]";
	}

	public String toPrettyString(String prefix) {
		StringBuffer buf = new StringBuffer();
		String prefixL1 = prefix + "...";
		//String prefixL2 = prefixL1 + "...";

		String subPatternIDSuffix = subID == null ? "" : "/" + subID;
		buf.append(prefix + "[patternID] " + ID + subPatternIDSuffix + "\n");

		// buf.append(prefixL1 + "nodeVars" + "\n");
		for (NodeVariable nodeVar : nodeVars) {
			buf.append(nodeVar.toPrettyString(prefixL1));
		}

		// buf.append(prefixL1 + "linkVars" + "\n");
		for (LinkVariable linkVar : linkVars) {
			buf.append(linkVar.toPrettyString(prefixL1));
		}

		// buf.append(prefixL1 + "notPatterns" + "\n");
		for (StructuralPattern notPattern : notPatterns) {
			buf.append(notPattern.toPrettyString(prefixL1));
		}

		return buf.toString();
	}
}
