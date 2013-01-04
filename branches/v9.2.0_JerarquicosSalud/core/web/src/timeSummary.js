
var memberListString;
var showAssignmentView = false;
var expCollAction = 'collapsed';
	
//View summary on onload method
function viewTimeSummary() {
	document.getElementById('errorMessage').innerHTML = '';
	showLoadingDiv(loadingMsg);
	Ext.Ajax.request({
	   url: JSPRootURL +'/business/report/TimeSummaryView/viewPage?module='+moduleId,
	   params: { moduleId : moduleId},
	   method: 'POST',
	   timeout: 180000,
	   success: function(result, request){
	      removeLoadingDiv();
		  document.getElementById('timeSummaryContent').innerHTML = result.responseText;
		  evaluateScript(result.responseText);
		  summaryWinResize();
		  setInnerDiv();
		  expCollAction = 'collapsed';
	   },
	   failure: function(result, response){
		   document.getElementById('errorMessage').innerHTML = '<font color="red"><b>'+loadingErrorMessage+'</b></font>';
		   removeLoadingDiv();	   
	   },
	   callback: function(result, response){
			setHeaderWidth();	   		
	   }
	});
}

// Export the CSV file 
function exportCSV() {
	if(document.getElementById('blankMessage') == null) {
		Ext.Ajax.request({
		   url: JSPRootURL +'/business/report/TimeSummaryView/exportCSV?module='+moduleId,
		   params: { moduleId : moduleId},
		   method: 'POST',
		   success: function(result, request){
			   	self.document.location = JSPRootURL +'/servlet/Download?downloadableObjectAttributeName=summaryCSVDownLoad&cleanup=true';
		   },
		   failure: function(result, response){
		   }
		});
	} else {
		extAlert(errorTitle, fileEmptyMessage, Ext.MessageBox.ERROR);
	}
}

// Export the Excel
function exportExcel() {
	if(document.getElementById('blankMessage') == null) {
		self.document.location = JSPRootURL +'/timesummary/exportExcel/';
    } else {
    	extAlert(errorTitle, fileEmptyMessage, Ext.MessageBox.ERROR);
    }
}
	
// Adjust the window on resize
function summaryWinResize(){
	var winWidth = getWindowWidth();	
  	var winHeight = getWindowHeight();
  	document.getElementById('content').style.width = (winWidth - 225)+'px';
 	document.getElementById('summaryContent').style.height = (winHeight - 255 ) + 'px';
}

// Set the size of header and footer
function setInnerDiv(){
	var winWidth = getWindowWidth();	
  	var winHeight = getWindowHeight();
  	if(document.getElementById('assignmentDataDiv') != null){
  		setHeaderWidth();
		document.getElementById('assignmentDataDiv').style.width = (winWidth - 228) + 'px';
	 	document.getElementById('assignmentDataDiv').style.height = (winHeight - 335 ) + 'px';
	 	document.getElementById('summaryHeader').style.width = (winWidth - 228) + 'px';
	 	document.getElementById('summaryFooter').style.width = (winWidth - 228) + 'px';
	 	document.getElementById('summaryHeader-div').style.width = (winWidth - 228)+ 'px';
	 	document.getElementById('summaryFooter-div').style.width = (winWidth - 228)+ 'px';
 	}
}
// View business summary after filter selection
function viewSummaryList(){
	var userIds = '';
	if(document.getElementById('businessMembers') != null){
		var selectedMembers = new Array();
		var selObj = document.getElementById('businessMembers');
		var count = 0;
		for (var i = 0; i < selObj.options.length; i++) {
		  	if (selObj.options[i].selected) {
		  		if(selObj.options[i].value == 'all'){
		  			for(var i = 1; i < selObj.options.length; i++){
		  				selectedMembers[count] = selObj.options[i].value;
		    			userIds += selectedMembers[count]+',';
		    			count++;
		  			}
		  			break;
		  		}
	  		if(selObj.options[i].value == spaceAdminId) {
	  			selObj.options[i].selected = true;
	  		}
	    	selectedMembers[count] = selObj.options[i].value;
	    	userIds += selectedMembers[count]+',';
	    	count++;
		  }
		}
	}	
	var showDailyView = document.getElementById('dailyViewEnabled').checked;
	showAssignmentView = document.getElementById('assignmentEnabled').checked;
	
	document.getElementById('errorMessage').innerHTML = '';
	showLoadingDiv(loadingMsg);
	var datePattern = '';
	var startDate =  getNextDate(document.getElementById('startDateValue').value.trim(), 'startDate');
	var endDate =  getNextDate(document.getElementById('endDateValue').value.trim(), 'endDate');
	if(startDate == invalidStartDateMsg){
		extAlert(errorTitle, invalidStartDateMsg, Ext.MessageBox.ERROR);
		removeLoadingDiv();
	} else if(endDate == invalidEndDateMsg){
		extAlert(errorTitle, invalidEndDateMsg, Ext.MessageBox.ERROR);
		removeLoadingDiv();
	} else if(isdateStartBeforeEnd(document.getElementById('startDateValue').value.trim(), document.getElementById('endDateValue').value.trim())){
		extAlert(errorTitle, invalidDateRangeMsg, Ext.MessageBox.ERROR);
		removeLoadingDiv();
	}else {
		Ext.Ajax.request({
		   params: { startDate : startDate, endDate: endDate, showDailyView : showDailyView, showAssignmentView : showAssignmentView, userIds : userIds, datePattern: datePattern},
		   url: JSPRootURL +'/business/report/TimeSummaryView/weeklyView?module='+moduleId,
		   method: 'POST',
	   	   timeout: 180000,
		   success: function(result, request){
		   	  removeLoadingDiv();	
			  document.getElementById('timeSummaryContent').innerHTML = result.responseText;
			  summaryWinResize();
			  setInnerDiv();
			  setHeaderWidth();
			   expCollAction = 'collapsed';
		   },
		   failure: function(result, response){
			   document.getElementById('errorMessage').innerHTML = '<font color="red"><b>'+loadingErrorMessage+'</b></font>';
			   removeLoadingDiv();		   
		   }
		});
	}
}

