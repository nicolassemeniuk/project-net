/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.form.Form;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.service.IPnBusinessSpaceService;
import net.project.hibernate.service.IUtilService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.Node;
import net.project.util.NodeFactory;
import net.project.util.NumberFormat;
import net.project.util.TextFormatter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 */
public class AssignmentStoreDataFactory {

	private static Logger log = Logger.getLogger(AssignmentStoreDataFactory.class);
	
	private Map<Integer, String> formAbbreviationByClassID = new HashMap <Integer, String> ();
	
	DateFormat currentuserDateFormat = null;
	
	public List<Node> getMyAssignmentGridDataString(List<PnAssignment> assignments, DateFormat userDateFormat, String assignmentViewParameter, String groupingParameter){
		currentuserDateFormat = userDateFormat;
		final List<String> columns = new ArrayList<String>(3);
        columns.add("objectId");
        columns.add("spaceId");
        columns.add("objectName");
        columns.add("objectType");
        columns.add("dueDate");
        columns.add("workRemaining");
        columns.add("workSpace");
        columns.add("assignorName");
        columns.add("assigneeName");
        columns.add("myWork");
        columns.add("myWorkComplete");
        columns.add("myPercentComplete");
        columns.add("startDate");
        columns.add("actualStartDate");
        columns.add("baseObjectType");
        
        IPnBusinessSpaceService pnBusinessSpaceService = ServiceFactory.getInstance().getPnBusinessSpaceService();
        
        final NodeFactory factory = new NodeFactory(columns);
        int spaceId = 0;
        ArrayList <Integer> addedBusinessSpaceIds  = new ArrayList<Integer>();
        PnBusiness business = null; 
        if (CollectionUtils.isNotEmpty(assignments)) {
        	Node parentNode = null;
        	Node businessNode = null;
        	Node otherProjectNode = null;
			for (PnAssignment assignment : assignments) {
					if (spaceId != assignment.getSpaceId() && !assignmentViewParameter.equals("flat")) {
						if (assignment.getSpaceType().equals(ObjectType.PROJECT) && groupingParameter.contains("Business")) {
							//load business for project.
                            business = pnBusinessSpaceService.getBusinessByProjectId(assignment.getSpaceId());
                            //if business found, Create a business node. 
                            if (business != null ) {
								if(addedBusinessSpaceIds.indexOf(business.getBusinessId()) == -1){
                                   //Create a business node if not created.
									businessNode = getParentNode(factory, business.getBusinessId(), business.getBusinessName(), ObjectType.BUSINESS);
								} else {
                                    //if node is already created.
                                    businessNode = getNodeForId(factory.getNodes(), business.getBusinessId());
								}
							} else { //if business not found for project create a non-business node and add all porject in it.
								if (otherProjectNode == null) {
                                //if non-business node is not created, create one.
									otherProjectNode = getParentNode(factory, -1, PropertyProvider.get("prm.personal.assignments.otherprojects.textname"), "non_business");
								}
								businessNode = otherProjectNode;
							}
							//checking grouping option. 
							if (groupingParameter.equalsIgnoreCase("BusinessProject") || groupingParameter.equalsIgnoreCase("ProjectBusiness")) {
                                //group by business and project both.
								parentNode = getParentNode(factory, assignment.getSpaceId(), assignment.getSpaceName(), assignment.getSpaceType());
								businessNode.add(parentNode);
							}else if (groupingParameter.equalsIgnoreCase("Business")) {
							    //group by business only.
								parentNode = businessNode;
							}
						} else if (!groupingParameter.trim().equals("")) {
                            //It may be possible the business node is already created in case of business meeting and form_data assignment. 
                            if(assignment.getSpaceType().equals(ObjectType.BUSINESS) && addedBusinessSpaceIds.indexOf(assignment.getSpaceId()) >= 0 ){
                                //get parent node for this spaceId.
                                parentNode = getNodeForId(factory.getNodes(), assignment.getSpaceId());
                            }else{
                                //If node not found create new.
                                parentNode = getParentNode(factory, assignment.getSpaceId(), assignment.getSpaceName(), assignment.getSpaceType());
                            }
						}
					}
					addChildNode(factory, parentNode, assignment, userDateFormat, assignmentViewParameter);
					
					spaceId = assignment.getSpaceId();
					if (business != null && addedBusinessSpaceIds.indexOf(business.getBusinessId()) == -1){
                        addedBusinessSpaceIds.add(business.getBusinessId());
					}
                    //add in addedBusinessSpaceIds list, if assignment parent is business.
                    if (assignment.getSpaceType().equals(ObjectType.BUSINESS) && addedBusinessSpaceIds.indexOf(spaceId) == -1 ){
                        addedBusinessSpaceIds.add(spaceId);
                    }
			}
		}
        	
        StringBuilder json = new StringBuilder();
        List<Node> nodes = factory.getNodes();
        List<Node> nodeToReturn = new ArrayList<Node>();
        Node nonBusinessOwnerNode = null;
        if (CollectionUtils.isNotEmpty(nodes)) {
           
        	int order = 1;
            for (Node node : nodes) {
                /* 
                 * Check whether this node has a parent. In such a case
                 * it will be added to the json represention by its parent.
                 */
            	if (null == MapUtils.getString(node.getMap(), "_parent") && MapUtils.getString(node.getMap(), "objectId").equals("-1")) {
            		nonBusinessOwnerNode = node;
            	} else if (null == MapUtils.getString(node.getMap(), "_parent")) {
                    node.order(order);
                    order = node.getRight() + 1;
                    json.append(node.toJSON());
                    json.append(',');
                }
            }
            if(nonBusinessOwnerNode != null){
            	nonBusinessOwnerNode.order(order);
            	json.append(nonBusinessOwnerNode.toJSON());
            }else {
            	json.deleteCharAt(json.length()-1);
            }
            for (Node node : nodes) {
            	if (null == MapUtils.getString(node.getMap(), "_parent") && MapUtils.getString(node.getMap(), "objectId").equals("-1")) {
            		nonBusinessOwnerNode = node;
            	} else if(null == MapUtils.getString(node.getMap(), "_parent")){
            		nodeToReturn.add(node);
            		for(Node node1 : node.getNodes()){
            			nodeToReturn.add(node1);
            			nodeToReturn.addAll(getChildNodes(node1));
            		}
            	}
            }
            if(nonBusinessOwnerNode != null){
            	nodeToReturn.add(nonBusinessOwnerNode);
            	for(Node node : nonBusinessOwnerNode.getNodes()){
            		nodeToReturn.add(node);
        			nodeToReturn.addAll(getChildNodes(node));
            	}
            }
        }
        return nodeToReturn;
    }
	
	
	/**
	 * To rearrange the child nodes.
	 * @param node
	 * @return
	 */
	private List<Node> getChildNodes(Node node){
		List<Node> nodeList = new ArrayList<Node>();
		for(Node node1 : node.getNodes()){
			nodeList.add(node1);
			nodeList.addAll(getChildNodes(node1));
		}
		return nodeList;
	}
	
