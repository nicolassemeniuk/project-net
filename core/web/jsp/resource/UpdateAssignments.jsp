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
    info="Allows user to update completion and work information for assignments."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
			net.project.base.property.PropertyProvider,
            net.project.security.Action,
            net.project.calendar.CalendarBean,
            net.project.space.SpaceTypes,
            org.apache.commons.lang.StringUtils,
            net.project.resource.PersonProperty,
            net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="scrollBackStartDate" class="java.util.Date" scope="request"/>
<jsp:useBean id="scrollForwardStartDate" class="java.util.Date" scope="request"/>
<jsp:useBean id="dateRangeStart" class="java.util.Date" scope="request"/>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request" />
<jsp:useBean id="readOnly" type="java.lang.Boolean" scope="request"/>
<jsp:useBean id="startOfWeek" type="java.util.Date" scope="session"/>
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="property" class="net.project.resource.PersonProperty" scope="session" />
<jsp:useBean id="assignmentStatus" type="java.lang.String" scope="request"/>
<jsp:useBean id="assignments" type="java.util.List" scope="request"/>
<%
    String userId = request.getParameter("personId") != null ? request.getParameter("personId") : SessionManager.getUser().getID();
    boolean isEditMode = Boolean.valueOf(request.getParameter("isEditMode")).booleanValue();
    boolean showBlogsOnRight = Boolean.valueOf(request.getParameter("enableBlogsRightTab")).booleanValue();
    boolean isActualWork = Boolean.valueOf(request.getParameter("isActualWork")).booleanValue();
    boolean isPercentComplete = Boolean.valueOf(request.getParameter("isPercentComplete")).booleanValue();
    
    // Set personal space if current space is other than personal space
    if(!user.getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE)){
    	user.setCurrentSpace(personalSpace);
    }
    
%>
<%! 
private String getPropertyValue(PersonProperty props, String property) {
    String[] expandedProps = props.get("prm.resource.timesheet", property, true);
    return (expandedProps != null && expandedProps.length > 0 ? expandedProps[0]: "null");
}
%>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS />
<template:import type="css" src="/styles/timesheet.css" />
<style type="text/css">	html, body{	overflow-y: hidden;	}</style>
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/trim.js" />
<template:import type="javascript" src="/src/dhtml/dhtmlutils.js" />
<template:import type="javascript" src="/src/dhtml/xmlrequest.js" />
<template:import type="javascript" src="/src/dhtml/visibility.js" />
<template:import type="javascript" src="/src/dhtml/findDOM.js" />
<template:import type="javascript" src="/src/upload.js" />
<script language="javascript" type="text/javascript">
var theForm;
var contentPanel;
var subject;
var content;
var workSubmitted;
var multipleWorkSubmitted;
var allFieldsValidation = true;
var storeError = "";
var errorneousFieldName = new Array();
var errorneousFieldID;
var JSPRootURL = '<%=SessionManager.getJSPRootURL() %>'
var blogItFor = 'timesheet';
var windowWidth = 0;
var windowHeight = 0;
var blogWidth = 0; 
var assignmentWidth = 0;
var pattern = '<%=SessionManager.getUser().getDateFormatter().getDateFormatExample()%>';
var workCaptureCommentEnabled = <%=PropertyProvider.getBoolean("prm.resource.timesheet.workcapturecomment.isenabled")%>;
var size = new Array();
var curWidth = 0;
var curX = 0;
var newX = 0;
var mouseButtonPos = "up";
var taskListPanelWidth ;
var errorAlertTitle = '<display:get name="prm.resource.global.exterroralert.title" />';
var confirmAlertTitle = '<display:get name="prm.global.extconfirm.title" />';
var jumpToDateValue;
var rightPanelOpened;

if(navigator.userAgent.toLowerCase().indexOf('chrome') > -1 || navigator.userAgent.toLowerCase().indexOf('safari') > -1 || navigator.userAgent.toLowerCase().indexOf("firefox/2.0") >= 0) {
	document.write ("<style> #assignment-list { position: absolute; } #footer-main-div {position: absolute;}</style>");
}
if(navigator.userAgent.toLowerCase().indexOf('safari') > -1) {
	document.write ("<style> .tday {padding-top: 3px;}</style>");
}

// Load default values while loading page
function setup() {
    if(workCaptureCommentEnabled) {
		createContentPanel();
	}
	theForm = document.forms[0];
	resizeTimesheet();
	personalizeView();
	restoreAllTotals();
}

