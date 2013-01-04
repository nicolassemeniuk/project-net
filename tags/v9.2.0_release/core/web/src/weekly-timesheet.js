// JS File for weekly timesheet 
var taskID;
var dateLongNameValue;
var commentMessage="";
var commentDate;
var taskIDArray = new Array();
var projectTotalArray = new Array();
var spaceIds = '';
var taskIdCSV = "";
var contains = new Array();
var containsArray = new Array();
var workCapturedArray = new Array();
var prevLongDate, longDate;
var commentIndex = 1;
var previousSpaceID = 0;
var colorObject;
var commentDialog = document.createElement('div');
var commentFlag = false;
var subjects = new Array();
var spaceId;
var currentObjectID;
var blogItSpaceID;
var setWorkAmount = 0;
var newWorkReported = 0;
var resetGrandTotal = new Array();
var resetProjectTotal = new Array();
var resetAssignTotal = new Array();
var resetWeeklyTotal = new Array();
var checkTimesheetModified = 0;
var isWorkCaptured = false;
var objectDailyTotal = new Array();
var isBlogCollpased = false;
var restoreDailyTotal = new Array();
var restoreWeeklyTotal = new Array();
var restoreAssignmentTotal = new Array();
var restoreProjectTotal = new Array();
var nonwrkDay = new Array();
var wrk24Days = new Array();
var messageFlag = false;
var subjectFlag = false;
var isValueChanged = false;
var valid = true;
var index = 0;
var currentRowCount;
var prevRowCount;
var blogitOpen = false;
var decCount = false;
var nonWrkWarning = true;

commentDialog.id = 'commentDialog';
commentDialog.style.display = 'block';
commentDialog.style.height = 'auto';
commentDialog.style.width = '270px';
commentDialog.style.position = 'absolute';

var commentDialogbody = document.createElement('div');
commentDialogbody.id = 'commentDialogbody';
commentDialogbody.style.display = 'block';
commentDialogbody.style.height = 'auto';
commentDialogbody.style.width = '100%';

