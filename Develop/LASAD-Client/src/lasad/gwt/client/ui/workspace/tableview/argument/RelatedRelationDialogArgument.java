package lasad.gwt.client.ui.workspace.tableview.argument;

import java.util.Vector;

import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.workspace.tableview.AbstractRelatedRelationDialog;

public class RelatedRelationDialogArgument extends
		AbstractRelatedRelationDialog {
	
	private MVCViewSession session;

	public RelatedRelationDialogArgument(
			Vector<AbstractUnspecifiedElementModel> models,
			AbstractMVCViewSession session) {
		super(models, session);
	}

	@Override
	protected MVCViewSession getSession() {
		return session;
	}

	@Override
	protected void setSession(AbstractMVCViewSession session) {
		this.session = (MVCViewSession)session;
	}

}
