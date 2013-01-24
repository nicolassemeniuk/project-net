var curWidth = 0;
var curX = 0;
var newX = 0;
var mouseButtonPos = "up";
var expandHeader = true;

var windowWidth = getWindowWidth();
var windowHeight = getWindowHeight();

//Function 'setPos(...) gets the original div width.
function setPos(e) {
	//For handling events in ie vs. w3c.
	curEvent = ((typeof event == "undefined")? e: event);
	//Sets mouse flag as down.
	mouseButtonPos = "down";
	//Gets position of click.
	curX = curEvent.clientX;
	//Get the width of the div.
	var tempWidth = document.getElementById("task-list").style.width;
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
	 	if(newWidth > (windowWidth - 306)) newWidth = (windowWidth - 306);
		taskListPanelWidth = newWidth;
		document.getElementById("splitterBarShadow").style.left = (newWidth + 1) + "px";
	}
}

function resizeRightPanel(){
		document.getElementById("splitterBarShadow").style.display = 'none';
		mouseButtonPos = "up";
		if(Ext.isIE)
			document.detachEvent("onmouseup", resizeRightPanel);
		else
			document.removeEventListener("mouseup", resizeRightPanel, true);
		
		var newWidth = taskListPanelWidth;
		var leftPaneIncreased = newWidth > parseInt(document.getElementById("task-list").style.width);
		//Set the new width.
		document.getElementById("task-list").style.width = newWidth + "px";
		document.getElementById('slidingpanel-toolbar').style.width = (newWidth + 3) + "px";
		//Set the new left of the splitter bar.
		document.getElementById("splitterBar").style.left = (newWidth + 1) + "px";
		document.getElementById("taskBlogDivRight").style.width = (windowWidth - newWidth  - 229) + 'px';
		document.getElementById("taskWikiDivRight").style.width = (windowWidth - newWidth  - 229) + 'px';
		document.getElementById('taskDetailsDivRight').style.width = (windowWidth - newWidth  - 229) + 'px';
		
		document.getElementById("rightTabSet").style.width = ((windowWidth - newWidth)  - 228)+'px';
		document.getElementById('schedule_info_div').style.width = newWidth - 2 + "px";
		document.getElementById("task-list-header").style.width = (newWidth-17) + "px";
		
		document.getElementById("rightTabSet").style.width = (parseInt(document.getElementById("slidingpanel").style.width) - parseInt(document.getElementById("slidingpanel-toolbar").style.width)-1)+'px';
		
		mangeRightTabScrolling(parseInt(document.getElementById("rightTabSet").style.width), !leftPaneIncreased);
		resizeWikiMenu();
		savePosition();
}

function resizePanel(){
	windowWidth = getWindowWidth();
	//calulate and set width of workplan panel.
	document.getElementById('pnettabset').style.width = (windowWidth - 217) + 'px';
	document.getElementById('slidingpanel').style.width = (windowWidth - 228) + 'px';
	document.getElementById('slidingpanel_content').style.width = (windowWidth - 229) + 'px';
	document.getElementById('bottom-bar').style.width = (windowWidth - 229) + 'px';
	resetPanelsHeight();
	resetPanelsWidth();
	resizePagingTab();
	putScreenResolutionInCookie();
};

