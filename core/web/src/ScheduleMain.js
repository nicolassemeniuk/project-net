//use to set object type eg task,summry. Directly wsed in blogit and some other places.
var objectType;   
var newTask_sDate;
var newTask_fDate;
//use to preserve current task ID eg 12345.
var currentEntryID;
//use to preserve current edited field abbrivation eg tn for Task Name.
var currentField;
//use to preserve current cell ID eg tn_12345.
var currentCellID;
//use to preserve current cell object.
var currentCellObject; 
//previously displayed value, use to reset cell value. 
var displayedValue;
//flag to show schedule is change.
var scheduleChanged = false;
var keyCode = 0;
var scheduleAltered = false;
var tabCounter = 0;
var functionNameString = new Array ('javascript:blogit();','javascript:modify();','javascript:remove();','javascript:share();','javascript:security();','javascript:taskup();','javascript:taskdown();','javascript:unindent();','javascript:indent();','javascript:resources();','javascript:linkTasks();','javascript:unlinkTasks();');
var linksToEnable = '';
var flatLoaded = false;
var orderDescending = false;
var sortingApplied = false;
var sortedColumn;
var slidingPaneClosed = false;
var rightPaneClosed = false;
var taskListPanelWidth;
var isSDFrom = false;
var isSDTo = false;
var isEDFrom = false;
var isEDTo = false;
var scheduleTabRight = false;
var taskListScrollLeft = 0;
var taskListScrollTop = 0;
var newRowPosition = null;
var childTaskList = '';
var isRecalculateComplete = false;
var isScheduleSaved = true;
var dateFieldEditor;
var blogPopupScreen = document.createElement('div');
blogPopupScreen.id = 'blogPopupScreen';
blogPopupScreen.style.display = 'block';
blogPopupScreen.style.height = (window.screen.height - 80) + 'px';
var warningWindow;
Ext.BLANK_IMAGE_URL = JSPRootURL+'/images/default/s.gif';

//submits all action parameter to serverside, performs action and also update tasklist.
function submitParameter(parameterString){
	//Initialize action which is to perform
	var newAction = Ext.util.JSON.decode(parameterString).action;
	
	//If any non shcedule editing action perform,
	if(scheduleChanged && newAction.indexOf("Changed") < 0 && newAction != "reload" && newAction != "saveSchedule"){
		//Confirm the schedue changes will revert .
		if(!confirm(modifiedMessage)){
			return;
		}
	}
	
	//Remove any action messages if displayed
	removeActionMessageDiv();
	//Show loading message untill response get back. variable "loadingMessage" is defined in TML page.
	showLoadingDiv(loadingMessage);
	//Add a transparent screen on page so that other opeteration can be avoid untill this request is complete.
	addLightBox();
	
	//We need to retain last selection so initialize it, will use to select these id after response get back. 
	lastSelectedIds = getSelections(theForm, ",");
	//Also keep scrolling position of task list panel because we need to set it again after response get back. 
	taskListScrollLeft = document.getElementById('task-list').scrollLeft;
	taskListScrollTop = document.getElementById('task-list').scrollTop; 
		
	//Ajax request
	Ext.Ajax.request({
		url: JSPRootURL + '/workplan/taskview/performaction',
		params: {parameterString : parameterString },
		method: 'POST',
		timeout: 180000,
		success: function(result, request){
			//We have two option to update task list after success, it is based on reponse: 
			//1. update only cell if schedule has not big chagnes, if rsponse is JSON string
			//2. Update all entire task list by replaceing innerHTML, if  response is HTML 
			//First identify response type HTML or JSON
			if(result.responseText.substring(0,1) != '<'){
				//If rsponse is JSON it will not start from tag "<"
				populateJSONResponse(eval("("+result.responseText+")"));
			} else if(result.responseText.substring(0,1) == '<') {
				//If rsponse is HTML it will start from tag "<"
				//Replace all task list HTML by new update reponse HTML 
				document.getElementById('task-list-container').innerHTML = result.responseText;
				//We need tup also update scheule status bar.
				if(document.getElementById('schedule_info_table')){
	 		    	updateScheduleStatusBBar(eval("("+document.getElementById('schedule-status').innerHTML+")"));
				}
				//show error if any
				if(document.getElementById('errorPanel').innerHTML != ''){
					showActionMessageDiv(document.getElementById('errorPanel').innerHTML);
	 		    	document.getElementById('errorPanel').innerHTML = '';
 		    	}
 		    	
	 		    //Update Gantt View too if if ganttChartPanel is defined
	 		    if(document.getElementById('ganttChartPanel')){ 
					document.getElementById('ganttChartPanel').src = document.getElementById('ganttChartPanel').src+'&'+(new Date()).getTime(); 
				}
				//reset tasklist panel/table height width;
 		    	resetPanelsWidth();
 		    	resetPanelsHeight();
 		    	resizeHeader();
 		    	//set task list left scroll position
 		    	document.getElementById('task-list').scrollLeft = taskListScrollLeft;
 		    	document.getElementById('task-list').scrollTop = taskListScrollTop;
			}
		},
		callback:function(result , response){
			// Remove the light box effect
			if(document.getElementById('blogPopupScreen') != null && !(warningWindow && warningWindow.isVisible())){
				document.getElementsByTagName('body')[0].removeChild(blogPopupScreen);
			}
			scheduleChanged = (newAction.indexOf("Changed") >= 0 && newAction != "sortChanged");
			if(scheduleChanged){
				displayButtons();
			}else{
				isRecalculateComplete = true;
				hideButtons();
			}
			//If action is quick add we need to add one more row for new task.
			if(newAction == "quickAdd" && document.getElementById('taskListTable').rows[newRowPosition]){
				//Check if selected task is summary and collapsed.
				var selectedId = getSelection(theForm);
				if(document.getElementById(selectedId) && document.getElementById(selectedId).getAttribute("kidsshown") == "false"){
					//Find the new created task. if task created by selecting summary task with collapsed state,
					//the task get created at the same level.
					var selectedRowLevel = document.getElementById(selectedId).getAttribute("level");
					var nextRow;
					do {
						nextRow = document.getElementById('taskListTable').rows[++newRowPosition];
					} while(nextRow.getAttribute("level") > selectedRowLevel)//if next row level is more then selected task level
					//Now the nextRow level and selected task level is same so this row must be new crated row.
					lastSelectedIds = nextRow.id;
				}else{
					lastSelectedIds = document.getElementById('taskListTable').rows[newRowPosition].id;
				}
				
				//load propertry for selected task.
				b(null, lastSelectedIds, "task");
				newRowPosition++;
				quick_add();
			}
			setup();
			initSortingImage();
		},
		failure: function(result, response){
			// Remove the light box effect
			if(document.getElementById('blogPopupScreen') != null){
				document.getElementsByTagName('body')[0].removeChild(blogPopupScreen);
			}
		}
	});
}

//To update inline edit data
function populateJSONResponse(taskData){
	//First initialize data length
	var dataLength = parseInt(taskData.length);
	//If task data length is less then two just return from here.
	//Since if any update it must have atleast length two, atleast one row update and schedule info.
	if( dataLength < 2){
		return;
	}
	 //the last index contains schedule information
	var scheduleInfo = eval("("+taskData[dataLength-1]+")");
	
	//Show warning if nonworking day date editing.
	if(warningEnable && scheduleInfo.isNonworkingDay){
		initializeWarningWindow(scheduleInfo, taskData);
		warningWindow.show();
		return;
	}
	
	//Show warning message if schedule date changed.
	if(warningEnable && scheduleInfo.scheduleDateChanged){
		showScheduleDateChagneWarning(scheduleInfo, taskData);
		return;
	}
	
	//No warning, update the tasksheet.
	updateInformation(scheduleInfo, taskData);
}

function showScheduleDateChagneWarning(scheduleInfo, taskData){
	Ext.Msg.show({
	   title:workPlanWarningTitle,
	   msg: msgFormat.format(scheduleDateChangeWarningMsg, scheduleInfo.changeType, scheduleInfo.date),
	   buttons: Ext.Msg.OKCANCEL,
	   fn: function(btn){
			if(btn != 'ok'){
				revert();	
				return;
			}else{
				//OK update the tasksheet.
				updateInformation(scheduleInfo, taskData);
			}
		},
	   icon: Ext.MessageBox.WARNING
	});
}

function updateInformation(scheduleInfo, taskData){
	//Update task row
	updateTaskRow(taskData);
	//Update schedule status bar.
	updateScheduleStatusBBar(scheduleInfo);
	//show message message if any
	if(scheduleInfo.message != '')
		showActionMessageDiv(scheduleInfo.message);
	
}

