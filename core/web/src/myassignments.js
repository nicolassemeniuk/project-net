var rowDoubleClicked = false;
var slidingPaneClosed = false;
var rightPaneClosed = false;
var prevSelectedRow;
var slidingPanelStatus = false;
var menuOpened = null;
var menuSource;
var groupByBusinessChecked = false;
var assignmentTypeChecked = false;
var assignmentsChecked = false;
var windowWidth = getWindowWidth();
var windowHeight = getWindowHeight();
var myAssignmentsHTML;
var allAssignmentsHTML;
var assignedByMeHTML;
var completedAssignmentsHTML;
var inProgressAssignmntsHTML;
var commingDueAssignmentsHTML;
var shouldHaveStartedAssignmentsHTML;
var lateAssignmentsHTML;
var assignmentCountObj;
var isActivatePanel = true;
var tabToActivate = '';
var disableTabClass = 'transparent url(../images/personal/inactive-tab.gif) no-repeat scroll left center';
var activeTabClass = 'transparent url(../images/personal/active-tab.gif) no-repeat scroll left center';
var numberOftabOpened = 0;
var tabNames = new Array ('panel_MyAssignments', 'panel_assignedByMe', 'panel_allAssignments', 'panel_completedAssignments', 'panel_inProgressAssignments', 'panel_shouldHaveStarted', 'panel_comingDue', 'panel_lateAssignments');
var assignmentTypeCheckBox = new Array('lateAssignment','assignmentComingDue','shouldHaveStart','inProgress');
var orderDescending = false;
var sortingApplied = false;
var sortedColumn;
var assignmentTaskListScrollLeft = 0;
var isOneClickFilterRequest = false;
var isAllTabsOpened = true;
var isLoadTask = true;
//Open right tab set if it is closed.
function openRightPanel(){
	collapseDiv(false, 'assignmentList', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '80%', '20%', false);
	document.getElementById('rightTabSet').style.display = '';
	resetPanelsWidth();
	document.getElementById("assignments-list-header").style.width = (parseInt(document.getElementById("assignmentList").style.width) - 17) + "px";
	notifyToggleTree('righttabsetexpanded', true);
}

//Close right tab set if it is closed.
function closeRightPanel(){
	collapseDiv(true, 'assignmentList', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '97%', '1%', false);
	document.getElementById('rightTabSet').style.display = 'none';
	resetPanelsWidth();
	document.getElementById("assignments-list-header").style.width = (parseInt(document.getElementById("assignmentList").style.width) - 17) + "px";
	notifyToggleTree('righttabsetexpanded', false);
}

//Toggle right tab set.
function toggleRightPanel(isOpen){
	//First close filter slider. it is opened
	if(document.getElementById('slidingpanel_content').style.display != 'none'){
		closeSlidingPanel();
		slidingPaneClosed = true;
	} else if (slidingPaneClosed){//open slide panel if it is closed previously.
		openSlidingPanel()
		slidingPaneClosed = false;
	}
	
	if(isOpen){
		closeRightPanel();
	} else {
		openRightPanel();
	}
}

