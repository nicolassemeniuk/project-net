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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="SecurityProcessing.jsp omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User,
            net.project.security.SecurityManager,
            net.project.base.*, 
            net.project.discussion.*" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityManager" class="net.project.security.SecurityManager" scope="session" />

<%
	String id = request.getParameter("id");
%>
<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.SECURITY%>"
					   objectID="<%=id%>" /> 
    
<%
	String objectType = request.getParameter("objectType");
    if (objectType == null)
        objectType = new String();
	String objectID = (String) session.getValue("objectID");

	if(request.getMethod().equals("POST"))
	{
		if (request.getParameter("theAction").equals("add"))
		{
			securityManager.addGroupToObjectACL(request);
%>
			<%-- Goback to the Security Console --%>
			<jsp:forward page="../security/SecurityMain.jsp" />
<%
		}
		else if (request.getParameter("theAction").equals("remove"))
		{
			securityManager.getHtmlRemovePost(request);
%>
			<%-- Goback to the Security Console --%>
			<jsp:forward page="../security/SecurityMain.jsp" />
<%
		}
		else if (request.getParameter("theAction").equals("apply"))
		{
			securityManager.getHtmlApplyPost(request);
		}
		else if (request.getParameter("theAction").equals("close"))
		{
			securityManager.clearObjectPermission();
		}

        if (objectType.equals("document")) 
        {
            // object type is a document therefore we must copy its permissions to the
            // discussion group it owns
            DiscussionManager discManager = new DiscussionManager();
            discManager.setUser(user);
            discManager.setObjectId(objectID);
            java.util.ArrayList list = discManager.getGroupList();        
            if (list.size() > 0) 
            {
                DiscussionGroup discussion = (DiscussionGroup)list.get(0);
                securityManager.copyPermissions(objectID, discussion.getID());
            }
        }
	}
%>

<html>
<head>
<title><%=PropertyProvider.get("prm.security.processing.closepagetitle")%></title>
<script language="javascript">

function finish () 
{
			//parent.opener.location = "Main.jsp";
			//parent.opener.focus();
			parent.close();
}
</script>
</head>
<body onLoad="finish()">

</body>
</html>
