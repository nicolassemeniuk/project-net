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
|   The page for searching and browsing objects to link.
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="page for adding a link to an object" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.link.ILinkableObject,
            net.project.search.IObjectSearch,
            net.project.search.DocumentSearch,
            net.project.search.PostSearch,
            net.project.search.CalendarSearch,
            net.project.search.DeliverableSearch,
            net.project.search.TaskSearch,
			net.project.search.FormDataSearch,
			net.project.search.FormListSearch,
            net.project.base.ObjectFactory,
            net.project.base.ObjectType,
	    	net.project.space.Space,
	    	net.project.security.User,
            net.project.security.SecurityProvider,
			net.project.security.SessionManager,
            java.util.Map,
            java.util.HashMap"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String id = securityProvider.getCheckedObjectID();
ILinkableObject m_parent_obj = ObjectFactory.makeLinkableObject(id);
%>
<security:verifyAccess action="modify"
					   module="<%=net.project.base.ObjectType.getModuleFromType(m_parent_obj.getType())%>" /> 

<%
String SIM_VAL = "simple";
String ADV_VAL = "advanced";
String BROWSE_VAL = "browse";

session.removeValue("ADDLINK_SEARCH");
 

request.setAttribute("parent_id", id);
String objectId = request.getParameter("objectId");
%>


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

function submitForm() {
    document.ObjSearch.submit();
}

function addLink() {
    m_form = document.container;
    if (verifySelection(m_form)) {
        if (getSelection(m_form) != document.browseForm.id.value) {
    	   document.browseForm.selected.value = getSelection(m_form);
           document.browseForm.submit();
        }
        else {
           var errorMessage = '<%=PropertyProvider.get("prm.global.links.addlink.cannotlink.message")%>';
		   extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
        }
    }
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=link_main";
	openwin_help(helplocation);
}
</script>
</head>



