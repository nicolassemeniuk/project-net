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

var grid;
var ds;
var xg = Ext.grid;
var reader;
var summary;
var sm;
var columnModel;
var colMonthIndex = monthIndex;
var months = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
var monthRenderIndex = 0;

Ext.onReady(function(){
	   Ext.QuickTips.init();
	   
	   document.getElementById('monthsId').value = months[monthIndex];
	   document.getElementById('yearsId').value = year;
	   
	   getWindowHeightWidth();
    	
	   var gridWin;  
	   
	   summary = new Ext.grid.GroupSummary();	  
	   
	   sm = new xg.RowSelectionModel({
			lock: true
	   });
		
	   function renderHours(v,params){	   	  
	   	   if (v > 0) return (v.toFixed(2));
       }
       
	   function getBackGroundColor(v,params){
	   		if(monthRenderIndex > 11){
       	       monthRenderIndex = 0;
       	    }	
			if(v > workingHoursInMonth[monthRenderIndex++]){
				params.css='cellColorRed';
				return (v.toFixed(2) );
			} else {
				return (v.toFixed(2));
			}
		}
       
       columnModel = new Ext.grid.ColumnModel([
       			 {
					header: resourceIdColumnLabel, 
					width: 0, 
					sortable: false, 
					hidden: true,
					hideable: false,
					dataIndex: 'resource_id'
				},{
					header: '', 
					width: 0, 
					sortable: false,
					hideable: false, 
					dataIndex: 'resourceName'
				},{
					header: projectIdColumnLabel, 
					width: 0, 
					hidden: true,
					sortable: false,
					hideable: false, 
					dataIndex: 'projectId'
				},{
					header: projectNameColumnLabel, 
					width: 100, 
					sortable: false, 
					dataIndex: 'projectName',
					summaryRenderer:function(v){
						return gridTotalRowLabel;
					}
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType:'sum',				
					dataIndex: 'month1',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType:'sum',
					dataIndex: 'month2',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor				
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType:'sum',
					dataIndex: 'month3',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType:'sum',
					dataIndex: 'month4',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),
					width: 60,
					align : 'center',
					sortable: false,
					summaryType:'sum',
					dataIndex: 'month5',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType:'sum',
					dataIndex: 'month6',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType:'sum',				
					dataIndex: 'month7',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType:'sum',
					dataIndex: 'month8',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor					
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType:'sum',
					dataIndex: 'month9',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType:'sum',
					dataIndex: 'month10',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),
					width: 60,
					align : 'center',
					sortable: false,
					summaryType:'sum',
					dataIndex: 'month11',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),
					width: 60, 
					align : 'center',
					sortable: false,
					summaryType: 'sum',
					dataIndex: 'month12',
					renderer : renderHours,
					summaryRenderer: getBackGroundColor
				}
			]);

	   reader = new Ext.data.ArrayReader({}, [
	       {name: 'resource_id'},
		   {name: 'resourceName'},
		   {name: 'project_id'},
		   {name: 'projectName'},	   
		   {name: 'month1'},
		   {name: 'month2'},
		   {name: 'month3'},
		   {name: 'month4'},
		   {name: 'month5'},
		   {name: 'month6'},
		   {name: 'month7'},
		   {name: 'month8'},
		   {name: 'month9'},
		   {name: 'month10'},
		   {name: 'month11'},
		   {name: 'month12'}
	   ]);   
         
       ds = new Ext.data.GroupingStore({
	            reader: reader,
	            data: xgdata1,
	            sortInfo:{field: 'project_id', direction: "DESC"},
		        groupField: 'resourceName'
	   });
	        
	   gridView = new Ext.grid.GroupingView({
	            forceFit: true,
	            groupTextTpl: '{text}',
	            startCollapsed : true,
            	hideGroupedColumn: true
        });
        
       grid = new xg.GridPanel({
	    	store: ds,
            cm: columnModel,
            plugins: summary,
            sm: sm,
	        view: gridView,        
		    autoExpandColumn : 'projectName',
		    collapsible: false,
        	animCollapse: false,
	        frame: true,
	        height : windowHeight-310,
			width : windowWidth-230,
			 enableColumnMove: false,
	        title: gridTitle,
	        iconCls: 'icon-grid'
	    }); 
		grid.render('summaryGridPosition');
});

