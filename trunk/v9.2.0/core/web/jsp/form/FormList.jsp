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
    info="Form List" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.form.*,
			net.project.project.*,
			net.project.security.*,
            net.project.persistence.*,
			net.project.base.*,
			java.net.URLEncoder,
			net.project.space.*" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%
	form.setUser(user);
	form.setSpace(user.getCurrentSpace());

	// Set the context for the form, and load from persistence.
	// Note - the load parameter is set to false by FormListProcessing when we simply want to switch lists withour reloading
	boolean doLoad = true;
	if (request.getParameter("load") != null && request.getParameter("load").equals("false")) {
		doLoad = false;
	}
	if (request.getParameter("id") != null && doLoad) {
        form.clear();
		form.setID(request.getParameter("id"));
		form.load();
		form.loadLists(false);
	}
	
	// Set the context for the FormList.
    FormList formList = null;
	formList = form.getDisplayList();
	formList.setCurrentSpace(user.getCurrentSpace());
	formList.loadData();
%>

<%-- 
 TEMPORARY DISABLE SECURITY for testing contacts.
<security:verifyAccess objectID='<%=form.getID()%>'
					   action="view"
				   module="<%=net.project.base.Module.FORM%>"
/> 
--%>
<template:getSpaceCSS />
<template:import type="css" src="/styles/blog.css" />


<template:import type="javascript" src="/src/dhtml/xmlrequest.js" />
<template:import type="javascript" src="/src/workCapture.js" />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var exportWindow;
	var noSelectionErrMes = '<display:get name="prm.global.javascript.verifyselection.noselection.error.message" />';    
	var moduleId = <%=Module.FORM%>;
	var spaceId='<%=user.getCurrentSpace().getID()%>';
	var blogItFor = 'formDataItems'+'<%=user.getCurrentSpace().getSpaceType().getName().replaceAll("'", "&acute;")%>';
	var objectType = 'form_data';
		
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { self.document.location= JSPRootURL + "/form/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>"; }
function reset() { self.document.location= JSPRootURL + "/form/FormList.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>"; }
function search() { self.document.location= JSPRootURL + "/form/FormSearch.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>"; }

<%-- Note - Create on form data secured as MODIFY action in FORM module --%>
function create() { 
	self.document.location= JSPRootURL + "/form/FormEdit.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>"; 
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
		theForm.action.value = "<%=net.project.security.Action.VIEW%>";
		theForm.elements["id"].value = getSelection(theForm);
		theAction("modify");
		theForm.submit();
	}
}

function remove() {
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.form.list.deleteform.message")%>', function(btn) { 
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

function notify(){
	if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>', 'nil', 'nil', 'true')){
	  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?targetObjectID="+ getSelection(theForm)+ "&action=<%=net.project.security.Action.MODIFY%>&module=<%=net.project.base.Module.FORM%>");
	  openNotifyPopup(getSelection(theForm), m_url);
	} else {
		  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?objectType=form_data&action=<%=net.project.security.Action.MODIFY%>&module=<%=net.project.base.Module.FORM%>&isCreateType=1");
		 openNotifyPopup(getSelection(theForm), m_url);
	}
}

function captureWork() {
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
    	var alertMessage = '<%=PropertyProvider.get("prm.form.assignment.javascript.workcapture.message")%>';
		var showAlertMessage = false;
        if (theForm.selected.length) {
            for (var i = 0; i < theForm.selected.length; i++) {
                if (theForm.selected[i].checked == true) {
					if(true == !isUserAssigned(theForm.selected[i].value, '<%=user.getID()%>')) {
						showAlertMessage = true;
					}
                }
            }
        } else {
			if(true == !isUserAssigned(theForm.selected.value, '<%=user.getID()%>')) {
                showAlertMessage = true;
			}
        }
		if(true == showAlertMessage){
			extAlert(errorTitle, alertMessage, Ext.MessageBox.ERROR);
			return;
		}
		
  	    theLocation= JSPRootURL + "/servlet/AssignmentController/CurrentAssignments/Update?module=<%=Module.FORM%>&action=<%=Action.VIEW%>";
        theLocation += "&" + formatQueryParameters(theForm.selected, 'objectID');
        theLocation += "&returnTo=<%=URLEncoder.encode("/form/FormList.jsp?module=" + Module.FORM + "&id=" + request.getParameter("id"), "UTF-8")%>";
	    self.location = theLocation;
    }
}

function isUserAssigned(objectId, userId) {
    var url = JSPRootURL + "/servlet/FormAssignmentController/FormAssignmentCheckAssignedUser?module=<%=Module.FORM%>&action=<%=Action.VIEW%>&ObjectId="+objectId+"&PersonId="+userId;
  	var xmlRequest = new XMLRemoteRequest();
    var responseText = xmlRequest.getRemoteDocumentString(url);
    var assignment = eval("(" + responseText + ')');
    return assignment.Matched;
}



