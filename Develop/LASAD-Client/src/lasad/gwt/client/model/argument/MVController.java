package lasad.gwt.client.model.argument;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.GraphMapInfo;
import lasad.gwt.client.model.events.LasadEvent;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class MVController extends AbstractMVController {

	private MapInfo mapInfo;

	private Map<AbstractUnspecifiedElementModel, Vector<MVCViewSession>> modelMapped2ViewSession = new HashMap<AbstractUnspecifiedElementModel, Vector<MVCViewSession>>();
	private Vector<MVCViewSession> registeredViewSessions = new Vector<MVCViewSession>();

	public MVController(GraphMapInfo mapInfo) {
		super(mapInfo);
		this.mapInfo = (MapInfo)mapInfo;
	}

	@Override
	public MVController addElementModel(AbstractUnspecifiedElementModel model) {
		if (!modelMapped2ViewSession.containsKey(model)) {

			modelMapped2ViewSession.put(model, new Vector<MVCViewSession>());
			elementModelMapping.put(model.getId(), model);
		}

		// Publish the new Model to the sessions
		for (MVCViewSession session : registeredViewSessions) {
			if (session.registerNewModel(model)) {
				// The Session subscribed the Element
				modelMapped2ViewSession.get(model).add(session);
			}
		}

		// Send all modeldata to the sessions
		return this;
	}

	/**
	 * Updates values on the model and via MVCViewSession on a concrete view
	 * 
	 * Note: To ensure proper highlighting its necessary to make sure that the USERNAME parameter is processed first
	 * 		 This is realized in the LASADActionReceiver when iterating over the actions.
	 * 
	 * @param id
	 * @param values
	 * @return Boolean
	 */
	@Override
	public boolean updateElement(int id, Vector<Parameter> values) {
		// Check if Model exists
		if (elementModelMapping.containsKey(id)) {
			// Model exists
			AbstractUnspecifiedElementModel model = elementModelMapping.get(id);
			
			// normal parameter update
			for (Parameter param : values) {

				if (model.setValue(param.getType(), param.getValue())) {
					// returns true if the model has been changed
					// Value has changed, updated subscribed Sessions
					for (MVCViewSession session : modelMapped2ViewSession.get(model)) {
						session.changeValue(model, param.getType());
					}
				}
				// Else, nothing to do
			}
			return true;
			
		} else {
			Logger.log("Tried to update non existing element with ID "+id, Logger.DEBUG_ERRORS);
			return false;
		}
	}
	
	public void deleteElement(int id, String username) {
		// Get Model
		AbstractUnspecifiedElementModel model = elementModelMapping.get(id);
		
		// Update username in model to realize highlighting of extend elements
		if(username != null){
			model.setValue(ParameterTypes.UserName, username);
		}
		
		deleteElement(model);
	}
	
	@Override
	public void deleteElement(AbstractUnspecifiedElementModel model) {

		Logger.log("[MVCController]deleteElement: elementID: " + model.getId(), Logger.DEBUG);
		if (modelMapped2ViewSession.containsKey(model)) {

			// Delete Model from Sessions
			for (MVCViewSession session : modelMapped2ViewSession.get(model)) {
				session.unregisterModel(model);
			}

			// Remove from Childs
			while (model.getChildren().size() > 0) {
				// deleteElement(model.getChilds().firstElement());
				removeParent(model, model.getChildren().firstElement());

			}
			// Remove from Parents
			while (model.getParents().size() > 0) {
				model.getParents().firstElement().removeChild(model);
			}

			elementModelMapping.remove(model.getId());
			// Delete the model
			modelMapped2ViewSession.remove(model);

			model = null;
		} else {
			// Nothing to Do, the model was already deleted in the past
		}
	}

	public AbstractUnspecifiedElementModel getElement(int id) {
		return elementModelMapping.get(id);
	}
	
	public Collection<AbstractUnspecifiedElementModel> getAllElements() {
		return elementModelMapping.values();
	}

	@Override
	public void registerViewSession(AbstractMVCViewSession session) {
		// Add Session
		MVCViewSession vSession = (MVCViewSession)session;
		this.registeredViewSessions.add(vSession);
		// Publish the managed Model to the Session
		Vector<AbstractUnspecifiedElementModel> workingModels = new Vector<AbstractUnspecifiedElementModel>();
		for (AbstractUnspecifiedElementModel model : modelMapped2ViewSession.keySet()) {
			if (vSession.registerNewModel(model)) {
				// Session wants to be a subscriber
				workingModels.add(model);
			}
		}
		// Subscribes the Session to the ModelElement
		for (AbstractUnspecifiedElementModel model : workingModels) {
			modelMapped2ViewSession.get(model).add(vSession);
		}
	}

	public void setParent(AbstractUnspecifiedElementModel child, AbstractUnspecifiedElementModel parent) {
		
		parent.addChild(child);
		
//		if (modelMapped2ViewSession.containsKey(child) && modelMapped2ViewSession.containsKey(parent)) {
//			parent.addChild(child);
//
//			// Publish the new Relations to all Sessions
//			for (MVCViewSession session : registeredViewSessions) {
//				if (session.registerNewModel(parent) && !modelMapped2ViewSession.get(parent).contains(session)) {
//					// The Session subscribed the Element
//					modelMapped2ViewSession.get(parent).add(session);
//				}
//				if (session.registerNewModel(child) && !modelMapped2ViewSession.get(child).contains(session)) {
//					// The Session subscribed the Element
//					modelMapped2ViewSession.get(child).add(session);
//				}
//			}
//		}
	}

	@Override
	public void removeParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child) {
		// Publish this action to the sessions
		for (MVCViewSession session : registeredViewSessions) {
			Vector<AbstractUnspecifiedElementModel> unsubscribedModels = session.unregisterParent(parent, child);
			if (unsubscribedModels.size() > 0) {
				for (AbstractUnspecifiedElementModel model : unsubscribedModels) {
					modelMapped2ViewSession.get(model).remove(session);
				}
			}
		}
		parent.removeChild(child);
	}

	@Override
	public String getMapID() {
		return mapInfo.getMapID();
	}
	
	@Override
	public MapInfo getMapInfo() {
		return mapInfo;
	}
	@Override
	public void setMapInfo(GraphMapInfo mapInfo) {
		this.mapInfo = (MapInfo)mapInfo;
	}

	// Event Handler/Listener Management
	@Override
	public void fireLasadEvent(LasadEvent event) {
		// Publish LasadEvent to Sessions
		for (MVCViewSession session : registeredViewSessions) {
			session.fireLasadEvent(event);
		}
	}
}