<%
    String search_type = request.getParameter("TYPE_PICK_DD");
    String search_level = request.getParameter("SEARCH_LEVEL");

    // Defines the display values for the specified object types
    Map typeDisplayMap = new HashMap();
    typeDisplayMap.put(ObjectType.DOCUMENT, PropertyProvider.get("prm.global.links.addlink.linktype.option.document.name"));
    typeDisplayMap.put(ObjectType.DELIVERABLE, PropertyProvider.get("prm.global.links.addlink.linktype.option.deliverable.name"));
    typeDisplayMap.put(ObjectType.POST, PropertyProvider.get("prm.global.links.addlink.linktype.option.post.name"));
   	typeDisplayMap.put(ObjectType.TASK, PropertyProvider.get("prm.global.links.addlink.linktype.option.task.name"));
    typeDisplayMap.put(ObjectType.EVENT, PropertyProvider.get("prm.global.links.addlink.linktype.option.calendar.name"));
    typeDisplayMap.put(ObjectType.FORM_DATA, PropertyProvider.get("prm.global.links.addlink.linktype.option.formdata.name"));
    typeDisplayMap.put(ObjectType.FORM_LIST, PropertyProvider.get("prm.global.links.addlink.linktype.option.formlist.name"));

    String doc_select = "";
    String post_select = "";
    String meet_select = "";
    String mile_select = "";
    String deliv_select = "";
    String task_select = "";
    String event_select = "";
    String formData_select = "";
    String formList_select = "";

    String simple_check = "";
    String advanced_check = "";
    String browse_check = "";

    String addButtonType = "";
    String addButtonLabel = "";
    String addButtonFunction = "";

    IObjectSearch m_search = null;

    // Default to DOCUMENT search
    if (search_type == null) {
	    search_type = ObjectType.DOCUMENT;
	    // This will be overriden later if the document doesn't support it
        search_level = SIM_VAL;
	}

    // Now set search variable based on search type
    if ( search_type.equals(ObjectType.DOCUMENT) ) {
        doc_select = "SELECTED";
        DocumentSearch m_docsrch = new DocumentSearch();
        m_search = m_docsrch;
    } else if ( search_type.equals(ObjectType.POST) ) {
        post_select = "SELECTED";
        PostSearch m_postsrch = new PostSearch();
	    m_search = m_postsrch;
    } else if ( search_type.equals(ObjectType.MEETING) ) {
        meet_select = "SELECTED";
    } else if ( search_type.equals(ObjectType.TASK) ) {
        task_select = "SELECTED";
        TaskSearch m_tasksrch = new TaskSearch();
	    m_search = m_tasksrch;
    } else if ( search_type.equals(ObjectType.EVENT) ) {
        event_select = "SELECTED";
        CalendarSearch m_eventsrch = new CalendarSearch();
	    m_search = m_eventsrch;
    } else if ( search_type.equals(ObjectType.DELIVERABLE) ) {
        deliv_select = "SELECTED";
        DeliverableSearch m_delsrch = new DeliverableSearch();
	    m_search = m_delsrch;
    } else if ( search_type.equals(ObjectType.FORM_DATA) ) {
        formData_select = "SELECTED";
        FormDataSearch m_formDatasrch = new FormDataSearch();
	    m_search = m_formDatasrch;
    } else if ( search_type.equals(ObjectType.FORM_LIST) ) {
        formList_select = "SELECTED";
        FormListSearch m_formListsrch = new FormListSearch();
	    m_search = m_formListsrch;
    }

    // Set the current space for searching
	m_search.addSpaceID(user.getCurrentSpace().getID());

    // Now check to see if the object type supports the search level, and reset
    // the search level to the default if the object type does not support it
    int searchLevelRequired = IObjectSearch.SIMPLE_SEARCH;
    if (search_level.equals(SIM_VAL)) {
    	searchLevelRequired = IObjectSearch.SIMPLE_SEARCH;
    } else if (search_level.equals(ADV_VAL)) {
    	searchLevelRequired = IObjectSearch.ADVANCED_SEARCH;
    } else if (search_level.equals(BROWSE_VAL)) {
    	searchLevelRequired = IObjectSearch.BROWSE_SEARCH;
    }
    if (!m_search.isSearchTypeSupported(searchLevelRequired)) {
    	// Search object doesn't support level
    	search_level = m_search.getSearchLevel(m_search.getDefaultSearchType());
    }

    if ( search_level.equals(SIM_VAL) ) {
        simple_check = "CHECKED";
	    addButtonType = "search";
	    addButtonLabel = PropertyProvider.get("prm.global.links.addlink.find.button.label", (String) typeDisplayMap.get(search_type));
	    addButtonFunction = "javascript:submitForm();";
        if (m_search != null) m_search.setSearchType(IObjectSearch.SIMPLE_SEARCH);
    } else if ( search_level.equals(ADV_VAL) ) {
        advanced_check = "CHECKED";
	    addButtonType = "search";
	    addButtonLabel = PropertyProvider.get("prm.global.links.addlink.find.button.label", (String) typeDisplayMap.get(search_type));
	    addButtonFunction = "javascript:submitForm();";
        if (m_search != null) m_search.setSearchType(IObjectSearch.ADVANCED_SEARCH);
    } else if ( search_level.equals(BROWSE_VAL) ) {
   	    browse_check = "CHECKED";
	    addButtonType = "add";
	    addButtonLabel = PropertyProvider.get("prm.global.links.addlink.add.button.label");
	    addButtonFunction = "javascript:addLink();";
        if (m_search != null) m_search.setSearchType(IObjectSearch.BROWSE_SEARCH);
    }
%>

<body class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='content'>

<table cellpadding=0 cellspacing=0 border=0 width="500">
<tr>
	<td class="channelHeader" width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td class="channelHeader" width=98% colspan=2 align=left><%=PropertyProvider.get("prm.global.links.addlink.channel.addlink.title")%></td>
	<td class="channelHeader" width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td width="15%" class="tableHeader"><%=PropertyProvider.get("prm.global.links.addlink.addlinkto.label")%></td>
	<td class="tableContent"><%=m_parent_obj.getName()%></td>
	<td>&nbsp;</td>
</tr>

<tr valign=TOP >
	<td>&nbsp;</td>
	<td width="15%" class="tableHeader"><B><%=PropertyProvider.get("prm.global.links.addlink.linktype.label")%>&nbsp;</B></td><td>

	<form method="POST" name="TYPE_PICK" action="addLinkForm.jsp">
	<select name="TYPE_PICK_DD" onChange="submit();">
	<option value="<%=ObjectType.DOCUMENT %>" <%=doc_select %>><%=typeDisplayMap.get(ObjectType.DOCUMENT)%></option>
	<% if (!user.getCurrentSpace().getID().equals("1")) { %>
		<option value="<%=ObjectType.DELIVERABLE %>" <%=deliv_select %>><%=typeDisplayMap.get(ObjectType.DELIVERABLE)%></option>
	<% } %> 
	<option value="<%=ObjectType.POST %>" <%=post_select %>><%=typeDisplayMap.get(ObjectType.POST)%></option>
	<% if (!user.getCurrentSpace().getID().equals("1")) { %>
		<option value="<%=ObjectType.TASK %>" <%=task_select %>><%=typeDisplayMap.get(ObjectType.TASK)%></option>
	<% } %>
	<option value="<%=ObjectType.EVENT %>" <%=event_select %>><%=typeDisplayMap.get(ObjectType.EVENT)%></option>
	<option value="<%=ObjectType.FORM_DATA %>" <%=formData_select %>><%=typeDisplayMap.get(ObjectType.FORM_DATA)%></option>
	<option value="<%=ObjectType.FORM_LIST %>" <%=formList_select %>><%=typeDisplayMap.get(ObjectType.FORM_LIST)%></option>
	</select>
	</td>
	<td>&nbsp;</td>
