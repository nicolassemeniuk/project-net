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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| Search Results
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Search Results" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.search.SearchManager,
            net.project.security.SessionManager,
            net.project.security.User,
			net.project.base.Module" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="search" class="net.project.search.SearchManager" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
	String objectType = null;
	objectType = request.getParameter("otype");
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
	theForm = self.document.actForm;
	isLoaded = true;
}

function cancel() {
   var theLocation = "<%=search.getRefererLink()%>";
   self.document.location = theLocation;
}

function search() { 
	self.document.location = JSPRootURL + "/search/Search.jsp?module=<%=""+search.getModuleID()%>";
}

function go_next() {
    document.paging.DIRECTION.value = "NEXT";
    document.paging.submit();
}

function go_prev() {
    document.paging.DIRECTION.value = "PREV";
    document.paging.submit();
}

function help() {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=search&section=results";
	openwin_help(helplocation);
}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%-------------------------------------------------------------------------------------------
  -- Toolbar
  -----------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="all.global.toolbar.standard.search">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.global.search.module.history")%>' 
							jspPage='<%=SessionManager.getJSPRootURL() + "/search/Search.jsp?module=" + search.getModuleID()%>' />
			<history:page display='<%=PropertyProvider.get("prm.global.search.results.module.history")%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" >
	</tb:band>	
</tb:toolbar>

<div id='content'>

<form method="POST" name="actForm">
	<input type="hidden" name="theAction"> 
</form>

<%
// page block
int cur_start = 0;
int cur_end = 0;

if (request.getParameter("START") != null)
    cur_start = Integer.parseInt( request.getParameter("START") );
else
    cur_start = 1;
    
if (request.getParameter("END") != null)
    cur_end = Integer.parseInt( request.getParameter("END") );
else
    cur_end = search.getMaxDisplayNum();

if (request.getParameter("DIRECTION") != null)
{
    if (request.getParameter("DIRECTION").equals("NEXT"))
        {
        cur_start += search.getMaxDisplayNum();
        cur_end += search.getMaxDisplayNum();
        }
    
    if (request.getParameter("DIRECTION").equals("PREV"))
        {
        cur_start -= search.getMaxDisplayNum();
        cur_end -= search.getMaxDisplayNum();
        }
    }
// end page block

int result_count = search.getResultCount();
int display_end = cur_end;
if (display_end > result_count)
	display_end = result_count;


// Display Block
search.setStylesheet("/search/xsl/search-results.xsl");
String num_results_string = "<P>" + PropertyProvider.get("prm.global.search.results.extended.channel.found.title", new Object[] {String.valueOf(result_count)}) + " <BR>";
String cur_show_string = PropertyProvider.get("prm.global.search.results.extended.showing.message", new Object[] {String.valueOf(cur_start), String.valueOf(display_end)});
//String num_results_string = "<P><B>" + result_count +"</B> Items Found " + " <BR>";
//String cur_show_string = "Showing <B>" + cur_start + "</B> through <B>" + display_end + "</B>";
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
	<td class="channelHeader" width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" width="96%">
  		&nbsp;<%=PropertyProvider.get("prm.global.search.results.extended.found.message", new Object[] {String.valueOf(result_count)})%>&nbsp;&nbsp;&nbsp;
     	<% if(result_count > search.getMaxDisplayNum()){ %>
          <%= cur_show_string %>
<%
} 
%>
</td>

<td class="channelHeader" width=1% algin="right"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr valign="top">
<td class="channelContent">&nbsp;</td>
<td colspan=2 class="channelContent">

<%
if (result_count >= 1 && result_count <= search.getMaxDisplayNum())
    {
%>
    <%=search.getPresentation(search.getXMLResults())%>
    </td></tr>
<%
    }
else if (result_count > search.getMaxDisplayNum())
    {
%>
    <%= search.getPresentation(search.getXMLResults(cur_start, cur_end)) %>
    </td></tr>
    <tr><td>&nbsp;</td>
    <td colspan="3" class="channelContent">
    <br/>
<%
    if (cur_start > 1)
        out.print("<A HREF=\"javascript:go_prev();\">" + PropertyProvider.get("prm.global.search.results.extended.previous.link") + "</A>&nbsp;&nbsp;&nbsp;");
    else
        out.print(PropertyProvider.get("prm.global.search.results.extended.previous.link") +  "&nbsp;&nbsp;&nbsp;");
            
    if (cur_end < result_count)
        out.print("<A HREF=\"javascript:go_next();\">" + PropertyProvider.get("prm.global.search.results.extended.next.link") + "</A>");
    else
        out.print(PropertyProvider.get("prm.global.search.results.extended.next.link"));
%>

    </td></tr>
    <tr><td colspan="3" class="channelContent">
    <br/>
    <form method="POST" name="paging" action="<%=SessionManager.getJSPRootURL()%>/search/SearchResults.jsp">
    <input type="hidden" name="START" value="<%=cur_start%>">
    <input type="hidden" name="END" value="<%=cur_end%>">
	<input type="hidden" name="module" value='<%=request.getParameter("module")%>'>
    <input type="hidden" name="DIRECTION" value="">
	<input type="hidden" name="otype" value="<%=objectType%>" />
    <input type="hidden" name="module" value="<%="" + search.getModuleID()%>">
    <input type="hidden" name="action" value="<%="" + search.getActionID()%>">    
	</form> 
    </td></tr>
    
<%
    }
else 
    {
%>
    <FONT class="tableHeader"><%=PropertyProvider.get("prm.global.search.results.extended.nonefound.message")%></FONT>
    </td></tr>
<%
    }
%>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="search" label='<%=PropertyProvider.get("prm.global.search.results.newsearch.button.label")%>' function='<%=SessionManager.getJSPRootURL() + "/search/Search.jsp?module="+search.getModuleID()+"&action="+search.getActionID()+"&otype=" + objectType%>' />
	</tb:band>
</tb:toolbar>

<template:getSpaceJS />
</body>
<%@ include file="/help/include_outside/footer.jsp" %>
</html>

