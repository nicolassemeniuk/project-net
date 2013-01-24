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
    info="Discussion" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.schedule.*,
            net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.util.DateFormat,
            net.project.base.finder.*,
            net.project.base.Module,
            net.project.util.Validator,
            net.project.schedule.report.PhaseFilter,
            net.project.process.ProcessBean,
            net.project.gui.html.HTMLOptionList,
            java.util.List,
            java.util.Collections,
            net.project.util.NumberFormat,
            net.project.schedule.report.TaskTypeFilter,
            java.util.Iterator,
            net.project.gui.html.HTMLOption,
            java.util.ArrayList,
            org.apache.log4j.Logger,
            java.math.BigDecimal,
            net.project.util.TimeQuantity"
    
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%@page import="net.project.security.Action"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="roster" class="net.project.resource.RosterBean" scope="session" />
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<% String baseUrl = SessionManager.getJSPRootURL(); %>
<script>
function clearFilters() {
    theForm.showAllTasks.checked = true;
    theForm.showLateTasks.checked = false;
    //theForm.showWithOverAllocatedResources.checked = false;
    theForm.showComingDue.checked = false;
    theForm.showUnassigned.checked = false;
    theForm.showAssignedToUser.checked = false;
    theForm.showOnCriticalPath.checked = false;
    theForm.showShouldHaveStarted.checked = false;
    //theForm.showNotYetStarted.checked = false;
    //theForm.showStartingSoon.checked = false;
    //theForm.showInProgress.checked = false;
    theForm.showStartedAfterPlannedStart.checked = false;
    theForm.startDateFilterStart.value = "";
    theForm.startDateFilterEnd.value = "";
    theForm.endDateFilterStart.value ="";
    theForm.endDateFilterEnd.value ="";

    selectSelect(theForm.type, "<%=Schedule.ALL%>");
    selectSelect(theForm.selectedPhaseID, "");

    selectSelect(theForm.taskNameComparator, "<%=TextComparator.DEFAULT.getID()%>");
    theForm.taskName.value ="";

    selectSelect(theForm.workPercentCompleteComparator, "<%=NumberComparator.DEFAULT.getID()%>");
    theForm.workPercentComplete.value ="";
    var openDiv = document.getElementById("userListOpen");
    var closedDiv = document.getElementById("userListClosed");
    closedDiv.className = 'visible';
    openDiv.className = 'hidden';
}

function allTasksChecked() {
    if (theForm.showAllTasks.checked) {
        theForm.showLateTasks.checked = false;
        //theForm.showWithOverAllocatedResources.checked = false;
        theForm.showComingDue.checked = false;
        theForm.showUnassigned.checked = false;
        theForm.showAssignedToUser.checked = false;
        theForm.showOnCriticalPath.checked = false;
        theForm.showShouldHaveStarted.checked = false;
        //theForm.showNotYetStarted.checked = false;
        //theForm.showStartingSoon.checked = false;
        //theForm.showInProgress.checked = false;
        theForm.showStartedAfterPlannedStart.checked = false;
        var openDiv = document.getElementById("userListOpen");
        var closedDiv = document.getElementById("userListClosed");
        closedDiv.className = 'visible';
        openDiv.className = 'hidden';
    }
}

function taskTypeChecked(checkbox) {
    if (checkbox.checked) {
        theForm.showAllTasks.checked = false;
        if(checkbox.name == "showAssignedToUser") {
            var openDiv = document.getElementById("userListOpen");
            var closedDiv = document.getElementById("userListClosed");
            closedDiv.className = 'hidden';
            openDiv.className = 'visible';
        }
    } else {
        if(checkbox.name == "showAssignedToUser") {
            var openDiv = document.getElementById("userListOpen");
            var closedDiv = document.getElementById("userListClosed");
            closedDiv.className = 'visible';
            openDiv.className = 'hidden';
        }
    }
}

function scheduleProperty(){

	    var link = JSPRootURL+"/schedule/properties/ScheduleProperties.jsp?action=<%=Action.MODIFY%>&module=<%=Module.SCHEDULE%>";
                
        self.location = link;
     
}  
</script>