// Function for calculating work change.
function w(objectID, dateLongName, uniqueDateValue, spaceId, hrsIndex) {
	// Always clear errors before invoking round-trip
	//Avinash bfd 3169 Able to record negative hours of work against a task.   
    if(document.getElementById("errorLocationID").innerHTML != ""){
    	document.getElementById("errorLocationID").className = 'errorMessage h';
	}
    var paramString = "module="+moduleId+"&objectID="+objectID;

    if(spaceId != previousSpaceID ) {
	 	if(typeof projectTotalArray[dateLongName] == 'undefined' || isNaN(projectTotalArray[dateLongName]) || projectTotalArray[dateLongName] == "" || projectTotalArray[dateLongName] != "") {
	 		previousSpaceID = spaceId;
	  		projectTotalArray[dateLongName] = 0;
	   	} 	
   	}

    var uniqueIndexId = "dW" + objectID + "X" + dateLongName;
    var hs = 'dH'+objectID+'H'+dateLongName;
	var oVCells = document.getElementById('wCF_dW'+objectID+'X'+dateLongName);
    var newWorkCaptureValue = document.getElementById("wF_"+uniqueIndexId).value;
    var oldWorkCaptureValue = (typeof oVCells != 'undefined' && oVCells.getAttribute("ov")) ? oVCells.getAttribute("ov") : '0';
    if(typeof dec_separator != 'undefined' && dec_separator != '.'){
    	newWorkCaptureValue = defaultDecSeparator(newWorkCaptureValue, dec_separator);
    	oldWorkCaptureValue = defaultDecSeparator(oldWorkCaptureValue, dec_separator);
    }
    var adjustedWorkCaptureValue = newWorkCaptureValue - oldWorkCaptureValue;
    if(typeof workCapturedArray[dateLongName] == 'undefined' || isNaN(workCapturedArray[dateLongName]) || workCapturedArray[dateLongName] == "") {
    	workCapturedArray[dateLongName] = 0;
    }
    workCapturedArray[dateLongName] = adjustedWorkCaptureValue;
    projectTotalArray[dateLongName] = adjustedWorkCaptureValue;
    objectDailyTotal[dateLongName] = adjustedWorkCaptureValue;
   
    if(isNaN(adjustedWorkCaptureValue)){
    	flagError(workCaptureInvalid);
    	valid = false;
    	isWorkCaptured = true;
    } else {
	    for(var i = 0; i < timeValues.length; i++) {
	    	var elementID = "dW" + objectID + "X" + timeValues[i];
		    var element = document.getElementById('wCF_dW'+objectID+'X'+timeValues[i]);
		    var newWorkElement = document.getElementById("wF_"+elementID);
		    var otherDayWork = document.getElementById('wCF_dW'+objectID+'X'+timeValues[i]);
		    var value = 0;
		    var newValue = 0;
		    if(typeof element != 'undefined' && element.getAttribute('ov') != null && element.getAttribute('ov') != ""){
		    	value = element.getAttribute('ov');
		     	if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		     		value = defaultDecSeparator(value, dec_separator);
		     	}
			}
			if(typeof newWorkElement != 'undefined' && newWorkElement != null && newWorkElement.value != null && newWorkElement.value != ""){
			   newValue = newWorkElement.value;
			   if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		     		newValue = defaultDecSeparator(newValue, dec_separator);
		       }
			} else {
			    var dayWork = otherDayWork.innerHTML.trim();
				if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		     		dayWork = defaultDecSeparator(otherDayWork.innerHTML.trim(), dec_separator);
		     	}
				if(otherDayWork != null && dayWork != '' && !isNaN(dayWork)) {
					newValue = dayWork;
				}
			}
			var checkWork = 0;
			checkWork = newValue - value;
    		paramString += "&tv"+i+"="+timeValues[i];
	    	
	    	if (checkWork != 0) {
	    		paramString += "&wc"+i+"="+checkWork;
	    		var hiddenElement = document.getElementsByName("workToUpdate"+objectID+"U"+dateLongName)[0];
			    if (typeof hiddenElement == 'undefined') {
			        hiddenElement = createHiddenElement(document, theForm, "workToUpdate"+objectID+"U"+dateLongName);
			    }
			    hiddenElement.value = checkWork;
			    isWorkCaptured = true;
			    executed = false;
			    isValueChanged = true;
	    	} 
	    }
	    var textElement = document.getElementById("wF_"+uniqueIndexId);
	    var oldWorkElement = document.getElementById("wCF_dW"+objectID+"X"+dateLongName);
	    if(!workCaptureCommentEnabled && textElement != 'undefined' && textElement != 'undefined' && textElement.value != 0 && textElement.value != '' && oldWorkElement != null) {
	    	var newWorkValue = textElement.value - oldWorkElement.getAttribute('ov');
    		if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		     		newWorkValue = defaultDecSeparator(newWorkValue, dec_separator);
		    }
		    paramString += "&dln1="+newWorkValue;
		}
		
	   if(workCaptureCommentEnabled) {
		    for (var i = 0; i < objectIds.length; i++) {
		    	var elementID = "dW"+ objectIds[i] + "X" + dateLongName;
		        var element = document.getElementById('wCF_dW'+objectIds[i]+'X'+dateLongName);
			    var newWorkElement = document.getElementById("wF_"+elementID);
			    var otherDayWork = document.getElementById('wCF_dW'+objectIds[i]+'X'+dateLongName);
			    var value = 0;
			    var newValue = 0;
			    
			   if(typeof element != 'undefined' && element.getAttribute('ov') != null && element.getAttribute('ov') != ""){
				   value = element.getAttribute('ov');
				   if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			     		value = defaultDecSeparator(value, dec_separator);
			     	}
				}
				if(typeof newWorkElement != 'undefined' && newWorkElement != null && newWorkElement.value != null && newWorkElement.value != ""){
				   newValue = newWorkElement.value;
				   if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			     		newValue = defaultDecSeparator(newValue, dec_separator);
			       }
				} else if(otherDayWork != null && otherDayWork.innerHTML.trim() != '' && !isNaN(otherDayWork.innerHTML.trim())) {
					newValue = otherDayWork.innerHTML.trim();
					if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			     		newValue = defaultDecSeparator(value, dec_separator);
			     	}
				}
				var checkWork = 0;
				checkWork = newValue - value;
		        if (checkWork != 0) {
		            paramString += "&dln"+i+"="+checkWork;
		        } 
		    }
		
		    for (var i = 0; i < index; i++) {
		    	var elementID = "dW"+ i + "X" + dateLongName;
		        var element = document.getElementById('wCF_dW'+i+'X'+dateLongName);
		        var newWorkElement = document.getElementById("wF_"+elementID);
		        var otherDayWork = document.getElementById('wCF_dW'+i+'X'+dateLongName);
			    var value = 0;
			    var newValue = 0;
			    if(typeof element != 'undefined' && element.getAttribute('ov') != null && element.getAttribute('ov') != ""){
				   value = element.getAttribute('ov');
				   if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			     		value = defaultDecSeparator(value, dec_separator);
			       }
				}
				if(typeof newWorkElement != 'undefined' && newWorkElement != null && newWorkElement.value != null && newWorkElement.value != ""){
				   newValue = newWorkElement.value;
				   if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			     		newValue = defaultDecSeparator(newValue, dec_separator);
			       }
				} else if(otherDayWork != null && otherDayWork.innerHTML.trim() != '' && !isNaN(otherDayWork.innerHTML.trim())) {
					newValue = otherDayWork.innerHTML.trim();
					if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			     		newValue = defaultDecSeparator(value, dec_separator);
			     	}
				}
				var checkWork = 0;
				checkWork = newValue - value;
		        if (checkWork != 0) {
		            paramString += "&dln"+i+"="+checkWork;
		        } 
		   }
	    }
	    
	    if(typeof document.getElementById("hA"+hrsIndex+"X") != 'undefined' && document.getElementById("hA"+hrsIndex+"X") != null) {
	    	var elementID = "dW"+ objectID + "X" + timeValues[hrsIndex];
	    	var element = document.getElementById('wCF_dW'+objectID+'X'+timeValues[hrsIndex]);
		    var newWorkElement = document.getElementById("wF_"+elementID);
		    var otherDayWork = document.getElementById('wCF_dW'+objectID+'X'+timeValues[hrsIndex]);
		    var value = 0;
		    var newValue = 0;
		    
		    if(typeof element != 'undefined' && element.getAttribute('ov') != null && element.getAttribute('ov') != ""){
			   value = element.getAttribute('ov');
			   if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		     		value = defaultDecSeparator(value, dec_separator);
		     	}
			}
			if(typeof newWorkElement != 'undefined' && newWorkElement != null && newWorkElement.value != null && newWorkElement.value != ""){
			   newValue = newWorkElement.value;
			   if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		     		newValue = defaultDecSeparator(newValue, dec_separator);
		     	}
			} else {
				var dayWork = otherDayWork.innerHTML.trim();
				if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			     	dayWork = defaultDecSeparator(dayWork, dec_separator);
			    }
				if(otherDayWork != null && otherDayWork.innerHTML.trim() != '' && !isNaN(dayWork)) {
					newValue = dayWork;
			    }
			}
			var checkWork = 0;
			checkWork = newValue - value;
			if (checkWork != 0) {
				document.getElementById("hA"+hrsIndex+"X").innerHTML = parseFloat(checkWork).toFixed(2);
				if(typeof dec_separator != 'undefined' && dec_separator != '.'){
					document.getElementById("hA"+hrsIndex+"X").innerHTML = changeDecSeparator(document.getElementById("hA"+hrsIndex+"X").innerHTML, dec_separator);
				}
			} else {
				document.getElementById("hA"+hrsIndex+"X").innerHTML = '&nbsp;';
			}
			document.getElementById("wF_"+uniqueDateValue).style.color = 'Green';
			document.getElementById("wF_"+uniqueDateValue).style.fontWeight = 'bold';
		}
	
	    paramString += "&totalAssignments="+(objectIds.length + index);
	    var xml = new XMLRemoteRequest();
	    if(typeof paramString != 'undefined'){
		    var result = xml.getRemoteDocumentString(JSPRootURL+"/servlet/AssignmentController/CurrentAssignments/WorkChanged?"+paramString+"&fromTimesheet=false&dateLongName="+dateLongName+"&textId="+uniqueIndexId+"&spaceID="+spaceId);
		    var evalLines = result.split(";");
		    for (var i = 0; i < evalLines.length; i++) {
		        eval(evalLines[i]);
		    }
		} else {
			if(document.getElementById("errorLocationID").innerHTML != ""){
				document.getElementById("errorLocationID").className = 'errorMessage h';
			}
			 errorneousFieldID = '';
			 errorneousFieldName = '';
		}
	}
	resetPanelsHeight();	
	// generating the space ids string for saving the blog entry
	if(valid && spaceIds.indexOf(spaceId) < 0){
		spaceIds += spaceId +',';
	}
}

