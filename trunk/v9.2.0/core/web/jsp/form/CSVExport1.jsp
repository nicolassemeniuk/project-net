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
    info="Choose character set in which to download CSV data"
    language="java"
    errorPage="/errors.jsp"
	import="net.project.security.SessionManager,
            net.project.form.FormListCSVDownload,
            net.project.form.Form,
            net.project.gui.html.HTMLOptionList,
            net.project.datatransform.csv.CSVCharacterSet,
            net.project.base.Module,
            net.project.security.Action,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<jsp:useBean id="formListCSVDownload" class="net.project.form.FormListCSVDownload" scope="session" />

<security:verifyAccess module="<%=net.project.base.Module.FORM%>"
                       action="view"
                       objectID='<%=form.getID()%>' />
<html>
<head>

<template:getSpaceCSS />

<template:import type="javascript" src="/src/util.js"/>
<template:import type="javascript" src="/src/window_functions.js"/>
<script language="javascript">
var theForm;

function setup() {
	theForm = self.document.forms[0];
}

function next() {
    theForm.submit();
}

function cancel() {
    window.close();
}
</script>

</head>

<body class="main" onLoad="setup();">

<table width="100%" border="0">
<tr><td class="tableHeader">
<%=PropertyProvider.get("prm.global.form.list.export.charset.pagetitle")%>
</td></tr>
<tr><td class="tableContent">
<%=PropertyProvider.get("prm.global.form.list.export.charset.instructions")%>
</td></tr>
<tr><td class="tableContent">
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/form/CSVExport2.jsp">
    <input type="hidden" name="module" value="<%=Module.FORM%>">
    <input type="hidden" name="action" value="<%=Action.VIEW%>">
    <input type="hidden" name="id" value="<%=form.getID()%>">

    <select name="characterSet">
        <%=HTMLOptionList.makeHtmlOptionList(CSVCharacterSet.getSupportedCharsets())%>
    </select>
</form>
</td></tr>
</table>

<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
		<tb:button type="next" />
	</tb:band>
</tb:toolbar>

</body>
</html>