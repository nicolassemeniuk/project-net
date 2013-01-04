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
var readyTosendRequest = false;
var selectedMembersCount = 0;
var windowHeight;
var projectIds = '';
var memberIds = '';
var inviteMemberEmailIds = '';
var projectNamesList = '';
var firstNameChange = false;
var lastNameChange = false;
var emailAddressChange = false;
var searchUserChange = false;
var clickedRowId;
var businessMembers;
var firstNameFlag = false;
var lastNameFlag = false;
var emailAddressFlag = false;
var searchUserFlag = false;
var memberIdList = '';

// adding member's emails with comma separated list.
function concatanateCheckedUsers(email, object){
	if(object.checked) {
		if(addToInvitedList.indexOf(email) == -1) {
			addToInvitedList += email + ",";
			selectedMembersCount++;
			document.getElementById('selectedCountSpan').innerHTML = selectedMembersCount;
		}
	} else {
		 addToInvitedList = addToInvitedList.replace(email + ",",''); 	
		 if(selectedMembersCount > 0) {
			 selectedMembersCount = selectedMembersCount-1;
			 document.getElementById('selectedCountSpan').innerHTML = selectedMembersCount;
		 }
	}
	document.getElementById('checkListId').value = addToInvitedList;
}

// this method calls transferCheckListToInviteeList() on event success
function prepareCheckList() {
	fireEvent(document.getElementById('checkListId'), 'change');
}

// this method trandform a business member to invitee list
function transferCheckListToInviteeList() {
	fireEvent(document.getElementById('transformToInvitedList'), 'click');
		
}

// this method desables the moved members
function moveMembers(){
	if(readyTosendRequest) {
		if(selectedMembersCount > 0){
		showLoadingDiv(transformUserMsg);
		var idsToDisableArray = document.getElementById('checkListId') != null ? document.getElementById('checkListId').value.split(',') : '';
			if(idsToDisableArray.length <= 0) {
				return false;
			}
			for(var i=0; i < idsToDisableArray.length; i++){
				if(document.getElementsByName('eml_'+idsToDisableArray[i])[0] != null){
					document.getElementsByName('eml_'+idsToDisableArray[i])[0].disabled = true;
				}
			}
			addToInvitedList = '';
			Ext.Ajax.request({
					url: JSPRootURL + '/directory/LoadMembers/move_member_to_inviteelist',
					params: {checkslist : document.getElementById('checkListId').value},
					method: 'POST',
					success: function(result, request) {
						evaluateScript(result.responseText);
				    	document.getElementById('inviteeList').innerHTML = result.responseText; 
				    	removeLoading();
					}, 
					failure: function(result, response) {
						extAlert(errorTitle, movingmemberFailureMsg, Ext.MessageBox.ERROR);
					}
			});
		} else {
			extAlert(errorAlertTitle, transformUserErrorMsg , Ext.MessageBox.ERROR);
		}
	}
}

// this method create json string of new invittee and set the details and calls addNewInvitee() on event success
function prepareNewInvitee() {
	if(document.getElementById("firstName").value == firstNameMsg || document.getElementById("firstName").value.trim() == ''){
		extAlert(errorAlertTitle, firstNameValidation , Ext.MessageBox.ERROR);
		return;
	}else if(document.getElementById("lastName").value == lastNameMsg || document.getElementById("lastName").value.trim() == ''){
		extAlert(errorAlertTitle, lastNameValidation , Ext.MessageBox.ERROR);
		return;
	}else if(document.getElementById("emailAddress").value == emailMsg || document.getElementById("emailAddress").value == ''){
		extAlert(errorAlertTitle, emailValidation , Ext.MessageBox.ERROR);
		return;
	}
	if(checkEmail(document.getElementById("emailAddress"), emailValidation) && readyTosendRequest) {
		showLoadingDiv('Adding New Invitee');
		var newInviteeJsonObject = '{"fname" : "'+ document.getElementById("firstName").value.replace(/\"/g, '\\\"')
								+ '","lname" : "'+ document.getElementById("lastName").value.replace(/\"/g, '\\\"')
								+ '","email" : "'+ document.getElementById("emailAddress").value +'"}';
		if(checkExistanceOfEmail()){
			Ext.Ajax.request({
				url: JSPRootURL + '/directory/LoadMembers/add_member_to_inviteelist',
				params: {newInviteeJsonObject : newInviteeJsonObject},
				method: 'POST',
				success: function(result, request) {
					evaluateScript(result.responseText);
			    	document.getElementById('inviteeList').innerHTML = result.responseText; 
			       	firstNameFlag = false;
					lastNameFlag = false;
					emailAddressFlag = false;
					searchUserFlag = false;
					setDefaultValue();
					removeLoading();
				}, 
				failure: function(result, response) {
					extAlert(errorTitle, addingNewInviteeFailureMsg, Ext.MessageBox.ERROR);
				}
			});
			readyTosendRequest = false;
		}
	}
}

/* enable the member in business member list on removing it from invtee list*/
function enableMemberOnRemove(email) {
	Ext.Ajax.request({
			url: JSPRootURL + '/directory/LoadMembers/remove_member',
			params: {emailId : email},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('inviteeList').innerHTML = result.responseText; 
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, 'Error while removing member', Ext.MessageBox.ERROR);	
			}
	});
	if(document.getElementsByName('eml_'+email)[0] != null){
	 	document.getElementsByName('eml_'+email)[0].disabled = false;
	 	document.getElementsByName('eml_'+email)[0].checked = false;
	}
 	if(selectedMembersCount > 0) {
	 	selectedMembersCount = selectedMembersCount - 1;
		document.getElementById('selectedCountSpan').innerHTML = selectedMembersCount;
	}
	document.getElementById('checkListId').value = ''; 
}

