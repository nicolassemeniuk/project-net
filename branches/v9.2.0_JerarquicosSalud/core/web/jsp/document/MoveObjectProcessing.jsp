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
    info="Process Move Object"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.util.*,
    net.project.security.*,
	net.project.base.property.PropertyProvider,
    net.project.base.Module"
 %>

<%@ include file="/base/taglibInclude.jsp" %>
 
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 


<%
    String objectID = request.getParameter("objectID");
    String containerID = request.getParameter("containerID");

    // SECURITY CHECK:
    // User must have create permission on the container and modify permission on the object
    if ((!securityProvider.isActionAllowed(objectID, Integer.toString(Module.DOCUMENT), Action.MODIFY)) ||
        (!securityProvider.isActionAllowed(containerID, Integer.toString(Module.DOCUMENT), Action.CREATE)))
        throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.moveobjectprocessing.authorizationfailed.message"));
                
    docManager.move (objectID, containerID);

%>

<html>
<head>
<title><display:get name="prm.document.moveobjectprocessing.title" /></title>
<script language="javascript1.2">

function finish () {

   var targetWindow;

   if (parent.opener.name == "main")
      targetWindow = parent.opener;
   else
      targetWindow = parent.opener.parent;


    targetWindow.location = "Main.jsp?module=<%=Module.DOCUMENT%>";
	targetWindow.focus();
	parent.close();	

}
</script>
</head>
<body onLoad="finish()">

</body>
</html>