// changes in date by adding next date
	function getNextDate(dateString, dateType) {
		if(dateString != ''){
			if(dateString == userDateFormatString)
				return '';
			if(checkDate(dateString, userDateFormatString)) {
				date = Date.parseDate(dateString, getJSUserDatePattern(userDateFormatString));
				date.setDate(date.getDate()+1);
				return date.getDate() + '/' + (date.getMonth()+1) + '/' + date.getFullYear();
			} else if(dateType == 'startDate'){
				return invalidStartDateMsg;	
			} else if(dateType == 'endDate'){
				return invalidEndDateMsg;	
			}
		} else {
			return dateString;
		}
	}
	
// Run the toggle for expand and collapse of project and resources
function toggleTree(id, type, actionFor) {
	var viewType = '';
	if(showAssignmentView){
		viewType = 'assignmentView';
	} else {
		viewType = 'projectView';
	}
	
    expandCollapse(id, actionFor, viewType);
    var resourceClassName = document.getElementById(id).className.split(' ');
    if((document.getElementById(id).className).trim() == 'visible'){
  		document.getElementById(id).className = 'hidden';
  		if(type == 'person'){
  			var resourceTotal = id.split('_');
	    	document.getElementById('resource_'+resourceTotal[1]).className = ' visible allTotalColum';
	    	document.getElementById('togglermember_'+resourceTotal[1]).src = JSPRootURL+'/images/minus.png';
  		} else {
  			var projectTotal = id.split('_');
	    	document.getElementById('prt_'+projectTotal[1]+'_'+projectTotal[2]).className = ' visible';
	    	document.getElementById('togglerproject_'+projectTotal[1]+'_'+projectTotal[2]).src = JSPRootURL+'/images/minus.png';
  		}
  	}else {
  		
  		document.getElementById(id).className = 'visible';
  		var projectTotal = id.split('_');
  		if(type == 'person'){
  			var resourceTotal = id.split('_');
	    	document.getElementById('resource_'+resourceTotal[1]).className = ' hidden allTotalColum';
  		} else {
	    	document.getElementById('prt_'+projectTotal[1]+'_'+projectTotal[2]).className = ' hidden';
  		}
  	}
    setHeaderWidth();
}

// Method for call expand and collapse
function expandCollapse(id, actionFor, viewType) {
    var row = document.getElementById(id);
    var kidsShown = row.getAttribute("kidsShown");
    if (kidsShown == null)
        kidsShown = "true";
    if(actionFor == 'expand'){
        collapseSummaryRow(row, viewType);
    } else {
        expandSummaryRow(row, viewType);
    }
}

// set scroll position
function scrollSummaryHeader(){
	setHeaderWidth();
	document.getElementById('summaryHeader-div').scrollLeft = document.getElementById('assignmentDataDiv').scrollLeft;
	document.getElementById('summaryFooter-div').scrollLeft = document.getElementById('assignmentDataDiv').scrollLeft;
}

