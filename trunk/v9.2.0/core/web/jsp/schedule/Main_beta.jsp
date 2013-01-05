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
            net.project.security.SessionManager,
            net.project.schedule.TaskType,
            net.project.base.Module,
            net.project.schedule.Schedule,
            net.project.schedule.ColumnVisibilityList,
            net.project.schedule.ColumnPositionList,
            java.net.URLEncoder,
            net.project.util.StringUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="schedule" class="net.project.schedule.Schedule" scope="session" />
<%
ColumnVisibilityList visibilityList = schedule.getColumnVisibilityList();
ColumnPositionList positionList = schedule.getColumnPositionList();
%>

<template:import type="javascript" src="/src/extjs/adapter/jquery/jquery.js" />
<template:import type="javascript" src="/src/components/BaseTreeGrid.js" />
<template:import type="javascript" src="/src/scheduleView.js" />

<style type="text/css">
    .x-grid3-header-offset{width:auto !important;}
    .x-grid3-row-table{width:auto !important;}
    .x-grid3-row{width:auto !important;}
    .x-grid3-cell-inner{white-space:normal;}
    .x-grid3-hd-inner{white-space:normal;}
    
    .tableFilterHeader  {
	    background-color: #DFE8F6 !important;
	 	color: #333399;
    }
    .tableContent  {
	    background-color: #DFE8F6;
    }
</style>

