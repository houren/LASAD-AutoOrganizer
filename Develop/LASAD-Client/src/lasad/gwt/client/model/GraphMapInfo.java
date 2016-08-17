package lasad.gwt.client.model;

import java.util.LinkedHashMap;
import java.util.Map;

import lasad.gwt.client.ui.workspace.tutorial.TutorialConfig;

/**
 * One of the key features for generality. Here the configuration of each Drawing area is
 * stored.
 */
public class GraphMapInfo {
	private int maxUser;
	private String mapID;
	private boolean feedback = false, tutorial = false, userList = false, miniMap = false, chatSystem = false, trackCursor = false, groupPointer = false, selectionDetails = false, directLinkingDenied = false,sentenceopener=false,straightLink=false,onlyAuthorCanModify=false,commitTextByEnter=false,autoGrowTextArea=false, allowLinksToLinks=false; //autoOrganize=false,
	private String title, ontologyName, templateName, templateTitle, xmlOntology, xmlTemplate, description, xmlTranscriptConfiguration,sentenceOpenerConfig;
	private LinkedHashMap<Integer, String> transcriptLines;
	private TutorialConfig tutorialConfig = null;
	
	public GraphMapInfo(String mapID) {
		this.mapID = mapID;
	}

	public boolean isDirectLinkingDenied() {
		return directLinkingDenied;
	}

	public void setDirectLinkingDenied(boolean directLinkingDenied) {
		this.directLinkingDenied = directLinkingDenied;
	}

	public String getMapID() {
		return mapID;
	}

	public void setMapID(String mapID) {
		this.mapID = mapID;
	}

	public String getOntologyName() {
		return ontologyName;
	}

	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateTitle() {
		return templateTitle;
	}

	public void setTemplateTitle(String templateTitle) {
		this.templateTitle = templateTitle;
	}

	public String getXmlOntology() {
		return xmlOntology;
	}

	public void setXmlOntology(String xmlOntology) {
		this.xmlOntology = xmlOntology;
	}

	public String getXmlTemplate() {
		return xmlTemplate;
	}

	public void setXmlTemplate(String xmlTemplate) {
		this.xmlTemplate = xmlTemplate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getMaxUser() {
		return maxUser;
	}

	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}

	public boolean isChatSystem() {
		return chatSystem;
	}

	public void setChatSystem(boolean active) {
		this.chatSystem = active;
	}

	
	
	
	public void setSentenceOpener(boolean active) {
		this.sentenceopener = active;
	}
	
	public boolean isSentenceOpener() {
		return sentenceopener;
	}
	
	public void setSentenceOpenerConfig(String xml){
		
		this.sentenceOpenerConfig=xml;
	}
	
public String getSentenceOpenerConfig(){
		
		return sentenceOpenerConfig;
	}





	
	public boolean isUserList() {
		return userList;
	}

	public void setUserList(boolean active) {
		this.userList = active;
	}
	
	public boolean isSelectionDetails() {
		return selectionDetails;
	}

	public void setSelectionDetails(boolean active) {
		this.selectionDetails = active;
	}
	
	public boolean isMiniMap() {
		return miniMap;
	}		

	public void setMiniMap(boolean active) {
		this.miniMap = active;
	}

	public String getXmlTranscriptConfiguration() {
		return xmlTranscriptConfiguration;
	}

	public void setXmlTranscriptConfiguration(String xmlTranscriptConfiguration) {
		this.xmlTranscriptConfiguration = xmlTranscriptConfiguration;
	}

	public void setTranscriptLines(LinkedHashMap<Integer, String> lines) {
		transcriptLines = lines;
	}

	public LinkedHashMap<Integer, String> getTranscriptLines() {
		return transcriptLines;
	}

	// [ELEMENTTYPE][TYPE]Infos
	Map<String, Map<String, ElementInfo>> elementInfos = new LinkedHashMap<String, Map<String, ElementInfo>>();

	public void addElementInfo(ElementInfo info) {
		if (!elementInfos.containsKey(info.getElementType())) {
			elementInfos.put(info.getElementType(), new LinkedHashMap<String, ElementInfo>());
		}
		elementInfos.get(info.getElementType()).put(info.getElementID(), info);
	}

	public Map<String, ElementInfo> getElementsByType(String type) {
		return elementInfos.get(type);
	}

	public boolean isTutorial() {
		return tutorial;
	}

	public void setTutorial(boolean tutorial) {
		this.tutorial = tutorial;
	}

	public TutorialConfig getTutorialConfig() {
		return tutorialConfig;
	}

	public void setTutorialConfig(TutorialConfig tutorialConfig) {
		this.tutorialConfig = tutorialConfig;
	}

	public boolean isFeedback() {
		return feedback;
	}

	public void setFeedback(boolean feedback) {
		this.feedback = feedback;
	}

	public boolean isTrackCursor() {
		return trackCursor;
	}

	public void setTrackCursor(boolean trackCursor) {
		this.trackCursor = trackCursor;
	}

	public boolean isGroupPointer() {
		return groupPointer;
	}

	public void setGroupPointer(boolean groupPointer) {
		this.groupPointer = groupPointer;
	}

	public boolean isStraightLink() {
		return straightLink;
	}
	
	public void setStraightLink(boolean straightLink) {
		this.straightLink = straightLink;
	}
	
	public boolean isOnlyAuthorCanModify() {
		return onlyAuthorCanModify;
	}

	public void setOnlyAuthorCanModify(boolean onlyAuthorCanModify) {
		this.onlyAuthorCanModify = onlyAuthorCanModify;
	}
	
	public boolean isCommitTextByEnter() {
		return commitTextByEnter;
	}

	public void setCommitTextByEnter(boolean commitTextByEnter) {
		this.commitTextByEnter = commitTextByEnter;
	}

	// Next two methods added by Kevin Loughlin for autoOrganize support
	/*
	public boolean isAutoOrganize()
	{
		return autoOrganize;
	}

	public void setAutoOrganize(boolean autoOrganize)
	{
		this.autoOrganize = autoOrganize;
	}
	*/

	/**
	 * 
	 * @author BM
	 * @return if its autogrown or not
	 */
	public boolean isAutoGrowTextArea() {
		return autoGrowTextArea;
		
	}
	
	/**
	 * @author BM
	 * @return
	 */
	public void setAutoGrowTextArea(boolean autoGrowTextArea) {
		this.autoGrowTextArea = autoGrowTextArea;
	}

	public boolean isAllowLinksToLinks()
	{
		return allowLinksToLinks;
	}

	public void setAllowLinksToLinks(boolean allowLinksToLinks)
	{
		this.allowLinksToLinks = allowLinksToLinks;
	}
}
