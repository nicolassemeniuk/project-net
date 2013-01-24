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
|   $Revision: 20285 $
|       $Date: 2010-01-16 13:15:23 -0300 (sáb, 16 ene 2010) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Form Designer" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.form.*,
			net.project.security.*,
			net.project.persistence.*,
			net.project.base.ObjectType" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="formMenu" class="net.project.form.FormMenu" scope="session" />
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />

<template:getSpaceCSS/>
<%! 
	boolean hasForms = true;
%>

<% 
	// clear the old form data.
	formDesigner.clear();
	formMenu.setSpace(user.getCurrentSpace());
	formMenu.setUser(user);
	formMenu.setDisplayPending(true);
	formMenu.setDisplaySystemForms(true);
    formMenu.setLoadOwnedFormsOnly(true);
	formMenu.setLoadVisibleFormsOnly(false);
    
	try
	{
		formMenu.load();
	}
	catch (FormException pe)
	{
		hasForms = false;
	}
%>
<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.FORM%>"
/> 
<script language="javascript" src="<%= SessionManager.getJSPRootURL() %>/src/standard_prototypes.js"></script>

<script language="javascript"><!--
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {

	// load the top frame first

	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { self.document.location= JSPRootURL + "/form/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>"; }
function reset() { self.document.location= JSPRootURL + "/form/designer/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>"; }

function create() { 
    theForm.target = "_self";
	theForm.action.value = "<%=net.project.security.Action.CREATE%>";
	theForm.elements["id"].value = '';
	theAction("create");
	theForm.submit();
}

function modify(id, owner) {
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
	    if (!id){
		   owner = getSelectionOwner(theForm);
	    }
		
        theForm.target = "_self";
		theForm.action.value = "<%=net.project.security.Action.MODIFY%>";
		theForm.elements["id"].value = getSelection(theForm);
		theAction("modify");
		theForm.owner.value = owner;
		theForm.submit();
	}
}

function remove() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')){
 	    var owner = getSelectionOwner(theForm); 	    
		if(owner == 'true'){
			Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.form.designer.main.delete.message")%>', function(btn) { 
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
		}else {
			extAlert(errorTitle, '<%=PropertyProvider.get("prm.form.designer.main.sharedformdelete.message")%>' , Ext.MessageBox.ERROR);
		}
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

function copy() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')){
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.form.designer.main.copy.message")%>', function(btn) { 
			if(btn == 'yes'){ 
				var ownerIdentity = getSelectionOwnerId(theForm);
				theForm.selectedOwnerId.value = ownerIdentity;
				
				theForm.target = "_self";
				theForm.action.value = "<%=net.project.security.Action.MODIFY%>";
				theForm.elements["id"].value = getSelection(theForm);
				theAction("copy");
				theForm.submit();
			}else{
			 	return false;
			}
		});
	}	
}

function notify(){

	var targetObjectID =  getSelection(theForm);
 	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>', 'nil', 'nil', 'true')) 
	   {
		  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?targetObjectID="+ targetObjectID +"&action=" + '<%=net.project.security.Action.MODIFY%>' + "&module=" + '<%=net.project.base.Module.FORM%>');
		  openNotifyPopup(targetObjectID,m_url);

	   } else{
		  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?objectType=" + '<%=ObjectType.FORM%>' + "&action=" + '<%=net.project.security.Action.MODIFY%>' + "&module=" + '<%=net.project.base.Module.FORM%>' + "&isCreateType=1");
		  openNotifyPopup(targetObjectID,m_url);
	   }
}

function help(){
	var helplocation='<%= SessionManager.getJSPRootURL() %>'+'/help/Help.jsp?page=form_designer_main';
	openwin_help(helplocation);
}

function importForms(){
	self.document.location= JSPRootURL + "FormDefinitionUpload.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>" ;
		 	
}

function exportForms(){
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {

		window.open('<%= request.getContextPath() %>'+ "/form/designer/formExportClient.htm?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>&formId=" + getSelection(theForm));
	}
}