<script language="javascript">
    var scheduleModule = '<%=Module.SCHEDULE%>';
    var milestoneText = '<%=TaskType.MILESTONE.getName()%>';
    var criticalPathTitle = '<%=PropertyProvider.get("prm.schedule.list.criticalpath.message")%>';
    var startEndDateTitle = '<%=PropertyProvider.get("prm.schedule.list.startdate.title")%>';
    var generalError = '<%=PropertyProvider.get("prm.base.errors.message")%>';
    var waitTitle = '<%=PropertyProvider.get("prm.schedule.import.xml.pleasewait.title")%>';
    var filterTitle = '<%=PropertyProvider.get("prm.report.channel.filter.name")%>';
    var workplanTitle = '<%=PropertyProvider.get("prm.schedule.mainpage.title")%>';
    var ganttTitle = '<%=PropertyProvider.get("prm.schedule.main.view.option.gantt.name")%>';
    var save = '<%=PropertyProvider.get("all.global.toolbar.action.submit")%>';
    var cancel = '<%=PropertyProvider.get("all.global.toolbar.action.cancel")%>';
    var modifiedMessage = '<%=PropertyProvider.get("prm.schedule.main.modified.confirm.message")%>';
    var scheduleModifiedMessage = '<%=PropertyProvider.get("prm.schedule.main.modified.extended.confirm.message")%>';
    var workHourLimit = '<%=PropertyProvider.get("prm.schedule.main.limit.workhour.message")%>';
    var accessDeniedToQuickAddMsg='<%=PropertyProvider.get("prm.schedule.main.accessdenied.toquickadd.message")%>';
    var months = <%=StringUtils.getJsonMonthsString()%>;
    var totalTask = <%=schedule.getTaskList().size()%>;
    var scheduleUserId = '<%=SessionManager.getUser().getID()%>';
    var schedulePageOffset = readCookie('scheduleGridOffset_'+scheduleUserId);
    var executed = false;
    var isSelectionMade = false;
    var store;
    var schedulePagingToolBar;
    var isBlogEnabled = <%=PropertyProvider.getBoolean("prm.blog.isenabled")%>;
    var rightPanelWidth = '290';
    var rightPanelCollapsed = false;
	//Initialize quick tips.
   	Ext.QuickTips.init();
    // Variables used for workplan tabbed view
    var windowWidth, windowHeight, workplanPanel;

    var cp = {
        sequence: {index: <%=positionList.getPosition("sequence")%>, hidden: false},
        name: {index: <%=positionList.getPosition("name")%>, hidden: false},
        phase: {index: <%=positionList.getPosition("phase")%>, hidden: <%=!visibilityList.isVisible("phase")%>},
        priority: {index: <%=positionList.getPosition("priority")%>, hidden: <%=!visibilityList.isVisible("priority")%>},
        calculationType: {index: <%=positionList.getPosition("calculationType")%>, hidden: <%=!visibilityList.isVisible("calculationType")%>},
        startDate: {index: <%=positionList.getPosition("startDate")%>, hidden: <%=!visibilityList.isVisible("startDate")%>},
        actualStartDate: {index: <%=positionList.getPosition("actualStartDate")%>, hidden: <%=!visibilityList.isVisible("actualStartDate")%>},
        baselineStartDate: {index: <%=positionList.getPosition("baselineStartDate")%>, hidden: <%=!visibilityList.isVisible("baselineStartDate")%>},
        startVariance: {index: <%=positionList.getPosition("startVariance")%>, hidden: <%=!visibilityList.isVisible("startVariance")%>},
        endDate: {index: <%=positionList.getPosition("endDate")%>, hidden: <%=!visibilityList.isVisible("endDate")%>},
        actualEndDate: {index: <%=positionList.getPosition("actualEndDate")%>, hidden: <%=!visibilityList.isVisible("actualEndDate")%>},
        baselineEndDate: {index: <%=positionList.getPosition("baselineEndDate")%>, hidden: <%=!visibilityList.isVisible("baselineEndDate")%>},
        endVariance: {index: <%=positionList.getPosition("endVariance")%>, hidden: <%=!visibilityList.isVisible("endVariance")%>},
        work: {index: <%=positionList.getPosition("work")%>, hidden: <%=!visibilityList.isVisible("work")%>},
        baselineWork: {index: <%=positionList.getPosition("baselineWork")%>, hidden: <%=!visibilityList.isVisible("baselineWork")%>},
        workVariance: {index: <%=positionList.getPosition("workVariance")%>, hidden: <%=!visibilityList.isVisible("workVariance")%>},
        workComplete: {index: <%=positionList.getPosition("workComplete")%>, hidden: <%=!visibilityList.isVisible("workComplete")%>},
        duration: {index: <%=positionList.getPosition("duration")%>, hidden: <%=!visibilityList.isVisible("duration")%>},
        baselineDuration: {index: <%=positionList.getPosition("baselineDuration")%>, hidden: <%=!visibilityList.isVisible("baselineDuration")%>},
        durationVariance: {index: <%=positionList.getPosition("durationVariance")%>, hidden: <%=!visibilityList.isVisible("durationVariance")%>},
        workPercentComplete: {index: <%=positionList.getPosition("workPercentComplete")%>, hidden: <%=!visibilityList.isVisible("workPercentComplete")%>},
        statusNotifiers: {index: <%=positionList.getPosition("statusNotifiers")%>, hidden: <%=!visibilityList.isVisible("statusNotifiers")%>},
        resources: {index: <%=positionList.getPosition("resources")%>, hidden: <%=!visibilityList.isVisible("resources")%>},
        dependencies: {index: <%=positionList.getPosition("dependencies")%>, hidden: <%=!visibilityList.isVisible("dependencies")%>},
        wbs: {index: <%=positionList.getPosition("wbs")%>, hidden: <%=!visibilityList.isVisible("wbs")%>}
    };

    var scheduleView = null;
    var onSubmit = {
        url: JSPRootURL + '/servlet/ScheduleController',
        success: function (form, action) {
        	var checkBoxArray = new Array();
        	var isAllChecked = false;
		    checkBoxArray = theForm.getElementsByTagName('input');
		    for (var index = 0; index < checkBoxArray.length; index++) {
        		if (checkBoxArray[index].type == 'checkbox') {
            		isAllChecked = checkBoxArray[index].checked;
            	}
    		}
    		theForm.showAllTasks.checked = !isAllChecked;
    		isSelectionMade = scheduleView.getSelectedRowIds().length == 1;
    		setScheduleProperties(true);
        },
        failure: function (form, action) {
        	executed = false;
        	showResultForApplyFilter = false;
            if(action.failureType == Ext.form.Action.SERVER_INVALID && action.result.errors && action.result.errors.length) {
                var scheduleErrors = action.result.errors;
                var errorString = "";
                for (var i = 0; i < scheduleErrors.length; i++) {
                    errorString += scheduleErrors[i] + "<br/>";
                }
                extAlert(errorTitle, errorString , Ext.MessageBox.ERROR);
            } else {
                if(action.response.responseText.indexOf('Access denied to module')){
            		extAlert(errorTitle, accessDeniedToQuickAddMsg , Ext.MessageBox.ERROR);
            	}else{
                	extAlert(errorTitle, workHourLimit , Ext.MessageBox.ERROR);
                }
            }
        },
        waitMsg: waitTitle + ' ...',
        waitTitle: waitTitle
    };
    
