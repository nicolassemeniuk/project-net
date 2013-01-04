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
| Person's portoflio of configuration spaces.
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.xml.XMLFormatter,
			net.project.base.Module,
			net.project.security.SecurityProvider, 
			net.project.security.User,
			net.project.security.Action,
			net.project.space.Space,
			net.project.configuration.ConfigurationPortfolio,
			net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="ConfigurationPortfolio" class="net.project.configuration.ConfigurationPortfolio" scope="request" />

<%
	boolean doReload = true;
	XMLFormatter xmlFormatter = new XMLFormatter();

	ConfigurationPortfolio.setID(user.getMembershipPortfolioID());
	ConfigurationPortfolio.load();

	String refLink = "/portfolio/ConfigurationPortfolio.jsp";
	String refLinkEncoded = java.net.URLEncoder.encode(refLink);
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%
// no security check necessary since this is the users portfolio
%>


<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
	
function setup() {
	load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.forms["main"];
	isLoaded = true;
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=configuration_portfolio";
	openwin_help(helplocation);
}

function cancel() { self.document.location = JSPRootURL + "/personal/Main.jsp"; }
function reset() { self.document.location = JSPRootURL + "/portfolio/ConfigurationPortfolio.jsp"; }

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
    	document.location = "<%=SessionManager.getJSPRootURL()%>/configuration/Main.jsp?module=<%=Module.CONFIGURATION_SPACE%>&action=<%=Action.VIEW%>&id=" +
        		getSelected(theForm.configuration_id);
    }
}

function remove() {
	if (isSelected(theForm.configuration_id)) {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.configuration.mainpage.confirm")%>', function(btn) { 
			if(btn == 'yes'){ 
				document.location = "<%=SessionManager.getJSPRootURL()%>/configuration/ConfigurationRemoveProcessing.jsp?module=<%=Module.CONFIGURATION_SPACE%>&action=<%=Action.DELETE%>&refLink=<%=refLinkEncoded%>";
			}else{
			 	return false;
			}
		});
	}
}

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
</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" leftTitle='<%=PropertyProvider.get("prm.personal.configuration.pagetitle")%>'   groupTitle="prm.personal.configuration.pagetitle">
	<tb:band name="standard">
		<tb:button type="remove" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="main" method="post">
    <input type="hidden" name="module" value="">
	<input type="hidden" name="theAction" value="">

<%-- Portfolio Table --%>
<table  border="0" cellpadding="0" cellspacing="0"  width="97%">
<tbody>
<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	<td valign="middle" align="left" class="channelHeader"><%=PropertyProvider.get("prm.personal.configuration.channel.youaremember.title")%></td>
	<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td colspan="2">
		<%-- Apply stylesheet to format Configuration portfolio --%>
		<%xmlFormatter.setStylesheet("/portfolio/xsl/configuration-portfolio.xsl");%>
		<%=xmlFormatter.getPresentation(ConfigurationPortfolio.getXML())%>
	</td>
</tr>
</tbody>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
