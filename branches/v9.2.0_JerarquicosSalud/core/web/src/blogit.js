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
// String prototype function for triming white spaces
String.prototype.trim = function(){
    a = this.replace(/^\s+/, '');
    return a.replace(/\s+$/, '');
}

// Creating div elements for blog-it popup window
var blogPopupScreen = document.createElement('div');
blogPopupScreen.id = 'blogPopupScreen';
blogPopupScreen.style.display = 'block';
var blogPopupDialogue = document.createElement('div');
blogPopupDialogue.id = 'blogPopupDialogue';
blogPopupDialogue.style.display = 'block';
blogPopupDialogue.style.height = 'auto';
blogPopupDialogue.style.width = '690px';
blogPopupDialogue.style.left = window.screen.width/10;
blogPopupDialogue.style.top = window.screen.height/6;

var blogPopupBody = document.createElement('div');
blogPopupBody.id = 'blogPopupBody';
blogPopupBody.style.display = 'block';
blogPopupBody.style.height = 'auto';
blogPopupBody.style.width = '100%';

var blogActionDiv = document.createElement('div');
blogActionDiv.id = 'blogActionDiv';
blogActionDiv.style.left = (window.screen.width/2) +'px';//'390px';
blogActionDiv.style.top = '0px';
blogActionDiv.style.background = '#FEE796';
blogActionDiv.style.position = 'absolute';
blogActionDiv.style.zIndex = 10000;
blogActionDiv.style.height = '22px'; 
blogActionDiv.style.width = '60px';
blogActionDiv.style.textAlign = 'center';
blogActionDiv.style.color =  '#0033FF';
blogActionDiv.innerHTML = '<h3 style="padding-top: 0.5em;">Saving...</h3>';

var setHistoryPopupBody = document.createElement('div');
setHistoryPopupBody.id = 'setHistoryPopupBody';
setHistoryPopupBody.style.display = 'block';
setHistoryPopupBody.style.height = 'auto';
setHistoryPopupBody.style.width = '100%';

var errorTitle = "Error";
var showtimediv = false;
var objectId;
var contentPanel, commentContentPanel;
var blogNotExist = true;
var weblogEntryId;
var subject;
var content;
var timeSheetTable = '';
var workDate = null;
var fromTime = null;
var toTime = null;
var months = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
var days = new Array('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday');
var timeValues;

var important = false;
var workSubmitted = 0;
var changedEstimate;
var htmlText = '';
var noOfBlogEntryLink = '';
var needRefresh = false;
var setNow = false;
var openFor = '';
var objectNameLength = 24;
var screenTop = 120;

// variable declarations related to blog it tabbed window
var blogItWin, blogDispPanel, popUpPanel, commentTabPanel, editTabPanel, historyTabPanel, tabs;
var webLogId, simpleFlag; 
var blogItWinHeight = 435;
var blogItWinHeightForTimesheet = 528;
var blogItWinDefaultHeight = 435;
var objType = '';
var htmlForCommentTab =''; 
var blankHtmlText = '';
var editHtmlText = '';
var showWindowForPopup = true;
var flagForNewComment = true;
var flagForBlogPageHandle = true;
var flagForHtmlEditor = false;
var flagForaddComment = false;
var flagForUpdateComment = false;
var flagForBlogNewComment = false;
var flagForBlogEdit = false;
var flagForHandlingRecentTab = false;
var selectedTask = '';
var selectedTaskOf = '';
var selectedTaskForToolTip = '';
var selectedTaskOfForToolTip = '';
var timesheetDivForWindow = '';
var isShowPicture = false;
var persistedShowHideEntryText;
var blogUserDate;
var recentTabActive;
var currentUserName;
var workCapturedInfoForTask;
var workNotCapturedInfoForTask;
var workCapturedInfoForAllassignments;
var workNotCapturedInfoForAllAssignments;
var retryLastBlogitRequestCount = 0;
var retryGCSDRequestCount = 0;
var expandIconTooltip;
var collapseIconTooltip;

// Blog-it function to open a popup for posting blog entry for selected object
function checkBlogitParameters() {
	if (typeof(openFor) != 'undefined' && openFor == 'blogPage') {
		showPopup('blogPage');
	} else if(typeof(blogItFor) == 'undefined' && typeof(noSelectionErrMes) == 'undefined' || (typeof(blogItFor) != 'undefined' && blogItFor == 'formDataItemsBusiness')){
		getCurrentSpaceDetails();
	} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'assignments'){
		if ((theForm.objectID) && (verifySelectionForField(theForm.objectID, "single", noSelectionErrMes))) {
			if(theForm.objectID.length == null){
				if(theForm.objectID.checked == true) {
					objectId = theForm.objectID.value;
				}
			} else {
				for (var i = 0; i < theForm.objectID.length; i++) {
					if(theForm.objectID[i].checked == true) {
						objectId = theForm.objectID[i].value;					
						break;
					}
	            }
            }
            showPopup();
		}
	} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'PersonalPortFolio'){

		if(typeof(selectedProjectId) != 'undefined'){ // defined in portfolio/ProjectPortfolio
				objectId = selectedProjectId; 
		}else{
			objectId = theForm.elements["selected"];
		}
		var objectIdList = null;
		//if scroll channel content is set to the portfolio project list take object id from the iframe 
		if(!objectId && document.getElementById('channelIFrame') != null){
			objectIdList = getSelectedProjectIdFromIframe(); // defined in portfolio/PersonalPortfolio.jsp
		}

		if(objectId != null || objectIdList != null){ //Check for the empty project list
			if(typeof(selectedProjectId) == 'undefined'){
				if(objectId) {
					objectId = getSelection(theForm);
				} else if(objectIdList){ // Take object id from the iframe of scroll channel content
					objectId = objectIdList.value;
					for (var i = 0; i < objectIdList.length; i++) {
						if (objectIdList[i].checked == true) {
							objectId = objectIdList[i].value;
							break;
						}
					}
				}
			}else {
				if(selectedProjectId == '')
					objectId = null;	
			}
			if(objectId)//check if the object id is selected from the form
				showPopup();
			else //set message for iframe
				extAlert(errorTitle, selectProject , Ext.MessageBox.ERROR);
		} else { //Common error message id there are no project in the portfolio list
			extAlert(errorTitle, noProjectInList , Ext.MessageBox.ERROR);
		}
	} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'myAssignments' ){
		//If no data in assignment grid
		if (firstAssignmentID == ''){
			extAlert('Error', noProjectTaskAvailableToBlogMsg, Ext.MessageBox.ERROR);
		} else if (typeof(assignmentTreeNodeId) != 'undefined' && assignmentTreeNodeId != 0){
			objectId = assignmentTreeNodeId;
			if ((typeof(objectType) != 'undefined' && objectType == 'task' || objectType == 'form_data')
					&& (typeof(workSpaceType) != 'undefined' && (workSpaceType != 'business' || objectType == 'project'))) {
				blogItWinHeight = blogItWinHeightForTimesheet; 	// for blog it window
				getTimeSheetEntries(objectId, scrollType);
        	   	showPopup();
			} else if((typeof(objectType) != 'undefined' && objectType == 'project' || objectType == 'summary' || objectType == 'temporary_task' || objectType == 'meeting' || objectType == 'person')
					&& (typeof(workSpaceType) != 'undefined' && (workSpaceType != 'business' || objectType == 'project'))) {
				blogItWinHeight = blogItWinDefaultHeight; // for blog it window
				showPopup();			
			} else {
				extAlert(errorTitle, blogitNotSupportedForObject , Ext.MessageBox.ERROR);
			}
		} else {
	         extAlert(errorTitle, selectRowBeforePostingBlog , Ext.MessageBox.ERROR);
		}
	} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'workPlanProject') {
		if (verifySelection(theForm, 'single', noSelectionErrorMessage)) {
		    var workPlanId;
			workPlanId = getSelection(theForm);//scheduleView.getSelectedRowIds();
			objectId = workPlanId;
				if(objectType == 'task') {
					blogItWinHeight = blogItWinHeightForTimesheet; // for blog it window
					getTimeSheetEntries(objectId, scrollType);
					showPopup();
				}else if(objectType == 'project' || objectType == 'summary') {
					showPopup();
				}else {
					extAlert(errorTitle, blogitNotSupportedForObject , Ext.MessageBox.ERROR);
				} 
			}
	} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'personalProfile'){
		if(typeof(spaceId) != 'undefined' && spaceId != 0){
			objectId = spaceId;
			showPopup();
		}
	} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'taskView'){
		objectId = taskId;
		blogItWinHeight = blogItWinHeightForTimesheet; // for blog it window
		getTimeSheetEntries(objectId, scrollType);
		showPopup();
	} else if(typeof(blogItFor) != 'undefined' && (blogItFor == 'formDataItemsProject' || blogItFor == 'formDataItemsPersonal')) {
		var formRecord = theForm.selected;
		if(formRecord) {
			if(!formRecord.length) {
				if(formRecord.checked){
					objectId = formRecord.value;				
				}
			} else {
				for (var formRecordIndex = 0; formRecordIndex < formRecord.length; formRecordIndex++){
					if (formRecord[formRecordIndex].checked == true) {
						objectId = formRecord[formRecordIndex].value;
						break;
					}
				}
			}			
		}	
		if(objectId != null && objectId != 0) {
			if(typeof(objectType) != 'undefined' && objectType == 'form_data'){
				if(typeof(blogItFor) != 'undefined' && blogItFor == 'formDataItemsProject' ) {//load timesheet entry for project space
					blogItWinHeight = blogItWinHeightForTimesheet; // for blog it window
					getTimeSheetEntries(objectId, scrollType);
				}
				showPopup();
			}
		} else {
			extAlert(errorTitle, selectFormRecordBeforePostingBlog , Ext.MessageBox.ERROR);
		}
	} else if(typeof(blogItFor) != 'undefined' && (blogItFor == 'project')) {
			objectId = spaceId;
			balloon.hideTooltip(1);
			showPopup();
	} else if(typeof(blogItFor) != 'undefined'  && (blogItFor == 'PersonalPortFolio')){
		// This is to check if the selcted object is folder i.e. doc_container
		// instead of document while posting blog from doc vault
		if(theForm.isRoot != undefined) {
			if(typeof(getSelection(theForm)) != 'undefined'){
				if(isObjectTypeOf (getSelection(theForm), 'bookmark')){
					extAlert(errorTitle, canNotPostBlogForUrl , Ext.MessageBox.ERROR);
					return;
				}
				else if(!isObjectTypeOf (getSelection(theForm), 'document')){
					extAlert(errorTitle, canNotPostBlogForFolder , Ext.MessageBox.ERROR);
					return;
				}
			}else{
				extAlert(errorTitle, selectDocumentRecordBeforePostingBlog , Ext.MessageBox.ERROR);
				return;
			}
	   	} else {
	   		getCurrentSpaceDetails();
			//extAlert(errorTitle, 'Currently BlogIt is not supported for this page' , Ext.MessageBox.ERROR);
			//return;
		}   		
	     // Ensure only one item selected
	    if (verifySelection(theForm, 'single', noSelectionErrMes)) {
		    objectId = getSelection(theForm);
		    showPopup();
		} else if(verifySelection(theForm, 'multiple', noSelectionErrMes)){
			extAlert(errorTitle, multiSelectionErrMes , Ext.MessageBox.ERROR);
		} else {
	         extAlert(errorTitle, noSelectionErrMes , Ext.MessageBox.ERROR);
		}
	} else if(typeof(blogItFor) != 'undefined' && objectId != '' && (blogItFor == 'meetingForProject' || blogItFor == 'meetingForPersonal') ){
		blogItWinHeight = blogItWinDefaultHeight;
		showPopup();
	} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'wikiproject' && wikiPageId != '') {
		objectId = wikiPageId;
		showPopup();
	} else if(typeof(blogItFor) != 'undefined' && (blogItFor == 'documentProject' || blogItFor == 'documentPersonal' || blogItFor == 'forTrashcan')){
		if (typeof(theForm) != 'undefined' && theForm.isRoot != undefined) {
			if (typeof(getSelection(theForm)) != 'undefined') {
				if (isObjectTypeOf (getSelection(theForm), 'bookmark')) {
					extAlert(errorTitle, canNotPostBlogForUrl , Ext.MessageBox.ERROR);
					return;
				}
				else if(!isObjectTypeOf (getSelection(theForm), 'document')) {
					extAlert(errorTitle, canNotPostBlogForFolder , Ext.MessageBox.ERROR);
					return;
				}
				if (verifySelection(theForm, 'single', noSelectionErrMes)) {
				    objectId = getSelection(theForm);
				    showPopup();
				}
			} else{
				extAlert(errorTitle, selectDocumentRecordBeforePostingBlog , Ext.MessageBox.ERROR);
				return;
			}
	   	} else if( typeof(format) != 'undefined' ){
			if( format != 'File Folder' && format != 'URL' ){
				if(objectId != null || typeof(objectId) != 'undefined') {
					showPopup();
				} else {
					 extAlert(errorTitle, selectRowBeforePostingBlog , Ext.MessageBox.ERROR);
				}
			} else {
				extAlert(errorTitle, blogitNotSupportedForObject , Ext.MessageBox.ERROR);
			}
		} else {
			extAlert(errorTitle, selectItemBeforePostingBlog , Ext.MessageBox.ERROR);
		}
	} else {
		getCurrentSpaceDetails();
	}
}

function blogit() {
	if(blogItWin == null && window == top) {
		checkBlogitParameters();
	}
}

