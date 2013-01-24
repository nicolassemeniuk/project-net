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
	
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="assignmentManagerBean" class="net.project.resource.AssignmentManagerBean" scope="request" />
<jsp:useBean id="status" class="java.lang.String" scope="request"/>
<jsp:useBean id="portfolio" class="net.project.portfolio.ProjectPortfolioBean" scope="request"/>
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>
<%
    String returnHereUrl = "/servlet/AssignmentController/PersonalAssignments?module=" + Module.PERSONAL_SPACE;
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<!-- BlogIt Style -->
<template:import type="css" src="/styles/blog.css" />
<template:getSpaceCSS />

<history:history displayHere="false">
    <history:module display='@prm.project.directory.assignments.module.history'
            jspPage='<%=SessionManager.getJSPRootURL() + "/servlet/AssignmentController/PersonalAssignments"%>'
            queryString='<%="module=" + Module.PERSONAL_SPACE%>' />
</history:history>

<script language="javascript">
var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>'; 
</script>
<!-- BlogIt JS -->
<template:import type="javascript" src="/src/blogit.js" />

<script language="javascript">
var theForm;
var isLoaded = false;

// Internationalization message for blog popup
var noSelectionErrMes = '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>';

var blogItFor = 'assignments';
var spaceId = <%= user.getCurrentSpace().getID()%>;
var moduleId = <%= Module.PERSONAL_SPACE%>;

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    isLoaded = true;
    theForm = self.document.forms[0];
}

function cancel() {
    self.document.location = JSPRootURL + "/personal/Main.jsp";
}

function reset() {
    theForm.reset();
    submitFilters();
}

function help() {
	var helplocation=JSPRootURL+"/help/Help.jsp?page=personal_assignments";
	openwin_help(helplocation);
}

function listTimesheet() {
        var url = JSPRootURL + "/resource/ListTimesheets.jsp?module=<%=Module.RESOURCE%>&action=<%=Action.VIEW%>";
        url += "&personID=<%=SessionManager.getUser().getID()%>";

        self.document.location = url;
}

function createTimesheet() {
        var url = JSPRootURL + "/servlet/AssignmentController/CurrentAssignments"+
            "/CreateTimesheet?module=<%=Module.RESOURCE%>&action=<%=Action.CREATE%>";
        url += "&personID=<%=SessionManager.getUser().getID()%>"
        url += "&returnTo=<%=URLEncoder.encode("/servlet/AssignmentController/PersonalAssignments?&module=" + Module.PERSONAL_SPACE+"&action="+Action.VIEW, "UTF-8")%>";

        self.document.location = url;
}

function captureWork() {
   if ((theForm.objectID) && (verifySelectionForField(theForm.objectID, "multiple", '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>'))) {
        var captureWorkSupported = true;

        if (theForm.objectID.length) {
            for (var i = 0; i < theForm.objectID.length; i++) {
				if(theForm.objectID[i].checked == true) {
					captureWorkSupported = eval("theForm.capture_work_" + theForm.objectID[i].value+ ".value") == "1";
					if (!captureWorkSupported) {
						break;
					}
				}
            }
        } else {
            captureWorkSupported = eval("theForm.capture_work_" + theForm.objectID.value+ ".value") == "1";
        }

        if (!captureWorkSupported) {
	        var errorMessage = '<%=PropertyProvider.get("prm.personal.assignments.invalidassignmenttype.message").replaceAll("'", "\\\\'")%>';
			extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
            return;
        }

        var url = JSPRootURL + "/servlet/AssignmentController/CurrentAssignments"+
            "/Update?module=<%=Module.RESOURCE%>&action=<%=Action.VIEW%>";
        url += "&" + formatQueryParameters(theForm.objectID);
        url += "&personID=<%=SessionManager.getUser().getID()%>"
        url += "&returnTo=<%=URLEncoder.encode("/servlet/AssignmentController/PersonalAssignments?&module=" + Module.PERSONAL_SPACE, "UTF-8")%>";

        self.document.location = url;
    }
}

function allAssignmentsChecked() {
    if (theForm.filterAllAssignments.checked) {
        theForm.filterLateAssignments.checked = false;
        theForm.filterAssignmentsComingDue.checked = false;
        theForm.filterShouldHaveStarted.checked = false;
        theForm.filterInProgress.checked = false;
    }
}

function assignmentTypeChecked(checkbox) {
    if (checkbox.checked) {
        theForm.filterAllAssignments.checked = false;
    }
}

