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
Ext.onReady(function(){
	   Ext.QuickTips.init();
	   getWindowHeightWidth();	   	   
	   var xg = Ext.grid;
       var gridWin;
       var taskId = 0;
	   
	   var summary = new Ext.grid.GroupSummary(); 
	   
	   var sm = new xg.RowSelectionModel({
			listeners: { 
				rowselect: function(sm, rowIndex, record){
			  		taskId = record.data.task_id;			  		
				}
			}
		});		
		
		function renderWithBackgroundColor(v, params, record){	   
			if(record.data.workspace == 'Resource Name'){
				params.css = 'rowColorForResource';				
				return '<b>'+v+'</b>';
			}
			if(record.data.task_assignment == gridTotalRowLabel){
				params.css = 'rowColorForTotals';				
				return '<b>'+ v +'</b>';
			}		   
			return;
		}
		
	   var reader = new Ext.data.ArrayReader({}, [
				   {name: 'task_id'},
                   {name: 'task_assignment'},
                   {name: 'workspace'},
                   {name: 'resource'},
                   {name: 'planStart'},
                   {name: 'planFinish'},
                   {name: 'actStart'},
                   {name: 'actFinish'},
                   {name: 'totWork'},
                   {name: 'wrkComplete'},
                   {name: 'wrkRemaining'},
                   {name: 'pctComplete', type:'float' },
         ]);  
		        
        ds = new Ext.data.Store({
            reader: reader,
            data: xgdata1
        });

        // creating the Grid
        grid = new xg.GridPanel({
		    ds: ds,
            columns: [
            {
	            header: "Task id",
				width: 0, 
				hidden: true,
				hideable: false,			
				dataIndex: 'task_id'
			},{
				id:'taskAssignment',
				header: taskAssignmentColumnHeader,
				width: 250, 
				locked: false,
				hideable: false,
				dataIndex: 'task_assignment',
				renderer : function(v, params, record){					
					if(record.data.workspace == 'Resource Name' || record.data.task_assignment == gridTotalRowLabel){
						return renderWithBackgroundColor(v, params, record);						
					}					
					return v;										
                }
			},{
				header: workspaceColumnHeader,
				width: 190, 
				dataIndex: 'workspace',
				renderer : function(v, params, record){
					if(v == 'Resource Name' || record.data.task_assignment == gridTotalRowLabel){						
						renderWithBackgroundColor(v, params, record);
					} else return v;			
				}		
			},{
				header: resourceColumnHeader, 
				width: 0, 
				hidden: true,
				hideable: false,
				dataIndex: 'resource',
			},{
				header: plannedStartColumnHeader,
				width: 105, 
				dataIndex: 'planStart',
				renderer : function(v, params, record){					
					if(record.data.workspace == 'Resource Name' || record.data.task_assignment == gridTotalRowLabel){
						return renderWithBackgroundColor(v, params, record);
					}
					return v;					
                }
			},{
				header: plannedFinishColumnHeader,
				width: 105, 
				dataIndex: 'planFinish',
				renderer : function(v, params, record){					
					if(record.data.workspace == 'Resource Name' || record.data.task_assignment == gridTotalRowLabel){
						return renderWithBackgroundColor(v, params, record);
					}
					return v;										
                }
			},{
				header: actualStartColumnHeader,
				width: 105, 
				dataIndex: 'actStart',
				renderer : function(v, params, record){					
					if(record.data.workspace == 'Resource Name' || record.data.task_assignment == gridTotalRowLabel){
						return renderWithBackgroundColor(v, params, record);						
					}
					return v;
                }
			},{
				header: actualFinishColumnHeader,
				width: 105, 
				dataIndex: 'actFinish',
				renderer : function(v, params, record){					
					if(record.data.workspace == 'Resource Name' || record.data.task_assignment == gridTotalRowLabel){
						return renderWithBackgroundColor(v, params, record);
					}
					return v;			
                }
			},{
				header: totalWorkColumnHeader,
				width: 105, 
				dataIndex: 'totWork',
				renderer : function(v, params, record){					
					if(record.data.workspace == 'Resource Name'){
						return renderWithBackgroundColor(v, params, record);
					} else if(record.data.task_assignment == gridTotalRowLabel){
						renderWithBackgroundColor(v, params, record);
						return '<b>'+ v.toFixed(2) +'</b>';
					} else if(v) {				
						return (v.toFixed(2));
					} else { 
						return '0.00 ';
					}
                }				
			},{
				header: workCompleteColumnHeader,
				width: 105, 
				dataIndex: 'wrkComplete',
				renderer : function(v, params, record){
					if(record.data.workspace == 'Resource Name'){
						return renderWithBackgroundColor(v, params, record);
					} else if(record.data.task_assignment == gridTotalRowLabel){
						renderWithBackgroundColor(v, params, record);
						return '<b>'+ v.toFixed(2) +'</b>';
					} else if(v){				
						return (v.toFixed(2));
					} else { 
						return '0.00 ';
					}
               	}				
			},{
				header: workRemainingColumnHeader,
				width: 105, 
				dataIndex: 'wrkRemaining',
				renderer : function(v, params, record){
					if(record.data.workspace == 'Resource Name'){
						return renderWithBackgroundColor(v, params, record);
					} else if(record.data.task_assignment == gridTotalRowLabel){
						renderWithBackgroundColor(v, params, record);
						return '<b>'+ v.toFixed(2) +'</b>';
					} else if(v){				
						return (v.toFixed(2));
					} else { 
						return '0.00 ';
					}
				}				
			},{
				header: workPercentCompleteColumnHeader,
				width: 110, 
				dataIndex: 'pctComplete',				
				renderer : function(v, params, record){
					if(record.data.workspace == 'Resource Name' || record.data.task_assignment == gridTotalRowLabel){
						return renderWithBackgroundColor(v, params, record);
					}
					if (v) {
						return ( v.toFixed(2) +' %');
					} else { 
						return '0.00 %';
					}
				}				
			}
		],
        
        view: new Ext.grid.GridView({
            forceFit: true
        }),

		autoExpandColumn : 'taskAssignment',
		sm: sm,
        frame: true,        
        height : windowHeight - 310,
		width : windowWidth - 230,
		enableColumnMove: false,
        collapsible: false,
        animCollapse: false,
        title: gridTitle,
        iconCls: 'icon-grid'
    });
    grid.render('gridPosition');
    
    grid.addListener('rowdblclick', function(grid, columnIndex, rowIndex, e){
    	if(taskId){
    		window.location.href = jspRootUrl+'/servlet/ScheduleController/TaskView?module=60&action=1&id='+taskId;
    		taskId = 0;
    	}
    });
});

