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
    info="Document Main Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.Module,
    		net.project.space.Space,
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
				module="<%=net.project.base.Module.DOCUMENT%>"
				action="view" 
/>

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%  final String SINGLE_QUOTE = "'";
	//Determine how the history will be displayed.  Is document a module, or is
    //it a tool embedded in another tool?
    String historyType = request.getParameter("historyType");
    boolean isHistoryTypeSpecified = !JSPUtils.isEmpty(historyType);
    String groupTitle = "prm.global.tool.document.name"; 

	if(Space.PERSONAL_SPACE.equals(user.getCurrentSpace().getType())) {
        groupTitle = "prm.document.mainpage.personal.title";
    }
    
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
	refLinkEncoded = java.net.URLEncoder.encode(SessionManager.getJSPRootURL()+"/document/Main.jsp?module="+net.project.base.Module.DOCUMENT);
	
    // Construct the URL that other pages will use to forward or redirect to
    // To return to this Main page
    // By constructing it from the URI and QueryString, we ensure that the page
    // is shown in the same state each time
    StringBuffer topContainerURL = new StringBuffer();
    //topContainerURL.append(request.getRequestURI()).append("?").append(request.getQueryString());
    topContainerURL.append("/document/Main.jsp").append("?").append(request.getQueryString());

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
	docManager.unSetListDeleted();
	session.setAttribute("refererLink", SessionManager.getJSPRootURL()+"/document/Main.jsp?module="+net.project.base.Module.DOCUMENT);
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

<%-- verify last operation result --%>
<script>
if ( <%= (Integer)request.getAttribute("lastDocErrorCode") == null ? "-1" : request.getAttribute("lastDocErrorCode") %> == 5001) {
	var errorMessage = '<display:get name="prm.document.error.duplicatename.message" />';
	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
}
</script>

<script language="javascript">
    var theForm;
    var errorMsg;
    var isLoaded = false;
    var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";
    //Internationalization variables for pop up messages
    var noSelectionErrMes = "<display:get name='prm.global.javascript.verifyselection.noselection.error.message' />";
    var confirmDeletionMes = "<display:get name='prm.global.javascript.confirmdeletion.message' />";
    var addDocumentToWorkflowErrMes = "<display:get name='prm.global.javascript.adddocumenttoworkflow.error.message' />";
    var documentCheckInErrMes = "<display:get name='prm.global.javascript.documentcheckin.error.message' />";
    var documentCheckOutErrMes = "<display:get name='prm.global.javascript.documentcheckout.error.message' />";
    var documentUndoCheckOutErrMes = "<display:get name='prm.global.javascript.documentundocheckout.error.message' />";
    var notificationErrMes = "<display:get name='prm.global.javascript.notification.error.message' />";
    var viewObjectErrMes = "<display:get name='prm.global.javascript.viewobject.error.message' />";
    var cannotMoveRootErrMes = "<display:get name='prm.global.javascript.moveroot.error.message' />";
    var cannotRemoveRootErrMes = "<display:get name='prm.global.javascript.removeroot.error.message' />";
    var cannotViewPropertiesRootErrMes = "<display:get name='prm.global.javascript.viewpropertiesroot.error.message' />";
	var blogItFor ='document'+'<%=user.getCurrentSpace().getSpaceType().getName()%>';
	var spaceId = <%= user.getCurrentSpace().getID()%>;
	var moduleId = <%= Module.DOCUMENT%>;
	var objectTypeNameFor = 'Document';
	var documentNotCheckedOut="<display:get name='prm.document.documentcontrolmanager.verifycheckin.notcheckedout.message' />";

    function setup() {

        load_menu('<%=user.getCurrentSpace().getID()%>');
		theForm = self.document.forms["container"];
        isLoaded = true;

    } // end setup()

	
	function help()
	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=document_main";
		openwin_help(helplocation);
	}

	function link() { 

		if(theForm.elements["selected"] && verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	    	if ( !isObjectTypeOf (getSelection(theForm), 'document') ) {
	    		var errorMsg = '<display:get name="prm.document.main.documentlinkingvalidation.message" />';
	    		extAlert(errorTitle, errorMsg , Ext.MessageBox.ERROR);
	    	} else {
				if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
				// BFD-4427
					self.document.location = JSPRootURL + "/link/LinkManager.jsp?context=<%=net.project.link.ILinkableObject.GENERAL%>&view=<%=net.project.link.LinkManagerBean.VIEW_ALL%>&id=" + getSelection(theForm) + "&action=<%=net.project.security.Action.MODIFY%>&module=<%=net.project.base.Module.DOCUMENT%>";
				} else {
					var errorMessage = '<display:get name="prm.document.main.objectlinkingvalidation.message" />';
					extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
				}
	    	} // if object is document
		}	
	
	}

	function discussTab() {
		self.location = "<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker?view=discuss&theAction=discuss&selected=" + getSelection(theForm) + "&action=<%=net.project.security.Action.VIEW%>&module=<%=net.project.base.Module.DOCUMENT%>";
	}

	<%
	// Only allow security in project space
	if (mySpace != null && !mySpace.equals(net.project.space.Space.PERSONAL_SPACE))
		{
	%>
		function security() 
		{
		   if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) 
		   {    
				if (!security)
					var security = openwin_security("security");
			
				if (security) {
					theAction("security");
					theForm.target = "security";
					theForm.submit();
					security.focus();
				}
			}
		}
	<%
		}
	%>

    function showError(errorMessage) {
        document.getElementById("errorLocationID").innerHTML = (errorMessage + "<br/>");
    }

    function search() {
        self.document.location = JSPRootURL + '/search/SearchController.jsp?module=<%=Module.DOCUMENT+"&refLink="+refLinkEncoded+"&otype="+net.project.base.ObjectType.DOCUMENT%>';
    }
	function reset() {
		self.document.location = JSPRootURL + "/document/Main.jsp?module=<%=net.project.base.Module.DOCUMENT%>"; 
	}