</tr>


<tr valign=TOP>
	<td>&nbsp;</td>
	<td class="tableHeader" nowrap><B><%=PropertyProvider.get("prm.global.links.addlink.findlinkthrough.label")%>&nbsp;</B></td>
	<td class="tableContent">
	<% if (m_search.isSearchTypeSupported(IObjectSearch.SIMPLE_SEARCH)) { %>
		<input type="RADIO" name="SEARCH_LEVEL" value="<%=SIM_VAL %>" onClick="submit();" <%=simple_check%>><B><%=PropertyProvider.get("prm.global.links.addlink.findlinkthrough.option.simple.name")%></B>
	<% } %>

	<% if (m_search.isSearchTypeSupported(IObjectSearch.ADVANCED_SEARCH)) { %>
		<input type="RADIO" name="SEARCH_LEVEL" value="<%=ADV_VAL %>" onClick="submit();" <%=advanced_check%>><B><%=PropertyProvider.get("prm.global.links.addlink.findlinkthrough.option.advanced.name")%></B>
	<% } %>

	<% if (m_search.isSearchTypeSupported(IObjectSearch.BROWSE_SEARCH)) { %>
		<input type="RADIO" name="SEARCH_LEVEL" value="<%=BROWSE_VAL %>" onClick="submit();" <%=browse_check%>><B><%=PropertyProvider.get("prm.global.links.addlink.findlinkthrough.option.browse.name")%></B>
	<% } %>

	<input type="hidden" name="id" value="<%=id%>">
	<input type="hidden" name="objectId" value="<%=request.getParameter("objectId")%>">
	<input type="hidden" name="action" value="<%=action%>">
	<input type="hidden" name="module" value="<%=module%>">
	</form>
	</td>
	<td>&nbsp;</td>
</tr>
</table>
<% 
if (m_search != null && m_search.getSearchType() != IObjectSearch.BROWSE_SEARCH) {
%>
<form method="POST" name="ObjSearch" action="addLinkSelection.jsp">	
<%=m_search.getSearchForm("ObjSearch", request)%>
<input type="hidden" name="TYPE" value="<%=search_type%>">
<input type="hidden" name="LEVEL" value="<%=search_level%>">
<input type="hidden" name="id" value="<%=id%>">
<input type="hidden" name="objectId" value="<%=objectId%>">
<input type="hidden" name="action" value="<%=action%>">
<input type="hidden" name="module" value="<%=module%>">
<input type="hidden" name="SUBMIT" value="weee">
</form>
<%
} else if (m_search != null && m_search.getSearchType() == IObjectSearch.BROWSE_SEARCH)	{
%>
<form method="POST" name="browseForm" action="addLinkConfirm.jsp">
<input type="hidden" name="selected">
<input type="hidden" name="SUBMIT2" value="Add Link">
<input type="hidden" name="id" value="<%=id%>">
<input type="hidden" name="action" value="<%=action%>">
<input type="hidden" name="module" value="<%=module%>">
</form>
	<% if (search_type.equals(ObjectType.DOCUMENT)) { %>
		<!--- DOC INCLUDE -->
		<jsp:include page="/link/documentBrowse.jsp" flush="true" />
	<% } else if (search_type.equals(ObjectType.FORM_LIST)) { %>
		<!--- FORM_LIST INCLUDE -->
		<jsp:include page="/link/formListBrowse.jsp" flush="true" />
	<% } %>

<%
}
%>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="back" label='<%=PropertyProvider.get("prm.global.links.addlink.back.button.label")%>' function='<%="LinkManager.jsp?module="+module+"&action="+action+"&id="+id+"&objectId="+objectId%>' order="1" />
		<tb:button type="<%=addButtonType%>" label="<%=addButtonLabel%>" function="<%=addButtonFunction%>" order="2" />
	</tb:band>
</tb:toolbar>
<B>
<% 
if (request.getParameter("linked") != null)
    out.println("<B><FONT COLOR=\"RED\">" + PropertyProvider.get("prm.global.links.addlink.linkadded.message", new Object[] {request.getParameter("linked")}) + "</FONT></B>");
session.putValue("ADDLINK_SEARCH",m_search);
 %>
</b>

<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS />
</body>
</html>