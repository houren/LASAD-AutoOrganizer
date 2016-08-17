package lasad.logging.xml;

import lasad.logging.Logger;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

//import org.apache.log4j.Logger;

public class ParameterXmlConverter {
	// static Logger logger = Logger.getLogger(ParameterXmlConverter.class);

	public static XmlFragment toXml(Parameter parameter) {
		XmlFragment xmlFragment = new XmlFragment("Parameter");
		xmlFragment.setAttribute("NAME", parameter.getType().getOldParameter());
		// xmlFragment.setAttribute("NAME", parameter.getType());
		xmlFragment.setAttribute("VALUE", parameter.getValue());
		return xmlFragment;
	}

	public static Parameter fromXml(XmlFragment xmlFragment) {
		try {
			String name = xmlFragment.getAttributeValue("NAME");
			String value = xmlFragment.getAttributeValue("VALUE");
			return new Parameter(ParameterTypes.fromString(name), value);
		} catch (Exception e) {
			// logger.error("[fromXML]:  Bad XML input: \n" + xmlFragment );
			Logger.logError("[fromXML]:  Bad XML input: \n" + xmlFragment);
			return null;
		}
	}

}