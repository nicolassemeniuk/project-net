var rowDoubleClicked = false;
var slidingPaneClosed = false;
var rightPaneClosed = false;
var projectId;
var prevSelectedRow;
var projectName;
var blogEntryIds;
var wikiPagesFoundMessage = '';
var serverRequestFailed = 'Server Request Fail';
var objectName;
var windowWidth = getWindowWidth();
var windowHeight = getWindowHeight();
var functionNameString = new Array ('javascript:deleteViews();');
var linksToEnable = '';
var viewChanged = false;
var orderDescending = false;
var rightTabLoadStatus;
var widgetStatus;
var sorterParameterString;

//Open right tab set if it is closed.
function openRightPanel(){

	if(!slidingPaneClosed) // if sliding filter panel is open the close it before opening right panel
		closeSlidingPanel();

	collapseDiv(false, 'projectList', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '80%', '20%', false);
	document.getElementById('rightTabSet').style.display = '';
	resizePanel();
	document.getElementById("project-list-header").style.width = (parseInt(document.getElementById('project-list').style.width)) + 'px';
	saveState("right_pane_collapsed", '0', 'twopane');
}

//Close right tab set if it is closed.
function closeRightPanel(){
	collapseDiv(true, 'projectList', 'blogsDiv', 'blogsCollapsed', 'blogsExpanded', '97%', '1%', false);
	document.getElementById('rightTabSet').style.display = 'none';
	resizePanel();
	document.getElementById('blogsCollapsed').style.height = document.getElementById('project-list').offsetHeight ;
	document.getElementById('project-list-header').style.width = (parseInt(document.getElementById('project-list').style.width)) + 'px';
	saveState("right_pane_collapsed", '2', 'twopane');
}

//Toggle right tab set.
function toggleRightPanel(isOpen){
	isOpen ? closeRightPanel() : openRightPanel();
	if(!Ext.isIE){
		resizePanel();
	}
}

//load blog entries in blog tab for selected row
function b(obj, forId) {
	rightTabLoadStatus = new Array (false,false,false);

	//if selection is not changed return back.
	if(selectedProjectId == forId && obj) 
		return;

	selectedProjectId = forId;

	if(obj){
		objectName = document.getElementById('s'+forId).innerHTML;
		setSelecteRowStyleFor(obj, obj.id);
	} 

//	Finally load corresponding information in right tab.
	initRightPanels();
}

//load selected row information in right tab.
function initRightPanels(){
	//load only active right tab.
	if(isActiveRightTab('blog-tab') && isBlogEnabled && !rightTabLoadStatus[0]){
		loadBlogsForSelectedRow(selectedProjectId);
		rightTabLoadStatus[0] = true;
	}if(isActiveRightTab('wiki-tab') && !rightTabLoadStatus[1]){
	 	loadWikiPageContents();	
		rightTabLoadStatus[1] = true;
	}if(isActiveRightTab('detail-tab') && !rightTabLoadStatus[2]){
	 	loadDetails(selectedProjectId, document.getElementById('projectDetailsDivRight'));
		rightTabLoadStatus[2] = true;
	}	
}

function loadBlogsForSelectedRow(forObjectId){
	document.getElementById('blogDivTop').innerHTML = loadingMessage + '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	Ext.Ajax.request({
	 	url: JSPRootURL +'/blog/view/loadblogentries?module='+moduleId,
	    params: {moduleId : moduleId, forObjectId : forObjectId, isProjectPortfolio : true},
		
		method: 'POST',
		success: function(result, request){
			var scriptTag = '<script type="text/javascript">';
			var javaScriptCode = result.responseText;
			javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
		    
			eval(javaScriptCode.replace(/\n/g, ''));
			// Set response and blog count message 
			document.getElementById('blogDivBody').innerHTML = '';
			document.getElementById('blogDivBody').innerHTML = result.responseText;
			document.getElementById('blogDivTop').innerHTML = document.getElementById('blogCountMessage').innerHTML;
			document.getElementById('blogCountMessage').style.display = 'none';
			if(document.getElementById('blogCountMessage').innerHTML == '' && totalNoOfBlogEntry == 0)	
				document.getElementById('blogCountMessage').innerHTML = blogNotFoundMsg;
			if(totalNoOfBlogEntry == 0){
				document.getElementById('blogDivTop').innerHTML = msgFormat.format(blogNotFoundForProjectMsg, objectName);
			} else {
				document.getElementById('blogDivTop').innerHTML = msgFormat.format('{0} blog entries found', totalNoOfBlogEntry);				
			}
			resetPanelsWidth();
		},
		failure: function(result, response){
		}
	});		   
	document.getElementById('blogDivBody').style.height = (document.getElementById('projectBlogDivRight').offsetHeight - document.getElementById('blogDivTop').offsetHeight) + 'px';
}

