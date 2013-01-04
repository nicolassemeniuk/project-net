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
|    $RCSfile$
|   $Revision: 18995 $
|       $Date: 2009-03-05 08:36:26 -0200 (jue, 05 mar 2009) $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Project Space" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.resource.SpaceInvitationManager, 
            net.project.security.SessionManager,
			net.project.base.property.PropertyProvider,
			net.project.security.Action,
			net.project.base.Module"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="spaceInvitationWizard" type="net.project.resource.SpaceInvitationManager" scope="session" />
<jsp:useBean id="searchResults" type="net.project.base.directory.search.ISearchResults" scope="session" />
<jsp:useBean id="validator" class="net.project.resource.InviteeValidator" scope="request" />
<jsp:useBean id="pageContextManager" class="net.project.session.PageContextManager" scope="session" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<security:verifyAccess action="create"
					   module="<%=Module.DIRECTORY%>" /> 
					   
<%-- Import CSS --%>
<template:getSpaceCSS />

<script language="javascript1.2">
var theForm;
var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

function setup(){
	theForm = self.document.forms["searchResults"];
}

function helpMe()
 {
  	var helplocation = JSPRootURL + "/help/HelpDesk.jsp?page=directory_member_add_search";
 	openwin_help(helplocation);
}

function validate() {
    var isChecked = false;
    if(theForm.resultID) {
	    if (theForm.resultID.length) {
            // There are many results
	        for (var i = 0; i < theForm.resultID.length && !isChecked; i++) {
	            if (theForm.resultID[i].checked) {
	                isChecked = true;
	            }
	        }
	    } else {
	        // Only one result
	        isChecked = theForm.resultID.checked;
	    }
	   if (!isChecked) {
			extAlert(errorTitle, '<display:get name="prm.directory.memberadddirectorysearchresults.validationwarning.message" />' , Ext.MessageBox.ERROR);
    	}
    } else {
    	extAlert(errorTitle, '<display:get name="prm.directory.nomembersadddirectorysearchresults.validationwarning.message" />' , Ext.MessageBox.ERROR);
    }
    return isChecked;
}

function isSelectionNeeded() {
    <%-- Selection is needed if inviteeList is currently empty --%>
    return <%=spaceInvitationWizard.getInviteeList().isEmpty() ? "true" : "false"%>;
}

function theAction (myAction) {
	theForm.theAction.value = myAction;
}

function cancel() {
    self.document.location = "<%=pageContextManager.getProperty("directory.url.complete")%>";
}

function next() {
    // Only submit if a selection is not needed or
    // (a selection is needed) and a checkbox is selected
    if (!isSelectionNeeded() || validate()) {
        theAction("next");
        theForm.submit();
    }
}

function add() {
    // Always validate a selection if click Add
    if (validate()) {
        theAction("add");
        theForm.submit();
    }
}

function wizSearch() {
    theAction("search");
    theForm.submit();
}

function removeInvitee(id) {
	theForm.inviteeID.value = id;
    theAction("removeInvitee");
    theForm.submit();
}

</script>
</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='content'>

<form name="searchResults" action="<%=SessionManager.getJSPRootURL()%>/roster/MemberAddDirectorySearchController.jsp" method="post">
	<input type="hidden" name="theAction" value="search">
	<input type="hidden" name="module" value="<%=Module.DIRECTORY%>">
	<input type="hidden" name="action" value="<%=Action.CREATE%>">
    <input type="hidden" name="inviteeID" value="">
<br>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td  nowrap  class="channelHeader" colspan="3" width="98%"><nobr><display:get name="prm.directory.memberadddirectorysearchresults.channel.invite.title" /></nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>

<tr>
    <td>&nbsp;</td>
    <td colspan="3">
        <table border="1" cellspacing="0" cellpadding="0" width="100%" height="100%" frame="box" rules="none" bordercolor="#000000">
            <tr>
                <td valign="top" height="100%">
                    <jsp:include page="/roster/include/DirectorySearch.jsp" flush="true" />
                </td>
            </tr>
        </table>
    </td>
    <td>&nbsp;</td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>
    
<%-- Display results --%>
<tr class="channelHeader">
    <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="3"><nobr><display:get name="prm.directory.memberadddirectorysearchresults.channel.searchresults.title" /></nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>

<%  
    if (validator.isInvalid()) {
        String errorMessage = (String) request.getAttribute("errorMsg");
%>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" class="fieldWithError">
            <%=errorMessage%>: <br>
            <%=validator.formatInvalidInvitees()%>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>

<%
    }
%>

<%-- Results are paged --%>
<pg:pager name="searchResults" scope="session" pageSize="10">
    
    <tr>
        <td>&nbsp;</td>
        <td colspan="3">
            <pnet-xml:transform name="searchResults" scope="session" stylesheet="/roster/xsl/InviteDirectorySearchResults.xsl" />
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>
    <tr>
        <td>&nbsp;</td>
        <td colspan="3" align="center" class="tableContent">
            <%-- Insert the pager index --%>
            <pg:index href='<%=SessionManager.getJSPRootURL() + "/roster/MemberAddDirectorySearchResults.jsp?module=" + Module.DIRECTORY + "&action=" + Action.CREATE%>' 
                      stylesheet="/base/xsl/PagerPageIndex.xsl" maxLinks="10" />
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>
</pg:pager>
    
    <tr> 
        <td colspan="5"> 
			<tb:toolbar style="action" showLabels="true">
				<tb:band name="action">
					<tb:button type="add" label='<%= PropertyProvider.get("prm.directory.memberadddirectorysearchresults.add.button.label") %>' />
					<tb:button type="next" />
					<tb:button type="cancel" />
				</tb:band>
			</tb:toolbar>
        </td>
    </tr>
    <tr><td colspan="5">&nbsp;</td></tr>

    <tr>
        <td colspan="5">
            <jsp:include page="/roster/include/CurrentInvitees.jsp" flush="true" />
        </td>
    </tr>

</table>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
