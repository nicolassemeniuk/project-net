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
|   $Revision: 20170 $
|       $Date: 2009-12-04 14:35:44 -0300 (vie, 04 dic 2009) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="News" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.news.NewsManagerBean,
			net.project.base.property.PropertyProvider,
			net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="newsManager" class="net.project.news.NewsManagerBean" scope="session" />
<% 
	newsManager.setSpace(user.getCurrentSpace());
	newsManager.setTruncatedPresentableMessageLength(160);
%>
<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.NEWS%>"
/> 

<%
	String filter_viewRange = request.getParameter("filter_viewRange");
	if (filter_viewRange != null) {
		newsManager.setViewRange(Integer.parseInt(filter_viewRange));
	}
	String sortColumn = request.getParameter("sortColumn");
	if(sortColumn != null) {
		newsManager.setSortColumn(Integer.parseInt(sortColumn));
	}
%>

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
	selectByValue(theForm.filter_viewRange, '<jsp:getProperty name="newsManager" property="viewRange" />');
	isLoaded = true;
}

function reset() { 
	self.document.location = JSPRootURL + '/news/Main.jsp?module=<%=net.project.base.Module.NEWS%>'; 
}

function create() {
	var theLocation = JSPRootURL + "/news/NewsEdit.jsp?action=<%=net.project.security.Action.CREATE%>" + 
					  "&module=<%=net.project.base.Module.NEWS%>&mode=create";
	self.location = theLocation;
}

function modify() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		var val = getSelection(theForm);
  	  	var theLocation = JSPRootURL + "/news/NewsEdit.jsp?action=<%=net.project.security.Action.MODIFY%>" + 
        	       		  "&id=" + val + 
					      "&module=<%=net.project.base.Module.NEWS%>";
		self.location = theLocation;
	}	  
}

function remove() {
	if ((theForm.selected) && (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))) {
    	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<display:get name="prm.news.main.deleteconfirmation.message" />', function(btn) { 
			if(btn == 'yes'){ 
				var idval = getSelection(theForm);//theForm.selected.value;
         		theForm.action.value = "<%=net.project.security.Action.DELETE%>";
         		theForm.id.value = idval;
        		theAction("remove");
         		theForm.submit();
			}else{
			 	return false;
			}
		});
	}
}
   
function properties() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		var theLocation = JSPRootURL + '<%="/news/NewsView.jsp?module=" + net.project.base.Module.NEWS + "&action=" + net.project.security.Action.VIEW + "&id="%>' + getSelection(theForm);
		self.location = theLocation;
	}
}

function changeView(sortColumn) {
	var location;
	location = JSPRootURL + 
			   "/news/Main.jsp?filter_viewRange=" + theForm.filter_viewRange.options[theForm.filter_viewRange.options.selectedIndex].value;
	if (arguments.length > 0) {
		location = location + "&sortColumn=" + sortColumn;
	}
   	location = location + "&module=<%=net.project.base.Module.NEWS%>";
	this.location = location;
}

function sort(sortColumn) {
	changeView(sortColumn);
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

function notify(){
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>', 'nil', 'nil', 'true')){
	  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?targetObjectID=" + getSelection(theForm) + '&action=<%=net.project.security.Action.MODIFY%>&module=<%=net.project.base.Module.NEWS%>');
	  openNotifyPopup( getSelection(theForm), m_url);
	} else {
		  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?objectType=news&action="+'<%=net.project.security.Action.CREATE%>&module=<%=net.project.base.Module.NEWS%>'+'&isCreateType=1');
		  openNotifyPopup( getSelection(theForm), m_url);
	}
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=news_main";
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
			<history:module display='<%= PropertyProvider.get("prm.news.main.module.history") %>' 
							jspPage='<%=SessionManager.getJSPRootURL() + "/news/Main.jsp?module=" + net.project.base.Module.NEWS%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" />
		<tb:button type="modify" />
		<tb:button type="remove" />
		<tb:button type="notify" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<session:getJSPRootURL />/news/MainProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=net.project.base.Module.NEWS%>" />
	<input type="hidden" name="id" />
	<input type="hidden" name="action" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td align="right" class="tableHeader"><display:get name="prm.news.main.channel.view.title" />&nbsp;</td>
	<td align="left" class="tableContent">
		<select name="filter_viewRange" onchange="changeView();">
			<option value="<%=NewsManagerBean.FILTER_ALL%>"><display:get name="prm.news.main.allnewsitems.option.name" /></option>
			<option value="<%=NewsManagerBean.FILTER_PAST_TWO_WEEKS%>"><display:get name="prm.news.main.twoweeks.option.name" /></option>
			<option value="<%=NewsManagerBean.FILTER_PAST_MONTH%>"><display:get name="prm.news.main.pastmonth.option.name" /></option>
		</select>			
	</td>
</tr>
</table>
<br />
<%-- Insert News List --%>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr class="channelHeader" align="left">
    <th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
	<th class="channelHeader">&nbsp;<display:get name="prm.news.main.channel.newsitem.title" /></th>
    <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
  </tr>
  <tr>
    <td>&nbsp;</td>
	<td>
		<jsp:setProperty name="newsManager" property="stylesheet" value="/news/xsl/news_menu.xsl" />
		<jsp:getProperty name="newsManager" property="newsItemsPresentation" />
    </td>
	<td>&nbsp;</td>
  </tr>
</table>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>
