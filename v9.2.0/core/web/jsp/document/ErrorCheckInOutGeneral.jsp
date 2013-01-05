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
    		net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<html>
<head>
<title><display:get name="prm.document.errorcheckinoutgeneral.title" /></title>
<template:import type="css" src="/styles/global.css" />
<template:import type="css" src="/styles/fonts.css" />

</head>

<body class="main">

<table border=0 cellpadding=0 cellspacing=0 width=80%>
<tr>
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-left.gif" width=41 height=41 alt="" border="0"></td>
	<td class="errorBanner" width=98% colspan="2">&nbsp; </td>
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
</tr>
<tr>
<td>&nbsp;</td>
<td colspan="2"> <%= (String) session.getValue("exceptionDisplayMessage") %></p>
</td>
    <td>&nbsp;</td>
  </tr>
  
  <tr>
  	<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
  	<td class="errorBanner" width=98% align=right colspan="2"><a href="javascript:self.close();" class="errorLink"><display:get name="prm.document.errorcheckinoutgeneral.close.button.label" /></a></td>
  	<td><a href="javascript:self.close();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
  </tr>
</table>
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>
