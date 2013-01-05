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
|   $Revision: 20776 $
|       $Date: 2010-04-30 09:24:27 -0300 (vie, 30 abr 2010) $
|     $Author: uroslates $  
|
|   Used to display results from search on addLinkForm.jsp
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="page for adding a link to an object" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.link.ILinkableObject,
            net.project.search.IObjectSearch,
            net.project.base.ObjectFactory,
            net.project.base.PnetException,
            net.project.xml.XMLFormatter,
            net.project.space.Space,
            net.project.security.User,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%! private static final int NUM_RESULTS_TO_DISPLAY = 10; %>

<%
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String id = securityProvider.getCheckedObjectID();
ILinkableObject m_parent_obj = ObjectFactory.makeLinkableObject(id);
String objectId = request.getParameter("objectId");
%>
<security:verifyAccess action="modify"
					   module="<%=net.project.base.ObjectType.getModuleFromType(m_parent_obj.getType())%>" /> 
<template:getDoctype />

<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />
<STYLE type="text/css">
    body {overflow: auto}
</STYLE>

<%-- Import Javascript --%>

<script language="javascript">
function go_cancel() {
    if (parent.document.theForm)
        parent.document.theForm.submit();
    else
        self.close();
}
function go_next() {
    document.paging.DIRECTION.value = "NEXT";
    document.paging.submit();
}

function go_prev() {
    document.paging.DIRECTION.value = "PREV";
    document.paging.submit();
}

function addLink() {
    m_form = document.selectObject;
    if (verifySelection(m_form)) {
        if (getSelection(m_form) != m_form.id.value)  {
           m_form.submit();
        }
        else {
           var errorMessage = '<%=PropertyProvider.get("prm.global.links.addlink.cannotlink.message")%>';
		   extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
        }
    }
}
</script>
</head>

<body class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='content'>

<%
XMLFormatter m_formatter = new XMLFormatter();
String m_parent = request.getParameter("id");


// do search...
IObjectSearch m_search = (IObjectSearch) session.getValue("ADDLINK_SEARCH");
if (m_search != null) {
    if (request.getParameter("SUBMIT") != null) {
        m_search.doSearch(request);
        session.removeValue("ADDLINK_SEARCH");
        session.putValue("ADDLINK_SEARCH",m_search);
    }
}
else {
    throw new PnetException();
}
    
// end do search
    

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
    cur_end = NUM_RESULTS_TO_DISPLAY;

if (request.getParameter("DIRECTION") != null) {
    if (request.getParameter("DIRECTION").equals("NEXT")) {
        cur_start += NUM_RESULTS_TO_DISPLAY;
        cur_end += NUM_RESULTS_TO_DISPLAY;
    }
    if (request.getParameter("DIRECTION").equals("PREV")) {
        cur_start -= NUM_RESULTS_TO_DISPLAY;
        cur_end -= NUM_RESULTS_TO_DISPLAY;
    }
}
// end page block

int result_count = m_search.getResultCount();
int display_end = cur_end;
if (display_end > result_count)
	display_end = result_count;


// Display Block
String XML_result = new String(); 
m_formatter.setStylesheet("/link/xsl/addLinkSelection.xsl");
String new_srch_link =	"<td class=\"actionBar\"  align=right width=\"20%\"><nobr><A HREF=\"addLinkForm.jsp?module=" + module + "&action=" + action + "&id=" + id + "\" class=\"channelNoUnderline\">" + PropertyProvider.get("prm.global.links.addlinkselection.extended.newsearch.button.label") + "&nbsp;<img src=\""+SessionManager.getJSPRootURL()+"/images/icons/actionbar-search_off.gif\" width=27 height=27 alt=\"\" border=0 align=\"absmiddle\"></a></nobr></td>";

String showString = "";
if (result_count > NUM_RESULTS_TO_DISPLAY )
	showString = PropertyProvider.get("prm.global.links.addlinkselection.showing.message", new Object[] {String.valueOf(cur_start), String.valueOf(display_end)});
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr class="channelHeader">
		<td class="channelHeader" width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
 		<td nowrap class="channelHeader" width="96%">&nbsp;<%=PropertyProvider.get("prm.global.links.addlinkselection.channel.found.title", new Object[] {String.valueOf(result_count)})%> &nbsp;&nbsp;&nbsp;<%=showString%></td>
<!-- 		<td nowrap class="channelHeader" width="96%">&nbsp;<.%=result_count%> Items Found &nbsp;&nbsp;&nbsp;<.%=showString%></td> -->
		<td class="channelHeader" width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
	<tr valign="top">
		<td class="channelContent">&nbsp;</td>
		<td colspan=2 class="channelContent">

<form name="selectObject" action="addLinkConfirm.jsp" method="POST">
	<input type="hidden" name="id" value="<%=id%>">
	<input type="hidden" name="objectId" value="<%=objectId%>">
	<input type="hidden" name="action" value="<%=action%>">
	<input type="hidden" name="module" value="<%=module%>">