function loadWikiPageContents() {
	 	getWikiPageForSelectedProject(objectName,selectedProjectId,spaceId,false,false, true);
}

// Load wiki page content for the selected object i.e. task in the right "Wiki" tab
function getWikiPageForSelectedProject(objectName, projectId, spaceId, isIndex, isPreview , projectSelected) {
	document.getElementById('wikiDivTop').innerHTML =  loadingMessage +' <img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	wikiObjectName = objectName;
	wikiSpaceId = projectId;
	wikiObjectType = objectType;
	wikiObjectId = projectId;
	Ext.Ajax.request({
		url: JSPRootURL+'/pwiki/wikiAjaxHandler/getPageContent?module='+moduleId, 
		params: {module: moduleId, spaceId: projectId, objectId: projectId, objectName: objectName, objectType: objectType, isPreview: isPreview, isIndex: isIndex, wikiItFor: wikiItFor},
		method: 'POST',
		success: function(result, request) {
			if(result.responseText.indexOf('AccessDenied.jsp') > 0 ) {
				loadAccessDenied(result.responseText);
				document.getElementById('wikiDivTop').innerHTML =  wikiPagesNotFoundMessage + objectName;					
			} else if(result.responseText != '') {
				document.getElementById('wikiDivBody').innerHTML =  '';					
				document.getElementById('wikiDivBody').innerHTML = result.responseText;
				toggleHtmlEditing(projectSelected);
				changeWikiTabHeaderMessage('wikiDivTop', wikiPagesFoundMessage, wikiPagesNotFoundMessage, objectName);
				// if loaded wiki page is index page then disable Index Page button in wiki menu else enable it
				if (isIndex){
					disableProjectPageIndexHtmlButton();
				} else {
					showProjectPageIndexHtmlButton();
				}
			}
		},
		failure: function(result, request) {
		}
	});		
	document.getElementById('wikiDivBody').style.height = (document.getElementById('projectWikiDivRight').offsetHeight - document.getElementById('wikiDivTop').offsetHeight) + 'px';
}

// set color to selected row and remove for previously selected
function setSelecteRowStyleFor(forTr, trId) {
	if(forTr.className.indexOf('wSelectedRow')== -1){
		forTr.className += 'wSelectedRow';
	}
	if(typeof(prevSelectedRow) != 'undefined' && document.getElementById(prevSelectedRow) != null && prevSelectedRow != trId) {
		document.getElementById(prevSelectedRow).className = document.getElementById(prevSelectedRow).className.replace('wSelectedRow', '');
	}
	prevSelectedRow = trId;
}

