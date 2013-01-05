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
			net.project.security.User, 
            net.project.security.Action,
            net.project.security.SecurityProvider,
            net.project.space.Space, 
            net.project.base.Module,
			net.project.security.group.CreateGroupWizard,
            net.project.security.group.GroupCollection,
            net.project.security.group.GroupProvider,
            net.project.security.group.Group,
            net.project.notification.email.RecipientProvider,
            net.project.security.ServletSecurityProvider" 
%>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
if(user.getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.directory.roles.groupedit.groupsnotaccessible.message"));
}


	if (request.getParameter("theAction").equals("remove") && request.getParameter("selected") != null )
	{
		// For the time-being .... Bit of a fudge ............. 
		// -- deepak
		GroupProvider groupProvider = new GroupProvider();
		Group spaceAdmin = groupProvider.getSpaceAdminGroup(user.getCurrentSpace().getID());
     	spaceAdmin.load();
	 
     	if (!spaceAdmin.isMember(user.getID())) {
			request.setAttribute ("id", request.getParameter("selected"));
		}
	
		request.setAttribute ("groupID", request.getParameter("selected"));
		request.setAttribute ("action", Integer.toString(Action.DELETE));
		ServletSecurityProvider.setAndCheckValues(request);
		pageContext.forward ("/security/group/GroupRemoveProcessing.jsp");
	}
	else if(request.getParameter("theAction").equals("security"))
	{
        // The other actions in this page do not require a security validation here
        // because their destination page will do the security validation.
        // SecurityMain is a common page though and will only do a validation that
        // the user has permission to view security settings.  We must do a validation
        // here that the user has modify permission before setting the session value 'objectID'
        int module = securityProvider.getCheckedModuleID();
        String id = securityProvider.getCheckedObjectID();
        int action = securityProvider.getCheckedActionID();
        if ((id.length() == 0) || 
            (action != net.project.security.Action.MODIFY_PERMISSIONS) ||
            (module != Module.DIRECTORY))
            throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.security.validationfailed.message"));

		session.putValue("objectID", request.getParameter("selected"));
		request.setAttribute ("action", Integer.toString(Action.VIEW));
		request.setAttribute ("module", Integer.toString(Module.SECURITY));
		pageContext.forward ("/security/SecurityMain.jsp");
	}
	else if(request.getParameter("theAction").equals("newgroup"))
	{
        request.setAttribute("id", "");
		request.setAttribute ("action", Integer.toString(Action.CREATE));
		net.project.security.ServletSecurityProvider.setAndCheckValues(request);
%>
        <jsp:useBean id="createWizard" class="net.project.security.group.CreateGroupWizard" scope="session" />
<%
		createWizard.clear();
        pageContext.forward ("/security/group/create/CreateGroupWizardStart.jsp");
	}
	else if(request.getParameter("theAction").equals("modify"))
	{
		request.setAttribute ("action", Integer.toString(Action.VIEW));
		request.setAttribute ("id", request.getParameter("selected"));
		pageContext.forward ("/security/group/GroupEdit.jsp");

	} else if (request.getParameter("theAction").equals("sendMail")) {
        
		// Load a group based on the id
		GroupProvider groupProvider = new GroupProvider();
	    Group group = groupProvider.newGroup(request.getParameter("id"));
	    group.setSpace(user.getCurrentSpace());
	    group.load();

        // Now load all the groups in this space for selection
       	GroupCollection groupCollection = new GroupCollection();
        groupCollection.setSpace(user.getCurrentSpace());
        groupCollection.loadAll();
        groupCollection.removePrincipalGroups();
        
        RecipientProvider recipientProvider = new RecipientProvider();
        recipientProvider.addAvailable(groupCollection);
        recipientProvider.setSelected(group);
        recipientProvider.setSorted();

        pageContext.removeAttribute("recipients", PageContext.SESSION_SCOPE);
        pageContext.setAttribute("recipients", recipientProvider, PageContext.SESSION_SCOPE);
        
        pageContext.forward ("/notification/email/ComposeMail.jsp?module=" + Module.PERSONAL_SPACE);
    }
%>