<% if (result_count >= 1 && result_count <= NUM_RESULTS_TO_DISPLAY) {
    XML_result = m_search.getXMLResults();
    out.println(m_formatter.getPresentation(XML_result));
    %>
    </form>
    	</td>
	</tr>
	</table>
	
	<tb:toolbar style="action" showLabels="true" bottomFixed="true">
			<tb:band name="action">
				<tb:button type="add" label='<%=PropertyProvider.get("prm.global.links.addlinkselection.addlink.button.label")%>' function='javascript:addLink();' order="1" />
				<tb:button type="search" label='<%=PropertyProvider.get("prm.global.links.addlinkselection.newsearch.button.label")%>' function='<%="addLinkForm.jsp?module="+module+"&action="+action+"&id="+id+"&objectId="+objectId%>' order="2" />
				<tb:button type="finish" label='<%=PropertyProvider.get("prm.global.links.addlinkselection.skipadding.button.label")%>' function='<%="LinkManager.jsp?module="+module+"&action="+action+"&id="+id%>' order="3" />
			</tb:band>
	</tb:toolbar>
<%} else if (result_count > NUM_RESULTS_TO_DISPLAY) {
    XML_result = m_search.getXMLResults(cur_start,cur_end);
    out.println(m_formatter.getPresentation(XML_result));
    %>
    </form>
	    </td>
	</tr>
    <tr>
		<td colspan=3 class="channelContent">
	   	<table  border="0" cellpadding="0" cellspacing="0" width="100%" vspace="0">
		   	<tr class="actionBar">
		   		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
    <%
    if (cur_start > 1)
    	out.print("<td class=\"actionBar\"  align=left width=\"20%\"><nobr><a href=\"javascript:go_prev();\" class=\"channelNoUnderline\">" + PropertyProvider.get("prm.global.links.addlinkselection.extended.previous.button.label") + "&nbsp;<img src=\""+SessionManager.getJSPRootURL()+"/images/icons/actionbar-previouspost_off.gif\" width=27 height=27 alt=\"\" border=0 align=\"absmiddle\"></a></nobr></td>");
    else
    	out.print("<td class=\"actionBar\"  align=right width=\"20%\"><nobr>&nbsp;</nobr></td>");
    	
	out.print("<td>&nbsp;</td>");
	
    if (cur_end < result_count)
         out.print("<td class=\"actionBar\"  align=right width=\"20%\"><nobr><a href=\"javascript:go_next();\" class=\"channelNoUnderline\">" + PropertyProvider.get("prm.global.links.addlinkselection.extended.next.button.label") + "&nbsp;<img src=\""+SessionManager.getJSPRootURL()+"/images/icons/actionbar-nextpost_off.gif\" width=27 height=27 alt=\"\" border=0 align=\"absmiddle\"></a></nobr></td>");

    else
    	out.print("<td class=\"actionBar\"  align=right width=\"20%\"><nobr>&nbsp;</nobr></td>");
    %>
    			<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
			</tr>
        </table>
		</td>
	</tr>
	<tr>
		<td colspan=3 class="channelContent">&nbsp;</td>
	</tr>
	
    <form method="POST" name="paging" action="addLinkSelection.jsp">
	    <input type="hidden" name="START" value="<%=cur_start%>">
	    <input type="hidden" name="END" value="<%=cur_end%>">
	    <input type="hidden" name="id" value="<%=id%>">
	    <input type="hidden" name="objectId" value="<%=objectId%>">
	    <input type="hidden" name="action" value="<%=action%>">
	    <input type="hidden" name="module" value="<%=module%>">
	    <input type="hidden" name="DIRECTION" value="">
    </form> 

	</table>

	<tb:toolbar style="action" showLabels="true" bottomFixed="true">
			<tb:band name="action">
				<tb:button type="back" function='<%="addLinkForm.jsp?module="+module+"&action="+action+"&id="+id+"&objectId="+objectId%>' />
				<tb:button type="add" label='<%=PropertyProvider.get("prm.global.links.addlinkselection.addlink.button.label")%>' function='javascript:addLink();' />
				<tb:button type="finish" function='<%="LinkManager.jsp?module="+module+"&action="+action+"&id="+id%>' />
			</tb:band>
	</tb:toolbar>
	
<%
 } else {
%>
	</form>
	<%=PropertyProvider.get("prm.global.links.addlinkselection.nomatch.message")%>
		</td>

	</table>
	
	<tb:toolbar style="action" showLabels="true" bottomFixed="true">
			<tb:band name="action">
				<tb:button type="search" label='<%=PropertyProvider.get("prm.global.links.addlinkselection.newsearch.button.label")%>' function='<%="addLinkForm.jsp?module="+module+"&action="+action+"&id="+id%>' />
			</tb:band>
	</tb:toolbar>
	
<%
    }    
%>

<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS />
</body>
</html>