function showResourceAllocation(personID) {
    var url = JSPRootURL + '/resource/ResourceAllocations.jsp?module=140&personID='+personID + '&startDate='+date;
    openwin_large('resource_allocation', url);
}

// display an alert when user trying to proceed without invitee
function noInvitees() {
	extAlert(errorAlertTitle, addUserToInviteeListErrorMsg , Ext.MessageBox.ERROR);
	return false;
}

function clearNewInvited(inviteeCounts) {
	inviteesCount = inviteeCounts;
	document.getElementById("emailAddress").value="";
	document.getElementById("firstName").value="";
	document.getElementById("lastName").value="";
	if(document.getElementById("inviteCountSpan") != null) {
		if(document.getElementById("sendInvitation") != null && inviteeCounts >= 0)
		document.getElementById("inviteCountSpan").innerHTML = inviteeCounts != '' ? inviteeCounts : 0;
	}
	removeLoading();
	setInviteeListDivHeight();
}

// set the search text on enter key press and call searchBusinessMembers() on event success
function prepareSearchText() {
	if(readyTosendRequest) {
		showLoadingDiv(loadingBusinessMemberErrorMsg);
		Ext.Ajax.request({
			url: JSPRootURL + '/directory/LoadMembers/load_user',
			params: {directoryId : document.getElementById('businessDirectory').value, searchUser : document.getElementById('searchUser').value},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('businessMemberList').innerHTML = result.responseText; 
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, searchingInviteeFailureMsg, Ext.MessageBox.ERROR);	
			}
		});
	}
	setBusinessMemberListDivHeight();
	selectedMembersCount = 0;		
}

// search the business members
function searchUsers(){
	if(readyTosendRequest) {
		showLoadingDiv(loadingBusinessMemberErrorMsg);
		fireEvent(document.getElementById('loadUsers'), 'click');
		readyTosendRequest = false;
	}
}

// send the project name for adding project
function sendprojectName(){
	if(readyTosendRequest) {
		var selected_index = document.selectProjectForm.elements["projectDirectory"].selectedIndex;
		showLoadingDiv(addingProjectOrSubbusinessErrorMsg +document.selectProjectForm.elements["projectDirectory"].options[selected_index].text + '...');
		fireEvent(document.getElementById('projectName'), 'click');
		readyTosendRequest = false;
	}
}

// search the project and subbusiness. 
function searchProjectAndSubbusiness(){
	if(readyTosendRequest) {
		showLoadingDiv(searchUserErrorMsg);
		fireEvent(document.getElementById('addProjectAndSubbusinessList'), 'click');
		readyTosendRequest = false;
	}
	
}

// prepare a json string of addition comment in mail
function prepareMailDetails() {
	if(inviteesCount != '' && inviteesCount > 0) {
		var additionalMessage = removeSpecialCharacter(document.getElementById("additionalMessage").value);
		var sendInvitationDetails = '{"isSendMail" : "'+ document.getElementById("sendToAll").checked
								+ '","additionalComment" : "'+ additionalMessage +'"}';
		document.getElementById('sendInvitationDetails').value = sendInvitationDetails;
		document.sendInvitations.submit();
	} else {
		return noInvitees();
	}
}

// enable or disable the additional comment box
function enableAdditionalComment() {
	if(document.getElementById('additionalMessage').style.display != 'none') {
		document.getElementById('additionalMessage').style.display = 'none';
	} else {
		document.getElementById('additionalMessage').style.display = '';
	}
}

// remove loading top div
function removeLoading() {
	readyTosendRequest = true;
	setBusinessMemberListDivHeight();
	removeLoadingDiv();
}

// remove loading top div
function removeBulkLoading() {
	readyTosendRequest = true;
	removeLoadingDiv();
}

// calculate and set the height to div according user's screen resolution
function setDivHeights() {
		var size = new Array();
		size = getWindowsWidthHeight();
		windowHeight = size[1];
		businessMemberListHeight = (windowHeight-342);
		inviteeListHeight = (businessMemberListHeight/3)-23;
		participantsListHeight = (businessMemberListHeight-inviteeListHeight-23-33-20-64);
		if(inviteeListHeight > 75){
			document.getElementById('businessMemberList').style.height = businessMemberListHeight + 'px';
			document.getElementById('inviteeList').style.height = inviteeListHeight + 'px';
			document.getElementById('participantsList').style.height = participantsListHeight + 'px';
			document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
		}else{
			businessMemberListHeight = 290;
			inviteeListHeight = 75;
			if(document.getElementById('businessMemberList') != null)
			document.getElementById('businessMemberList').style.height = 290 + 'px';
			if(document.getElementById('inviteeList') != null)
			document.getElementById('inviteeList').style.height = 75 + 'px';
			if(document.getElementById('participantsList') != null)
			document.getElementById('participantsList').style.height = 75 + 'px';
			document.getElementsByTagName('html')[0].style.overflowY = 'scroll';
		}
}

// Set Div widths on page render
function setDivWidths() {
	var width = getWindowWidth() -196 ;
	
	if(document.getElementById('businessMemberList')){
		document.getElementById('businessMemberList').style.width = width*0.47 +'px'; 
		document.getElementById('businessMemberList').style.overflow = 'auto';
	}
	
	if(document.getElementById('inviteMemberSearchDiv')){
		document.getElementById('inviteMemberSearchDiv').style.width = width*0.47 +'px'; 
		document.getElementById('inviteMemberSearchDiv').style.overflowX = 'auto';
		document.getElementById('inviteMemberSearchDiv').style.overflowY = 'hidden';
	}
	
	if(document.getElementById('inviteeList')){
		document.getElementById('inviteeList').style.width =  width*0.45 +'px';
		document.getElementById('inviteeList').style.overflow = 'auto';
	}

	if(document.getElementById('participantsList')){
		document.getElementById('participantsList').style.width =  width*0.45 +'px';
		document.getElementById('participantsList').style.overflow = 'auto';
	}
	
	if(document.getElementById('adduserDiv')){
		document.getElementById('adduserDiv').style.width =  width*0.45 +'px';
		document.getElementById('adduserDiv').style.overflowX = 'auto';
	}
}

