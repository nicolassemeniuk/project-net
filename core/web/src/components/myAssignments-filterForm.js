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
var assignmentNameComparatorComboData	=	[['equals','Equal To'],['notequals','Not Equal To'],['contains','Contains']];	
var percentCompleteComparatorComboData	=	[['equals','Equal To'],['notequals','Not Equal To'],['lessthan','Less Than'],['greaterthan','Greater Than']];	

var workSpace;
var percentCompleteComparator;
var businessCombo;
var allAssignments;
var lateAssignment;
var assignmentComingDue;
var shouldHaveStart;
var inProgress;
var fromDate;
var toDate;
var percentWorkComplete;
var	assignmentNameComparator;
var assignmentName;

var filterForm = new Ext.FormPanel({
		labelWidth	: 75,
		frame		: true,
		collapsible	: true,
		collapsed	: true,
		titleCollapse : true,
		//hideCollapseTool : true,
       	labelAlign	: 'left',
       	title		: '<b>Filters</b>',
	   	height		: 210,
       	width		: 565,
       	items		: [{        	
			layout	:'column',
			border	:false,
			items	:[{//column 1
				columnWidth	: .3,						   
				layout		: 'form',
				border		: false,
				items		: [
					percentCompleteComparator =	new Ext.form.ComboBox({
						fieldLabel : '<b>%Complete</b>',
						labelAlign	: 'left',
						store : new Ext.data.SimpleStore({
							fields	: ['code', 'desc'], 
							data	: percentCompleteComparatorComboData
						}),
						listClass: 'x-combo-list-small',
						valueField : 'code',
						displayField : 'desc',
						editable : false,
						typeAhead : true,
						mode : 'local',
						triggerAction: 'all',
						value : percentCompleteComparatorValue,
						selectOnFocus : true,
						width : 70
					 }),
					 assignmentNameComparator = new Ext.form.ComboBox({
							fieldLabel: '<b>Name</b>',
							labelAlign	: 'left',
							store : new Ext.data.SimpleStore({
							   fields : ['code', 'desc'], 
							   data	: assignmentNameComparatorComboData
							}),
							listClass: 'x-combo-list-small',
							valueField :'code',
							displayField :'desc',
							value :assignmentNameComparatorValue,
							editable : false,
							typeAhead : true,
							mode : 'local',
							triggerAction : 'all',
							selectOnFocus :true,
							labelSeparator : ':',
							width :70
					}),
					fromDate = new Ext.form.DateField({
						fieldLabel : '<b>Dates</b>',
						name : 'fromDate',
						labelWidth:30,
						value : startDateValue,
						emptyText : 'Start Date',
						format: getJSUserDatePattern(userDateFormatString),
						invalidText:'Current date is not a valid date. It must be in the format '+ userDateFormatString,
						width : 70,
						labelSeparator : ':'
					}),
					{html:'<b>Project Filter:</b>', hieght:10},
					workSpace = new Ext.ux.Multiselect({
						 dataFields  :  ['code', 'desc'],
						 data :  projectOptionsData,
						 valueField  : 'code',
						 displayField  : 'desc',
						 listClass: 'x-combo-list-small',
						 width :  150,
						 height :  80,
						 allowBlank :  true,												
						 hideLabel :  true
					})
					]
				},{//column 2
				columnWidth	:.3,						   
				layout: 'form',
				border: false,
				items: [
					 percentWorkComplete  = new Ext.form.NumberField({
						hideLabel : true,
						width :100,
						value : percentCompleteValue * 100,
						allowNegative: false,
						style: 'text-align:left'
					 }),
					assignmentName = new Ext.form.TextField({																
						hideLabel : true,
						value : assignmentNameValue,
						width :100
					}),
					toDate = new Ext.form.DateField({
						hideLabel : true,
						emptyText : 'Due Date',
						value : endDateValue,
						format: getJSUserDatePattern(userDateFormatString),
						invalidText:'Current date is not a valid date. It must be in the format '+ userDateFormatString,
						width :70
					})
					]
				},{//column 2
				columnWidth	:.4,						   
				layout : 'form',
				border : false,												
				items : [
					businessCombo = new Ext.form.ComboBox({
						store : new Ext.data.SimpleStore({
							fields :	['code', 'desc'], 
							data : businessOptionsData
						}),
						fieldLabel : '<b>Business</b>',
						listClass: 'x-combo-list-small',
						valueField :'code',
						displayField : 'desc',
						editable : false,
						typeAhead : true,
						mode : 'local',
						triggerAction : 'all',
						value : businessValue,
						emptyText  : 'No Businesses',
						selectOnFocus : true,
						labelSeparator : ':',
						listWidth : 140,
						width : 100,
						listeners:{'select' : function (thisCombo, record, index){
							if(thisCombo.value != ''){
								workSpace.setDisabled(true);
							}else{
								workSpace.setDisabled(false);
							}
						}}
					}),
					allAssignments = new  Ext.form.Checkbox ({
						labelSeparator : '',
						id : 'allassignment',
						labelAlign	: 'left',
						//fieldLabel : '<b>All Assignments</b>'
						checked  : true,
						boxLabel : '<b>All Assignments</b>',
						listeners: {'check' : assignmentTypeChecked}
					 }),
					 lateAssignment = new  Ext.form.Checkbox ({
					 	labelSeparator : '',
					 	id : 'lateassignment',
						boxLabel : '<b>Late Assignments</b>',
						listeners: {'check' : assignmentTypeChecked }
					 }),
					 assignmentComingDue = new  Ext.form.Checkbox ({
					 	labelSeparator : '',
					 	id : 'assignmentcomingdue',
						boxLabel : '<b>Coming Due</b>',
						listeners: {'check' : assignmentTypeChecked}
					 }),
					 shouldHaveStart = new  Ext.form.Checkbox ({
						labelSeparator : '',
						id : 'shouldhavestart',
						boxLabel : '<b>Should Have Started</b>',
						listeners: {'check' : assignmentTypeChecked}
					 }),
					 inProgress = new  Ext.form.Checkbox ({
					 	labelSeparator : '',
					 	id : 'inprogress',
						boxLabel : '<b>In Progress</b>',
						listeners: {'check' : assignmentTypeChecked}
					 })
					],
				buttons :[{
					text: '<b>Apply</b>',
					ctCls : 'x-btn-over',
					handler : function(){
						applyFilter	(
									businessCombo.getValue(),
									workSpace.getValue(), 
									lateAssignment.getValue(),
									assignmentComingDue.getValue(),
									shouldHaveStart.getValue(),
									inProgress.getValue(),
									fromDate.getValue(),
									toDate.getValue(),
									percentCompleteComparator.getValue(),
									percentWorkComplete.getValue(),
									assignmentNameComparator.getValue(),
									assignmentName.getValue()
						);
					}
				}]
        	}]
        }]
    });

    
