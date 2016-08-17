package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Data structure to keep track of fresh Id requests for agents and/or patterns
 * 
 * @author anahuacv
 * 
 */
public class FreshIdRequestQueue {
	//<requestId, RequestInfo>
	Map<Integer, RequestInfo>  requestMap = new HashMap<Integer, RequestInfo>();
	
	
	public void add(int requestId, RequestInfo requestInfo){
		requestMap.put(requestId, requestInfo);
	}
	public RequestInfo remove(int requestId){
		return requestMap.remove(requestId);
	}
	public RequestInfo get(int requestId){
		return requestMap.get(requestId);
	}
	public boolean containsKey(int requestId){
		return requestMap.containsKey(requestId);
	}
}
