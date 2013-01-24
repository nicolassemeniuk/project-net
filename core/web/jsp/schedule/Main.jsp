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
    info="Schedule"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.schedule.*,
            net.project.schedule.TaskType,
            net.project.security.*,
            net.project.channel.*,
            net.project.security.SessionManager,
            net.project.base.Module,
            java.util.Iterator,
            net.project.util.ErrorDescription,
            java.util.Date,
            net.project.util.TimeQuantityUnit,
            net.project.util.NumberFormat,
            java.util.Map,
            net.project.util.Validator,
            net.project.gui.html.HTMLOptionList,
            java.util.Collection,
            net.project.schedule.mvc.handler.taskedit.Helper,
            net.project.base.finder.*,
            java.net.URLEncoder,
            net.project.resource.PersonProperty,
            net.project.resource.PersonPropertyGlobalScope"
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>

<%!
String baseUrl = SessionManager.getJSPRootURL();
%>
 
<security:verifyAccess action="view"
                       module="<%=net.project.base.Module.SCHEDULE%>" />

<%
    NumberFormat nf = NumberFormat.getInstance();
    // bfd-2330 by Avinash Bhamare on 10th Feb 2006
    //schedule.clear();

    // Get the hierarchy view and set value in schedule
    String hierarchyView = request.getParameter("hierarchyView");
    Integer sessionHierarchyView = (Integer)session.getAttribute("workspaceHierarchyView");

    if (hierarchyView != null && !hierarchyView.equals("")) {
        schedule.setHierarchyView(Integer.parseInt(hierarchyView));
        session.setAttribute("workspaceHierarchyView", new Integer(hierarchyView));
    } else if (sessionHierarchyView != null) {
        schedule.setHierarchyView(sessionHierarchyView.intValue());
        hierarchyView = String.valueOf(sessionHierarchyView);
    } else {
        schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
        hierarchyView = Schedule.HIERARCHY_VIEW_EXPANDED+"";
    }

    schedule.clearFinderFilterList();
    schedule.clearFinderListener();

    String workspaceFilterListSpaceID = (String)session.getAttribute("workspaceFilterListSpaceID");

    if (workspaceFilterListSpaceID != null && workspaceFilterListSpaceID.equals(user.getCurrentSpace().getID())) {
        FinderFilterList list = (FinderFilterList)session.getAttribute("workspaceFilterList");
        if (list!=null) {
            schedule.setFinderFilterList(list);
        }
        FinderListener listener = (FinderListener)request.getAttribute("taskFinderListener");
        if (listener!=null) {
            schedule.setFinderListener(listener);
        }
    }

    schedule.setSpace(user.getCurrentSpace());

    // is the a specified column sort order in the request?
    String order = request.getParameter("order");
    if (order != null) {
        schedule.setOrder(order);
    }

    //Load the schedule and the schedule entries
    schedule.loadAll();
    if(schedule.getWarnings() == 1) {
        errorReporter.addWarning(PropertyProvider.get("prm.schedule.main.dirty.warning.message"));
    }

    if (schedule.getHierarchyView() == Schedule.HIERARCHY_VIEW_GANTT) {
        response.sendRedirect(baseUrl+"/schedule/gantt/GanttWrap.jsp?module=" + Module.SCHEDULE);
    } else { // Closed at end of page

    //Determine what default values should be added into the quick task add form
    String taskName, work, workUnits, startTimeString, endTimeString;
    if (errorReporter.errorsFound()) {
        //We are returning to this page because there was an error with quickadd
        Map submittedValues = errorReporter.getParameters();
        taskName = (String)submittedValues.get("name");
        work = (String)submittedValues.get("work");
        workUnits = (String)submittedValues.get("workUnits");
        startTimeString = (String)submittedValues.get("startTimeString");
        endTimeString = (String)submittedValues.get("endTimeString");
    } else {
        //Initial visit to the page
        taskName = "";
        work = "";
        workUnits = String.valueOf(TimeQuantityUnit.HOUR.getUniqueID());
        startTimeString = user.getDateFormatter().formatDate(new Date());
        endTimeString = user.getDateFormatter().formatDate(new Date());
    }
    PersonProperty property = new PersonProperty();
	property.setScope(new PersonPropertyGlobalScope(user));
	String[] pageSize = property.get("prm.global.project.workplan.pagesize", "pageSize");
	int size = 0;
	if(pageSize.length == 0){
		size = 100;
	} else {
		size = Integer.parseInt(pageSize[0]);
	}

	session.setAttribute("schedule", schedule);		
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<meta http-equiv="expires" content="0">
<%-- Import CSS --%>
<template:import type="css" src="/styles/colors.css" />
<template:import type="css" src="/styles/schedule.css" />
<template:import type="css" src="/styles/blog.css" />
<template:import type="css" src="/src/extjs/resources/css/TreeGrid.css" />
<template:import type="css" src="/styles/wiki.css" />
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:getSpaceJS />
<template:import type="javascript" src="/src/PopupTooltip.js"/>
<template:import type="javascript" src="/src/dhtml/PopupWindow.js" />
<template:import type="javascript" src="/src/dhtml/tree.js" />
<template:import type="javascript" src="/src/dhtml/xmlrequest.js" />
<template:import type="javascript" src="/src/workCapture.js" />
<template:import type="javascript" src="/src/wiki.js" />

<style type="text/css">
    #dateRangeID {        
        display: none;
        position:absolute;
        border-style:none;
    }
    a.dateAnchor:link {
        color: black;
        text-decoration: none;
    }
    a.dateAnchor:visited {
        color: black;
        text-decoration: none;
    }
    a.dateAnchor:hover {
        color: inherit;
        text-decoration: underline;
    }
    
