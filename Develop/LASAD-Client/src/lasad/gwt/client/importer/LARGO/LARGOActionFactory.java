package lasad.gwt.client.importer.LARGO;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class LARGOActionFactory {

	private static LARGOActionFactory instance = null;

	private LARGOActionFactory() {}

	public static LARGOActionFactory getInstance() {
		if (instance == null) {
			instance = new LARGOActionFactory();
		}
		return instance;
	}

	private ActionPackage createActionPackage() {
		return new ActionPackage();
	}

	private Action createBox(String mapID, ElementInfo config, HashMap<String, String> parsedBoxValues) {
		Action a = createSpecificAction(Commands.CreateElement, Categories.Map);
		a.addParameter(ParameterTypes.Type, "box");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.ElementId, config.getElementID());
		a.addParameter(ParameterTypes.ObjectId, parsedBoxValues.get("ID"));
		a.addParameter(ParameterTypes.PosX, parsedBoxValues.get("posx"));
		a.addParameter(ParameterTypes.PosY, parsedBoxValues.get("posy"));
		return a;
	}

	private Action createSpecificAction(Commands cmd, Categories category) {
		return new Action(cmd, category);
	}

	private Vector<Action> createBoxElementsAction(ElementInfo currentElement, String mapID, HashMap<String, String> parsedValues) {
		Vector<Action> allActions = new Vector<Action>();

		for (ElementInfo childElement : currentElement.getChildElements().values()) {

			if (!(parsedValues.get("andQnt") == null) && childElement.getElementID().equals("and")) {

				String s = parsedValues.get("andQnt");
				int quantity = Integer.parseInt(s);
				childElement.setQuantity(quantity);
			} else if ((parsedValues.get("and") == null) && childElement.getElementID().equals("and")) {
				childElement.setQuantity(0);
			}

			if (!(parsedValues.get("even") == null) && childElement.getElementID().equals("even")) {
				childElement.setQuantity(1);
			} else if ((parsedValues.get("even") == null) && childElement.getElementID().equals("even")) {
				childElement.setQuantity(0);
			}

			if (!(parsedValues.get("except") == null) && childElement.getElementID().equals("except")) {
				childElement.setQuantity(1);
			} else if ((parsedValues.get("except") == null) && childElement.getElementID().equals("except")) {
				childElement.setQuantity(0);
			}

			if (!(parsedValues.get("outcome") == null) && childElement.getElementID().equals("outcome")) {
				childElement.setQuantity(1);
			} else if ((parsedValues.get("outcome") == null) && childElement.getElementID().equals("outcome")) {
				childElement.setQuantity(0);
			}

			if (!(parsedValues.get("transcript") == null) && childElement.getElementID().equals("transcriptlink")) {
				childElement.setQuantity(1);
			} else if ((parsedValues.get("transcript") == null) && childElement.getElementID().equals("transcriptlink")) {
				childElement.setQuantity(0);
			}

			for (int i = 0; i < childElement.getQuantity(); i++) {
				Action b = null;

				if (childElement.getElementType().equals("text")) {
					b = createSpecificAction(Commands.CreateElement, Categories.Map);

					b.addParameter(ParameterTypes.ElementId, childElement.getElementID());
					// TODO: abgesehen von dem and-Teil lassen sich alle zusammenfassen zu b.addParameter(ParameterTypes.Text, parsedValues.get(childElement.getElementID()));
					if (childElement.getElementID().equals("text")) {
						b.addParameter(ParameterTypes.Text, parsedValues.get("text"));
					} else if (childElement.getElementID().equals("outcome")) {
						b.addParameter(ParameterTypes.Text, parsedValues.get("outcome"));
					} else if (childElement.getElementID().equals("if")) {
						b.addParameter(ParameterTypes.Text, parsedValues.get("if"));
					} else if (childElement.getElementID().equals("and")) {
						b.addParameter(ParameterTypes.Text, parsedValues.get("and" + i));
					} else if (childElement.getElementID().equals("even")) {
						b.addParameter(ParameterTypes.Text, parsedValues.get("even"));
					} else if (childElement.getElementID().equals("then")) {
						b.addParameter(ParameterTypes.Text, parsedValues.get("then"));
					} else if (childElement.getElementID().equals("except")) {
						b.addParameter(ParameterTypes.Text, parsedValues.get("except"));
					}
				}

				else if (childElement.getElementType().equals("transcript-link")) {

					int startRow = Integer.parseInt(parsedValues.get("startRow"));
					int startPoint = Integer.parseInt(parsedValues.get("startPoint"));
					int endRow = Integer.parseInt(parsedValues.get("endRow"));
					int endPoint = Integer.parseInt(parsedValues.get("endPoint"));

					b = createSpecificAction(Commands.CreateElement, Categories.Map);

					b.addParameter(ParameterTypes.ElementId, "transcriptlink");

					b.addParameter(ParameterTypes.StartRow, startRow + "");
					b.addParameter(ParameterTypes.StartPoint, startPoint + "");
					b.addParameter(ParameterTypes.EndRow, endRow + "");
					b.addParameter(ParameterTypes.EndPoint, endPoint + "");
				} else if (childElement.getElementType().equals("awareness")) {
					b = createSpecificAction(Commands.CreateElement, Categories.Map);

					b.addParameter(ParameterTypes.ElementId, String.valueOf(childElement.getElementID()));

					long millis = Long.parseLong(parsedValues.get("date"));
					Date tmpDate = new Date(millis);
					String date = tmpDate.toString();
					date = date.substring(4, 16);
					b.addParameter(ParameterTypes.Text, parsedValues.get("author") + ", " + date);
				}

				if (b != null) {
					b.addParameter(ParameterTypes.Type, childElement.getElementType());
					b.addParameter(ParameterTypes.Parent, "LAST-ID");
					b.addParameter(ParameterTypes.MapId, mapID);
					b.addParameter(ParameterTypes.Time, parsedValues.get("date"));
					allActions.add(b);
				}
			}
		}
		return allActions;
	}

	public ActionPackage createBoxWithElements(String mapID, ElementInfo currentElement, HashMap<String, String> parsedBoxValues) {
		ActionPackage p = createActionPackage();
		Action a = createBox(mapID, currentElement, parsedBoxValues);
		p.addAction(a);

		Vector<Action> b = createBoxElementsAction(currentElement, mapID, parsedBoxValues);
		if (b.size() > 0) {
			for (Action c : b) {
				p.addAction(c);
			}
		}
		return p;
	}

	public ActionPackage createLink(String id, ElementInfo elementInfo, HashMap<String, Object> edgeValues) {
		ActionPackage p = createActionPackage();

		Action a = createSpecificAction(Commands.CreateElement, Categories.Map);
		a.addParameter(ParameterTypes.Type, elementInfo.getElementType());
		a.addParameter(ParameterTypes.MapId, id);
		a.addParameter(ParameterTypes.ElementId, String.valueOf(elementInfo.getElementID()));
		a.addParameter(ParameterTypes.Parent, ((Integer) edgeValues.get("source")) + "");
		a.addParameter(ParameterTypes.Parent, ((Integer) edgeValues.get("target")) + "");

		p.addAction(a);

		// Now walk through the possible Elements
		for (ElementInfo childElement : elementInfo.getChildElements().values()) {
			for (int i = 0; i < childElement.getQuantity(); i++) {
				Action b = null;

				if (childElement.getElementType().equals("text")) {
					b = createSpecificAction(Commands.CreateElement, Categories.Map);

					b.addParameter(ParameterTypes.Type, childElement.getElementType());
					b.addParameter(ParameterTypes.MapId, id);
					b.addParameter(ParameterTypes.Parent, "LAST-ID");
					b.addParameter(ParameterTypes.ElementId, childElement.getElementID());

					if (childElement.getElementID().equals("text")) {
						b.addParameter(ParameterTypes.Text, (String) edgeValues.get("text"));
					}
				}
				if (b != null) p.addAction(b);
			}
		}
		return p;
	}
	
	public ActionPackage parsingFinished(String importType) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.ImportFinished, Categories.File);
		a.addParameter(ParameterTypes.ImportType, importType);
		p.addAction(a);
		return p;
	}
}