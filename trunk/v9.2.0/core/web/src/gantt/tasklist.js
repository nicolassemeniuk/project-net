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
//Yes, current selection is a string.  It is easier than a list in JavaScript
var currentSelection = " ";
var detect = navigator.userAgent.toLowerCase();
var OS,browser,version,total,thestring;

function isSelected(id) {
    return (currentSelection.indexOf(" " + id + " ") > -1);
}

function select(id, rowElement) {
    rowElement.className = rowElement.className + " textSelected";
    currentSelection += " "+id+" ";
}

function unselect(id, rowElement) {
    rowElement.className = rowElement.className.replace(" textSelected", "");
    currentSelection = currentSelection.replace(" "+id, "");
}

function selectMouseClick(e) {
    //Decipher the id
    var id = this.id.substring(4);

    //Get the row that the user clicked on
    var rowElement = document.getElementById("row"+id);
    if (!isSelected(id)) {
        select(id, rowElement);
    } else {
        unselect(id, rowElement);
    }
}

// this function stands for browser detection together with function checkIt
function browserDetect(){

	
	if (checkIt('konqueror'))
	{
		browser = "Konqueror";
		OS = "Linux";
	}
	else if (checkIt('safari')) browser = "Safari"
	else if (checkIt('omniweb')) browser = "OmniWeb"
	else if (checkIt('opera')) browser = "Opera"
	else if (checkIt('webtv')) browser = "WebTV";
	else if (checkIt('icab')) browser = "iCab"
	else if (checkIt('msie')) browser = "Internet Explorer"
	else if (checkIt('firefox')) browser = "Firefox"
	else if (!checkIt('compatible'))
	{
		browser = "Netscape Navigator"
		version = detect.charAt(8);
	}
	else browser = "An unknown browser";
	return browser;
}


function checkIt(string)
{
	place = detect.indexOf(string) + 1;
	thestring = string;
	return place;
}


// this function create hidden parameters colapse, and expand, tohse two parametrs are hidden parameters on form in
// TaskFrame.jsp. Those parameters shows which node is colapsed and which is expanded. Those hidden parameters in
// form are comma separated
function concatenation(colapsedNode, expandedNode, addNumber, expanded){
	
	var result = new Array();
	
	if (expanded){
		var indColapsed = colapsedNode.indexOf(addNumber);
		if (indColapsed != -1){
			colapsedNode = colapsedNode.substring(0, indColapsed-1) + colapsedNode.substring(indColapsed+addNumber.length, colapsedNode.length);
		}	
		var indExpanded = expandedNode.indexOf(addNumber);
		if (indExpanded == -1){
				expandedNode+=","+addNumber;
		}
		
	} else {
		var indExpanded = expandedNode.indexOf(addNumber);
		if (indExpanded != -1){
			expandedNode = expandedNode.substring(0, indExpanded-1) + expandedNode.substring(indExpanded+addNumber.length, expandedNode.length);
		}
		var indColapsed = colapsedNode.indexOf(addNumber);
		if (indColapsed == -1){
				colapsedNode+=","+addNumber;
		}
		
	}
	result[0]=colapsedNode;
	result[1]=expandedNode;
	return result;
}

function expanderClick(e) {
    var expanded = this.getAttribute("expanded");
    window.status = expanded;   
    var colapsedNode = window.document.forms['t'].colapse.value;
    var expandedNode = window.document.forms['t'].expand.value; 
    if (colapsedNode == null){
    	colapsedNode="";
    }
    if (expandedNode == null){
    	expandedNode="";
    }
    
    if (expanded == "true") {
        this.src = "../../images/expand.gif";
        this.setAttribute("expanded", "false");
        var res = new Array();
        res = concatenation(colapsedNode, expandedNode, this.getAttribute("taskId"), false);
        window.document.forms['t'].colapse.value=res[0];
        window.document.forms['t'].expand.value=res[1];        
    } else {
        this.src = "../../images/unexpand.gif";
        this.setAttribute("expanded", "true");          
        var res = new Array();
        res = concatenation(colapsedNode, expandedNode, this.getAttribute("taskId"), true);  
        window.document.forms['t'].colapse.value=res[0];
        window.document.forms['t'].expand.value=res[1];
    }    
    var form = window.document.forms['t'];   
    form.submit();  
}

function TaskListTable() {
}

TaskListTable.prototype.setTaskList = function(taskList) {
    this.taskList = taskList;
}

TaskListTable.prototype.setColumns = function(columns) {
    this.columns = columns;
}

