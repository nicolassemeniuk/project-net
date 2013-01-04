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
    info="Create New Brand" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.brand.*,
	net.project.base.property.PropertyManager,
	net.project.security.User,
	net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="brandManager" class="net.project.brand.BrandManager" scope="session" />


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

 <%
      // setup stuff here
%>


<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<%--
<security:verifyAccess 
				module="<%=module%>"
				action="create"
				objectID = "<%=id%>"
/>
--%>
<%
	String jspRootUrl = net.project.security.SessionManager.getJSPRootURL();
	String myRootUrl = jspRootUrl;
%>

<template:getDoctype />

<%@page import="net.project.base.property.PropertyProvider"%><html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />
     
<script language="javascript">
		var theForm;
		var isLoaded = false;

    function setup() {
       theForm = self.document.forms[0];
       isLoaded = true;
	}
	
    function submit() {
	   theForm.submit();
    }

    function reset() {
	   theForm.reset();
    }

	function help() {
       var helplocation="<%=jspRootUrl%>/help/Help.jsp?page=brand_main&section=create";
       openwin_help(helplocation);
    }


</script>
</head>

<body onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
	
<%------------------------------------------------------------------------
  -- Toolbar and History setup
  -----------------------------------------------------------------------%>

<%--
<tb:toolbar style="tooltitle" showAll="true" groupTitle="Tokens">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="Import Document"
			              level="1"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/document/CreateDocument.jsp"%>'
			/>
		</history:history>
	</tb:setAttribute>
	<tb:band name="document" />
	<tb:band name="standard" >
    </tb:band>
</tb:toolbar>
--%>

<div id='content'>

<form method="post" action="<%=myRootUrl%>/configuration/brand/CreateBrandProcessing.jsp">
 
  <input type="hidden" name="theAction">

  <table border="0" cellspacing="0" cellpadding="0" width="97%">
  <tr class="channelHeader">
	  <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
	  <th class="channelHeader" colspan="4" nowrap><%=PropertyProvider.get("prm.project.admin.profile.fileds.required.label") %></th>
	  <td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
	</tr>
	<tr> 
    <td colspan="6">&nbsp;</td>
    </tr>
    <tr> 
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired"><%=PropertyProvider.get("prm.project.configuration.brand.name.label") %></td>
      <td nowrap colspan="3"> 
        <input type="text" name="name" size="40" maxlength="80">
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired"><%=PropertyProvider.get("prm.project.configuration.brand.abbreviation.label") %></td>
      <td nowrap colspan="3"> 
        <input type="text" name="abbreviation" size="40" maxlength="80">
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired"><%=PropertyProvider.get("prm.project.configuration.brand.description.label") %></td>
      <td nowrap colspan="3"> 
        <input type="text" name="description" size="40" maxlength="250">
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.default.languages.label") %></td>
      <td nowrap colspan="3"> 
        <select name="defaultLanguage">
		<%=PropertyManager.getLanguageOptionList()%>
         </select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.configuration.supported.languages.label") %></td>
      <td nowrap colspan="3"> 
        <select name="supportedLanguages" multiple height="3">
		<%=PropertyManager.getLanguageOptionList()%>
	    </select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
    <td>&nbsp;</td>
      <td nowrap align="left" class="fieldRequired"><%=PropertyProvider.get("prm.project.configuration.supported.hostnames.label") %></td>
      <td nowrap colspan="3"> 
        <input type="text" name="supportedHostnames" size="40" maxlength="250">
      </td>
      <td>&nbsp;</td>
    </tr>
  </table>

</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="submit" />
    </tb:band>
</tb:toolbar>

</div>
  

<template:getSpaceJS />
</body>
</head>

