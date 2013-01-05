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
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%@ page
    contentType="text/html; charset=UTF-8"
    info="Create Template Errors"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.methodology.MethodologySpaceBean,
			net.project.methodology.MethodologyProvider,
			net.project.security.User,
			net.project.security.Action,
			net.project.base.Module,
			net.project.space.Space,
			net.project.project.DomainListBean,
			net.project.security.SessionManager"
%>


<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domainList" class="net.project.project.DomainListBean" scope="page"/>
<jsp:useBean id="methodologySpace" class="net.project.methodology.MethodologySpaceBean" scope="session" />

<jsp:useBean id="methodologyProvider" class="net.project.methodology.MethodologyProvider" scope="request" />

<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.METHODOLOGY_SPACE%>" /> 

<%
	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");
%>

<template:getSpaceCSS />

<template:getSpaceJS space="methodology" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    
    
 function setup() {
    theForm = self.document.forms[0];
    isLoaded = true;
}

function submit() { 
    theAction("submit");
   	theForm.submit();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=methodology_portfolio&section=create_errors";
	openwin_help(helplocation);
}

function cancel() { 
	self.document.location = JSPRootURL + "<%=(refLink != null && refLink.length() > 0 ? refLink : "/methodology/MethodologyList.jsp?module=" + Module.METHODOLOGY_SPACE)%>";
}

function reset() { theForm.reset(); }

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.template.name">
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr class="channelHeader" align="left">
		<th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		<th class="channelHeader">Create Template Results</th>
    	<th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td align="left">
            <display:get name="prm.template.create.error.successwitherrors" />
        </td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td align="left" class="tableContent">
			<%=methodologyProvider.getAllErrorMessages()%>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	    <tb:button type="cancel" label="@prm.template.create.error.button.close.label" />
	</tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceNavBar space="methodology"/>
</body>
</html>