package lasad.controller.xmlController;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * XML parser class for every task related to chat log xml text
 * 
 * @author MB
 */
public class ChatParser {
	Document doc;
	Element rootElement;
	Vector<String[]> chatMessages;

	/**
	 * Builds root document from {@xmlText}
	 * 
	 * @param xmlText The chat log xml (may be extracted from a map xml file)
	 * @author MB
	 */
	public ChatParser(String xmlText) {
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
	 * Parses xml map file and retrieves all chat messages
	 * 
	 * @return Vector of chat messages: String[0]: author; String[1]: time stamp; String[2]: message; String[3]: sentence opener;
	 *         String[4]: opener color;
	 */
	public Vector<String[]> GetChatMessages() {
		if (chatMessages == null) {
			chatMessages = new Vector<String[]>();
			List<org.jdom.Element> elements = new Vector<org.jdom.Element>();
			for (Object o : rootElement.getChildren("msg"))
			{
				elements.add((org.jdom.Element) o);
			}
			String[] message = new String[5];

			for (org.jdom.Element element : elements) {
				message[0] = element.getAttributeValue("author");
				message[1] = element.getAttributeValue("timestamp");
				message[2] = element.getText();

				if ((message[3] = element.getAttributeValue("opener")) != null) {
					message[4] = element.getAttributeValue("openercolor");
				}
				chatMessages.add(message);
			}
		}
		return this.chatMessages;
	}
}
