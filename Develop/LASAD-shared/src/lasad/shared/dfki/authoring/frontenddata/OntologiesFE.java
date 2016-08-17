package lasad.shared.dfki.authoring.frontenddata;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * a list of ontologies (to be used in the graphical frontend).
 * 
 * @author anahuacvalero
 * 
 */
public class OntologiesFE implements Serializable, ObjectFE {

	/**
	 * 
	 */
	private static final long serialVersionUID = -170828096497587599L;
	Set<String> elementSet = new HashSet<String>();
	
	public OntologiesFE(){
		
	}
	
	public void add(String ontology) {
		if(!elementSet.contains(ontology)){
			elementSet.add(ontology);
		}
	}
	
	public List<String> getElements() {
		Iterator<String> iter = elementSet.iterator();
		List <String> elementList = new Vector<String>();
		while(iter.hasNext()){
			elementList.add(new String(iter.next()));
		}
		return elementList;
		
	}

	@Override
	public String toString() {
		return "OntologiesFE [elementSet=" + elementSet + "]";
	}

}
