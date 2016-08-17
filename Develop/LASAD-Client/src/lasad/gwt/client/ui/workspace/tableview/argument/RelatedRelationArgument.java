package lasad.gwt.client.ui.workspace.tableview.argument;

import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.workspace.tableview.AbstractRelatedRelation;
import lasad.gwt.client.ui.workspace.tableview.AbstractRelatedRelationDialog;
import lasad.gwt.client.ui.workspace.tableview.TableCellTypeEnum;
import lasad.gwt.client.ui.workspace.tableview.argument.elements.RatingElementArgument;
import lasad.gwt.client.ui.workspace.tableview.argument.elements.TextElementArgument;
import lasad.gwt.client.ui.workspace.tableview.argument.elements.TranscriptElementArgument;
import lasad.gwt.client.ui.workspace.tableview.argument.elements.UrlElementArgument;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractRatingElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTextElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTranscriptElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractUrlElement;

public class RelatedRelationArgument extends AbstractRelatedRelation {
	
	private final ActionFactory actionFactory = ActionFactory.getInstance();
	private final LASADActionSender actionSender = LASADActionSender.getInstance();
	
	private MVCViewSession session;

	public RelatedRelationArgument(ElementInfo info, TableCellTypeEnum type) {
		super(info, type);
	}

	@Override
	protected void sendRemoveElementToServer(String mapID, int elementID) {
		actionSender.sendActionPackage(actionFactory.removeElement(mapID, elementID));
	}

	@Override
	public MVCViewSession getSession() {
		return session;
	}

	@Override
	public void setSession(AbstractMVCViewSession session) {
		this.session = (MVCViewSession)session;
	}

	@Override
	protected AbstractRelatedRelationDialog getRelatedRelationDialog(
			Vector<AbstractUnspecifiedElementModel> models,
			AbstractMVCViewSession session) {
		return new RelatedRelationDialogArgument(models, session);
	}

	@Override
	protected AbstractRatingElement getRatingElement(ElementInfo info) {
		return new RatingElementArgument(info);
	}

	@Override
	protected AbstractTextElement getTextElement(ElementInfo info) {
		return new TextElementArgument(info);
	}

	@Override
	protected AbstractTranscriptElement getTranscriptElement(
			ElementInfo childInfo) {
		return new TranscriptElementArgument(childInfo);
	}

	@Override
	protected AbstractUrlElement getUrlElement(ElementInfo childInfo) {
		return new UrlElementArgument(childInfo);
	}

}
