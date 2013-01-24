/* 
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
*/
var assignmentTreeNodeId = 0;
var blogPanel;
var wikiPanel;
var nodeSelect = false;
var assignmentTree;
var assignorOrAssigneeCombo;
var objectType;
var windowWidth = 0;
var windowHeight = 0;
var objectName, workSpaceName, workSpaceType;
var myColumnViewRecord;
var myColumnSizeViewRecord;
var taskSpaceId;
var rightPanel;
var leftPanel;
var isLastRequestCommitted = true;
var childTaskList='';//for list of all children of selected row (project/task) including that task/project id.
var listOfChildren = '';//list of sub task/children only
var rightPanelLastState = '';
var filterFormExpanded = false;

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.BLANK_IMAGE_URL = JSPRootURL+'/images/default/s.gif';
	
	getWindowHeightWidth();
	
	blogPanel = new Ext.Panel({
    	id: 'blog',
        title: 'Blog',
        autoScroll: true
    });
    
    /* wiki tab */
    wikiPanel = new Ext.Panel({
		id: 'wiki',
		title: 'Wiki',
		autoScroll: true,
		bbar: [{
			text:		'Wiki Menu',
			id:			'wikiMenuBtn',
            menu:		standardWikiMenu
		}]
    });
    
     leftPanel = new Ext.TabPanel({
                   		region : 'center',
                   		resizeTabs : true,
						deferredRender : false,
	                    activeTab: 0,
       		            //width: '70%',
               		    height: windowHeight-130,
               		    autoScroll: true,
                    	tbar:[ filterForm],
                    	items:[assignmentTreeGrid,{el:'my-timeline',id:'my-timeline',title:'Timeline&nbsp;<sup>(Beta)</sup>'}],
						listeners : {'tabchange' : onLeftTabPanelChange
						}
       		        });
       		                
   	rightPanel = new Ext.TabPanel({
                   		region : 'east',
	                    activeTab : 0,
	                    width : rightPanelWidth,
	                    collapsible : true,
	                    collapsed : rightPanelCollapsed,
	              		split: true,
               		    height: windowHeight-130,
						listeners : {'expand' : replacePanelState,
									'collapse': replacePanelState,
									'resize' : replaceRightPanelResizeState
						}
	});
	
	var assignmentPanel = new Ext.Panel({
            resizable :true,
            height: windowHeight-110,
			width : windowWidth-210,
            layout: 'border',
            items: [leftPanel]
        });

	//initializing Right Tab panel
	if ( isBlogEnabled || isWikiEnabled ){
		assignmentPanel.add( rightPanel );
		if( isBlogEnabled ){
			rightPanel.add( blogPanel );
		}
		if( isWikiEnabled ){
			rightPanel.add( wikiPanel );
		}
	}

     assignmentPanel.render('assignmentPanelPosition');
     
     myColumnViewRecord = initializeMyColumnViewRecord(myAssignmentsColumn);
     myColumnSizeViewRecord = initializeMyColumnViewRecord(myAssignmentsColumnSize);
	 
	 personalizeAssignmentColumn();
	 initializeTeeFlatToggleButtons();
	 
	 assignmentTreeGrid.getBottomToolbar().add('-',
	 	{iconCls: 'restore-default',
	 	  tooltip :'Restore default view',
		 handler: restoreDefault 
		},'-'
	 );
	 
	 //-----------Adding Listeners..------------------

	 filterForm.on('expand',function(panel){
	 	filterFormExpanded = true;
		assignmentTreeGrid.setHeight(windowHeight-355);
		document.getElementById('my-timeline').style.height = '55%';
		//Reload time line view if timeline tab is activated.
		if(leftPanel.getActiveTab().id == 'my-timeline'){
			showTimeLineView();
		}
	 });
	 
	 filterForm.on('collapse',function(panel){
	 	filterFormExpanded = false;
		assignmentTreeGrid.setHeight(windowHeight-127);
		leftPanel.setHeight(assignmentPanel.getSize().height+2);
		leftPanel.setHeight(windowHeight-110);
		document.getElementById('my-timeline').style.height = '99%';
		//Reload time line view if timeline tab is activated.
		if(leftPanel.getActiveTab().id == 'my-timeline'){
			showTimeLineView();
		}
	 });

	assignmentTreeGrid.getColumnModel().addListener('hiddenchange', replaceMyAssignmentColumn);
	
	//To Store column width of each column in my assignments grid
	assignmentTreeGrid.addListener('columnresize',replaceMyAssignmentsColumnSize);
	
	//To Store column width of each column in my assignments grid when user risie right panel
	//assignmentTreeGrid.addListener('resize',replaceMyAssignmentsColumnSize);
	
	//assignmentType filter selection initializing 
	//assignmentType.setValue(assignmentTypesValue);
	initializeAssignmentTypeMenu();
	initializeassigneeOrAssignorMenu();
	
	//WorkSpace filter selection initializing 
	workSpace.setValue(projectValue);
	if(businessValue != ''){
		workSpace.setDisabled(true);
	}
	initializeFilterCheckBoxes();
	
	
	//-------------- Widget resizeing on browser window resize ------------
	
	window.onresize = resizeGrid;
	function resizeGrid() {
		getWindowHeightWidth(); 	
		try{
			leftPanel.setHeight(windowHeight-110);
			rightPanel.setHeight(windowHeight-110);
			assignmentPanel.setHeight(windowHeight-110);
			assignmentPanel.setWidth(windowWidth-210);
		   }catch(err){}
	}
});

