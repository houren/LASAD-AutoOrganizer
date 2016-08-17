package lasad.controller.xmlController;

import java.io.IOException;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * XML parser class for every task related to template xml text
 * 
 * @author MB
 */
public class TemplateParser {
	private Document doc;
	private Element rootElement;

	private String templateName;
	private String ontologyName;

	/**
	 * Builds root document from {@xmlText}
	 * 
	 * @param xmlText The template xml (may be extracted from a map xml file)
	 * @author MB
	 */
	public TemplateParser(String xmlText) {
		StringReader in = new StringReader(xmlText);

		SAXBuilder builder = new SAXBuilder();
		try {
			doc = builder.build(in);
			rootElement = doc.getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return {@templateName}
	 * @author MB
	 */
	public String getTemplateName() {
		if (templateName == null) {
			if (rootElement.getName().equalsIgnoreCase("maptemplate")) {
				templateName = rootElement.getAttributeValue("uniquename");
			}
		}
		return templateName;
	}

	public String getOntologyName() {
		if (ontologyName == null) {
			if (rootElement.getName().equalsIgnoreCase("maptemplate")) {
				ontologyName = rootElement.getAttributeValue("ontology");
			}
		}
		return ontologyName;
	}
}