function showPopup(openForPage){
  	flagForHandlingRecentTab = false; // for blog it window
	if(objectId != 'null' && objectId != ''){
		// Html text for popup window
		var htmlText = '', subText = '';		
		openFor = openForPage;	
		var leftPos, topPos;
		if(!showWindowForPopup) {
			htmlText += '<div id="dialog">'; 
			htmlText += '	<h1>New Blog Entry</h1>';
			htmlText += '	<div class="close">';
			htmlText += '		<a href="javascript:hidePopup();">';
			htmlText += '			<img border="0" alt="x" src="'+JSPRootURL+'/images/menu/close.gif"/>';
			htmlText += '		</a>';
			htmlText += '	</div>';
			htmlText += '</div>';
		}
		htmlText += '<div id="entry-description"><span id ="blog-it_UserDate" class="blogDate">';
		if (typeof(blogPopupUserDate) != 'undefined' &&  blogPopupUserDate != null && blogPopupUserDate != '') {
			htmlText += blogPopupUserDate;
		}
		htmlText += '</span><br/>';
		htmlText += '<span id="blog-it_selectedTaskId" class="task"></span>';	
		if(typeof(workSpaceType) != 'undefined' && workSpaceType != '' 
			&& (workSpaceType == 'project' || workSpaceType == 'person'
			|| workSpaceType == 'Project' || workSpaceType == 'Person')){
			workSpaceType = capitaliseMe(workSpaceType);
		} else {
			workSpaceType = 'Project';
		}
		 
		if((typeof(blogItFor) != 'undefined' &&  typeof(objectType) != 'undefined') 
			&& (blogItFor == 'myAssignments') && objectType != 'project' && objectType != 'person') {
			var mainWorkSpaceType = workSpaceType;
			htmlText += '<span id="blogProjectTitleId" class="blogProjectTitle" title="' + mainWorkSpaceType + ': ' + workSpaceName + '">';
			htmlText +=	mainWorkSpaceType+': '+getSubString(workSpaceName, objectNameLength)+'</span>';
		} else if(typeof(objectName) != 'undefined' &&  typeof(objectType) != 'undefined' && typeof(blogItFor) != 'undefined' 
			&& blogItFor != 'formDataItemsProject' &&  blogItFor != 'formDataItemsPersonal' && objectType != 'person' && objectType != 'meeting' 
			&& blogItFor != 'taskView') {
			htmlText += '<span id="blogProjectTitleId" class="task" title="'+workSpaceType+': '+objectName+'">'+workSpaceType+': '+getSubString(objectName, objectNameLength)+'</span>';
		} else {
			htmlText += '<span id="blogProjectTitleId" class="blogProjectTitle"></span>';
		}
		if(typeof(blogItFor) != 'undefined' && (blogItFor == 'currentSpace')) {
			htmlText += '<span id="addToSapce" > Adding blog entry to ' + objectType + ' blog.</span>';	
		}
		if(!showWindowForPopup) {
			if(typeof(openForPage) == 'undefined' || openForPage != 'blogPage') {
				htmlText += '<br/><span id="noOfBlogs">';
				htmlText += ' View Existing Blogs. Loading... <img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" /></span> ';
			}
		} else {
			htmlText += '<br/><span id="noOfBlogs"></span>';
		}
		htmlText += '</div>';
		
		subText += '<tr><td/><td align="left" colspan="2"><table width="100%"><tr>';
		// including important checkbox for blog entry on blog page and my assignments page
		subText += '<td class="table-content-done" nowrap="nowrap">&nbsp;';
		subText += '<input type="checkbox" id="isImportant" onclick="setImpFlag();"/>&nbsp;Important';
		subText += '</td><td nowrap="nowrap" id="importantMsg" class="show-message" style="display: none;">&nbsp;Explain why this entry is important</td>';
		subText += ' <td align="right" style="padding-left:5px" nowrap="nowrap"> <input id="new-blog-it" type="button" value="Submit" onclick="saveEntry();" />';		
		subText += ' &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
		subText += ' <input type="button" value="Cancel" onclick="hidePopup();" /> </td></tr></table>';
	
		leftPos = window.screen.width/5;	
		if(typeof(blogItFor) != 'undefined' && (blogItFor == 'myAssignments' || blogItFor == 'formDataItemsProject' || blogItFor == 'taskView' || blogItFor == 'workPlanProject')) {
			topPos = window.screen.height/17;
			setNow = true;	
			screenTop = 30;	
			if(typeof(objectType) != 'undefined' && (objectType == 'task' || objectType == 'form_data' )) {			
				htmlText += '<div id="blog-it_timeSheet" style="width: 100%;"><label style="padding-left:15px;font-size:12px;">Loading Timesheet... </label><img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" /></div>';
			}
		} else {
			topPos = window.screen.height/12;		
			setNow = true;
		}
		blogPopupDialogue.style.top = topPos+"px";
		blogPopupDialogue.style.left = leftPos+"px";
		showLoadingDiv('Loading...');

		htmlText += ' <table align="left" cellpadding="5" cellspacing="5" > ';
		htmlText += ' <tr> <td class="message-subject" nowrap="nowrap" style="padding-left: 30px" align="right"> Subject: </td> <td> <input id="blog-it_title" maxlength="240" name="title" size="60" type="text" value="">  </td> </tr>';
		htmlText += ' <tr> <td valign="top" class="message-subject" nowrap="nowrap" style="padding-left:5px" align="right"> Message: </td> <td> <div id="contentTextareaPosition"></div> </td> </tr>';
		htmlText +=  subText + '</table>';
		
		if(!showWindowForPopup){
			window.document.body.style.overflowY = 'hidden';
			window.document.body.style.overflow = 'hidden';
		}
		if(!showWindowForPopup){
			if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
				document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
			}	
			hideHtmlSelectTags();
		}
		
		if(navigator.userAgent.toLowerCase().indexOf("firefox/2.0") >= 0) {        
	    	blogPopupDialogue.style.overflow = 'auto';
	    }
	 	if(!showWindowForPopup){
	 		document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);	
		    document.getElementsByTagName('body')[0].appendChild(blogPopupDialogue);		
		    document.getElementById('blogPopupDialogue').appendChild(blogPopupBody);	
		    document.getElementById('blogPopupBody').innerHTML = htmlText;
	    } else {
	    	blogPopupScreen.style.opacity = 0;
	    	blogPopupScreen.style.filter = 'alpha(opacity:0)';
	    	document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);
	   		showTabPanel(htmlText);
	    }
	    
	// Initializing Ext QuickTips before creating htmleditor
	if(!showWindowForPopup){
		Ext.QuickTips.init();
		// Creating rich text box for contents
		contentPanel = new Ext.FormPanel({
			border: false,
			width: '100%',
			items: [{
				xtype: 'htmleditor',
				id: 'content',
				fieldLabel: 'Content',
				width: '600px',
				height: 200,
				anchor: '95%',
				enableFontSize: false,
				style: 'border: thin; border-color: #33BDFF;',
				renderTo: document.getElementById('contentTextareaPosition'),
				listeners: {			
					'render': function(component){
						var size = component.getSize();
						if(showWindowForPopup){
							component.setSize(size.width+(642-size.width), 200);
						} else {
							component.setSize(size.width+(550-size.width), 200);
						}	
						}
					}
				}]
			});
			if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
				if(contentPanel != null && contentPanel != 'undefined'){
			   		contentPanel.getComponent('content').focus(true);
			   	}
			}
		}
		if(document.getElementById('blog-it_selectedTaskId') != null){
			document.getElementById('blog-it_selectedTaskId').innerHTML = '';
			selectedTaskForToolTip = '';
		}
		if(openForPage != 'blogPage'){
			// getting number of blog entries if exist for selected object
			getBlogEntriesByObjectId(objectId);
		}
	} else {
		extAlert(errorTitle, blogitNotSupportedToThisPage , Ext.MessageBox.ERROR);
		return;
	}
}

function reInitializeValues(){
	htmlText = '';
	scrollType = 'day';
	timeSheetTable = '';	
	workCaptureHTMLText = '';
	timesheetHTMLText = '';
	workRemainingDivText = '';
	setPercentComplete = false;
	validTimeSheet = false;
	validPercentChange = true;
	dayValue = 0;
	screenTop = 120;
	flagForUpdateComment = false;
	flagForBlogNewComment = false;
	flagForBlogEdit = false;
}

// Method to hide popup window
function hidePopup(){
	reInitializeValues();
  	if(showWindowForPopup && typeof tabs != 'undefined' && tabs.getActiveTab().getId() == 'blog-it-comments') {
		if((openFor == 'blogPage') 
			|| (blogItFor == 'myAssignments' || blogItFor == 'workPlanProject' || blogItFor == 'PersonalPortFolio')
			|| (typeof(needToLoadBlogEntries) != 'undefined' && needToLoadBlogEntries && blogItFor == 'taskView')
			|| (blogItFor == 'documentProject' || blogItFor == 'documentPersonal' || blogItFor=='forTrashcan') || blogItFor == 'timesheet'){
			document.getElementsByTagName('body')[0].removeChild(blogPopupScreen);
			if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
				showHtmlSelectTags();
			}
			blogItWin.destroy();
			if(blogItWin != null){blogItWin = null;}
		} else {
			tabs.hideTabStripItem(2);
			tabs.activate('blogs');
		}
	} else {
		if(!showWindowForPopup) {
			document.getElementsByTagName('body')[0].removeChild(blogPopupDialogue);
		}else{
			blogItWin.destroy();
			if(blogItWin != null){blogItWin = null;}
		}

		if(!flagForBlogPageHandle){
			blogPopupDialogue.style.width = '690px';
		}

		blogPopupBody.innerHTML = loadingImage;	
		if (blogPopupScreen) document.getElementsByTagName('body')[0].removeChild(blogPopupScreen);
		if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0 && !showWindowForPopup) {
			if(typeof(blogItFor) != 'undefined' && blogItFor != 'project'){
				if(blogItFor == 'myAssignments'){
					document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
				} else if( blogItFor == 'personalProfile' ) {
					document.getElementsByTagName('body')[0].style.overflow = 'hidden';
				} else {
					document.getElementsByTagName('body')[0].style.overflowY = 'scroll';			
				}
			} else {
				document.getElementsByTagName('html')[0].style.overflowY = 'scroll';
				document.getElementsByTagName('body')[0].style.overflowY = 'hidden';
				document.getElementsByTagName('html')[0].style.overflowX = 'auto';
			}
			showHtmlSelectTags();
		}
		
		blogPopupScreen.style.display = 'block';
		if(!flagForBlogPageHandle){
			blogPopupDialogue.style.display = 'block';
		}
   }
}

// Hide combo boxes on page before showing the popup box
function hideHtmlSelectTags(){
	var selectTags = document.getElementsByTagName('select');
	if(selectTags != null && selectTags.length > 0){
		for(var index = 0; index < selectTags.length; index++ ){
			selectTags[index].style.visibility = 'hidden';
		}
	}
}

// Show the combo boxes again after hiding the popup box
function showHtmlSelectTags(){
	var selectTags = document.getElementsByTagName('select');
	if(selectTags != null && selectTags.length > 0){
		for(var index = 0; index < selectTags.length; index++ ){
			selectTags[index].style.visibility = 'visible';
		}
	}
}

