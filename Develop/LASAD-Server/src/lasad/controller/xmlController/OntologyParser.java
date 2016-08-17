package lasad.controller.xmlController;

import java.io.IOException;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * XML parser class for every task related to ontology xml text
 * 
 * @author MB
 */
public class OntologyParser {
	Document doc;
	String ontologyName;

	/**
	 * Builds root document from {@xmlText}
	 * 
	 * @param xmlText The ontology xml (may be extracted from a map xml file)
	 * @author MB
	 */
	public OntologyParser(String xmlText) {
		StringReader in = new StringReader(xmlText);

		SAXBuilder builder = new SAXBuilder();
		try {
			doc = builder.build(in);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return {@ontologyType}
	 * @author MB
	 */
	public String getOntologyName() {
		if (ontologyName == null) {
			Element rootElement = doc.getRootElement();
			if (rootElement.getName().equalsIgnoreCase("ontology")) {
				ontologyName = rootElement.getAttributeValue("type");
			}
		}
		return ontologyName;
	}
}