// set inviteelist div height when zone refreshed
function setInviteeListDivHeight() {
	document.getElementById('inviteeList').style.height = inviteeListHeight + 'px';
}

// set businessMemberlist div height when zone refreshed
function setBusinessMemberListDivHeight() {
	if(document.getElementById('businessMemberList') != null && typeof(businessMemberListHeight) != 'undefined'){
		document.getElementById('businessMemberList').style.height = businessMemberListHeight + 'px';
	}
}

// adding project.
function addProjectOrSubbusinessColumn() {
	var alreadyAddedProject;
	if(projectIds != null){
		alreadyAddedProject = projectIds.split(',');
	}
	if(document.getElementById('projectDirectory').value == "-1"){
		extAlert(errorAlertTitle, selectProjectOrSubbusinessToAdd , Ext.MessageBox.ERROR);
	} else if (document.getElementById('projectDirectory').value == "-2"){
		extAlert(errorAlertTitle, noProjectOrSubbusinessErrorMeg , Ext.MessageBox.ERROR);
	} else if (alreadyAddedProject.indexOf(document.getElementById('projectDirectory').value) >= 0){
		extAlert(errorAlertTitle, selectedProjectOrSubbusinessAlreadyAddedErrorMeg , Ext.MessageBox.ERROR);
	} else {
		if(readyTosendRequest) {
    		var selected_index = document.selectProjectForm.elements["projectDirectory"].selectedIndex;
			showLoadingDiv(addingProjectOrSubbusinessErrorMsg + document.selectProjectForm.elements["projectDirectory"].options[selected_index].text + '...');
			persistSelectedRecords('afterProjectAddPersistPrevious');
		}
	}
}

// set the previously selected members and roles
// also set the value in hidden field members_projectid to get the selected values on submiting page
function setSelectedRecords() {
	var arr_selectedChkBoxIds = selectedChkBoxIds.split(',');
	var arr_selectedRoleIds = selectedRoleIds.split(',');
	var arr_countAsPerProject = countAsPerProjects.split(',');
	var projectId;
	var memberId;
	if(arr_selectedChkBoxIds.length > 0) {
		for(var chkIndex = 0; chkIndex < arr_selectedChkBoxIds.length-1; chkIndex++) {
			if(document.getElementById('chk_' + arr_selectedChkBoxIds[chkIndex]) != null) {
				document.getElementById('chk_' + arr_selectedChkBoxIds[chkIndex]).checked = true;
				document.getElementById('roles_' + arr_selectedChkBoxIds[chkIndex]).style.display = '';
				document.getElementById('rolesselect_' + arr_selectedChkBoxIds[chkIndex]).value = arr_selectedRoleIds[chkIndex];
				projectId = (arr_selectedChkBoxIds[chkIndex].split('_'))[0];
				memberId = (arr_selectedChkBoxIds[chkIndex].split('_'))[1];
				if(document.getElementById('members_'+projectId).value.indexOf(memberId)== -1){
					document.getElementById('members_' + projectId).value += memberId + ',';
				}
			}
		}
		
	}
	
	// for setting total member selected count for each project.
	if(arr_countAsPerProject.length > 0) {
		for(var chkIndex = 0; chkIndex < arr_countAsPerProject.length-1; chkIndex++) {
			var projectId = (arr_countAsPerProject[chkIndex].split('_'))[0];
			var count = (arr_countAsPerProject[chkIndex].split('_'))[1];
			if(document.getElementById('selected_'+projectId) != null && projectId.indexOf(document.getElementById('selected_'+projectId).innerHTML) >= 0){
				document.getElementById('selected_'+projectId).innerHTML = count;
			}
		}
	}
}

