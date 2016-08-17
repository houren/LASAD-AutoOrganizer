package lasad.shared.dfki.meta.ontology;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import lasad.shared.dfki.meta.ontology.descr.CreatorPropDescr;
import lasad.shared.dfki.meta.ontology.descr.FirstTsPropDescr;
import lasad.shared.dfki.meta.ontology.descr.LastTsPropDescr;
import lasad.shared.dfki.meta.ontology.descr.LinkDescr;
import lasad.shared.dfki.meta.ontology.descr.ModifiersPropDescr;
import lasad.shared.dfki.meta.ontology.descr.NodeDescr;
import lasad.shared.dfki.meta.ontology.descr.NonStandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.StandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.TypePropDescr;

/**
 * Description of objects (nodes, links) that can be created in a session in the
 * EUE.
 * 
 * @author Oliver Scheuer
 * 
 */
public class Ontology implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2399729616453935594L;

	private String ontologyID;

	/*
	 * Key - elemTypeID
	 */
	private HashMap<String, NodeDescr> nodeDescriptions = new HashMap<String, NodeDescr>();
	private HashMap<String, LinkDescr> linkDescriptions = new HashMap<String, LinkDescr>();

	private static List<StandardPropDescr> standardElemProps = new Vector<StandardPropDescr>();
	static {
		standardElemProps.add(new TypePropDescr());
		standardElemProps.add(new FirstTsPropDescr());
		standardElemProps.add(new LastTsPropDescr());
		standardElemProps.add(new CreatorPropDescr());
		standardElemProps.add(new ModifiersPropDescr());
	}

	public Ontology(){
		
	}
	
	public static List<StandardPropDescr> getStandardElemProperties() {
		return standardElemProps;
	}

	public Ontology(String ontologyID) {
		this.ontologyID = ontologyID;
	}

	public void addNodeDescription(NodeDescr nodeDescr) {
		nodeDescriptions.put(nodeDescr.getTypeID(), nodeDescr);
	}

	public void addLinkDescription(LinkDescr linkDescr) {
		linkDescriptions.put(linkDescr.getTypeID(), linkDescr);
	}

	public String getOntologyID() {
		return ontologyID;
	}

	public Set<String> getLinkTypes() {
		return linkDescriptions.keySet();
	}

	public Set<String> getNodeTypes() {
		return nodeDescriptions.keySet();
	}

	public HashMap<String, NodeDescr> getNodeDescriptions() {
		return nodeDescriptions;
	}

	public HashMap<String, LinkDescr> getLinkDescriptions() {
		return linkDescriptions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((linkDescriptions == null) ? 0 : linkDescriptions.hashCode());
		result = prime
				* result
				+ ((nodeDescriptions == null) ? 0 : nodeDescriptions.hashCode());
		result = prime * result
				+ ((ontologyID == null) ? 0 : ontologyID.hashCode());
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
		Ontology other = (Ontology) obj;
		if (linkDescriptions == null) {
			if (other.linkDescriptions != null)
				return false;
		} else if (!linkDescriptions.equals(other.linkDescriptions))
			return false;
		if (nodeDescriptions == null) {
			if (other.nodeDescriptions != null)
				return false;
		} else if (!nodeDescriptions.equals(other.nodeDescriptions))
			return false;
		if (ontologyID == null) {
			if (other.ontologyID != null)
				return false;
		} else if (!ontologyID.equals(other.ontologyID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		// buf.append(getClass().getSimpleName()).append(
		// ": ontologyID=" + ontologyID + "\n"); // getSimpleName() is not
		// supported by GWT
		buf.append("Ontology: ontologyID=" + ontologyID + "\n");
		buf.append("NODE DESCRIPTIONS\n");
		for (Iterator<NodeDescr> nodeDescrIter = nodeDescriptions.values()
				.iterator(); nodeDescrIter.hasNext();) {
			buf.append(nodeDescrIter.next().toPrettyString());
		}
		buf.append("LINK DESCRIPTIONS\n");
		for (Iterator<LinkDescr> linkDescrIter = linkDescriptions.values()
				.iterator(); linkDescrIter.hasNext();) {
			buf.append(linkDescrIter.next().toPrettyString());
		}
		return buf.toString();
	}

	public String getNonStandardPropDisplayTextFromNode(String nodeId,
			String propertyId, String code) {
		NodeDescr node = nodeDescriptions.get(nodeId);
		if (node != null) {
			NonStandardPropDescr prop = node
					.getNonStandardPropDescrById(propertyId);
			if (prop != null) {
				return prop.getValue(code);
			}
		}
		return null;
	}
	
	public List<NonStandardPropDescr> getNonStandardElemProperties(String nodeId) {
		List<NonStandardPropDescr> retVal = new Vector<NonStandardPropDescr>();
		NodeDescr node = nodeDescriptions.get(nodeId);
		if (node != null) {
			retVal = node.getNonStandardProps();
		}
		return retVal;
	}

}
