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
// default work capture view
var scrollType = 'day';

var hiddenElement;
var paramString = '';
var percentComplete, work, workUpdated, workReported, workRemained;
var currentbox = ''; 
var workCaptureHTMLText, timesheetHTMLText, workRemainingDivText;
var invalidErrorKey = '';
var setPercentComplete = false;
var submitWorkRemaining = false;
var validTimeSheet = false;
var validPercentChange = true;
var workDone = false;
var dayTimeValue = null;
var dayElement, dayValue;
var reportedHistory;
var dec_separator;
var retryTimesheetRequestCount = 0;
var estimatedTotWorkErrorMessage;
var workHoursLessThanZeroErrorMessage;
var loadingMsg;
var belowZeroErrMsg;
var calEstimateErrMsg;
var endTimeErrMsg;
var serverReqErrMsg;
var historyDetailErrMsg;
var internalServerErrMsg;
var workCompleteLbl;
var workRemainingLbl;
var _totEstimated = "";

// getting time sheet html text
function getTimeSheet(scrollby, scrolltype) {
	Ext.Ajax.request({
		url: JSPRootURL+'/assignments/Timesheet?module='+moduleId,
		params: { module : moduleId, objectId: objectId, scrollType: scrolltype, scroll: scrollby },
		method: 'POST',
	    success: function(result, request) {
	   		if(result.responseText != null && result.responseText.trim() != "") {
		   	    var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
				var responseText = result.responseText.replace(docTypeTag, '');	
				timesheetHTMLText = responseText;
				var scriptTag = '<script type="text/javascript">';
				var javaScriptCode = responseText;
				javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
				eval(javaScriptCode.replace(/\n/g, ''));
				if(typeof(timesValueArray) != 'undefined' && timesValueArray != null && timesValueArray != ''){
					timeValues = new Array();
				    timeValues = timesValueArray.split(",");
				    validTimeSheet = true;
			    } else {
			    	validTimeSheet = false;
			    	timesheetDivForWindow = '';
			    	blogItWin.setHeight(blogItWinDefaultHeight);
			    }
			    timesheetDivForWindow = timesheetHTMLText;	
				if(document.getElementById('blog-it_timeSheet') != null){
					document.getElementById('blog-it_timeSheet').innerHTML = timesheetHTMLText;					
					if(navigator.userAgent.toLowerCase().indexOf("firefox/2.0") >= 0) { 
						contentPanel.getComponent('content').updateToolbar();
					}
				}
				if(typeof(dayValue) != 'undefined' && dayValue != '' && dayValue != 0) {
			   	   // preserving work for today in day/week view
			       dayElement = document.getElementById("dateupdX" + objectId + "X" + dayTimeValue);
			       oldDayElement = document.getElementById("wCF_" + objectId + "X" + dayTimeValue);
			       if(typeof(dayElement) != 'undefined' && typeof oldDayElement != 'undefined') {
			       	  var oldDayValue = 0;
			       	  if(oldDayElement != null && oldDayElement.getAttribute('ov') != "") {
			       	  	var oldValue = parseFloat(defaultDecSeparator(oldDayElement.getAttribute('ov'), dec_separator));
			       	  	oldDayValue = oldValue;
			       	  }
			       	  var workCaptured = parseFloat(dayValue) + oldDayValue;
				   	  dayElement.value = workCaptured;
				   	  blogItWorkChanged(objectId, 'true', dayTimeValue, moduleId, scrolltype);
			   	   }
			   }
	   	   } else {
		   	   if(document.getElementById('blog-it_timeSheet') != null){
		   	   	  document.getElementById('blog-it_timeSheet').innerHTML = '';
		   	   }
		   }
		   retryTimesheetRequestCount = 0;
	   },
	   failure: function(result, response) {
			if(retryTimesheetRequestCount <= 5) {
				retryTimesheetRequestCount++;
				setTimeout("getTimeSheet(null, 'day')", 100);
			}
	   }
	});
}

// to get the generated body of time sheet div first time only or by links: day/week
function getTimeSheetEntries(objectId, scrolltype){
	getTimeSheet(null, scrolltype);
}

