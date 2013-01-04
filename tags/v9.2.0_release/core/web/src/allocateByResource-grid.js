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

var comboList = comboListRecord;
var grid;
var xg = Ext.grid;
var reader;
var sm;
var ds;
var columnModel;
var colMonthIndex = monthIndex;  
var months = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');

Ext.onReady(function(){	 
	    getWindowHeightWidth();
	    
	    Ext.QuickTips.init();
	    
	    sm = new xg.RowSelectionModel({
			lock: true
		});
		
		function renderWithPercent(v, params, record){
			params.attr = 'ext:qtip="'+clickToEditTooltip+'"';
			if (record.data.projectName == gridTotalRowLabel){
				params.css = 'rowColorForTotals';
				return '<b>'+v.toFixed(2)+'%<b>';
			}
			if(v){
				return(v.toFixed(2)+'%');
			}
	    }
	    
	   function renderUntilEnd(v, record){
			record.data.month1=v;
			record.data.month2=v;
			record.data.month3=v;
			record.data.month4=v;
			record.data.month5=v;
			record.data.month6=v;
			record.data.month7=v;
			record.data.month8=v;
	   }
		
	   function getComboDisplayedValue(id){
			var object = document.getElementById('project');
			for(var index = 0; index < object.length; index++){
				if (object.options[index].value == id){
					return object.options[index].text;
				}
			}
		}

		function isNotInList(v){
			if(comboList.indexOf('$'+v+'$') == -1) {
				comboList+='$'+v+'$';
				return true;
			} else {
				return false;
			}
		}
				
		columnModel = new Ext.grid.ColumnModel([
       			 {
					header: 'Project Id', 
					width: 0, 
					sortable: false, 
					hidden: true,
					hideable: false,
					dataIndex: 'project_id'
				},{
					header: projectColumnHeader, 
					width: 200, 
					sortable: false,					
					dataIndex: 'projectName',
					renderer : function(v, params, record){
						if (v == gridTotalRowLabel){
							params.css = 'rowColorForTotals';
							return '<b>'+gridTotalRowLabel+'<b>';
						}
						return v;
					}
				},{				
					header: untilEndOfProjectHeader,
					width: 200,
					sortable: false,					
					dataIndex: 'untilEnd',
					editor: new Ext.form.NumberField({
			            allowNegative: false,
				        style: 'text-align:left',
						maxValue:100,
						maxText: allocationNotMoreThan100percentMessage,
						minValue:0,
						minText: allocationZeroPercentMessage
				    }),
					renderer : function(v, params, record){
						params.attr = 'ext:qtip="'+untilEndOfProjectColumnTooltip+'"';
						if (record.data.projectName == gridTotalRowLabel){
							params.css = 'rowColorForTotals';
						}
						if (v){
							return (v.toFixed(2) +'%');
						}
					}
				},{
					header: msgFormat.format(allocationLbl,'1'), 
					width: 0, 
					sortable: false,
					hidden: true,
					dataIndex: 'month1_allocationId'
				},{
					header: getMonthYear(), 
					width: 100, 
					align : 'center',
					sortable: false,
					dataIndex: 'month1',
					editor: new Ext.form.NumberField({
			            allowNegative: false,
				        style: 'text-align:left',
						maxValue:100,
						maxText: allocationNotMoreThan100percentMessage,
						minValue:0,
						minText: allocationZeroPercentMessage
				    }),
					renderer: renderWithPercent
				},{
					header: msgFormat.format(allocationLbl,'2'), 
					width: 0, 
					sortable: false,
					hidden: true,
					dataIndex: 'month2_allocationId'
				},{
					header: getMonthYear(),  
					width: 100, 
					align : 'center',
					sortable: false,
					dataIndex: 'month2',
					editor: new Ext.form.NumberField({
			            allowNegative: false,
				        style: 'text-align:left',
						maxValue:100,
						maxText: allocationNotMoreThan100percentMessage,
						minValue:0,
						minText: allocationZeroPercentMessage
				    }),
					renderer: renderWithPercent
				},{
					header: msgFormat.format(allocationLbl,'3'), 
					width: 0, 
					sortable: false,
					hidden: true,
					dataIndex: 'month3_allocationId'
				},{
					header: getMonthYear(),
					width: 100, 
					align : 'center',
					sortable: false,
					dataIndex: 'month3',
					editor: new Ext.form.NumberField({
			            allowNegative: false,
				        style: 'text-align:left',
						maxValue:100,
						maxText: allocationNotMoreThan100percentMessage,
						minValue:0,
						minText: allocationZeroPercentMessage
				    }),
					renderer: renderWithPercent
				},{
					header: msgFormat.format(allocationLbl,'4'), 
					width: 0, 
					sortable: false,
					hidden: true,
					dataIndex: 'month4_allocationId'
				},{
					header: getMonthYear(), 
					width: 100, 
					align : 'center',
					sortable: false,
					dataIndex: 'month4',
					editor: new Ext.form.NumberField({
			            allowNegative: false,
				        style: 'text-align:left',
						maxValue:100,
						maxText: allocationNotMoreThan100percentMessage,
						minValue:0,
						minText: allocationZeroPercentMessage
				    }),
					renderer: renderWithPercent
				},{
					header: msgFormat.format(allocationLbl,'5'), 
					width: 0, 
					sortable: false,
					hidden: true,
					dataIndex: 'month5_allocationId'
				},{
					header: getMonthYear(),   
					width: 100,
					align : 'center',
					sortable: false,
					dataIndex: 'month5',
					editor: new Ext.form.NumberField({
			            allowNegative: false,
				        style: 'text-align:left',
						maxValue:100,
						maxText: allocationNotMoreThan100percentMessage,
						minValue:0,
						minText: allocationZeroPercentMessage
				    }),
					renderer: renderWithPercent
				},{
					header: msgFormat.format(allocationLbl,'6'), 
					width: 0, 
					sortable: false,
					hidden: true,
					dataIndex: 'month6_allocationId'
				},{
					header: getMonthYear(),  
					width: 100, 
					align : 'center',
					sortable: false,					
					dataIndex: 'month6',
					editor: new Ext.form.NumberField({
			            allowNegative: false,
				        style: 'text-align:left',
						maxValue:100,
						maxText: allocationNotMoreThan100percentMessage,
						minValue:0,
						minText: allocationZeroPercentMessage
				    }),
					renderer: renderWithPercent
				},{
					header: msgFormat.format(allocationLbl,'7'), 
					width: 0, 
					sortable: false,
					hidden: true,
					dataIndex: 'month7_allocationId'
				},{
					header: getMonthYear(), 
					width: 100, 
					align : 'center',
					sortable: false, 				
					dataIndex: 'month7',
					editor: new Ext.form.NumberField({
			            allowNegative: false,
				        style: 'text-align:left',
						maxValue:100,
						maxText: allocationNotMoreThan100percentMessage,
						minValue:0,
						minText: allocationZeroPercentMessage
				    }),
					renderer: renderWithPercent
				},{
					header: msgFormat.format(allocationLbl,'8'), 
					width: 0, 
					sortable: false,
					hidden: true,
					dataIndex: 'month8_allocationId'
				},{
					header: getMonthYear(),
					width: 100, 
					align : 'center',
					sortable: false,					
					dataIndex: 'month8',
					editor: new Ext.form.NumberField({
			            allowNegative: false,
				        style: 'text-align:left',
						maxValue:100,
						maxText: allocationNotMoreThan100percentMessage,
						minValue:0,
						minText: allocationZeroPercentMessage
				    }),
					renderer: renderWithPercent
				}
			]);
	   
	    reader = new Ext.data.ArrayReader({}, [
	        {name: 'project_id', type: 'string'},
		    {name: 'projectName', type: 'string'},		   
		    {name: 'untilEnd', type: 'float'},
		    {name: 'month1_allocationId', type: 'int'},
		    {name: 'month1', type: 'float'},
		    {name: 'month2_allocationId', type: 'int'},
		    {name: 'month2', type: 'float'},
		    {name: 'month3_allocationId', type: 'int'},
		    {name: 'month3', type: 'float'},
		    {name: 'month4_allocationId', type: 'int'},
		    {name: 'month4', type: 'float'},
		    {name: 'month5_allocationId', type: 'int'},
		    {name: 'month5', type: 'float'},
		    {name: 'month6_allocationId', type: 'int'},
		    {name: 'month6', type: 'float'},
		    {name: 'month7_allocationId', type: 'int'},
		    {name: 'month7', type: 'float'},
		    {name: 'month8_allocationId', type: 'int'},
		    {name: 'month8', type: 'float'}
	    ]); 
         
        ds = new Ext.data.GroupingStore({
	             reader: reader,
	             data: xgdata1
	    });
	        
	    gridView = new Ext.grid.GroupingView({
               forceFit: true	            
        });
	   
	    var combo = new Ext.form.ComboBox({
                typeAhead: true,
                triggerAction: 'all',
                transform: 'gridProjectList',
                lazyRender: true,
                listClass: 'x-combo-list-small',
                editable : false
        });

		var projectRecord = Ext.data.Record.create([
		    {name: 'project_id', type: 'string'},
		    {name: 'projectName', type: 'string'},		   
		    {name: 'untilEnd', type: 'float'},
		    {name: 'month1_allocationId', type: 'int'},
		    {name: 'month1', type: 'float'},
		    {name: 'month2_allocationId', type: 'int'},
		    {name: 'month2', type: 'float'},
		    {name: 'month3_allocationId', type: 'int'},
		    {name: 'month3', type: 'float'},
		    {name: 'month4_allocationId', type: 'int'},
		    {name: 'month4', type: 'float'},
		    {name: 'month5_allocationId', type: 'int'},
		    {name: 'month5', type: 'float'},
		    {name: 'month6_allocationId', type: 'int'},
		    {name: 'month6', type: 'float'},
		    {name: 'month7_allocationId', type: 'int'},
		    {name: 'month7', type: 'float'},
		    {name: 'month8_allocationId', type: 'int'},
		    {name: 'month8', type: 'float'}
        ]);
	  
        grid = new xg.EditorGridPanel({
			store: ds,
            cm: columnModel,
            loadMask: true,
	        view: gridView,
			autoExpandColumn : 'projectName',
		    sm: sm,
	        frame: true,
			collapsible: false,
			animCollapse: false,
	        height : windowHeight-310,
			width : windowWidth-230,
			enableColumnMove: false,
			clicksToEdit: 1,
			title: gridTitle,
			iconCls: 'icon-grid',
			tbar: [
				combo
			, '-' 
			,{
			    text: '<b>'+addButtonCaption+'</b>',
				tooltip: { title: addProjectTooltipTitle, text: clickToAddProjectTooltip, width:'150'},
				iconCls: 'add',
				handler : function(){
					var project = new projectRecord({
                    project_id: combo.value,
					projectName: getComboDisplayedValue(combo.value),
					untilEnd: '',
                    month1_allocationId: 0,
                    month1: '',
                    month2_allocationId: 0,
                    month2: '',
                    month3_allocationId: 0,
                    month3: '',
                    month4_allocationId: 0,
					month4: '',
					month5_allocationId: 0,
					month5: '',
					month6_allocationId: 0,
					month6: '',
					month7_allocationId: 0,
					month7: '',
					month8_allocationId: 0,
					month8: ''
				});
				grid.stopEditing();
				if (combo.value == '0'){
	                extAlert(extAlertTitle, selectProjectToAddMessage, Ext.MessageBox.ERROR);
				} else if(viewedPersonId == 0){
					extAlert(extAlertTitle, resourceNotExistToAllocateMessage, Ext.MessageBox.ERROR);
				} else if (isNotInList(combo.value)){
					if (grid.getStore().getCount()>0){
						ds.insert(grid.getStore().getCount()-1, project);
					} else {
						ds.insert(0, project);
					}
				} else {
					extAlert(extAlertTitle, projectAlreadyAddedMessage, Ext.MessageBox.ERROR);
				}
                grid.startEditing(0, 0);
				}
			}, '-' 
			,{
			    text: '<b>'+submitButtonCaption+'</b>',
				tooltip: { title: saveAllocationsTooltipTitle, text: saveAllocationsTooltip, width:'150'},
				handler : saveGridValues				
			}, '-']
	    }); 
		
		grid.addListener('afteredit', function(e){
			isGridEdited = true;
			if(e.field=='untilEnd'){
				renderUntilEnd(e.value, e.record);
			}else{
				e.record.data.untilEnd='';
			}
			refreshGrid();
		});
		grid.render('gridPosition');		
});

