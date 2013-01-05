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
|   $Revision: 20667 $
|       $Date: 2010-04-06 11:00:23 -0300 (mar, 06 abr 2010) $
|     $Author: umesha $
|
| Workflow Errors
+----------------------------------------------------------------------*/
%>
<%@ 
	page info="Workflow Error Processing Page"
    contentType="text/html; charset=UTF-8"
    isErrorPage="true"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager,
			net.project.security.User,
			net.project.space.Space,
			net.project.workflow.ErrorCodes.Code,
			net.project.workflow.IWorkflowException,
			net.project.security.AuthorizationFailedException"
%>
<%
	if (exception instanceof AuthorizationFailedException) {
	    pageContext.forward("/AccessDenied.jsp");
		return;
	} 

	if (exception instanceof IWorkflowException) {
		// Workflow error handler
		IWorkflowException workflowExceptionBean = (IWorkflowException)exception;
        pageContext.setAttribute("workflowException", workflowExceptionBean, PageContext.PAGE_SCOPE);
%>
<jsp:useBean id="workflowException" type="net.project.workflow.IWorkflowException" scope="page" />
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%-- Import CSS --%>
<template:getSpaceCSS />
<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
    var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';  

	function setup() {
		page=true;
		theForm = self.document.forms[0];
		isLoaded = true;	
	}


</script>
</head>

<body class="main" onLoad="setup();">

<div id='content'>

<form method="post">
<input type="hidden" name="theAction">
</form>
<div align="center">
<table border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98%><%=PropertyProvider.get("prm.workflow.errors.banner.label")%></td>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContent">
			<jsp:setProperty name="workflowException" property="stylesheet" value="/workflow/xsl/workflow_exception.xsl" />
			<jsp:getProperty name="workflowException" property="propertiesPresentation" />
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98% align=right><a href="javascript:history.back();" class="errorLink"><%=PropertyProvider.get("prm.workflow.errors.banner.label")%></a></td>
		<td><a href="javascript:history.back();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
	</tr>
</table>
</div>
    
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>                                                                                      
<%
	}
%>