// Navigate to previous week
function scrollBack() {
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/AssignmentController/CurrentAssignments/Update?module=<%=securityProvider.getCheckedModuleID()%>&action=<%=securityProvider.getCheckedActionID()%>&startDate=<%=scrollBackStartDate.getTime()%>" + "&isFromTimeSheet=true&personId="+userId+"&isEditMode=<%=isEditMode%>&enableBlogsRightTab=<%=showBlogsOnRight%>&assignmentStatus=<%=assignmentStatus%>&isActualWork=<%=isActualWork%>&isPercentComplete=<%=isPercentComplete%>";
}

// Navigate to next week
function scrollForward() {
    self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/AssignmentController/CurrentAssignments/Update?module=<%=securityProvider.getCheckedModuleID()%>&action=<%=securityProvider.getCheckedActionID()%>&startDate=<%=scrollForwardStartDate.getTime()%>" + "&isFromTimeSheet=true&personId=<%=userId%>&isEditMode=<%=isEditMode%>&enableBlogsRightTab=<%=showBlogsOnRight%>&assignmentStatus=<%=assignmentStatus%>&isActualWork=<%=isActualWork%>&isPercentComplete=<%=isPercentComplete%>";
}

// Cancels the current edited workcaptured values
function cancel() {
	<%if(PropertyProvider.getBoolean("prm.resource.timesheet.monthlyview.isenabled")) {%>
		self.document.location = "<%=SessionManager.getJSPRootURL()%>/timesheet/CalendarTimesheet?module="+<%=Module.PERSONAL_SPACE%>;
	<%} else {%>
		self.document.location = "<%=SessionManager.getJSPRootURL()+"/servlet/AssignmentController/CurrentAssignments/Update?module=" + Module.PERSONAL_SPACE + "&action="
				+ Action.MODIFY + "&startDate=" + dateRangeStart.getTime() + "&isFromTimeSheet=true&personId=" + userId
				+ "&assignmentStatus=" + assignmentStatus +"&isEditMode="+isEditMode+"&enableBlogsRightTab="
				+ showBlogsOnRight +"&isActualWork="+ isActualWork +"&isPercentComplete="+isPercentComplete%>";
	<%}%>
}

// Error to show that is returned as response
function flagError(errorText, fieldName) {
    document.getElementById("errorLocationID").innerHTML = (errorText + "<br/>");
    document.getElementById("errorLocationID").className = 'errorMessage v';
    //Avinash bfd 3169 Able to record negative hours of work against a task. 
    valid = false;
    allFieldsValidation = valid;
    storeError = errorText;
    
    if(typeof fieldName != 'undefined' ){
	    errorneousFieldID = fieldName;
	    errorneousFieldName[errorneousFieldID] = document.getElementById("wCF_"+fieldName).innerHTML.trim();
    }
}


//Update the work captured 
function update() {
	valid = !(typeof errorneousFieldID != 'undefined' && (errorneousFieldName != null && errorneousFieldID != null 
				&& errorneousFieldID != "" && document.getElementById("workField_"+errorneousFieldID) != null 
				&& (document.getElementById("workField_"+errorneousFieldID).value < 0 
					|| document.getElementById("workField_"+errorneousFieldID).value > 24)));
				
	//Avinash bfd 3169 Able to record negative hours of work against a task. 
	if(valid){
		theForm.theAction.value = "/CurrentAssignments/UpdateProcessing";
		   	subject = document.getElementById('blog-it_title').value;
			content = contentPanel.getComponent('content').getRawValue().trim();
			
			for(var i = containsArray.length; i > 0; i--) {
				if(typeof containsArray[i] != 'undefined' && taskIDArray[i] != 'undefined') {
					commentMessage += containsArray[i] + ",";
					taskIdCSV += taskIDArray[i] + ",";
				}
			}
			if(typeof commentMessage != 'undefined' && commentMessage != null) {
				theForm.multipleWorkSubmitted.value = commentMessage.substring(0,(commentMessage.length - 1));
				theForm.subject.value = subject;
				theForm.isImportant.value = document.getElementById('isImportantEntry').checked
				theForm.taskIdArray.value = taskIdCSV.substring(0, (taskIdCSV.length - 1));
				theForm.spaceIdArray.value = spaceIds.substring(0, (spaceIds.length - 1));
			}
			if(content != ""){
				if(document.getElementById('blog-it_title').value.trim() == '') {
			    	Ext.MessageBox.confirm(confirmAlertTitle, "Post blog without a subject?", function(btn) { 
			    			if(btn == 'yes'){
			    				theForm.submit();
			    				return true;
			    			} 
			    			return false; 
			    		});
				} else {
					theForm.submit();
				}
				
			} else {
				extAlert(errorAlertTitle, '<display:get name="prm.blog.addweblogentry.validation.message" />', Ext.MessageBox.ERROR);
			}
    } else {
	    extAlert(errorAlertTitle, storeError, Ext.MessageBox.ERROR);
    }
    document.getElementById('btnSubmit').disabled = true;
	document.getElementById('btnCancel').disabled = true;
}
function submitAssignmentCriteria() {
	self.document.location = "<%=SessionManager.getJSPRootURL()%>/servlet/AssignmentController/CurrentAssignments/Update?module=<%=securityProvider.getCheckedModuleID()%>&action=<%=securityProvider.getCheckedActionID()%>&startDate=<%=dateRangeStart.getTime()%>&isFromTimeSheet=true&assignmentStatus="+theForm.fValue.value+"&personId="+<%=userId%>+"&isEditMode="+<%=isEditMode%>+"&enableBlogsRightTab="+<%=showBlogsOnRight%>+"&isActualWork="+<%=isActualWork%>+"&isPercentComplete="+<%=isPercentComplete%>;
}

