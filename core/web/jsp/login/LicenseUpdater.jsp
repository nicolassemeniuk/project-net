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
|     $Author: avinash $
|
|  Licensing Updater
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Licenseupdater"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.license.create.LicenseSelectionType,
            net.project.base.IUniqueTransaction,
            net.project.util.UniqueTransaction"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="licenseUpdater" class="net.project.license.create.LicenseUpdater" scope="session" />
<jsp:useBean id="licenseContext" class="net.project.license.create.LicenseContext" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%  String specificMessage = null;
    if (licenseUpdater.getResult() != null) {
        specificMessage = (licenseUpdater.getResult().hasMessage() ? licenseUpdater.getResult().getMessage() : null);
    }

    //Create a globally unique transaction for this license update.  This prevents
    //any credit card transactions from being submitted twice.
    IUniqueTransaction uniqueTransaction = new UniqueTransaction(request.getRemoteAddr());
    session.setAttribute("uniqueTransaction", uniqueTransaction);

    if (!licenseContext.hasErrors()) {
        licenseContext.clear();
    }

    // Figure out trial period
    int trialPeriod = 0;
    if (net.project.license.system.LicenseProperties.getInstance() != null) {
	    trialPeriod = net.project.license.system.LicenseProperties.getInstance().getDefaultTrialLicensePeriodDays();
    } else {
	    throw new net.project.license.LicenseException(PropertyProvider.get("prm.license.updater.notinstalled.message"));
	}
%>

<html>
<head>
<title><display:get name="prm.global.login.license.pagetitle"/></title>

<%-- Default Stylesheet stuff --%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.login")%>' />

<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/checkRadio.js" />

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
    if (!checkRadio(theForm.selectionTypeID,'<%=PropertyProvider.get("prm.license.updater.selectoption.message")%>')) return false;
    return true;
}

function submit() {
    if(validateForm()) {
        theAction("submit");
	    theForm.submit();
    }
}

function cancel() {
    self.document.location = JSPRootURL + "/Login.jsp";
}


// Execute the setup() function if it is defined in the html page
function trySetup() {
	if (window.setup) {
		return window.setup();
	} else {
		// Not defined, do nothing
        return true;
	}
}

function bodyOnLoad(functionsToExecute) {
    if (functionsToExecute) {
        return eval(functionsToExecute);
    } else {
        return trySetup();
    }
}
</script>



<body class="main" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0 onload='setup();'>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
        <td align="left" valign="top">
            <form name="main" action="<%=SessionManager.getJSPRootURL() + "/login/LicenseUpdaterProcessing.jsp"%>" method="post">
                <input type="hidden" name="theAction">
            <table width="100%" cellpadding="0" cellspacing="0" border="0">
                <tr class="actionBar">
                    <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
                    <td colspan="2" valign="middle" class="ActionBar"><display:get name="prm.global.license.create.header" /></td>
                    <td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>

                <tr align="left">
                    <td>&nbsp;</td>
                    <td class="tableContent" align="left" colspan="2">
                        <display:get name="prm.global.license.update.instruction" />
            <%  if (specificMessage != null) { %>
                        <br>
                        <%=specificMessage%>
            <%  } %>
                    </td>
                    <td>&nbsp;</td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>

                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                    <td colspan="4">
                        <%-- Display any errors --%>
                        <%=licenseContext.getAllErrorMessages()%>
                    </td>
                </tr>
                <tr align="left">
                    <td></td>
                    <td colspan="2">
                        <jsp:include page="/admin/license/include/LicenseSelect.jsp">
                            <jsp:param name="domainSource" value="userObject" />
                        </jsp:include>
                    </td>
                    <td>&nbsp;</td>
                </tr>
            </table>
            </form>
		</td>
		<!-- End of Content -->
	</tr>
</table>

<tb:toolbar style="action" showLabels="true">
                            <tb:band name="action">
                                <tb:button type="submit" />
                            </tb:band>
</tb:toolbar>

</body>

<%-- Clear out errors displayed on this page --%>
<%licenseContext.clearErrors();%>

</html>
