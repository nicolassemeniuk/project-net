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
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            java.net.URLEncoder,
            java.util.Date,
            net.project.gui.history.History,
            net.project.base.property.PropertyProvider,
            java.io.StringWriter,
            java.io.PrintWriter"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
    Throwable throwable = (Throwable)session.getAttribute("reportedException");


    /* Be very careful about tokens on this screen.  If they cannot be loaded
       for some reason, the user wouldn't be able to report the error. */
    String currentPathInfoLabel, stackTraceLabel, emailLabel, currentSpaceLabel,
        currentSpaceIDLabel, serverNameLabel, browserInfoLabel, userIDLabel,
        nameLabel, currentDateLabel, userNameLabel, showContentsLabel,
        hideContentsLabel, instructions, bugReportLabel, errorDescriptionLabel,
        reproductionStepsLabel, additionalInfoLabel;

    String userName, currentDate, userDisplayName, userID, browserInfo, 
        serverName, currentSpaceID, currentSpaceName, email, historyXML;
    
    try {
        errorDescriptionLabel = PropertyProvider.get("prm.base.reporterrors.errordescription.label");
        reproductionStepsLabel = PropertyProvider.get("prm.base.reporterrors.reproductionsteps.label");
        additionalInfoLabel = PropertyProvider.get("prm.base.reporterrors.additionalinfo.label");
        bugReportLabel = PropertyProvider.get("prm.base.reporterrors.bugreport.label");
        instructions = PropertyProvider.get("prm.base.reporterrors.instructions");
        hideContentsLabel = PropertyProvider.get("prm.base.reporterrors.hidecontents.message");
        showContentsLabel = PropertyProvider.get("prm.base.reporterrors.showcontents.message");
        userNameLabel = PropertyProvider.get("prm.base.reporterrors.username.label");
        currentDateLabel = PropertyProvider.get("prm.base.reporterrors.currentdate.label");
        nameLabel = PropertyProvider.get("prm.base.reporterrors.name.label");
        userIDLabel = PropertyProvider.get("prm.base.reporterrors.userid.label");
        browserInfoLabel = PropertyProvider.get("prm.base.reporterrors.browserinfo.label");
        serverNameLabel = PropertyProvider.get("prm.base.reporterrors.servername.label");
        currentSpaceIDLabel = PropertyProvider.get("prm.base.reporterrors.currentspaceid.label");
        currentSpaceLabel = PropertyProvider.get("prm.base.reporterrors.currentspacename.label");
        emailLabel = PropertyProvider.get("prm.base.reporterrors.email.label");
        stackTraceLabel = PropertyProvider.get("prm.base.reporterrors.stacktrace.label");
        currentPathInfoLabel = PropertyProvider.get("prm.base.reporterrors.currentpathinfo.label");

        if (user != null) {
            userName = user.getUserName();
            userDisplayName = user.getDisplayName();
            userID = user.getID();
            email = user.getEmail();

            if (user.getCurrentSpace() != null) {
                currentSpaceID = user.getCurrentSpace().getID();
                currentSpaceName = user.getCurrentSpace().getName();
            } else {
                currentSpaceID = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
                currentSpaceName = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
            }
        } else {
            userName = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
            userDisplayName = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
            userID = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
            email = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
            currentSpaceID = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
            currentSpaceName = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
        }

        currentDate = new Date().toString();
        browserInfo = request.getHeader("User-Agent");
        if (browserInfo == null)
            browserInfo = request.getHeader("HTTP_USER_AGENT");
        if (browserInfo == null)
            browserInfo = request.getHeader("Browser not reporting User-Agent");
        serverName = request.getServerName();

        History history = (History)session.getAttribute("historyTagHistoryObject");
        historyXML = history.getXMLBody();
    } catch (Exception e) {
        errorDescriptionLabel = PropertyProvider.get("prm.base.reporterrors.error.description.label");
        reproductionStepsLabel = PropertyProvider.get("prm.base.reporterrors.reproduction.steps.label");
        additionalInfoLabel = "";
        bugReportLabel = PropertyProvider.get("prm.base.reporterrors.bug.report.label");
        instructions = PropertyProvider.get("prm.base.reporterrors.instructions.label");
        hideContentsLabel = PropertyProvider.get("prm.base.reporterrors.click.here.report.label");
        showContentsLabel = PropertyProvider.get("prm.base.reporterrors.click.here.report.label");
        userNameLabel = PropertyProvider.get("prm.base.reporterrros.username.label");
        currentDateLabel = PropertyProvider.get("prm.base.reporterrors.current.date.label");
        nameLabel = PropertyProvider.get("prm.base.reporterrors.name.label");
        userIDLabel = PropertyProvider.get("prm.base.reporterrors.user.id.label");
        browserInfoLabel = PropertyProvider.get("prm.base.reporterrors.browser.info.label");
        serverNameLabel = PropertyProvider.get("prm.base.reporterrors.server.name.label");
        currentSpaceIDLabel = PropertyProvider.get("prm.base.reporterrors.current.space.id.label");
        currentSpaceLabel = PropertyProvider.get("prm.base.reporterrors.current.space.label");
        emailLabel = PropertyProvider.get("prm.base.reporterrros.email.label");
        stackTraceLabel = PropertyProvider.get("prm.base.reporterrors.stacktrace.label");
        currentPathInfoLabel = PropertyProvider.get("prm.base.reporterrors.current.path.info.label");
        userName = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
        currentDate = new Date().toString();
        userDisplayName = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
        userID = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
        currentSpaceID = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
        currentSpaceName = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
        email = PropertyProvider.get("prm.base.reporterrors.unavailable.label");
        browserInfo = PropertyProvider.get("prm.base.reporterrors.user.agent.unavailable.label");
        serverName = PropertyProvider.get("prm.base.reporterrors.server.name.unavailable.label");
        historyXML = "";
    } 
