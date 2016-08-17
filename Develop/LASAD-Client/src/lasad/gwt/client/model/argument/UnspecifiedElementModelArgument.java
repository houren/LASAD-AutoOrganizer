package lasad.gwt.client.model.argument;

import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;

public class UnspecifiedElementModelArgument extends
		AbstractUnspecifiedElementModel {
	
	//private MVController controller;

	public UnspecifiedElementModelArgument(int id, String type, String author) {
		super(id, type, author);
	}

	public UnspecifiedElementModelArgument(int id, String type) {
		super(id, type);
	}

	@Override
	public void register2MVController(AbstractMVController controller) {
		/*this.controller = */((MVController)controller).addElementModel(this);
	}

}