//To submit ajax request
function submitAction(url, parameters, activatedPanel){
	showLoadingDiv(loadingMessage);
	assignmentTaskListScrollLeft = document.getElementById('assignmentList').scrollLeft;
	Ext.Ajax.request({
		   url: url+'?'+parameters,
		   method: 'POST',
		   params:{module:moduleId},
		   success: function(result, request){
		   		updateAssignments(result.responseText,activatedPanel);
		   		initSortingImage();
		   		removeLoadingDiv();
		   		initializeTab();
		   		document.getElementById('assignmentList').scrollLeft = assignmentTaskListScrollLeft;
		   },
		   failure: function(result, response){
			   //extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
	});

}

//To load Blog entries for selected object.
function loadBlogForAssignment(forTr, assignmentId, spaceId, assignmentobjectType, assignmentobjectName, assignmentworkobjectName, parentWorkSpaceType){
	if(rowDoubleClicked){
		goToObjectDetailPage(assignmentobjectType);
		rowDoubleClicked = false;
		return;
	}
	
	if(assignmentTreeNodeId == assignmentId)
		return;
	
	setSelecteRowStyleFor(forTr, forTr.id);
	assignmentTreeNodeId = assignmentId;
	taskSpaceId = spaceId;
	objectType = assignmentobjectType;
	objectName = assignmentobjectName;
	workSpaceName = assignmentworkobjectName;
	// get all children of selected row
	if (objectType != 'non_business' && objectType != 'project' && parentWorkSpaceType != ''){	
		workSpaceType = parentWorkSpaceType;
	} else {
		workSpaceType = '';
	}
	if(objectType != 'business' && workSpaceType != 'business' && objectType != 'non_business' ){	
		childTaskList = assignmentTreeNodeId+',';
		getChildRowId(forTr.id);
	}else{
		childTaskList = '-1';
	}
	if( isBlogEnabled ){
		loadBlogEntriesForAssignment('myAssignments', needRefresh, assignmentTreeNodeId, childTaskList, taskSpaceId);
	}
	if(isWikiEnabled ){
		loadWikiForAssignment(objectName, taskSpaceId, false, true);
	}
	loadDetails(assignmentId, document.getElementById('taskDetailsDivRight'), isLoadTask);
}

//Load wiki for assignment
function loadWikiForAssignment(objName, spaceId, isIndex, listItemSelected) {
	document.getElementById('wikiDivTop').innerHTML = loadingMessage +' <img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	disableHtmlWikiButtons();
	disableProjectPageIndexHtmlButton();
	objectTypeForWiki =  objectType == 'summary' ? 'task': objectType ;//Object type 'task' and 'summary' both are task.
	spaceId = spaceId == -1 ? "" : spaceId;
	if(objectTypeForWiki == 'business') {
		document.getElementById('wikiDivBody').innerHTML =  '';
		document.getElementById('wikiDivTop').innerHTML = "<br /><div style=\"padding-left: 15px;\"><label style=\"text-align: center; color: #848484; \">"+wikiNotSupportedMsg+"</label>";
		return;
	}
	Ext.Ajax.request({
		   url: JSPRootURL+'/pwiki/WikiAjaxHandler/getPageContent?module='+moduleId,
		   params: {spaceId: spaceId, objectId: assignmentTreeNodeId, objectType: objectTypeForWiki, objectName: objName, moduleId: moduleId, isPreview: 'false', isIndex: isIndex, wikiItFor: wikiItFor},
		   method: 'POST',
		   success: function(result, request){
		   		if(result.responseText.indexOf('AccessDenied.jsp') > 0 ) {
					loadAccessDenied(result.responseText);
					document.getElementById('wikiDivTop').innerHTML =  wikiPagesNotFoundMessage + objectName;					
				} else {
					document.getElementById('wikiDivBody').innerHTML =  '';					
					document.getElementById('wikiDivBody').innerHTML =  result.responseText;
					toggleHtmlEditing(listItemSelected);
					changeWikiTabHeaderMessage('wikiDivTop', wikiPagesFoundMessage, wikiPagesNotFoundMessage, objectName);
				}
		   },
		   failure: function(result, response){
			  
		   }
	});
	document.getElementById('wikiDivBody').style.height = (document.getElementById('taskWikiDivRight').offsetHeight - document.getElementById('wikiDivTop').offsetHeight) + 'px';
}

//Loads blog entries 
function loadBlogEntriesForAssignmentObject(forObjectId, childObjectsList, spaceId, offset){
	document.getElementById('blogDivTop').innerHTML = loadingMessage +' <img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	posts = null;
	Ext.Ajax.request({
		   url: JSPRootURL +'/blog/view/loadBlogEntries?module='+moduleId,
		   params: {module : moduleId, forObjectId : forObjectId, childObjectsList : childObjectsList, workSpaceId : spaceId, offset: offset, posts: posts},
		   method: 'POST',
		   success: function(result, request){
		   		 var scriptTag = '<script type="text/javascript">';
                 var javaScriptCode = result.responseText;
                 javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
                 eval(javaScriptCode.replace(/\n/g, ''));
 				 document.getElementById('blogDivBody').innerHTML = result.responseText;
 				 document.getElementById('blogDivTop').innerHTML = document.getElementById('blogCountMessage').innerHTML;
 				 //Apply blog-tab paging
 				 applyRightTabBlogPaging('personal');
 				 document.getElementById('blogCountMessage').innerHTML = '';
 				 //If row is double clicked 
 				 if(rowDoubleClicked){
					goToObjectDetailPage(objectType);
					rowDoubleClicked = false;
					return;
				}
		   },
		   failure: function(result, response){
			   	//extAlert('Error', 'Server request failed please try again...1', Ext.MessageBox.ERROR);
		   }
	});
}

//Open filter if it is closed. 
function openSlidingPanel(){
	document.getElementById('slidingpanel_content').style.display = '';
	document.getElementById('slidingpanel_toggler').className = 'tap5c_slidingPanelSubject-toggler-collapse';
	slidingPanelStatus = true;
	resetPanelsHeight();
}

//Close filter if it is opened.
function closeSlidingPanel(){
	document.getElementById('slidingpanel_content').style.display = 'none';
	document.getElementById('slidingpanel_toggler').className = 'tap5c_slidingPanelSubject-toggler';
	slidingPanelStatus = false;
	resetPanelsHeight();
}

//Toggles filter panel.
function toggleSlidingPanel(){
	if(activeTab == 'panel_MyAssignments'){
		//first close right panel, if it is open
		if(document.getElementById('blogsCollapsed').style.display == 'none'){
			closeRightPanel();
			rightPaneClosed = true;
		}else if(rightPaneClosed){ //open right panel if it is closed previously.
			openRightPanel();
			rightPaneClosed = false;
		}
	}
	if(document.getElementById('slidingpanel_content').style.display == 'none'){
		openSlidingPanel();
	}else{
		closeSlidingPanel();
	}
}

// set color to selected row and remove for previously selected
function setSelecteRowStyleFor(forTr, trId) {
	if(forTr.className.indexOf('wSelectedRow')== -1){
		forTr.className += ' wSelectedRow';
	}
	if(typeof(prevSelectedRow) != 'undefined' && document.getElementById(prevSelectedRow) != null) {
		document.getElementById(prevSelectedRow).className = document.getElementById(prevSelectedRow).className.replace('wSelectedRow', '');
	}
	prevSelectedRow = trId;
}

//To uncheck all checkbox in fiter when all all assignments check box is checked.
function allAssignmentsChecked() {
    if (filterForm.allAssignments.checked) {
        filterForm.lateAssignment.checked = !filterForm.allAssignments.checked;
        filterForm.assignmentComingDue.checked = !filterForm.allAssignments.checked;
        filterForm.shouldHaveStart.checked = !filterForm.allAssignments.checked;
        filterForm.inProgress.checked = !filterForm.allAssignments.checked;
    }
}

//To uncheck all assignment checkbox
function setassignmentTypeChecked(checkbox) {
	filterForm.allAssignments.checked = !checkbox.checked;
}

//To disable project filter when bussines selected.
function bussinessChanges(business){
	if(business.value != ''){
		filterForm.workSpace.disabled = true;
	} else {
		filterForm.workSpace.disabled = false;
	}
}

//To load new filter values
function applyFilter(business, projects, lateAssignment, comingDueDate, shouldHaveStart, inProgress, startDate, endDate, percentCompleteComparator, percentComplete, assignmentNameComparator, assignmentName){
		projects = getProjectFilterSelectedValues();
    	var startDateString = '';
		var endDateString = '';
		fromDate.setValue(startDate);
		toDate.setValue(endDate);
		startDate = fromDate.getValue();
		endDate = toDate.getValue();
		
		if(filterForm.fromDate.value.trim() != userDateFormatString ){
			if(filterForm.toDate.value.trim() != ''){ 
				if(startDate == ''){
					extAlert(errorTitle, invalidDateMsg, Ext.MessageBox.ERROR);
					return false;
				}
			}
		}
		if(filterForm.toDate.value.trim() != userDateFormatString){
			if(filterForm.toDate.value.trim() != ''){ 
				if(endDate == ''){
					extAlert(errorTitle, invalidDateMsg, Ext.MessageBox.ERROR);
					return false;
				}
			}
		}
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
			filterForm.allAssignments.checked = true;
		}
		showLoadingDiv(loadingMessage);
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
					assignmentName : assignmentName,
					windowWidth : windowWidth
		   },
		   method: 'POST',
		   success: function(result, request){
		   		nodeSelect = false;
		   		updateAssignments(result.responseText, null);
		   		assignmentTreeNodeId = 0;
		   		removeLoadingDiv();
		   },
		   failure: function(result, response){
			   //extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
}

//Ext date fields
toDate = new Ext.form.DateField({
						hideLabel : true,
						emptyText : dueDateEmptyText,
						value : endDateValue,
						format: getJSUserDatePattern(userDateFormatString),
						invalidText: msgFormat.format(dateFormatErr, userDateFormatString),
						width :70
					});
fromDate = new Ext.form.DateField({
						fieldLabel : '<b>'+datesLbl+'</b>',
						name : 'fromDate',
						labelWidth:30,
						value : startDateValue,
						emptyText : startDateEmptyText,
						format: getJSUserDatePattern(userDateFormatString),
						invalidText:msgFormat.format(dateFormatErr, userDateFormatString),
						width : 70,
						labelSeparator : ':'
					});
					
