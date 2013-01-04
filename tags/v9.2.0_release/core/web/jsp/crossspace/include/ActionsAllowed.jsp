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
    import="net.project.base.property.PropertyProvider,
            java.net.URLEncoder,
            net.project.security.SessionManager,
            net.project.security.Action,
            net.project.base.Module,
            java.net.URLEncoder,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="filter" type="net.project.taglibs.input.InputTagFilter" scope="request" />
<jsp:useBean id="isContainer" type="java.lang.Boolean" scope="request"/>
<html>
<head>
<title></title>

<%-- import javascript --%>
<template:import type="javascript" src="/src/sharing.js"/>

<script language="javascript" type="text/javascript">
</script>

</head>
<body>
    <div class="tableContent">
        <input:checkbox elementID="allowableAction_1" name="allowableAction" value="1" filter="<%=filter%>"/><label for="allowableAction_1"><display:get name="prm.sharing.actionsallowed.share"/></label><br/>
        <%-- TODO:  read-only task share not supported yet.  buggy. --%>
<%--        
        <input:checkbox elementID="allowableAction_2" name="allowableAction" value="2" filter="<%=filter%>"/><label for="allowableAction_2"><display:get name="prm.sharing.actionsallowed.sharereadonly"/></label><br/>
--%>
        <input:checkbox elementID="allowableAction_4" name="allowableAction" value="4" filter="<%=filter%>"/><label for="allowableAction_4"><display:get name="prm.sharing.actionsallowed.copy"/></label><br/>
        <% if (!isContainer.booleanValue()) { %>
            <input:checkbox elementID="allowableAction_8" name="allowableAction" value="8" filter="<%=filter%>"/><label for="allowableAction_8"><display:get name="prm.sharing.actionsallowed.move"/></label><br/>
        <% } %>
    </div>
</body>
</html>