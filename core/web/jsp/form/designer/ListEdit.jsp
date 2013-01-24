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

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Form Designer -- List" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.form.*,
			net.project.project.*,
			net.project.security.*,
			java.util.Iterator" 
%>

<%!
	Iterator fieldIterator = null;
	FormField field = null;
	ListFieldProperties properties = null;
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="formListDesigner" class="net.project.form.FormListDesigner" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request"/>


<%@page import="net.project.util.HTMLUtils"%><html>
<head>
<title><%=PropertyProvider.get("prm.form.designer.listeditpage.title")%></title>

<%
	// If a list id was passed on the URL
    String listID = request.getParameter("listID");
    if (listID == null) {
        listID = (String)request.getAttribute("listID");
    }

    if (listID != null)
	{
        formListDesigner.clear();
        formListDesigner.setForm(formDesigner);
        formListDesigner.setID(listID);
        formListDesigner.setIsNewList(false);
        formListDesigner.load();
    }
    
    // Added for getting all user defined fields ids
    // when any custom field added in Form Fields 
    fieldIterator = formDesigner.getFields().iterator();
    String validFieldsIds = "";
    while (fieldIterator.hasNext()) {
      field = (FormField) fieldIterator.next();
      if(field.getElementLabel() != null ) {
    	  validFieldsIds += field.getID() + ",";
      }
    }
%>

<template:getSpaceCSS/>
<security:verifyAccess objectID='<%=formDesigner.getID()%>'
					   action="modify"
					   module="<%=net.project.base.Module.FORM%>"
/> 
<script language="javascript" src="<%= SessionManager.getJSPRootURL() %>/src/errorHandler.js"></script>
<script language="javascript" src="<%= SessionManager.getJSPRootURL() %>/src/checkComponentForms.js"></script>
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	var isAnyFieldChecked = false;

function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
    focusFirstField(theForm);
}

function cancel() { self.document.location = JSPRootURL + "/form/designer/ListsManager.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=formDesigner.getID()%>"; }
function reset()  { theForm.reset(); }

function submit() {
    if (!checkTextbox(theForm.elements["name"], '<display:get name="prm.form.designer.listedit.description.listnamerequired.message" />')) return;
	if(!fieldCheck()){
		extAlert(errorTitle, '<%=PropertyProvider.get("prm.form.designer.listedit.musthaveuserdefinefields.message")%>' , Ext.MessageBox.ERROR);
	} else {
		theAction("submit");
		theForm.submit();
	}
}

function fieldCheck() {
	var userFields = '<%=validFieldsIds%>';
	var userFieldList = userFields.split(',');
	if(userFields.length != 0){
		for(var index = 0; index < userFieldList.length - 1; index++) { 
			if(document.getElementById(userFieldList[index]) != null 
				&& document.getElementById(userFieldList[index]).checked) {
				return true;
			}
		}
	}else {
		extAlert(errorTitle, '<%=PropertyProvider.get("prm.form.designer.listedit.musthaveuserdefinefields.message")%>', Ext.MessageBox.ERROR);
		return false;
	}
	return false;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=form_list_edit";
	openwin_help(helplocation);
}


</script>
</head>

<body class="main" bgcolor="#FFFFFF" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%---------------------------------------------------------------------------
  --  Toolbar                                                                
  -------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
		<history:display />
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>
<div id='content'>
<form id="listForm" method="post" action="<%= SessionManager.getJSPRootURL() %>/form/designer/ListEditProcessing.jsp">
<input type="hidden" name="theAction">

<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value="<jsp:getProperty name="formDesigner" property="ID"/>" />

<input type="hidden" name="class_id" value="<%=formDesigner.getID()%>">
<input type="hidden" name="list_id" value="<%=formListDesigner.getID()%>">
<input type="hidden" name="role" value="<%=request.getParameter("role")%>">

<errors:show clearAfterDisplay="false" scope="request"/>

<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr>
<td>
		<span class=pageTitle>
	  	<% if (formListDesigner.isNewList()) { %>
		<%=PropertyProvider.get("prm.form.designer.listedit.add.pagetitle")%>
		<% } else { %>
		<%=PropertyProvider.get("prm.form.designer.listedit.edit.pagetitle")%>
		<% } %>
	  	</span>
	</td>
	<td align="right" nowrap>
		<span class="smallHeaderGrey"><jsp:getProperty name="formDesigner" property="name" /></span>
	</td>
</tr>
<tr><td>&nbsp;</td></tr>
</table>

<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr><%=PropertyProvider.get("prm.form.designer.listedit.channel.description.title")%></nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<%---------------------------------------------------------------------------
  --  List Properties                                                        
  -------------------------------------------------------------------------%>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr>
	<td class="fieldRequired"><%=PropertyProvider.get("prm.form.designer.listedit.description.listname.label")%></td>
	<td class="tableContent">
		<input type="text" name="name" size="31" maxlength="40" value='<%= net.project.util.HTMLUtils.formatHtml(formListDesigner.getName()) %>'>
	</td>
