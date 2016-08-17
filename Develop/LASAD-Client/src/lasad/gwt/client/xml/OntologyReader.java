package lasad.gwt.client.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.GraphMapInfo;
import lasad.gwt.client.ui.workspace.questionnaire.QuestionConfig;
import lasad.gwt.client.ui.workspace.questionnaire.QuestionnaireStepConfig;
import lasad.gwt.client.ui.workspace.tutorial.TutorialConfig;
import lasad.gwt.client.ui.workspace.tutorial.TutorialStepConfig;
import lasad.shared.communication.objects.parameters.OntologyParameterTypeConverter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;



/**
 * This class is used to parse the XML definition of the ontology (elements +
 * links) from the server to get the information needed to create elements
 * (boxes + links)
 * 
 */

public class OntologyReader {
	//public static String chatPanelInfo="";
	//public static Boolean enableSentenceOpener=false;
	public static GraphMapInfo buildTemplateInfosFromXML(GraphMapInfo mapInfo, String xml) {
		Document xmlDoc = XMLParser.parse(xml);

		// The <ontology> element
		Node template = xmlDoc.getLastChild();

		if (template.getNodeName().equalsIgnoreCase("maptemplate")) {
			// work on ChildNodes
			for (int i = 0; i < template.getChildNodes().getLength(); i++) {
				Node node = template.getChildNodes().item(i);
				if (node.getNodeName().equalsIgnoreCase("mapdetails")) {
					// read mapDetails
					for (int d = 0; d < node.getChildNodes().getLength(); d++) {
						Node mapDetailsNode = node.getChildNodes().item(d);
						if (mapDetailsNode.getNodeName().equalsIgnoreCase("description")) {
							if (mapDetailsNode.hasChildNodes()) {
								mapInfo.setDescription(mapDetailsNode.getFirstChild().getNodeValue());
							}
						} else if (mapDetailsNode.getNodeName().equalsIgnoreCase("options")) {
							mapInfo.setMaxUser(Integer.parseInt(getAttribute(mapDetailsNode, "maxuser")));
							
							
							//mapInfo.setChatSystem(((getAttribute(mapDetailsNode, "chatsystem").equalsIgnoreCase("true")) ? true : false));
							
							// Modified by Kevin Loughlin to add autoorganize
							/*
							if(getAttribute(mapDetailsNode, ParameterTypes.AutoOrganize.name()).equalsIgnoreCase("true")){
								mapInfo.setAutoOrganize(true);
							}
							*/

							// MODIFIED BY BM!!!
							// get the autogrowstatement from the XML
							if(getAttribute(mapDetailsNode, ParameterTypes.AutoGrowTextArea.name()).equalsIgnoreCase("true")){
								mapInfo.setAutoGrowTextArea(true);
							}
							//MODIFY END!
							
							if(getAttribute(mapDetailsNode, "chatsystem").equalsIgnoreCase("true")){
								
								mapInfo.setChatSystem(true);
								if(getAttribute(mapDetailsNode, "sentenceopener").equalsIgnoreCase("true")){
								
									mapInfo.setSentenceOpenerConfig(xml);
									//chatPanelInfo=xml;
								mapInfo.setSentenceOpener(true);
								}
								else{
									//enableSentenceOpener=false;
									//chatPanelInfo="";
									
									mapInfo.setSentenceOpenerConfig("");
									//chatPanelInfo=xml;
								mapInfo.setSentenceOpener(false);
								}
								
							}
							else {
								mapInfo.setChatSystem(false);
								//enableSentenceOpener=false;
								
								mapInfo.setSentenceOpener(false);
								
							}
							
							if(getAttribute(mapDetailsNode, "direct-linking-denied") != null) {
								mapInfo.setDirectLinkingDenied(((getAttribute(mapDetailsNode, "direct-linking-denied").equalsIgnoreCase("true")) ? true : false));
							}
							mapInfo.setUserList(((getAttribute(mapDetailsNode, "listofusers").equalsIgnoreCase("true")) ? true : false));
							mapInfo.setMiniMap(((getAttribute(mapDetailsNode, "minimap").equalsIgnoreCase("true")) ? true : false));
							mapInfo.setFeedback(((getAttribute(mapDetailsNode, "feedback").equalsIgnoreCase("true")) ? true : false));
							mapInfo.setTrackCursor(((getAttribute(mapDetailsNode, "track-cursor").equalsIgnoreCase("true")) ? true : false));
							mapInfo.setGroupPointer(((getAttribute(mapDetailsNode, "group-pointer").equalsIgnoreCase("true")) ? true : false));
							mapInfo.setSelectionDetails(((getAttribute(mapDetailsNode, "selection-details").equalsIgnoreCase("true")) ? true : false));
							mapInfo.setStraightLink(((getAttribute(mapDetailsNode, "straightlink").equalsIgnoreCase("true")) ? true : false));
							mapInfo.setOnlyAuthorCanModify(((getAttribute(mapDetailsNode, "onlyauthorcanmodify").equalsIgnoreCase("true")) ? true : false));
							mapInfo.setCommitTextByEnter(((getAttribute(mapDetailsNode, "committextbyenter").equalsIgnoreCase("true")) ? true : false));
							mapInfo.setAllowLinksToLinks(((getAttribute(mapDetailsNode, "allowlinkstolinks").equalsIgnoreCase("true")) ? true : false));
						}
					}
				} else if (node.getNodeName().equalsIgnoreCase("transcript")) {
					// Transcript Configuration
					for (int d = 0; d < node.getChildNodes().getLength(); d++) {
						Node transcriptDetailsNode = node.getChildNodes().item(d);
						if (transcriptDetailsNode.getNodeName().equalsIgnoreCase("lines")) {
							// Work on TranscriptLines
							LinkedHashMap<Integer, String> transcriptLines = new LinkedHashMap<Integer, String>();

							for (int l = 0; l < transcriptDetailsNode.getChildNodes().getLength(); l++) {
								Node transcriptLineNode = transcriptDetailsNode.getChildNodes().item(l);

								if (transcriptLineNode.getNodeName().equalsIgnoreCase("line")) {
									// Work on Line
									transcriptLines.put(getIntAttribut(transcriptLineNode, "number"), getAttribute(transcriptLineNode, "text"));
								}
							}

							mapInfo.setTranscriptLines(transcriptLines);
							break;
						}
					}
					mapInfo.setXmlTranscriptConfiguration(node.toString());

				} else if (node.getNodeName().equalsIgnoreCase("tutorial")) {
					// Tutorial Configuration
					mapInfo.setTutorialConfig(buildTutorialConfigFromElementNode(node));
				}
			}
		}
		return mapInfo;
	}

