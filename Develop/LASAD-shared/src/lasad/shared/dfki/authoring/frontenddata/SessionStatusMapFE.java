package lasad.shared.dfki.authoring.frontenddata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lasad.shared.dfki.meta.ServiceStatus;


/**
 * runtime status of sessions and agents (to be used in the graphical frontend).
 * 
 * @author oliverscheuer
 * 
 */
public class SessionStatusMapFE implements Serializable, ObjectFE {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5021658016723349365L;
	private Map<String, ServiceStatus> sessionID2Status = new HashMap<String, ServiceStatus>();
	private Map<String, Map<String, ServiceStatus>> sessionID2agent2Status = new HashMap<String, Map<String, ServiceStatus>>();
	
	public SessionStatusMapFE(){
		
	}

	public Map<String, ServiceStatus> getSessionID2Status() {
		return sessionID2Status;
	}

	public void setSessionID2Status(Map<String, ServiceStatus> sessionID2Status) {
		this.sessionID2Status = sessionID2Status;
	}

	public void addSessionStatus(String sessionID, ServiceStatus status) {
		sessionID2Status.put(sessionID, status);
	}

	public void addAgentStatus(String sessionID, String agentID,
			ServiceStatus status) {
		Map<String, ServiceStatus> agent2Status = sessionID2agent2Status
				.get(sessionID);
		if (agent2Status == null) {
			agent2Status = new HashMap<String, ServiceStatus>();
			sessionID2agent2Status.put(sessionID, agent2Status);
		}
		agent2Status.put(agentID, status);
	}

	public Map<String, Map<String, ServiceStatus>> getSessionID2agent2Status() {
		return sessionID2agent2Status;
	}

	public void setSessionID2agent2Status(
			Map<String, Map<String, ServiceStatus>> sessionID2agent2Status) {
		this.sessionID2agent2Status = sessionID2agent2Status;
	}

	@Override
	public String toString() {
		return "SessionStatusMap [sessionID2Status=" + sessionID2Status
				+ ", sessionID2agent2Status=" + sessionID2agent2Status + "]";
	}

}