function assignmentTypeChecked(thisCheckbox, checked) {
	if(checked){
		if(thisCheckbox.id == 'allassignment'){
			lateAssignment.setValue(false);
			assignmentComingDue.setValue(false);
			shouldHaveStart.setValue(false);
			inProgress.setValue(false);
		}else{
			allAssignments.setValue(false);
		}
	}
}

function isStartDateAfterEndDate(startDate, endDate){
	if(startDate.getFullYear() > endDate.getFullYear()){
		return true;
	}
	if( startDate.getFullYear() == endDate.getFullYear() && startDate.getMonth() > endDate.getMonth()){
		return true;
	}
	if(startDate.getFullYear() == endDate.getFullYear() && startDate.getMonth() == endDate.getMonth() && startDate.getDate() > endDate.getDate()){
		return true;
	}
	return false;
}

function applyFilter(business, projects, lateAssignment, comingDueDate, shouldHaveStart, inProgress, startDate, endDate, percentCompleteComparator, percentComplete, assignmentNameComparator, assignmentName){
    	var startDateString = '';
		var endDateString = '';
		if(checkValidDate(startDate, endDate)){
			if (startDate != '' && endDate != '') {
				startDateString = startDate.dateFormat(getJSUserDatePattern(userDateFormatString));
				endDateString = endDate.dateFormat(getJSUserDatePattern(userDateFormatString));
			}
		} else {
			return;
		}
		
    	if(business != '' || projects == '0'){
    		projects = '';
    	}
    	
    	// if no any assignment checked checke all assingment.
		if(!(lateAssignment || comingDueDate || shouldHaveStart || inProgress)){
			allAssignments.setValue(true);
		}
		
    	Ext.Ajax.request({
		   url: JSPRootURL+'/assignments/My/setFilterParameter',
		   params: {module: moduleId, 
		   			business: business,
		   			projects: projects,
					lateAssignment: lateAssignment,
					comingDueDate: comingDueDate,
					shouldHaveStart	: shouldHaveStart,
					inProgress: inProgress,
					startDate: startDateString,
					endDate	: endDateString,
					percentCompleteComparator:percentCompleteComparator,
					percentComplete :percentComplete,
					assignmentNameComparator :assignmentNameComparator,
					assignmentName : assignmentName
		   },
		   method: 'POST',
		   success: function(result, request){
		   		nodeSelect = false;
		   		refreshAssignmentGridAndCountFromStart(true);
		   		assignmentTreeNodeId = 0;
		   },
		   failure: function(result, response){
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
}

// to check date validations
function checkValidDate(startDate, endDate) {
	if(!fromDate.isValid()){
		extAlert('Error', 'Invalid start date!', Ext.MessageBox.ERROR);
		return false;
	} else if(!toDate.isValid()){
		extAlert('Error', 'Invalid due date!', Ext.MessageBox.ERROR);
		return false;
	}
	if(startDate != null && startDate != ''&& (endDate == null || endDate == '' )){
		extAlert('Error', 'Please select due date.', Ext.MessageBox.ERROR);
		return false;
	}
	if((startDate == null || startDate == '') && endDate != null && endDate != ''){
		extAlert('Error', 'Please select start date.', Ext.MessageBox.ERROR);
		return false; 	
	}
	if(startDate != '' && endDate != '' && isStartDateAfterEndDate(startDate, endDate)){
		extAlert('Error', 'Start date can not be after due date.', Ext.MessageBox.ERROR);
		return false;
	}
	return true;
}