// To check date validations
function checkValidDate(startDate, endDate) {
	if(!fromDate.isValid()){
		extAlert(errorTitle, startDateErr, Ext.MessageBox.ERROR);
		return false;
	} else if(!toDate.isValid()){
		extAlert(errorTitle, dueDateErr, Ext.MessageBox.ERROR);
		return false;
	}
	if(startDate != null && startDate != ''&& (endDate == null || endDate == '' )){
		extAlert(errorTitle, emptyDueDateErr, Ext.MessageBox.ERROR);
		return false;
	}
	if((startDate == null || startDate == '') && endDate != null && endDate != ''){
		extAlert(errorTitle, emptyStartDateErr, Ext.MessageBox.ERROR);
		return false; 	
	}
	if(startDate != '' && endDate != '' && isStartDateAfterEndDate(startDate, endDate)){
		extAlert(errorTitle, sDAfterDDateErr, Ext.MessageBox.ERROR);
		return false;
	}
	return true;
}										

//To check start date after end date.
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

//Manage group menus
function showGroupMenu(isOpen, menuToOpen, menuEventSource, functionName ){
	if(menuOpened && menuOpened != menuToOpen){
		document.getElementById(menuOpened).style.display = 'none';
		document.getElementById(menuSource).onclick = new Function('showGroupMenu(true,\''+menuOpened+'\',\''+menuSource+'\')');
	}
	if(document.getElementById('wikiSubMenu').style.display == ''){
		document.getElementById('wikiSubMenu').style.display = 'none';
	}
	if(isOpen) {
		menuOpened = menuToOpen;
		menuSource = menuEventSource;
		var menuPos = getElementAbsolutePos(document.getElementById(menuEventSource));
		document.getElementById(menuToOpen).style.left =	menuPos.x + 'px';
		document.getElementById(menuToOpen).style.zIndex = '15000';
		document.getElementById(menuToOpen).style.top = parseInt(menuPos.y + 19) + 'px';
		document.getElementById(menuToOpen).style.display = '';
		document.getElementById(menuEventSource).onclick = new Function('showGroupMenu(false,\''+menuOpened+'\',\''+menuSource+'\')');
	} else {
		document.getElementById(menuToOpen).style.zIndex = '14999';
		document.getElementById(menuToOpen).style.left = '-10000px';
		document.getElementById(menuToOpen).style.top = '-10000px';
		document.getElementById(menuToOpen).style.display = 'none';
		document.getElementById(menuEventSource).onclick = new Function('showGroupMenu(true,\''+menuOpened+'\',\''+menuSource+'\')');
		menuOpened = '';
		menuSource = '';
	}
}

//To set grouping parameter for Menus on row click
function setGroupingParameter(checkBox, groupingField){
	if(!groupByBusinessChecked){
		if(checkBox.checked){
			checkBox.checked = false;
		}else{
			checkBox.checked = true;
		}
		onGroupItemCheck(groupingField, checkBox.checked);
	}else{
		groupByBusinessChecked = false;
	}
}

//To set assignment type parameter for Menus on row click
function setAssignmentTypeParameter(checkBox, groupingField){
	if(!assignmentTypeChecked){
		if(checkBox.checked){
			checkBox.checked = false;
		}else{
			checkBox.checked = true;
		}
		onAssignmentTypeItemCheck();
	}else{
		assignmentTypeChecked = false;
	}
}

//To set assignments parameter for Menus on row click
function setAssignmentsParameter(checkBox){
	if(!assignmentsChecked){
		if(checkBox.checked){
			checkBox.checked = false;
		}else{
			checkBox.checked = true;
		}
		onAssignorOrAssigneeMenuItemCheck();
	}else{
		assignmentsChecked = false;
	}
}

//To load values on group by parameter changed
function onGroupItemCheck(groupingField, checked){
	var parameters = 'module='+moduleId+'&groupingField='+groupingField+'&toGroup='+checked+'&windowWidth='+windowWidth;
	var url = JSPRootURL+'/assignments/My/setGroupingParameter';
	submitAction(url, parameters, null);
}

//To load values on assignment type parameter changed
function onAssignmentTypeItemCheck(){
	var assignmentTypes = '';
	var itemChecked = 0;
	var assignmentTypeMenuItems = document.getElementById('AssignmentTypeMenu').getElementsByTagName('input');
	
	for(var index = 0; index < assignmentTypeMenuItems.length; index++){
		if(assignmentTypeMenuItems[index].checked){
			assignmentTypes += assignmentTypeMenuItems[index].id.split('@')[0] + ',';
			itemChecked ++;
		}
	}
	for(var index = 0; index < assignmentTypeMenuItems.length; index++){
		if(itemChecked == 1 && assignmentTypeMenuItems[index].checked){
			assignmentTypeMenuItems[index].disabled = true;
		}else {
			assignmentTypeMenuItems[index].disabled = false;
		}
	}
	
	var parameters = 'module='+moduleId+'&assignmentTypes='+assignmentTypes+'&windowWidth='+windowWidth;
	var url = JSPRootURL+"/assignments/My/setAssignmentTypeParameter";
	submitAction(url, parameters, null);
}

//To load values on assignments parameter changed
function onAssignorOrAssigneeMenuItemCheck(){
	var assignmentOf = '';
	var itemChecked = 0;
	var assignmentsMenuItems = document.getElementById('AssignmentsMenu').getElementsByTagName('input');
	for(var index = 0; index < assignmentsMenuItems.length; index++){
		if(assignmentsMenuItems[index].checked){
			assignmentOf  += assignmentsMenuItems[index].id ;
			itemChecked ++;
		}
	}
	for(var index = 0; index < assignmentsMenuItems.length; index++){
		if(itemChecked == 1 && assignmentsMenuItems[index].checked){
			assignmentsMenuItems[index].disabled = true;
		}else {
			assignmentsMenuItems[index].disabled = false;
		}
	}
	var parameters = 'module='+moduleId+'&assignmentOf='+assignmentOf+'&windowWidth='+windowWidth;
	var url = JSPRootURL+'/assignments/My/setAssignorOrAssigneeParameter';
	submitAction(url, parameters, null);
}

//To handle click events to close group menu 
document.onclick = function(e) {
	var type = e ? e.target : window.event.srcElement;
	if(menuOpened != '' && typeof(menuOpened) != 'undefined'){
		if(document.getElementById(menuOpened) && type != document.getElementById(menuSource)&& document.getElementById(menuOpened).style.display == ''){
			document.getElementById(menuOpened).style.display = 'none';
			document.getElementById(menuSource).onclick = new Function('showGroupMenu(true,\''+menuOpened+'\',\''+menuSource+'\')');
		}
	}
	if(document.getElementById('wikiSubMenu') && type != document.getElementById('wikiMenuLink') && document.getElementById('wikiSubMenu').style.display == ''){
		document.getElementById('wikiSubMenu').style.display = 'none';
		document.getElementById('wikiMenuLink').onclick = new Function('showWikiSubMenu(true)');
		document.getElementById('wikiMenuImg').onclick = new Function('showWikiSubMenu(true)');
	}
	return true;
};