// scroll the days/week forward/backward
// scrollby : previous/next date/week value
function scroll(scrollby, scrolltype) {
	try{
		if(document.getElementById("loadingDiv") != null){
			document.getElementById("loadingDiv").innerHTML = loadingMsg+'<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" /></td></tr>';
		}
	}catch(err){}	getTimeSheet(scrollby, scrolltype);
}

// get the updated records 
// called on change of textfields, handles errors/sets span by updated values
function blogItWorkChanged(objectId, showFilterPane, dateLongName, moduleId, scrolltype) {	
	paramString = '';
	var errorMessage = '';
	var value;
	var persistValue = 0;
	var valueOfId = '';
	var newValue;
	var oldValue;
    document.getElementById("blogErrorLocationID").innerHTML = "&nbsp;";
    document.getElementById("notWorkingDay").innerHTML = "&nbsp;";
    for (var i = 0; i < timeValues.length; i++) {
		var newElement = document.getElementById("dateupdX" + objectId + "X" + timeValues[i]);
        newValue = defaultDecSeparator(newElement.value, dec_separator);
        var oldElement = document.getElementById("wCF_" + objectId + "X" + timeValues[i]);
        oldValue = defaultDecSeparator(oldElement.getAttribute("ov"), dec_separator);
        value = newValue - oldValue;
		if(newValue != "" && value != 0) {
			paramString += "&dateupdX" + objectId + "X" + timeValues[i]+"="+value;
		} else if(value == 0) {
			paramString += "&dateupdX" + objectId + "X" + timeValues[i]+"="+0;
		}
		// please always set the timestamp so that we are ok for bfd-3075
		paramString += "&tv"+i+"="+timeValues[i];
        if (newValue.trim() != "" && value != 0) {
            paramString += "&wc"+i+"="+value;
            if(isValidValue(''+value, false)){
            	persistValue = parseFloat(value.toFixed(2));
            	valueOfId = "dateupdX" + objectId + "X" + timeValues[i];
		    	validTimeSheet = true;
		    	if(document.getElementById('new-blog-it') != null){
			     	document.getElementById('new-blog-it').disabled = false;
			    }
	        } else {
				if(invalidErrorKey != ''){//invalid Error Key is get set while getting time sheet entries
					errorMessage = invalidErrorKey.replace(/0/, newValue);
					document.getElementById("blogErrorLocationID").innerHTML = "&nbsp;"+errorMessage;
					validTimeSheet = false;
					return false;
				}
	        }
        } else {
        	validTimeSheet = true;
        	if(document.getElementById('new-blog-it') != null){
			   document.getElementById('new-blog-it').disabled = false;
			}
        }    	
    }
    
    //var reportedHistory = document.getElementById('wkrp').innerHTML;
    //reportedHistory = reportedHistory.substring(0,reportedHistory.length-4);
   	if(persistValue != '') {
	   	if(typeof(reportedHistory) != 'undefined' && isBelowZero(reportedHistory, ''+persistValue)) {
	   		persistValue = 0;
			 extAlert(errorTitle, belowZeroErrMsg, Ext.MessageBox.ERROR);
			 if(document.getElementById(valueOfId) != null)
			 	document.getElementById(valueOfId)[0].value = '';
			return false;
		}
	}
	persistValue = 0;
   	
   	var newElement = document.getElementById("dateupdX" + objectId + "X" + dateLongName);
   	var oldElement = document.getElementById("wCF_" + objectId + "X" + dateLongName);
   	var oldWorkForDayElement =  document.getElementById("oW_" + dateLongName);
	var checkWork = defaultDecSeparator(newElement.value , dec_separator) - defaultDecSeparator(oldElement.getAttribute("ov"), dec_separator);
   	if (newElement.value != "" && checkWork != 0) {
    	paramString += "&dln0="+parseFloat(checkWork.toFixed(2));
    	document.getElementById('w_'+dateLongName).innerHTML = parseFloat(checkWork.toFixed(2));
    	document.getElementById('w_'+dateLongName).innerHTML = changeDecSeparator(document.getElementById('w_'+dateLongName).innerHTML, dec_separator);
    	var oldWorkForDay = parseFloat(defaultDecSeparator(oldWorkForDayElement.getAttribute('oldWorkForDay'), dec_separator)) + checkWork;
    	document.getElementById('dW_'+dateLongName).innerHTML = changeDecSeparator(parseFloat(oldWorkForDay.toFixed(2)), dec_separator);
    	newElement.value = changeDecSeparator(parseFloat(defaultDecSeparator(newElement.value, dec_separator)), dec_separator);
   	} else if (checkWork == 0) {
    	paramString += "&dln0="+0;
    	document.getElementById('w_'+dateLongName).innerHTML = '0';
    	document.getElementById('dW_'+dateLongName).innerHTML = changeDecSeparator(oldWorkForDayElement.getAttribute('oldWorkForDay'), dec_separator);
   	}
   	// setting dayTimeValue & day value to preserve on toggle between day/week view
   	dayTimeValue = dateLongName;
   	dayValue = checkWork;
   	
   	// calling ajax request to update timesheet values
	Ext.Ajax.request({
	   url: JSPRootURL +'/assignments/My/Update_Time_Sheet_Entries?module='+moduleId+''+paramString,
	   params: {module : moduleId, objectId : objectId, 
	   			totalAssignments : 1, fromTimesheet : showFilterPane, 
	   			dateLongName : dateLongName, scrollType : scrolltype},
	   method: 'POST',
	   success: function(result, request){
		    var responseText = result.responseText;
			var evalLines = responseText.split(";");
			eval(responseText);
			removeLoadingDiv();
	   },
	   failure: function(result, response){				  
		   document.getElementById("blogErrorLocationID").innerHTML = calEstimateErrMsg;
		   validTimeSheet = false;
		   removeLoadingDiv();
		   //extAlert(errorTitle, 'Server Request Failed..', Ext.MessageBox.ERROR);
		   //hidePopup();
	   }
	});
}

