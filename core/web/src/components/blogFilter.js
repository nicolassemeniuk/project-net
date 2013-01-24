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
// initializing content panel with html editor
var prev_spn='DaysPostsFor_20';
var projectsCombo = null;        
var itemTypeCombo = null;
var teamCombo = null;
var taskCombo = null;
var filterForm;
var showAllRadio;
var showDatesRadio;
var showDaysAndPostLine;
var fromDate;
var toDate;
var strStartDate = null; 
var strEndDate = null;
var timeReportedChk;
var bodyStyleForArchiveFilter;
var toggleRadioStatus = true;
var timeReportedChkLable = '';
var importantChk;
var importantChkLabel = '';
setTimeReportedChkLable();
setImportantChkLabel();
var offset = 0;

var memberId, taskId;
var showTimeReportedEntries = false;
var showImportantBlogEntries = false;
var itemType;
var filterObjectId = null;
var retryFilterRequestCount = 0;
var blogLaodingScreen = document.createElement('div');
	blogLaodingScreen.id = 'blogLaodingScreen';
	blogLaodingScreen.style.display = 'block';
	blogLaodingScreen.style.opacity = 0;
	blogLaodingScreen.style.filter = 'alpha(opacity:0)';

var index = 0;
	
if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
	bodyStyleForArchiveFilter = 'position:relative;top:-10px;padding-bottom:15px';	
}else {
	bodyStyleForArchiveFilter = 'position:relative;top:0px;padding-bottom:15px';
}

Ext.onReady(function(){

    Ext.QuickTips.init();
    
fromDate = new Ext.form.DateField({
						id : 'fromDateId',
						renderTo : 'startDateId',
						format:'m/d/Y',
						emptyText : 'Start Date',
						labelSeparator : ':',
						invalidText: 'Start date is not valid - It must be in the format of mm/dd/yyyy',
						listeners : {'change' : setStartDateByCombo}
					});
					
toDate = new Ext.form.DateField({
						id : 'toDateId',
						renderTo : 'endDateId',
						format:'m/d/Y',
						emptyText : 'End Date',
		                labelSeparator : ':',
		                invalidText: 'End date is not valid - It must be in the format of mm/dd/yyyy',
						listeners : {'change' : setEndDateByCombo}
					});	
  });

