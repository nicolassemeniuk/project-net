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
    info="LDAP Authenticator Configuration" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.SessionManager,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="domain" class="net.project.security.domain.UserDomain" scope="session" /> 
<jsp:useBean id="attributeMapEditor" class="net.project.base.directory.ldap.LDAPAttributeMapEditor" scope="request" /> 

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<%
    String domainID = domain.getID();

    // Manually replace directoryConfigurationBean object in session
    // We must do this because the directoryConfigurationBean bean might be of a different
    // type than any current session bean
    net.project.base.directory.ldap.LDAPDirectoryConfiguration directoryConfigurationBean = new net.project.base.directory.ldap.LDAPDirectoryConfiguration();
    pageContext.removeAttribute("directoryConfiguration", PageContext.SESSION_SCOPE);
    pageContext.setAttribute("directoryConfiguration", directoryConfigurationBean, PageContext.SESSION_SCOPE);
    directoryConfigurationBean.setDomainID(domain.getID());
    directoryConfigurationBean.load();
    
    // Initialize the attribute map editor based on the current
    // configuration
    attributeMapEditor.initialize(directoryConfigurationBean);
%>
<jsp:useBean id="directoryConfiguration" type="net.project.base.directory.ldap.LDAPDirectoryConfiguration" scope="session" />
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.application.domain.directory.ldap.edit.title" /></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<template:getSpaceCSS />
     

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">

       var theForm;
       var isLoaded = false;
       var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

    function setup() {
	    theForm = self.document.forms["main"];
        isLoaded = true;
        selectRadio(theForm.searchTypeID, '<%=(directoryConfiguration.getSearchTypeID() != null ? directoryConfiguration.getSearchTypeID() : "" + net.project.base.directory.ldap.SearchType.ALL_SUBTREES)%>');
        theForm.useSSLCheckbox.checked = <%=directoryConfiguration.isUseSSL() ? "true" : "false"%>;
        selectRadio(theForm.nonAuthenticatedAccessTypeID, '<%=(directoryConfiguration.getNonAuthenticatedAccessTypeID() != null ? directoryConfiguration.getNonAuthenticatedAccessTypeID() : "" + net.project.base.directory.ldap.NonAuthenticatedAccessType.ANONYMOUS)%>');
        setAutoRegistrationRequiredFields (theForm.automaticRegistration.value);
}

    function cancel() {
	    self.document.location = JSPRootURL + '/admin/domain/DomainEdit.jsp?module=<%=net.project.base.Module.APPLICATION_SPACE%>&action=<%=net.project.security.Action.MODIFY%>&domainID=<%=domain.getID()%>';
    }

	function help()	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=directory_ldap_edit";
		openwin_help(helplocation);
	}
    
    function validate() {
        if(!checkTextbox(theForm.hostnameValues, '<display:get name="prm.application.domain.directory.ldap.hostnamelist.validate.required" />')) return false;
        if(theForm.useSSLCheckbox.checked) {
            if(!checkTextbox(theForm.secureHostnameValues, '<display:get name="prm.application.domain.directory.ldap.sslhostnamelist.validate.required" />')) return false;
        }
        if(!checkTextbox(theForm.searchBaseDN, '<display:get name="prm.application.domain.directory.ldap.searchbasedn.validate.required" />')) return false;
        if(!checkRadio(theForm.searchTypeID, '<display:get name="prm.application.domain.directory.ldap.searchtree.validate.required" />')) return false;
        if(getSelectedValue(theForm.searchTypeID) == '<%=net.project.base.directory.ldap.SearchType.LIMIT_SUBTREES%>') {
            if(!checkTextbox(theForm.searchSubtrees, '<display:get name="prm.application.domain.directory.ldap.searchtree.subtree.validate.required" />')) return false;
        }
        if(!checkTextbox(theForm.searchFilterExpression, '<display:get name="prm.application.domain.directory.ldap.searchfilterexpression.validate.required" />')) return false;
        if(!checkTextbox(theForm.usernameAttribute, '<display:get name="prm.application.domain.directory.ldap.loginnameattribute.validate.required" />')) return false;
        if(!checkRadio(theForm.nonAuthenticatedAccessTypeID, '<display:get name="prm.application.domain.directory.ldap.nonauthaccess.validate.required" />')) return false;
        if(getSelectedValue(theForm.nonAuthenticatedAccessTypeID) == '<%=net.project.base.directory.ldap.NonAuthenticatedAccessType.SPECIFIC_USER%>') {
            if(!checkTextbox(theForm.specificUserDN, '<display:get name="prm.application.domain.directory.ldap.nonauthaccess.specificuser.userdn.validate.required" />')) return false;
            if(!checkTextbox(theForm.specificUserPassword, '<display:get name="prm.application.domain.directory.ldap.nonauthaccess.specificuser.password.validate.required" />')) return false;
        }
        if(theForm.availableForDirectorySearchCheckbox.checked) {
            if(!checkTextbox(theForm.directorySearchDisplayName, '<display:get name="prm.application.domain.directory.ldap.availableforsearch.displayname.validate.required" />')) return false;
        }

        if(theForm.automaticRegistration.value == "true") {
            if(!checkTextbox(theForm.ldapAttributeName_2, '<display:get name="prm.application.domain.directory.ldap.autoregister.required.firstname"/>')) return false;
            if(!checkTextbox(theForm.ldapAttributeName_3, '<display:get name="prm.application.domain.directory.ldap.autoregister.required.lastname"/>')) return false;
            if(!checkTextbox(theForm.ldapAttributeName_6, '<display:get name="prm.application.domain.directory.ldap.autoregister.required.displayname"/>')) return false;
        }

        return true;
    }

    function clickAllowAutoRegistration (checkbox, autoRegistrationHiddenField) {

        var fieldsRequired = false;

        setBoolean(checkbox, autoRegistrationHiddenField);

        if (autoRegistrationHiddenField.value == "true") {
            fieldsRequired = "true";
        } else {
            fieldsRequired = "false";
        }

        setAutoRegistrationRequiredFields (fieldsRequired);
    }

    function setAutoRegistrationRequiredFields (required) {

    var cssClass = (required == "true") ? 'fieldRequired' : 'fieldNonRequired';

        self.document.getElementById("person.firstname").className = cssClass;
        self.document.getElementById("person.lastname").className = cssClass;
        self.document.getElementById("person.displayname").className = cssClass;
    }

    function submit() {
        if (validate()) {
            theAction("submit");
            theForm.submit();
        }
    }

    function tabClick(nextPage) {
        self.document.location = JSPRootURL + nextPage + '?module=<%=net.project.base.Module.APPLICATION_SPACE%>&action=<%=net.project.security.Action.MODIFY%>&domainID=<%=domainID%>';
    }
    
    function lookupUsername() {
        if (!checkTextbox(theForm.lookupUsername, '<display:get name="prm.application.domain.directory.ldap.attributemapping.fetchattributes.validate.required" />')) return;
        if (validate()) {
            theAction("lookupUsername");
            theForm.submit();
        }
    }

    function test() {
        if (validate()) {
            theAction("test");
            theForm.submit();
        }
    }