//Returns the current value of a field called "isOwner"
//This field is a hidden field and holds info if current space is owner of selected form
function getSelectionOwner(theForm) {
var idval;
if(!theForm) {
		 // Form is undefined
     // idval remains undefined
	  } else {
	   if (theForm.selected) {
		   if (theForm.selected.length) {
			   for (var i = 0; i < theForm.selected.length; i++) {
				  if (theForm.selected[i].checked == true) {
					idval = theForm.isOwner[i].value;
					break;
				  }
			   }
		   } else {
			  if (theForm.selected.checked == true) {
				idval = theForm.isOwner.value;
			  }
		   }
	   }
}
return idval;
}

//Returns the current value of a field called "ownerId"
//This field is a hidden field and holds info if current space is owner of selected form
function getSelectionOwnerId(theForm) {
var idval;
if(!theForm) {
		 // Form is undefined
   // idval remains undefined
	  } else {
	   if (theForm.selected) {
		   if (theForm.selected.length) {
			   for (var i = 0; i < theForm.selected.length; i++) {
				  if (theForm.selected[i].checked == true) {
					idval = theForm.ownerId[i].value;
					break;
				  }
			   }
		   } else {
			  if (theForm.selected.checked == true) {
				idval = theForm.ownerId.value;
			  }
		   }
	   }
}
return idval;
}

--></script>

</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.form.designer.main.module.history")%>'
							jspPage='<%=SessionManager.getJSPRootURL()+ "/form/designer/Main.jsp"%>'
							queryString='<%="module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY%>'
			/>

		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" label='<%=PropertyProvider.get("prm.form.main.new.button.tooltip")%>'/>
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.form.main.edit.button.tooltip")%>'/>
		<tb:button type="remove" label='<%=PropertyProvider.get("prm.form.main.delete.button.tooltip")%>'/>
		<tb:button type="copy" label='<%=PropertyProvider.get("prm.form.main.copy.button.tooltip")%>'/>
		<tb:button type="notify" />
		<tb:button type="security" />
	</tb:band>
	<tb:band name="action" groupHeading="Form">	
		<tb:button type="custom" label='<%=PropertyProvider.get("prm.form.designer.main.import.label")%>' function='<%= SessionManager.getJSPRootURL() + "/form/designer/FormDefinitionUpload.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.MODIFY%>' imageEnabled='/images/icons/form-import_over.gif' imageOver='/images/icons/form-import_on.gif' />
		<tb:button type="custom" label='<%=PropertyProvider.get("prm.form.designer.main.export.label")%>' imageEnabled='/images/icons/form-export_over.gif'  imageOver='/images/icons/form-export_on.gif' function='javascript:exportForms();'/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="MainProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="owner">
	<input type="hidden" name="selectedOwnerId">
	<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
	<input type="hidden" name="action" />
	<input type="hidden" name="id" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td align="left" class="pageTitle"><%=PropertyProvider.get("prm.form.designer.main.pagetitle")%></td>
</tr>
<tr>
	<td colspan="6">&nbsp;</td>
</tr>
</table>
  
<!--table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td align="right" class="tableContent">&nbsp;</td>
	<td align="right" class="tableContent" width="1%"><a href="javascript:exportForms();"><img border="0" src="<%=SessionManager.getJSPRootURL()%>/images/folder.gif">&nbsp;<display:get name="prm.form.designer.main.export.label" /></a>&nbsp;&nbsp;|&nbsp;&nbsp;</td>
	<td align="right" class="tableContent" width="1%"><a href="<%= SessionManager.getJSPRootURL() %>/form/designer/FormDefinitionUpload.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>"><img border="0" src="<%=SessionManager.getJSPRootURL()%>/images/file.gif">&nbsp;<display:get name="prm.form.designer.main.import.label" /></a>&nbsp;</td>
</tr>
</table-->    
   
<% if (formMenu.size() > 0) { %>

	<%-- Apply stylesheet to format Form Menu table --%>
	<jsp:setProperty name="formMenu" property="stylesheet" value="/form/designer/xsl/form-menu.xsl" />
	<jsp:getProperty name="formMenu" property="presentation" />	
	
<%
}
else
{
%>
	<table align="center"><tr><td><%=PropertyProvider.get("prm.form.main.noforms.message")%></td></tr></table>
<% 
}
%>

<p />

<%-- Action toolbar --%>
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	</tb:band>
</tb:toolbar>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>