	public static GraphMapInfo buildOntologyInfosFromXML(GraphMapInfo mapInfo, String xml) {
		Document xmlDoc = XMLParser.parse(xml);

		// The <ontology> element
		Node ontology = xmlDoc.getLastChild();

		if (ontology.getNodeName().equalsIgnoreCase("ontology")) {

			// work on ChildNodes
			for (int i = 0; i < ontology.getChildNodes().getLength(); i++) {
				Node node = ontology.getChildNodes().item(i);

				if (node.getNodeName().equalsIgnoreCase("elements")) {

					// Work on Ontology Elements

					for (int d = 0; d < node.getChildNodes().getLength(); d++) {
						Node elementNode = node.getChildNodes().item(d);

						if (elementNode.getNodeName().equalsIgnoreCase("element")) {
							ElementInfo elementInfo = workOnElementNode(elementNode);
							mapInfo.addElementInfo(elementInfo);
						}
					}
				}
			}
		}
		return mapInfo;
	}

	public static ElementInfo workOnElementNode(Node elementNode) {
		ElementInfo info = new ElementInfo();
		if (elementNode.getNodeName().equalsIgnoreCase("element")) {
			info.setElementID(getAttribute(elementNode, "elementid"));
			info.setElementType(getAttribute(elementNode, "elementtype"));
			info.setQuantity(getIntAttribut(elementNode, "quantity"));
			info.setMinQuantity(getIntAttribut(elementNode, "minquantity"));
			info.setMaxQuantity(getIntAttribut(elementNode, "maxquantity"));

			// work on childNodes
			for (int i = 0; i < elementNode.getChildNodes().getLength(); i++) {
				Node node = elementNode.getChildNodes().item(i);

				if (node.getNodeName().equalsIgnoreCase("elementoptions")) {
					for (int d = 0; d < node.getAttributes().getLength(); d++) {
						Node att = node.getAttributes().item(d);
						
						//MB: This is needed because of the fact that newly created ontologies 
						//use other attribute tags than the old ones due to the switch from strings to parameter types enumeration
						ParameterTypes paraType;
						if ((paraType = ParameterTypes.fromString(att.getNodeName())) == null) {
							try {
								paraType = OntologyParameterTypeConverter.getParameterByOntologyValue(att.getNodeName());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						info.addElementOption(paraType, att.getNodeValue());
					}
				}
				
				// Modified by Ugur:  Getting radio button items and their text for radiobutton element
				else if(node.getNodeName().equalsIgnoreCase("radiobuttonitems"))
				   {
					
					//Map<String, String> uiOptions = new HashMap<String, String>();
				ArrayList <Map<String, String>> radiobuttons= new ArrayList <Map<String, String>>();
				
					  for (int g = 0; g < node.getChildNodes().getLength(); g++) {
							Node radioNode = node.getChildNodes().item(g);

							
							
							if (radioNode.getNodeName().equalsIgnoreCase("radiobuttonitem")) {
								
								Map<String, String> radionbutton = new HashMap<String, String>();
				
									 Element item = (Element) radioNode;
									
									 radionbutton.put("id",item.getAttribute("id"));
									 radionbutton.put("label",item.getAttribute("label"));
									 radionbutton.put("checked",item.getAttribute("checked"));
									 
									 radiobuttons.add(radionbutton); 
								 
							    
							}
						    }
					  
					  
					  info.addRadioElementOption(radiobuttons);
					
			   
				   }
				
				else if (node.getNodeName().equalsIgnoreCase("uisettings")) {
					for (int d = 0; d < node.getAttributes().getLength(); d++) {
						Node att = node.getAttributes().item(d);
						
						//MB: This is needed because of the fact that newly created ontologies 
						//use other attribute tags than the old ones due to the switch from strings to parameter types enumeration
						ParameterTypes paraType;
						if ((paraType = ParameterTypes.fromString(att.getNodeName())) == null) {
							try {
								paraType = OntologyParameterTypeConverter.getParameterByOntologyValue(att.getNodeName());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						info.addUiOption(paraType, att.getNodeValue());
					}
				} else if (node.getNodeName().equalsIgnoreCase("childelements")) {
					// work on child elements
					for (int d = 0; d < node.getChildNodes().getLength(); d++) {
						if (node.getChildNodes().item(d).getNodeName().equalsIgnoreCase("element"))
							info.addChildElement(workOnElementNode(node.getChildNodes().item(d)));
					}
				}
			}
		}
		return info;
	}

	public static String getAttribute(Node node, String name) {
		if (node.getAttributes().getNamedItem(name) != null) {
			return node.getAttributes().getNamedItem(name).getNodeValue();
		}
		return "";
	}

	public static int getIntAttribut(Node node, String name) {
		String value = getAttribute(node, name);
		if (value.equalsIgnoreCase("")) {
			return 0;
		}
		return Integer.parseInt(value);
	}
	
	public static boolean getBooleanAttribut(Node node, String name) {
		String value = getAttribute(node, name);
		if (value == null || !value.equalsIgnoreCase("true")) {
			return false;
		}
		return true;
	}

	public static TutorialConfig buildTutorialConfigFromElementNode(Node node) {
		TutorialConfig config = null;

		if (node.getNodeName().equalsIgnoreCase("tutorial")) {
			config = new TutorialConfig();

			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				Node childNode = node.getChildNodes().item(i);

				if (childNode.getNodeName().equalsIgnoreCase("steps")) {
					for (int d = 0; d < childNode.getChildNodes().getLength(); d++) {
						Node stepNode = childNode.getChildNodes().item(d);
						if (stepNode.getNodeName().equalsIgnoreCase("step")) {
							TutorialStepConfig stepConfig = new TutorialStepConfig();

							stepConfig.setId(getAttribute(stepNode, "id"));
							stepConfig.setTitle(getAttribute(stepNode, "title"));

							for (int e = 0; e < stepNode.getChildNodes().getLength(); e++) {
								Node stepChildNode = stepNode.getChildNodes().item(e);

								if (stepChildNode.getNodeName().equalsIgnoreCase("text")) {

									// TutorialStepText
									stepConfig.setText(stepChildNode.getFirstChild().getNodeValue());
								}
								// QuestionnaireStepQuestions
								else if (stepChildNode.getNodeName().equalsIgnoreCase("questions")) {
									// Create QuestionStepConfig
									QuestionnaireStepConfig questionStep = new QuestionnaireStepConfig();

									for (int f = 0; f < stepChildNode.getChildNodes().getLength(); f++) {
										Node questionsChildNode = stepChildNode.getChildNodes().item(f);

										// StepQuestion
										if (questionsChildNode.getNodeName().equalsIgnoreCase("question")) {
											String questionQId = getAttribute(questionsChildNode, "qid");
											String questionId = getAttribute(questionsChildNode, "id");
											String question = questionsChildNode.getFirstChild().getNodeValue();

											// Create QuestionConfig
											QuestionConfig questionConfig = new QuestionConfig(questionId, questionQId, question);

											String questionType = getAttribute(questionsChildNode, "type");
											if (questionType == null || questionType.equalsIgnoreCase("text")) {
												questionConfig.setType("text");
											} else if (questionType.equalsIgnoreCase("score")) {
												questionConfig.setType("score");
												questionConfig.setMinScore(getIntAttribut(questionsChildNode, "minscore"));
												questionConfig.setMaxScore(getIntAttribut(questionsChildNode, "maxscore"));
											} else if (questionType.equalsIgnoreCase("radioscore")) {
												questionConfig.setType("radioscore");
												questionConfig.setMinScore(getIntAttribut(questionsChildNode, "minscore"));
												questionConfig.setMaxScore(getIntAttribut(questionsChildNode, "maxscore"));
												questionConfig.setMinScoreLabel(getAttribute(questionsChildNode, "minscorelabel"));
												questionConfig.setMaxScoreLabel(getAttribute(questionsChildNode, "maxscorelabel"));
											}

											questionStep.getQuestionnaireQuestions().add(questionConfig);
										}
									}

									if (questionStep.getQuestionnaireQuestions().size() > 0) {
										stepConfig.setQuestionnaireStepConfig(questionStep);
									}
								}
							}
							config.getTutorialSteps().add(stepConfig);
						}
					}
				}
			}
		}
		return config;
	}

	public static void fillHashMapsFromXML(HashMap<String, Vector<ElementInfo>> ontologyToContribution, HashMap<ElementInfo, Vector<ElementInfo>> contributionToElement, String ontologyXML) {
		Document xmlDoc = XMLParser.parse(ontologyXML);

		// The <ontology> element
		Node ontology = xmlDoc.getLastChild();

		if (ontology.getNodeName().equalsIgnoreCase("ontology")) {

			String ontologyName = getAttribute(ontology, "type");
			ontologyToContribution.put(ontologyName, new Vector<ElementInfo>());

			// Child that holds boxes and relations
			for (int i = 0; i < ontology.getChildNodes().getLength(); i++) {
				Node node = ontology.getChildNodes().item(i);

				if (node.getNodeName().equalsIgnoreCase("elements")) {

					// Boxes and Relations
					for (int d = 0; d < node.getChildNodes().getLength(); d++) {
						Node elementNode = node.getChildNodes().item(d);

						if (elementNode.getNodeName().equalsIgnoreCase("element")) {

							ElementInfo boxOrRelationElementInfo = workOnElementNode(elementNode);

							ontologyToContribution.get(ontologyName).add(boxOrRelationElementInfo);

							if (contributionToElement.get(boxOrRelationElementInfo) == null) {
								contributionToElement.put(boxOrRelationElementInfo, new Vector<ElementInfo>());
							}

							for (String key : boxOrRelationElementInfo.getChildElements().keySet()) {
								contributionToElement.get(boxOrRelationElementInfo).add(boxOrRelationElementInfo.getChildElements().get(key));
							}

						}

					}
				}
			}
		}
	}
}