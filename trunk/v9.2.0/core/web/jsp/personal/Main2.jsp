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
       info="Personal Space" 
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.base.property.PropertyProvider,
               net.project.channel.ChannelManager, 
               net.project.channel.Channel,
               net.project.schedule.TaskType,
               net.project.document.DocumentManagerBean,
               net.project.security.User, 
               net.project.security.SessionManager, 
               net.project.space.PersonalSpaceBean,
               net.project.util.NumberFormat,
               net.project.gui.toolbar.Button,
               net.project.gui.toolbar.ButtonType,
               net.project.base.Module,
               net.project.security.Action,
               net.project.channel.State"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />

<%-- History setup, but not displayed on this page --%>
<history:history displayHere="false">
	<history:business show="false" />
	<history:project display="<%=personalSpace.getName()%>"
					 jspPage='<%=SessionManager.getJSPRootURL() + "/personal/Main.jsp"%>' />
</history:history>

<%
   user.setCurrentSpace(personalSpace);
   docManager.getNavigator().put("TopContainerReturnTo", SessionManager.getJSPRootURL() + "/personal/Main.jsp");

	String forwardTo = request.getParameter("page");
	if (forwardTo != null) {
		response.sendRedirect(java.net.URLDecoder.decode(forwardTo, SessionManager.getCharacterEncoding()));
		return;
	}

   int channelLocationIndex = 0;

%>
<template:getDoctype />
<%@page import="net.project.hibernate.service.ServiceFactory"%>
<%@page import="net.project.hibernate.service.IBlogProvider"%>
<%@page import="java.util.List"%>
<%@page import="net.project.hibernate.model.PnWeblogEntry"%>
<%@page import="java.util.Date"%>
<%@page import="net.project.hibernate.constants.WeblogConstants"%>
<html>
<head>
<meta http-equiv="expires" content="0">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="pragma" content="no-cache">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:import type="css" src="/styles/personal_dashboard.css" />

<template:getSpaceCSS />
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/menu/JSCookMenu.js" />
<template:import type="css" src="/src/menu/ThemeOffice/theme.css" />
<template:import type="javascript" src="/src/menu/ThemeOffice/theme.js" />
<template:import type="javascript" src="/src/components/gridView.js" />

<script language="javascript" type="text/javascript">
	var isLoaded = false;
	var isFlatView = true;
	var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var scheduleModule = '<%=Module.SCHEDULE%>';
    var milestoneText = '<%=TaskType.MILESTONE.getName()%>';
    var grid = null; 
    var theGrid = null;
    var root = "<%= SessionManager.getJSPRootURL() %>";

// Custom renderers
function workRenderer(value, element, record){
    return value ? String.format('{0} {1}', record.data['work'], record.data['workUnits']) : '';
}

function durationRenderer(value, element, record){
    // Check this whether it is a milestone
    if(record.data['isMilestone'] == "1"){
        return milestoneText;
    }
    return value ? record.data['duration'] : '';
}

function formatPercentRenderer(value) {
    return value ? String.format('{0} {1}', value, '%') : '0 %';
};

function nameRenderer(value, element, record){
    var result = "";
    // Check this whether it is a milestone
    if(record.data['isMilestone'] == "1"){
        result = String.format('<img src="{0}/images/milestone.gif" height="10" width="10" border="0" />', JSPRootURL);
        result += ' ';
    }
    result += value;
    return result;
}

function dependencyRenderer(value){
    var result = "";
    if(value && value.length > 0){
        // What we get is a separated comma list of Task IDs that this task is depending on
        var idArray = value.split(',');
        for(var i=0; i<idArray.length; i++){
            // For each ID, search the correspondent record in store
            var record = grid.getRecordById(trim(idArray[i]));
            result+= record.data['sequence'];
            if(i < idArray.length - 1){
                // Then it remains more sequence numbers to add
                result += ', ';
            }
        }
    }
    return result;
}

	function setup() {
	   setupGrid();
		isLoaded = true;
	}
	
function setupGrid(){
    if(null == grid){
        grid = new GridView();
    }
    
    setRecordDefinition();
    grid.setConfigurationReader('id', '*:has(id)');
    grid.setDataStore(JSPRootURL + '/personal/tasks.jsp?module=60');
    
    setColumns();
    grid.init('schedule-grid');
    theGrid = grid.getGrid();
    theGrid.on('rowclick', function (theGrid, rowIndex, e) {
    						var record = grid.getStore().getAt(rowIndex);
    						changeTask(record.get('id'));
    					}
    		);
}