// Check for valid content in rich textbox
function isValidContent(contentPanel){
	validateHtmlContent(contentPanel);
	var contentResult = '';
	// Pattern to search html tags 
	var htmlPattern = /<[^>]*>/g;
	if(contentPanel.getId() != 'comment'){
 		contentResult = contentPanel.getComponent('content').getRawValue().trim().replace(htmlPattern, "");
 	} else {
 		contentResult = contentPanel.getComponent('contentForComment').getRawValue().trim().replace(htmlPattern, "");
 	}	
	
	// Pattern to search html entities
	var htmlEntityPattern = /&[#]*[\w|\d]*;/g;
	contentResult = contentResult.replace(htmlEntityPattern, "");
	
	if(contentPanel.getId() != 'comment'){
		content = contentPanel.getComponent('content').getRawValue().trim();
	}else{
		content = contentPanel.getComponent('contentForComment').getRawValue().trim();
	}	
	
	// Removing white space html entities like &nbsp;, <br> if nothing else exists in contents
	while(content.length > 4 && content.substring(content.lastIndexOf('<br>'),content.length).trim() == '<br>'){
		while(content.length > 4 && content.substring(content.lastIndexOf('<br>'),content.length).trim() == '<br>'){
		    content = content.substring(0,content.lastIndexOf('<br>'));
		}
		while(content.length > 6 && content.substring(content.lastIndexOf('&nbsp;'),content.length).trim() == '&nbsp;'){
		   content = content.substring(0,content.lastIndexOf('&nbsp;'));
		}
	}
	
	// Check special character code 160 in safari browser which has been inserted 
	// in html editor when space button pressed  some times this character inserted instead of 32 (i.e. char code for space)
	if(navigator.userAgent.toLowerCase().indexOf("safari") >= 0) {
		var tempContentResult = contentResult;
		var otherCharExist = false;
		contentResult = '';
		for(var charIndex = 0; charIndex < tempContentResult.length; charIndex++ ){
			if(tempContentResult.charCodeAt(charIndex) != 160){
				contentResult += tempContentResult.charAt(charIndex);
			} else if (tempContentResult.charCodeAt(charIndex) != 160 && tempContentResult.charCodeAt(charIndex) != 32) {
				otherCharExist = true;
			}
			if(otherCharExist){
				contentResult = tempContentResult;
				break;
			}
		}
	}
	
	if(contentResult.trim() == '' || content.trim() == ''){		
		return false;
	}
	return true;
}

// Check for valid blog entry i.e. subject, content and time sheet values
function isValidBlogEntry() {
	if(typeof(blogItFor) != 'undefined' && typeof(objectType) != 'undefined' 
		&& (blogItFor == 'myAssignments' || blogItFor == 'formDataItemsProject' 
				|| blogItFor == 'taskView' || blogItFor == 'workPlanProject') 
					&& (objectType == 'task' || objectType == 'form_data')&& !setPercentComplete){
		if(validTimeSheet){
			workSubmitted = 0;
			for (var i = 0; i < timeValues.length; i++) {
				var element = document.getElementById("dateupdX" + objectId + "X" + timeValues[i]);
				var oldElement = document.getElementById("wCF_" + objectId + "X" + timeValues[i]);
				var newValue = defaultDecSeparator(element.value, dec_separator);
				var oldValue = defaultDecSeparator(oldElement.getAttribute("ov"), dec_separator);
				var value = newValue - oldValue;
				if(newValue.trim() != "" && value != 0 && !isNaN(newValue.trim())) {
					if(typeof(dec_separator) != 'undefined' && dec_separator != null && dec_separator != '.') {
						value = defaultDecSeparator(value, dec_separator);
					}
					workSubmitted = parseFloat(workSubmitted) + parseFloat(value);
				}
			}
			if(typeof(dec_separator) != 'undefined' && dec_separator != null) {
				workSubmitted = parseFloat(workSubmitted.toFixed(2));
			}
		}
		if(document.getElementById("timesetdiv") != null && document.getElementById("timesetdiv").style.display != 'none' && document.getElementById("estRemaining").value.trim() != ''){
			changedEstimate = document.getElementById("totEstimated").value.trim()+' hrs';
		}
	}
	subject = document.getElementById('blog-it_title').value;
	
	// Check for content is not blank	
	return isValidContent(contentPanel);
}

//This method extracts the html tags used and find the appropriate closing tag, 
//returns false if missing closing tag 
//validate the HTML content and close the missing end tags
function validateHtmlContent(contentBox) {
	var htmlContent = "";
	if(contentBox.getId() != 'comment') {
		if(contentBox.getComponent('content').sourceEditMode)
			htmlContent = contentBox.getComponent('content').getRawValue().trim();
		else return;
	} else {
		if(contentBox.getComponent('contentForComment').sourceEditMode)
			htmlContent = contentBox.getComponent('contentForComment').getRawValue().trim();
		else return;
	}
	var tags = "";
	if(htmlContent) {
		htmlContent = htmlContent.replace(/<br>/g, "");
		tags = htmlContent.match(/<[^/](([w]*)[w]*)[^>]*>(?!.*<[^/]2>)/gi); // get all the tags use in html content
	}
	if(tags != null && tags != "") {
		tags = tags + "";
		var tagsArr = tags.split(',');
		var missingTags = '';
		var lastOpenTagOfMissingTg = "";
		var foundMissingTag  = false;
		for(var openTg = 0; openTg < tagsArr.length; openTg++) {
			if(!(tagsArr[openTg].indexOf('<img ') > 0 || tagsArr[openTg].indexOf('<span ') > 0)) {
			    var closingTg = tagsArr[openTg];
			    if(closingTg.indexOf(" ") > 0) {
			    	closingTg = closingTg.substring(0, closingTg.indexOf(" "))+">"
			    }
		    	closingTg = closingTg.replace('<', '</');
			    if(!(htmlContent.indexOf(closingTg) > 0)) {
			    	lastOpenTagOfMissingTg = tagsArr[openTg];
			    	missingTags += closingTg;
			    	foundMissingTag = true;
			    }
			    // make a string minimum to search next missing closing tag of respective open tag
			    htmlContent = htmlContent.substring((htmlContent.indexOf(closingTg) + closingTg.length), htmlContent.length);
			}
		}
		if(foundMissingTag) {
			addMissingClosingTags(missingTags, lastOpenTagOfMissingTg, contentBox);
		}
	}
	return true;
}

// Add the missing closing tags before top parents closing tag
function addMissingClosingTags(missingTags, lastOpenTagOfMissingTg, contentBox) {
	if(missingTags != null && missingTags != "" && lastOpenTagOfMissingTg != null && lastOpenTagOfMissingTg != "") {
		var htmlContent = "";
		if(contentBox.getId() != 'comment') {
			htmlContent = contentBox.getComponent('content').getRawValue().trim();
		} else {
			htmlContent = contentBox.getComponent('contentForComment').getRawValue().trim();
		}
		var placeHolder = htmlContent.substring(htmlContent.indexOf(lastOpenTagOfMissingTg), htmlContent.length);
		if(placeHolder.indexOf('</') > 0) { //if found first parents closing tag, insert all closing tags there and append it with content
			var completedTags = placeHolder.replace('</', missingTags + '</');
			htmlContent = htmlContent.replace(placeHolder, completedTags);
		} else { // otherwise append closing tag at the end
			htmlContent += missingTags;
		}
		// finally replace the original content with modified content with all missing closing tags
		if(contentBox.getId() != 'comment') {
			contentBox.getComponent('content').setValue(htmlContent);
		} else {
			htmlContent = contentBox.getComponent('contentForComment').setValue(htmlContent);
		}
	}
}

// method to check if blog entry is without any subject 
// then get confirmation of the user before saving blog entry
function saveEntry() {
	if(document.getElementById('blog-it_title').value.trim() == '' && isValidContent(contentPanel)) {
	     Ext.MessageBox.confirm("Confirm", "Post blog without a subject?", function(btn) { if(btn == 'yes'){ saveEntryAfterConfirm(); return true; } else return false; });
	} else {  //to check matching closing end tag in content
		saveEntryAfterConfirm();
	} 
}

// Saves the blog entry
function saveEntryAfterConfirm() {
	document.getElementById('new-blog-it').disabled = true;
	// Check for blog entry is valid or not and save the blog entry
	if(!isValidBlogEntry()) {
		if(typeof(validationMessageForBlogComment) == 'undefined'){
			validationMessageForBlogComment = validationMegForBlogComment;
		}
		extAlert(errorTitle, validationMessageForBlogComment,  Ext.MessageBox.ERROR);
		document.getElementById('new-blog-it').disabled = false;
	} else if(!blogNotExist) {
	    // save blog entry from Assignments page with work capture or work remaining div changes if made
		if(typeof(blogItFor) != 'undefined' && typeof(objectType) != 'undefined' && (blogItFor == 'myAssignments' || blogItFor == 'formDataItemsProject' || blogItFor == 'taskView' || blogItFor == 'workPlanProject') && (objectType == 'task' || objectType == 'form_data' )) {
			if(document.getElementById("workDone") != null) {
				workDone = document.getElementById("workDone").checked;
			}
		    // submit timesheet if any work is captured i.e validTimeSheet
			if(typeof document.getElementById("timeWorkedDiv") != 'undefined' && document.getElementById("timeWorkedDiv") != null && workSubmitted != 0) {
				if(validTimeSheet) {					
					blogPopupScreen.style.display = 'none';
					blogPopupDialogue.style.display = 'none';
					showLoadingDiv(savingblog);
					submitTimeSheet(objectId);
				} else {
					document.getElementById('new-blog-it').disabled = false;
					return false;
				}
			} 
			// to submit work remaining percentages changed from work remaining div
			else if(document.getElementById("timeWorkedDiv") != null && document.getElementById("timesetdiv").style.display != 'none') {
				showLoadingDiv(savingblog);
				if(document.getElementById("estRemaining").value.trim() != '' && validTimeSheet) {					
					percentChanged(objectId, 'submit');
				} else {
					saveBlogEntry();
				}
			}
			// to submit work done value if done checkbox is selected
			else if(workDone){
				if(parseInt(workUpdated.replace(/hrs/,'').trim()) > 0){
					// flag is set to indicate whether to refresh the my assignment list? while hidepopup()
					needRefresh = true;
					showLoadingDiv(savingblog);
					// make assignment 100% complete and save the blog entry
					if(workDone) percentChanged(objectId, 'done');
				} else {
					document.getElementById("blogErrorLocationID").innerHTML = canNotEnterWorkComplete;
				}
			}
			else if(typeof document.getElementById("timeWorkedDiv") == 'undefined' || document.getElementById("timeWorkedDiv") == null){
				showLoadingDiv(savingblog);
				saveBlogEntry();
			}
			// to save blog entry without any work capture or work remaining div changes
			else {
				if(!validTimeSheet){
					document.getElementById('new-blog-it').disabled = false;
					return false;
				}
				showLoadingDiv(savingblog);
				saveBlogEntry();
			}
		}
		// to save blog entry from other pages other than Assignments page
		else {
			showLoadingDiv(savingblog);
			saveBlogEntry();
		}
	} else if(openFor == 'blogPage' ) {
		updateEntry(null);
	} else {
		extAlert(errorTitle, blogActivationMsg, Ext.MessageBox.ERROR);
	}
}

// save blog entry after all related validations
function saveBlogEntry(){	
	hidePopup();
	if(content.toLowerCase().indexOf('<style') != -1){
		content = getStylesEscapedFromContent(content);
	}
	// Saving blog entry by ajax request
	var url = JSPRootURL +'/blog/AddWeblogEntry/SaveBlogEntry'+(new Date()).getTime();
	Ext.Ajax.request({
	   url: url+'?module='+moduleId,
	   params: { module : moduleId, subject : subject, content : content, spaceId : spaceId,
				 objectId : objectId, isImportant : important, workSubmitted : workSubmitted, changedEstimate : changedEstimate },
	   method: 'POST',
	   success: function(result, request){
	   	   if(typeof document.getElementById('blogActionDiv') != 'undefined'){
	  	   	   removeLoadingDiv();
	  	   }
		   workSubmitted = 0; 
		   changedEstimate = null;
		   important = false;
		   if(result.responseText == "true") {
				var urlLocation = self.document.location + "";
				if(typeof(blogItFor) != 'undefined' && blogItFor == 'myAssignments'){
					loadBlogEntriesForAssignment(blogItFor, needRefresh, assignmentTreeNodeId, childTaskList, taskSpaceId);
				} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'personalProfile'){						
					loadLastBlogEntry();
				} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'taskView'){
					if(typeof(needToLoadBlogEntries) != 'undefined' && needToLoadBlogEntries){
						self.location = self.location;
					}
				} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'workPlanProject'){
					if(needRefresh){
						reloadTaskList();
						needRefresh = false;
					}
					loadBlogsForSelectedRow(objectId, objectId, spaceId);
				} else if((typeof(blogItFor) != 'undefined' && (blogItFor == 'project' || blogItFor == 'currentSpace')) || urlLocation.indexOf('project/DirectorySetup.jsp') > 0 ){
					getLastBlogIts(spaceId);
				} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'activity'){
					applyFilter('apply'); 
				} else if(typeof(blogItFor) != 'undefined'  && (blogItFor == 'documentProject' || blogItFor == 'documentPersonal' || blogItFor=='forTrashcan') && typeof(currentSpaceId) != 'undefined'){
					loadBlogEntriesForObject('', false, objectId, objectId, currentSpaceId);
				} else if(typeof(blogItFor) != 'undefined' && blogItFor == 'PersonalPortFolio'){
					loadBlogsForSelectedRow(selectedProjectId);
				}  
		   } else {
			   extAlert(errorTitle, 'Saving failed... Please try again.', Ext.MessageBox.ERROR);
		   }		   
	   },
	   failure: function(result, response){
		   extAlert(errorTitle, 'Saving failed... Please try again.', Ext.MessageBox.ERROR);
		   removeLoadingDiv();
	   }
	});
}

function updateWin(url, width, height, title){
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		if(!showWindowForPopup){
			document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
		}	
		hideHtmlSelectTags();
	}	
	
	if(!showWindowForPopup){	
		window.document.body.style.overflow = 'hidden';
		document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);		
		document.getElementsByTagName('body')[0].appendChild(blogPopupDialogue);		
		document.getElementById('blogPopupDialogue').appendChild(blogPopupBody);	
		document.getElementById('blogPopupDialogue').style.width = '690px';
		document.getElementById('blogPopupBody').innerHTML = getLoadingText();	
	}else{
		blogPopupScreen.style.opacity = 0
		document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);
		blogPopupScreen.style.filter = 'alpha(opacity:0)';
		if(!showWindowForPopup){
			window.document.body.style.overflow = 'visible';
		}
		blogPopupDialogue.style.display = 'none';
	}
	url = escapeUrl(url);
	
	Ext.Ajax.request({
	   url: url+'?module='+moduleId,
	   params: {module : moduleId },
	   method: 'POST',
	   success: function(result, request){
			if(!showWindowForPopup){
				document.getElementById('blogPopupBody').innerHTML = result.responseText;
			}else {
	   	   		editHtmlText = result.responseText;
	   	   		flagForBlogEdit = true;
	   	   		showLoadingDiv('Loading...');
				showTabPanel(editHtmlText, url+'?module='+moduleId);
			}
	   	   if(title == 'Edit'){
	   	   	   contentPanel.getComponent('content').value = document.getElementById('blogEntryContent').value;
	   	   }
	   	   contentPanel.getComponent('content').render('contentTextareaPosition');
	   },
	   failure: function(result, response){
		   extAlert(errorTitle, 'Error occurred while loading popup.', Ext.MessageBox.ERROR);
	   }
	});
	setNow = true;
}

function saveUpdatedBlogEntry(weblogEntryId){
	if(isValidContent(contentPanel)) {
		if(document.getElementById('blog-it_title').value.trim() == '') {
		     Ext.MessageBox.confirm("Confirm", "Post blog without a subject?", function(btn) { if(btn == 'yes') {updateEntry(weblogEntryId);return true;} else return false; });
		} else {
			updateEntry(weblogEntryId);
		}
	} else {
		extAlert(errorTitle, validationMessageForBlogEntry,  Ext.MessageBox.ERROR);
	}
}

// updating the blog entry 
function updateEntry(weblogEntryId) {
	if(document.getElementById('isImportant') != null){
		important = document.getElementById('isImportant').checked
	}
	subject = document.getElementById('blog-it_title').value;
	hidePopup();
	showLoadingDiv('Saving...');
	if(content.toLowerCase().indexOf('<style') != -1){
		content = getStylesEscapedFromContent(content);
	}
	var url = JSPRootURL +'/blog/AddWeblogEntry/SaveUpdateBlogEntry';
	Ext.Ajax.request({
	   url: url+'?module='+moduleId,
	   params: { module : moduleId, subject : subject, content : content, spaceId : spaceId,
				 objectId : objectId, weblogentryId : weblogEntryId, isImportant : important},
	   method: 'POST',
	   success: function(result, request){
	   	   important = false;
		   if(result.responseText != "" && result.responseText != "true" && document.getElementById('FullViewBody_'+weblogEntryId) != null) {
		   		document.getElementById('FullViewBody__'+weblogEntryId).innerHTML = result.responseText;
				document.getElementById('TitleViewTitle_'+weblogEntryId).innerHTML = document.getElementById('FullViewTitle_'+weblogEntryId).innerHTML;
				setLinkTarget();
		   } else if(result.responseText == "true") {
		   		fromDate.setValue('');
				toDate.setValue('');
		   		applyFilters('clearFilter');
		   		if(document.getElementById("blogCountMessage") != null){
				}
		   }
		   removeLoadingDiv();
		   applyFilters('apply'); //To reload blog entries on updating an entry
	   },
	   failure: function(result, response){
		   extAlert(errorTitle, 'Saving failed... Please try again.', Ext.MessageBox.ERROR);
		   removeLoadingDiv();
	   }
	});
	
}

