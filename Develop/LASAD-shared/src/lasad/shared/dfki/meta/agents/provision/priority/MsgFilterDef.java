package lasad.shared.dfki.meta.agents.provision.priority;

import java.io.Serializable;

/**
 * 
 * @author oliverscheuer
 * 
 */
public abstract class MsgFilterDef implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3681291596228409324L;

	public MsgFilterDef(){
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MsgFilterDef))
			return false;
		return true;
	}
}
