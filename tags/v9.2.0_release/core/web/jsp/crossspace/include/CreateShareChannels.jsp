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
            net.project.security.Action,
            net.project.base.Module,
            java.net.URLEncoder,
            net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="hierarchy" class="java.lang.String" scope="request" />
<jsp:useBean id="idList" class="java.lang.String" scope="request" />

<template:import type="javascript" src="/src/sharing.js"/>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/CrossSpaceController/CreateShareProcessing">
    <input type="hidden" name="selectedIDs" value="<%=idList%>">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.SHARE%>">
    <input type="hidden" name="hierarchy" value="<%=hierarchy%>">
    <input type="hidden" name="referrer" value="<%=URLEncoder.encode((String)request.getAttribute("referrer"), SessionManager.getCharacterEncoding())%>">

<div id="errorsDiv">
	<errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="session"/>
</div>

<channel:channel name="CreateShare" customizable="false">
    <channel:insert name="objectsToBeShared" title='<%=PropertyProvider.get("prm.crossspace.createshare.objectsshared.label")%>'
        row="0" column="0" width="40%" minimizable="false" closeable="false"
        include="/crossspace/include/ToBeShared.jsp"/>
    <channel:insert name="AllowedActions" title='<%=PropertyProvider.get("prm.crossspace.createshare.allowedactions.label")%>'
        row="0" column="1" width="60%" minimizable="false" closeable="false"
        include="/crossspace/include/ActionsAllowed.jsp"/>
    <channel:insert name="SharingPermissions" title='<%=PropertyProvider.get("prm.crossspace.createshare.sharingpermissions.label")%>'
        row="1" column="0" width="100%" minimizable="false" closeable="false"
        include="/crossspace/include/CreateShare.jsp"/>
</channel:channel>

</form>

