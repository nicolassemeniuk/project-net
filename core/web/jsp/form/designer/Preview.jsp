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
    info="Form Designer -- Preview" 
    language="java" 
    errorPage="/errors.jsp"
    import="java.util.*,
            net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.form.FormDesigner,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.util.*"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />

<%
	// reload the form to get all the latest changes.
	formDesigner.load();
	
	String  refererURL = (String) pageContext.getAttribute("refererURL", pageContext.SESSION_SCOPE);

    //load the time units
    List availableUnits = new ArrayList();
    availableUnits.add(TimeQuantityUnit.HOUR);
    availableUnits.add(TimeQuantityUnit.DAY);
    availableUnits.add(TimeQuantityUnit.WEEK);
    if (formDesigner.getSupportsAssignment()) {
        //load the assignies for the space
        roster.setSpace(user.getCurrentSpace());
        roster.load();
    }
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<template:getSpaceCSS/>

<security:verifyAccess objectID='<%=formDesigner.getID()%>'
					   action="modify"
					   module="<%=net.project.base.Module.FORM%>"
/>
<template:import type="javascript" src="/src/standard_prototypes.js"/>

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	<%-- bfd 3071 :  Javascript error when building a form --%>
	var fieldNames = new Array(<%=formDesigner.getFields().size()%>);
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function submit () {
	theAction("submit");
	theForm.submit();
}

function cancel() { 
<%
	if(refererURL != null && !refererURL.trim().equals("")) {
%>
	self.document.location = '<%= refererURL %>';
<% 
	} else {
%>		
	self.document.location= JSPRootURL + "/form/designer/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>"; 
<%	
	}
%>	
}

function reset()  { self.document.location = JSPRootURL + "/form/designer/Preview.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=formDesigner.getID()%>"; }

function resizeTextArea(field, inc) {
<%--bfd 3294: JS error throws when clicking the web link in forms.  --%>
	if(field==null || field=="null")
		return;
	eval('theForm.' + field + '.rows +=' + inc);
}

function setFieldRef(field) {
	if (field == null)
		field = popupField;
		
	eval('form_popup.fieldRef = theForm.' + field);
}

function popupTextArea(field, label) {
<%--bfd 3294: JS error throws when clicking the web link in forms.  --%>
	if(field==null || field=="null")
		return;
	form_popup = window.open('../TextAreaPopup.jsp?module=<%=Module.FORM%>&label='+label, 'form_popup', "height=700,width=800, resizable=yes");
	
	if (form_popup.isloaded == true) {
		setFieldRef(field);
		form_popup.copyFieldData();
		form_popup.focus();
	}
	else
	{
		fieldCopyFailed = true;
		popupField = field;
	}
}
function popupDate(fieldName) {
	autoDate(fieldName, JSPRootURL);
}

function help() {
    var helplocation = "<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=form_preview";
    openwin_help(helplocation);
}

</script>

</head>
<body class="main" bgcolor="#FFFFFF" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle"><history:display /></tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="">
<input type="hidden" name="theAction">

<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value="<jsp:getProperty name="formDesigner" property="ID"/>" />

<%-- Header and Tab Bar --%>	
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr align="left">
	<td align="left">
		<span class="pageTitle"><%=PropertyProvider.get("prm.form.designer.main.pagetitle")%></span>
	</td>
	<td align="right" nowrap>
		<span class="pageTitle"><jsp:getProperty name="formDesigner" property="name" /></span>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
</table>
<tab:tabStrip>
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.definitionedit.definition.tab")%>' href='<%="DefinitionEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.fieldsmanager.fields.tab")%>' href='<%="FieldsManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.preview.preview.tab")%>' href='<%="Preview.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' selected="true" />
	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.listsmanager.lists.tab")%>' href='<%="ListsManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>'/>
<%-- Disabled for now since they are not implemented yet
	<tab:tab label="Search" href='<%="SearchEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label="Links" href='<%="LinksManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label="Documentation" href='<%="DocumentationManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
--%>
	<% if(!user.getCurrentSpace().getType().equals(net.project.space.Space.PERSONAL_SPACE)){%>
	    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.workflowselect.workflows.tab")%>' href='<%="WorkflowSelect.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <% } %>
    <display:if name="prm.global.form.sharing.isenabled">
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.formsharing.sharing.tab")%>' href='<%="FormSharing.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    </display:if>    
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.activateedit.activate.tab")%>' href='<%="ActivateEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
</tab:tabStrip>
<br />
<%-- Render an empty form for designer preview --%>
<table class="tableContentHighlight" border="1" width="100%">
<tr>
<td>
<%-- Display the Form assignment fields --%>
<%if (formDesigner.getSupportsAssignment()) { %>
<table width="100%" class="tableContent" border="0">
    <tr align="left" valign="middle">
        <th nowrap class="tableHeader" width="15%"><display:get name="prm.form.assignment.assigneduser.label"/></th>
        <th nowrap class="tableHeader" width="25%"><display:get name="prm.form.assignment.work.label"/></th>
        <th nowrap class="tableHeader" width="15%"><display:get name="prm.form.assignment.workcomplete.label"/></th>
        <th nowrap class="tableHeader" width="15%"><display:get name="prm.form.assignment.workpercentcomplete.label"/></th>
        <th nowrap class="tableHeader" width="15%"><display:get name="prm.form.assignment.startdate.label"/></th>
        <th nowrap class="tableHeader" width="15%"><display:get name="prm.form.assignment.enddate.label"/></th>
    </tr>
    <tr align="left" valign="middle">
        <td nowrap width="15%">
            <select name="assignedUser">
                <option value=""><display:get name="prm.form.designer.fieldedit.type.option.none.name"/></option>
                <%=roster.getSelectionList(user.getID())%>
            </select>
        </td>
        <td nowrap width="25%">
            <input:text elementID="work" name="work" size="5" maxLength="10" onBlur="" value='0' />
            <input:select elementID="work_units" name="work_units" onChange="" options="<%=availableUnits%>" defaultSelected="<%=TimeQuantityUnit.HOUR%>" />
        </td>
        <td nowrap width="15%">
        </td>
        <td nowrap width="15%">
            <input:text elementID="work_percent_complete" name="work_percent_complete" onBlur="" value="0%" size="4" maxLength="7"/>
        </td>
        <td nowrap width="15%">&nbsp;</td>
        <td nowrap width="15%">&nbsp;</td>
    </tr>
</table>
<br />
<%}%>
<%
if (formDesigner.hasFields())
{
	formDesigner.clearData();
	formDesigner.writeHtml(new java.io.PrintWriter(out));
}
else
{
%>
<table border="0" width="100%" bgcolor="#ffffff"><tr><td align="center"><i><%=PropertyProvider.get("prm.form.designer.preview.nofields.message")%></i></td></tr></table>
<% } %>
</td>
</tr>
</table>
<br />

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>
<p />

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

