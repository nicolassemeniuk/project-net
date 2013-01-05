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
var columnModel;
var grid;
var ds;
var gridView;
var xg = Ext.grid;
var reader;
var summary;
var sm;
var columnModel;
var colMonthIndex = monthIndex;
var monthYearData = new Array();
var monthRenderIndex = 0;
var summaryResourceId = 0;
var months = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');

function openWin(urlValues){
	Ext.Ajax.request({
		   url: urlValues,
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){ 
					var  obj = document.getElementById('resourceDetailPopup');					
					document.getElementById('resourceDetails').innerHTML = result.responseText;
					document.getElementById('popupScreen').style.visibility = 'visible';
					document.getElementById('businessList').style.visibility = 'hidden';
					document.getElementById('monthsId').style.visibility = 'hidden';
					document.getElementById('yearsId').style.visibility	= 'hidden';
					obj.style.visibility = 'visible';
		   },
		   failure: function(result, response){				   
				   extAlert(extAlertTitle, serverRequestFailedMessage, Ext.MessageBox.ERROR);
		   }
	});		
}
function enableScreen(){
	document.getElementById('resourceDetailPopup').style.visibility = 'hidden';
	document.getElementById('popupScreen').style.visibility = 'hidden';
	document.getElementById('businessList').style.visibility = 'visible';
	document.getElementById('monthsId').style.visibility = 'visible';
	document.getElementById('yearsId').style.visibility	= 'visible';
}

