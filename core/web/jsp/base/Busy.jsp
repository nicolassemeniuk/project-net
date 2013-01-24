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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| Busy screen for indicating long running process
+----------------------------------------------------------------------*/
%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Busy Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager, 
			net.project.security.User,
			net.project.space.Space,
			net.project.base.PnetException"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%--
	processingURL	- page for actual processing
	processingParams - Query String for processing page
	title			- Title of Busy page
	message			- Message
--%>
<%
	String processingURL = request.getParameter("processingURL");
	String processingParams = java.net.URLDecoder.decode(request.getParameter("processingParams"));
	String title = request.getParameter("title");
	String message = request.getParameter("message");
	
	if (processingURL == null || processingURL.length() == 0) {
		throw new PnetException("Missing parameter 'processingURL' in Busy.jsp");
	}
%>
<html>

<head>
<meta name="refresh" content="0;" />

<%--
	Insert Refresh tag to cause redirection to processing page
--%>
<META HTTP-EQUIV="refresh" CONTENT="0;URL=<%=processingURL + "?" + processingParams%>">

<title><%=title%></title>
<template:getSpaceCSS />
<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
   theForm = self.document.forms["main"];
   isLoaded = true;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=base_busy";
	openwin_help(helplocation);
}


</script>
</head>
<body onLoad="setup();" class="main"  id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<br>
<div id='content'>
<div align="center">
<table width="75%" border="0" cellpadding="0" cellspacing="0">
	<tr class="channelHeader" align="left">
		<th class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">&nbsp;<%=title%></th>
    	<th class="channelHeader" align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr class="tableContent">
		<td>&nbsp;</td>
		<td align="left" class="tableContent">
			<%=message%>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>

</div>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
</body>

</html>
