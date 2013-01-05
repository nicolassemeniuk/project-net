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
//Automatically import the rest of the scripts that we need
function loadScript(scriptFile) {
    var head = document.getElementsByTagName("head").item(0);
    var script = document.createElement("script");
    script.src = scriptFile;
    script.type = "text/javascript";
    head.appendChild(script);
}
loadScript("../../src/gantt/task.js");
loadScript("../../src/gantt/path.js");
loadScript("../../src/gantt/utils.js");
loadScript("../../src/gantt/ganttviewvariables.js");
loadScript("../../src/gantt/graphics.js");

/*
 * This object draws a gantt chart.
 */
function GanttChart() {
    //Default view level is "day"
    this.viewLevel = 1;
    this.taskStartOffset = 0;
}

GanttChart.prototype.setTaskList = function(taskList) {
    this.taskList = taskList;
}

GanttChart.prototype.setLinkPaths = function(linkPaths) {
    this.linkPaths = linkPaths;
}

GanttChart.prototype.setViewLevel = function(viewLevel) {
    this.view = getViewForLevel(viewLevel);
}

GanttChart.prototype.getViewLevel = function() {
    return this.view.getViewLevelID();
}

GanttChart.prototype.setTaskStartOffset = function(taskStartOffset) {
    this.taskStartOffset = taskStartOffset;
}

GanttChart.prototype.setJSPRootURL = function(jspRootURL) {
    this.jspRootURL = jspRootURL;
}

GanttChart.prototype.setToday = function(today) {
    this.today = today;
}

GanttChart.prototype.setStartPosition = function(sPosition) {
    this.sPosition = sPosition;
}

GanttChart.prototype.setEndPosition = function(ePosition) {
    this.ePosition = ePosition;
}

GanttChart.prototype.scrollIfNecessary = function() {
    //Figure out how far we are currently scrolled to the left
    var viewableWidth = document.body.clientWidth;

    if (viewableWidth < 200 || viewableWidth == undefined) {
        return;
    }

    if (viewableWidth < this.earliestLeft) {
        //Make some room so the task doesn't appear truncated.
        var scrollToX = this.earliestLeft - 40;

        //Scroll to the position of the first task
        scroll(this.earliestLeft, 0);
    }
}

GanttChart.prototype.showProgressLines = function(showProgressLines) {
    this.showProgressLines = showProgressLines;
}

GanttChart.prototype.drawResourceName = function(currentTask, left, top, number) {
    var resourceNameSpan = document.createElement("span");
    resourceNameSpan.id = "resource" + number;
    resourceNameSpan.className = "resourceName";
    resourceNameSpan.style.top = (top-2) + "px";
    resourceNameSpan.style.left = left + "px";

    resourceNameSpan.appendChild(document.createTextNode(currentTask.getResources()));
    document.body.appendChild(resourceNameSpan);
}

GanttChart.prototype.drawMilestoneDate = function(currentTask, left, top, number) {
    var milestoneDateSpan = document.createElement("span");
    milestoneDateSpan.id = "milestoneDate" + number;
    milestoneDateSpan.className = "milestoneDate";
    milestoneDateSpan.style.top = (top-2) + "px";
    milestoneDateSpan.style.left = left + "px";

    milestoneDateSpan.appendChild(document.createTextNode(currentTask.getFinishDate()));
    document.body.appendChild(milestoneDateSpan);
}

