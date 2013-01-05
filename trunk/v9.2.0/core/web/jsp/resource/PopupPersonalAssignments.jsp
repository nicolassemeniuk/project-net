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
       info="Popup Assignment List" 
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
               net.project.base.finder.NumberComparator,
               net.project.portfolio.ProjectPortfolioBean,
               net.project.persistence.PersistenceException,
               net.project.resource.AssignmentType,
               net.project.gui.html.HTMLOptionList,
               net.project.base.finder.TextComparator,
               net.project.util.NumberFormat,
               net.project.resource.mvc.handler.PersonalAssignmentsFilterHandler,
               java.util.Collection,
               java.util.Arrays,
               net.project.util.Validator,
               java.util.Date,
               net.project.calendar.PnCalendar,
               net.project.resource.mvc.handler.UpdateAssignmentsHandler,
               java.net.URLEncoder,
               net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
	
<jsp:useBean id="assignmentManagerBean" class="net.project.resource.AssignmentManagerBean" scope="request" />
<%
    String returnHereUrl = "/servlet/AssignmentController/PersonalAssignments?module=" + Module.PERSONAL_SPACE + "&mode=2";
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
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';  

function setup() {
    isLoaded = true;
    theForm = self.document.forms[0];
}

function add() {
    var checkedObjects = new Array();
    var checkedObjectNames = new Array();
    if (theForm.objectID.length) {
        for (var i = 0; i < theForm.objectID.length; i++) {
            if(theForm.objectID[i].checked == true) {
                captureWorkSupported = eval("theForm.capture_work_" + theForm.objectID[i].value+ ".value") == "1";
                if (captureWorkSupported) {
                    checkedObjects[checkedObjects.length] = theForm.objectID[i].value;
                    var hrefElt = document.getElementById("href"+theForm.objectID[i].value);
                    checkedObjectNames[checkedObjectNames.length] = hrefElt.innerHTML;
                }
            }
        }
    } else {
        captureWorkSupported = eval("theForm.capture_work_" + theForm.objectID.value+ ".value") == "1";
        if (captureWorkSupported) {
            checkedObjects[checkedObjects.length] = theForm.objectID.value;
            var hrefElt = document.getElementById("href"+theForm.objectID.value);
            checkedObjectNames[checkedObjectNames.length] = hrefElt.innerHTML;
        }
    }

    window.opener.addTaskComplete(checkedObjects, checkedObjectNames);
}

</script>
</head>
<body class="main" onLoad="setup();">

<form method="post" action="<%=SessionManager.getJSPRootURL() + "/servlet/AssignmentController/PersonalAssignments?module=" + Module.PERSONAL_SPACE + "&mode=2"%>">
<input type="hidden" name="action" value="<%=Action.VIEW%>">
<input type="hidden" name="module" value="<%=Module.PERSONAL_SPACE%>">

<errors:show clearAfterDisplay="true"/>
        
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
    <tr>
        <td width="1%">&nbsp;</td>
        <td>
            <pnet-xml:transform stylesheet="/resource/xsl/view-assignments.xsl" xml="<%=assignmentManagerBean.getXML()%>">
                <pnet-xml:param name="returnTo" value="<%=URLEncoder.encode(returnHereUrl, SessionManager.getCharacterEncoding())%>" />
            </pnet-xml:transform>
        </td>
        <td width="1%">&nbsp;</td>
    </tr>
	<tr align="right" valign="top">
        <td colspan="2">
            <tb:toolbar style="action" showLabels="true" width="97%">
            <tb:band name="action">
                <tb:button type="add"/>
            </tb:band>
            </tb:toolbar>
        </td>
    </tr>
</table>

</form>

<template:getSpaceJS />
</body>
</html>