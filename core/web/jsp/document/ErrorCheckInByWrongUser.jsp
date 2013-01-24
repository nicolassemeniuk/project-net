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

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Check Out Document"
    language="java" 
    errorPage="DocumentErrors.jsp"
    import="net.project.document.*,
			net.project.base.property.PropertyProvider,
    		net.project.security.SecurityProvider,
    		net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<%
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();

    Document document = (Document) docManager.getCurrentObject();

	if ((module !=docManager.getModuleFromContainerID(document.getContainerID())) || (action != net.project.security.Action.VIEW) || (!id.equals(document.getID()))) 
		throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.errorcheckinbywronguser.authorizationfailed.message"));

    document.setUser( docManager.getUser() );
	
	String[] props = { document.getName() , document.getCheckedOutBy() };
	
%>


<html>
<head>
<title><display:get name="prm.document.errorcheckinbywronguser.label" /></title>
<template:import type="css" src="/styles/global.css" />
<template:import type="css" src="/styles/fonts.css" />

</head>

<body class="main">

<table border=0 cellpadding=0 cellspacing=0 width=80%>
<tr>
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-left.gif" width=41 height=41 alt="" border="0"></td>
	<td class="errorBanner" width=98% colspan="2"><%= document.getName() %> </td>
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
</tr>
<tr>
<td>&nbsp;</td>
<td colspan="2"><%= PropertyProvider.get("prm.document.errorcheckinbywronguser.messsage", props) %></p>
<p><%= (String) session.getValue("exceptionDisplayMessage") %></p>
<hr>
</td>
<td>&nbsp;</td></tr>
  <tr>
  <td>&nbsp;</td>
    <td width="48%" nowrap><font face="Arial, Helvetica, sans-serif" size="-1"><b><display:get name="prm.document.errorcheckinbywronguser.checkedoutby.label" /></b></font></td>
 	<td width="52%"><%= document.getCheckedOutBy() %></td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
  <td>&nbsp;</td>
    <td width="48%" nowrap><font face="Arial, Helvetica, sans-serif" size="-1"><b>
     	<display:get name="prm.document.errorcheckinbywronguser.checkedouton.label" /></b></font></td>
    <td width="52%"><%= document.getCkoDate() %></td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
  <td>&nbsp;</td>
    <td width="48%" nowrap><font face="Arial, Helvetica, sans-serif" size="-1"><b><display:get name="prm.document.errorcheckinbywronguser.returndate.label" /></b></font></td>
    <td width="52%"><%= document.getCkoReturn() %></td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
  <td>&nbsp;</td>
    <td width="48%" nowrap><font face="Arial, Helvetica, sans-serif" size="-1"><b><display:get name="prm.document.errorcheckinbywronguser.userscomment.label" /></b></font></td>
    <td width="52%"><%= document.getNotes() %></td>
    <td>&nbsp;</td>
  </tr>
  
  <tr>
  	<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
  	<td class="errorBanner" width=98% align=right colspan="2"><a href="javascript:self.close();" class="errorLink"><display:get name="prm.document.errorcheckinbywronguser.close.button.label" /></a></td>
  	<td><a href="javascript:self.close();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
  </tr>

</table>

<p>&nbsp;</p>
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
