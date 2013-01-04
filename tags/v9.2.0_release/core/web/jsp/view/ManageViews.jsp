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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Manage Views"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.portfolio.view.IViewList,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="viewBuilder" type="net.project.portfolio.view.ViewBuilder"  scope="session" />

<%
    String module = request.getParameter("module");
    IViewList viewList = viewBuilder.getViewList();
%>
<security:verifyAccess module="<%=Integer.valueOf(module).intValue()%>" action="view" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="project" />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';

function setup() {
    theForm = self.document.forms[0];
    isLoaded = true;
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=view_manage";
    openwin_help(helplocation);
}

function cancel() { self.document.location = JSPRootURL + "/portfolio/PersonalPortfolio.jsp?module=<%=module%>&portfolio=true"; }
function reset() { self.document.location = JSPRootURL + "/view/ManageViews.jsp?module=<%=module%>"; }

function modify(id) {
	if(!theForm.viewID) {
		var errorMessage = '<display:get name="prm.view.manageviews.javascript.verifyselection.noviews.error.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
	} else {
		if((theForm.viewID.length == null) && (theForm.viewID.checked == false)) {
			var errorMessage = '<display:get name="prm.global.javascript.verifyselection.noselection.error.message" />';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		} else {
		    if (arguments.length > 0) {
		        selectRadio(theForm.viewID, id);
		    }
		    if (!checkRadio(theForm.viewID, "<display:get name="prm.global.javascript.verifyselection.noselection.error.message" />")) return;
		    theAction("modify");
		    theForm.submit();
		}
	}
}

function create() {
    theAction("create");
    theForm.submit();
}

function remove() {
	if(!theForm.viewID) {
		var errorMessage = '<display:get name="prm.view.manageviews.javascript.verifyselection.noviews.error.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
	} else {
		if((theForm.viewID.length == null) && (theForm.viewID.checked == false)) {
			var errorMessage = '<display:get name="prm.global.javascript.verifyselection.noselection.error.message" />';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
		} else {
			Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title" />', '<display:get name="prm.view.manageviews.delete.message" />', function(btn) { 
				if(btn == 'yes'){ 
			    	if (!checkRadio(theForm.viewID, "<display:get name="prm.global.javascript.verifyselection.noselection.error.message" />")) return;
			    	theAction("remove");
			    	theForm.submit();
				}
			});
		}
	}
}

function changeView(viewID) {
    self.document.location = JSPRootURL + "/portfolio/PersonalPortfolio.jsp?"+
        "module=<%=module%>&viewID=" + viewID + "&portfolio=true";
}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" showAll="true" leftTitle='<%=PropertyProvider.get("prm.global.view.manage.title")%>' groupTitle="prm.portfolio.portfolio.objecttype.description" space="project">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display='<%=PropertyProvider.get("prm.global.view.manage.title")%>'
                            jspPage='<%=SessionManager.getJSPRootURL() + "/view/ManageViews.jsp?module=" + module%>' />
            </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
        <tb:button type="create" />
        <tb:button type="modify" />
        <tb:button type="remove" />
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" action="<%=SessionManager.getJSPRootURL()%>/view/ManageViewsProcessing.jsp" method="post">
    <input type="hidden" name="module" value="<%=module%>">
    <input type="hidden" name="theAction">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr class="channelHeader">
    <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
    <td valign="middle" align="left" class="channelHeader"><display:get name="prm.global.view.channel.views.title" /></td>
    <td width="1%" align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td>
        <pnet-xml:transform stylesheet="/view/xsl/manage-views-list.xsl" content="<%=viewList.getModifiableViewsXMLBody()%>" />
    </td>
    <td>&nbsp;</td>
</tr>
</table>

</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS space="project" />
</body>
</html>