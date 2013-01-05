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
    info="Schedule Working Times"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.gui.html.HTMLOptionList"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="listHelper" type="net.project.calendar.workingtime.WorkingTimeCalendarListHelper" scope="request" />
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<security:verifyAccess action="view" module="<%=Module.SCHEDULE%>" />

<!DOCTYPE html public "-//W3C//DTD html 4.0 Transitional//EN">
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<script language="javascript" type="text/javascript">
    var theForm;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';

function setup() {
    theForm = self.document.forms[0];
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=schedule_workingtimelist";
	openwin_help(helplocation);
}

function cancel() {
    self.document.location = JSPRootURL + '/workplan/taskview?action=<%=Action.VIEW%>&module=<%=Module.SCHEDULE%>';
}

function reset() {
    self.document.location = JSPRootURL + '/servlet/ScheduleController/WorkingTime/List?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>';
}

function create() {
    theAction("create");
    theForm.submit();
}

function modify() {
    if (verifySelection(theForm, 'single', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        theAction("modify");
        theForm.submit();
    }
}

function remove() {
    if (verifySelection(theForm, 'single', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
    Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.schedule.workingtime.list.removeconfirm.message")%>', function(btn) { 
		if(btn == 'yes'){ 
			theAction("remove");
            theForm.submit();
		}else{
		 	return false;
		}
	});
   }
}

function changeDefault() {
    var selectedCalendarID = getSelectedValue(theForm.defaultCalendarID);
    if (!selectedCalendarID || selectedCalendarID == '') {
    	var errorMessage = '<display:get name="prm.schedule.workingtime.list.javascript.changedefaultnocalendar.error.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
    } else {
        theAction("changeDefault");
        theForm.handlerName.value = "/WorkingTime/List";
        theForm.submit();
    }
}

function submit() {
	if(validateForm(theForm)) {
	    theAction("workingtimes");
	    theForm.submit();
    }
}
function validateForm(theForm) {
    if (!checkNumeric(theForm.hoursPerDay,'<display:get name="prm.schedule.properties.hoursperday.mumericrequired.message"/>')) return false;
    if(theForm.hoursPerDay.value <=0 ||  theForm.hoursPerDay.value > 24){
    	extAlert(errorTitle, '<display:get name="prm.schedule.properties.hoursperday.range.message"/>' , Ext.MessageBox.ERROR);
    	theForm.hoursPerDay.focus()
    	return false;
    }
    if (!checkNumeric(theForm.hoursPerWeek,'<display:get name="prm.schedule.properties.hoursperweek.mumericrequired.message"/>')) return false;
    if(theForm.hoursPerWeek.value <=0 ||  theForm.hoursPerDay.value > 186){
    	extAlert(errorTitle, '<display:get name="prm.schedule.properties.hoursperweek.range.message"/>' , Ext.MessageBox.ERROR);
    	theForm.hoursPerWeek.focus()
    	return false;
    }
    if (!checkNumeric(theForm.daysPerMonth,'<display:get name="prm.schedule.properties.dayspermonth.mumericrequired.message"/>')) return false;
    if(theForm.daysPerMonth.value <=0 ||  theForm.hoursPerDay.value > 30){
    	extAlert(errorTitle, '<display:get name="prm.schedule.properties.daysspermonth.range.message"/>' , Ext.MessageBox.ERROR);
    	theForm.daysPerMonth.focus()
    	return false;
    }
    return true;
}
</script>

</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page displayToken='prm.schedule.workingtime.list.pagetitle'
					jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/List" %>'
					queryString='<%="module="+Module.SCHEDULE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
        <tb:button type="create" />
        <tb:button type="modify" />
        <tb:button type="remove" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>
<tab:tabStrip width="97%">
    <tab:tab labelToken='prm.schedule.properties.pagetitle' href='<%=SessionManager.getJSPRootURL() + "/schedule/properties/ScheduleProperties.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY %>'/>
    <tab:tab labelToken='prm.schedule.properties.changeworkingtimes.link' href='<%=SessionManager.getJSPRootURL() + "/servlet/ScheduleController/WorkingTime/List?module="+Module.SCHEDULE+"&action="+Action.VIEW%>' selected="true"/>
    <tab:tab labelToken='prm.schedule.properties.history.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/ScheduleHistory.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.properties.baseline' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/Baseline/List?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <tab:tab labelToken='prm.schedule.tasklistdecorating.pagetitle' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/TaskListDecorating.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
    <display:if name="@prm.crossspace.isenabled">
    <tab:tab labelToken='prm.schedule.properties.sharing' href='<%=SessionManager.getJSPRootURL()+"/servlet/ScheduleController/ScheduleProperties/Sharing?module="+Module.SCHEDULE+"&action="+Action.SHARE%>'/>
    </display:if>
    <tab:tab labelToken='prm.schedule.properties.tools.link' href='<%=SessionManager.getJSPRootURL()+"/schedule/properties/Tools.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>'/>