//Function for displaying comment popup
function pb(objectID, dateLongName, dayName, currentobject, currentSpaceId, browCount) {
	windowWidth = getWindowWidth();
	
	// clear some global values for the further use
	clearValues();

	// Reassign all the global values for the values
	longDate = dateLongName;
	dateLongNameValue = dayName;
	spaceId = currentSpaceId;
	taskID = objectID;
	document.getElementById('assignment-list').onselectstart = function(){return true;};
	if(typeof prevLongDate != 'undefined' && document.getElementById("wCF_"+prevLongDate) != null){
		document.getElementById("wCF_"+prevLongDate).className = 'ics ws bc';
	}
	
	if(document.getElementById("wCF_"+longDate) != null) {
		document.getElementById("wCF_"+longDate).className += ' ecbc';
	}
	
	// Prepare values for storing old when reset is called set this old values
	for(var resetIndex = 0; resetIndex < timeValues.length; resetIndex++){
		var resetTextID = 'dW'+taskID+'X'+timeValues[resetIndex];
		
		if(typeof resetGrandTotal[timeValues[resetIndex]] == 'undefined' || resetGrandTotal[timeValues[resetIndex]] == '') {
	 		resetGrandTotal[timeValues[resetIndex]] = document.getElementById("dln"+timeValues[resetIndex]).innerHTML;
	 	}
	 	var dateName = resetTextID.substring((resetTextID.indexOf('X')+1));
	 	if(typeof resetProjectTotal['pT_'+dateName+'X'+spaceId] == 'undefined' || resetProjectTotal['pT_'+dateName+'X'+spaceId] == '') {
	 		resetProjectTotal['pT_'+dateName+'X'+spaceId] = document.getElementById('pT_'+dateName+'X'+spaceId).innerHTML;
	 	}
	 	
	 	if(typeof resetWeeklyTotal['wT'+taskID] == 'undefined' || resetWeeklyTotal['wT'+taskID] == '') {
	 		resetWeeklyTotal['wT'+taskID] = document.getElementById('wT'+taskID).innerHTML;
	 	}
	 	
	 	if(typeof resetAssignTotal['at'+taskID] == 'undefined' || resetAssignTotal['at'+taskID] == '') {
	 		resetAssignTotal['at'+taskID] = document.getElementById('at'+taskID).innerHTML;
	 	}
	}
	
	var message;
	var subject;
	if(document.getElementById("bM") != null) {
		message = document.getElementById("bM").value;
	}
	if(document.getElementById("bS") != null) {
		subject = document.getElementById("bS").value;
	}
	
	contains[taskID] = message != "" ? message : 'Message';
	subjects[taskID] = subject != "" ? subject : 'Subject';
	
	if(workCaptureCommentEnabled) {
		var pos = getElementAbsolutePos(currentobject);  
		screenWidth = window.screen.width;
		adjustWidth = (pos.x + 33 + parseInt(commentDialog.style.width.substring(0,3)));
	
		commentDialog.style.left = pos.x + 33;
		commentDialog.style.top	= pos.y + 15;
		var imagePath = JSPRootURL+"/images/menu/close.gif";
		var htmlText ="";
			
		htmlText += "<table cellpadding='0' border='0' style='border-collapse: collapse' width='100%'>"
		htmlText += "<tr style='background: #fef5c7'><td style='font-size:12px;'>Comment</td>"
		htmlText += "<td align='right' style='font-size: 12px;'>"
		htmlText += "<div class='close'>";
		htmlText += "		<a href='javascript:hideWorkCaptureCommentPopup();'>";
		htmlText += '			<img border="0" alt="x" src="'+imagePath+'"/>';
		htmlText += "		</a>";
		htmlText += "</div></td></tr>"
		htmlText += "<tr style='background: #fef5c7;'><td colspan='2' style='padding-right:10px;padding-left:10px;'><textarea rows='2' cols='40' id='bM'></textarea></td></tr><tr style='height:10px;background: #fef5c7'><td colspan='2'></td></tr>"
		htmlText += "<tr style='background: #fef5c7;'><td style='width:100%' align='right' colspan='2'><input type='button' onclick='submitComment();' value='Save'/>"
		htmlText += "<input type='button' onclick='hideWorkCaptureCommentPopup();' value='Cancel'/></td></tr></table>"
		if(typeof contains[longDate] == 'undefined') {
			contains[longDate] = "";
		}
		if(document.getElementById("bM") != 'undefined' && document.getElementById("bM") != null) {
			document.getElementById("bM").value = contains[longDate];
		}
		document.getElementsByTagName('body')[0].appendChild(commentDialog);		
		document.getElementById('commentDialog').appendChild(commentDialogbody);
		document.getElementById('commentDialogbody').innerHTML = htmlText;
	} else if(!isWorkCaptured){
		if(typeof currentObjectID != 'undefined' && typeof currentRowCount != 'undefined' && currentObjectID != '' && currentObjectID != taskID && prevRowCount != browCount){
			hideBlogMessageWindow();
		}
		if(document.getElementById('blogsCollapsed').style.display != '') {
			collapseDiv(true, 'assignmentsDiv', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '99%', '1%', false);
			resetPanelsWidth();
			document.getElementById('splitterimgclose').style.cursor = 'help';
			document.getElementById('splitterimgclose').onclick = new Function('function disable(){}');
		}
		
		if(document.getElementById('blogsCollapsed').style.display == '') {
			document.getElementById('splitterimgclose').style.cursor = 'help';
			document.getElementById('splitterimgclose').onclick = new Function('function disable(){}');
		}
		
		if(!blogitOpen) {
			createInlineBlogit(browCount, objectID);
			currentRowCount = browCount;
			prevRowCount = currentRowCount;
		}
		
		commentFlag = true;
		currentObjectID = taskID;
		blogItSpaceID = spaceId;
		
		if(typeof contains[taskID] == 'undefined') {
			contains[taskID] = "";
		}
		if(typeof subjects[taskID] == 'undefined') {
			subjects[taskID] = "";
		}
		
		if(document.getElementById("bM") != 'undefined' && document.getElementById("bM") != null) {
			document.getElementById("bM").value = contains[taskID];
		}
		
		if(document.getElementById("bS") != 'undefined' && document.getElementById("bS") != null) {
			document.getElementById("bS").value = subjects[taskID];
		}
		getBlogEntriesByObjectId(taskID);
	} else if(currentObjectID != taskID){
		if(document.getElementById("errorLocationID").innerHTML != ""){
		    document.getElementById("errorLocationID").className = 'errorMessage h';
		}
		Ext.Msg.show({
				title: confirmAlertTitle,
				msg: blogConfirmMessage,
				buttons: Ext.Msg.YESNOCANCEL,
				fn: processResult,
				icon: Ext.MessageBox.QUESTION
		});
	}
	prevLongDate = longDate;
	setHeaderWidth();
}
//Hide comment popup
function hideWorkCaptureCommentPopup() {
	if(typeof commentDialog != 'undefined' && commentDialog != null) {
		document.getElementsByTagName('body')[0].removeChild(commentDialog);
	}
}

//Function for submitting the comment.
function submitComment(){
	var uniqueIndexId = longDate;
	if(document.getElementById("wF_"+uniqueIndexId) != null && document.getElementById("wF_"+uniqueIndexId).value != "" ) {
		var commentInput = document.getElementById("bM").value;
		contains[longDate] = commentInput;
		containsArray[commentIndex] = contains[longDate];
		taskIDArray[commentIndex] = taskID;
		commentIndex++;
	
		if(commentInput != null && commentInput != ""){
			if(typeof commentDialog != 'undefined' && commentDialog != null) {
				document.getElementsByTagName('body')[0].removeChild(commentDialog);
				document.getElementById("wCF_"+uniqueIndexId).style.background = '#ffeb8a';
			}
		} else {
			flagError(workCaptureCommentErrorMessage);
		}
	} else {
		flagError(captureWorkMessage);
	}
}

function pC(objectID) {
	// Always clear errors before invoking round-trip
	if(document.getElementById("errorLocationID").innerHTML != ""){
		document.getElementById("errorLocationID").className = 'errorMessage h';
    }
    var paramString = "module="+moduleId+"&objectID="+objectID;
    paramString += "&pc=" + escape(document.getElementsByName("pc"+objectID)[0].value);

    for (var i = 0; i < timeValues.length; i++) {
        var element = document.getElementsByName("dX" + objectID + "X" + timeValues[i]);
        var value = element[0].value;
        if (value != "") {
            paramString += "&wc"+i+"="+value;
            paramString += "&tv"+i+"="+timeValues[i];
        }
    }

    var xml = new XMLRemoteRequest();
    var result = xml.getRemoteDocumentString(JSPRootURL+"/servlet/AssignmentController/CurrentAssignments/PCChanged?"+paramString);
    var evalLines = result.split(";");

    for (var i = 0; i < evalLines.length; i++) {
        eval(evalLines[i]);
    }
}

//ajax callback functions
function setPercentComplete(id, percentComplete) {
    var element = document.getElementsByName("pc" + id)[0];
    if(element != 'undefined' && element != null) {
	    element.value = percentComplete;
    }
}

function setWorkRemaining(id, workRemaining) {
    var element = document.getElementById("wkrm"+id);
    if(element != null){
	    element.innerHTML = workRemaining;
	}
}

