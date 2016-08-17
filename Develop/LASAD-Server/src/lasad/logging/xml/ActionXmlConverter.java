package lasad.logging.xml;

import lasad.logging.Logger;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;

//import org.apache.log4j.Logger;

public class ActionXmlConverter {

	//static Logger logger = Logger.getLogger(ActionXmlConverter.class);

	public static XmlFragment toXml(Action action) {

		XmlFragment xmlFragment = new XmlFragment("Action");
		xmlFragment.setAttribute("COMMAND", action.getCmd().toString());
		xmlFragment.setAttribute("CATEGORY", action.getCategory().toString());
		for (Parameter parameter : action.getParameters()) {
			xmlFragment.addContent(ParameterXmlConverter.toXml(parameter));
		}
		return xmlFragment;
	}

	public static Action fromXml(XmlFragment fragment) {
		try {
			String cmd = fragment.getAttributeValue("COMMAND");
			String category = fragment.getAttributeValue("CATEGORY");
			Action action = new Action(Commands.valueOf(cmd),
					Categories.valueOf(category));
			for (XmlFragment paramElement : fragment.getChildren("Parameter")) {
				Parameter p = ParameterXmlConverter.fromXml(paramElement);
				if (p != null) {
					action.addParameter(p.getType(), p.getValue());
				}
			}
			return action;
		} catch (Exception e) {
			//logger.error("[fromXML]:  Bad XML input: \n" + fragment);
			Logger.logError("[fromXML]:  Bad XML input: \n" + fragment);
			return null;
		}
	}

}