function popupHelp(page) {
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page="+page;
	openwin_help(helplocation);
}

function help(page) {
    openwin_help("<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=timesheet");
}

function showpopUp(){
	var url = "<%=SessionManager.getJSPRootURL()+"/timesheet/PersonalizeTimesheet?isActualWork="+isActualWork+"&isPercentComplete="+isPercentComplete+"&assignmentStatus="+assignmentStatus+"&personId="+userId%>";
	showUploadPopup(url, 400, null, 'timesheet');
}

function createContentPanel(){
	contentPanel = new Ext.FormPanel({
		border: false,
		width: '100%',	
		items: [{
			xtype: 'htmleditor',
			id: 'content',
			fieldLabel: 'Content',
			width: '520px',
			height: 200,
			anchor: '95%',
			enableFontSize: false,
			style: 'border: thin; border-color: #33BDFF;',
			renderTo: document.getElementById('contentTextarea'),
			listeners: {			
				'render': function(component){
						var size = component.getSize();
						component.setSize(size.width+(520-size.width), 200);
					}
				}
			}]
	});
}

// function for exporting excelsheet
function exportToExcel(){
	<%if(assignments.size() > 0) {%>
		document.location = JSPRootURL+"/timesheet/exportExcel?module=<%=Module.PERSONAL_SPACE%>&startDate=<%=dateRangeStart.getTime()%>&filter=<%=assignmentStatus%>";
	<%} else {%>
		extAlert(errorAlertTitle, '<%=PropertyProvider.get("prm.resource.timesheet.empty.excelsheet.errormessage")%>', Ext.MessageBox.ERROR);
	<%}%>
}

// Method for jump date location
function getJumpToDateLocation(){
	if(jumpToDate != null && jumpToDate.getValue() != '' && jumpToDate.isValid()){
		self.document.location = "<%=SessionManager.getJSPRootURL()+"/servlet/AssignmentController/CurrentAssignments/Update?module=" + Module.PERSONAL_SPACE + "&action="
			+ Action.MODIFY + "&startDate=" + dateRangeStart.getTime() + "&isFromTimeSheet=true&personId=" + userId
			+ "&assignmentStatus=" + assignmentStatus +"&isEditMode="+isEditMode+"&enableBlogsRightTab="
			+ showBlogsOnRight +"&isActualWork="+ isActualWork +"&isPercentComplete="+isPercentComplete+"&jtodate="%>"+jumpToDate.getValue().dateFormat(getJSUserDatePattern(pattern));
	} else {
		extAlert(errorAlertTitle, '<%=PropertyProvider.get("prm.resource.timesheet.invalid.jumptodate.message")%>', Ext.MessageBox.ERROR);
	}
	
}

// Reset width and height on resizing the window
window.onresize = resizeTimesheet;
function resizeTimesheet(){
	putScreenResolutionInCookie();
	resetPanelsWidth();
	resetPanelsHeight();
}

