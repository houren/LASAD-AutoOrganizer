package lasad.gwt.client.ui.workspace.tableview;

import java.util.Set;

import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class RelationInfo {

	// *************************************************************************************
	// Fields
	// *************************************************************************************

	private AbstractUnspecifiedElementModel fromModel;

	private Set<AbstractUnspecifiedElementModel> toModels;
	
	private AbstractUnspecifiedElementModel selectedToModel;

	// *************************************************************************************
	// getter & setter
	// *************************************************************************************

	public void selectToModel(String id) {
		
		for (AbstractUnspecifiedElementModel m: toModels) {
			
			String str = m.getValue(ParameterTypes.RootElementId);
			
			if (str.equals(id)) {
				
				selectedToModel = m;
				
				break;
			}
		}
		
	}
	
	
	public AbstractUnspecifiedElementModel getFromModel() {
		return fromModel;
	}

	public void setFromModel(AbstractUnspecifiedElementModel fromModel) {
		this.fromModel = fromModel;
	}

	public Set<AbstractUnspecifiedElementModel> getToModels() {
		return toModels;
	}

	public void setToModels(Set<AbstractUnspecifiedElementModel> toModels) {
		this.toModels = toModels;
	}


	public AbstractUnspecifiedElementModel getSelectedToModel() {
		return selectedToModel;
	}

	public void setSelectedToModel(AbstractUnspecifiedElementModel selectedToModel) {
		this.selectedToModel = selectedToModel;
	}


}
