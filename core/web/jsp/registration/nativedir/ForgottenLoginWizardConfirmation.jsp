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
    info="Forgotten Login Wizard"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<%
    session.removeValue("user");
%>
<html>
<head>

<title><%=PropertyProvider.get("prm.registration.forgottenloginwizard.confirmationpage.title")%></title>

<%-- Import CSS --%>
<template:import type="css" src="/styles/registration.css" />
<template:getSpaceCSS space="personal"/>

<script language="javascript" type="text/javascript">
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
	function cancel() {
		self.document.location = JSPRootURL + "/Login.jsp";
	}
</script>
</head>

<body id="bodyWithFixedAreasSupport" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0 class="main">

<div id="topframe">
<table width=100% cellpadding=1 cellspacing=0 border=0>
  	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="right" class="regBanner"><%=PropertyProvider.get("prm.registration.forgottenloginwizard.confirmation.pagetitle")%>&nbsp;</td>
 	</tr>
</table>
</div>

<div id='content'>

<br>
<div align="center">

<table width=80% cellpadding=0 cellspacing=0 border=0>
	<tr class="actionBar">
		<td width=1% class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar">&nbsp;</td>		
		<td width=1% align=right class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>	
    <tr>
        <td>&nbsp;</td>
        <td colspan="2" align="center">
            <b><%=PropertyProvider.get("prm.registration.forgottenloginwizard.confirmation.emailsent.message")%></b><p>
        </td>
        <td>&nbsp;</td>
    </tr>
</table>

</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" label='<%=PropertyProvider.get("prm.registration.forgottenloginwizard.confirmation.button.return.label")%>' function="javascript:cancel();" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