function help(){
	var helplocation='<%= SessionManager.getJSPRootURL() %>'+'/help/Help.jsp?page=formlist_main';
	openwin_help(helplocation);
}

function exportCSV() {
    exportWindow = openwin_dialog('csvexport', '<%= SessionManager.getJSPRootURL() %>/form/CSVExport1.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>', 250, 400);
}

function triggerCSVDownload() {
    exportWindow.close();
    self.document.location = '<%=SessionManager.getJSPRootURL() + "/servlet/Download?downloadableObjectAttributeName=formListCSVDownload&cleanup=true"%>';
}

function workflow() {
         
   if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {	

	   if (!workflow) {
	       var workflow = openwin_wizard("workflow");

	       if (workflow) {
		   theAction("workflow");
		   theForm.target = "workflow";
		   theForm.submit();
		   workflow.focus();
	       }
	   }

   } // end verify
}

function print() {
	var url = getVar("JSPRootURL") + "/form/FormPrint.jsp?id=" + <%= request.getParameter("id") %> + "&module=30&rendom="+ new Date().getTime();
	var printWindow = window.open(url, "print_window", "height=600,width=800,directory=0,resizable=1,statusbar=1,hotkeys=0,menubar=0,scrollbars=1,status=1,toolbar=0");
	if(printWindow) printWindow.focus();
}
</script>
</head>
<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
<!--<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=net.project.util.HTMLUtils.escape(form.getName())%>'
  					      jspPage='<%=SessionManager.getJSPRootURL() + "/form/FormList.jsp"%>'
						  queryString='<%="module="+ net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW + "&id=" + form.getID()%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" label='<%= PropertyProvider.get("prm.form.list.create.button.tooltip") %>'/>
		<tb:button type="modify" label='<%= PropertyProvider.get("prm.form.list.modify.button.tooltip") %>'/>
		<tb:button type="remove" label='<%= PropertyProvider.get("prm.form.list.remove.button.tooltip") %>'/>
		<tb:button type="notify" />		
		<tb:button type="workflow" />
		<tb:button type="search" />
	</tb:band>
  <tb:band name="action" groupHeading="Form"> 
    <tb:button type="custom" label='<%=PropertyProvider.get("prm.global.form.list.import.label")%>' function='<%= SessionManager.getJSPRootURL() + "/form/CSVImport.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW + "&id=" + form.getID()%>'/>
    <tb:button type="custom" label='<%=PropertyProvider.get("prm.global.form.list.export.label")%>' function='javascript:exportCSV();'/>
  </tb:band>  
</tb:toolbar>