</style>


<script language="javascript">
    var theForm;
    var ajaxForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var decimalSeparator = '<%=nf.getDecimalSeparator()%>';
    var phaseWindow;
    var percentageWindow;
    var resourcesWindow;
    var isFlatView = true;
    var isIndentedView = true;
    var moduleId = <%=Module.SCHEDULE%>;
    var spaceId = '<%=user.getCurrentSpace().getID()%>'; 
	var blogItFor = 'workPlan'+'<%=user.getCurrentSpace().getSpaceType().getName()%>';
	var objectType = 'task';
	var wikiItFor = 'workPlan'+'<%=user.getCurrentSpace().getSpaceType().getName()%>';
	var userId = '<%=SessionManager.getUser().getID()%>';	// added for wiki integration
	

    // Internationalization message
    var noSelectionErrMes = '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>';
    var oneOrZeroSelectionErrMes = '<%=PropertyProvider.get("prm.schedule.main.verifyselection.oneorzero.message")%>';
    var multOnlySelectionErrMes = '<%=PropertyProvider.get("prm.global.javascript.verifyselection.multipleselectionrequired.error.message")%>';
    var adjoiningSelectionErrMes = '<%=PropertyProvider.get("prm.global.javascript.verifyadjoiningselection.error.message")%>';
    var firstTaskIndentMessage = '<%=PropertyProvider.get("prm.schedule.main.cannotindentfirsttask.error.message")%>';
    var indentFlatViewMessage = '<display:get name="prm.schedule.main.indentnotallowedforflatview.message"/>';
    var unIndentFlatViewMessage = '<display:get name="prm.schedule.main.unindentnotallowedforflatview.message"/>';
    var scheduleMultSelectMessage = '<%=PropertyProvider.get("prm.schedule.main.multiselection.error.message")%>';
    var topOfListMessage = '<%=PropertyProvider.get("prm.schedule.main.taskattopoflist.error.message")%>';
    var bottomOfListMessage = '<%=PropertyProvider.get("prm.schedule.main.taskatbottomoflist.error.message")%>';
    var notAllowedForImportedMessage = '<%=PropertyProvider.get("prm.schedule.taskview.resource.notallowedforimportedtask.message")%>';
    var notAllowedForImportedMessage1 = '<%=PropertyProvider.get("prm.schedule.taskview.updatepercent.notallowedforimportedtask.message")%>';
    var notAllowedForImportedMessage2 = '<%=PropertyProvider.get("prm.schedule.taskview.capturework.notallowedforimportedtask.message")%>';
    var noModifiedTasksMsg = '<%=PropertyProvider.get("prm.schedule.modified.norecords.message")%>';
    var taskupErrorMsg='<%=PropertyProvider.get("prm.schedule.main.taskup.invalid.message")%>';

    var expandAllTitle = '<%=PropertyProvider.get("all.global.toolbar.channel.expandall")%>';
    var collapseAllTitle = '<%=PropertyProvider.get("all.global.toolbar.channel.collapseall")%>';

    var flatViewName = '<%=PropertyProvider.get("prm.schedule.main.view.option.flat.name")%>';
	var indentedViewName = '<%=PropertyProvider.get("prm.schedule.main.view.option.indented.name")%>';
	var schedulePageSize = parseInt(<%=size%>);
	var showResultForApplyFilter = false ;
	
function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
    theForm = self.document.forms[0];
    isFlatView = ('<%=schedule.getHierarchyView()%>' == '<%=Schedule.HIERARCHY_VIEW_FLAT%>');
    isIndentedView = ('<%=schedule.getHierarchyView()%>' == '<%=Schedule.HIERARCHY_VIEW_EXPANDED%>');
    focus(theForm, "name");
    if(typeof setupBeta == 'function') {
        setupBeta();
        ajaxForm = new Ext.form.BasicForm(theForm);
    }
    isLoaded = true;
}

function cancel() {
    var theLocation=JSPRootURL+"/project/Dashboard";
    self.location = theLocation;
}

function reset() {
    var theLocation=JSPRootURL+"/schedule/Main.jsp?module=<%=Module.SCHEDULE%>";
    self.location = theLocation;
}

function importMSP() {
    var theLocation=JSPRootURL+"/servlet/ImportController/ChooseFile?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>";
    self.location = theLocation;
}

function exportMSP() {
    var theLocation=JSPRootURL+"/servlet/ImportController/ExportXml?module=<%=Module.SCHEDULE%>&action=<%=Action.MODIFY%>";
    self.location = theLocation;
}

function create() {
    if (verifySelectionPlan(theForm, 'oneorzero', oneOrZeroSelectionErrMes)) {
        var theLocation=JSPRootURL+"/servlet/ScheduleController/TaskCreate?action=<%=Action.CREATE%>&module=<%=Module.SCHEDULE%>";
         var val = scheduleView.getSelectedRowId();
        if (val && val > -1) {
            theLocation += "&createAfter="+val;
        }
        self.location = theLocation;
    }
}