function loadWikiForAssignment(objName, spaceId) {
	disableWikiButtons();
	disableShowProjectPageIndexButton();
	
	objectTypeForWiki =  objectType == 'summary' ? 'task': objectType ;//Object type 'task' and 'summary' both are task.
	wikiPanel.load({
	    url: JSPRootURL+'/pwiki/WikiAjaxHandler/getPageContent?module='+moduleId,
		params: {spaceId: spaceId, objectId: assignmentTreeNodeId, objectType: objectTypeForWiki, objectName: objName, moduleId: moduleId, isPreview: 'false'},
	    method: 'POST',
	    discardUrl: false,
	    nocache: false,
	    text: "Loading...",
	    callback: function(){
			toggleEditing();							// upon retreiving the content - check should "Edit" button be displayed
			tableOfContentClickHandler();				// TOC click handler
	   	},
	    timeout: 30,
	    scripts: false
	});
}

function initializeTeeFlatToggleButtons(){
	if(assignmentViewParameter == 'flat'){
		toggleTreeFlat.setIconClass('tree-icon');
		toggleTreeFlat.setText('<b>Tree View</b>');
		toggleExpandCollapse.setDisabled(true);
		assignmentTreeGrid.getTopToolbar().items.get('groupby').setDisabled(true);
	} else {
		toggleTreeFlat.setIconClass('flat-icon');
		toggleTreeFlat.setText('<b>Flat View</b>');
		toggleExpandCollapse.setDisabled(false);
		assignmentTreeGrid.getTopToolbar().items.get('groupby').setDisabled(false);
	}
}
    
function initializeExpnadCollapseToggleButtons(){
	if(collapsed){
		toggleExpandCollapse.setIconClass('expandall-icon');
		toggleExpandCollapse.setText('<b>&nbsp;Expand All</b>');
	}else {
		toggleExpandCollapse.setIconClass('collapseall-icon');
		toggleExpandCollapse.setText('<b>&nbsp;Collapse All</b>');
	}
}

function replaceMyAssignmentColumn(thisColumnModel, columnIndex ,isHidden){
	var	value = (isHidden ? 0 : 1 );
		Ext.Ajax.request({
			url: JSPRootURL+'/assignments/My/replaceMyAssignmentColumn',
			params: {module: moduleId , column : thisColumnModel.getDataIndex(columnIndex), value: value},
			method: 'POST',
			success: function(result, request){
				myColumnViewRecord.set(thisColumnModel.getDataIndex(columnIndex), value);
			},
			failure: function(result, response){
				extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
			}
		});
		if(columnIndex == 1){
			assignmentTreeGrid.getView().refresh();
		}
}

function replacePanelState(panel){
	var	value = ( panel.isVisible() ? 0 : 1 );
	Ext.Ajax.request({
		url: JSPRootURL+'/assignments/My/replaceMyAssignmentsPanelState',
		params:{module:moduleId, value : value},
		method :'POST',
		success: function(result, request){
		},
		failure: function(result, response){
			extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		}
	});
}

