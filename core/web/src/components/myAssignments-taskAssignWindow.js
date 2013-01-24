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
var workUnitComboData =  [ ['4','hours'],['8','days'],['16','weeks']];	
var assignResourceGrid;
var resourceIdCheck;
var assignResouceForm;
var assignmentWindow;
var selectedTaskId;
var startDate;
var dueDate;
var formTitle;


String.prototype.trim = function(){
    a = this.replace(/^\s+/, '');
    return a.replace(/\s+$/, '');
}
	Ext.QuickTips.init();
	Ext.BLANK_IMAGE_URL = JSPRootURL+'/images/default/s.gif';
function initializeAssignmentWindow(){
	formTitle = workSpaceName != '' ? workSpaceName : objectName;
	resourceIdCheck = new Ext.grid.CheckboxSelectionModel({
		listeners: { 'rowselect': function ( thisSelectionModel, rowIndex, record){
						setResourcesNames(record);
					},
					'rowdeselect': function ( thisSelectionModel, rowIndex, record){
						resetResourcesNames(record);
					}
					   
		}
	});

	assignResourceGrid = new Ext.grid.EditorGridPanel({
	        store: new Ext.data.SimpleStore({
	        	autoLoad : true,
				url: JSPRootURL+'/assignments/My/GETRESOURCESTOASSIGN?module='+moduleId,
		        fields: [
		           {name: 'resourceId'},	
		           {name: 'resourceName'},
		           {name: 'percentAssigned'}, 
		           {name: 'personTimeZone'},
		           {name: 'utilizationSummary'} 
		        ]
			}),
	        cm: new Ext.grid.ColumnModel([
			 	 resourceIdCheck,
			 	 {header: "personId", dataIndex: 'resourceId', hidden : true},
			 	 {id:'person', header: 'Person', dataIndex: 'resourceName', width: 220 },
			     {header: "% Assigned", dataIndex: 'percentAssigned', width: 100, 
			     editor: new Ext.form.NumberField({
				            allowNegative: false,
				            allowBlank :false,
					        style: 'text-align:left',
							maxValue:100,
							minValue:0
					    }),
				 renderer: function (v, params, record){
			    	        params.attr = 'ext:qtip = "<b>'+clickToEditTooltip+'</b>"';
			    	        return v;
			    	    }
				 },
			     {header: "Utilization Summary", dataIndex: 'utilizationSummary',width: 100,  style :'text-align:right', renderer : renderUtilizationImaege}
		   ]),
	        sm: resourceIdCheck,
	        stripeRows : true,
	        frame : true,
	        autoExpandColumn: 'person',
	        height : 170,
	        width : 460,
	        iconCls : 'icon-grid',
	        clicksToEdit : 1,
	        title:'Resources',
	        titleCollapse : true,
	        deferRowRender:false,
	        collapsed: true,
	        collapsible: true,
	        listeners: { 'expand': function (thisGrid){
	        				assignmentWindow.setHeight(460);
					},
					'collapse': function (thisGrid){
						assignmentWindow.setHeight(320);
					}
			}
	 });
	
	assignResouceForm  = new Ext.FormPanel({
	        labelWidth: 75,
			labelAlign:'center',
	        frame:true,
	        bodyStyle:'padding:5px 5px 0',
	        width: 500,
	        height:350,
	        items: [
	        		{html:'<table width="100%" cellspacing="0"><tr height="22px" style="font-size:12px;"><td width="17%"><b>'+projectTitle+'</b></td><td><b>'+formTitle+'</b></td></tr></table>'},
	        		taskName = new Ext.form.TextField({
	               			fieldLabel : '<b>'+textFieldNameLabel+'</b>',
	               			labelSeparator : ':',
	               			width : 250,
	               			maxLength : 255,
				        	style: 'text-align:left',
							allowBlank :false
				        	
				    }),
				    taskDescription = new Ext.form.TextArea({
					    	fieldLabel: '<b>'+textFieldDescriptionLabel+'</b>',
					    	labelSeparator:':',
					    	width : 250,
					    	height : 50,
					    	maxLength :1000,
					        style: 'text-align:left'
					}),
	        	{
	            layout:'column',
	            border:false,
	            items:[{
					columnWidth:.3,
					layout: 'form',
					border:false,
					items: [
						taskWork = new Ext.form.NumberField({
							id : 'work',
				    		fieldLabel: '<b>'+numberFieldWorkLabel+'</b>',
				    		labelSeparator:':',
				    		width:55,
				    		labelAlign	: 'left',
				    		allowBlank :false,
				    		allowNegative: false,
					        style: 'text-align:left',
							maxLength : 10,
							listeners : {'change':calculateTask}
				        })
	                  ]
	           		},{
					columnWidth:.7,
					layout: 'form',
					border:false,
					items: [
					    taskWorkUnit = new Ext.form.ComboBox({
		                       hideLabel : true,
		                       id : 'workUnit',
		                       store: new Ext.data.SimpleStore({
		                          fields: ['unitValue', 'workUnit'], 
		   						data : workUnitComboData
		                       }),
		                       valueField:'unitValue',
		                       displayField:'workUnit',
		                       typeAhead: true,
		                       mode: 'local',
		                       value:'4',
		                       triggerAction: 'all',
		                       emptyText:'Select...',
		                       selectOnFocus:true,
		                       width:60,
		                       listWidth:60,
		                       style: 'text-align:left',
		                       listClass: 'x-combo-list-small',
							   listeners : {'change':calculateTask}
		                       
		                  })
	               		]
	               	}]
	                },
				        startDate = new Ext.form.DateField({
				        	id : 'startDate',
							fieldLabel : '<b>'+startDateLabel+'</b>',
							name : 'startDate',
							labelWidth:30,
							format: getJSUserDatePattern(userDateFormatString),
							width : 100,
							invalidText: msgFormat.format(dateFormatErr, userDateFormatString),
							labelSeparator : ':',
						    listeners : {'change':calculateTask}
					   }),
					   dueDate = new Ext.form.DateField({
					   		id : 'dueDate',
							fieldLabel : '<b>'+endDateLabel+'</b>',
							name : 'End Date',
							labelWidth:30,
							format: getJSUserDatePattern(userDateFormatString),
							width : 100,
							invalidText:msgFormat.format(dateFormatErr, userDateFormatString),
							labelSeparator : ':',
							listeners : {'change':calculateTask}
					   }),
					{html:'<b>'+resourcesLabel+' &nbsp;&nbsp;&nbsp;</b><span id="selectedResourceName"></span>', hieght:30},   
					{html:'&nbsp;', hieght:10},
					assignResourceGrid ],
			buttons:[{
	                 text:'Submit',
	                 handler: function (){
	                 		if(taskCalculated)
	                 			submitAssignResouceForm();
	                 	}
	                 },
	                 { text: newAssignmentCancelLabel,
	                   handler: function(){
							assignmentWindow.hide();
							assignmentWindow.destroy();
	                  	}
	                 }
	            ]
	    });
	
	assignmentWindow = new Ext.Window({
	                layout:'fit',
	                title: newAssignmentHeader ,
	                width:500,
	                height:320,
	                closeAction:'hide',
	                plain: true,
	                listeners : {'hide' : function (thisWindow){
		                	document.getElementsByTagName('body')[0].removeChild(blogPopupScreen);
	                	}
	                },
	                items:[assignResouceForm]
				});

}