function setupBeta(){
    if(isFlatView){
        setupScheduleView("flat");
    } else if(isIndentedView){
        setupScheduleView("indented");
    }
}

// Method for getting current window height and width
function getWindowHeightWidth(){
	if((navigator.userAgent.toLowerCase()).indexOf( "msie" ) != -1 ) {
		//IF IE
		if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
			windowWidth = document.documentElement.clientWidth;
			windowHeight = document.documentElement.clientHeight;
		} else {
		//IE 4 compatible
			windowWidth = document.body.clientWidth;
			windowHeight = document.body.clientHeight;
		}
	} else {
		//Non-IE
		windowWidth = window.innerWidth;
		windowHeight = window.innerHeight;
	}
}

// Calling method for panel resizing on window resize event
window.onresize = resizePanel;

// Method for resizing the panel with respect to window size
function resizePanel() {
	getWindowHeightWidth();
	workplanPanel.setHeight(windowHeight);
	filters.setWidth(scheduleView.getTreeGrid().getSize().width);
	scheduleView.getTreeGrid().getView().refresh();
}

// Method to initialize tabbed workplan view
function initializeWorkplanPanel(){
	// tab panel for gantt chart
	var ganttPanel = new Ext.Panel({
		id: 'gantt',
	    title: ganttTitle,
	    width: '100%',
	    height: '100%',
		html : '<iframe id="ganttChartPanel" frameborder="0" width="100%" height="100%" scrolling="auto" src="'+JSPRootURL+'/schedule/gantt/Gantt.jsp?module=160&action=<%=net.project.security.Action.VIEW%>"></iframe>'
	});
	
	var schedulePanel = new Ext.Panel({
		id: 'schedulePanel',
	    title: 'Schedule',
	    width: '100%',
	    height: '100%',
	    autoScroll: true,
	    contentEl:'schedule-grid'
	});
	    	
    document.getElementById('ddOpen').className='visible';
	
	// panel for filters
	filters = new Ext.Panel({
		contentEl : 'ddOpen',
		frame		: true,
		collapsible	: true,
		collapsed	: true,
		titleCollapse : true,
		autoScroll:  true,
       	title		: '<b>' + filterTitle + '</b>',
       	width		: scheduleView.getTreeGrid().getSize().width,
       	listeners	:{'expand':function(obj){leftPanel.setHeight(workplanPanel.getSize().height-2);},
       				 'collapse':function(obj){leftPanel.setHeight(workplanPanel.getSize().height);}}
   	});

	getWindowHeightWidth();

	// tab panel for work plan
	leftPanel = new Ext.TabPanel({
   		region : 'center',
		deferredRender : false,
        activeTab: 0,
        height: windowHeight,
    	tbar: [ filters ],
       	items: [ schedulePanel ],
       	bbar : [ schedulePagingToolBar ]
       	
	}); 
	blogPanel = new Ext.Panel({
	    	id: 'blog',
	        title: 'Blog',
	        autoScroll: true
    });
    rightPanel = new Ext.TabPanel({
                  	region : 'east',
                    activeTab : 0,
                    width : rightPanelWidth,
                    collapsible : true,
                    collapsed : rightPanelCollapsed,
              		split: true,
              		height: windowHeight-130
					
	});
	// main panel for both tabs - workplan and gantt chart
	workplanPanel = new Ext.Panel({
	 	resizable : true,
        height: windowHeight,
        layout: 'border',  
        items: [ leftPanel ]
     });
	if (isBlogEnabled){
		workplanPanel.add( rightPanel );
		if( isBlogEnabled ){
			rightPanel.add( blogPanel );
		}
	}
	workplanPanel.render('workplan-panel');
	leftPanel.add(ganttPanel);
	scheduleView.getTreeGrid().getStore().addListener('load', function(thisStore, records, options ){
		if(document.getElementById('ganttChartPanel')){
       		document.getElementById('ganttChartPanel').src = document.getElementById('ganttChartPanel').src+'&'+(new Date()).getTime();        
       	}
    });
    filters.setWidth(scheduleView.getTreeGrid().getSize().width);
    rightPanel.on('collapse',function(panel){
   	 	scheduleView.getTreeGrid().setWidth(leftPanel.getSize().width);
    	if(leftPanel.getActiveTab().id == 'gantt'){
    		filters.setWidth( ganttPanel.getSize().width);
    	} else {
    		filters.setWidth(scheduleView.getTreeGrid().getSize().width);
    	}
    });
    rightPanel.on('expand',function(panel){
    scheduleView.getTreeGrid().setWidth(leftPanel.getSize().width);
    	if(leftPanel.getActiveTab().id == 'gantt'){
    		filters.setWidth( ganttPanel.getSize().width);
    	} else {
    		filters.setWidth(scheduleView.getTreeGrid().getSize().width);
    	}
    });
    
}