// Toggle blog panel view to expand / collapse 
function toggleRightPanel(isOpen){
	isOpen ? closeRightPanel() : openRightPanel();
}

// Close right panel
function closeRightPanel(){
	collapseDiv(true, 'assignmentsDiv', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '98%', '2%', false);
	//save as right panel unexpanded
	saveUserSettings("rightpanelexpanded", "false");
	resetPanelsWidth();
	rightPanelOpened = false;
}

// Open right panel
function openRightPanel(){
	collapseDiv(false, 'assignmentsDiv', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '80%', '20%', false);
	//save as right panel expanded
	saveUserSettings("rightpanelexpanded", "true");
	resetPanelsWidth();
	rightPanelOpened = true;
}

// Resize all panels width
function resetPanelsWidth(){
	windowWidth = getWindowWidth();
	document.getElementById('timecard').style.width = (windowWidth - 223);
	document.getElementById('timecard-header').style.width = (windowWidth - 223);
	document.getElementById('timecard-footer').style.width = (windowWidth - 223);
	if(document.getElementById('blogsCollapsed').style.display == ''){
		document.getElementById('assignment-list').style.width = (windowWidth - 223) + 'px';
		document.getElementById('splitterBar').style.display = 'none';
		if(windowWidth <= 1016) {
			document.getElementById("Timesheet").style.width = '97%';
		}
		setHeaderWidth();
	}else{
		//if right panel expanded
		document.getElementById('splitterBar').style.display = '';
		if(taskListPanelWidth && taskListPanelWidth <=(windowWidth - 297)){
			resizeRightPanel();
		}else {
			taskListPanelWidth = windowWidth - 463;
			resizeRightPanel();
		}
	}
	setColumnWidth();
}
//Function 'setPos(...) gets the original div width.
function setPos(e) {
	//For handling events in ie vs. w3c.
	curEvent = ((typeof event == "undefined")? e: event);
	//Sets mouse flag as down.
	mouseButtonPos = "down";
	//Gets position of click.
	curX = curEvent.clientX;
	//Get the width of the div.
	var tempWidth = document.getElementById("assignment-list").style.width;
	//Get the width value.
	var widthArray = tempWidth.split("p");
	//Set the current width.
	curWidth = parseInt(widthArray[0]);
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) 
		document.attachEvent("onmouseup",   resizeRightPanel);
	else
		document.addEventListener("mouseup", resizeRightPanel, true);
}

//Function getPos(...) changes the width of the div while the mouse button is pressed.
function getPos(e){
	if( mouseButtonPos == "down" ) {
		//For handling events in ie vs. w3c.
		curEvent = ((typeof event == "undefined")? e: event);
		//Get new mouse position.
		newX = curEvent.clientX;
		//Calculate movement in pixels.
		var pixelMovement = parseInt(newX - curX);
		//Determine new width.
		var newWidth = parseInt(curWidth + pixelMovement)-11;
		//Enforce a minimum width.
		if(newWidth < 60)	newWidth = 60; 
	 	if(newWidth > (windowWidth - 297)) newWidth = (windowWidth - 297);
		taskListPanelWidth = newWidth;
		setSplitterPosition('splitterBarShadow',newWidth);
	}
}

function savePosition(){
	//save assignment panel width
	if(rightPanelResized)
		saveUserSettings("assignmentpanelwidth", taskListPanelWidth);
	rightPanelResized = false;
}

// Resize the width of right panel according to splitter
function resizeRightPanel(){
	document.getElementById("splitterBarShadow").style.display = 'none';
	mouseButtonPos = "up";

	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) 
		document.detachEvent("onmouseup", resizeRightPanel);
	else
		document.removeEventListener("mouseup", resizeRightPanel, true);

	newWidth = taskListPanelWidth;
	newWidth = parseInt(newWidth) < 60 ? 60 : newWidth;
		
	//Set the new width.
	document.getElementById("assignment-list").style.width = (parseInt(newWidth)+12)+ "px";
	document.getElementById('content').style.top = '108px';
	document.getElementById('content').style.marginTop = '5px';
		 
	setSplitterPosition("splitterBar", newWidth);
	document.getElementById("taskBlogDivRight").style.width = (windowWidth - parseInt(newWidth)  - 233) + 'px';
	
	if(<%=errorReporter.errorsFound()%>){
		document.getElementById('splitterBar').style.top =  document.getElementById('Timesheet').offsetTop;
		if(navigator.userAgent.toLowerCase().indexOf('safari') > -1) {
			document.getElementById('assignment-list').style.top = parseInt(document.getElementById("assignment-list").style.top)+ parseInt(document.getElementById("header-main-div").offsetHeight);
		}
	}
	setHeaderWidth();
	saveUserSettings("assignmentpanelwidth", taskListPanelWidth);
}