//To update rows 
function updateTaskRow(taskData){
	//Now update all cell by response string task data
	//Since last index contain schedule status info so we dont need to take last index of array.
	for(var index = 0; index < taskData.length-1; index++){
		var record = eval("("+taskData[index]+")");
		//Update work cell.
		updateCellValue('w_'+record.id, record.taskWork);
		//update duration cell.
		updateCellValue('d_'+record.id, record.taskDuration);
		//update start date cell.
		updateCellValue('sd_'+record.id, record.taskStartDate, record.hiddenStartDate);
		//update end date cell.
		updateCellValue('ed_'+record.id, record.taskEndDate, record.hiddenEndDate);
		//update work complete cell.
		updateCellValue('wc_'+record.id, record.taskWorkComplete);
		//update work percent complete cell.
		updateCellValue('wpc_'+record.id, record.taskWorkPercentComplete);
	 }
	//apply uniform cell css, Since at the time of edting cell we make some changes on cell style to avoid filcking.
	applyCellStyles();
}

//Update specified cellID element by specified value and also hidden value if available.
function updateCellValue(cellId, value, hValue){
	var _element = document.getElementById(cellId);
	if(_element){
		//Check if new value and current cell value is mismatched. 
		if(_element.innerHTML != value){
			//Means cell has some changes, apply css of edit cell.
			_element.className += ' editedCell';
		}
		_element.innerHTML = value;
	}
	//if there are some hidden value to assign
	if(hValue){
		var _hElement = document.getElementById('h'+cellId);
		if(_hElement){
			//Hidden element is also available, Assign it 
			_hElement.value = hValue;
		}
	}
}

//revert all changes done during editing.
function revert(){
	scheduleChanged = false;
	isScheduleSaved = false;
	isRecalculateComplete = true;
	hideButtons();
	//for quick add editor.
	if(document.getElementById('new_added_thd_name') && newRowPosition){
		document.getElementById('taskListTable').deleteRow(newRowPosition);
		newRowPosition = null;
		removeEditor();
	}else{
		reloadTaskList();
	}
}

//reload task list
function reloadTaskList(){
	var orderDescending = false;
	var sortingApplied = false;
	submitParameter('{"action":"reload"}');
}

/**Work paln editing**/
//sfc: Schedule Field Clicked
//[Field Definitions] w:work, tn:task name, wpc:work percent complete ,wc:work complete, d:duration, ed:end Date, sd:start date.
function sfc(field, scheduleEntryId, isSummary, isFromShare){
	//When any cell get clicked.
	
	//Return back if editor is activated for the same cell. 
	//There is no need to perform any action if clicked on the cell in which editor is already inserted.
	if(document.getElementById('input_'+scheduleEntryId)
		|| document.getElementById('edit_combo_position_'+ field +'_'+scheduleEntryId)
		|| document.getElementById('editDatePosition_'+field+'_'+scheduleEntryId)
		|| document.getElementById('new_added_thd_name')){
		return; //nothing to do.
	}
	
	//First remove the old editor before inserting new one. 
	removeEditor();

	if(isSummary){
	//Return back if task summary task, editng is currently not supported for this type of task.
		return;
	} else if(isFromShare){
	//Return back if task is shared from other space, editng is currently not supported for this type of task.
		extAlert(errorAlertTitle, canNotEditShareTask, Ext.MessageBox.ERROR);
		return;
	} else {
		//When any cell get clicked, First click should select that row and then on next click that cell become editable.
		//We select row on click of <TR>, and "wSelectedRow" css is get applied on that row.
		//When we click on any cell the <TD> onclick event fired before the <TR> onclick in all browser, This "sfc()" method get called before selecting row.
		//Here is the check if row is selected, insert editor. If not selected nothing to do return back.
		var rowObj = document.getElementById(scheduleEntryId);
		if( rowObj && rowObj.className.indexOf('wSelectedRow') >= 0){//If row is selected
			//initialize current cell information. will use later for revering cell value and removing this particualr editor.
			currentEntryID = scheduleEntryId;
			currentField = field;
			currentCellID = field + '_' + scheduleEntryId;
			currentCellObject = document.getElementById(currentCellID);
			//Initialize Current cell displayed value, which is to use for reverting cell value.
			displayedValue = currentCellObject.innerHTML;
			
			//Now insert editor in this cell. 
			insertEditor();
		}else{
			return;
		}
	}
}

/**
 * get cell value for specific field and entry Id.
 * Which is use to supply in editor as value.
**/
function getCellValue(){
	//Initialize displayedValue which is to use for reverting cell value.
	var value = document.getElementById(currentCellID).innerHTML;
	//Handling some special case
	if (currentField == 'sd' || currentField == 'ed' || currentField  == 'p' || currentField  == 'pr') {
		//for the Start Date(sd), End Date(ed), phase(p), priority(pr), We need to hidden field for editing purpose 
		// because for date we need value in simple short format eg m/d/y by seeing limitaion of EXT-JS calender. 
		//and same with the phase(p) and Priority(pr) we need code for maintaining dropdown and saving value.
		return document.getElementById('h'+ currentField + '_' + currentEntryID).value;
	} //else if (currentField  == 'wpc'){
		//For wrok percent complete (wpc) we need only value not % symbol for editing so remove % from the display value.
		//return	value.split('%')[0];
	//} 
	//and for all other editiable field the displayed value will same for editor.
	return value;
}

/**Insert aditor for edit field, as per field type
 *	if field type is date then to insert date field and if field type is text then insert simple text filed.
**/
function insertEditor(){
	//before the inserting editor remove applied padding from clicked table. This will help to avoid flicking
	if(currentField == 'tn'){
		//For the task name <TD> is different then orhe <TD> 
		//This is because of one span in this td which contain only name not the tree view related icon.
		document.getElementById('td_tn_td_'+currentEntryID).style.padding = '0px';
	}else{
		document.getElementById('td_'+currentCellID).style.padding = '0px';
	}
	
	if(currentField == 'sd' || currentField == 'ed'){
		//if field type is date(StartDate sd or EndDate ed) insert date field in clicked td.
		insertDateEditor();
	} else if(currentField == 'p' || currentField == 'pr'){
		//For the phase and priority we need drop down box
		insertComboEditor();
	} else{
		//otherwise insert text field in clicked td.
		insertInputEditor();
	}
	//Because we have added css for no selection when we use resizer  
	document.getElementById('taskListTable').className = '';
	document.getElementById('task-list').onselectstart = function(){return true;};
	document.getElementById('recalculateButton').disabled = false;
}

//Insert EXT-JS calendar as Date Editor.
function insertDateEditor(){
	//First replace cell with a uniqe div, and then render EXT-JS date field with this div.
	document.getElementById(currentCellID).innerHTML = '<div id="editDatePosition_'+currentCellID+'" style="height:21px;"/>';
	dateFieldEditor = new Ext.form.DateField({
		renderTo :'editDatePosition_'+currentCellID,
		hideLabel : true,
		format: getJSUserDatePattern(userDateFormatString),
		invalidText: dateFormatMessage + userDateFormatString,
		width : 80,
		height :15,
		value : getCellValue(),
		listeners:{'focus': function (thisObj){
			thisObj.addListener("specialkey", onSpecialkeyPress);
			thisObj.addListener("change", onDateChange);
		}}
	}).focus();
}

//on date change this mehtod will call
function onDateChange(thisObj, newValue, oldValue){
	if(newValue != '' && newValue.dateFormat(getJSUserDatePattern(userDateFormatString)) != oldValue.dateFormat(getJSUserDatePattern(userDateFormatString))){
		handleDateChange(newValue.dateFormat(getJSUserDatePattern(userDateFormatString)));
	}else{
		removeEditor();
	}
}

//on special key peress this mehtod will call
function onSpecialkeyPress(thisObj, e){
	if(e.getKey() == Ext.EventObject.ENTER){
		if(thisObj.getValue() != '' && thisObj.getValue().dateFormat(getJSUserDatePattern(userDateFormatString)) != getCellValue()){
			handleDateChange(thisObj.getValue().dateFormat(getJSUserDatePattern(userDateFormatString)));
		}else{
			removeEditor();
		}
	}
}

//Insert EXT-Combo box as Combo editor.
function insertComboEditor(){
	//First initialize combo box store. variable "phaseComboData" and "priorityComboData" is initialized in TML page.
	//Currently we have only to column which needs combo editor. 
	var _store = new Ext.data.SimpleStore({fields :	['code', 'desc'], data : currentField == 'p' ? phaseComboData : priorityComboData});
	//Inisilize value before rendering editor;
	var value = getCellValue();
	// render combo if store has any date.
	if(_store.getCount() > 0){
		//First replace cell with a uniqe div, and then render EXT-JS combo field with this div.
		document.getElementById(currentCellID).innerHTML = '<div id="edit_combo_position_'+currentCellID+'" style="height:21px;"/>';
		renderComboWithDiv("edit_combo_position_" + currentCellID, value, currentField, currentEntryID, _store, parseInt(getInputSize()))
	}
}

