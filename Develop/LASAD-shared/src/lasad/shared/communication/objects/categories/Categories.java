package lasad.shared.communication.objects.categories;
public enum Categories {




	Auth("Auth"),
	Authoring("Authoring"),
	Error("Error"), 
	Info("Info"),
	Management("Management"), 
	Map("Map"), 
	Notify("Notify"),
	Replay("Replay"),
	Session("Session"), 
	UserEvent("UserEvent"),
	None("None"), 
	Communication("Communication"),
	Feedback("Feedback"), 
	Questionnaire("Questionnaire"), 
	File("File"),
	Heartbeat("Heartbeat"),
	FeedbackAuthoring("FeedbackAuthoring"),
	CenterMap("CenterMap");
	
	private String oldCategories;

	public String getOldCategories() {
		return oldCategories;
	}
	
	Categories(String oldString){
		oldCategories = oldString;
	}
}