function setRecordDefinition(){
    if(null != grid){
        grid.addRecord(null, 'sequence', 'sequence', 'int');
        grid.addRecord(null, 'name', 'name', null);
        grid.addRecord(null, 'description', 'description', null);
        grid.addRecord(null, 'work', 'work', null);
        grid.addRecord(null, 'workUnits', 'workUnits', null);
        grid.addRecord(null, 'duration', 'duration', null);
        grid.addRecord(null, 'calculationType', 'calculationType', null);
        grid.addRecord(null, 'isMilestone', 'isMilestone', null);
        grid.addRecord('Y-m-d\\TH:i:s', 'startDateTime', 'startDate', 'date');
        grid.addRecord('Y-m-d\\TH:i:s', 'actualStart', 'actualStartDate', 'date');
        grid.addRecord('Y-m-d\\TH:i:s', 'endDateTime', 'endDate', 'date');
        grid.addRecord('Y-m-d\\TH:i:s', 'actualFinish', 'actualEndDate', 'date');
        grid.addRecord(null, 'workInHours', 'workInHours', 'float');
        grid.addRecord(null, 'workRemainingString', 'workRemainingString', null);
        grid.addRecord(null, 'durationInHours', 'durationInHours', 'float');
        grid.addRecord(null, 'workPercentComplete', 'workPercentComplete', 'float');
        grid.addRecord(null, 'workComplete', 'workCompleteString', null);
        grid.addRecord(null, 'assigneeString', 'assignees', null);
        grid.addRecord(null, 'phase_name', 'phase', null);
        grid.addRecord(null, 'priorityString', 'priority', null);
        grid.addRecord(null, 'idlist', 'idlist', null);
        grid.addRecord(null, 'project', 'project', null);
        grid.addRecord(null, 'type', 'type', null);
        grid.addRecord(null, 'id', 'id', 'int');
    }
}

