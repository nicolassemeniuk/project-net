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
//This is a nasty workaround for firefox.  Its DOM is completely screwed up.
//It has no idea of what a sibling is, and parents are a joke too.
function getNextSibling(row) {
    var nextSibling = row.nextSibling;
    while (nextSibling && (nextSibling.getAttribute == undefined || !(nextSibling.getAttribute("level") || nextSibling.getAttribute("rowSep")))) {
        nextSibling = nextSibling.nextSibling;
    }

    return nextSibling;
}

function collapse(row) {
    var startLevel = row.getAttribute("level");
    var childRow = getNextSibling(row);
    var rowSepLevel = childRow.getAttribute("rowSep");
    while (childRow && ((parseInt(childRow.getAttribute("level")) > startLevel) || (rowSepLevel))) {
        if (!rowSepLevel || parseInt(rowSepLevel) > startLevel) {
            hide(childRow);
        }
        childRow = getNextSibling(childRow);
        if (childRow) {
            rowSepLevel = childRow.getAttribute("rowSep");
        }
    }
}

//Expand a previously collapsed row
function expand(row) {
    var startLevel = row.getAttribute("level");
    var dontExpandUntilLevel = -1;

    var childRow = getNextSibling(row);
    var childRowLevel = (childRow ? parseInt(childRow.getAttribute("level")) : 0);
    while (childRow && ((childRowLevel > startLevel) || (childRow.getAttribute("rowSep")))) {
        //In a situation where we are expanding, but a summary task was previously
        //collapsed, don't show that inner summary task's children.
        if (dontExpandUntilLevel == -1 || dontExpandUntilLevel >= childRowLevel) {
            show(childRow);

            //This means we've gone back up to the top level, stop hiding children.
            if (dontExpandUntilLevel != -1) {
                dontExpandUntilLevel = -1;
            }
        }

        //If a summary task is being shown, but its children are not being shown,
        //set a variable that makes sure the children will be skipped.
        var kidsShown = childRow.getAttribute("kidsShown");
        if (dontExpandUntilLevel == -1 && kidsShown && kidsShown=="false") {
            dontExpandUntilLevel = childRowLevel;
        }

        //Deal with the special case of row separators.  We want to show the rowSep
        //of the summary task which has hidden children.
        var rowSepLevel = childRow.getAttribute("rowSep");
        if (rowSepLevel && dontExpandUntilLevel == rowSepLevel) {
            show(childRow);
        }

        childRow = getNextSibling(childRow);
        childRowLevel = (childRow ? parseInt(childRow.getAttribute("level")) : 0);
    }
}

//Show all rows, whether they are collapsed or not.
function showAll(id, notifyMethod) {
    var row = document.getElementById(id);
    show(row);

    var childRow = row;
    while (childRow) {    	
        show(childRow);

        //Check to see if this is a summary row.  If it is, change the contents
        //of the row so it knows it is expanded.
        showSummaryRow(childRow, notifyMethod);

        childRow = getNextSibling(childRow);
    }
}

function hideAll(id, notifyMethod) {
    var row = document.getElementById(id);

    var childRow = row;
    while (childRow) {
    
        //First, look for normal rows
        var childRowLevel = childRow.getAttribute("level");
        if (childRowLevel && (parseInt(childRowLevel) > 1)) {
            hide(childRow);
        }

        //Now look for separator rows
        var rowSepLevel = childRow.getAttribute("rowSep");
        if (rowSepLevel && (parseInt(rowSepLevel) > 1)) {
            hide(childRow);
        }

        //Now see if it is a summary row, so we can deal with the [+] and attributes
        hideSummaryRow(childRow, notifyMethod);
        childRow = getNextSibling(childRow);
    }

}

function hide(row) {
    row.className += " hidden";
}

function show(row) {
	var re = new RegExp("hidden", "g");
    row.className = row.className.replace(re, "");
}

function hideSummaryRow(row, notifyMethod) {
    var id = row.getAttribute("id");
    if (id) {
        var toggler = document.getElementById("toggler" + id);
        if (toggler) {       
            toggler.src = "../e.gif";
        }
        row.setAttribute("kidsShown", "false");

        if (notifyMethod != undefined) {
            notifyMethod("node"+id.split('_')[0]+"expanded", "false");
        }
    }
}

function showSummaryRow(row, notifyMethod) {
    var id = row.getAttribute("id");
    if (id) {
        var toggler = document.getElementById("toggler" + id);
        if (toggler) {
            toggler.src = "../u.gif";
        }
        row.setAttribute("kidsShown", "true");

        if (notifyMethod != undefined) {
            notifyMethod("node"+id.split('_')[0]+"expanded", "true");
        }
    }
}

function toggle(id) {
    var row = document.getElementById(id);
    var image = document.getElementById("toggler" + id);
    var kidsShown = row.getAttribute("kidsShown");
    if (kidsShown == null)
        kidsShown = "true";

    if (kidsShown == "true") {
        collapse(row);
        row.setAttribute("kidsShown", "false");
        image.src = "../e.gif";

    } else {
        expand(row);
        row.setAttribute("kidsShown", "true");
        image.src = "../u.gif";
    }
}

//To get Childrens on node
function getChildRowId(id){
	var row = document.getElementById(id);
	if(!row) return; 
	var startLevel = row.getAttribute("level");
	var dontExpandUntilLevel = -1;
	var childRow = getNextRow(row);
	var childRowLevel = (childRow ? parseInt(childRow.getAttribute("level")) : 0);
	while (childRow && ((childRowLevel > startLevel) || (childRow.getAttribute("rowSep")))) {
	         childRow = getNextRow(childRow);
	     childRowLevel = (childRow ? parseInt(childRow.getAttribute("level")) : 0);
	}
}

// To get Next Row
function getNextRow(row) {
   var nextSibling = row.nextSibling;
   if(typeof childTaskList != 'undefined') { 
	   childTaskList += row.id.split('_')[0] + ",";
   }
   while (nextSibling && (nextSibling.getAttribute == undefined || !(nextSibling.getAttribute("level") || nextSibling.getAttribute("rowSep")))) {
  	 nextSibling = nextSibling.nextSibling;
   }
   return nextSibling;
}