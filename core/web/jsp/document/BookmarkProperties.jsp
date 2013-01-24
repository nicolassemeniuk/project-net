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
|   $Revision: 20747 $
|   $Date: 2010-04-23 10:41:09 -0300 (vie, 23 abr 2010) $
|   $Author: uroslates $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Bookmark Properties" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    net.project.link.LinkManagerBean, 
    net.project.security.User,
    net.project.security.SessionManager,
	net.project.base.property.PropertyProvider,
    net.project.base.Module"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="bookmark" class="net.project.document.BookmarkBean" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="display_linkMgr" class="net.project.link.LinkManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" type="net.project.security.SecurityProvider" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations
  ----------------------------------------------------------------------%>

<%
    String mySpace = user.getCurrentSpace().getType();
	String JSPRootURL = SessionManager.getJSPRootURL();

    // When invoked from certain places, docManager does not already
    // know the current object
    String id = securityProvider.getCheckedObjectID();
    if (id != null && id.length() != 0) {
		if (!id.equals (docManager.getCurrentObjectID()))
			docManager.setCurrentObjectID(id);
	}

     bookmark.setID ( docManager.getCurrentObjectID() );
     bookmark.setUser ( docManager.getUser() );
     bookmark.loadProperties();
	
     int module = docManager.getModuleFromContainerID( bookmark.getContainerID() );
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:verifyAccess 
				module="<%=module%>"
				action="view"
				objectID = "<%=docManager.getCurrentObjectID()%>"
/>


<%------------------------------------------------------------------------
  -- Page Setup
  ----------------------------------------------------------------------%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />

<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/standard_prototypes.js" />

<template:import type="javascript" src="/src/document/child-actions.js" />

<script language="javascript">
		var theForm;
		var isLoaded = false;
		var errorMsg;
		var JSPRootURL = getVar("JSPRootURL");
        //Internationalization variables for pop up messages
        var confirmDocumentDeletionMes = "<%=PropertyProvider.get("prm.global.javascript.document.confirmdeletion.message")%>";
        
	function setup() {
		load_menu('<%=user.getCurrentSpace().getID()%>');
        theForm = self.document.forms[0];
        isLoaded = true;
    }
	
	function help() {
		var helplocation=JSPRootURL+"/help/Help.jsp?page=document_bookmark_properites";
		openwin_help(helplocation);
	}


</script>
</head>

<body class="main" id="bodyWithFixedAreasSupport" onLoad="setup()">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.global.tool.document.name">
	<tb:setAttribute name="leftTitle">
		<history:history target="main">
			<history:page display='<%=PropertyProvider.get("prm.document.bookmarkproperties.page.history") %>'
						  jspPage='<%=JSPRootURL + "/document/Main.jsp"%>'
  						  queryString='<%="module=" + net.project.base.Module.DOCUMENT%>'
			/>
		</history:history>
	</tb:setAttribute>
 	<tb:band name="standard" showAll="true">
		<tb:button type="create" label='<%=PropertyProvider.get("prm.document.bookmarkproperties.create.button.tooltip") %>' />
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.document.bookmarkproperties.modify.button.tooltip") %>' />
		<tb:button type="remove" />
		<tb:button type="properties" label='<%=PropertyProvider.get("prm.document.bookmarkproperties.properties.button.tooltip") %>' />
		<%if (!mySpace.equals(net.project.space.Space.PERSONAL_SPACE)) {%>
			<tb:button type="security" />
		<%}%>
	</tb:band>
	<tb:band name="document" showAll="true">
		<tb:button type="move" />
    </tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=JSPRootURL%>/servlet/DocumentActionBroker" name="">
      <input type="hidden" name="theAction">
      <input type="hidden" name="objectID" value="<%=bookmark.getID()%>">
      <input type="hidden" name="module" value="<%=module %>">
  

        <table border="0" width="100%" vspace="0" cellpadding="2" cellspacing="0"> 

	<jsp:setProperty name="bookmark" property="stylesheet" value="/document/xsl/bookmark-properties.xsl" /> 
	<jsp:getProperty name="bookmark" property="presentation" />

	</table>

    </form>

        <br clear=all>  
        <table border=0 cellpadding=0 cellspacing=0 width="97%"><tr>
        <td valign="top" align="left">
        
        <table width="400" border="0" cellspacing="0" cellpadding="0">
        <tr class="channelHeader">
		<td class="channelHeader" width="1%"><img src="<%=JSPRootURL%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td nowrap class="channelHeader" width="85%"><display:get name="prm.document.bookmarkproperties.channel.links.title" /></td>
		<td align=right class="channelHeader" width="1%"><img src="<%=JSPRootURL%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
        </tr>
        <tr valign="top">
        <td class="channelContent">&nbsp;</td>
        <td colspan=2 class="channelContent">         
        <%
        display_linkMgr.setView(display_linkMgr.VIEW_ALL);
        display_linkMgr.setContext(net.project.link.ILinkableObject.GENERAL);
        display_linkMgr.setRootObject((Bookmark) bookmark);
        %>         
        <jsp:include page="/link/displayLinks.jsp" flush="true" />
        </td>
        </tr>
	<tr class="actionBar">
		<td width=1% class="actionBar"><img src="<%=JSPRootURL%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
		<td class="actionBar"  align=right><nobr><a HREF="<%=JSPRootURL%>/document/DocumentAddLink.jsp?module=<%=net.project.base.Module.DOCUMENT%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=bookmark.getID()%>&context=<%=net.project.link.ILinkableObject.GENERAL%>&view=<%=display_linkMgr.VIEW_ALL%>" class="channelNoUnderline"><%=PropertyProvider.get("prm.document.bookmarkproperties.modify.link") %>&nbsp;<img src="<%=JSPRootURL%>/images/icons/actionbar-modify_off.gif" width=27 height=27 alt="" border=0 align="absmiddle"></a></nobr></td>
		<td width=1% align=right class="actionBar"><img src="<%=JSPRootURL%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
        </table>
        </td>
        <td width=100%>&nbsp;</td>
        </tr></table>

<display:if name="prm.global.application.debug.isenabled">
    
    <B>-------- XML (bookmark contents) --------</B><P>
    <%=bookmark.getXML()%>
    
</display:if>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