function setResourcesNames(record) {
	if(document.getElementById('selectedResourceName').innerHTML.search(record.data.resourceName) != -1) {
		return;
	} else {
		if(document.getElementById('selectedResourceName').innerHTML != ''){
			document.getElementById('selectedResourceName').innerHTML += ',';	
		}
		document.getElementById('selectedResourceName').innerHTML += record.data.resourceName;		
	}
}
	
function resetResourcesNames(record) {
	var resourcesNames = document.getElementById('selectedResourceName').innerHTML;
	resourcesNames = resourcesNames.replace(record.data.resourceName,"");
	if(resourcesNames.charAt(resourcesNames.length - 1 ) == ',') {
		resourcesNames = resourcesNames.substring(0, resourcesNames.length - 1);
	}	
	if(resourcesNames.charAt(0) == ',') {
		resourcesNames = resourcesNames.substring(1, resourcesNames.length);
	}	
	if(resourcesNames.search(',,') != -1){	
		resourcesNames = resourcesNames.replace(',,', ',');	 	 
	}	
	document.getElementById('selectedResourceName').innerHTML = resourcesNames;
}
	
function renderUtilizationImaege(v, params, record){
	params.attr = 'ext:qtip = "<b>'+utilizationTooltip+' </b>"';
	return '<a href="#"><img src="'+JSPRootURL+'/images/icons/toolbar-utilization-summary-icon-on.gif" border="0" height = "14" width = "14" onclick="showResourceAllocation('+record.data.resourceId+');"/></a>';
}