//To Activate tab on right side.
function activate_rightTab(clickedTab) {
	document.getElementById('projectBlogDivRight').style.display = 'none';
	document.getElementById('projectWikiDivRight').style.display = 'none';
	document.getElementById('projectDetailsDivRight').style.display = 'none';
	document.getElementById('blog-tab').className = 'right-tab deactive-right-tab';
	document.getElementById('wiki-tab').className = 'right-tab deactive-right-tab';
	document.getElementById('detail-tab').className = 'right-tab deactive-right-tab';
	if(clickedTab == 'blog-tab') {
		// Activating right blog tab here
		document.getElementById('projectBlogDivRight').style.display = '';
	 	document.getElementById('wikiMenu').style.display = 'none';
		document.getElementById('blog-tab').className = document.getElementById('blog-tab').className.replace('deactive-right-tab', 'active-right-tab');
	} else if(clickedTab == 'wiki-tab'){
	 	// Activating right wiki tab here
		document.getElementById('projectWikiDivRight').style.display = '';
	 	document.getElementById('wikiMenu').style.display = '';
	 	document.getElementById('wiki-tab').className = document.getElementById('wiki-tab').className.replace('deactive-right-tab', 'active-right-tab');
		document.getElementById('projectWikiDivRight').style.height = (windowHeight - document.getElementById('wikiMenu').offsetHeight - 230) + 'px'; 
	}else{
		// Activating right details tab here
		document.getElementById('projectDetailsDivRight').style.display = '';
	 	document.getElementById('wikiMenu').style.display = 'none';
	 	document.getElementById('detail-tab').className = document.getElementById('detail-tab').className.replace('deactive-right-tab', 'active-right-tab');
	}
	saveState("right_pane_active_tab", clickedTab , 'twopane');
	if(selectedProjectId != '')
		initRightPanels();
	resizePanel();
}

//Expand all tree nodes
function expand_all() {
	if (firstProjectID != '') {
        showAll(firstProjectID, notifyToggleTree) ;
    } else {
    	extAlert(errorAlertTitle, noNodesToexpandMessage , Ext.MessageBox.ERROR);
    }
}
	
//Collapse all tree nodes
function collapse_all() {
    if (firstProjectID != '') {
        hideAll(firstProjectID, notifyToggleTree);
    } else {
    	extAlert(errorAlertTitle, noNodesToCollapseMessage , Ext.MessageBox.ERROR);
    }
}

//Run the normal toggle
function toggleTree(id) {
	toggle(id);
    var expanded = document.getElementById(id).getAttribute("kidsShown");
    notifyToggleTree("node"+id.split('_')[0]+"expanded", expanded);
    selectedProjectId = '';    
}
 
function notifyToggleTree(id, value) {
	if(id != 'tasklisttablecontainerwidth' && id.indexOf('expanded') == -1){
		saveColumnWidth(id, value);
	}
}

// Function to show/hide/close widget 
function hideContent(context,value){
	if( context == 'portfolio_status' ){
		if( value == 1 ){
		  minimize('PortfolioStatusWidgetContent','psminmaximg','psminmaxlink',context);
		}else if( value == 0 ) {
		  maximize('PortfolioStatusWidgetContent','psminmaximg','psminmaxlink',context);
		}else if( value == 2 ){
		  closeWidget('PortfolioStatusWidget','widgetcloseimg','pscloselink',context);
		}
	}else {
		if( value == 1 ){
		  minimize('PortfolioBudgetWidgetContent','pbminmaximg','pbminmaxlink',context);
		}else if( value == 0){
		  maximize('PortfolioBudgetWidgetContent','pbminmaximg','pbminmaxlink',context);
		}else if( value == 2 ){
		  closeWidget('PortfolioBudgetWidget','widgetcloseimg','pbcloselink',context);
		}
	}
	saveState(context, value, 'widget');
	changeTwoPaneTop();
}
	
// Minimize widget div with changing image source, content display and title.
function minimize(divId,imgId,linkId,context){
	if(document.getElementById(divId) != null) {
		document.getElementById(divId).style.display = 'none';
	}
	if( document.getElementById(imgId) != null){
 	   document.getElementById(imgId).src = JSPRootURL+'/images/project/dashboard_arrow-down.gif';
 	}
 	if(document.getElementById(linkId) != null){
	    document.getElementById(linkId).href = "javascript:hideContent('"+context+"','0');";
	}
	if( document.getElementById(imgId) != null){
	    document.getElementById(imgId).title = downTitle;
	}   	   
}
	
// Maximize widget div with changing image source, content display and title.
function maximize(divId,imgId,linkId,context){
	if(document.getElementById(divId) != null) {
		document.getElementById(divId).style.display='';
	}
	if( document.getElementById(imgId) != null){
 	   document.getElementById(imgId).src = JSPRootURL+'/images/project/dashboard_arrow-up.gif';	    
 	}
 	if(document.getElementById(linkId) != null){
	    document.getElementById(linkId).href = "javascript:hideContent('"+context+"','1');";
	}
	if( document.getElementById(imgId) != null){
  	    	document.getElementById(imgId).title = upTitle;
	}   	   
}
	