	private Node getParentNode(NodeFactory factory, Integer spaceId, String spaceName, String spaceType){
		Node parentNode = factory.nextNode();
		parentNode.set("objectId", spaceId);
		parentNode.getAssignment().setObjectID(spaceId.toString());
		parentNode.set("spaceId", spaceId);
		parentNode.getAssignment().setSpaceID(spaceId.toString());
		parentNode.set("objectName", HTMLUtils.escape(spaceName).replaceAll("'", "&acute;"));
		parentNode.getAssignment().setObjectName(HTMLUtils.escape(spaceName).replaceAll("'", "&acute;"));
		parentNode.set("objectType", "non_business".equals(spaceType)? "" : spaceType);
		parentNode.getAssignment().setObjectType("non_business".equals(spaceType)? "" : spaceType);
		parentNode.set("dueDate", "");
		parentNode.getAssignment().setDueDate("");
		parentNode.getAssignment().setAssignmentDueDate(null);
		parentNode.set("workRemaining", "");
		parentNode.getAssignment().setWorkRemaining("");
		parentNode.getAssignment().setWorkRemainingInHours(null);
		parentNode.set("workSpace", "");
		parentNode.getAssignment().setWorkSpace("");
		parentNode.set("assignorName", "");
		parentNode.getAssignment().setAssignorName(null);
		parentNode.set("assigneeName", "");
		parentNode.getAssignment().setAssigneeName(null);
		parentNode.set("myWork", "");
		parentNode.getAssignment().setMyWork("");
		parentNode.getAssignment().setWorkInHours(null);
		parentNode.set("myWorkComplete", "");
		parentNode.getAssignment().setMyWorkComplete("");
		parentNode.getAssignment().setWorkCompleteInHours(null);
		parentNode.set("myPercentComplete", "");
		parentNode.getAssignment().setWorkPercentComplete(null);
		parentNode.getAssignment().setMyPercentComplete("");
		parentNode.set("startDate", "");
		parentNode.getAssignment().setStartDate("");
		parentNode.getAssignment().setAssignmentstartDate(null);
		parentNode.set("actualStartDate", "");
		parentNode.getAssignment().setActualStartDate("");
		parentNode.getAssignment().setAssignmentActualStartDate(null);
		parentNode.set("baseObjectType", spaceType);
		parentNode.getAssignment().setBaseObjectType(spaceType);
		
		return parentNode; 
	}
	
