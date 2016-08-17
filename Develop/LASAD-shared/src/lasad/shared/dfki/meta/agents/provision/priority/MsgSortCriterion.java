package lasad.shared.dfki.meta.agents.provision.priority;

import java.io.Serializable;

/**
 * 
 * @author oliverscheuer
 *
 */
public class MsgSortCriterion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5508135863380077912L;

	public MsgSortCriterion(){
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
}