// Closing widget div with changing image source, content display and title
function closeWidget(divId,imgId,linkId,context){		
	document.getElementById(divId).style.display = 'none';
    document.getElementById(imgId).src = JSPRootURL+'/images/project/dashboard_close.gif' 	    
    document.getElementById(linkId).href = "javascript:saveState('"+context+"','2');";
    document.getElementById(imgId).title = closeTitle;
}
	
// Show div with changing image source, content display and title
function showWidget(divId,imgId,linkId,context){
	document.getElementById(divId).style.display = '';
    document.getElementById(imgId).src = JSPRootURL+'/images/project/dashboard_close.gif' 	    
    document.getElementById(linkId).href = "javascript:saveState('"+context+"','2');";
    document.getElementById(imgId).title = closeTitle;
}
	
// Ajax request for minimize and miximize functionality
function saveState(context, value, type){
	Ext.Ajax.request({
	   url: JSPRootURL+'/portfolio/Project/saveContextChange',
	   params: {module: 150, context: context, value: value, type : type},
	   method: 'POST',
	   success: function(result, text){
	   	var	resText = result.responseText;
	   },
	   failure: function(result, response){
	   }
	});
}

// Function to change two pane view top position when widgets are minimized/maximized/closed
function changeTwoPaneTop(){
	if((!document.getElementById('PortfolioStatusWidget') && !document.getElementById('PortfolioBudgetWidget')) || (document.getElementById('PortfolioStatusWidget').style.display == 'none' && document.getElementById('PortfolioBudgetWidget').style.display == 'none')){
		document.getElementById("pnettabset").style.top = 40 + 'px'; // if both widgets are closed
		var widgetRow = document.getElementById("widgets-row");
		if(widgetRow)
			widgetRow.style.height = 0 + 'px';
		widgetStatus = 0;
	} else if(typeof(document.getElementById('PortfolioStatusWidgetContent')) != 'undefined' && typeof(document.getElementById('PortfolioBudgetWidgetContent')) == 'undefined' || ((document.getElementById('PortfolioStatusWidgetContent') != null && document.getElementById('PortfolioBudgetWidgetContent') != null))){
		if( (document.getElementById('PortfolioStatusWidgetContent').style.display == 'none' && document.getElementById('PortfolioBudgetWidgetContent').style.display == 'none' )){
			document.getElementById("pnettabset").style.top = 85 + 'px'; // if both widgets are minimized
			widgetStatus = 2;
		} else if((document.getElementById('PortfolioBudgetWidget').style.display == 'none' && document.getElementById('PortfolioStatusWidgetContent').style.display == 'none' )){
			document.getElementById("pnettabset").style.top = 85 + 'px'; // if status widget is minimized and budget widget is closed
			widgetStatus = 2;
		} else if((document.getElementById('PortfolioStatusWidget').style.display == 'none' && document.getElementById('PortfolioBudgetWidgetContent').style.display == 'none' )){
			document.getElementById("pnettabset").style.top = 85 + 'px'; // if budget widget is minimized and status widget is closed
			widgetStatus = 2;
		}else {
			document.getElementById("pnettabset").style.top = 240 + 'px'; // if either of widget is opened
			widgetStatus = 1;
		}
	}
	document.getElementById("splitterBar").style.top = document.getElementById("pnettabset").offsetTop + 'px';
	document.getElementById("splitterBarShadow").style.top = document.getElementById("splitterBar").offsetTop + 'px';
	document.getElementById("dropIndicator").style.top = (document.getElementById("pnettabset").offsetTop + 115) + 'px';
	resetPanelsHeight();
}

// Method to show access denied page
function loadAccessDenied(url) {
	Ext.Ajax.request({
	   url: JSPRootURL + url,
	   params: {disableBackLink: true },
	   method: 'POST',
	   success: function(result, request){
	   		document.getElementById('projectWikiDivRight').innerHTML = result.responseText;
	   },
	   failure: function(result, request) {
			document.getElementById('projectWikiDivRight').innerHTML = serverRequestFailed;
	   }
	});
}