//Insert simple input editor.
function insertInputEditor(){
	//Inisilize value before rendering editor;
	var value = getCellValue();
	document.getElementById(currentCellID).innerHTML = 
		'<input class="inputCss" type="text" style="width: '
		+ getInputSize()+';" id="input_'+currentEntryID
		+'"  name="input_'+currentEntryID+'" maxlength="255"'
		+' onKeyDown="if(event.keyCode==13) performEditing(this);"'
		+' onBlur="performEditing(this);" />';
		//Focus this field. 
		document.getElementById('input_'+currentEntryID).focus();
		//set current value in the cell.
		document.getElementById('input_'+currentEntryID).value = replaceAll(value, '&amp;', '&');
}

//size of editor for rendering it in clicked cell.
//This method will return size of input as per visible column width.
function getInputSize(){
	if(currentField == "tn"){
		//In case of task name cell the TD id is different then other cell.
		return (parseInt(document.getElementById("tn_td_"+currentEntryID).offsetWidth)-27)+'px';
	}
	//Otherwsie for the all cell we can get width directly. 
	return (parseInt(document.getElementById(currentCellID).offsetWidth)-10)+'px';
}

//We need to remove editor after editing perform or any other cell is to edit. 
function removeEditor(value){
	if(!isEditingInProcess()){
		return;
	}
	//Since we set editor as innerHTML so we can remove it by simply reset value as cell innerHTML.
	//use displayedValue if no value is specified.
	document.getElementById(currentCellID).innerHTML = (value ? value : displayedValue);
	if(value)
		currentCellObject.className += ' editedCell';
	//Since we have made changes on cell padding for avoiding flicking, We need to again put those padding for making view as previous.
	applyCellStyles()
}

//If any changes made in cell style, reapply it.
function applyCellStyles(){
	//Element 'td_'+currentCellID is some time undefine in case of task name cell
	if(document.getElementById('td_'+currentCellID))
		document.getElementById('td_'+currentCellID).style.padding = '3px 3px 3px 5px';
	//For task name cell we have different ID pattern.
	document.getElementById('td_tn_td_'+currentEntryID).style.padding = '3px 3px 3px 5px';
	//Since we also removed css and event for selecting cell value while editing, We need to apply these again. 
    document.getElementById('taskListTable').className = 'disableSelection';
	document.getElementById('task-list').onselectstart = function(){return false;};
}

// Handles date change by submitting request to server.
function handleDateChange(newValue){
	//Some time the event get fired autometically even if no date field in screen and there is no changes, Might be EXT-JS bug.  
	//so remove all added listener from date field, to avoid unneccessary event firing.
	dateFieldEditor.removeListener("change", onDateChange);
	dateFieldEditor.removeListener("specialkey", onSpecialkeyPress);
		
	//IF cell value and new edited value is different save this.
	if(newValue != '' && newValue != getCellValue()){
		submitParameter( '{"makeWorkingDay":"false","newDateToChange":"'+newValue+'","taskId":"'+currentEntryID+'","action":"'+ (currentField == "sd" ? "startDateChanged" : "endDateChanged") +'"}');
	}else{
		//Othewise just remove this editor.
		removeEditor();
	}
}

//For all input editor one common mehtod to find handler and sumbmit changes in server side.
function performEditing(object){
	//First remove the events from input object.
	object.onkeydown = function(){return false;};
	object.onblur = function(){return false;};
	
	//If no change in new value and old value, Just remove editor and return nothing to do. 
	if(object.value == displayedValue){
		removeEditor();
		return;
	} 
	if(currentField == 'tn'){
		//If field is task name
		handleTaskNameEditing(object.value);
	} else if(currentField =='w' || currentField == 'd'){
		//If field is work
		handleTaskWorkAndDurationEditing(object.value);
	} else if(currentField == 'wc' || currentField == 'wpc'){
		//For work complete and work percent complete, we need to show one warning message. if task has any assignment.
		
		if(warningEnable && document.getElementById("has_assignment_"+currentEntryID)&& document.getElementById("has_assignment_"+currentEntryID).value=="true"){
			Ext.Msg.show({
			   title:workPlanWarningTitle,
			   msg: workCompleteEditingWarningMsg,
			   buttons: Ext.Msg.OKCANCEL,
			   fn: function(btn){
					if(btn=='ok'){
						//First Remove editor.
						removeEditor();
						//and open blogit window for work capture. 
						blogit();
						return;
					}else{
						(currentField == 'wc'? handleTaskWorkCompleteEditing(object.value): handleTaskWorkPercentCompleteEditing(object));
						removeEditor(object.value);
					}},
			   icon: Ext.MessageBox.WARNING
			});
		} else {
			(currentField == 'wc'? handleTaskWorkCompleteEditing(object.value): handleTaskWorkPercentCompleteEditing(object));
			removeEditor(object.value);
			
		}
	}
	
	if(currentField != 'wc' && currentField != 'wpc'){
		//Remove editor finally.
		removeEditor(object.value);
	}
}

//For seperating unit and number we need index of unit in input value.
function getUnitIndex(newValue){
	if(newValue.indexOf('h') > -1){
		//For hour unit
		return unitIndex = newValue.indexOf('h'); 
	} else if(newValue.indexOf('w') > -1){
		//For week unit
		return unitIndex = newValue.indexOf('w'); 
	} else if(newValue.indexOf('d') > -1){	
		//For day unit
		return unitIndex = newValue.indexOf('d'); 
	}
}

//on task  workpercent complete editing
//Handles it by submitting request to server.
function handleTaskWorkPercentCompleteEditing(object){
	//First check if value range is correct.
	if(checkRangeInt(object, 0, 100, percentCompleteInvalidMessage, percentCompleteInvalidMessage, decimalSeparator)) {
		submitParameter('{"newPercentAmount":"'+changeDecSeparator(object.value, decimalSeparator)+'","taskId":"'+currentEntryID+'","action":"workCompletePercentChanged"}');
	}	
}

//on task duration or work edited
//Handles it by submitting request to server.
function handleTaskWorkAndDurationEditing(newValue){
	//If new value is empty show alert and return back.
	if(newValue == ''){
		extAlert(errorAlertTitle, (currentField == 'd' ? durationRequiredMsg : workRequiredMsg) ,Ext.MessageBox.ERROR);
		return;
	}
	//Separate out value and unit, because new vaue is string of work/duration with unit.
	//First get unit index.
	var unitIndex = getUnitIndex(newValue);
	//initialize work/duration value from new value string by substring o to index of unit.
	var workValue = newValue.substring(0, unitIndex);
	//initialize unit from new value string by substring unit index to newValue length.
	var unit = newValue.substr(unitIndex, newValue.length);
	//Convert unit string to unit code.
	unit = getWorkUnit(unit.trim());
	//If no unit code found for string show alert and return.
	if(unit =='none'){
		extAlert(errorAlertTitle, (currentField == 'd' ? invalidDurationUnitMsg : invalidWorkUnitMsg), Ext.MessageBox.ERROR);
		removeEditor();
		return;
	}
	//if work value is not valid show alert and return.
	if(!isValidNumber(workValue)){
		extAlert(errorAlertTitle, (currentField == 'd' ? invalidDurationMsg : invalidWorkAmountMsg) ,Ext.MessageBox.ERROR);
		removeEditor();
		return;
	}
	//Every thing is OK now sumbmit parameter to server.
	if(currentField == 'd'){
	//if field is duration 
		submitParameter('{"newDuration":"'+workValue.trim()+'","durationUnit":"'+unit+'","taskId":"'+currentEntryID+'","action":"durationChanged"}');
	}else{
	//Else field is work
		submitParameter('{"newWork":"'+workValue.trim()+'","workUnit":"'+unit+'","taskId":"'+currentEntryID+'","action":"workChanged"}');
	}
}