<!-- //
// This function is for triming the spaces in values
String.prototype.trim = function(){
    a = this.replace(/^\s+/, '');
    return a.replace(/\s+$/, '');
}
function setShowAllRadio() {
	document.getElementById("showAllRadio").checked = true;
	document.getElementById("showDatesRadio").checked = false;
}
function setDatesRadio() {
	posts = null;
	
	if(document.getElementById(prev_spn) != null){
		document.getElementById(prev_spn).style.backgroundColor = '#dfe8f7';
	}
	document.getElementById("showAllRadio").checked = false;
	document.getElementById("showDatesRadio").checked = true;
}
function setStartDateByCombo() {
	if(fromDate.getValue() != ''){
		startDate = fromDate.getValue();
		strStartDate = null;
		strEndDate = null;
		posts = null;
	}else{
		startDate = null;
	}
}
function setEndDateByCombo() {
	if(toDate.getValue() != ''){
		endDate = toDate.getValue();
		strStartDate = null;
		strEndDate = null;
		posts = null;	
	}else{
		endDate = null;
	}
}
function setDates(archiveDates,link) {
	var dates = new Array(null, null);
	if(archiveDates.indexOf('-') > 0){
		dates = archiveDates.split('-');		
		if(dates.length == 2){
			strStartDate = dates[0];
			strEndDate = dates[1];
			startDate = null;
			endDate = null;
			posts = null;
		 }
	}else{
		posts = archiveDates;//assign number of posts to show
		startDate = null;
		endDate = null;
		strStartDate = null;
		strEndDate = null;
	}
	fromDate.setValue('');
	toDate.setValue('');	
	document.getElementById("showAllRadio").checked = true;
	document.getElementById("showDatesRadio").checked = false;
}
function setBackgroundColorOf(cur_spn) {
	document.getElementById(cur_spn).style.backgroundColor = '#a4fdcf';	
	if(prev_spn != '' && prev_spn != cur_spn){
		document.getElementById(prev_spn).style.backgroundColor = '#dfe8f7';	
	}
	prev_spn = cur_spn;	
}
// apply fiters on blog entries
function applyFilters(filterType) {	
	var url = JSPRootURL +'/blog/view/filter_Blog_Entries?';
	var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	var loadingBlogEnries = '<font color="blue" style="font-weight: bold; size: 12px;" >Loading blog entries... </font>';
		loadingBlogEnries += '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
		
	var loadingFilteredText = '<font color="blue" style="font-weight: bold; size: 12px;" >Loading filtered entries... </font>';
		loadingFilteredText += '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
		
	if (filterType == 'clearFilter') {
		memberId = null,
		taskId = null;
		itemType = null;
		filterObjectId = null;
		showTimeReportedEntries = false;
		showImportantBlogEntries = false;
		strEndDate = null;
		strStartDate = null;
		if (document.getElementById('team') != null) {
			if (teamMemberId != null && teamMemberId != '') {
				memberId = teamMemberId;
				document.getElementById('team').value = memberId;
			} else {
				document.getElementById('team').value = 0;
			}
		}
		if (document.getElementById('task') != null) {document.getElementById('task').value = 0;}
		if (document.getElementById('itemTypeFilter') != null) {document.getElementById('itemTypeFilter').value = 0; setTask();}
		if (document.getElementById('projectFilter') != null) {document.getElementById('projectFilter').value = 0;}
		if (document.getElementById('timeReportedId') != null) {document.getElementById('timeReportedId').checked = false;}
		if (document.getElementById('importantBlogEntryId') != null) {document.getElementById('importantBlogEntryId').checked = false;}
		applyLoadingStyleFor('loadBlogEntries_Body');
		if (document.getElementById('blogCountMessage') != null) {
			document.getElementById('blogCountMessage').innerHTML = loadingBlogEnries;
		}
		offset = 0;
	}
	else if (filterType == 'apply') {
		if (document.getElementById('timeReportedId') != null && typeof(document.getElementById('timeReportedId')) != 'undefined') {// for loading error in IE timeReportedChk is null
			showTimeReportedEntries = document.getElementById('timeReportedId').checked;
		}
		
		if (document.getElementById('importantBlogEntryId') != null && typeof(document.getElementById('importantBlogEntryId')) != 'undefined') {// for loading error in IE timeReportedChk is null
			showImportantBlogEntries = document.getElementById('importantBlogEntryId').checked;
		}
		
		if (validateDates()) { //just alert for entered dates
				if (fromDate.getValue() != '' && toDate.getValue() != '') {
					strStartDate = getFormattedDate(fromDate.getValue());
					strEndDate = getNextDate(toDate.getValue());
				} else {
					strStartDate = strEndDate = null;
				}
		} else {
			return false;
		}
		
		if (document.getElementById('team') != null) {
			memberId = document.getElementById('team').value;
			if (memberId == 0) {
				memberId = null;
			}
		}
		
		if (teamMemberId != null && teamMemberId != '') {
			memberId = teamMemberId;
			if (document.getElementById('team') != null)
				document.getElementById('team').value = memberId;
		}
		
		if (document.getElementById('task') != null) {
			filterObjectId = document.getElementById('task').value;
			if (filterObjectId == 0) {
				filterObjectId = null;
			}
		}
		
		if (document.getElementById('itemTypeFilter') != null) {
			itemType = document.getElementById('itemTypeFilter').value;
			if (itemType == 0) {
				itemType = null;
			}
		}
		
		if (document.getElementById('projectFilter') != null) {
			filterObjectId = document.getElementById('projectFilter').value;
			if (filterObjectId == 0) {
				filterObjectId = null;
			}
		}
		applyLoadingStyleFor('loadBlogEntries_Body');
		if (document.getElementById('blogCountMessage') != null) {
			document.getElementById('blogCountMessage').innerHTML = loadingFilteredText;
		}
		offset = 0;
	} else if ((filterType == 'next' || filterType == 'prev')) {
		if (filterType == 'next') {
			offset += 20;
			document.getElementById('nextLoading_T').innerHTML += '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
			document.getElementById('nextLoading_B').innerHTML += '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
		} else if (filterType == 'prev') {
			offset -= 20;
			var prevPostText = document.getElementById('prevLoading_T').innerHTML;
			document.getElementById('prevLoading_T').innerHTML = '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />' + prevPostText;
			document.getElementById('prevLoading_B').innerHTML = '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />' + prevPostText;
			
		} 
		applyLoadingStyleFor('loadBlogEntries_Body');
	} else if(filterType == 'task'){
		blogEntriesLoadedForObject=false;
		itemType = typeID;
		filterObjectId = taskID;
		offset=0;
		showImportantBlogEntries=false;
		showTimeReportedEntries=false;
	} else { // this condition is for deletion of blog entry to set the offset.
		if(nextEntriesCount == 1) {
			if(offset > 20)
				offset -= 20;
			else 
				offset = 0;
		}
	}
	Ext.Ajax.request({
		   url: url +'?module='+moduleId,
		   params: {startDate : strStartDate,blogEntriesLoadedForObject : blogEntriesLoadedForObject, endDate : strEndDate, memberId : memberId, objectId : filterObjectId, module : moduleId, posts : posts, showTimeReportedEntries : showTimeReportedEntries, itemType : itemType, showImportantBlogEntries : showImportantBlogEntries, spaceType : spaceType, offset : offset},
		   method: 'POST',
		   success: function(result, request){
		   	   var responseText = result.responseText.replace(docTypeTag, '');
		   	   document.getElementById('loadBlogEntries').innerHTML = responseText;
		   	   if(posts == '20' && document.getElementById("DaysPostsFor_20") != null)
		   	   		document.getElementById("DaysPostsFor_20").style.backgroundColor = '#a4fdcf';
		   	   var javaScriptCode = responseText;
		   	   var scriptTag = '<script type="text/javascript">';
		       javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
		       eval(javaScriptCode.replace(/\n/g, ''));
		       if(filterType == 'task'){
		    	   document.getElementById('itemTypeFilter').value='taskId';
		    	   document.getElementById('task').value=filterObjectId;
		    	   window.scrollTo(0,findPosY(document.getElementById('FullViewBody__'+weblogEntryId)));
		       }
		       
		       if(document.getElementById("blogCountMessage").innerHTML.indexOf("Blog entries not found") >= 0){
			       if(document.getElementById('hidePictureLink') != null){
			       		if( actionsIconEnabled ) {
			       			document.getElementById('hidePictureLink').innerHTML = '<a href="#" class="disabled"><img hspace=\'0\' border=\'0\' name=\'hidePictureImage\' src=\''+ hidePictureImageOff +'\' title=\''+hidePicturesLink+'\' />'+hidePicturesLink+'</a>';
			       		} else {
			       			document.getElementById('hidePictureLink').innerHTML = '<a href="#" class="disabled">'+hidePicturesLink+'</a>';
			       		}
		       		}
		       		if( actionsIconEnabled ) {
			       		document.getElementById('entryViewLink').innerHTML = '<a href="#" class="disabled"><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'showTitlesImage\' src=\''+showTitlesOnlyImageOff+'\' title=\''+showTitlesOnlyLink+'\'/>'+showTitlesOnlyLink+'</a>';
			       	} else {
			       		document.getElementById('entryViewLink').innerHTML = '<a href="#" class="disabled">'+showTitlesOnlyLink+'</a>';
			       	}
		       }else{
		       		if(document.getElementById('hidePictureLink') != null){
			       		if( actionsIconEnabled ) {
							document.getElementById('hidePictureLink').innerHTML = '<a id=\'linktag\' linktag.onmouseover=\'document.hidePictureImage.src=\''+showHidePictureOver+'\' linktag.onmouseout=\'document.hidePictureImage.src=\''+showHidePictureOn+'\' href=\'javascript:showHidePictures(true);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'hidePictureImage\' src=\''+showHidePictureOn+'\' title=\''+hidePicturesLink+'\'/>'+hidePicturesLink+'</a>';
						} else {
							document.getElementById('hidePictureLink').innerHTML = '<a id=\'linktag\' linktag.onmouseover=\'document.hidePictureImage.src=\''+showHidePictureOver+'\' linktag.onmouseout=\'document.hidePictureImage.src=\''+showHidePictureOn+'\' href=\'javascript:showHidePictures(true);\'>'+hidePicturesLink+'</a>';
						}
		       		}
		       		if( actionsIconEnabled ) {
			       		document.getElementById('entryViewLink').innerHTML = '<a id=\'linktitles\' linktitles.onmouseover=\'document.showTitlesImage.src=\''+showTitlesOnlyImageOver+'\' linktag.onmouseout=\'document.showTitlesImage.src=\''+showTitlesOnlyImageOn+'\' href=\'javascript:showHideEntryText(false);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'showTitlesImage\' src=\''+showTitlesOnlyImageOn+'\' title=\''+showTitlesOnlyLink+'\'/>'+showTitlesOnlyLink+'</a>';
			       	} else {
			       		document.getElementById('entryViewLink').innerHTML = '<a id=\'linktitles\' linktitles.onmouseover=\'document.showTitlesImage.src=\''+showTitlesOnlyImageOver+'\' linktag.onmouseout=\'document.showTitlesImage.src=\''+showTitlesOnlyImageOn+'\' href=\'javascript:showHideEntryText(false);\'>'+showTitlesOnlyLink+'</a>';
			       	}	
				}
				
		      if(typeof(persistedShowHideEntryText) != 'undefined')
			       	showHideEntryText(persistedShowHideEntryText);
		       if(spaceType == 'project') {
		       	   //document.getElementById('hidePictureLink').innerHTML = '<a href="javascript:showHidePictures(true);">Hide Pictures</a></span>&nbsp;<br />';
		       	   //document.getElementById('hidePictureLink').style.display = 'block';
		       	   if(typeof(persistedShowHideEntryText) != 'undefined' && persistedShowHideEntryText == true)
			       	   showHidePictures(isShowPicture);
		       }
		       setLinkTarget();
		       resetLoadingStyleFor('loadBlogEntries_Body');
		       retryFilterRequestCount = 0;
		       createMultipleWorkSubmittedTable();
		   },
		   failure: function(result, response){
				if(retryFilterRequestCount < 5) {
					retryFilterRequestCount++;
			  		setTimeout("applyFilters('apply')", 1000);
			  	}
		   }
		});
}