function clearFilters() {
    theForm.filterAllAssignments.checked = true;
    theForm.filterLateAssignments.checked = false;
    theForm.filterAssignmentsComingDue.checked = false;
    theForm.filterShouldHaveStarted.checked = false;
    theForm.filterInProgress.checked = false;

    clearSelection(theForm.filterSpace);
    selectSelect(theForm.filterSpace, '<%=user.getID()%>');
    clearSelection(theForm.filterType);
    selectSelect(theForm.filterType, '<%=AssignmentType.TASK.getHtmlOptionValue()%>');

    selectSelect(theForm.filterStatus, '');
    theForm.assignmentDateStart.value = '';
    theForm.assignmentDateEnd.value = '';
    selectSelect(theForm.percentCompleteComparator, '<%=NumberComparator.LESS_THAN.getID()%>');
    theForm.percentComplete.value = '<%=NumberFormat.getInstance().formatNumber(100)%>';
    selectSelect(theForm.nameComparator, '<%=TextComparator.CONTAINS.getID()%>');
    theForm.filterName.value = '';
}

function submitFilters() {
    theForm.submit();
}

function showResourceAllocation(personID, startDate) {
    var url = '<%=SessionManager.getJSPRootURL()+"/resource/ResourceAllocations.jsp?module=140&personID="%>'+
        personID + '&startDate=' + startDate;

    openwin_large('resource_allocation', url);
}

function appendToURL(url, field, fieldName) {
    if (field) {
        if (!field.length) {
            url += "&" + fieldName + "=" + field.value;
        } else {
            //Add all of the values of the field to the url
            for (var i = 0; i < field.length; i++) {
                if (field[i].selected) {
                    url += "&" + fieldName + "=" + field[i].value;
                }
            }
        }
    }

    return url;
}

function appendCheckboxToURL(url, field, fieldName) {
    if (field && field.checked) {
        url += "&" + fieldName + "=" + field.value;
    } else {
        url += "&" + fieldName + "=false";
    }

    return url;
}
</script>

<%
    String showFiltersDropDownValue = (String)request.getAttribute("showFiltersDropDown");
    boolean showFilterDropDown = (showFiltersDropDownValue != null && showFiltersDropDownValue.equals("true")) || showFiltersDropDownValue == null;
    String percentCompleteValue = " value=\""+request.getAttribute("percentComplete")+"\"";
    String percentCompleteComparator = (String)request.getAttribute(PersonalAssignmentsFilterHandler.PERCENT_COMPLETE_COMPARATOR_NAME);

    Collection selectedTypes = (Collection)request.getAttribute(PersonalAssignmentsFilterHandler.ASSIGNMENT_TYPES_NAME);
    boolean lateAssignmentsFilter = ((Boolean)request.getAttribute(PersonalAssignmentsFilterHandler.LATE_ASSIGNMENTS_NAME)).booleanValue();
    boolean assignmentsComingDueFilter = ((Boolean)request.getAttribute(PersonalAssignmentsFilterHandler.COMING_DUE_NAME)).booleanValue();
    boolean shouldHaveStartedFilter = ((Boolean)request.getAttribute(PersonalAssignmentsFilterHandler.SHOULD_HAVE_STARTED_NAME)).booleanValue();
    boolean inProgressFilter = ((Boolean)request.getAttribute(PersonalAssignmentsFilterHandler.IN_PROGRESS_NAME)).booleanValue();
    String[] spaceIDs = (String[])request.getAttribute(PersonalAssignmentsFilterHandler.SPACE_NAME);
    String[] statusIDs = (String[])request.getAttribute(PersonalAssignmentsFilterHandler.STATUS_NAME);
    //replace back to allOpen as the handler class inserted the open ids instead of allOpen
    if(statusIDs.length == 2 && Arrays.asList(statusIDs).contains(AssignmentStatus.ACCEPTED.getID()) && Arrays.asList(statusIDs).contains(AssignmentStatus.IN_PROCESS.getID())) {
        statusIDs = new String[] {"allOpen"};
    }

    String startDateFilter = (String)request.getAttribute(PersonalAssignmentsFilterHandler.ASSIGNMENT_START_NAME);
    String startDateFilterValue = (Validator.isBlankOrNull(startDateFilter) ? "" : " value=\""+startDateFilter+"\"");
    String endDateFilter = (String)request.getAttribute(PersonalAssignmentsFilterHandler.ASSIGNMENT_END_NAME);
    String endDateFilterValue = (Validator.isBlankOrNull(endDateFilter) ? "" : " value=\""+endDateFilter+"\"");
    String assignmentName = (String)request.getAttribute(PersonalAssignmentsFilterHandler.ASSIGNMENT_NAME);
    String assignmentNameValue = assignmentName != null ? "value=\""+assignmentName+"\"" : "";
    String assignmentComparator = (String)request.getAttribute(PersonalAssignmentsFilterHandler.ASSIGNMENT_COMPARATOR_NAME);
    if (Validator.isBlankOrNull(assignmentComparator)) {
        assignmentComparator = TextComparator.CONTAINS.getID();
    }

    boolean allAssignmentsChecked = (!lateAssignmentsFilter && !assignmentsComingDueFilter && !shouldHaveStartedFilter && !inProgressFilter);
    String allAssignmentsValue = (allAssignmentsChecked ? " checked" : "");

    String startOfMonth = (String)request.getAttribute("allocationSummaryStartTime");
