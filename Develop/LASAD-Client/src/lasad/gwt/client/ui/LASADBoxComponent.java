package lasad.gwt.client.ui;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.BoxComponent;

public class LASADBoxComponent extends BoxComponent {
	protected boolean commitTextByEnter = false;
	protected boolean onlyAuthorCanModify = false;
	protected AbstractUnspecifiedElementModel connectedModel;
	protected ElementInfo config;
	
	/**
	 * Checks whether only author/owner can modify.
	 * if 'onlyauthorcanmodify' flag in template is set to true, then checks.
	 * Otherwise, modifications to elements are allowed. 
	 * @return
	 */
	public boolean isModificationAllowed() {
		String currentUser = LASAD_Client.getInstance().getUsername();
		String author = connectedModel.getAuthor();
		
		boolean result = true;
		// get onlyAuthorCanModify flag from ontology specification
		String ontologyFlag = this.config.getElementOption(ParameterTypes.OnlyAuthorCanModify);
		
		// check with the flag specified in the template file 
		if (ontologyFlag == null || ontologyFlag.equalsIgnoreCase("true")) {
			result = (this.onlyAuthorCanModify && !currentUser.equalsIgnoreCase(author)) ? false : true;
		}
		
		return result;
	}
	
	
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {
		if (connectedModel == null) {
			connectedModel = model;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isOnlyAuthorCanModify() {
		return onlyAuthorCanModify;
	}

	public void setOnlyAuthorCanModify(boolean onlyAuthorCanModify) {
		this.onlyAuthorCanModify = onlyAuthorCanModify;
	}
	
	public AbstractUnspecifiedElementModel getConnectedModel() {
		return connectedModel;
	}
	
	public void setConnectedModel(AbstractUnspecifiedElementModel connectedModel) {
		this.connectedModel = connectedModel;
	}
	
	public boolean isCommitTextByEnter() {
		return commitTextByEnter;
	}

	public void setCommitTextByEnter(boolean commitTextByEnter) {
		this.commitTextByEnter = commitTextByEnter;
	}
}