function setWorkReported(id, workReported, dateName, spaceID) {
    var element = document.getElementById("wkrp"+id);
    if(element != null){
    	element.innerHTML = workReported;
    }
    errorneousFieldID = '';
	errorneousFieldName = '';
    newWorkReported = workReported;
    setTotals(id, dateName, spaceID, workReported);
}

function setWork(id, work, workAmount) {
	var theForm = document.forms[0];
    var element = document.getElementById("wk"+id);
    if(element != null){
	    element.innerHTML = work;
	}
    //Pass the information on to the updating handler.
    var hiddenElement = document.getElementsByName("wkUpdate"+id)[0];
    if (hiddenElement == undefined) {
        hiddenElement = createHiddenElement(document, theForm, "wkUpdate"+id);
    }
    hiddenElement.value = workAmount;
    setWorkAmount = workAmount;
}

function setDaySummaryWork(datelongname, workReported) {
    var element = document.getElementById("dln"+datelongname);
    element.innerHTML = workReported;
}

function hide(row) {
    row.className += " h";
}

function show(row) {
	var re = new RegExp("h", "g");
    row.className = row.className.replace(re, "");
}

//Method for expand .
function eP(rowNo){
	setHeaderWidth();
	var row = document.getElementById(rowNo+"_row");
	var startLevel = row.getAttribute("lvl");
	var childRow;
	hide(row);
	//save as expanded node
	saveUserSettings("node"+row.getAttribute("sid")+"expanded", "true");
	
	for(var i=rowNo-1; i >= 0; i--){
		if(childRow && parseInt(childRow.getAttribute("lvl")) <= startLevel) 
			return;
		childRow = document.getElementById(i+"_row");
		show(childRow);
		if(i == currentRowCount && currentObjectID && currentObjectID != '') {
			document.getElementById('pbr2'+currentObjectID).style.display = '';
			document.getElementById('pbr1'+currentObjectID).style.display = '';
		}
	}
}

//collapse projects.
function cP(rowNo){
	setHeaderWidth();
	if(currentObjectID && currentObjectID != ''){
		document.getElementById('pbr2'+currentObjectID).style.display = 'none';
		document.getElementById('pbr1'+currentObjectID).style.display = 'none';
	}
	
	var row = document.getElementById(rowNo+"_row");
	var startLevel = row.getAttribute("lvl");
	var childRow;
	hide(row);
	//save as unexpanded node
	saveUserSettings("node"+row.getAttribute("sid")+"expanded", "false");
	
	for(var i=rowNo+1; i <= rowCount+1; i++){
		if(childRow && parseInt(childRow.getAttribute("lvl")) <= startLevel){
			show(childRow);
			return;
		}
		childRow = document.getElementById(i+"_row");
		if(childRow)
			hide(childRow);
	}
}

//Method for expand / collapse all projects 
function expandCollapseAll(collapse){
	for(var i=1; i <= rowCount; i++){
		var row = document.getElementById(i+"_row");
		if(!row) continue;
		if(row.getAttribute("collapsednode")){
			collapse ? show(row) : hide(row);
		}else{
			collapse ? hide(row) : show(row);
		}
		
		//save as expand/collapse node
		if(row.getAttribute("sid"))
			saveUserSettings("node"+row.getAttribute("sid")+"expanded", !collapse);
	}
	setHeaderWidth();
	if(currentObjectID && currentObjectID != ''){
		document.getElementById('pbr2'+currentObjectID).style.display = collapse ? 'none' : '';
		document.getElementById('pbr1'+currentObjectID).style.display = collapse ? 'none' : '';
	}
}

//Set important flag for blog entry.
function setImportantFlag() {
	if(document.getElementById('isImportantEntry').checked){
		important = true;
		document.getElementById('impMsg').style.display = '';
	} else {
		important = false;
		document.getElementById('impMsg').style.display = 'none';
	}
}

var prevSelectedTaskId = 0;

//Show blog entries for selected assignment.
function b(taskId, endDate, currentObject, monthName, shortEndDate, newMonthName, currentSpaceID){
	var showExpanCollapseImage = true;
	var showPersonImage = false;
	var taskName = document.getElementById('a'+taskId).innerHTML.trim(); 
	if(prevSelectedTaskId != taskId){
		if(typeof showBlogsOnRight != 'undefined' && showBlogsOnRight){
			blogEntriesDivId = 'taskBlogDivRight';
			showExpanCollapseImage = true;
			showPersonImage = false;
		} else {
			blogEntriesDivId = 'TaskBlogDivBottom';
		}
		if(document.getElementById(blogEntriesDivId) != null) {
			if(typeof document.getElementById('blogDivTop') != 'undefined' && document.getElementById('blogDivTop') != null) {
				document.getElementById('blogDivTop').innerHTML = loadingMessage + '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
			}
			
			Ext.Ajax.request({
				url: JSPRootURL +'/blog/view/loadBlogEntries?module='+moduleId,
				params: {module : 60, forObjectId : taskId, endDate :endDate, childObjectsList :taskId, workSpaceId: currentSpaceID},
				method: 'POST',
				success: function(result, request){
					// eval javascript code
					var scriptTag = '<script type="text/javascript">';
					var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
					responseText = result.responseText.replace(docTypeTag, '');
					var javaScriptCode = responseText;
					javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
				    
					eval(javaScriptCode.replace(/\n/g, ''));
					// Set response and blog count message 
					
					document.getElementById('taskBlogDivRight').innerHTML = responseText;
					document.getElementById('blogCountMessage').className += ' blogDivTop';
					var mnth = (monthName != newMonthName) ? newMonthName + " " + shortEndDate :  monthName +" " + shortEndDate;
					var blogCountMessage = document.getElementById('blogCountMessage').innerHTML;
					var blogCount = blogCountMessage.substring(0, blogCountMessage.indexOf(" "));
					if(document.getElementById('blogCountMessage').innerHTML == 'Blog entries not found.'){
						document.getElementById('blogCountMessage').innerHTML = msgFormat.format(noBlogPostsMessage, taskName);
					} else if(parseInt(blogCount) == 1){
						document.getElementById('blogCountMessage').innerHTML = msgFormat.format(blogPostMessage, blogCount, taskName, mnth);
					} else {
						document.getElementById('blogCountMessage').innerHTML = msgFormat.format(blogPostsMessage, blogCount, taskName, mnth);
					}
				},
				failure: function(result, response){
				}
			});	
			prevSelectedTaskId = taskId;
			document.getElementById(blogEntriesDivId).style.paddingLeft = '0px';
		}
	}
	if(colorObject != 'undefined' && colorObject != null) {
		if(typeof colorObject != 'undefined') {
			colorObject.style.background = '#ffffff';
		}
	}
	colorObject = currentObject;
	
	if(typeof currentObject != 'undefined') {
		currentObject.style.background = '#DFE8F6';
	}
}