</tr>
<tr>
	<td class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.listedit.description.description.label")%></td>
	<td class="tableContent">
		<input type="text" name="description"  size="31" maxlength="80" value='<%= net.project.util.HTMLUtils.formatHtml(formListDesigner.getDescription()) %>'>
	</td>
</tr>
<tr> 
	<td class="fieldNonRequired"> 
		<input type="checkbox" name="isDefault" value="1" <jsp:getProperty name="formListDesigner" property="systemDefaultChecked" />>
		<%=PropertyProvider.get("prm.form.designer.listedit.description.setdefault.label")%>
	</td>
</tr>

<%
if ((request.getParameter("role") != null) && request.getParameter("role").equals("user")) {
%>
<tr> 
	<td class="tableContent"><%=PropertyProvider.get("prm.form.designer.listedit.shared.listvisibility.label")%></td>
	<td colspan="4"> 
		<select name="isShared">
<% 
	if (formListDesigner.isShared()) {
 %>
			<option value="1" selected ><%=PropertyProvider.get("prm.form.designer.listedit.shared.withthisworkspace.label")%></option>
			<option value="0"><%=PropertyProvider.get("prm.form.designer.listedit.shared.justmyself.label")%></option>
<%
} else {
%>
			<option value="1"><%=PropertyProvider.get("prm.form.designer.listedit.shared.withthisworkspace.label")%></option>
			<option value="0" selected><%=PropertyProvider.get("prm.form.designer.listedit.shared.justmyself.label")%></option>
<%
}
%>
		</select>
	</td>
</tr>
<%
}
%>
<tr><td>&nbsp;</td></tr>
</table>

<%---------------------------------------------------------------------------
  --  Field Selection                                         
  -------------------------------------------------------------------------%>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr><td>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
	<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" border="0"></td>
	<td nowrap class="channelHeader" colspan="2"><nobr><%=PropertyProvider.get("prm.form.designer.listedit.channel.selectfields.title")%></nobr></td>
	<td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" border="0"></td>
</tr>
</table>
</td></tr>
<tr>
<td>
<table border="0" align="left" cellpadding="2" cellspacing="0">
<tr class="tableHeader"> 
	<th width="60" nowrap><%=PropertyProvider.get("prm.form.designer.listedit.selectfields.includefield.column")%></th>
	<th width="55" nowrap><%=PropertyProvider.get("prm.form.designer.listedit.selectfields.columnorder.column")%></th>
	<th width="100" align="left" nowrap><%=PropertyProvider.get("prm.form.designer.listedit.selectfields.fieldname.column")%></th>
	<th width="80"><%=PropertyProvider.get("prm.form.designer.listedit.selectfields.columnwidth.column")%></th>
	<th width="69"><%=PropertyProvider.get("prm.form.designer.listedit.selectfields.nowrap.column")%></th>
	<th width="69"><%=PropertyProvider.get("prm.form.designer.listedit.selectfields.indentedsubfield.column")%></th>
	<th width="69"><%=PropertyProvider.get("prm.form.designer.listedit.selectfields.calculatetotal.column")%></th>
</tr>
		
<%
// For each field
fieldIterator = formDesigner.getFields().iterator();

while (fieldIterator.hasNext())
{
  field = (FormField) fieldIterator.next();
  //System.out.println("MY-DEBUG : ListEdit.jsp : The Form Field is :" + field.getFieldLabel());
  if(field.isSelectable()) // Modified to take care of fields like calculated field which need to appear in 
                            // the list but are not stored in the database hence don't have a dbStorageType.
  //if (field.isStorable()) 
  { 
	properties = formListDesigner.getListFieldProperties(field);
%>
	<tr> 
		<td align="center" class="tableContent"> 
			<input type="checkbox" <%=properties.getListFieldCheck()%> id="<%=field.getID()%>" name="field_checked__<%=field.getID()%>" value="1">
		</td>
		<td align="center" nowrap class="tableContent"> 
			<input type="text" name="column_num__<%=field.getID()%>" size="2" maxlength="2" value="<%=net.project.util.HTMLUtils.formatHtml(properties.getFieldOrder())%>">
		</td>
		<td nowrap class="tableContent">
			<%=HTMLUtils.escape(field.getFieldLabel())%> &nbsp; &nbsp;
		</td>
		<td align="center" nowrap class="tableContent"> 
			<input type="text" name="field_width__<%=field.getID()%>" size="2" maxlength="3" value="<%=net.project.util.HTMLUtils.formatHtml(properties.getFieldWidth())%>">
			%
		</td>
		<td align="center" class="tableContent">
 			<input type="checkbox" <%=properties.getNoWrapCheck()%> name="wrap_mode__<%=field.getID()%>" value="1">
		</td>
		<td align="center" class="tableContent">
			<input type="checkbox" <%=properties.getSubfieldCheck()%> name="indented_subfield__<%=field.getID()%>" value="1">
		</td>
<%-- if (!field.isStorable()) { // We want this option only for Calculated Fields & Number Fields ??

--%>
<% if (field instanceof CalculationField || field instanceof NumberField) { // We want this option only for Calculated Fields & Number Fields ??

%>

		<td align="center" class="tableContent">
			<input type="checkbox" <%=properties.getCalculateTotalCheck()%> name="calculate_total__<%=field.getID()%>" value="1">
		</td>
<% } %>		
	</tr>
<%
  } // if
}
%>
</table>
</td>
</tr>
<tr><td>&nbsp;</td></tr>

</table>

<table border="0" width="100%" cellspacing="0" cellpadding="0">
<%---------------------------------------------------------------------------
  --  Sort Options                                                           
  -------------------------------------------------------------------------%>
<tr>
<td>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="2"><nobr><%=PropertyProvider.get("prm.form.designer.listedit.channel.sorting.title")%></nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
</td>
</tr>

<tr>
<td>
<table border="0" align="left" cellpadding="2" cellspacing="0">
<tr>
	<td align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.listedit.sorting.sortby.label")%></td>
	<td align="left" class="fieldNonRequired">
		<select name="sortFieldID0">
		<%=formListDesigner.getHtmlSortOptionList(formListDesigner.getSortFieldID(0))%>
		</select>
	</td>
	<td class="fieldNonRequired">
		<input type="Radio" name="sortAscending0" value="1" <%=formListDesigner.getSortFieldAscendingChecked(0) %>><%=PropertyProvider.get("prm.form.designer.listedit.sorting.ascending.label")%></input>
		&nbsp;&nbsp;
		<input type="Radio" name="sortAscending0" value="0" <%=formListDesigner.getSortFieldDescendingChecked(0) %>><%=PropertyProvider.get("prm.form.designer.listedit.sorting.descending.label")%></input>
		<%-- formListDesigner.getSortFieldAscending(1))--%>
	</td>