-->

	<div id="left-navbar">
		<div style="padding: 0px;">
			<div id="form_channel">
				<div style="padding: 1px; width: 100%;">
					<div style="padding: 1px; width: 100%;">
						<div id="leftheading-<%=user.getCurrentSpace().getType() %>" style="left: 0px;"> <%=PropertyProvider.get("prm.global.tool.form.name")%> </div>
						<div class="project-title" style="margin-top: 30px;"> 	<%=PropertyProvider.get("prm.form.list.pagetitle", new Object [] {net.project.util.HTMLUtils.escape(form.getName()), form.getAbbreviation()})%></div>
						<% if(user.getCurrentSpace().isTypeOf(SpaceTypes.PROJECT_SPACE)){ %>
							<%= ((ProjectSpaceBean)user.getCurrentSpace()).getProjectSpaceDetails()%>
						<% } %>
						<br clear="both"/>
						<div class="spacer-for-toolbox"></div>
						<div class='toolbox-heading'>Toolbox</div>
						<div id="toolbox-item" class="toolbox-item">
							<%if(PropertyProvider.getBoolean("prm.global.actions.icon.isenabled")){ %>
							   <% if (PropertyProvider.getBoolean("prm.blog.isenabled") && (user.getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE) || user.getCurrentSpace().isTypeOf(SpaceTypes.PROJECT_SPACE))) { %>
							    <span id="blog-ItEnabled">
                                 <a href="javascript:blogit();" onmouseover=" document.imgblogit.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-blogit_over.gif'"
				     				onmouseout=" document.imgblogit.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-blogit_on.gif'">
				     				<img hspace="0" border="0" name="imgblogit" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-blogit_on.gif"
				 					title="Blog-it" alt="Blog-it" /><%= PropertyProvider.get("all.global.toolbar.standard.blogit") %></a>
                                </span>
                              <br/>	
                              <%} %>
                              <span>
                                <a href="javascript:create();"  onmouseover=" document.createimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-create.gif'"
				     				onmouseout=" document.createimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-create_on.gif'"><img hspace="0" border="0" name="createimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-create_on.gif"
				 					title="Create" alt="Create" /><%= PropertyProvider.get("prm.form.list.create.button.tooltip") %></a>
                              </span>
                              <br/>
                               <span>
                                <a href="javascript:modify();" onmouseover=" document.modifyimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-modify.gif'"
				     				onmouseout=" document.modifyimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-modify_on.gif'"><img hspace="0" border="0" name="modifyimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-modify_on.gif"
				 					title="Modify" alt="Modify" /><%= PropertyProvider.get("prm.form.list.modify.button.tooltip") %></a>
                              </span>
                              <br/>	
                              <span>
                                <a href="javascript:remove();" onmouseover=" document.removeimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-remove.gif'"
				     				onmouseout=" document.removeimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-remove_on.gif'"><img hspace="0" border="0" name="removeimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-remove_on.gif"
				 					title="Remove" alt="Remove" /><%= PropertyProvider.get("prm.form.list.remove.button.tooltip") %></a>
                              </span>
                              <br/>	 
                              <span>
                                <a href="javascript:notify();" onmouseover="document.notifyimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-notify.gif'"
				     				onmouseout=" document.notifyimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-notify_on.gif'"><img hspace="0" border="0" name="notifyimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-notify_on.gif"
				 					title="Notify" alt="Notify" /><%= PropertyProvider.get("all.global.toolbar.standard.notify") %></a>
                              </span>
                              <br/>
                              <%if(!user.getCurrentSpace().isTypeOf(Space.PERSONAL_SPACE)) {%>
                              <span>
                                <a href="javascript:workflow();" onmouseover="document.workflowimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-workflow.gif'"
				     				onmouseout=" document.workflowimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-workflow_on.gif'"><img hspace="0" border="0" name="workflowimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-workflow_on.gif"
				 					title="Workflow" alt="Workflow" /><%= PropertyProvider.get("all.global.toolbar.standard.workflow") %></a>
                              </span>
                              <br/><%}%> 
                              <span>
                                <a href="javascript:search();"  onmouseover="document.searchimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-search.gif'"
				     				onmouseout=" document.searchimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-search_on.gif'"><img hspace="0" border="0" name="searchimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-search_on.gif"
				 					title="Search" alt="Search" /><%= PropertyProvider.get("prm.form.list.search.button.tooltip") %></a>
                              </span>
                              <br/>
                              <%if(!user.getCurrentSpace().isTypeOf(Space.PERSONAL_SPACE)) {%>
                              <span>
                                <a href="javascript:security();"  onmouseover="document.securityimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-security.gif'"
				     				onmouseout=" document.securityimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-security_on.gif'"><img hspace="0" border="0" name="securityimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-security_on.gif"
				 					title='<%= PropertyProvider.get("all.global.toolbar.standard.security") %>' alt='<%= PropertyProvider.get("all.global.toolbar.standard.security") %>' /><%= PropertyProvider.get("all.global.toolbar.standard.security") %></a>
                              </span>
                              <br/>  
                              <%}%>
						 	<%} else { %>
						 		<% if (PropertyProvider.getBoolean("prm.blog.isenabled") && (user.getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE) || user.getCurrentSpace().isTypeOf(SpaceTypes.PROJECT_SPACE))) { %>
							  <span id="blog-ItEnabled">
                                 <a href="javascript:blogit();"><%= PropertyProvider.get("all.global.toolbar.standard.blogit") %></a>
                              </span>
                              <br/>
                              <%} %>
                              <span>
                                <a href="javascript:create();"><%= PropertyProvider.get("prm.form.list.create.button.tooltip") %></a>
                              </span>
                              <br/>
                              <span>
                                <a href="javascript:modify();"><%= PropertyProvider.get("prm.form.list.modify.button.tooltip") %></a>
                              </span>
                              <br/>							
                              <span>
                                <a href="javascript:remove();"><%= PropertyProvider.get("prm.form.list.remove.button.tooltip") %></a>
                              </span>
                              <br/>                              
                              <span>
                                <a href="javascript:notify();"><%= PropertyProvider.get("all.global.toolbar.standard.notify") %></a>
                              </span>
                              <br/><%if(!user.getCurrentSpace().isTypeOf(Space.PERSONAL_SPACE)) {%>
                              <span>
                                <a href="javascript:workflow();"><%= PropertyProvider.get("all.global.toolbar.standard.workflow") %></a>
                              </span>
                              <br/><%}%>
                              <span>
                                <a href="javascript:search();"><%= PropertyProvider.get("prm.form.list.search.button.tooltip") %></a>
                              </span>
                              <br/>  
							  <%if(!user.getCurrentSpace().isTypeOf(Space.PERSONAL_SPACE)) {%>
                              <span>
                                <a href="javascript:security();"><%= PropertyProvider.get("all.global.toolbar.standard.security") %></a>
                              </span>
                              <br/><%}%>                              
                           <%} %>                                                                                        
						</div>
						<div id="toolbox-heading" class="toolbox-heading">Form</div>
						<div id="toolbox-item" class="toolbox-item">
						  <%if(PropertyProvider.get("prm.global.actions.icon.isenabled").equals("1")){ %>
                              <span>
                                <a href="javascript:print();" onmouseover="document.printimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-rollover-create-ext.gif'"
				     				onmouseout=" document.printimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-create-ext_on.gif'"><img hspace="0" border="0" name="printimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/toolbar-gen-create-ext_on.gif"
				 					title="Print" alt="Print" /><%= PropertyProvider.get("prm.form.designer.main.print.label") %></a>
                              </span>
                              <br/>						  
		   				      <span>
                                 <a href="<%= SessionManager.getJSPRootURL() + "/form/CSVImport.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW + "&id=" + form.getID()%>" onmouseover=" document.importimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/form-import_over.gif'"
				     				onmouseout=" document.importimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/form-import_on.gif'"><img hspace="0" border="0" name="importimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/form-import_on.gif"
				 					title="Import" alt="Import" /><%= PropertyProvider.get("prm.global.form.list.import.label") %></a>
    	                      </span>
                              <br/>	  
                              <span>
                                <a href="javascript:exportCSV();" onmouseover=" document.exportimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/form-export_over.gif'"
				     				onmouseout=" document.exportimg.src = '<%=SessionManager.getJSPRootURL() %>/images/icons/form-export_on.gif'"><img hspace="0" border="0" name="exportimg" src="<%=SessionManager.getJSPRootURL() %>/images/icons/form-export_on.gif"
				 					title="Export" alt="Export" /><%= PropertyProvider.get("prm.global.form.list.export.label") %></a>
                              </span>
                              <br/>	
						  <% } else { %>
                              <span>
                                <a href="javascript:print();"><%= PropertyProvider.get("prm.form.designer.main.print.label") %></a>
                              </span>
                              <br/>						  
							  <span>
                                 <a href="<%= SessionManager.getJSPRootURL() + "/form/CSVImport.jsp?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW + "&id=" + form.getID()%>"><%= PropertyProvider.get("prm.global.form.list.import.label") %></a>
                              </span>
                              <br/>
                              <span>
                                <a href="javascript:exportCSV();"><%= PropertyProvider.get("prm.global.form.list.export.label") %></a>
                              </span>
                              <br/>							
                           <% } %>					
						</div>
					</div>
				</div>
			</div>
		</div>		
	</div>