//on task work complete edited
//Handles it by submitting request to server.
function handleTaskWorkCompleteEditing(newValue){
	if(newValue != ''){
		//Separate out value and unit, because new vaue is string of workcomplete with unit.
		var unitIndex = getUnitIndex(newValue);
		var workValue = newValue.substr(0, unitIndex);
		var unit = newValue.substr(unitIndex, newValue.length);
		unit = getWorkUnit(unit.trim());
		if(unit !='none'){
			if(isValidNumber(workValue)){
				//We need actual old value for validation
				var row = document.getElementById(currentEntryID);
				var originalWorkandUnit = row.cells[3].innerHTML;
				var origingalWork = '';
				var origingalWorkUnit = '';
				if(originalWorkandUnit.indexOf('&nbsp;') != -1){
					origingalWork = originalWorkandUnit.split('&nbsp;')[0];
					origingalWorkUnit = getWorkUnit(originalWorkandUnit.split('&nbsp;')[1].trim());
				} else {
					var originalWorkUnitIndex = getUnitIndex(originalWorkandUnit);
					origingalWorkUnit = originalWorkandUnit.substr(originalWorkUnitIndex, originalWorkandUnit.length);
					origingalWorkUnit = getWorkUnit(origingalWorkUnit.trim());
					var origingalWork = originalWorkandUnit.substr(0, originalWorkUnitIndex);
				}
				if(moreWorkComplete(origingalWork, origingalWorkUnit, workValue, unit)){
					submitParameter('{"newWorkComplete":"'+workValue+'","workCompleteUnit":"'+unit+'","taskId":"'+currentEntryID+'","action":"workCompleteChanged"}');
				}
			}else{
				extAlert(errorAlertTitle, invalidWorkCompleteAmountMsg, Ext.MessageBox.ERROR);
			}
		} else {
			extAlert(errorAlertTitle,invalidWorkCompleteUnitAmountMsg ,Ext.MessageBox.ERROR);
		}
	} else {
		extAlert(errorAlertTitle, workCompleteRequiredMsg, Ext.MessageBox.ERROR);
	}
}

//To check work complete is greater than work
function moreWorkComplete(workValue, workUnit, workPercent, workPercentUnit){
	var workCompleteValue = getWorkHours(parseFloat(workPercent),parseInt(workPercentUnit));
	var workValue = getWorkHours(parseFloat(workValue), parseInt(workUnit));
	if (workCompleteValue > workValue) {				
		errorHandler(document.getElementById(currentCellID), moreWorkPercentCompleteMessage);
        return false;
	} 
	return true;
}

//on task name edited
function handleTaskNameEditing(newValue){
 	if(newValue.trim() != ''){
 		if(!verifyNoHtmlContent(newValue.trim(), noHtmlContentsMessage)) {
 			removeEditor();
 			return;
 		}
 		//Just update cell immidiate and send request to server
 		//Since task name edit will not affect the schedule calculation so update it Asynchronously
	 	document.getElementById('tn_'+currentEntryID).innerHTML = newValue;
	 	document.getElementById('tn_'+currentEntryID).className += ' editedCell';
		var parameterString = ('{"taskName":"'+escapeQuotes(newValue)+'","taskId":"'+currentEntryID+'","action":"taskNameChanged"}');
		updateAsynchronously(parameterString, "tn", newValue, newValue, currentEntryID);
	}else {
		extAlert(errorAlertTitle, taskNameRequiredMessage , Ext.MessageBox.ERROR);
	}	
}

//finding user input unit.
function getWorkUnit(unit){
	var unitFound = 'none';
	switch(unit){
		//For hour(4) possible/supported unit string 
		case 'h' : unitFound = '4'; break;
		case 'hr' : unitFound = '4'; break;
		case 'hrs'  :unitFound = '4'; break;			  	 
		case 'hour' :unitFound = '4'; break;
		case 'hours':unitFound = '4'; break;
		//For day(4) possible/supported unit string
		case 'd':	unitFound = '8'; break;				  	 	  	 
		case 'day':	unitFound = '8'; break;
		case 'days':unitFound = '8'; break;
		//For week(4) possible/supported unit string
	    case 'w':	unitFound = '16'; break;	
	    case 'wk':	unitFound = '16'; break;				  	 	  	 
   		case 'wks':unitFound = '16';break;
		case 'week' :unitFound = '16';break;
		case 'weeks':unitFound = '16'; break;		
	}
	return unitFound;
}

//save scheule changes
function saveScheduleChanges(){
	isRecalculateComplete = true;
	addLightBox();
	hideButtons();
    submitParameter('{"action":"saveSchedule"}');
}

//load blog entries in blog tab for selected row
function b(obj, forId, forType) {
	//select row and set style of seleciton.
	selectRow(forId);
	
	//if selection is not changed return back.
	if(objectId == forId) return;
	objectId = forId;
	
	//set object type only if type is not null 
	if(forType != null) 
		objectType = forType;
	
	//Finally load corresponding information in right tab.
	initRightPanels();
}

//load selected row information in right tab.
function initRightPanels(){
	//retrun back if no any row selected.
	if(!selectedRows.length || selectedRows.length == 0)
		return ;
	
	childTaskList = selectedRows[selectedRows.length-1] + ","
	getChildRowId(selectedRows[selectedRows.length-1]);
	
	//load only active right tab.
	if(isActiveRightTab('blog-tab') && isBlogEnabled)
 		loadBlogsForSelectedRow(selectedRows[selectedRows.length-1], childTaskList, spaceId);
 	if(isActiveRightTab('wiki-tab'))
	 	loadWikiPageContents('taskWikiDivRight', 'wikiDivBody', 'wikiDivTop');
 	if(isActiveRightTab('detail-tab'))
 		loadDetails(selectedRows[selectedRows.length-1], document.getElementById('taskDetailsDivRight'));
}

// go to task properties page on double click
function p(forId) {
	if(!isEditingInProcess())
	self.document.location = JSPRootURL+"/servlet/ScheduleController/TaskView?module="+moduleId+"&action=1&id="+forId;
}

/*All about selection handling*/

//To set key code when pressed
function setKeyCode(event){
	if(!event){
		event = window.event;
	}
	if(event.keyCode == 16 || event.keyCode == 17){ 
		keyCode = event.keyCode;
	} else if(event.keyCode == 38){//To handle key code for Arrow Key
		selectTaskWithArrowkey('up');
	} else if(event.keyCode == 40){
		selectTaskWithArrowkey('down');
	}
}

//To reset key code when released
function resetKeyCode(event){
	if(!event){
		event = window.event;
	}
	keyCode = 0;
}

//Mange selection single and multiple selection
function selectRow(id){
	if(keyCode == 17){//if Ctrl key pressed
		selectedRows[selectedRows.length] = id;
	} else if(keyCode == 16){//if Shift key pressed
		var allRows = document.getElementById('taskListTable').tBodies[0].rows;
		var first = document.getElementById(selectedRows[0]).rowIndex;
		var last = document.getElementById(id).rowIndex;
		if(first > last){/*just swep it*/first = first + last; last = first - last;	first = first - last;}
		for(first; first <= last; first++){
			selectedRows[selectedRows.length] = allRows[first].id;
		}
	} else {//single selection.
		clearSelections();//clear all selection first.
		selectedRows[0] = id;
	}
	setRowSelected();
}

//Manage arrow key selections.
function selectTaskWithArrowkey(key){
	var tableRows = document.getElementById('taskListTable').tBodies[0].rows;
	var index;
	if(key == "up"){
		var rowSelected = document.getElementById(getSelection(theForm)).rowIndex;
		index = rowSelected - 1 <= 1 ? 1 : rowSelected - 1;
	} else{
		var rowSelected = document.getElementById(getSelectionFromEnd(theForm)).rowIndex;
		index = rowSelected + 1 >= tableRows.length ?  rowSelected : rowSelected + 1;
	}
	if(tableRows[index].id)
		b(tableRows[index], tableRows[index].id, null);//select it and load right pane.
}

//set row as selected, check checkboxes as well as set selection style "wSelectedRow".
function setRowSelected(){
	for(i=0; i < selectedRows.length; i++){
		forTr = document.getElementById(selectedRows[i]);
		if(document.getElementById('chk_'+selectedRows[i]))
			document.getElementById('chk_'+selectedRows[i]).checked = true;
		if(forTr && forTr.className.indexOf('wSelectedRow')== -1){//If row is not selected
			forTr.className += ' wSelectedRow';
		}
	}
}

// clear selections, Uncheck checked checkboxes as well remove slection style "wSelectedRow".
function clearSelections(){
	for(i=0; i < selectedRows.length; i++){
		forTr = document.getElementById(selectedRows[i]);
		if(document.getElementById('chk_'+selectedRows[i]))
			document.getElementById('chk_'+selectedRows[i]).checked = false;
		if(forTr && forTr.className.indexOf('wSelectedRow') >= 0){//If row is selected
			forTr.className = forTr.className.replace('wSelectedRow', '');
		}
	}
	selectedRows = new Array();//clear selected row array
}

