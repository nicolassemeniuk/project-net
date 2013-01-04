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
    info="Documents Deleted Main Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.document.DocumentManagerBean,
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.space.ISpaceTypes,
			net.project.base.property.PropertyProvider,
            net.project.util.JSPUtils"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess 
				module="<%=net.project.base.Module.TRASHCAN%>"
				action="list_Deleted" 
/>

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%  final String SINGLE_QUOTE = "'";

    boolean isHistoryTypeSpecified = false;
	
	if(!isHistoryTypeSpecified) {
        // This means we've been accessed from the navbar link

        // Check to see if the docManager was previously showing
        // a system folder
        // If so, we want to reset the doc manager to the workspace
        // root folder
        if (docManager.isVisitSystemContainer()) {
            // Reset the docManager
    		docManager = new DocumentManagerBean();
		    pageContext.setAttribute("docManager", docManager, PageContext.SESSION_SCOPE);
        }
	}

    String mySpace = user.getCurrentSpace().getType();
	
    String refLink,refLinkEncoded=null;
	refLinkEncoded = java.net.URLEncoder.encode("/trashcan/ViewDeletedDocuments.jsp?module="+net.project.base.Module.TRASHCAN);
	
    // Construct the URL that other pages will use to forward or redirect to
    // To return to this Main page
    // By constructing it from the URI and QueryString, we ensure that the page
    // is shown in the same state each time
    StringBuffer topContainerURL = new StringBuffer();
    //topContainerURL.append(request.getRequestURI()).append("?").append(request.getQueryString());
    topContainerURL.append("/trashcan/ViewDeletedDocuments.jsp").append("?").append(request.getQueryString());

    // If a historyType parameter was not specified in the query string, we add it
    // as unspecified.  This ensures that the docManager will not be flushed next
    // time it is visited
    if (!isHistoryTypeSpecified) {
        if (!topContainerURL.toString().endsWith("?")) {
            topContainerURL.append("&");
        }
        topContainerURL.append("historyType=").append("unspecified");
    }
    docManager.getNavigator().put("TopContainer",topContainerURL.toString()); 
    docManager.setCancelPage((String)docManager.getNavigator().get("TopContainerReturnTo"));
	docManager.setListDeleted();
%>


<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />
<template:import type="css" src="/styles/blog.css" />
<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/document_prototypes.js" />

<%-- THIS IS THE DOCUMENT WHICH CONTAINS THE SCRIPT FOR THIS PAGE --%>
<template:import type="javascript" src="/src/document/list-actions.js" />

<script language="javascript">
    var theForm;
    var errorMsg;
    var isLoaded = false;
    var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";
    //Internationalization variables for pop up messages
    var cannotMoveRootErrMes = '<display:get name="prm.global.javascript.moveroot.error.message" />';
    var cannotRemoveRootErrMes = '<display:get name="prm.global.javascript.removeroot.error.message" />';
    var noSelectionErrMes = '<display:get name="prm.global.javascript.verifyselection.noselection.error.message" />';
    var confirmDeletionMes = '<display:get name="prm.global.javascript.confirmdeletion.message" />';
    var cannotViewPropertiesRootErrMes = '<display:get name="prm.global.javascript.viewpropertiesroot.error.message" />';
	var moduleId = <%=Module.TRASHCAN%>;
	var spaceId='<%=user.getCurrentSpace().getID()%>';
	var blogItFor="forTrashcan";
	
    function setup() {

        load_menu('<%=user.getCurrentSpace().getID()%>');
		theForm = self.document.forms[0];
        isLoaded = true;

    } // end setup()

	function help()
	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=document_main";
		openwin_help(helplocation);
	}

    function showError(errorMessage) {
        document.getElementById("errorLocationID").innerHTML = (errorMessage + "<br/>");
    }
</script>
</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Draw Toolbar
  -----------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" groupTitle="prm.setup.trashcan.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.setup.trashcan.title")%>' 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/trashcan/ViewDeletedDocuments.jsp"%>'
						  queryString='<%="module=" + net.project.base.Module.TRASHCAN%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="properties" label='<%= PropertyProvider.get("prm.document.main.properties.button.tooltip") %>'/>
		<tb:button type="remove" label='<%= PropertyProvider.get("prm.document.main.perdelete.button.tooltip") %>' />
	</tb:band>
	<tb:band name="trashcan">
	  <tb:button type="undo_delete" label='<%= PropertyProvider.get("prm.document.main.undodelete.button.tooltip") %>' />
	</tb:band>  
</tb:toolbar>

<div id='content'>

    <jsp:include page="include/container.jsp" flush="true" /> 

    <br clear=all>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>