GanttChart.prototype.drawTasks = function(taskList) {
    if (!taskList) {
        alert("Tasks must be set prior to displaying the gantt.");
        return;
    }

    var todayPX = this.today * this.view.getPixelsPerDay();
    var currentTaskTop = 39;
    var graphics = new Graphics();
    graphics.penColor = "red";
    var lastX = todayPX;
    var lastY = 33;
    this.earliestLeft = (taskList.length == 0 ? 0 : 1000000);

    for (var i = 0; i < taskList.length; i++) {
        var currentTask = taskList[i];
		if(currentTask != null){
        //Make the style string which will define how it looks
        var left = (currentTask.getStartBlock()+this.taskStartOffset) * this.view.getPixelsPerDay();
        var width = currentTask.getDurationBlocks() * this.view.getPixelsPerDay();

        if (this.earliestLeft > left) {
            this.earliestLeft = left;
        }

        //Construct the task object
        if (currentTask.isSummaryTask()) {
            var summaryTask = document.createElement("table");
            var stTBody = document.createElement("tbody");
            var stTR = document.createElement("tr");
            var stOpenTD = document.createElement("td");
            var stCloseTD = document.createElement("td");
            var startDiamond = document.createElement("img");
            var closeDiamond = document.createElement("img");

            summaryTask.className = "summaryTask";
            summaryTask.id = "task"+(i+1);
            summaryTask.style.width = width+"px";
            summaryTask.style.top = currentTaskTop+"px";
            summaryTask.style.left = left+"px";
            summaryTask.appendChild(stTBody);
            stTBody.appendChild(stTR);

            stOpenTD.className = "summaryTaskLeftEnd";
            startDiamond.src = "../../images/schedule/ganttSummaryEnd.gif";
            stOpenTD.appendChild(startDiamond);
            stTR.appendChild(stOpenTD);

            stCloseTD.className = "summaryTaskRightEnd";
            closeDiamond.src = "../../images/schedule/ganttSummaryEnd.gif";
            stCloseTD.appendChild(closeDiamond);
            stTR.appendChild(stCloseTD);

            //Add the summary Task to body of the document
            document.body.appendChild(summaryTask);

            //Put the name of the resource next to the task
            this.drawResourceName(currentTask, left+width+35, currentTaskTop, i);
        } else if (currentTask.isMilestone()) {
            //Because milestones often have zero duration, their width might
            //show up as zero.  Fix that
            width = 11;
            var milestone = document.createElement("div");
            milestone.className = "milestone";
            milestone.id = "task" + (i+1);
            milestone.style.width = width+"px";
            milestone.style.top = currentTaskTop+"px";
            milestone.style.left = left + "px";

            //The spacer is required -- otherwise the gantt
            //doesn't show up in Netscape
            var spacer = document.createElement("img");
            spacer.src= "../../images/spacers/trans.gif";
            spacer.style.width = width + "px";
            spacer.style.height = "11px";
            spacer.border="0";
            milestone.appendChild(spacer);

            document.body.appendChild(milestone);

            //Put the end date of the milestone next to the milestone
            this.drawMilestoneDate(currentTask, left+width+5, currentTaskTop, i);
        } else {
            var htmlTask = document.createElement("span");
            htmlTask.className = "task";
            htmlTask.id = "task"+(i+1);
            htmlTask.style.width = width+"px";
            htmlTask.style.top = currentTaskTop+"px";
            htmlTask.style.left = left+"px";

            //Now figure out how wide the percent complete inside the task should be
            var percentWidth = width * (currentTask.getPercentComplete() / 100);
            var htmlTaskPercentComplete = document.createElement("img");
            htmlTaskPercentComplete.src="../../images/spacers/trans.gif";
            htmlTaskPercentComplete.className="taskPercent";
            htmlTaskPercentComplete.style.width = percentWidth+"px";
            htmlTaskPercentComplete.border="0";

            //Now assemble all of the created objects and add them to the html
            htmlTask.appendChild(htmlTaskPercentComplete);
            document.body.appendChild(htmlTask);

            //Put the name of the resource next to the task
            this.drawResourceName(currentTask, left+width+10, currentTaskTop, i);
        }

        if (this.showProgressLines && (taskList.length < 100)) {
            if (currentTask.getPercentComplete() < 100) {
                var completionFactor;
                if (currentTask.isMilestone()) {
                    completionFactor = 0.50;
                } else {
                    completionFactor = (currentTask.getPercentComplete()/100);
                }

                //Draw the progress lines
                var progressLocation = document.createElement("img");
                progressLocation.src = "../../images/schedule/progressLocation.gif";
                progressLocation.className = "progressLocation";
                progressLocation.style.height = "11px";
                progressLocation.style.width = "11px";
                progressLocation.style.top = currentTaskTop + "px";
                progressLocation.style.left = left + (width*completionFactor) - 5 + "px";
                document.body.appendChild(progressLocation);

                //Draw a line from the previous location back to the current day
                graphics.drawLine(lastX, lastY, todayPX, currentTaskTop-5);

                //Draw a second line from the current day to the current task
                graphics.drawLine(todayPX, currentTaskTop-5, (left + (width*completionFactor)), currentTaskTop+5);
                lastX = (left + (width*completionFactor));
                lastY = currentTaskTop+5;
            } else {
                //Draw a line from the previous location back to the current day
                graphics.drawLine(lastX, lastY, todayPX, currentTaskTop-5);

                graphics.drawLine(todayPX, currentTaskTop-5, todayPX, currentTaskTop+5);
                lastX = todayPX;
                lastY = currentTaskTop+5;
            }
        }

        //Prepare for the next task
        currentTaskTop += 22;
    }
    }
}

GanttChart.prototype.drawLinkPaths = function() {
	//get end position of drawing link path.
    var _ePosition = this.linkPaths.length > this.ePosition ? this.ePosition : this.linkPaths.length;
    if(_ePosition > this.sPosition){
	    for (var i = this.sPosition; i < _ePosition; i++) {
		    var pathGroup = this.linkPaths[i];
        for (var j = 0; j < pathGroup.length; j++) {
            var currentItem = pathGroup[j];
            currentItem.draw(this.view.getPixelsPerDay(), 11, this.taskStartOffset, document);
        }
    }
}
}

