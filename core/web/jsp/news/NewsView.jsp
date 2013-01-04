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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="View News Item" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.Space,
			net.project.security.User,
			net.project.news.NewsBean,
			net.project.security.SessionManager,
			net.project.util.HTMLUtils" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="news" class="net.project.news.NewsBean" scope="session" />
<% 
	news.setSpace(user.getCurrentSpace());
	news.setUser(user);
		
	if (request.getParameter("id") != null) {
		news.setID(request.getParameter("id"));
		news.load();
	}
%>
<security:verifyAccess objectID="<%=news.getID()%>"
					   action="view"
					   module="<%=net.project.base.Module.NEWS%>"
/> 

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { 
	self.document.location = JSPRootURL + "/news/Main.jsp?module=<%=net.project.base.Module.NEWS%>";
}

function back() {
	self.document.location = JSPRootURL + "/news/Main.jsp?module=<%=net.project.base.Module.NEWS%>";
}

function reset() { 
	self.document.location = JSPRootURL + '/news/NewsView.jsp?module=<%=net.project.base.Module.NEWS%>&action=<%=net.project.security.Action.VIEW%>&id=<%=news.getID()%>'; 
}

function modify() {
	var theLocation = JSPRootURL + "/news/NewsEdit.jsp?action=<%=net.project.security.Action.MODIFY%>" + 
       	       		  "&id=<%=news.getID()%>" + 
				      "&module=<%=net.project.base.Module.NEWS%>";
	self.document.location = theLocation;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=news_view";
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
			<history:module display='<%=PropertyProvider.get("prm.news.module.history")%>' 
							jspPage='<%=SessionManager.getJSPRootURL() + "/news/Main.jsp?module=" + net.project.base.Module.NEWS%>' />
			<history:page display='<%=PropertyProvider.get("prm.news.newsview.module.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/news/NewsView.jsp?module=" + net.project.base.Module.NEWS + "&action=" + net.project.security.Action.VIEW + "&id=" + news.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="modify" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/news/NewsViewProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=net.project.base.Module.NEWS%>" />
	<input type="hidden" name="action" value="<%=net.project.security.Action.VIEW%>"/>
	<input type="hidden" name="id" value='<jsp:getProperty name="news" property="ID" />'/>

	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr class="channelHeader">
			<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" /></td>
		  	<td class="channelHeader" colspan="2"><nobr><display:get name="prm.news.newsview.channel.newsitem.label" /></nobr></td>
		  	<td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" /></td>
	   	</tr>
		<tr><td colspan="4">&nbsp;</td></tr>
	</table>
    <table width="100%"  border="0" cellpadding="0" cellspacing="2">
        <tr>
            <td class="tableHeader" align="left"><display:get name="prm.news.newsview.topic.label" /></td>
            <td class="tableHeader" align="left">&nbsp;</td>
            <td class="tableContent" width="100%"><%= HTMLUtils.escape(news.getTopic())%></td>
        </tr>
        <tr>
            <td class="tableHeader" align="left" nowrap="true"><display:get name="prm.news.newsview.priority.label" /></td>
            <td class="tableHeader" align="left">&nbsp;</td>
            <td class="tableContent"><jsp:getProperty name="news" property="priorityName" /></td>
        </tr>
        <tr>
            <td class="tableHeader" align="left" nowrap="true"><display:get name="prm.news.newsview.postedby.label" /></td>
            <td class="tableHeader" align="left">&nbsp;</td>
            <td class="tableContent"><jsp:getProperty name="news" property="postedByFullName" /></td>
        </tr>
        <tr>
            <td class="tableHeader" align="left" nowrap="true"><display:get name="prm.news.newsview.postedon.label" /></td>
            <td class="tableHeader" align="left">&nbsp;</td>
            <td class="tableContent"><output:date date="<%= news.getPostedDatetime() %>" /></td>
        </tr>
        <tr><td colspan="3">&nbsp;</td></tr>

        <tr>
            <td class="tableHeader" align="left"><display:get name="prm.news.newsview.message.label" /></td>
        </tr>
        <tr>
            <td  class="tableContent" width="100%" colspan="3">
                <output:text hyperlink="true"><jsp:getProperty name="news" property="message" /></output:text>
            </td>
        </tr>
    </table>

</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
		<tb:band name="action" enableAll="true">
      </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
