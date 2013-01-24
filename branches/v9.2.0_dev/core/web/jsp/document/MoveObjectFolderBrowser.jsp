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
    info="Document List"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.security.User,
	net.project.base.property.PropertyProvider,
	net.project.security.SessionManager"

%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="container" class="net.project.document.ContainerBean" />
<jsp:useBean id="containerTreePath" class="net.project.document.PathBean" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
	int action = net.project.security.Action.VIEW;
	int module = docManager.getModuleFromContainerID(docManager.getCurrentContainerID());
	String id = docManager.getCurrentContainerID();
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess
				module="<%=module%>"
				action="view"
				objectID = "<%=id%>"
/>

<%------------------------------------------------------------------------
  -- more page setup
  ----------------------------------------------------------------------%>

<%
	container.setID( docManager.getCurrentContainerID() );
	container.setUser( docManager.getUser() );
    // Exclude object being moved from the container
    // This will prevent us from displaying a folder that is being moved
    // which in turn prevents the ability to try and move a folder into itself
    container.setExcludeObject(docManager.getCurrentObject());
    container.load();

	// set the path object of the container
    containerTreePath.setRootContainerID(docManager.getRootContainerID());
    containerTreePath.setObject(container);
    containerTreePath.load();

%>


<html>
<head>
<title><display:get name="prm.document.moveobjectfolderbrowser.title" /></title>



<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />

<script language="javascript">

	function cancel() {
	    parent.window.close();
    }

</script>


</head>

<body class="main">

<form name="container" method="post" action="<%=SessionManager.getJSPRootURL()%>/document/MoveObjectProcessing.jsp">

	<input type="hidden" name="theAction">
	<input type="hidden" name="containerID" value="<%= container.getID() %>" >
	<input type="hidden" name="objectID" value="<%= docManager.getCurrentObjectID() %>" >

	<input type="hidden" name="module" value="<%=module%>">
	<input type="hidden" name="action" value="<%=action%>">

<table border="0" width="100%"  cellpadding="0" cellspacing="0">
    <tr>
        <td colspan="4">
            <%-- Show current folder --%>
            <table>
                <tr>
                    <td align="left" class="tableHeader">
                        <display:get name="prm.document.moveobjectfolderbrowser.moveto.label" />
                    </td>
                    <td align="left" class="tableContent">
                        <jsp:setProperty name="containerTreePath" property="stylesheet" value="/document/xsl/container-move-object-path.xsl" />
                        <jsp:getProperty name="containerTreePath" property="presentation" />
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr align="left">
        <td colspan="2">&nbsp;</td>
        <td align="left" class="tableHeader">
            <display:get name="prm.document.moveobjectfolderbrowser.name.label" />
        </td>
        <td align="left" class="tableHeader">
            <display:get name="prm.document.moveobjectfolderbrowser.lastmodified.label" />
        </td>
    </tr>
    <tr class="tableLine">
        <td  colspan="4" class="tableLine"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>

              <jsp:setProperty name="container" property="stylesheet" value="/document/xsl/container-folders-only.xsl" /> 
              <jsp:getProperty name="container" property="presentation" /> 
</table>

<tb:toolbar style="action" showLabels="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="jump" label='<%= PropertyProvider.get("prm.document.moveobjectfolderbrowser.jump.button.label")%>' function="javascript:document.container.submit();" />
        <tb:button type="cancel" function="javascript:cancel();" />
    </tb:band>
</tb:toolbar>

</form>
<template:getSpaceJS />
</body>
</html>