function modify() {
    var id = getSingleSelected(); // Container for the selected Task ID 
    if(id == -1){ // Some error happened
        return;
    }
    // Task Edit security handled within TaskEdit page
    var link = JSPRootURL+"/servlet/ScheduleController/TaskEdit?action=<%=Action.VIEW %>"
                +"&id="+id
                +"&module=<%=Module.SCHEDULE%>";
    self.location = link;
}

// Method to generate gantt pdf file
function generateGanntPDF() {
	var link = JSPRootURL+"/servlet/GanttPDF?module=<%=Module.SCHEDULE%>";
    self.location = link;
}

// Returns the task ID
function getSingleSelected(){
    // Selected records are driven by a HTML Select object 
    if (verifySelectionPlan(theForm, 'single', noSelectionErrMes)) {
        // Get value from HTML Select object
        return scheduleView.getSelectedRowId();
    } else {
        return -1;
    }
}

function verifySelectionPlan(theForm, type, noSelectionErrMes, multiSelectionErrMes, multiOnlyErrMes) {
    var retval = false;
    var errorMsg
    if (scheduleView && scheduleView.getTreeGrid().getStore().getCount() == 0 && type != "oneorzero"){
	    extAlert(errorTitle, '<display:get name="prm.schedule.main.noitemsinlist.errormessage" />' , Ext.MessageBox.ERROR);
    	return false;
    }
    if (!noSelectionErrMes) noSelectionErrMes =  "Please select an item from the list";
    if (!multiSelectionErrMes) multiSelectionErrMes = "This operation only supports single item selection.  Please select only one item from the list";
    if (!multiOnlyErrMes) multiOnlyErrMes = "This operation requires that you select at least two items.";
    var numElements = scheduleView.getSelectionModel().getCount();
    if (type == "multiple" || type == "multi") {
      if (numElements < 1) errorMsg = noSelectionErrMes; else retval = true;
    } else if (type == "multionly" || type == "multipleonly") {
      if (numElements < 2) errorMsg = multiOnlyErrMes; else retval = true;
    } else if (type == "oneorzero") {
      if (numElements > 1) errorMsg = noSelectionErrMes; else retval = true;
    } else {
      if (numElements < 1) errorMsg = noSelectionErrMes; else if (numElements > 1) errorMsg = multiSelectionErrMes; else retval = true;
    } 
    if (!retval){ 
    	executed = false;
    	extAlert(errorTitle, errorMsg , Ext.MessageBox.ERROR)
    };
    return retval;
}

function linkTasks() {
    if (verifySelectionPlan(theForm, 'multionly', '', '', multOnlySelectionErrMes)) {
        theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
        theForm.theAction.value = '/MainProcessing/LinkTasks';
        theForm.action.value = "<%=Action.MODIFY%>";
        ajaxForm.submit(onSubmit);
    }
}

function unlinkTasks() {
    if (verifySelectionPlan(theForm, 'multionly', '', '', multOnlySelectionErrMes)) {
        theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
        theForm.theAction.value = "/MainProcessing/UnlinkTasks";
        theForm.action.value = "<%=Action.MODIFY %>";
        ajaxForm.submit(onSubmit);
    }
}

function changeSelection() {
    changeCheckedState(self.document.forms[0].selected, theForm.changeCheckedState.checked);
}

function toggleSelection() {
    toggleCheckedState(self.document.forms[0].selected, theForm.changeCheckedState);
}

function recalculate() {
	if(document.getElementById("warningId") != null){
		document.getElementById("warningId").innerHTML = "";
    }
    theForm.theAction.value = "/MainProcessing/Recalculate";
    theForm.action.value = "<%=Action.MODIFY%>";
    ajaxForm.submit(onSubmit);
}

function indent() {
    // This action is not available for Flat views.
    if(!isFlatView){
        // Ensure one or more items selected and they are adjacent items
        if (verifySelectionPlan(theForm, 'multi', noSelectionErrMes) && verifyAdjoiningSelectionPlan(theForm, adjoiningSelectionErrMes)) {
            var firstIndex;
            var ids = scheduleView.getSelectedRowIds();
            var records = scheduleView.getSelectionModel().getSelections();
            firstIndex = scheduleView.getRecordById(ids[0]).data.sequence - 1; 
            if (firstIndex == 0) {
                extAlert(errorTitle, firstTaskIndentMessage , Ext.MessageBox.ERROR);
                return;
            }
            var minIndexRecord = getNodeWithMinIndex(records);
            var parentRecord = scheduleView.getStore().getNodePrevSibling(minIndexRecord);
            theForm.theAction.value = "/MainProcessing/Indent";
            theForm.parentTask.value = parentRecord.id;
            theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
            theForm.action.value = "<%=Action.MODIFY%>";
            ajaxForm.submit(onSubmit);
        }
    } else{
        extAlert(errorTitle, indentFlatViewMessage , Ext.MessageBox.ERROR);
        return;
    }
}
//finds node having minimum index
function getNodeWithMinIndex(records){
	if(records.length > 1){
		var min = records[0];
		for (var iterator = 1; iterator < records.length; iterator++) {
			if (parseInt(min.data.sequence) > parseInt(records[iterator].data.sequence)) {
				min = records[iterator];
			}
		}
		return min;
	}
	else {
		return records[0];
	}
}	
	

