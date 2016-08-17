package lasad.gwt.client.model.pattern;

import java.util.Vector;

import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.events.LASADEventListenerInterface;
import lasad.gwt.client.model.events.LasadEvent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController;

/**
 * 
 * @author Anahuac
 *
 */
public abstract class PatternMVCViewSession extends AbstractMVCViewSession{
	
	PatternController controller;

	public PatternMVCViewSession(AbstractMVController controller) {
		super(controller);
		this.controller = (PatternController)controller;
	}

	public abstract Vector<MVCViewRecipient> workOnRegisterNewModel(AbstractUnspecifiedElementModel model);

	public abstract void workOnDeleteElementModel(AbstractUnspecifiedElementModel model);

	public abstract Vector<MVCViewRecipient> workOnUnregisterParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child);

	// Some Getters and Setters

	@Override
	public PatternController getController() {
		return controller;
	}
	@Override
	public void setController(AbstractMVController controller) {
		this.controller = (PatternController)controller;
	}
	
	@Override
	public void addLasadEventListener(String event, LASADEventListenerInterface listener){
		
	}
	@Override
	public void removeLasadEventListener(String event, LASADEventListenerInterface listener){
		
	}
	@Override
	public void removeLasadEventListener(LASADEventListenerInterface listener){
		
	}
	@Override
	public void fireLasadEvent(LasadEvent event){
		
	}

}