// Getting number of blog entries if exist for selected object by ajax request 
function getBlogEntriesByObjectId(objectId){	
    Ext.Ajax.request({
	   url: JSPRootURL +'/blog/AddWeblogEntry/GetBlogEntries?module='+moduleId,
	   params: {module: moduleId, objectId: objectId},
	   method: 'POST',
	   success: function(result, request){
	       var responseText = result.responseText;
	       var responseObj = eval("("+ result.responseText +")");
	       // if true show "Recent Blogs" tab by default else set "New Blog Entry" tab as default
	       recentTabActive =  responseObj.recentTabActive;
	       // checking blog entry is allowed or not for user in current space
	       if(!responseObj.accessAllowed){
       			removeLoadingDiv();
       			hidePopup();
       			extAlert(errorTitle, responseObj.securityValidationMsg, Ext.MessageBox.ERROR);
       			return;
	       }
	       // if blog not exist for selected object send request to create a blog first
	       if(responseObj.BlogNotExist != null) {
	       		if(showWindowForPopup && typeof tabs != 'undefined'){
	       			tabs.hideTabStripItem(0);
	       	   		tabs.activate('blog-it-for-popUp');
	       	   	}	
	       	   if(document.getElementById('new-blog-it') != null) {
				   document.getElementById('new-blog-it').disabled=true;
			   }
			   if(document.getElementById('noOfBlogs') != null) {
		       		document.getElementById('noOfBlogs').innerHTML = 'Blog not active for this space. Activating it now...<img src="'+JSPRootURL+'/images/default/grid/loading.gif" />'; 
			   }		
		       if(responseObj.activateBlogFor != null) {
	       	   	   createBlog(responseObj.BlogNotExist, responseObj.activateBlogFor, capitaliseMe(getSubString(responseObj.blogContextType, objectNameLength)));		       
	       	   } else {
   	       	   	   createBlog(responseObj.BlogNotExist, objectId, capitaliseMe(getSubString(responseObj.blogContextType, objectNameLength)));
   	       	   }
	       }
	       // show message if null value received
	       else if(responseObj == 'null' && document.getElementById('noOfBlogs') != null){
	       	   document.getElementById('noOfBlogs').innerHTML = '<font color="red"><b>Loading blogs failed please try again.</font>'; 
	       }
	       // if blog entries found then show the link for viewing those blog entries
	       else {
		       blogNotExist = false;
		       if(!responseObj.blogNotSupported){
		       		if(document.getElementById('recentBlogCount') != null){
		       		 	document.getElementById('recentBlogCount').innerHTML = 'Recent Blogs (' + responseObj.blogCount + ')';
		       		}
			        if(responseObj.blogCount == 0){
			       		if(showWindowForPopup && typeof tabs != 'undefined'){
	       					tabs.hideTabStripItem(0); 
	       					tabs.activate('blog-it-for-popUp');
						}	
						if(document.getElementById('noOfBlogs') != null) {
			   	       			document.getElementById('noOfBlogs').innerHTML = '<b>Adding first blog entry.</b>';
						}
			   	    } else if(typeof(blogItFor) != 'undefined' && (blogItFor == 'myAssignments' ||  blogItFor == 'workPlanProject' ||  blogItFor == 'taskView') && objectType != 'person') {
				   	   if(blogItFor == 'taskView') {
				   	   		if (recentTabActive) { // if true show "Recent Blogs" tab by default else set "New Blog Entry" tab as default
			   	   				tabs.unhideTabStripItem(1);
			   	   			} else {
			   	   				tabs.activate('blog-it-for-popUp');
			   	   			}
				   	   }
				   	   if(!showWindowForPopup && document.getElementById('noOfBlogs') != null)	{
					   		document.getElementById('noOfBlogs').innerHTML = '<a href=\''+JSPRootURL+'/blog/view/show_blog_entries_for_object?objectId='+objectId
			  	       		+'&objectType='+responseObj.blogContextType+'&module='+(responseObj.moduleId)
			  	       		+'\'>View ('+responseObj.blogCount+') existing blogs.</a>';
			  	       }else if(document.getElementById('noOfBlogs') != null){
			  	       		document.getElementById('noOfBlogs').innerHTML = '';	
			  	       }
			   	    } else {
			   	   		if(showWindowForPopup){
			   	   			if (recentTabActive) { // if true show "Recent Blogs" tab by default else set "New Blog Entry" tab as default
			   	   				tabs.unhideTabStripItem(1);
			   	   			} else if(typeof tabs != 'undefined' ){
			   	   				tabs.activate('blog-it-for-popUp');
			   	   			}	
			   	   		}	
						if(!showWindowForPopup && document.getElementById('noOfBlogs') != null) {
					  	       document.getElementById('noOfBlogs').innerHTML = '<a href=\''+JSPRootURL+'/blog/view/show_blog_entries_for_object?objectId='+objectId
					  	       +'&objectType='+responseObj.blogContextType+'&module='+(responseObj.moduleId)
					  	       +'\'>View ('+responseObj.blogCount+') existing blogs.</a>';
			  	       	}
			   	   }
		   	   } else {
		   	   		if(blogItFor && blogItFor != 'timesheet') {
			   	   	    extAlert(errorTitle, loadingBlogErrorMsg, Ext.MessageBox.ERROR);
			   			hidePopup();
		   			} else {
		   				document.getElementById('errorLocationID').innerHTML = activationErrorMsg;
		   			}
		   			
		   	   }
	   	   }
	   	   if (responseObj.blogContextType != null && responseObj.blogContextName != null) {
				selectedTaskForToolTip = capitaliseMe(responseObj.blogContextTypeTooltip) + ': '+ responseObj.blogContextNameTooltip;
				selectedTask = capitaliseMe(getSubString(responseObj.blogContextType, objectNameLength)) + ': '+ getSubString(responseObj.blogContextName, objectNameLength);
				setContextAndToolTipFor('blog-it_selectedTaskId', selectedTask, selectedTaskForToolTip);
		   }
		   if(typeof(blogItFor) != 'undefined' && blogItFor == 'PersonalPortFolio' 
		   		&& responseObj.blogSpaceType != null && responseObj.blogSpaceName != null ) {
			    selectedTaskForToolTip = capitaliseMe(responseObj.blogSpaceTypeTooltip) + ': ' + responseObj.blogSpaceNameTooltip;
				selectedTask = capitaliseMe(getSubString(responseObj.blogSpaceType, objectNameLength)) + ': '+ getSubString(responseObj.blogSpaceName, objectNameLength);
				setContextAndToolTipFor('blog-it_selectedTaskId', selectedTask, selectedTaskForToolTip);
		   } else if (typeof(blogItFor) != 'undefined' && responseObj.blogSpaceName != null && (blogItFor == 'workPlanProject' || blogItFor == 'taskView')) {
		   		selectedTaskOfForToolTip = 'Project: ' + responseObj.blogSpaceName;
		   		selectedTaskOf = 'Project' + ': '+ getSubString(responseObj.blogSpaceNameTooltip, objectNameLength);
		   		setContextAndToolTipFor('blogProjectTitleId', selectedTaskOf, selectedTaskOfForToolTip);
		   }
		   
		   if(responseObj.userDate != null && typeof(responseObj.userDate) != 'undefined' && responseObj.userDate != ''){
		   		if(document.getElementById('blog-it_UserDate') != null)
			   		document.getElementById('blog-it_UserDate').innerHTML = responseObj.userDate;
		   		blogUserDate = responseObj.userDate;
		   }
	   },
	   failure: function(result, response){
	   	   if(blogItFor && blogItFor != 'timesheet') {
		  	 	extAlert(errorTitle, 'Server Request Failed..', Ext.MessageBox.ERROR);
		   		hidePopup();
		   }
	   },
	   callback: function(opt,succ,response){
	   		if(navigator.userAgent.toLowerCase().indexOf("firefox/2.0") >= 0) { 	
	   			if(contentPanel != null && contentPanel != 'undefined'){
	   				contentPanel.getComponent('content').getToolbar().syncSize();
	   			}
			}	
		}
	});
}

// set the context and tooltip for given divid
function setContextAndToolTipFor(divId, contextValue, contextValueTooltip) {
	if (document.getElementById(divId) != null) {
		document.getElementById(divId).innerHTML = contextValue.replace("&amp;acute;", "'").replace("&acute;", "'");
		document.getElementById(divId).title = contextValueTooltip.replace("&amp;acute;", "'").replace("&acute;", "'");
	}
}

// creating a blog if not exist for blog type personal/project
function createBlog(blogType, objectId, objectType){
 	Ext.Ajax.request({
	   url: JSPRootURL +'/blog/view/'+blogType+'?module='+moduleId,
	   params: {module: moduleId, objectId: objectId},
	   method: 'POST',
	   success: function(result, request){
	      if(result.responseText == "true" && document.getElementById('noOfBlogs') != null && document.getElementById('new-blog-it') != null){
				document.getElementById('new-blog-it').disabled = false;
	          	if(objectType == ''){objectType = 'space'}
	          	document.getElementById('noOfBlogs').innerHTML = '<b>Blog activated and this will be the first blog entry for this '+objectType+'.</b>';
	          	blogNotExist = false;
	      } else if(document.getElementById('noOfBlogs') != null){
	      	  document.getElementById('noOfBlogs').innerHTML = activationFailedErrorMsg;
	      }
	   },
	   failure: function(result, response){				  
		   extAlert(errorTitle, 'Server Request Failed..', Ext.MessageBox.ERROR);
		   hidePopup();
	   }
	});
}

// Adding comment to blog entry
function addComment(url){
	flagForaddComment = false;
	
	htmlForCommentTab = '';
	weblogEntryId = url.substring(url.lastIndexOf('/')+1, url.length);
	//'flagForUpdateComment' is set for if user click on new comment but  already tab open for another comment
	if(flagForUpdateComment){
		simpleFlag = true;                     //'simpleFlag' set for handling htmleditor for 'new comment' tab
		flagForaddComment = true;              //'flagForaddComment' set for avoid loading of comment data every time
	}
	if(showWindowForPopup) { 
		if((openFor == 'blogPage') || (typeof(blogItFor) != 'undefined' && blogItFor == 'myAssignments' || blogItFor == 'workPlanProject' || blogItFor == 'PersonalPortFolio')|| 
				(typeof(needToLoadBlogEntries) != 'undefined' && needToLoadBlogEntries && blogItFor == 'taskView' || 
				(typeof(blogItFor) != 'undefined' && blogItFor == 'timesheet'))) {
			blogItWinHeight = blogItWinDefaultHeight; // for blog it window
			flagForBlogNewComment = true;
			showTabPanel(blankHtmlText);
		} else {
			tabs.unhideTabStripItem(2);
			tabs.activate('blog-it-comments');
		}
	} else {
		flagForBlogPageHandle = false;
	}
	
	// hiding the scroll bars if any before opening popup
	if(!showWindowForPopup){
		window.document.body.style.overflowY = 'hidden';
		window.document.body.style.overflow = 'hidden';
	}	
	var leftPos = window.screen.width/5;
	var topPos = window.screen.height/12;
	blogPopupDialogue.style.top = topPos+"px";
	blogPopupDialogue.style.left = leftPos+"px";
	
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		if(!showWindowForPopup){
			document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
		}	
	}
	if(!showWindowForPopup) {
		document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);
		document.getElementsByTagName('body')[0].appendChild(blogPopupDialogue);
		document.getElementById('blogPopupDialogue').appendChild(blogPopupBody);
		document.getElementById('blogPopupBody').innerHTML = getLoadingText();
		
	Ext.Ajax.request({
	   url: url+'?module='+moduleId,
	   params: { module : moduleId },
	   method: 'POST',
	   success: function(result, request){
		   var titleText = '<div id="dialog">'; 
		   titleText += '	<h1>New Comment</h1>';
		   titleText += '	<div class="close">';
		   titleText += '		<a href="javascript:hidePopup();">';
		   titleText += '			<img border="0" alt="x" src="'+JSPRootURL+'/images/menu/close.gif"/>';
		   titleText += '		</a>';
		   titleText += '	</div>';
		   titleText += '</div>';
		   if(!showWindowForPopup) {
		   		document.getElementById('blogPopupBody').innerHTML = titleText + result.responseText;		   		
		   }			
		   if(!simpleFlag){
			   // Initializing Ext QuickTips before creating htmleditor
			   Ext.QuickTips.init();
			   // Creating rich text box for contents
			   commentContentPanel = new Ext.FormPanel({
			   	   id : 'comment',
				   border: false,
				   width: '100%',
				   items: [{
					   xtype: 'htmleditor',
					   id: 'contentForComment',
					   fieldLabel: 'Content',
					   width: '520px',
					   height: 200,
					   anchor: '95%',
					   enableFontSize: false,
					   style: 'border:thin;border-color:#33BDFF;',
					   renderTo: document.getElementById('contentTextareaPositionForComment'),
					   listeners: {			
						   'render': function(component){
							   var size = component.getSize();
							   // here width(i.e.520) is the width given to width attribute above
							   // and height(i.e.200) is height given to height attribute above
							   component.setSize(size.width+(520-size.width), 200);
						   }
					   }
				  }]
			  });
		   } 
		   if(document.getElementById('blogCommentContent:icon') != null){
		   	   document.getElementById('blogCommentContent:icon').style.display = 'none';
		   }
	   },
	   failure: function(result, response){
		   extAlert(errorTitle, 'Server Request Failed..', Ext.MessageBox.ERROR);
		   hidePopup();
	   }
	});
	} else {
		blogPopupScreen.style.opacity = 0;
		blogPopupScreen.style.filter = 'alpha(opacity:0)';
		document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);
		if(!showWindowForPopup){
			window.document.body.style.overflow = 'visible';
		}
		blogPopupDialogue.style.display = 'none';
	}
	setNow = true;
	if((!flagForBlogPageHandle) && (!showWindowForPopup)) {
	}else{
		blogPopupDialogue.style.display = 'none';
	}	
}

