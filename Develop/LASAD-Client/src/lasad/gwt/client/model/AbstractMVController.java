package lasad.gwt.client.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.model.argument.UnspecifiedElementModelArgument;
import lasad.gwt.client.model.events.LasadEvent;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public abstract class AbstractMVController {

//	private MapInfoNew mapInfo;
	private int actualRevision = 0;

	protected Map<Integer, AbstractUnspecifiedElementModel> elementModelMapping = new HashMap<Integer, AbstractUnspecifiedElementModel>();
//	private Map<AbstractUnspecifiedElementModel, Vector<MVCViewSession>> modelMapped2ViewSession = new HashMap<AbstractUnspecifiedElementModel, Vector<MVCViewSession>>();
//	private Vector<MVCViewSession> registeredViewSessions = new Vector<MVCViewSession>();

	public AbstractMVController(GraphMapInfo mapInfo) {
//		this.mapInfo = mapInfo;
	}

	abstract public AbstractMVController addElementModel(AbstractUnspecifiedElementModel model);

	public AbstractUnspecifiedElementModel createElement(int id, String type) {
		AbstractUnspecifiedElementModel model = new UnspecifiedElementModelArgument(id, type);
		this.addElementModel(model);
		return model;
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
	abstract public boolean updateElement(int id, Vector<Parameter> values);
	
	public void deleteElement(int id, String username) {
		// Get Model
		AbstractUnspecifiedElementModel model = elementModelMapping.get(id);
		
		// Update username in model to realize highlighting of extend elements
		if(username != null){
			model.setValue(ParameterTypes.UserName, username);
		}
		
		deleteElement(model);
	}

	abstract public void deleteElement(AbstractUnspecifiedElementModel model);

	public AbstractUnspecifiedElementModel getElement(int id) {
		return elementModelMapping.get(id);
	}
	
	public Collection<AbstractUnspecifiedElementModel> getAllElements() {
		return elementModelMapping.values();
	}

	abstract public void registerViewSession(AbstractMVCViewSession session);

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

	abstract public void removeParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child);

	abstract public String getMapID();
//	{
//		return mapInfo.getMapID();
//	}

	abstract public GraphMapInfo getMapInfo();
//	{
//		return mapInfo;
//	}

	abstract public void setMapInfo(GraphMapInfo mapInfo);
//	{
//		this.mapInfo = mapInfo;
//	}

	public int getActualRevision() {
		return actualRevision;
	}

	public void setActualRevision(int actualRevision) {
		this.actualRevision = actualRevision;
	}

	// Event Handler/Listener Management

	abstract public void fireLasadEvent(LasadEvent event);
//	{
//		// Publish LasadEvent to Sessions
//		for (MVCViewSession session : registeredViewSessions) {
//			session.fireLasadEvent(event);
//		}
//	}

}