//To set default values for filters
function setGroupingParameters(){
	document.getElementById('groupByBussiness').checked = isGroupMenuItemChecked('Business');
	document.getElementById('groupByProject').checked = isGroupMenuItemChecked('Project');
	document.getElementById('meeting').checked = isAssignmentTypeMenuItemChecked('meeting');
	document.getElementById('task@data').checked = isAssignmentTypeMenuItemChecked('task');
	document.getElementById('form_data').checked = isAssignmentTypeMenuItemChecked('form_data');
	document.getElementById('assignor').checked = isAssignorOrAssigneeMenuItemChecked('assignor');
	document.getElementById('assignee').checked = isAssignorOrAssigneeMenuItemChecked('assignee');
}

//To set ajax response
function updateAssignments(newAssignments, activatedPanel){
	if(typeof(activatedPanel != 'undefined') && activatedPanel != null){
		initializeAssignments(activatedPanel, newAssignments);
		if(!sortingApplied){
			activate_tab(activatedPanel);
		}else{
			// For sort condition
			document.getElementById('assignmentContainer').innerHTML = newAssignments; 
		}
	}else{
		document.getElementById('assignmentContainer').innerHTML = newAssignments;
		myAssignmentsHTML = document.getElementById('assignmentContainer').innerHTML;
		firstAssignmentID = document.getElementById('firstAssignmentId').innerHTML;
		if(document.getElementById('assignmentsCount').innerHTML != ''){
			assignmentCountObj = eval("("+document.getElementById('assignmentsCount').innerHTML+")");
			document.getElementById('assignmentTotalCount').innerHTML = assignmentCountObj.totalFilteredAssignments;
		}
	}
		resetPanelsWidth();
		resetPanelsHeight();
		document.getElementById('assignment-table-container').width = (document.getElementById('assignmentListTable').offsetLeft + document.getElementById('assignmentListTable').offsetWidth) + 1;
		document.getElementById('assignment-list-table-container-header').width = (document.getElementById('assignmentListTable').offsetLeft + document.getElementById('assignmentListTable').offsetWidth)+1;
		alignColumns('thd_objectName');
	
}

//To initialize response on left side link action
function initializeAssignments(activatedPanel, newAssignments){
	if(activatedPanel == 'panel_allAssignments'){
		allAssignmentsHTML = newAssignments;
	}else if(activatedPanel == 'panel_assignedByMe'){
		assignedByMeHTML = newAssignments;
	}else if(activatedPanel == 'panel_completedAssignments'){
		completedAssignmentsHTML = newAssignments;
	}else if(activatedPanel == 'panel_inProgressAssignments'){
		inProgressAssignmntsHTML = newAssignments;
	}else if(activatedPanel == 'panel_comingDue'){
		commingDueAssignmentsHTML = newAssignments;
	}else if(activatedPanel == 'panel_shouldHaveStarted'){
		shouldHaveStartedAssignmentsHTML = newAssignments;
	}else if(activatedPanel == 'panel_lateAssignments'){
		lateAssignmentsHTML = newAssignments;
	}else if(activatedPanel == 'panel_MyAssignments'){
		myAssignmentsHTML = newAssignments;
	}
}


//To check previously checked assignment type.
function isAssignmentTypeMenuItemChecked(type){
	return assignmentTypesValue.toString().indexOf(type) >= 0;
}

//To check previously checked assignments.
function isAssignorOrAssigneeMenuItemChecked(assignmentOf){
	return assigneeOrAssignorParameter.indexOf(assignmentOf) >= 0;
}

//To check previously checked group by type
function isGroupMenuItemChecked(groupBy){
	return groupingParameter.indexOf(groupBy) >= 0;
}

//To add LateAssignmnets tab
function showLateAssignmnets(){
	setTabContainerWidth('panel_lateAssignments');
	if(isNewTabToAdd('panel_lateAssignments')){
		displayClickedTab('panel_lateAssignments');
		var url = JSPRootURL+'/assignments/My/getLateAssignmentsTreeData';
		var parameters = 'module='+moduleId;
		submitAction(url, parameters, 'panel_lateAssignments' );
	}else{
		isOneClickFilterRequest = false;
		activate_tab('panel_lateAssignments');
	}
}
//To add ComingDue tab
function showComingDueAssignmnets(){
	setTabContainerWidth('panel_comingDue');
	if(isNewTabToAdd('panel_comingDue')){
		displayClickedTab('panel_comingDue');
		var url = JSPRootURL+'/assignments/My/getComingDueAssignmentsTreeData';
		var parameters = 'module='+moduleId;
		submitAction(url, parameters, 'panel_comingDue' );
	}else{
		isOneClickFilterRequest = false;
		activate_tab('panel_comingDue');
	}
}
//To add Should Have Started tab
function showShouldHaveStartedAssignmnets(){
	setTabContainerWidth('panel_shouldHaveStarted');
	if(isNewTabToAdd('panel_shouldHaveStarted')){
		displayClickedTab('panel_shouldHaveStarted');
		var url = JSPRootURL+'/assignments/My/getShouldHaveStartedAssignmentsTreeData';
		var parameters = 'module='+moduleId;
		submitAction(url, parameters, 'panel_shouldHaveStarted' );
	}else{
		isOneClickFilterRequest = false;
		activate_tab('panel_shouldHaveStarted');
	}
}
//To add InProgress tab
function showInProgressAssignmnets(){
	setTabContainerWidth('panel_inProgressAssignments');
	if(isNewTabToAdd('panel_inProgressAssignments')){
		displayClickedTab('panel_inProgressAssignments');
		var url = JSPRootURL+'/assignments/My/getInProgressAssignmentsTreeData';
		var parameters = 'module='+moduleId;
		submitAction(url, parameters, 'panel_inProgressAssignments' );
	}else{
		isOneClickFilterRequest = false;
		activate_tab('panel_inProgressAssignments');
	}
	
}

//To add All Assignments tab
function showAllAssignmnets(){
	setTabContainerWidth('panel_allAssignments');
	if(isNewTabToAdd('panel_allAssignments')){
		displayClickedTab('panel_allAssignments');
		var url = JSPRootURL+'/assignments/My/getAllAssignmentsTreeData';
		var parameters = 'module='+moduleId;
		submitAction(url, parameters, 'panel_allAssignments' );
	}else{
		isOneClickFilterRequest = false;
		activate_tab('panel_allAssignments');
	}
}