// create a json object of selected members and roles to display 
// on adding new project or sub business along with selected project 
// or sub business id
function persistSelectedRecords(persistFor) {
	var projectsIds = projectIds.split(',');
	var members = memberIds.split(',');
	var selectedRoles = '';
	var selectedChkBoxIds = '';
	var countAsPerProject = '';
	for(var index = 0; index < projectsIds.length-1; index++){
		 for(var mindex = 0; mindex < members.length-1 ; mindex++){
		 	if(document.getElementById('chk_' + projectsIds[index] + '_' +members[mindex]) != null 
		 		&& typeof(document.getElementById('chk_' + projectsIds[index] + '_' +members[mindex]) != 'undefined') 
		 		&& document.getElementById('chk_' + projectsIds[index] + '_' +members[mindex]).checked) {
		 		selectedChkBoxIds += projectsIds[index] + '_' +members[mindex] + ',';
		 		selectedRoles += document.getElementById('rolesselect_' + projectsIds[index] + '_' +members[mindex]).value + ',';
		 		
		 	}
		}
		if(document.getElementById('selected_'+projectsIds[index]) != null)
			countAsPerProject += projectsIds[index] +'_'+ (parseInt(document.getElementById('selected_'+projectsIds[index]).innerHTML.trim())) + ',';
	}
	var selectedRecords = '{"projectOrSubbusinessId" : ' + document.getElementById('projectDirectory').value + '' ;
	selectedRecords += ',"selectedChkBoxIds" : "' + selectedChkBoxIds + '"';
	selectedRecords += ',"selectedRoleIds" : "' + selectedRoles + '"';
	selectedRecords += ',"countAsPerProject" : "' + countAsPerProject + '"}';
	
	if(persistFor == 'afterProjectAddPersistPrevious'){
		document.getElementById('addProjectName').value = selectedRecords;
		Ext.Ajax.request({
			url: JSPRootURL + '/directory/ManageDirectoryInviteMember/add_project',
			params: {projectId : document.getElementById('projectDirectory').value, selectedRecords : selectedRecords},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('bulkInviteList').innerHTML = result.responseText; 
		    	setSelectedRecords();
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, addSpaceFailureMsg, Ext.MessageBox.ERROR);	
			}
		});
	} else if(persistFor == 'afterDirectoryChangePersistPrevious'){
		document.getElementById('persistanceValues').value = selectedRecords;
		Ext.Ajax.request({
			url: JSPRootURL + '/directory/ManageDirectoryInviteMember/load_member',
			params: {directoryId : document.getElementById('subBusinessProjectDirectory').value, searchUser : document.getElementById('searchUser').value, selectedRecords : selectedRecords},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('bulkInviteList').innerHTML = result.responseText; 
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, changeDirectoryLoadMemberFailureMsg, Ext.MessageBox.ERROR);	
			}
		});
	} else if(persistFor == 'afterProjectDeletePersistPrevious'){
		document.getElementById('persistanceValue').value = selectedRecords;
		Ext.Ajax.request({
			url: JSPRootURL + '/directory/ManageDirectoryInviteMember/remove_project',
			params: {projectId : document.getElementById('removeProjectName').value, selectedRecords : selectedRecords},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('bulkInviteList').innerHTML = result.responseText; 
		    	setSelectedRecords();
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, deleteSpaceFailureMsg, Ext.MessageBox.ERROR);	
			}
		});
	}
}

function refreshPage() {
	self.document.location = self.document.location;
}

// set the search text on enter key press and call searchBusinessMembers() on event success
function prepareSearchTextForManageDirectories() {
	if(readyTosendRequest) {
		showLoadingDiv(loadingBusinessMemberErrorMsg);
		persistSelectedRecords('afterDirectoryChangePersistPrevious');
		setSelectedRecords();
	}
}

//select all checkboxes 
function enableAllMembers(projectId, fromPage) {
	var members;
	if(fromPage == 'invitemembers'){
		members = inviteMemberEmailIds.split(',');
	}else{
		members = memberIdList.split(',');
	}
	for(var index = 0; index < members.length; index++){
		if(fromPage == 'invitemembers'){
			if(document.getElementById('chk_'+projectId+'_'+members[index]) != null && !document.getElementById('chk_'+projectId+'_'+members[index]).checked){
				document.getElementById('chk_'+projectId+'_'+members[index]).checked = true;
				document.getElementById('roles_'+projectId+'_'+members[index]).style.display = '';
				document.getElementById('selected_'+projectId).innerHTML = (parseInt(document.getElementById('selected_'+projectId).innerHTML.trim()) + 1) ;
				if(document.getElementById('members_'+projectId).value.indexOf(members[index])== -1){
					document.getElementById('members_'+projectId).value += members[index] + ',';
				}
			}
		}else{
			if(document.getElementById('chk_'+projectId+'_'+members[index]) != null && document.getElementById('members_'+projectId).value.indexOf(members[index])== -1){
				document.getElementById('chk_'+projectId+'_'+members[index]).checked = false;
				document.getElementById('members_'+projectId).value += members[index] + ',';
				document.getElementById('img_'+projectId+'_'+members[index]).style.visibility = 'visible';
				document.getElementById('removed_'+projectId).style.visibility = 'visible';
				document.getElementById('removed_'+projectId).innerHTML = (parseInt(document.getElementById('removed_'+projectId).innerHTML.trim()) - 1) ;
			}
		}
	}	
 }

//enable role list on selecting checkboxes.
function enableRoleList(projectId, memberId, totalProjects){
	var relatedProjectCheck = document.getElementById('chk_'+projectId+'_'+memberId);
	var relatedProjectSelect = document.getElementById('roles_'+projectId+'_'+memberId);
	var actualRoleSelect = document.getElementById('rolesselect_'+projectId+'_'+memberId);
	if(relatedProjectCheck.checked){
		relatedProjectSelect.style.display = '';
		document.getElementById('members_'+projectId).value += memberId + ',';
		document.getElementById('selected_'+projectId).innerHTML = (parseInt(document.getElementById('selected_'+projectId).innerHTML.trim()) + 1) ;
	}else{
		relatedProjectSelect.style.display = 'none';
		document.getElementById('members_'+projectId).value += document.getElementById('members_'+projectId).value.replace(memberId, '');
		document.getElementById('selected_'+projectId).innerHTML = (parseInt(document.getElementById('selected_'+projectId).innerHTML.trim()) - 1) ;
	}
}

