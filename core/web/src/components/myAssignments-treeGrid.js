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
var rootTitle ='<a href="'+JSPRootURL+'/personal/Main.jsp?page='+ JSPRootURL + '/assignments/My?module='+moduleId+'" id="ux-maximgb-treegrid-brditem-" class="ux-maximgb-treegrid-brditem">'+userDisplayName+' </a>/ Assignments'
var assignmentTreeGrid ;
var collapsed = true;
var toggleExpandCollapse;
var toggleTreeFlat;
var groupMenu;
var assignmentTypeMenu;
var record;
var columnModel;
var updatedGridIds;
var Controller = function(){
	function createGrid(){
		groupMenu = new Ext.menu.Menu({
	        id: 'groupMenu',
	        items: [
	            {
	                text: 'Business',
	                checked: isGroupMenuItemChecked('Business'),
	                checkHandler: onGroupItemCheck
	            },
	            {
	                text: 'Project',
	                checked: isGroupMenuItemChecked('Project'),
	                checkHandler: onGroupItemCheck
	            },
	            {
	                text: 'Assignment',
	                checked:true,
	                disabled: true
	                //checkHandler: onGroupItemCheck
	            }]
         });
         
         assignmentTypeMenu = new Ext.menu.Menu({
	        id: 'assignmentType',
	        items: [
	        	/*{
	                text: 'All',
	                id: 'all',
	                checked: isAssignmentTypeMenuItemChecked('all'),
	                handler : onAssignmentTypeItemCheck
	            },*/
	            {
	                text: 'Meeting',
	                id: 'meeting',
	                checked: isAssignmentTypeMenuItemChecked('meeting'),
	                checkHandler : onAssignmentTypeItemCheck
	            },
	            {
	                text: 'Task',
	                id: 'task',
	                checked: isAssignmentTypeMenuItemChecked('task'),
	                checkHandler : onAssignmentTypeItemCheck
	            },
	            /*{
	                text: 'Activity',
	                id: 'activity',
	                checked: isAssignmentTypeMenuItemChecked('activity'),
	                checkHandler : onAssignmentTypeItemCheck
	            },*/
	            {
	                text: 'Form',
	                id: 'form_data',
	                checked: isAssignmentTypeMenuItemChecked('form_data'),
	                checkHandler : onAssignmentTypeItemCheck
	            }]
         });
         
         assignorOrAssigneeMenu = new Ext.menu.Menu({
	        id: 'assignmentOf',
	        items: [
	            {
	                text: 'Assigned to me',
	                id: 'assignee',
	                checked: isAssignorOrAssigneeMenuItemChecked('assignee'),
	                checkHandler: onAssignorOrAssigneeMenuItemCheck
	            },
	            {
	                text: 'Assigned by me',
	                id: 'assignor',
	                checked: isAssignorOrAssigneeMenuItemChecked('assignor'),
	                checkHandler: onAssignorOrAssigneeMenuItemCheck
	            }]
         });
		
	    record = Ext.data.Record.create([
	   		{name: 'objectName',type: 'string'},
	   		{name: 'workSpace',type: 'string'},
	     	{name: 'dueDate',type: 'date'},
	     	{name: 'workRemaining',type: 'float'},
	     	{name: 'objectId', type: 'int'},
	     	{name: 'spaceId', type: 'int'},
	     	{name: 'objectType', type: 'string'},
	     	{name: 'assigneeName', type: 'string'},
	     	{name: 'assignorName', type: 'string'},
	     	
	     	//Some Additional column
	     	{name: 'myWork', type: 'float'},
	     	{name: 'myWorkComplete', type: 'float'},
	     	{name: 'myPercentComplete', type: 'float'},
	     	{name: 'startDate', type: 'date'},
	     	{name: 'actualStartDate', type: 'date'},
	     	{name: 'baseObjectType', type: 'string'},
	     	
	     	//TeeView related column
	     	{name: '_id', type: 'int'},
	     	{name: '_level', type: 'int'},
	     	{name: '_lft', type: 'int'},
	     	{name: '_rgt', type: 'int'},
	     	{name: '_is_leaf', type: 'bool'}
	   	]);

	   var store = new Ext.ux.maximgb.treegrid.NestedSetStore({
				//autoLoad : true,
				url: JSPRootURL+'/assignments/My/getAssignmentsTreeData?module='+moduleId,
				reader: new Ext.data.JsonReader({
							id: '_id',
							root: 'data',
							totalProperty: 'total',
							successProperty: 'success'
						},
						record
				),
				listeners : {'load' : function( thisStore, records, options ) {
						assignmentTreeGrid.getSelectionModel().clearSelections();
						collapsed = false;
						getGridTreeState();
						filterForm.disable();
						filterForm.enable();
						reInitializeRightTabPanel();
						updatedGridIds = remvoeId(updatedGridIds, assignmentTreeGrid.id);
				   		//reload Timeline tab panel if it is activated. otherwise no need to load this panel, it is autometically get reloaded on tab activate.
				   		if(leftPanel.getActiveTab().id == 'my-timeline'){
							showTimeLineView();
						}
					}
				}
		});
	   
		columnModel = [
				{id:'objectName', header: "Assignment Name", renderer: renderFontColor, width: 310, sortable: true, dataIndex: 'objectName', hideable : false},
		        {id:'objectType', header: "Type", renderer: renderObjectType, hidden: true, width: 95, sortable: true, dataIndex: 'objectType'},
				{id:'workSpace', header: 'Work Space', sortable: true, width: 95, dataIndex: 'workSpace'},
		        {id:'startDate', header: 'Start Date', renderer: renderDate, hidden: true, sortable: true, dataIndex: 'startDate'},
				{id:'dueDate', header: 'Due Date', renderer: renderDate, width: 95, sortable: true, dataIndex: 'dueDate'},
				{id:'actualStartDate', header: 'Actual Start', renderer: renderDate, width: 95, hidden: true, sortable: true, dataIndex: 'actualStartDate'},
		        {id:'myPercentComplete', header: '% Complete', renderer: renderPercent, width: 95, hidden: true, sortable: true, dataIndex: 'myPercentComplete'},
		        {id:'myWork', header: 'Work', renderer: renderWork, hidden: true, width: 95, sortable: true, dataIndex: 'myWork'},
		        {id:'myWorkComplete', header: 'Work Complete', renderer: renderWork, width: 95, hidden: true, sortable: true, dataIndex: 'myWorkComplete'},
				{id:'workRemaining', header: 'Work Remaining', renderer: renderWorkRemaining, width: 95, sortable: true, dataIndex: 'workRemaining'},
		        {id:'assigneeName', header: 'Assignee', renderer: removeNull, width: 95, hidden: true, sortable: true, dataIndex: 'assigneeName'},
		        {id:'assignorName', header: 'Assignor', renderer: removeNull, width: 95, hidden: true, sortable: true, dataIndex: 'assignorName'}
		    ];

		assignmentTreeGrid = new Ext.ux.maximgb.treegrid.GridPanel({
		    store: store,
			master_column_id : 'objectName',
			columns: columnModel,
		    sm: new Ext.grid.RowSelectionModel( {singleSelect:true, listeners : {'rowselect' : onGridRowSelct} }),
			stripeRows: true,
			autoExpandColumn:'objectName',
			title: 'Assignments',
			id: 'assignments',
			root_title: rootTitle,
			height:520,
			width:505,
			autoScroll: true,
			loadMask: true,
			viewConfig : {
                enableRowBody : true,
                forceFit: true
            },
            listeners: {'rowdblclick': onGridRowDoubleClick, 'activate' : onGridActivate },
            tbar:[
            	toggleTreeFlat = new Ext.Button({
                   		iconCls: 'flat-icon',
                   		text : '<b>Flat View</b>',
   	                	tooltip:'<b>Tree view/ Flat view</b>',
                    	handler: function (){
	                    	getTreeOrFlatView( assignmentViewParameter != 'flat' ? 'flat' : 'indent');
                    	}
                   	}), '-',
                 toggleExpandCollapse = new Ext.Button({
				       iconCls: 'expandall-icon',
				       text:	'<b>&nbsp;Expand All</b>',
				       tooltip: '<b>Expand all/ Collapse all</b>',
				       handler : function(){
          						if( collapsed){ 
          							collapsed = false;
          							expandAllNodes(store);
          						} else {
          							collapsed = true;
          							collapseAllNodes(store);
          						}
          						replaceGridTreeState(collapsed);
    					}
				}), '-',
				{
		            text:'<b>Group By</b>',
		            iconCls: 'group-icon',
		            id:'groupby',
		            tooltip: '<b>Group by Business, Project and Assignment</b>',
		            menu: groupMenu  
		        }, '-',
		        {
		        	text:'<b>Assignment Type</b>',
		            iconCls: 'assignment-type',
		            id:'assignmentType',
		            tooltip: '<b>Filter assignment type</b>',
		            menu: assignmentTypeMenu
		        },
		        {
			        text:'<b>Assignment</b>',
		            iconCls: 'assignment-of',
		            id:'assignorOrAssignee',
		            tooltip: '<b>Assignment: Assigned to me and Assigned by me .</b>',
		            menu: assignorOrAssigneeMenu
		        }
            ]
            ,
			bbar: new Ext.ux.maximgb.treegrid.PagingToolbar({
		      	store: store,
		      	displayInfo: true,
		      	displayMsg: '<b>Total Assignments: '+totalFilteredAssignments+' </b>',
		      	emptyMsg: '<b>Total Assignments: '+totalFilteredAssignments+' </b>',
		      	pageSize: defaultPageSize
			})
		});
 	}
	return {init : function(){
			createGrid();
			assignmentTreeGrid.getBottomToolbar().doLoad(startDisplayFrom);
		}
	}
}();

