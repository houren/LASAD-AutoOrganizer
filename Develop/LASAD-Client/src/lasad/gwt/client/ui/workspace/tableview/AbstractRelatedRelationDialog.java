package lasad.gwt.client.ui.workspace.tableview;

import java.util.Vector;

import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.MVCViewRecipient;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;

public abstract class AbstractRelatedRelationDialog extends Dialog{

	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private Vector<AbstractUnspecifiedElementModel> models;
	
//	private MVCViewSession session;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public AbstractRelatedRelationDialog(Vector<AbstractUnspecifiedElementModel> models, AbstractMVCViewSession session) {
		
		this.models = models;
//		this.session = session;
		setSession(session);
		
		setHeading("Related relations ...");
		setHideOnButtonClick(true);
		setButtons(Dialog.OK);
		setModal(true);
		setClosable(false);
		setResizable(false);
		 
		setLayout(new AccordionLayout());
		addComponents();
		
		setSize(350, 350);
		show();
	}
	
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	private void addComponents() {
		
		for (AbstractUnspecifiedElementModel model: models) {
			
			Vector<MVCViewRecipient> recipients = getSession().getMVCViewRecipientsByModel(model);
			
			for (MVCViewRecipient recipient: recipients) {
				
				if (recipient instanceof AbstractRelatedRelation) {
					
					AbstractRelatedRelation relatedRelation = (AbstractRelatedRelation)recipient;
					
					if (relatedRelation.getType() == TableCellTypeEnum.RELEATION) {
						
						add(relatedRelation);
					}
					
				}
				
			}
			
		}
		
	}
	
	public void removeComponent(AbstractRelatedRelation relation) {
		
		remove(relation);
		
		if (getItemCount() == 0) {
			hide();
		}
		
	}
	
	protected abstract AbstractMVCViewSession getSession();

	protected abstract void setSession(AbstractMVCViewSession session);
	
}