function submitAssignResouceForm(){
	var resourceIdRecord = resourceIdCheck.getSelections();
	var resourceIds = '';
	var percentAssigned = '';
	
	if(!validateTaskProperty()){
		return;
	}
	if(resourceIdRecord.length == 0 ){
		extAlert(errorAlertTitle, selectResourceMessage, Ext.MessageBox.ERROR);
		return ;
	}
   	for(var i=0; i<resourceIdRecord.length; i++){
   		resourceIds += resourceIdRecord[i].data.resourceId + ',';
	   	if(resourceIdRecord[i].data.percentAssigned == ''){
		   	extAlert(errorAlertTitle, percentAssignedMessage, Ext.MessageBox.ERROR);
		   	return;
	   	}
   		percentAssigned += resourceIdRecord[i].data.percentAssigned + ',';
   	}
   	
   	assignmentWindow.hide();
   	assignmentWindow.destroy();
   	Ext.get(document.body).mask(waitingMessage,'x-mask-loading');
   	
	Ext.Ajax.request({
 		url : JSPRootURL+'/assignments/My/addAndAssignTask',
		params: {module: moduleId , 
				taskName : taskName.getValue().trim(),
				taskDescription : taskDescription.getValue(),
				resourceIds : resourceIds,
				percentAssigned : percentAssigned
				},
		method: 'POST',
		timeout : 70000,
		success: function(result, request){
			Ext.get(document.body).unmask();
			if(result.responseText.trim() != ''){
				extAlert(errorAlertTitle, result.responseText, Ext.MessageBox.ERROR);
			}else{
				refreshAssignmentGridAndCountFromStart(false);
		   	}
		},
		failure: function(result, response){
			Ext.get(document.body).unmask();
			
			extAlert(errorAlertTitle, serverRequestFailedMessage, Ext.MessageBox.ERROR);
		}
		});
}

function calculateTask(thisField, newValue, oldValue) {
	taskCalculated = false
	if(!taskWork.isValid(false)){
		extAlert(errorAlertTitle, workErrorMessage, Ext.MessageBox.ERROR);
		return;
	}
	var startDateString = '';
	var dueDateString = '';
	if(startDate.getValue() != ''){
		date = startDate.getValue();
		startDateString = date.dateFormat(getJSUserDatePattern(userDateFormatString));
	}
	if(dueDate.getValue() != ''){
		date = dueDate.getValue();
		dueDateString = date.dateFormat(getJSUserDatePattern(userDateFormatString));
	}
	if(startDateString == '' || dueDateString == ''){
		extAlert(errorAlertTitle, validDateMessage, Ext.MessageBox.ERROR);
		return;
	}
	
	Ext.Ajax.request({
 		url : JSPRootURL+'/assignments/My/calculateTask',
		params: {module: moduleId ,
				calculateBy: thisField.id,
				work : taskWork.getValue(),
				workUnit: taskWorkUnit.getValue(),
				startDate: startDateString,
				dueDate : dueDateString
				},
		method: 'POST',
		success: function(result, request){
			var responseObject = eval("(" +result.responseText +")" );
			if(responseObject.error == ''){
				taskWork.setValue(responseObject.work);
				taskWorkUnit.setValue(responseObject.workUnit);
				startDate.setValue(responseObject.startDate);
				dueDate.setValue(responseObject.dueDate);
			} else{
		    	extAlert(errorAlertTitle, responseObject.error, Ext.MessageBox.ERROR);
		    	taskWork.setValue('');
		    }
		    taskCalculated = true;
		},
		failure: function(result, response){
			taskCalculated = true;
			extAlert(errorAlertTitle, serverRequestFailedMessage, Ext.MessageBox.ERROR);
		}
	});
	
}
function validateTaskProperty(){
	if(!taskName.isValid(false) || taskName.getValue().trim().length == 0 ){
		extAlert(errorAlertTitle, validTaskMessage, Ext.MessageBox.ERROR);
		return false;
	}
	if(!taskDescription.isValid(false)){
		extAlert(errorAlertTitle, taskDescriptionMessage, Ext.MessageBox.ERROR);
		return false;
	}
	if(!taskWork.isValid(false)){
		extAlert(errorAlertTitle, workErrorMessage, Ext.MessageBox.ERROR);
		return false;
	}
    if(!verifyNoHtmlContent(taskName.getValue(), noHtmlContentMessage)){
    	return false;
    }
	return true;
}