</script>
</head>


<body onLoad="setup();" class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Draw Toolbar
  -----------------------------------------------------------------------%>

<tb:toolbar style="tooltitle" groupTitle='<%= PropertyProvider.get(groupTitle) %>'>
	<tb:setAttribute name="leftTitle">
        <% if (JSPUtils.isEqual(historyType, "pageLevel2")) { %>
		<history:history>
            <history:page display='<%= PropertyProvider.get("prm.document.main.page.history") %>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/document/Main.jsp"%>'
  						  queryString='<%="module=" + net.project.base.Module.DOCUMENT%>'
                          level='2'/>
		</history:history>
        <% } else if (JSPUtils.isEmpty(historyType)) { %>
		<history:history>
            <history:module display='<%= PropertyProvider.get("prm.document.main.module.history") %>'
                            jspPage='<%=SessionManager.getJSPRootURL() + "/document/Main.jsp"%>'
                            queryString='<%="module=" + net.project.base.Module.DOCUMENT%>'
			/>
		</history:history>
        <% } else { %>
        <history:display/>
        <% } %>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
		<tb:button type="create" label='<%= PropertyProvider.get("prm.document.main.create.button.tooltip") %>' />
		<tb:button type="modify" label='<%= PropertyProvider.get("prm.document.main.modify.button.tooltip") %>' />
		<tb:button type="remove" label='<%= PropertyProvider.get("prm.document.main.remove.button.tooltip") %>' />
		<tb:button type="properties" label='<%= PropertyProvider.get("prm.document.main.properties.button.tooltip") %>'/>
		<tb:button type="notify" />
		<tb:button type="link" label='<%= PropertyProvider.get("all.document.toolbar.link.tooltip") %>'/>
		<%if (!mySpace.equals(net.project.space.Space.PERSONAL_SPACE)) {%>
			<tb:button type="workflow" />
			<tb:button type="security" />
		<%}%>
	</tb:band>	
	<tb:band name="document" showAll="true" groupHeading='<%= PropertyProvider.get("prm.document.groupheading.label") %>'>
		<tb:button type="check_out" />
		<tb:button type="check_in" />
		<tb:button type="view" />
		<tb:button type="undo_check_out" />
		<tb:button type="move" />
		<tb:button type="new_folder" />
	</tb:band>

</tb:toolbar>

<div id='content'>

    <jsp:include page="include/container.jsp" flush="true" /> 
    <br clear=all>
 
   <% if (request.getParameter("formId") != null) { %>
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
			<tbody>
				<tr>
				   <td align="right">
				       <a href=<%= SessionManager.getJSPRootURL() + "/form/FormEdit.jsp?module=" + Module.FORM+ "&action=1&id="+request.getParameter("formId")+"&readOnly=false" %>> Return to <%= session.getAttribute("formDataName") %>  </a>&nbsp;&nbsp;&nbsp;
				   </td>
				</tr>
			</tbody>
		</table>
    <% } %>
   
    <%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/document/list-actions.js" />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>


