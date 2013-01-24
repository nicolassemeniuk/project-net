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
    info="Forgotten Information Wizard"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="loginPasswordHelper" class="net.project.base.directory.nativedir.LoginPasswordHelper" scope="session" />
<html>
<head>
<title><%=PropertyProvider.get("prm.registration.forgotteninfowizardpage.title")%></title>

<%-- Import CSS --%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceCSS space="personal"/>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/checkEmail.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
    function setup() {
        theForm = self.document.forms["forgotten"];
        focusFirstField(theForm);
    	isLoaded = true;
    }

    function validate() {
        if (!checkTextbox(theForm.email, '<%=PropertyProvider.get("prm.registration.forgotteninfowizard.enteremail.message")%>')) return false;
        if (!checkEmail(theForm.email,'<%=PropertyProvider.get("prm.registration.forgottenpasswordwizard.emailmustbevalid.message")%>')) return false;
        if (!checkRadio(theForm.item, '<%=PropertyProvider.get("prm.registration.forgotteninfowizard.selectoption.message")%>')) return false;
        return true;
    }
    
    function back() {
        theAction("back");
        theForm.submit();
    }

    function next() {
        if (validate()) {
            theAction("next");
            theForm.submit();
        }
    }

    function cancel() {
        theAction("cancel");
        theForm.submit();
    }
    
    function defaultSubmit() {
        if (validate()) {
            theAction("next");
            return true;
        } else {
            return false;
        }
    }

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">

<div id="topframe">
<table width="100%" cellpadding="1" cellspacing="0" border="0">
 	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="right" class="regBanner"><%=PropertyProvider.get("prm.registration.forgotteninfowizard.pagetitle")%>&nbsp;</td>
 	</tr>
</table>
</div>

<div id='content'>

<br />

<form name="forgotten" method="post" action="<%=SessionManager.getJSPRootURL()%>/registration/nativedir/ForgottenInfoWizardProcessing.jsp"
      onSubmit="return defaultSubmit();">
    <input type="hidden" name="theAction">

<div align="center">
<table width=80% cellpadding=0 cellspacing=0 border=0>
    <tr>
		<td width=1% class="ActionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td valign="middle" class="ActionBar" colspan="2"><%=PropertyProvider.get("prm.registration.forgotteninfowizard.channel.forgotten.title")%>&nbsp;</td>
		<td width=1% align=right class="ActionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
    </tr>
    <tr><td colspan="4">&nbsp;</td></tr>

<% if (request.getAttribute("errorMsg") != null) { %>
    <tr>
        <td>&nbsp;</td>
        <td colspan="2" class="fieldWithError"><%=request.getAttribute("errorMsg") %></td>
        <td>&nbsp;</td>
    </tr>
<% } %>    
    <tr>
        <td>&nbsp;</td>
        <td class="fieldRequired"><%=PropertyProvider.get("prm.registration.forgotteninfowizard.enteremail.label")%>&nbsp;</td>
        <td><input type="text" name="email" value="" MAXLENGTH="80" SIZE="20"></td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="4">&nbsp;</td></tr>

    <tr>
        <td>&nbsp;</td>
        <td class="fieldRequired" colspan="2"><%=PropertyProvider.get("prm.registration.forgotteninfowizard.makeselection.label")%>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td class="fieldRequired" colspan="2">
            <table border="0">
                <tr>
                    <td>&nbsp;</td>
                    <td class="fieldNonRequired" width="10%"><input type="radio" name="item" value="password" CHECKED></td>
                    <td class="fieldNonRequired"><%=PropertyProvider.get("prm.registration.forgotteninfowizard.option.forgotpassword.name")%></td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td class="fieldNonRequired" width="10%"><input type="radio" name="item" value="login"></td>
                    <td class="fieldNonRequired"><%=PropertyProvider.get("prm.registration.forgotteninfowizard.option.forgotname.name")%></td>
                    <td>&nbsp;</td>
                </tr>
            </table>
        </td>
        <td>&nbsp;</td>
    </tr>
 </table>
</div>
 
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="back" />
		<tb:button type="cancel" />
		<tb:button type="next" />
	</tb:band>
</tb:toolbar>
 
</form>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
