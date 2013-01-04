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
//------------------------------------------------------
// Global Variables and functions
//------------------------------------------------------
var ie = (document.all);
var n6 = (window.sidebar);
var isResizing = false;
var columnResizing;
var tableColumns;

function mouseClickDispatcher(e) {
    tableColumns[this.id].mouseClick(e);
}

function mouseMoveDispatcher(e) {
    tableColumns[this.id].mouseMove(e);
}

function mouseDownDispatcher(e) {
    tableColumns[this.id].mouseDown(e);
}

function mouseUpDispatcher(e) {
    tableColumns[this.id].mouseUp(e);
}

//------------------------------------------------------
//Column class definition
//------------------------------------------------------
function Column(columnName, width, noWrap, textAlign) {
    this.columnName = columnName;
    this.width = width;
    this.noWrap = noWrap;
    this.textAlign = textAlign;
}

Column.prototype.getColumnName = function() {
    return this.columnName;
}

Column.prototype.hasColumnName = function() {
    return (this.columnName != undefined);
}

Column.prototype.getWidth = function() {
    return this.width;
}

Column.prototype.hasWidth = function() {
    return (this.width != undefined);
}

Column.prototype.getTextAlign = function() {
    return this.textAlign;
}

Column.prototype.hasTextAlign = function() {
    return (this.textAlign != undefined);
}

Column.prototype.getNoWrap = function() {
    if (this.noWrap == undefined) {
        return false;
    } else {
        return this.noWrap;
    }
}

Column.prototype.setImage = function(imageName, height, width) {
    this.imageName = imageName;
    this.imageHeight = height;
    this.imageWidth = width;
}

Column.prototype.hasImage = function() {
    return (this.imageName != undefined);
}

Column.prototype.getImageName = function() {
    return this.imageName;
}

Column.prototype.getImageHeight = function() {
    return this.imageHeight;
}

Column.prototype.getImageWidth = function() {
    return this.imageWidth;
}

Column.prototype.setTD = function(td) {
    this.td = td;
}

Column.prototype.getTD = function() {
    return this.td;
}

Column.prototype.inResizeZone = function(x) {
    return false; //(this.td.offsetLeft+this.td.offsetWidth-3 < x);
}

Column.prototype.getX = function(e) {
    var xPos;
    if (e) {
       xPos = e.pageX;
    } else {
       xPos = event.x;
    }

    return xPos;
}

Column.prototype.mouseMove = function(e) {
    var xPos = this.getX(e);
    //window.status = "X:"+event.x+" Y:"+event.y;
    if (isResizing) {
        this.td.style.cursor = 'col-resize';

        //Dispatch to the correct mouse move handler,
        //if necessary
        if (columnResizing != this) {
            columnResizing.mouseMove(e);
            return;
        }

        var sizeDiff = xPos - this.resizeStartX;
        this.td.style.width = this.td.offsetWidth + sizeDiff;
        this.resizeStartX = xPos;
    } else {
        if (this.inResizeZone(xPos)) {
            if (ie) {
              this.td.style.cursor = 'col-resize';
            } else {
              this.td.style.cursor = 'w-resize';
            }
        } else {
            this.td.style.cursor = 'auto';
        }
    }
}

Column.prototype.mouseClick = function(e) {
    //window.status = "Mouse click on " + this.columnName + " column";
}

Column.prototype.mouseDown = function(e) {
    var xPos = this.getX(e);

    if (this.inResizeZone(xPos)) {
        this.resizeStartX = xPos;
        isResizing = true;
        columnResizing = this;
    }
}

Column.prototype.mouseUp = function(e) {
    if (isResizing) {
        isResizing = false;
        columnResizing = undefined;
    }
}