// Set splitter position
function setSplitterPosition(id, newWidth){
	var obj = document.getElementById(id);
	obj.style.display = '';
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		var version = parseFloat(navigator.appVersion.split("MSIE")[1]);
		if(version == 8) {
			obj.style.left = (parseInt(newWidth)+ 30) + "px";
			obj.style.top = 60 + "px";
		} else {
			obj.style.left = (parseInt(newWidth)+ 33) + "px";
			obj.style.top = 59 + "px";
		}
	} else {
		var left = getElementAbsolutePos(document.getElementById("Timesheet")).x ;
		obj.style.left = ((parseInt(left)+parseInt(newWidth))+12) + "px";
	}
}

// resize all panels height
function resetPanelsHeight(){
	windowHeight = getWindowHeight();
	//calulate and set height of workplan panel.
	document.getElementById('Timesheet').style.height = (windowHeight - 180) + 'px';
	document.getElementById('taskBlogDivRight').style.height = (windowHeight - 200) + 'px';
	document.getElementById('splitterBar').style.height =  (windowHeight - 180)+'px';
	document.getElementById('splitterBarShadow').style.height =  (windowHeight - 180)+'px';
	document.getElementById('assignment-list').style.height = (windowHeight - 255 ) + 'px';
	document.getElementById('blogsCollapsed').style.height = (windowHeight - 180) + 'px';
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
			var version = parseFloat(navigator.appVersion.split("MSIE")[1]);
			if(version == 8) {
				document.getElementById('taskBlogDivRight').style.height = (windowHeight - 229) + 'px';
			}
	}
}

// Apply user's personal setting 
function personalizeView(){
	if("<%=getPropertyValue(property, "rightpanelexpanded")%>" == "false"){
		rightPanelOpened = false;
		closeRightPanel();
	}else{
		if(!isNaN('<%=getPropertyValue(property, "assignmentpanelwidth")%>')){
			taskListPanelWidth = '<%=getPropertyValue(property, "assignmentpanelwidth")%>';
		}
		rightPanelOpened = true;
		openRightPanel();
	}
}

// set header width
function setHeaderWidth(){
	if(hasVerticalScrollbar(document.getElementById("assignment-list"))){
		document.getElementById("timecard-header-div").style.width = (parseInt(document.getElementById("assignment-list").style.width) - 16)+ "px";
		document.getElementById("timecard-footer-div").style.width = (parseInt(document.getElementById("assignment-list").style.width) - 16)+ "px";
		document.getElementById("header-main-div").style.width = parseInt(document.getElementById("assignment-list").style.width)+ "px";
		document.getElementById("footer-main-div").style.width = parseInt(document.getElementById("assignment-list").style.width)+ "px";
	}else{
		document.getElementById("timecard-header-div").style.width = parseInt(document.getElementById("assignment-list").style.width)+ "px";
		document.getElementById("timecard-footer-div").style.width = parseInt(document.getElementById("assignment-list").style.width)+ "px";
		document.getElementById("header-main-div").style.width = parseInt(document.getElementById("assignment-list").style.width)+ "px";
		document.getElementById("footer-main-div").style.width = parseInt(document.getElementById("assignment-list").style.width)+ "px";
	}
	if(navigator.userAgent.toLowerCase().indexOf('safari') > -1) {
		document.getElementById('assignment-list').style.top = '216px';
				document.getElementById("footer-main-div").style.top = parseInt(document.getElementById('assignment-list').style.top) 
																	+ parseInt(document.getElementById('assignment-list').offsetHeight);
	}
	if(navigator.userAgent.toLowerCase().indexOf('chrome') > -1 || navigator.userAgent.toLowerCase().indexOf("firefox/2.0") >= 0) {
		document.getElementById('assignment-list').style.top = '214px';
		document.getElementById("footer-main-div").style.top = parseInt(document.getElementById('assignment-list').style.top) 
																	+ parseInt(document.getElementById('assignment-list').offsetHeight);
	}
}