function setupScheduleView(type) {
    if(null == scheduleView){
        scheduleView = new TreeGrid();
        setGridRecordDefinition(scheduleView);
        setColumns(scheduleView);
        
        scheduleView.init('schedule-grid', JSPRootURL +'/ajax/schedule/WorkplanRetrieve', "id");
        
        // initializing workplan tab panel
        initializeWorkplanPanel();
        loadBlogEntriesForAssignment('', false, 0, '', spaceId);
    }
}

function setGridRecordDefinition(grid){
    if(null != grid){
        grid.addRecord('_id', '_id', 'int');
        grid.addRecord('_level', '_level', 'int');
        grid.addRecord('_lft', '_lft', 'int');
        grid.addRecord('_rgt', '_rgt', 'int');
        grid.addRecord('_is_leaf', '_is_leaf', 'boolean');
        grid.addRecord('id', 'id', 'int');
        setBaseRecordDefinition(grid);
    }
}

function setBaseRecordDefinition(grid){
   if(null != grid) {
        grid.addRecord('sequence', 'sequence', 'int');
        grid.addRecord('name', 'name', 'string');
        grid.addRecord('phase', 'phase', 'string');
        grid.addRecord('priority', 'priority', 'string');
        grid.addRecord('calculationType', 'calculationType', 'string');
        grid.addRecord('startDate', 'startDate', 'date', 'n/j/y');
        grid.addRecord('actualStartDate', 'actualStartDate', 'date', 'n/j/y');
        grid.addRecord('baselineStartDate', 'baselineStartDate', 'date', 'n/j/y');
        grid.addRecord('startVariance', 'startVariance', 'float');
        grid.addRecord('startVarianceUnits', 'startVarianceUnits', null);
        grid.addRecord('endDate', 'endDate', 'date', 'n/j/y');
        grid.addRecord('actualEndDate', 'actualEndDate', 'date', 'n/j/y');
        grid.addRecord('baselineEndDate', 'baselineEndDate', 'date', 'n/j/y');
        grid.addRecord('endVariance', 'endVariance', 'float');
        grid.addRecord('endVarianceUnits', 'endVarianceUnits', null);
        grid.addRecord('work', 'work', 'float');
        grid.addRecord('workUnits', 'workUnits', null);
        grid.addRecord('baselineWork', 'baselineWork', 'float');
        grid.addRecord('baselineWorkUnits', 'baselineWorkUnits', null);
        grid.addRecord('workVariance', 'workVariance', 'float');
        grid.addRecord('workVarianceUnits', 'workVarianceUnits', null);
        grid.addRecord('workComplete', 'workComplete', 'float');
        grid.addRecord('workCompleteUnits', 'workCompleteUnits', null);
        grid.addRecord('duration', 'duration', 'float');
        grid.addRecord('durationUnits', 'durationUnits', null);
        grid.addRecord('baselineDuration', 'baselineDuration', 'string');
        grid.addRecord('baselineDurationUnits', 'baselineDurationUnits', null);
        grid.addRecord('durationVariance', 'durationVariance', 'float');
        grid.addRecord('durationVarianceUnits', 'durationVarianceUnits', null);
        grid.addRecord('workPercentComplete', 'workPercentComplete', 'float');
        grid.addRecord('statusNotifiers', 'statusNotifiers', null); 
        grid.addRecord('isMilestone', 'isMilestone', null);
        grid.addRecord('hasAssignments', 'hasAssignments', null);
        grid.addRecord('ATP', 'ATP', 'string');
        grid.addRecord('hasDependencies', 'hasDependencies', null);
        grid.addRecord('DTP', 'DTP', 'string');
        grid.addRecord('isDateConstrained', 'isDateConstrained', null);
        grid.addRecord('DCTP', 'DCTP', 'string');
        grid.addRecord('CriticalPath', 'CriticalPath', null);
        grid.addRecord('AfterDeadline', 'AfterDeadline', null);
        grid.addRecord('ADTP', 'ADTP', 'string');
        grid.addRecord('ExternalTask', 'ExternalTask', null);
        grid.addRecord('ETTP', 'ETTP', 'string');
        grid.addRecord('rowClass', 'rowClass', null);
        grid.addRecord('treeVisibility', 'treeVisibility', null);
        grid.addRecord('resources', 'resources', 'string');
        grid.addRecord('dependencies', 'dependencies', 'string');
        grid.addRecord('taskType', 'taskType', 'string');
        grid.addRecord('wbs', 'wbs', 'string');
    }
}