//reset the subject and message.
function rC() {
	if(document.getElementById("errorLocationID").innerHTML != ""){
		document.getElementById("errorLocationID").className = 'errorMessage h';
	}
	if(isWorkCaptured) {
		Ext.MessageBox.confirm(confirmAlertTitle, resetConfirmMessage, function(btn) {
   			if(btn == 'yes'){
   				hideBlogMessageWindow();
				executed = true;
				isWorkCaptured = false;
				resetUIComponents();
				document.getElementById(prevRowCount+'_row').style.backgroundColor = '#FFFFFF';
   			} 
   		});
	} else {
		hideBlogMessageWindow();
		executed = true;
		isWorkCaptured = false;
		resetUIComponents();
	}
	document.getElementById('assignment-list').onselectstart = function(){return false;};
}
//Function for saving blog entry for Large work comment.
function pbm() {
	valid = !(typeof errorneousFieldID != 'undefined' && (errorneousFieldName != null && errorneousFieldID != null 
				&& errorneousFieldID != "" && document.getElementById("wF_"+errorneousFieldID) != null 
				|| document.getElementById("wF_"+errorneousFieldID) != null && document.getElementById("wF_"+errorneousFieldID).value < 0 
					|| document.getElementById("wF_"+errorneousFieldID) != null && (document.getElementById("wF_"+errorneousFieldID).value > 24)));
					
	for (var validIndex = 0; validIndex < timeValues.length; validIndex++) {
		if(wrk24Days[timeValues[validIndex]] && wrk24Days[timeValues[validIndex]] == 'true') {
			valid = false;
			storeError = totalWorkHourInvalid;
			break;
		}
	}
	for (var validIndex = 0; validIndex < timeValues.length; validIndex++) {
		if(nonwrkDay[timeValues[validIndex]] && nonwrkDay[timeValues[validIndex]] == 'true') {
			nonWrkWarning = false;
			break;
		} else {
			nonWrkWarning = true;
		}
	}
	for (var validIndex = 0; validIndex < timeValues.length; validIndex++) {
		var otherDayWork = document.getElementById('wCF_dW'+currentObjectID+'X'+timeValues[validIndex]);
		var newWork = otherDayWork.innerHTML.trim();
		if(typeof dec_separator != 'undefined' && dec_separator != '.'){
	    	newWork = defaultDecSeparator(otherDayWork.innerHTML.trim(), dec_separator);
    	}
		if(otherDayWork != null && newWork != '' && newWork != '&nbsp;' && !isNaN(newWork) && newWork < 0) {
			valid = false;
			storeError = negativeWorkInvalid;
			break;
		}
	}
	for (var validIndex = 0; validIndex < timeValues.length; validIndex++) {
		var otherDayWork = document.getElementById('wCF_dW'+currentObjectID+'X'+timeValues[validIndex]);
		var newWork = otherDayWork.innerHTML.trim();
		if(typeof dec_separator != 'undefined' && dec_separator != '.'){
	    	newWork = defaultDecSeparator(otherDayWork.innerHTML.trim(), dec_separator);
    	}
    	if(navigator.userAgent.toLowerCase().indexOf('safari') > -1) {
    		if(isNaN(parseFloat(newWork))) { newWork = ''; }
    	}
		if(otherDayWork != null && newWork != '' && newWork != '&nbsp;' && isNaN(newWork) ) {
			valid = false;
			storeError = workCaptureInvalid;
			break;
		}
	}
	if(valid) {		
		var workComplete = "";
		var workDoneParamString;
		if(typeof document.getElementById("wD") != 'undefined' && document.getElementById("wD").checked) {
			workComplete = 100;
		}
		subject = subjectFlag ? document.getElementById('bS').value : '';
		content = document.getElementById('bM').value;
		subject = subject.trim();
		content = content.trim();
		var newWorkSubmitted = 0;
		var paramString = "module="+moduleId;
		for (var blogIndex = 0; blogIndex < timeValues.length; blogIndex++) {
			var elementID = "dW" + currentObjectID + "X" + timeValues[blogIndex];
			var element = document.getElementById('wCF_dW'+currentObjectID+'X'+timeValues[blogIndex]);
			var newWorkElement = document.getElementById("wF_"+elementID);
			var otherDayWork = document.getElementById('wCF_dW'+currentObjectID+'X'+timeValues[blogIndex]);
			var value = 0;
			var newValue = 0;
			if(typeof element != 'undefined' && element.getAttribute('ov') != null && element.getAttribute('ov') != ""){
				value = element.getAttribute('ov');
				if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		     		value = defaultDecSeparator(value, dec_separator);
		     	}
			}
			if(typeof newWorkElement != 'undefined' && newWorkElement != null && newWorkElement.value != null && newWorkElement.value != ""){
			    newValue = newWorkElement.value;
			    if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		     		newValue = defaultDecSeparator(newValue, dec_separator);
		     	}
			} else {
				var eachDayWork = otherDayWork.innerHTML.trim();
				if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		     		eachDayWork = defaultDecSeparator(otherDayWork.innerHTML.trim(), dec_separator);
			    }
				if(otherDayWork != null && eachDayWork != '' && eachDayWork != '&nbsp;' && !isNaN(eachDayWork)) {
					newValue = eachDayWork;
			     }
			}
			newWorkSubmitted += newValue - value;
			if(!isNaN(newValue - value) && (newValue - value) != 0) {
				paramString += "&workToUpdate="+(newValue - value).toFixed(2);
				paramString += "&dateSeconds="+timeValues[blogIndex];
			}
		}
		paramString += "&objectID="+currentObjectID;
		paramString += "&personId="+userId;
		
		// generating parameters string when done checkbox clicked
		// to make assignment complete we have to send total work reported untill now as updated work
		if(typeof document.getElementById("wD") != 'undefined' && document.getElementById("wD").checked) {
			paramString += "&wkUpdate"+currentObjectID+"="+parseFloat(newWorkReported).toFixed(2);
		} else {
			paramString += "&wkUpdate"+currentObjectID+"="+setWorkAmount;
		}
		paramString += "&pc=" +workComplete;

		if(!isNaN(newWorkSubmitted)) {
			if(newWorkSubmitted != 0) {
				executed = true;
				if(messageFlag && content != '') {
					if(!nonWrkWarning) {
						Ext.MessageBox.confirm(confirmAlertTitle, nonWorkingDayInvalid, function(btn) {
				   			if(btn == 'yes'){
				   				if(subject != null && subject == '') {
									Ext.MessageBox.confirm(confirmAlertTitle, blogPostSubjectConfirm, function(btn) {
							   			if(btn == 'yes'){
											saveEntries(paramString, parseFloat(newWorkSubmitted).toFixed(2), subject, content);
							   			} 
							   		});
							   	} else {
									saveEntries(paramString, parseFloat(newWorkSubmitted).toFixed(2), subject, content);
								}
				   			} 
					   	});
					} else {
						if(subject != null && subject == '') {
							Ext.MessageBox.confirm(confirmAlertTitle, blogPostSubjectConfirm, function(btn) {
					   			if(btn == 'yes'){
									saveEntries(paramString, parseFloat(newWorkSubmitted).toFixed(2), subject, content);
					   			} 
					   		});
					   	} else {
							saveEntries(paramString, parseFloat(newWorkSubmitted).toFixed(2), subject, content);
						}
					}
				} else {
					extAlert(errorAlertTitle, blogEntryContentErrorMessage, Ext.MessageBox.ERROR);
				}
			} else {
				extAlert(errorAlertTitle, captureWorkMessage, Ext.MessageBox.ERROR);
			}
		} else {
			extAlert(errorAlertTitle, workCaptureInvalid, Ext.MessageBox.ERROR);
		}
	} else {
		extAlert(errorAlertTitle, storeError, Ext.MessageBox.ERROR);
	}
}

