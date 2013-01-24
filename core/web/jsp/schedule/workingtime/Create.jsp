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
|   $Revision: 20526 $
|       $Date: 2010-03-04 10:28:11 -0300 (jue, 04 mar 2010) $
|     $Author: nilesh $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Edit Working Time Calendar"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.gui.html.HTMLOptionList,
            net.project.calendar.workingtime.WorkingTimeCalendarCreateHelper,
            net.project.schedule.SchedulePropertiesHelper"
%>
<jsp:useBean id="createHelper" type="net.project.calendar.workingtime.WorkingTimeCalendarCreateHelper" scope="request" />
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<%@ include file="/base/taglibInclude.jsp"%>

<security:verifyAccess action="create" module="<%=Module.SCHEDULE%>" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


<script language="javascript" type="text/javascript">
    var theForm;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';

function setup() {
    theForm = self.document.forms[0];
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=schedule_workingtimecreate";
	openwin_help(helplocation);
}

function cancel() {
    self.document.location = JSPRootURL + '/servlet/ScheduleController/WorkingTime/List?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>';
}

function reset() {
    theForm.reset();
}

function submit() {
    theAction("submit");
    theForm.submit();
}

function selectRadioBase() {
   selectRadio(theForm.elements["calendarType"], "<%=WorkingTimeCalendarCreateHelper.CalendarType.BASE.getID()%>");
}

function selectRadioResource() {
    selectRadio(theForm.elements["calendarType"], "<%=WorkingTimeCalendarCreateHelper.CalendarType.RESOURCE.getID()%>");
}

//To handle form submission on hitting enter key
function onformSubmit(){
	if(theForm.name.value.trim() != ''){
		submit();
	} else {
		extAlert(errorTitle,'<display:get name="prm.schedule.workingtime.create.name.isrequired.message" />',Ext.MessageBox.ERROR);
		return false;
	}
}
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page level="2" display='<%=PropertyProvider.get("prm.schedule.workingtime.create.pagetitle")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/Create" %>'
					queryString='<%="module="+Module.SCHEDULE+"&action="+Action.CREATE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true" />
</tb:toolbar>

<div id='content'>

<br>

<form name="main" method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController/WorkingTime/Create" onSubmit="return onformSubmit();">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.CREATE%>">
    <input type="hidden" name="theAction">
    <input type="hidden" name="calendarType" value="<%=WorkingTimeCalendarCreateHelper.CalendarType.BASE.getID()%>">

    <errors:show scope="request" />

    <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr>
            <td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
            <td nowrap class="channelHeader" align=left colspan="2"><%=PropertyProvider.get("prm.schedule.workingtime.create.pagetitle")%></td>
            <td width="1%" align="right" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
        </tr>

        <tr>
            <td>&nbsp;</td>
            <td colspan="2">

    <table border="0" width="100%">
        <colgroup>
            <col width="1%">
            <col width="25%">
        </colgroup>

        <%-- Base Calendar --%>
        <tr class="tableContent">
            <!-- td><input type="radio" name="calendarType" id="calendarTypeBase" value="<%=WorkingTimeCalendarCreateHelper.CalendarType.BASE.getID()%>" /></td -->
            <td colspan="3" class="fieldRequired"><label for="calendarTypeBase"><display:get name="prm.schedule.workingtime.create.calendartype.base.label" /></label></td>
        </tr>
        <tr class="tableContent">
            <td>&nbsp;</td>
            <td class="fieldNonRequired"><label for="name"><display:get name="prm.schedule.workingtime.create.name.label" /></label></td>
            <td><input type="text" name="name" id="name" value='<c:out value="${getName}"/>' size="20" maxLength="255" onChange="selectRadioBase();" /></td>
        </tr>
        
        <tr class="tableContent">
            <td colspan="3">&nbsp;</td>
        </tr>
        <% if((SchedulePropertiesHelper.PERSONAL_RESOURCE_CALENDAR).equals(schedule.getResourceCalendar())){%>
        
        <tr class="tableContent">
            <td colspan="3" class="fieldRequired" style="padding-left: 50px;"><display:get name="prm.schedule.workingtime.createpersonalresourcecalendar.note.label" /></td>
        </tr>
        <%} else { %>
        <%-- Resource Calendar --%>
        <tr class="tableContent">
            <td><input type="radio" name="calendarType" id="calendarTypeResource" value="<%=WorkingTimeCalendarCreateHelper.CalendarType.RESOURCE.getID()%>" /></td>
            <td colspan="2" class="fieldRequired"><label for="calendarTypeResource"><display:get name="prm.schedule.workingtime.create.calendartype.resource.label" /></label></td>
        </tr>
        <tr class="tableContent">
            <td>&nbsp;</td>
            <td class="fieldNonRequired"><display:get name="prm.schedule.workingtime.create.resource.label" /></td>
            <td>
                <select name="resourceID" onChange="selectRadioResource();">
                    <%=HTMLOptionList.makeHtmlOptionList(createHelper.getAvailableResourceOptions())%>
                </select>
            </td>
        </tr>
        <tr class="tableContent">
            <td>&nbsp;</td>
            <td class="fieldNonRequired"><display:get name="prm.schedule.workingtime.create.parentcalendar.label" /></td>
            <td>
                <select name="parentCalendarID" onChange="selectRadioResource();">
                    <%=HTMLOptionList.makeHtmlOptionList(createHelper.getBaseCalendarOptions())%>
                </select>
            </td>
        </tr>
        <%}%>
    </table>

            </td>
            <td>&nbsp;</td>
        </tr>
    </table>

</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
        <tb:button type="submit"/>
        <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