//Filter related code
function applyFilter(){
	if(checkFilterDate()){
		if(!isdateStartBeforeEnd(getDateValue(filterForm.startDateFilterStart.value),getDateValue(filterForm.startDateFilterEnd.value)) && !isdateStartBeforeEnd(getDateValue(filterForm.endDateFilterStart.value),getDateValue(filterForm.endDateFilterEnd.value))){
			var parameterString =  '{"action":"applyFilter';
				parameterString += '","showAllTasks":"'+ filterForm.showAllTasks.checked;
				parameterString += '","showLateTasks":"'+ filterForm.showLateTasks.checked;
				parameterString += '","showComingDue":"'+filterForm.showComingDue.checked;
				parameterString += '","showAssignedToUser":"'+ filterForm.showAssignedToUser.checked;
				parameterString += '","showOnCriticalPath":"'+  filterForm.showOnCriticalPath.checked;
				parameterString += '","showUnassigned":"'+ filterForm.showUnassigned.checked;
				parameterString += '","showShouldHaveStarted":"'+ filterForm.showShouldHaveStarted.checked;
				parameterString += '","showStartedAfterPlannedStart":"'+ filterForm.showStartedAfterPlannedStart.checked;
				parameterString += '","startDateFilterStart":"'+ getDateValue(filterForm.startDateFilterStart.value);
				parameterString += '","startDateFilterEnd":"'+ getDateValue(filterForm.startDateFilterEnd.value);
				parameterString += '","endDateFilterStart":"'+ getDateValue(filterForm.endDateFilterStart.value);
				parameterString += '","endDateFilterEnd":"'+ getDateValue(filterForm.endDateFilterEnd.value);
				parameterString += '","selectedPhaseID":"'+ filterForm.selectedPhaseId.value;
				parameterString += '","taskName":"'+ filterForm.taskName.value;
				parameterString += '","taskNameComparator":"'+ filterForm.taskNameComparator.value;
				parameterString += '","workPercentComplete":"'+filterForm.workPercentComplete.value;
				parameterString += '","workPercentCompleteComparator":"'+ filterForm.workPercentCompleteComparator.value;
				parameterString += '","type":"'+ filterForm.taskType.value;
				parameterString += '","assignedUser":"'+ filterForm.assignedUser.value + '"}';
			submitParameter(parameterString);
		}else{
			extAlert(errorAlertTitle, invalidFilterDateRangeMessage , Ext.MessageBox.ERROR);
		}
	}
}

function clearFilter(){
	filterForm.showAllTasks.checked = true;
	filterForm.showLateTasks.checked = false;
	filterForm.showComingDue.checked = false;
	filterForm.showAssignedToUser.checked = false;
	document.getElementById("user-list").style.display = 'none';
	filterForm.showOnCriticalPath.checked = false;
	filterForm.showUnassigned.checked = false;
	filterForm.showShouldHaveStarted.checked = false;
	filterForm.showStartedAfterPlannedStart.checked = false;
	filterForm.startDateFilterStart.value = userDateFormatString;
	filterForm.startDateFilterEnd.value = userDateFormatString;
	filterForm.endDateFilterStart.value = userDateFormatString;
	filterForm.endDateFilterEnd.value = userDateFormatString;
	filterForm.selectedPhaseId.value = '';
	filterForm.taskName.value = '';
	filterForm.taskNameComparator.value = 'contains';
	filterForm.workPercentComplete.value = '';
	filterForm.workPercentCompleteComparator.value = 'equals';
	filterForm.taskType.value ='all';
	isEDFrom = isEDTo = isSDFrom = isSDTo = false;
	filterForm.startDateFilterStart.style.color = '#A1A1A1';
	filterForm.startDateFilterEnd.style.color = '#A1A1A1';
	filterForm.endDateFilterStart.style.color = '#A1A1A1';
	filterForm.endDateFilterEnd.style.color = '#A1A1A1';
}

//sort the column.
function sort(column){
	//Confirm the schedue changes will revert.
	if(scheduleChanged && !confirm(modifiedMessage)){
		return;
	}
	isRecalculateComplete = true;
	hideButtons();
	orderDescending = !orderDescending;
	sortedColumn = column;
	sortingApplied = true;
	document.getElementById('sort-img-'+sortedColumn).width = "12";
	document.getElementById('sort-img-'+sortedColumn).height = "12";
	document.getElementById('sort-img-'+sortedColumn).src = JSPRootURL + '/images/default/tree/loading-small.gif';
	document.getElementById('sort-img-'+sortedColumn).style.visibility = 'visible';
	submitParameter('{"action":"sortChanged","columnIndex":"'+ column +'"}');
}

function initSortingImage(){
	if(sortingApplied){
		var imageSrc;
		if(orderDescending){
			imageSrc = JSPRootURL + '/images/default/grid/sort_desc.gif';
		}else{
			imageSrc = JSPRootURL + '/images/default/grid/sort_asc.gif';
		}
		document.getElementById('sort-img-'+sortedColumn).width = "12";
		document.getElementById('sort-img-'+sortedColumn).height = "5";
		document.getElementById('sort-img-'+sortedColumn).src = imageSrc;
		document.getElementById('sort-img-'+sortedColumn).style.visibility = 'visible';
		sortingApplied = false;
	}
}

function verifyAction(){
	if(theForm.theAction.value == "");
		return false;
}

function replaceAll(text, strA, strB) {
    while ( text.indexOf(strA) != -1) {
        text = text.replace(strA,strB);
    }
    return text;
}

//To check if any changes in task list. shows confirmation message.
function beforeUnload(e){
	saveTasklistScrollPostion();
	if(scheduleChanged){
		return modifiedMessage;
	}
};

function loadBlogsForSelectedRow(forObjectId, childObjectsList, spaceId, offset){
	childObjectsList = childObjectsList + "," + getSelection(theForm, ',');
	childObjectsList = childObjectsList == "" ? forObjectId : childObjectsList;
	document.getElementById('blogDivTop').innerHTML = loadingMessage + '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	Ext.Ajax.request({
		url: JSPRootURL +'/blog/view/loadBlogEntries?module='+moduleId,
		params: {module : 60, forObjectId : forObjectId, childObjectsList : childObjectsList, workSpaceId : spaceId, offset: offset},
		method: 'POST',
		success: function(result, request){
			// eval javascript code
			var scriptTag = '<script type="text/javascript">';
			var javaScriptCode = result.responseText;
			javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
		    
			eval(javaScriptCode.replace(/\n/g, ''));
			// Set response and blog count message 
			document.getElementById('blogDivBody').innerHTML = result.responseText;
			document.getElementById('blogDivTop').innerHTML = document.getElementById('blogCountMessage').innerHTML;
			document.getElementById('blogCountMessage').style.display = 'none';
			if(document.getElementById('blogCountMessage').innerHTML == '')	document.getElementById('blogCountMessage').innerHTML = blogNotFoundMsg;
			if(document.getElementById('blogCountMessage').innerHTML == 'Blog entries not found'){
				if((document.getElementById('tn_'+forObjectId).innerHTML).substring(0,1) == '<'){
					document.getElementById('blogDivTop').innerHTML = msgFormat.format(blogNotFoundForTaskMsg, displayedValue);
				} else {
					document.getElementById('blogDivTop').innerHTML = msgFormat.format(blogNotFoundForTaskMsg, document.getElementById('tn_'+forObjectId).innerHTML);
				}
			}
			resetPanelsWidth();
			applyRightTabBlogPaging('project');
		},
		failure: function(result, response){
		}
	});		   
}

//saving scroll position in Cookie
function saveTasklistScrollPostion(){
	var windowScrollbarY = typeof window.pageYOffset != 'undefined' ? window.pageYOffset : document.documentElement.scrollTop;
	Set_Cookie( _tScrollPosName, document.getElementById('task-list').scrollTop + "!~" + windowScrollbarY, '', JSPRootURL+'/', '', '' );
}

//setting scroll position from Cookie if it is saved.
function setTaskListScrollPostion(){
	var _scroll = Get_Cookie(_tScrollPosName);
	if(_scroll !=null && _scroll.indexOf("!~") > 0){
		document.getElementById('task-list').scrollTop = _scroll.substring(0, _scroll.indexOf("!~"));
		window.scrollTo(0, _scroll.substring(_scroll.indexOf("!~")+2, _scroll.length));
	}
}
   
