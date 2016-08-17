package lasad.gwt.client.model.pattern;

import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController;

public class UnspecifiedElementModelPattern extends AbstractUnspecifiedElementModel {
	
	//private PatternController controller;

	public UnspecifiedElementModelPattern(int id, String type, String author) {
		super(id, type, author);
	}

	public UnspecifiedElementModelPattern(int id, String type) {
		super(id, type);
	}

	@Override
	public void register2MVController(AbstractMVController controller) {
		/*this.controller = */((PatternController)controller).addElementModel(this);
	}

}
