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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Discussion" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.discussion.DiscussionManager,
			net.project.discussion.DiscussionGroup,
			net.project.security.SessionManager,
			net.project.security.SecurityProvider,
			net.project.security.User,
			net.project.space.Space,
			net.project.util.HTMLUtils" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="discussions" class="net.project.discussion.DiscussionManager" scope="session" />
<%
DiscussionGroup discussion = null;
String historyDisplay = null;

// Make sure a security check has been passed to create a new discussion group
String id = securityProvider.getCheckedObjectID();
if (id.length() == 0) {
%>
	<security:verifyAccess action="create"
						   module="<%=net.project.base.Module.DISCUSSION%>" /> 

<%
    discussion = new DiscussionGroup();
    discussion.setName("");
    discussion.setDescription("");
    discussion.setCharter("");
    historyDisplay = PropertyProvider.get("prm.discussion.groupnew.create.module.history");
} else {
%>
	<security:verifyAccess action="modify"
						   module="<%=net.project.base.Module.DISCUSSION%>" /> 

<%
    discussion = discussions.getDiscussionGroup(id);
	historyDisplay = PropertyProvider.get("prm.discussion.groupnew.modify.module.history", new Object [] {discussion.getName()});
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
	var isLoaded = false;
//Fix For: bug-847 
window.history.forward(-1);
	
function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
    isLoaded = true;
    theForm = self.document.forms[0];
}

function cancel() {
   self.location = "<%= SessionManager.getJSPRootURL() %>/discussion/Main.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>"; 
}

function submit () {
   theForm.name.value = trim(theForm.name.value)
   var name = theForm.name.value;
   var charter = theForm.charter.value;
   if ((name == null) || (name.length == 0) ) {
   	 var errorMessage = '<%=PropertyProvider.get("prm.discussion.groupnew.musthavename.message")%>';
	 extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
   } else {
     theAction("submit");
     theForm.submit();
   }
}

function reset() {
   theForm.reset()
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=discussion_main&section=create";
	openwin_help(helplocation);
}
</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.discussion.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="<%=historyDisplay%>" />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="GroupNewProcessing.jsp">
  <input type="hidden" name="module" value="<%= net.project.base.Module.DISCUSSION %>">
<%
   if (discussion.getID() != null) {
%>
  <input type="hidden" name="action" value="<%= net.project.security.Action.MODIFY %>">
  <input type="hidden" name="id" value="<%= discussion.getID() %>">  
<%
   } else {
%>
  <input type="hidden" name="action" value="<%= net.project.security.Action.CREATE %>">
<% } %>
  <input type="hidden" name="theAction">
  <table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr class="channelHeader">

	<td width=1% class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td colspan="2" class="channelHeader"><%=PropertyProvider.get("prm.discussion.groupnew.channel.required.message")%></td>
	<td align=right width=1% class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>

</tr>
  <tr>	
  <td>&nbsp;</td>
      <td valign="top" width="137" nowrap  class="FieldRequired"><%=PropertyProvider.get("prm.discussion.groupnew.name.label")%></td>
      <td valign="baseline" width="451" nowrap colspan="2">	
        <input name="name" size="25" maxlength="80" value="<%= discussion.getName()%>">
      </td>
  </tr>
  <tr>	
  <td>&nbsp;</td>
      <td valign="top" width="137" nowrap  class="FieldNonRequired"><%=PropertyProvider.get("prm.discussion.groupnew.description.label")%></td>
      <td valign="baseline" width="451" nowrap colspan="2">	
        <input name="description" size="40" maxlength="80" value="<%= HTMLUtils.escape(discussion.getDescription())%>">
      </td>
  </tr>
  <tr>
  <td>&nbsp;</td>
      <td valign="top" width="137" nowrap class="FieldNonRequired"><%=PropertyProvider.get("prm.discussion.groupnew.charter.label")%></td>
      <td width="451" nowrap colspan="2">
      <textarea cols=50 name="charter" rows=5  wrap=VIRTUAL><%= HTMLUtils.escape(discussion.getCharter()) %></textarea>
      </td>
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
