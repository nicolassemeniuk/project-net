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
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
			net.project.security.group.CreateGroupWizard,
            net.project.security.Action,
            net.project.security.User,
            net.project.space.Space"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="createWizard" class="net.project.security.group.CreateGroupWizard" scope="session" />

<%
    if (user.getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.roles.groupedit.rolesnotaccessible.message"));
    }
%>

<security:verifyAccess action="create"
					   module="<%=Module.DIRECTORY%>" /> 


<%
    // Set the current page
    createWizard.setCurrentPageForName(CreateGroupWizard.SELECT_SPACE_PAGE);
    CreateGroupWizard.SelectSpacePage currentPage = (CreateGroupWizard.SelectSpacePage) createWizard.getCurrentPage();

    String theAction = request.getParameter("theAction");

    if (theAction != null && theAction.equals("cancel")) {
    		pageContext.forward("/base/CloseWindow.jsp");

    } else if (theAction != null && theAction.equals("back")) {
        pageContext.forward("/security/group/create/CreateGroupWizardStart.jsp");

    } else if (theAction != null && theAction.equals("next")) {
        currentPage.clearSelectedSpaces();
        currentPage.setSelectedSpaces(request.getParameterValues("selectedSpaceID"));

        currentPage.clearSelectedDomains();
        currentPage.setSelectedDomains(request.getParameterValues("selectedDomainID"));

        currentPage.validate();
        if (currentPage.hasErrors()) {
            // Return to previous page
            pageContext.forward("/security/group/create/CreateGroupWizardInherit.jsp");

        } else {
            // Everything is OK
            pageContext.forward("/security/group/create/CreateGroupWizardRoles.jsp");
        }

    } else {
        throw new net.project.base.PnetException("Missing or invalid action in CreateGroupWizardInheritProcessing.jsp");
    }
%>
