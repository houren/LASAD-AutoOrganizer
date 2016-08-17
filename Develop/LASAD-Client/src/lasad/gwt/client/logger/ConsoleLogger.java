package lasad.gwt.client.logger;



public class ConsoleLogger implements LoggerInterface {

	private static ConsoleLogger instance;
	
	private ConsoleLogger() {
		
	}
	
	public static ConsoleLogger getInstance() {
		if (ConsoleLogger.instance == null) {
			instance = new ConsoleLogger();
		}
		return instance;
	}
	
	@Override
	public void log(String logText) {
		System.out.println(logText);
	}
	
	@Override
	public void logErr(String logText) {
		System.err.println(logText);
	}
}
