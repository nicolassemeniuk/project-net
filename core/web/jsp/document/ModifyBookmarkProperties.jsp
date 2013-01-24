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
|   $Revision: 20066 $
|   $Date: 2009-10-06 14:30:22 -0300 (mar, 06 oct 2009) $
|   $Author: dpatil $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Modify Document" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.Bookmark,
    		net.project.document.DomainListBean,
    		net.project.document.DocumentControlManager,
		    net.project.security.SessionManager,
			net.project.base.property.PropertyProvider"
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
  -- Load bookmark for modification
  ----------------------------------------------------------------------%>
<%
      DocumentControlManager dcm = new DocumentControlManager();

	Bookmark bookmark = new Bookmark();

    bookmark.setID( docManager.getCurrentObjectID() );
    bookmark.load();

    docManager.setCurrentObject (bookmark);

    dcm.setUser (docManager.getUser());
    dcm.verifyUpdateBookmark (bookmark);

    docManager.setCancelPage((String)docManager.getNavigator().get("TopContainer"));
%>


<%@page import="net.project.util.HTMLUtils"%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>
<template:getSpaceCSS />



<template:import type="javascript" src="/src/document/create-modify-actions.js" />
<template:import type="javascript" src="/src/checkUrl.js" />

	<script language="javascript">
		var theForm;
		var isLoaded = false;
		var errorMsg;
        var isModifyPage = true;
        var JSPRootURL = getVar("JSPRootURL");
        //Internationalization variables for pop up messages
        var nameRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.name.error.message")%>";
        var objectSelectionRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.objectselection.error.message")%>";
        var fileSelectionRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.fileselection.error.message")%>";
        var commentErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.comments.error.message")%>";
		var urlSelectionRequiredErrMes = "<%=PropertyProvider.get("prm.global.javascript.document.verifyform.urlselection.error.message")%>";

		var urlSpaceAreNotAllowedErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.spacearenotallowed")%>';
        var urlIncorrectErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.incorrect")%>';
        var urlInvalidCharactersErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.invalidcharacters")%>';
        var urlDomainNameInvalidCharactersErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.domain.name.invalidcharacters")%>';
        var urlDomainNameInvalidErrMes = '<%=PropertyProvider.get("prm.document.importobject.url.domain.name.invalid")%>';
		
	function setup() {
		load_menu('<%=user.getCurrentSpace().getID()%>');
		theForm = self.document.forms[0];
        isLoaded = true;

	}
	
	function help() {
		var helplocation=JSPRootURL+"/help/Help.jsp?page=document_modify&section=bookmark_properties";
		openwin_help(helplocation);
	}
	
	function refresh(){
		theForm.refresh();
	}

	function submit () {
		theAction("submit");
		if (isURL2(theForm.url.value, urlSpaceAreNotAllowedErrMes, urlIncorrectErrMes, urlInvalidCharactersErrMes, urlDomainNameInvalidCharactersErrMes, urlDomainNameInvalidErrMes)) {
			theForm.submit();
		}else {
			extAlert(errorTitle, '<%=PropertyProvider.get("prm.document.importobject.url.validation")%>', Ext.MessageBox.ERROR);
		}
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
			<history:page display='<%= PropertyProvider.get("prm.document.modifybookmarkproperties.page.history") %>'
						  jspPage='<%=JSPRootURL + "/document/ModifyBookmarkProperties.jsp"%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="document" />
	<tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=JSPRootURL%>/document/ModifyBookmarkProcessing.jsp" name="document">
 
  <input type="hidden" name="theAction">
  
  <input type="hidden" name="action" value="<%=action%>">
  <input type="hidden" name="module" value="<%=module%>">
  <input type="hidden" name="id" value="<%=id%>">
  <input type="hidden" name="documentType" value="bookmark">

  <table border="0" cellspacing="0" width="100%" cellpadding="0">
  <tr class="channelHeader">
	  <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
	  <th class="channelHeader"  colspan="4" nowrap><display:get name="prm.document.modifybookmarkproperties.fieldsrequired.instruction" /></th>
	  <td width="1%"  class="channelHeader" align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
	  </tr>

    <tr>
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired" width="20%"><display:get name="prm.document.modifybookmarkproperties.bookmarkname.label" /></td>
      <td nowrap colspan="3" class="tableContent"> 
        <input type="text" name="name" size="40" maxlength="80" VALUE="<%=HTMLUtils.escape(bookmark.getName()) %>" >
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr> 
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldNonRequired"><display:get name="prm.document.modifybookmarkproperties.description.label" /></td>
      <td nowrap colspan="3"> 
        <input type="text" name="description" size="40" maxlength="80" VALUE="<%= net.project.util.HTMLUtils.escape(bookmark.getDescription()) %>" >
      </td>
      <td>&nbsp;</td>
    </tr>

  <tr> 
    <td>&nbsp;</td>
    <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.modifybookmarkproperties.url.label" /></td>
    <td nowrap colspan="3">
      <input type="text" name="url" size="60" maxlength="240" VALUE="<%= bookmark.getURL() %>" >
    </td>
    <td>&nbsp;</td>
  </tr>

    <tr>
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.modifybookmarkproperties.owner.label" /></td>
      <td nowrap colspan="3" class="tableContent"> 
        <select name="authorID">

		<%= DomainListBean.getRosterOptionList( docManager.getSpace(), bookmark.getOwnerID() ) %>

	</select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td nowrap align="left" colspan="6">&nbsp;</td>
    </tr>
    <tr> 
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired"><display:get name="prm.document.modifybookmarkproperties.status.label" /></td>
      <td nowrap colspan="3" class="tableContent"> 
        <select name="statusID" size="1">

		<%= DomainListBean.getDocStatusOptionList( bookmark.getStatusID() ) %>

         
        </select>
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr> 
      <th nowrap align="left">&nbsp;</th>
      <td nowrap colspan="5">&nbsp; </td>
    </tr>
    <tr> 
     <th nowrap align="left">&nbsp;</th>
      <td valign="top" nowrap align="left" colspan="5" class="fieldNonRequired"><display:get name="prm.document.modifybookmarkproperties.comments.label" /><br>
        <textarea rows="2" name="notes" cols="50" wrap="hard"><%= net.project.util.HTMLUtils.escape(bookmark.getNotes()) %></textarea>
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