%>

<html>
<head>
<title></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<style>
.visible {
    visibility: visible;
}

.hidden {
    display: none;
    border-collapse: collapse;
}
</style>

<script language="javascript" type="text/javascript">
function toggleVisibility() {
    var addlInfo = document.getElementById("additionalInfo");
    var hideShowInstructions = document.getElementById("hideShowInstructions");

    if (addlInfo.className == 'visible') {
        addlInfo.className = 'hidden';
        hideShowInstructions.innerHTML = '<%=showContentsLabel%>';
    } else {
        addlInfo.className = 'visible';
        hideShowInstructions.innerHTML = '<%=hideContentsLabel%>';
    }
}

function submit() {
    self.document.forms[0].submit();
}

function cancel() {
    history.back();
}
</script>

</head>

<body class="main">

<form action="ReportErrorProcessing.jsp" method="post">
<input type="hidden" name="userName" value="<%=URLEncoder.encode(userName, SessionManager.getCharacterEncoding())%>">
<input type="hidden" name="currentDate" value="<%=URLEncoder.encode(currentDate, SessionManager.getCharacterEncoding())%>">
<input type="hidden" name="userDisplayName" value="<%=URLEncoder.encode(userDisplayName, SessionManager.getCharacterEncoding())%>">
<input type="hidden" name="userID" value="<%=URLEncoder.encode(userID, SessionManager.getCharacterEncoding())%>">
<input type="hidden" name="browserInfo" value="<%=URLEncoder.encode(browserInfo, SessionManager.getCharacterEncoding())%>">
<input type="hidden" name="serverName" value="<%=URLEncoder.encode(serverName, SessionManager.getCharacterEncoding())%>">
<input type="hidden" name="currentSpaceID" value="<%=URLEncoder.encode(currentSpaceID, SessionManager.getCharacterEncoding())%>">
<input type="hidden" name="currentSpaceName" value="<%=URLEncoder.encode(currentSpaceName, SessionManager.getCharacterEncoding())%>">
<input type="hidden" name="email" value="<%=URLEncoder.encode(email, SessionManager.getCharacterEncoding())%>">


<table border="0" width="500">
<tr>
    <td width="133" rowspan="2">
        <img src="<%=SessionManager.getJSPRootURL()%>/images/bugreport.gif" height="154" width="128">
    </td>
    <td class="tableContent">
    <div class="tableHeader"><%=bugReportLabel%></div><br>
    <%=instructions%>
    </td>
</tr>
<tr>
    <td class="tableContent">
      <a href="javascript:toggleVisibility();">
      <div id="hideShowInstructions"><%=showContentsLabel%></div>
      </a>
    </td>
</tr>
</table>

<br>
<table border="0" width="97%">
<tr>
    <td colspan="6">
    <table border="0" cellspacing="0" cellpadding="0" vspace="0" width="100%">
    <tr>
    <td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
    <td nowrap class="channelHeader" align=left><%=bugReportLabel%></td>
    <td width="1%" align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
    </tr>
    </table>
    </td>
</tr>
<tr>
    <td width="1%">&nbsp;</td>
    <td class="tableHeader" valign="top"><%=errorDescriptionLabel%></td>
    <td colspan="4"><textarea name="errorDescription" cols="100" rows="5"></textarea></td>
</tr>
<tr>
    <td width="1%">&nbsp;</td>
    <td class="tableHeader" valign="top"><%=reproductionStepsLabel%></td>
    <td colspan="4"><textarea name="reproductionSteps" cols="100" rows="5"></textarea></td>
</tr>
</table>
<table border="0" width="97%" id="additionalInfo" class="hidden">
<tr>
    <td colspan="5">
    <table border="0" cellspacing="0" cellpadding="0" vspace="0" width="100%">
    <tr>
    <td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
    <td nowrap class="channelHeader" align=left><%=additionalInfoLabel%></td>
    <td width=1% align=right class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
    </tr>
    </table>
    </td>
</tr>
<tr>
    <td width="1%">&nbsp;</td>
    <td class="tableHeader" width="15%"><%=userNameLabel%></td>
    <td class="tableContent"><%=userName%></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader"><%=currentDateLabel%></td>
    <td class="tableContent"><%=currentDate%></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader"><%=nameLabel%></td>
    <td class="tableContent"><%=userDisplayName%></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader" width="20%"><%=userIDLabel%></td>
    <td class="tableContent"><%=userID%></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader"><%=browserInfoLabel%></td>
    <td class="tableContent"><%=browserInfo%></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader"><%=serverNameLabel%></td>
    <td class="tableContent"><%=serverName%></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader"><%=currentSpaceIDLabel%></td>
    <td class="tableContent"><%=currentSpaceID%></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader"><%=currentSpaceLabel%></td>
    <td class="tableContent"><%=currentSpaceName%></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader"><%=emailLabel%></td>
    <td class="tableContent"><%=email%></td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader" valign="top"><%=stackTraceLabel%></td>
    <td colspan="3">
    <textarea name="stackTrace" cols="100" rows="5" readonly>
    <%
        if (throwable != null) {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            out.write(writer.toString());
        }
    %>
    </textarea>
    </td>
</tr>
<tr>
    <td></td>
    <td class="tableHeader" valign="top"><%=currentPathInfoLabel%></td>
    <td colspan="3">
    <textarea name="history" cols="100" rows="5" readonly>
    <%= historyXML %>
    </textarea>
    </td>
</tr>

</table>

<tb:toolbar style="action" showLabels="true" width="97%">
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