// set scroll position
function scrollHeader(){
	setHeaderWidth();
	document.getElementById('timecard-header-div').scrollLeft = document.getElementById('assignment-list').scrollLeft;
	document.getElementById('timecard-footer-div').scrollLeft = document.getElementById('assignment-list').scrollLeft;
}

// Handling onchange event for jump to date field while selecting the date.
function jumpToDateEntered() {
	jumpToDateValue = document.getElementById('jumpTimeString').value;
	if(jumpToDateValue.trim() != ""){
		self.document.location = "<%=SessionManager.getJSPRootURL()+"/servlet/AssignmentController/CurrentAssignments/Update?module=" + Module.PERSONAL_SPACE + "&action="
			+ Action.MODIFY + "&startDate=" + dateRangeStart.getTime() + "&isFromTimeSheet=true&personId=" + userId
			+ "&assignmentStatus=" + assignmentStatus +"&isEditMode="+isEditMode+"&enableBlogsRightTab="
			+ showBlogsOnRight +"&isActualWork="+ isActualWork +"&isPercentComplete="+isPercentComplete+"&jtodate="%>"+jumpToDateValue;
	} else {
		extAlert(errorAlertTitle, '<%=PropertyProvider.get("prm.resource.timesheet.invalid.jumptodate.message")%>', Ext.MessageBox.ERROR);
	}
}

// Set column width
function setColumnWidth(){
	var elementsName = document.getElementsByTagName('div');
	for(var index = 0; index < elementsName.length; index++){
	    if(elementsName[index].className == 'at') {
	       elementsName[index].style.width = document.getElementById('assignments-header').offsetWidth - 21 + 'px';
	    }
	}
}
// Handling keydown event for the Jump to date field
document.onkeydown = function(e){
	var type = e ? e.target : window.event.srcElement;
	e =(window.event)? event : e;
	if(e.keyCode == 13 && type == document.getElementById('jumpTimeString')) {
		jumpToDateEntered();
	} 
}
</script>

</head>
<%
	CalendarBean calendarBean = new CalendarBean();
	calendarBean.setTimeZone(SessionManager.getUser().getTimeZone());
	String requestDate = calendarBean.formatDateAs(dateRangeStart, "MMddyyyy");
%>
<body class="main" onLoad="setup();" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='left-navbar'>
	<div id='leftheading-person'><display:get name="prm.personal.nav.timesheet" /></div>
	<div style='clear: both'></div>
	<div class="timesheet-user"><%=HTMLUtils.escape((String)request.getAttribute("personName"))%></div>
	<div class='toolbox-heading'><display:get name="prm.global.toolbox.heading" /></div>
	<div id='toolbox-item' class='toolbox-item'>
		<span id="blog-ItEnabled" >
			<a class="pnet-links" onclick="javascript:blogit();" onmouseout=" document.imgblogit.src = '<%=SessionManager.getJSPRootURL()+"/images/icons/toolbar-gen-blogit_on.gif"%>'" onmouseover=" document.imgblogit.src = '<%=SessionManager.getJSPRootURL()+"/images/icons/toolbar-gen-blogit_over.gif"%>'"><img alt="Blog-it" title="Blog-it" border=0 hspace=0 src="<%=SessionManager.getJSPRootURL()+"/images/icons/toolbar-gen-blogit_on.gif"%>" name="imgblogit">&nbsp;<display:get name="all.global.toolbar.standard.blogit" /></a>
		</span><br/>
		<%if(PropertyProvider.getBoolean("prm.resource.timesheet.monthlyview.isenabled")) {%>
		<span>
			<a href="<%=SessionManager.getJSPRootURL() + "/timesheet/CalendarTimeSheet?personId="+userId+"&DisplayDate="+requestDate%>" onmouseout=" document.imgcustom.src = '<%=SessionManager.getJSPRootURL()+"/images/icons/project-personalize-page_on.gif"%>'" onmouseover=" document.imgcustom.src = '<%=SessionManager.getJSPRootURL()+"/images/icons/project-personalize-page_over.gif"%>'"><img alt="Monthly Timesheet" title="Monthly Timesheet" border=0 hspace=0 src="<%=SessionManager.getJSPRootURL()+"/images/icons/project-personalize-page_on.gif"%>" name="imgcustom">&nbsp;Monthly Timesheet</a>
		</span><br/>
		<% } if(PropertyProvider.getBoolean("prm.resource.timesheet.personalizepage.isenabled")) {%>
		<span>
			<a class="pnet-links" onclick="javascript:showpopUp();" onmouseout=" document.imgcustom.src = '<%=SessionManager.getJSPRootURL()+"/images/icons/project-personalize-page_on.gif"%>'" onmouseover=" document.imgcustom.src = '<%=SessionManager.getJSPRootURL()+"/images/icons/project-personalize-page_over.gif"%>'"><img alt="<%= PropertyProvider.get("prm.global.personalizepage.link") %>" title="<%= PropertyProvider.get("prm.global.personalizepage.link") %>" border=0 hspace=0 src="<%=SessionManager.getJSPRootURL()+"/images/icons/project-personalize-page_on.gif"%>" name="imgcustom">&nbsp;<display:get name="prm.global.personalizepage.link" /></a>
		</span><br/>
		<% } %>
		<span>
			<a class="pnet-links" onclick="javascript:exportToExcel();" onmouseout=" document.imgcustom.src = '<%=SessionManager.getJSPRootURL()+PropertyProvider.get("prm.resource.timesheet.exporttoexcelon.imagepath")%>'" onmouseover=" document.imgcustom.src = '<%=SessionManager.getJSPRootURL()+PropertyProvider.get("prm.resource.timesheet.exporttoexcelover.imagepath")%>'"><img alt="<%=PropertyProvider.get("prm.resource.timesheet.exporttoexcel.link") %>" title="<%=PropertyProvider.get("prm.resource.timesheet.exporttoexcel.link") %>" border=0 hspace=0 src="<%=SessionManager.getJSPRootURL()+PropertyProvider.get("prm.resource.timesheet.exporttoexcelon.imagepath")%>" name="imgcustom">&nbsp;<display:get name="prm.resource.timesheet.exporttoexcel.link" /></a>
		</span><br/>
	</div>