//function for hiding blog message window and resetting previous work captured values
function hideBlogMessageWindow() {	
	for(var resetIndex = 0; resetIndex < timeValues.length; resetIndex++){
		var resetTextID = 'dW'+currentObjectID+'X'+timeValues[resetIndex];
		var element = document.getElementById('wCF_dW'+currentObjectID+'X'+timeValues[resetIndex]);

		if(typeof element != 'undefined' && element.getAttribute('ov') != null && element.getAttribute('ov') != '0.0' && element.getAttribute('ov') != '') {
			document.getElementById("wCF_"+resetTextID).innerHTML = element.getAttribute('ov');
			if(typeof dec_separator != 'undefined' && dec_separator != '.'){
				document.getElementById("wCF_"+resetTextID).innerHTML = changeDecSeparator(element.getAttribute('ov'), dec_separator);
			}
		} else {
			document.getElementById("wCF_"+resetTextID).innerHTML = '&nbsp;';
		}

		//Reset all the total as per previous
		if(typeof resetGrandTotal != 'undefined' && typeof resetGrandTotal[timeValues[resetIndex]] != 'undefined') {
 			document.getElementById("dln"+timeValues[resetIndex]).innerHTML = (typeof resetGrandTotal[timeValues[resetIndex]] == 'undefined') ? 0 : resetGrandTotal[timeValues[resetIndex]];
 			document.getElementById("dln"+timeValues[resetIndex]).style.color = 'Black';
	 	}
	 	
	 	if(typeof restoreProjectTotal != 'undefined' && typeof resetProjectTotal['pT_'+timeValues[resetIndex]+'X'+blogItSpaceID] != 'undefined') {
		 	var spaceTotal = 'pT_'+timeValues[resetIndex]+"X"+blogItSpaceID;
	 		document.getElementById('pT_'+timeValues[resetIndex]+'X'+blogItSpaceID).innerHTML = parseFloat(resetProjectTotal[spaceTotal]);
	 		document.getElementById('pT_'+timeValues[resetIndex]+'X'+blogItSpaceID).style.color = 'Black';
	 	}
	 	
	 	if(typeof resetWeeklyTotal != 'undefined' && typeof resetWeeklyTotal['wT'+currentObjectID] != 'undefined') {
	 		document.getElementById('wT'+currentObjectID).innerHTML = resetWeeklyTotal['wT'+currentObjectID] ;
	 		document.getElementById('wT'+currentObjectID).style.color = 'Black';
	 	}
	 	
	 	if(typeof resetAssignTotal != 'undefined' && typeof resetAssignTotal['at'+currentObjectID] != 'undefined') {
	 		document.getElementById('at'+currentObjectID).innerHTML = resetAssignTotal['at'+currentObjectID];
	 		document.getElementById('at'+currentObjectID).style.color = 'Black';
	 	}
	 	
	 	if(typeof document.getElementById("hA"+resetIndex+"X") != 'undefined' && document.getElementById("hA"+resetIndex+"X") != null 
	 	 		&& document.getElementById("hA"+resetIndex+"X").innerHTML != '') {
	 	 	document.getElementById("hA"+resetIndex+"X").innerHTML = '';
	 	}
	 	if(typeof objectDailyTotal[timeValues[resetIndex]] != 'undefined' && objectDailyTotal[timeValues[resetIndex]] != '') {
	 		objectDailyTotal[timeValues[resetIndex]] = '';
	 	} 
	 	if(document.getElementById("wCF_"+resetTextID) != null) {
			document.getElementById("wCF_"+resetTextID).className = 'ics ws';
		}
		
		nonwrkDay[timeValues[resetIndex]] = false;
		wrk24Days[timeValues[resetIndex]] = false;
	}
	if(typeof document.getElementById('bS') != 'undefined' && document.getElementById('bS') != '') {
		document.getElementById('bS').value = '' ;
	}
	if(typeof document.getElementById('bM') != 'undefined' && document.getElementById('bM') != '') {
		document.getElementById('bM').value = '';
	}
	if(typeof document.getElementById("wD") != 'undefined' && document.getElementById("wD").checked) {
	 	document.getElementById("wD").checked = false;
	}
	
	if(colorObject && colorObject != null) {
		colorObject.style.background = '#FFFFFF';
	}
	removeInlineBlogit(prevRowCount);
	commentFlag = false;
	currentObjectID = "";
	prevLongDate = '';
	valid = valid ? !valid : valid;
	blogitOpen = false;
	decCount = false;
	nonWrkWarning = true;
	resetUIComponents();
	setHeaderWidth();
	// Enable right click context menu
	document.oncontextmenu = new Function('return true');
}

// Catch event before unloading the page if blog is not posted.
window.onbeforeunload = function(e){
	if(timeValues && timeValues != null) {
		for(var clickIndex = 0; clickIndex < timeValues.length; clickIndex++){
			var elementID = "dW" + currentObjectID + "X" + timeValues[clickIndex];
			var element = document.getElementById('wCF_dW'+currentObjectID+'X'+timeValues[clickIndex]);
			var newWorkElement = document.getElementsByName("wF_"+elementID);
			var otherDayWork =  document.getElementById('wCF_dW'+currentObjectID+'X'+timeValues[clickIndex]);
			var value = 0;
		    var newValue = 0;

		    if(typeof element != 'undefined' && element != null && element.getAttribute('ov') != null && element.getAttribute('ov') != ""){
			   value = element.getAttribute('ov');
			    if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			     	value = defaultDecSeparator(value, dec_separator);
			    }
			}
			if(typeof newWorkElement != 'undefined' && newWorkElement != null && newWorkElement.value != null && newWorkElement.value != ""){
			   newValue = newWorkElement.value;
			   if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			   		newValue = defaultDecSeparator(newValue, dec_separator);
			   }
			} else if(typeof otherDayWork!= 'undefined' && otherDayWork != null){
				var eachDayWork = otherDayWork.innerHTML.trim();
				if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			   		eachDayWork = defaultDecSeparator(otherDayWork.innerHTML.trim(), dec_separator);
			    }
				if(typeof otherDayWork != 'undefined' && otherDayWork != null && eachDayWork != '' && eachDayWork != '&nbsp;' && !isNaN(eachDayWork)) {
					newValue = eachDayWork;
				}
			}
			var checkWork = 0;
			checkWork = newValue - value;
			if(checkWork != 0) {
				checkTimesheetModified = checkWork;
				break;
			}
		}
	}
	if(!executed){
		if(checkTimesheetModified != 0) {
			return modifiedMessage;
		}
	}else{
		executed = true;
	}
};

// Method to save blog entries and work captured changes
function saveEntries(paramString, newWorkSubmitted, subject, content){
	isWorkCaptured = false;
	removeInlineBlogit(currentRowCount);
	showLoadingDiv('Saving...');
	var xml = new XMLRemoteRequest();
	var result = xml.getRemoteDocumentString(JSPRootURL+"/servlet/AssignmentController/CurrentAssignments/UpdateProcessing?"+paramString);
	
	var url = JSPRootURL +'/blog/AddWeblogEntry/SaveBlogEntry'+(new Date()).getTime();
	Ext.Ajax.request({
	   url: url+'?module='+moduleId,
	   params: { module : moduleId, subject : subject, content : content, spaceId : spaceId,
				 objectId : currentObjectID, workSubmitted : newWorkSubmitted},
	   method: 'POST',
	   success: function(result, request){
	   		submitFormAfterWorkLog(false);
	   },
	   failure: function(result, response){
		   submitFormAfterWorkLog(false);
	   }
	});
}

