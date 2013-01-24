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
// A couple of notes about notation.  We still use the term "block" so we can account for different
// zoom levels.  Blocks start at "zero".
//
// Rows aren't as straight forward.  Here is the structure of a gantt chart from a
// drawing perspective:
//
// 5px gutter   -->
// 10 px task   -->    XXXXXXXXXXXXXXXXXXX_
//              -->    XXXXXXXXXXXXXXXXXXX |
// 10 px gutter -->               _________|
//              -->              |
// 10 px task   -->              |_\ XXXXXXXXXXXXXXXXXX
//              -->                / XXXXXXXXXXXXXXXXXX
//
// The first 5px gutter is skipped.  (Just don't think about it.)  The first task is in row 0,
// the horizontal link line is in row 1.  The second task is in row 2.
//
// This means that tasks are only in even numbered rows.  Odd numbered rows are reserved for the
// times that a dependency line needs to make a u-turn.  Row numbering is also zero based.
//


//------------------------------------------------------------------------------
// Global method
//------------------------------------------------------------------------------
function getHeightForDirection(direction, blocks, pixelsPerRow) {
    if (direction == 'E' || direction == 'W') {
        return 1;
    } else if (direction == 'N') {
        return Math.floor(blocks*pixelsPerRow);
    } else if (direction == 'S') {
        	return Math.ceil(blocks*pixelsPerRow);
    }
}

function getWidthForDirection(direction, blocks, pixelsPerBlock) {
    if (direction == 'N' || direction == 'S') {
        return 1;
    } else if (direction == 'E') {
        return Math.ceil(blocks*pixelsPerBlock);
    } else {
        return Math.floor(blocks*pixelsPerBlock);
    }
}

function getTopOffsetForDirection(direction, pixelsPerRow) {
    if (direction != 'N') {
        return Math.floor(pixelsPerRow*0.5);
    } else {
        return 0;
    }
}

function getLeftOffsetForDirection(direction, pixelsPerDay) {
    if (direction == 'W') {
        return 0;
    } else {
        return Math.floor(pixelsPerDay*0.5);
    }
}




//------------------------------------------------------------------------------
// Line Object
//------------------------------------------------------------------------------
function Line(startRow, startBlock, direction, distanceBlocks) {
    this.startRow = startRow;
    this.startBlock = startBlock;
    this.direction = direction;
    this.distanceBlocks = distanceBlocks;
}

Line.prototype.draw = function(pixelsPerDay, pixelsPerRow, taskStartOffset, document) {
    var lineImg = document.createElement("img");
    lineImg.src = "../../images/schedule/linkline.gif";
    lineImg.className = "linkLine";
    lineImg.style.height = getHeightForDirection(this.direction, this.distanceBlocks, pixelsPerRow)+"px";
    lineImg.style.width = getWidthForDirection(this.direction, this.distanceBlocks, pixelsPerDay)+"px";
    lineImg.style.top = (39+(this.startRow*pixelsPerRow)+
        getTopOffsetForDirection(this.direction, pixelsPerRow)) + "px";
    lineImg.style.left = ((this.startBlock*pixelsPerDay)+(taskStartOffset*pixelsPerDay)+
        getLeftOffsetForDirection(this.direction, pixelsPerDay))+ "px";
    lineImg.id = "l_img_"+lineImg.style.height+"_"+lineImg.style.width+"_"+lineImg.style.top+"_"+lineImg.style.left;

	if(!document.getElementById(lineImg.id))
    document.body.appendChild(lineImg);
}



//------------------------------------------------------------------------------
// Turn Object
//------------------------------------------------------------------------------
function Turn(row, block, startDirection, endDirection) {
    this.row = row;
    this.block = block;
    this.startDirection = startDirection;
    this.endDirection = endDirection;
}

