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
    info="Application Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
    		net.project.security.SessionManager,
            net.project.util.jwhich,
            net.project.xml.XMLFormatter,
            java.util.Iterator,
            java.util.StringTokenizer"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%
    String className = (request.getParameter("className") != null) ? request.getParameter("className") : "";
    String classPath = null;
    String theAction = request.getParameter("theAction");
	boolean lookup = (theAction != null && theAction.equals("lookup") && className != null) ? true : false;

	if (lookup) {
        classPath = net.project.util.jwhich.getClasspathXML (className);
    }
%>


<template:getDoctype />

<%@page import="net.project.base.property.PropertyProvider"%><html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<script language="javascript">

	var theForm;

	function setup() {
		theForm = this.document.jwhich;
	}
	
	function reset() { 
		theForm.reset();   
	}
	
	function lookup() {

		theForm.theAction.value = "lookup";
		theForm.submit();
	}

</script>
</script>

<%-- Import CSS --%>
<template:getSpaceCSS />

</head>

<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.classlookup">
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" name="jwhich" action="JWhich.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="240">

<table width="400" border="0" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
	<td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" width="85%"><%=PropertyProvider.get("prm.project.admin.utililies.search.for.class.label") %></td>
	<td nowrap class="channelHeader">&nbsp;</td>
	<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr valign="top">
	<td class="channelContent">&nbsp;</td>
	<td colspan=2 class="channelContent">
		<table width="100%" border="0">
		<tr>
			<td class="tableHeader" nowrap><%=PropertyProvider.get("prm.project.admin.utililies.class.name.label") %></td>
			<td class="tableHeader" colspan="3"><input type="text" name="className" size="60" value="<%=className%>"></td>
		</tr>
		<tr>
		</tr>
		</table>
	</td>
</tr>
<tr>
    <td colspan="4">
        <table width="100%">
        <td>
            <tb:toolbar style="action" showLabels="true">
                <tb:band name="action" enableAll="true">
                    <tb:button type="submit" label="Lookup" function="javascript:lookup();" />
                    
                </tb:band>
            </tb:toolbar>
        </td>
        </table>
    </td>
</tr>
</table>
<br clear="all" />
<%
    if (lookup) {
        XMLFormatter formatter = new XMLFormatter();
        formatter.setStylesheet("/admin/utilities/xsl/jwhich-matches.xsl");
        out.print(formatter.getPresentation(classPath));
    }
%>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