</div>
<div id='content' style="top:108px; margin-top: 5px;" >

<form action="<%=SessionManager.getJSPRootURL()%>/servlet/AssignmentController" method="post" onsubmit="return false;">
<input type="hidden" name="theAction" value="/CurrentAssignments/Update">
<input type="hidden" name="module" value="<%=Module.SCHEDULE%>">
<input type="hidden" name="action" value="<%=Action.VIEW%>">
<input type="hidden" name="returnTo" value="<%=request.getAttribute("returnTo")%>">
<input type="hidden" name="returnTo2" value="<%=request.getAttribute("returnTo2")%>">
<input type="hidden" name="startDate" value="<%=dateRangeStart.getTime()%>">
<input type="hidden" name="multipleWorkSubmitted" id="multipleWorkSubmitted" />
<input type="hidden" name="subject" id="subject" />
<input type="hidden" name="spaceId" value="<%= SessionManager.getUser().getID()%>" />
<input type="hidden" name="objectId" value="<%= SessionManager.getUser().getID()%>" />
<input type="hidden" name="isImportant" />
<input type="hidden" name="taskIdArray" />
<input type="hidden" name="spaceIdArray" />
<input type="hidden" name="personId" value="<%=userId%>"/>
<input type="hidden" name="isActualWork" value="<%= isActualWork%>"/>
<input type="hidden" name="isPercentComplete" value="<%=isPercentComplete%>"/>
<input type="hidden" name="assignmentStatus" value="<%=assignmentStatus%>"/>
<input type="hidden" name="isActualWork" value="<%=isActualWork%>"/>
<input type="hidden" name="isPercentComplete" value="<%=isPercentComplete%>"/>
<input type="hidden" name="isFromTimeSheet" value="<%=Boolean.valueOf(request.getParameter("isFromTimeSheet")).booleanValue()%>" />

<jsp:include page="include/UpdateAssignments.jsp" flush="true" > 
    <jsp:param name="readOnly" value="<%=readOnly.booleanValue()%>" />
</jsp:include>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
<template:import type="javascript" src="/src/weekly-timesheet.js" />
<template:import type="javascript" src="/src/timesheet-blogit.js" />
<script type="text/javascript">
	var windowWidth = getWindowWidth();
	var windowHeight = getWindowHeight();
	<%if(StringUtils.isNotEmpty(request.getParameter("jtodate"))) {%>
		document.getElementById('jumpTimeString').value = '<%=request.getParameter("jtodate")%>';
	<% } %>
</script>
</body>
</html>