// Array data for the grid
var xgdata1 = Ext.grid.dummyData;
xgdata1 = gridData;

function getMonthYear(){
	if(colMonthIndex > 0){
		if(colMonthIndex > 11 ){
			year = year + 1;	
			colMonthIndex = (colMonthIndex%12);
		}		
	}
	return months[colMonthIndex++]+(year+'').substring(2,(year+'').length);
}

function getAssignmentSummaryByCriteria(arg){
	if(isNotValidDateRange(arg)){
		return;
	}
	document.getElementById('viewBtn').disabled = true;
	document.getElementById('searchMessage').innerHTML 
					= '<label style="color:red; font-weight:bold;">'+ loadingMsg +'</label>';
	setTimeout('2000');
	disableNextPreviousMonthImageButtons();
	var startMonth = document.getElementById('monthsId').value;
	var startYear = document.getElementById('yearsId').value;
	var businessId = ''+document.getElementById('business').value;

	if(arg!=0){
		var tempMonthindex = months.indexOf(startMonth) + arg;
		
		if(tempMonthindex > 11){
			startMonth = months[tempMonthindex - 12];
			startYear = parseInt(startYear) + 1;
		}
		else if(tempMonthindex < 0 ){
			startMonth = months[tempMonthindex + 12];
			startYear = parseInt(startYear) - 1;
		}else{
			startMonth = months[tempMonthindex];
		}
	}

	document.getElementById('monthsId').value = startMonth;
	document.getElementById('yearsId').value = startYear;
	
	if(businessId == '0'){
		businessId = 'NotSelected';
	}
	try{
		var projectId = ''+document.getElementById('project').value;
	} catch(err){
		extAlert(extAlertTitle, projectNotExistToViewMessage, Ext.MessageBox.ERROR);
		document.getElementById('searchMessage').innerHTML = '';
		return false;
	}
	Ext.Ajax.request({
	   url: jspRootUrl+escapeUrl('/resource/management/AssignmentSummary/'+ businessId +'&'+ projectId +'&'+ startMonth +'&'+ startYear),
	   params: {module: 310},
	   method: 'POST',
	   success: function(result, request){
	 		colMonthIndex = months.indexOf(startMonth); 
			year = parseInt(startYear);			
	        columnModel.setColumnHeader(4, getMonthYear());        
	        columnModel.setColumnHeader(5, getMonthYear());
	        columnModel.setColumnHeader(6, getMonthYear());
	        columnModel.setColumnHeader(7, getMonthYear());
	        columnModel.setColumnHeader(8, getMonthYear());
	        columnModel.setColumnHeader(9, getMonthYear());
	        columnModel.setColumnHeader(10, getMonthYear());        
	        columnModel.setColumnHeader(11, getMonthYear());
	        columnModel.setColumnHeader(12, getMonthYear());
	        columnModel.setColumnHeader(13, getMonthYear());
	        columnModel.setColumnHeader(14, getMonthYear());
	        columnModel.setColumnHeader(15, getMonthYear());		   
		    resText = result.responseText.split('|&|');
		    workingHoursInMonth = resText[1].substring(1,resText[1].length-1).split(",");
		    monthRenderIndex = 0;
		    xgdata1 = Ext.util.JSON.decode(resText[0]);
		    if(xgdata1 == ''){						
		 	    document.getElementById('searchMessage').innerHTML = '<label style="color:red; font-weight:bold;">'+noDataFoundMessage+'</label>';
	        } else {
			    document.getElementById('searchMessage').innerHTML = '';
			}
		    ds.loadData(xgdata1);
		    grid.getView().refresh();
		    document.getElementById('viewBtn').disabled = false;
		    enableNextPreviousMonthImageButtons();
	   },
	   failure: function(result, response){
		    document.getElementById('viewBtn').disabled = false;
			extAlert(extAlertTitle, serverRequestFailedMessage, Ext.MessageBox.ERROR);
	   }
	});
}

window.onresize = resizeGrid;
function resizeGrid() {
	getWindowHeightWidth(); 
	try{
		grid.setHeight(windowHeight-310);
		grid.setWidth(windowWidth-230);
		grid.getView().refresh();
	}catch(err){}
}
