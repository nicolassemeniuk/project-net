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
       info="Assignment List" 
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.security.User, 
               net.project.security.SecurityProvider,
               net.project.security.SessionManager,
               net.project.security.Action,
               net.project.schedule.Schedule,
               net.project.base.Module,
               net.project.base.property.PropertyProvider,
               net.project.space.Space,
               net.project.resource.RosterBean,
               net.project.resource.AssignmentManagerBean,
			   net.project.resource.Assignment,
               net.project.xml.XMLFormatter,
               net.project.space.PersonalSpaceBean,
			   net.project.resource.SpaceInvitationManager,
               java.util.List,
               net.project.resource.AssignmentStatus,
               net.project.gui.html.HTMLOptionList,
               java.net.URLEncoder"
%>
<%@ include file="/base/taglibInclude.jsp" %>
	
<jsp:useBean id="user" class="net.project.security.User" scope="session" />  
<jsp:useBean id="assignmentManagerBean" class="net.project.resource.AssignmentManagerBean" scope="request" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="spaceInvitationWizard" type="net.project.resource.SpaceInvitationManager" scope="session"/>
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<%!
// a few constants
public static int PROJECT = 997;
public static int PROJECT_ADMIN = 998;
%>

<%
	int module = securityProvider.getCheckedModuleID();

    int pageState = PROJECT;

    if(securityProvider.isUserSpaceAdministrator()) {
        pageState = PROJECT_ADMIN;
        roster.setSpace(SessionManager.getUser().getCurrentSpace());
        roster.load();
    }

    Space currentSpace = user.getCurrentSpace();

    assignmentManagerBean.setSpaceID(currentSpace.getID());

    String userID = user.getID();
    if(pageState == PROJECT_ADMIN) {
        String personID = request.getParameter("person_id");
        if(personID != null) {
            userID = personID;
        }
    }
    assignmentManagerBean.setPersonID(userID);

	// Set the Filter for the assignment status to display
	// Default status is "All Open Assignments"
    String status = request.getParameter("status");
    if(status == null) {
        status = "1";
	}

	if (status.equals("0")) {
		assignmentManagerBean.setStatusFilter((List)null);
	} else if (status.equals("1")) {
		assignmentManagerBean.setStatusFilter(AssignmentStatus.getPersonalAssignmentStatuses());
	} else {
		assignmentManagerBean.setStatusFilter(AssignmentStatus.getForID(status));
	}

    assignmentManagerBean.loadAssignments();

    // Construct the URL for navigating back to the same View
    String returnHereUrl = "/resource/Assignments.jsp?module=" + Module.PROJECT_SPACE + "&person_id=" + userID + "&status=" + status + "&rosterComboSelectedIndex=" + request.getParameter("rosterComboSelectedIndex");
%>


<security:verifyAccess action="view" module="<%=Module.PROJECT_SPACE%>" />

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<history:history displayHere="false">
    <history:page display='<%=PropertyProvider.get("prm.project.directory.assignments.module.history")%>'
            jspPage='<%=SessionManager.getJSPRootURL() + "/resource/Assignments.jsp"%>'
            queryString='<%="module=" + net.project.base.Module.PERSONAL_SPACE%>' />
</history:history>


<script language="javascript">
var theForm;
var isLoaded = false;
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() 
{
    load_menu('<%=user.getCurrentSpace().getID()%>');
    isLoaded = true;
    theForm = self.document.forms[0];

<% 
    if(pageState == PROJECT_ADMIN) {
		String rosterComboIndex = request.getParameter("rosterComboSelectedIndex");
		if(rosterComboIndex == null) {
%>           
            for(i=0; i < theForm.person_id.length; i++) {
                theForm.person_id.options.selectedIndex = i;
                if(theForm.person_id.options[theForm.person_id.options.selectedIndex].value == <%= user.getID() %> )
                    i = theForm.person_id.length;
	        }
<%      } else { %>
			theForm.person_id.options.selectedIndex = <%= rosterComboIndex %>;
<%
        } 
	}
%>
	// Select the Status dropdown value
	selectByValue(theForm.status, <%=status%>);
}

