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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Document List" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
	net.project.security.User,
	net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="container" class="net.project.document.ContainerBean" />
<jsp:useBean id="objectPath" class="net.project.document.PathBean" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
	int action = net.project.security.Action.VIEW;
	int module = docManager.getModuleFromContainerID(docManager.getCurrentContainerID());
	String id = docManager.getCurrentObjectID();
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess 
				module="<%=module%>"
				action="view"
				objectID = "<%=id%>"
/>

<%------------------------------------------------------------------------
  -- more page setup
  ----------------------------------------------------------------------%>

<%
	IContainerObject containerObject = null;
    containerObject = docManager.getCurrentObject();

	// set the path of the object
    objectPath.setRootContainerID(docManager.getRootContainerID());
    objectPath.setObject(containerObject);
    objectPath.load();
%>


<html>
<head>
<title><display:get name="prm.document.moveobject.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />

<script language="javascript">
//fixing bug  bfd-2594
function loaded() {
	var s = location.href;
var i = s.indexOf('&gotoframe');
if (i>1) {
	i += 11;
	var gotolink = s.substr(i,s.length-i);
	top.moveBrowser.location.href=gotolink;
}

}
</script>

</head>

<body class="main" onLoad="loaded()">

<form name="container" method="post" action="<%=SessionManager.getJSPRootURL()%>/document/MoveObjectProcessing.jsp"> 

	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=module%>">
	<input type="hidden" name="action" value="<%=action%>">

<table border="0" cellspacing="0" width="100%" cellpadding="0">

	<tr class="channelHeader">
	<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
	<th class="channelHeader"  colspan="4" nowrap><display:get name="prm.document.moveobject.channel.move.title" /></th>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
</table>

<table border="0" vspace="0" cellpadding="0" cellspacing="0" width="100%">
  <tr> 
    <th align="right" width="189"> 
      <div align="left" class="tableHeader"><display:get name="prm.document.moveobject.name.label" /></div>
    </th>
    <th nowrap align="right" width="14">&nbsp;</th>
    <td align="left" width="651" class="tableContent"> <%= containerObject.getName() %> </td>
    <input type="hidden" name="objectID" VALUE="<%= containerObject.getID() %>">
  </tr>
  <tr> 
    <th align="right" width="189"> 
      <div align="left" class="tableHeader"><display:get name="prm.document.moveobject.description.label" /></div>
    </th>
    <th nowrap align="right" width="14">&nbsp;</th>
    <td align="left" width="651" class="tableContent"> <%= net.project.util.HTMLUtils.escape(containerObject.getDescription()) %> 
      &nbsp;</td>
  </tr>
  <tr> 
    <th align="right" width="189"> 
      <div align="left" class="tableHeader"><display:get name="prm.document.moveobject.currentlocation.label" /></div>
    </th>
    <th nowrap align="right" width="14">&nbsp;</th>
    <jsp:setProperty name="objectPath" property="stylesheet" value="/document/xsl/path-to-container-object.xsl" /> 
    <td align="left" width="651" class="tableContent"> <jsp:getProperty name="objectPath" property="presentation" /> 
      &nbsp;</td>
  </tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0">

	<tr class="channelHeader">
	<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
	<th class="channelHeader"  colspan="4" nowrap>&nbsp;</th>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
</table>

</form>


<template:getSpaceJS />
</body>
</html>

