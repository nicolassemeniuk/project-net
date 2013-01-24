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
    info="Modify Document"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.document.Document,
    		net.project.document.DomainListBean,
    		net.project.document.DocumentControlManager,
		    net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
            net.project.util.HTMLUtils"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

 <%
	  int action = net.project.security.Action.MODIFY;
	  int module = docManager.getModuleFromContainerID(docManager.getCurrentContainerID());
	  String id = docManager.getCurrentObjectID();
	  String JSPRootURL = SessionManager.getJSPRootURL();
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
  -- Load document for modification
  ----------------------------------------------------------------------%>
<%
    DocumentControlManager dcm = new DocumentControlManager();
    Document document = new Document();

    document.setID( docManager.getCurrentObjectID() );
    document.load();

    docManager.setCurrentObject(document);

    dcm.setUser (docManager.getUser());
    dcm.verifyUpdateDocument (document);

    docManager.setCancelPage((String)docManager.getNavigator().get("TopContainer"));
%>


<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>
<template:getSpaceCSS />

<template:import type="javascript" src="/src/document/create-modify-actions.js" />

<script language="javascript">
var theForm;
var isLoaded = false;
var errorMsg;
var isModifyPage = true;
var JSPRootURL = getVar("JSPRootURL");

//Internationalizatized popup messages
var nameRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.name.error.message")%>";
var objectSelectionRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.objectselection.error.message")%>";
var fileSelectionRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.fileselection.error.message")%>";
var urlSelectionRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.urlselection.error.message")%>";
var commentErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.comments.error.message")%>";
var descriptionErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.description.error.message")%>";

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    theForm = self.document.forms[0];
    isLoaded = true;
}

function refresh() {
    theForm.refresh();
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=document_modify&section=document_properties";
    openwin_help(helplocation);
}

</script>

</head>

<body onLoad="setup();" class="main" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Toolbar and History setup
  -----------------------------------------------------------------------%>

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.document.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%= PropertyProvider.get("prm.document.modifydocumentproperties.page.history") %>'
						  jspPage='<%=JSPRootURL + "/document/ModifyDocumentProperties.jsp"%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="document" />
	<tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=JSPRootURL%>/document/ModifyDocumentProcessing.jsp" name="frm">

  <input type="hidden" name="theAction">

  <input type="hidden" name="action" value="<%=action%>">
  <input type="hidden" name="module" value="<%=module%>">
  <input type="hidden" name="id" value="<%=id%>">
    <input type="hidden" name="documentType" value="document">

  <table border="0" cellspacing="0" width="100%" cellpadding="0">
  <tr class="channelHeader">
	  <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
	  <th class="channelHeader"  colspan="4" nowrap><display:get name="prm.document.modifydocumentproperties.fieldsrequired.instruction" /></th>
	  <td width="1%"  class="channelHeader" align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
	  </tr>

    <tr>
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired" width="20%"><display:get name="prm.document.modifydocumentproperties.documentname.label" /></td>
      <td nowrap colspan="3" class="tableContent">
        <input type="text" name="name" size="40" maxlength="80" VALUE="<%=HTMLUtils.escape(document.getName()) %>" >
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr>
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.modifydocumentproperties.author.label" /></td>
      <td nowrap colspan="3" class="tableContent">
        <select name="authorID">

		<%= DomainListBean.getRosterOptionList( docManager.getSpace(), document.getAuthorID() ) %>

	</select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td nowrap align="left" colspan="6">&nbsp;</td>
    </tr>
    <tr>
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldNonRequired"><display:get name="prm.document.modifydocumentproperties.description.label" /></td>
      <td nowrap colspan="3">
        <textarea name="description" cols="80" rows="5"><%= (document.getDescription() != null)?document.getDescription():"" %></textarea>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr>
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.modifydocumentproperties.status.label" /></td>
      <td nowrap colspan="3" class="tableContent">
        <select name="statusID" size="1">

		<%= DomainListBean.getDocStatusOptionList( document.getStatusID() ) %>


        </select>
      </td>
      <td>&nbsp;</td>
    </tr>

<%--
	<tr>
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired">Contact:</td>
      <td nowrap colspan="3" class="tableContent">
        <select name="contact" onChange="update_contact(this.options[this.selectedIndex].value);">

		<%= domain.getRosterOptionList( docManager.getSpace(), docManager.getUser().getID() ) %>

        </select>
      </td>
      <td>&nbsp;</td>
    </tr>
--%>
    <tr>
      <th nowrap align="left">&nbsp;</th>
      <td nowrap colspan="5">&nbsp; </td>
    </tr>
    <tr>
     <th nowrap align="left">&nbsp;</th>
      <td valign="top" nowrap align="left" colspan="5" class="fieldNonRequired"><display:get name="prm.document.modifydocumentproperties.comments.label" /><br>
        <textarea rows="2" name="notes" cols="50" wrap="hard"><%= (document.getNotes() != null)?document.getNotes():"" %></textarea>
      </td>
    </tr>

  </table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="submit" />
    </tb:band>
</tb:toolbar>
</form>

</div>

<template:getSpaceJS />
</body>
</html>