function cancel() { self.document.location = JSPRootURL + "/project/Dashboard"; }
function reset() { self.document.location = JSPRootURL + "/resource/Assignments.jsp"
                    + "?module=<%=module%>"; }

function security() {
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
	    if (!security)
	        var security = openwin_security("security");
    
	    if (security) {
            theAction("security");
            theForm.target = "security";
            theForm.submit();
            security.focus();
	    }
	}
}

function modify() {
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        theAction("modify");
        theForm.submit();
	}
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=directory_project&section=assignment";
	openwin_help(helplocation);
}

function selectByValue(theSelect, theValue) {
	if (theSelect) {
		for (i = 0; i < theSelect.options.length; i++) {
			if (theSelect.options[i].value == theValue) {
				theSelect.options[i].selected = true;
			}
		}
	}
}

function comboChange() {
<%
    if(pageState == PROJECT || pageState == PROJECT_ADMIN) {
	    if (pageState == PROJECT_ADMIN) { %>
            theForm.rosterComboSelectedIndex.value = theForm.person_id.options.selectedIndex;
<%
        }
    }
%>
    theForm.submit();
}


</script>

</head>
<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.nav.assignment">
	<tb:setAttribute name="leftTitle">
		<history:history />
	</tb:setAttribute>
	<tb:band name="standard">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/resource/Assignments.jsp" >
<input type="hidden" name="theAction">
<input type="hidden" name="module" value="<%=module%>">

<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
    <tr>
        <td>
        <tab:tabStrip>
            <tab:tab label='<%=PropertyProvider.get("prm.directory.directory.tab.participants.title")%>' href='<%=SessionManager.getJSPRootURL() + "/roster/Directory.jsp?module=" + Module.DIRECTORY%>' display="<%=spaceInvitationWizard.isParticipantsSupported()%>" />
            <tab:tab label='<%=PropertyProvider.get("prm.directory.directory.tab.assignments.title")%>' selected="true" href='<%=SessionManager.getJSPRootURL() + "/resource/Assignments.jsp?module=" + Module.PROJECT_SPACE%>' display="<%=spaceInvitationWizard.isAssignmentsSupported()%>" />
            <tab:tab label='<%=PropertyProvider.get("prm.directory.directory.tab.roles.title")%>' href='<%=SessionManager.getJSPRootURL()+ "/security/group/GroupListView.jsp?module=" + Module.DIRECTORY%>' display="<%=spaceInvitationWizard.isRolesSupported()%>" />
        </tab:tabStrip>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>
            <span class="tableHeader"><display:get name="prm.project.directory.assignments.statusfilter.label"/></span>
            &nbsp;
            <select name="status" onChange="javascript:comboChange()">
              <option value="0"><display:get name="prm.personal.assignments.statusfilter.option.all.name"/></option>
              <option value="1"><display:get name="prm.personal.assignments.statusfilter.option.allopen.name"/></option>
              <%=HTMLOptionList.makeHtmlOptionList(AssignmentStatus.getValidStatuses())%>
            </select>
            &nbsp;
<% if ( pageState == PROJECT_ADMIN ) { %>
            <%-- Include filter on persons --%>
            <span class="tableHeader"><%=PropertyProvider.get("prm.project.directory.assignments.viewtasksfor.label")%></span>
            <jsp:setProperty name="roster" property="stylesheet" value="/resource/xsl/roster-combo.xsl" />
            <jsp:getProperty name="roster" property="presentation" />
            <input type="hidden" name="rosterComboSelectedIndex" value="0">
<% } %>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>
            <pnet-xml:transform stylesheet="/resource/xsl/view-assignments.xsl" xml="<%=assignmentManagerBean.getXML()%>">
                <pnet-xml:param name="returnTo" value="<%=URLEncoder.encode(returnHereUrl, SessionManager.getCharacterEncoding())%>" />
            </pnet-xml:transform>
        </td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
            <tb:band name="action" />
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