	private void addChildNode(NodeFactory factory, Node parentNode, PnAssignment assignment, DateFormat userDateFormat, String assignmentViewParameter){
		boolean addedNode = false;
        boolean checkForTargetParent = true;
        //if the same node(for this object id) is already added we will make a temporary node and will add all same node in it. 
        Node sameNode = getNodeForId(factory.getNodes(), assignment.getPnObjectName().getObjectId());
        if (sameNode != null) {
            if (StringUtils.isNotEmpty(sameNode.getMap().get("baseObjectType").toString()) && !sameNode.getMap().get("baseObjectType").toString().equals("temporary_task")) {
                //if temporary node not added for this same node, make one and add this one in it.
                putItInTemporaryNode(factory, sameNode);
            }
            parentNode = sameNode;
            checkForTargetParent = false;
        }

		Node childNode = factory.nextNode();
	
		childNode.set("objectId", assignment.getPnObjectName().getObjectId());
		childNode.getAssignment().setObjectID(assignment.getPnObjectName().getObjectId().toString());
		childNode.set("spaceId", assignment.getSpaceId());
		childNode.getAssignment().setSpaceID(assignment.getSpaceId().toString());
		if (assignment.getPnObjectType().getObjectType().equals(ObjectType.FORM_DATA)){
			String formAbbrevation = getFormAbbreviationByClassId(assignment.getPnClassInstance().getClassId());
			childNode.set("objectName", HTMLUtils.escape(assignment.getPnObjectName().getName()).replaceAll("'", "&acute;"));
			childNode.getAssignment().setObjectName(formAbbrevation + "-" + assignment.getPnClassInstance().getSequenceNo() + ": "+
					HTMLUtils.escape(assignment.getPnObjectName().getName()).replaceAll("'", "&acute;"));
		}else {
			childNode.set("objectName", HTMLUtils.escape(assignment.getPnObjectName().getName()).replaceAll("'", "&acute;"));
			childNode.getAssignment().setObjectName(HTMLUtils.escape(assignment.getPnObjectName().getName()).replaceAll("'", "&acute;"));
		}
		
		//Object type to show in type column of assignment grid.
		if (assignment.getPnObjectType().getObjectType().equals(ObjectType.FORM_DATA)){
			childNode.set("objectType",getFormAbbreviationByClassId(assignment.getPnClassInstance().getClassId()));
			childNode.getAssignment().setObjectType(getFormAbbreviationByClassId(assignment.getPnClassInstance().getClassId()));
		} else {
			childNode.getAssignment().setObjectType(assignment.getPnObjectType().getObjectType());
			childNode.set("objectType", assignment.getPnObjectType().getObjectType());
		}
		childNode.set("dueDate", currentuserDateFormat.formatDateMedium(assignment.getEndDate()));
		childNode.getAssignment().setDueDate(currentuserDateFormat.formatDateMedium(assignment.getEndDate()));
		childNode.getAssignment().setAssignmentDueDate(assignment.getEndDate());
		childNode.set("workRemaining", formatWork(getWorkInHours(assignment.getWork(), assignment.getWorkUnits()).
				subtract(getWorkInHours(assignment.getWorkComplete(), assignment.getWorkCompleteUnits()))));
		if(assignment.getPnObjectType().getObjectType().equals(ObjectType.MEETING)) {
			childNode.getAssignment().setWorkRemaining("");
		} else {
			childNode.getAssignment().setWorkRemaining(formatWork(getWorkInHours(assignment.getWork(), assignment.getWorkUnits()).
					subtract(getWorkInHours(assignment.getWorkComplete(), assignment.getWorkCompleteUnits()))));
		}
		childNode.getAssignment().setWorkRemainingInHours(getWorkInHours(assignment.getWork(), assignment.getWorkUnits()).
		 		subtract(getWorkInHours(assignment.getWorkComplete(), assignment.getWorkCompleteUnits())));
		childNode.set("workSpace", HTMLUtils.escape(assignment.getSpaceName().replaceAll("'", "&acute;")));
		childNode.getAssignment().setWorkSpace(HTMLUtils.escape(assignment.getSpaceName()).replaceAll("'", "&acute;"));
		childNode.set("assignorName", (StringUtils.isNotEmpty(assignment.getPnAssignor().getFirstName()) ? 
				(assignment.getPnAssignor().getFirstName() + " "+ assignment.getPnAssignor().getLastName().substring(0,1).toUpperCase()).replaceAll("'", "&acute;") : ""));
		childNode.getAssignment().setAssignorName((StringUtils.isNotEmpty(assignment.getPnAssignor().getFirstName()) ? 
				(assignment.getPnAssignor().getFirstName() + " "+ assignment.getPnAssignor().getLastName().substring(0,1).toUpperCase()).replaceAll("'", "&acute;") : ""));
        childNode.set("assigneeName", (assignment.getPnPerson().getFirstName() + " " + assignment.getPnPerson().getLastName().substring(0, 1).toUpperCase()).replaceAll("'", "&acute;"));
        childNode.getAssignment().setAssigneeName((assignment.getPnPerson().getFirstName() + " " + assignment.getPnPerson().getLastName().substring(0, 1).toUpperCase()).replaceAll("'", "&acute;"));
        childNode.set("myWork",  formatWork(getWorkInHours(assignment.getWork(), assignment.getWorkUnits())));
        if(assignment.getPnObjectType().getObjectType().equals(ObjectType.MEETING)) {
        	childNode.getAssignment().setMyWork("");
        } else {
        	childNode.getAssignment().setMyWork(formatWork(getWorkInHours(assignment.getWork(), assignment.getWorkUnits())));
        }
		childNode.getAssignment().setWorkInHours(getWorkInHours(assignment.getWork(), assignment.getWorkUnits()));
		childNode.set("myWorkComplete", formatWork(getWorkInHours(assignment.getWorkComplete(), assignment.getWorkCompleteUnits())));
		if(assignment.getPnObjectType().getObjectType().equals(ObjectType.MEETING)) {
			childNode.getAssignment().setMyWorkComplete("");
		} else {
			childNode.getAssignment().setMyWorkComplete(formatWork(getWorkInHours(assignment.getWorkComplete(), assignment.getWorkCompleteUnits())));
		}
		childNode.getAssignment().setWorkCompleteInHours(getWorkInHours(assignment.getWorkComplete(), assignment.getWorkCompleteUnits()));
		childNode.set("myPercentComplete",  NumberFormat.getInstance().formatPercent((assignment.getPercentComplete().multiply(new BigDecimal(100))).doubleValue(),"#.##")+"%");
		childNode.getAssignment().setMyPercentComplete(NumberFormat.getInstance().formatPercent((assignment.getPercentComplete().multiply(new BigDecimal(100))).doubleValue(),"#.##")+"%");
		childNode.getAssignment().setWorkPercentComplete((assignment.getPercentComplete().multiply(new BigDecimal(100))));
		childNode.set("startDate", currentuserDateFormat.formatDateMedium(assignment.getStartDate()));
		childNode.getAssignment().setStartDate(currentuserDateFormat.formatDateMedium(assignment.getStartDate()));
		childNode.getAssignment().setAssignmentstartDate(assignment.getStartDate());
		childNode.set("actualStartDate", currentuserDateFormat.formatDateMedium(assignment.getActualStart()));
		childNode.getAssignment().setActualStartDate(currentuserDateFormat.formatDateMedium(assignment.getActualStart()));
		childNode.getAssignment().setAssignmentActualStartDate(assignment.getActualStart());
		
		//A hidden column for object base type.
		childNode.set("baseObjectType",(assignment.getPnObjectType().getObjectType() != null && assignment.getPnObjectType().getObjectType().equals(ObjectType.TASK) 
				? assignment.getPnTask().getTaskType() : assignment.getPnObjectType().getObjectType()));
		childNode.getAssignment().setBaseObjectType((assignment.getPnObjectType().getObjectType() != null && assignment.getPnObjectType().getObjectType().equals(ObjectType.TASK) 
				? assignment.getPnTask().getTaskType() : assignment.getPnObjectType().getObjectType()));
        if(StringUtils.isNumeric(""+assignment.getPnTask().getParentTaskId()) 
        		&& assignment.getSpaceType().equals("project")
        		&& !assignmentViewParameter.equals("flat") && checkForTargetParent){
            int parentTaskId = assignment.getPnTask().getParentTaskId();   
            List<Node> nodes = factory.getNodes();
            for (Node targetParent : nodes) {
                if(parentTaskId == Integer.parseInt(targetParent.getMap().get("objectId").toString())){
                    targetParent.add(childNode);
                    addedNode = true;
                    break;
                }
            }
        } 
        if(parentNode != null && !addedNode){
			parentNode.add(childNode);
		}
		
	}
    
