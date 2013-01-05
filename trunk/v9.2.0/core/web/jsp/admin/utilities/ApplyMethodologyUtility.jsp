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
    info="List all Users"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.Action,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.xml.XMLFormatter"
  %>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="methodologyProvider" class="net.project.methodology.MethodologyProvider" scope="page" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="application" />

<%-- Import JavaScript --%>
<template:getSpaceJS space="application" />

<script language="JavaScript">
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var Module = '<%= Module.APPLICATION_SPACE %>';
	var theForm;
	

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    load_header();
    isLoaded = true;
	theForm = theForm = self.document.forms[0];
}

function cancel() {

    reset();
}

function apply() {
    // Avinash BFD 3152
    var result = true;
    if(theForm.methodologyID.value == "" ){
	    var errorMessage = '<%=PropertyProvider.get ("prm.project.admin.utilities.applymethodologytemplate")%>';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
    	result = false;
    }else
    if(theForm.workspaceID.value == "" ){
	    var errorMessage = '<%=PropertyProvider.get ("prm.project.admin.utilities.applymethodologyworkspace")%>';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
    	result = false;
    }
    if(result == true){
	    theAction("apply");
	    theForm.submit();
 	}
}

function reset() {
//Avinash: setting cancellation page path 
    self.document.location = JSPRootURL + '/admin/Main.jsp?module=' + Module + '&action=<%=Action.VIEW%>';
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=admin_userlist";
    openwin_help(helplocation);
}



</script>

</head>

<body class="main" id="bodyWithFixedAreasSupport" onload="setup();">
<template:getSpaceMainMenu />

<%-- Create the toolbar --%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.template">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Apply Methodology Utility"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/utilities/ApplyMethodologyUtility.jsp"%>'
                            queryString='<%="module="+Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="mainForm" method="post" action="<session:getJSPRootURL />/admin/utilities/ApplyMethodologyProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=Module.APPLICATION_SPACE%>" />
<input type="hidden" name="action" value='<%=Action.MODIFY%>' />
<p/>
<p/>

<display:get name="@prm.application.template.copy.src"/> <select name="methodologyID">
                <option SELECTED value=""><display:get name="prm.project.create.wizard.methodology.none" /></option>
                <%= methodologyProvider.getMethodologyOptionList() %>
              </select><%-- <a href="javascript:methodologyBrowser()">Methodology Browser</a>--%>
<display:get name="prm.application.template.copy.dest"/> <input type="text" name="workspaceID">

<p/>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action" enableAll="true">
        <tb:button type="cancel" label="Cancel" function="javascript:cancel();" />
		<tb:button type="submit" label="Apply Template" function="javascript:apply();" />
    </tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceNavBar space="application"/>
</body>
</html>
