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
|   $Revision: 18426 $
|       $Date: 2008-11-27 13:19:35 -0200 (jue, 27 nov 2008) $
|     $Author: dpatil $
|
|  Licensing Updater
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Licenseupdater"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.license.create.LicenseSelectionType,
            net.project.base.IUniqueTransaction,
            net.project.util.UniqueTransaction"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="licenseUpdater" class="net.project.license.create.LicenseUpdater" scope="session" />
<jsp:useBean id="licenseContext" class="net.project.license.create.LicenseContext" scope="session" />
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%	user.setCurrentSpace(personalSpace);
    //Create a globally unique transaction for this license update.  This prevents
    //any credit card transactions from being submitted twice.
    IUniqueTransaction uniqueTransaction = new UniqueTransaction(request.getRemoteAddr());
    session.setAttribute("uniqueTransaction", uniqueTransaction);

    if (!licenseContext.hasErrors()) {
        licenseContext.clear();
    }
%>
<html>
<head>
<title><%=PropertyProvider.get("prm.personal.licenseupdaterpage.title")%></title>

<%-- Additional head stuff --%>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import java script --%>
<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/util.js" />
    
<script language="javascript">
	var theForm;
	var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
    isLoaded = true;
    theForm = self.document.forms["main"];
    selectRadio(theForm.selectionTypeID, "<%=licenseContext.getSelectionTypeID()%>");
}
    
function validateForm() {
    if (!checkRadio(theForm.selectionTypeID,'<%=PropertyProvider.get("prm.personal.licenseupdater.selectoption.message")%>')) return false;
    return true;
}

function licenseSubmit() {
    if(validateForm()) {
        theAction("submit");
	    theForm.submit();
    }
}
    
function cancel() {
    self.document.location = '<%=SessionManager.getJSPRootURL() + "/personal/ProfileLicense.jsp?module=" + Module.PERSONAL_SPACE%>';
}
</script>

</head>
<%-- End of head --%>

<%-- Begin Content --%>		
<body class="main" onload="setup();">

<form name="main" action="<%=SessionManager.getJSPRootURL() + "/personal/LicenseUpdaterProcessing.jsp"%>" method="post" onsubmit="javascript:return licenseSubmit();">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%=""+Module.APPLICATION_SPACE%>">
    <input type="hidden" name="action" value="<%=""+Action.MODIFY%>">
    
<table width="100%" cellpadding="0" cellspacing="0" border="0">
	<tr class="actionBar">
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar"><%=PropertyProvider.get("prm.personal.licenseupdater.assign.button.label")%></td>		
		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
    <tr>
		<td>&nbsp;</td>
		<td colspan="2">
	<jsp:include page="/admin/license/include/LicenseSelect.jsp" />
        </td>
		<td>&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true">
				<tb:band name="action">
					<tb:button type="cancel" />
					<tb:button type="submit" function="javascript:licenseSubmit();"/>
				</tb:band>
</tb:toolbar>

</form>

</body>
<%-- End Content --%>

</html>
<%-- Clear out errors displayed on this page --%>
<%licenseContext.clearErrors();%>
