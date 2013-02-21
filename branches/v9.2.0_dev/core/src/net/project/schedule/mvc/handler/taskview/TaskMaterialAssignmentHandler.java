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
|   $Revision: 19063 $
|       $Date: 2009-04-05 14:27:40 -0300 (dom, 05 abr 2009) $
|     $Author: nilesh $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.taskview;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.schedule.ScheduleEntry;

/**
 * Class which does all of the logic required to load the TaskMaterials page.
 * 
 */
public class TaskMaterialAssignmentHandler extends AbstractTaskMaterialAssignmentHandler {

	public TaskMaterialAssignmentHandler(HttpServletRequest request) {
		super(request);
		setViewName("/schedule/TaskViewMaterial.jsp");
	}

	/**
	 * Add the necessary elements to the model that are required to render a
	 * view. Often this will include things like loading variables that are
	 * needed in a page and adding them to the model.
	 * 
	 * The views themselves should not be doing any loading from the database.
	 * The whole reason for an mvc architecture is to avoid that. All loading
	 * should occur in the handler.
	 * 
	 * @param request
	 *            the <code>HttpServletRequest</code> that resulted from the
	 *            user submitting the page.
	 * @param response
	 *            the <code>HttpServletResponse</code> that will allow us to
	 *            pass information back to the user.
	 * @return a <code>Map</code> which is the updated model.
	 * @throws java.lang.Exception
	 *             if any error occurs.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = super.handleRequest(request, response);
		ScheduleEntry scheduleEntry = (ScheduleEntry) model.get("scheduleEntry");

		// TODO acá hay que comparar con lo que está en memoria, porque todavía
		// no tenemos guardados los cambios en la base de datos, por lo tanto
		// hacer scheduleEntry.getMaterialAssignments().overAssignationExists()
		// no funciona.
		model.put("overallocatedMaterialsExist", false);
		return model;
	}

}