// reset empty values for Inline blog it
function ssm(){
	if(messageFlag && document.getElementById('bM').value == ''){
		document.getElementById('bM').style.color = 'Gray';
		document.getElementById('bM').value = blogMessage;
		messageFlag = false;
	}
	if(subjectFlag && document.getElementById('bS').value == ''){
		document.getElementById('bS').style.color = 'Gray';
		document.getElementById('bS').value = blogSubject;
		subjectFlag = false;
	}
}

// set subject value for inline blog it
function ss(){
	if(!subjectFlag){
		subjectFlag = true;
		document.getElementById('bS').value = '';
		document.getElementById('bS').style.color = 'Black';
	}
}

// set message value for inline blog it
function sm(){
	if(!messageFlag){
		messageFlag = true;
		document.getElementById('bM').value = '';
		document.getElementById('bM').style.color = 'Black';
	}
}

// set totals daily totals, project totals
function setTotals(id, dateName, spaceID, workReported){
	setDailyTotal(dateName);
    setProjectTotal(dateName, spaceID);
    setAssignmentTotal(id, workReported);
    setWeeklyTotal(id);
}

// set Daily total
function setDailyTotal(dateName){
	var dailyTotal = restoreDailyTotal[dateName];
	if(typeof dec_separator != 'undefined' && dec_separator != '.'){
	  	dailyTotal = defaultDecSeparator(dailyTotal, dec_separator);
	}
	
    var grandTotal = 0.0 + parseFloat(dailyTotal);
    if(typeof grandTotal != 'undefined'){
		document.getElementById("dln"+dateName).innerHTML = (parseFloat(workCapturedArray[dateName]) + parseFloat((grandTotal < 0) ? -grandTotal : grandTotal)).toFixed(2);
		
		if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			document.getElementById("dln"+dateName).innerHTML = changeDecSeparator(document.getElementById("dln"+dateName).innerHTML, dec_separator);
		}
		
		document.getElementById("dln"+dateName).style.color = 'Green';
		
		// Store daily total for checking validation of work more than 24 hours
		if(typeof dec_separator != 'undefined' && dec_separator != '.'){
		  	dailyTotal = defaultDecSeparator(dailyTotal, dec_separator);
		}
	    objectDailyTotal[dateName] = (parseFloat(workCapturedArray[dateName]) + parseFloat((grandTotal < 0) ? -grandTotal : grandTotal)).toFixed(2);
    }
}

// set project total
function setProjectTotal(dateName, spaceID){
	var spaceTotal = dateName+"X"+spaceID;
    var projectDailyTotal = restoreProjectTotal[spaceTotal];
    if(typeof dec_separator != 'undefined' && dec_separator != '.'){
	  	projectDailyTotal = defaultDecSeparator(projectDailyTotal, dec_separator);
	}
    var projectTotal = 0.0 + parseFloat(projectDailyTotal);
	if(typeof projectTotal != 'undefined'){
		projectTotal =  (parseFloat(projectTotalArray[dateName]) + parseFloat((projectTotal < 0) ? -projectTotal : projectTotal)).toFixed(2);
		document.getElementById("pT_"+spaceTotal).innerHTML = projectTotal;
		if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			document.getElementById("pT_"+spaceTotal).innerHTML = changeDecSeparator(document.getElementById("pT_"+spaceTotal).innerHTML, dec_separator);
		}
		document.getElementById("pT_"+spaceTotal).style.color = 'Green';
	}
}

// set Assignment total 
function setAssignmentTotal(assignmentID, workReported){
	if(typeof document.getElementById("at"+assignmentID) != 'undefined' && document.getElementById("at"+assignmentID) != null) {
		document.getElementById("at"+assignmentID).innerHTML = parseFloat(workReported).toFixed(2);
		if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			document.getElementById("at"+assignmentID).innerHTML = changeDecSeparator(document.getElementById("at"+assignmentID).innerHTML, dec_separator);
		}
		document.getElementById("at"+assignmentID).style.color = 'Green';
	}
}

// set weekly total
function setWeeklyTotal(assignmentID){
	if(typeof document.getElementById("wT"+assignmentID) != 'undefined' && document.getElementById("wT"+assignmentID) != null) {
		var objectTotal = document.getElementById("wT"+assignmentID).innerHTML.trim();
		
		if(typeof dec_separator != 'undefined' && dec_separator != '.'){
	  		objectTotal = defaultDecSeparator(objectTotal, dec_separator);
		}
		var newTotal = 0.0;
		for(var weekIndex=0; weekIndex < timeValues.length; weekIndex++) {
			var uniqueIndexId = "dW" + assignmentID + "X" + timeValues[weekIndex];
			var newWorkCaptureValue = 0.0;
			if(document.getElementById('wF_'+uniqueIndexId) != null) {
    			newWorkCaptureValue = document.getElementById('wF_'+uniqueIndexId).value.trim();
    			if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			  		newWorkCaptureValue = defaultDecSeparator(newWorkCaptureValue, dec_separator);
				}
    		}
    		if(!isNaN(newWorkCaptureValue)) {
    			if(newWorkCaptureValue != '') {
					newTotal = parseFloat(newTotal) + parseFloat(newWorkCaptureValue);
					var eachDayWork = document.getElementById('wCF_'+uniqueIndexId).innerHTML.trim();
					if(typeof dec_separator != 'undefined' && dec_separator != '.'){
				  		eachDayWork = defaultDecSeparator(document.getElementById('wCF_'+uniqueIndexId).innerHTML.trim(), dec_separator);
					}
					if(eachDayWork != null && eachDayWork != '' && !isNaN(eachDayWork)){
						newTotal = parseFloat(newTotal) + parseFloat(eachDayWork);
					}
				} else {
					var eachDayWork = document.getElementById('wCF_'+uniqueIndexId).innerHTML.trim();
					if(typeof dec_separator != 'undefined' && dec_separator != '.'){
				  		eachDayWork = defaultDecSeparator(document.getElementById('wCF_'+uniqueIndexId).innerHTML.trim(), dec_separator);
					}
					if(eachDayWork != null && eachDayWork != '' && !isNaN(eachDayWork)){
						newTotal = parseFloat(newTotal) + parseFloat(eachDayWork);
					}
				}
			}
		}
		document.getElementById("wT"+assignmentID).innerHTML = parseFloat(newTotal).toFixed(2);
		if(typeof dec_separator != 'undefined' && dec_separator != '.'){
			document.getElementById("wT"+assignmentID).innerHTML = changeDecSeparator(document.getElementById("wT"+assignmentID).innerHTML, dec_separator);
		}
		document.getElementById("wT"+assignmentID).style.color = 'Green';
	}
}

// store all the totals for 
function restoreAllTotals(){
	if(timeValues && timeValues != null && projectIds && projectIds != null && objectIds && objectIds != null){
		// set Daily Total
		for(var restoreIndex=0; restoreIndex < timeValues.length; restoreIndex++){
			restoreDailyTotal[timeValues[restoreIndex]] = document.getElementById('dln'+timeValues[restoreIndex]).innerHTML;
		}
		// store Project total
		for(var restoreIndex=0; restoreIndex < projectIds.length; restoreIndex++){
			for(var dayIndex=0; dayIndex < timeValues.length; dayIndex++){
				var spaceTotal = timeValues[dayIndex]+"X"+projectIds[restoreIndex];
				restoreProjectTotal[spaceTotal] = document.getElementById('pT_'+spaceTotal).innerHTML;
			}
		}
	}
}