function setColumns(grid){
    if(null != grid){
        grid.addColumn(cp['sequence'].index, 'sequence', 'sequence', '<display:get name="prm.schedule.list.sequence.column" />', false, null, 40, null, true, false, true, true,'left');
        grid.addColumn(cp['name'].index, 'name', 'name', '<display:get name="prm.schedule.list.task.column" />', false, grid.nameRenderer, 180, grid.getEditor('name'), false, false, true, true,'left');
        grid.addColumn(cp['phase'].index, null, 'phase', '<display:get name="prm.schedule.list.phase.column" />', cp['phase'].hidden, null, 80, null, false, true, true, true,'left');
        grid.addColumn(cp['priority'].index, null, 'priority', '<display:get name="prm.schedule.list.priority.column" />', cp['priority'].hidden, null, 80, null, false, true, true, true,'left');
        grid.addColumn(cp['calculationType'].index, null, 'calculationType', '<display:get name="prm.schedule.list.calculationtype.column" />', cp['calculationType'].hidden, null, 100, null, false, true, true, true,'left');
        grid.addColumn(cp['startDate'].index, null, 'startDate', '<display:get name="prm.schedule.list.startdate.column" />', cp['startDate'].hidden, grid.dateRenderer, 80, grid.getEditor('startDate'), false, true, true, true,'left');
        grid.addColumn(cp['actualStartDate'].index, null, 'actualStartDate', '<display:get name="prm.schedule.list.actualstartdate.column" />', cp['actualStartDate'].hidden, grid.dateRenderer, 80, null, false, true, true, true,'left');
        grid.addColumn(cp['baselineStartDate'].index, null, 'baselineStartDate', '<display:get name="prm.schedule.list.baselinestartdate.column" />', cp['baselineStartDate'].hidden, grid.dateRenderer, 80, null, false, true, true, true,'left');       
        grid.addColumn(cp['startVariance'].index, null, 'startVariance', '<display:get name="prm.schedule.list.startvariance.column" />', cp['startVariance'].hidden, grid.timeRenderer, 80, null, false, true, true, true,'left');
        grid.addColumn(cp['endDate'].index, null, 'endDate', '<display:get name="prm.schedule.list.enddate.column" />', cp['endDate'].hidden, grid.dateRenderer, 80, grid.getEditor('endDate'), false, true, true, true,'left');
        grid.addColumn(cp['actualEndDate'].index, null, 'actualEndDate', '<display:get name="prm.schedule.list.actualenddate.column" />', cp['actualEndDate'].hidden, grid.dateRenderer, 80, null, false, true, true, true,'left');        
        grid.addColumn(cp['baselineEndDate'].index, null, 'baselineEndDate', '<display:get name="prm.schedule.list.baselineenddate.column" />', cp['baselineEndDate'].hidden, grid.dateRenderer, 80, null, false, true, true, true,'left');       
        grid.addColumn(cp['endVariance'].index, null, 'endVariance', '<display:get name="prm.schedule.list.endvariance.column" />', cp['endVariance'].hidden, grid.timeRenderer, 80, null, false, true, true, true,'left');
        grid.addColumn(cp['work'].index, null, 'work', '<display:get name="prm.schedule.list.work.column" />', cp['work'].hidden, grid.timeRenderer, 80, grid.getEditor('work'), false, true, true, true,'left');
        grid.addColumn(cp['baselineWork'].index, null, 'baselineWork', '<display:get name="prm.schedule.list.baselinework.column" />', cp['baselineWork'].hidden, grid.timeRenderer, 80, null, false, true, true, true,'left');
        grid.addColumn(cp['workVariance'].index, null, 'workVariance', '<display:get name="prm.schedule.list.workvariance.column" />', cp['workVariance'].hidden, grid.timeRenderer, 80, null, false, true, true, true,'left');
        grid.addColumn(cp['workComplete'].index, null, 'workComplete', '<display:get name="prm.schedule.list.workcomplete.column" />', cp['workComplete'].hidden, grid.timeRenderer, 80, grid.getEditor('workComplete'), false, true, true, true,'left');
        grid.addColumn(cp['duration'].index, null, 'duration', '<display:get name="prm.schedule.list.duration.column" />', cp['duration'].hidden, grid.timeRenderer, 80, grid.getEditor('duration'), false, true, true, true,'left');
        grid.addColumn(cp['baselineDuration'].index, null, 'baselineDuration', '<display:get name="prm.schedule.list.baselineduration.column" />', cp['baselineDuration'].hidden, grid.timeRenderer, 80, null, false, true, true, true,'left');        
        grid.addColumn(cp['durationVariance'].index, null, 'durationVariance', '<display:get name="prm.schedule.list.durationvariance.column" />', cp['durationVariance'].hidden, grid.timeRenderer, 80, null, false, true, true, true,'left');
        grid.addColumn(cp['workPercentComplete'].index, null, 'workPercentComplete', '<display:get name="prm.schedule.list.complete.column" />', cp['workPercentComplete'].hidden, grid.percentRenderer, 100, grid.getEditor('workPercentComplete'), false, true, true, true,'right');
        grid.addColumn(cp['statusNotifiers'].index, null, 'statusNotifiers', '<display:get name="prm.schedule.list.statusnotifiers.column" />', cp['statusNotifiers'].hidden, grid.extraImagesRenderer, 80, null, false, true, true, false,'left');
        grid.addColumn(cp['resources'].index, null, 'resources', '<display:get name="prm.schedule.list.resources.column" />', cp['resources'].hidden, null, 100, null , false, true, true, false,'left');
        grid.addColumn(cp['dependencies'].index, null, 'dependencies', '<display:get name="prm.schedule.list.dependencies.column" />', cp['dependencies'].hidden, null, 100, null, false, true, true, false,'left');
        grid.addColumn(cp['wbs'].index, null, 'wbs', '<display:get name="prm.schedule.list.wbs.column" />', cp['wbs'].hidden, null, 100, null, false, true, true, false,'left');
    }
}