<%
	roster.setSpace(user.getCurrentSpace());
	roster.load();
    // also, every time we come to this page (when not returning search results), reload the space's instance of the roster also.
	// It might be appropriate to simply make the directory module act on the space's roster instance, but at this time the impact
    // of such a move has not been analyzed.  (PCD: 6/15/2002)
	user.getCurrentSpace().getRoster().reload();

    DateFormat df = SessionManager.getUser().getDateFormatter();
    NumberFormat nf = NumberFormat.getInstance();

    String showFiltersDropDownValue = (String)request.getAttribute("showFiltersDropDown");
    boolean showFilterDropDown = (showFiltersDropDownValue != null && showFiltersDropDownValue.equals("true"));

    FinderFilterList filterList = schedule.getFinderFilterList();
    boolean showLateTasks = filterList.deepSearch("showLateTasks") != null;
    boolean showWithOverAllocatedResources = filterList.deepSearch("showWithOverAllocatedResources") != null;
    boolean showComingDue = filterList.deepSearch("showComingDue") != null;
    boolean showUnassigned = filterList.deepSearch("showUnassigned") != null;
    boolean showAssignedToUser = filterList.deepSearch("showAssignedToUser") != null;
    String assignedUser = request.getParameter("assignedUser");
    if(assignedUser == null)
        assignedUser = user.getID();
    boolean showOnCriticalPath = filterList.deepSearch("showOnCriticalPath") != null;
    boolean showShouldHaveStarted = filterList.deepSearch("showShouldHaveStarted") != null;
    boolean showStartedAfterPlannedStart = filterList.deepSearch("showStartedAfterPlannedStart") != null;
    boolean showAllTasks = !showLateTasks && !showWithOverAllocatedResources && !showComingDue
          && !showUnassigned && !showAssignedToUser && !showOnCriticalPath && !showShouldHaveStarted
          && !showStartedAfterPlannedStart;

    String comingDueTooltip = PropertyProvider.get("prm.schedule.main.taskscomingdue.tooltip", NumberFormat.getInstance().formatNumber(PropertyProvider.getInt("prm.schedule.filters.taskscomingdue.numberofdays.value")));
    String shouldHaveStartedTooltip = PropertyProvider.get("prm.schedule.main.tasksshouldhavestarted.tooltip");

    DateFilter startDateFilter = (DateFilter)filterList.deepSearch("startDateFilter");

    String startDateFilterStart = (startDateFilter != null ? df.formatDate(startDateFilter.getDateRangeStart()) : "");
    String startDateFilterEnd = (startDateFilter != null ? df.formatDate(startDateFilter.getDateRangeFinish()) : "");

    String startDateStartValue = (Validator.isBlankOrNull(startDateFilterStart) ? "" : "value=\""+startDateFilterStart+"\"");
    String startDateEndValue = (Validator.isBlankOrNull(startDateFilterEnd) ? "" : "value=\""+startDateFilterEnd+"\"");


    TaskTypeFilter taskTypeFilter = (TaskTypeFilter)filterList.deepSearch("type");

    boolean allSelected = (taskTypeFilter != null ? !taskTypeFilter.isSelected() : true);
    boolean milestonesSelected = (taskTypeFilter != null ? taskTypeFilter.isLoadMilestones(): false);
    boolean tasksSelected = (taskTypeFilter != null ? taskTypeFilter.isLoadTasks() : false);

    if (milestonesSelected && tasksSelected) {
        allSelected = true;
        milestonesSelected = false;
        tasksSelected = false;
    }

    DateFilter endDateFilter = (DateFilter)filterList.deepSearch("endDateFilter");

    String endDateFilterStart = (endDateFilter != null ? df.formatDate(endDateFilter.getDateRangeStart()) : "");
    String endDateFilterEnd = (endDateFilter != null ? df.formatDate(endDateFilter.getDateRangeFinish()) : "");

    String endDateStartValue = (Validator.isBlankOrNull(endDateFilterStart) ? "" : "value=\""+endDateFilterStart+"\"");
    String endDateEndValue = (Validator.isBlankOrNull(endDateFilterEnd) ? "" : "value=\""+endDateFilterEnd+"\"");


    PhaseFilter pf = (PhaseFilter)filterList.deepSearch("selectedPhaseID");
    List selectedPhase = (pf != null ? pf.getPhaseID() : Collections.EMPTY_LIST);

    List selectedPhaseOptions = new ArrayList();
    for (Iterator it = selectedPhase.iterator(); it.hasNext();) {
        String phaseID = (String) it.next();
        selectedPhaseOptions.add(new HTMLOption(phaseID, ""));
    }

    ProcessBean process = new ProcessBean();
    process.loadProcess(SessionManager.getUser().getCurrentSpace().getID());


    TextFilter taskNameFilter = (TextFilter)filterList.deepSearch("taskName");

    FilterComparator taskNameComparator = (taskNameFilter != null ? taskNameFilter.getComparator() : TextComparator.CONTAINS);
    String taskName = (taskNameFilter != null ? taskNameFilter.getValue() : "");
    String taskNameValue = (!taskName.equals("") ? "value=\""+taskName+"\"" : "");


    NumberFilter workPercentCompleteFilter = (NumberFilter)filterList.deepSearch("workPercentCompleteFilter");

    NumberComparator workPercentCompleteComparator = (NumberComparator)(workPercentCompleteFilter != null ? workPercentCompleteFilter.getComparator() : null);
    String workPercentComplete = (workPercentCompleteFilter != null ? nf.formatNumber(workPercentCompleteFilter.getNumber().floatValue()) : "");
    String workPercentCompleteValue = (!workPercentComplete.equals("") ? "value=\""+workPercentComplete+"\"" : "");

