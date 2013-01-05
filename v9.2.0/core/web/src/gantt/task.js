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
function Task(id, taskName, percentComplete, duration, startDate, finishDate, predecessors, resources, startBlock, durationBlocks, summaryTask, milestone, hierarchyLevel, expand) {
    this.id = id;
    this.taskName = taskName;
    this.percentComplete = percentComplete;
    this.duration = duration;
    this.startDate = startDate;
    this.finishDate = finishDate;
    this.predecessors = predecessors;
    this.resources = resources;
    this.startBlock = startBlock;
    this.durationBlocks = durationBlocks;
    this.summaryTask = summaryTask;
    this.milestone = milestone;
    this.hierarchyLevel = hierarchyLevel;
    this.expand = expand;
}

Task.prototype.getID = function() {
    return this.id;
}

Task.prototype.getTaskName = function() {
    return this.taskName;
}

Task.prototype.getPercentComplete = function() {
    return this.percentComplete;
}

Task.prototype.getStartBlock = function() {
    return this.startBlock;
}

Task.prototype.getDurationBlocks = function() {
    return this.durationBlocks;
}

Task.prototype.getDuration = function() {
    return this.duration;
}

Task.prototype.getStartDate = function() {
    return this.startDate;
}

Task.prototype.getFinishDate = function() {
    return this.finishDate;
}

Task.prototype.getPredecessors = function() {
    return this.predecessors;
}

Task.prototype.getResources = function() {
    return this.resources;
}

Task.prototype.isSummaryTask = function() {
    return this.summaryTask;
}

Task.prototype.isMilestone = function() {
    return this.milestone;
}

Task.prototype.getHierarchyLevel = function() {
    return this.hierarchyLevel;
}

Task.prototype.getExpand = function() {
    return this.expand;
}