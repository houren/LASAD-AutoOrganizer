package lasad.gwt.client.ui.workspace.argumentmap;

import lasad.gwt.client.ui.workspace.tabs.map.ImportMapFormPanel;

import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.Element;

public class ImportMapDialog extends Window
{

	public ImportMapDialog()
	{
	}

	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		this.setAutoHeight(true);
		this.setWidth(500);
		this.setAutoHide(true);
		this.add(new ImportMapFormPanel(this));
	}
}

	