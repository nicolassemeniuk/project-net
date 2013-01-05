//rightTebSetArray need to be define and initialize in page e.g. var  rightTebSetArray = new Array('blog-tab','wiki-tab','detail-tab');

var offset = 0;
/*
*Load details in specified panel for specified object id.
*/
function loadDetails(objectId, panel, isLoadTask){
	if(!objectId || !panel){
		return;
	}
	
	panel.innerHTML = '<div class="two-pane-right-top">' + loadingMessage + '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" /></div>'
	Ext.Ajax.request({
		url: JSPRootURL +'/details/ObjectDetails/prm?module='+moduleId,
		params: {module : moduleId, objectId : objectId, isLoadTask: isLoadTask},
		method: 'POST',
		success: function(result, request){
	   		panel.innerHTML = result.responseText;
		},
		failure: function(result, response){
		}
	});		   
}

function scrollRightTabSet(direction){
	if(!rightTebSetArray || rightTebSetArray.length < 2){
		return;
	}
	if(direction == 'pre'){
		activate_rightTab(rightTebSetArray[getActiveRightTabIndex() - 1]);
	}else{
		activate_rightTab(rightTebSetArray[getActiveRightTabIndex() + 1]);
	}
	
	for(var i=0; i < rightTebSetArray.length; i++){
		document.getElementById(rightTebSetArray[i]).style.display = '';
	}
	mangeRightTabScrolling(parseInt(document.getElementById("rightTabSet").style.width), false)
}

function mangeRightTabScrolling(panelWidth, isIncreased){
	if(!rightTebSetArray || rightTebSetArray.length < 2){
		return;
	}
	if(isIncreased){
		showRightTabs(panelWidth);
	}else{
		hideRightTabs(panelWidth);
	}
	
	var previous = false;
	var next = false;
	var activeTabIndex = 0;
	
	for(var i=0; i<rightTebSetArray.length; i++){
		if(document.getElementById(rightTebSetArray[i]).className.indexOf("deactive-right-tab") < 0 ){
			activeTabIndex = i;
			break;
		}
	}
	
	for(var i=0; i<rightTebSetArray.length; i++){
		if(document.getElementById(rightTebSetArray[i]).style.display == 'none'){
			if(i < activeTabIndex)
				previous = true;
			else
				next = true;
		}
	}
	
	document.getElementById('right-tab-scroll-previous').style.display = 'none';
	document.getElementById('right-tab-scroll-next').style.display = 'none';
	
	if(previous){
		document.getElementById('right-tab-scroll-previous').style.display = '';
	}
	if(next){
		document.getElementById('right-tab-scroll-next').style.display = '';
	}
}

function hideRightTabs(panelWidth){
	if((getDisplayedRightTabsWidth()+50 > panelWidth) && (getDisplyedRightTabCount() > 1)){
		for(var i= rightTebSetArray.length-1; i>=0; i--){
			if(document.getElementById(rightTebSetArray[i]).style.display != 'none' && document.getElementById(rightTebSetArray[i]).className.indexOf("deactive-right-tab") != -1){
				document.getElementById(rightTebSetArray[i]).style.display = 'none';
				break;
			}
		}
		hideRightTabs(panelWidth);
	}
}

function showRightTabs(panelWidth){
	if((panelWidth >= (getDisplayedRightTabsWidth() + 80) ) && (getDisplyedRightTabCount() != rightTebSetArray.length) ){
		for(var i= rightTebSetArray.length-1; i>=0; i--){
			if(document.getElementById(rightTebSetArray[i]).style.display == 'none'){
				document.getElementById(rightTebSetArray[i]).style.display = '';
				break;
			}
		}
		showRightTabs(panelWidth);
	}
}

function getDisplayedRightTabsWidth(){
	var tabHeadersWidth = 0;
	for(var i=0; i<rightTebSetArray.length; i++){
		if(document.getElementById(rightTebSetArray[i]).style.display != 'none')
			tabHeadersWidth += 30//parseInt(document.getElementById(rightTebSetArray[i]).style.width);
	}
	return tabHeadersWidth;
}

function getDisplyedRightTabCount(){
	var displayedTab = 0;	
	for(var i=0; i < rightTebSetArray.length; i++){
		if(document.getElementById(rightTebSetArray[i]).style.display != 'none')
			displayedTab++;
	}
	return displayedTab;
}

function getActiveRightTabIndex(){
	for(var i=0; i < rightTebSetArray.length; i++){
		if(document.getElementById(rightTebSetArray[i]).className.indexOf("deactive-right-tab") == -1)
			return i;
	}
}

function isActiveRightTab(tabId){
	return document.getElementById(tabId).className.indexOf("deactive-right-tab") == -1;
}