GanttChart.prototype.display = function() {
    //First, draw all of the task as span objects
    this.drawTasks(this.taskList);

    var topHeaders = this.view.getTopHeaders();
    var topHeaderWidths = this.view.getTopHeaderWidths();
    var bottomHeaders = this.view.getBottomHeaders();

    //Figure out how wide the top header will need to be
    var ganttChartWidth = 0;
    for (var i = 0; i < topHeaderWidths.length; i++) {
        ganttChartWidth += topHeaderWidths[i];
    }

    //Now draw the table that the tasks are in
    var ganttTable = document.createElement("table");
//    ganttTable.style.height = "100%";
    ganttTable.style.width = ganttChartWidth+"px";
    ganttTable.className = "textTable";
    var ganttTableTBody = document.createElement("tbody");
    ganttTable.appendChild(ganttTableTBody);

    //
    //Draw the top headers
    //
    var topHeaderTable = document.createElement("table");
    topHeaderTable.style.width = "100%";
    topHeaderTable.className = "textTable";
    var headerTBody = document.createElement("tbody");
    topHeaderTable.appendChild(headerTBody);
    var weekTR = document.createElement("tr");

    for (var i = 0; i < topHeaders.length; i++) {
        var weekTD = document.createElement("td");

        weekTD.className = "dateHeader";
        weekTD.style.width= topHeaderWidths[i];
        weekTD.appendChild(document.createTextNode(topHeaders[i]));
        weekTR.appendChild(weekTD);
    }
    headerTBody.appendChild(weekTR);

    //Put the header table inside the gantt table
    var headerTR = document.createElement("tr");
    var headerTD = document.createElement("td");
    headerTD.style.margin = "0";
    headerTD.style.padding = "0";
    headerTD.style.verticalAlign = "top";
    headerTD.style.height = "17px";

    headerTD.appendChild(topHeaderTable);
    headerTR.appendChild(headerTD);
    ganttTableTBody.appendChild(headerTR);

    //Draw the bottom headers
    //This might not be as easy as it seems.  For some view types, the bottom
    //header might have more columns than the colspan.  In this case we want to
    //use up all the bottom headers until we have exhausted the list, then
    //repeat.  This allows for:
    //
    //  Q1 '03      Q2 '03      Q3 '03      Q4 '03
    //  Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec
    //
    var bottomHeaderTable = document.createElement("table");
    //bottomHeaderTable.style.width = "100%";
    bottomHeaderTable.className = "textTable";
    var bottomTBody = document.createElement("tbody");
    bottomHeaderTable.appendChild(bottomTBody);
    var dayOfWeekTR = document.createElement("tr");

    var totalHeadersToDraw;
    if (this.view.getRepeatBottomHeader() == -1) {
        totalHeadersToDraw = bottomHeaders.length;
    } else {
        totalHeadersToDraw = topHeaders.length * this.view.getRepeatBottomHeader();
    }

    var currentBottomHeaderNumber = 0;
    for (var numberCreated = 0; numberCreated < totalHeadersToDraw; numberCreated++) {
        if (currentBottomHeaderNumber == bottomHeaders.length) {
            currentBottomHeaderNumber = 0;
        }

        var dayTD = document.createElement("td");
        dayTD.className = "dow";
        dayTD.style.width = this.view.getBottomHeaderWidth();
        dayTD.appendChild(document.createTextNode(bottomHeaders[currentBottomHeaderNumber]));
        dayOfWeekTR.appendChild(dayTD);
        currentBottomHeaderNumber++;
    }

    bottomTBody.appendChild(dayOfWeekTR);

    //Put the bottom header table inside the gantt table
    headerTR = document.createElement("tr");
    headerTD = document.createElement("td");
    headerTD.style.margin = "0";
    headerTD.style.padding = "0";
    headerTD.style.verticalAlign = "top";
    headerTD.style.height = "17px";

    headerTD.appendChild(bottomHeaderTable);
    headerTR.appendChild(headerTD);
    ganttTableTBody.appendChild(headerTR);

    //Create the big body where the tasks will be drawn
    var tasksBodyTR = document.createElement("tr");
    var tasksBodyTD = document.createElement("td");

    tasksBodyTD.className = "ganttbody";
    tasksBodyTD.style.backgroundImage = this.view.getBodyImage(this.jspRootURL);
    tasksBodyTD.style.backgroundRepeat = "repeat";

    //Figure out how high the body of the gantt should be
    var taskBodyHeight = (this.taskList.length * 22);
    /*if (taskBodyHeight < 1000) {
        taskBodyHeight = 1000;
    }*/
    tasksBodyTD.style.height = taskBodyHeight + "px";

    tasksBodyTR.appendChild(tasksBodyTD);
    ganttTableTBody.appendChild(tasksBodyTR);
    document.body.appendChild(ganttTable);
}