</script>

</head>

<body class="main" onload="setup()"  id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />
	
<%------------------------------------------------------------------------
  -- Toolbar and History setup
  -----------------------------------------------------------------------%>

<tb:toolbar style="tooltitle" showAll="true"  groupTitle="prm.application.nav.domainmanager">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="@prm.application.domain.directory.ldap.edit.title"
						  jspPage='<%=SessionManager.getJSPRootURL() + "/admin/domain/AuthenticatorConfigurationEdit.jsp"%>'
                          queryString='module=<%=net.project.base.Module.APPLICATION_SPACE%>&domainID=<%=domainID%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" >
    </tb:band>
</tb:toolbar>

<div id='content'>

<br>

<tab:tabStrip>
    <tab:tab label='<%=PropertyProvider.get("prm.domains.tab.editdomain.label") %>' href="javascript:tabClick('/admin/domain/DomainEdit.jsp');" />
    <tab:tab label='<%=PropertyProvider.get("prm.domains.tab.directoryproviderconfiguration.label")%>' href="javascript:tabClick('/admin/domain/AuthenticatorConfigurationEdit.jsp');" selected="true" />
</tab:tabStrip>

<form name="main" method="post" action="<%=SessionManager.getJSPRootURL() + "/admin/domain/LDAPDirectoryConfigurationProcessing.jsp"%>">
    <input type="hidden" name="module" value="<%=net.project.base.Module.APPLICATION_SPACE%>">
    <input type="hidden" name="action" value="<%=net.project.security.Action.MODIFY%>">
    <input type="hidden" name="theAction">

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