// call when submitting data
function creatJSONbject(){
	var newMembersJsonObject = '';
	var projectsIds = projectIds.split(',');
	showLoadingDiv(processingErrorMsg);
	for(var index = 0; index < projectsIds.length-1; index++){
		if(document.getElementById('members_'+projectsIds[index]) != null && document.getElementById('members_'+projectsIds[index]).value != '') {
			 var members = document.getElementById('members_'+projectsIds[index]).value.split(',');
			 for(var mindex = 0; mindex < members.length-1 ; mindex++){
			 	var newMembersJsonObjectPart = '{"selectedrole" : "'+document.getElementById('rolesselect_' + projectsIds[index] + '_' + members[mindex]).value
									+ '","memberId" : "'+ members[mindex] +'"},';
							
				if(document.getElementById(projectsIds[index]) != null){
					document.getElementById(projectsIds[index]).value += newMembersJsonObjectPart;
				}
			 }
		}
	}
	
	for(var index = 0; index < projectsIds.length-1; index++){
		if(document.getElementById(projectsIds[index]) != null && document.getElementById(projectsIds[index]).value != '') {
			var list = document.getElementById(projectsIds[index]).value.substring(0,document.getElementById(projectsIds[index]).value.length-1);
			newMembersJsonObject += '"' + projectsIds[index] + '":[' + list +'],';
		}
	}
	
	if(newMembersJsonObject != '') {
		newMembersJsonObject = '{'+newMembersJsonObject.substring(0, newMembersJsonObject.length-1)+'}';
		document.getElementById('selectedProjectsList').value = newMembersJsonObject;
		prepareBulkMailDetails(newMembersJsonObject);
	} else {
		if(projectsIds.length == 1){
			extAlert(errorAlertTitle, selectProjectOrSubbusinessToAdd , Ext.MessageBox.ERROR);
		}else{
			extAlert(errorAlertTitle, selectMemberToInviteErrorMsg , Ext.MessageBox.ERROR);
		}
		removeLoadingDiv();
		return false;
	}
}

// for displaying div
function displayDivNone(projectLists){
	for(var index = 0; index < projectLists.length; index++ ){
		if(document.getElementById(projectLists[index]) != null){
			document.getElementById(projectLists[index]).style.display = 'none';
			if(document.getElementById('tabs_'+projectLists[index]) != null){
				document.getElementById('tabs_'+projectLists[index]).className = 'projectDeactiveTab tab_cls';
			} if(document.getElementById('leftsideTab_'+projectLists[index]) != null){
				document.getElementById('leftsideTab_'+projectLists[index]).className = 'leftsidedeactivetab'; 
			} if(document.getElementById('rightsideTab_'+projectLists[index]) != null){
				document.getElementById('rightsideTab_'+projectLists[index]).className = 'rightsidedeactivetab';
			}
		}	
	}
}

// for cheking div is displayed or not
function checkDiv(divId){
	var projectLists = projectNamesList.split(',');
	displayDivNone(projectLists);
	for(var projectIndex = 0; projectIndex < projectLists.length; projectIndex++ ){
		if(projectLists[projectIndex] == divId && document.getElementById(divId).style.display == 'none'){
			document.getElementById(divId).style.display = 'block';		
			document.getElementById('tabs_'+projectLists[projectIndex]).className = 'projectActiveTab tab_cls';
			document.getElementById('leftsideTab_'+projectLists[projectIndex]).className = 'leftsideactivetab'; 
			document.getElementById('rightsideTab_'+projectLists[projectIndex]).className = 'rightsideactivetab';
			break;
		}
	}
}

// prepare a json string of addition comment in mail
function prepareBulkMailDetails() {
	var additionalMessage = removeSpecialCharacter(document.getElementById("additionalMessage").value);
	var sendInvitationDetails = '{"isSendMail" : "'+ document.getElementById("sendToAll").checked
							+ '","additionalComment" : "'+ additionalMessage +'"}';
	document.getElementById('sendInvitationDetails').value = sendInvitationDetails;
}

// remove project from list
function removeProjectNameFromList(projectId) {
	 document.getElementById('removeProjectName').value = projectId;
	 persistSelectedRecords('afterProjectDeletePersistPrevious');
	 setSelectedRecords();
}

// call on submiting bulk invitation responsibility
function sendInvitationAlert(){
	document.form.submit();
}

// Set default value. 			
function setDefaultValue(){
	if(!firstNameFlag && document.getElementById('firstName') != null){
		document.getElementById('firstName').value = firstNameMsg;
		document.getElementById('firstName').style.color = 'gray'
	} 
	if(!lastNameFlag && document.getElementById('lastName') != null){
		document.getElementById('lastName').value = lastNameMsg;
		document.getElementById('lastName').style.color = 'gray'
	}
	if(!emailAddressFlag && document.getElementById('emailAddress') != null){
		document.getElementById('emailAddress').value = emailMsg;
		document.getElementById('emailAddress').style.color = 'gray'
	}
	if(!searchUserFlag && document.getElementById('searchUser') != null){
		document.getElementById('searchUser').value = searchMsg;
		document.getElementById('searchUser').style.color = 'gray'		
	}
}

// Resetting value.
function resetValue(id, msg) {
	if(document.getElementById(id).value == '' ) {
		document.getElementById(id).value = msg;
		document.getElementById(id).style.color = 'gray';
	}
}

// adding roles in bulk with hidden field.
function concatenateCheckedUsersRolesInBulk(id , object, from){
var projId;
   if(from == 'onclick'){
	   if(object.checked){
			addToInvitedList += id + ",";
			projId = id.substring(0,id.indexOf("_"));
		} else{
			addToInvitedList = addToInvitedList.replace(id + ",", ''); 	
		}
		if(document.getElementById(projId+'_userRolescheckListId') != null && addToInvitedList != null){
			document.getElementById(projId+'_userRolescheckListId').value = addToInvitedList;
		}
	}else{
		addToInvitedList += id + ",";
		projId = id.substring(0,id.indexOf("_"));
		if(document.getElementById(projId+'_userRolescheckListId') != null && addToInvitedList != null){
			document.getElementById(projId+'_userRolescheckListId').value = addToInvitedList;
		}
	}
}

// Call for already checked roles from manage directories page.	
function allreadyCheck(){
	addEarlierRoles = true;
	var controls = document.getElementsByTagName('input'); 
	for (var controlsIndex = 0; controlsIndex < controls.length; controlsIndex++) {
		if (controls[controlsIndex].type == 'checkbox'){
			if(controls[controlsIndex].checked && !controls[controlsIndex].disabled){
				var ids = controls[controlsIndex].id;
				concatenateCheckedUsersRolesInBulk(ids, this, 'onload');
			}
		}  
	}
}