function verifyAdjoiningSelectionPlan(theForm, adjoiningSelectionErrMes) {
    if (!adjoiningSelectionErrMes) adjoiningSelectionErrMes = "You may only select items which are next to each other.";
    var selectedRecords = scheduleView.getSelectedRowIdsOnly();
    var retval = true;
    if(selectedRecords.length >= 2) {
        for(var i = 0; i < selectedRecords.length-1; i++) {
            var id1 = selectedRecords[i];
            var id2 = selectedRecords[i+1];
            var r1 = scheduleView.getRecordById(id1);
            var r2 = scheduleView.getRecordById(id2);
            if(!r1 || !r2 || r1.data.sequence + 1 != r2.data.sequence) {
                retval = false;
                break;
            }
        }
    } 
    if (!retval) extAlert(errorTitle, adjoiningSelectionErrMes , Ext.MessageBox.ERROR);
    return retval;
}

function unindent() {
    // This action is not available for Flat views
    if(!isFlatView){
        // Ensure one or more tiems selected
        if (verifySelectionPlan(theForm, 'multi', noSelectionErrMes)) {
            theForm.theAction.value = "/MainProcessing/Unindent";
            theForm.action.value = "<%=Action.MODIFY%>";
            theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
            ajaxForm.submit(onSubmit);
        }
    } else{
        extAlert(errorTitle, unIndentFlatViewMessage , Ext.MessageBox.ERROR);
        return;
    }
    
}

function taskup() {
    // Ensure only one item selected
    if (verifySelectionPlan(theForm, 'single', noSelectionErrMes, scheduleMultSelectMessage)) {
	    var record = scheduleView.getTreeGrid().getSelectionModel().getSelected();
	    var allowUp = true;
	    
	    if(scheduleView.getTreeGrid().getStore().getNodeParent(record)){
			allowUp = scheduleView.getTreeGrid().getStore().hasPrevSiblingNode(record);
	    }
        var firstIndexChecked = !scheduleView.getSelectionModel().hasPrevious(); 
        if (firstIndexChecked) {
            extAlert(errorTitle, topOfListMessage , Ext.MessageBox.ERROR);
            return;
        }
        if(allowUp){
	        theForm.theAction.value = "/MainProcessing/TaskUp";
	        theForm.action.value = "<%=Action.MODIFY%>";
	        theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
	        ajaxForm.submit(onSubmit);
        } else {
            extAlert(errorTitle, taskupErrorMsg , Ext.MessageBox.ERROR);
        	
        }
    }

}

function taskdown() {
    // Ensure only one item selected
    if (verifySelectionPlan(theForm, 'single', noSelectionErrMes, scheduleMultSelectMessage)) {
        var lastIndexChecked = !scheduleView.getSelectionModel().hasNext(); 
        if (lastIndexChecked) {
            extAlert(errorTitle, bottomOfListMessage , Ext.MessageBox.ERROR);
            return;
        }
        theForm.theAction.value = "/MainProcessing/TaskDown";
        theForm.action.value = "<%=Action.MODIFY%>";
        theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
        ajaxForm.submit(onSubmit);
    }

}

function remove() {
    if (verifySelectionPlan(theForm, "multiple", noSelectionErrMes)) {
         var idval = scheduleView.getSelectedRowId();

         resourcesWindow = openwin_dialog('deletetask_dialog<%= user.getCurrentSpace().getID() %>', 'about:blank', 350, 450, 'yes');
         theForm.theAction.value = "/TaskList/DeleteTaskDialog";
         theForm.action.value = "<%= net.project.security.Action.DELETE %>";
         theForm.id.value = idval;
         theForm.target = "deletetask_dialog<%= user.getCurrentSpace().getID() %>";
         theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
         theForm.submit();
         theForm.target = "_self";
   }

}

function properties() {
    if (verifySelectionPlan(theForm, 'single', noSelectionErrMes)) {
        var val = scheduleView.getSelectedRowId();
        var link;
        if (val && val > -1) {
            link = JSPRootURL+"/servlet/ScheduleController/TaskView?action=<%=Action.VIEW %>"
                +"&id="+val
                +"&module=<%=Module.SCHEDULE%>";
            self.location = link;
        }
    }
}

function search() { 
    self.document.location = JSPRootURL + "/project/SearchProject.jsp?otype=<%=net.project.base.ObjectType.TASK%>&module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; 
}

function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=schedule_main";
    openwin_help(helplocation);
}

function notify(){
    var id = -1;
    if (verifySelectionPlan(theForm, 'oneorzero', noSelectionErrMes)) {
        // Get value from HTML Select object
        id = scheduleView.getSelectedRowId();
    } 
    if(id == -1){ // Some error happened
		    var link = JSPRootURL+"/notification/CreateSubscription1.jsp?action=<%=Action.MODIFY %>"
		                +"&module=<%=Module.DOCUMENT%>"
						+"&isCreateType=1"
						+"&objectType=task";
		    var notify_win = openwin_notification(link);
		    notify_win.focus();       
    } else{
		    var link = JSPRootURL+"/notification/CreateSubscription1.jsp?action=<%=Action.MODIFY %>"
		                +"&selected="+id
		                +"&module=<%=Module.DOCUMENT%>";
		    var notify_win = openwin_notification(link);
		    notify_win.focus();
	}
}