// Array data for the grid
var xgdata1 = Ext.grid.dummyData;
xgdata1 = gridData;

function daysInMonth(iMonth, iYear)
{
	return 32 - new Date(iYear, iMonth-1, 32).getDate();
}

// check for valid date format
function checkDate(field) {
	var date = field.value;
	var dateArray = date.split("/");	
	var theDate = new Date(date);
	var d1 = new String(theDate.getDate());
	var zeros = new RegExp("0");
	var tmp = new String(dateArray[1])
	tmp = tmp.replace(zeros, "");
	var d2 = new RegExp(tmp);
	var isDateValid = true;
	date = date.replace(" ", "");
	if (date != null && date.length > 0) { 
		d1 = d1.replace(zeros, "");
		if (d1.search(d2) == -1) {
			isDateValid = false;
		} else if (dateArray.length != 3) {
			isDateValid = false;
		} else if ((dateArray[0] < 1) || (dateArray[0] > 12)) {
			isDateValid = false;
		} else if ((dateArray[1] < 1) || (dateArray[1] > 31)) {
	 		isDateValid = false;
		} else if (dateArray[2].length != 4)  {			
			isDateValid = false;
		}
	} else {
		isDateValid = false		
	}
	return isDateValid;
}

// method for fetching grid data by ajax request
function fetchGridData(arg){ 
	document.getElementById('searchMessage').innerHTML 
			= '<label style="color:red; font-weight:bold;">'+ loadingMsg +'</label>';
	disableNextPreviousMonthImageButtons();
	
	var from = document.getElementById('from').value;
	var to = document.getElementById('to').value;
	var listId = document.getElementById('resourceList').value;	
	var fromArr = from.split('/');
	var toArr = to.split('/');
	
	if(arg!=0){
		var fromTempMonth = parseInt(fromArr[0])+arg;
		
		if(fromTempMonth >12 && arg > 0){
			fromArr[0] = fromTempMonth - 12;
			fromArr[2] = parseInt(fromArr[2]) + 1;
		}else if (fromTempMonth < 1 && arg < 0){
			fromArr[0] = fromTempMonth + 12;
			fromArr[2] = parseInt(fromArr[2]) - 1;
		}else{
			fromArr[0] = fromTempMonth;
		}
		
		toArr[0] = parseInt(fromArr[0]);
		toArr[2] = fromArr[2];
		fromArr[1] = '01';
		toArr[1] = daysInMonth(parseInt(toArr[0]) ,parseInt(toArr[2]));
	}
	
	from = fromArr[0]+'-'+fromArr[1]+'-'+fromArr[2];
	to = toArr[0]+'-'+toArr[1]+'-'+toArr[2];

	document.getElementById('from').value = fromArr[0]+'/'+fromArr[1]+'/'+fromArr[2];
	document.getElementById('to').value= toArr[0]+'/'+toArr[1]+'/'+toArr[2];

	if(listId == '0'){
		listId = 'NotSelected';
	}
	
	Ext.Ajax.request({
		   url: jspRootUrl+escapeUrl('/resource/management/ViewDetails/'+from+'&'+to+'&'+listId),
		   params: {module: 360},
		   method: 'POST',
		   success: function(result, request){ 
					var resText = new Array();
					if(result.responseText == 'Invalid1'){
						document.getElementById('searchMessage').innerHTML 
							= '<label style="color:red; font-weight:bold;">'+ 
							fromDateInvalidMessage 
							+'</label>';
					} else if(result.responseText == 'Invalid2'){
						document.getElementById('searchMessage').innerHTML 
							= '<label style="color:red; font-weight:bold;">'+ 
					        toDateInvalidMessage
							+'</label>';					
					} else {					
				        xgdata1 = Ext.util.JSON.decode(result.responseText);
						if(xgdata1 == ''){						
							document.getElementById('searchMessage').innerHTML 
								= '<label style="color:red; font-weight:bold;">'+ searchMessage +'</label>';
				        } else {
							document.getElementById('searchMessage').innerHTML = '';
						}
						ds.loadData(xgdata1);
						grid.getView().refresh();
					}
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
// method to resize grid on window resize event
function resizeGrid() {
	getWindowHeightWidth(); 
	try{
		grid.setHeight(windowHeight-310);
		grid.setWidth(windowWidth-230);
		grid.getView().refresh();
	}catch(err){}
}
