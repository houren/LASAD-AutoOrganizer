package lasad.gwt.client.importer;

public class ImportFileChecker {
	
	private ImportFileChecker() {}
	
	public static boolean checkArgunaut(String xmlFileName) {
		if (xmlFileName.contains(".gml")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean checkValidFileType(String fileName) {
		if ((!fileName.endsWith("_log.xml")) && (fileName.endsWith(".xml") || fileName.endsWith(".gml"))) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean checkLargo(String xmlFileContent) {
//		if (xmlFileContent.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><DocumentRoot> <MetaData version=\"2.0")) {
		if (xmlFileContent.contains("DocumentRoot") && xmlFileContent.contains("MetaData version")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static String getArgunautTemplateName() {
		return "argunaut";
	}
	
	public static String getArgunautOntologyName() {
		return "Argunaut";
	}
	
	public static String getLargoOntologyName() {
		return "LARGO";
	}
	
	public static String getLargoTemplateName(String fileName) {
		fileName = fileName.replace(".xml", "");
		String[] parts = fileName.split("_");
		//see largo file name pattern
		return "largo_" + parts[2] + "_" + parts[3];
	}
}
