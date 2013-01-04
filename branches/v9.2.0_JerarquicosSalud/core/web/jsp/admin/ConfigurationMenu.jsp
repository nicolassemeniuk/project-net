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

<%----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Configuration Menu" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.security.SessionManager,
			net.project.security.Action,
			net.project.base.Module,
			net.project.admin.ApplicationSpace" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<%
	String refLink = "/admin/ConfigurationMenu.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW;
	String refLinkEncoded = java.net.URLEncoder.encode(refLink);
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	theForm = self.document.forms["main"];
	isLoaded = true;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_configuration_menu";
	openwin_help(helplocation);
}

function create() { 
	document.location = "<%=SessionManager.getJSPRootURL()%>/configuration/ConfigurationEdit.jsp?mode=create&module=<%=Module.CONFIGURATION_SPACE%>&action=<%=Action.CREATE%>&refLink=<%=refLinkEncoded%>";
}

function view(id) {
	if (arguments.length != 0) {
		document.location = "<%=SessionManager.getJSPRootURL()%>/configuration/Main.jsp?module=<%=Module.CONFIGURATION_SPACE%>&action=<%=Action.VIEW%>&id=" + 
							id;
	}
}

<%-- Modify capability removed from app space configuration menu
     Must now enter configuration space to modify it.
function modify(id) {
	if (arguments.length != 0) {
		aRadio = theForm.configuration_id;
		if (aRadio) {
			for (i = 0; i < aRadio.length; i++) {
				if (aRadio[i].value == id) aRadio[i].checked = true;
			}
		}
	}
	if (isSelected(theForm.configuration_id)) {
		document.location = "<%=SessionManager.getJSPRootURL()%>/admin/ConfigurationEdit.jsp?module=<%=Module.CONFIGURATION_SPACE%>&action=<%=Action.MODIFY%>&configurationID=" + 
							 getSelected(theForm.configuration_id);
	}
}
--%>

function remove() {
	if (isSelected(theForm.configuration_id)) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.removeconfiguration.confirm")%>', function(btn) { 
			if(btn == 'yes'){ 
				document.location = "<%=SessionManager.getJSPRootURL()%>/configuration/ConfigurationRemoveProcessing.jsp?module=<%=Module.CONFIGURATION_SPACE%>&action=<%=Action.DELETE%>&configurationID=" + getSelected(theForm.configuration_id) + "&refLink=<%=refLinkEncoded%>";
			}else{
			 	return false;
			}
		});
	}
}

function reset() { 
	self.document.location = JSPRootURL + '/admin/ConfigurationMenu.jsp?module=<%=Module.APPLICATION_SPACE%>&action=<%=Action.VIEW%>'; 
}

<%-- Not implemented yet
function security() {
	var security;
	if (isSelected(theForm.workflow_id)) {
	    if (!security || security.closed == true) {
			security = openwin_security("security");
		}
        if (security && !security.closed) {
    	    theAction("security");
            theForm.target = "security";
			theForm.action.value = "<%=Action.MODIFY_PERMISSIONS%>";
			theForm.id.value = getSelected(theForm.configuration_id);
            theForm.submit();
            security.focus();
        }
    }
}
--%>

function isSelected(aList) {
	if (aList) {
		if (aList.checked) return true;
		for (i = 0; i < aList.length; i++) {
			if (aList[i].checked) return true;
		}
	}
	return false;
}

function getSelected(aList) {
	if (aList) {
		if (aList.checked) return aList.value;
		for (i = 0; i < aList.length; i++) {
			if (aList[i].checked) return aList[i].value;
		}
	}
	return null;
}

function copy() {
	if (isSelected(theForm.configuration_id)) {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', "Are you sure you want to copy this configuration?", function(btn) { 
			if(btn == 'yes'){ 
				theAction("copy");
            	theForm.target = "_self";
    			theForm.action.value = "<%=Action.CREATE%>";
    			theForm.id.value = getSelected(theForm.configuration_id);
    			theForm.submit();
			}else{
			 	return false;
			}
		});
	}
}
</script>

</head>
<body  onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />


<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.configuration">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display="Configurations" 
						  jspPage='<%=SessionManager.getJSPRootURL() + "/admin/ConfigurationMenu.jsp"%>'
						  queryString='<%="module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" />
<%--		<tb:button type="modify" />--%>
		<tb:button type="remove" />
		
		<tb:button type="copy" />
		
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" method="post" action="<session:getJSPRootURL />/admin/ConfigurationMenuProcessing.jsp">
	<input type="hidden" name="theAction">
	<input type="hidden" name="module" value="<%=Module.CONFIGURATION_SPACE%>" />
	<input type="hidden" name="action" />
	<input type="hidden" name="id" />
	
<channel:channel name='<%="ApplicationSpaceMain_" + applicationSpace.getName()%>' customizable="false">

	<channel:insert name='<%="ConfigurationMenu_" + applicationSpace.getName()%>'
					title="Defined Configurations" minimizable="false" closeable="false"
					include="include/ConfigurationMenu.jsp" >
	</channel:insert>

</channel:channel>
	
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
