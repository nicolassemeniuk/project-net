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
    info="Allows user to information for timesheet."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.util.DateFormat,
            net.project.util.NumberFormat,
            net.project.base.Module,
            net.project.security.Action,
            net.project.base.property.PropertyProvider,
            java.util.GregorianCalendar,
            java.util.Date,
            java.util.Iterator,
            net.project.util.TimeQuantity,
            net.project.resource.Timesheet,
            net.project.resource.TimesheetStatus"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="timesheet" type="net.project.resource.Timesheet" scope="session"/>
<jsp:useBean id="scrollBackStartDate" class="java.util.Date" scope="request"/>
<jsp:useBean id="scrollForwardStartDate" class="java.util.Date" scope="request"/>
<jsp:useBean id="dateRangeStart" class="java.util.Date" scope="request"/>
<jsp:useBean id="assignments" type="java.util.List" scope="request"/>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request" />
<jsp:useBean id="readOnly" type="java.lang.Boolean" scope="request"/>

<html>
<head>

<title><display:get name="prm.resource.timesheet.create.pagetitle"/></title>

<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/dhtml/dhtmlutils.js" />
<template:import type="javascript" src="/src/dhtml/xmlrequest.js" />
<template:import type="javascript" src="/src/dhtml/visibility.js" />
<template:import type="javascript" src="/src/dhtml/findDOM.js" />

<script language="javascript" type="text/javascript">
var theForm;

function setup() {
    theForm = document.forms[0];
<%  if(assignments.size() > 0) { %>
        setVisibility("divtimecard", "visible");
        setDisplay("divtimecard", "block");
<%  } else { %>
        setVisibility("divtimecard0", "visible");
        setDisplay("divtimecard0", "block");
<%  } %>
}

function scrollBack() {
    var returnTo = escape("<%=request.getParameter("returnTo")%>");
    var action =  "<%=request.getAttribute("action")%>";
    if(action == "<%=Action.CREATE%>") {
        self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/AssignmentController/CurrentAssignments/CreateTimesheet?module=<%=securityProvider.getCheckedModuleID()%>&action=<%=securityProvider.getCheckedActionID()%>&startDate=<%=scrollBackStartDate.getTime()%>&" + (returnTo != "" ? "&returnTo=" + returnTo : "");
    } else {
    	var errorMessage = '<%=PropertyProvider.get("prm.resource.timesheet.scroll.error")%>';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
       }
}

function scrollForward() {
    var returnTo = escape("<%=request.getParameter("returnTo")%>");
    var action =  "<%=request.getAttribute("action")%>";
    if(action == "<%=Action.CREATE%>") {
        self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/AssignmentController/CurrentAssignments/CreateTimesheet?module=<%=securityProvider.getCheckedModuleID()%>&action=<%=securityProvider.getCheckedActionID()%>&startDate=<%=scrollForwardStartDate.getTime()%>&" + (returnTo != "" ? "&returnTo=" + returnTo : "");
    } else {
        var errorMessage = '<%=PropertyProvider.get("prm.resource.timesheet.scroll.error")%>';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
    }
}

function flagError(errorText) {
    document.getElementById("errorLocationID").innerHTML += (errorText + "<br/>");
    //Avinash bfd 3169 Able to record negative hours of work against a task. 
    valid=false;
}

function accept() {
	var errorMessage = '<%=PropertyProvider.get("prm.project.form.designer.feature.not.implemented.label")%>';
	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
}

function decline() {
	var errorMessage = '<%=PropertyProvider.get("prm.project.form.designer.feature.not.implemented.label")%>';
	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
}


function back() {
	location.href = theForm.returnTo.value;
}

function cancel() {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>','<%=PropertyProvider.get ("prm.resource.timesheet.javascript.cancel.confirm.message")%>', function(btn) { 
		if(btn == 'yes'){ 
			theForm.theAction.value = "/CurrentAssignments/CancelTimesheetProcessing";
		   //Avinash bfd 3169 Able to record negative hours of work against a task. 
			if(valid) {
				theForm.submit();
			}
		} else {
			return false;
		}
	});
}

function submit() {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>','<%=PropertyProvider.get ("prm.resource.timesheet.javascript.submit.confirm.message")%>', function(btn) { 
		if(btn == 'yes') { 
		 	theForm.theAction.value = "/CurrentAssignments/SubmitTimesheetProcessing";
        	//Avinash bfd 3169 Able to record negative hours of work against a task. 
        	if(valid && verifyNames()) {
            	theForm.submit();
        	}
		} else {
		 	return false;
		}
	});
}

function update() {
    theForm.theAction.value = "/CurrentAssignments/UpdateTimesheetProcessing";
	//Avinash bfd 3169 Able to record negative hours of work against a task. 
	if(valid && verifyNames()){
    	theForm.submit();
    }
}

function create() {
    if(objectIds.length + index == 0) {
    	var errorMessage = '<%=PropertyProvider.get ("prm.resource.timesheet.javascript.empty.alert.message")%>';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
    } else {
        theForm.theAction.value = "/CurrentAssignments/CreateTimesheetProcessing";
        //Avinash bfd 3169 Able to record negative hours of work against a task. 
        if(valid && verifyNames()){
            theForm.submit();
        }
    }
}

