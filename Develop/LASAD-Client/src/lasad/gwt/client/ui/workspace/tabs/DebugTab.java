package lasad.gwt.client.ui.workspace.tabs;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.logger.LoggerInterface;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * Implements the admin panel which is ONLY for debugging
 */
public class DebugTab extends TabItem implements LoggerInterface {

	private ContentPanel panel;
	private TextArea log;

	public DebugTab() {
		this.setText("DEBUG-Panel");
		this.setClosable(true);
		this.addStyleName("pad-text");
		this.setLayout(new FitLayout());

		panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setBorders(false);

		this.add(panel);

		log = new TextArea();
		log.setSize("100%", "100%");
		
		Logger.addLogListener(this);

		panel.add(log);

		this.layout();
		panel.layout();
	}

	@Override
	public void log(String logText) {
		log.setValue(log.getValue() + "\n" + logText);
	}

	@Override
	public void logErr(String logText) {
		log.setValue(log.getValue() + "\n" + "[ERROR] " + logText);
	}
}