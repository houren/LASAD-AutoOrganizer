package lasad.gwt.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.model.events.LASADEventListenerInterface;
import lasad.gwt.client.model.events.LasadEvent;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public abstract class AbstractMVCViewSession {
	protected Map<AbstractUnspecifiedElementModel, Vector<MVCViewRecipient>> modelMapped2ViewRecipient = new HashMap<AbstractUnspecifiedElementModel, Vector<MVCViewRecipient>>();
	protected Map<MVCViewRecipient, Vector<AbstractUnspecifiedElementModel>> viewRecipientMapped2Model = new HashMap<MVCViewRecipient, Vector<AbstractUnspecifiedElementModel>>();

	public AbstractMVCViewSession(AbstractMVController controller) {
		super();
	}

	public boolean registerNewModel(AbstractUnspecifiedElementModel model) {
		// Returns true, when the Model gets at least one recipient @ this
		// Session

		Vector<MVCViewRecipient> newRecipients = workOnRegisterNewModel(model);

		if (newRecipients != null) {
			// Register Model
			if (!modelMapped2ViewRecipient.containsKey(model)) {
				modelMapped2ViewRecipient.put(model, new Vector<MVCViewRecipient>());
			}

			// Register Views
			for (MVCViewRecipient recipient : newRecipients) {

				recipient.establishModelConnection(model);

				modelMapped2ViewRecipient.get(model).add(recipient);

				if (!viewRecipientMapped2Model.containsKey(recipient)) viewRecipientMapped2Model.put(recipient, new Vector<AbstractUnspecifiedElementModel>());

				viewRecipientMapped2Model.get(recipient).add(model);

				// Publish modeldata to the Recipient
				// Try-Catch
				// is necessary, because highlighting of relations will cause a keySet change, which results in a ConcurrentModificationException while iteration
				// a second run on the updated keySet should work
				boolean run;
				do{
					run = false;
					try{
						for (ParameterTypes vName : model.getElementValues().keySet()) {
							recipient.changeValueMVC(model, vName);								
						}

					}catch(Exception e){
						run = true;
					}
				}while(run == true);

			}
		}

		// At least one connection established to a view of this session
		if (modelMapped2ViewRecipient.get(model).size() > 0) {

			return true;
		} else {
			// No Connection established to a view of this session
			return false;
		}
	}

	public void unregisterModel(AbstractUnspecifiedElementModel model) {
		workOnDeleteElementModel(model);

		if (modelMapped2ViewRecipient.containsKey(model)) {
			for (MVCViewRecipient recipient : modelMapped2ViewRecipient.get(model)) {
				viewRecipientMapped2Model.get(recipient).remove(model);
				recipient.deleteModelConnection(model);
			}
			modelMapped2ViewRecipient.remove(model);
		}
	}

	public Vector<AbstractUnspecifiedElementModel> unregisterParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child) {
		// Returns a list of no longer subscribed models
		Vector<AbstractUnspecifiedElementModel> unsubscribedModels = new Vector<AbstractUnspecifiedElementModel>();

		Vector<MVCViewRecipient> recipientsToDelete = workOnUnregisterParent(parent, child);

		if (recipientsToDelete != null) {
			// There are some Recipients, which sould be deleted
			for (MVCViewRecipient recipient : recipientsToDelete) {
				// disconnect the recipient form all Models
				while (viewRecipientMapped2Model.get(recipient).size() != 0) {
					AbstractUnspecifiedElementModel model = viewRecipientMapped2Model.get(recipient).firstElement();
					modelMapped2ViewRecipient.get(model).remove(recipient);
					if (modelMapped2ViewRecipient.get(model).size() == 0) {
						// Unsubscribe Model from this Session
						unsubscribedModels.add(model);
					}
					viewRecipientMapped2Model.get(recipient).remove(model);
				}

				recipient.deleteModelConnection(child);

			}
		}

		return unsubscribedModels;
	}

	public void changeValue(AbstractUnspecifiedElementModel model, ParameterTypes vname) {
		// Publish changes to all registered Recipients
		for (MVCViewRecipient view : modelMapped2ViewRecipient.get(model)) {
			view.changeValueMVC(model, vname);

		}
	}

	public abstract Vector<MVCViewRecipient> workOnRegisterNewModel(AbstractUnspecifiedElementModel model);

	public abstract void workOnDeleteElementModel(AbstractUnspecifiedElementModel model);

	public abstract Vector<MVCViewRecipient> workOnUnregisterParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child);

	// Some Getters and Setters

	abstract public AbstractMVController getController();
//	{
//		return controller;
//	}

	abstract public void setController(AbstractMVController controller);
//	{
//		this.controller = controller;
//	}

	public Vector<MVCViewRecipient> getMVCViewRecipientsByModel(AbstractUnspecifiedElementModel model) {
		if (modelMapped2ViewRecipient.containsKey(model)) {
			return modelMapped2ViewRecipient.get(model);
		}
		return new Vector<MVCViewRecipient>();
	}

	public void registerMVCViewRecipientForModel(MVCViewRecipient r, AbstractUnspecifiedElementModel model) {
		Vector<MVCViewRecipient> newRecipients = null;

		if (modelMapped2ViewRecipient.containsKey(model)) {
			newRecipients = modelMapped2ViewRecipient.get(model);
		}

		if(newRecipients != null) {
			newRecipients.add(r);
			modelMapped2ViewRecipient.put(model, newRecipients);
		}

	}

	// Event Handler/Listener Management

//	private HashMap<String, Vector<LASADEventListenerInterface>> lasadEvent2Listener = new HashMap<String, Vector<LASADEventListenerInterface>>();
//	private HashMap<LASADEventListenerInterface, Vector<String>> lasadListener2Event = new HashMap<LASADEventListenerInterface, Vector<String>>();

	abstract public void addLasadEventListener(String event, LASADEventListenerInterface listener);
//	{
//		if (!lasadEvent2Listener.containsKey(event)) {
//			lasadEvent2Listener.put(event, new Vector<LASADEventListenerInterface>());
//		}
//
//		if (!lasadListener2Event.containsKey(listener)) {
//			lasadListener2Event.put(listener, new Vector<String>());
//		}
//
//		// Add Event-Listener-Combo
//
//		lasadEvent2Listener.get(event).add(listener);
//		lasadListener2Event.get(listener).add(event);
//	}

	abstract public void removeLasadEventListener(String event, LASADEventListenerInterface listener);
//	{
//		if (lasadListener2Event.get(listener) != null) {
//
//			lasadListener2Event.get(listener).remove(event);
//			lasadEvent2Listener.get(event).remove(listener);
//		}
//	}

	abstract public void removeLasadEventListener(LASADEventListenerInterface listener);
//	{
//		if (lasadListener2Event.get(listener) != null) {
//
//			for (String event : lasadListener2Event.get(listener)) {
//
//				lasadEvent2Listener.get(event).remove(listener);
//			}
//
//			lasadListener2Event.remove(listener);
//		}
//	}

	abstract public void fireLasadEvent(LasadEvent event);
//	{
//		// Publish a LasadEvent to all registered listeners
//		if (lasadEvent2Listener.containsKey(event.getType())) {
//			// Set Session
//			event.setSession(this);
//			for (LASADEventListenerInterface listener : lasadEvent2Listener.get(event.getType())) {
//				listener.fireLasadEvent(event);
//			}
//		}
//	}
}
