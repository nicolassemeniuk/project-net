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
    info="Group Form processing.. omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SecurityProvider,
			net.project.security.group.Group,
            net.project.security.group.GroupProvider,
			net.project.security.User,
			net.project.space.Space,
			net.project.base.Module,
            net.project.security.ServletSecurityProvider" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

	
<%

if(user.getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.roles.groupedit.groupsnotaccessible.message"));
}

%>
<%--
	// For the time-being .... Bit of a fudge ............. 
		// -- deepak
--%> 
<%
	
	// Check for the SpaceAdministrator group
	
	GroupProvider groupProvider = new GroupProvider();
	Group spaceAdmin = groupProvider.getSpaceAdminGroup(user.getCurrentSpace().getID());
    spaceAdmin.load();
	
	Group group = groupProvider.newGroup((String)request.getAttribute("groupID"));
    group.setSpace(user.getCurrentSpace()); 
	
     if (spaceAdmin.isMember(user.getID())) {
%>
	<security:verifyAccess action="delete"
					module="<%=Module.DIRECTORY%>" /> 	
<%
	} else {
		String id = securityProvider.getCheckedObjectID();
		int action = securityProvider.getCheckedActionID(); 
		
%>
	<security:verifyAccess action="delete"
					module="<%=Module.DIRECTORY%>"
					objectID="<%=id%>" /> 

<%
	}
	
    if (group.isOwnedBySpace()) {
        // Group is owned by space
        // We must check that the role may be removed
        // System roles (Space Administrator, Team Member) may not be removed
        group.validateRemove();
        if (!group.hasErrors()) {
        	group.remove();
        } else {
            throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.groupremove.remove.accessdenied.message"));
        }
    
    } else {
        // Group is inherited by current space
        group.removeInheritance();
    }

    request.setAttribute("id", "");
	request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
	ServletSecurityProvider.setAndCheckValues(request);
	
	// Go back to the Security Console
	response.sendRedirect("/security/group/GroupListView.jsp?module="+Module.DIRECTORY);
%>