    private void putItInTemporaryNode(NodeFactory factory, Node node){
        // Create copy of node.
        Node newNode = factory.nextNode();
        newNode.set("objectId", node.getMap().get("objectId").toString());
        newNode.getAssignment().setObjectID(node.getMap().get("objectId").toString());
        newNode.set("spaceId", node.getMap().get("spaceId").toString());
        newNode.getAssignment().setSpaceID(node.getMap().get("spaceId").toString());
        newNode.set("objectName", node.getMap().get("objectName").toString());
        newNode.getAssignment().setObjectName(node.getMap().get("objectName").toString());
        newNode.set("objectType", node.getMap().get("objectType").toString());
        newNode.getAssignment().setObjectType(node.getMap().get("objectType").toString());
        newNode.set("dueDate", node.getMap().get("dueDate").toString());
        newNode.getAssignment().setDueDate(node.getMap().get("dueDate").toString());
        newNode.getAssignment().setAssignmentDueDate(node.getAssignment().getAssignmentDueDate());
        newNode.set("workRemaining", node.getMap().get("workRemaining").toString());
        newNode.getAssignment().setWorkRemaining(node.getMap().get("workRemaining").toString());
        newNode.getAssignment().setWorkRemainingInHours(node.getAssignment().getWorkRemainingInHours());
        newNode.set("workSpace", node.getMap().get("workSpace").toString());
        newNode.getAssignment().setWorkSpace(node.getMap().get("workSpace").toString());
        newNode.set("assignorName", node.getMap().get("assignorName").toString());
        newNode.getAssignment().setAssignorName(node.getMap().get("assignorName").toString());
        newNode.set("assigneeName", node.getMap().get("assigneeName").toString());
        newNode.getAssignment().setAssigneeName(node.getMap().get("assigneeName").toString());
        newNode.set("myWork", node.getMap().get("myWork").toString());
        newNode.getAssignment().setMyWork(node.getMap().get("myWork").toString());
        newNode.getAssignment().setWorkInHours(node.getAssignment().getWorkInHours());
        newNode.set("myWorkComplete", node.getMap().get("myWorkComplete").toString());
        newNode.getAssignment().setMyWorkComplete(node.getMap().get("myWorkComplete").toString());
        newNode.getAssignment().setWorkCompleteInHours(node.getAssignment().getWorkCompleteInHours());
        newNode.set("myPercentComplete", node.getMap().get("myPercentComplete").toString());
        newNode.getAssignment().setMyPercentComplete(node.getMap().get("myPercentComplete").toString()); 
        newNode.getAssignment().setWorkPercentComplete(node.getAssignment().getWorkPercentComplete());
        newNode.set("startDate", node.getMap().get("startDate").toString());
        newNode.getAssignment().setStartDate(node.getMap().get("startDate").toString());
        newNode.getAssignment().setAssignmentstartDate(node.getAssignment().getAssignmentstartDate());
        newNode.set("actualStartDate", node.getMap().get("actualStartDate").toString());
        newNode.getAssignment().setActualStartDate(node.getMap().get("actualStartDate").toString());
        newNode.getAssignment().setAssignmentActualStartDate(node.getAssignment().getAssignmentActualStartDate());
        newNode.set("baseObjectType", node.getMap().get("baseObjectType").toString());
        newNode.getAssignment().setBaseObjectType(node.getMap().get("baseObjectType").toString());
        
        //Now clear old node information to to make it temporary. 
        node.set("dueDate", "");
        node.getAssignment().setDueDate("");
        node.getAssignment().setAssignmentDueDate(null);
        node.set("workRemaining", "");
        node.getAssignment().setWorkRemaining("");
        node.getAssignment().setWorkRemainingInHours(null);
        node.set("workSpace", "");
        node.getAssignment().setWorkSpace("");
        node.set("assignorName", "");
        node.getAssignment().setAssignorName(null);
        node.set("assigneeName", "");
        node.getAssignment().setAssigneeName(null);
        node.set("myWork", "");
        node.getAssignment().setMyWork("");
        node.getAssignment().setWorkInHours(null);
        node.set("myWorkComplete", "");
        node.getAssignment().setMyWorkComplete("");
        node.getAssignment().setWorkCompleteInHours(null);
        node.set("myPercentComplete", "");
        node.getAssignment().setMyPercentComplete("");
        node.getAssignment().setWorkPercentComplete(null);
        node.set("startDate", "");
        node.getAssignment().setStartDate("");
        node.getAssignment().setAssignmentstartDate(null);
        node.set("actualStartDate", "");
        node.getAssignment().setActualStartDate("");
        node.getAssignment().setAssignmentActualStartDate(null);
        node.set("baseObjectType", "");
        node.getAssignment().setBaseObjectType("");
        node.set("baseObjectType", "temporary_task");
        if(node.getMap().get("objectType").toString().equalsIgnoreCase("task")) {
        	node.getAssignment().setBaseObjectType("temporary_task");
        } else if(node.getMap().get("objectType").toString().equalsIgnoreCase("meeting")){
        	node.getAssignment().setBaseObjectType("temporary_meeting");
        } else {
        	node.getAssignment().setBaseObjectType("temporary_formdata");
        }
        node.add(newNode);
    }

