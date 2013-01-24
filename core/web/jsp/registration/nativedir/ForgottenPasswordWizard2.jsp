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
    info="Forgotten Password Wizard"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.persistence.PersistenceException,
        	net.project.security.SessionManager,
        	net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<html>
<head>

<title><%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2page.title")%></title>

<%-- Import CSS --%>
<template:import type="css" src='<%=PropertyProvider.get("prm.global.css.registration")%>' />
<template:getSpaceCSS space="personal"/>

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />


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
        if (!checkTextbox(theForm.email, '<%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.email.message")%>')) return false;
        if (!checkTextbox(theForm.verificationCode, '<%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.code.message")%>')) return false;
        if (!checkLength(theForm.password,6,'<%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.passwordrequired.message")%>')) return false;
        if (!checkPasswords(theForm.password, theForm.password_2, '<%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.passwordmatch.message")%>')) return false;
        return true;
    }
    
    function finish() {
        if (validate()) {
            theAction("next");
            theForm.submit();
        }
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
 		<td align="right" class="regBanner"><%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.pagetitle")%>&nbsp;</td>
 	</tr>
</table>
</div>

<div id='content'>

<br />

<form name="forgotten" action="<%=SessionManager.getJSPRootURL()%>/registration/nativedir/ForgottenPasswordWizard2Processing.jsp" method="post">
    <input type="hidden" name="theAction">

<div align="center">

<table width=80% cellpadding=0 cellspacing=0 border=0>
	<tr class="actionBar">
		<td width=1% class="ActionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td colspan="2" valign="middle" class="ActionBar">&nbsp;</td>		
		<td width=1% align=right class="ActionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>	

<% if (request.getAttribute("errorMsg") != null) { %>
    <tr>
        <td>&nbsp;</td>
        <td colspan="2" class="fieldWithError"><%=request.getAttribute("errorMsg")%></td>
        <td>&nbsp;</td>
    </tr>
<% } %>
    <tr>
        <td>&nbsp;</td>
        <td colspan="2">            <%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.emailsent.message")%><br>&nbsp;
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.email.label")%>&nbsp;</td>
        <%
        //Avinash:------------------------------------------
        String email = request.getParameter("email");
        String code = request.getParameter("code");
        if(email==null)
        	email="";
        if(code==null)
        	code="";
        %>
        <td><input type="text" name="email" value="<%= email%>"></td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%" nowrap class="fieldRequired"><%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.code.label")%>&nbsp;</td>
        <td><input type="text" name="verificationCode" value="<%= code %>" ></td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="4">&nbsp;</td></tr>
    
    <tr>
        <td>&nbsp;</td>
        <td colspan="2">            <%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.enter.instruction")%><br>&nbsp;
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td width="10%" class="fieldRequired"><%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.new.label")%></td>
        <td><input type="password" name="password"></td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td class="fieldRequired" nowrap><%=PropertyProvider.get("prm.registration.forgottenpasswordwizard2.confirm.label")%></td>
        <td><input type="password" name="password_2"></td>
        <td>&nbsp;</td>
    </tr>
</table>
</div>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="finish" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS space="personal"/>
</body>
</html>
