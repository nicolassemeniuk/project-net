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
/*-----------------------------------------------------------------------------+
 |
 |    $RCSfile$
 |   $Revision: 14684 $
 |       $Date: 2006-01-21 19:36:49 +0530 (Sat, 21 Jan 2006) $
 |     $Author: andrewr $
 |
 +-----------------------------------------------------------------------------*/
package net.project.schedule.importer;

import net.project.schedule.TaskDependency;

class MSPLink {
	private int predecessorID;

	private int successorID;

	private TaskDependency taskDependency;

	public int getPredecessorID() {
		return predecessorID;
	}

	public void setPredecessorID(int predecessorID) {
		this.predecessorID = predecessorID;
	}

	public int getSuccessorID() {
		return successorID;
	}

	public void setSuccessorID(int successorID) {
		this.successorID = successorID;
	}

	public TaskDependency getTaskDependency() {
		return taskDependency;
	}

	public void setTaskDependency(TaskDependency taskDependency) {
		this.taskDependency = taskDependency;
	}
}
