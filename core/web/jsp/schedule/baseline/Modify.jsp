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
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.security.Action,
            net.project.base.Module,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="baseline" class="net.project.schedule.Baseline" scope="request" />
<jsp:useBean id="reloadParams" class="java.lang.String" scope="request"/>
<jsp:useBean id="reloadURL" class="java.lang.String" scope="request"/>
<jsp:useBean id="action" class="java.lang.String" scope="request"/>
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request"/>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />

<script language="javascript" type="text/javascript">

var theForm;
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
    theForm = self.document.forms[0];
}

function reset() {
   /*bfd-5136 fix - Avinash : following link works if only modify baseline, but problem in refreshing while new baseline creation
   *self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Baseline/Modify?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>&baselineID=<%=baseline.getID()%>";
   */
    theForm.reset();
}

function cancel() {
    //history.back(); bfd-5138 fix
    self.document.location = "<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/Baseline/List?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>";
}

function submit() {
    if(validateForm(theForm)) {
		theForm.submit();
    }
}

function recalculate() {
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Baseline/RefreshBaselinePlan?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>&baselineID=<%=baseline.getID()%>";
}

function validateForm(frm) {
    if (!checkTextbox(frm.name,'<%=PropertyProvider.get("prm.schedule.baseline.create.name.required.message")%>')) return false;
    if (!checkMaxLength(frm.description,1000,'<%=PropertyProvider.get("prm.schedule.baselineedit.descriptionsize.message")%>')) return false;
    return true;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=schedule_baseline";
	openwin_help(helplocation);
}
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="@prm.schedule.baseline.create.pagetitle"
					jspPage='<%=reloadURL%>'
					queryString='<%=reloadParams%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/Baseline/CreateProcessing">
<input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
<input type="hidden" name="action" value="<%=request.getAttribute("action")%>">
<% if (request.getAttribute("action").equals(String.valueOf(Action.MODIFY))) { %>
<input type="hidden" name="baselineID" value="<%=baseline.getID()%>">
<% } %>

<errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="request"/>
<%-- Provide a div for server round-trip error messaging --%>
<div id="errorLocationID" class="errorMessage"></div>

<channel:channel name="baselineModify" customizable="false">
    <channel:insert name="baseline" width="97%" minimizable="false"
        titleToken="prm.schedule.baseline.create.pagetitle">
        <display:if condition="<%=action.equals(String.valueOf(Action.MODIFY))%>"> 
            <channel:button type="recalculate" labelToken="prm.schedule.baseline.create.replacewithcurrent.label"/>
        </display:if>
        <table border="0" width="100%">
            <tr>
              <td class="tableHeader" class="fieldRequired"><display:get name="prm.schedule.baseline.create.name"/></td>
              <td class="tableContent"><input type="text" name="name" length="40" maxlength="255"<%=baseline.getName() != null? " value=\""+baseline.getName()+"\"" : ""%>></td>
            </tr>
            <tr class="tableContent">
              <td><display:get name="prm.schedule.baseline.create.description"/></td>
              <td><textarea name="description" cols="40" rows="4"><%=baseline.getDescription() != null ? baseline.getDescription() : ""%></textarea></td>
            </tr>
            <tr class="tableHeader">
              <td colspan="2"><input type="checkbox" name="isDefaultForObject" value="true"<%=baseline.isDefaultForObject()?" checked" : ""%>><display:get name="prm.schedule.baseline.create.isdefaultbaseline"/></td>
            </tr>
        </table>
    </channel:insert>
</channel:channel>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
        <tb:button type="submit"/>
        <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
