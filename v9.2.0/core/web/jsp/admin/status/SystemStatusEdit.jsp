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
    info="Site Status Channel" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.AuthorizationFailedException,
            net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.database.DBBean,
            net.project.base.property.PropertyProvider,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.security.User,net.project.status.StatusMessage"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="statusMessage" class="net.project.status.StatusMessage" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<%	
	statusMessage.clear();
	if(request.getParameter("selected") != null && !request.getParameter("selected").trim().equals("")) {
		statusMessage.setID(request.getParameter("selected"));
		statusMessage.load();
	}	
%>

<script language="javascript">
	var theForm;
	var isLoaded = false;
    var updatedProfile;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms[0];
	isLoaded = true;
}

function checkTitleLength(){
	return checkMaxMinLength(theForm.messageTitle,30,1,'The title can have between one to 30 characters ');
}

function checkMessageLength(){
	return checkMaxMinLength(theForm.messageText,500,1,'The message can have between one to 500 characters ');
}

function cancel()	{
    self.document.location = JSPRootURL + "/admin/status/Main.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.MODIFY%>";
}

function reset()	{
    theForm.reset();
}

function submit()	{
	if (!checkTitleLength() || !checkMessageLength())
        return;

    if (isChecked(theForm.dummyMessageStatus)){
		theForm.messageStatus.value='A';
	} else {
		theForm.messageStatus.value='I';
	}
	theForm.submit();
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_status_edit";
	openwin_help(helplocation);
}
</script>


</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />


<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.systemstatus">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="Edit System Status" 
					jspPage='<%=SessionManager.getJSPRootURL() + "/admin/status/SystemStatusEdit.jsp"%>'
					queryString='<%="module=" + net.project.base.Module.APPLICATION_SPACE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br />
<form action="SystemStatusEditProcessing.jsp" method="post">
    <input type="hidden" name="module" value="<%= net.project.base.Module.APPLICATION_SPACE %>">
	<input type="hidden" name="messageStatus" value="">
	<table width="100%" border="0" cellpadding=0 cellspacing=0 border=0>
      <tr>
        <td colspan="4" class="tableHeader"> 
        	<%=PropertyProvider.get("prm.project.admin.profile.fileds.required.label") %>
         </td>
      </tr>
      <tr class="actionBar">
      <td colspan="4">
        <table border="0" width="100%" cellpadding="0" cellspacing="0">
        <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
        <td colspan="4" valign="middle" class="ActionBar">
			<%=PropertyProvider.get("prm.project.admin.status.edit.system.label") %>
		</td>
        <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
        </tr>
        </table>
        </td>
      </tr>
	  
	  <tr><td colspan="4">&nbsp;</td></tr>

      <tr>
        <td nowrap class="fieldRequired" align="left"><%=PropertyProvider.get("prm.project.admin.status.message.title.label")%>&nbsp;&nbsp;
           <input type="text" name="messageTitle" size="30" maxlength="30" value="<c:out value="${statusMessage.messageTitle}"/>" >
		</td>		 
      </tr>
	  
	  <tr><td colspan="4">&nbsp;</td></tr>
	  
	  <tr>
	  	<td class="fieldNotRequired" align="left"><%=PropertyProvider.get("prm.project.admin.status.set.active.label")%>&nbsp;&nbsp;
			<input type="Checkbox" name="dummyMessageStatus" value="A" <%= statusMessage.getMessageStatus() != null && statusMessage.getMessageStatus().equals("A") ? "CHECKED" :"" %> >
		</td>
	 </tr>	
	
	  <tr><td colspan="4">&nbsp;</td></tr>
	
      <tr align="left">
        <td nowrap class="fieldRequired" align="left"><%=PropertyProvider.get("prm.project.admin.status.message.text.label")%>&nbsp;&nbsp;</td>
	</tr>
	<tr>	
        <td nowrap align="left">
          <TEXTAREA name="messageText" cols="80" rows="7" ><c:out value="${statusMessage.messageText}"/></TEXTAREA>
        </td>
        <td nowrap>&nbsp;</td>
      </tr>
    </table>

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