//To add Completed Assignment tab
function showCompletedAssignmnets(){
	setTabContainerWidth('panel_completedAssignments');
	if(isNewTabToAdd('panel_completedAssignments')){
		displayClickedTab('panel_completedAssignments');
		var url = JSPRootURL+'/assignments/My/getCompletedAssignmentsTreeData';
		var parameters = 'module='+moduleId;
		submitAction(url, parameters, 'panel_completedAssignments' );
	}else{
		isOneClickFilterRequest = false;
		activate_tab('panel_completedAssignments');
	}
}

//To add All assigned by me tab
function showAllAssignedByMe(){
	setTabContainerWidth('panel_assignedByMe');
	if(isNewTabToAdd('panel_assignedByMe')){
		displayClickedTab('panel_assignedByMe');
		var url = JSPRootURL+'/assignments/My/getAllAssignedByMeTreeData'
		var parameters = 'module='+moduleId;
		submitAction(url, parameters, 'panel_assignedByMe' );
	}else{
		isOneClickFilterRequest = false;
		activate_tab('panel_assignedByMe');
	}
}

//To check if tab is already displayed.
function isNewTabToAdd(tabId){
	return document.getElementById(tabId).style.display == 'none';
}

//Run the normal toggle
function toggleTree(id) {
    toggle(id);
    var expanded = document.getElementById(id).getAttribute("kidsShown");
    notifyToggleTree("node"+id.split('_')[0]+"expanded", expanded);
}
	
//Expand all tree nodes
function expand_all() {
    if (firstAssignmentID != '') {
        showAll(firstAssignmentID, notifyToggleTree) ;
    } else {
    	extAlert(errorAlertTitle, noNodesToexpandMessage , Ext.MessageBox.ERROR);
    }
}
	
//Collapse all tree nodes
function collapse_all() {
    if (firstAssignmentID != '') {
        hideAll(firstAssignmentID, notifyToggleTree);
    } else {
    	extAlert(errorAlertTitle, noNodesToCollapseMessage , Ext.MessageBox.ERROR);
    }
}

// apply mouse over style for all rows other than header
function mo(forTr) {
	forTr.className += ' wMouseOverColor';
}

function mu(forTr) {
	forTr.className = replaceAll(forTr.className, 'wMouseOverColor', '');
}

//To load values according to filter
function submitFilters(){
	applyFilter(filterForm.businessCombo.value,filterForm.workSpace.value,filterForm.lateAssignment.checked,filterForm.assignmentComingDue.checked,filterForm.shouldHaveStart.checked,filterForm.inProgress.checked,filterForm.fromDate.value,filterForm.toDate.value,filterForm.percentCompleteComparator.value,filterForm.percentWorkComplete.value,filterForm.assignmentNameComparator.value,filterForm.assignmentName.value);
}

//To get values from multi select.
function getProjectFilterSelectedValues(){
	var projectIds = '';
	var projectOptions = filterForm.workSpace.options;
	for(var index=0; index < projectOptions.length; index++){
		if(projectOptions[index].selected){
			projectIds += projectOptions[index].value+','
		}
	}
	return projectIds.substring(0,projectIds.lastIndexOf(','));
}

//To handle row double click
function onGridRowDoubleClick(objectId, baseObjectType, assignmentObjectName,workSpace){
	rowDoubleClicked = true;
	assignmentTreeNodeId = objectId;
	objectType = baseObjectType;
	objectName = objectName;
	workSpaceName = workSpace;
}
	
function goToObjectDetailPage(objectType) {
	if(objectType.toString() == 'project') {
		window.location.href = JSPRootURL+'/project/Main.jsp?id='+assignmentTreeNodeId+'&page='+JSPRootURL+'/project/Dashboard?id='+assignmentTreeNodeId;
	} else if (objectType.toString() == 'business'){
		window.location.href = JSPRootURL+'/business/Main.jsp?id='+assignmentTreeNodeId;
	} else if(objectType.toString() == 'task' || objectType.toString() == 'summary' || objectType.toString() == 'temporary_task'){
       	window.location.href = JSPRootURL+'/servlet/AssignmentController/TaskView?module=60&action=1&id='+assignmentTreeNodeId+'&refLink=/assignments/My?module='+moduleId;
	} else if(objectType.toString() == 'form_data' || objectType.toString() == 'temporary_formdata'){
		window.location.href = JSPRootURL+'/form/FormEdit.jsp?id='+assignmentTreeNodeId+'&module=30&action=1&spaceID='+ taskSpaceId +'&redirectedFromSpace=true';
	} else if(objectType.toString() == 'meeting' || objectType.toString() == 'activity' || objectType.toString() == 'temporary_meeting'){
		window.location.href = JSPRootURL+'/calendar/MeetingManager.jsp?id='+assignmentTreeNodeId+'&action=1&module=70';
	} else if(objectType.toString() == 'person'){
		window.location.href = JSPRootURL+'/personal/profile/'+assignmentTreeNodeId+'?module=160';
	} else {
		extAlert('Error', 'Details are not available for this object.', Ext.MessageBox.ERROR);
	}		
}

