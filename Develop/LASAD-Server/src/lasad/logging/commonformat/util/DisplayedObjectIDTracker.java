package lasad.logging.commonformat.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Translates between the object IDs displayed in the GUI and 'technical' IDs
 * used for internal processing.
 * 
 * 
 */
public class DisplayedObjectIDTracker {

//	private static Log logger = LogFactory.getLog(DisplayedObjectIDTracker.class);

	private static Map<SessionIDEUEIDPair, String> realID2displayID = new HashMap<SessionIDEUEIDPair, String>();

	public static String getDisplayID(String sessionID, String eueID) {
		SessionIDEUEIDPair idPair = new SessionIDEUEIDPair(sessionID, eueID);
		String displayID = realID2displayID.get(idPair);
//		if (displayID == null) {
//			logger.warn("Cannot find displayID for EUE entity '" + eueID
//					+ "' in session '" + sessionID + "'.");
//		}
		return displayID;
	}

	public static void addMapping(String sessionID, String eueID, String displayID) {
//		logger.debug("Mapping added (session=" + sessionID + "): " + eueID + " -> " + displayID);
		SessionIDEUEIDPair idPair = new SessionIDEUEIDPair(sessionID, eueID);
		realID2displayID.put(idPair, displayID);
	}

	private static class SessionIDEUEIDPair {

		String sessionID;
		String eueID;

		public SessionIDEUEIDPair(String sessionID, String eueID) {
			this.sessionID = sessionID;
			this.eueID = eueID;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((eueID == null) ? 0 : eueID.hashCode());
			result = prime * result
					+ ((sessionID == null) ? 0 : sessionID.hashCode());
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
			SessionIDEUEIDPair other = (SessionIDEUEIDPair) obj;
			if (eueID == null) {
				if (other.eueID != null)
					return false;
			} else if (!eueID.equals(other.eueID))
				return false;
			if (sessionID == null) {
				if (other.sessionID != null)
					return false;
			} else if (!sessionID.equals(other.sessionID))
				return false;
			return true;
		}

	}
}