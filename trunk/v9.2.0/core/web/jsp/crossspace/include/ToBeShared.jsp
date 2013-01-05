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
    info=""
    contentType="text/html; charset=UTF-8"
    language="java"
    errorPage="/errors.jsp"
    import="java.util.Iterator,
            net.project.crossspace.IExportableObject"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="objectsToShare" type="java.util.List" scope="request" />

<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript" type="text/javascript">
</script>

</head>

<body>
    <table>
        <% for (Iterator it = objectsToShare.iterator(); it.hasNext();) { %>
                    <tr><td class="tableContent"><li>
        <%
                IExportableObject exportableObject = (IExportableObject)it.next();
        		//bfd 3226: workspaces 'or' no User's are selected throws NPE
                out.println(exportableObject!=null?exportableObject.getName():"Schedule");
        %>
                    </li></td></tr>
        <% } %>
    </table>
<template:getSpaceJS />
</body>
</html>
