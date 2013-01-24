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
    info="Form Designer -- Fields" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.xml.XMLFormatter"
%>

<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
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
    
function setup() {

    theForm = self.document.forms[0];
	var fieldCount = <%=formDesigner.getFields().size()%>;

    if (fieldCount <= 0) {
    	var errorMessage = "You must have at least one field in your form.";
    	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
    	self.document.location= JSPRootURL + "/form/designer/FieldsManager.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=formDesigner.getID()%>";
    } else {
	    isLoaded = true;
	}
}

function cancel() { self.document.location= JSPRootURL + "/form/designer/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>"; }
function reset()  { self.document.location = JSPRootURL + "/form/designer/ListsManager.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=formDesigner.getID()%>"; }

function create() {
    theAction("create");
    theForm.submit();
}

function remove() {
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		if(<%=formDesigner.getListSize()%> > 1){
	        Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.form.designer.listsmanager.remove.message")%>', function(btn) { 
				if(btn == 'yes'){ 
					theAction("remove");
	            	theForm.submit();
				}else{
				 	return false;
				}
			});
		} else{
			var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.validatelistdeletion.noenoughquantity.error.message")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		}
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
            if (!aRadio.length){
            	aRadio.checked = true;
            } else{
                for (i = 0; i < aRadio.length; i++){
                    if (aRadio[i].value == id){
                    	aRadio[i].checked = true;
                    }
                }
            }
        }
    }
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) { 
        theAction("modify");
        theForm.submit();
    }
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=form_lists_manager";
	openwin_help(helplocation);
}

</script>

</head>
<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
    <tb:setAttribute name="leftTitle"><history:display /></tb:setAttribute>
    <tb:band name="standard">
        <tb:button type="create" label='<%= PropertyProvider.get("prm.form.designer.listsmanager.create.button.tooltip") %>'/>
        <tb:button type="remove" label='<%= PropertyProvider.get("prm.form.designer.listsmanager.remove.button.tooltip") %>'/>
        <tb:button type="modify" label='<%= PropertyProvider.get("prm.form.designer.listsmanager.modify.button.tooltip") %>'/>
    </tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="ListsManagerProcessing.jsp">
<input type="hidden" name="theAction">

<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>" />
<input type="hidden" name="id" value="<jsp:getProperty name="formDesigner" property="ID"/>" />

<%-- Header and Tab Bar --%>    
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr  align="left">
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
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.preview.preview.tab")%>' href='<%="Preview.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.listsmanager.lists.tab")%>' href='<%="ListsManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' selected="true" />
<%-- Disabled for now since they are not implemented yet
    <tab:tab label="Search" href='<%="SearchEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label="Links" href='<%="LinksManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label="Documentation" href='<%="DocumentationManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
--%>
	<% if(!user.getCurrentSpace().getType().equals(net.project.space.Space.PERSONAL_SPACE)){%>
    	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.workflowselect.workflows.tab")%>' href='<%="WorkflowSelect.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <%}%>
    <display:if name="prm.global.form.sharing.isenabled">
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.formsharing.sharing.tab")%>' href='<%="FormSharing.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    </display:if>    
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.activateedit.activate.tab")%>' href='<%="ActivateEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
</tab:tabStrip>
<br />
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<%

if ((formDesigner.getLists() == null) || (formDesigner.getLists().size() < 1))
{
%>

<tr> 
    <td valign="top" align="center"><%=PropertyProvider.get("prm.form.designer.listsmanager.nolists.message")%></td>
</tr>
<tr><td>&nbsp;</td></tr>

<% 
}
else
{
%>

<tr> 
    <td valign="top"> 
        <%-- Apply stylesheet to format the form lists --%>
        <%
            XMLFormatter formatter = new XMLFormatter();
            formatter.setStylesheet("/form/designer/xsl/form-lists.xsl");
            out.println(formatter.getPresentation(formDesigner.getXML()));
        %>
    </td>
</tr>

<% } %>
</table>

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