// Saving comment
function saveComment() {
	flagForUpdateComment = false;
	if(isValidContent(commentContentPanel)) {
	     if(!showWindowForPopup) {
			hidePopup();
		}
		showLoadingDiv('Saving...');
		if(content.toLowerCase().indexOf('<style') != -1){
			content = getStylesEscapedFromContent(content);
		}
		// Saving blog entry comment by ajax request
		var url = JSPRootURL +'/blog/AddWeblogEntryComment/Save'+(new Date()).getTime();
		document.getElementById('blog-it').disabled = true;
		Ext.Ajax.request({
		   url: url+'?module='+moduleId,
		   params: { module : moduleId, weblogEntryId : weblogEntryId, content : content },
		   method: 'POST',
		   success: function(result, request){
		   	   if(result.responseText == "true") {
		   	   	    removeLoadingDiv();					
		   	   	    loadAddedComment(weblogEntryId, 'comment');
					//close window for blog page and myassignment page otherwise go to 'recent blog' blog tab
					if(showWindowForPopup){
						if((openFor == 'blogPage' || blogItFor == 'myAssignments' || blogItFor == 'workPlanProject' || blogItFor == 'PersonalPortFolio') ||
							(typeof(needToLoadBlogEntries) != 'undefined' && needToLoadBlogEntries && blogItFor == 'taskView')
							|| blogItFor == 'timesheet'){
							blogItWin.destroy();
    						hidePopup();
	    				} else {
    						flagForHandlingRecentTab = false;
							tabs.hideTabStripItem(2);
							tabs.activate('blogs');
	    				}
	    			}
		   	   } else if(result.responseText == "false"){
		   	   		extAlert(errorTitle, savingCommentFailedErrorMsg, Ext.MessageBox.ERROR);
				    removeLoadingDiv();
		   	   }
		   },
		   failure: function(result, response){
			   extAlert(errorTitle, savingCommentFailedErrorMsg, Ext.MessageBox.ERROR);			   
			   removeLoadingDiv();
		   }
		});
	} else {
		var validationMessageForBlogComment = validationMegForBlogComment;
		extAlert(errorTitle, validationMessageForBlogComment, Ext.MessageBox.ERROR);
	}
}

// setting important flage if checkbox clicked
function setImpFlag() {
	if(document.getElementById('isImportant').checked){
		important = true;
		document.getElementById('importantMsg').style.display = '';
	} else {
		important = false;
		document.getElementById('importantMsg').style.display = 'none';
	}
}

function loadAddedComment(weblogEntryId, loadType) {
	var commentCnt;
	var commentDivId = 'commentDivFor_'+weblogEntryId;
	
	// the comment-entry css class is not required on my assignment page
	var isCommentClassRequired = !(typeof(blogItFor) != 'undefined' && (blogItFor == 'myAssignments' || blogItFor == 'workPlanProject'));	

	document.getElementById('Comments_For_'+weblogEntryId).innerHTML = '<span style="padding-left:20">Loading... <img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" /></span>';
	
	Ext.Ajax.request({
	   url: JSPRootURL +'/blog/entry/show_blog_entry?module='+moduleId,
	   params: {module : moduleId, weblogEntryId : weblogEntryId, loadType : loadType, isCommentClassRequired : isCommentClassRequired},
	   method: 'POST',
	   success: function(result, request){
	  	if(result.responseText != "") {
			var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
			var responseText = result.responseText.replace(docTypeTag, '');	
				if(document.getElementById('Comments_For_'+weblogEntryId) !=null) {
 					document.getElementById('Comments_For_'+weblogEntryId).innerHTML = responseText;
 				}
				if(typeof blogItFor != 'undefined' && blogItFor == 'timesheet'){
					if(typeof document.getElementById('blogCommentDiv') != 'undefined' && document.getElementById('blogCommentDiv') != null){
						document.getElementById('blogCommentDiv').className = '';
						document.getElementById('blogPostDiv').className = 'post-body1';
					}
				}
			var scriptTag = '<script type="text/javascript">';
			var javaScriptCode = responseText;
			javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
			eval(javaScriptCode.replace(/\n/g, ''));
			if(document.getElementById("pnWeblogCommentSize_"+weblogEntryId) == null) {
				var addDivIfNotNoComment =  '<a id="hideShowCommentsLinkFor_'+weblogEntryId+'" href="javascript:hideComments('+weblogEntryId+',\'New comment\')">';
				addDivIfNotNoComment += '<strong>Hide Comments</strong></a>';
				addDivIfNotNoComment += '<span id="pnWeblogCommentSize_'+weblogEntryId+'"> ('+commentCnt+')</span>';
				if(document.getElementById(commentDivId) != null) {	
					document.getElementById(commentDivId).innerHTML = addDivIfNotNoComment;				
				}	
			} else if(commentCnt == '0'){				
				document.getElementById(commentDivId).innerHTML = '<strong>&darr; '+noCommentsMessage+'</strong>';
			} else {
				document.getElementById("pnWeblogCommentSize_"+weblogEntryId).innerHTML = " ("+commentCnt+")";
			}
			setLinkTarget();
	  	}
	   },
	   failure: function(result, response){				  
		   extAlert(errorTitle, 'Server Request Failed..', Ext.MessageBox.ERROR);
		   hidePopup();
	   }
	});
}

// To open close blog entry clicking on + , - image
function openCloseEntry(divId) {
	var titleViewDivId = 'TitleViewFor_'+divId;
	var fullEntryDivId = 'FullViewFor_'+divId;
	var len = openBlogEntries;	
	
	if(document.getElementById(titleViewDivId).style.display == 'block') {
		openBlogEntries ++;
    	document.getElementById(titleViewDivId).style.display = 'none';
		document.getElementById(fullEntryDivId).style.display = 'block';
		showHidePictures(isShowPicture);
		document.getElementById('expandUnexpandImage_'+divId).src = JSPRootURL+'/images/minus.png';
		document.getElementById('expandUnexpandImage_'+divId).title = collapseIconTooltip;
	} else {
		openBlogEntries --;
		document.getElementById(fullEntryDivId).style.display = 'none';
		document.getElementById(titleViewDivId).style.display = 'block';	
		document.getElementById('expandUnexpandImage_'+divId).src = JSPRootURL+'/images/plus.png';
		document.getElementById('expandUnexpandImage_'+divId).title = expandIconTooltip;
	}
	  
	  if(openBlogEntries == blogEntryIdForImageDisplay.split(',').length-1) {
  		fullEntriesView = true;
		if(typeof actionsIconEnabled != 'undefined' && actionsIconEnabled ){
			if(typeof showTitlesOnlyImageOver != 'undefined' && typeof showTitlesOnlyImageOn != 'undefined' && typeof showTitlesOnlyLink != 'undefined') {
				document.getElementById('entryViewLink').innerHTML = '<a id=\'linktitles\' linktitles.onmouseover=\'document.showTitlesImage.src=\''+showTitlesOnlyImageOver+'\' linktag.onmouseout=\'document.showTitlesImage.src=\''+showTitlesOnlyImageOn+'\' href=\'javascript:showHideEntryText(false);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'showTitlesImage\' src=\''+showTitlesOnlyImageOn+'\' title=\''+showTitlesOnlyLink+'\'/>'+showTitlesOnlyLink+'</a>';
			}
		} else if(typeof showTitlesOnlyLink != 'undefined'){
			document.getElementById('entryViewLink').innerHTML = '<a href=\'javascript:showHideEntryText(false);\'>'+showTitlesOnlyLink+'</a>';
		}
		if(document.getElementById('hidePictureLink') != null) {
			//document.getElementById('hidePictureLink').innerHTML = '<a href="javascript:showHidePictures(true);">Hide Pictures</a>';
		}
	} else if(openBlogEntries == 0) {
		fullEntriesView = false;
		if( typeof actionsIconEnabled != 'undefined' && actionsIconEnabled ){
			if(typeof showTitlesOnlyImageOver != 'undefined' && typeof showTitlesOnlyImageOn != 'undefined' && typeof showFullEntriesLink != 'undefined') {
				document.getElementById('entryViewLink').innerHTML = '<a id=\'linktitles\' linktitles.onmouseover=\'document.showFullEntryImage.src=\''+showTitlesOnlyImageOver+'\' linktag.onmouseout=\'document.showFullEntryImage.src=\''+showTitlesOnlyImageOn+'\' href=\'javascript:showHideEntryText(true);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'showFullEntryImage\' src=\''+showTitlesOnlyImageOn+'\' title=\''+showFullEntriesLink+'\'/>'+showFullEntriesLink+'</a>';
			}
		} else {
			if(typeof showFullEntriesLink != 'undefined') {
				document.getElementById('entryViewLink').innerHTML = '<a href=\'javascript:showHideEntryText(true);\'>'+showFullEntriesLink+'</a>';
			}
		}
		if(document.getElementById('hidePictureLink') != null) {
			document.getElementById('hidePictureLink').innerHTML = '<a href="#" class="disabled"><img hspace=\'0\' border=\'0\' name=\'hidePictureImage\' src="'+showHidePictureOn+'\' title=\''+hidePicturesLink+'\' />'+hidePicturesLink+'</a>';	
		}
	}
}

// For title and full entry view
function showHideEntryText(show) {
	var entryIds = blogEntryIdForImageDisplay.split(',');
	persistedShowHideEntryText = show;
	if(show) {
		for(var idIndex = 0; idIndex < entryIds.length-1; idIndex++) {
			if(document.getElementById('FullViewFor_'+entryIds[idIndex]) != null && typeof document.getElementById('FullViewFor_'+entryIds[idIndex]) != 'undefined'){
				document.getElementById('TitleViewFor_'+entryIds[idIndex]).style.display = 'none';
				document.getElementById('FullViewFor_'+entryIds[idIndex]).style.display = 'block';
				if(document.getElementById('expandUnexpandImage_'+entryIds[idIndex])!=null){
					document.getElementById('expandUnexpandImage_'+entryIds[idIndex]).src = JSPRootURL+'/images/minus.png';
					document.getElementById('expandUnexpandImage_'+entryIds[idIndex]).title = collapseIconTooltip;
				}
				
				openBlogEntries = blogEntryIdForImageDisplay.split(',').length-1;
				fullEntriesView = true;
			}
		}
	} else {
		for(var idIndex = 0; idIndex < entryIds.length-1; idIndex++) {
			if(document.getElementById('TitleViewFor_'+entryIds[idIndex]) != null && typeof document.getElementById('TitleViewFor_'+entryIds[idIndex]) != 'undefined'){
				document.getElementById('FullViewFor_'+entryIds[idIndex]).style.display = 'none';
				document.getElementById('TitleViewFor_'+entryIds[idIndex]).style.display = 'block';
				if(document.getElementById('expandUnexpandImage_'+entryIds[idIndex])!=null){
					document.getElementById('expandUnexpandImage_'+entryIds[idIndex]).src = JSPRootURL+'/images/plus.png';
					document.getElementById('expandUnexpandImage_'+entryIds[idIndex]).title = expandIconTooltip;
				}
				openBlogEntries = 0;
				fullEntriesView = false;
			}
		}
	}
	if(fullEntriesView) {
		for(var idIndex = 0; idIndex < entryIds.length-1; idIndex++) {
			if( actionsIconEnabled ){
				document.getElementById('entryViewLink').innerHTML = '<a id=\'linktitles\' linktitles.onmouseover=\'document.showTitlesImage.src=\''+showTitlesOnlyImageOver+'\' linktag.onmouseout=\'document.showTitlesImage.src=\''+showTitlesOnlyImageOn+'\' href=\'javascript:showHideEntryText(false);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'showTitlesImage\' src=\''+showTitlesOnlyImageOn+'\' title=\''+showTitlesOnlyLink+'\'/>'+showTitlesOnlyLink+'</a>';
			} else {
				document.getElementById('entryViewLink').innerHTML = '<a href=\'javascript:showHideEntryText(false);\'>'+showTitlesOnlyLink+'</a>';
			}
			if(document.getElementById('image_'+entryIds[idIndex])!=null && document.getElementById('hidePictureLink') != null){
			if(document.getElementById('image_'+entryIds[idIndex]).style.display == 'block') {
				if( actionsIconEnabled ){
					document.getElementById('hidePictureLink').innerHTML = '<a id=\'linktag\' linktag.onmouseover=\'document.hidePictureImage.src=\''+showHidePictureOver+'\' linktag.onmouseout=\'document.hidePictureImage.src=\''+showHidePictureOn+'\' href=\'javascript:showHidePictures(true);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'hidePictureImage\' src=\''+showHidePictureOn+'\' title=\''+hidePicturesLink+'\'/>'+hidePicturesLink+'</a>';
				} else {
					document.getElementById('hidePictureLink').innerHTML = '<a href="javascript:showHidePictures(true);">'+hidePicturesLink+'</a>';
				}
			} else {
				if( actionsIconEnabled ) {
					document.getElementById('hidePictureLink').innerHTML = '<a id=\'linktag\' linktag.onmouseover=\'document.showPicturesImage.src=\''+showHidePictureOver+'\' linktag.onmouseout=\'document.showPicturesImage.src=\''+showHidePictureOn+'\' href=\'javascript:showHidePictures(false);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'showPicturesImage\' src=\''+showHidePictureOn+'\' title=\''+showPicturesLink+'\'/>'+showPicturesLink+'</a>';
				} else {
					document.getElementById('hidePictureLink').innerHTML = '<a href="javascript:showHidePictures(false);">'+showPicturesLink+'</a>';
				}
		    }
		  }
		}
	} else {
		if( actionsIconEnabled ) {
			if(document.getElementById("blogCountMessage").innerHTML.indexOf("Blog entries not found") >= 0){
				document.getElementById('entryViewLink').innerHTML = '<a  class="disabled" id=\'linktitles\' href=\'javascript:showHideEntryText(false);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'showTitlesImage\' src=\''+showTitlesOnlyImageOff+'\' title=\''+showTitlesOnlyLink+'\'/>'+showTitlesOnlyLink+'</a>';
				document.getElementById('hidePictureLink').innerHTML = '<a href="#" class="disabled"><img hspace=\'0\' border=\'0\' name=\'hidePictureImage\' src=\''+ hidePictureImageOff +'\' title=\''+hidePicturesLink+'\' />'+hidePicturesLink+'</a>';
			}else {
				document.getElementById('entryViewLink').innerHTML = '<a id=\'linktitles\' linktitles.onmouseover=\'document.showFullEntryImage.src=\''+showTitlesOnlyImageOver+'\' linktag.onmouseout=\'document.showFullEntryImage.src=\''+showTitlesOnlyImageOn+'\' href=\'javascript:showHideEntryText(true);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'showFullEntryImage\' src=\''+showTitlesOnlyImageOn+'\' title=\''+showFullEntriesLink+'\'/>'+showFullEntriesLink+'</a>';
			}
		} else {	
			document.getElementById('entryViewLink').innerHTML = '<a href=\'javascript:showHideEntryText(true);\'>'+showFullEntriesLink+'</a>';
		}
		if(document.getElementById('hidePictureLink') != null) {
			if( actionsIconEnabled ) {
				document.getElementById('hidePictureLink').innerHTML = '<a href="#" class="disabled"><img hspace=\'0\' border=\'0\' name=\'hidePictureImage\' src=\''+ hidePictureImageOff + '\' title=\''+hidePicturesLink+'\' />'+hidePicturesLink+'</a>';
			} else {
				document.getElementById('hidePictureLink').innerHTML = '<a href="#" class="disabled">'+hidePicturesLink+'</a>';
			}
		}
	}
}

