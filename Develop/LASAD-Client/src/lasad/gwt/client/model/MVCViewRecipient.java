package lasad.gwt.client.model;

import lasad.shared.communication.objects.parameters.ParameterTypes;

public interface MVCViewRecipient {
	public AbstractUnspecifiedElementModel getConnectedModel();

	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vname);

	public void deleteModelConnection(AbstractUnspecifiedElementModel model);

	public boolean establishModelConnection(AbstractUnspecifiedElementModel model);
}