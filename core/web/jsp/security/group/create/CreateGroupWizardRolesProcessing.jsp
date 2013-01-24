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
    info="Create Group Wizard Roles Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
			net.project.security.group.CreateGroupWizard,
            net.project.security.Action,
            net.project.security.User,
            net.project.space.Space,
            net.project.base.PnetException"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="createWizard" class="net.project.security.group.CreateGroupWizard" scope="session" />

<%
    if (user.getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.roles.groupedit.rolesnotaccessible.message"));
    }
%>

<security:verifyAccess action="create" module="<%=Module.DIRECTORY%>" />

<%
    // Set the current page
    createWizard.setCurrentPageForName(CreateGroupWizard.SELECT_ROLE_PAGE);
    CreateGroupWizard.SelectRolePage currentPage = (CreateGroupWizard.SelectRolePage) createWizard.getCurrentPage();

    String theAction = request.getParameter("theAction");

    if (theAction != null && theAction.equals("cancel")) {
    		pageContext.forward("/base/CloseWindow.jsp");

    } else if (theAction != null && theAction.equals("back")) {
        pageContext.forward("/security/group/create/CreateGroupWizardInherit.jsp");

    } else if (theAction != null && theAction.equals("finish")) {
        currentPage.clearSelectedGroups();
        currentPage.setSelectedGroups(request.getParameterValues("selected"));
        currentPage.setPermissionSelection(request.getParameter("permissionSelection"));

        currentPage.validate();
        if (currentPage.hasErrors()) {
            // Problem on page
            pageContext.forward("/security/group/create/CreateGroupWizardRoles.jsp");

        } else {
            // Everything is OK
            // Inherit those groups
            currentPage.inheritSelectedGroups();
            
    		pageContext.forward("/base/CloseWindow.jsp?openerCallback=reset()");
        }

    } else {
        throw new PnetException("Missing or invalid action in CreateGroupWizardRolesProcessing.jsp");
    }
%>