	public String getTimlineDataString(List<PnAssignment> assignments, DateFormat userDateFormat) {
		String returnStrnig = "{'events' : [";
		if (CollectionUtils.isNotEmpty(assignments)) {
			for (PnAssignment assignment : assignments) {
				if (isAssignableType(assignment.getPnObjectType().getObjectType()) && assignment.getStartDate() != null) {
					if (!returnStrnig.equals("{'events' : [")) {
						returnStrnig += ",";
					}
					returnStrnig += "{'start': '" + userDateFormat.formatDate(assignment.getStartDate(), "MMM dd yyyy");
					if (assignment.getEndDate() != null) {
						returnStrnig += "','end': '"
								+ userDateFormat.formatDate(assignment.getEndDate(), "MMM dd yyyy");
						// + "','isDuration': true";
					}
					returnStrnig += "','title': '"
							+ TextFormatter.truncateString(assignment.getPnObjectName().getName(), 40).replaceAll("'", "&acute;")
							+ "','description': '<b>ProjectName :</b> " + TextFormatter.truncateString(assignment.getSpaceName(), 40).replaceAll("'", "&acute;")
							+ "<br/><b>Assigned By :</b> " + (StringUtils.isNotEmpty(assignment.getPnAssignor().getFirstName()) ? 
                                            (assignment.getPnAssignor().getFirstName() + " "+ assignment.getPnAssignor().getLastName().substring(0,1).toUpperCase()).replaceAll("'", "&acute;") : "")
							+ "<br/><b>Assigned To :</b> " + (assignment.getPnPerson().getFirstName() + " " + assignment.getPnPerson().getLastName().substring(0, 1).toUpperCase()).replaceAll("'", "&acute;")
							+ "<br/><b>Start Date:</b> " + userDateFormat.formatDate(assignment.getStartDate(), "MMM dd yyyy")
							+ "<br/><b>End Date:</b> " + userDateFormat.formatDate(assignment.getEndDate(), "MMM dd yyyy")
							// +"','image': '"+
							// +"','link': '"+
							+ "','spaceId': '"+ assignment.getSpaceId()
							+ "','workSpace': '"+ assignment.getSpaceName().replaceAll("'", "&acute;")
							+ "','objectId': '"+ assignment.getPnObjectName().getObjectId()
							+ "','objectType': '"+ (assignment.getPnObjectType().getObjectType().equals(ObjectType.TASK) ? assignment
									.getPnTask().getTaskType() : assignment.getPnObjectType().getObjectType());
					
					//Chek is late assignment.
					IUtilService utilService = ServiceFactory.getInstance().getUtilService();
					if (utilService.clearTimePart(assignment.getEndDate())
							.before(utilService.clearTimePart(new Date()))
							&& assignment.getPercentComplete().doubleValue() < 1) {
						returnStrnig += "','color': 'red";
					}
					returnStrnig += "'}";
				}
			}
		}
		returnStrnig += "]}";
		return returnStrnig;
	}
    