Turn.prototype.draw = function(pixelsPerDay, pixelsPerRow, taskStartOffset, document) {
    var firstDirectionImg = document.createElement("img");
    firstDirectionImg.src = "../../images/schedule/linkline.gif";
    firstDirectionImg.className = "linkLine";
    firstDirectionImg.style.height = getHeightForDirection(this.startDirection, 0.5, pixelsPerRow) + "px";
    firstDirectionImg.style.width = getWidthForDirection(this.startDirection, 0.5, pixelsPerDay) + "px";
    firstDirectionImg.style.top = (39+(this.row*pixelsPerRow)+getTopOffsetForDirection(this.startDirection,pixelsPerRow))+"px";
    firstDirectionImg.style.left = (this.block*pixelsPerDay + (taskStartOffset*pixelsPerDay)+getLeftOffsetForDirection(this.startDirection,pixelsPerDay)) + "px";

    firstDirectionImg.id = "f_img_"+firstDirectionImg.style.height+"_"+firstDirectionImg.style.width+"_"+firstDirectionImg.style.top+"_"+firstDirectionImg.style.left;

    var secondDirectionImg = document.createElement("img");
    secondDirectionImg.src = "../../images/schedule/linkline.gif";
    secondDirectionImg.className = "linkLine";
    secondDirectionImg.style.height = getHeightForDirection(this.endDirection, 0.5, pixelsPerRow) + "px";
    secondDirectionImg.style.width = getWidthForDirection(this.endDirection, 0.5, pixelsPerDay) + "px";
    secondDirectionImg.style.top = (39+(this.row*pixelsPerRow)+getTopOffsetForDirection(this.endDirection,pixelsPerRow))+"px";
    secondDirectionImg.style.left = (this.block*pixelsPerDay+(taskStartOffset*pixelsPerDay)+getLeftOffsetForDirection(this.endDirection,pixelsPerDay)) + "px";

    secondDirectionImg.id = "s_img_"+secondDirectionImg.style.height+"_"+secondDirectionImg.style.width+"_"+secondDirectionImg.style.top+"_"+secondDirectionImg.style.left;

	if(!document.getElementById(firstDirectionImg.id))
    document.body.appendChild(firstDirectionImg);
    
    if(!document.getElementById(secondDirectionImg.id))
    document.body.appendChild(secondDirectionImg);
}


//------------------------------------------------------------------------------
// Terminator Object
//------------------------------------------------------------------------------
function Terminator(row, block, direction) {
    this.row = row;
    this.block = block;
    this.direction = direction;
}

Terminator.prototype.getImageHeightForDirection = function(direction) {
    if (direction == 'N' || direction == 'S') {
        return 5;
    } else {
        return 11;
    }
}

Terminator.prototype.getImageWidthForDirection = function(direction) {
    if (direction == 'N' || direction == 'S') {
        return 11;
    } else {
        return 5;
    }
}

Terminator.prototype.getLeftOffsetForDirection = function(direction, pixelsPerDay) {
    if (direction == 'E') {
        return (pixelsPerDay-5);
    } else {
        return 0;
    }
}

Terminator.prototype.getTopOffsetForDirection = function(direction, pixelsPerRow) {
    if (direction == 'S') {
        return (pixelsPerRow-5);
    } else {
        return 0;
    }
}

Terminator.prototype.draw = function(pixelsPerDay, pixelsPerRow, taskStartOffset, document) {
    //Make sure that the terminating arrow is centered in the day
    //The arrow is 11 pixels in width
    var centeringBufferSpace = 0;
    if (this.direction == 'N' || this.direction == 'S') {
        centeringBufferSpace = Math.floor((pixelsPerDay - 11)/2);
    }

    var terminatorImage = document.createElement("img");
    terminatorImage.src = "../../images/schedule/linkarrow-"+this.direction.toLowerCase()+".gif";
    terminatorImage.className = "linkArrow";
    terminatorImage.style.height = this.getImageHeightForDirection(this.direction) + "px";
    terminatorImage.style.width = this.getImageWidthForDirection(this.direction) + "px";
    terminatorImage.style.top = (39+(this.row*pixelsPerRow)+this.getTopOffsetForDirection(this.direction, pixelsPerRow)) + "px";
    terminatorImage.style.left = (centeringBufferSpace+(this.block*pixelsPerDay)+(taskStartOffset*pixelsPerDay)+this.getLeftOffsetForDirection(this.direction, pixelsPerDay)) + "px";

    document.body.appendChild(terminatorImage);
}