// For Hiding/Showing comments from blog entries
function hideComments(entryId, commentToken){
	var commentsBlockId = 'Comments_For_'+entryId;
	var addCommentForId = 'addCommentFor_'+entryId;
	if(document.getElementById(commentsBlockId).style.display == 'block') {
		document.getElementById(commentsBlockId).style.display = 'none';
		if(!showWindowForPopup){
			document.getElementById(addCommentForId).innerHTML = '';
		}
		document.getElementById('hideShowCommentsLinkFor_'+entryId).innerHTML = 'View Comments';
	} else {
		document.getElementById(commentsBlockId).style.display = 'block';
		if(!showWindowForPopup){
			document.getElementById(addCommentForId).innerHTML = commentToken;
		}
		document.getElementById('hideShowCommentsLinkFor_'+entryId).innerHTML = '<strong>Hide Comments</strong>';
	}
}

// For Hiding/Showing pictures of person's who posted blog entries
function showHidePictures(show) {
	isShowPicture = show;
	var entryIdArray = blogEntryIdForImageDisplay.split(',');
	if(show) {
		for(var i = 0; i < entryIdArray.length-1; i++) {
			if(document.getElementById('image_'+ entryIdArray[i])!=null){
			if(typeof document.getElementById('image_'+ entryIdArray[i]) != 'undefined' 
				&& document.getElementById('image_'+ entryIdArray[i]).style.display != null) { 
				document.getElementById('image_'+ entryIdArray[i]).style.display = 'none';
			}
			}
		}
	} else {
		for(var i = 0; i < entryIdArray.length-1; i++) {
			if(document.getElementById('image_'+ entryIdArray[i])!=null){
			if(typeof document.getElementById('image_'+ entryIdArray[i]) != 'undefined' 
				&& document.getElementById('image_'+ entryIdArray[i]).style.display != null) {
				document.getElementById('image_'+ entryIdArray[i]).style.display = 'block';
			}
			}
		}
	}
	if(document.getElementById('hidePictureLink') != null) {
		if(!show) {
			if( actionsIconEnabled ){
				document.getElementById('hidePictureLink').innerHTML = '<a id=\'linktag\' linktag.onmouseover=\'document.hidePictureImage.src=\''+showHidePictureOver+'\' linktag.onmouseout=\'document.hidePictureImage.src=\''+showHidePictureOn+'\' href=\'javascript:showHidePictures(true);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'hidePictureImage\' src=\''+showHidePictureOn+'\' title=\''+hidePicturesLink+'\'/>'+hidePicturesLink+'</a>';
			}else{
				document.getElementById('hidePictureLink').innerHTML = '<a href=\'javascript:showHidePictures(true);\'>'+hidePicturesLink+'</a>';				
			}
		} else {
			if( actionsIconEnabled ){
				document.getElementById('hidePictureLink').innerHTML = '<a id=\'linktag\' linktag.onmouseover=\'document.showPicturesImage.src=\''+showHidePictureOver+'\' linktag.onmouseout=\'document.showPicturesImage.src=\''+showHidePictureOn+'\' href=\'javascript:showHidePictures(false);\'><img id=\'pic\' hspace=\'0\' border=\'0\' name=\'showPicturesImage\' src=\''+showHidePictureOn+'\' title=\''+showPicturesLink+'\'/>'+showPicturesLink+'</a>';
			}else{
				document.getElementById('hidePictureLink').innerHTML = '<a href="javascript:showHidePictures(false);">'+showPicturesLink+'</a>';
			}
		}
	}
}

// resize the panel for scroll bar of blog it window in IE
function restorePanel(){
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		blogDispPanel.setWidth(blogDispPanel.getSize().width-2);
		blogDispPanel.setWidth(blogDispPanel.getSize().width+2);
	}
	setLinkTarget();
	if(typeof(blogItFor) != 'undefined' && blogItFor != 'PersonalPortFolio' ){
		createMultipleWorkSubmittedTable();
	}
}

// function for load blog entries for tab panel window
function loadBlogEntriesForBlogit(panel) {
	var entriesForPopup = true;
	var setCommentHide = true;	
	var url, params;
	if(!flagForBlogNewComment && (typeof(blogItFor) != 'undefined' && (blogItFor == 'myAssignments' || blogItFor == 'workPlanProject') || (typeof(needToLoadBlogEntries) != 'undefined' && needToLoadBlogEntries && blogItFor == 'taskView')) ) {
		tabs.activate('blog-it-for-popUp');
		tabs.hideTabStripItem(0);
		tabs.hideTabStripItem(2);
	} else if((typeof(openFor) != 'undefined' && openFor == 'blogPage' && !flagForBlogEdit && !flagForBlogNewComment)/*|| (typeof(blogItFor) != 'undefined' && blogItFor == 'myAssignments')*/){
		openFor = 'blogPage';
		tabs.activate('blog-it-for-popUp');
		tabs.hideTabStripItem(0);
		tabs.hideTabStripItem(2);
	} else {
	 	url = JSPRootURL +'/blog/view/show_blog_entries_for_object?module='+moduleId;
	    params = {moduleId : moduleId, objectId : objectId, entriesForPopup : entriesForPopup, objectType : objType};
	}
	//load if blogDispPanel is activated
	// 'flagForHandlingRecentTab' set for avoid the loading of blog entry every time when the 'recent blog' tab activated
	if(tabs.getActiveTab().getId() == 'blogs'&& (!flagForHandlingRecentTab) && !flagForBlogNewComment && !flagForBlogEdit) {
		Ext.Ajax.request({
		   url: url,
		   params: params,
		   method: 'POST',
		   success: function(result, request){
		      if(document.getElementById('recentBlogEntries') != null){
		      	  document.getElementById('recentBlogEntries').innerHTML = '&nbsp;' + result.responseText;
		      }
		   },
		   failure: function(result, response){
		   },
		   callback: restorePanel
		});
	}
	flagForHandlingRecentTab = true;
}

//Function for display tab panel window for blog-it
function showTabPanel(htmlText, url) {
	flagForHtmlEditor = false;
	blogPopupDialogue.style.display = 'none';
	
	//tab for display blog entry
	blogDispPanel = new Ext.Panel({
		id: 'blogs',
		title: '<span id="recentBlogCount">Recent Blogs<span>',
		autoScroll: true,
		html: '<div id="recentBlogEntries">'+ getLoadingText(loadingRecentBlog) +'</div>',
		listeners : {
   			'activate': loadBlogEntriesForBlogit
	    }
	});
	
	//tab for comment entry	
	commentTabPanel = new Ext.Panel({
		id: 'blog-it-comments',
		title: 'Add Comment ',
		autoScroll: true,
		html: '<div id="commentForBlog">'+ getLoadingText() +'</div>',
		listeners : {
   			'activate': loadCommentForBlogit
	    }
	});
	
	editTabPanel = new Ext.Panel({
		id: 'edit-Blog-Entry',
		title: 'Edit Blog Entry',
		autoScroll: true,
		html: htmlText,
		listeners : {
   			'activate': initializeContentPanelFromPage
	    }
	});	
	
	//tab for Blog entry
	popUpPanel = new Ext.Panel({
		id: 'blog-it-for-popUp',
		title: 'New Blog Entry',
		html: htmlText,
		listeners : {
   			'activate': initializeContentPanelNewBlog
	    }
	});			
           
    //Main Tab panel 
    tabs = new Ext.TabPanel({
		id: 'tabs',
		region: 'center',
		margins:'3 3 3 0', 
		activeTab: 0,
		defaults:{autoScroll:true},
		items:[
			 blogDispPanel,
	   	     popUpPanel,
	   	     commentTabPanel,
	   	     editTabPanel
	   	]							            
    });
        	
	// Blog-it window 
	blogItWin = new Ext.Window({
		title: 'Blog-it',
		closable: true,
		width: 780,
		height: blogItWinHeight,
		plain: true,
		resizable: true,
		layout: 'border',
		items: [  tabs  ],
		listeners : { 'show' : appendHiddenTextField,
					  'beforedestroy' : setFocusToWindow,
					  'close' : closeWindow }
	});
	
    blogItWin.show(); 
    
    if((flagForBlogNewComment) && ((openFor == 'blogPage') || (typeof(blogItFor) != 'undefined' && 
    		blogItFor == 'myAssignments') || blogItFor == 'workPlanProject'|| blogItFor == 'PersonalPortFolio' || (typeof(needToLoadBlogEntries) != 'undefined' && needToLoadBlogEntries && blogItFor == 'taskView') 
    		|| ( blogItFor == 'documentPersonal' || blogItFor == 'documentProject' || blogItFor == 'forTrashcan' )|| blogItFor == 'timesheet')){
    	tabs.hideTabStripItem(0);
    	tabs.hideTabStripItem(1);                 // display window for new comment on blog page and 
    	tabs.hideTabStripItem(3);                 // my assignment nav bar panel
    	tabs.activate('blog-it-comments');
    } else if( openFor == 'blogPage' && (flagForBlogEdit)){
    	tabs.hideTabStripItem(0);
    	tabs.hideTabStripItem(1);                 // display edit blog entry tab by default for edit link on blog page
    	tabs.hideTabStripItem(2);
    	tabs.activate('edit-Blog-Entry');
    	
    } else {
    	flagForHandlingRecentTab = true;
		tabs.hideTabStripItem(2);                // display Recent blog tab by default
		tabs.hideTabStripItem(3);
	}
}

// Manage the message and paging on successfull deletion of blog entry
function managePagingView(){
	if (blogEntriesOnPageCnt <= 1) {
		document.getElementById('pagingView_T').style.display = 'none';
		document.getElementById('pagingView_B').style.display = 'none';
		document.getElementById('blogCountMessage').innerHTML = '<font color="grey" style="size: 12px;">Blog entries not found.</font>';
		document.getElementById('entryViewLink').innerHTML = '<a href="#" class="disabled">Show Titles Only</a>';		
		if(document.getElementById('hidePictureLink') != null){
			document.getElementById('hidePictureLink').innerHTML = '<a href="#" class="disabled">Hide Pictures</a>';
		}	
	} else {
		blogEntriesOnPageCnt--;
		nextEntriesCount--;
		rangeForDisplay = rangeForDisplay - 1;
		totalNoOfBlogEntry = totalNoOfBlogEntry - 1;
		if(nextEntriesCount == 0){
			rangeForDisplay = totalNoOfBlogEntry;
			document.getElementById('loadNextBlogPosts_T').style.display  ='none';
			document.getElementById('loadNextBlogPosts_B').style.display  ='none';
		} else if(nextEntriesCount < 20){
			document.getElementById('loadNextBlogPosts_T').innerHTML = '<a href="javascript:applyFilters("next")"> Next '+nextEntriesCount+ '&nbsp;<span class="prevNextArrow">&#8594;</span> </a>';
			document.getElementById('loadNextBlogPosts_B').innerHTML = '<a href="javascript:applyFilters("next")"> Next '+nextEntriesCount+ '&nbsp;<span class="prevNextArrow">&#8594;</span> </a>';
		}
		
		document.getElementById('totalNoOfBlogEntry_T').innerHTML = totalNoOfBlogEntry;
		document.getElementById('totalNoOfBlogEntry_B').innerHTML = totalNoOfBlogEntry;
		document.getElementById('rangeForDisplay_T').innerHTML = rangeForDisplay;
		document.getElementById('rangeForDisplay_B').innerHTML = rangeForDisplay;
		if(rangeForDisplay == 0){
			document.getElementById('entryViewLink').innerHTML = '<a href="#" class="disabled">Show Titles Only</a>';		
			if(document.getElementById('hidePictureLink') != null){
				document.getElementById('hidePictureLink').innerHTML = '<a href="#" class="disabled">Hide Pictures</a>';
			}	
		}
	}	
}

// to delete blog entry (blog entry can be deleted by space administrator)
function deleteBlogEntry(weblogEntryId, work, changedEstimate) {
	deleteBlogEntryHandler = function (btn){
	    if(btn == 'yes'){
	    	var persistedMessage = document.getElementById('blogCountMessage').innerHTML;
			document.getElementById('blogCountMessage').innerHTML = '<font color="blue" style="font-weight: bold; size: 12px;">Deleting blog entry...</font>';
			document.getElementById('blogCountMessage').innerHTML += '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
			Ext.Ajax.request({
			   url: JSPRootURL+'/blog/AddWeblogEntry/delete_blog_entry?module='+moduleId,
			   params: {module : moduleId, entryId : weblogEntryId},
			   method: 'POST',
			   success: function(result, request) {
			      if(result.responseText != "false" && result.responseText != "unauthorize") {	
			      	  document.getElementById('TitleViewFor_'+weblogEntryId).innerHTML = '';
			      	  document.getElementById('FullViewFor_'+weblogEntryId).innerHTML = '';
			      	  document.getElementById('TitleViewFor_'+weblogEntryId).style.display = 'none';
			      	  document.getElementById('FullViewFor_'+weblogEntryId).style.display = 'none';
			      	  document.getElementById('blogCountMessage').innerHTML = '<font color="red" style="font-weight: bold; size: 12px;">Blog entry deleted successfully.</font>';
			      	  //managePagingView();
			      	  applyFilters('');
		      	  } else if("unauthorize" == result.responseText){    
		      	  		extAlert(errorTitle, blogentryDeletePermission, Ext.MessageBox.ERROR);
			            document.getElementById('blogCountMessage').innerHTML = persistedMessage;
		      	  } else {
			          extAlert(errorTitle, deleteBlogEntryFailedErrorMsg, Ext.MessageBox.ERROR);
			          document.getElementById('blogCountMessage').innerHTML = persistedMessage;
			      }
			   },
			   failure: function(result, response) {				  
				   extAlert(errorTitle, 'Server Request Failed..', Ext.MessageBox.ERROR);
				   document.getElementById('blogCountMessage').innerHTML = '';
				   hidePopup();
			   }
			});
		}
	}
	var confirmMessage = (work == 'null' && changedEstimate == 'null' ? confirmDeleteBlogEntryMessage : confirmDeleteBlogEntryWithWrkChEstMessage);
	
	// confirmation for deleting blog entries 
	Ext.MessageBox.show({
		title: 'Confirm',
		msg: confirmMessage,
		buttons: Ext.MessageBox.YESNO,
		fn: deleteBlogEntryHandler,
		icon: Ext.MessageBox.QUESTION
	});
}

