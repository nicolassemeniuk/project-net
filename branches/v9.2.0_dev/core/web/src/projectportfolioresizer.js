var curWidth = 0;
var curX = 0;
var newX = 0;
var mouseButtonPos = "up";
var projectListPanelWidth;
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
	var tempWidth = document.getElementById("project-list").style.width;
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
		if(newWidth < 60)	newWidth = 60; ;
	 	if(newWidth > (windowWidth - 303)) newWidth = (windowWidth - 303);
		projectListPanelWidth = newWidth;
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
		
		var newWidth = projectListPanelWidth;
		var leftPaneIncreased = newWidth > parseInt(document.getElementById("project-list").style.width);

		//Set the new width.
		document.getElementById('project-list').style.width = (newWidth - 6) + "px";
		document.getElementById("project-list-header").style.width = (newWidth - 22) + "px";
		document.getElementById('slidingpanel-toolbar').style.width = (newWidth) + "px";

		//Set the new left of the splitter bar.
		document.getElementById("splitterBar").style.left = (newWidth - 5) + "px";
		document.getElementById("projectBlogDivRight").style.width = (windowWidth - newWidth  - 225) + 'px';
		document.getElementById("projectWikiDivRight").style.width = (windowWidth - newWidth  - 225) + 'px';
		document.getElementById("projectDetailsDivRight").style.width = (windowWidth - newWidth  - 225) + 'px';
		document.getElementById("rightTabSet").style.width = (parseInt(document.getElementById("slidingpanel").style.width) - parseInt(document.getElementById("slidingpanel-toolbar").style.width) - 3)+'px';
		mangeRightTabScrolling(parseInt(document.getElementById("rightTabSet").style.width), !leftPaneIncreased);

		saveState('project_list_panel_width', (document.getElementById("project-list").style.width).replace("px",""), 'twopane');
}

function resizePanel(){
	windowWidth = getWindowWidth();
	//calulate and set width of project panel.
	document.getElementById('projectportfoliomaindiv').style.width = (windowWidth - 222) + 'px';
	document.getElementById('pnettabset').style.width = (windowWidth - 225) + 'px';
	document.getElementById('slidingpanel').style.width = (windowWidth - 225) + 'px';
	document.getElementById('Project').style.width = (windowWidth - 227) + 'px';
	document.getElementById('slidingpanel_content').style.width = (windowWidth - 235) + 'px';	
	resetPanelsHeight();
	resetPanelsWidth();
	changeTwoPaneTop();
	putScreenResolutionInCookie();
};

function resetPanelsHeight(){
	var sc = parseInt(document.getElementById('slidingpanel_content').offsetHeight); //sliding panel height. it will be 0 if closed.
	windowHeight = getWindowHeight() - sc;
	
	if(widgetStatus == 0){        // when both widgets are closed
		resetPanelsHeightWidgetClosed(windowHeight);
	} else if(widgetStatus == 1){  // when both or either of two widgets are open
		resetPanelsHeightWidgetOpen(windowHeight);
	} else{                       // when both widgets are minimized
		resetPanelsHeightWidgetMinimized(windowHeight);
	}
}

function resetPanelsWidth(){
	windowWidth = getWindowWidth();
	//if right panel collapsed
	if(document.getElementById('blogsCollapsed').style.display == ''){
		document.getElementById('slidingpanel-toolbar').style.width = (windowWidth - 225) + 'px';
		document.getElementById('project-list').style.width = (windowWidth - 230) + 'px';
		document.getElementById('project-list-header').style.width = (windowWidth - 242) + 'px';
		document.getElementById('splitterBar').style.display = 'none';
	}else{
		//if right panel expanded
		document.getElementById('splitterBar').style.display = '';
		if(projectListPanelWidth && projectListPanelWidth < windowWidth - 296){
			resizeRightPanel();
		}else{
			projectListPanelWidth =((windowWidth * 5)/6) - 235;
			resizeRightPanel();
		}
	}
}

// To apply personalize settings 
function applyPersonalSettings(){
	if(rightPaneCollapsed == null){
		openRightPanel();
	} else if(rightPaneCollapsed == 'true'){ // if right side pane collapsed 
		closeRightPanel();
	} else{ // if right side pane expanded
		openRightPanel();
		//set width for right (task list) panel if width is as per persisted resolution.
		if(projectListPanelWidth < windowWidth - 297){
			projectListPanelWidth = projectListPanelWidthPersonalProperty;
			resizeRightPanel();
		}
	}
	activate_rightTab(rightPaneActiveTab);	
}