function activate_tab(clickedPanel){
	removeActionMessageDiv();
	if(clickedPanel == 'panel_Schedule'){
		document.getElementById('Schedule').style.display = '';
		document.getElementById('Gantt View').style.display = 'none';
		
		document.getElementById('panel_Schedule').className = 'tap5c_tab-set-panel activated disableSelection';
		document.getElementById('panel_Gantt View').className = 'tap5c_tab-set-panel deactivated disableSelection';
		
		if(document.getElementById('blogsCollapsed').style.display != ''){
			document.getElementById('rightTabSet').style.display = '';
		}
		document.getElementById('column_settings_link').style.visibility = 'visible';
		document.getElementById('collaspe_all_link').style.visibility = 'visible';
		document.getElementById('epandall_all_link').style.visibility = 'visible';
		document.getElementById('quick_add_link').style.visibility = 'visible';
		document.getElementById('schedule_info_div').style.width = parseInt(document.getElementById('task-list').style.width) + 'px';
		disableScheduleActions(false);
		if(scheduleTabRight && document.getElementById('slidingpanel_content').style.display == 'none'){ //open right panel if it is closed previously.
			openRightPanel();
			scheduleTabRight = false;
		}

	}else{
		//first close right panel, if it is opened
		scheduleTabRight = false;
		if(document.getElementById('blogsCollapsed').style.display == 'none'){
			closeRightPanel();
			scheduleTabRight = true;
		}
		tabCounter++;
		if(scheduleAltered || tabCounter == 1){
			document.getElementById('Gantt View').innerHTML ='<iframe id="ganttChartPanel" frameborder="0" width="100%" height="100%" scrolling="auto" src="'+JSPRootURL+'/schedule/gantt/Gantt.jsp?module=160&amp;action=1&amp"'+new Date()+'></iframe>';
			scheduleAltered = false;
		}
		document.getElementById('Gantt View').style.display = '';
		document.getElementById('Schedule').style.display = 'none';

		document.getElementById('panel_Gantt View').className = 'tap5c_tab-set-panel activated disableSelection';
		document.getElementById('panel_Schedule').className = 'tap5c_tab-set-panel deactivated disableSelection';

		document.getElementById('column_settings_link').style.visibility = 'hidden';
		document.getElementById('collaspe_all_link').style.visibility = 'hidden';
		document.getElementById('epandall_all_link').style.visibility = 'hidden';
		document.getElementById('quick_add_link').style.visibility = 'hidden';
		document.getElementById('slidingpanel-toolbar').style.width = (windowWidth - 227) + 'px';
		document.getElementById('schedule_info_div').style.width = (windowWidth - 232) + 'px';
		disableScheduleActions(true);
	}
	activeTab = clickedPanel;
	resizePanel();
}

//To disable links when gantt chart tab is opened
function disableScheduleActions(disable){
	if (disable) {
	for (var iterator =0; iterator < scheduleLinks.length; iterator++) {
			if (functionNameString.indexOf(scheduleLinks[iterator].href) != -1) {
					linksToEnable += iterator+',';
					scheduleLinks[iterator].href = "#";
					scheduleLinks[iterator].className = 'disabled';
			}
		}
	} else {
		linksToEnable = linksToEnable.trim().split(',');
		for (var index = 0; index < linksToEnable.length; index++) {
			if (linksToEnable[index]!= '') {
				scheduleLinks[linksToEnable[index]].href = functionNameString[index];
				scheduleLinks[linksToEnable[index]].className = 'enabled';
			}
		}
		linksToEnable = '';
	}
}

// Get the value in hours
function getWorkHours(workValue, workUnits) {
	if (workUnits == 4) {
		return workValue;
	}else if (workUnits == 8) {
		return workValue * 8;
	}else if (workUnits == 16) {
		return workValue * 40;
	}else { return workValue}
}

function openSlidingPanel(){
	document.getElementById('slidingpanel_content').style.display = '';
	document.getElementById('slidingpanel_toggler').className = 'tap5c_slidingPanelSubject-toggler-collapse';
	resetPanelsHeight();
}

function closeSlidingPanel(){
	document.getElementById('slidingpanel_content').style.display = 'none';
	document.getElementById('slidingpanel_toggler').className = 'tap5c_slidingPanelSubject-toggler';
	resetPanelsHeight();
}

function toggleSlidingPanel(){
	if(activeTab == 'panel_Schedule'){
		//first close right panel, if it is open
		if(document.getElementById('blogsCollapsed').style.display == 'none'){
			closeRightPanel();
			rightPaneClosed = true;
		}else if((rightPaneClosed || scheduleTabRight) && document.getElementById('slidingpanel_content').style.display != 'none'){ //open right panel if it is closed previously.
			openRightPanel();
			rightPaneClosed = false;
			scheduleTabRight = false;
		}
	}
	if(document.getElementById('slidingpanel_content').style.display == 'none'){
		openSlidingPanel();
	}else{
		closeSlidingPanel();
	}
	resetPanelsWidth();
}

function toggleRightPanel(isOpen){
	//First close filter slider. if it is opened
	if(document.getElementById('slidingpanel_content').style.display != 'none'){
		closeSlidingPanel();
		slidingPaneClosed = true;
	} else if (slidingPaneClosed && isOpen){//open slide panel if it is closed previously.
		openSlidingPanel()
		slidingPaneClosed = false;
	} else{
		slidingPaneClosed = false;
	}
	
	if(isOpen){
		closeRightPanel();
	} else {
		openRightPanel();
	}
	resetPanelsHeight();
}

function openRightPanel(){
	collapseDiv(false, 'taskList', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '80%', '20%', false);
	document.getElementById('rightTabSet').style.display = '';
	resetPanelsWidth();
	notifyToggleTree('righttabsetexpanded', true);
}

function closeRightPanel(){
	collapseDiv(true, 'taskList', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '97%', '1%', false);
	document.getElementById('rightTabSet').style.display = 'none';
	resetPanelsWidth();
	notifyToggleTree('righttabsetexpanded', false);
}

function displayButtons(btn){
	if(document.getElementById('button-bar').style.display == 'none'){
		document.getElementById('task-list').style.height = (parseInt(document.getElementById("task-list").style.height) - 30) + 'px';
	}
	if(btn && btn == "quickButton"){
		document.getElementById("quickButton").style.display = '';
		document.getElementById("saveButton").style.display = 'none';
		document.getElementById("recalculateButton").style.display = 'none';
		document.getElementById('new_added_thd_name').focus();
	}else{
		document.getElementById("quickButton").style.display = 'none';
		document.getElementById("saveButton").style.display = '';
		document.getElementById("recalculateButton").style.display = '';
	}
	document.getElementById('button-bar').style.display = '';
	
}

function hideButtons(){
	if(isRecalculateComplete) {
		if(document.getElementById('button-bar').style.display == ''){
			document.getElementById('task-list').style.height = (parseInt(document.getElementById("task-list").style.height) + 30) + 'px';
		}
		document.getElementById('button-bar').style.display = 'none';
	}
}

function updateScheduleStatusBBar(scheduleStatusInfo){
	if(scheduleStatusInfo){
		document.getElementById('startDateStatus').innerHTML = scheduleStatusInfo.startDate;
		document.getElementById('endDateStatus').innerHTML = scheduleStatusInfo.endDate;
		document.getElementById('taskCountStatus').innerHTML = scheduleStatusInfo.taskCount;
		document.getElementById('workStatus').innerHTML = scheduleStatusInfo.work;
	}
}

function activate_rightTab(clickedTab) {
	//return if tab is already active.
	if(isActiveRightTab(clickedTab)) return;
	
	document.getElementById('wikiMenu').style.display = 'none';
	document.getElementById('blogDivTop').style.display = 'none';
	document.getElementById('taskBlogDivRight').style.display = 'none';
	document.getElementById('taskWikiDivRight').style.display = 'none';
	document.getElementById('taskDetailsDivRight').style.display = 'none';
	document.getElementById('blog-tab').className = 'right-tab deactive-right-tab';
	document.getElementById('wiki-tab').className = 'right-tab deactive-right-tab';
	document.getElementById('detail-tab').className = 'right-tab deactive-right-tab';
		
	if(clickedTab == 'blog-tab') {
		// Activating right blog tab here
		document.getElementById('taskBlogDivRight').style.display = '';
		document.getElementById('blogDivTop').style.display = '';
		document.getElementById('blog-tab').className = document.getElementById('blog-tab').className.replace('deactive-right-tab', 'active-right-tab');
	} else if(clickedTab == 'wiki-tab'){
	 	// Activating right wiki tab here
		document.getElementById('taskWikiDivRight').style.display = '';
	 	document.getElementById('wikiMenu').style.display = '';
	 	document.getElementById('wiki-tab').className = document.getElementById('wiki-tab').className.replace('deactive-right-tab', 'active-right-tab');
	} else{
		// Activating right details tab here
		document.getElementById('taskDetailsDivRight').style.display = '';
	 	document.getElementById('detail-tab').className = document.getElementById('detail-tab').className.replace('deactive-right-tab', 'active-right-tab');
	}
	
	//refresh panel if this tab it is  re-activated.
	//panel will not refresh if old objectid and requested id is same. 
	var id = objectId;
	objectId = null;
	b(null, id, null);
	
	//persist tab settings.
	notifyToggleTree('rightTabSet', clickedTab);
}