// set row backgroud color on click, and remove previously selected row's color.
function hl(row) {
	if(typeof(clickedRowId) != 'undefined' && clickedRowId != '')
		clickedRowId.className = clickedRowId.className.replace('heighlightRow', '');
	clickedRowId = row;
	row.className += ' heighlightRow ';
}

// set row backgroud color on mouse over.
function ov(row) {
	row.className += ' m_overRow ';
}

// remove row backgroud color on mouse out.
function ou(row) {
	row.className = row.className.replace('m_overRow', '');
}

// set div height.
function resizeBulkDiv(divid, width, height) {
	var size = getWindowsWidthHeight();
	if((size[1]-height) > 0 && (size[0]-width) > 0) {
		document.getElementById(divid).style.width =  size[0]-width + 'px';
		document.getElementById(divid).style.height = size[1]-height + 'px';
	}
}

// submit business form after changing business and apply search. 
function selectBusienssFormSubmit(){
	document.selectBusienssForm.submit();
}

// enabling additional comment link.
function enableCommentLink(){
	if(document.getElementById('sendToAll').checked){
		document.getElementById('sendMailLinkId').disabled = false;
		document.getElementById('additionalcomment').className = 'enableCommentTitle';
		document.getElementById('sendMailLinkId').href = "javascript:enableAdditionalComment();";
	} else {
		document.getElementById('sendMailLinkId').disabled = true;
		document.getElementById('sendMailLinkId').href = "#";
		document.getElementById('additionalMessage').style.display = "none";
		document.getElementById('additionalcomment').className = 'additionalCommentTitle';
	}
}

// Clear all values
function clearValue(id){
	if(!firstNameFlag && id == 'firstName') {
		firstNameFlag = true;
		document.getElementById(id).value = '';
		document.getElementById(id).style.color = 'black';
	} 
	if(!lastNameFlag && id == 'lastName'){
		lastNameFlag = true;
		document.getElementById(id).value = '';
		document.getElementById(id).style.color = 'black';
	} 
	if(!emailAddressFlag && id == 'emailAddress'){
		emailAddressFlag = true;
		document.getElementById(id).value = '';
		document.getElementById(id).style.color = 'black';
	} 
	if(!searchUserFlag && id == 'searchUser'){
		searchUserFlag = true;
		document.getElementById(id).value = '';
		document.getElementById(id).style.color = 'black';
	}
	
}

// set default values.
function setDefaultValues(id){
	if(typeof(firstNameMsg) != 'undefined' && firstNameFlag && document.getElementById(id).value == '' && id == 'firstName'){
		firstNameFlag = false;
		document.getElementById(id).value  = firstNameMsg;
		document.getElementById(id).style.color = 'gray';
	} 

	if(typeof(lastNameMsg) != 'undefined' && lastNameFlag && document.getElementById(id).value == '' && id == 'lastName'){
		lastNameFlag = false;
		document.getElementById(id).value  = lastNameMsg;
		document.getElementById(id).style.color = 'gray';
	} 

	if(typeof(emailMsg) != 'undefined' && emailAddressFlag && document.getElementById(id).value == '' && id == 'emailAddress'){
		emailAddressFlag = false;
		document.getElementById(id).value  = emailMsg;
		document.getElementById(id).style.color = 'gray';
	}
	
	if(typeof(searchMsg) != 'undefined' && searchUserFlag && document.getElementById(id).value == '' && id == 'searchUser'){
		searchUserFlag = false;
		document.getElementById(id).value = searchMsg;
		document.getElementById(id).style.color = 'gray';
	}
}

// Call when search for member needed.
function prepareSearchTextForRemoveMembers(){
	if(document.getElementById('projectDirectory').value != "-2") {
		showLoadingDiv(loadingMemberErrorMsg);
		Ext.Ajax.request({
			url: JSPRootURL + '/directory/ManageDirectoryRemoveMember/load_member',
			params: {searchUser : document.getElementById('searchUser').value},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('bulkInviteList').innerHTML = result.responseText; 
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, loadMemberForRemoveFailureMsg, Ext.MessageBox.ERROR);	
			}
		});
	} else{
		extAlert(errorAlertTitle, noSpacePresentForSearchMember , Ext.MessageBox.ERROR);
	}
}

// Call for adding project or subbusiness.
function addProjectOrSubbusinessColumnForRemoveMembers(){
	var alreadyAddedProject;
		if(projectIds != null){
			alreadyAddedProject = projectIds.split(',');
		}
		if(document.getElementById('projectDirectory').value == "-1"){
			extAlert(errorAlertTitle, selectProjectOrSubbusinessToAdd , Ext.MessageBox.ERROR);
		} else if (document.getElementById('projectDirectory').value == "-2"){
			extAlert(errorAlertTitle, noProjectOrSubbusinessErrorMeg , Ext.MessageBox.ERROR);
		} else if (alreadyAddedProject.indexOf(document.getElementById('projectDirectory').value) >= 0){
			extAlert(errorAlertTitle, selectedProjectOrSubbusinessAlreadyAddedErrorMeg , Ext.MessageBox.ERROR);
		} else {
    		var selected_index = document.selectProjectForm.elements["projectDirectory"].selectedIndex;
			showLoadingDiv(addingProjectOrSubbusinessErrorMsg + document.selectProjectForm.elements["projectDirectory"].options[selected_index].text + '...');
			document.getElementById('projectOrSubbusinessId').value = document.getElementById('projectDirectory').value;
			persistSelectedRecordsForRM('afterProjectAddPersistPrevious','addproject');
		}
}