Ext.onReady(Controller.init);

function removeNull(val){
	return (val == 'null' ? '' : val);
}

function renderPercent(val, params, record){
	if(record.get('baseObjectType') == 'task'  || record.get('baseObjectType') == 'summary' || record.get('baseObjectType') == 'form_data'){
		return ( isNaN(val) || val == '' ? '0%' :(val * 100).toFixed(2)+'%');
	}else{
		return '';
	}
}

function renderDate(theday, params, record){
	if(theday && theday != ''){
		today = new Date();
		
		if(today.getDate() == theday.getDate() && today.getMonth() == theday.getMonth() && today.getFullYear() == theday.getFullYear()){
			return 'Today';
		}
		if(today.getDate() + 1 == theday.getDate() && today.getMonth() == theday.getMonth() && today.getFullYear() == theday.getFullYear()){
			return 'Tomorrow';
		}
		if(today.getDate() - 1 == theday.getDate() && today.getMonth() == theday.getMonth() && today.getFullYear() == theday.getFullYear()){
			return 'Yesterday';
		}
		if(today.getFullYear() == theday.getFullYear()){
			return  months[theday.getMonth()]+' '+theday.getDate();
		} else {
			return months[theday.getMonth()]+' '+ theday.getDate() + ', ' + theday.getFullYear();
		}
	} else {
		return '';
	}
	
}

