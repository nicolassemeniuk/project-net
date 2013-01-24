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
    info="Process Document Undo Check Out"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.security.*,
    net.project.util.*,
    net.project.base.Module"
 %>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="document" class="net.project.document.DocumentBean" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<%
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
        String id = securityProvider.getCheckedObjectID();


    // no need for security check, it is done within docManager
    document = (DocumentBean) docManager.getCurrentObject();
    document.setUser(docManager.getUser());
    docManager.undoCheckOutDocument (document); 
%>
<%
   String forwardingPage =  SessionManager.getJSPRootURL() + (String)docManager.getNavigator().get("TopContainer");

   if ((forwardingPage == null))
	   forwardingPage = SessionManager.getJSPRootURL() + "/document/Main.jsp?module="+Module.DOCUMENT;

%>

<html>
<head>
<title><display:get name="prm.document.undocheckoutprocessing.title" /></title>
<script language="javascript1.2">

function finish () {

   var targetWindow;

   if (parent.opener.name == "main")
      targetWindow = parent.opener;
   else
      targetWindow = parent.opener.parent;


    targetWindow.location = "<%=forwardingPage%>";
	targetWindow.focus();
	parent.close();	

}
</script>
</head>
<body onLoad="finish()">

</body>
</html>