//To find y position of any dom object 
function findPosY(obj) {
    var curtop = 0;
    try{
	    if(obj.offsetParent)
	        while(1) {
	          curtop += obj.offsetTop;
	          if(!obj.offsetParent)
	            break;
	          obj = obj.offsetParent;
	        }
	    else if(obj.y){
	        curtop += obj.y;
		}
	}catch(err){curtop = 30;}
	return curtop;
}


function validateDates(){
	if(fromDate.isValid() && toDate.isValid()){
		if(startDate != null && startDate != '' && endDate != null && endDate != ''){
			if(isStartDateAfterEndDate(startDate, endDate)){
				extAlert('Error', 'Start date can not be after end date.', Ext.MessageBox.ERROR);
				return false;
			} 
		}
		if(startDate != null && startDate != ''&& (endDate == null || endDate == '' )){
			extAlert('Error', 'Please select end date.', Ext.MessageBox.ERROR);
			return false;
		}
		if((startDate == null || startDate == '') && endDate != null && endDate != ''){
			extAlert('Error', 'Please select start date.', Ext.MessageBox.ERROR);
			return false; 	
		}
		return true;
	} else if(!fromDate.isValid()){
		extAlert('Error', 'Invalid Start Date!', Ext.MessageBox.ERROR);
		return false;
	} else if(!toDate.isValid()){
		extAlert('Error', 'Invalid End Date!', Ext.MessageBox.ERROR);
		return false;
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

//For setting time reported check box label depending on browser
function setTimeReportedChkLable(){
	if(Ext.isIE6){
		timeReportedChkLable = '<div width="30" style="padding-left:20px;font-size:12px;fonr-family:Arial,Helvetica,sans-serif;">Only show blog entries with time reported</div>';
	}else{
		timeReportedChkLable = '<div style="padding-left:3px;font-size:12px;fonr-family:Arial,Helvetica,sans-serif;">Only show blog entries with time reported</div>';
	}
}

function setImportantChkLabel(){
	if(Ext.isIE6){
		importantChkLabel='<div width="30" style="padding-left:20px;font-size:12px;fonr-family:Arial,Helvetica,sans-serif;">Important Blog Entries</div>';
	}else{
		importantChkLabel='<div style="padding-left:3px;font-size:12px;fonr-family:Arial,Helvetica,sans-serif;">Important Blog Entries</div>';
	}
}

// open a filter and change icon
function openCloseFilter(){

	if(document.getElementById('openFilter').style.display == 'none'){
		document.getElementById('openFilter').style.display = '';
		document.getElementById('imageId').src = JSPRootURL+'/images/filter-up.gif';
		document.getElementById('imageId').title = 'up';
	} else {
		document.getElementById('openFilter').style.display = 'none'
		document.getElementById('imageId').src = JSPRootURL+'/images/filter-down.gif';
		document.getElementById('imageId').title = 'down';
	}

}

// to hide cross icons
function filterSetup(){
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		if(document.getElementById('itemTypeFilter:icon') != null){
			document.getElementById('itemTypeFilter:icon').style.display = 'none';
		}
		if(document.getElementById('projectFilter:icon') != null){
			document.getElementById('projectFilter:icon').style.display = 'none';
		}
		if(document.getElementById('task:icon') != null){
			document.getElementById('task:icon').style.display = 'none';
		}
		if(document.getElementById('team:icon') != null){
			document.getElementById('team:icon').style.display = 'none';
		}
	}
					
}

// get formatted start date
function getFormattedDate(dt) {
	return dt.getDate() + '/' + (dt.getMonth() + 1) + '/' + dt.getFullYear();
}
// to get next date of seleted end date
function getNextDate(dt) {
	dt.setDate(dt.getDate()+1);
	return dt.getDate() + '/' + (dt.getMonth() + 1) + '/' + dt.getFullYear();
}

function applyLoadingStyleFor(applyTo) {
	var arrayWH = getWidthHeightOf(applyTo);
	if(arrayWH[0] != 'undefined') {
		blogLaodingScreen.style.height = arrayWH[0] + 'px';
		blogLaodingScreen.style.width = arrayWH[1] + 'px';
	}
    document.getElementById(applyTo).appendChild(blogLaodingScreen);
    
}

function getWidthHeightOf(applyTo) {
	var arrayWH = new Array();
	try {
		if(typeof(window.pageYOffset) == 'number') {
			//Netscape compliant
			arrayWH[0] = document.getElementById(applyTo).getHeight();
			arrayWH[1] = document.getElementById(applyTo).getWidth();
		} else if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
			//IE6 standards compliant mode
			arrayWH[0] = document.getElementById(applyTo).offsetHeight;
			arrayWH[1] = document.getElementById(applyTo).offsetWidth;
		}
	} catch(e) {
		arrayWH[0] = 0;
		arrayWH[1] = 0;
	}
	return arrayWH;
}

function resetLoadingStyleFor(applyTo) {
	if(document.getElementById('blogLaodingScreen') != null)
		document.getElementById(applyTo).removeChild(blogLaodingScreen);
}

// set item type to 'task' if any task is selected from task combo
function setItemType() {
	if(document.getElementById('task') != null){
		if(document.getElementById('task').value != 0 && document.getElementById('itemTypeFilter').value != 0) {
			document.getElementById('itemTypeFilter').value = 'taskId';
		}
	}
}

// set 'All tasks' option in task combo if item type is other than task
function setTask() {
	if((document.getElementById('itemTypeFilter').value != 'taskId' && document.getElementById('itemTypeFilter').value != 0)) {
		if(document.getElementById('task') != null){
			document.getElementById('task').value = 0;
			document.getElementById('task').disabled = true;
		}
	}else{
		if(document.getElementById('task') != null){
			document.getElementById('task').disabled = false;
		}	
	}
}

