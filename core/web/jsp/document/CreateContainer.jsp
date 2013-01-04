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
|   $RCSfile$
|   $Revision: 19856 $
|   $Date: 2009-08-25 10:06:50 -0300 (mar, 25 ago 2009) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>

<%@ page
    contentType="text/html; charset=UTF-8"
    info="Create New Folder"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.document.*,
            net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
            net.project.security.User"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="container" class="net.project.document.ContainerBean" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%
	  String id = docManager.getCurrentContainerID();
	  int module = docManager.getModuleFromContainerID(id);
	  int action = net.project.security.Action.CREATE;
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess
                objectID="<%=id%>"
				action="create"
				module="<%=module%>"
/>


<html>
<head>
<title><display:get name="prm.document.createcontainer.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/trim.js" />

<template:import type="javascript" src="/src/document_prototypes.js" />
<template:getSpaceJS />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

    function setup() {
        theForm = self.document.forms["NEWFOLDER"];
        focusFirstField(theForm);
    	isLoaded = true;
    }


 function validate()
 {
 	if(!checkTextbox(document.NEWFOLDER.name,'<display:get name="prm.document.createcontainer.foldernamevalidation.message" />'))return false;
 	return true;
 }

 function submit()
 {
 	if(validate())
 		document.NEWFOLDER.submit();
 }

 function cancel() {
	self.close();
 }
 
 function printDirectoryExistsMessage() {
	var erLabel = '<%= request.getAttribute("error.directoryexists") %>';
	if (erLabel == 'directoryexists') {
		var errorMessage = '<display:get name="prm.document.container.error.uniquename.message" />';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
	}
}
 
</script>
</head>


<body class="main" onLoad="setup();" style="background: none;">

<div class=pageHeader align="left">
  <form name="NEWFOLDER" method="post" action="<%=SessionManager.getJSPRootURL()%>/document/CreateContainerProcessing.jsp" name="new_container">
  <table border="0" cellspacing="0" width="100%" cellpadding="0">
  <input type="hidden" name="module" value="<%=module%>">
  <input type="hidden" name="action" value="<%=action%>">
  <input type="hidden" name="id" value="<%=id%>">
  <input type="hidden" name="historyType" value="<%= request.getParameter("historyType") %>">

  	<tr class="channelHeader">
  	<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
  	<th class="channelHeader"  colspan="4" nowrap><display:get name="prm.document.createcontainer.channel.create.title" /></th>
  	<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
  	</tr>
</table>
    <table border="0" cellspacing="4" align="center" width="100">
      <tr>
        <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.createcontainer.parentcontainer.label" /></td>
        <td><%=docManager.getCurrentContainer().getName()%></td>
      </tr>
      <tr>
        <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.createcontainer.foldername.label" /></td>
        <td>
          <input type="text" name="name" maxlength="80">
          <script language="javascript">printDirectoryExistsMessage();</script>
        </td>
      </tr>
      <tr>
        <td nowrap align="left" class="fieldNonRequired"><display:get name="prm.document.createcontainer.description.label" /></td>
        <td>
          <input type="text" name="description" maxlength="80">
        </td>
      </tr>
      <tr>
        <td nowrap colspan="2" align="left">&nbsp; </td>
      </tr>

    </table>

<tb:toolbar style="action" showLabels="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="submit" label='<%= PropertyProvider.get("prm.document.createcontainer.submit.button.label")%>' />
    </tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

</div>
</body>
</html>