// calculate the time worked from start and end time entered into timesetdiv
function showTimeBox() {
	var fromhrs = document.getElementById("fromHours").value*60;
	var fromminutes = document.getElementById("fromMinutes").value*1;
	var tohrs = document.getElementById("toHours").value*60;
	var tominutes = document.getElementById("toMinutes").value*1;
	var totalFromMinutes = fromhrs + fromminutes;
	var totalToMinutes = tohrs + tominutes;
	if(totalFromMinutes < totalToMinutes) {
		var time = ((totalToMinutes) - (totalFromMinutes));
		document.getElementById(currentbox).value = time/60;
		document.getElementById("blogErrorLocationID").innerHTML = "&nbsp;";
		// getting dayTimeValue for selected timebox to change work
		var dayTimeValue = document.getElementById('timesheetbox0').name.split('X')[2];
		blogItWorkChanged(objectId, 'true', dayTimeValue, moduleId, scrollType);
	} else {
		document.getElementById("blogErrorLocationID").innerHTML = endTimeErrMsg;
	}
}

// hide/show the timesetDiv to enter start and end time
function showtime(openfor) {
	if(openfor != ''){
		if(currentbox != openfor){
			document.getElementById("timesetdiv").style.display = "block";
		} else {
			if(document.getElementById("timesetdiv").style.display == "none") {
				document.getElementById("timesetdiv").style.display = "block";
			} else {
				document.getElementById("timesetdiv").style.display = "none";
			}
		}
	} else {
		document.getElementById("timesetdiv").style.display = "none";
	}
	currentbox = openfor;
}

// changes scroll type as per links: days/week and generate the timesheet div again
function changeScrollType(scrolltype) {
	try{
		if(document.getElementById("loadingDiv") != null){
			document.getElementById("loadingDiv").innerHTML = loadingMsg+'<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" /></td></tr>';
		}
	}catch(err){}
	getTimeSheetEntries(objectId, scrolltype);
	scrollType = scrolltype;
	//showtime('');
}

