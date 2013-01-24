var curWidth = 0;
var curX = 0;
var newX = 0;
var mouseButtonPos = "up";
var taskListPanelWidth;
var expandHeader = true;

//Resize right tab set according to the assignment list div.
function resizeRightPanel(){
	document.getElementById("splitterBarShadow").style.display = 'none';
	mouseButtonPos = "up";
	if(Ext.isIE)
		document.detachEvent("onmouseup", resizeRightPanel);
	else
		document.removeEventListener("mouseup", resizeRightPanel, true);
	//taskListPanelWidth = newWidth;
	var newWidth = taskListPanelWidth;
	var leftPaneIncreased = newWidth > parseInt(document.getElementById("assignmentList").style.width);
	
	//Set the new width.
	document.getElementById("assignmentList").style.width = newWidth + "px";
	document.getElementById('slidingpanel-toolbar').style.width = (newWidth + 3) + "px";
	//Set the new left of the splitter bar.
	document.getElementById("splitterBar").style.left = (newWidth + 1) + "px";
	document.getElementById("taskBlogDivRight").style.width = (windowWidth - newWidth  - 229) + 'px';
	document.getElementById("taskWikiDivRight").style.width = (windowWidth - newWidth  - 227) + 'px';
	document.getElementById('taskDetailsDivRight').style.width = (windowWidth - newWidth  - 227) + 'px';
	
	document.getElementById("rightTabSet").style.width = (windowWidth - newWidth  - 232)+'px';
	document.getElementById('assignments_info_div').style.width = newWidth - 2 + "px";
	document.getElementById("assignments-list-header").style.width = (newWidth-17) + "px";
	//re-adjust right tab set finally.
	try{
		document.getElementById("rightTabSet").style.width = (parseInt(document.getElementById("slidingpanel").style.width) - parseInt(document.getElementById("slidingpanel-toolbar").style.width)-1)+'px';
	}catch(e){
	}
	mangeRightTabScrolling(parseInt(document.getElementById("rightTabSet").style.width), !leftPaneIncreased);
	resizeWikiMenu();
	savePosition();
}

//Initialize view height.
function resetPanelsHeight(){
	windowHeight = getWindowHeight();
	//calulate and set height of workplan panel.
	document.getElementById('pnettabset').style.height =  (windowHeight - 125) + 'px';
	document.getElementById('pnetTabsetContent').style.height =  (windowHeight - 152) + 'px';
	document.getElementById('blogDivBody').style.height = (windowHeight - 202) + 'px';
	
	document.getElementById('taskBlogDivRight').style.height =  (windowHeight - 202) + 'px';
	document.getElementById('taskWikiDivRight').style.height = (windowHeight - 202) + 'px';
	document.getElementById('taskDetailsDivRight').style.height = (windowHeight - 202) + 'px';
	
	document.getElementById('splitterBar').style.height =  (windowHeight - 152) + 'px';
	document.getElementById("splitterBarShadow").style.height = (windowHeight - 173) + 'px';
	if(slidingPanelStatus){
		if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
			document.getElementById('MyAssignments').style.height = (windowHeight - 370) + 'px';
			document.getElementById('assignmentList').style.height =  (windowHeight - 370) + 'px';
			document.getElementById('blogsCollapsed').style.height =  (windowHeight - 370) + 'px';
		}else{
			document.getElementById('MyAssignments').style.height = (windowHeight - 369) + 'px';
			document.getElementById('assignmentList').style.height =  (windowHeight - 369) + 'px';
			document.getElementById('blogsCollapsed').style.height =  (windowHeight - 369) + 'px';
		}
	}else{
		document.getElementById('MyAssignments').style.height = (windowHeight - 202) + 'px';
		document.getElementById('assignmentList').style.height =  (windowHeight - 202) + 'px';
		document.getElementById('blogsCollapsed').style.height =  (windowHeight - 202) + 'px';
	}
	resizePagingTab();
}