// remove project from list
function removeProjectNameFromRemoveMemberList(projectId) {
	 document.getElementById('removeProjectName').value = projectId;
	 persistSelectedRecordsForRM('afterProjectDeletePersistPrevious','removeproject');
	 setSelectedRecordsForRM();
}

//enable role list on selecting checkboxes.
function enableMemberToRemove(projectId, memberId){
	var relatedProjectCheck = document.getElementById('chk_'+projectId+'_'+memberId);
	if(relatedProjectCheck.checked){
		document.getElementById('members_'+projectId).value = document.getElementById('members_'+projectId).value.replace(memberId, '');
		document.getElementById('removed_'+projectId).style.visibility = 'visible';
		if(document.getElementById('removed_'+projectId).innerHTML != '-1'){
			document.getElementById('removed_'+projectId).innerHTML = (parseInt(document.getElementById('removed_'+projectId).innerHTML.trim()) + 1) ;
		}else{
			document.getElementById('removed_'+projectId).innerHTML = (parseInt(document.getElementById('removed_'+projectId).innerHTML.trim()) + 1) ;
			document.getElementById('removed_'+projectId).style.visibility = 'hidden';
		}
		document.getElementById('img_'+projectId+'_'+memberId).style.visibility = 'hidden';
	}else{
		document.getElementById('members_'+projectId).value += memberId + ',';
		document.getElementById('removed_'+projectId).style.visibility = 'visible';
		document.getElementById('removed_'+projectId).innerHTML = (parseInt(document.getElementById('removed_'+projectId).innerHTML.trim()) - 1) ;
		document.getElementById('img_'+projectId+'_'+memberId).style.visibility = 'visible';
	}
}

// call when submitting data
function removeMemberJSONbject(){
	var newMembersJsonObject = '';
	var projectsIds = projectIds.split(',');
	showLoadingDiv(removingErrorMsg);
	for(var index = 0; index < projectsIds.length-1; index++){
		if(document.getElementById('members_'+projectsIds[index]) != null && document.getElementById('members_'+projectsIds[index]).value != '') {
			 var members = document.getElementById('members_'+projectsIds[index]).value.split(',');
			 for(var mindex = 0; mindex < members.length-1 ; mindex++){
			 	if(members[mindex].length != 0 && document.getElementById(projectsIds[index]) != null){
				 	var newMembersJsonObjectPart = '{"memberId" : "'+ members[mindex] +'"},';
					document.getElementById(projectsIds[index]).value += newMembersJsonObjectPart;
				}
			 }
		}
	}
	
	for(var index = 0; index < projectsIds.length-1; index++){
		if(document.getElementById(projectsIds[index]) != null && document.getElementById(projectsIds[index]).value != '') {
			var list = document.getElementById(projectsIds[index]).value.substring(0,document.getElementById(projectsIds[index]).value.length-1);
			newMembersJsonObject += '"' + projectsIds[index] + '":[' + list +'],';
		}
	}
	if(newMembersJsonObject != '') {
		newMembersJsonObject = '{'+newMembersJsonObject.substring(0, newMembersJsonObject.length-1)+'}';
		Ext.Ajax.request({
			url: JSPRootURL + '/directory/ManageDirectoryRemoveMember/submit_spaces_for_remove',
			params: {selectedProjectsList : newMembersJsonObject},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('bulkInviteList').innerHTML = result.responseText; 
		    	var highlightMemberIds = highlightMembers.split(',');
				for(var index = 0; index < highlightMemberIds.length -1; index++){
					if(document.getElementById('memberName_'+highlightMemberIds[index]) != null){
						document.getElementById('memberName_'+highlightMemberIds[index]).style.background = 'yellow';
					}
					if(document.getElementById('projectName_'+highlightMemberIds[index].split('_')[0]).style.background != 'yellow'){			
						document.getElementById('projectName_'+highlightMemberIds[index].split('_')[0]).style.background = 'yellow';
					}
				}
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, removeMemberFailureMsg, Ext.MessageBox.ERROR);	
			}
		});
	} else {
		if(projectsIds.length == 1){
			extAlert(errorAlertTitle, selectProjectOrSubbusinessToAdd , Ext.MessageBox.ERROR);
		}else{
			extAlert(errorAlertTitle, selectMemberToRemoveErrorMsg , Ext.MessageBox.ERROR);
		}
		removeLoadingDiv();
		return false;
	}
}

// Checking already added email in invitee list.
function checkExistanceOfEmail(){
	var emailId = inviteeEmails.split(',');
	if( emailId != ''  && document.getElementById('emailAddress').value != '' && (emailId.indexOf(document.getElementById('emailAddress').value) >= 0)){
		extAlert(errorAlertTitle, alreadyAddedMemberErrorMsg , Ext.MessageBox.ERROR);
		removeLoading();
		return false;
	}
	 if(document.getElementById('emailAddress').value != '' 
	 				&& document.getElementById('participantsList').innerHTML.indexOf(document.getElementById('emailAddress').value.toLowerCase()) != -1){
		extAlert(errorAlertTitle, alreadyInvitedMemberErrorMsg , Ext.MessageBox.ERROR);
		removeLoading();
		return false;
	}
	return true;
}

// Set the previously selected members
// also set the value in hidden field members_projectid to get the selected values on submiting page
function setSelectedRecordsForRM() {
	if(typeof selectedChkBoxIds != 'undefined') {
		var arr_selectedChkBoxIds = selectedChkBoxIds.split(',');
		var projectId;
		var memberId;
		if(arr_selectedChkBoxIds.length > 0) {
			for(var chkIndex = 0; chkIndex < arr_selectedChkBoxIds.length-1; chkIndex++) {
				if(document.getElementById('chk_' + arr_selectedChkBoxIds[chkIndex]) != null) {
					document.getElementById('chk_' + arr_selectedChkBoxIds[chkIndex]).checked = false;
					document.getElementById('img_'+arr_selectedChkBoxIds[chkIndex]).style.visibility = 'visible';
					projectId = (arr_selectedChkBoxIds[chkIndex].split('_'))[0];
					memberId = (arr_selectedChkBoxIds[chkIndex].split('_'))[1];
					if(document.getElementById('members_'+projectId).value.indexOf(memberId)== -1){
						document.getElementById('members_' + projectId).value += memberId + ',';
					}
				}
			}
		}
	}
}