function resizeHeader(){
	if(Ext.isIE){
		document.getElementById(taskListInnerRow).style.height = document.getElementById(taskListRow).offsetHeight + 'px'
	}else{
		if(typeof(document.getElementById(taskListInnerRow).childNodes[1]) == 'undefined') 
			document.getElementById(taskListInnerRow).style.height = document.getElementById(taskListRow).offsetHeight + 'px'	
		else
			document.getElementById(taskListInnerRow).childNodes[1].style.height = document.getElementById(taskListRow).offsetHeight + 'px'	
	}
}

// apply right tab paging in two pane view
function applyRightTabBlogPaging(spaceType){
	// for paging in blog tab
	var htmlString = '';
	var prevImageURL = '';
	var nextImageURL = '';
	if(spaceType != '' && spaceType == 'project'){
		nextImageURL = JSPRootURL+"/images/blog-next.gif";
		prevImageURL = JSPRootURL+"/images/blog-prev.gif";
	} else if(spaceType != '' && spaceType == 'personal'){
		prevImageURL = JSPRootURL+"/images/personal/dashboard_arrow-left.gif";
		nextImageURL = JSPRootURL+"/images/personal/dashboard_arrow-right.gif";
	}
	
	if(showNextLink && !showPrevLink && totalNoOfBlogEntry > 10){
		htmlString += '<table cellpadding="0" cellspacing="0" class="blog-tab-paging-table"><tr><td>';
		htmlString += '<span class="text-paging"> '+ msgFormat.format(entriesMessage, offsetForDisplay, rangeForDisplay, totalNoOfBlogEntry) + '</span>&nbsp; <span class="next"><a href="javascript:applyPaging(\'next\')"><img align="absmiddle" src="'+nextImageURL+'" title="'+ msgFormat.format(nextBlogPageTitle, nextEntriesCount) +'"/></a></span>';
		htmlString += '<span id="nextLoading_T"></span>';
		htmlString += '</td></tr></table>';
		
	} else if(showPrevLink && !showNextLink && totalNoOfBlogEntry > 10){
		htmlString += '<table cellpadding="0" cellspacing="0" class="blog-tab-paging-table"><tr><td>';
		htmlString += '<span id="prevLoading_T"></span>';
		htmlString += '<span class="text-paging"><a href="javascript:applyPaging(\'prev\')"><img align="absmiddle" src="'+prevImageURL+'" title="'+ msgFormat.format(prevBlogPageTitle, totalPosts) +'"/></a> &nbsp;'+ msgFormat.format(entriesMessage, offsetForDisplay, rangeForDisplay, totalNoOfBlogEntry) +'</span>';
		htmlString += '</td></tr></table>';
	}
	
	// for previous and next navigation of blog posts
	if(showNextLink && showPrevLink && totalNoOfBlogEntry > 10){
		htmlString += '<table cellpadding="0" cellspacing="0" class="blog-tab-paging-table"><tr><td>';
		htmlString += '<span id="prevLoading_T"></span>';
		htmlString += '<span class="text-paging"> &nbsp;<a href="javascript:applyPaging(\'prev\')">';
		htmlString += '<img align="absmiddle" src="'+prevImageURL+'" title="'+ msgFormat.format(prevBlogPageTitle, totalPosts) +'"/></a> &nbsp;'+ msgFormat.format(entriesMessage, offsetForDisplay, rangeForDisplay, totalNoOfBlogEntry) +'</span>';
		htmlString += '<span class="next"> &nbsp;<a href="javascript:applyPaging(\'next\')"><img src="'+nextImageURL+'" align="absmiddle" title="'+ msgFormat.format(nextBlogPageTitle, nextEntriesCount) +'"/></a></span>';
		htmlString += '<span id="nextLoading_T"></span>';
		htmlString += '</td></tr></table>';
	}
	
	// Write HTML for paging and make changes in header
	document.getElementById('blog-tab-paging').style.display = '';
	document.getElementById('blog-tab-paging').innerHTML = htmlString;
	document.getElementById('blogDivTop').style.border = 'none';
	
	resizePagingTab();
}

//Function for applying paging in blog tab
function applyPaging(filterType){
	var loadImg = '<img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	if (filterType == 'next') {
		offset += 10;
		document.getElementById('nextLoading_T').innerHTML += loadImg;
	} else if (filterType == 'prev') {
		offset -= 10;
		var prevPostText = document.getElementById('prevLoading_T').innerHTML;
		document.getElementById('prevLoading_T').innerHTML = loadImg + prevPostText;
	}
	showBlogAfterPaging();
}

//Function resize blog tab's height while paging according to the contents
function resizePagingTab(){
	var headerHeight = document.getElementById('blogDivTop').offsetHeight + document.getElementById('blog-tab-paging').offsetHeight; 
	document.getElementById('blogDivBody').style.height = (document.getElementById('taskBlogDivRight').style.height.replace('px', '') - headerHeight) + 'px';
}