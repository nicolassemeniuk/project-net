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
    info="Edit Form" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.form.*,
            net.project.space.SpaceURLFactory,
			net.project.security.*, 
			net.project.base.Module,
            net.project.util.JSPUtils" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%
	String mySpace=user.getCurrentSpace().getType();
	String refererSpaceURL = null ;
	
	// If the user is in their personal space, change context to Form Edit space
	if (mySpace == null || mySpace.equals(net.project.space.Space.PERSONAL_SPACE) && request.getParameter("spaceID")!= null 
			|| request.getParameter("redirectedFromTimeSumaryReport") != null && request.getParameter("redirectedFromTimeSumaryReport").equals("true") && request.getParameter("spaceID")!= null ) {
        String redirect = SpaceURLFactory.constructSpaceURLForMainPage(request.getParameter("spaceID")) +
                               "&page=" + java.net.URLEncoder.encode(request.getRequestURI() + "?" + request.getQueryString().replaceAll("&redirectedFromTimeSumaryReport=true", ""));
        response.sendRedirect(redirect);
            
        refererSpaceURL = net.project.space.SpaceURLFactory.constructSpaceURLForMainPage(user.getCurrentSpace());
        pageContext.setAttribute("refererURL",refererSpaceURL , pageContext.SESSION_SCOPE);	
        
        return;
    }
	
	if ( pageContext.getAttribute("refererURL", pageContext.SESSION_SCOPE) == null && request.getParameter("spaceID")!= null) {
    	refererSpaceURL = net.project.space.SpaceURLFactory.constructSpaceURLForMainPage(user.getCurrentSpace());
		pageContext.setAttribute("refererURL",refererSpaceURL , pageContext.SESSION_SCOPE);	
    }
	
	//added by IVANA : bug3161 
	net.project.space.Space  tempSpace = user.getCurrentSpace();
	securityProvider.setSpace(tempSpace);
	boolean hasAccess = false;
	if (securityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.FORM),
              net.project.security.Action.MODIFY)) {
		hasAccess = true;
	}	

	// Set up edit mode
	String editMode = null;
    String formDataName = null ;
	String formDataID = request.getParameter("id");

    // Creating a new formData instance
    if (JSPUtils.isEmpty(formDataID)) {
        editMode = "create";
        formDataName = PropertyProvider.get("prm.form.list.create.page.title");

    // Editing an existing formData instance
    } else {
        FormList formList = null;
        if (form.getID() != null) {
    	    formList = form.getDisplayList();
        }
        editMode = "modify";
        FormData formData = new FormData();
        formData.load(formDataID);
		formDataName = formData.getShortName();
        session.setAttribute("formDataName", formDataName);
//		try {
//			if (!formData.getForm().getOwningSpaceID().equals(user.getCurrentSpace().getID())) {
//				String redirect = SpaceURLFactory.constructSpaceURLForMainPage(formData.getForm().getOwningSpaceID()) + "&page=" + java.net.URLEncoder.encode(request.getRequestURI() + "?" + request.getQueryString());
//				response.sendRedirect(redirect);
//				refererSpaceURL = SpaceURLFactory.constructSpaceURLForMainPage(user.getCurrentSpace());
//				pageContext.setAttribute("refererURL", refererSpaceURL, pageContext.SESSION_SCOPE);
//				return;
//			}
//		} catch (Exception e) {}

        //Make sure the correct form is in memory.  If it isn't, set it now
        if (!formData.getForm().getID().equals(form.getID())) {
            form = formData.getForm();
            form.loadLists(true);
            pageContext.setAttribute("form", form, PageContext.SESSION_SCOPE);
        } else {
            //We use the existing one, because it might have custom lists
            formData.setForm(form);
            form.setData(formData);
        }
    }
%>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkRange.js" />
<template:import type="javascript" src="/src/checkIsNumber.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<template:import type="javascript" src="/src/forms.js" />
<script language="javascript">
var theForm;
var isLoaded = false;
var goSubmit = true;
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function documents() {
    self.document.location = "<%=SessionManager.getJSPRootURL()+"/form/FormEditDocuments.jsp?module="+Module.FORM+"&formDataID="+formDataID%>";
}

function discussion() {
    self.document.location = "<%=SessionManager.getJSPRootURL()+"/form/FormEditDiscussion.jsp?module="+Module.FORM+"&formDataID="+formDataID%>";
}

function help(){
	var helplocation='<%= SessionManager.getJSPRootURL() %>'+'/help/Help.jsp?page=form_edit';
	openwin_help(helplocation);
}

var fieldNames = new Array(<%=form.getFields().size()%>);

function validateFields() {
    return checkRequiredFields(fieldNames, "", " is required") && goSubmit;
}
</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
            <history:module display='<%=PropertyProvider.get("prm.form.main.module.history")%>'
                            jspPage='<%=SessionManager.getJSPRootURL() + "/form/Main.jsp"%>'
                            queryString='<%="module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW%>'
                            />
            <history:page display='<%=form.getName()%>'
  					      jspPage='<%=SessionManager.getJSPRootURL() + "/form/FormList.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW + "&id=" + form.getID()%>'
			              />
            <history:page display='<%=formDataName%>'
                          jspPage='<%=SessionManager.getJSPRootURL() + "/form/FormEdit.jsp"%>'
                          queryString='<%="module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW+"&id="+formDataID+"&readOnly=false"%>'
                          level='1'/>
        </history:history>
	</tb:setAttribute>
	<tb:band name="standard" >
	</tb:band>	
</tb:toolbar>

