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
    info="Interface Settings"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.space.ISpaceTypes,
    		net.project.security.Action,
			net.project.security.SessionManager,
			net.project.resource.PersonProperty,
    		net.project.resource.PersonPropertyGlobalScope"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<%
	PersonProperty property = new PersonProperty();
	property.setScope(new PersonPropertyGlobalScope(user));
	String[] properties = property.get("prm.global.login", "startPage");
	String[] pageSize = property.get("prm.global.project.workplan.pagesize", "pageSize");
	int size = 0;
	if(pageSize.length == 0){
		size = 100;
	} else {
		size = Integer.parseInt(pageSize[0]);
	}
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />

<script language="javascript">
	var theForm;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';
	var module = '<%=net.project.base.Module.PERSONAL_SPACE%>';
function cancel() {
	self.document.location = JSPRootURL + "/personal/Setup.jsp?module=" + module;
}
function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=personal_interface_settings";
	openwin_help(helplocation);
}
function setup(){
	theForm = self.document.forms[0];
	showTaskField();
}

// validating task page size for project workspace.
function validate(){
	var myObj = theForm.txtPageSize;
	if (typeof(myObj) == 'undefined' || myObj.value == '') {
	   extAlert(errorTitle,'<%=PropertyProvider.get("prm.personal.setup.interfacesettings.channel.emptypagesize.errormessage")%>', Ext.MessageBox.ERROR);
	   return false;
	}else if(isNaN(myObj.value)){
		extAlert(errorTitle,'<%=PropertyProvider.get("prm.personal.setup.interfacesettings.channel.pagesize.errormessage")%>', Ext.MessageBox.ERROR);
		return false;	
	}else if( myObj.value < 50 ){
		extAlert(errorTitle,'<%=PropertyProvider.get("prm.personal.setup.interfacesettings.channel.pagesize.numbererrormessage")%>', Ext.MessageBox.ERROR);
		return false;
	}
    return true; 
}

function submit() {
		theAction("submit");
		theForm.submit(); 
}

</script>
</head>

<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.setup.interfacesettings.module.history">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.personal.setup.interfacesettings.module.history")%>' 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/personal/InterfaceSettings.jsp"%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form action="InterfaceSettingsProcessing.jsp" method="post">
	<input type="hidden" name="module" value="<%= net.project.base.Module.PERSONAL_SPACE %>">
	<input type="hidden" name="theAction">
	<input type="hidden" name="action" value="<%=Action.MODIFY%>">
	<table border="0" align="left" width="600" cellpadding="0" cellspacing="0">
		<tr align="left" class="channelHeader">
			<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
			<td nowrap colspan="4" class="channelHeader"><display:get name="prm.personal.setup.interfacesettings.channel.startingpage.title" /></td>
			<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
		</tr>
		<tr align="left" class="tableContent"> 
			<td nowrap class="tableContent">&nbsp;</td>
		</tr>
		<tr align="left"> 
			<td>&nbsp;</td> 
			<td nowrap class="fieldNonRequired" width="20%"><display:get name="prm.personal.setup.interfacesettings.startingpage.space" />:&nbsp;</td>
			<td class="tableContent"> 
				<select name="spaceType">
					<option  <%= properties != null && properties.length > 0 && properties[0].equals(ISpaceTypes.COMPANY_SPACE) ? "SELECTED" : "" %> value="<%=ISpaceTypes.COMPANY_SPACE%>"><display:get name="prm.interfacesettings.startingpage.personalassignments.option" /></option>
					<option  <%= properties != null && properties.length > 0 && properties[0].equals(ISpaceTypes.PERSONAL_SPACE) ? "SELECTED" : "" %> value="<%=ISpaceTypes.PERSONAL_SPACE%>"><display:get name="prm.interfacesettings.startingpage.personalworkspace.option" /></option>
					<option  <%= properties != null && properties.length > 0 && properties[0].equals(ISpaceTypes.BUSINESS_SPACE) ? "SELECTED" : "" %> value="<%=ISpaceTypes.BUSINESS_SPACE%>"><display:get name="prm.interfacesettings.startingpage.business.option" /></option>
					<option  <%= properties != null && properties.length > 0 && properties[0].equals(ISpaceTypes.PROJECT_SPACE)  ? "SELECTED" : "" %> value="<%=ISpaceTypes.PROJECT_SPACE%>"><display:get name="prm.interfacesettings.startingpage.project.option" /></option>
				</select>
			</td>
		</tr>	
		<tr align="left" class="tableContent"> 
			<td nowrap class="tableContent" colspan="3">&nbsp;</td>
		</tr>
	</table>

<%-----------------------------------------------------------------------------------------------------------
    -- Action Bar                                                                                         
--------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" />
	</tb:band>
</tb:toolbar>
	
</form>    

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