%>

</head>
<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.personal.nav.assignment">
	<tb:setAttribute name="leftTitle">
		<history:history />
	</tb:setAttribute>
	<tb:band name="standard">
		 <display:if name="@prm.blog.isenabled">
			<tb:button type="blogit" />
		</display:if>
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>
<form method="post" action="<%=SessionManager.getJSPRootURL() + "/servlet/AssignmentController/PersonalAssignments/Filter"%>">
<input type="hidden" name="action" value="<%=Action.VIEW%>">
<input type="hidden" name="module" value="<%=Module.PERSONAL_SPACE%>">

<errors:show clearAfterDisplay="true"/>
<div id="errorLocationID" class="errorMessage"></div>

<channel:channel customizable="false" name="personal_assignments">
    <channel:insert name="assignments_channel" closeable="false" minimizable="false">
        <channel:button type="calendar" labelToken="prm.personal.assignments.showallocationsummary.label" href='<%="javascript:showResourceAllocation("+user.getID()+","+startOfMonth+");"%>'/>
        <display:if name="@prm.resource.timesheet.isenabled">
            <channel:button type="create" labelToken="prm.personal.assignments.showtimesheet.label" href="javascript:createTimesheet();" />
            <channel:button type="properties" labelToken="prm.personal.assignments.listtimesheets.label" href="javascript:listTimesheet();" />
        </display:if>
        <!-- Assignment Filters -->
        <dropDown:dropDown closed="<%=!showFilterDropDown%>" showClosedContentWhenOpen="true" >
            <dropDown:closed>
                <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td width="1%">&nbsp;</td>
                    <td align="center" width="20" class="tableFilterContent">
                        <dropDown:image/>
                    </td>
                    <td class="tableFilterContent">
                        <span class="tableFilterHeader"><display:get name="prm.personal.assignments.totalassignments.label"/></span>
                        &nbsp;
                        <%=assignmentManagerBean.getAssignments().size()%>
                    </td>
                    <td width="1%">&nbsp;</td>
                </tr>
                </table>
            </dropDown:closed>
            <dropDown:open>
                <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <%-- Row 1 --%>
                <tr>
                    <td width="1%">&nbsp;</td>
                    <td class="tableFilterHeader"><display:get name="prm.project.directory.assignments.spacefilter.label"/></td>
                    <td class="tableFilterContent" rowspan="3" valign="top">
                        <select name="filterSpace" class="tableFilterControl" size="4" multiple style="width:200px">
                            <option value=""<%=(spaceIDs.length == 0 || Arrays.asList(spaceIDs).contains(""))?" selected": ""%>><display:get name="prm.personal.assignments.option.all.name"/></option>
                            <%=HTMLOptionList.makeHtmlOptionList(portfolio, spaceIDs)%>
                        </select>
                        <input type="hidden" name="spaceComboSelectedIndex" value="0">
                    </td>
                    <td class="tableFilterContent">
                        <input type="checkbox" name="filterAllAssignments" onClick="allAssignmentsChecked();" class="tableFilterControl"<%=allAssignmentsValue%> value="true">
                        <span class="tableFilterHeader"><display:get name="prm.personal.assignments.allassignments.label"/></span>
                    </td>
                    <td class="tableFilterContent">
                        <span class="tableFilterHeader"><display:get name="prm.project.directory.assignments.statusfilter.label"/></span>
                        &nbsp;
                        <select name="filterStatus" class="tableFilterControl">
                          <option value=""<%=(statusIDs.length == 0 || Arrays.asList(statusIDs).contains(""))?" selected":""%>><display:get name="prm.personal.assignments.statusfilter.option.all.name"/></option>
                          <option value="allOpen"<%=Arrays.asList(statusIDs).contains("allOpen")?" selected":""%>><display:get name="prm.personal.assignments.statusfilter.option.allopen.name"/></option>
                          <%=HTMLOptionList.makeHtmlOptionList(AssignmentStatus.getValidStatuses(), statusIDs)%>
                        </select>
                    </td>
                    <td width="1%">&nbsp;</td>
                </tr>
                <%-- Row 2 --%>
                <tr>
                    <td></td>
                    <td class="tableFilterContent"></td>
                    <td class="tableFilterContent">
                        <input type="checkbox" name="filterLateAssignments" onClick="assignmentTypeChecked(this);" class="tableFilterControl" <%=(lateAssignmentsFilter?"CHECKED":"")%> value="true"><span class="tableFilterHeader"><display:get name="prm.personal.assignments.lateassignments.label"/></span>
                    </td>
                    <td class="tableFilterContent">
                        <span class="tableFilterHeader"><display:get name="prm.personal.assignments.assigneddates.label"/></span>
                        <input type="text" name="assignmentDateStart" size="10" maxlength="10" class="tableFilterControl"<%=startDateFilterValue%>><util:insertCalendarPopup fieldName="assignmentDateStart" rootURL="<%=SessionManager.getJSPRootURL()%>"/>-
                        <input type="text" name="assignmentDateEnd" size="10" maxlength="10" class="tableFilterControl"<%=endDateFilterValue%>><util:insertCalendarPopup fieldName="assignmentDateEnd" rootURL="<%=SessionManager.getJSPRootURL()%>"/>
                    </td>
                </tr>
                <%-- Row 3 --%>
                <tr>
                    <td></td>
                    <td class="tableFilterContent"></td>
                    <td class="tableFilterContent">
                        <input type="checkbox" name="filterAssignmentsComingDue" onClick="assignmentTypeChecked(this);" class="tableFilterControl" <%=(assignmentsComingDueFilter ? "CHECKED":"")%> value="true"><span class="tableFilterHeader"><display:get name="prm.personal.assignments.assignmentscomingdue.label"/></span>
                    </td>
                    <td class="tableFilterContent">
                        <span class="tableFilterHeader"><display:get name="prm.personal.assignments.percentcomplete.label"/></span>
                        <select name="percentCompleteComparator" class="tableFilterControl">
                        <%=NumberComparator.getOptionList(NumberComparator.getForID(percentCompleteComparator == null ? NumberComparator.LESS_THAN.getID() : percentCompleteComparator))%>
                        </select>
                        <input type="text" name="percentComplete" size="3" <%=percentCompleteValue%> class="tableFilterControl">
                        <input type="button" onClick="theForm.percentComplete.value='';" value="<%=PropertyProvider.get("prm.personal.assignments.percentcomplete.clear")%>" class="tableFilterControl">
                    </td>
                </tr>
                <%-- Row 4 --%>
                <tr>
                    <td></td>
                    <td class="tableFilterHeader"><display:get name="prm.personal.assignments.assignmenttype.label"/></td>
                    <td class="tableFilterContent" rowspan="3" valign="top">
                        <select name="filterType" size="4" class="tableFilterControl" multiple style="width:200px">
                            <option value="all"<%=(selectedTypes != null && selectedTypes.size() > 0 ? "" : " selected")%>><display:get name="prm.personal.assignments.alltypes.label"/></option>
                            <%=HTMLOptionList.makeHtmlOptionList(AssignmentType.ALL, selectedTypes)%>
                        </select>
                    </td>
                    <td class="tableFilterContent">
                        <input type="checkbox" name="filterShouldHaveStarted" onClick="assignmentTypeChecked(this);" class="tableFilterControl" <%=(shouldHaveStartedFilter?"CHECKED":"")%> value="true"><span class="tableFilterHeader"><display:get name="prm.personal.assignments.shouldhavestarted.label"/></span>
                    </td>
                    <td class="tableFilterContent">
                        <span class="tableFilterHeader"><display:get name="prm.personal.assignments.name.label"/></span>
                        <select name="filterNameComparator" class="tableFilterControl">
                        <%=TextComparator.getOptionList(TextComparator.getForID(assignmentComparator))%>
                        </select>
                        <input type="text" name="filterName" size="20" class="tableFilterControl"<%=assignmentNameValue%>>
                    </td>
                </tr>
                <%-- Row 5 --%>
                <tr>
                    <td></td>
                    <td class="tableFilterContent"></td>
                    <td class="tableFilterContent">
                        <input type="checkbox" name="filterInProgress" onClick="assignmentTypeChecked(this);" class="tableFilterControl"<%=(inProgressFilter?"CHECKED":"")%> value="true"><span class="tableFilterHeader"><display:get name="prm.personal.assignments.inprogress.label"/></span>
                    </td>
                    <td class="tableFilterContent" rowspan="2" align="middle">
                        <input type="button" value="<%=PropertyProvider.get("prm.personal.assignments.clearfilters.label")%>" onClick="clearFilters();">&nbsp;<input type="button" id="applyFilters" value="<%=PropertyProvider.get("prm.personal.assignments.applyfilters.label")%>" onClick="submitFilters();">
                    </td>
                </tr>
                <%-- Row 6 --%>
                <tr>
                    <td></td>
                    <td class="tableFilterContent"></td>
                    <td class="tableFilterContent"></td>
                </tr>
                </table>
            </dropDown:open>
        </dropDown:dropDown>
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
        </table>
    </channel:insert>
</channel:channel>

<tb:toolbar style="action" showLabels="true" width="100%" bottomFixed="true">
    <tb:band name="action" />
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
