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
|   $Revision: 19063 $
|       $Date: 2009-04-05 14:27:40 -0300 (dom, 05 abr 2009) $
|     $Author: nilesh $
|
|--------------------------------------------------------------------%>
<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.xml.XMLFormatter,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="space" type="net.project.space.Space" scope="request" />
<jsp:useBean id="xmlProvider" type="net.project.persistence.IXMLPersistence" scope="request" />

<%
    // Save referer or get from request if null
    String referer = request.getParameter("referer");
    if (referer == null || "".equals(referer)) {
        referer = (String) request.getAttribute("referer");
    } 
    //if still null then set it to schedule main page as default
    if(referer == null || "".equals(referer))
        referer="/workplan/taskview?module="+Module.SCHEDULE+"&action="+Action.VIEW;
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">
var theForm;
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
    theForm = self.document.forms[0];
}

function remove() {
	var confirmationMessage = "<display:get name='prm.project.setup.object.shares.deletetaskconfirmation.message' />";
    if ((theForm.selected) && (verifySelection(theForm, "multiple", '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))) {
        Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', confirmationMessage , function(btn){
              if(btn == 'yes') {
                  theForm.theAction.value = "removeShare";
 				  theForm.submit();
              }
         });
    }
}

function cancel() {
   self.document.location = JSPRootURL + '/<%=referer%>' + <%=referer.indexOf("?") >= 0 ? "'&'" : "'?'"%> + 'module=<%=net.project.base.Module.PROJECT_SPACE%>';
}
function reset() {
    theForm.reset();
}

function helpMe() {
  	var helplocation = JSPRootURL + "/help/HelpDesk.jsp?page=schedule_sharing";
 	openwin_help(helplocation);
}

</script>

</head>

<body class="main" onLoad="setup()" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="all.global.toolbar.standard.share">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=net.project.base.property.PropertyProvider.get("prm.project.setup.object.shares")%>'
					jspPage='<%=SessionManager.getJSPRootURL() + "/crossspace/ShowShares.jsp" %>'
					queryString='<%="module="+Module.REPORT%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
        <tb:button type="remove" label='<%=PropertyProvider.get("prm.project.setup.object.shares.delete.tootip")%>'/>
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/CrossSpaceController/RemoveShare">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
<input type="hidden" name="action" value="<%=Action.DELETE%>">
<input type="hidden" name="referer" value="<%=referer%>">

<table border="0" cellpadding="0" cellspacing="0" width="97%">
<tr class="channelHeader">
    <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
    <td nowrap="true" class="channelHeader" colspan="4"><%=PropertyProvider.get("prm.crossspace.showshares.channeltitle", space.getName())%></td>
    <td align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
</tr>
<tr>
    <td></td>
    <td>
        <table width="100%" cellpadding="0" cellspacing="0">
            <tr>
                <td width="1%">&nbsp;</td>
                <td class="tableHeader"><display:get name="prm.crossspace.showshares.columns.sharedobject"/></td>
                <td class="tableHeader"><display:get name="prm.crossspace.showshares.columns.numberofshares"/></td>
                <td class="tableHeader"><display:get name="prm.crossspace.showshares.columns.sharesecurity"/></td>
                <td class="tableHeader"><display:get name="prm.crossspace.showshares.columns.sharedby"/></td>
            </tr>
            <tr><td class="headerSep" colspan="5"></td></tr>
            <%
                XMLFormatter format = new XMLFormatter();
                format.setStylesheet("/crossspace/xsl/sharelist.xsl");
                out.println(format.getPresentation(xmlProvider.getXML()));
            %>
        </table>
    </td>
    <td></td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
            <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