// called when user updates the work
function setWorkComplete(percentComplete1, workReported1, workRemaining1, work1, workAmount1) {
	if(document.getElementById('blog-it_timeSheet') != null){
		document.getElementById("pc").innerHTML = percentComplete1;
		document.getElementById("wkrm").innerHTML = workRemaining1;
		document.getElementById("wkrp").innerHTML = workReported1;
		document.getElementById("wk").innerHTML = work1;
		hiddenElement = "&wkUpdate"+objectId+"="+workAmount1;
		if(document.getElementById("timesetdiv").style.display != 'none') {
			getEstimates(percentComplete1, workReported1, workRemaining1, work1, workAmount1);
		}
	}
}

function showErrorDiv(errorMessage) {
	if(document.getElementById("notWorkingDay").innerHTML == '&nbsp;'){
		document.getElementById("blogErrorLocationID").innerHTML = errorMessage;
		validTimeSheet = false;
	}
}

function showNotWorkingDayErrorDiv(errorMessage){
	blogPopupScreen.style.display = 'block';
	blogPopupDialogue.style.display = 'block';
	if(document.getElementById("blogErrorLocationID").innerHTML == '&nbsp;'){
		document.getElementById("notWorkingDay").innerHTML = errorMessage;
		validTimeSheet = false;
	}
}

// send the updated data to store into database
function submitTimeSheet(objectId) {	
	var urlParams = ''+moduleId
	var chargeCodeId;
	if(document.getElementById('chargeCodeList'))
		chargeCodeId = getSelectedValue(document.getElementById('chargeCodeList'));
	urlParams += typeof hiddenElement != 'undefined' ? hiddenElement : '';
	urlParams += typeof paramString != 'undefined' ? paramString : '';
	Ext.Ajax.request({
	   url: JSPRootURL +'/assignments/My/Submit_Time_Sheet_Entries?module='+urlParams,
	   params: {module: moduleId, objectId: objectId, chargeCodeId: chargeCodeId, workDone: workDone},
	   method: 'POST',
	   success: function(result, request){
	       var responseText = result.responseText;
		   if(responseText == "submitted"){
				// flag is set to indicate whether to refresh the my assignment list? while hidepopup()
				needRefresh = true;
				if(workDone) { 
					percentChanged(objectId, 'done');
				} else if(document.getElementById("timesetdiv").style.display != 'none' && document.getElementById("estRemaining").value.trim() != '') {
					percentChanged(objectId, 'submit');
				} else {
					saveBlogEntry();
				}
			} else {
				eval(responseText);
				document.getElementById('new-blog-it').disabled = false;
			}
			removeLoadingDiv();
	   },
	   failure: function(result, response){				  
		   extAlert(errorTitle, 'Server Request Failed..', Ext.MessageBox.ERROR);
		   hidePopup();
		   removeLoadingDiv();
	   }
	});
}

// get generated html text for history of a task of person from database and send to showHistorydiv
function showHistory() {
	var allreadyOpened = false;
	if(showWindowForPopup){
		tabs.items.each(function(object){
			if(object.id == 'history'){
				object.show();
				allreadyOpened = true;
			}
		});
	}
	if (!allreadyOpened) {
		var htmlText = '';
		showHistorydiv(htmlText, 1);	// 1 - to show loading div till response arrive
		Ext.Ajax.request({
			url: JSPRootURL +'/assignments/TaskHistory?module='+moduleId,
			params: {module: moduleId, objectId: objectId, selectedTaskTooltip: selectedTaskForToolTip, selectedTask: selectedTask },
			method: 'POST',
			success: function(result, request) {
			   	showHistorydiv(result.responseText, 2); // 2 - got response to show now.
			},
			failure: function(result, response) {
			   extAlert(errorTitle, 'Server Request Failed..', Ext.MessageBox.ERROR);
			   hidePopup();
			}
		});
	}
}