Ext.onReady(function(){				
	   Ext.QuickTips.init();
	   
	   document.getElementById('monthsId').value = months[monthIndex];
	   document.getElementById('yearsId').value = year;
	   
	   getWindowHeightWidth();
	   
	   summary = new Ext.grid.GroupSummary();	   
	   sm = new xg.RowSelectionModel({
			lock: true
	   });		
		
       function renderWithColor(v, params, record){ 
	       summaryResourceId = record.data.resource_id;
       	   if(monthRenderIndex > 5){
       	       monthRenderIndex = 0;
       	   }		  
		   if(v){
			    return '<span style="color:green;"> <a href="#" onclick="openWin(\''+jspRootUrl+escapeUrl('/resource/management/AssignmentDetail/'+
							record.data.resource_id + '&' + record.data.projectId + '&' +monthYearData[monthRenderIndex++])+'\');">'+ 
							v.toFixed(2) + '</a> </span>';			
		   } else {
			   monthRenderIndex++;
			   return v.toFixed(2);
		   }
   	   }
   	   
   	   function renderSummaryWithColor(v, params, record){   	   	   
   	   	   if(monthRenderIndex > 5){
       	       monthRenderIndex = 0;
       	   }       	   
   	   	   if(v > workingHoursInMonth[monthRenderIndex]){
   	           params.css = 'cellColorRed';	   	       
	   	   }
	   	   if(v){
	   	    return '<span style="color:green;"> <a href="#" onclick="openWin(\''+jspRootUrl+escapeUrl('/resource/management/AssignmentDetail/'+
							summaryResourceId + '&0&' +monthYearData[monthRenderIndex++])+'\');">'+ 
							v.toFixed(2) + '</a> </span>';	
		  }else{
		  	 monthRenderIndex++;
			 return v.toFixed(2);
		  }
	   }
       
       columnModel = new Ext.grid.ColumnModel([
       			 {
					header: resourceColumnHeader, 
					width: 0, 
					sortable: false, 
					hidden: true,
					hideable: false,
					dataIndex: 'resource_id'
				},{
					header: '', 
					width: 0, 
					sortable: false, 
					hidden: true,
					hideable: false,
					dataIndex: 'resource'
				},{
					header: '', 
					width: 0, 
					sortable: false, 
					hidden: true,
					hideable: false,
					dataIndex: 'projectId'
				},{				
					header: projectNameColumnHeader,
					width: 200,
					sortable: false,
					groupable  : false,
					locked: false,								
					dataIndex: 'projectName',
					renderer: function(v){
						return '<span style="color:green;">'+ v +'</span>';
					},
					summaryRenderer:function(v){
						return gridTotalRowLabel;
					}
				},{
					header: getMonthYear(), 
					width: 105, 
					sortable: false,
					groupable  : false,
					summaryType: 'sum',
					align : 'center',
					dataIndex: 'month1',
					renderer: renderWithColor,
					summaryRenderer:renderSummaryWithColor
				},{
					header: getMonthYear(), 
					width: 105, 
					sortable: false,
					groupable: false,
					summaryType:'sum',
					align : 'center',
					dataIndex: 'month2',					
					renderer: renderWithColor,
					summaryRenderer:renderSummaryWithColor
				},{
					header: getMonthYear(), 
					width: 105, 
					sortable: false,
					groupable: false,
					summaryType:'sum',
					align : 'center',
					dataIndex: 'month3',
					renderer: renderWithColor,
					summaryRenderer:renderSummaryWithColor
				},{
					header: getMonthYear(), 
					width: 105, 
					sortable: false,
					groupable: false,
					summaryType:'sum',
					align : 'center',
					dataIndex: 'month4',
					renderer: renderWithColor,
					summaryRenderer:renderSummaryWithColor
				},{
					header: getMonthYear(), 
					width: 105,
					sortable: false,
					groupable: false,
					summaryType:'sum',
					align : 'center',
					dataIndex: 'month5',
					renderer: renderWithColor,
					summaryRenderer:renderSummaryWithColor
				},{
					header: getMonthYear(), 
					width: 105, 
					sortable: false,
					groupable: false,
					summaryType:'sum',
					align : 'center',
					dataIndex: 'month6',
					renderer: renderWithColor,
					summaryRenderer:renderSummaryWithColor
				},{
					header: totalAvailableColumnHeader, 
					width: 105, 
					sortable: false,
					groupable: false,
					dataIndex: 'totAvailable',
					renderer: function(v){ return; },
					summaryRenderer: function(v, params, record){
						return (getWorkingHourSum() - (record.data.month1 + record.data.month2 + record.data.month3 + record.data.month4 +record.data.month5 + record.data.month6)).toFixed(2);
					}
				}
			]);

	   reader = new Ext.data.ArrayReader({}, [
	       {name: 'resource_id'},
		   {name: 'resource'},
		   {name: 'projectId'},		   
		   {name: 'projectName'},
		   {name: 'month1'},
		   {name: 'month2'},
		   {name: 'month3'},
		   {name: 'month4'},
		   {name: 'month5'},
		   {name: 'month6'},
   		   {name: 'totAvailable'}
	   ]);   
         
       ds = new Ext.data.GroupingStore({
            reader: reader,
            data: xgdata1,
            sortInfo: {field: 'projectName', direction: "ASC"},
            groupField: 'resource'
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
	        view: gridView,
	        plugins: summary,
		    autoExpandColumn : 'projectName',
		    sm: sm,
	        frame: true,
		    height: windowHeight-310,
			width : windowWidth-230,
	        collapsible: false,
	        animCollapse: false,
	        enableColumnMove: false,
	        title: summaryGridTitle,
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
	monthYearData[monthRenderIndex++] = (colMonthIndex + 1) +'&'+ year;
	return months[colMonthIndex++]+(year+'').substring(2,(year+'').length);
}

function fetchDataByMonths(monthsToFetch){
	if(isNotValidDateRange(monthsToFetch)){
		return;
	}
	if(isNextPreviousMonthImageButtonsDisabled()){
		return;
	}
	document.getElementById('viewBtn').disabled = true;
	disableNextPreviousMonthImageButtons();
	
	document.getElementById('searchMessage').innerHTML 
				= '<label style="color:red; font-weight:bold;">'+ loadingMsg +'</label>';
	setTimeout('2000');
	var startMonth = document.getElementById('monthsId').value;
	var startYear = document.getElementById('yearsId').value;
	var businessListId = ''+document.getElementById('businessList').value;
	
	if(monthsToFetch!=0){
		var tempMonthindex = months.indexOf(startMonth) + monthsToFetch;
		
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
	
	Ext.Ajax.request({
		   url: jspRootUrl+escapeUrl('/resource/management/viewSummary/'+ businessListId +'&'+ startMonth +'&'+ startYear),
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
					colMonthIndex = months.indexOf(startMonth); 
					year = parseInt(startYear);	
					monthRenderIndex = 0;
					
					columnModel.setColumnHeader(4, getMonthYear());
   			        columnModel.setColumnHeader(5, getMonthYear());
			        columnModel.setColumnHeader(6, getMonthYear());
			        columnModel.setColumnHeader(7, getMonthYear());
			        columnModel.setColumnHeader(8, getMonthYear());
			        columnModel.setColumnHeader(9, getMonthYear());	
			        
			        resText = result.responseText.split('|&|');
					workingHoursInMonth = resText[1].substring(1,resText[1].length-1).split(",");
					
					xgdata1 = Ext.util.JSON.decode(resText[0]);
					if(xgdata1 == ''){						
						document.getElementById('searchMessage').innerHTML = '<label style="color:red; font-weight:bold;">'+ searchMessage +'</label>';
			        } else {
						document.getElementById('searchMessage').innerHTML = '';
					}
					ds.loadData(xgdata1);
					grid.reconfigure(ds,columnModel);
					grid.getView().refresh();
					document.getElementById('viewBtn').disabled = false;
					enableNextPreviousMonthImageButtons();					
		   },
		   failure: function(result, response){
				   document.getElementById('viewBtn').disabled = false;
				   enableNextPreviousMonthImageButtons();
				   extAlert(extAlertTitle, serverRequestFailedMessage, Ext.MessageBox.ERROR);
		   }
	});	
}

function getWorkingHourSum(){
	var workingHourSum = 0;
	for(var hourIndex = 0; hourIndex < workingHoursInMonth.length; hourIndex++){
		workingHourSum += parseInt(workingHoursInMonth[hourIndex]);
  	}
  	return workingHourSum;
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