function renderWork(work, params, record){
	if(record.get('baseObjectType') == 'task' || record.get('baseObjectType') == 'summary' || record.get('baseObjectType') == 'form_data'){
		if(!isNaN(work) && work != ''){
			if(work > 160 && work < 480) {
				return work / 8 + ' days';
			} else if (work > 480) {
				return  work / 40 + ' wks';
			} else {
				return work + ' hrs';
			}
		} else {
			return '0 hrs';
		}
	}else{
		return '';
	}
}

function renderWorkRemaining(work, params, record){
	return ( work < 0 ? '0 hrs' :renderWork(work, params, record));
}

function renderFontColor(name, params, record){
	// Add object type as quick tip if type column is hidden.
	if(assignmentTreeGrid.getColumnModel().isHidden(1)){
		params.attr = 'ext:qtip="'+record.get('objectType')+'"';
	}
	if(record.get('objectType') == 'business'){
		return '<font color="green">'+ name +'</font>';
	} else if(record.get('objectType') == 'project') {
		return '<font color="blue">'+ name +'</font>';
	} else if(record.get('objectType') == 'non_business'){
		return '<font color="green"><I>'+ name +'</I></font>';
	} else if(assignmentTreeGrid.getColumnModel().isHidden(1) && record.get('objectType') == 'task'){
		return "<img src='"+JSPRootURL+"/src/extjs/resources/images/default/tree/t.gif'/>&nbsp;"+ name;
	} else if(assignmentTreeGrid.getColumnModel().isHidden(1) && record.get('objectType') == 'meeting'){
		return "<img src='"+JSPRootURL+"/src/extjs/resources/images/default/tree/m.gif'/>&nbsp;"+ name;		
	} else if(assignmentTreeGrid.getColumnModel().isHidden(1) && record.get('baseObjectType') == 'form_data'){
		return "<img src='"+JSPRootURL+"/src/extjs/resources/images/default/tree/f.gif'/>&nbsp;"+ name;
	} else {
		return name;
	}
}

