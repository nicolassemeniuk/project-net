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
    info="TC Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,net.project.util.Version,net.project.base.property.PropertyProvider" 
 %>
<%@ include file="/base/taglibInclude.jsp" %>

<html>
   <head>
	<title>Project.net Help</title>
	<link href="<%=SessionManager.getJSPRootURL()%>/styles/fonts.css" rel="stylesheet" rev="stylesheet" type="text/css">
	<link href="<%=SessionManager.getJSPRootURL()%>/styles/help.css" rel="stylesheet" rev="stylesheet" type="text/css">
   </head>

   <body class="main" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
 	<table width=100% cellpadding=1 cellspacing=0 border=0>
 	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="right" class="regBanner"><display:get name="prm.global.registration.termsofuse.banner" />&nbsp;</td>
 	</tr>
	<tr>
		<td colspan="2" class="navBg"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width=10 height=25 alt="" border="0"></td>
	</tr>
	</table>  

<%-- Display the Terms of Use text is printable format --%>
	<blockquote>
		<b>TERMS OF USE</b>
		<p>
			<display:get name="@prm.global.legal.termsofuse"/>
	</blockquote>

	<%@ include file="/help/include_outside/footer.jsp" %>

   </body>
</html>

