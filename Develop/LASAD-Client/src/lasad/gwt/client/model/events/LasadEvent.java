package lasad.gwt.client.model.events;

import lasad.gwt.client.model.AbstractMVCViewSession;

public class LasadEvent {

	private AbstractMVCViewSession session = null;
	private String type;
	private Object data = null;

	public LasadEvent() {};

	public LasadEvent(AbstractMVCViewSession session) {
		this.session = session;
	}

	public AbstractMVCViewSession getSession() {
		return session;
	}

	public void setSession(AbstractMVCViewSession session) {
		this.session = session;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}