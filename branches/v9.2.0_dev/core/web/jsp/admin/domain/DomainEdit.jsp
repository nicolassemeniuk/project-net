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
    info="Create New Document" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.Action,
            net.project.base.property.PropertyProvider,
        	net.project.security.SessionManager"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" scope="session" /> 

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    // Load the domain (if editing) and determine page title
    String pageTitle = null;
	String domainID = request.getParameter("domainID");
	
    if (domainID == null || domainID.length() == 0) {
        pageTitle = PropertyProvider.get("prm.domains.tab.createdomain.label");
        domain.clear();
        
    } else {
        pageTitle = PropertyProvider.get("prm.domains.tab.editdomain.label");
		domain.clear();
        domain.setID(domainID);
		domain.load();
	}

    // Get a loaded collection of directory provider types
    // These are displayed in a selection list
    net.project.base.directory.DirectoryProviderTypeCollection directoryProviderTypeCollection = net.project.base.directory.DirectoryProviderTypeCollection.getInstance();
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title> 


<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<template:getSpaceCSS />
     
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">

       var theForm;
       var isLoaded = false;
       var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

    function setup() {
	    theForm = self.document.forms["main"];
        isLoaded = true;
    }

    function submit() {
        if (validate()) {
    	    theAction ("submit");
    	    theForm.submit();
        }
    }

    function cancel() {
	    self.document.location = JSPRootURL + '/admin/domain/Main.jsp?module=<%=net.project.base.Module.APPLICATION_SPACE%>';
    }

    function reset() {
        theForm.reset();
    }
    
	function help()	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=domain_edit";
		openwin_help(helplocation);
	}
    
    function validate() {
        //Check to make sure that required fields are present
        if(!checkTextbox(theForm.name, "Please enter a domain name")) return false;
        return true;    
    }

    function tabClick(nextPage) {
        theForm.nextPage.value = nextPage + '?module=<%=net.project.base.Module.APPLICATION_SPACE%>&action=<%=Action.MODIFY%>&domainID=<%=domainID%>';
        submit();            
    }
    
</script>

</head>

<body class="main" onload="setup()" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
	
<%------------------------------------------------------------------------
  -- Toolbar and History setup
  -----------------------------------------------------------------------%>

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.domainmanager">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="<%=pageTitle%>"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/admin/domain/Main.jsp"%>'
                          queryString='module=<%=net.project.base.Module.APPLICATION_SPACE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" >
    </tb:band>
</tb:toolbar>

<div id='content'>

<br>

<tab:tabStrip>
    <tab:tab label="<%=pageTitle%>" href="javascript:tabClick('/admin/domain/DomainEdit.jsp');" selected="true" />
    <tab:tab label='<%=PropertyProvider.get("prm.domains.tab.directoryproviderconfiguration.label")%>' href="javascript:tabClick('/admin/domain/AuthenticatorConfigurationEdit.jsp');" />
</tab:tabStrip>

<form name="main" method="post" action="<%=SessionManager.getJSPRootURL()%>/admin/domain/DomainEditProcessing.jsp">
 
  <input type="hidden" name="theAction">
  <input type="hidden" name="nextPage">
  <input type="hidden" name="domainID" value='<%=domainID%>'>
  <input type="hidden" name="module" value='<%=net.project.base.Module.APPLICATION_SPACE%>'>
  <input type="hidden" name="action" value='<%=Action.MODIFY%>'>
  <input type="hidden" name="refLink" value='<%= request.getParameter("refLink")%>'>
    
<table border="0" cellspacing="0" cellpadding="0" width="100%">

  <%-- Fields in black are required --%>
  <tr>
    <td>&nbsp;</td>
    <td class="tableHeader" colspan="2">
        <display:get name="prm.global.display.requiredfield" />
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
  
  <tr> 
    <td>&nbsp;</td>
    <td align="left" class="fieldRequired"><%=PropertyProvider.get("prm.project.admin.domain.domain.name.label") %>&nbsp;</td>
    <td class="fieldRequired"> 
      <input type="text" name="name" size="40" maxlength="80" value='<%= net.project.util.HTMLUtils.escape(domain.getName()) %>' >
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    <td align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.admin.domain.description.label")%>&nbsp;</td>
    <td class="fieldNonRequired"> 
      <input type="text" name="description" size="40" maxlength="80" value='<%= net.project.util.HTMLUtils.escape(domain.getDescription()) %>' >
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    <td align="left" class="fieldRequired"><%=PropertyProvider.get("prm.project.admin.domain.directory.label")%> &amp; <%=PropertyProvider.get("prm.project.admin.domain.authentication.provider.label")%>&nbsp;</td>
    <td class="fieldRequired"> 
      <select name="directoryProviderTypeID">
	    <%=directoryProviderTypeCollection.getOptionList(domain.getDirectoryProviderTypeID())%>
      </select>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.admin.domain.supported.configurations.label")%>&nbsp;</td>
    <td class="fieldNonRequired"> 
      <select name="supportedConfigurations" multiple size="5">
      	    <%=net.project.configuration.ConfigurationSpaceManager.getConfigurationProviderOptionList(domain)%>
      </select>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>

  <tr> 
    <td>&nbsp;</td>
    <td class="fieldNonRequired" colspan="2"> 
        <table border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td class="fieldNonRequired">
                    <input type="hidden" name="verificationRequired" value="<jsp:getProperty name="domain" property="verificationRequired" />">
                    <input type="checkbox" name="verificationRequiredCheckbox" <%=(domain.isVerificationRequired() ? "checked" : "")%> onClick="setBoolean(theForm.verificationRequiredCheckbox, theForm.verificationRequired);">
                </td>
                <td class="fieldNonRequired">
                    &nbsp;<%=PropertyProvider.get("prm.project.admin.domain.required.verification.label")%>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td align="left" valign="top" class="tableContent">
                	<%=PropertyProvider.get("prm.project.admin.domain.check.message.label")%>
                     <br>
                </td>
            </tr>
        </table>
    </td>
    <td>&nbsp;</td>
  </tr>

  <tr>
    <td>&nbsp;</td>
    <td class="fieldNonRequired" colspan="2">
        <table border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td class="fieldNonRequired">
                    <input type="checkbox" name="supportsCreditCards"<%=(domain.supportsCreditCardPurchases() ? " checked" : "")%> value="1">
                </td>
                <td class="fieldNonRequired">
                    &nbsp;<%=PropertyProvider.get("prm.project.admin.domain.allow.purchase.label")%>
                </td>
            </tr>
        </table>
    </td>
    <td>&nbsp;</td>
  </tr>

  <tr><td colspan="4">&nbsp;</td></tr>

  <tr>
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldNonRequired" colspan="2">
    	<%=PropertyProvider.get("prm.project.admin.domain.registration.instructions.label")%>
    	&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td class="fieldNonRequired" colspan="2">
      <textarea name="registrationInstructions" cols="80" rows="5"><%= net.project.util.HTMLUtils.escape(domain.getRegistrationInstructions()) %></textarea>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="tableContent" colspan="2">
        <%=PropertyProvider.get("prm.project.admin.domain.instructions.1.label")%> <br>
        <%=PropertyProvider.get("prm.project.admin.domain.instructions.2.label")%> <br>
        <%=PropertyProvider.get("prm.project.admin.domain.instructions.3.label")%>  
        <%=PropertyProvider.get("prm.project.admin.domain.instructions.4.label")%> <code>{@all.global.toolbar.action.submit}</code>
    </td>
    <td>&nbsp;</td>
  </tr>

</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="submit" />
    </tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

