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
var xg = Ext.grid;
var reader;
var summary;
var sm;
var ds;
var columnModel;
var colMonthIndex = monthIndex;
var months = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');

Ext.onReady(function(){
	   Ext.QuickTips.init();
	   
	   document.getElementById('monthsId').value = months[monthIndex];
	   document.getElementById('yearsId').value = year;
	   
	   getWindowHeightWidth();
	   
	   summary = new Ext.grid.GroupSummary();	  
	   
	   sm = new xg.RowSelectionModel({
			lock: true
		});		
		
		function getBackGroundColor(v,params){
			if(!v){
				params.css='cellColorGreen';
				return v.toFixed(2)+'%';
			}
			else if(v<100){
				params.css='cellColorYellow';
				return v.toFixed(2)+'%';
			}
			else if(v>100){
				params.css='cellColorRed';
				return v.toFixed(2)+'%';
			}
			else{
				return v.toFixed(2)+'%';
			}
		}

		function renderWithPercent(v){
			if (v){ return (v.toFixed(2) +'%');}
		}

       columnModel = new Ext.grid.ColumnModel([
       			 {
					header: 'Resource_id', 
					width: 0, 
					sortable: false, 
					hidden: true,
					hideable: false,
					dataIndex: 'resource_id'
				},{
					header: '', 
					width: 200, 
					sortable: false,
					hidden: true,
					hideable: false,
					dataIndex: 'resource'
				},{
					header: projectColumnHeader, 
					width: 200, 
					sortable: false,					
					dataIndex: 'project',
					summaryRenderer:function(v){
						return gridTotalRowLabel;
					}
				},{				
					header: numberOfPrjectColumnHeader,
					width: 200,
					sortable: false,
					summaryType:'count',	
					dataIndex: 'numberOfProject'
				},{
					header: getMonthYear(), 
					width: 100, 
					align : 'center',
					sortable: false,
					summaryType:'sum',	
					dataIndex: 'month1',
					renderer : renderWithPercent,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(), 
					width: 100, 
					align : 'center',
					sortable: false,
					summaryType:'sum',	
					dataIndex: 'month2',
					renderer : renderWithPercent,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),  
					width: 100, 
					align : 'center',
					sortable: false,
					summaryType:'sum',	
					dataIndex: 'month3',
					renderer : renderWithPercent,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(), 
					width: 100, 
					align : 'center',
					sortable: false,
					summaryType:'sum',	
					dataIndex: 'month4',
					renderer : renderWithPercent,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(),  
					width: 100,
					align : 'center',
					sortable: false,
					summaryType:'sum',	
					dataIndex: 'month5',
					renderer : renderWithPercent,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(), 
					width: 100, 
					align : 'center',
					sortable: false,					
					summaryType:'sum',	
					dataIndex: 'month6',
					renderer : renderWithPercent,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(), 
					width: 100, 
					align : 'center',
					sortable: false, 		
					summaryType:'sum',	
					dataIndex: 'month7',
					renderer : renderWithPercent,
					summaryRenderer: getBackGroundColor
				},{
					header: getMonthYear(), 
					width: 100, 
					align : 'center',
					sortable: false,			
					summaryType:'sum',	
					dataIndex: 'month8',
					renderer : renderWithPercent,
					summaryRenderer: getBackGroundColor
				}
			]);

	   reader = new Ext.data.ArrayReader({}, [
	       {name: 'resource_id'},
		   {name: 'resource'},
		   {name: 'project'},
		   {name: 'numberOfProject', type: 'string'},		   
		   {name: 'month1', type: 'float'},
		   {name: 'month2', type: 'float'},
		   {name: 'month3', type: 'float'},
		   {name: 'month4', type: 'float'},
		   {name: 'month5', type: 'float'},
		   {name: 'month6', type: 'float'},
		   {name: 'month7', type: 'float'},
		   {name: 'month8', type: 'float'}
	   ]);   
         
       ds = new Ext.data.GroupingStore({
            reader: reader,
            data: xgdata1,
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
		    autoExpandColumn : 'project',
		    sm: sm,
	        frame: true,
			collapsible: false,
			animCollapse: false,
	        height : windowHeight - 310,
			width : windowWidth - 230,
			enableColumnMove: false,
	        title: gridTitle      
	   }); 
	   grid.render('gridPosition');
});

// Array data for the grid
var xgdata1 = Ext.grid.dummyData;
xgdata1 = allocationSummaryData;

function getMonthYear(){	
	if(colMonthIndex > 0){
		if(colMonthIndex > 11 ){
			year = year + 1;	
			colMonthIndex = (colMonthIndex%12);
		}		
	}
	return months[colMonthIndex++]+(year+'').substring(2,(year+'').length);
}

function getAllocationSummaryByCriteria(arg){	
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
	
	
	if(businessId == '0'){
		businessId = 'NotSelected';
	}
	Ext.Ajax.request({
	   url: jspRootUrl+escapeUrl('/resource/management/allocationSummary/'+ businessId +'&'+ startMonth +'&'+ startYear),
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
		    resText = result.responseText;		   
		    xgdata1 = Ext.util.JSON.decode(resText);
		    if(xgdata1 == ''){						
		 	    document.getElementById('searchMessage').innerHTML = '<label style="color:red; font-weight:bold;">'+noReservationDataFoundMessage+'</label>';
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
			extAlert(extalertTitle, serverRequestFailedMessage, Ext.MessageBox.ERROR);
	   }
	});
}

window.onresize = resizeGrid;
function resizeGrid() {
	getWindowHeightWidth(); 
	try{
		grid.setHeight(windowHeight - 310);
		grid.setWidth(windowWidth - 230);
		grid.getView().refresh();
	}catch(err){}
}
