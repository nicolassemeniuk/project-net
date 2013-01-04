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
    info="Token Edit" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.Module,
			net.project.security.Action,
			net.project.base.property.PropertyProvider,
			net.project.base.property.PropertyManager,
			net.project.base.property.PropertiesFilter,
			net.project.base.property.BrandGlossary,
			net.project.brand.Brand,
			net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="brandTokenEditFilter" class="net.project.base.property.PropertiesFilter" scope="session" />
<jsp:useBean id="brand" class="net.project.brand.Brand" scope="session" />

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


<%------------------------------------------------------------------------
  -- Variable Declarations and Page Setup
  ----------------------------------------------------------------------%>

<%

	String brandID = request.getParameter("brandID");
	String language = request.getParameter("language");
	String numberOfTokens = request.getParameter("numberOfTokens");

	// If no brand id specified in the request get brand id and language
	// from the current session-scoped brand object
	if (brandID == null || brandID.length() == 0) {
		brandID = brand.getID();
		language = brand.getDefaultLanguage();
		numberOfTokens = "100";
	}

	if (numberOfTokens == null || numberOfTokens.length() == 0) {
		numberOfTokens = "100";
	}
	BrandGlossary glossary = new BrandGlossary ( brandID, language, numberOfTokens );
	glossary.load();

	glossary.setPropertiesFilter ( brandTokenEditFilter );
	glossary.applyFilter (true);

	boolean suppressTokens = net.project.util.Conversion.toBoolean ( request.getParameter("suppressTokens") );

	if ( suppressTokens )
	    brandTokenEditFilter.clear();
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript">
	var theForm;
	var isLoaded = false;
	var form_popup;
	var popupField;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';

    function setup() {
       theForm = self.document.tokenEdit;
       isLoaded = true;
	}

    function submit() {
	   theAction ("editToken");
	   theForm.submit();
    }

    function reloadApplicationCache() {
        theAction ("reloadApplicationCache");
        theForm.submit();
        }

    function create() {
	   theAction ("addToken");
	   theForm.submit();
    }

	function cancel() {
	   self.document.location="<%=SessionManager.getJSPRootURL()%>/configuration/Main.jsp";
	}

    function filter() {
       theAction ("filter");
       theForm.submit();
   }

    function changeLanguage(dropDown) {
	   self.document.location="<%=SessionManager.getJSPRootURL()%>/configuration/brand/BrandTokenEdit.jsp?brandID="
          + <%=brandID%> + "&language=" + dropDown.value;
	}

    function reset() {
	theForm.reset();
    }

    function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=brand_main&section=create";
	openwin_help(helplocation);
    }


    function resizeTextArea(field, inc) {
	eval('document.forms["tokenEdit"].elements[field].rows +=' + inc);
    }

    function popupTextArea(field, label) {
	form_popup = window.open('<%=SessionManager.getJSPRootURL()%>/form/TextAreaPopup.jsp?label='+label, 'form_popup', "height=700,width=500, resizable=yes");

	if (form_popup.isloaded == true) {
	    setFieldRef(field);
	    form_popup.copyFieldData();
	    form_popup.focus();
	}
	else
	    {
	    fieldCopyFailed = true;
	    popupField = field;
	}
    }

    function setFieldRef(field) {
	if (field == null)
		field = popupField;

	eval('form_popup.fieldRef = document.forms["tokenEdit"].elements[field]');
    }

    function tabClick(nextPage) {
	self.document.location = JSPRootURL + nextPage;
    }

</script>
</head>

<body class="main" onload="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<%------------------------------------------------------------------------
  -- Toolbar and History setup
  -----------------------------------------------------------------------%>

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tokens.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:module display="Edit Tokens"
					 jspPage='<%=SessionManager.getJSPRootURL() + "/configuration/brand/BrandTokenEdit.jsp"%>'
					 queryString='<%="module=" + Module.CONFIGURATION_SPACE + "&action=" + Action.MODIFY + "&brandID=" + glossary.getBrandID() + 
								     "&language=" + glossary.getLanguage() + "&suppressTokens=true"%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
        <tb:button type="create" />
	</tb:band>
</tb:toolbar>

<div id='scrollwidecontent'>

<form name="tokenEdit" method="post" action="<%=SessionManager.getJSPRootURL()%>/configuration/brand/BrandTokenEditProcessing.jsp">
   <input type="hidden" name="brandID" value="<%=brandID%>">
   <input type="hidden" name="theAction">

<%--------------------------------------------------------------------------------------------------------------------------------------------
	-- Brand Settings
	----------------------------------------------------------------------------------------------------------------------------------------%>
<table width="400" border="0" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
	<td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><%=PropertyProvider.get("prm.project.configuration.brand.settings.label") %></td>
	<td nowrap class="channelHeader">&nbsp;</td>
	<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr valign="top">
	<td class="channelContent">&nbsp;</td>
	<td colspan=2 class="channelContent">
		<table width="100%" border="0">
		<tr>
			<td class="tableHeader" nowrap><%=PropertyProvider.get("prm.project.configuration.brand.label")%><span class="tableContent"><%=glossary.getBrandName()%></span></td>
			<td class="tableHeader"><%=PropertyProvider.get("prm.project.configuration.language.label")%></td>
			<td class="tableContent">
				  <select name="currentLanguage" onchange="changeLanguage(this);">
                              <%=glossary.getSupportedLanguageOptionList()%>
				  </select>
			</td>
		</tr>
            <tr>
			<td class="tableHeader" colspan="3"><a href="javascript:reloadApplicationCache()"><%=PropertyProvider.get("prm.project.configuration.reload.token.cache.label") %></a></td>
		</tr>
		</table>
