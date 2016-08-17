package lasad.gwt.client.ui.workspace.graphmap.elements;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/** 
 * Dialogue to upload a file to the server. The file will not be saved persistently, but available for reading
 * @author Marcel Bergmann
 *
 */
public class GXTFileUploadDialog extends LayoutContainer {

	public GXTFileUploadDialog(Element parent, int index) {

		setStyleAttribute("margin", "10px");

		final FormPanel panel = new FormPanel();
		panel.setHeading("File Upload Example");
		panel.setFrame(true);
		panel.setAction(GWT.getModuleBaseURL() + "fileupload");
		panel.setEncoding(Encoding.MULTIPART);
		panel.setMethod(Method.POST);
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		panel.setWidth(350);
		panel.setHeight(340);

		TextField<String> name = new TextField<String>();
		name.setFieldLabel("Name");
		panel.add(name);

		FileUploadField file = new FileUploadField();
		file.setAllowBlank(false);
		file.setFieldLabel("File");
		panel.add(file);

		panel.addListener(Events.Submit, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// HANDLE SUBMIT
				MessageBox.info("Action", "Submit handled!", null);

			}
		});

		// List<Stock> list = TestData.getStocks();
		// final ListStore<Stock> store = new ListStore<Stock>();
		// store.add(list);
		Button btn = new Button("Submit");
		btn.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (!panel.isValid()) {
					return;
				}

				panel.submit();
				MessageBox.info("Action", "Your file was uploaded", null);
			}
		});
		panel.addButton(btn);

		add(panel);
		this.layout();
	}
}