function getSubString(subStringOf, limit) {
	if(typeof(subStringOf) != 'undefined' && subStringOf != '' && subStringOf.length > limit) {
		return subStringOf.substring(0,limit)+'...';
	} else {
		return subStringOf;
	}
}

function getLoadingText(loadingText) {
	return '<span class="loadingText">'+ (typeof loadingText == 'undefined' ? 'Loading...' : loadingText) +'</span> <img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
}

function loadpanel(panel, url) {
	if(document.getElementById('commentForBlog') != null){
		document.getElementById('commentForBlog').innerHTML = getLoadingText();
	}
	url = JSPRootURL+'/blog/AddWeblogEntryComment/'+weblogEntryId;
	var params = { module : moduleId };
	if(tabs.getActiveTab().getId() == 'blog-it-comments') {
		Ext.Ajax.request({
		   url: url,
		   params: params,
		   method: 'POST',
		   success: function(result, request){
		      if(document.getElementById('commentForBlog') != null){
		      	  document.getElementById('commentForBlog').innerHTML = '&nbsp;' + result.responseText;
		      }
		   },
		   failure: function(result, response){
		   },
		   callback: initializeContentPanelForComment
		});
	}	
}
 
//function for load comment content for tab panel window 
function loadCommentForBlogit(panel) {
	htmlForCommentTab = '';
	var url = JSPRootURL+'/blog/AddWeblogEntryComment/'+weblogEntryId;
	//load if comment tab is activated
	if(flagForUpdateComment){
		if(webLogId != weblogEntryId){
			//display error message if user click on new comment link but tab already open for another comment
			Ext.MessageBox.confirm("Confirm", "Add comment tab is already open for other blog entry. Do you want to open new comment tab?", 
				function(btn) {
					if(btn == 'yes'){
						if(flagForaddComment) {
							loadpanel(panel);
						}			
						simpleFlag = true;
						simpleFlag = false;
						flagForaddComment = true;
						flagForUpdateComment = true;
						webLogId = weblogEntryId;
					} else {
						weblogEntryId = webLogId;
						return false;
					}
				} 
			);  //end of ext msg function	
		}else{
			return false;
		}				
	}else {		
		if(!flagForaddComment) {
			loadpanel(panel);
		}	
		flagForaddComment = true;
		flagForUpdateComment = true;
		simpleFlag = false;
		webLogId = weblogEntryId;
	}
}

function initializeContentPanelNewBlog(){
	if(!flagForHtmlEditor) {
		if(document.getElementById('blog-it_selectedTaskId') != null) {
			document.getElementById('blog-it_selectedTaskId').innerHTML = selectedTask;
			document.getElementById('blog-it_selectedTaskId').title = selectedTaskForToolTip;
		}
		if (selectedTaskOf != '' && document.getElementById('blogProjectTitleId') != null) {
   			document.getElementById('blogProjectTitleId').innerHTML = selectedTaskOf;
   			document.getElementById('blogProjectTitleId').title = selectedTaskOfForToolTip;
   		}
	    if(document.getElementById('blog-it_UserDate') != null && typeof(blogUserDate) != 'undefined' && blogUserDate != ''){
	   		document.getElementById('blog-it_UserDate').innerHTML = blogUserDate;
	    }
		if((typeof(blogItFor) != 'undefined') && ((blogItFor == 'formDataItemsProject') || blogItFor == 'taskView' || blogItFor == 'workPlanProject') && document.getElementById('blog-it_timeSheet') != null){
			document.getElementById('blog-it_timeSheet').innerHTML = timesheetDivForWindow;
		}
		Ext.QuickTips.init();
		contentPanel = new Ext.FormPanel({
			border: false,
			width: 550,
			items: [{        	
				xtype: 'htmleditor',
				id: 'content',
				fieldLabel: 'Content',
				width: 520,
				height: 200,
				anchor: '95%',
				enableFontSize: false,
				style: 'border: thin; border-color: #33BDFF;',
				renderTo: document.getElementById('contentTextareaPosition'),
				listeners: {			
					'render': function(component){
						var size = component.getSize();
						if(showWindowForPopup){
							component.setSize(size.width+(642-size.width), 200);
						} else {
							component.setSize(size.width+(550-size.width), 200);
						}
						focusContent();
						removeLoadingDiv();
					}
				}
			}]
		});
		if(document.getElementById('blogCommentContent:icon') != null){
			document.getElementById('blogCommentContent:icon').style.display = 'none';
		}
		flagForHtmlEditor = true;
	} 
}

// to initialize content panel for edit window from blog page
function initializeContentPanelFromPage() {
	if(typeof initializeContentPanel != 'undefined'){
		initializeContentPanel();
	}
}

function closeWindow() {
	if(tabs.getActiveTab().getId() == 'blog-it-comments') {
		flagForUpdateComment = false;
		flagForBlogNewComment = false;
		flagForBlogEdit = false;
		tabs.hideTabStripItem(2);
		if(contentPanel != null && contentPanel != 'undefined'){
			contentPanel.getComponent('content').value = '';
		}
	}
	document.getElementsByTagName('body')[0].removeChild(blogPopupScreen);
	blogItWin.destroy(); 
	if(blogItWin != null){blogItWin = null;}
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		showHtmlSelectTags();
	}	
}

// appending text field in window to set focus for blog window closed in IE
// to solve the problem of text fields on the page not getting activated on some situations
function appendHiddenTextField(){
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0 && document.getElementById('blogit_hiddent_txt') == null){
		var blogit_hidden_textfield = document.createElement('input');
		blogit_hidden_textfield.type = 'text';
		blogit_hidden_textfield.id = 'blogit_hiddent_txt';
		blogit_hidden_textfield.style.height = '0px';
		blogit_hidden_textfield.style.width = '0px';
		blogit_hidden_textfield.style.border = '0px';
		document.getElementsByTagName('body')[0].appendChild(blogit_hidden_textfield);	
	}
}

// setting focus to a text field appended in method appendHiddenTextField()
function setFocusToWindow(w){
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0 && document.getElementById('blogit_hiddent_txt')){		
		var scrOfY = 0;
		if(typeof(window.pageYOffset) == 'number') {
			scrOfY = window.pageYOffset;
		} else if(document.body && document.body.scrollTop) {
			//DOM compliant
			scrOfY = document.body.scrollTop;
		} else if(document.documentElement && document.documentElement.scrollTop) {
			//IE6 standards compliant mode
			scrOfY = document.documentElement.scrollTop;
		}
		document.getElementById('blogit_hiddent_txt').focus();		
		window.scrollBy(0, scrOfY);
	}
}

// to initialize HTML editor for comment tab
function initializeContentPanelForComment(){
	commentContentPanel = null;
	Ext.QuickTips.init();
	commentContentPanel = new Ext.FormPanel({
	    id: 'comment', 
		border: false,
		width: 550,
		items: [{        	
			xtype: 'htmleditor',
			id: 'contentForComment',
			fieldLabel: 'Content',
			width: 520,
			height: 200,
			anchor: '95%',
			enableFontSize: false,
			style: 'border: thin; border-color: #33BDFF;',
			renderTo: document.getElementById('contentTextareaPositionForComment'),
			listeners: {			
				'render': function(component){
					var size = component.getSize();
					if(showWindowForPopup){
						component.setSize(size.width+(642-size.width), 200);
					} else {
						component.setSize(size.width+(550-size.width), 200);
					}
					focusCommentContent();
					removeLoadingDiv();
				}
			}
		}]
	});	
}

//To get details of current space
function getCurrentSpaceDetails() { 
	objectType = readCookie('_styp');
	if(objectType == 'application') {
		objectId = spaceId = 1;
	} else {
		objectId = spaceId = readCookie('_sid');
	}
	if (typeof(currentSpaceIdForBlog) != 'undefined' && typeof(currentSpaceTypeForBlog) != 'undefined') {
		objectType = currentSpaceTypeForBlog;
		objectId = currentSpaceIdForBlog
	}
	var objectSpace = objectType;
	JSPRootURL = readCookie('JSPRootURL');
	blogItFor = (typeof(blogItFor) != 'undefined' && blogItFor == 'activity') ? 'activity' : 'currentSpace';
	if(objectType != null && typeof(objectType) != 'undefined' ) {
		if(objectType == 'business' || objectType == 'application' || objectType == 'methodology' || objectType == 'configuration'){
			objectType = 'person';// just to display title for which space the blog entry is being saved
		}
		if(typeof(moduleId) == 'undefined' || moduleId == ''){
			Ext.Ajax.request({
			   url: JSPRootURL +'/blog/view/get_moduleid_from_space',
			   params: {spaceType : objectSpace},
			   method: 'POST',
			   success: function(result, request){
					var responseObj = eval("("+ result.responseText +")");
					if(responseObj.moduleId  != null && responseObj.moduleId != ''){
						moduleId = responseObj.moduleId;
						showPopup();
						if(responseObj.userDate != null && typeof(responseObj.userDate) != 'undefined' && responseObj.userDate != '' && document.getElementById('blog-it_UserDate') != null){
					   		document.getElementById('blog-it_UserDate').innerHTML = responseObj.userDate;
					    }
					}
					retryGCSDRequestCount = 0;
			   },
			   failure: function(result, response){
					if(retryGCSDRequestCount < 5) {
						retryGCSDRequestCount++;
						setTimeout("getCurrentSpaceDetails()", 100);
					}
			   }
			});
		} else {
			showPopup();
		}
	}
}

//To read the cookie value by name
function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return '';
}

// To get last blog entry date links
function getLastBlogItDateLinks(blogIts){
	var lastBlogEntryLinks;			
	if (document.all){
		lastBlogEntryLinks = document.getElementsByTagName('td');
		for(var elementIndex = 0; elementIndex < lastBlogEntryLinks.length; elementIndex++){
			if(lastBlogEntryLinks[elementIndex].name == 'lastBlogEntryLink'){
				lastBlogEntryLinks[elementIndex].innerHTML = showLastBlogItLink(getLastBlogItJSONObjectForPersonId(blogIts, lastBlogEntryLinks[elementIndex].id.replace(/teammate_/, '')));
			}
		}
	} else {
		lastBlogEntryLinks = document.getElementsByName('lastBlogEntryLink');
		for(var elementIndex = 0; elementIndex < lastBlogEntryLinks.length; elementIndex++){
			lastBlogEntryLinks[elementIndex].innerHTML = showLastBlogItLink(getLastBlogItJSONObjectForPersonId(blogIts, lastBlogEntryLinks[elementIndex].id.replace(/teammate_/, '')));
		}
	}
}

// To get last blog it json object for person from json array
function getLastBlogItJSONObjectForPersonId(blogItsJSONArray, id){
	for(var index = 0; index < blogItsJSONArray.length; index++){
		if(blogItsJSONArray[index].userId == id){
			return blogItsJSONArray[index];
		}
	}
}

// To get last blog entry links of all members of current space
function getLastBlogIts(spaceId){
	Ext.Ajax.request({
		url: JSPRootURL +'/blog/view/get_last_blogit_date_link',
		params: {spaceId : spaceId},
		method: 'POST',
		success: function(result, request){
			if(result.responseText != "false") {
				var allBlogIts = eval("("+ result.responseText +")");
				getLastBlogItDateLinks(allBlogIts);
			}
			retryLastBlogitRequestCount = 0;
		},
		failure: function(result, response){
			if(retryLastBlogitRequestCount <= 5) {
				retryLastBlogitRequestCount++;
				setTimeout("getLastBlogIts("+spaceId+")", 1000);
			}
		}
	});
}

// Generating last blog entry link for project blog
function showLastBlogItLink(responseObject){
	if(responseObject)
		return "<a href='"+ JSPRootURL +"/blog/view/"+ responseObject.spaceId +"/"+ responseObject.userId +"/project/"
		+ responseObject.moduleId +"?module="+ responseObject.moduleId 
		+ "&teamMemberId=" + responseObject.userId + "'>" + responseObject.date + "</a>";
	else 
		return "";
}

// display an error message for deleted object (called for deleted context link on blog page)
function showErrorForContextLink(weblogEntryId) {
	if(document.getElementById('blogcontextLinkFor_'+weblogEntryId) != null) {
	    var contextLink = document.getElementById('blogcontextLinkFor_'+weblogEntryId).innerHTML;
	    var contextType = contextLink.substring(0,contextLink.indexOf(':'));
	    var contextValue = contextLink.substring(contextLink.indexOf(':'), contextLink.length-1);
		extAlert(errorTitle, contextType + ' <i>' + contextValue + '</i> is deleted!' , Ext.MessageBox.ERROR);
	}
}

// dipslay an error message for deleted form or form record (called for deleted context link on blog page)
function showErrorForFormContextLink(weblogEntryId) {
	if(document.getElementById('blogcontextLinkFor_'+weblogEntryId) != null) {
	    var contextLink = document.getElementById('blogcontextLinkFor_'+weblogEntryId).innerHTML;
	    var contextType = contextLink.substring(0,contextLink.indexOf('-'));
	    var contextValue = contextLink.substring(contextLink.indexOf(':')+1, contextLink.length-1);
		extAlert(errorTitle, '<i>'+ contextType + '</i>  or <i>' + contextValue + '</i> is deleted!' , Ext.MessageBox.ERROR);
	}
}