// display the loading div/response div by showHistory()
// 1 indicate loading text
// 2 indicate response arrived
function showHistorydiv(responseText, loading) {
	if(loading == 1) {
		htmlText = '<div>'+responseText+'</div>';
		if(!showWindowForPopup){
			document.getElementById('blogPopupBody').style.display = "none";
		}	
	} else {
		htmlText = '<div>'+responseText+'</div>';
			if(showWindowForPopup){
			historyTabPanel = new Ext.Panel({
					id: 'history',
					title: 'History',
					autoScroll: true,
					closable: true,
					html: htmlText
				});
				
				tabs.add(historyTabPanel);
				tabs.setActiveTab('history');
		}	
	}
	if(!showWindowForPopup){
		document.getElementById('blogPopupDialogue').appendChild(setHistoryPopupBody);	
		document.getElementById('setHistoryPopupBody').innerHTML = htmlText;
	}	
}

//close history popup
function closeHistoryPopup() {
	document.getElementById('blogPopupDialogue').style.height = 'auto';
	document.getElementById('blogPopupDialogue').style.overflow = 'hidden';
	document.getElementById('blogPopupDialogue').removeChild(setHistoryPopupBody);
	document.getElementById('blogPopupBody').style.display = 'block';
}

// set div for adjustment of percentage.
function setPercentageDiv() {
	var leftPos = window.screen.width/3;
	var topPos = window.screen.height/5;
	document.getElementById('blogPopupBody').style.display = "none";
	document.getElementById('blogPopupDialogue').style.width = "410px";
	document.getElementById('blogPopupDialogue').style.left = leftPos+"px";
	document.getElementById('blogPopupDialogue').style.top = topPos+"px";
	
	document.getElementById('blogPopupDialogue').appendChild(setHistoryPopupBody);
	document.getElementById('setHistoryPopupBody').innerHTML = loadingMsg+'<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="middle" />';
	
	Ext.Ajax.request({
		url: JSPRootURL +'/assignments/ChangePercentComplete?module='+moduleId,
		params: {module: moduleId, objectId: objectId, selectedTask: objectName, percentComplete: percentComplete, workRemained: workRemained},
		method: 'POST',
		success: function(result, request) {
		   var responseText = result.responseText;
			if(responseText != null && responseText != '') {
				document.getElementById('setHistoryPopupBody').innerHTML = responseText;
				setPercentComplete = true;
			}
		},
		failure: function(result, response) {				  
		   extAlert(errorTitle, historyDetailErrMsg, Ext.MessageBox.ERROR);
		   hidePercentPopup();
		}
	});
}

// hide percentage adjustment div on cancel and submit button
function hidePercentPopup() {
	setPercentComplete = false;
	blogPopupDialogue.style.width = '80%';
	var leftPos = window.screen.width/10;
	var topPos = window.screen.height/16;
	document.getElementById('blogPopupDialogue').style.left = leftPos+"px";
	document.getElementById('blogPopupDialogue').style.top = topPos+"px";
	document.getElementById('blogPopupDialogue').style.height = "auto";
	document.getElementById('blogPopupDialogue').style.overflow = 'hidden';
	document.getElementById('blogPopupDialogue').removeChild(setHistoryPopupBody);
	document.getElementById('blogPopupBody').style.display = "block";
}

