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
|   $Revision: 15794 $
|       $Date: 2007-04-07 10:41:46 +0530 (Sat, 07 Apr 2007) $
|     $Author: vmalykhin $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="LDAP Configuration Tester" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.SessionManager,
            net.project.security.Action,
            net.project.base.Module,
            net.project.base.directory.ldap.LDAPConfigurationTester"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" scope="session" /> 

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    // This page serves two purposes; it is a form for doing the test and also
    // displays test results
    boolean isResultAvailable = false;
    LDAPConfigurationTester tester = new LDAPConfigurationTester((net.project.base.directory.ldap.LDAPDirectoryConfiguration) domain.getDirectoryConfiguration());
    pageContext.setAttribute("tester", tester, PageContext.PAGE_SCOPE);
    
    String theAction = request.getParameter("theAction");
    
    if (theAction != null && theAction.equals("testAuthenticate")) {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        tester.testAuthenticate(login, password);
        isResultAvailable = true;
    }
%>
<template:getDoctype />
<html>
<head>
<title>Test LDAP Directory Configuration</title> 

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<template:getSpaceCSS />
     

<script language="javascript">

    var theForm;
    var isLoaded = false;
    var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

    function setup() {
	    theForm = self.document.forms["ldapTest"];
        focusFirstField(theForm);
        isLoaded = true;
    }

    function cancel() {
	    self.document.location = JSPRootURL + '/admin/domain/AuthenticatorConfigurationEdit.jsp?module=<%=net.project.base.Module.APPLICATION_SPACE%>&action=<%=net.project.security.Action.MODIFY%>&domainID=<%=domain.getID()%>';
    }


    function testAuthenticate() {
        theAction("testAuthenticate");
        theForm.submit();
    }

    function testSearch() {
        theAction("testSearch");
        theForm.submit();
    }
    

</script>

</head>

<body class="main" onLoad="setup();"  id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='content'>
<form name="ldapTest" action="<%=SessionManager.getJSPRootURL()%>/admin/domain/ldap/ConfigurationTest.jsp" method="post">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=Module.APPLICATION_SPACE%>">
	<input type="hidden" name="action" value="<%=Action.MODIFY%>">

<table width="100%" border="0">
<tr>
	<td align="left" class="pageTitle">Test LDAP Directory Configuration</td>
	<td align="right" class="pageTitle">&nbsp;</td>
</tr>
</table>

<br>

<div align="center">
<table border="0" align="left" cellpadding="0" cellspacing="0" width="80%">

<%-- Display results if we've just done a test --%>
<%  if (isResultAvailable) { %>
    <tr align="left"> 
        <td>&nbsp;</td>
        <td class="tableHeader" colspan="2">Test Results</td>
        <td>&nbsp;</td>
    </tr>
    <tr align="left"> 
        <td>&nbsp;</td>
        <td class="tableContent" colspan="2">
            <pnet-xml:transform name="tester" scope="page" stylesheet="/admin/domain/ldap/xsl/ConfigurationTestResults.xsl" />
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="4">&nbsp;</td></tr>
<%  } %>    

    <%-- Test Authentication --%>
	<tr class="channelHeader">
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td  nowrap  colspan="2"class="channelHeader"><nobr>Test Authentication</nobr></td>
		<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr> 
    <tr align="left"> 
        <td>&nbsp;</td>
        <td class="tableContent" colspan="2">
            Enter a login name and password to test authentication against the configured LDAP servers.
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr align="left"> 
        <td>&nbsp;</td>
        <td nowrap class="fieldRequired">Login Name:</td>
        <td class="fieldRequired"> 
            <input type="text" name="login" size="20" maxlength="80">
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr align="left"> 
        <td>&nbsp;</td>
        <td nowrap class="fieldRequired">Password:</td>
        <td class="fieldRequired"> 
            <input type="password" name="password" size="20" maxlength="80">
        </td>
        <td>&nbsp;</td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true"  bottomFixed="true">
			<tb:band name="action">
				<tb:button type="submit" label="Authenticate" function="javascript:testAuthenticate();" />
			</tb:band>
</tb:toolbar>

</div>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