</table>

<br clear="all" />

<%--------------------------------------------------------------------------------------------------------------------------------------------
	-- Filter Tokens
	----------------------------------------------------------------------------------------------------------------------------------------%>
<table width="400" border="0" cellspacing="0" cellpadding="0">
<tr class="channelHeader">
	<td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" width="85%"><%=PropertyProvider.get("prm.project.configuration.filter.tokens.label")%></td>
	<td nowrap class="channelHeader">&nbsp;</td>
	<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr valign="top">
	<td class="channelContent">&nbsp;</td>
	<td colspan=2 class="channelContent">
		<table width="100%" border="0">
		<tr>
			<td class="tableHeader" nowrap><%=PropertyProvider.get("prm.project.configuration.token.name.label")%></td>
			<td class="tableHeader" colspan="3"><input type="text" name="filterTokenName" value='<c:out value="${getName}"/>'></td>
		</tr>
		<tr>
			<td class="tableHeader" nowrap><%=PropertyProvider.get("prm.project.configuration.token.value.label")%></td>
			<td class="tableHeader" colspan="3"><input type="text" name="filterTokenValue" value='<c:out value="${getValue}"/>'></td>
		</tr>
		<tr>
			<td class="tableHeader"><%=PropertyProvider.get("prm.project.configuration.property.type.label")%></td>
			<td class="tableHeader" colspan="3">
				<select name="filterPropertyType">
				<option value=""><%=PropertyProvider.get("prm.project.configuration.all.label")%></option>
			      <%=PropertyManager.getPropertyTypeOptionList ( brandTokenEditFilter.getType() )%>
				</select>
			</td>
			<td class="tableHeader"><%=PropertyProvider.get("prm.project.configuration.category.label")%></td>

			   <td class="tableHeader">
			      <select multi name="filterCategories" size="3" >
				<option value=""><%=PropertyProvider.get("prm.project.configuration.all.label")%></option>
				<%=PropertyManager.getCategoryOptionList ( brandTokenEditFilter.getFilterCategories() )%>
				  </select>
				</td>
		</tr>
		<tr>
			<td class="tableHeader" nowrap><%=PropertyProvider.get("prm.project.configuration.show.tokens.label")%></td>
			<td class="tableHeader" colspan="3">
			<select name="numberOfTokens" >
				<option <%=("100".equals(numberOfTokens))?"selected":"" %> value="100"><%=PropertyProvider.get("prm.project.configuration.first.100.tokens.label")%></option>
				<option <%=("200".equals(numberOfTokens))?"selected":"" %> value="200"><%=PropertyProvider.get("prm.project.configuration.first.200.tokens.label")%></option>
				<option <%=("500".equals(numberOfTokens))?"selected":"" %> value="500"><%=PropertyProvider.get("prm.project.configuration.first.500.tokens.label")%></option>
				<option <%=("1000".equals(numberOfTokens))?"selected":"" %> value="1000"><%=PropertyProvider.get("prm.project.configuration.first.1000.tokens.label")%></option>
				<option <%=("-1".equals(numberOfTokens))?"selected":"" %> value="-1"><%=PropertyProvider.get("prm.project.configuration.all.tokens.label")%></option>
			</select>
		</tr>
		</table>
	</td>
</tr>
<tr>
    <td colspan="4">
        <tb:toolbar style="action" showLabels="true" width="100%">
            <tb:band name="action" enableAll="true">
                <tb:button type="submit" label="Filter" function="javascript:filter();" />
                
            </tb:band>
        </tb:toolbar>
    </td>
</tr>
</table>
<br clear="all" />


<%-- if show tokens --%>
<% if ( !suppressTokens ) { %>

	<table border="0" cellspacing="0" cellpadding="0">
	<%
    //Avinash : bfd - 3148  Show Path Error if any
 if(request.getParameter("message")!=null){String message = (String)request.getParameter("message");%>
 		<tr><td colspan="4"><span class="errorMessage"><display:get name="<%= message %>" /></span></td></tr><%}%>
 		
	<tr class="channelHeader">
		<td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td nowrap class="channelHeader" align="left" width="85%"><%=PropertyProvider.get("prm.project.configuration.edit.tokens.label")%></td>
		<td nowrap class="channelHeader">
			<tb:toolbar style="channel" showLabels="true">
				<tb:band name="channel">
					<tb:button type="modify" label="Submit Changes" function='javascript:submit();' />
				</tb:band>
			</tb:toolbar>
		</td>
		<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
	<tr class="channelHeader">
		<td class="channelContent">&nbsp;</td>
		<td colspan="2" align="center">
			 <%glossary.setStylesheet("/configuration/brand/xsl/token-edit.xsl");%>
			 <%=glossary.getPresentation()%>
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<tb:toolbar style="action" showLabels="true">
    			<tb:band name="action" enableAll="true">
        			<tb:button type="submit" label="Submit Changes" function="javascript:submit();" />
    			</tb:band>
			</tb:toolbar>
		</td>
</tr>
</table>
<% } %>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>