// called if percent changed/submitted to update/save work.
function percentChanged(objectId, type) {
	var pc;
	var submitChange = true;
	var errorText = 'All fields are required.';
	
	// generating parameters string when done checkbox clicked
	// to make assignment complete we have to send total work reported untill now as updated work
	var paramString = "&wkUpdate"+objectId+"="+( parseFloat(document.getElementById("wkrp").innerHTML.replace('hrs','').trim()).toFixed(2) ); 

    // getting changed percentage value	
	if(type != 'done' && typeof document.getElementsByName("pc"+objectId)[0] != 'undefined') {
		pc = document.getElementsByName("pc"+objectId)[0].value;
	}
	
	// on submit from "change estimated time to finish" popup, validating the comment and percent value
	if(type == 'submit') { // && setPercentComplete
		paramString = "&wkUpdate"+objectId+"="+document.getElementById("totEstimated").value; // != '0' ? document.getElementById("totEstimated").value : document.getElementById("totEstimated").value);
		pc = document.getElementById("estPctComplete").innerHTML.replace('%','').trim();
		work = document.getElementById("totEstimated").value;
	}
	// when done check box clicked while work capture
	else if(type == 'done'){
		pc = 100;		
	}
	
	// caliing ajax request for update/submit changed percentages
	if(submitChange) {
		Ext.Ajax.request({
			url: JSPRootURL +'/assignments/My/Percent_Change?module='+moduleId+''+paramString,
			params: {module: moduleId, objectId: objectId, type: type, pc: pc, work: parseFloat(work.replace(/hrs/,'').trim())},
			method: 'POST',
			success: function(result, request){
			    var responseText = result.responseText;
			    // on successful action
				if(responseText == 'submitted'){
					// flag is set to indicate whether to refresh the my assignment list on hidePopup()
					needRefresh = true;
					// saving blog entry on submit after estimates or work done value saved
					if(type == 'submit' || type == 'done') {
						saveBlogEntry();
					}
				}
				// on update or error evaluating received function call
				else {
					eval(responseText);
				}
			},
			failure: function(result, response){				  
			   extAlert(errorTitle, serverReqErrMsg, Ext.MessageBox.ERROR);
			   hidePopup();
			}
		});
	} else {
		extAlert(errorTitle, errorText, Ext.MessageBox.ERROR);
	}
}

// set the values while percent updated
function setPercent(percentComplete1, work1, workRemaining1, workAmount1) {
	validPercentChange = true;
	document.getElementById("percentErrorLocationID").innerHTML = '';
	document.getElementById('pc1').innerHTML = (percentComplete1 * 1).toFixed(2)+'%';
	if(setPercentComplete && document.getElementById('work1') != null) {
		document.getElementById('work1').innerHTML = workRemaining1.replace(/hrs/,'').trim();	
	}
	if(document.getElementById('work2') != null) {
		document.getElementById('work2').innerHTML = workRemaining1;
	}
	percentComplete = percentComplete1;
	work = work1;
	workUpdated = workAmount1;
	workRemained = workRemaining1;
	if(!setPercentComplete) {
		document.getElementById('pc2').innerHTML = 100 - percentComplete;
		document.getElementById('workComplete').innerHTML = parseInt(work.replace(/hrs/,'').trim(), 10) - parseInt(workRemaining1.replace(/hrs/,'').trim(), 10)+' hrs';		
	}
}

function showErrorDiv1(errorMessage) {
	validPercentChange = false;
    document.getElementById("percentErrorLocationID").innerHTML = errorMessage;
	document.getElementsByName("pc"+objectId)[0].value = '0';
}

function ShowPercentError() {
	 extAlert(errorTitle, internalServerErrMsg, Ext.MessageBox.ERROR);
}

// called when time sheet loads first time to use while percent adjusment div opens 
function setDefault(PercentComplete1, workRemaining1, WorkComplete, work1) {
	percentComplete = PercentComplete1
	workUpdated = WorkComplete;
	workRemained = workRemaining1;
	work = work1;
	if (WorkComplete.length > 4)
    	reportedHistory = WorkComplete.substring(0, WorkComplete.length-4);
}

// called when changed percentage submitted to set span of main blogit popup
function setPercentChanges(percentComplete1, workRemaining1, work1, workAmount1) {
	document.getElementById("pc").innerHTML = percentComplete1;	
	document.getElementById("wkrm").innerHTML = workRemaining1;
	document.getElementById("wk").innerHTML = work1;
	// checking if percent complete becomes 100 then select done checkbox
	if(percentComplete1 == '100') {
		document.getElementById("workDone").checked	= true;
	} else {
		document.getElementById("workDone").checked	= false;
	}
}

