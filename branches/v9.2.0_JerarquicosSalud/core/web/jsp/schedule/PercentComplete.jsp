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
            net.project.util.NumberFormat,
            net.project.base.Module,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<security:verifyAccess action="modify" module="<%=Module.SCHEDULE%>"/>
<%
    NumberFormat nf = NumberFormat.getInstance();
%>

<html>
<head>
<title><display:get name="prm.schedule.percentcomplete.pagetitle"/></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkRange.js" />

<script language="javascript" type="text/javascript">
var theForm;

var decimalSeperator = '<%=nf.getDecimalSeparator()%>';

function setup() {
	theForm = self.document.forms[0];
    theForm.percentage.focus();
}

function cancel() {
    window.close();
}

function submit(fromHandler) {
    if  (!checkTextbox(theForm.percentage, '<%=PropertyProvider.get("prm.schedule.taskview.percentworkcomplete.pleaseenteravalue.message")%>') ||
         !checkRangeInt(theForm.percentage,0,100,'<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>', '<%=PropertyProvider.get("prm.schedule.taskview.resources.percentagerange.integer.message")%>',decimalSeperator)) {
            theForm.percentage.focus();
	        theForm.percentage.select();
			if(fromHandler)
	            return false;
			else 
				return;
    }
    window.opener.percentageComplete(theForm.percentage.value);
}

function keyHandler(evt) {
	var isIE = (document.all ? true : false);
	var code = (isIE ? window.event.keyCode : evt.which);
    // If Enter key pressed invoke Submit routine (aborting if it returns false)
	if (code == 13) {
		return submit(true);
	}
	return true;
}

</script>

</head>

<body class="main" onLoad="setup();">

<form onKeyPress="return keyHandler(event)">
<table width="100%">
    <tr><td colspan="4">
        <table cellpadding="0" cellspacing="0">
            <tr>
                <td class="channelHeader" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
                <td nowrap class="channelHeader" align=left><display:get name="prm.schedule.percentcomplete.pagetitle"/></td>
                <td width="1%" align=right class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
            </tr>
        </table>
    </td></tr>
    <tr>
        <td width="1%"></td>
        <td class="tableHeader" colspan="2"><display:get name="prm.schedule.percentcomplete.instructions"/></td>
        <td width="1%"></td>
    </tr>
    <tr>
        <td width="1%"></td>
        <td class="tableContent"><display:get name="prm.schedule.percentcomplete.percent.label"/></td>
        <td><input type="text" name="percentage" value="100" size="3" maxlength="3">%</td>
    </tr>
</table>
</form>


<tb:toolbar style="action" showLabels="true" width="100%">
    <tb:band name="action">
            <tb:button type="submit"/>
            <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<template:getSpaceJS />
</body>
</html>