//Initialize assignemtns view width.
function resetPanelsWidth(){
	windowWidth = getWindowWidth();
	//if right panel collapsed
	if(document.getElementById('blogsCollapsed').style.display == ''){
		document.getElementById('slidingpanel-toolbar').style.width = (windowWidth - 228) + 'px';
		//document.getElementById('assignment-table-container').style.width = (windowWidth - 63) + 'px';
		document.getElementById('assignmentList').style.width = (windowWidth - 232) + 'px';
		document.getElementById('splitterBar').style.display = 'none';
		document.getElementById('assignments_info_div').style.width = (windowWidth - 236) + 'px';
	}else{
		//if right panel expanded
		document.getElementById('splitterBar').style.display = '';
		if(!(taskListPanelWidth && taskListPanelWidth < windowWidth - 297)){
			taskListPanelWidth = (((windowWidth * 5)/6) - 235)
		}
		resizeRightPanel();
	}
	document.getElementById('bottom-bar').style.width = windowWidth-225 +'px';
	document.getElementById("assignments-list-header").style.width = (parseInt(document.getElementById("assignmentList").style.width) - 17) + "px";
	//document.getElementById('container').style.width = document.getElementById('assignmentList').style.width;
}

//Function 'setPos(...) gets the original div width.
function setPos(e) {
	//For handling events in ie vs. w3c.
	curEvent = ((typeof event == "undefined")? e: event);
	//Sets mouse flag as down.
	mouseButtonPos = "down";
	//Gets position of click.
	curX = curEvent.clientX;
	//Get the width of the div.
	var tempWidth = document.getElementById("assignmentList").style.width;
	//Get the width value.
	var widthArray = tempWidth.split("p");
	//Set the current width.
	curWidth = parseInt(widthArray[0]);
	
	if(Ext.isIE)
		document.attachEvent("onmouseup",   resizeRightPanel);
	else
		document.addEventListener("mouseup", resizeRightPanel, true);
}

//Function getPos(...) changes the width of the div while the mouse button is pressed.
function getPos(e){
	if( mouseButtonPos == "down" ) {
		document.getElementById("splitterBarShadow").style.display = '';
		//For handling events in ie vs. w3c.
		curEvent = ((typeof event == "undefined")? e: event);
		//Get new mouse position.
		newX = curEvent.clientX;
		//Calculate movement in pixels.
		var pixelMovement = parseInt(newX - curX);
		//Determine new width.
		var newWidth = parseInt(curWidth + pixelMovement);
		//Enforce a minimum width.
		if(newWidth < 60)	newWidth = 60; 
	 	if(newWidth > (windowWidth - 308)) newWidth = (windowWidth - 308);
		document.getElementById("splitterBarShadow").style.left = (newWidth + 1) + "px";
		taskListPanelWidth = newWidth;
	}
}

//To handel window resize events.
function resizePanel(){
	windowWidth = getWindowWidth();
	//calulate and set width of workplan panel.
	document.getElementById('pnettabset').style.width = (windowWidth - 217) + 'px';
	document.getElementById('slidingpanel_content').style.width = (windowWidth - 234) + 'px';
	document.getElementById('slidingpanel').style.width = (windowWidth - 228) + 'px';
	resetPanelsHeight();
	resetPanelsWidth();
	putScreenResolutionInCookie()
};

//Initialze personal setting if saved.
function applyPersonalSettings(){
	 personProperty = Ext.util.JSON.decode(personProperty);
	//if no user setting saved
	if(personProperty.rightTabsetExpanded == "null"){
		openRightPanel();
	}else if(personProperty.rightTabsetExpanded == "true"){//if right tab set expanded
		openRightPanel();
		//set width for right (task list) panel if width is as per persisted resolution.
		if(personProperty.assignmentListPanelWidth < windowWidth - 297){
			taskListPanelWidth = personProperty.assignmentListPanelWidth;
			resizeRightPanel();
		}
	}else{
		closeRightPanel();
	}
	if(!isNaN(personProperty.taskListTableContainerWidth)){
		document.getElementById('assignment-table-container').width = personProperty.taskListTableContainerWidth;
	}else{
		document.getElementById('assignment-table-container').width = (document.getElementById('assignmentListTable').offsetLeft + document.getElementById('assignmentListTable').offsetWidth) + 50;
	}
	document.getElementById('assignment-list-table-container-header').width = document.getElementById('assignment-table-container').width;
	if(personProperty.rightTabSet){
		activate_rightTab(personProperty.rightTabSet);
	}else{
		activate_rightTab(rightTebSetArray[0]);
	}
	resizeHeader();
}

// To save task list div width
function savePosition(){
	notifyToggleTree('tasklistpanelwidth', (document.getElementById("assignmentList").style.width).replace("px",""));
}

//To catch resize event
window.onresize = resizePanel;