function verifyNames() {
    for(var i = 0; i < index ; i++) {
        var nameElements = document.getElementsByName("activityId"+i);
        if (nameElements && nameElements.length > 0) {
            for (var j = 0; j < nameElements.length; j++) {
                var flag = verifyNonBlankField_withAlert(nameElements[j], '<%=PropertyProvider.get ("prm.resource.timesheet.javascript.name.alert.message")%>');
                if(flag == false) 
                    return flag;
            }
        }
    }
    return true;
}

function reset() {
    var id = "<%= timesheet.getID() %>";
    var startDate = <%= timesheet.getStartDate() != null ? timesheet.getStartDate().getTime() : "" %>;
	var returnTo = escape("<%=request.getParameter("returnTo")%>");
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/AssignmentController/CurrentAssignments/CreateTimesheet?module=<%=securityProvider.getCheckedModuleID()%>&action=<%=securityProvider.getCheckedActionID()%>&timesheetId="+id + (startDate != "" ? "&startDate=" + startDate : "") + (returnTo != "" ? "&returnTo=" + returnTo : "");
}

function popupHelp(page) {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page="+page;
	openwin_help(helplocation);
}

function help(page) {
    openwin_help("<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=timesheet_create");
}

</script>

</head>

<body class="main" onLoad="setup();" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.resource.timesheet.label">
    <tb:setAttribute name="leftTitle">
        <history:history>
			<history:page displayToken="@prm.resource.timesheet.create.pagetitle"
					jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/AssignmentController/CurrentAssignments/CreateTimesheet" %>'
					queryString='<%="module="+Module.RESOURCE+"&action="+Action.CREATE%>' />
            </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<br>
<form method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/AssignmentController">
    <input type="hidden" name="theAction">
    <input type="hidden" name="timesheetId" value="<jsp:getProperty name="timesheet" property="ID" />">
    <input type="hidden" name="module" value="<%=Module.RESOURCE%>">
    <input type="hidden" name="action" value="<%=request.getAttribute("action")%>">
    <input type="hidden" name="returnTo" value="<%=request.getAttribute("returnTo")%>">
    <input type="hidden" name="returnTo2" value="<%=request.getAttribute("returnTo2")%>">
    <input type="hidden" name="startDate" value="<%=dateRangeStart.getTime()%>">

<channel:channel name="createtimesheet" customizable="false">
    <channel:insert name="newInformation" row="1" column="1" minimizable="false" titleToken="prm.resource.timesheet.label">
    <table width="100%" border="0" align="left" cellpadding="0" cellspacing="0">
        <tr align="left" valign="top">
            <td colspan="4">
            <errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="request"/>
            <%-- Provide a div for server round-trip error messaging --%>
            <div id="errorLocationID" class="errorMessage"></div>
            </td>
        </tr>
        
        <tr align="left" valign="top">
            <td colspan="4">

            </td>
        </tr>

		<tr align="left" valign="middle">
			<td class="fieldRequired" width="24%"><display:get name="prm.resource.timesheet.create.startdate.label"/>&nbsp;</td>
			<td width="25%">
                <input:text elementID="startDateString" name="startDateString" size="10" maxLength="10"  readOnly="true" value="<%=timesheet.getStartDateString()%>" />
			</td>
			<td class="fieldRequired" width="25%"><display:get name="prm.resource.timesheet.create.enddate.label"/>&nbsp;</td>
			<td width="24%">
                <input:text elementID="endDateString" name="endDateString" size="10" maxLength="10"  readOnly="true" value="<%=timesheet.getEndDateString()%>" />
			</td>
		</tr>

		<tr align="left" valign="top">
            <td colspan="4">
				<jsp:include page="include/UpdateAssignments.jsp" flush="true" > 
                    <jsp:param name="readOnly" value="<%=readOnly.booleanValue()%>" />
                </jsp:include>
			</td>
        </tr>

		<tr align="left" valign="top">
            <td colspan="4">
                <tb:toolbar style="action" showLabels="true" width="97%">
                <tb:band name="action">
<%          if(timesheet.getID() != null && timesheet.getTimesheetStatus().equals(TimesheetStatus.DRAFT) && !readOnly.booleanValue()) { 
            //here I can update the timesheet, submit it for approval or cancel it.
%>
                    <tb:button type="submit"/>
                    <tb:button type="update"/>
                    <tb:button type="cancel"/>

<%          } else if(timesheet.getID() != null && timesheet.getTimesheetStatus().equals( TimesheetStatus.SUBMITTED) && !readOnly.booleanValue()) {
            //here one can accept or decline the submitted timesheet
            //this would be meant only for resources managers view
%>
                    <tb:button type="accept"/>
                    <tb:button type="decline"/>
<%
            } else if(timesheet.getID() == null && !readOnly.booleanValue()) { 
            //here I can only create a new time sheet
%>
                    <tb:button type="create"/>
<%          }  %>
                    <tb:button type="back"/>
                </tb:band>
                </tb:toolbar>
			</td>
        </tr>
    </table>
    </channel:insert>
</channel:channel>
</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>