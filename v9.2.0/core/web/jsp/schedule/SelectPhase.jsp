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
|   $Revision: 19638 $
|       $Date: 2009-08-07 12:43:02 -0300 (vie, 07 ago 2009) $
|     $Author: nilesh $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.schedule.mvc.handler.taskedit.Helper,
            net.project.gui.html.HTMLOptionList,
            net.project.base.property.PropertyProvider,
            net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess action="modify" module="<%=Module.SCHEDULE%>"/>

<html>
<head>
<title><display:get name="prm.schedule.selectphase.pagetitle"/></title>

<%-- Import CSS --%>
<template:getSpaceCSS />


<script language="javascript" type="text/javascript">
var theForm;

function setup() {
	theForm = self.document.forms[0];
}

function cancel() {
    window.close();
}

function submit() {
   	 window.opener.phaseComplete(theForm.phaseID.value);
}

// Validate phase selection
function validate(){
	var phaseCombo = theForm.phaseID;
	if(theForm.phaseID.value != '' ){
		return true;
	}else{
		extAlert('Error', '<%=PropertyProvider.get("prm.schedule.select.phase.error.message")%>' , Ext.MessageBox.ERROR);
		return false; 
	}
}
</script>
 
</head>

<body class="main" onLoad="setup();">

<form>
<table width="100%">
    <tr><td colspan="4">
        <table cellpadding="0" cellspacing="0">
            <tr>
                <td class="channelHeader" width="1%"><img  src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15></td>
                <td nowrap class="channelHeader" align=left><display:get name="prm.schedule.selectphase.pagetitle"/></td>
                <td width="1%" align=right class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15></td>
            </tr>
        </table>
    </td></tr>
    <tr>
        <td width="1%"></td>
        <td class="tableHeader" colspan="2"><display:get name="prm.schedule.selectphase.instructions"/></td>
    </tr>
    <tr>
        <td width="1%"></td>
        <td class="tableContent"><display:get name="prm.schedule.selectphase.phase.label"/></td>
        <td>
            <select name="phaseID">
            <option value=""><display:get name="all.global.none"/></option>
            <%=HTMLOptionList.makeHtmlOptionList(Helper.getPhaseOptions(user.getCurrentSpace()))%>
            </select>
        </td>
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
