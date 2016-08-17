package lasad.gwt.client.importer.ARGUNAUT;

import java.util.Date;
import java.util.Vector;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.ElementInfo;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * Specialized factory to generate ActionPackages for the Digalo / ARGUNAUT
 * parser
 **/
public class ARGUNAUTActionFactory {

	private static ARGUNAUTActionFactory instance = null;

	public static ARGUNAUTActionFactory getInstance() {
		if (instance == null) {
			instance = new ARGUNAUTActionFactory();
		}
		return instance;
	}

	private ARGUNAUTActionFactory() {

	}

	public ActionPackage createBoxWithElementsAndContent(ElementInfo currentElement, String nodeId, String mapID, int posX, int posY, String headline, String text, String author, String creationDate, String modificationDate, String nodeFirstModificationDate) {
		ActionPackage p = createActionPackage();
		Action a = createBox(currentElement, nodeId, mapID, posX, posY, creationDate, modificationDate, nodeFirstModificationDate);
		p.addAction(a);

		Vector<Action> b = createBoxElementsAndContentAction(currentElement, mapID, headline, text, author, creationDate);
		if (b.size() > 0) {
			for (Action c : b) {
				p.addAction(c);
			}
		}
		return p;
	}

	private ActionPackage createActionPackage() {
		return new ActionPackage();
	}

	private Action createSpecificAction(Commands cmd, Categories category) {
		return new Action(cmd, category);
	}
	
	public ActionPackage parsingFinished(String importType) {
		ActionPackage p = new ActionPackage();
		Action a = new Action(Commands.ImportFinished, Categories.File);
		a.addParameter(ParameterTypes.ImportType, importType);
		p.addAction(a);
		return p;
	}

	private Action createBox(ElementInfo config, String objectID, String mapID, int posX, int posY, String creationDate, String modificationDate, String nodeFirstModificationDate) {
		Action a = createSpecificAction(Commands.CreateMap, Categories.Map);
		a.addParameter(ParameterTypes.Type, "box");
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.ElementId, config.getElementID());
		a.addParameter(ParameterTypes.ObjectId, objectID);
		a.addParameter(ParameterTypes.PosX, String.valueOf(posX));
		a.addParameter(ParameterTypes.PosY, String.valueOf(posY));
		a.addParameter(ParameterTypes.CreationDate, creationDate);
		a.addParameter(ParameterTypes.ModificationDate, modificationDate);
		a.addParameter(ParameterTypes.FirstModificationDate, nodeFirstModificationDate);
		a.addParameter(ParameterTypes.Replay, "true");
		return a;
	}

	private Vector<Action> createBoxElementsAndContentAction(ElementInfo currentElement, String mapID, String headline, String text, String author, String creationdate) {

		Vector<Action> allActions = new Vector<Action>();

		for (ElementInfo childElement : currentElement.getChildElements().values()) {
			Logger.log("Creating action for ChildElement: " + childElement.getElementType(), Logger.DEBUG_DETAILS);
			for (int i = 0; i < childElement.getQuantity(); i++) {
				Action b = null;

				b = createSpecificAction(Commands.CreateElement, Categories.Map);
				b.addParameter(ParameterTypes.Type, childElement.getElementType());
				b.addParameter(ParameterTypes.MapId, mapID);
				b.addParameter(ParameterTypes.Parent, "LAST-ID");
				b.addParameter(ParameterTypes.ElementId, String.valueOf(childElement.getElementID()));
				b.addParameter(ParameterTypes.Replay, "true");

				if ((childElement.getElementType().equals("text")) || (childElement.getElementType().equals("url"))) {
					// In case the child element is a headline element
					if (childElement.getElementID().equals("headline")) {
						b.addParameter(ParameterTypes.Text, headline);
					}
					// In case the child element is a text field element
					else if (childElement.getElementID().equals("text")) {
						b.addParameter(ParameterTypes.Text, text);
					}
				} else if (childElement.getElementType().equals("rating")) {
					b.addParameter(ParameterTypes.Score, childElement.getElementOption(ParameterTypes.Score));
				} else if (childElement.getElementType().equals("awareness")) {

					Date date = new Date(Long.parseLong(creationdate));
					String dateString = date.toString();
					dateString = dateString.substring(4, 16);

					b.addParameter(ParameterTypes.Text, author + ", " + dateString);

				}
				if (b != null) {
					allActions.add(b);
				}
			}
		}
		return allActions;
	}

	public ActionPackage createLinkWithElements(ElementInfo info, String mapID, String startElementID, String endElementID, String author, String creationDate) {

		ActionPackage p = createActionPackage();

		Vector<Action> b = createLinkWithElementsAction(info, mapID, startElementID, endElementID, author, creationDate);
		if (b.size() > 0) {
			for (Action c : b) {
				p.addAction(c);
			}
		}
		return p;
	}

	private Vector<Action> createLinkWithElementsAction(ElementInfo info, String mapID, String startElementID, String endElementID, String author, String creationDate) {

		Vector<Action> resultingActions = new Vector<Action>();

		Action a = createSpecificAction(Commands.CreateElement, Categories.Map);
		a.addParameter(ParameterTypes.Type, info.getElementType());
		a.addParameter(ParameterTypes.MapId, mapID);
		a.addParameter(ParameterTypes.ElementId, String.valueOf(info.getElementID()));
		a.addParameter(ParameterTypes.Parent, startElementID);
		a.addParameter(ParameterTypes.Parent, endElementID);
		a.addParameter(ParameterTypes.Time, creationDate);
		a.addParameter(ParameterTypes.CreationDate, creationDate);
		a.addParameter(ParameterTypes.Replay, "true");

		resultingActions.add(a);

		for (ElementInfo childElement : info.getChildElements().values()) {
			Logger.log("Creating action for ChildElement: " + childElement.getElementType(), Logger.DEBUG_DETAILS);
			for (int i = 0; i < childElement.getQuantity(); i++) {
				Action b = null;
				b = createSpecificAction(Commands.CreateElement, Categories.Map);
				b.addParameter(ParameterTypes.Type, childElement.getElementType());
				b.addParameter(ParameterTypes.MapId, mapID);
				b.addParameter(ParameterTypes.Parent, "LAST-ID");
				b.addParameter(ParameterTypes.ElementId, String.valueOf(childElement.getElementID()));
				b.addParameter(ParameterTypes.Replay, "true");

				if ((childElement.getElementType().equals("text")) || (childElement.getElementType().equals("url"))) {

				} else if (childElement.getElementType().equals("rating")) {
					b.addParameter(ParameterTypes.Score, childElement.getElementOption(ParameterTypes.Score));
				} else if (childElement.getElementType().equals("awareness")) {
					Date date = new Date(Long.parseLong(creationDate));
					String dateString = date.toString();
					dateString = dateString.substring(4, 16);

					b.addParameter(ParameterTypes.Text, author + ", " + dateString);
				}
				if (b != null) {
					resultingActions.add(b);
				}
			}
		}
		return resultingActions;
	}
}
