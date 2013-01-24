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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 20058 $
|       $Date: 2009-10-05 12:40:42 -0300 (lun, 05 oct 2009) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Edit News Item" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.PnetException,
			net.project.security.SessionManager,
			net.project.util.DateFormat,
			net.project.util.HTMLUtils" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="news" class="net.project.news.NewsBean" scope="session" />
<jsp:useBean id="newsManager" class="net.project.news.NewsManagerBean" scope="page" />
<% 
	news.setSpace(user.getCurrentSpace());
	news.setUser(user);
	newsManager.setSpace(user.getCurrentSpace());
	
	String mode = request.getParameter("mode");
	String modeDisplay = null;
	
	int action = 0;

	if (mode == null || mode.equals("")) {
		mode = "modify";
		action = net.project.security.Action.MODIFY;
	}

	if (mode.equals("create")) {
	
		modeDisplay = PropertyProvider.get("prm.news.newsedit.create.newsitem.label");
		action = net.project.security.Action.CREATE;
		news.clear();
		news.setDefaultValues();
%>
		<security:verifyAccess action="create"
							   module="<%=net.project.base.Module.NEWS%>" /> 

<%
	} else {
	
		modeDisplay = PropertyProvider.get("prm.news.newsedit.update.newsitem.label");
		// Edit mode, load news unless existing errors require displaying
		if (!news.hasErrors()) {
			if (request.getParameter("id") != null &&
			    !request.getParameter("id").equals("")) {
				mode = "modify";
				action = net.project.security.Action.MODIFY;
				news.setID(request.getParameter("id"));
				news.load();
			} else {
				throw new PnetException("Missing parameter id in news/NewsEdit.jsp");
			}
		}
%>
	   <security:verifyAccess action="modify"
							  module="<%=net.project.base.Module.NEWS%>"
							  objectID="<%=news.getID()%>" /> 
<%
	}
	int modeInt = -1;
	if (mode.equals("create")) {
		modeInt = net.project.security.Action.CREATE;
	} else {
		modeInt = net.project.security.Action.MODIFY;
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
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<script language="javascript" type="text/javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
    theForm = self.document.forms[0];
	isLoaded = true;

	selectByValue(theForm.priorityID, '<jsp:getProperty name="news" property="priorityID" />');
	selectByValue(theForm.notificationID, '<jsp:getProperty name="news" property="notificationID" />');
}

function cancel() {
	self.document.location = JSPRootURL + "/news/Main.jsp?module=<%=net.project.base.Module.NEWS%>";
}

function reset() { 
	<% if (mode.equals("create")) { %>
		self.document.location = JSPRootURL + "/news/NewsEdit.jsp?module=<%=net.project.base.Module.NEWS%>&action=<%=action%>&mode=<%=mode%>";
	<% } else { %>
		self.document.location = JSPRootURL + "/news/NewsEdit.jsp?module=<%=net.project.base.Module.NEWS%>&action=<%=action%>&id=<%=news.getID()%>&mode=<%=mode%>";
	<% } %>
}

function submit() {
	if(checkTextbox(theForm.topic,'<%=PropertyProvider.get("prm.news.newsedit.topic.errormessage")%>')) {
		preprocessSubmit();
		theForm.submit();
	}
}

function preprocessSubmit() {
	theAction("submit");
	return true;
}

function selectByValue(theSelect, theValue) {
	if (theSelect) {
		for (i = 0; i < theSelect.options.length; i++) {
			if (theSelect.options[i].value == theValue) {
				theSelect.options[i].selected = true;
			}
		}
	}
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=news_edit";
	openwin_help(helplocation);
}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.news.module.description">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=modeDisplay%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/news/NewsEdit.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.NEWS + "&action=" + net.project.security.Action.MODIFY + "&id=" + news.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action='<%=SessionManager.getJSPRootURL()%>/news/NewsEditProcessing.jsp' onSubmit="preprocessSubmit();" > 
<input type="hidden" name="theAction" />
<input type="hidden" name="module" value="<%=net.project.base.Module.NEWS%>" />
<input type="hidden" name="action" value="<%=modeInt%>" />
<input type="hidden" name="id" value='<jsp:getProperty name="news" property="ID" />' />
<input type="hidden" name="postedByID" value='<jsp:getProperty name="news" property="postedByID" />'/>

<%-- News Edit Form --%> 
<input type="hidden" name="spaceID" value='<%=user.getCurrentSpace().getID()%>' />
<br />
<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr class="channelHeader">
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td  nowrap class="channelHeader" colspan="2"><nobr><%=modeDisplay%><display:get name="prm.news.newsedit.channel.newsitem.name"/></nobr></td>
		<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<%-- Display any problems --%>
		<td colspan="2"><%=news.getErrorsTable()%></td>
		<td>&nbsp;</td>
	</tr>
    <tr> 
	  <td>&nbsp;</td>
      <td  align="left" nowrap class="fieldRequired"><%=news.getFlagError("topic", PropertyProvider.get("prm.news.newsedit.topic.label"))%></td>
      <td  align="left" nowrap> 
        <input type="text" size="60" name="topic" maxlength="80" value='<c:out value="${news.topic}"/>' size="30">
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
  	  <td>&nbsp;</td>
      <td  align="left" nowrap class="fieldNonRequired"><display:get name="prm.news.newsedit.postedby.label" /></td>
      <td  align="left" nowrap class="tableContent""> 
	  		<c:out value="${news.postedByFullName}"/>
      </td>
      <td>&nbsp;</td>
	</tr>
	
    <tr> 
	  <td>&nbsp;</td>
      <td  align="left" nowrap class="fieldNonRequired"><display:get name="prm.news.newsedit.postedon.label" /></td>
      <td  align="left" nowrap class="tableContent"> 
	  		<%=HTMLUtils.escape(DateFormat.getInstance().formatDate(news.getPostedDatetime()))%>
      </td>
      <td>&nbsp;</td>
	</tr>
	
    <tr> 
	  <td>&nbsp;</td>
      <td  align="left" nowrap class="fieldRequired"><%=news.getFlagError("priority_id", PropertyProvider.get("prm.news.newsedit.priority.label"))%></td>
      <td  align="left" nowrap> 
		<select name="priorityID">
	  		<jsp:getProperty name="newsManager" property="priorityOptionList" />
        </select>
      </td>
      <td>&nbsp;</td>
    </tr>
	
	<%-- Row spacer --%>
	<tr><td colspan="4">&nbsp;</td></tr>
	
    <tr> 
	  <td>&nbsp;</td>
      <td  align="left" nowrap colspan="2" class="fieldNonRequired"><%=news.getFlagError("Message", PropertyProvider.get("prm.news.newsedit.message.label"))%><br>
        <textarea name="message" cols="80" rows="10" wrap="virtual"><c:out value="${news.message}"/></textarea>
      </td>
      <td>&nbsp;</td>
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
<%news.clearErrors();%>
