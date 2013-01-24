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
|   $Revision: 18948 $
|       $Date: 2009-02-21 09:39:24 -0200 (sáb, 21 feb 2009) $
|     $Author: ritesh $
|
|--------------------------------------------------------------------%>
<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="shell" class="net.project.admin.debuggingshell.Shell" scope="request" />

<html>
<head>
<title>Debugging Shell</title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript" type="text/javascript">
function cancel() {
    history.back();
}
</script>

</head>

<body>
<%
    String commands = request.getParameter("commands");
    if (commands != null) {
       // shell.executeCommands(commands, response);
    }
%>

<form action="<%=SessionManager.getJSPRootURL()%>/admin/utilities/DebuggingShell.jsp" method="post">
<input type="hidden" name="module" value="<%=Module.APPLICATION_SPACE%>">
<textbox name="commands">
<%
    if (commands != null) {
        out.print(commands);
    }
%>
</textbox>

</form>


<template:getSpaceJS />
</body>
</html>
