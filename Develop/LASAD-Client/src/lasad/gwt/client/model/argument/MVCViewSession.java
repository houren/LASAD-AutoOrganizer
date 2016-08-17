package lasad.gwt.client.model.argument;

import java.util.HashMap;
import java.util.Vector;

import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.events.LASADEventListenerInterface;
import lasad.gwt.client.model.events.LasadEvent;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public abstract class MVCViewSession extends AbstractMVCViewSession {

	MVController controller = null;

	public MVCViewSession(AbstractMVController controller) {
		super(controller);
		this.controller = (MVController)controller;
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

	public MVController getController() {
		return controller;
	}

	@Override
	public void setController(AbstractMVController controller) {
		this.controller = (MVController)controller;
	}

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

	private HashMap<String, Vector<LASADEventListenerInterface>> lasadEvent2Listener = new HashMap<String, Vector<LASADEventListenerInterface>>();
	private HashMap<LASADEventListenerInterface, Vector<String>> lasadListener2Event = new HashMap<LASADEventListenerInterface, Vector<String>>();

	@Override
	public void addLasadEventListener(String event, LASADEventListenerInterface listener) {
		if (!lasadEvent2Listener.containsKey(event)) {
			lasadEvent2Listener.put(event, new Vector<LASADEventListenerInterface>());
		}

		if (!lasadListener2Event.containsKey(listener)) {
			lasadListener2Event.put(listener, new Vector<String>());
		}

		// Add Event-Listener-Combo

		lasadEvent2Listener.get(event).add(listener);
		lasadListener2Event.get(listener).add(event);
	}
	
	@Override
	public void removeLasadEventListener(String event, LASADEventListenerInterface listener) {
		if (lasadListener2Event.get(listener) != null) {

			lasadListener2Event.get(listener).remove(event);
			lasadEvent2Listener.get(event).remove(listener);
		}
	}

	@Override
	public void removeLasadEventListener(LASADEventListenerInterface listener) {
		if (lasadListener2Event.get(listener) != null) {

			for (String event : lasadListener2Event.get(listener)) {

				lasadEvent2Listener.get(event).remove(listener);
			}

			lasadListener2Event.remove(listener);
		}
	}

	@Override
	public void fireLasadEvent(LasadEvent event) {
		// Publish a LasadEvent to all registered listeners
		if (lasadEvent2Listener.containsKey(event.getType())) {
			// Set Session
			event.setSession(this);
			for (LASADEventListenerInterface listener : lasadEvent2Listener.get(event.getType())) {
				listener.fireLasadEvent(event);
			}
		}
	}
}