// Array data for the grid
var xgdata1 = Ext.grid.dummyData;
xgdata1 = resourceAllocationData;

function saveGridValues(){	
	if(viewedPersonId == 0){
		extAlert(extAlertTitle, resourceNotExistToAllocateMessage, Ext.MessageBox.ERROR);
		return false;
	}
	
	if(!isGridEdited){
		extAlert(extAlertTitle, noRecordToSaveMessage, Ext.MessageBox.ERROR);
		return false;
	}else{
		isGridEdited = false;
	}

	var allocationArray = '[ ';
	var recordsLength = grid.getStore().getCount();
	if(recordsLength > 0){
		document.getElementById('searchMessage').innerHTML 
				= '<label style="color:red; font-weight:bold;">'+ savingMsg +'</label>';
		setTimeout('2000');
	for(var recIndex = 0; recIndex < recordsLength - 1; recIndex++){
		var record = grid.getStore().getAt(recIndex);		
		allocationArray += '{ "projectId" : '+ record.get('project_id') +','
							+'"allocationIds" : ['+ record.get('month1_allocationId') +','
												+  record.get('month2_allocationId') +','
												+  record.get('month3_allocationId') +','
												+  record.get('month4_allocationId') +','
												+  record.get('month5_allocationId') +','
												+  record.get('month6_allocationId') +','
												+  record.get('month7_allocationId') +','
												+  record.get('month8_allocationId') +'],';
			allocationArray += '"monthValues" : ['+ record.get('month1') +','
								+  record.get('month2') +','
								+  record.get('month3') +','
								+  record.get('month4') +','
								+  record.get('month5') +','
								+  record.get('month6') +','
								+  record.get('month7') +','
								+  record.get('month8') +',]},';
		
	}
	allocationArray = allocationArray.substring(0, allocationArray.length - 1) + ' ]';	
	Ext.Ajax.request({
		       url: jspRootUrl+'/resource/management/allocateBy/dummy/ForSaveAllocations',
			   params: {module: 310, allocationArray: allocationArray},
			   method: 'POST',
			   success: function(result, request){
						resText = result.responseText.split('|&|');
						comboList = resText[1];
						document.getElementById('resource').innerHTML = resText[6];
						document.getElementById('business').value = resText[2];
						document.getElementById('person').value = resText[3];
						document.getElementById('monthsId').value = resText[4];
						document.getElementById('yearsId').value = resText[5];
						
					    xgdata1 = Ext.util.JSON.decode(resText[0]);	
				        ds.loadData(xgdata1);
				        grid.reconfigure(ds,columnModel);
				        grid.getView().refresh();
				        document.getElementById('searchMessage').innerHTML = '';
			   },
			   failure: function(result, response){
					extAlert(extAlertTitle, serverRequestFailedMessage, Ext.MessageBox.ERROR);
			   }
		});	
	}else{
		extAlert(extAlertTitle, noRecordToSaveMessage, Ext.MessageBox.ERROR);
	}
}

