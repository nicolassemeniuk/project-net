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
|   $Revision: 19311 $
|       $Date: 2009-06-01 16:48:29 -0300 (lun, 01 jun 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Send Mail Errors" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");

    // There may be an error message or an exception
    String errorMessage = (String) request.getAttribute("errorMessage");
    net.project.notification.EmailException ee = (net.project.notification.EmailException) request.getAttribute("emailException");
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<script language="javascript">
	var theForm;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  
	var isLoaded = false;
	
function setup() {
   load_menu('<%=user.getCurrentSpace().getID()%>');
   isLoaded = true;
   theForm = self.document.forms["main"];
}

function cancel() { 
   self.document.location = JSPRootURL + "<%=refLink%>"; 
}

</script>

</head>

<body class="main"  onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.setup.notifications.link">
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr>
    <td>&nbsp;</td>
    <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        	<tr align="right">
        		<td align="left" class="pageTitle"><%=PropertyProvider.get("prm.directory.grouplisveiw.sendmailerrors.pagetitle")%>&nbsp;</td>
        		<td align="right" class="pageTitle">&nbsp;</td>
        	</tr>
        </table>
    </td>
    <td>&nbsp;</td>
<tr>
    <td colspan="3">&nbsp;</td>
</tr>
<tr>	
    <td>&nbsp;</td>
    <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td class="tableContent">
                <pre class="tableContentFontOnly">
<%=(errorMessage != null ? errorMessage : net.project.util.HTMLUtils.escape(PropertyProvider.get("prm.directory.grouplisveiw.sendmailerrors.msg")))%>&nbsp;
                </pre>
                </td>
            </tr>
        </table>
    </td>
    <td>&nbsp;</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" label='<%=PropertyProvider.get("prm.directory.grouplisveiw.sendmailerrors.button.close.label")%>' />
	</tb:band>
</tb:toolbar>

<br />

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>