package lasad.logging.xml;

import java.util.List;

import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.Parameter;



public class ActionPackageXmlConverter {
	
	public static XmlFragment toXml(ActionPackage actionPackage){
		XmlFragment xmlFragment = new XmlFragment("ActionPackage");
		for (Action action : actionPackage.getActions()){
			xmlFragment.addContent(ActionXmlConverter.toXml(action));
		}
		for (Parameter parameter :actionPackage.getParameters()){
			xmlFragment.addContent(ParameterXmlConverter.toXml(parameter));
		}
		return xmlFragment;
	}
	
	public static ActionPackage createFromXml(XmlFragment fragment){
		ActionPackage actionPackage = new ActionPackage();
		List<XmlFragment> children = fragment.getChildren("Action");
		for (XmlFragment actionElement : children){
			Action a = ActionXmlConverter.fromXml(actionElement);
			if (a != null){
				actionPackage.addAction(a);
			}
		}
		for (XmlFragment paramElement : fragment.getChildren("Parameter")){
			Parameter p = ParameterXmlConverter.fromXml(paramElement);
			if (p != null){
				actionPackage.addParameter(p.getType(), p.getValue());
			}	
		}
		return actionPackage;
	}

}
