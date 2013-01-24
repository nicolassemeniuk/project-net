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
			net.project.security.User,
			net.project.security.SecurityProvider,
			net.project.security.SessionManager,
			net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="discussions" class="net.project.discussion.DiscussionManager" scope="session" />
<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.DISCUSSION%>" /> 
<%
DiscussionGroup discussion = null;
String id = securityProvider.getCheckedObjectID();
discussion = discussions.getDiscussionGroup(id);
%>
<template:getDoctype />
<%@page import="net.project.util.HTMLUtils"%>
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />
<%--
String mySpace=null;
mySpace=user.getCurrentSpace().getType();
String spaceName=null;
		
if(mySpace.equals(Space.PROJECT_SPACE))
	spaceName=ActionToolBar.PROJECT;
else if(mySpace.equals(Space.BUSINESS_SPACE))
	spaceName=ActionToolBar.BUSINESS;
else
    spaceName=ActionToolBar.PERSONAL;
<link href="/styles/<%=spaceName%>.css" rel="stylesheet" rev="stylesheet" type="text/css">
--%>
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	
function setup() {
   load_menu('<%=user.getCurrentSpace().getID()%>');
   isLoaded = true;
   theForm = self.document.forms[0];
}

function cancel() 
   {
   self.location = "<%= SessionManager.getJSPRootURL() %>/discussion/Main.jsp?module=<%= net.project.base.Module.DISCUSSION %>&action=<%= net.project.security.Action.VIEW %>"; 
   }

function modify () 
   {
   this.location = "<%= SessionManager.getJSPRootURL() %>/discussion/GroupNew.jsp?"+
                  "module=<%= net.project.base.Module.DISCUSSION %>"+
                  "&action=<%=net.project.security.Action.MODIFY %>&id=<%= id %>";
   }

function reset() 
   { 
   theForm.reset() 
   }

function help()
{
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=discussion_main&section=create";
	openwin_help(helplocation);
}
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.discussion.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.discussion.groupproperties.module.history", new Object [] {discussion.getName()})%>'/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form>
  <input type="hidden" name="theAction">
  <table border="0" cellspacing="0" cellpadding="0" width="600">
<tr class="channelHeader">

	<td width=1% class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td colspan="2" class="channelHeader"></td>
	<td align=right width=1% class="channelHeader"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>

</tr>
  <tr>	
  <td>&nbsp;</td>
      <td valign="top" width="137" nowrap  class="FieldRequired"><%=PropertyProvider.get("prm.discussion.groupnew.name.label")%></td>
      <td valign="baseline" width="451" nowrap colspan="2">	<%=HTMLUtils.escape(discussion.getName())%> </td>
  </tr>
  <tr>	
  <td>&nbsp;</td>
  </tr>
  <tr>	
  <td>&nbsp;</td>
      <td valign="top" width="137" nowrap  class="FieldNonRequired"><%=PropertyProvider.get("prm.discussion.groupnew.description.label")%></td>
      <td valign="baseline" width="451" nowrap colspan="2">	
        <PRE class="tableContentFontOnly"><%= net.project.util.HTMLUtils.escape(discussion.getDescription()) %></PRE>
      </td>
  </tr>
  <tr>
  <td>&nbsp;</td>
      <td valign="top" width="137" nowrap class="FieldNonRequired"><%=PropertyProvider.get("prm.discussion.groupnew.charter.label")%></td>
      <td width="451" nowrap colspan="2">
       <PRE class="tableContentFontOnly"><%= net.project.util.HTMLUtils.escape(discussion.getCharter()) %></PRE>
      </td>
  </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
