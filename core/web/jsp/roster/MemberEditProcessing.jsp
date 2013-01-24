<%--
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
--%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="MemberEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.Module, 
			net.project.base.property.PropertyProvider,    
    		net.project.security.Action,
    		java.util.ArrayList,
    		java.util.List,
    		java.util.Arrays,
    		net.project.security.group.Group,
	   		net.project.persistence.PersistenceException,
	   		net.project.security.group.GroupProvider,
	   		net.project.security.group.GroupTypeID,
	   		org.apache.commons.collections.CollectionUtils,
	   		net.project.security.SessionManager,
	   		net.project.notification.EventCodes,
	   		net.project.resource.Roster,
	   		org.apache.log4j.Logger, 
		    org.apache.commons.lang.StringUtils,
            net.project.hibernate.service.ServiceFactory"
%>
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="person" class="net.project.resource.Person" scope="request" />

<%
// Make sure a security check has been passed to view a discussion group
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String memberid = request.getParameter("memberid");
String id = securityProvider.getCheckedObjectID();

String referrer = (String) session.getAttribute("pnet_refLink");

if ((id.length() > 0) || 
    (module != net.project.base.Module.DIRECTORY) 
    || (action != net.project.security.Action.MODIFY)
    || (memberid == null)) 
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.memberedit.authorizationfailed.message"));

// We can not use standard security here because person id spans across all spaces.
// Therefore we will only grant edit permission on a member if it is that member or a space
// admin
if ((!user.getID().equals(memberid)) &&
    (!securityProvider.isUserSpaceAdministrator())){
	throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.memberedit.authorizationfailed.message"));
}

person = roster.getPerson(memberid);

    if (( "".equals(request.getParameter("responsibilities")))||(request.getParameter("responsibilities") == null) ) {
		person.setResponsibilities("");
	}else{
		person.setResponsibilities(request.getParameter("responsibilities"));
	}
	
	if (( "".equals(request.getParameter("title")))||(request.getParameter("title") == null)) {
		person.setTitle("");
	}else{
		person.setTitle(request.getParameter("title"));
	}
	
	List<String> roles = new ArrayList<String>();
	List<String> existingGroups = new ArrayList<String>();
	
	if(request.getParameterValues("roles")!=null){
		roles = Arrays.asList(request.getParameterValues("roles"));
	}
	
	if(request.getParameter("existingRoles") != null){
		existingGroups =  Arrays.asList(request.getParameter("existingRoles").split(","));
	}	
	
	List<String> updateRoles = new ArrayList<String>();	
	List<String> updateRolesToRemove = new ArrayList<String>();	
	
	if(roles != null && existingGroups != null){
		for(String role : roles){
			if(!existingGroups.contains(role)){
				updateRoles.add(role);
			}
		}
		for(String group : existingGroups){
			if(!roles.contains(group)){
				updateRolesToRemove.add(group);
			}
		}
	}
	
	String [] removeRoleIds= new String[updateRolesToRemove.size()];
	String [] memberIds = {memberid};
	boolean forwardToSamePage = false;
	if(CollectionUtils.isNotEmpty(updateRolesToRemove)){
		removeRoleIds = updateRolesToRemove.toArray(removeRoleIds); 
		Group groupBean = null;
		GroupProvider groupProvider = new GroupProvider();
	    for(String ids : removeRoleIds){
	    	groupBean = groupProvider.newGroup(ids);
	  	 	groupBean.setSpace(user.getCurrentSpace());
	  	    groupBean.load();
	  	 	if (!groupBean.getGroupTypeID().equals(GroupTypeID.TEAM_MEMBER)) {
	  	 		 groupBean.validateRemoveMember(removeRoleIds, memberIds);
	  	 		 forwardToSamePage = true;
	  	 	     request.setAttribute("groupBean", groupBean);
    	 	 }
	        if (!groupBean.hasErrors() && !groupBean.getGroupTypeID().equals(GroupTypeID.TEAM_MEMBER)) {
	        	groupBean.removeMembers(removeRoleIds, memberIds);
	        	forwardToSamePage = false;
	        }
	    }
	}
	if(CollectionUtils.isNotEmpty(updateRoles)){
		String []updateRole = new String[updateRoles.size()];
		updateRole = updateRoles.toArray(updateRole);
		person.setRoles(updateRole);
	}	
	roster.storePersonRosterProperties(person, true);
	
	String chargeCodeId = request.getParameter("chargecode");
		if(StringUtils.isNotEmpty(chargeCodeId))
			ServiceFactory.getInstance().getPnObjectHasChargeCodeService().save(Integer.valueOf(person.getID()), Integer.valueOf(chargeCodeId), Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID()));

	
	// Create notification for member edit event
	net.project.project.ProjectEvent event = new net.project.project.ProjectEvent();
	event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
	event.setTargetObjectID(SessionManager.getUser().getCurrentSpace().getID());
	event.setTargetObjectType(EventCodes.MODIFY_PROJECT_PARTICIPANT);
	event.setTargetObjectXML(Roster.getXMLMailBody(person.getDisplayName()));
	event.setEventType(EventCodes.MODIFY_PROJECT_PARTICIPANT);
	event.setUser(SessionManager.getUser());
	event.setDescription(PropertyProvider.get("prm.notification.type.participantedited.description") +": \"" + person.getDisplayName() + "\"");
	try {
		event.store();
	} catch (PersistenceException pnetEx) {
		Logger.getLogger("Error occurred while creating notification : "+ pnetEx.getMessage());
	}

	request.setAttribute ("module", Integer.toString(Module.DIRECTORY));
	request.setAttribute("action", Integer.toString(Action.VIEW));
	net.project.security.ServletSecurityProvider.setAndCheckValues(request);
	
	if(referrer != null && referrer.contains(SessionManager.getJSPRootURL() + "/portfolio/PersonalPortfolio.jsp?module='"+Module.PERSONAL_SPACE+"'&portfolio=true")) {
		session.removeAttribute("pnet_refLink");
	}
	if(forwardToSamePage){
		forwardToSamePage = false;
		request.setAttribute("action", Integer.toString(Action.MODIFY));
		net.project.security.ServletSecurityProvider.setAndCheckValues(request);
		pageContext.forward("MemberEdit.jsp");
	}else{
		request.setAttribute("action", Integer.toString(Action.VIEW));
		net.project.security.ServletSecurityProvider.setAndCheckValues(request);
		pageContext.forward("/roster/Directory.jsp");
	}
	
%> 