</tab:tabStrip>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/ScheduleController">
    <input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
    <input type="hidden" name="action" value="<%=Action.MODIFY%>">
    <input type="hidden" name="theAction">
    <input type="hidden" name="handlerName" value="/WorkingTime/Edit">

    <errors:show scope="request" />

    <table border="0" cellspacing="0" cellpadding="0" width="97%">

        <tr>
            <td>&nbsp;</td>
            <td colspan="2">
            <%-- Schedule Default Calendar Selection --%>
            <table width="100%">
                <colgroup span="2" width="0*" />
                <tr>
                    <td width="25%" class="tableHeader"><label for="defaultCalendarID"><display:get name="prm.schedule.workingtime.list.scheduledefault.label" /></label></td>
                    <td width="75%" class="tableContent">
                        <select name="defaultCalendarID" id="defaultCalendarID">
                            <%=HTMLOptionList.makeHtmlOptionList(listHelper.getDefaultCalendarOptions(), listHelper.getDefaultCalendarID())%>
                        </select>
                        &nbsp;
                        <a href="javascript:changeDefault();"><display:get name="prm.schedule.workingtime.list.setdefault.label" /></a>
                    </td>
                </tr>
            </table>
            </td>
            <td>&nbsp;</td>
        </tr>
        
        <tr><td colspan="4">&nbsp;</td></tr>

        <tr>
            <td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
            <td nowrap class="channelHeader" align=left colspan="2"><display:get name="prm.schedule.properties.workingtimes.channltitle" /></td>
            <td width="1%" align="right" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
        </tr>
        
        <%-- Hours per day --%>
        <tr>
            <td>&nbsp;</td>
			    <td class="tableHeader" width="24%">
			        <display:get name="prm.schedule.properties.hoursperday.label"/>
			    </td>
			    <td class="tableContent">
			    	<input type="text" name="hoursPerDay" size="20" maxlength="80" value='<jsp:getProperty name="schedule" property="hoursPerDay"/>'>
			    </td>
            <td>&nbsp;</td>
        </tr>
        <%-- Hours per week --%>
        <tr>
            <td>&nbsp;</td>
			    <td class="tableHeader">
			        <display:get name="prm.schedule.properties.hoursperweek.label"/>
			    </td>
			    <td class="tableContent">
			    	<input type="text" name="hoursPerWeek" size="20" maxlength="80" value='<jsp:getProperty name="schedule" property="hoursPerWeek"/>'>
			    </td>
            <td>&nbsp;</td>
        </tr>
        <%-- Days per Month --%>
        <tr>
            <td>&nbsp;</td>
			    <td class="tableHeader">
			        <display:get name="prm.schedule.properties.dayspermonth.label"/>
			    </td>
			    <td class="tableContent">
			    	<input type="text" name="daysPerMonth" size="20" maxlength="80" value='<jsp:getProperty name="schedule" property="daysPerMonth"/>'>
			    </td>
            <td>&nbsp;</td>
        </tr>
        

        <tr><td colspan="4">&nbsp;</td></tr>

        <tr>
            <td class="channelHeader" width="1%"><img  src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
            <td nowrap class="channelHeader" align=left colspan="2"><%=PropertyProvider.get("prm.schedule.workingtime.list.pagetitle")%></td>
            <td width="1%" align="right" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td colspan="2">

            <%-- Actual list --%>
            <table width="100%">
                    <tr class="tableContent">
                    <td>
                        <pnet-xml:transform stylesheet="/schedule/workingtime/xsl/CalendarList.xsl" name="listHelper" scope="request" />
                    </td>
                </tr>
            </table>

            </td>
            <td>&nbsp;</td>

        </tr>
    </table>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
        <tb:button type="cancel"/>
        <tb:button type="submit"/>
    </tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