    private Node getNodeForId(List<Node> nodes, Integer id){
        for (Node targetParent : nodes) {
            if(id == Integer.parseInt(targetParent.getMap().get("objectId").toString())){
                return  targetParent;
            }
        }
        return null;
    }
    
	private boolean isAssignableType(String assignmentType){
		//Only these object types are to show in assignment dashboard.
		if(assignmentType.equalsIgnoreCase(ObjectType.MEETING)
				||assignmentType.equalsIgnoreCase(ObjectType.TASK)
				||assignmentType.equalsIgnoreCase(ObjectType.ACTIVITY)
				||assignmentType.equalsIgnoreCase(ObjectType.FORM_DATA)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Converts work in hours
	 * @param work
	 * @param workUnit
	 * @return
	 */
	private BigDecimal getWorkInHours(BigDecimal work, Integer workUnit){
    	BigDecimal workHours = BigDecimal.valueOf(0);
    	if (work != null && workUnit != null) {
    		workHours = work;
			if (workUnit.intValue() == 8) {//if workunit is day 
				workHours = workHours.multiply(BigDecimal.valueOf(8));
			} else if (workUnit.intValue() == 16) {//if workunit is week
				workHours = workHours.multiply(BigDecimal.valueOf(40));
			} else if (workUnit.intValue() == 2) {//if workunit is minute
				workHours = workHours.divide(BigDecimal.valueOf(60));
			}//else work will be already in hour (bydefault) nothing to do.
		}
    	//return Double.valueOf(NumberFormat.getInstance().formatNumber(workHours.doubleValue(), 0, 2));
    	return workHours;
    }
	
	/**
	 * Formats work
	 * @param workHours
	 * @return
	 */
	private String formatWork(BigDecimal workHours){
    	if(workHours.doubleValue() > 160 && workHours.doubleValue() < 480) {
			return NumberFormat.getInstance().formatNumber(workHours.doubleValue() / 8) + " days";
		} else if (workHours.doubleValue() > 480) {
			return  NumberFormat.getInstance().formatNumber(workHours.doubleValue() / 40) + " wks";
		} else {
			return NumberFormat.getInstance().formatNumber(workHours.doubleValue()) + " hrs";
		}
    }
	
	public String getFormAbbreviationByClassId(Integer classId){
		String formAbbreviation = (String) formAbbreviationByClassID.get(classId);
		//Load form if not in map.
		if (formAbbreviation == null) {
			Form form = new Form();
			form.setID(classId.toString());
			try {
				form.load();
				formAbbreviation = form.getAbbreviation().replaceAll("'", "&acute;");
				formAbbreviationByClassID.put(classId, formAbbreviation);
			} catch (PersistenceException pnetEx) {
					
			}
		}
		return formAbbreviation; 
	}
	
	public String generateUserBusinessOptionsString(List<BusinessSpace> businesses) {
		String businessOptionsString = "[";
		if (CollectionUtils.isNotEmpty(businesses)) {
			for (BusinessSpace business : businesses) {
				if (!businessOptionsString.equals("[")) {
					businessOptionsString += ",";
				} else {
					businessOptionsString += "['','All'],";
				}
				businessOptionsString += "['" + business.getID() + "','"
						+ TextFormatter.truncateString(business.getName(), 40).replaceAll("'", "`") + "']";
			}
		}
		return businessOptionsString += "]";
	}
	
	public String generateUserProjectOptionsString(List<PnProjectSpace> projects) {
		String projectOptionsString = "[";
		if (CollectionUtils.isNotEmpty(projects)) {
			for (PnProjectSpace project : projects) {
				if (!projectOptionsString.equals("[")) {
					projectOptionsString += ",";
				} else {
					projectOptionsString += "['0','All'],";
				}
				projectOptionsString += "['" + project.getProjectId() + "','"
						+ TextFormatter.truncateString(project.getProjectName(), 40).replaceAll("'", "`") + "']";
			}
		}else{
            projectOptionsString += "['','No Project Found']";
        }
		return projectOptionsString += "]";
	}
	
	public String getSpaceResourceGridStrng(Space projectSpace) {
		String resourceAssignGridData = "[";
		if (projectSpace != null) {
			AssignmentRoster assignmentRoster = new AssignmentRoster();
			try {
				assignmentRoster.setSpace(projectSpace);
				assignmentRoster.load();
			} catch (PersistenceException pnetEx) {
				log.error("Error occured while getting resourcelist of project space in getResourceAssignGridData(): "
						+ pnetEx.getMessage());
			}
			for (Iterator it = assignmentRoster.iterator(); it.hasNext();) {
				AssignmentRoster.Person person = (AssignmentRoster.Person) it.next();
				if (!resourceAssignGridData.equals("[")) {
					resourceAssignGridData += ", ";
				}
				resourceAssignGridData += "['" + person.getID() + "','" + HTMLUtils.escape(person.getDisplayName()).replaceAll("'", "&acute;") + "', '']";
			}
		}
		return resourceAssignGridData += "]";
	}
	
	/**
	 * To clear time part of day
	 * @param date
	 * @return
	 */
	public Date clearTimePart(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime(); 
	}
	
	/**
	 * To get previous or next day's date
	 * @param currentDate
	 * @param day
	 * @return
	 */
	public Date getDateToComare(Date currentDate, int day){
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.DATE, day);
		return cal.getTime();
	}
}
