package lasad.gwt.client.ui.workspace.tableview.argument;

import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.workspace.tableview.AbstractRelatedRelationDialog;
import lasad.gwt.client.ui.workspace.tableview.AbstractTableCell;
import lasad.gwt.client.ui.workspace.tableview.TableCellTypeEnum;
import lasad.gwt.client.ui.workspace.tableview.argument.elements.RatingElementArgument;
import lasad.gwt.client.ui.workspace.tableview.argument.elements.TextElementArgument;
import lasad.gwt.client.ui.workspace.tableview.argument.elements.TranscriptElementArgument;
import lasad.gwt.client.ui.workspace.tableview.argument.elements.UrlElementArgument;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractRatingElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTextElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTranscriptElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractUrlElement;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.ActionPackage;

public class TableCellArgument extends AbstractTableCell {
	
	private final ActionFactory actionFactory = ActionFactory.getInstance();
	private final LASADActionSender actionSender = LASADActionSender.getInstance();
	
	private MVCViewSession session;

	public TableCellArgument(ElementInfo info, TableCellTypeEnum type) {
		super(info, type);
	}

	@Override
	protected void sendUpdateTranscriptLinkToServer(String mapID,
			int elementID, TranscriptLinkData tData) {
		actionSender.sendActionPackage(actionFactory.updateTranscriptLink(mapID, elementID, tData));
	}

	@Override
	protected void sendCreateTranscriptLinkToServer(String mapID, String boxID,
			TranscriptLinkData tData) {
		actionSender.sendActionPackage(new ActionPackage().addAction(actionFactory.createTranscriptLink(mapID, boxID, tData)));
	}

	@Override
	protected void sendAddElementToServer(String mapID, int boxID,
			String elementType, String elementID) {
		actionSender.sendActionPackage(actionFactory.addElement(mapID, boxID, elementType, elementID));
	}

	@Override
	protected void sendRemoveElementToServer(String mapId, int elementId) {
		actionSender.sendActionPackage(actionFactory.removeElement(mapId, elementId));
	}

	@Override
	protected AbstractRelatedRelationDialog getRelatedRelationDialog(
			Vector<AbstractUnspecifiedElementModel> models,
			AbstractMVCViewSession session) {
		return new RelatedRelationDialogArgument(models, session);
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