function security(){
    var id = getSingleSelected(); // Container for the selected Task ID 
    if(id == -1){ // Some error happened
        return;
    }
    if (!security){
       var security = openwin_security("security");
    }
    if (security) {
       theForm.theAction.value = "/MainProcessing/ModifyPermissions";
       theForm.target = "security";
       theForm.action.value = "<%=Action.MODIFY_PERMISSIONS %>";
       theForm.id.value = id;
       theForm.submit();
       theForm.target = '_self';
    }
}

function changeView(order) {
    var viewType = order;
    if(viewType != 2) {
        Ext.Ajax.request({
	 		url: JSPRootURL+'/ajax/schedule/WorkplanAction',
			params: {viewType : viewType},
			method: 'POST',
			success: function(response, options){
                if(options.params.viewType == 0) {
                    isFlatView = true;
                    isIndentView = false;
                } else if(options.params.viewType == 1) {
                    isFlatView = false;
                    isIndentView = true;
                }
                scheduleView.getStore().reload({params:{all: true}});
          },
		  failure: function(response, options){
		   		extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
   	      }
        });
    }
}

function sort(order) {
    changeView(order);
}

function quickAdd() {
    if(theForm.work.value.indexOf('-') >= 0 || theForm.work.value.indexOf('+') >= 0 ){
         extAlert('<display:get name="prm.resource.global.exterroralert.title" />','<display:get name="prm.schedule.quickadd.work.validnumber.message" />',Ext.MessageBox.ERROR); 
         return;
    }
    if (verifySelectionPlan(theForm, 'oneorzero', oneOrZeroSelectionErrMes) && verifyNoHtmlContent(theForm.name.value, '<%=PropertyProvider.get("prm.schedule.main.verifyname.nohtmlcontent.message")%>')) {
        theForm.theAction.value = "/MainProcessing/QuickAdd";
        theForm.action.value = "<%=Action.CREATE%>";
        theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
        ajaxForm.submit(onSubmit);
    }
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

function percentage() {
    if (verifySelectionPlan(theForm, "multiple", noSelectionErrMes)) {
       var alertMessage = notAllowedForImportedMessage1;
       var showAlertMessage = false;
       var selectedIds = scheduleView.getSelectedRowIds();
       for(var i=0; i < selectedIds.length; i++) {
            var selectedId = selectedIds[i];
            if(isExternalTask(selectedId)){
                alertMessage = alertMessage + document.getElementById( "hi"+selectedId ).value + "\n";
                showAlertMessage = true;
            }
       }
       if(true == showAlertMessage){
            extAlert(errorTitle, alertMessage , Ext.MessageBox.ERROR);
            return;
       }

       percentageWindow = openwin_dialog('percentage', '<%=baseUrl+"/schedule/PercentComplete.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>', 175, 450);
    }
}

function percentageComplete(percentage) {
    theForm.percentage.value = percentage;
    percentageWindow.close();
    theForm.theAction.value = "/MainProcessing/Percentage";
    theForm.action.value = "<%=Action.MODIFY%>";
    theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
    ajaxForm.submit(onSubmit);
}

function phase() {
    if (verifySelectionPlan(theForm, "multiple", noSelectionErrMes)) {
        phaseWindow = openwin_dialog('phase', '<%=baseUrl+"/schedule/SelectPhase.jsp?module="+Module.SCHEDULE+"&action="+Action.MODIFY%>', 175, 450);
    }
}

function phaseComplete(phase) {
    theForm.phaseID.value = phase;
    phaseWindow.close();
    theForm.theAction.value = "/MainProcessing/SetPhase";
    theForm.action.value = "<%=Action.MODIFY%>";
    theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
    ajaxForm.submit(onSubmit);
}

function isExternalTask(p_taskid) {
    try{
        var tid = document.getElementById("hi"+p_taskid);
        if(tid != null)
            return true;
        else
            return false;
    }catch(notAnExternalTask){
        return false;
    }
}

function resources() {
    if (verifySelectionPlan(theForm, 'multiple', noSelectionErrMes)) {
        var alertMessage = notAllowedForImportedMessage;
        var showAlertMessage = false;
       var selectedIds = scheduleView.getSelectedRowIds();
       for(var i=0; i < selectedIds.length; i++) {
            var selectedId = selectedIds[i];
            if(isExternalTask(selectedId)){
                alertMessage = alertMessage + document.getElementById( "hi"+selectedId ).value + "\n";
                showAlertMessage = true;
            }
       }
        if(true == showAlertMessage){
            extAlert(errorTitle, alertMessage , Ext.MessageBox.ERROR);
            return;
        }

        //Open the dialog box with the required information
        resourcesWindow = openwin_dialog('resource_dialog', 'about:blank', 350, 450, 'yes');
        theForm.theAction.value = "/TaskList/AssignResourcesDialog";
        theForm.action.value = "<%=Action.MODIFY%>";
        theForm.target = 'resource_dialog';
        theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
        theForm.submit();
        theForm.target = '_self';
    }
}

function resourcesComplete() {
    //Resources have been reset.  We need to reload the page.
    resourcesWindow.close();
    reset();
}

function resourcesError(errorText) {
    resourcesWindow.close();
    document.getElementById("errorLocationID").innerHTML += (errorText + "<br/>");
}

function dPopup(dependencies) {
    var newHTML = "";

    newHTML = newHTML + "<table class='popup' cellpadding='0' cellspacing='0'>";
    newHTML = newHTML + "  <tr><td>";
    newHTML = newHTML + "    <table class='popupTitle' width='100%'>";
    newHTML = newHTML + "      <tr><td><b><%=PropertyProvider.get("prm.schedule.main.dependencies")%></b></td></tr>";
    newHTML = newHTML + "    </table>";
    newHTML = newHTML + "  </td></tr>";
    newHTML = newHTML + "  <tr><td>";
    newHTML = newHTML + "    <table class='popupBody' width='100%' id='depContent' cellpadding='2' cellspacing='2'>";
    newHTML = newHTML + "      <tr class='tableHeader'><td><%=PropertyProvider.get("prm.schedule.list.sequence.column")%></td><td><%=PropertyProvider.get("prm.schedule.taskview.dependencies.name.column")%></td><td><%=PropertyProvider.get("prm.schedule.taskview.dependencies.type.column")%></td><td><%=PropertyProvider.get("prm.schedule.taskview.dependencies.lagtime.column")%></td></tr>";
    newHTML = newHTML + "      <tr class='headerSep'><td colspan='4'></td></tr>";

    var rows = dependencies.split("|");
    var rowInfo;
    for (var i = 0; i < rows.length; i++) {
      rowInfo = rows[i].split("@");
      newHTML = newHTML + "      <tr><td>"+rowInfo[0]+"</td><td>"+rowInfo[1]+"</td><td>"+rowInfo[2]+"</td><td>"+rowInfo[3]+"</td></tr>";
      newHTML = newHTML + "      <tr><td colspan='4' class='rowSep'></td></tr>";
    }
    newHTML = newHTML + "    </table>";
    newHTML = newHTML + "  </td></tr>";
    newHTML = newHTML + "</table>";

    document.getElementById("depPopup").innerHTML = newHTML;

    pup("depPopup");
}

function dClose() {
    phide("depPopup");
}

function aPopup(assignments) {
    var newHTML = "";

    newHTML = newHTML + "<table class='popup' cellpadding='0' cellspacing='0'>";
    newHTML = newHTML + "  <tr><td>";
    newHTML = newHTML + "    <table class='popupTitle' width='100%'>";
    newHTML = newHTML + "      <tr><td><b><%=PropertyProvider.get("prm.schedule.main.assignments")%></b></td></tr>";
    newHTML = newHTML + "    </table>";
    newHTML = newHTML + "  </td></tr>";
    newHTML = newHTML + "  <tr><td>";
    newHTML = newHTML + "    <table class='popupBody' width='100%' id='depContent'>";
    newHTML = newHTML + "      <tr class='tableHeader'><td><%=PropertyProvider.get("prm.schedule.taskview.resources.assign.person.column")%></td><td><%=PropertyProvider.get("prm.schedule.taskview.resources.assign.assigned.column")%></td><td><%=PropertyProvider.get("prm.schedule.taskview.resources.assign.work.column")%></td><td><%=PropertyProvider.get("prm.schedule.taskview.resources.assign.workcomplete.column")%></td></tr>";
    newHTML = newHTML + "      <tr class='headerSep'><td colspan='4'></td></tr>";

    var rows = assignments.split("|");
    var rowInfo;
    for (var i = 0; i < rows.length; i++) {
      rowInfo = rows[i].split("@");
      newHTML = newHTML + "      <tr><td>"+rowInfo[0]+"</td><td>"+rowInfo[1]+"</td><td>"+rowInfo[2]+"</td><td>"+rowInfo[3]+"</td></tr>";
      newHTML = newHTML + "      <tr><td colspan='4' class='rowSep'></td></tr>";
    }
    newHTML = newHTML + "    </table>";
    newHTML = newHTML + "  </td></tr>";
    newHTML = newHTML + "</table>";

    document.getElementById("resPopup").innerHTML = newHTML;

    pup("resPopup");
}

function aClose() {
    phide("resPopup");
}

var dateRangePopup = new PopupWindow("dateRangeID");
dateRangePopup.offsetY = 25;

// 09-18-2006. Fix for BFD-3242
//dateRangePopup.autoHide();

function showDateTimes(anchor, scheduleEntryID) {
    document.getElementById("dateRangeFrameID").src = JSPRootURL+'/schedule/ScheduleEntryDateTimes.jsp?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>&scheduleEntryID=' + scheduleEntryID;
    dateRangePopup.showPopup(anchor)
}

function hideDateTimes() {
    dateRangePopup.hidePopup();
}

function submitFilters() {
	showResultForApplyFilter = true;
    theForm.theAction.value = "/MainProcessing/ApplyFilters";
    theForm.action.value = "<%=Action.VIEW%>";
    ajaxForm.submit(onSubmit);
}

function addExternal() {
    if (verifySelectionPlan(theForm, 'oneorzero', oneOrZeroSelectionErrMes)) {
        theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
        theForm.theAction.value = "/Sharing/CreateFromExternal";
        theForm.action.value = "<%=Action.CREATE%>";
        theForm.submit();
    }
}

function share() {
    if (verifySelectionPlan(theForm, 'multi', noSelectionErrMes)) {
        theForm.selectedIds.value = scheduleView.getSelectedRowIds().join(",");
        theForm.theAction.value = "/MainProcessing/Share";
        theForm.action.value = "<%=Action.SHARE%>";
        theForm.submit();
    }
}

function captureWork() {
    if (verifySelectionPlan(theForm, 'multiple', noSelectionErrMes)) {
        var alertMessage = notAllowedForImportedMessage2;
        var showAlertMessage = false;
       var selectedIds = scheduleView.getSelectedRowIds();
       for(var i=0; i < selectedIds.length; i++) {
            var selectedId = selectedIds[i];
            if(isExternalTask(selectedId)){
                alertMessage = alertMessage + document.getElementById( "hi"+selectedId ).value + "\n";
                showAlertMessage = true;
            }
       }
        if(true == showAlertMessage){
            extAlert(errorTitle, alertMessage , Ext.MessageBox.ERROR);
            return;
        }
        
        theLocation=JSPRootURL+"/servlet/AssignmentController/CurrentAssignments/Update?module=<%=Module.SCHEDULE%>&action=<%=Action.VIEW%>";
        theLocation += "&" + formatQueryParameters1(scheduleView.getSelectedRowIds(), 'objectID');
        theLocation += "&returnTo=<%=URLEncoder.encode("/schedule/Main.jsp?module=" + Module.SCHEDULE + "&action=" + Action.VIEW, "UTF-8")%>";
        self.location = theLocation;
    }
}

function formatQueryParameters1(i, paramName) {
    result = "";
    for(i in ids) {
        if (result.length > 0) {
            result += "&";
        }
        result += paramName + "=" +ids[i];
    }
    return result;
}

//Toggle the visible state of a tree node
function toggleTree(id) {
    //Run the normal toggle
    toggle(id);

    //Now store the value of this node in the database
    var expanded = document.getElementById(id).getAttribute("kidsShown");
    notifyToggleTree(id, expanded);
}

function notifyToggleTree(id, expanded) {
    var xml = new XMLRemoteRequest();
    xml.asyncRequest(JSPRootURL + "/servlet/ScheduleController/Main/StoreTreeViewSettings?"+
        "module=<%=Module.SCHEDULE%>&name=node"+id+"expanded&value="+expanded);
}

<%
    ScheduleEntry first = schedule.getTaskList().first();
    String firstID = (first == null ? "" : first.getID());
%>
//Expand all tree nodes
function expand_all() {
    if(isScheduleGridIndented) {
        var store = scheduleView.getStore();
        store.data.each(function(record){
            if(!store.isLeafNode(record) && store.isLoadedNode(record) && !store.isExpandedNode(record)){
                store.expandNode(record);
             }
        });
    }
}

//Collapse all tree nodes
function collapse_all() {
    if(isScheduleGridIndented) {
        var store = scheduleView.getStore();
        store.data.each(function(record){
            if(!store.isLeafNode(record) && store.isLoadedNode(record) && store.isExpandedNode(record)){
                store.collapseNode(record);
            }
        });   
    }
}

</script>

</head>

<body class="main" onLoad="setup();" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<!-- Dependencies Popup -->
<div id="depPopup" style="position:absolute;z-index:200;top:-100;left:-100">
</div>

<!-- Resources Popup -->
<div id="resPopup" style="position:absolute;z-index:200;top:-100;left:-100">
</div>

<%---------------------------------------------------------------------------------------
  -- Draw toolbar
  -------------------------------------------------------------------------------------%>
<tb:toolbar style="tooltitle" groupTitle="prm.project.nav.schedule">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display='<%=PropertyProvider.get("prm.schedule.main.module.history")%>'
                    jspPage='<%=baseUrl + "/schedule/Main.jsp"%>'
                    queryString='<%="module="+net.project.base.Module.SCHEDULE+"&hierarchyView="+hierarchyView%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard" showAll="true">
        <display:if name="@prm.schedule.main.importxml.enabled">
        <tb:button type="import" label='<%=PropertyProvider.get("prm.schedule.main.importxml")%>' />
        <tb:button type="export" label='<%=PropertyProvider.get("prm.schedule.main.exportxml")%>'/>  
        </display:if> 
        <tb:button type="create" label='<%=PropertyProvider.get("prm.schedule.main.create.tooltip")%>' />
        <tb:button type="modify" label='<%=PropertyProvider.get("prm.schedule.main.modify.tooltip")%>' />
        <tb:button type="remove" label='<%=PropertyProvider.get("prm.schedule.main.delete.tooltip")%>' />
        <tb:button type="notify" label='<%=PropertyProvider.get("prm.schedule.main.notify.tooltip")%>' />
        <tb:button type="properties" label='<%=PropertyProvider.get("prm.schedule.main.properties.tooltip")%>' />
        <display:if name="@prm.crossspace.isenabled">
        <tb:button type="add_external"/>
        <tb:button type="share"/>
        </display:if>
        <tb:button type="custom" imageEnabled='<%=PropertyProvider.get("all.global.toolbar.standard.export.image.on")%>' imageOver='<%=PropertyProvider.get("all.global.toolbar.standard.export.image.over") %>' label='Export Gantt PDF' function='javascript:generateGanntPDF();' />
        <display:if name="@prm.blog.isenabled">
        <tb:button type="blogit" />
        </display:if>
        <tb:button type="custom" imageEnabled='<%=PropertyProvider.get("all.global.toolbar.standard.wiki.image.on")%>' imageOver='<%=PropertyProvider.get("all.global.toolbar.standard.wiki.image.over") %>' label='Wiki' function='javascript:wiki();' />
        <tb:button type="security"/>
    </tb:band>    
    <tb:band name="schedule" showAll="true" groupHeading="Schedule">
        <%-- Only allow tasks to be moved up or down if the tasks are in order. --%>
        <% if (schedule.getOrderBy().equals(TaskFinder.SEQUENCE_NUMBER_COLUMN)) { %>
        <tb:button type="task_up"/>
        <tb:button type="task_down"/>
        <% } %>
        <tb:button type="task_left"/>
        <tb:button type="task_right"/>
        <tb:button type="resources"/>
        <tb:button type="percentage"/>
        <tb:button type="phase"/>
        <tb:button type="link_tasks" />
        <tb:button type="unlink_tasks" />
        <% if (schedule.isAutocalculateTaskEndpoints()) { %>
        <tb:button type="recalculate"/>
        <% } %>
    </tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="<%=baseUrl%>/servlet/ScheduleController">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%= net.project.base.Module.SCHEDULE %>">
    <input type="hidden" name="action">
    <input type="hidden" name="id">
    <input type="hidden" name="parentTask">
    <input type="hidden" name="percentage">
    <input type="hidden" name="phaseID">
    <input type="hidden" name="selectedIds">

<errors:show clearAfterDisplay="true" stylesheet="/base/xsl/error-report.xsl" scope="session"/>
<div id="errorLocationID" class="errorMessage"></div>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
    <td align="left" valign="middle" class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.name.label")%>&nbsp</td>
    <td align="left" valign="middle"><input type="text" name="name" style="height: 18px;" size="15" maxlength="255" <%=(Validator.isBlankOrNull(taskName) ? "" : " value=\""+taskName+"\"")%>></td>
    <td align="left" valign="middle" class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.work.label")%></td>
    <td align="left" valign="middle" nowrap="true">
        <input type="text" name="work" style="height: 18px;" size="3" maxlength="5"<%=(Validator.isBlankOrNull(work) ? "" : " value=\""+work+"\"")%>>
        <select name="workUnits" style="font-size: 10px">
            <%=HTMLOptionList.makeHtmlOptionList(Helper.getWorkUnitOptions(), workUnits)%>
        </select>
    </td>
    <% if (!schedule.isAutocalculateTaskEndpoints()) { %>
    <td align="left" valign="middle" class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.startdate.label")%></td>
    <td align="left" valign="middle"><input type="text" name="startTimeString" size="10" maxlength="10" style="height: 18px;" value="<%=startTimeString%>"><util:insertCalendarPopup fieldName="startTimeString"/></td>
    <td align="left" valign="middle" class="tableHeader"><%=PropertyProvider.get("prm.schedule.taskedit.status.finishdate.label")%></td>
    <td align="left" valign="middle"><input type="text" name="endTimeString" size="10" maxlength="10" style="height: 18px;"  value="<%=endTimeString%>"><util:insertCalendarPopup fieldName="endTimeString"/></td>
    <% } %>
    <td align="left" valign="middle">
        <a href="javascript:quickAdd()"><img name="quickadd" src="<%=SessionManager.getJSPRootURL()%>/images/schedule/quick_add.gif"
                                                             width="100" height="28"
                                                             onmouseover="document.quickadd.src='<%=SessionManager.getJSPRootURL()%>/images/schedule/quick_add_hover.gif'"
                                                             onmouseout="document.quickadd.src='<%=SessionManager.getJSPRootURL()%>/images/schedule/quick_add.gif'" border="0"></a>
    </td>
</tr>
</table>
<br reset="all">

<jsp:include page="include/taskList.jsp" />
<jsp:include page="Main_beta.jsp" />

<%@ include file="/help/include_outside/footer.jsp" %>
</form>
<%-- Hidden table used to display date range popup; its position is irrelevant on this page --%>
<div id="dateRangeID">
<table cellpadding="0" cellspacing="0" class="popup">
<tr><td>
    <table width="100%" class="popupTitle">
        <tr>
            <td><display:get name="prm.schedule.scheduleentrydatetimes.title" /></td>
            <td align="right">
                <a href="javascript:hideDateTimes();" title='<display:get name="prm.schedule.scheduleentrydatetimes.close.title" />'>X</a>
            </td>
        </tr>
    </table>
</td></tr>
<tr class="popupBody"><td>
    <%-- We put the iframe in the table so that the scroll bar is within the table --%>
    <iframe id="dateRangeFrameID" width="<display:get name='prm.schedule.main.timespopup.width' />" height="<display:get name='prm.schedule.main.timespopup.height' />" frameborder="0"></iframe>
</td></tr>
</table>
</div>
<script language="JavaScript">
//Now check the selected ids that were passed into this screen.
var listOfIDsToCheck = '<%=request.getAttribute("selectedList")%>';
checkCheckboxes(self.document.forms[0].selected, listOfIDsToCheck);
</script>
<%
// END ELSE NOT GANTT VIEW
     }
%>
</body>
</html>