function loadWikiPageContents(wikiTabContentDiv, wikiDivBody, wikiDivTop) {
	if(document.getElementById("tn_"+getSelection(theForm)) != null) {
	 	document.getElementById(wikiDivTop).innerHTML = loadingMessage +'<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	 	getWikiPageForSelectedTask(document.getElementById("tn_"+getSelection(theForm)).innerHTML, getSelection(theForm), spaceId, objectType, true, wikiTabContentDiv, wikiDivBody, wikiDivTop);
	}
}

function changeStartDateText() {
	if(isSDFrom && document.getElementById('startDateFilterStart').value == ''){
		document.getElementById('startDateFilterStart').style.color = '#A1A1A1';
		document.getElementById('startDateFilterStart').value = userDateFormatString;
		isSDFrom = false;
	}
	if(isSDTo && document.getElementById('startDateFilterEnd').value == ''){
		document.getElementById('startDateFilterEnd').style.color = '#A1A1A1';
		document.getElementById('startDateFilterEnd').value = userDateFormatString;
		isSDTo = false;
	}
}

function setStartDateFrom() {
	if(!isSDFrom){
		isSDFrom = true;
		if (document.getElementById('startDateFilterStart').value.trim() == userDateFormatString) {
			document.getElementById('startDateFilterStart').value = '';
		}
		document.getElementById('startDateFilterStart').style.color = 'Black';
	}
}

function setStartDateTo() {
	if(!isSDTo){
		isSDTo = true;
		if (document.getElementById('startDateFilterEnd').value.trim() == userDateFormatString) {
			document.getElementById('startDateFilterEnd').value = '';
		}
		document.getElementById('startDateFilterEnd').style.color = 'Black';
	}
}

function changeEndDateText() {
	if(isEDFrom && document.getElementById('endDateFilterStart').value == ''){
		document.getElementById('endDateFilterStart').style.color = 'Gray';
		document.getElementById('endDateFilterStart').value = userDateFormatString;
		isEDFrom = false;
	}
	if(isEDTo && document.getElementById('endDateFilterEnd').value == ''){
		document.getElementById('endDateFilterEnd').style.color = 'Gray';
		document.getElementById('endDateFilterEnd').value = userDateFormatString;
		isEDTo = false;
	}
}
function setEndDateFrom() {
	if(!isEDFrom) {
		isEDFrom = true;
		if (document.getElementById('endDateFilterStart').value.trim() == userDateFormatString) {
			document.getElementById('endDateFilterStart').value = '';
		}
		document.getElementById('endDateFilterStart').style.color = 'Black';
	}
}

function setEndDateTo() {
	if(!isEDTo) {
		isEDTo = true;
		if (document.getElementById('endDateFilterEnd').value.trim() == userDateFormatString) {
			document.getElementById('endDateFilterEnd').value = '';
		}
		document.getElementById('endDateFilterEnd').style.color = 'Black';
	}
}

function getDateValue(dateValue) {
	if(dateValue.trim() != '' && dateValue != userDateFormatString) {
		return dateValue;
	} else {
		return dateValue = '';
	}
}

/** 
Insert a blank row in existing task list.
If any row is selected then insert new blank row just after the selected row otherwise insert at the last of tasklist.
*/
function quick_add(){
	if(document.getElementById('new_added_thd_name') ){
		if(document.getElementById('new_added_thd_name').value != '' || document.getElementById('new_added_thd_work').value != '' ||
		(!isAutoEndPointCalculateSchedule && (newTask_sDate.getValue() != '' || newTask_fDate.getValue() != ''))){
			extAlert(errorAlertTitle, quickAddErrorMessage, Ext.MessageBox.ERROR);
			return;
		}else{
			revert();
		}
	}
	if(scheduleChanged){
		extAlert(errorAlertTitle, quickAddErrorMessage, Ext.MessageBox.ERROR);
		return;
	}
	if(!document.getElementById('thd_work')){
		extAlert(errorAlertTitle, workColumnMustVisibleMessage, Ext.MessageBox.ERROR);
		return;
	}
	if (!verifySelectionForField(theForm.selected, 'oneorzero', oneOrZeroMessage)) {
		return false;
	}
	var htbl = document.getElementById('taskListTable-header');
	var tbl = document.getElementById('taskListTable');
	if(newRowPosition == null){
		newRowPosition = tbl.rows.length;
		if(!isNaN(getSelection(theForm))){
			for(var i=0; i<tbl.rows.length;i++){
				if(tbl.rows[i].id == getSelection(theForm)){
					newRowPosition = i+1;
					break;
				}
			}
		}
	}
	
	//add new row at calulated postion of table
  	var row = tbl.insertRow(newRowPosition);
  	//row.id = "new_added_task_row";
  	var _cell = htbl.rows[0].cells;
  	for(var i=0; i<_cell.length; i++){
  		var nextCell = row.insertCell(i);
  		//if cell is for column name and work render an simple text editor.
  		if(_cell[i].id == "thd_name" || _cell[i].id == "thd_work"){
  			var _div = document.createElement('div');
  			_div.className = "div_"+_cell[i].id;
			var el = document.createElement('input');
			el.type = 'text';
			el.id = 'new_added_'+ _cell[i].id;
			if(_cell[i].id == "thd_work") el.value = "8 hrs";
			el.style.width = (parseInt(_cell[i].offsetWidth) - 25)+'px';
			_div.appendChild(el);
			nextCell.appendChild(_div);
  		}
  		//if schedule auto end point calculation is false then insert editor for start and end date field with extjs calender.
  		else if(!isAutoEndPointCalculateSchedule && (_cell[i].id == "thd_startDate" || _cell[i].id == "thd_endDate")){
  			var _div = document.createElement('div');
  			_div.id = 'div_id_'+_cell[i].id;
  			_div.style.height = '21px';
  			_div.className = "div_"+_cell[i].id;
  			nextCell.appendChild(_div);
  			if(_cell[i].id == "thd_startDate")
  				newTask_sDate = renderCalendarWithDiv(_div.id);
  			else
	  			newTask_fDate = renderCalendarWithDiv(_div.id);
  		}else{ // otherwise keep the cell blank without editor.
			var textNode = document.createTextNode("");
			nextCell.width = _cell[i].width;
			nextCell.appendChild(textNode);	
		}
  	}
  	document.getElementById('taskListTable').className = '';
	document.getElementById('task-list').onselectstart = function(){return true;};
  	scheduleChanged = true;
  	displayButtons("quickButton");
  	if(parseInt(getElementAbsolutePos(document.getElementById('new_added_thd_name')).y) < 185){
  		document.getElementById('task-list').scrollTop = parseInt(document.getElementById('task-list').scrollTop) - 50;
  	}
}
//save new added task.
function saveNewTask(){
	var nameField = document.getElementById('new_added_thd_name');
	var workField = document.getElementById('new_added_thd_work');
	
	if(nameField.value.trim() == ''){
		extAlert(errorAlertTitle, taskNameRequiredMessage, Ext.MessageBox.ERROR);
		nameField.focus();
		return;
	}
	if(workField.value.trim() == ''){
		extAlert(errorAlertTitle, workRequiredMsg, Ext.MessageBox.ERROR);
		workField.focus();
		return;
	}
	if(nameField.value.trim() != '' && !verifyNoHtmlContent(nameField.value.trim(), noHtmlContentsMessage)){
		nameField.focus();
		return;
	}
	
	var work = workField.value;
	var unitIndex = getUnitIndex(work);
	var workValue = work.substr(0, unitIndex);
	var unit = work.substr(unitIndex, work.length);
	unit = getWorkUnit(unit.trim());
	if(unit =='none'){
		extAlert(errorAlertTitle, invalidWorkUnitMsg,Ext.MessageBox.ERROR);
		workField.focus();
		return;
	}
	if(!isValidNumber(workValue)){
		extAlert(errorAlertTitle, invalidWorkAmountMsg, Ext.MessageBox.ERROR);
		workField.focus();
		return;
	}
	
	var startDateString = "";
	var finishDateString = "";
	if(!isAutoEndPointCalculateSchedule){
		if(newTask_sDate && newTask_sDate.getValue() != ''){
			date = newTask_sDate.getValue();
			startDateString = date.dateFormat(getJSUserDatePattern(userDateFormatString));
		}
		if(newTask_fDate && newTask_fDate.getValue() != ''){
			date = newTask_fDate.getValue();
			finishDateString = date.dateFormat(getJSUserDatePattern(userDateFormatString));
		}
	}
	var taskName = escapeQuotes(nameField.value);
	quickAddString = '{"action":"quickAdd","taskName" : "'+ taskName
					+ '","taskDesc" : "","work" : "'+ workValue
					+ '","workMeasure" : "'+ unit
					+ '","startDateString" : "'+ startDateString
					+ '","finishDateString" : "'+ finishDateString
					+ '","selected" : "'+ getSelections(theForm, ',') +'"}';
	scheduleChanged = false;			
	submitParameter(quickAddString);
	hideButtons();
}