//refresh the surrounding components and reloads grid with page offset
function setScheduleProperties(doCallback){
	var selectedSpans = Ext.DomQuery.select('div#ddClosed table tr.tableFilterContentWB td>span.tableFilterHeaderWB:next(span)');
 	var taskCount = '';
     Ext.Ajax.request({
              url:JSPRootURL + '/ajax/schedule/WorkplanAction',
              params: {workplanInfo : true ,all: true},
              method: 'POST',
              success: function(response, opts){
                  var workplan = Ext.util.JSON.decode(response.responseText);
                  for(var xx = 0; xx < selectedSpans.length; xx++) {
                      var next = Ext.get(selectedSpans[xx]).next('span', true);
                     	if(xx == 2)
                     	taskCount = workplan.workplanInfo[xx];
	                    next.innerHTML = workplan.workplanInfo[xx];
                  }
              },
              failure: function(response, opts){
              },
              callback: function(response, opts){
              	if(doCallback){
	              	if(taskCount != totalTask){
	              		totalTask = taskCount;
	              		if(isSelectionMade){
	              			isSelectionMade = false;
	              			reloadGridAsNeeded(readCookie('scheduleGridOffset_'+scheduleUserId));
	              		} else {
	              			var numberOfPages = Math.ceil(totalTask / schedulePageSize);
	              			var pageStart = schedulePageSize * (numberOfPages-1);
	              			reloadGridAsNeeded(pageStart);
	               		} 
	                } else {
		                reloadGridAsNeeded(readCookie('scheduleGridOffset_'+scheduleUserId));
	                }
                }
              }
              
     });
}

// Reloads grid depending on page offset
function reloadGridAsNeeded(pageOffset){
	if(!showResultForApplyFilter){
        schedulePagingToolBar.doLoad(pageOffset);
    } else {
        showResultForApplyFilter = false;
        schedulePagingToolBar.doLoad(0);
    }
}


</script>

<div id="workplan-panel" style="width:98%;"></div>
<div id="schedule-grid"></div>


