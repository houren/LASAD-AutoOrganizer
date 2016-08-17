package lasad.shared.dfki.meta.agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.shared.dfki.meta.agents.analysis.structure.StructuralAnalysisTypeManipulator;
import lasad.shared.dfki.meta.agents.analysis.structure.StructureAnalysisType;
import lasad.shared.dfki.meta.ontology.Ontology;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class ActionAgentConfigDataManipulator {

	private Ontology ontology = null;
	private ActionAgentConfigData agentConfig;

	private Map<ServiceID, StructuralAnalysisTypeManipulator> id2analysisManipulator = new HashMap<ServiceID, StructuralAnalysisTypeManipulator>();

	// ...

	public StructuralAnalysisTypeManipulator getStructuralAnalysisManipulator(
			ServiceID id) {
		return id2analysisManipulator.get(id2analysisManipulator);
	}

	public ServiceID addFreshStructuralAnalysis() {

		// TODO: Request fresh service ID from backend
		ServiceID id = null;
		StructureAnalysisType aType = new StructureAnalysisType(id);
		agentConfig.addAnalysisType(aType);
		StructuralAnalysisTypeManipulator aTypeManipulator = new StructuralAnalysisTypeManipulator(
				aType, ontology);
		id2analysisManipulator.put(id, aTypeManipulator);
		return id;
	}

	public List<ServiceType> getServiceTypesAffectedByAnalysisTypeRemoval(
			ServiceID typeToRemove) {
		List<ServiceType> affectedTypes = new Vector<ServiceType>();
		// TODO: implement
		return affectedTypes;
	}

	public void removeStructuralAnalysis(ServiceID id) {
		agentConfig.removeAnalysisType(id);
		id2analysisManipulator.remove(id);
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
		List<String> supportedOntologies = new Vector<String>();
		supportedOntologies.add(ontology.getOntologyID());
		agentConfig.setSupportedOntologiesDef(new SupportedOntologiesDef(
				supportedOntologies));
		// TODO: Propagate ontology to all service type manipulators
	}

	public List<ServiceType> getServiceTypesAffectedByOntologyChange(
			Ontology replacementOntology) {
		List<ServiceType> affectedTypes = new Vector<ServiceType>();
		// TODO: implement
		return affectedTypes;
	}

}