function replaceRightPanelResizeState(panel){
	if(isLastRequestCommitted && rightPanel.isVisible()){
		isLastRequestCommitted = false;
		Ext.Ajax.request({
			url: JSPRootURL+'/assignments/My/replaceMyAssignmentsPanelResizeState',
			params:{module:moduleId, value : panel.getSize().width},
			method :'POST',
			success: function(result, request){
				isLastRequestCommitted = true;
				replaceMyAssignmentsColumnSize();
			},
			failure: function(result, response){
				isLastRequestCommitted = true;
			}
		});
	}
}

function replaceMyAssignmentsColumnSize(){
	var columnSize='';
	var columnName='';
	var cm = assignmentTreeGrid.getColumnModel();
	var count = cm.getColumnCount();
	
	for(var colIndex = 0; colIndex < count; colIndex++){
		columnName += cm.getDataIndex(colIndex) + ',';
		columnSize += cm.getColumnWidth(colIndex) + ',';
		myColumnSizeViewRecord.set(cm.getDataIndex(colIndex), cm.getColumnWidth(colIndex));
	}
	
	if(isLastRequestCommitted){
		isLastRequestCommitted = false;
		Ext.Ajax.request({
			url: JSPRootURL+'/assignments/My/replaceMyAssignmentsColumnSize',
			params:{module:moduleId , columnName : columnName , columnSize : columnSize},
			method :'POST',
			success: function(result, request){
				isLastRequestCommitted = true;
			},
			failure: function(result, response){
				isLastRequestCommitted = true;
			}
		});
	}
}

function restoreDefault(){
	myAssignmentsColumn = myAssignmentsColumnSize = [{'objectName' : null,'workSpace' : null,'dueDate' : null,'workRemaining' : null,'objectId' : null,'objectType' : null,'assigneeName' : null,'assignorName' : null,'myWork' : null,'myWorkComplete' : null,'myPercentComplete' : null,'startDate' : null,'actualStartDate' : null}];
	myColumnViewRecord = initializeMyColumnViewRecord(myAssignmentsColumn);
    myColumnSizeViewRecord = initializeMyColumnViewRecord(myAssignmentsColumnSize);
	personalizeAssignmentColumn();
	restoreDefaultAssignmetntPanelView();
}

function personalizeAssignmentColumn(){
	var cm = assignmentTreeGrid.getColumnModel();
	var count = cm.getColumnCount();
	var columnIndex;
	for(columnIndex = 0; columnIndex < count; columnIndex++){
		var hidden = getBoolean(cm.getDataIndex(columnIndex));
		cm.setHidden(columnIndex, hidden);
		if(!hidden){
			cm.setColumnWidth(columnIndex, getColumnWidth(cm.getDataIndex(columnIndex)));
		}
	}
}

function getColumnWidth(column){
	value = myColumnSizeViewRecord.get(column);
	if(value != null){
		return value;
	} else {
		if(column == 'objectName'){
			return 333;
		} else if(column == 'dueDate'){
			return 95;
		} else if(column == 'workRemaining'){
			return 95;
		} else{
			return 0;
		}
	}
}

function getBoolean(column){
	value = myColumnViewRecord.get(column);
	if(value == '0'){
		return true;
	} else if(value == '1'){
		return false;
	} else {
		if(column == 'objectName'){
			return false;
		} else if(column == 'dueDate'){
			return false;
		} else if(column == 'myWorkComplete'){
			return false;
		} else if(column == 'workRemaining'){
			return false;
		} else if(column == 'assignorName'){
			return false;
		} else{
			return true;
		}
	}
}

function restoreDefaultAssignmetntPanelView(){
	assignmentTreeGrid.setWidth(555);
	leftPanel.setWidth(559);
	rightPanel.setWidth(248);
	rightPanel.setPagePosition(750,101);
    rightPanel.toggleCollapse(false);
    rightPanel.expand(true);
}