// getting work remaining div html text
function getWorkRemainingDiv(){
	var pcComplete = parseFloat(percentComplete.replace('%',''));
	var wrkRem = 100 - pcComplete;
	var wrk = parseFloat(work.replace('hrs','').trim());
	workRemainingDivText = '<br /><table style="padding-left: 10%;" align="left" width="60%"><tr><td align="left" nowrap="nowrap" colspan="3"><div id="percentErrorLocationID" class="errorMessage" style="color: red;"></div></td>';
	workRemainingDivText +=	'<tr><td><b>'+workCompleteLbl+'</b></td><td class="blogbar" width="1px" rowspan="6"></td><td><b>'+workRemainingLbl+'</b></td></tr>';
	workRemainingDivText += '<tr><td class="blogbar" colspan="3"></td></tr>';
	workRemainingDivText += '<tr><td><span id="pc1">'+percentComplete+'</span></td><td><span id="pc2">'+wrkRem+'</span>%</td></tr>';
	workRemainingDivText += '<tr><td><span id="workComplete">'+(wrk - parseFloat(workRemained.replace('hrs','').trim()))+' hrs</span></td><td><span id="work2">'+workRemained+'</span></td></tr>';
	workRemainingDivText += '<tr><td class="blogbar" colspan="3"></td></tr>';
	workRemainingDivText += '<tr><td><b>'+workRemainingLbl+' :</b></td>';
	workRemainingDivText += '<td><input type="text" size="5" name="pc'+objectId+'" value="'+wrkRem+'" onchange="percentChanged(\''+objectId+'\',\'update\');"/>%</td>';	
}

function getEstimates(percentComplete1, workReported1, workRemaining1, work1, workAmount1){
	if(document.getElementById("estReported") != null){
		document.getElementById("estReported").innerHTML = workReported1;
	}
	if(document.getElementById("totEstimated").value.trim() != '' && document.getElementById("estRemaining").value.trim() != ''){
		var tot_est = (parseFloat(defaultDecSeparator(document.getElementById("wkrp").innerHTML, dec_separator)).toFixed(2)*1.00 + defaultDecSeparator(document.getElementById("estRemaining").value, dec_separator)*1.00).toFixed(2);
		document.getElementById("totEstimated").value = changeDecSeparator(tot_est, dec_separator);
		document.getElementById("estPctComplete").innerHTML = changeDecSeparator((((parseFloat(defaultDecSeparator(document.getElementById("wkrp").innerHTML, dec_separator)).toFixed(2)*1.00 / parseFloat(defaultDecSeparator(document.getElementById("totEstimated").value, dec_separator)).toFixed(2)*1.00) * 100).toFixed(2)+'%'), dec_separator);
	} else {
		document.getElementById("estPctComplete").innerHTML = document.getElementById("pc").innerHTML;
	}
}

function getHoursInDays(hours){
	return (hours / 8.0).toFixed(1);
}

function totalEstimatesChanged() {
	if(document.getElementById("totEstimated").value.trim() == '') {
		document.getElementById("estRemaining").value = '';
		document.getElementById("estPctComplete").innerHTML = document.getElementById("pc").innerHTML;
		document.getElementById("estReported").innerHTML = document.getElementById("wkrp").innerHTML;
	} else if(isNumberValid("totEstimated")){
		var totEstimatedValue = defaultDecSeparator(document.getElementById("totEstimated").value, dec_separator);
		var reportedWork = defaultDecSeparator(document.getElementById("wkrp").innerHTML, dec_separator);
		if(isTotEstimatesValid(totEstimatedValue, reportedWork)){
			var estRemaining = (parseFloat(totEstimatedValue).toFixed(2) * 1.00 - parseFloat(reportedWork).toFixed(2) * 1.00).toFixed(2);
			estRemaining = estRemaining > 0 ? estRemaining : 0; // set zero for -ve estimate remaining
			document.getElementById("estRemaining").value = changeDecSeparator(estRemaining, dec_separator);
			document.getElementById("estPctComplete").innerHTML = changeDecSeparator((((parseFloat(reportedWork).toFixed(2)*1.00 / parseFloat(totEstimatedValue).toFixed(2)*1.00) * 100).toFixed(2)+'%'), dec_separator);
		}else{
			document.getElementById("totEstimated").value = _totEstimated;
		}
	}
}