function resetPanelsHeight(){
	var sc = parseInt(document.getElementById('slidingpanel_content').offsetHeight); //sliding panel height. it will be 0 if closed.
	windowHeight = getWindowHeight() - sc;
	
	//calulate and set height of workplan panel.
	document.getElementById('pnettabset').style.height = ((windowHeight - 125) < 560 - sc ? 560 - sc : (windowHeight - 125)) + 'px';
	
	document.getElementById('Schedule').style.height = (((windowHeight - 205) < 475 - sc ? 475 - sc : (windowHeight - 205)) + 3) + 'px';
	document.getElementById('Gantt View').style.height = (((windowHeight - 205) < 475 - sc ? 475 - sc : (windowHeight - 205)) + 3) + 'px';
	document.getElementById('taskBlogDivRight').style.height = (((windowHeight - 205) < 475 - sc ? 475 - sc : (windowHeight - 205)) + 3) + 'px';
	
	document.getElementById('blogDivBody').style.height = (((windowHeight - 230) < 450 - sc ? 450 - sc : (windowHeight - 230)) + 3) + 'px';
	document.getElementById('taskWikiDivRight').style.height = (((windowHeight - 205) < 475 - sc ? 475 - sc : (windowHeight - 205)) + 3) + 'px'; 
	document.getElementById('taskDetailsDivRight').style.height = (((windowHeight - 205) < 475 - sc ? 475 - sc : (windowHeight - 205)) + 3) + 'px'; 
	
	document.getElementById('splitterBar').style.height = (((windowHeight - 153) < 527 - sc ? 527 - sc : (windowHeight - 153)) - 1) + 'px';
	document.getElementById('splitterBarShadow').style.height = (((windowHeight - 153) < 527 - sc ? 527 - sc : (windowHeight - 153)) - 1) + 'px';
	
	if(document.getElementById('button-bar').style.display != 'none'){
		document.getElementById('task-list').style.height = ((windowHeight - 235) < 445 - sc ? 445 - sc : (windowHeight - 235)) + 'px';
		document.getElementById('blogsCollapsed').style.height = ((windowHeight - 235) < 445 - sc ? 445 - sc : (windowHeight - 235)) + 'px';
	}else{
		document.getElementById('task-list').style.height = (((windowHeight - 205) < 475 - sc ? 475 - sc : (windowHeight - 205)) + 3) + 'px';
		document.getElementById('blogsCollapsed').style.height = (((windowHeight - 205) < 475 - sc ? 475 - sc : (windowHeight - 205)) + 3) + 'px';
	}
	//set inicator top position
	document.getElementById('dropIndicator').style.top = (140 + sc)+'px';
}

function resetPanelsWidth(){
	windowWidth = getWindowWidth();
	
	//if right panel collapsed
	if(document.getElementById('blogsCollapsed').style.display == ''){
		document.getElementById('slidingpanel-toolbar').style.width = (windowWidth - 228) + 'px';
		document.getElementById('task-list').style.width = (windowWidth - 234) + 'px';
		document.getElementById('task-list-header').style.width = (windowWidth - 250) + 'px';
		document.getElementById('splitterBar').style.display = 'none';
		document.getElementById('schedule_info_div').style.width = (windowWidth - 237) + 'px';
	}else{
		//if right panel expanded
		document.getElementById('splitterBar').style.display = '';
		if(taskListPanelWidth && taskListPanelWidth < windowWidth - 296){
			resizeRightPanel();
		}else{
			taskListPanelWidth =((windowWidth * 5)/6) - 235;
			resizeRightPanel();
		}
	}
	document.getElementById('task-list-table-container-header').width = (document.getElementById('taskListTable').offsetLeft + document.getElementById('taskListTable').offsetWidth) + 50;
	document.getElementById('task-list-table-container').width = (document.getElementById('taskListTable').offsetLeft + document.getElementById('taskListTable').offsetWidth) + 50;
	notifyToggleTree("tasklisttablecontainerwidth" , document.getElementById('task-list-table-container').width);
}

function savePosition(){
	notifyToggleTree('tasklistpanelwidth', (document.getElementById("task-list").style.width).replace("px",""));
}

function applyPersonalSettings(){
	var personProperty = Ext.util.JSON.decode(personalSettingsString);
	
	//if no user setting saved
	if(personProperty.rightTabsetExpanded == "null"){
		openRightPanel();
		resizeRightPanel((((windowWidth * 5)/6) - 235));
	}else if(personProperty.rightTabsetExpanded == "true"){//if right tab set expanded
		openRightPanel();
		//set width for right (task list) panel if width is as per persisted resolution.
		if(personProperty.taskListPanelWidth < windowWidth - 296){
			taskListPanelWidth = personProperty.taskListPanelWidth;		 
			resizeRightPanel();
		}
	}else{
		closeRightPanel();
	}
	
	if(!isNaN(personProperty.taskListTableContainerWidth)){
		document.getElementById('task-list-table-container').width = personProperty.taskListTableContainerWidth;
	}else{
		document.getElementById('task-list-table-container').width = (document.getElementById('taskListTable').offsetLeft + document.getElementById('taskListTable').offsetWidth) + 50;
	}
	activate_rightTab(personProperty.rightTabSet == "null" ? 'blog-tab' : personProperty.rightTabSet);
	resizeHeader();	
}

//To catch resize event
window.onresize = resizePanel;
resizePanel();

function showBottomWikiMenu(openMenu) {
	if(openMenu) {
		document.getElementById('wikiMenu').style.display = '';
	} else {
		document.getElementById('wikiMenu').style.display = 'none';
	}
}

//handle events
window.document.onkeydown = setKeyCode;
window.document.onkeyup = resetKeyCode;
window.onbeforeunload = beforeUnload;
