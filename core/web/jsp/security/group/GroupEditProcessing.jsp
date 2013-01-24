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
			net.project.security.Action,
            net.project.space.Space, 
            net.project.base.Module" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="group" type="net.project.security.group.Group" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
	if (user.getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
	    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.roles.groupedit.groupsnotaccessible.message"));
	}
%>
<security:verifyAccess action="modify"
					   module="<%=Module.DIRECTORY%>"
					   objectID="<%=group.getID()%>" /> 

<%
	String theAction = request.getParameter("theAction");
    String forwardTo = null;

	if (theAction.equals("remove") ||
		theAction.equals("create") ||
		theAction.equals("modify"))	{

        // Allow non-system groups to have name / description be updated
		if (!group.isSystem()) {
%>		
			<jsp:setProperty name="group" property="name" /> 
			<jsp:setProperty name="group" property="description" /> 
<%
			if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
			    group.setDescription("");
			}
			group.clearErrors();
			group.validate();
			if (group.hasErrors()) {
				request.setAttribute ("module", Integer.toString(Module.DIRECTORY));
				request.setAttribute ("action", Integer.toString(Action.VIEW));
				pageContext.forward("GroupEdit.jsp");
				return;

			} else {
		        group.store();
			}

		}

		if (theAction.equals("remove") ||
			theAction.equals("create"))	{

			if (theAction.equals("remove")) {
                String[] groupIDs = request.getParameterValues("group_memberSelected");
                String[] personIDs = request.getParameterValues("person_memberSelected");
                
                group.validateRemoveMember(groupIDs, personIDs);
                if (!group.hasErrors()) {
    				group.removeMembers(groupIDs, personIDs);
                }

			} else {
                String[] groupIDs = request.getParameterValues("group_availableSelected");
                String[] personIDs = request.getParameterValues("person_availableSelected");

                group.validateAddMember(groupIDs, personIDs);
				if (!group.hasErrors()) {
                    group.addMembers(groupIDs, personIDs);
                }
			}

            if (group.hasErrors()) {
    			// Errors, must go back to GroupEdit
                forwardTo = "GroupEdit.jsp";
            
            } else {
                // No errors, currently go back to GroupEdit anyway
    			forwardTo = "GroupEdit.jsp";
            }


		} else {
			// Modify
	        forwardTo = "GroupListView.jsp";
	        request.setAttribute("id", "");
	    }

	    securityProvider.refreshGroups(user);
		request.setAttribute ("module", Integer.toString(Module.DIRECTORY));
		request.setAttribute ("action", Integer.toString(Action.VIEW));
		net.project.security.ServletSecurityProvider.setAndCheckValues(request);
		pageContext.forward(forwardTo);

	} else {
		throw new net.project.base.PnetException("Unknown or missing action '" + theAction + "' in GroupEditProcessing.jsp");
	}
%>
