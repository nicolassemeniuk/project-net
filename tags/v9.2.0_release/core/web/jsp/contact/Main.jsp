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
|   $RCSfile$
|   $Revision: 18888 $
|   $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|   $Author: avinash $
|
| Contact management using the forms engine.
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Contacts" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider, net.project.form.*, net.project.project.*, net.project.security.*" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
 
<%! 
	FormList formList = null;
	String classID = null;
%>

<% 
	classID = user.getCurrentSpace().getSystemFormID("Contacts");
	if (classID != null)
	{
		form.setUser(user);
		form.setSpace(user.getCurrentSpace());
		form.setID(user.getCurrentSpace().getSystemFormID("Contacts"));
		form.load();
		form.loadLists(false);
	
		// Set the context for the FormList.
		formList = form.getDisplayList();
		formList.loadData();
	}
%>

<%-- 
<security:verifyAccess
		action="view"
		module="<%=net.project.base.Module.FORM%>"
/> 
--%>

<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {

	theForm = self.document.forms[0];
	isLoaded = true;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=form_contact";
	openwin_help(helplocation);
}

function cancel() { self.document.location= "../Main.jsp?module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; }
function reset() { self.document.location= JSPRootURL + "/contact/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>"; }
function search() { self.document.location= JSPRootURL + "/form/FormSearch.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>"; }

<%-- Note - Create on form data secured as VIEW action in form module --%>
function create() { 
	self.document.location= JSPRootURL + "/form/FormEdit.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>"; 
}

<%-- Called when a column header is clicked --%>
function sort(sortBy) {
	theForm.sortBy.value = sortBy;
    theForm.target = "_self";
	theForm.action.value = "<%=net.project.security.Action.VIEW%>";
	theForm.elements["id"].value = "<%=form.getID()%>";
	theAction("sort");
	theForm.submit();	
}

<%-- Called when a different list is selected or "default" checkbox clicked --%>
function submit() {
    theForm.target = "_self";
	theForm.action.value = "<%=net.project.security.Action.VIEW%>";
	theForm.elements["id"].value = "<%=form.getID()%>";
	theAction("submit");
	theForm.submit();	
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
        theForm.target = "_self";
		theForm.action.value = "<%=net.project.security.Action.MODIFY%>";
		theForm.elements["id"].value = getSelection(theForm);
		theAction("modify");
		theForm.submit();
	}
}

function remove() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.contact.delete.question.js")%>', function(btn) { 
			if(btn == 'yes'){ 
		        theForm.target = "_self";
				theForm.action.value = "<%=net.project.security.Action.DELETE%>";
				theForm.elements["id"].value = getSelection(theForm);
				theAction("remove");
				theForm.submit();
			}else{
			 	return false;
			}
		});
	}
}

function security() {
	var security;
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	    if (!security || security.closed == true) {
			security = openwin_security("security");
		}
        if (security && !security.closed) {
            theForm.target = "security";
			theForm.action.value = "<%=net.project.security.Action.MODIFY_PERMISSIONS%>";
			theForm.elements["id"].value = getSelection(theForm);
    	    theAction("security");
            theForm.submit();
            security.focus();
        }
    }
}
</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.contact">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=form.getName()%>'
  					      jspPage='<%=SessionManager.getJSPRootURL() + "/form/FormList.jsp"%>'
						  queryString='<%="module="+ net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW + "&id=" + form.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" />
		<tb:button type="modify" />
<%--		<tb:button type="remove" />  UNTIL IMPLEMENTED IN FormListProcessing --%>
		<tb:button type="security" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<%--------------------------------------------------------------------------------------------
  -- Page Content                                                                             
  ------------------------------------------------------------------------------------------%>
<%
if (classID == null) 
{
%>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr><td>&nbsp;</td></tr>
<tr>
	<td colspan="3">
		<%=PropertyProvider.get("prm.project.contact.module.not.available.workspace.label") %>
	</td>
</tr>
</table>

<%
}
else
{
%>
<form method="post" action="../form/FormListProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="displayedList" value="<%= formList.getID() %>">
<input type="hidden" name="sortBy" />
<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" />
<input type="hidden" name="id" />

<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr class="pageTitle"> 
	<td valign="top" align="left" class="pageTitle" colspan="3">
		<jsp:getProperty name="form" property="name" />
	</td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
	<td colspan="3">
		<table border="0" width="100%" cellpadding="0" cellspacing="0">
		<tr>
		<td align="left"><span class="tableHeader"><%=PropertyProvider.get("prm.project.contact.view.label") %></span> 
			<select name="displayListID" onChange="window.submit();">
				<jsp:getProperty name="form" property="formListOptionList" />
	        </select>
			<a href="<%= SessionManager.getJSPRootURL() %>/form/ListManager.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>"><span class="tableContent">Edit views</span></a>
		</td>
		<td align="right" class="tableContent">
		<a href="<%= SessionManager.getJSPRootURL() %>/servlet/CsvExport?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>"><img border="0" src="<%= SessionManager.getJSPRootURL() %>/images/appicons/excel.gif"> Spreadsheet</a>
		</td>	
		<td>&nbsp;&nbsp;&nbsp;</td>
		</tr>
		</table>
	</td>
</tr>
<tr class="channelHeader">
	<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" border="0" width="8" height="15" alt=""></td>
	<td align="left" class="channelHeader">
		<%=formList.getName()%> --
		<%=formList.getDescription()%>
	</td>
	<td align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" border="0" width="8" height="15" alt=""></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td>
	<% 
		// Display the formlist.
		formList.setIncludeHtmlSelect(true);
		formList.writeHtml(new java.io.PrintWriter(out));
	%>
	</td>
	<td>&nbsp;</td>
</tr>
</table>
</form>
<%
}
%>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>