// function for processing the create blog comment for other assignment
function processResult(buttonId) {
	if(buttonId  == 'yes') {
		pbm();
	} else if(buttonId == 'no') {
		valid = true;
		isWorkCaptured = false;
		hideBlogMessageWindow();
		document.getElementById(currentRowCount+'_row').style.backgroundColor = '#DFE8F6';
		pb(taskID, longDate , dateLongNameValue, colorObject, spaceId, currentRowCount);
	} else {
		document.getElementById(prevRowCount+'_row').style.backgroundColor = '#DFE8F6';
		document.getElementById(currentRowCount+'_row').style.backgroundColor = '#FFFFFF';
		document.getElementById('wCF_'+prevLongDate).className = 'ics ws';
	}
}

// function for clearing the previous global values
function clearValues() {
	dateLongNameValue = "";
	longDate = "";
	taskID = "";
	spaceId = "";
}

// Function for saving the user view settings
function saveUserSettings(property, value){
	Ext.Ajax.request({
		url: JSPRootURL + '/timesheet/PersonalizeTimesheet/saveChanges',
		params: {module: moduleId, property: property, value: value},
		method: 'POST',
		success: function(result, request){}
	});
}

function resetUIComponents(){
	if(rightPanelOpened && document.getElementById('blogsExpanded').style.display != '') {
			collapseDiv(false, 'assignmentsDiv', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '80%', '20%', false);
	 		resetPanelsWidth();
			document.getElementById('splitterimgopen').style.cursor = 'pointer';
			document.getElementById('splitterimgopen').onclick = new Function('toggleRightPanel(true)');
			document.getElementById('splitterimgclose').style.cursor = 'pointer';
			document.getElementById('splitterimgclose').onclick = new Function('toggleRightPanel(false)');
		} else {
			document.getElementById('splitterimgopen').style.cursor = 'pointer';
			document.getElementById('splitterimgopen').onclick = new Function('toggleRightPanel(true)');
			document.getElementById('splitterimgclose').style.cursor = 'pointer';
			document.getElementById('splitterimgclose').onclick = new Function('toggleRightPanel(false)');
		}
		subjectFlag = subjectFlag ? !subjectFlag : subjectFlag;
		messageFlag = messageFlag ? !messageFlag : messageFlag;
}

// Set non working day flag for each day.
function setNonWorkingDayFlag(nonWorkingDay, weekDay) {
	nonwrkDay[weekDay] = nonWorkingDay;
}

// Set Is work completed in particular day is more than 24 Hours
function setWork24DayFlag(iswrk24Day, weekDay) {
	wrk24Days[weekDay] = iswrk24Day;
}

// Inserts inline work capture field
function e(elementID, taskType, valueName, id, date, count, sid, rowCount) {
	// Clear the previous message 
    if(document.getElementById("errorLocationID").innerHTML != ""){
    	document.getElementById("errorLocationID").className = 'errorMessage h';
	}
	if(document.getElementById("errorreporter") && document.getElementById("errorreporter").innerHTML != '') {
		document.getElementById("errorreporter").innerHTML = '';
	}
	currentRowCount = rowCount;
	var hs = 'dH'+id+'H'+date;
	var oVCells = document.getElementsByName(hs);
	var workValue = (typeof oVCells[0] != 'undefined' && oVCells[0].getAttribute("ov") != null) ? parseFloat(oVCells[0].getAttribute("ov")) : 0.0;
	
	if(typeof dec_separator != 'undefined' && dec_separator != '.' && typeof oVCells[0] != 'undefined' && oVCells[0].getAttribute("ov") != null){
	  	workValue = defaultDecSeparator(oVCells[0].getAttribute("ov"), dec_separator);
	}
	if(elementID != null && taskType != null && taskType != 'summary') {
			if(document.getElementById('wCF_'+elementID).innerHTML.trim() != '&nbsp;' && document.getElementById('wCF_'+elementID).innerHTML.trim() != workValue && !isNaN(parseFloat(document.getElementById('wCF_'+elementID).innerHTML.trim()))) {
			    var value = document.getElementById('wCF_'+elementID).innerHTML.trim();
				if(typeof dec_separator != 'undefined' && dec_separator != '.'){
	  				value = defaultDecSeparator(document.getElementById('wCF_'+elementID).innerHTML.trim(), dec_separator);
				}
				workValue = (document.getElementById('wCF_'+elementID).innerHTML.trim() == '0') ? '' : parseFloat(value);
			} else {
				workValue = (workValue == 0.0) ? '' : workValue;
			}
			if(document.getElementById('wCF_'+elementID).innerHTML.indexOf('<') == -1){
				var textValue = workValue;
				if(typeof dec_separator != 'undefined' && dec_separator != '.'){
					textValue = changeDecSeparator(workValue, dec_separator);
				}
				document.getElementById('wCF_'+elementID).innerHTML = '<input type="text" class="tb" value=\''+ textValue +'\' size="1" maxlength="5" id=\'wF_'+elementID+'\' name='+valueName+' onfocus=\'pb('+id+', "'+elementID+'", '+date+', this, '+sid+', '+rowCount+');\' onchange=\'w('+id+','+date+', "'+elementID+'", '+sid+', '+count+');\'  onkeypress=\'return numberCheck(event, this);\' onblur=\'re(this, "'+elementID+'", "'+workValue+'");\' />'
				// Disable right click context menu
				document.oncontextmenu = new Function('return false');
			}
			if(document.getElementById('wF_'+elementID) != null) {
				document.getElementById('wF_'+elementID).focus();
			}
	} else {
		document.getElementById('wCF_'+elementID).className += " sT";
	}
}

// Redirects assignment details page according to object type
function r(type, id, sid) {
	if(type == 'form_data') {
		self.location = JSPRootURL + '/form/FormEdit.jsp?module='+formModule+'&action='+action+'&id='+id+'&spaceID='+sid;
	} else {
		self.location = JSPRootURL + '/servlet/ScheduleController/TaskView?module='+scheduleModule+'&action='+action+'&id='+id;
	}
}

// Remove the inline editor for the work captured field
function re(obj, elementID, hField){
	document.getElementById('wCF_'+elementID).innerHTML = (obj.value).trim() != '' ? (obj.value).trim() : '&nbsp;';
	// Enable right click context menu
	document.oncontextmenu = new Function('return true');
	decCount = false;
	if(typeof dec_separator != 'undefined' && dec_separator != '.' && (obj.value).trim() != ''){
		document.getElementById('wCF_'+elementID).innerHTML = changeDecSeparator((obj.value).trim(), dec_separator);
	}
	oldWork = parseFloat(hField);
	newWorkValue = parseFloat(document.getElementById('wCF_'+elementID).innerHTML.trim());
	if(typeof dec_separator != 'undefined' && dec_separator != '.'){
	  	oldWork = defaultDecSeparator(hField, dec_separator);
	  	newWorkValue = document.getElementById('wCF_'+elementID).innerHTML.trim();
	}
	if (!isNaN((newWorkValue - oldWork)) && (newWorkValue - oldWork) != 0) {
		document.getElementById('wCF_'+elementID).className += " editedCell";
	}
	if(typeof dec_separator != 'undefined' && dec_separator != '.' && document.getElementById('wCF_'+elementID).innerHTML.trim() != '&nbsp;'){
		document.getElementById('wCF_'+elementID).innerHTML = changeDecSeparator(document.getElementById('wCF_'+elementID).innerHTML.trim(), dec_separator);
	}
	document.getElementById('wCF_'+elementID).className += " bc";
}

// Redirects to the project dashboard on double click
function rp(sid) {
	self.location = JSPRootURL + '/project/Dashboard?id='+sid;
}