// To reset panel height when both or either if widget is open
function resetPanelsHeightWidgetOpen(windowHeight){
	if(slidingPaneClosed){
		document.getElementById('pnettabset').style.height = (windowHeight - 318) + 'px';
		document.getElementById('project-content-main-div').style.height = (windowHeight - 333) + 'px';
	} else {
		document.getElementById('pnettabset').style.height = (getWindowHeight() - 318) + 'px';
		document.getElementById('project-content-main-div').style.height = (getWindowHeight() - 333) + 'px';
	}

	document.getElementById('Project').style.height = (windowHeight - 408) + 'px';
	document.getElementById('project-list').style.height = (windowHeight - 358) + 'px';
	
	document.getElementById('blogDivBody').style.height = (windowHeight - 395) + 'px';
	document.getElementById('projectBlogDivRight').style.height = (windowHeight - 358) + 'px';
	document.getElementById('projectWikiDivRight').style.height = (windowHeight - document.getElementById('wikiMenu').offsetHeight - 356) + 'px'; 
	document.getElementById('projectDetailsDivRight').style.height = (windowHeight - 398) + 'px'; 
	document.getElementById('splitterBar').style.height = (windowHeight - 333) + 'px';
	document.getElementById('splitterBarShadow').style.height = (windowHeight - 333) + 'px';
	document.getElementById('blogsCollapsed').style.height = (windowHeight - 361) + 'px';
}

// To reset panel height when both widgets are minimized
function resetPanelsHeightWidgetMinimized(windowHeight){
	if(slidingPaneClosed){
		document.getElementById('pnettabset').style.height = (windowHeight - 192) + 'px';
		document.getElementById('project-content-main-div').style.height = (windowHeight - 207) + 'px';
	} else {
		document.getElementById('pnettabset').style.height = (getWindowHeight() - 192) + 'px';
		document.getElementById('project-content-main-div').style.height = (getWindowHeight() - 207) + 'px';
	}

	document.getElementById('Project').style.height = (windowHeight - 282) + 'px';
	document.getElementById('project-list').style.height = (windowHeight - 232) + 'px';
	
	document.getElementById('blogDivBody').style.height = (windowHeight - 269) + 'px';
	document.getElementById('projectBlogDivRight').style.height = (windowHeight - 232) + 'px';
	document.getElementById('projectWikiDivRight').style.height = (windowHeight - document.getElementById('wikiMenu').offsetHeight - 230) + 'px'; 
	document.getElementById('projectDetailsDivRight').style.height = (windowHeight - 272) + 'px'; 
	document.getElementById('splitterBar').style.height = (windowHeight - 207) + 'px';
	document.getElementById('splitterBarShadow').style.height = (windowHeight - 207) + 'px';
	document.getElementById('blogsCollapsed').style.height = (windowHeight - 235) + 'px';
}

// To reset panel height when both widgets are closed
function resetPanelsHeightWidgetClosed(windowHeight){
	if(slidingPaneClosed){
		document.getElementById('pnettabset').style.height = (windowHeight - 152) + 'px';
		document.getElementById('project-content-main-div').style.height = (windowHeight - 167) + 'px';
	} else {
		document.getElementById('pnettabset').style.height = (getWindowHeight() - 152) + 'px';
		document.getElementById('project-content-main-div').style.height = (getWindowHeight() - 167) + 'px';
	}

	document.getElementById('Project').style.height = (windowHeight - 242) + 'px';
	document.getElementById('project-list').style.height = (windowHeight - 192) + 'px';
	
	document.getElementById('blogDivBody').style.height = (windowHeight - 229) + 'px';
	document.getElementById('projectBlogDivRight').style.height = (windowHeight - 192) + 'px';
	document.getElementById('projectWikiDivRight').style.height = (windowHeight - document.getElementById('wikiMenu').offsetHeight - 190) + 'px'; 
	document.getElementById('projectDetailsDivRight').style.height = (windowHeight - 192) + 'px'; 
	document.getElementById('splitterBar').style.height = (windowHeight - 167) + 'px';
	document.getElementById('splitterBarShadow').style.height = (windowHeight - 167) + 'px';
	document.getElementById('blogsCollapsed').style.height = (windowHeight - 195) + 'px';
}

//To catch resize event
window.onresize = resizePanel;
window.onafterload = resizePanel;

function showBottomWikiMenu(openMenu) {
	if(openMenu) {
		document.getElementById('wikiMenu').style.display = '';
	} else {
		document.getElementById('wikiMenu').style.display = 'none';
	}
}