function getMonthYear(){	
	if(colMonthIndex > 0){
		if(colMonthIndex > 11 ){
			year = year + 1;	
			colMonthIndex = (colMonthIndex%12);
		}		
	}
	return months[colMonthIndex++]+(year+'').substring(2,(year+'').length);
}		

function fetchDataByMonths(months){
	if(isGridEdited){
		Ext.MessageBox.confirm(confirmIgnoreChangesTitle, 
			ignoreChangesMessage, 
			function(btn){
				if(btn=='yes'){
					isGridEdited = false;
					showResult(months);
				}else{
					return false;
				}
			}
		);
	}else{
		showResult(months);
	}	
}
function showResult(numOfMonths){
	if(document.getElementById('person').value == '0' || document.getElementById('person').value == '') {
		extAlert(extAlertTitle, noResourcesToViewAllocationMessage, Ext.MessageBox.ERROR);
			return false;
	}
	
	if(isNotValidDateRange(numOfMonths)){
		return;
	}
	
	if(isNextPreviousMonthImageButtonsDisabled()){
		return ;
	}
	document.getElementById('viewBtn').disabled = true;
	disableNextPreviousMonthImageButtons();
	
	document.getElementById('searchMessage').innerHTML 
				= '<label style="color:red; font-weight:bold;">'+ loadingMsg +'</label>';
	setTimeout('2000');
	var personId = document.getElementById('person').value;
	var bussinessId = document.getElementById('business').value;
	var startMonth = document.getElementById('monthsId').value;
	var startYear = document.getElementById('yearsId').value;
	
	if(numOfMonths!=0){
		var tempMonthindex = months.indexOf(startMonth) + numOfMonths;
		
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
		   url: jspRootUrl+escapeUrl('/resource/management/AllocateBy/'+ startMonth +'&'+ startYear+'&'+ personId +'&'+ bussinessId +'/ForFetchDataByMonths'),
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){ 
					
					colMonthIndex = months.indexOf(startMonth); 
					year = parseInt(startYear);
					
			        columnModel.setColumnHeader(4, getMonthYear());
			        columnModel.setColumnHeader(6, getMonthYear());
			        columnModel.setColumnHeader(8, getMonthYear());
			        columnModel.setColumnHeader(10, getMonthYear());
			        columnModel.setColumnHeader(12, getMonthYear());					
			        columnModel.setColumnHeader(14, getMonthYear());
			        columnModel.setColumnHeader(16, getMonthYear());
			        columnModel.setColumnHeader(18, getMonthYear());  
			       
			        resText = result.responseText.split('|&|');
					comboList = resText[1];
					viewedPersonId = resText[2];
					
			        xgdata1 = Ext.util.JSON.decode(resText[0]);					
					if(xgdata1 == ''){						
						document.getElementById('searchMessage').innerHTML = '<label style="color:red; font-weight:bold;">'+reservationNotFoundMessage+'</label>';
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

window.onresize = resizeGrid;
function resizeGrid() {
	getWindowHeightWidth();
	try{
		grid.setHeight(windowHeight-310);
		grid.setWidth(windowWidth-230);
		grid.getView().refresh();
	}catch(err){}
}

function refreshGrid(){
	var recordsLength = grid.getStore().getCount();
	var columnTotal=[0,0,0,0,0,0,0,0];
	var gridData = '[ ';
	var recordsLength = grid.getStore().getCount();
	if(grid.getStore().getAt(recordsLength-1).get('projectName') == gridTotalRowLabel){
		recordsLength--;
	}
	for(var recIndex = 0; recIndex < recordsLength; recIndex++){
		var record = grid.getStore().getAt(recIndex);	
		gridData +='['+ record.get('project_id') +',"'+ record.get('projectName') +'",'+record.get('untilEnd')+',';
			gridData +=	  record.get('month1_allocationId') +','+  record.get('month1') 
				+','+  record.get('month2_allocationId') +','+  record.get('month2') 
				+','+  record.get('month3_allocationId') +','+  record.get('month3') 
				+','+  record.get('month4_allocationId') +','+  record.get('month4') 
				+','+  record.get('month5_allocationId') +','+  record.get('month5') 
				+','+  record.get('month6_allocationId') +','+  record.get('month6') 
				+','+  record.get('month7_allocationId') +','+  record.get('month7')
				+','+  record.get('month8_allocationId') +','+  record.get('month8')+'],'; 
			
			columnTotal[0] += record.get('month1') == '' ? 0 : parseFloat(record.get('month1'));
			columnTotal[1] += record.get('month2') == '' ? 0 : parseFloat(record.get('month2'));
			columnTotal[2] += record.get('month3') == '' ? 0 : parseFloat(record.get('month3'));
			columnTotal[3] += record.get('month4') == '' ? 0 : parseFloat(record.get('month4'));
			columnTotal[4] += record.get('month5') == '' ? 0 : parseFloat(record.get('month5'));
			columnTotal[5] += record.get('month6') == '' ? 0 : parseFloat(record.get('month6'));
			columnTotal[6] += record.get('month7') == '' ? 0 : parseFloat(record.get('month7'));
			columnTotal[7] += record.get('month8') == '' ? 0 : parseFloat(record.get('month8'));
	}
	gridData += "['0','"+gridTotalRowLabel+"','',''," 
			+ columnTotal[0].toFixed(2) + ",''," 
			+ columnTotal[1].toFixed(2) + ",'',"
			+ columnTotal[2].toFixed(2) + ",''," 
			+ columnTotal[3].toFixed(2) + ",''," 
			+ columnTotal[4].toFixed(2) + ",''," 
			+ columnTotal[5].toFixed(2) + ",''," 
			+ columnTotal[6].toFixed(2) + ",''," 
			+ columnTotal[7].toFixed(2) + "]]";
    xgdata1 = Ext.util.JSON.decode(gridData);
	ds.loadData(xgdata1);
	grid.reconfigure(ds,columnModel);
	grid.getView().refresh();
}
window.onbeforeunload = function(){
	if(isGridEdited){
		return ignoreChangesMessage;
	}
};