//To Activate tab on right side.
function activate_rightTab(clickedTab) {
	document.getElementById('wikiMenu').style.display = 'none';
	document.getElementById('taskBlogDivRight').style.display = 'none';
	document.getElementById('taskWikiDivRight').style.display = 'none';
	document.getElementById('taskDetailsDivRight').style.display = 'none';
	document.getElementById('blog-tab').className = 'right-tab deactive-right-tab';
	document.getElementById('wiki-tab').className = 'right-tab deactive-right-tab';
	document.getElementById('detail-tab').className = 'right-tab deactive-right-tab';
		
	if(clickedTab == 'blog-tab') {
		// Activating right blog tab here
		document.getElementById('taskBlogDivRight').style.display = '';
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
	notifyToggleTree('rightTabSet', clickedTab);
}

//To dispay right tab when right tab width is small
function togglerHeaderTabs(enableTab) {
	if(enableTab == 'right') {
		showBottomWikiMenu(true);
		enableHeaderScroll(true, '', 'none', 'wiki-tab', 'black', '#6776A6');
	} else {
		enableHeaderScroll(false, 'none', '', 'blog-tab', '#6776A6', 'black');
		document.getElementById('wiki-tab').style.background = '#EED765';
		showBottomWikiMenu(false);
	}
}

//To show bottom wiki menu.
function showBottomWikiMenu(openMenu) {
	if(openMenu) {
		document.getElementById('wikiMenu').style.display = '';
	} else {
		document.getElementById('wikiMenu').style.display = 'none';
	}
}


function enableHeaderScroll(scrollHeader, showWiki, showBlog, headerBG, wikiFontColor, blogFontColor) {
	var leftImgPath;
	if(scrollHeader) {
		leftImgPath = JSPRootURL+'/images/personal/scroll_left.gif';
		document.getElementById("viewHiddenRightTabs").innerHTML = '<img src='+leftImgPath+' id="scrollImg" onclick="togglerHeaderTabs(\'left\')"></img>'
	} else {
		leftImgPath = JSPRootURL+'/images/personal/scroll_right.gif';
		document.getElementById("viewHiddenRightTabs").innerHTML = '<img src='+leftImgPath+' id="scrollImg" onclick="togglerHeaderTabs(\'right\')"></img>'
	}
	document.getElementById('wiki-tab').style.display = showWiki;
	document.getElementById('taskWikiDivRight').style.display = showWiki;

	document.getElementById('blog-tab').style.display = showBlog;
	document.getElementById('taskBlogDivRight').style.display = showBlog;

	document.getElementById(headerBG).style.background = '#FEF5C7';
	document.getElementById('wiki-tab').style.color = wikiFontColor;
	document.getElementById('blog-tab').style.color = blogFontColor;
	
}

// To get tabs to disable on tab click
function removeTabName(tabName){
	var tabNamesString = '';
	for(var tabIndex = 0 ;tabIndex< tabNames.length; tabIndex++){
		if(tabName != tabNames[tabIndex]){
			tabNamesString += tabNames[tabIndex]+',';
		}
	}
	return tabNamesString.substring(0,tabNamesString.lastIndexOf(',')).split(',');
}

//To activate tab
function activate_tab(clickedPanel){
	if(document.getElementById('slidingpanel_content').style.display == ''){
		closeSlidingPanel();
	}
	if(!isActivatePanel){
		clickedPanel = tabToActivate;
		isActivatePanel = true;
	}
	removeActionMessageDiv();
	var tabsTodisable = removeTabName(clickedPanel);
	activeTab = clickedPanel;
	if(document.getElementById(clickedPanel).style.display == 'none'){
		numberOftabOpened += 1;
		document.getElementById(clickedPanel).style.display = '';
	}
	for(tabIndex = 0; tabIndex < tabsTodisable.length; tabIndex++){
		tabsTodisable[tabIndex]  
		document.getElementById(tabsTodisable[tabIndex]).style.background = disableTabClass;
		document.getElementById(tabsTodisable[tabIndex]).className = document.getElementById(tabsTodisable[tabIndex]).className.replace('activated', '');
	}
	if(typeof(myAssignmentsHTML) == 'undefined' || myAssignmentsHTML == ''){
		myAssignmentsHTML = document.getElementById('assignmentContainer').innerHTML;
	}  
	document.getElementById(clickedPanel).style.background = activeTabClass;
	document.getElementById(clickedPanel).className += ' activated';
	if(clickedPanel == 'panel_MyAssignments'){
		if(typeof(myAssignmentsHTML) != 'undefined'){
			document.getElementById('assignmentContainer').innerHTML = myAssignmentsHTML;
			firstAssignmentID = document.getElementById('firstAssignmentId') == null ? firstObjectId : document.getElementById('firstAssignmentId').innerHTML;
			if(document.getElementById('assignmentsCount') != null){
				assignmentCountObj = eval("("+document.getElementById('assignmentsCount').innerHTML+")");
				document.getElementById('assignmentTotalCount').innerHTML = assignmentCountObj.totalFilteredAssignments;
			}else{
				document.getElementById('assignmentTotalCount').innerHTML = totalFilteredAssignments;
			}
		}  
		enableFilterToolbar();
	} else if(clickedPanel == 'panel_allAssignments'){  
		document.getElementById('assignmentContainer').innerHTML = allAssignmentsHTML;
		firstAssignmentID = document.getElementById('firstAssignmentId').innerHTML;
		assignmentCountObj = eval("("+document.getElementById('assignmentsCount').innerHTML+")");
		document.getElementById('assignmentTotalCount').innerHTML = assignmentCountObj.numberOfAllAssignments;
		disableFilterToolbar();
	} else if(clickedPanel == 'panel_assignedByMe'){  
		document.getElementById('assignmentContainer').innerHTML = assignedByMeHTML;
		firstAssignmentID = document.getElementById('firstAssignmentId').innerHTML;
		assignmentCountObj = eval("("+document.getElementById('assignmentsCount').innerHTML+")");
		document.getElementById('assignmentTotalCount').innerHTML = assignmentCountObj.numberOfAllAssignedByMe;
		disableFilterToolbar();
	} else if(clickedPanel == 'panel_completedAssignments'){
		document.getElementById('assignmentContainer').innerHTML = completedAssignmentsHTML;
		firstAssignmentID = document.getElementById('firstAssignmentId').innerHTML;
		assignmentCountObj = eval("("+document.getElementById('assignmentsCount').innerHTML+")");
		document.getElementById('assignmentTotalCount').innerHTML = assignmentCountObj.numberOfCompletedAssignments;
		disableFilterToolbar();
	}else if(clickedPanel == 'panel_inProgressAssignments'){
		document.getElementById('assignmentContainer').innerHTML = inProgressAssignmntsHTML;
		firstAssignmentID = document.getElementById('firstAssignmentId').innerHTML;
		assignmentCountObj = eval("("+document.getElementById('assignmentsCount').innerHTML+")");
		document.getElementById('assignmentTotalCount').innerHTML = assignmentCountObj.numberOfInProgressAssignments;
		disableFilterToolbar();
	}else if(clickedPanel == 'panel_comingDue'){
		document.getElementById('assignmentContainer').innerHTML = commingDueAssignmentsHTML;
		firstAssignmentID = document.getElementById('firstAssignmentId').innerHTML;
		assignmentCountObj = eval("("+document.getElementById('assignmentsCount').innerHTML+")");
		document.getElementById('assignmentTotalCount').innerHTML = assignmentCountObj.numberOfComingDueAssignments;
		disableFilterToolbar();
	}else if(clickedPanel == 'panel_shouldHaveStarted'){
		document.getElementById('assignmentContainer').innerHTML = shouldHaveStartedAssignmentsHTML;
		firstAssignmentID = document.getElementById('firstAssignmentId').innerHTML;
		assignmentCountObj = eval("("+document.getElementById('assignmentsCount').innerHTML+")");
		document.getElementById('assignmentTotalCount').innerHTML = assignmentCountObj.numberOfShouldHaveStartedAssignments;
		disableFilterToolbar();
	}else if(clickedPanel == 'panel_lateAssignments'){
		document.getElementById('assignmentContainer').innerHTML = lateAssignmentsHTML;
		firstAssignmentID = document.getElementById('firstAssignmentId').innerHTML;
		assignmentCountObj = eval("("+document.getElementById('assignmentsCount').innerHTML+")");
		document.getElementById('assignmentTotalCount').innerHTML = assignmentCountObj.numberOfLateAssignments;
		disableFilterToolbar();
	}
	if(!isOneClickFilterRequest){
		resetPanelsWidth();
		resetPanelsHeight();
		document.getElementById('assignment-table-container').width = (document.getElementById('assignmentListTable').offsetLeft + document.getElementById('assignmentListTable').offsetWidth) + 1;
		document.getElementById('assignment-list-table-container-header').width = (document.getElementById('assignmentListTable').offsetLeft + document.getElementById('assignmentListTable').offsetWidth)+1;
		alignColumns('thd_objectName');
	}else{
		isOneClickFilterRequest = false;
	}
}	

//To disable filter toolbar links when other tab is opened.
function disableFilterToolbar(){
	initializeTab();
	document.getElementById('filterToggler').style.display = 'none';
	document.getElementById('slidingpanel_toggler').style.display = 'none';
	document.getElementById('groupBy').style.visibility = 'hidden';
	document.getElementById('assignmentType').style.visibility = 'hidden';
	document.getElementById('assignments').style.visibility = 'hidden';
	document.getElementById('column_settings_link').style.visibility = 'hidden';
	document.getElementById('groupby-sep').style.visibility = 'hidden';
	document.getElementById('assignmenttype-sep').style.visibility = 'hidden';
	document.getElementById('assignment-sep').style.visibility = 'hidden';
}

//To enable filter tolbar links
function enableFilterToolbar(){
	initializeTab();
	document.getElementById('filterToggler').style.display = '';
	document.getElementById('slidingpanel_toggler').style.display = '';
	document.getElementById('groupBy').style.visibility = '';
	document.getElementById('assignmentType').style.visibility = '';
	document.getElementById('assignments').style.visibility = '';
	document.getElementById('column_settings_link').style.visibility = '';
	document.getElementById('groupby-sep').style.visibility = '';
	document.getElementById('assignmenttype-sep').style.visibility = '';
	document.getElementById('assignment-sep').style.visibility = '';
}

//To display assignmets links
function displayAssignmentsLinks(){
	var actionBoxString = '';
		if(actionsIconEnabled){
			actionBoxString += "<span>";
			actionBoxString += "	<a href='javascript:showResourceAllocation("+ userId +");' onmouseover=\"document.utilization.src = '"+ JSPRootURL +utilizationSummaryImageOver+"'\" onmouseout=\"document.utilization.src = '"+ JSPRootURL +utilizationSummaryImageOn+"'\">";
			actionBoxString += "		<img alt='"+utilizationSummaryLinkTitle+"' title='"+utilizationSummaryLinkTitle+"' border='0' hspace='0' src='" + JSPRootURL +utilizationSummaryImageOn+"' name='utilization'/>&nbsp;" + utilizationSummaryLinkTitle;
			actionBoxString += "	</a>";
			actionBoxString += "</span>";
		} else {
			actionBoxString += "<span> <a href='javascript:showResourceAllocation("+ userId +");'>"+ utilizationSummaryLinkTitle +" </a> </span>";
		}
		
		document.getElementById('toolbox-item').innerHTML += actionBoxString;
		
		//Generating assignment links
		actionBoxString = '<div id="toolbox-heading" class="toolbox-heading"> '+filterPreset+' </div><div id="toolbox-item" class="toolbox-item">';
		actionBoxString += '<span id="allAssignedbyme"><a href="javascript:showAllAssignedByMe();">'+assignedByMeLink+'&nbsp;('+numberOfAllAssignedByMe+')</a></span><br/>';
	    actionBoxString += '<span id="allAssignments"><a href="javascript:showAllAssignmnets();">'+allAssignmentsLink+'&nbsp;('+numberOfAllAssignments+')</a></span><br/>';
	    actionBoxString += '<span id="completedAssignments"><a href="javascript:showCompletedAssignmnets();">'+completedAssignmentsLink+'&nbsp;('+numberOfCompetedAssignments+')</a></span><br/>';
	    actionBoxString += '<span id="inprogress"><a href="javascript:showInProgressAssignmnets();">'+inProgressLink+'&nbsp;('+ numberOfInProgressAssignments +')</a></span><br/>';
	    actionBoxString += '<span id="comingdue"><a href="javascript:showComingDueAssignmnets();">'+comingDueLink+'&nbsp;('+ numberOfComingDueAssignments +')</a></span><br/>';
	    actionBoxString += '<span id="shouldhavestarted"><a href="javascript:showShouldHaveStartedAssignmnets();">'+shouldHaveStartedLink+'&nbsp;('+ numberOfShouldHaveStartedAssignments +')</a></span><br/>';	    
	    actionBoxString += '<span id="lateAssignment"><a href="javascript:showLateAssignmnets();">'+lateAssignmentsLink+'&nbsp;('+ numberOfLateAssignments +')</a></span></div>';	    
	    document.getElementById('left-navbar').innerHTML += actionBoxString;
}

// To save the view setting
function notifyToggleTree(propertyName, propertyValue) {
	if(activeTab == 'panel_MyAssignments'){
		Ext.Ajax.request({
			   url: JSPRootURL+'/assignments/My/StoreViewsettings',
			   params: {module: moduleId, propertyName : propertyName, propertyValue : propertyValue},
			   method: 'POST',
			   success: function(result, request){
			   },
			   failure: function(result, response){
				
			   }
			});
	}
}

function refreshAssignmentGridAndCountFromStart(loadFromStart){
	showLoadingDiv(loadingMessage);
	Ext.Ajax.request({
		   url: JSPRootURL+'/assignments/My/getAssignmentsTreeData',
		   params: {module: moduleId},
		   method: 'POST',
		   success: function(result, request){
		   		updateAssignments(result.responseText, null);
				if(document.getElementById('inprogress') != null && document.getElementById('lateAssignment') && document.getElementById('shouldhavestarted') && document.getElementById('comingdue')
					&& document.getElementById('allAssignments') != null && document.getElementById('completedAssignments') != null){
					document.getElementById('lateAssignment').innerHTML = '<a href="javascript:showLateAssignmnets();">'+lateAssignmentsLink+'&nbsp;('+ assignmentCountObj.numberOfLateAssignments +')</a>';
					document.getElementById('comingdue').innerHTML = '<a href="javascript:showComingDueAssignmnets();">'+comingDueLink+'&nbsp;('+ assignmentCountObj.numberOfComingDueAssignments +')</a>';
				    document.getElementById('shouldhavestarted').innerHTML = '<a href="javascript:showShouldHaveStartedAssignmnets();">'+shouldHaveStartedLink+'&nbsp;('+ assignmentCountObj.numberOfShouldHaveStartedAssignments +')</a>';
				    document.getElementById('inprogress').innerHTML = '<a href="javascript:showInProgressAssignmnets();">'+inProgressLink+'&nbsp;('+ assignmentCountObj.numberOfInProgressAssignments +')</a>';
				    document.getElementById('allAssignments').innerHTML = '<a href="javascript:showAllAssignmnets();">'+allAssignmentsLink+'&nbsp;('+ assignmentCountObj.numberOfAllAssignments +')</a>';
				    document.getElementById('completedAssignments').innerHTML = '<a href="javascript:showCompletedAssignmnets();">'+completedAssignmentsLink+'&nbsp;('+ assignmentCountObj.numberOfCompletedAssignments +')</a>';
				    document.getElementById('allAssignedbyme').innerHTML = '<a href="javascript:showAllAssignedByMe();">'+assignedByMeLink+'&nbsp;('+ assignmentCountObj.numberOfAllAssignedByMe +')</a>';
				}
				removeLoadingDiv();
		   },
		   failure: function(result, response){
			   removeLoadingDiv();
		   }
	});
}

// To close tab
function closeTab(tabToClose){
	isActivatePanel = false;
	numberOftabOpened -= 1;
	if(numberOftabOpened <= 5 && !isAllTabsOpened){
		document.getElementById('container').style.width = document.getElementById('pnettabset').style.width;
		isAllTabsOpened = true;
	}
	document.getElementById(tabToClose).style.display = 'none';
	tabToActivate = 'panel_MyAssignments';
}

// To get previous tab
function getPreviousTabToActivate(tabClosed){
	var previousTab;
	for(var tabIndex = 0 ;tabIndex< tabNames.length; tabIndex++){
		if(tabClosed == tabNames[tabIndex]){
			previousTab = tabNames[tabIndex-1];
			break;
		}
	}
	return previousTab;

}

// To set tab container width 
function setTabContainerWidth(clickPanel){
	isOneClickFilterRequest = true;
	document.getElementById('assignmentContainer').style.visibility = 'hidden';
	document.getElementById('assignment-table-container').style.display = 'none';
	if(numberOftabOpened >= 5 && isAllTabsOpened){
		document.getElementById('container').style.width = parseInt(document.getElementById('container').style.width)+ 300 +'px';
		isAllTabsOpened = false;
	}
}

//To clear filters
function clearFilter(){
	filterForm.businessCombo.value = '';
	filterForm.workSpace.value = '0';
	for(var index=0; index < assignmentTypeCheckBox.length; index++){
		document.getElementById(assignmentTypeCheckBox[index]).checked = false;
	}
	filterForm.fromDate.style.color = 'Gray';
	filterForm.fromDate.value = userDateFormatString;
	filterForm.toDate.value = userDateFormatString;
	filterForm.toDate.style.color = 'Gray';
	filterForm.percentCompleteComparator.value = 'equals';
	filterForm.percentWorkComplete.value = '';
	filterForm.assignmentNameComparator.value = 'equals';
	filterForm.assignmentName.value = '';
	bussinessChanges(document.getElementById('businessCombo'));
}

// To set date 
function setDate(object){
	if(object.value.trim() == userDateFormatString) {
		object.value = '';
	}
	object.style.color = 'Black';
}

// To change date
function changeDate(object){
	if(object.value == ''){
		object.style.color = 'Gray';
		object.value = userDateFormatString;
	}
}

// To set default persisted data
function setDefaultFilterParameter(){
	var businessOptions = document.getElementById('businessCombo').options;
	for(var businessIndex = 0; businessIndex < businessOptions.length; businessIndex++){
		if(businessValue == businessOptions[businessIndex].value){
			businessOptions[businessIndex].selected = true;
			break;
		}
	}
	var workSpaceOptions = document.getElementById('workSpace').options;
	for(var projectIndex = 0; projectIndex < workSpaceOptions.length; projectIndex++){
		if(projectValue.indexOf(workSpaceOptions[projectIndex].value) != -1){
			workSpaceOptions[projectIndex].selected = true;
		}
	}
	var percentCompleteComparatorOptions = document.getElementById('percentCompleteComparator').options;
	for(var percentIndex = 0; percentIndex < percentCompleteComparatorOptions.length; percentIndex++){
		if(percentCompleteComparatorValue == percentCompleteComparatorOptions[percentIndex].value){
			percentCompleteComparatorOptions[percentIndex].selected = true;
			break;
		}
	}
	var assignmentNameComparatorOptions = document.getElementById('assignmentNameComparator').options;
	for(var nameIndex = 0; nameIndex < assignmentNameComparatorOptions.length; nameIndex++){
		if(assignmentNameComparatorValue == assignmentNameComparatorOptions[nameIndex].value){
			percentCompleteComparatorOptions[nameIndex].selected = true;
			break;
		}
	}
	filterForm.lateAssignment.checked = lateAssignmentCheck == 'true';
	filterForm.inProgress.checked = inProgressCheck == 'true';
	filterForm.shouldHaveStart.checked = shouldHaveStartCheck == 'true';
	filterForm.assignmentComingDue.checked = comingDueDateCheck == 'true';
	for(var checkIndex = 0; checkIndex < assignmentTypeCheckBox.length; checkIndex++){
		if(document.getElementById(assignmentTypeCheckBox[checkIndex]).checked){
			setassignmentTypeChecked(document.getElementById(assignmentTypeCheckBox[checkIndex]));
			break;
		}
	}
}

// Sort the column.
function sort(column){
	orderDescending = !orderDescending;
	sortedColumn = column;
	sortingApplied = true;
	document.getElementById('sort-img-'+sortedColumn).src = JSPRootURL + '/images/default/tree/loading-small.gif';
	document.getElementById('sort-img-'+sortedColumn).style.visibility = 'visible';
	var parameters = 'columnIndex='+column+'&activeTab='+activeTab.trim();
	var url = JSPRootURL+'/assignments/My/sortChanged';
	submitAction(url, parameters, activeTab);
}  

// Initialize the sorting images 
function initSortingImage(){
	if(sortingApplied){
		var imageSrc;
		if(orderDescending){
			imageSrc = JSPRootURL + '/images/default/grid/sort_desc.gif';
		}else{
			imageSrc = JSPRootURL + '/images/default/grid/sort_asc.gif';
		}
		document.getElementById('sort-img-'+sortedColumn).src = imageSrc;
		document.getElementById('sort-img-'+sortedColumn).style.visibility = 'visible';
		sortingApplied = false;  
	}
}

// Initialize tab
function initializeTab(){
	if(document.getElementById('assignmentContainer').style.visibility == 'hidden'){
		document.getElementById('assignmentContainer').style.visibility = 'visible';
		document.getElementById('assignment-table-container').style.display = '';
	}
	
}

// To immediatialy tab after clicking one click filter
function displayClickedTab(clickedTab){
	 if(document.getElementById(clickedTab).style.display == 'none'){
			numberOftabOpened += 1;
			document.getElementById(clickedTab).style.display = '';
	 }
}

//show blog while navigating with paging
function showBlogAfterPaging(){
	loadBlogEntriesForAssignmentObject(assignmentTreeNodeId, childTaskList, taskSpaceId, offset);
}