TaskListTable.prototype.display = function() {
    var taskListTable = document.createElement("table");
    taskListTable.className = "textTable";
    var taskListTBody = document.createElement("tbody");

    taskListTable.appendChild(taskListTBody);

    //Prepare the global array of columns
    tableColumns = this.columns;

    //Build the column headers
    var tableHeaderTR = document.createElement("tr");
    tableHeaderTR.style.height = "34px";
    for (var i = 0; i < this.columns.length; i++) {
        var headerTD = document.createElement("td");
        headerTD.id = i;
        headerTD.onclick = mouseClickDispatcher;
        headerTD.onmousemove = mouseMoveDispatcher;
        headerTD.onmouseup = mouseUpDispatcher;
        headerTD.onmousedown = mouseDownDispatcher;

        headerTD.className = "textHeader";
        if (this.columns[i].hasWidth()) {
            headerTD.style.width = this.columns[i].getWidth();
        }
        if (this.columns[i].hasTextAlign()) {
            headerTD.style.textAlign = this.columns[i].getTextAlign();
        }
        headerTD.noWrap = this.columns[i].getNoWrap();

        //Add an image, if one is set
        if (this.columns[i].hasImage) {
            var image = document.createElement("img");
            image.src = this.columns[i].getImageName();
            image.width = this.columns[i].getImageWidth();
            image.height = this.columns[i].getImageHeight();

            headerTD.appendChild(image);
        }

        //Add text, if there is text to add
        if (this.columns[i].hasColumnName()) {
            headerTD.appendChild(document.createTextNode(this.columns[i].getColumnName()));
        }

        this.columns[i].setTD(headerTD);
        tableHeaderTR.appendChild(headerTD);
    }
    taskListTBody.appendChild(tableHeaderTR);

    //Add table rows
    
    for (var j = 0; j < this.taskList.length; j++) {
        var currentTask = this.taskList[j];
        	if(currentTask != null){
		        var textClass = "text";
		        if (currentTask.isSummaryTask()) {
		            textClass = "boldText";
		        }
		
		        var taskTR = document.createElement("tr");
		        taskTR.className = textClass;
		        taskTR.id = "row" + currentTask.getID();
		
		        var taskNumberTD = document.createElement("td");
		        taskNumberTD.className = "rowHeader";
		        taskNumberTD.id = "col0"+currentTask.getID();
		        taskNumberTD.onclick = selectMouseClick;
		        taskNumberTD.appendChild(document.createTextNode(j+1));
		        taskTR.appendChild(taskNumberTD);
		
		        var taskInfoTD = document.createElement("td");
		        taskInfoTD.className = textClass;
		        taskTR.appendChild(taskInfoTD);
		
		        var taskNameTD = document.createElement("td");
	        	taskNameTD.className = textClass;	
	
		        //Put a spacer in front of the task name
		        
		        if ("Firefox" == browserDetect()){
			        var indented = document.createElement("img");
			        indented.src="../../images/1px.gif";
			        indented.setAttribute("height", "1px");
			        var widt = (2 + ((currentTask.getHierarchyLevel() - 1) * 20)) + "px";	 
			        indented.setAttribute("width", widt);
			        taskNameTD.appendChild(indented);
		        } else {
			        var spacer = document.createElement("span");
			        spacer.style.width = (2 + ((currentTask.getHierarchyLevel() - 1) * 20)) + "px";	        
			        taskNameTD.appendChild(spacer);
				}
		        //If the task is a summary task, add the plus sign that can be used
		        //to expand or unexpand the subtasks of this task.
		        if (currentTask.isSummaryTask()) {
		            var expandImage = document.createElement("img");	            
		            expandImage.className = "summaryTaskExpander";
		            expandImage.onclick = expanderClick;	    
		            if (currentTask.getExpand()=="true"){    
		            	expandImage.src = "../../images/expand.gif";
		            	expandImage.setAttribute("expanded", "false");
		            } else {
		            	expandImage.src = "../../images/unexpand.gif";
		            	expandImage.setAttribute("expanded", "true");
		            }            
		            expandImage.setAttribute("taskId", currentTask.getID());	            
		            taskNameTD.appendChild(expandImage);
		        }
		
		        taskNameTD.appendChild(document.createTextNode(currentTask.getTaskName()));
		        taskTR.appendChild(taskNameTD);
		
		        var durationTD = document.createElement("td");
		        durationTD.className = textClass;
		        durationTD.appendChild(document.createTextNode(currentTask.getDuration()));
		        taskTR.appendChild(durationTD);
		
		        var startTD = document.createElement("td");
		        startTD.className = textClass;
		        startTD.appendChild(document.createTextNode(currentTask.getStartDate()));
		        taskTR.appendChild(startTD);
		
		        var finishTD = document.createElement("td");
		        finishTD.className = textClass;
		        finishTD.appendChild(document.createTextNode(currentTask.getFinishDate()));
		        taskTR.appendChild(finishTD);
		
		        var predecessorTD = document.createElement("td");
		        predecessorTD.className = textClass;
		        predecessorTD.appendChild(document.createTextNode(currentTask.getPredecessors()));
		        taskTR.appendChild(predecessorTD);
		
		        var resourcesTD = document.createElement("td");
		        resourcesTD.className = textClass;
		        resourcesTD.appendChild(document.createTextNode(currentTask.getResources()));
		        taskTR.appendChild(resourcesTD);
		
		        taskListTBody.appendChild(taskTR);
	    }
    }
    window.document.body.appendChild(taskListTable);
}