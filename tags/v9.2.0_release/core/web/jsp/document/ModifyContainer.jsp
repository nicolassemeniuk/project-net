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
|   $Revision: 18888 $
|   $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|   $Author: avinash $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Create New Folder" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
    		net.project.document.Container,
    		net.project.document.DocumentControlManager,
    		net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

 <%
	  int action = net.project.security.Action.MODIFY;
	  int module = docManager.getModuleFromContainerID(docManager.getCurrentContainerID());
	  String id = docManager.getCurrentObjectID();
	  String JSPRootUrl = SessionManager.getJSPRootURL();
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess 
				module="<%=module%>"
				action="modify"
				objectID = "<%=id%>"
/>


<%------------------------------------------------------------------------
  -- Verify Container Update
  ----------------------------------------------------------------------%>
  
<%
    DocumentControlManager dcm = new DocumentControlManager();
    Container container = (Container) docManager.getCurrentObject();

    dcm.setUser (docManager.getUser());
    dcm.verifyUpdateContainer (container);
%>

<script language="javascript">
var isError = '1';
if (isError == '<%= request.getAttribute("error.unique") %>') {
	var errorMessage = '<display:get name="prm.document.modifycontainer.uniqueerror" />';
	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
}
</script>
 
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<template:import type="javascript" src="/src/document/child-actions.js" />
<template:import type="javascript" src="/src/document_prototypes.js" />

<script language="javascript">
		var theForm;
		var isLoaded = false;
		var errorMsg;
        var isModifyPage = true;
        var JSPRootURL = getVar("JSPRootURL");
		var name = "<%=container.getName()%>";
		var description = "<%=net.project.util.HTMLUtils.escape(container.getDescription())%>";

		function setup() {
			theForm = self.document.forms[0];
				isLoaded = true;
		
			}
		 function validate()
		 {
			if(!checkTextbox(theForm.name,'<display:get name="prm.document.modifycontainer.foldernamevalidation.message" />'))return false;
			if(theForm.description.value.length > 500 ){
				extAlert(errorTitle, '<display:get name="prm.document.modifycontainer.folderdescriptionvalidation.message" />' , Ext.MessageBox.ERROR);
				return false;
			}
			return true;
		 }
			
		function submitForm()
		{
			if(validate())
			theForm.submit();
		}
		
		function cancel() {
		   self.document.location= JSPRootURL + "/document/Main.jsp?module=<%= module %>"
		}

		function help() {
			var helplocation = JSPRootURL + "/help/Help.jsp?page=document_modify&section=container_properties";
			openwin_help(helplocation);
		}
		
		function reset() {
			theForm.name.value = name;
			theForm.description.value = description;
		}
		
</script>


</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Toolbar and History setup
  -----------------------------------------------------------------------%>

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.document.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.document.modifycontainer.page.history") %>'
						  jspPage='<%=JSPRootUrl + "/document/ModifyContainer.jsp" %>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="document" />
	<tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<div class=pageHeader align="left">

  <form method="post" action="<%=JSPRootUrl%>/document/ModifyContainerProcessing.jsp" name="new_container">
    <input type="hidden" name="module" value="<%=module%>">
    <input type="hidden" name="action" value="<%=action%>">
    <input type="hidden" name="theAction">
    <input type="hidden" name="id" value="<%=id%>">    
    <table border="0" cellspacing="0" cellpadding="0" align="left" width="600">
  	<tr class="channelHeader">
  	<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
  	<th class="channelHeader"  colspan="2" nowrap><display:get name="prm.document.modifycontainer.channel.modifyfolder.title" /></th>
  	<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
  	</tr>
  	<tr><td colspan="4">&nbsp;</td></tr>
      <tr>
      	<td>&nbsp;</td>
        <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.modifycontainer.foldername.label" />&nbsp;</td>
        <td> 
          <input type="text" name="name" VALUE="<%=container.getName()%>" maxlength="80">
        </td>
        <td>&nbsp;</td>
      </tr>
      <tr>
      <td>&nbsp;</td>
        <td nowrap align="left" class="fieldNonRequired"><display:get name="prm.document.modifycontainer.description.label" />&nbsp;</td>
        <td>
          <input type="text" name="description" value="<%=net.project.util.HTMLUtils.escape(container.getDescription())%>">
        </td>
        <td>&nbsp;</td>
      </tr>
  </table>
</div>
  
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="submit" function="javascript:submitForm();"/>
    </tb:band>
</tb:toolbar>

</form>

</div>

<template:getSpaceJS />
</body>
</html>

