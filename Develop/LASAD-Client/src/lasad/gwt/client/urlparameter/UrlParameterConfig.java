package lasad.gwt.client.urlparameter;

import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class UrlParameterConfig {
	
	private boolean autoLogin=false;
	private boolean isStandAlone = false;
	private String username;
	private String password;
	private boolean isPasswordEncrypted;
	private String mapId;
	private String groupId;
	private String challengeId;
	private String challengeName;
	private String token;

	

	public UrlParameterConfig(){
		String autoLoginStr = com.google.gwt.user.client.Window.Location.getParameter("autologin");
		autoLogin = Boolean.parseBoolean(autoLoginStr);
		if (autoLogin){
			username = com.google.gwt.user.client.Window.Location.getParameter("user");
			password = com.google.gwt.user.client.Window.Location.getParameter("pw");
			String isPasswordEncryptedStr = com.google.gwt.user.client.Window.Location.getParameter("pwEncrypted");
			isPasswordEncrypted = Boolean.parseBoolean(isPasswordEncryptedStr);
			token = com.google.gwt.user.client.Window.Location.getParameter("token");
		}
		
		mapId = com.google.gwt.user.client.Window.Location.getParameter("mapId");
		groupId = com.google.gwt.user.client.Window.Location.getParameter("groupId");
		challengeId = com.google.gwt.user.client.Window.Location.getParameter("challengeId");
		challengeName = com.google.gwt.user.client.Window.Location.getParameter("challengeName");
		String isStandAloneStr = com.google.gwt.user.client.Window.Location.getParameter("isStandAlone");
		isStandAlone = Boolean.parseBoolean(isStandAloneStr);
		
		System.out.println("[lasad.gwt.client.LASAD_CLient][checkAndParseUrlParams] user=" + username 
				+ " - pw=" + password  + " - mapid=" + mapId + " - groupId=" +groupId 
				+ " - challengeId=" + challengeId + " - challengeName=" + challengeName 
				+ " - pwEncrypted=" + isPasswordEncrypted+ " - token=" + token
				);
	}
	
	//mutator
	public void addParamsToActionPackage(ActionPackage ap) {
		if (groupId != null || challengeId != null || challengeName != null || token != null){
			for (Action action : ap.getActions()){
				if (groupId != null){
					action.addParameter(ParameterTypes.groupId, groupId);
				}
				if (challengeId != null){
					action.addParameter(ParameterTypes.challengeId, challengeId);
				}
				if (challengeName != null){
					action.addParameter(ParameterTypes.challengeName, challengeName);
				}
				if (token != null){
					action.addParameter(ParameterTypes.token, token);
				}
			}
		}
	}


	public boolean isAutoLogin() {
		return autoLogin;
	}

	public String getUsername() {
		return username;
	}


	public String getPassword() {
		return password;
	}


	public String getMapId() {
		return mapId;
	}

	public boolean isPasswordEncrypted() {
		return isPasswordEncrypted;
	}
	
	public boolean isStandAlone(){
		return isStandAlone;
	}

}
