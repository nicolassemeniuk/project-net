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
            net.project.space.Space,
            net.project.base.PnetException,
            net.project.security.group.Group,
            net.project.security.group.EveryoneGroup,
            net.project.space.ISpaceTypes,
            net.project.project.ProjectSpace,
            net.project.project.ProjectVisibility,
            net.project.base.PnetRuntimeException"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="createWizard" class="net.project.security.group.CreateGroupWizard" scope="session" />

<security:verifyAccess action="create" module="<%=Module.DIRECTORY%>"/>

<%
    createWizard.setCurrentPageForName(CreateGroupWizard.INHERIT_SECURITY_PAGE);
    CreateGroupWizard.InheritSecurityPage currentPage = (CreateGroupWizard.InheritSecurityPage)createWizard.getCurrentPage();

    String theAction = request.getParameter("theAction");

    if ("cancel".equals(theAction)) {
        pageContext.forward("/base/CloseWindow.jsp");
    } else if ("back".equals(theAction)) {
        //Make sure you save the settings from this page
        currentPage.setPermissionSelection(request.getParameter("permissionSelection"));
        //Move back to the previous page
        pageContext.forward("/security/group/create/CreateGroupWizardStart.jsp");
    } else if ("finish".equals(theAction)) {
        Group everyoneGroup = new EveryoneGroup();
        currentPage.setGroup(everyoneGroup);
        currentPage.setPermissionSelection(request.getParameter("permissionSelection"));

        //Validate that all the required information is here
        currentPage.validate();
        if (!currentPage.hasErrors()) {
            everyoneGroup.validate();
        }

        //If this is the project space, the user had the opportunity to set the
        //visibility of the project, store that
        if (user.getCurrentSpace().getSpaceType().getID().equals(ISpaceTypes.PROJECT_SPACE)) {
            ProjectSpace space = (ProjectSpace)session.getAttribute("projectSpace");
            String visibilityID = currentPage.getProjectVisibilityID();
            if (visibilityID != null) {
                space.setVisibility(ProjectVisibility.findByID(visibilityID));
                space.store();
            }
        }

        if (everyoneGroup.hasErrors() || currentPage.hasErrors()) {
            request.setAttribute ("module", Integer.toString(Module.DIRECTORY));
            request.setAttribute ("action", Integer.toString(Action.CREATE));
            pageContext.forward("/security/group/create/CreateGroupWizardStart.jsp");

        } else {
            everyoneGroup.setUser(user);
            everyoneGroup.setSpace(user.getCurrentSpace());
            everyoneGroup.store();

            //Now make sure we inherit security based on the user's request
            currentPage.inheritSecurity();

            // Close Window and reset in opener page
            pageContext.forward("/base/CloseWindow.jsp?openerCallback=reset()");
       }
    } else {
        throw new PnetRuntimeException("Missing or invalid action in CreateGroupWizardStartProcessing.jsp");
    }
%>