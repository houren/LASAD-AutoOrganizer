package lasad.logging.commonformat.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anahuac
 * 
 */
public class CFObjectBasicData {
	private String id; // ID
	private String dataType; // TYPE
	private String nodeType; // ELEMENT-ID
	private String userID; // USERNAME
	private boolean directionChanged = false; // link direction changed?
	private List<String> sourceList = new ArrayList<String>();
	private List<String> targetList = new ArrayList<String>();

	public CFObjectBasicData() {
	}

	public CFObjectBasicData(String id, String dataType, String nodeType,
			String userID) {
		this.id = id;
		this.dataType = dataType;
		this.nodeType = nodeType;
		this.userID = userID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public boolean isDirectionChanged() {
		return directionChanged;
	}

	public void setDirectionChanged(boolean directionChanged) {
		this.directionChanged = directionChanged;
	}

	public List<String> getSourceList() {
		return sourceList;
	}

	public void add2SourceList(String source) {
		sourceList.add(source);
	}

	public List<String> getTargetList() {
		return targetList;
	}

	public void add2TargetList(String target) {
		targetList.add(target);
	}

	@Override
	public String toString() {
		return "CFObjectBasicData [dataType=" + dataType
				+ ", directionChanged=" + directionChanged + ", id=" + id
				+ ", nodeType=" + nodeType + ", sourceList=" + sourceList
				+ ", targetList=" + targetList + ", userID=" + userID + "]";
	}
	
	
	
}