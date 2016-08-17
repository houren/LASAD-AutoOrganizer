package lasad.gwt.client.ui.workspace.tableview;

import java.util.Vector;

import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.LayoutContainer;



public abstract class AbstractChildElement extends LayoutContainer implements MVCViewRecipient, FocusableInterface {

	// *************************************************************************************
	// Fields
	// *************************************************************************************

//	protected final ActionFactory actionFactory = ActionFactory.getInstance();
//	protected final LASADActionSender actionSender = LASADActionSender.getInstance();

	protected AbstractUnspecifiedElementModel model;
	protected ElementInfo info;

	protected AbstractMapTable argumentMapTable;

	protected boolean activeViewMode;

	protected boolean blocked;

	protected Vector<ParameterTypes> elementVars = new Vector<ParameterTypes>();

	// *************************************************************************************
	// Constructor
	// *************************************************************************************

	public AbstractChildElement(ElementInfo info) {

		this.info = info;

	}

	// *************************************************************************************
	// Methods
	// *************************************************************************************

	public abstract ChildElementTypeEnum getType();

	public abstract String getSummary();

	public abstract void setReadOnly(boolean readOnly);

	protected abstract void switchToViewMode();

	protected abstract void switchToEditMode();

	protected abstract String getVarValue(ParameterTypes vName);

	protected abstract void setVarValue(ParameterTypes vName, String value);

	public void setViewMode(boolean status) {

		if (activeViewMode == false && status == true) {

			switchToViewMode();
			activeViewMode = true;
		}

		if (activeViewMode == true && status == false) {

			switchToEditMode();
			activeViewMode = false;
		}

	}

	private void updateModel() {

		Vector<Object[]> values = new Vector<Object[]>();

		for (ParameterTypes vName : elementVars) {
			String value = getVarValue(vName);
			if (value != null && ((model.getValue(vName) != null && !model.getValue(vName).equals(value)) || model.getValue(vName) == null)) {
				// Value has changed
				Object[] tmp = { vName, value };
				values.add(tmp);
			}
		}

		if (values.size() > 0) {

			String mapId = model.getValue(ParameterTypes.MapId);

//			actionSender.sendActionPackage(actionFactory.updateElementWithMultipleValues(mapId, model.getId(), values));
			sendUpdateElementWithMultipleValuesToServer(mapId, model.getId(), values);
		}
	}
	
	protected abstract void sendUpdateElementWithMultipleValuesToServer(String mapID, int elementID, Vector<Object[]> values);

	// *************************************************************************************
	// methods of Inteface MVCViewRecipient
	// *************************************************************************************

	@Override
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vname) {

		if (elementVars.contains(vname)) {

			setVarValue(vname, model.getValue(vname));
		}
	}

	@Override
	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {

	// removeFromParent();
	}

	@Override
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {

		if (this.model == null) {
			this.model = model;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public AbstractUnspecifiedElementModel getConnectedModel() {

		return model;
	}

	// *************************************************************************************
	// methods of Inteface FocusableElementInterface
	// *************************************************************************************

	@Override
	public FocusableInterface getFocusParent() {

		Vector<AbstractUnspecifiedElementModel> parents = model.getParents();

		if (parents.size() == 1) {

			AbstractUnspecifiedElementModel parent = parents.firstElement();

			MVCViewSession viewSession = (MVCViewSession) argumentMapTable.getMyViewSession();
			MVCViewRecipient recipient = viewSession.getMVCViewRecipientsByModel(parent).firstElement();
//			MVCViewRecipient recipient = argumentMapTable.getMyViewSession().getMVCViewRecipientsByModel(parent).firstElement();
			if (recipient instanceof FocusableInterface) {

				return (FocusableInterface) recipient;
			}
		}

		return null;
	}

	@Override
	public void setElementFocus(boolean focus) {

		// Focus = 1 -> Switch to Editmode Focus = 0 -> Switch to Viewmode

		if (focus) {
			// Get Focus, switch to EditMode
			if (activeViewMode) {
				setViewMode(false);
			}
			// else nothing to do

		} else {
			// Lost Focus, switch to ViewMode
			// And Update Values
			if (!activeViewMode) {
				setViewMode(true);
				updateModel();
			}
		}
	}

	// *************************************************************************************
	// getter & setter
	// *************************************************************************************

	/**
	 * @return the argumentMapTable
	 */
	public AbstractMapTable getArgumentMapTable() {
		return argumentMapTable;
	}

	/**
	 * @param argumentMapTable
	 *            the argumentMapTable to set
	 */
	public void setArgumentMapTable(AbstractMapTable argumentMapTable) {
		this.argumentMapTable = argumentMapTable;
	}

	public boolean isActiveViewMode() {

		return activeViewMode;
	}

	/**
	 * @param activeViewMode
	 *            the activeViewMode to set
	 */
	public void setActiveViewMode(boolean activeViewMode) {
		this.activeViewMode = activeViewMode;
	}

	public boolean isBlocked() {

		return blocked;
	}

	public void setBlocked(boolean blocked) {

		this.blocked = blocked;
	}

}