//Render a ext js calendar with specifted div
function renderCalendarWithDiv(renderTo){
	return new Ext.form.DateField({
			renderTo : renderTo,
			hideLabel : true,
			format: getJSUserDatePattern(userDateFormatString),
			invalidText: dateFormatMessage + userDateFormatString,
			width : 70,
			height :15
		});
}

// Clear all right panels 
function clearRightPanels(){
		document.getElementById('blogDivBody').innerHTML = '';
		document.getElementById('blogDivTop').innerHTML = selectTaskForBlogEntryMsg;
		document.getElementById('wikiDivTop').innerHTML = selectTaskForWikiMsg;
		document.getElementById('taskDetailsDivRight').innerHTML = '<div class="two-pane-right-top">'+ selectTaskForDetailMsg+'</div>';
}

// To get formatted date
function checkFilterDate(){
	if(checkDateEntered(getDateValue(filterForm.startDateFilterStart.value))&& checkDateEntered(getDateValue(filterForm.startDateFilterEnd.value)) && checkDateEntered(getDateValue(filterForm.endDateFilterStart.value)) && checkDateEntered(getDateValue(filterForm.endDateFilterEnd.value))){
		return true;
	} 
	extAlert(errorAlertTitle, invalidFilterDateMessage , Ext.MessageBox.ERROR);
	return false
}

// To check date is black or valid date
function checkDateEntered(dateValue){
	if(dateValue.trim() == ''){
		return true;
	}else{
		return checkDate(dateValue, userDateFormatString);
	}
}

function renderComboWithDiv(renderTo, value, field, scheduleEntryId, _store, size){
	return new Ext.form.ComboBox({
		store : _store,
		renderTo : renderTo,
		value : value=="null"? "" : value,
		hideLabel : true,
		listClass: 'x-combo-list-small',
		valueField :'code',
		displayField : 'desc',
		editable : false,
		typeAhead : true,
		mode : 'local',
		triggerAction : 'all',
		selectOnFocus : true,
		height : 15,
		width : size,
		listeners:{'select' : function (thisCombo, record, index){
			var parameterString = '{"newValue":"'+thisCombo.getValue() +'","taskId":"'+scheduleEntryId+'","action":"'+(field == "p" ? "phaseChanged" : "priorityChanged" )+'"}';
			document.getElementById('h'+ field+ '_' + scheduleEntryId).value = thisCombo.getValue();
			updateAsynchronously(parameterString, field, thisCombo.getValue(), _store.getAt(index).get("desc"), scheduleEntryId);
		}}
	});
}

function updateAsynchronously(parameterString, field, value, displayValue, scheduleEntryId){
	displayedValue = value == ""? "&nbsp;" : replaceAll(displayValue, '&amp;', '&');
	removeEditor();
	if(field != 'p'){
		scheduleChanged = true;
		if(document.getElementById(field+'_'+scheduleEntryId))
			document.getElementById(field+'_'+scheduleEntryId).className += ' editedCell';
		displayButtons();
	}
	if(field == 'p') showLoadingDiv(phaseSavingMessage);
	
	Ext.Ajax.request({
		url: JSPRootURL + '/workplan/taskview/performaction',
		params: {parameterString : parameterString },
		method: 'POST',
		timeout: 180000,
		success: function(result, request){
			removeLoadingDiv();
		}
	});
}

function isValidNumber(value){
	var _number = value.trim();
	if(decimalSeparator != ".")
		_number = replaceAll(value, decimalSeparator, ".");
	_number = replaceAll(_number, ",", "");
	return (value.trim() != '' && !isNaN(_number));
}

//Hilite last selections,  get called form setup.
function hiLiteLastSelected(){
	if(lastSelectedIds != ""){
		setSelections(theForm, lastSelectedIds);
	}

	var selections = getSelections(theForm, ",");
	if(selections != ""){
		clearSelections();
		selectedRows = selections.split(",");
		//To manage a blank space at last position of array.
		//if last character is comma in selections string, it will return last id as blank
		//so avoid this condition by replacing blank by previous id.
		//In this situation the length of selectedRows must be greater than 1.
		if(selectedRows.length && selectedRows.length > 1 && selectedRows[selectedRows.length-1] == ""){
			selectedRows[selectedRows.length-1] = selectedRows[selectedRows.length-2];
			//To set the object type of the selected task
			var lastRowSelected = document.getElementById(selectedRows[1]);
			objectType = lastRowSelected.getAttribute("entryType");
		}
		setRowSelected();
		initRightPanels();
	}else{
		clearRightPanels();
	}
}

// Function for escaping single and double quotes in JSON String
function escapeQuotes(strToReplace) {
	strToReplace = strToReplace.replace(/"/g, '\\"');
	strToReplace = strToReplace.replace(/'/g, '\\\'');
	return strToReplace;
}

// Function to recalculate current schedule with modified entries
function recalculateModified(){
	document.getElementById('recalculateButton').disabled = true;
	isRecalculateComplete = true;
	scheduleChanged = false;
	isScheduleSaved = false;
	submitParameter('{"action":"recalculateChanged"}');
}

// Function for adding light box effect while editing or submit the changes
function addLightBox(){
	blogPopupScreen.style.opacity = 0;
	blogPopupScreen.style.filter = 'alpha(opacity:0)';
	document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);
}

//show blog while navigating with paging
function showBlogAfterPaging(){
	loadBlogsForSelectedRow(selectedRows[selectedRows.length-1], childTaskList, spaceId, offset);
}

// Check is edting in process or any aditor are apear in task list 
function isEditingInProcess(){
	return (document.getElementById('input_'+currentEntryID)
		|| document.getElementById('edit_combo_position_'+ currentCellID)
		|| document.getElementById('editDatePosition_'+currentCellID));
}

//On scroll start remove all editor from task panel because in IE calendar fields not scroll with the row.
function onScrollStart(){
	if(currentCellObject){
		currentCellObject.innerHTML = displayedValue;
		currentCellObject.style.padding = '3px 3px 3px 5px';
	}
}

function initializeWarningWindow(info, taskData){
	document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);
			
	var selected_option = 0;
	var _form = new Ext.form.FormPanel({
	  width: 400,
      height:100,	
	  items : [
	  	{
	  		xtype: 'label', 
	  		style: 'font-size:12px;',
		  	text:msgFormat.format(nonWorkingDayWarningMsg, info.name, info.dateType, info.currentDate)
		},
	  	{
	      xtype: 'radio',
	      name: 'selected_option',
	      boxLabel: msgFormat.format(nextWorkingDayMsg, info.name, info.nextDate),
	      //style: 'margin-left:30px;',
	      hideLabel : true,
	      checked : true,
	      listeners : {'check' : function (thisObj, check){
	      		if(check) selected_option = 0;
	     	}
	      } 
	    },
	    {
	      xtype: 'radio',
	      name: 'selected_option',
	      inputValue: '1',
	      boxLabel: msgFormat.format(makeWorkingDayMsg, info.currentDate),
	      //style: 'text-align:center',
	      hideLabel : true,
	      listeners : {'check' : function (thisObj, check){
	      		if(check) selected_option = 1;
	     	}
	      }
	    }
	    ]
	});

	warningWindow = new Ext.Window({
		layout:'fit',
		title: workPlanWarningTitle,
		closeAction:'hide',
		plain: true,
		listeners : {
			'hide' : function (thisWindow){
				if(document.getElementById('blogPopupScreen') != null)
					document.getElementsByTagName('body')[0].removeChild(blogPopupScreen);
				if(selected_option == 0){
		 			if(warningEnable && info.scheduleDateChanged){
						showScheduleDateChagneWarning(info, taskData);
					}else{
						//No warning update the tasksheet.
						updateInformation(info, taskData);
					}
		 		}
		 		warningWindow.destroy();
			}},
		items:[_form],
		buttons:[
			{text:submitBtnCaption, handler: function (){
				if(selected_option == 1){
					submitParameter( '{"makeWorkingDay":"true", "newDateToChange":"'+info.editedDate+'","taskId":"'+currentEntryID+'","action":"'+ (currentField == "sd" ? "startDateChanged" : "endDateChanged") +'"}');							
				}
				warningWindow.hide();
			}},
	        {text:cancelBtnCaption, handler: function(){
	        	warningWindow.hide();
	        }}
        ]
	});
}
