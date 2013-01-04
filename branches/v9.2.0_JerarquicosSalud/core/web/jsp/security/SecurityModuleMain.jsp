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
    info="Security Module Permissions Screen" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.SecurityManager,
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.security.Action,
            net.project.security.group.Group,
            net.project.security.group.GroupCollection,
            net.project.base.*,
            net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<jsp:useBean id="securityManagerModule" class="net.project.security.SecurityManager" scope="session" />
<%
	String id = request.getParameter("id");
%>

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.SECURITY%>" 
					   objectID="<%=id%>" /> 

<%
    // Get all groups for security.  This excludes Space Administrator type
    // groups, includes Principal groups
	GroupCollection group = new GroupCollection();
	group = securityManagerModule.getSecurityGroups(user.getCurrentSpace());

    // Set up the security console
	securityManagerModule.setSpace(user.getCurrentSpace());
	securityManagerModule.setUser(user);
	securityManagerModule.setGroups(group);
	securityManagerModule.makeModuleSecurityConsole();	
   	String baseUrl = SessionManager.getJSPRootURL();
	
	String refLink = request.getParameter("referer") == null ? "" : request.getParameter("referer") ;
	
	if(refLink != null && !refLink.trim().equals("")) {
		pageContext.setAttribute("pnet_reflink",refLink , pageContext.SESSION_SCOPE);
	}

	
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<script language="javascript">
	var theForm;
	var isLoaded = false;
	
function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	isLoaded = true;
	theForm = self.document.forms[0];
	this.focus();
}

function change() {
	theForm.action.value = "<%=Action.VIEW%>";
	theAction("submit");
	theForm.submit();	
}

function myApply() {
	theAction("apply");
	theForm.submit();	
}

function submit()
{
	myApply();
}

function cancel()
{
	myClose();
}

function myClose() {
	theAction("close");
	theForm.submit();
}

function reset()
{
	document.SECURITY.reset();
}
function help()
{
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=security_project&section=module";
	openwin_help(helplocation);
}

function CheckAll() {
 	for (var i=0;i<theForm.elements.length;i++) {
 		var e=theForm.elements[i];
		if(e.name =='modulePermissionActive')
			continue;
		
  		if (theForm.name != 'SELALL')
   		e.checked=theForm.SELALL.checked;
 	}
}
</script>

</head>

<body class="main"  onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="all.global.toolbar.standard.security">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.security.main.module.history")%>' 
					jspPage='<%=baseUrl + "/security/SecurityModuleMain.jsp"%>'
					queryString='<%="module="+Module.SECURITY%>' />
			<history:page display='<%=PropertyProvider.get("prm.security.main.module.module.history")%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="SecurityModuleProcessing.jsp" name="SECURITY">
<input type="hidden" name="theAction">
<input type="hidden" name="action" value="<%=Action.MODIFY_PERMISSIONS%>">
<input type="hidden" name="module" value="<%=Module.SECURITY%>">
  
<table border="0" width="100%" cellpadding="0" cellspacing="0">    
<tr>
	<td colspan="3">
	<tab:tabStrip>
		<tab:tab label='<%=PropertyProvider.get("prm.security.main.module.tab")%>' href='<%=SessionManager.getJSPRootURL() + "/security/SecurityModuleMain.jsp?module=180" %>' selected="true" />
		<%-- 
			for object based security, this is  disabled for now --%>
			<tab:tab label='<%=PropertyProvider.get("prm.security.main.newobject.tab")%>' href='<%=SessionManager.getJSPRootURL() + "/security/SecurityDefaultMain.jsp?module=180" %>' /> 
		
	</tab:tabStrip>
	</td>
</tr> 
<tr> 
	<td align="left" colspan="3">&nbsp; </td>
</tr>
<tr> 
      <td align="left" class="pageTitle"><%=PropertyProvider.get("prm.security.main.role.label")%>
        <select name="DisplayListID" onChange="change();">
          <%= group.getOptionList(securityManagerModule.getSelectedID())%> 
        </select>
      </td>
      <td align="left" class="tableContent">
		<input type="checkbox" name="modulePermissionActive" <%= securityManagerModule.getChecked()%>/> <%=PropertyProvider.get("prm.security.main.permissions.label")%>
      </td>
	    <td align="left" class="tableContent">
		<input type="checkbox" onClick="CheckAll()" name="SELALL" /><%=PropertyProvider.get("prm.security.main.selectall.label")%>
      </td>
 </tr>
 <tr> 
 	<td align="left" colspan="3">&nbsp; </td>
 </tr>

<tr> 
      <td colspan="3"> 
	<jsp:setProperty name="securityManagerModule" property="stylesheet" value="/security/securityModule.xsl" />
	<jsp:getProperty name="securityManagerModule" property="presentation" />
	</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" function="javascript:myClose();" />
		
		<tb:button type="submit" function="javascript:myApply();" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>