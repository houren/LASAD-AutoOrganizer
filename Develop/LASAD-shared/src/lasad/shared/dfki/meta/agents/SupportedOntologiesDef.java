package lasad.shared.dfki.meta.agents;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class SupportedOntologiesDef implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2116699025175237975L;
	private boolean allOntologies = false;
	private List<String> supportedOntologies = new Vector<String>();
	
	public SupportedOntologiesDef(){
		
	}

	public SupportedOntologiesDef(List<String> supportedOntologies) {
		this.supportedOntologies = supportedOntologies;
	}

	public SupportedOntologiesDef(boolean allOntologies) {
		this.allOntologies = allOntologies;
	}

	public boolean isAllOntologies() {
		return allOntologies;
	}

	public void setAllOntologies(boolean allOntologies) {
		this.allOntologies = allOntologies;
	}

	public List<String> getSupportedOntologies() {
		return supportedOntologies;
//		return null;
	}

	public void setSupportedOntologies(List<String> supportedOntologies) {
		this.supportedOntologies = supportedOntologies;
	}

}