// Reloads project list in two pane view.
function reloadProjectList(){
	Ext.Ajax.request({
		url: JSPRootURL + '/portfolio/Project/reload',
		params: {module: moduleId},
		method: 'POST',
		timeout: 180000,
		success: function(result, request){
				document.getElementById('project-list-table-container').innerHTML = result.responseText;
 		    	var cssString = document.getElementById('project-list-css').innerHTML;
				updateColumnWidth(eval(cssString));
		},
		callback: function(result, request) {
			if(document.getElementById('uploadDialogue')) {
				hideUploadPopup();
			}
			resizePanel();
			removeLoadingDiv();	
		}
	});
}

// To save customize view.
function saveCustomview(){
	var form = self.document.form;
	var enteredViewName = getSelectedValue(form.name);
	var currentViewName = getSelectedValue(form.currentviewname);
	var action = 'createportfolio';
	var defaultScenarioID = '';
	var bussinessID = '';
	var makeShared = false;
	var shareAllUsers = false;
	
	if(typeof(enteredViewName) == 'undefined' || enteredViewName == null || trim(enteredViewName).length == 0){
	    extAlert(errorTitle, errorViewNameEmptyMsg , Ext.MessageBox.ERROR);
	    return;
	} if(enteredViewName.indexOf(sharedViewSuffix) != -1){
	    	extAlert(errorTitle,msgFormat.format(errorViewNameContainsSharedMsg, sharedViewSuffix) , Ext.MessageBox.ERROR);
	    	return;
	}  
	if(form.makeShared.checked) {
		for (var i = 0; i < form.businessList.length; i++) {
			if(form.businessList.options[i].selected){
				bussinessID += form.businessList.options[i].value +",";
			}
		}
		bussinessID = bussinessID.substring(0, bussinessID.length-1); 		//remove last unnecessary comma.
		if(typeof(form.shareAllUsers) == 'undefined' && bussinessID == ''){
	    	extAlert(errorTitle, errorSelectBusinessMsg, Ext.MessageBox.ERROR);
	    	return;
		}
		shareAllUsers = form.shareAllUsers.checked
		
		if(bussinessID == '' && !shareAllUsers){
	    	extAlert(errorTitle, errorMakeViewSharedMsg, Ext.MessageBox.ERROR);
	    	return
		}
		makeShared = true;
	}
	for (var i = 0; i < form.defaultForScenarioID.length; i++) {
		if(form.defaultForScenarioID[i].checked){
			defaultScenarioID += form.defaultForScenarioID[i].value +",";
		}
	}
	defaultScenarioID = defaultScenarioID.substring(0, defaultScenarioID.length-1); 		//remove last unnecessary comma.
	
	if (enteredViewName == currentViewName){
		if(!isUserCreatedView){  // check for shared view modification
	    	extAlert(errorTitle, errorSharedViewModificationMsg, Ext.MessageBox.ERROR);
	    	return;
		} else {
			Ext.MessageBox.confirm(confirmViewModifyTitle, 
				msgFormat.format(confirmViewModifyMsg, currentViewName), 
				function(btn){
					if(btn == 'yes'){
						action = 'modifyportfolio';
						modifyCustomview(action, defaultScenarioID, enteredViewName, makeShared, bussinessID, shareAllUsers);
					}else{
						form.elements['name'].focus();
					}
				}
			);
		}
		
		
	} else {
		var viewList = self.document.forms[0].portfolioList;
		for (var index = 1; index < viewList.length; index++) {
			var viewName = viewList.options[index].text;
			if(enteredViewName == trim(viewName.replace(sharedViewSuffix, ''))){
			    extAlert(errorTitle, errorDuplicateViewNameMsg, Ext.MessageBox.ERROR);
			    return;
			}
		}
		showLoadingDiv(savingMsg);
		Ext.Ajax.request({
			url: JSPRootURL + '/portfolio/saveportfolioview/'+action,
			params: {module: moduleId, defaultScenarioID : defaultScenarioID, viewName: enteredViewName
					, makeShared: makeShared, bussinessID: bussinessID, shareAllUsers: shareAllUsers},
			method: 'POST',
			timeout: 180000,
			success: function(result, request){
				viewChanged = false;			
				reloadViewDropDownList(result.responseText);		
			}		
		});
	}
}

