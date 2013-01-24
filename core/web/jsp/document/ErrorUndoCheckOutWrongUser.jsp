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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Check Out Document"
    language="java" 
    errorPage="DocumentErrors.jsp"
    import="net.project.document.*,
    net.project.security.SecurityProvider,
    net.project.security.User,
	net.project.base.property.PropertyProvider,
	net.project.security.SessionManager"

%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 

<%
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();
    
    Document document = (Document) docManager.getCurrentObject();
	
    if ((module != docManager.getModuleFromContainerID(document.getContainerID())) || (action != net.project.security.Action.VIEW) || (!id.equals(document.getID()))) 
		throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.errorundocheckoutwronguser.authorizationfailed.message"));	   

    document.setUser( docManager.getUser() );
	
	String[] props = { document.getName() , document.getCheckedOutBy() };
	
%>


<html>
<head>
<title><display:get name="prm.document.errorundocheckoutwronguser.title" /></title>
<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<template:import type="javascript" src="/src/util.js" />

<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />
<script language="javascript">
    var theForm;
    var isLoaded = false;
    var errorMsg;

    function setup() {

	theForm = self.document.forms[0];
        isLoaded = true;
    }

    function spaceAdministratorOverride() {

	theForm.ignoreUserMismatchConstraint.value = "true";

	theAction ("undo_check_out");
	theForm.submit();
    }
</script>

</head>

<body class="main" onload="setup();">

<form name="checkoutErrorForm" method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker"> 
    <input type="hidden" name="module" value="<%= module %>">
    <input type="hidden" name="theAction">
    <input type="hidden" name="objectID" value="<%= document.getID() %>">
    <input type="hidden" name="ignoreUserMismatchConstraint">

<table border=0 cellpadding=0 cellspacing=0 width=80%>
<tr>
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-left.gif" width=41 height=41 alt="" border="0"></td>
	<td class="errorBanner" width=98% colspan="2"><display:get name="prm.document.errorundocheckoutwronguser.channel.undocheckout.title" /></td>
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
</tr>
<tr>
<td>&nbsp;</td>
<td colspan="2"><p><%= PropertyProvider.get("prm.document.errorundocheckoutwronguser.undofailed.message") %></p>
<p><%= (String) session.getValue("exceptionDisplayMessage") %></p>
</td>
<td>&nbsp;</td></tr>

<%
     if (user.isSpaceAdministrator()) {
%>
<tr>
  <td colspan="4">&nbsp;</td>
</tr>

  <tr> 
  <td>&nbsp;</td>
    <td colspan="3"><font face="Arial, Helvetica, sans-serif" size="-1">
    <b><a href="javascript:spaceAdministratorOverride()"><display:get name="prm.document.errorundocheckoutwronguser.override.link" /></a></b></font>
    </td>
  </tr>
<%
     }
%>

  <tr>
  	<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
  	<td class="errorBanner" width=98% align=right colspan="2"><a href="javascript:self.close();" class="errorLink"><display:get name="prm.document.errorundocheckoutwronguser.close.link" /></a></td>
  	<td><a href="javascript:self.close();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
  </tr>

</table>
</form>
<p>&nbsp;</p>
</body>
</html>
