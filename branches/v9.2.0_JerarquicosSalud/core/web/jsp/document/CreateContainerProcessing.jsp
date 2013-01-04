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
|   $Revision: 19856 $
|    $Date: 2009-08-25 10:06:50 -0300 (mar, 25 ago 2009) $
|   $Author: Adam Klatzkin    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Process Create Contiainer"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    		net.project.security.*,
			net.project.base.property.PropertyProvider,
    		net.project.base.Module,
    		net.project.util.JSPUtils"
 %>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="newContainer" class="net.project.document.ContainerBean" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<%
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();

    if ((!id.equals( docManager.getCurrentContainerID())) || (module != docManager.getModuleFromContainerID(id)) || (action != net.project.security.Action.CREATE))
           throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.createcontainer.authorizationfailed.message"));

	String tmp = null;

	newContainer.setContainerID ( docManager.getCurrentContainerID() );
	newContainer.setUser ( docManager.getUser() );
%>

<%-- Get the form fields --%>
	<jsp:setProperty name="newContainer" property="*" />

<%
	// bfd - 2994 issue
	if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		newContainer.setDescription("");
	}
%>

<%-- Store to the database --%>
	<%
	try {
		newContainer.create(); 
	} catch (net.project.base.PnetException e) {
		if (e.getReasonCode() == -100) {
			//this is duplicate name exception, just say to user
			request.setAttribute("error.directoryexists","directoryexists");
			pageContext.forward("CreateContainer.jsp");
		} else {
		throw e;
		}
	}
	
	String historyType = request.getParameter("historyType");
	if(JSPUtils.isEmpty(historyType) || historyType.equals("null")){
		historyType = "unspecified";
	} 
	%>



<html>
<head>
<title><display:get name="prm.document.createcontainerprocessing.title" /></title>
<script language="javascript1.2">

function finish () {

	parent.opener.location = "Main.jsp?module=<%=module%>&historyType=<%=historyType%>";
	parent.opener.focus();
	parent.close();	
}
</script>
</head>
<body onLoad="finish()">

</body>
</html>