%>
<%-- Apply stylesheet to format task listing rows --%>
<dropDown:dropDown closed="<%=!showFilterDropDown%>" showClosedContentWhenOpen="true">
    <dropDown:closed>
     <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr class="tableFilterContentWB">
            <td width="1%">&nbsp;</td>
            <td width="23%">
              <span class="tableFilterHeaderWB"><display:get name="prm.schedule.main.schedulestartdate.label"/></span>
              <span><%=df.formatDate(schedule.getScheduleStartDate())%><span>
            </td>
            <td width="23%">
                <span class="tableFilterHeaderWB"><display:get name="prm.schedule.main.scheduleenddate.label"/></span>
                <span><%=df.formatDate(schedule.getScheduleEndDate())%><span>
            </td>
            <td width="15%">
                <span class="tableFilterHeaderWB"><display:get name="prm.schedule.main.taskcount.label"/></span>
                <span><%=nf.formatNumber(schedule.getEntries().size())%></span>
            </td>
            <td width="20%">
                <span class="tableFilterHeaderWB"><display:get name="prm.schedule.main.work.label"/></span>
                <span>
                <%
                    TimeQuantity divisor = schedule.getTotalWork();
                    if (divisor.getAmount().signum() == 0) {
                        divisor = new TimeQuantity(1, divisor.getUnits());
                    }

                    Object[] params = new Object[] {
                        schedule.getTotalWorkComplete().toShortString(0,2),
                        schedule.getTotalWork().toShortString(0,2),
                        NumberFormat.getInstance().formatPercent(schedule.getTotalWorkComplete().divide(divisor, 2, BigDecimal.ROUND_HALF_UP).doubleValue())
                    };
                    out.print(PropertyProvider.get("prm.schedule.main.work.value", params));
                %>
                </span>
            </td>
            <td width="15%">
                <span class="tableFilterHeaderWB"><a href="javascript:scheduleProperty()"><display:get name="prm.schedule.main.scheduleproperties.link"/></a> </span>
            </td>
            
            <td width="1%">&nbsp;</td>
        </tr>
      </table>
    </dropDown:closed>
    <dropDown:open>
        <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr class="tableFilterHeader">
            <td><display:get name="prm.schedule.main.filtercheckboxes.header.label"/></td>
            <td colspan="5"></td>
            <td width="1%" class="tableContent">&nbsp;</td>
        </tr>
        <tr class="tableFilterHeader" height="20">
            <td rowspan="4">
                <input type="checkbox" name="showAllTasks" onClick="allTasksChecked();" value="true" <%=(showAllTasks?"checked":"")%>><display:get name="prm.schedule.main.alltasks.label"/><br>
                <input type="checkbox" name="showLateTasks" onClick="taskTypeChecked(this);" value="true" <%=(showLateTasks?"checked":"")%>><display:get name="prm.schedule.main.latetasks.label"/><br>
                <!--<input type="checkbox" name="showWithOverAllocatedResources" onClick="taskTypeChecked(this);" value="true" <%=(showWithOverAllocatedResources?"checked":"")%>><display:get name="prm.schedule.main.taskoverallocatedresources.label"/><br>-->
                <input type="checkbox" name="showComingDue" onClick="taskTypeChecked(this);" value="true" title="<%=comingDueTooltip%>" <%=(showComingDue?"checked":"")%>><span title="<%=comingDueTooltip%>"><display:get name="prm.schedule.main.taskscomingdue.label"/></span><br>
                <input type="checkbox" name="showUnassigned" onClick="taskTypeChecked(this);" value="true" <%=(showUnassigned?"checked":"")%>><display:get name="prm.schedule.main.unassignedtasks.label"/><br>
                <input type="checkbox" name="showAssignedToUser" onClick="taskTypeChecked(this);" value="true" <%=(showAssignedToUser?"checked":"")%>><display:get name="prm.schedule.main.showtasksassignedtouser.label"/><br>
                <dropDown:dropDown closed="<%=!showAssignedToUser%>" showClosedContentWhenOpen="true" divId="userList">
                    <dropDown:closed>
                    </dropDown:closed>
                    <dropDown:open>
                    <table width="100%" cellpadding="0" cellspacing="0" border="0">
                    <tr class="tableFilterHeader">
                        <td align="left">
                            <select name="assignedUser" class="tableFilterControl">
                                <%=roster.getSelectionList(assignedUser)%>
                            </select>
                        </td>
                    </tr>
                    </table>
                    </dropDown:open>
                </dropDown:dropDown>
                <input type="checkbox" name="showOnCriticalPath" onClick="taskTypeChecked(this);" value="true" <%=(showOnCriticalPath?"checked":"")%>><display:get name="prm.schedule.main.tasksonthecriticalpath.label"/><br>
                <input type="checkbox" name="showShouldHaveStarted" onclick="taskTypeChecked(this);" value="true" title="<%=shouldHaveStartedTooltip%>" <%=(showShouldHaveStarted?"checked":"")%>><display:get name="prm.schedule.main.tasksshouldhavestarted.label"/><br>
                <input type="checkbox" name="showStartedAfterPlannedStart" onclick="taskTypeChecked(this);" value="true" <%=(showStartedAfterPlannedStart?"checked":"")%>><display:get name="prm.schedule.main.tasksstartedafterplannedstart.label"/>
            </td>
            <td width="10%"><display:get name="prm.schedule.main.startdatefilter.label"/></td>
            <td>
                <input type="text" name="startDateFilterStart" size="10" maxlength="10" class="tableFilterControl"<%=startDateStartValue%>><util:insertCalendarPopup fieldName="startDateFilterStart" rootURL="<%=SessionManager.getJSPRootURL()%>"/>-
                <input type="text" name="startDateFilterEnd" size="10" maxlength="10" class="tableFilterControl"<%=startDateEndValue%>><util:insertCalendarPopup fieldName="startDateFilterEnd" rootURL="<%=SessionManager.getJSPRootURL()%>"/>
            </td>
            <td align="right"><%=PropertyProvider.get("prm.schedule.main.type.label")%>&nbsp;</td>
            <td align="left">
                <select name="type" class="tableFilterControl">
                    <option value="<%=Schedule.ALL%>"<%=(allSelected ? " selected" : "")%>><%=PropertyProvider.get("prm.schedule.main.type.option.all.name")%></option>
                    <option value="<%=TaskType.MILESTONE.getID()%>"<%=(milestonesSelected ? " selected" : "")%>><%=PropertyProvider.get("prm.schedule.main.type.option.milestone.name")%></option>
                    <option value="<%=TaskType.TASK.getID()%>"<%=(tasksSelected ? " selected" : "")%>><%=PropertyProvider.get("prm.schedule.main.type.option.task.name")%></option>
                </select>
            </td>
            <td rowspan="4" valign="top">
        </tr>
        <tr class="tableFilterHeader">
            <td><display:get name="prm.schedule.main.enddatefilter.label"/></td>
            <td>
                <input type="text" name="endDateFilterStart" size="10" maxlength="10" class="tableFilterControl"<%=endDateStartValue%>><util:insertCalendarPopup fieldName="endDateFilterStart" rootURL="<%=SessionManager.getJSPRootURL()%>"/>-
                <input type="text" name="endDateFilterEnd" size="10" maxlength="10" class="tableFilterControl"<%=endDateEndValue%>><util:insertCalendarPopup fieldName="endDateFilterEnd" rootURL="<%=SessionManager.getJSPRootURL()%>"/>
            </td>
            <td align="right"><%=PropertyProvider.get("prm.schedule.main.phase.label")%>&nbsp;</td>
            <td align="left">
                <select name="selectedPhaseID" class="tableFilterControl">
                    <%=HTMLOptionList.makeHtmlOptionList(process.getPhaseOptions(true), selectedPhaseOptions)%>
                </select>
            </td>
        </tr>
        <tr class="tableFilterHeader">
            <td><display:get name="prm.schedule.main.taskname.label"/></td>
            <td>
                <select name="taskNameComparator" class="tableFilterControl">
                <%=TextComparator.getOptionList(taskNameComparator)%>
                </select>
                <input type="text" name="taskName" size="20" class="tableFilterControl"<%=taskNameValue%>>
            </td>
            <td colspan="2">
                <input type="button" value="<%=PropertyProvider.get("prm.schedule.main.applyfilters.message")%>" class="tableFilterControl" onClick="submitFilters();">
            </td>
            </tr>
            <tr class="tableFilterHeader">
            <td><display:get name="prm.schedule.main.workpercentcomplete.label"/></td>
            <td>
                <select name="workPercentCompleteComparator" class="tableFilterControl">
                <%=NumberComparator.getOptionList(workPercentCompleteComparator)%>
                </select>
                <input type="text" name="workPercentComplete"<%=workPercentCompleteValue%>>
                <input type="button" onClick="theForm.workPercentComplete.value='';" value="<%=PropertyProvider.get("prm.schedule.main.percentcomplete.clear.label")%>" class="tableFilterControl">
            </td>
            <td colspan="2">
                <input type="button" value="<%=PropertyProvider.get("prm.schedule.main.clearfilters.message")%>" class="tableFilterControl" onClick="clearFilters();">
            </td>
        </tr>
        </table>
    </dropDown:open>
</dropDown:dropDown>
