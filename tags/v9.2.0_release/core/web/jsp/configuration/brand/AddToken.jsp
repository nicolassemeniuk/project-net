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
    info="Token Edit" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.brand.BrandManager,
            net.project.base.property.PropertyManager,
	        net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<jsp:useBean id="addBrand" class="net.project.brand.BrandManager" scope="session"/> 

 <%

	String jspRootUrl = net.project.security.SessionManager.getJSPRootURL();
	String myRootUrl = jspRootUrl;

	   addBrand.setID ( request.getParameter("contextID") );
	   addBrand.setRequestedLanguage ( request.getParameter("language") );
	   addBrand.load();

String tokenName = request.getParameter("tokenName");

if (tokenName == null)
	tokenName = "";

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


<template:getDoctype />

<%@page import="net.project.base.property.PropertyProvider"%><html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Setup the space stylesheets and js files --%>
<template:getSpaceCSS />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkRadio.js" />


<script language="javascript">
		var theForm;
		var isLoaded = false;

    function setup() {
       theForm = self.document.addToken;
       isLoaded = true;
	   setVar("referrerPage", self.document.referrer);
	}

function validateForm(frm) {

	if (!checkTextbox(frm.tokenName,"Token Name is a required field")) return false;
	//if (!checkTextbox(frm.defaultBrandValue,"A default English value is a required field")) return false;
    if (!checkRadio(theForm.isTranslatableProperty, "Is Translatable is a required field")) return false;

	return true;
}

	function finish() {
	   theAction ("finish");

	   if (validateForm(theForm)) {
          theForm.submit();
	   }

    }

    function addAnother() {
	   theAction ("addAnotherToken");
	   if (validateForm(theForm)) {
          theForm.submit();
	   }
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

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tokens.title">
	<tb:setAttribute name="leftTitle">Add Token to: <%=addBrand.getName()%>(<%=request.getParameter("language")%>)</tb:setAttribute>
	<tb:band name="standard" >
        <tb:button type="create" />
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="addToken" method="post" action="<%=myRootUrl%>/configuration/brand/AddTokenProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="goBack" value="<%=request.getParameter("goBack")%>">
	 <table width="400" border="0" cellspacing="0" cellpadding="0">
			<tr class="channelHeader">
	    	    <td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		        <td nowrap class="channelHeader" width="85%">
		        	<%=PropertyProvider.get("prm.project.configuration.add.token.label") %>
		        </td>
				<td nowrap class="channelHeader">&nbsp;</td>
		       	<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	        </tr>
	        <tr valign="top">
		        <td class="channelContent">&nbsp;</td>
		        <td colspan=2 class="channelContent">
					<table width="100%" border="0">
					  <tr>
						<td class="fieldRequired" nowrap>
							<%=PropertyProvider.get("prm.project.configuration.token.name.label") %>
						</td>
						<td colspan="3"> 
						  <input type="text" name="tokenName" value="<%=tokenName%>" size="40" maxlength="120">
						</td>
					  </tr>
					  <tr> 
						<td class="fieldRequired" nowrap>
							<%=PropertyProvider.get("prm.project.configuration.default.en.value.label") %>
						</td>
						<td colspan="3"> 
						  <input type="text" name="defaultBrandValue" size="40" maxlength="4000">
						</td>
					  </tr>
					  <tr> 
						<td class="fieldRequired" nowrap>
						<%=PropertyProvider.get("prm.project.configuration.property.type.label") %>
						</td>
						<td colspan="3"> 
                      	     <select name="propertyType">
                                <%=PropertyManager.getPropertyTypeOptionList ()%>
						  </select>

						</td>
					  </tr>
					  <tr> 
						<td class="fieldNonRequired" nowrap><%=addBrand.getName()%> (<%=addBrand.getActiveLanguage()%>) 
						<%=PropertyProvider.get("prm.project.configuration.value.label") %>
						</td>
						<td colspan="3">
						  <input type="text" name="currentBrandValue" size="40" maxlength="4000">
						</td>
					  </tr>
                      <tr>
						<td class="fieldRequired" nowrap>
							<%=PropertyProvider.get("prm.project.configuration.is.tranlatable.label") %>
						</td>
						<td colspan="3" class="tableContent">
						  <%=PropertyProvider.get("prm.project.configuration.true.label") %>
						  <input type="radio" name="isTranslatableProperty" value="1">
                          <%=PropertyProvider.get("prm.project.configuration.false.label") %>
                          <input type="radio" name="isTranslatableProperty" value="0">
						</td>
					  </tr>
					</table>
			 </tr>
		  </table>
		  
<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="add" label="Add Another" function="javascript:addAnother();" />
		<tb:button type="finish" label="Finish" function="javascript:finish();" />
    </tb:band>
</tb:toolbar>

</form>

</div>

<template:getSpaceJS />
</body>
</html>