// Create a json object of selected members to display 
// on adding new project or sub business along with selected project 
// or sub business id
function persistSelectedRecordsForRM(persistFor, requestFor) {
	var projectsIds = projectIds.split(',');
	var members = memberIdList.split(',');
	var selectedChkBoxIds = '';
	var isRequestFor = 'false';
	if(requestFor == 'onload'){
		isRequestFor = 'true';
	}
	for(var index = 0; index < projectsIds.length-1; index++){
		 for(var mindex = 0; mindex < members.length-1 ; mindex++){
		 	if(document.getElementById('chk_' + projectsIds[index] + '_' +members[mindex]) != null 
		 		&& typeof(document.getElementById('chk_' + projectsIds[index] + '_' +members[mindex]) != 'undefined') 
		 		&& !document.getElementById('chk_' + projectsIds[index] + '_' +members[mindex]).checked) {
		 		selectedChkBoxIds += projectsIds[index] + '_' +members[mindex] + ',';
		 	}
		}
	}
	var selectedRecords = '{"projectOrSubbusinessId" : ' + document.getElementById('projectDirectory').value + '' ;
	selectedRecords += ',"selectedChkBoxIds" : "' + selectedChkBoxIds + '"}';
	if(persistFor == 'afterProjectAddPersistPrevious'){
		document.getElementById('addProjectName').value = selectedRecords;
		Ext.Ajax.request({
			url: JSPRootURL + '/directory/ManageDirectoryRemoveMember/add_project',
			params: {projectId : document.getElementById('projectDirectory').value, selectedRecords : selectedRecords, requestFor : isRequestFor},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('bulkInviteList').innerHTML = result.responseText; 
		    	setSelectedRecordsForRM();
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, addspaceFailureMsg, Ext.MessageBox.ERROR);	
			}
		});
	} else if(persistFor == 'afterProjectDeletePersistPrevious'){
		document.getElementById('persistanceValue').value = selectedRecords;
		Ext.Ajax.request({
			url: JSPRootURL + '/directory/ManageDirectoryRemoveMember/remove_project',
			params: {projectId : document.getElementById('removeProjectName').value, selectedRecords : selectedRecords},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('bulkInviteList').innerHTML = result.responseText; 
		    	setSelectedRecordsForRM();
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, removeSpaceFailureMsg, Ext.MessageBox.ERROR);	
			}
		});
	}
}

// Redirect to directory page from bulk responsibility invite member page.
function redirectToDirectoryPage(){
	self.location = JSPRootURL + "/business/DirectorySetup.jsp?module=" + moduleId;
}

// setting already selected values to persist last page view
function setAlreadySelectedValues(){
	var controls = document.getElementsByTagName('input'); 
	for (var controlsIndex = 0; controlsIndex < controls.length; controlsIndex++) {
		if (controls[controlsIndex].type == 'checkbox'){
			if(controls[controlsIndex].checked && !controls[controlsIndex].disabled){
				var ids = controls[controlsIndex].id;
				var removechk = ids.substr(4,ids.length);
				var projectId = removechk.split('_')[0];
				selectedChkBoxIds += ids + ',';
				selectedRoleIds += 'rolesselect_'+removechk + ',';
				if(document.getElementById('roles_'+removechk).style.display == 'none'){
					document.getElementById('roles_'+removechk).style.display = '';
					document.getElementById('selected_'+projectId).innerHTML = (parseInt(document.getElementById('selected_'+projectId).innerHTML.trim()) + 1) ;
				}
			}
		}  
	}
	setSelectedRecords();
}

// For evaluating response of ajax request for getting script data
function evaluateScript(responseText){
	var scriptTag = '<script type="text/javascript">';
	var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	var javaScriptCode = responseText.replace(docTypeTag, '');
	javaScriptCode = javaScriptCode.substring(javaScriptCode.indexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
	eval(javaScriptCode.replace(/\n/g, ''));
}

// For refreshing content of invite list
function forRefreshInviteeList() {
	if(readyTosendRequest) {
		showLoadingDiv(loadingBusinessMemberErrorMsg);
		Ext.Ajax.request({
			url: JSPRootURL + '/directory/LoadMembers/load_invitee',
			params: {random : 1234},
			method: 'POST',
			success: function(result, request) {
				evaluateScript(result.responseText);
		    	document.getElementById('inviteeList').innerHTML = result.responseText; 
		    	removeLoading();
			}, 
			failure: function(result, response) {
				extAlert(errorTitle, loadingInviteeFailureMsg, Ext.MessageBox.ERROR);	
			}
		});
	}
	setBusinessMemberListDivHeight();
	selectedMembersCount = 0;		
}

// function to set div heights and widths
function setDivAlignments(){
	setDivHeights();
}
// function to set teammateContent Div width and height
function resizeTeammateContentDiv(){
	if(document.getElementById('teammateContent')){
		document.getElementById('teammateContent').style.width = (getWindowWidth() - 226) + 'px';
		document.getElementById('teammateContent').style.height = (getWindowHeight() - 220) + 'px';
	}
}

function resizeDiv(){
 resizeTeammateContentDiv();
 setDivWidths();
}

//To catch resize event
window.onresize = resizeDiv;
window.onload = resizeDiv;