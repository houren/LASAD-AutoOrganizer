package lasad.logging.commonformat.util;

import java.util.List;

import lasad.logging.commonformat.CfXmppWriter;
import lasad.logging.commonformat.util.jaxb.Action;
import lasad.logging.commonformat.util.jaxb.Actiontype;
import lasad.logging.commonformat.util.jaxb.Object;
import lasad.logging.commonformat.util.jaxb.ObjectDef;
import lasad.logging.commonformat.util.jaxb.Objects;
import lasad.logging.commonformat.util.jaxb.Preamble;
import lasad.logging.commonformat.util.jaxb.Properties;
import lasad.logging.commonformat.util.jaxb.Property;
import lasad.logging.commonformat.util.jaxb.User;

public class CF2StringUtil {
	public static final String NEXTLINE = "\n";
	public static final String IDENT = "  ";
	
	/*
	 * This is the sequence required to create an xml file compatible with CF descriptor
	 * getFileHeader()
	 * preamble2String(preamble)
	 * getOpenActions()
	 * action2String(action)
	 * getFileTail()
	 * 
	 */

	public static String getFileHeader(){
		StringBuffer result = new StringBuffer();
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + NEXTLINE);
		result.append("<interactiondata>");	// + NEXTLINE
		return result.toString();
	}
	
	public static String getOpenActions(){
		StringBuffer result = new StringBuffer();
		result.append("<actions>");	// + NEXTLINE
		return result.toString();
	}
	
	public static String getCloseActions(){
		StringBuffer result = new StringBuffer();
		result.append("</actions>" + NEXTLINE);
		return result.toString();
	}
	
	public static String getFileTail(){
		StringBuffer result = new StringBuffer();
		result.append(getCloseActions());
		result.append("<interactiondata>");	// + NEXTLINE
		return result.toString();
	}
	
	public String action2String(Action action){
		StringBuffer result = new StringBuffer();
		
		result.append("<action");
		result.append(" time=" + "\"" + action.getTime() + "\">" + NEXTLINE);
		
		result.append(actionType2String(action.getActiontype()));
		result.append(user2String(action.getUser()));
		result.append(object2String(action.getObject()));
		
		result.append("</action>");	// + NEXTLINE
		
		//System.out.println("###" + result.toString());
		return result.toString();
	}
	
	private String actionType2String(Actiontype actionType){
		StringBuffer result = new StringBuffer();
		
		result.append("<actionType");
		result.append(" logged=" + "\"" + CfXmppWriter.DEPLOYMENT_VERSION + "\"");
		result.append(" type=" + "\"" + actionType.getType() + "\"");
		result.append(" classification=" + "\"" + actionType.getClassification() + "\"/>" + NEXTLINE);
		
		return result.toString();
	}
	
	private String user2String(List<User> userList){
		StringBuffer result = new StringBuffer();
		User user = userList.get(0);
		result.append("<user");
		result.append(" role=" + "\"" + user.getRole() + "\"");
		result.append(" id=" + "\"" + user.getId() + "\"/>" + NEXTLINE);
		
		return result.toString();
	}
	
	private String object2String(Object obj){
		StringBuffer result = new StringBuffer();
		
		if(obj != null){
			result.append("<object");
			result.append(" type=" + "\"" + obj.getType() + "\"");
			result.append(" id=" + "\"" + obj.getId() + "\">" + NEXTLINE);
			
			List<Object> childrenList = obj.getObject();
			if (childrenList != null){
				for(Object child : childrenList){
					result.append(object2String(child));
				}
			}
			result.append(properties2String(obj.getProperties()));
			
			result.append("</object>" + NEXTLINE);
		}
		
		return result.toString();
	}
	
	public String preamble2String(Preamble preamble){
		StringBuffer result = new StringBuffer();
		result.append("<preamble>" + NEXTLINE);
		result.append("<users/>" + NEXTLINE);
		result.append("<groups/>" + NEXTLINE);
		
		result.append(objects2String(preamble.getObjects()));
		
		result.append("<roles/>" + NEXTLINE);
		result.append("<conditions/>" + NEXTLINE);
		result.append("<time_range/>" + NEXTLINE);
		result.append("</preamble>");	// + NEXTLINE
		
		System.out.println("###" + result.toString());
		return result.toString();
	}
	
	private String objects2String(Objects objects){
		StringBuffer result = new StringBuffer();
		List<ObjectDef> objDefList = objects.getObjectDef();
		if(objDefList != null && objDefList.size() >0){
			result.append("<objects>" + NEXTLINE);
			for(ObjectDef objDef : objDefList){
				result.append(objectDef2String(objDef));
			}
			result.append("</objects>" + NEXTLINE);
		}
		else{
			result.append("<objects/>" + NEXTLINE);
		}
		
		return result.toString();
	}
	
	private String objectDef2String(ObjectDef objDef){
		StringBuffer result = new StringBuffer();
		result.append("<object_def");
		result.append(" id=" + "\"" + objDef.getId() + "\"");
		result.append(" type=" + "\"" + objDef.getType() + "\">" + NEXTLINE);
		result.append(properties2String(objDef.getProperties()));
		result.append("</object_def>" + NEXTLINE);
		return result.toString();
	}
	
	private String properties2String(Properties properties){
		StringBuffer result = new StringBuffer();
		boolean flagError = false;
		
		if(properties != null){
			List<Property> propsList = properties.getProperty();
			if(propsList != null && propsList.size() > 0){
				result.append("<properties>" + NEXTLINE);
				for(Property prop : propsList){
					result.append("<property");
					result.append(" name=" + "\"" + prop.getName() + "\"");
					if(prop.getValue() != null){
						result.append(" value=" + "\"" + prop.getValue() + "\"");
						//result.append(" value=" + "\"" + prop.getValue() + "\"");
					}
					result.append("/>" + NEXTLINE);
					//result.append(" name=" + "\"" + prop.getName() + "\"/>" + NEXTLINE);
				}
				result.append("</properties>" + NEXTLINE);
			}
			else{
				flagError = true;
			}
		}
		else{
			flagError = true;
		}
		
		if(flagError){
			result.append("<properties/>" + NEXTLINE);
		}
		return result.toString();
	}
}
