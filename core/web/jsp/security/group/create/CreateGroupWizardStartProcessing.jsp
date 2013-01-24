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
    info="Create Group Wizard Start Processing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.security.Action,
            net.project.security.User,
			net.project.security.group.CreateGroupWizard,
            net.project.security.group.Group,
            net.project.security.group.UserDefinedGroup,
            net.project.space.Space,
            net.project.base.PnetRuntimeException,
            net.project.security.group.EveryoneGroup,
            net.project.space.ISpaceTypes,
            net.project.project.ProjectSpace,
            net.project.project.ProjectVisibility,
            org.apache.log4j.Logger"
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
    createWizard.setCurrentPageForName(CreateGroupWizard.CREATE_PAGE);
    CreateGroupWizard.CreatePage currentPage = (CreateGroupWizard.CreatePage) createWizard.getCurrentPage();

    String theAction = request.getParameter("theAction");

    if (theAction != null && theAction.equals("cancel")) {
    		pageContext.forward("/base/CloseWindow.jsp");

    } else if (theAction != null && theAction.equals("next")) {
        // Set the properties in the current wizard page
        if (request.getParameter("createAction") == null) {
            currentPage.setCreateAction("create");
        } else {
            currentPage.setCreateAction(request.getParameter("createAction"));
        }
        currentPage.clearInheritFrom();
        currentPage.setInheritFrom(request.getParameterValues("inheritFrom"));
        
        if (currentPage.getCreateAction().equals("create")) {
            // Create a new group
%>
        	<jsp:useBean id="group" class="net.project.security.group.UserDefinedGroup" type="net.project.security.group.Group" scope="page" />
        	<%
//        	Avinash : bfd-3153 (It's possible to create several roles with apparent equal names)  
			String name = request.getParameter("name");
        	if(name != null ){
	            String str = name.trim().replaceAll(" ","^");
	            java.util.StringTokenizer objSpace = new java.util.StringTokenizer(str,"^");
	        	String[] wordArr = new String[objSpace.countTokens()];
	        	StringBuffer strBuff = new StringBuffer();
	        	int i=0;
        		while (objSpace.hasMoreTokens())
        			wordArr[i++] = (String)objSpace.nextToken();
        		for(i=0; i<wordArr.length; i++){
        			strBuff.append(wordArr[i]);
        			strBuff.append(" ");
        		}
        		group.setName(strBuff.toString());
        	}
        	else
        		group.setName(name);
        	%>
<%--         	<jsp:setProperty name="group" property="name" /> --%>
        	<jsp:setProperty name="group" property="description" />

<%
            // Store this group in the current wizard page
            currentPage.setGroup(group);
            
        	group.validate();
        	if (group.hasErrors()) {
        		request.setAttribute ("module", Integer.toString(Module.DIRECTORY));
        		request.setAttribute ("action", Integer.toString(Action.CREATE));
        		pageContext.forward("/security/group/create/CreateGroupWizardStart.jsp");

        	} else {
        		group.setUser(user);
        		group.setSpace(user.getCurrentSpace());
        		group.store();

        		// Close Window and reset in opener page
        		pageContext.forward("/base/CloseWindow.jsp?openerCallback=reset()");
           }

        } else if (currentPage.getCreateAction().equals("inherited")) {
            // Inherit a role
            currentPage.validate();
            if (currentPage.hasErrors()) {
                // Something not filled in correctly
                pageContext.forward("/security/group/create/CreateGroupWizardStart.jsp");
            } else {
                // Everything is OK
                pageContext.forward("/security/group/create/CreateGroupWizardInherit.jsp");
            }
        } else if (currentPage.getCreateAction().equals("everyone")) {
            currentPage.setProjectVisibilityID(request.getParameter("visibilityID"));
            currentPage.validate();
            
            if (currentPage.hasErrors()) {
                pageContext.forward("/security/group/create/CreateGroupWizardStart.jsp");
            } else {
                pageContext.forward("/security/group/create/CreateGroupInheritSecurity.jsp");
            }
        } else {
            throw new net.project.base.PnetException("Missing or invalid action in CreateGroupWizardStartProcessing.jsp");
        }
    }
%>