// display access denied message for user
function showAccessDeniedError(weblogEntryId) {
	if(document.getElementById('blogcontextLinkFor_'+weblogEntryId) != null) {
	    var contextLink = document.getElementById('blogcontextLinkFor_'+weblogEntryId).innerHTML;
	    var contextType = contextLink.substring(0,contextLink.indexOf('-'));
	    var contextValue = contextLink.substring(contextLink.indexOf(':')+1, contextLink.length-1);
		extAlert(errorTitle, msgFormat.format(accessDeniedError, "form data <i>"+contextType+"</i> ", "<i>"+contextValue+"</i>"), Ext.MessageBox.ERROR);
	}
}		
		
// to set target of link which added while blog it
function setLinkTarget() {
	var divs = document.getElementsByName('weblogEntryContentsDiv');
	for (var dindex = 0; dindex < divs.length; dindex++) {
		links = divs[dindex].getElementsByTagName('a');		
		for (var index = 0; index < links.length; index++) {	
			links[index].target = "_blank";
		}
	}
}

// to display alert for deleted object
function showObjectDeletedError(objectName, weblogEntryId, deletedOf){
	if(document.getElementById('deleted'+objectName+'_'+weblogEntryId) != null) {
		var objectValue = document.getElementById('deleted'+objectName+'_'+weblogEntryId).innerHTML;
	    objectValue = objectValue.substring(objectValue.indexOf(':'), objectValue.length-1);
	    if (deletedOf != '' && document.getElementById('blogcontextLinkFor_'+weblogEntryId) != null) {
		    var contextLink = document.getElementById('blogcontextLinkFor_'+weblogEntryId).innerHTML;
		    extAlert(errorTitle, objectName + ': <i>' + objectValue + '</i> of ' + contextLink + ' is deleted!' , Ext.MessageBox.ERROR);
	    } else {
		    extAlert(errorTitle, objectName + ': <i>' + objectValue + '</i> is deleted!' , Ext.MessageBox.ERROR);
	    }
	}
}

// To check the object status and redirect to the object URL, if it is deleted then show error message alert
function checkAndRedirect(weblogEntryId, objectId, objectType, objectName, isUserDeleted, isProjectDeleted){
	if(moduleId == '60') { //To show blog entries of task(on click) displayed in blog entries in right pane
		document.getElementById('blogcontextLinkFor_'+weblogEntryId).href=JSPRootURL + '/blog/view/' + weblogEntryId+ '/' + objectId+ '/' + objectType+'?module=60';
		document.location.href=document.getElementById('blogcontextLinkFor_'+weblogEntryId).href;
	} else {
	Ext.Ajax.request({
	   url: JSPRootURL +'/blog/view/check_and_redirect?module='+moduleId,
	   params: { weblogEntryId: weblogEntryId, objectId: objectId, objectType: objectType, 
	   			 objectName: objectName, isUserDeleted: isUserDeleted, isProjectDeleted: isProjectDeleted },
	   method: 'POST',
	   success: function(result, request){
			var responseText = result.responseText;
			if(responseText != 'false'){
			    eval(responseText);
			} else {
			    extAlert(errorTitle, checkAndRedirectErrorMsg, Ext.MessageBox.ERROR);
			}
	   },
	   failure: function(result, response){
		   extAlert(errorTitle, 'Server request failed... Please try again.', Ext.MessageBox.ERROR);
	   }
	});
   }
}

// To get title case string
function capitaliseMe(contextType) {
	if (contextType != null && contextType != '') {
		var changeCaseOf = contextType.substring(0,1).toUpperCase();
		contextType = changeCaseOf + contextType.substring(1,contextType.length);
		return contextType;
	} else { return ''; }
}

function focusCommentContent() {
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		if(commentContentPanel != null && commentContentPanel != 'undefined'){
	   		commentContentPanel.getComponent('contentForComment').focus(true);
	   		commentContentPanel.getComponent('contentForComment').updateToolbar();
	   	}
	}
}

function focusContent() {
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		if(contentPanel != null && contentPanel != 'undefined'){
	   		contentPanel.getComponent('content').focus(true);
	   		showHtmlSelectTags();
	   	}
	}
}

// displays a descriptive alert for what the work hours are shown
function describeMeForCurrentDate(workHours, capturedOnDate, showForMe) {
	var message = '';
	
	var userName = '<label id="mylabel" style="color:blue;">' + currentUserName + '</label>';
	var assignmentsName = '<label style="color:blue;">"' + selectedTaskForToolTip + '"</label>';
	if (showForMe == 'current') {
		if (workHours != '0 hrs') {
			if (workCapturedInfoForTask != 'undefined') {
				message = msgFormat.format(workCapturedInfoForTask, userName , workHours, capturedOnDate, assignmentsName);
			}
		} else {
			if (workNotCapturedInfoForTask != 'undefined') {
				message = msgFormat.format(workNotCapturedInfoForTask, userName, capturedOnDate, assignmentsName);
			}
		}
	} else {
		if(workHours != '0 hrs') {
			if (workCapturedInfoForAllassignments != 'undefined') {
				message = msgFormat.format(workCapturedInfoForAllassignments, userName, workHours, assignmentsName, capturedOnDate);
			}
		} else {
			if (workNotCapturedInfoForAllAssignments != 'undefined') {
				message = msgFormat.format(workNotCapturedInfoForAllAssignments, userName, assignmentsName, capturedOnDate);
			}
		}
	}
	extAlert('Information', message , Ext.MessageBox.INFO);
}

// load the blog entries to right blog panel for selected objects
function loadBlogEntriesForAssignment(fromPage, needRefreshFlag, assignmentTreeNodeId, childTaskList, spaceId){
	loadBlogEntriesForObject(fromPage, needRefreshFlag, assignmentTreeNodeId, childTaskList, spaceId);
	loadDetails(assignmentTreeNodeId, document.getElementById('taskDetailsDivRight'), isLoadTask);
}

function loadBlogEntriesForObject(fromPage, needRefreshFlag, forObjectId, childObjectsList, spaceId){
	if(needRefreshFlag && fromPage == 'myAssignments'){
		needRefresh = false;
		refreshAssignmentGridAndCountFromStart(false);
	} 
	loadBlogEntriesForAssignmentObject(forObjectId, childObjectsList, spaceId);
	
}

// method to load and show the blog entries on a page
function showBlogEntriesForTaskOnPage(moduleId, taskId, blogEntriesDivId, showExpanCollapseImage, showPersonImage, showCommentLink, startDate, endDate){
	var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	var divIdForBlogEntries = blogEntriesDivId != null ? blogEntriesDivId : 'TaskBlogDiv';
	var blogCount;
	Ext.Ajax.request({
		   url: JSPRootURL +'/blog/entries/show_blog_entries_for_task?module='+moduleId,
		   params: { moduleId : moduleId, taskId : taskId, showExpanCollapseImage: showExpanCollapseImage, 
		   			 showPersonImage: showPersonImage, showCommentLink: showCommentLink, startDate: startDate, endDate: endDate },
		   method: 'POST',
		   success: function(result, request){		   	   
			   if(result.responseText != "") {
					var responseText = result.responseText.replace(docTypeTag, '');	
					document.getElementById(divIdForBlogEntries).innerHTML = responseText;	
					var scriptTag = '<script type="text/javascript">';
					var javaScriptCode = responseText;
					javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
				    
					eval(javaScriptCode.replace(/\n/g, ''));
					if(blogEntryIds != '' && blogEntryIds != null){
						if( typeof(document.getElementById('entryViewLink')) != 'undefined' && document.getElementById('entryViewLink') != null){
							if( typeof(actionsIconEnabled) != 'undefined'  && actionsIconEnabled && typeof(showTitlesOnlyImageOn) != 'undefined' && typeof(showTitlesOnlyLink) != 'undefined' && typeof(showTitlesOnlyImageOver) != 'undefined'){
								document.getElementById('entryViewLink').innerHTML="<a id='showTitlesLinkId' showTitlesLinkId.onmouseover='document.showTitlesImage.src='"+showTitlesOnlyImageOver+"' showTitlesLinkId.onmouseout='document.showTitlesImage.src='"+showTitlesOnlyImageOn+"' href='javascript:showHideEntryText(false);'><img hspace='0' border='0' name='showTitlesImage' src='"+showTitlesOnlyImageOn+"' title='"+showTitlesOnlyLink+"' />"+showTitlesOnlyLink+"</a>";
							} else if(typeof showTitlesOnlyLink != 'undefined'){ 
								document.getElementById('entryViewLink').innerHTML="<a href='javascript:showHideEntryText(false);'>"+showTitlesOnlyLink+"</a>";
							}								
						} else {
							var entryViewLink = document.createElement('span');
							entryViewLink.id = 'entryViewLink';
							if( typeof(actionsIconEnabled) != 'undefined'  && actionsIconEnabled && typeof(showTitlesOnlyImageOn) != 'undefined' && typeof(showTitlesOnlyLink) != 'undefined' && typeof(showTitlesOnlyImageOver) != 'undefined') {
								entryViewLink.innerHTML="<a id='showTitlesLinkId' showTitlesLinkId.onmouseover='document.showTitlesImage.src='"+showTitlesOnlyImageOver+"' showTitlesLinkId.onmouseout='document.showTitlesImage.src='"+showTitlesOnlyImageOn+"' href='javascript:showHideEntryText(false);'><img hspace='0' border='0' name='showTitlesImage' src='"+showTitlesOnlyImageOn+"' title='"+showTitlesOnlyLink+"' />"+showTitlesOnlyLink+"</a>";
							} else if(typeof(showTitlesOnlyLink) != 'undefined'){
								entryViewLink.innerHTML="<a href='javascript:showHideEntryText(false);'>"+showTitlesOnlyLink+"</a>";								
							}
							document.getElementById('toolbox-item').appendChild(entryViewLink);
						}
					} 
					setLinkTarget();
			   } else {
			      document.getElementById(divIdForBlogEntries).innerHTML = noBlogEntryFoundForTask;
			   }
		   },
		   failure: function(result, response){
		   	   document.getElementById(divIdForBlogEntries).innerHTML = "Ineternal server Error!";
		   }
	});
}

function createMultipleWorkSubmittedTable() {
	if(typeof blogEntryIds != 'undefined'){
		blogEntryIds = blogEntryIds.substring(0, (blogEntryIds.length-1));
		var blogEntryIdsArray = blogEntryIds.split(',');
		var multipleWorkSubmitted = null;
		var loadingMutlipleWorkText = '<font color="blue" style="size: 10px;">Loading Multiple Work Submitted Details... </font>';
		loadingMutlipleWorkText += '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	
		if(document.getElementById("multipleWorkSubmitted_"+blogEntryIdsArray[index]) != null) {
			document.getElementById("divmultipleWorkSubmitted_"+blogEntryIdsArray[index]).innerHTML = loadingMutlipleWorkText;
			multipleWorkSubmitted = document.getElementById("multipleWorkSubmitted_"+blogEntryIdsArray[index]).value;
		}
		if(index < blogEntryIdsArray.length){
			getMultipleWorkSubmittedTable(blogEntryIdsArray[index],multipleWorkSubmitted);
		}
	}
}

// Method to show the multiple work captured table generated for blog entry if multiple work has been captured
function getMultipleWorkSubmittedTable(blogEntryId, multipleWorkSubmitted) {
	if(multipleWorkSubmitted !=null && multipleWorkSubmitted != "") {
		var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
		var url = JSPRootURL +'/blog/MultipleWorkSubmittedEntries/get_multiple_worksubmitted?module='+moduleId;
		Ext.Ajax.request({
		   url: url,
		   params: {multipleWorkSubmitted : multipleWorkSubmitted},
		   method: 'POST',
		   success: function(result, request){
		  	  	  var responseText = result.responseText.replace(docTypeTag, ''); 
			  	  document.getElementById("divmultipleWorkSubmitted_"+blogEntryId).innerHTML = responseText;
			  	  index++;
			  	  createMultipleWorkSubmittedTable();
		   },
		   failure: function(result, response){
			   extAlert(errorTitle, 'Server request failed... Please try again.', Ext.MessageBox.ERROR);
		   }
		});
	} else {
	  	  index++;
	  	  createMultipleWorkSubmittedTable();
	}
}

// Escaping styles from blogs and comments html content
function getStylesEscapedFromContent(contentText){
	var tempElement = document.createElement('div');
	tempElement.id = 'tempElement';
	tempElement.style.display = 'none';
	
	document.getElementsByTagName('body')[0].appendChild(tempElement);
	tempElement.innerHTML = contentText;
	
	var styleTags = tempElement.getElementsByTagName('style');
	// Comment out styles from all style tags in content text
	for(var styleTagIndex = 0; styleTagIndex < styleTags.length; styleTagIndex++){
		var styleTagContent = styleTags[styleTagIndex].innerHTML;
		styleTagContent = '/*' + styleTagContent.replace(/\/\*/g, '').replace(/\*\//g, '') + '*/';
		styleTags[styleTagIndex].innerHTML = styleTagContent;
	}
	
	var stylesEscapedContent = document.getElementById('tempElement').innerHTML;
	document.getElementsByTagName('body')[0].removeChild(tempElement);
	return stylesEscapedContent;
}

// Delete comment
function deleteComment(commentId, weblogEntryId){
	var url = JSPRootURL +'/blog/AddWeblogEntryComment/Delete'+(new Date()).getTime();
	Ext.Ajax.request({
	   url: url+'?module='+moduleId,
	   params: { module : moduleId, commentId : commentId},
	   method: 'POST',
	   success: function(result, request){
	   	   if( result.responseText == "true") {
	   	   	    loadAddedComment(weblogEntryId, 'comment');
	   	   } else if( result.responseText == "false"){
	   	   		extAlert(errorTitle, faileToDeleteMessage, Ext.MessageBox.ERROR);
	   	   }
	   },
	   failure: function(result, response){
		   extAlert(errorTitle, faileToDeleteMessage, Ext.MessageBox.ERROR);			   
	   }
	});
}