function estimatedRemainingChanged() {
	if(document.getElementById("estRemaining").value.trim() == '') {
		document.getElementById("totEstimated").value = '';
		document.getElementById("estPctComplete").innerHTML = document.getElementById("pc").innerHTML;
		document.getElementById("estReported").innerHTML = document.getElementById("wkrp").innerHTML;
	} else if(isNumberValid("estRemaining")){
		var estRemainingValue = defaultDecSeparator(document.getElementById("estRemaining").value, dec_separator);
		var reportedWork = defaultDecSeparator(document.getElementById("wkrp").innerHTML, dec_separator);
		var totEstimated = (parseFloat(reportedWork).toFixed(2) * 1.00 + parseFloat(estRemainingValue).toFixed(2) * 1.00).toFixed(2);
		document.getElementById("estPctComplete").innerHTML = changeDecSeparator((((parseFloat(reportedWork).toFixed(2)*1.00 / parseFloat(totEstimated).toFixed(2)*1.00) * 100).toFixed(2)+'%'), dec_separator);
		_totEstimated = changeDecSeparator(totEstimated, dec_separator);
		document.getElementById("totEstimated").value = _totEstimated;
	}
}

function getEstNotAccurateDiv() {
	if(document.getElementById("timesetdiv").style.display == 'none') {
		document.getElementById("estReported").innerHTML = document.getElementById("wkrp").innerHTML;
		if(document.getElementById("totEstimated").value.trim() == '' && document.getElementById("estRemaining").value.trim() == ''){
			document.getElementById("estPctComplete").innerHTML = document.getElementById("pc").innerHTML; //percentComplete;
		}
		document.getElementById("timesetdiv").style.display = '';
		if(!showWindowForPopup){	
			document.getElementById("blogPopupDialogue").style.top = (parseInt(document.getElementById("blogPopupDialogue").style.top.replace('px','').trim()) - 30) +'px';
		}
	} else {
		document.getElementById("timesetdiv").style.display = 'none';
		if(!showWindowForPopup){	
			document.getElementById("blogPopupDialogue").style.top = (parseInt(document.getElementById("blogPopupDialogue").style.top.replace('px','').trim()) + 30) +'px';
		}	
	}
	if(navigator.userAgent.toLowerCase().indexOf("firefox/2.0") >= 0) { 
		contentPanel.getComponent('content').updateToolbar();
	}
}

// method to toggle between work capture div and work remaining div
// if show is true then work cature div shown else work remaining div
function showWorkCaptureDiv(show){
	if(show) {
		document.getElementById("blog-it_timeSheet").innerHTML = timesheetHTMLText;
		document.getElementById("timeWorkedDiv").innerHTML = workCaptureHTMLText;
	} else {
		setPercentComplete = false;
	}
}

function isNumberValid(id) {
	if(!isValidValue(document.getElementById(id).value, true) || document.getElementById(id).value <= 0 ) {
		extAlert(errorTitle, workHoursLessThanZeroErrorMessage, Ext.MessageBox.ERROR);
		document.getElementById(id).value = '';	
		return false;
	}
	return true;
}

// check the entered negative value and allready reported hours not less zero
function isBelowZero(reportedValue, curentValue) {
	var reportedValue1;
	if(dec_separator != '.' && curentValue != '') {
		reportedValue = reportedValue.replace(dec_separator, '.');
		curentValue = curentValue.replace(dec_separator, '.');
	}
	var lessThanZero = false;
	if(curentValue < 0){
		curentValue = curentValue * -1;
		lessThanZero = reportedValue < curentValue;
	}
	return lessThanZero;
}

// check the entered value is in proper decimal format.
function isValidValue(enteredValue, isForEst) {
	var isValid = true;
	if(enteredValue != '' && !isNaN(enteredValue)) {
		enteredValue = defaultDecSeparator(enteredValue, dec_separator) * 1;
		if(!isForEst && (!isValid || !(enteredValue <= 24))) {
			isValid = false;
		}
	} else {
		isValid = false;
	}
	return isValid;	
}

// check if total estimated hours are less than reported work hours
function isTotEstimatesValid(totEstimatedValue, reportedWork) {
	if(totEstimatedValue < reportedWork){
		extAlert(errorTitle, estimatedTotWorkErrorMessage, Ext.MessageBox.ERROR);
		return false;
	}
	return true;
}