function selectResourceAsDefault(resoruceId){
	var recordsLength = assignResourceGrid.getStore().getCount();
	for(var row = 0; row < recordsLength; row++){
		if(assignResourceGrid.getStore().getAt(row).get('resourceId') == resoruceId){
			resourceIdCheck.selectRow( row, true );
			assignResourceGrid.getStore().getAt(row).set('percentAssigned', 100);
			document.getElementById('selectedResourceName').innerHTML = assignResourceGrid.getStore().getAt(row).get('resourceName');
		}
	}
}

function create(){
	//If no data in assignment grid
	if (firstAssignmentID == ''){
		extAlert('Error', noProjectTaskAvailableMsg, Ext.MessageBox.ERROR);
		return;
	}
	if(assignmentTreeNodeId != 0 && ( objectType.indexOf('task') != -1   || objectType == 'summary' || objectType == 'project')){
		//Mask a screen as 'please wait' to prevent other actions during this process.
		Ext.get(document.body).mask(waitingMessage,'x-mask-loading');
		taskCalculated = true;
		selectedTaskId = assignmentTreeNodeId;
		if(objectType == 'project'){
	   		selectedTaskId = '';
	   	} 
		Ext.Ajax.request({
	 		url : JSPRootURL+'/assignments/My/getNewAssignmentData',
			params: {module: moduleId ,
					projectId : taskSpaceId,
					selectedTaskId : selectedTaskId
					},
			method: 'POST',
			success: function(result, request){
				var responseObject = eval("(" +result.responseText +")" );
				//Now unmask the please wait screen.
				Ext.get(document.body).unmask();
				if(responseObject.error == ''){
					initializeAssignmentWindow();
					taskWork.setValue(responseObject.work);
					taskWorkUnit.setValue(responseObject.workUnit);
					startDate.setValue(responseObject.startDate);
					dueDate.setValue(responseObject.dueDate);
			    	blogPopupScreen.style.height = window.screen.height+'px';
			    	document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);
			    	assignmentWindow.show();
			    	selectResourceAsDefault(userId);
		    	} else {
		    		extAlert(errorAlertTitle, responseObject.error, Ext.MessageBox.ERROR);
		    	}
			},
			failure: function(result, response){
				//Now unmask the please wait screen.
				Ext.get(document.body).unmask();
				extAlert(errorAlertTitle, serverRequestFailedMessage, Ext.MessageBox.ERROR);
			}
		});
	} else {
		extAlert(errorAlertTitle, selectTaskOrProject, Ext.MessageBox.ERROR);
	}
}

function showResourceAllocation(personID) {
    var url = JSPRootURL+'/resource/ResourceAllocations.jsp?module=140&personID='+ personID;
    openwin_large('resource_allocation', url);
}

// function to replace all occurences of strA with strB in given text
function replaceAll(text, strA, strB) {
    while ( text.indexOf(strA) != -1) {
        text = text.replace(strA,strB);
    }  
    return text;
}