// To modify saved customized view.
function modifyCustomview(action, defaultScenarioID, enteredViewName, makeShared, bussinessID, shareAllUsers){
	showLoadingDiv(savingMsg);
	Ext.Ajax.request({
		url: JSPRootURL + '/portfolio/saveportfolioview/'+action,
		params: {module: moduleId, defaultScenarioID : defaultScenarioID, viewName: enteredViewName
		, makeShared: makeShared, bussinessID: bussinessID, shareAllUsers: shareAllUsers},
		method: 'POST',
		timeout: 180000,
		success: function(result, request){
			reloadViewDropDownList(result.responseText);		
			viewChanged = false;			
		}
	});
}

// To confirm customized view delete operation.
function deleteViews(){
	var action = 'removeportfolio';
	var portfolioList = self.document.viewChangeForm.portfolioList;
	var selectedViewName = portfolioList[portfolioList.selectedIndex].text;
	var viewID = getSelectedValue(portfolioList);

	Ext.MessageBox.confirm(confirmViewDeleteTitle, 
		msgFormat.format(confirmViewDeleteMsg, selectedViewName), 
		function(btn){
			if(btn == 'yes'){
			deleteCustomview(action);
		}
	});
}	

// To delete customized view.
function deleteCustomview(action){	
	Ext.Ajax.request({
		url: JSPRootURL + '/portfolio/saveportfolioview/'+action,
		params: {module: moduleId},
		method: 'POST',
		timeout: 180000,
		success: function(result, request){
		   	self.document.location = JSPRootURL + '/portfolio/Project?module=' + moduleId + '&viewID=' + result.responseText + '&portfolio=true';
		}
	});
}

// To add and remove shared view html contents in save portfolio view popup.
function loadSharedHtmlContent(field) {
	if(field.checked == true){
		document.getElementById('sharedViewContent').style.display = '';
		document.getElementById('saveViewMainDiv').style.height = (document.getElementById('saveViewMainDiv').offsetHeight + 74) + 'px'; 
		document.getElementById('sharedViewContent').style.height = 73 + 'px'; 
	} else {
		document.getElementById('sharedViewContent').style.display = 'none';
		document.getElementById('saveViewMainDiv').style.height = (document.getElementById('saveViewMainDiv').offsetHeight - 76) + 'px'; 
		document.getElementById('sharedViewContent').style.height = 0 + 'px'; 
	}
}

//To disable delete views link when shared view is selected
function disablePortfolioActions(disable){
	var deleteLink = document.getElementById('deleteViewLink');
	if (disable) {
			deleteLink.href = "#";
			deleteLink.className = 'disabled';
	} else {
			deleteLink.href = 'javascript:deleteViews();';
			deleteLink.className = 'enabled';
	}
}

//To change view using ajax request
function changeView(viewID,viewName){
	showLoadingDiv(loadingMessage);
	Ext.Ajax.request({
	   url: JSPRootURL +'/portfolio/Project/changeView?module='+moduleId,
	   params: {moduleId : moduleId, viewID : viewID, isSharedView : isSharedView},
	   method: 'POST',
	   success: function(result, request){
			reloadProjectList()
			reloadProtfolioFilters();
			updateRightSidePane();
	   },
	   failure: function(result, response){
	   }
	});
}

// Toggle portfolioViewList on selection fron sharedViewList and vice versa
function toggleViewListSelection(shared){
	if(shared) {
		self.document.forms[0].portfolioList.selectedIndex = 0;
	} else {
		self.document.forms[0].sharedPortfolioList.selectedIndex = 0;
	}
}
// Method to Show Project Dashboard when project row is double clicked from two pane view
function s(projectId) {
	self.document.location = JSPRootURL + '/project/Main.jsp?page='+ JSPRootURL +'/project/Dashboard?id='+projectId;
}

// Method to resize column width when views are loaded using ajax request
function updateColumnWidth(cssObject){
	for (var index = 0; index < cssObject.length; index++) {
		if (typeof(cssObject[index]) != 'undefined' && cssObject[index] != '') {
			changeStyle(cssObject[index].cssClassName, cssObject[index].columnWidth);
		}
	}
}

