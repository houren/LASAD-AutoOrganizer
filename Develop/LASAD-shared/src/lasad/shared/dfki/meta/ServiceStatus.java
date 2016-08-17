package lasad.shared.dfki.meta;

/**
 * Runtime status of Feedback-Engine components ({@link Session}s and
 * {@link IComponent}s).
 * 
 * @author oliverscheuer
 * 
 */
public enum ServiceStatus {

	/**
	 * component not fully instantiated yet
	 */
	UNDER_CONSTRUCTION("UNDER_CONSTRUCTION"),

	/**
	 * component instantiated and ready to start services
	 */
	READY_TO_START("READY_TO_START"),

	/**
	 * component about to start
	 */
	STARTING("STARTING"),

	/**
	 * component is currently providing services
	 */
	RUNNING("RUNNING"),

	/**
	 * component about to stop
	 */
	STOPPING("STOPPING"),

	/**
	 * component is used up and cannot be used anymore
	 */
	STALE("STALE");

	//private final String status;

	ServiceStatus(String status) {
		//this.status = status;
	}

	public static boolean isActive(ServiceStatus status) {
		return status == STARTING || status == RUNNING || status == STOPPING;
	}
}