<% 
    String attributeMapErrorMessage = (String) request.getAttribute("errorMsgAttributeMap");
    if (attributeMapErrorMessage != null && attributeMapErrorMessage.trim().length() > 0) {
%>
  <tr>
    <td>&nbsp;</td>
    <td class="fieldWithError" align="left" colspan="2">
      <%=attributeMapErrorMessage%>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
<%  } %>
  
  <tr> 
    <td>&nbsp;</td>
    <td align="left" class="fieldRequired" nowrap><display:get name="prm.application.domain.directory.ldap.hostnamelist" />:&nbsp;</td>
    <td class="fieldRequired"> 
      <input type="text" name="hostnameValues" size="80" maxlength="1000" value='<%= net.project.util.HTMLUtils.escape(directoryConfiguration.getHostnameValues()) %>' >
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td class="tableContent">
        <display:get name="prm.application.domain.directory.ldap.hostnamelist.instructions" />
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>

  <tr> 
    <td>&nbsp;</td>
    <td align="left" class="fieldNonRequired" nowrap><display:get name="prm.application.domain.directory.ldap.usessl" />:&nbsp;</td>
    <td class="fieldNonRequired"> 
        <input type="hidden" name="useSSL" value="<%= directoryConfiguration.isUseSSL() %>" >
        <input type="checkbox" name="useSSLCheckbox" <%=(directoryConfiguration.isUseSSL() ? "checked" : "")%> onClick="setBoolean(theForm.useSSLCheckbox, theForm.useSSL);">
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    <td align="left" class="fieldNonRequired" nowrap><display:get name="prm.application.domain.directory.ldap.sslhostnamelist" />:&nbsp;</td>
    <td class="fieldNonRequired"> 
      <input type="text" name="secureHostnameValues" size="80" maxlength="1000" value='<%= net.project.util.HTMLUtils.escape(directoryConfiguration.getSecureHostnameValues()) %>' >
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td class="tableContent">
        <display:get name="prm.application.domain.directory.ldap.sslhostnamelist.instructions" />
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>

  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldRequired" nowrap><display:get name="prm.application.domain.directory.ldap.searchbasedn" />:&nbsp;</td>
    <td class="fieldRequired"> 
      <input type="text" name="searchBaseDN" size="80" maxlength="1000" value='<%= net.project.util.HTMLUtils.escape(directoryConfiguration.getSearchBaseDN()) %>' >
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td class="tableContent">
        <display:get name="prm.application.domain.directory.ldap.searchbasedn.instructions" />
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>

  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldRequired" colspan="2"><display:get name="prm.application.domain.directory.ldap.searchtree.label1" />:&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldRequired" colspan="2">
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr>
            <td width="5%" class="fieldNonRequired" valign="top">
              <input type="radio" name="searchTypeID" value="<%="" + net.project.base.directory.ldap.SearchType.ALL_SUBTREES%>" >&nbsp;
            </td>
            <td class="fieldNonRequired" valign="top" colspan="2">
              <display:get name="prm.application.domain.directory.ldap.searchtree.allbranches" />
            </td>
        </tr>
        <tr>
            <td width="5%" class="fieldNonRequired" valign="top">
              <input type="radio" name="searchTypeID" value="<%="" + net.project.base.directory.ldap.SearchType.LIMIT_SUBTREES%>" >&nbsp;
            </td>
            <td class="fieldNonRequired" valign="top" colspan="2">
              <display:get name="prm.application.domain.directory.ldap.searchtree.subtree" />: <br>
              <input type="text" name="searchSubtrees" size="40" maxlength="1000" value="<%= net.project.util.HTMLUtils.escape(directoryConfiguration.getSearchSubtrees()) %>" > <br>
              <span class="tableContent"><display:get name="prm.application.domain.directory.ldap.searchtree.subtree.instructions" /></span>
            </td>
        </tr>
      </table>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
  
  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldRequired" nowrap><display:get name="prm.application.domain.directory.ldap.searchfilterexpression" />:&nbsp;</td>
    <td class="fieldRequired"> 
        <input type="text" name="searchFilterExpression" size="40" maxlength="1000" value="<%= net.project.util.HTMLUtils.escape(directoryConfiguration.getSearchFilterExpression()) %>" >
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td class="tableContent" valign="top">
        <display:get name="prm.application.domain.directory.ldap.searchfilterexpression.instructions" />
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>

  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldRequired" nowrap><display:get name="prm.application.domain.directory.ldap.loginnameattribute" />:&nbsp;</td>
    <td class="fieldRequired"> 
      <input type="text" name="usernameAttribute" size="80" maxlength="1000" value='<%= net.project.util.HTMLUtils.escape(directoryConfiguration.getUsernameAttribute()) %>' >
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td class="tableContent">
        <display:get name="prm.application.domain.directory.ldap.loginnameattribute.instructions" />    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>

  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldRequired" colspan="2"><display:get name="prm.application.domain.directory.ldap.nonauthaccess.label1" />:</td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldRequired" colspan="2">
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr>
            <td width="5%" class="fieldNonRequired" valign="top">
              <input type="radio" name="nonAuthenticatedAccessTypeID" value="<%="" + net.project.base.directory.ldap.NonAuthenticatedAccessType.ANONYMOUS%>" >&nbsp;
            </td>
            <td class="fieldNonRequired" valign="top" colspan="2">
              <display:get name="prm.application.domain.directory.ldap.nonauthaccess.anonymous" /> <br>
            </td>
        </tr>
        <tr>
            <td width="5%" class="fieldNonRequired" valign="top">
              <input type="radio" name="nonAuthenticatedAccessTypeID" value="<%="" + net.project.base.directory.ldap.NonAuthenticatedAccessType.SPECIFIC_USER%>" >&nbsp;
            </td>
            <td class="fieldNonRequired" valign="top" colspan="2">
              <display:get name="prm.application.domain.directory.ldap.nonauthaccess.specificuser" />:
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td class="fieldNonRequired" valign="top"><display:get name="prm.application.domain.directory.ldap.nonauthaccess.specificuser.userdn" />:&nbsp;</td>
            <td class="fieldNonRequired" valign="top">
                <input type="text" name="specificUserDN" size="40" maxlength="1000" value="<%= net.project.util.HTMLUtils.escape(directoryConfiguration.getSpecificUserDN()) %>" >
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td class="tableContent">
                <display:get name="prm.application.domain.directory.ldap.nonauthaccess.specificuser.userdn.instructions" />
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td class="fieldNonRequired" valign="top"><display:get name="prm.application.domain.directory.ldap.nonauthaccess.specificuser.password" />:&nbsp;</td>
            <td class="fieldNonRequired" valign="top">
                <input type="password" name="specificUserPassword" size="40" maxlength="1000" value="<%= net.project.util.HTMLUtils.escape(directoryConfiguration.getSpecificUserPassword()) %>" >
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td class="tableContent">
                <display:get name="prm.application.domain.directory.ldap.nonauthaccess.specificuser.password.instructions" />
            </td>
        </tr>
      </table>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>

  <%-- Registration and Directory Management Options --%>
  <tr>
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldRequired" colspan="2"><display:get name="prm.application.domain.directory.ldap.options.label" />:</td>
    <td>&nbsp;</td>
  </tr>

  <tr>
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldNonRequired" colspan="2">
        <input type="hidden" name="automaticRegistration" value="<%=directoryConfiguration.allowsAutomaticRegistration()%>" >
        <input type="checkbox" name="automaticRegistrationCheckbox" <%=(directoryConfiguration.allowsAutomaticRegistration() ? "checked" : "")%> onClick="clickAllowAutoRegistration(theForm.automaticRegistrationCheckbox, theForm.automaticRegistration);">
        &nbsp;<display:get name="prm.application.domain.directory.ldap.automaticRegistration" /></td>
    <td>&nbsp;</td>
  </tr>

    <tr><td colspan="4">&nbsp;</td></tr>

  <tr>
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldNonRequired" colspan="2">
        <input type="hidden" name="availableForDirectorySearch" value="<jsp:getProperty name="directoryConfiguration" property="availableForDirectorySearch" />" >
        <input type="checkbox" name="availableForDirectorySearchCheckbox" <%=(directoryConfiguration.isAvailableForDirectorySearch() ? "checked" : "")%> onClick="setBoolean(theForm.availableForDirectorySearchCheckbox, theForm.availableForDirectorySearch);">
        &nbsp;<display:get name="prm.application.domain.directory.ldap.availableforsearch" /></td>
    <td>&nbsp;</td>
  </tr>
  <tr> 
    <td>&nbsp;</td>
    <td align="left" valign="top" class="fieldNonRequired" colspan="2">
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr>
            <td width="5%">&nbsp;</td>
            <td class="fieldNonRequired" valign="top" nowrap><display:get name="prm.application.domain.directory.ldap.availableforsearch.displayname" />:&nbsp;</td>
            <td class="fieldNonRequired" valign="top">
                <input type="text" name="directorySearchDisplayName" size="40" maxlength="80" value="<%= net.project.util.HTMLUtils.escape(directoryConfiguration.getDirectorySearchDisplayName()) %>" >
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td class="tableContent">
                <display:get name="prm.application.domain.directory.ldap.availableforsearch.displayname.instructions" />
            </td>
        </tr>
      </table>
    </td>
    <td>&nbsp;</td>
  </tr>
  <tr>
      <td colspan="4">
          <tb:toolbar style="action" showLabels="true">
              <tb:band name="action" enableAll="true">
                  <tb:button type="submit" />
                  <tb:button type="finish" label="@prm.application.domain.directory.ldap.toolbar.action.test" function="javascript:test();" />
                  <tb:button type="cancel" />
              </tb:band>
          </tb:toolbar>
      </td>
  </tr>
