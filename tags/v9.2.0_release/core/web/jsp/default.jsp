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
<html>
<head>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Bad Browser... sit."
    language="java"
    errorPage="/errors.jsp"
	import="net.project.base.property.PropertyProvider"
 %>
<title><%=PropertyProvider.get("prm.project.main.welcome.label")%></title>
</head>

<body bgcolor="#FFFFFF">
<div align="center">
  <table border="0" cellpadding="0" cellspacing="0" width="100%" height="95%">
    <tr>
      <td width="100%" valign="middle" align="center"><a href="NavigationFrameset.jsp"><img border="0" src="/images/pnet_logo130_white.gif" alt = "<%=PropertyProvider.get("prm.project.main.click.enter.application.alt")%>" title = "<%=PropertyProvider.get("prm.project.main.click.enter.application.alt")%>"></a></td>
    </tr>
  </table>
</div>
</body>
</html>
