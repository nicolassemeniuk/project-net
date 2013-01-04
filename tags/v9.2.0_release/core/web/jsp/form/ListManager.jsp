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
    info="Tracking Database -- List Views" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,net.project.space.*,net.project.form.*, net.project.project.*, net.project.security.*" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<html>
<head>
<title>Tracking Database -- List Views</title>
<template:import type="javascript" src="/src/window_functions.js" />
<template:getSpaceCSS/>

<%----------------------------------------------------------------------------------------------
  -- Security Check                                                                             
  --------------------------------------------------------------------------------------------%>
<security:verifyAccess objectID='<%=form.getID()%>'
					   action="view"
					   module="<%=net.project.base.Module.FORM%>"
/> 

<script language="javascript" src="<%= SessionManager.getJSPRootURL() %>/src/errorHandler.js"></script>
<script language="javascript" src="<%= SessionManager.getJSPRootURL() %>/src/util.js"></script>
<script language="javascript" src="<%= SessionManager.getJSPRootURL() %>/src/standard_prototypes.js"></script>
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	
function setup() {
    theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { self.document.location= JSPRootURL + "/form/FormList.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>"; }
function reset()  { self.document.location = JSPRootURL + "/form/ListManager.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>"; }

function create() {
	theAction("create");
	theForm.submit();
}

function help(){
	var helplocation='<%= SessionManager.getJSPRootURL() %>'+'/help/Help.jsp?page=form_list_manager';
	openwin_help(helplocation);
}

function remove() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.post.deleteselectedpost.confirm")%>', function(btn) { 
				if(btn == 'yes'){ 
					theAction("remove");
					theForm.submit();
				}else{
				 	return false;
				}
		});
	}
}

function properties() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) 
	{	
		theAction("properties");
		theForm.submit();
	}
}

function modify(id) {
	if (arguments.length != 0) {
		aRadio = theForm.selected;
		if (aRadio) {
			if (!aRadio.length) aRadio.checked = true;
			else 
				for (i = 0; i < aRadio.length; i++) 
					if (aRadio[i].value == id) aRadio[i].checked = true;
		}
	}
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {	
		theAction("modify");
		theForm.submit();
	}
}
</script>
</head>

<body class="main" onLoad="setup();">
<%----------------------------------------------------------------------------------------------
  -- Toobar                                                                                     
  --------------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle"><history:display /></tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" />
		<tb:button type="remove" />
		<tb:button type="properties" />
		<tb:button type="modify" />
	</tb:band>
</tb:toolbar>

<form method="post" action="ListManagerProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value="<jsp:getProperty name="form" property="ID"/>" />

<%-- Page Header --%>	
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr  align="left">
	<td align="left">
		<span class="pageTitle">Edit Tracking Database List Views</span>
	</td>
	<td align="right" nowrap>
		<span class="pageTitle"><jsp:getProperty name="form" property="name" /></span>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
</table>


<%!
// XSLT Formatter
net.project.xml.XMLFormatter formatter = new net.project.xml.XMLFormatter(); 
%>


<%----------------------------------------------------------------------------------------------
  -- User's Lists                                                                               
  --------------------------------------------------------------------------------------------%>
<%
form.loadLists(false, form.USER_LISTS);
formatter.setStylesheet("/form/xsl/user-lists.xsl");
%>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr class="channelHeader" align="left">
	<th align="left" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"/></th>
	<th class="channelHeader"><%=user.getDisplayName()%>'s Lists</th>
	<th class="channelHeader" align="right">
			<tb:toolbar style="channel" showLabels="true">
				<tb:band name="channel">
					<tb:button type="create" label="New List" function="javascript:create();" />
				</tb:band>
			</tb:toolbar>
	</th>
	<th align="right" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"/></th>
</tr>
</table>
<table border="0" cellspacing="0" cellpadding="0" width="100%">	
<% if ((form.getLists() == null) || (form.getLists().size() < 1)) { %>
	<tr><td valign="top" align="center">No lists have been defined.</td></tr>
	<tr><td>&nbsp;</td></tr>
<% } else { %>
	<tr> 
	<td valign="top"> 
        <%=formatter.getPresentation(form.getXML()) %>	 		
	</td>
	</tr>
<% } %>
</table>

<br><br>
<%----------------------------------------------------------------------------------------------
  -- Lists Shared by other users                                                                
  --------------------------------------------------------------------------------------------%>
<%
form.loadLists(false, form.SHARED_LISTS);
formatter.setStylesheet("/form/xsl/shared-lists.xsl");
%>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr class="channelHeader" align="left">
	<th align="left" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"/></th>
	<th class="channelHeader">Lists Shared By Other Users</th>
	<th align="right" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"/></th>
</tr>
</table>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<% if ((form.getLists() == null) || (form.getLists().size() < 1)) { %>
	<tr><td valign="top" align="center">No lists have been defined.</td></tr>
	<tr><td>&nbsp;</td></tr>
<% } else { %>
	<tr> 
	<td valign="top"> 
        <%=formatter.getPresentation(form.getXML()) %>	 		
	</td>
	</tr>
<% } %>
</table>

<br><br>
<%----------------------------------------------------------------------------------------------
  -- Administrator-defined global lists                                                         
  --------------------------------------------------------------------------------------------%>
<%
form.loadLists(false, form.ADMIN_LISTS);
formatter.setStylesheet("/form/xsl/admin-lists.xsl");
%>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr class="channelHeader" align="left">
	<td align="left" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
	<th class="channelHeader">Administrator-defined Lists</th>
	<td align="right" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
</tr>
</table>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<% if ((form.getLists() == null) || (form.getLists().size() < 1)) { %>
	<tr><td valign="top" align="center">No lists have been defined.</td></tr>
	<tr><td>&nbsp;</td></tr>
<% } else { %>
	<tr> 
	<td valign="top"> 
        <%=formatter.getPresentation(form.getXML()) %>	 		
	</td>
	</tr>
<% } %>
</table>
  

<%----------------------------------------------------------------------------------------------
  -- Actionbar                                                                                  
  --------------------------------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>


<%@ include file="/help/include_outside/footer.jsp" %>
</form>
</body>
</html>