<div id='scrollwidecontent'>
<br>
<channel:channel name="formedit_main" customizable="false">
    <channel:insert name="Main" title="" minimizable="false" width="100%" 
                    closeable="false" include="/form/include/FormEdit.jsp?readOnly=false">
        <%if ((form.getSupportsDocumentVault()) && (!editMode.equals("create"))) { %>
        <channel:button style="channel" type="documents" label='<%=PropertyProvider.get("prm.form.edit.documents.button.label")%>' href="javascript:documents();"/>
        <%}%>
        <%if ((form.getSupportsDiscussionGroup()) && (!editMode.equals("create"))) { %>
        <channel:button style="channel" type="discuss" label='<%=PropertyProvider.get("prm.form.edit.discussion.button.label")%>' href="javascript:discussion();"/>
        <%}%>
         <% if(hasAccess){ %>
        <% if (!editMode.equals("create")) { %>
        <channel:button style="channel" type="modify" labelToken="prm.form.edit.update.button.label" href="javascript:update();"/>
            <%}%>
        <channel:button style="channel" type="submit" labelToken="prm.form.edit.submit.button.label" href="javascript:submit();"/>
        <%}%>
    </channel:insert>
</channel:channel>

<%--<%if ((form.getSupportsAssignment()) && (!editMode.equals("create"))) { %>
<!--<channel:channel name="formedit_assignment" customizable="false">
    <channel:insert name="Assignment" title="" minimizable="false" width="100%" 
                    closeable="false" include='<%="/form/include/FormAssignmentEdit.jsp?module="+Module.FORM+"&formDataID="+formDataID+"&formDataName="+formDataName%>'>
    </channel:insert>
</channel:channel>-->
<%}%>--%>

<%-- These script functions rely on the correctly loaded form in the session --%>
<script language="javascript">
function setup() {
   load_menu();
   theForm = self.document.forms[0];
   isLoaded = true;
   var workComplete = Ext.DomQuery.selectNode("#work_complete");
   if(workComplete && workComplete.value == '0') {
       var workPercentComplete  = Ext.DomQuery.selectNode("#work_percent_complete");
       if(workPercentComplete)
           workPercentComplete.readOnly = true;
   }
}

function cancel() {
	self.document.location = '<%= pageContext.getAttribute("refererURL", pageContext.SESSION_SCOPE) != null ? (String) pageContext.getAttribute("refererURL",pageContext.SESSION_SCOPE) : "FormList.jsp?load=false&module="+Module.FORM+"&action="+Action.VIEW+"&id="+form.getID() %>';
}

function isValidWork(){
	if(theForm.work){
		  if (checkIsPositiveNumber(theForm.work,'<%=PropertyProvider.get("prm.schedule.taskedit.error.work.validnumber.message")%>', true )) {
	          return true;
		  }
		  return false;
	 }
	 return true;
}

function submit() {
	if ("modify" == "<%=editMode%>") {
		theForm.elements["id"].value = "<%=formDataID%>";
		theForm.elements["action"].value = "<%=Action.MODIFY%>";
	} else {
		theForm.elements["action"].value = "<%=Action.CREATE%>";
	}
    if (isValidWork()) {
    	if(validateFields()){
        	theAction("submit");
        	theForm.submit();
        }
     }else{
      		extAlert(errorTitle,'<display:get name="prm.schedule.quickadd.work.validnumber.message"/>' , Ext.MessageBox.ERROR);
   	} 
}

function update() {
	if ("modify" == "<%=editMode%>") {
		theForm.elements["id"].value = "<%=formDataID%>";
		theForm.elements["action"].value = "<%=net.project.security.Action.VIEW%>";

        if (validateFields()) {
            theAction("update");
            theForm.submit();
        }
    }
}

function reset() {
	theForm.reset();
}
</script>

<%-----------------------------------------------------------------------------------------------------------
     --  Linker                                                                                              
	 ------------------------------------------------------------------------------------------------------%>
<%
	// Display links in channel only for existing formdata.
	if (form.hasData() && form.getData().getID() != null && !form.getData().getID().equals("null")) {
		// Get form data as a page scope object for link taglib
		net.project.form.FormData formData = form.getData();
		pageContext.setAttribute("formData", formData, PageContext.PAGE_SCOPE);
		String refererLink = SessionManager.getJSPRootURL() + "/form/FormEdit.jsp" +
						 "?module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW + "&id=" + form.getData().getID();
%>
<channel:channel name='<%="FormEdit_" + form.getName()%>' customizable="false">
	<links:insert rootObjectName="formData" rootObjectScope="page" view="all"
				  module='<%=""+Module.FORM%>' objectID="<%=formData.getID()%>"
				  referringLink="<%=refererLink%>">
		<channel:insert name='<%="FormEditView_Links_" + form.getData().getName()%>'
						title='<%=PropertyProvider.get("prm.form.edit.channel.links.title")%>' width="400" minimizable="false" closeable="false">
		</channel:insert>
	</links:insert>
</channel:channel>
<% } %>

<%-----------------------------------------------------------------------------------------------------------
--  Bottom Action Bar                                                                            
------------------------------------------------------------------------------------------------------%>
<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
	<tb:band name="action" >
	    <% if(hasAccess){ %>
	    	<tb:button type="submit" />
    	<%}%>
		<!-- tb:button type="cancel" / -->
		
	</tb:band>
</tb:toolbar>

<%
	//Reset the referer link ... It will be set in the FormEdit Page according to the user space
	pageContext.removeAttribute("refererURL", PageContext.SESSION_SCOPE);
%>

</div>

<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS /></body>
</html>