// set header width
function setHeaderWidth(){
	if(document.getElementById("assignmentDataDiv") != null){
		if(hasVerticalScrollbar(document.getElementById("assignmentDataDiv")) && document.getElementById("assignmentDataDiv").style.width != ''){
			document.getElementById("summaryHeader-div").style.width = document.getElementById("assignmentDataDiv").style.width.replace("px", "") - 17 + "px";
			document.getElementById("summaryFooter-div").style.width = document.getElementById("assignmentDataDiv").style.width.replace("px", "") - 17 + "px";
			document.getElementById("assignmentDataTable").style.width = document.getElementById("summaryFooter-div").style.width;
		} else {
			document.getElementById("summaryHeader-div").style.width = document.getElementById("assignmentDataDiv").style.width;
			document.getElementById("summaryFooter-div").style.width = document.getElementById("assignmentDataDiv").style.width;
			document.getElementById("assignmentDataTable").style.width = document.getElementById("summaryFooter-div").style.width;
		}
	}
	if(navigator.userAgent.toLowerCase().indexOf('safari') > -1) {
		document.getElementById('assignmentDataDiv').style.top = '216px';
				document.getElementById("summaryFooter-div").style.top = parseInt(document.getElementById('assignmentDataDiv').style.top) 
																	+ parseInt(document.getElementById('assignmentDataDiv').offsetHeight);
	}
	if(navigator.userAgent.toLowerCase().indexOf('chrome') > -1) {
		document.getElementById('assignmentDataDiv').style.top = '214px';
		document.getElementById("summaryFooter-div").style.top = parseInt(document.getElementById('assignmentDataDiv').style.top) 
																	+ parseInt(document.getElementById('assignmentDataDiv').offsetHeight);
	}
}

// For evaluating response of ajax request for getting script data
function evaluateScript(responseText){
	var scriptTag = '<script type="text/javascript">';
	var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	var javaScriptCode = responseText.replace(docTypeTag, '');
	javaScriptCode = javaScriptCode.substring(javaScriptCode.indexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
	eval(javaScriptCode.replace(/\n/g, ''));
}

// Function to send request to save expand/collpased setting
function saveUserSettings(value, ids, ViewType){
	Ext.Ajax.request({
		url: JSPRootURL + '/business/report/TimeSummaryView/saveChanges?module='+moduleId,
		params: {value: value, ids: ids, viewType: ViewType, showAssignmentView: showAssignmentView},
		method: 'POST',
		success: function(result, request){}
	});
}

//Collapse a previously expanded row
function collapseSummaryRow(row, viewType) {
	var ids = '';
	ids = row.id+',';
	
    var startLevel = row.getAttribute("level");
    var childRow = getNextSibling(row);
    var rowSepLevel = childRow.getAttribute("rowSep");
    while (childRow && ((parseInt(childRow.getAttribute("level")) > startLevel) || (rowSepLevel))) {
        if (!rowSepLevel || parseInt(rowSepLevel) > startLevel) {
        	var idString = childRow.id.split('_');
        	if(showAssignmentView && idString[0] == 'project'){
        		childRow.className = '';
        	}
        	if(idString[0] != 'assignment'){
        		ids += childRow.id+',';
        	}
            hide(childRow);
            var idString = childRow.id.split('_');
        }
        childRow = getNextSibling(childRow);
        if (childRow) {
            rowSepLevel = childRow.getAttribute("rowSep");
        }
    }
      saveUserSettings('false', ids, viewType);
}

//Expand a previously collapsed row
function expandSummaryRow(row, viewType) {
	var ids = '';
	ids = row.id+',';
	
    var startLevel = row.getAttribute("level");
    var dontExpandUntilLevel = -1;
    var cnt = 0;
    var childRow = getNextSibling(row);
    var childRowLevel = (childRow ? parseInt(childRow.getAttribute("level")) : 0);
    while (childRow && ((childRowLevel > startLevel) || (childRow.getAttribute("rowSep")))) {
        //In a situation where we are expanding, but a summary task was previously
        //collapsed, don't show that inner summary task's children.
        if (dontExpandUntilLevel == -1 || dontExpandUntilLevel >= childRowLevel) {
            var idString = childRow.id.split('_');
            if(showAssignmentView && idString[0] == 'project'){
            	//saveUserSettings('node'+childRow.id+'expanded'+viewType, 'false');
            } else {
            	if(idString[0] != 'assignment'){
	            	ids += childRow.id+',';
            	}
            	show(childRow);
            } 	
            //This means we've gone back up to the top level, stop hiding children.
            if (dontExpandUntilLevel != -1) {
                dontExpandUntilLevel = -1;
            }
        }

        //If a summary task is being shown, but its children are not being shown,
        //set a variable that makes sure the children will be skipped.
        var kidsShown = childRow.getAttribute("kidsShown");
        if (dontExpandUntilLevel == -1 && kidsShown && kidsShown=="false") {
            dontExpandUntilLevel = childRowLevel;
        }

        //Deal with the special case of row separators.  We want to show the rowSep
        //of the summary task which has hidden children.
        var rowSepLevel = childRow.getAttribute("rowSep");
        if (rowSepLevel && dontExpandUntilLevel == rowSepLevel) {
        	ids += childRow.id+',';
            show(childRow);
        }

        childRow = getNextSibling(childRow);
        childRowLevel = (childRow ? parseInt(childRow.getAttribute("level")) : 0);
    }
    saveUserSettings('true', ids, viewType);
}
