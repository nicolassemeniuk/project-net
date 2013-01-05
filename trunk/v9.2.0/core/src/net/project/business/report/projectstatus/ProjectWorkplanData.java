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

/*----------------------------------------------------------------------+
 |
 |    $RCSfile$
 |   $Revision: 15475 $
 |       $Date: 2006-12-10 14:25:36 +0530 (Sun, 10 Dec 2006) $
 |     $Author: sjmittal $
 |
 +----------------------------------------------------------------------*/
package net.project.business.report.projectstatus;

import net.project.project.ProjectSpace;
import net.project.schedule.Schedule;

/**
 * Data object which stores both the ProjectSpace and its Schedule's data
 * needed to produce the report.
 * 
 * @author Sachin Mittal
 * @since Version 1.0
 */
public class ProjectWorkplanData {
	public ProjectSpace projectSpace;
	public Schedule schedule;

}