</tr>
<tr>
	<td align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.listedit.sorting.thenby.label")%></td>
	<td align="left" class="fieldNonRequired">
		<select name="sortFieldID1">
		<%=formListDesigner.getHtmlSortOptionList(formListDesigner.getSortFieldID(1))%>
		</select>
	</td>
	<td class="fieldNonRequired">
		<input type="Radio" name="sortAscending1" value="1" <%=formListDesigner.getSortFieldAscendingChecked(1) %>><%=PropertyProvider.get("prm.form.designer.listedit.sorting.ascending.label")%></input>
		&nbsp;&nbsp;
		<input type="Radio" name="sortAscending1" value="0" <%=formListDesigner.getSortFieldDescendingChecked(1) %>><%=PropertyProvider.get("prm.form.designer.listedit.sorting.descending.label")%></input>
		<%-- formListDesigner.getSortFieldAscending(1))--%>
	</td>
	</td>
</tr>
<tr>
	<td align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.form.designer.listedit.sorting.thenby.label")%></td>
	<td align="left">
		<select name="sortFieldID2">
		<%=formListDesigner.getHtmlSortOptionList(formListDesigner.getSortFieldID(2))%>
		</select>
	</td>
	<td class="fieldNonRequired">
		<input type="Radio" name="sortAscending2" value="1" <%=formListDesigner.getSortFieldAscendingChecked(2) %>><%=PropertyProvider.get("prm.form.designer.listedit.sorting.ascending.label")%></input>
		&nbsp;&nbsp;
		<input type="Radio" name="sortAscending2" value="0" <%=formListDesigner.getSortFieldDescendingChecked(2) %>><%=PropertyProvider.get("prm.form.designer.listedit.sorting.descending.label")%></input>
		<%-- formListDesigner.getSortFieldAscending(1))--%>
	</td>
	</td>
</tr>
</table>
</td>
</tr>
<tr><td>&nbsp;</td></tr>
</table>

<table border="0" width="100%" cellspacing="0" cellpadding="0">

<%---------------------------------------------------------------------------
  --  Filter Options                                                         
  -------------------------------------------------------------------------%>
<tr>
<td>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="2"><nobr><%=PropertyProvider.get("prm.form.designer.listedit.channel.filters.title")%></nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
</td>
</tr>
</table>
<table>
<tr>
<td>
<table border="0" align="left" cellpadding="2" cellspacing="0">
<%
// For each field
fieldIterator = formDesigner.getFields().iterator();

while (fieldIterator.hasNext())
{
	field = (FormField) fieldIterator.next();
	//properties = formListDesigner.getListFieldProperties(field);
	if (field.isFilterable())
	{
%>
		<tr class="fieldNonRequired"> 
			<% field.writeFilterHtml(formListDesigner.getFieldFilter(field), new java.io.PrintWriter(out)); %>
		</tr>
<%
	}
}
%>
</table>
</td>
</tr>
</table>

<table border="0" width="100%" cellspacing="0" cellpadding="0">
</table>
<%---------------------------------------------------------------------------
  --  Action Bar                                                             
  -------------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true">
	<tb:band name="action">
		<tb:button type="submit" />
		
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>
<p />
<errors:populate populateOnErrorOnly="true" scope="request"></errors:populate>
<errors:clear scope="request"/>
<br clear="all">
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