function initializeMyColumnViewRecord(data){
 	var columnStore = new Ext.data.Store({
				autoLoad : true,
				reader: new Ext.data.JsonReader({id: '_id'},
				 Ext.data.Record.create([
				   		{name: 'objectName'},
				   		{name: 'workSpace'},
				     	{name: 'dueDate'},
				     	{name: 'workRemaining'},
				     	{name: 'objectId'},
				     	{name: 'objectType'},
				     	{name: 'assigneeName'},
				     	{name: 'assignorName'},
				     	//Additional Column
				     	{name: 'myWork'},
						{name: 'myWorkComplete'},
						{name: 'myPercentComplete'},
						{name: 'startDate'},
						{name: 'actualStartDate'}
				   	])
				 ),
	            data: data
	   });
	return columnStore.getAt(0);
}

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
	}else{
		//Non-IE
		windowWidth = window.innerWidth;
		windowHeight = window.innerHeight;
	}
}

function onLeftTabPanelChange(thisPanel, tab){
	//check if filter is to display for tab.
	if(tab.id == 'my-timeline' || tab.id == 'assignments'){
		filterForm.enable();
	}else{
		filterForm.disable();
		filterForm.removeClass('x-item-disabled');
	}
	//load time line only if time line tab is visible.
	if(tab.id == 'my-timeline'){
		showTimeLineView();
		reInitializeRightTabPanel();
	}
	//Addjust grid panel and time line panel
	if(filterFormExpanded){
		assignmentTreeGrid.setHeight(windowHeight-355);
	}
	
	//close all timeline bubble.
	if(timeLineLoaded){
		timeLine.getBand(0).closeBubble();
		timeLine.getBand(1).closeBubble();
	}
}

function initializeFilterCheckBoxes(){
	if(lateAssignmentCheck || comingDueDateCheck || shouldHaveStartCheck || inProgressCheck){
		lateAssignment.setValue(lateAssignmentCheck);
		assignmentComingDue.setValue(comingDueDateCheck);
		shouldHaveStart.setValue(shouldHaveStartCheck);
		inProgress.setValue(inProgressCheck);
	} else{
		allAssignments.setValue(true);
	}
}

function initializeAssignmentTypeMenu(){
	assignmentTypeMenu.items.each(function(object){
		if(assignmentTypesValue.length == 1 && object.checked){
			object.setDisabled(true);
		}
	});
}

function initializeassigneeOrAssignorMenu(){
	assignorOrAssigneeMenu.items.each(function(object){
		if(assigneeOrAssignorParameter.length < 16 && object.checked){
			object.setDisabled(true);
		}
	});
}
//To add LateAssignmnets tab
function showLateAssignmnets(){
	if(isNewTabToAdd('late-assignments')){
		leftPanel.add(getNewTreeGrid('Late Assignments','late-assignments')).show();
	}
}
//To add ComingDue tab
function showComingDueAssignmnets(){
	if(isNewTabToAdd('coming-due')){
		leftPanel.add(getNewTreeGrid('Coming Due','coming-due')).show();
	}
}
//To add Should Have Started tab
function showShouldHaveStartedAssignmnets(){
	if(isNewTabToAdd('should-have-started')){
		leftPanel.add(getNewTreeGrid('Should Have Started','should-have-started')).show();
	}
}
//To add InProgress tab
function showInProgressAssignmnets(){
	if(isNewTabToAdd('in-progress')){
		leftPanel.add(getNewTreeGrid('In Progress','in-progress')).show();
	}
}

//To add All Assignments tab
function showAllAssignmnets(){
	if(isNewTabToAdd('all-assignments')){
		leftPanel.add(getNewTreeGrid('All Assignments','all-assignments')).show();
	}
}

//To add Completed Assignment tab
function showCompletedAssignmnets(){
	if(isNewTabToAdd('complete-assignments')){
		leftPanel.add(getNewTreeGrid('Complete Assignments','complete-assignments')).show();
	}

}

//To add All assigned by me tab
function showAllAssignedByMe(){
	if(isNewTabToAdd('all-assigned-by-me')){
		leftPanel.add(getNewTreeGrid('Assigned By Me','all-assigned-by-me')).show();
	}
}

//To check new tab is to add or to show added tab.
function isNewTabToAdd(tabId){
	var newTabToAdd = true;
	leftPanel.items.each(function(object){
		if(object.id == tabId){
			newTabToAdd = false;
			object.show();
		}
	});
	return newTabToAdd;
}