</table>

<br>

<%-- Attribute Mapping Section --%>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr class="channelHeader">
    	<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border=0></td>
        <td colspan="3" class="channelHeader"><display:get name="prm.application.domain.directory.ldap.attributemapping.title" /></td>
    	<td width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>
    <tr>
        <td>&nbsp;</td>
        <td class="tableHeader" colspan="3">
            <display:get name="prm.application.domain.directory.ldap.attributemapping.instructions" />
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>
    <tr>
        <td>&nbsp;</td>
        <td class="fieldRequired" align="left" colspan="3">
            <display:get name="prm.application.domain.directory.ldap.attributemapping.fetchattributes" />:
            &nbsp;&nbsp;
            <input type="text" name="lookupUsername" size="20" maxlength="1000">
            &nbsp;&nbsp;
  			<a href="javascript:lookupUsername();">
                <img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Lookup Username" border="0" align="absmiddle">
            </a>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>
    
    <tr>
        <td>&nbsp;</td>
        <td colspan="3">
            <jsp:include page="/admin/domain/include/LDAPAttributeMapModify.jsp" flush="true" />
        </td>
        <td>&nbsp;</td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
                <tb:band name="action" enableAll="true">
                    <tb:button type="submit" />
                    <tb:button type="cancel" />
                </tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>