// Method to open pop down sliding pane filter
function openSlidingPanel(){
	document.getElementById('slidingpanel_content').style.display = '';
	document.getElementById('slidingpanel_toggler').className = 'tap5c_slidingPanelSubject-toggler-collapse';
	document.getElementById('Project').style.display = 'none';
	var slidingPanelHeight;
	if(widgetStatus == 0) {
		slidingPanelHeight = (windowHeight - 197);
	} else if(widgetStatus == 1) {
		slidingPanelHeight = (windowHeight - 363);
	} else {
		slidingPanelHeight = (windowHeight - 237);
	}
	document.getElementById('slidingpanel_content').style.height = slidingPanelHeight + 'px';
	slidingPaneClosed = false;
	resetPanelsHeight();
}

// Method to close pop down sliding pane filter
function closeSlidingPanel(){
	document.getElementById('slidingpanel_content').style.display = 'none';
	document.getElementById('slidingpanel_toggler').className = 'tap5c_slidingPanelSubject-toggler';
	document.getElementById('slidingpanel_content').style.height = 0 + 'px';
	document.getElementById('Project').style.display = '';
	slidingPaneClosed = true;
}

// Method to manage open and closing of pop down sliding pane filter
function toggleSlidingPanel(){
	if(document.getElementById('slidingpanel_content').style.display == 'none'){
		if(!rightPaneClosed) 		//first close right panel, if it is open
			closeRightPanel();
		openSlidingPanel();
	} else{
		closeSlidingPanel();
	}
	resetPanelsWidth();
}

// To submit View Filter Form by Ajax
function submitFilters(){
    theForm = self.document.viewFilterForm;
    if(validateFilters(theForm)){
		showLoadingDiv(loadingMessage);
	    ajaxForm = new Ext.form.BasicForm(theForm);
	    ajaxForm.submit(onSubmit);
	    viewChanged = true;			
	    closeSlidingPanel();
	}
}

// Action to perform on form submit
var onSubmit = {
	   url: JSPRootURL +'/portfolio/Project/applyFilters',
       success: function (form,action) {
			reloadProjectList();       		
        },
        failure: function (form,action) {
			reloadProjectList();       		
        }
 };
 
// To sort project list when sorting is aplied on multiple columns 
// columnName will contain json representation of 
// selected columns along with order
function sort(columnName, multiLevelSort){
	if(columnName != ''){
		showLoadingDiv(loadingMessage);
		if(multiLevelSort){
			sorterParameterString = columnName;
		} else {
			orderDescending = !orderDescending;
			sortedColumn = columnName;
			document.getElementById('sort-img-'+sortedColumn).width = "12";
			document.getElementById('sort-img-'+sortedColumn).height = "12";
			document.getElementById('sort-img-'+sortedColumn).src = JSPRootURL + '/images/default/tree/loading-small.gif';
			document.getElementById('sort-img-'+sortedColumn).className = '';
			document.getElementById('sort-img-'+sortedColumn).style.display = '';
			sorterParameterString = '[{"columnName":"'+ columnName +'", "order":"'+ orderDescending +'"}]';
		}
		Ext.Ajax.request({
		   url: JSPRootURL +'/portfolio/Project/applySort',
		   params: {sorterParameterString: sorterParameterString, multiLevelSort : multiLevelSort},
		   method: 'POST',
		   success: function(result, request){
				if(!multiLevelSort)
					reloadProjectList();       		
		   },
		   failure: function(result, request) {
				if(!multiLevelSort)
					reloadProjectList();       		
		   },
		   callback:function(result , response){
			if(!multiLevelSort)
				initSortingImage(sortedColumn);
		   }
		});
	}
}

// To display sorting image in column header
function initSortingImage(sortedColumn){
	var imageSrc;
	if(orderDescending){
		imageSrc = JSPRootURL + '/images/default/grid/sort_desc.gif';
	}else{
		imageSrc = JSPRootURL + '/images/default/grid/sort_asc.gif';
	}
	document.getElementById('sort-img-'+sortedColumn).width = "12";
	document.getElementById('sort-img-'+sortedColumn).height = "5";
	document.getElementById('sort-img-'+sortedColumn).src = imageSrc;
	document.getElementById('sort-img-'+sortedColumn).style.display = '';
}

