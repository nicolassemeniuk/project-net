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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="LDAP Forgotten Info"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<html>
<head>
<title>Forgotten Login or Password Wizard</title>

<%-- Import CSS --%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceJS space="personal"/>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkRadio.js" />


<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
    function setup() {
        theForm = self.document.forms["forgotten"];
    	isLoaded = true;
    }

    function back() {
        theAction("back");
        theForm.submit();
    }

    function cancel() {
        theAction("cancel");
        theForm.submit();
    }

</script>
</head>
    
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">

<div id="topframe">
<table width="100%" cellpadding="1" cellspacing="0" border="0">
 	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="right" class="regBanner">Forgotten Password or Login Name &nbsp;</td>
 	</tr>
</table>
</div>

<div id='content'>

<br />

<form name="forgotten" method="post" action="<%=SessionManager.getJSPRootURL()%>/registration/ldap/ForgottenInfoController.jsp">
    <input type="hidden" name="fromPage" value="ldapForgottenInfoPage">
    <input type="hidden" name="theAction">
    
<div align="center">
<table width="80%" cellpadding=0 cellspacing=0 border=0>
	<tr class="actionBar">
		<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width="8" height="27" alt="" border="0"></td>
		<td colspan="2" valign="middle" class="actionBar"><%=PropertyProvider.get("prm.project.registration.ldap.label") %></td>
		<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width="8" height="27" alt="" border="0"></td>
	</tr>
	<tr align="left">
        <td>&nbsp;</td>
        <td colspan="2">
            <%=PropertyProvider.get("prm.project.registration.ldap.message")%>
        </td>
        <td>&nbsp;</td>
    </tr>
 </table>
</div>
 
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="back" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
