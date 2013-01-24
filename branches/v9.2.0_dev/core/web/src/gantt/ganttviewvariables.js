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
var hourlyViewVariables = new GanttViewVariables(0, 480, 40, 480, 12);
var dailyViewVariables = new GanttViewVariables(1, 105, 15, 15, 7);
var weeklyViewVariables = new GanttViewVariables(2, 231, 21, 7, -1);
var monthlyViewVariables = new GanttViewVariables(3, 160, 36.95, 5.28, -1);
var quarterlyViewVariables = new GanttViewVariables(4, 105, 34, 1.133, 3);
var halfYearViewVariables = new GanttViewVariables(5, 120, 20, 0.6575, 6);
var yearlyWithQuarterViewVariables = new GanttViewVariables(6, 100, 25, 0.27, 4);
var yearlyWithHalfViewVariables = new GanttViewVariables(7, 50, 25, 0.136, 2);

function getViewForLevel(level) {
    if (level == 0) {
        return hourlyViewVariables;
    } else if (level == 1) {
        return dailyViewVariables;
    } else if (level == 2) {
        return weeklyViewVariables;
    } else if (level == 3) {
        return monthlyViewVariables;
    } else if (level == 4) {
        return quarterlyViewVariables;
    } else if (level == 5) {
        return halfYearViewVariables;
    } else if (level == 6) {
        return yearlyWithQuarterViewVariables;
    } else if (level == 7) {
        return yearlyWithHalfViewVariables;
    } else {
        alert("Error in GanttViewVariables: No view found for level " + level);
    }
}

function GanttViewVariables(viewLevelID, topHeaderWidth, bottomHeaderWidth, pixelsPerDay, repeatBottomHeader) {

    this.headersLoaded = false;
    this.viewLevelID = viewLevelID;
    this.topHeaderWidth = topHeaderWidth;
    this.bottomHeaderWidth = bottomHeaderWidth;
    this.pixelsPerDay = pixelsPerDay;
    this.repeatBottomHeader = repeatBottomHeader;
}

GanttViewVariables.prototype.getViewLevelID = function() {
    return this.viewLevelID;
}

GanttViewVariables.prototype.getTopHeaderWidth = function() {
    return this.topHeaderWidth + "px";
}

GanttViewVariables.prototype.getTopHeaderWidthInt = function() {
    return this.topHeaderWidth;
}

GanttViewVariables.prototype.getBottomHeaderWidth = function() {
    return this.bottomHeaderWidth + "px";
}

GanttViewVariables.prototype.getPixelsPerDay = function() {
    return this.pixelsPerDay;
}

GanttViewVariables.prototype.getRepeatBottomHeader = function() {
    return this.repeatBottomHeader;
}

GanttViewVariables.prototype.getBodyImage = function(jspRootURL) {
    return "url(" + jspRootURL + "/servlet/GanttBackground/g.png?viewID="+
        this.viewLevelID+"&width="+this.topHeaderWidth+"&pixelsPerDay="+
        this.pixelsPerDay+")";
}

GanttViewVariables.prototype.setTopHeaders = function(topHeaders) {
    this.topHeaders = topHeaders;
}

GanttViewVariables.prototype.setBottomHeaders = function(bottomHeaders) {
    this.bottomHeaders = bottomHeaders;
}

GanttViewVariables.prototype.getTopHeaders = function() {
    return topHeaders;
}

GanttViewVariables.prototype.getBottomHeaders = function() {
    return bottomHeaders;
}

GanttViewVariables.prototype.getTopHeaderWidths = function() {
    return headerWidths;
}