function setColumns(){
    if(null != grid){
        grid.addColumn('sequence', '<display:get name="prm.schedule.list.sequence.column" />', true, null, 40, null);
        grid.addColumn('name', 'Assignment', false, nameRenderer, null, null);
        grid.addColumn('project', 'Project', false, null, null, null);
        grid.addColumn('type', 'Type', false, null, null, null);
        grid.addColumn('description', '<display:get name="prm.schedule.list.taskdescription.column" />', true, null, null, null);        
        grid.addColumn('startDate', '<display:get name="prm.schedule.list.startdate.column" />', true, Ext.util.Format.dateRenderer('n/j/y'), null, null);
        grid.addColumn('actualStartDate', '<display:get name="prm.schedule.list.actualstartdate.column" />', true, Ext.util.Format.dateRenderer('n/j/y'), null, null);
        grid.addColumn('endDate', 'Due Date', false, Ext.util.Format.dateRenderer('n/j/y'), null, null);
        grid.addColumn('actualEndDate', '<display:get name="prm.schedule.list.actualenddate.column" />', true, Ext.util.Format.dateRenderer('n/j/y'), null, null);
        grid.addColumn('workInHours', '<display:get name="prm.schedule.list.work.column" />', true, workRenderer, null, null);
        grid.addColumn('durationInHours', '<display:get name="prm.schedule.list.duration.column" />', true, durationRenderer, null, null);
        grid.addColumn('workComplete', '<display:get name="prm.schedule.list.workcomplete.column" />', true, null, null, null);
        grid.addColumn('workPercentComplete', '<display:get name="prm.schedule.list.complete.column" />', true, formatPercentRenderer, null, null);
        grid.addColumn('workRemainingString', 'Work Remaining', false, null, null, null);
        grid.addColumn('idlist', '<display:get name="prm.schedule.list.dependencies.column" />', true, dependencyRenderer, null, null);
        grid.addColumn('assignees', '<display:get name="prm.schedule.list.resources.column" />', true, null, null, null);
        grid.addColumn('phase', '<display:get name="prm.schedule.list.phase.column" />', true, null, null, null);
        grid.addColumn('priority', '<display:get name="prm.schedule.list.priority.column" />', true, null, null, null);
        grid.addColumn('calculationType', '<display:get name="prm.schedule.list.calculationtype.column" />', true, null, null, null);
        grid.addColumn('id', 'id', true, null, 40, null);
    }
}

	function changeTask(task) {
        var xmlHttpReq = false;
        var self = this;
        // Mozilla/Safari
        if (window.XMLHttpRequest) {
            self.xmlHttpReq = new XMLHttpRequest();
        }
        // IE
        else if (window.ActiveXObject) {
            self.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
        }
       
        self.xmlHttpReq.open('POST', root + "/personal/include/weblogsByTaskId.jsp?module=160", true);
        self.xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        self.xmlHttpReq.onreadystatechange = function() {
            if (self.xmlHttpReq.readyState == 4) {
                updatepage(self.xmlHttpReq.responseText);
            }
        }
        self.xmlHttpReq.send(getquerystring(task));
    }
   
    function getquerystring(task) {
        return "task=" + task;
    }
       
    function updatepage(str){
        document.getElementById("blog-div").innerHTML = str;
    }
	
	var projectMenu =
	[
	    ['', 'Dashboard', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit,
	    ['', 'Calendar', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit,
	    ['', 'Assignments', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit,
	    ['', 'Documents', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit,
	    ['', 'Forms', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit,
	    ['', 'Templates', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit,
	    ['', 'Setup', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit,
	    ['', 'Licensing', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', ''], _cmSplit,
	    ['', 'My Blog', '<%=SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE%>', '', '']
	];

</script>

</head>
<body onLoad="setup();" class="main">
<template:getSpaceMainMenu />

<div style="margin-top: 60px;">
	<div id="toolbar_top">
		<div id="toolbar_container">
			<div id="projectMenuId"></div>
		</div>
		<script type="text/javascript"><!--
			cmDraw ('projectMenuId', projectMenu, 'hbr', cmThemeOffice);
		--></script>
	</div>
</div>

<div id='content'>
	<div style="padding: 10px;">
		<div style="width: 100%; height: 10px;"></div>
		<div style="width: 100%;">
			<div style="float: right; width: 50%;">
				<span style="background-color: #9acbfe; color: #696c72; margin-right: 3px;">
					<span style="padding: 5px;">
						Blog 
					</span>
				</span>
				<span style="background-color: #d9e3ee; color: #696c72; margin-right: 3px;">
					<span style="padding: 5px;">
						Wiki 
					</span>
				</span>
				<span style="background-color: #d9e3ee; color: #696c72;">
					<span style="padding: 5px;">
						Links
					</span>
				</span>
			</div>
			<div style="clear: both;"></div>
		</div>
		<div style="width: 50%; float: left; height: 400px;">
			<div style="width: 100%; background-color: #d0d0d0;">
				<div style="padding: 3px;"> 
					<b>Assignments</b>
				</div>
			</div>
			<div id='schedule-grid'></div>
		</div>
		<div style="width: 50%; float: right; height: 400px;">
			<div style="width: 100%; background-color: #e9e9e9;">
				<div style="padding: 3px;"> 
					<b>new entry</b>
				</div>
			</div>
			<div id="blog-div">
<%

	ServiceFactory factory = ServiceFactory.getInstance();
	IBlogProvider blogProvider = factory.getBlogProvider();
	List<PnWeblogEntry> weblog = blogProvider.getWeblogEntriesFromProjectBlogByPerson(new Integer(user.getID()), null, new Date(), new Date(), WeblogConstants.STATUS_PUBLISHED, 0, 0);
	if((weblog != null) && (weblog.size() > 0)) {
		for(int i=0; i<weblog.size(); i++) {
			PnWeblogEntry entry = weblog.get(i);
%>
			<%= entry.getPubTimeString() %><br />
			<br /><p />
			<%= entry.getTitle() %><br />
			<br /><p />
			<br /><p />
			<%= entry.getText() %><br />
			<%= entry.getPnWeblogComment().size() %> comments: <a href="#">add a comment</a> | <a href="#">edit</a><br />
			<hr />
<%

		}
	}
%>
			</div>
		</div>
	</div>
</div>

<template:getSpaceJS />
</body>
</html>

