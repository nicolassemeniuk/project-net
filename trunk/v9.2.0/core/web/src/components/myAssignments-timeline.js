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

var url = JSPRootURL+'/src/timeline/api/timeline-api.js?'+versionNumber;
document.write("<script src='" + url + "' type='text/javascript'></script>");

var timeLine;
var timeLineLoaded = false;
function initializeTimeLine(){
	var eventSource = new Timeline.DefaultEventSource(0);
	var theme = Timeline.ClassicTheme.create();
         theme.event.bubble.width = 320;
         theme.event.bubble.height = 220;
	
	
	var bandInfos = [
	  Timeline.createBandInfo({
	  	//trackHeight:    1.0,
		trackGap:       0.15,			
		eventSource:    eventSource,
		width:          "70%", 
		intervalUnit:   Timeline.DateTime.WEEK,
		intervalPixels: 100,
		theme:          theme
	}),
	Timeline.createBandInfo({
		showEventText:  false,
		trackHeight:    0.4,
		trackGap:       0.15,
		eventSource:    eventSource,
		width:          "30%", 
		intervalUnit:   Timeline.DateTime.MONTH, 
		intervalPixels: 200,
		theme:          theme
	})
	];	
	
	bandInfos[1].syncWith = 0;
 	bandInfos[1].highlight = true;
	
	timeLine = Timeline.create(document.getElementById("my-timeline"), bandInfos, Timeline.HORIZONTAL);
	Timeline.loadJSON(JSPRootURL+"/assignments/My/getTimeLineData?module="+moduleId+"&rendom="+ new Date().getTime(), function(data, url){ eventSource.loadJSON(data, url); });
	
	Timeline.DurationEventPainter.prototype._onClickDurationEvent = function(domEvt,evt,target){
		assignmentTreeNodeId = childTaskList = evt.getProperty("objectId");
		taskSpaceId = evt.getProperty("spaceId");
		objectType = evt.getProperty("objectType");
		objectName = evt.getProperty("title");
		workSpaceName = evt.getProperty("workSpace");
		loadBlogEntriesForAssignment();
	
		/* Load wiki after activate wiki tab. */
		if(rightPanel.getActiveTab().id == 'wiki'){
			loadWikiForAssignment(objectName, taskSpaceId);
		} else{
			rightPanel.setActiveTab(1);
			loadWikiForAssignment(objectName, taskSpaceId);
			rightPanel.setActiveTab(0);
		}
		
		domEvt.cancelBubble=true;
		if("pageX"in domEvt){
			 var x=domEvt.pageX;
			 var y=domEvt.pageY;
		}else{
			var c=Timeline.DOM.getPageCoordinates(target);
			var x=domEvt.offsetX+c.left;
			var y=domEvt.offsetY+c.top;
		}
		this._showBubble(x,y,evt);
	};
}

var resizeTimerID = null;
function onResize() {
	if (resizeTimerID == null) {
	    resizeTimerID = window.setTimeout(function() {
	        resizeTimerID = null;
	        timeLine.layout();
	    }, 500);
	}
}

function showTimeLineView(){
	timeLineLoaded = false;
	document.getElementById('my-timeline').style.visibility = 'visible';
	initializeTimeLine();
	timeLineLoaded = true;
}

//document.body.onresizeend = onResize();