// To show alter on Window.beforeUnload event if changes made to view are not saved
function beforeUnload(e){
	if(viewChanged)
		return viewModifiedMessage;
	else
		executed = true;
}

// To apply field validation in filters
function validateFilters(frm){
	// if selectedProjectID_filter_26_ignore_other_filters(fiter to ignore all other filters and select by project from multi select list) not selected
	if(!frm.selectedProjectID_filter_26_ignore_other_filters.checked){
		// if filterfilter_3(filter for % work complete) is not numeric
	  	if(frm.filterfilter_3 && (frm.filterfilter_3.value != '') && ((!isNumber(frm.filterfilter_3.value)) ||  (parseInt(frm.filterfilter_3.value, 10) > 100))) {
			extAlert(errorTitle, errorInvalidPercentcompleteValueMsg , Ext.MessageBox.ERROR);
	   		return false;
	  	}
		// if filterfilter_19_value(filter for budget tolat cost) is not numeric
	   	if(frm.filterfilter_19_value && (frm.filterfilter_19_value.value != '') && !(isNumber(frm.filterfilter_19_value.value))){
			extAlert(errorTitle, errorInvalidBudgetCostValueMsg , Ext.MessageBox.ERROR);
	   		return false;
	   	}
	}
	return true;
}

// To check for given strValue is numeric
function isNumber(strValue){
  var objRegExp  =  /(^-?\d\d*\.\d*$)|(^-?\d\d*$)|(^-?\.\d\d*$)/;
  return objRegExp.test(strValue);
}

// To reload portfolio view drop down list when new view is created or existing view is modified.
function reloadViewDropDownList(viewID){
	Ext.Ajax.request({
	   url: JSPRootURL +'/portfolio/Project/reloadProjectPortfolioViewDropDowList',
	   params: {moduleId : moduleId, viewID: viewID},
	   method: 'POST',
	   success: function(result, request){
		    document.getElementById('myportfolio-view-drop-down-list-td').innerHTML = result.responseText;
			isUserCreatedView = true;
			disablePortfolioActions(false);
			removeLoadingDiv();		
			hideUploadPopup();
	   },
	   failure: function(result, request) {
	   }
	});
}

// To load portfolio view filters when a view is loaded by selecting from drop down list.
function reloadProtfolioFilters(){
	Ext.Ajax.request({
	   url: JSPRootURL +'/portfolio/Project/reloadProjectPortfolioViewFilter',
	   params: {moduleId : moduleId},
	   method: 'POST',
	   success: function(result, request){
		    document.getElementById('slidingpanel_content').innerHTML = result.responseText;
	   },
	   failure: function(result, request) {
	   }
	});
}

// To update contents if rigth side blog,wiki and details tab when a view is loaded by selecting from drop down list
function updateRightSidePane(){
	document.getElementById('blogDivTop').innerHTML = selectProjectBlogTabMessage;
	document.getElementById('wikiDivTop').innerHTML = selectProjectWikiTabMessage;

	document.getElementById('blogDivBody').innerHTML = '';
	document.getElementById('projectDetailsDivRight').innerHTML = '<div class="two-pane-right-top" id="detailsDivTop">' + selectProjectDetailsTabMessage + '</div>';
	document.getElementById('projectWikiDivRight').innerHTML = '<div class="two-pane-right-top" id="wikiDivTop">'+ selectProjectWikiTabMessage +'</div>';
}

// To set id to action links on left side toolbar
function setIDLeftToolBarLinks(){
	for (var iterator = 0; iterator < portfolioLinks.length; iterator++) {
		if (portfolioLinks[iterator].href == 'javascript:deleteViews();'){
			portfolioLinks[iterator].setAttribute('id','deleteViewLink');	
		}
	}
}

// To display tooltip text for project name on mouse over event
function t(id) {
	document.getElementById('t'+id).title = document.getElementById('s'+id).innerHTML;
}