function renderObjectType (name, params, record){
	return (name == 'non_business' ? '' : name );

}

function isGroupMenuItemChecked(groupBy){
	return groupingParameter.indexOf(groupBy) >= 0;
}

function isAssignmentTypeMenuItemChecked(type){
	return assignmentTypesValue.toString().indexOf(type) >= 0;
}

function isAssignorOrAssigneeMenuItemChecked(assignmentOf){
	return assigneeOrAssignorParameter.indexOf(assignmentOf) >= 0;
}

function getTreeOrFlatView(view){
	assignmentViewParameter = view;
	initializeTeeFlatToggleButtons();
	Ext.Ajax.request({
		   url: JSPRootURL+'/assignments/My/setViewParameter',
		   params: {module: moduleId , view: view },
		   method: 'POST',
		   success: function(result, request){
		   		nodeSelect = false;
		   		assignmentTreeGrid.getStore().reload();
		   		assignmentTreeGrid.getView().refresh();
	   			toggleExpandCollapse.setIconClass('expandall-icon');
		   },
		   failure: function(result, response){
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
	});
}

function onGroupItemCheck(item, checked){
	Ext.Ajax.request({
	   url: JSPRootURL+'/assignments/My/setGroupingParameter',
	   params: {module: moduleId , groupingField : item.text , toGroup: checked},
	   method: 'POST',
	   success: function(result, request){
	   		assignmentTreeGrid.getStore().reload();
	   		assignmentTreeGrid.getView().refresh();
	   },
	   failure: function(result, response){
		   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
	   }
	});
}

function onAssignorOrAssigneeMenuItemCheck(){
	var assignmentOf = '';
	var itemChecked = 0;
	assignorOrAssigneeMenu.items.each(function(object){
		if(object.checked){
			assignmentOf += object.id;
			itemChecked ++;
		}
	});
	assignorOrAssigneeMenu.items.each(function(object){
		if(itemChecked == 1 && object.checked){
			object.setDisabled(true);
		}else {
			object.setDisabled(false);
		}
	});
	
	Ext.Ajax.request({
		   url: JSPRootURL+'/assignments/My/setAssignorOrAssigneeParameter',
		   params: {module: moduleId , assignmentOf : assignmentOf},
		   method: 'POST',
		   success: function(result, request){
		   		refreshAssignmentGridAndCountFromStart(true);
		   },
		   failure: function(result, response){
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
	});
}

function onAssignmentTypeItemCheck(item, checked){
	var assignmentTypes = '';
	var itemChecked = 0;
	assignmentTypeMenu.items.each(function(object){
		if(object.checked){
			assignmentTypes += object.id + ',';
			itemChecked ++;
		}
	});
	assignmentTypeMenu.items.each(function(object){
		if(itemChecked == 1 && object.checked){
			object.setDisabled(true);
		}else {
			object.setDisabled(false);
		}
	});
	
	Ext.Ajax.request({
		   url: JSPRootURL+'/assignments/My/setAssignmentTypeParameter',
		   params: {module: moduleId , assignmentTypes : assignmentTypes},
		   method: 'POST',
		   success: function(result, request){
		   		refreshAssignmentGridAndCountFromStart(true);
		   },
		   failure: function(result, response){
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
	});
}



function expandAllNodes(store){
	store.data.each(function(record){
		if(!store.isLeafNode(record) && store.isLoadedNode(record) && record.get('baseObjectType') != "temporary_task" ){store.expandNode(record);}
	});
	initializeExpnadCollapseToggleButtons();
}
	
function collapseAllNodes(store){
	store.data.each(function(record){
		if(!store.isLeafNode(record) && store.isLoadedNode(record)){store.collapseNode(record);}
	});
	initializeExpnadCollapseToggleButtons();
}

function getNewTreeGrid(name, id){
	var store = getGridStoreByGridId(id);
	return new Ext.ux.maximgb.treegrid.GridPanel({
			    store: store,
			    closable: true,
			    //closeAction : 'hide',
				master_column_id : 'objectName',
				columns: columnModel,
			    sm: new Ext.grid.RowSelectionModel( {singleSelect: true, listeners: {'rowselect' : onGridRowSelct} }),
				stripeRows: true,
				autoExpandColumn:'objectName',
				title: name,
				id: id,
				root_title: rootTitle,
				autoHieght: true,
				height:550,
				width:505,
				autoScroll: true,
				loadMask: true,
				listeners: {'rowdblclick': onGridRowDoubleClick, 'activate' : onGridActivate},
		        tbar: [getExpandCollapseButtonByStore(store)],
				bbar: getPagingToolbarByStore(store)
			});
}

function getExpandCollapseButtonByStore(store){
	return new Ext.Button({
			       iconCls: 'collapseall-icon',
			       text:	'<b>&nbsp;Collapse All</b>',
			       tooltip: '<b>Expand all/ Collapse all</b>',
			       disabled: (assignmentViewParameter == 'flat'),
			       listeners : {'click' : function( thisBtn, e) {
								if((thisBtn.text).indexOf('Expand') >= 0){
									expandAllNodes(store);
									thisBtn.setIconClass('collapseall-icon');
									thisBtn.setText('<b>&nbsp;Collapse All</b>');
								}else{
									collapseAllNodes(store);
									thisBtn.setIconClass('expandall-icon');
									thisBtn.setText('<b>&nbsp;Expand All</b>');
								}
							}
					}
    			});
}

function getPagingToolbarByStore(store){
	return new Ext.ux.maximgb.treegrid.PagingToolbar({
		   	store: store,
		   	displayInfo: false,
		   	pageSize: defaultPageSize
		});
}

function getGridStoreByGridId(gridId){
	return new Ext.ux.maximgb.treegrid.NestedSetStore({
				autoLoad : true,
				url: getStoreUrlById(gridId),
				reader: new Ext.data.JsonReader({
							id: '_id',
							root: 'data',
							totalProperty: 'total',
							successProperty: 'success'
						},
						record
				),
				listeners : {'load' : onGridStoreLoad}
			});
}

function getStoreUrlById(id){
	if(id == 'late-assignments'){
		return JSPRootURL+'/assignments/My/getLateAssignmentsTreeData?module='+moduleId;
	}
	if(id == 'coming-due'){
		return JSPRootURL+'/assignments/My/getComingDueAssignmentsTreeData?module='+moduleId;
	}
	if(id == 'should-have-started'){
		return JSPRootURL+'/assignments/My/getShouldHaveStartedAssignmentsTreeData?module='+moduleId;
	}
	if(id == 'in-progress'){
		return JSPRootURL+'/assignments/My/getInProgressAssignmentsTreeData?module='+moduleId;
	}
	if(id == 'all-assignments'){
		return JSPRootURL+'/assignments/My/getAllAssignmentsTreeData?module='+moduleId;
	}
	if(id == 'complete-assignments'){
		return JSPRootURL+'/assignments/My/getCompletedAssignmentsTreeData?module='+moduleId;
	}
	if(id == 'all-assigned-by-me'){
		return JSPRootURL+'/assignments/My/getAllAssignedByMeTreeData?module='+moduleId;
	}
}

function onGridRowSelct(thisSelectionModel, rowIndex, record){
	assignmentTreeNodeId = record.get('objectId');
	taskSpaceId = record.get('spaceId');
	objectType = record.get('baseObjectType');
	objectName = record.get('objectName');
	workSpaceName = record.get('workSpace');
	// get all children of selected row
	if (objectType != 'non_business' && objectType != 'project' && assignmentTreeGrid.getStore().getNodeParent(record) != null){	
		workSpaceType = assignmentTreeGrid.getStore().getNodeParent(record).get('baseObjectType');
	} else {
		workSpaceType = '';
	}
	if(objectType != 'business' && workSpaceType != 'business' && objectType != 'non_business' ){	
		childTaskList = assignmentTreeNodeId;
		getNodeChildrens(assignmentTreeGrid, record);
	}else{
		childTaskList = '-1';
	}
	if( isBlogEnabled ){
 		loadBlogEntriesForAssignment('myAssignments', needRefresh, assignmentTreeNodeId, childTaskList, taskSpaceId);
 	}
	
	/* Load wiki after activate wiki tab. */
	if(isWikiEnabled ){
		if(rightPanel.getActiveTab().id == 'wiki'){
			loadWikiForAssignment(objectName, taskSpaceId);
		} else{
			rightPanel.setActiveTab(1);
			loadWikiForAssignment(objectName, taskSpaceId);
			rightPanel.setActiveTab(0);
		}
  	}
}

// to generate a list of children tasks
function getNodeChildrens(grid, rec) {
	var childs = grid.getStore().getNodeChildren(rec);
    for(var index = 0; index < childs.length; index++) {
		childTaskList += "," + childs[index].get('objectId');
		getNodeChildrens(grid, childs[index]);
	}
}

function onGridRowDoubleClick(grid, rowIndex, e){
	assignmentTreeNodeId = grid.getStore().getAt(rowIndex).get('objectId');
	objectType = grid.getStore().getAt(rowIndex).get('baseObjectType');
	objectName = grid.getStore().getAt(rowIndex).get('objectName');
	workSpaceName = grid.getStore().getAt(rowIndex).get('workSpace');
	goToObjectDetailPage(objectType);
}
	
function goToObjectDetailPage(objectType) {
	if(objectType.toString() == 'project') {
		window.location.href = JSPRootURL+'/project/Main.jsp?id='+assignmentTreeNodeId+'&page='+JSPRootURL+'/project/Dashboard?id='+assignmentTreeNodeId;
	} else if (objectType.toString() == 'business'){
		window.location.href = JSPRootURL+'/business/Main.jsp?id='+assignmentTreeNodeId;
	} else if(objectType.toString() == 'task' || objectType.toString() == 'summary' || objectType.toString() == 'temporary_task'){
       	window.location.href = JSPRootURL+'/servlet/AssignmentController/TaskView?module=60&action=1&id='+assignmentTreeNodeId+'&refLink=/assignments/My?module='+moduleId;
	} else if(objectType.toString() == 'form_data'){
		window.location.href = JSPRootURL+'/form/FormEdit.jsp?id='+assignmentTreeNodeId+'&module=30&action=1&spaceID='+ taskSpaceId +'&redirectedFromSpace=true';
	} else if(objectType.toString() == 'meeting' || objectType.toString() == 'activity'){
		window.location.href = JSPRootURL+'/calendar/MeetingManager.jsp?id='+assignmentTreeNodeId+'&action=1&module=70';
	} else if(objectType.toString() == 'person'){
		window.location.href = JSPRootURL+'/personal/profile/'+assignmentTreeNodeId+'?module=160';
	} else {
		extAlert('Error', 'Details are not available for this object.', Ext.MessageBox.ERROR);
	}		
}

function onGridActivate(panel){
	//close filter for all on click filter tab.
	if(filterFormExpanded && panel.id != 'assignments'){
		filterForm.collapse(false);
	}
	assignmentTreeGrid = panel;
	assignmentTreeGrid.getSelectionModel().clearSelections();
	reInitializeRightTabPanel();
	refreshPanel(panel);
}

function onGridStoreLoad(thisStore, records, options ) {
	assignmentTreeGrid.getSelectionModel().clearSelections();
	updatedGridIds = remvoeId(updatedGridIds, assignmentTreeGrid.id);
	expandAllNodes(thisStore);
	reInitializeRightTabPanel();
}

function reInitializeRightTabPanel(){
	try{
		assignmentTreeNodeId = 0;
		if(isBlogEnabled){
			loadBlogEntriesForAssignment('myAssignments', needRefresh, assignmentTreeNodeId, childTaskList, taskSpaceId);
		}
		if(isWikiEnabled){
			if(rightPanel.getActiveTab().id == 'wiki'){
				loadWikiForAssignment();
			} else{
				rightPanel.setActiveTab(1);
				loadWikiForAssignment();
				rightPanel.setActiveTab(0);
			}
		}
	}catch(err){}
}
//Using ajax request refresh assignment count and reloading grid.
function refreshAssignmentGridAndCountFromStart(loadFromStart){
	Ext.Ajax.request({
		   url: JSPRootURL+'/assignments/My/getAssignmentsCount',
		   params: {module: moduleId},
		   method: 'POST',
		   success: function(result, request){
				var obj = eval("(" +result.responseText +")" );
				assignmentTreeGrid.getBottomToolbar().displayMsg = '<b>Total Assignments: '+obj.totalFilteredAssignments + '</b>';
				assignmentTreeGrid.getBottomToolbar().emptyMsg = '<b>Total Assignments: '+obj.totalFilteredAssignments + '</b>';
				if(document.getElementById('inprogress') != null && document.getElementById('lateAssignment') && document.getElementById('shouldhavestarted') && document.getElementById('comingdue')
					&& document.getElementById('allAssignments') != null && document.getElementById('completedAssignments') != null){
					document.getElementById('lateAssignment').innerHTML = '<a href="javascript:showLateAssignmnets();">Late Assignments ('+ obj.numberOfLateAssignments +')</a>';
					document.getElementById('comingdue').innerHTML = '<a href="javascript:showComingDueAssignmnets();">Coming Due ('+ obj.numberOfComingDueAssignments +')</a>';
				    document.getElementById('shouldhavestarted').innerHTML = '<a href="javascript:showShouldHaveStartedAssignmnets();">Should Have Started ('+ obj.numberOfShouldHaveStartedAssignments +')</a>';
				    document.getElementById('inprogress').innerHTML = '<a href="javascript:showInProgressAssignmnets();">In Progress ('+ obj.numberOfInProgressAssignments +')</a>';
				    document.getElementById('allAssignments').innerHTML = '<a href="javascript:showAllAssignmnets();">All Assignments ('+ obj.numberOfAllAssignments +')</a>';
				    document.getElementById('completedAssignments').innerHTML = '<a href="javascript:showCompletedAssignmnets();">Completed Assignments ('+ obj.numberOfCompletedAssignments +')</a>';
				    document.getElementById('allAssignedbyme').innerHTML = '<a href="javascript:showAllAssignedByMe();">Assigned By Me ('+ obj.numberOfAllAssignedByMe +')</a>';
				}
				setUpdatedGridIds(obj);
				if(loadFromStart){
					assignmentTreeGrid.getBottomToolbar().doLoad(0);
				} else{
					assignmentTreeGrid.getStore().reload();
				}
		   },
		   failure: function(result, response){
			   extAlert('Error', 'Server request failed please try again...1', Ext.MessageBox.ERROR);
		   }
	});
}
//This is method to persist grid state by using ajax request.
function replaceGridTreeState(state){
	Ext.Ajax.request({	
		url: JSPRootURL+'/assignments/My/replaceGridTreeState',
		params: {module:moduleId , value : state ? 1 : 0 },
		method: 'POST'
	});
}

//This is method to get grid state by using ajax request.
function getGridTreeState(){
	Ext.Ajax.request({
		url: JSPRootURL+'/assignments/My/getGridTreeState',
		params: { module:moduleId },
		method: 'POST',
		success: function(result , request){
			if(result.responseText == '1'){
				collapsed = true;
				collapseAllNodes(assignmentTreeGrid.getStore());
			} else {
				collapsed = false;
				expandAllNodes(assignmentTreeGrid.getStore());
			}
		}
	});
}	
		
//This method is to genrate updatedGridId by checking count update of all assingment grid.
function setUpdatedGridIds(gridCountObj){
	updatedGridIds = '';
	leftPanel.items.each(function(object){
		if(isCountUpdated(object.id, gridCountObj)){
			updatedGridIds += object.id + ',';
		}
	});
}	

//This method is to check, if assignment count updated of gird.
function isCountUpdated(gridId, gridCountObj){
	var returnBoolean = false;
	if(gridId == 'late-assignments'){
		returnBoolean = numberOfLateAssignments != gridCountObj.numberOfLateAssignments;
		numberOfLateAssignments = gridCountObj.numberOfLateAssignments;
	}
	if(gridId == 'coming-due'){
		returnBoolean = numberOfComingDueAssignments != gridCountObj.numberOfComingDueAssignments;
		numberOfComingDueAssignments = gridCountObj.numberOfComingDueAssignments;
	}
	if(gridId == 'should-have-started'){
		returnBoolean = numberOfShouldHaveStartedAssignments != gridCountObj.numberOfShouldHaveStartedAssignments;
		numberOfShouldHaveStartedAssignments = gridCountObj.numberOfShouldHaveStartedAssignments;
	}
	if(gridId == 'in-progress'){
		returnBoolean = numberOfInProgressAssignments != gridCountObj.numberOfInProgressAssignments;
		numberOfInProgressAssignments = gridCountObj.numberOfInProgressAssignments;
	}
	if(gridId == 'complete-assignments'){
		returnBoolean = numberOfCompetedAssignments != gridCountObj.numberOfCompletedAssignments;
		numberOfCompetedAssignments = gridCountObj.numberOfCompletedAssignments;
	}
	if(gridId == 'all-assignments'){
		returnBoolean = numberOfAllAssignments != gridCountObj.numberOfAllAssignments;
		numberOfAllAssignments = gridCountObj.numberOfAllAssignments;
	}
	if(gridId == 'assignments'){
		returnBoolean = totalFilteredAssignments != gridCountObj.totalFilteredAssignments;
		totalFilteredAssignments = gridCountObj.totalFilteredAssignments;
	}
	if(gridId == 'all-assigned-by-me'){
		returnBoolean = numberOfAllAssignedByMe != gridCountObj.numberOfAllAssignedByMe;
		numberofAllAssignedByMe = gridCountObj.numberOfAllAssignedByMe;
	}
	return returnBoolean;
}	

//This method is to refresh grid panel, if there is any update in panel.
function refreshPanel(panel){
	if(updatedGridIds && updatedGridIds.indexOf(panel.id) >= 0){
		panel.getStore().reload();
	}
}

//This method is use to removing perticular Id form a id string.
function remvoeId(idString, id){
	var subStrOne;
	var subStrTwo;
	if(idString){
		subStrOne = idString.substring(0,idString.indexOf(id));
		subStrTwo = idString.substring(idString.indexOf(id)+id.length+1, idString.length);
	}
	return  subStrOne + subStrTwo;
}