<%--------------------------------------------------------------------------------------------
  -- Page Content                                                                             
  ------------------------------------------------------------------------------------------%>
<div id='scrollwidecontent' style="top: 92px;">
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/form/FormListProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="displayedList" value="<%= formList.getID() %>">
<input type="hidden" name="sortBy" />
<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" />
<input type="hidden" name="id" />
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<!--<tr class="pageTitle"> 
	<td valign="top" align="left" class="pageTitle" colspan="3">
		<%=PropertyProvider.get("prm.form.list.pagetitle", new Object [] {net.project.util.HTMLUtils.escape(form.getName()), form.getAbbreviation()})%>
	</td>
</tr>
-->
<tr>
	<td colspan="3">
		<history:history> 
			<history:page display='<%=form.getName()%>'
  					      jspPage='<%=SessionManager.getJSPRootURL() + "/form/FormList.jsp"%>'
						  queryString='<%="module="+ net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW + "&id=" + form.getID()%>'
			/>
		</history:history>	
	</td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
	<td colspan="3">
		<table border="0" width="100%" cellpadding="0" cellspacing="0">
		<tr>
		<td align="left"><span class="tableHeader"><%=PropertyProvider.get("prm.form.list.listview.label")%></span> 
			<select name="displayListID" onChange="window.submit();">
				<jsp:getProperty name="form" property="formListOptionList" />
	        </select>
<%-- NOT READY YET.  Removed for Chevron install .  -Roger
			<a href="<%= SessionManager.getJSPRootURL() %>/form/ListManager.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>&id=<%=form.getID()%>"><span class="tableContent">Edit views</span></a>
--%>			
		</td>
		<td align="right" class="tableContent">&nbsp;
		</td>	
		<td>&nbsp;&nbsp;&nbsp;</td>
		</tr>
		</table>
	</td>
</tr>
<tr class="channelHeader">
	<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" border="0" width="8" height="15" alt=""></td>
	<td align="left" class="channelHeader">
		<%=net.project.util.HTMLUtils.escape(formList.getName())%> --
		<%= net.project.util.HTMLUtils.escape(formList.getDescription())%>
	</td>
	<td align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" border="0" width="8" height="15" alt=""></td>
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
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>
<template:getSpaceJS />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>
