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

<%@ page contentType="text/html; charset=UTF-8"
	info="Process Modify Page" language="java" errorPage="/errors.jsp"
	import="net.project.base.property.PropertyProvider,
			net.project.process.ProcessBean,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.AccessVerifier"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider"
	class="net.project.security.SecurityProvider" scope="session" />

<%
	ProcessBean m_process = new ProcessBean();
	String titleText = "";
	String nameDefault = "";
	String descDefault = "";
	String cancelURL = "";
	String id_form_field = "";

	String m_space_id = user.getCurrentSpace().getID();
	String id = (String)request.getParameter("id");
	int action = securityProvider.getCheckedActionID();
	int module = securityProvider.getCheckedModuleID();
	if (action == net.project.security.Action.CREATE) {
        AccessVerifier.verifyAccess(Module.PROCESS, Action.CREATE);
		titleText = PropertyProvider.get("prm.project.process.modifyprocess.channel.create.title");
		/*cancelURL =  SessionManager.getJSPRootURL() + "/methodology/Main.jsp?module="
					+ Module.METHODOLOGY_SPACE + "&id=" + m_space_id;*/
//		cancelURL = SessionManager.getJSPRootURL() + "/project/Dashboard?module="+ Module.PROJECT_SPACE + "&id=" + m_space_id;
// bfd 3317 :Could not load calendar from database" error thrown
		String Space = user.getCurrentSpace().getSpaceType().getName();
		if("template".equals(Space))
			Space="methodology";
		cancelURL = SessionManager.getJSPRootURL() + "/"+Space.toLowerCase()+"/Main.jsp?action="+Action.VIEW;
		
	} else {
		// bfd-2738 by Avinash Bhamare on 9th feb 2006.
		// added check to id, if id is null then set action as CREATE
		if("null".equals(id) )	{
			AccessVerifier.verifyAccess(Module.PROCESS, Action.CREATE);
			titleText = PropertyProvider.get("prm.project.process.modifyprocess.channel.create.title");
			cancelURL = SessionManager.getJSPRootURL() + "/project/Dashboard";
		}else {
			AccessVerifier.verifyAccess(Module.PROCESS, Action.MODIFY, id);
			m_process.setID(id);
			m_process.load();
			nameDefault = m_process.getName();
			descDefault = m_process.getDesc();
			titleText = PropertyProvider.get("prm.project.process.modifyprocess.channel.modify.title");
			cancelURL = SessionManager.getJSPRootURL() + "/process/Main.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW;
			id_form_field = "<input type=\"HIDDEN\" name=\"id\" value=\"" + id + "\">";
		}
 	}

// Decide whether to show security toolbar button
String enableSecurityButton = "false";
if (action ==  net.project.security.Action.MODIFY) {
	enableSecurityButton = "true";
}
%>

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
        var theForm;
        var errorMsg;
        var JSPRootURL = "<%= SessionManager.getJSPRootURL() %>";

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.mainForm;
    isLoaded = true;
}

function cancel() {
   var theLocation = "<%=cancelURL%>";
   self.document.location = theLocation;
}

function reset() {
	theForm.reset();
}

function security() {
    var m_url = JSPRootURL + "/process/secureCheck.jsp?module=<%=Module.PROCESS%>&action=<%=Action.MODIFY_PERMISSIONS%>&id=<%=id%>";
    var link_win = openwin_security("security");
    link_win.document.location = m_url;
    link_win.focus();
}

function submitForm(){
    if (validateForm(document.mainForm)) {
        theAction("submit");
        theForm.submit();
    }
}

// Called on form onsubmit event
function submitFormFromOnSubmit(){
    if (validateForm(document.mainForm)) {
        theAction("submit");
        theForm.submit();
    }else{
    	return false;
    }
}
 
function validateForm(frm) {
	if (! checkTextbox (frm.processname, '<%=PropertyProvider.get("prm.project.process.modifyprocess.namerequired.message")%>'))
		return false;
    if (!checkMaxLength(theForm.processdesc,500,'<display:get name="prm.project.process.processdescriptionlength.message" />')) return false;
	return true;
}

function help() {
   	var helplocation=JSPRootURL+"/help/Help.jsp?page=process_main&section=process_modify";
   	openwin_help(helplocation);
}

</script>
</head>


<body class="main" bgcolor="#FFFFFF" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.process">
	<tb:setAttribute name="leftTitle">
		<history:history />
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="security" enable="<%=enableSecurityButton%>" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="mainForm" method="post" action="modifyProcessProcessing.jsp" onsubmit="return submitFormFromOnSubmit();">
<input type="hidden" name="theAction"> <%=id_form_field%> <input
	type="hidden" name="action" value="<%=action%>"> <input type="hidden"
	name="module" value="<%=module%>"> <input type="hidden" name="space_id"
	value="<%=m_space_id%>">

<table border="0" align="left" cellpadding="0" cellspacing="0"
	width="600">
	<tr align="left" class="channelHeader">
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8
			height=15 alt="" border=0></td>
		<td nowrap colspan="2" class="channelHeader"><%=titleText%></td>
		<td width=1% align=right><img
			src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt=""
			border=0></td>

	</tr>
	<tr align="left" valign="top">
		<td>&nbsp;</td>
		<td class="fieldRequired"><%=PropertyProvider
									.get("prm.project.process.modifyprocess.name.label")%>
		</td>
		<td><input type="text" name="processname" size="40" maxlength="80"
			value="<%=nameDefault%>"></td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="top">
		<td>&nbsp;</td>
		<td nowrap class="fieldNonRequired"><%=PropertyProvider
									.get("prm.project.process.modifyprocess.description.label")%>
		</td>
		<td nowrap><textarea wrap="VIRTUAL" cols="50" rows="2" onkeyup="return checkMaxLength(this, 500 ,'');"
			name="processdesc"><%= net.project.util.HTMLUtils.escape(descDefault)%></TEXTAREA></td>
		<td>&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" function ="javascript:submitForm();"/>
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp"%>

<template:getSpaceJS />
</body>
</html>
