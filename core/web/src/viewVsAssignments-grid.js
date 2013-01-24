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
var monthYearData = new Array();
var monthRenderIndex = 0;
var isOverUtilized = new Array();
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
					document.getElementById('business').style.visibility = 'hidden';
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
	document.getElementById('resourceDetailPopup').style.visibility='hidden';
	document.getElementById('popupScreen').style.visibility='hidden';
	document.getElementById('business').style.visibility = 'visible';
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
		
	   function renderWithBackgroundColor(v, params, record){
	   	   if(monthRenderIndex > 11){
       	       monthRenderIndex = 0;
       	   }
		   if(v != 0 && v != ''){
			   if(v > 100){
				   params.css = 'cellColorRed';
				   isOverUtilized[monthRenderIndex] = true;
			   }else if(!isOverUtilized[monthRenderIndex]){
			   	isOverUtilized[monthRenderIndex] = false;
			   }
			   return '<span style="color:green;"> <a href="#" onclick="openWin(\''+jspRootUrl+'/resource/management/AllocAssignDetails/'+
						record.data.resourceId + '&' + record.data.projectId + '&' +monthYearData[monthRenderIndex++]+'\');">'+ 
						v.toFixed(2) + '%</a> </span>';
		   }else if(!isOverUtilized[monthRenderIndex]){
			   isOverUtilized[monthRenderIndex] = false;
		   }
		   monthRenderIndex++;  
		   return '--';
	   }
	   
	   function summaryRenderer(v, params, record){
	   		if(monthRenderIndex > 11){
       	       monthRenderIndex = 0;
       	    }
       	   	if(isOverUtilized[monthRenderIndex]){
			   params.css = 'cellColorRed';
			   isOverUtilized[monthRenderIndex] = false;
		    }
		     monthRenderIndex++;
		     if(v > 0){
			     return v.toFixed(2) +'%';
		     }
		    return '--';
	   }
	   
       columnModel = new Ext.grid.ColumnModel([
       			 {
					header: resourceColumnHeader, 
					width: 0, 
					sortable: false, 
					hidden: true,
					hideable: false,
					dataIndex: 'resourceId'
				},{
					header: '', 
					width: 0, 
					sortable: false,
					hidden: true,
					hideable: false,
					dataIndex: 'resource'
				},{
					header: projectIdColumnLabel, 
					width: 0, 
					hidden: true,
					sortable: false,
					hideable: false, 
					dataIndex: 'projectId'
				},{
					header: projectColumnHeader, 
					width: 200, 
					sortable: false,					
					dataIndex: 'project',
					renderer : function(v){
						monthRenderIndex = 0;
						return v;					
					},
					summaryRenderer:function(v){
						return gridTotalRowLabel;
					}					
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false,
					dataIndex: 'month1',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false,
					dataIndex: 'month2',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false,
					dataIndex: 'month3',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false,
					dataIndex: 'month4',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false,
					dataIndex: 'month5',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(),  
					width: 80, 
					align : 'center',
					sortable: false,
					dataIndex: 'month6',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false,
					dataIndex: 'month7',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(),  
					width: 80,
					align : 'center',
					sortable: false,
					dataIndex: 'month8',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false,					
					dataIndex: 'month9',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false,					
					dataIndex: 'month10',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false, 				
					dataIndex: 'month11',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				},{
					header: getMonthYear(), 
					width: 80, 
					align : 'center',
					sortable: false,					
					dataIndex: 'month12',
					summaryType:'sum',
					renderer : renderWithBackgroundColor,
					summaryRenderer : summaryRenderer
				}
			]);

	   reader = new Ext.data.ArrayReader({}, [
	       {name: 'resourceId'},
		   {name: 'resource'},
		   {name: 'projectId'},
		   {name: 'project'},		  
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
            data: xgdata,
            sortInfo:{field: 'resource', direction: "ASC"},
            groupField:'resource'   
	   });
	        
	   gridView = new Ext.grid.GroupingView({
            forceFit: true,
            startCollapsed : true	         
        });
        
       grid = new xg.GridPanel({
		    plugins: summary,
	    	store: ds,
            cm: columnModel,
	        view: gridView,	        
		    autoExpandColumn : 'projectName',
		    sm: sm,
	        frame: true,
			collapsible: false,
			animCollapse: false,
			enableColumnMove: false,
	        height : windowHeight - 310,
			width : windowWidth - 230,
	        title: gridTitle      
	    }); 
		grid.render('gridPosition');
});

// Array data for the grid
var xgdata = Ext.grid.dummyData;
xgdata = assignmentVsAllocationData;
			
function getMonthYear(){	
	if(colMonthIndex > 0){
		if(colMonthIndex > 11 ){
			year = year + 1;	
			colMonthIndex = (colMonthIndex % 12);			
		}		
	}
	monthYearData[monthRenderIndex++] = (colMonthIndex + 1) +'&'+ year;
	return months[colMonthIndex++]+(year+'').substring(2,(year+'').length);	
}

function getAssignmentVsAllocationDataByCriteria(arg){
	if(isNotValidDateRange(arg)){
		return;
	}
		
	document.getElementById('viewBtn').disabled = true;	
	document.getElementById('searchMessage').innerHTML 	= '<label style="color:red; font-weight:bold;">'+ loadingMsg +'</label>';
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
	
	Ext.Ajax.request({
	   url: jspRootUrl+escapeUrl('/resource/management/ViewVsAssignments/'+ businessId +'&'+ startMonth +'&'+ startYear),
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
	        columnModel.setColumnHeader(10, getMonthYear());        
	        columnModel.setColumnHeader(11, getMonthYear());
	        columnModel.setColumnHeader(12, getMonthYear());
	        columnModel.setColumnHeader(13, getMonthYear());
	        columnModel.setColumnHeader(14, getMonthYear());
	        columnModel.setColumnHeader(15, getMonthYear());
		    resText = result.responseText;		   
		    xgdata1 = Ext.util.JSON.decode(resText);
		    if(xgdata1 == ''){						
		 	    document.getElementById('searchMessage').innerHTML = '<label style="color:red; font-weight:bold;">'+dataNotFoundMessage+'</label>';
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
