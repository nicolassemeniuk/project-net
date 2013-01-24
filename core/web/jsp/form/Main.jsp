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

<%@ include file="/base/taglibInclude.jsp" %>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Form Menu" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.space.*,
			net.project.form.*,
			net.project.security.*, 
			net.project.persistence.*,
			net.project.base.*" 
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />
<jsp:useBean id="formMenu" class="net.project.form.FormMenu" scope="page" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%! 
	boolean hasForms = true;
	boolean hasModifyPermission = false;
%>

<% 
	String groupTitle = "prm.personal.nav.form";
	if(Space.PERSONAL_SPACE.equals(user.getCurrentSpace().getType())) {
        groupTitle = "prm.personal.mainpage.form.title";
    }
	//form.clear();
	formMenu.setSpace(user.getCurrentSpace());
	formMenu.setUser(user);
	formMenu.setDisplayPending(false);
	
	try {
		formMenu.load();
	}
	catch (FormException fe) {
		hasForms = false;
	}

	String refLink,refLinkEncoded=null;
	refLinkEncoded = java.net.URLEncoder.encode(SessionManager.getJSPRootURL()+"/form/Main.jsp?module="+net.project.base.Module.FORM);
	
	// Set boolean which decides whether to enable Designer link and Create button
	// User can modify if they are Space Admin or Power User	
	hasModifyPermission = securityProvider.isUserSpaceAdministrator() || securityProvider.isActionAllowed(null, net.project.base.Module.FORM, net.project.security.Action.MODIFY);
	// securityProvider.isUserPowerUser() 
%>

<template:getSpaceCSS/>
<template:import type="javascript" src="/src/notifyPopup.js" />
<template:import type="javascript" src="/src/dhtml/xmlrequest.js" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
	var noSelectionErrMes = '<display:get name="prm.global.javascript.verifyselection.noselection.error.message" />';    
	
function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

function cancel() { self.document.location = JSPRootURL + "/project/Dashboard"; }
function reset()  { self.document.location = JSPRootURL + "/form/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.VIEW%>"; }

function search() {
    self.document.location = JSPRootURL + '/search/SearchController.jsp?module=<%=net.project.base.Module.FORM+"&refLink="+refLinkEncoded%>&otype=<%=ObjectType.FORM_DATA%>';
}

function create() { 
    theForm.target = "_self";
	theForm.action.value = "<%=net.project.security.Action.CREATE%>";
	theForm.elements["id"].value = '';
	theAction("create");
	theForm.submit();
}
function notify(){
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>', 'nil', 'nil', 'true')){
		  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?targetObjectID=" + getSelection(theForm) + '&action=<%=net.project.security.Action.MODIFY%>&module=<%=net.project.base.Module.FORM%>');
		  openNotifyPopup( getSelection(theForm), m_url);
		} else {
			  var m_url = (JSPRootURL + "/notification/CreateSubscription2.jsp?objectType=form&action="+'<%=net.project.security.Action.CREATE%>&module=<%=net.project.base.Module.FORM%>'+'&isCreateType=1');
			  openNotifyPopup( getSelection(theForm), m_url);
		}
}

function help(){
	var helplocation='<%= SessionManager.getJSPRootURL() %>'+'/help/Help.jsp?page=form_main';
	openwin_help(helplocation);
}

function workflow() {

   if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
         
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
</script>

</head>
<body  onLoad="setup();" id="bodyWithFixedAreasSupport" class="main">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle='<%= PropertyProvider.get(groupTitle) %>'>
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display='<%=PropertyProvider.get("prm.form.main.module.history")%>' 
						    jspPage='<%=SessionManager.getJSPRootURL() + "/form/Main.jsp"%>'
							queryString='<%="module=" + net.project.base.Module.FORM + "&action=" + net.project.security.Action.VIEW%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="notify" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="MainProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=net.project.base.Module.FORM%>" />
<input type="hidden" name="action" />
<input type="hidden" name="id" />
<!--
<%
if (hasModifyPermission)
{
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td align="right" class="tableContent"><a href="designer/Main.jsp?module=<%=net.project.base.Module.FORM%>&action=<%=net.project.security.Action.MODIFY%>"><%=PropertyProvider.get("prm.form.main.designer.link")%></a>&nbsp;&nbsp;&nbsp;&nbsp;</td>
</tr>
</table>
<%
}
%> 
--> 
<% if (formMenu.size() > 0) { %>

	<%-- Apply stylesheet to format Form Menu table --%>
	<jsp:setProperty name="formMenu" property="stylesheet" value="/form/xsl/form-menu.xsl" />
	<jsp:getProperty name="formMenu" property="presentation" />			

<% } else { %>
	<table align="center"><tr><td><%=PropertyProvider.get("prm.form.main.noforms.message")%></td></tr></table>
<% } %>
 
</form>
<p />
<br clear=all>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<template:import type="javascript" src="/src/notifyPopup.js" />
</body>
</html>
