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
|   $Revision: 19699 $
|   $Date: 2009-08-11 11:08:24 -0300 (mar, 11 ago 2009) $
|   $Author: dpatil $
|
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Document List" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*,
    		net.project.security.*,
    		net.project.space.Space,
			net.project.base.property.PropertyProvider,
    		net.project.base.Module"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="containerPath" class="net.project.document.PathBean" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="historyBean" class="net.project.history.HistoryBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
     int module = securityProvider.getCheckedModuleID();
     int action = securityProvider.getCheckedActionID();
     String id = securityProvider.getCheckedObjectID();

    Container containerBean = null;

    containerBean = (Container) docManager.getCurrentObject();
    pageContext.setAttribute("container", containerBean, PageContext.PAGE_SCOPE);

    if ((module != docManager.getModuleFromContainerID(containerBean.getContainerID())) || (action != net.project.security.Action.VIEW) || (!id.equals(containerBean.getID())))
          throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.document.containerproperties.authorizationfailed.message"));

    docManager.setCancelPage((String)docManager.getNavigator().get("TopContainer"));

%>

<%

    // set the path object of the container
    containerPath.setRootContainerID(docManager.getRootContainerID());
    containerPath.setObject(containerBean);
    containerPath.load();

%>
<jsp:useBean id="container" type="net.project.document.Container" scope="page" />

<template:getDoctype />
<html>
<head><title><display:get name="prm.global.application.title" /></title>

<%
 String mySpace=null;
 mySpace=user.getCurrentSpace().getType();

 	// configure the history bean
 	historyBean.setModuleActive(true);
 	historyBean.setPageDisplayName(PropertyProvider.get("prm.document.containerproperties.page.history"));
 	historyBean.setPageJspName("");
	historyBean.setPageActive(false);
%>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>


<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />
     
<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/standard_prototypes.js" />

<template:import type="javascript" src="/src/document/child-actions.js" />

	<%-- THIS IS THE DOCUMENT WHICH CONTAINS THE script FOR THIS PAGE --%>

	<script language="javascript1.2">
		var theForm;
		var page = false;
		var errorMsg;
		var isLoaded;
        var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";
        //Internationalization variables for pop up messages
        var confirmDocumentDeletionMes = "<%=PropertyProvider.get("prm.global.javascript.document.confirmdeletion.message")%>";

	function setup() {
        isLoaded = true;
        theForm = self.document.forms[0];

    }

function cancel() {

	resetFocus();

	theAction("reset");
	theForm.submit();
}
function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=document_main&section=container";
	openwin_help(helplocation);
}

    </script>

</head>

<body class="main" onLoad="setup()" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="prm.global.tool.document.name">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display='<%=PropertyProvider.get("prm.document.containerproperties.page.history")%>'
						  jspPage='<%=SessionManager.getJSPRootURL() + "/document/Main.jsp"%>'
  						  queryString='<%="module=" + net.project.base.Module.DOCUMENT%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="document" showAll="true" />
	<tb:band name="standard" showAll="true">
		<tb:button type="create" label='<%=PropertyProvider.get("prm.document.containerproperties.create.button.tooltip")%>' />
		<tb:button type="modify" label='<%=PropertyProvider.get("prm.document.containerproperties.modify.button.tooltip")%>' />
		<tb:button type="remove" />
		<%if (!mySpace.equals(net.project.space.Space.PERSONAL_SPACE)) {%>
			<tb:button type="security" />
		<%}%>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker" name="">
      <input type="hidden" name="theAction">
      <input type="hidden" name="versionID">
      <input type="hidden" name="module" value="<%= module %>">
  <table border="0" cellspacing="0" width="100%" cellpadding="0">
  <tr class="channelHeader">
	  <td width="1%" class="channelHeader"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
	  <th class="channelHeader"  colspan="4" nowrap>&nbsp;</th>
	  <td width="1%"  class="channelHeader" align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
</tr>


          <tr>
             <td>&nbsp;</td>
            <td width="140" class="tableHeader"><display:get name="prm.document.containerproperties.foldername.label" /></td>
            <th nowrap align="right" width="5">&nbsp;</th>
            <td align="left" width="500" class="tableContent"> <jsp:getProperty name="container" property="name" /> 
              &nbsp; </td>
            <td align="left">&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
          <tr> 
          <td>&nbsp;</td>
            <th width="140" class="tableHeader"><display:get name="prm.document.containerproperties.description.label" /></th>
            <th nowrap align="right" width="5">&nbsp;</th>
            <td align="left" width="500" class="tableContent"><%=net.project.util.HTMLUtils.escape(container.getDescription()) %>
              &nbsp;</td>
            <td align="left">&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
          <tr> 
          <td>&nbsp;</td>
            <th width="140" class="tableHeader"><display:get name="prm.document.containerproperties.path.label" /></th>
            <th nowrap align="right" width="5">&nbsp;</th>

            <jsp:setProperty name="containerPath" property="stylesheet" value="/document/xsl/path-to-container-object.xsl" /> 
            <td align="left" width="500" class="tableContent"> <jsp:getProperty name="containerPath" property="presentation" /> 
              &nbsp;</td>

            <td align="left">&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
          <td>&nbsp;</td>
            <th width="140" class="tableHeader"><display:get name="prm.document.containerproperties.object.label" /></th>
            <th nowrap align="right" width="5">&nbsp;</th>
            <td align="left" width="500" class="tableContent"><jsp:getProperty name="container" property="numObjects" /> &nbsp;</td>
            <td align="left">&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
          <td>&nbsp;</td>
            <th width="140" class="tableHeader"><display:get name="prm.document.containerproperties.lastmodifiedby.label" /></th>
            <th nowrap align="right" width="5">&nbsp;</th>
            <td align="left" width="500" class="tableContent"><jsp:getProperty name="container" property="modifiedBy" /> &nbsp;</td>
            <td align="left">&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
          <tr> 
          <td>&nbsp;</td>
            <th width="140" class="tableHeader"><display:get name="prm.document.containerproperties.lastmodifieddate.label" /></th>
            <th nowrap align="right" width="5">&nbsp;</th>
            <td align="left" width="500" class="tableContent"><jsp:getProperty name="container" property="dateModified" /> &nbsp;</td>
            <td align="left">&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
        <tr class="actionBar">
        	<td width=1% class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
        	<td class="actionBar" align=right colspan="4"><nobr><!--a href="javascript:cancel();" class="channelNoUnderline"><display:get name="prm.document.containerproperties.cancel.link" />&nbsp;<img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-cancel_off.gif" width=27 height=27 alt="" border=0 align="absmiddle"></a--></nobr></td>
        	<td width=1% align=right class="actionBar"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
	</tr>
</table>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

